package tom.cache.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import tom.cache.CacheClient;
import tom.cache.Logger;
import tom.cache.Resource;

class Validator implements Runnable
{
	/*
	 * Validator: defines a worker that ensures that a resource is fresh.
	 * The worker waits on the future task to return (this is immediate for
	 * a resource that has previously been loaded into the cache) and then
	 * tests it for freshness. The algorithm checks to see if the content
	 * has been in the cache less than FRESH milliseconds Content stored for
	 * less than this time is fresh. Any older and it must be validated
	 * using a 'Conditional GET' process against the origin server
	 */

	private HttpURLConnection HttpURL;
	private CacheClient handle;
	private URL url;
	private Date date;
	private String urlString, key;
	private long now, then, diff;
	private InputStream raw;
	private InputStream bin;
	private int contentLength, bytesRead, offset, status;
	private Resource resource;
	private byte[] data;
	private final int FRESH = 3600;
	private Logger logger;

	public Validator(Resource resource, CacheClient handle, Logger logger)
	{
		this.resource = resource;
		this.handle = handle;
		this.logger = logger;
	}

	/** Run returns a fresh resource to the handler */
	public void run()
	{

		key = resource.getKey();
		System.out.println("per_cm_valid_call: validating " + key);

		/* beginning freshness check algorithm */

		Calendar calFreshnessLifetime = Calendar.getInstance();

		/*
		 * bump FRESH time off the calendar to perform the comparison
		 */
		calFreshnessLifetime.add(calFreshnessLifetime.SECOND, FRESH);

		/*
		 * getTime() returns a date object that can be used in a standard
		 * object comparison. If the resource has been in the cache for more
		 * than FRESH time then the compareTo() returns 1. This resource
		 * would be considered stale
		 */
		Calendar calCachedAt = resource.getCachedDate();
		if (calFreshnessLifetime.getTime().compareTo(calCachedAt.getTime()) < 1)
		{
			/* fresh */
			System.out.println("per_cm_valid_call: " + key + " valid");

			assert (resource != null) : "resource is null";

			/*
			 * resource is now fresh so we can invoke the callback routine
			 * at the handler to continue pipeline processing.
			 * 
			 * note1: The handler has been waiting on fresh resource so we
			 * need to wake it up.
			 * 
			 * note2. setFresh() acts as the gate for the correct handler as
			 * notifyAll() wakes up all handles.
			 */
			resource.setFresh(true);
			handle.validated(resource);

			synchronized (handle)
			{
				handle.notifyAll();
			}
		} else
		{
			/* stale */
			System.out.println("per_cm_valid_call: " + key + " stale");

			/* how long is the resource stale by? */
			long staleTime = (calFreshnessLifetime.getTimeInMillis() - calCachedAt
					.getTimeInMillis());
			System.out
					.println("per_cm_valid_call: stale time=" + staleTime);

			/* validate the stale resource */
			resource = conditionalGet(resource);
			resource.setFresh(true);
			handle.validated(resource);

			synchronized (handle)
			{
				handle.notifyAll();
			}
		}
	}

	private void resourceSanityCheck(Resource resource, String note)
	{
		System.out.println(note + resource.getKey());
		System.out.println(note + resource.getContentLength());
		System.out.println(note + resource.getContentType());
		System.out.println(note + resource.getLastMod().getTime());
		System.out.println(note + resource.getUrl());
		System.out.println(note + resource.getCachedDate().getTime());
	}

	/**
	 * Validates a resource via origin. HTTP 304 is not-modified. HTTP 200
	 * means that the resource has changed. A new copy of the resource is
	 * included in the server response
	 */
	private Resource conditionalGet(Resource resource)
	{
		System.out.println("per_cm: performing freshness check on " + key);
		try
		{
			/* setup comms to origin */
			urlString = resource.getUrl();
			url = new URL(urlString);
			HttpURL = (HttpURLConnection) url.openConnection();
		} catch (MalformedURLException mue)
		{
			mue.printStackTrace();
		} catch (IOException ie)
		{
			System.err.println("Couldnt revalidate: " + url);
		}

		/*
		 * If the resource has been modified at the origin since it was
		 * cached at the proxy we need a new copy.
		 * 
		 * So when was it cached?
		 */
		Calendar calCondGet = resource.getCachedDate();

		/*
		 * we load the HTTP request with the If-Modified-Since header. This
		 * is set to when the resource was initially cached
		 */
		HttpURL.setIfModifiedSince(calCondGet.getTimeInMillis());

		try
		{
			status = HttpURL.getResponseCode();
			logger.log(resource.getUrl() + " status: " + status);
			if (status == 304)
			{
				/* update cache metadata: set cachedAt field */
				resource.cachedAt(Calendar.getInstance());
				System.out.println("per_cond_get: 304 Not Modified");
				return resource;
			} else
			{
				if (status == 200)
				{
					/*
					 * the origin has told us that the content is new and
					 * has sent us a copy. Thanks! We need to store this new
					 * resource data in our resource object
					 */
					contentLength = HttpURL.getContentLength();
					raw = HttpURL.getInputStream();
					bin = new BufferedInputStream(raw);
					data = new byte[contentLength];
					int bytesAvailable = 0;

					/* read the content into our byte array */
					while (offset < contentLength)
					{
						bytesAvailable = bin.available();

						bytesRead = bin.read(data, offset, bytesAvailable);
						offset += bytesRead;
					}

					bin.close();
					raw.close();

					/* set data field in resource object */
					resource.setData(data);

					/* Update metadata: set cachedAt field */
					Calendar calValidated = Calendar.getInstance();
					resource.cachedAt(calValidated);
					System.out.println("per_cm_valid_call: " + key
							+ " cached at: " + calValidated.getTime());

					/* set Last-Modified header */
					Calendar calLastMod = Calendar.getInstance();
					calLastMod.setTimeInMillis(HttpURL.getLastModified());
					resource.setLastMod(calLastMod);
				}
				assert (resource != null) : "resource is null";
				System.out.println("per_cond_get: 200 OK");
				return resource;
			}
		} catch (IOException ie)
		{
			logger.log(resource.getUrl() + " validation failure");
			ie.printStackTrace();
			try
			{
				bin.close();
				raw.close();
			} catch (IOException ioe)
			{
				logger.log(resource.getUrl() + " close stream error");
			}
			return null;
		}
	}
}
