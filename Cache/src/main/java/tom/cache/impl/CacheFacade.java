/**
 * File: CacheManager.java 
 * Creation Date: 26/07/2012
 * Last Modification Date: 25/08/2012
 * 
 * @author: Tom Geary 
 * @version 1.10 DEBUG
 *
 * Description: The CacheManager implements the CacheFacade to provide 
 * fresh resource to clients. 
 * It operates on a Producer - nConsumer basis serializing access to the 
 * cache for loading operations but parallel access (via threadpool) during 
 * fetches.
 *
 * A load adds a retrieval FutureTask to the cache. To implement concurrency repeat 
 * load requests return the previously added reference. 
 *
 * The fetch routine blocks until the FutureTask returns the resource 
 * from the origin. fetch() then begins validating the resource. Of course,
 * content that has just been retrieved will just fall through this routine.
 * Its designed to process content that has been in the cache for a while.
 *
 * Both Validation and Retrieval operations run as separate Threads 
 * controlled by a ThreadPoolExecutor. This provides for parallel 
 * processing of client requests for resource.
 */

package tom.cache.impl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import tom.cache.CacheClient;
import tom.cache.Logger;
import tom.cache.Resource;

public class CacheFacade
{
	/* lock: guards access to thread pool and hits, misses variables */
	private final Lock lock;
	private volatile int hits, misses;
	private volatile float hitRatio;
	private ThreadPoolExecutor exec;

	/* Set the size of the Thread Pool here */
	private final int THREADS;
	private final int MAX_THREADS;
	private final int TIMEOUT;
	private final int QUEUE_LENGTH;

	private Logger safeLogger;
	private static CacheFacade instance = null;
	
	// the underlying caching data structure matches a string key
	// against a promise to return a resource. Blocks during call.
	private ConcurrentHashMap<String, FutureTask<Resource>> cache; 

	private CacheFacade(
			int threads, 
			int maxThreads,
			int timeout,
			int queueLength)
	{
		lock = new ReentrantLock();
		// exec = Executors.newFixedThreadPool(NTHREADS);
		THREADS = threads;
		MAX_THREADS = maxThreads;
		TIMEOUT = timeout;
		QUEUE_LENGTH = queueLength;
		exec = new ThreadPoolExecutor(THREADS, MAX_THREADS, TIMEOUT, TimeUnit.SECONDS,
				new ArrayBlockingQueue(QUEUE_LENGTH));

		safeLogger = ThreadSafeLogger.getInstance();
		cache = new ConcurrentHashMap<String, FutureTask<Resource>>();
		safeLogger.start();
	}

	public static CacheFacade getInstance(
			int threads, 
			int maxThreads,
			int timeout,
			int queueLength)
	{
		if (instance == null)
		{
			instance = new CacheFacade(threads, maxThreads, timeout, queueLength);
		}
		return instance;
	}
	
	public void retrieve(WebResource resource, CacheClient handle)
	{
		String key = resource.getKey();
		System.out.println("per_cm: starting retrieval... " + key);

		/*
		 * load the resource into the cache if it isnt already
		 */
		load(key, resource);

		/*
		 * fetch it from cache. Via origin if necessary. note. this will block
		 * if it isnt in cache while it downloads
		 */
		fetch(key, handle);

		/* gather local stats for debug */
		safeLogger.log(stats());
	}


	/**
	 * This method gets field values for cache hits and misses, calculates the
	 * hit ratio, and writes them out to stats.log
	 */
	private String stats()
	{

		int _hits, _misses;
		float _hitRatio;

		/* synchronize access to shared state */
		lock.lock();
		_hits = hits;
		_misses = misses;
		_hitRatio = hitRatio;
		lock.unlock();

		_hitRatio = (float) _hits / ((float) _hits + (float) _misses) * 100;
		System.out.println("hit ratio: " + _hitRatio + "%");

		try
		{
			OutputStream out = new FileOutputStream("stats.log");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					out));
			writer.write(String.valueOf(_hits) + "\n");
			writer.write(String.valueOf(_misses) + "\n");
			writer.write(String.valueOf(_hitRatio) + "\n");

			writer.flush();
			writer.close();
		} catch (FileNotFoundException fnfe)
		{
		} catch (IOException e)
		{
		}

		return "hit ratio: " + _hitRatio + "\nhits: " + _hits + "\nmisses: "
				+ _misses + "\n";

	}

	/**
	 * Adds the resouce retrieval task into the cache. It uses a retriever
	 * thread to execute the task.
	 */
	private void load(String key, WebResource resource)
	{

		Future future;
		FutureTask<Resource> fTask;
		Callable<Resource> retriever;

		/*
		 * Because the cache is accessed in parallel we must synchronize access.
		 * Not because the cache doesnt allow concurrent reads - it does but we
		 * dont want multiple threads loading the same task
		 */
		lock.lock();

		future = cache.get(key);
		if (future == null)
		{
			/*
			 * if future is null there is no entry for this key in the cache.
			 * ie. it is a new request -> cache miss
			 */
			misses++;

			System.out.println("per_cm: cache miss");
			retriever = new Retriever(resource);
			fTask = new FutureTask<Resource>(retriever);

			/* load the cache with the retrieval task */
			future = cache.putIfAbsent(key, fTask);
			System.out.println("per_cm: loading.. " + key);
			exec.submit(fTask);

			// DEBUG: how long is the queue?
			System.out.println("cm_retr_q: " + exec.getQueue().size());

			lock.unlock();
		} else
		{
			/*
			 * if future isnt null there must be a FutureTask object in the
			 * cache. Therefore a thread is currently either retreiving or
			 * validating the resource associated with it. -> cache hit.
			 */
			hits++;

			lock.unlock();
			System.out.println("per_cm: cache hit");
		}
	}

	/**
	 * Fetches the result of the task and submits a new validation task to the
	 * ThreadPoolExecutor. The validation task is offloaded so that parallelism
	 * may be exploited
	 */
	private void fetch(String key, CacheClient handle)
	{

		/*
		 * a placeholder for the downloading Resource object
		 */
		Future futureLoaded;

		FutureTask<Validator> futureTask;
		Runnable validator;
		WebResource resource;

		System.out.println("per_cm_fetch: fetching " + key);

		/*
		 * PRE-CONDITION: FutureTask must be in cache as load 'happens-before'
		 * in java memory model
		 */
		futureLoaded = cache.get(key);

		assert (futureLoaded != null) : "no loaded task";

		/*
		 * retrieve the result of the future task that was loaded. Blocking if
		 * needed. The result is cast to a resource object that can be
		 * validated.
		 */
		while (true)
		{
			try
			{
				resource = (WebResource) futureLoaded.get();
				assert (resource != null) : "resource not returned by fetch";
				System.out.println("per_cm_fetch: " + key + " fetched");
				break;
			} catch (Exception excp)
			{
				excp.printStackTrace();
			}
		}

		/*
		 * The task of validating the resource is offloaded to a worker thread
		 * (validator) this sends the reference for a fresh resource back to the
		 * HTTPHandler (handle)
		 */
		validator = new ValidatorFactory().getValidator(resource, handle, safeLogger);
		futureTask = new FutureTask<Validator>(validator, null);
		lock.lock();
		exec.execute(futureTask);

		// DEBUG: how long is the queue?
		System.out.println("cm_val_q: " + exec.getQueue().size());

		lock.unlock();
	}
}
