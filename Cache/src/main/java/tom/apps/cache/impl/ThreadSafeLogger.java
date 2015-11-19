/**
 * File: ThreadSafeLogger.java
 * Creation Date: 24/07/2012
 * Last Modification Date: 27/07/2012
 * 
 * @author: Tom Geary 
 * @version 1.0
 *
 * Description: The ThreadSafe logger implements Singleton pattern to ensure only one log file is generated. It provides an API that allows start, stop and log operations. log jobs are submitted by blocking queue. Chits are used to track the number of messages needed to write out. It also ensures that on shutdown no details are lost.
 */

package tom.apps.cache.impl;

import java.io.*;
import java.util.concurrent.*;

import tom.apps.cache.Logger;

public class ThreadSafeLogger implements Logger
{
	private BlockingQueue<String> _queue;
	private Worker _logger;
	private OutputStream _out = null;
	private BufferedWriter _writer = null;
	private static ThreadSafeLogger _instance = null;
	private boolean _isShutdown = false;
	private int _chits;
	private static final int LENGTH = 100;

	/* Implement the Singleton Pattern */
	private ThreadSafeLogger() 
	{
		try 
		{
			_queue = new ArrayBlockingQueue<String>(LENGTH);
			_out = new FileOutputStream(new File("peregrine.log"));
			_writer = new BufferedWriter(new OutputStreamWriter(_out));
		} 
		catch (IOException e) 
		{
		}
	}

	public synchronized static Logger getInstance() 
	{
		if (_instance == null)
			_instance = new ThreadSafeLogger();
		return _instance;
	}

	public void start() {
		synchronized (this) {
			_logger = new Worker();
		}
		_logger.start();
	}

	public void stop() {
		synchronized (this) {
			_isShutdown = true;
		}

		// if logger is blocked on queue. wake it up.
		_logger.interrupt();
	}

	public void log(String message) {
		synchronized (this) {
			if (_isShutdown)
				throw new IllegalStateException();
			_chits++;
		}
		try {
			_queue.put(message);
		} catch (InterruptedException e) {
			/* reset the interrupt flag */
			_logger.interrupt();
		}
	}

	private class Worker extends Thread {
		public void run() {
			try {
				while (true) {
					try {
						synchronized (this) {
							if (_isShutdown && _chits == 0)
								break;
						}
						String message = _queue.take();
						synchronized (this) {
							--_chits;
						}
						_writer.write(message);
						_writer.flush();
					} catch (IOException e) {
					} catch (InterruptedException e) {
						/* retry */
					}
				}
			} finally {
				try {
					_writer.close();
				} catch (IOException e) {
					System.err.println("Bombed out of logger :(");
				}
			}
		}
	}
}
