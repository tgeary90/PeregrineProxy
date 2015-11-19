package tom.apps.proxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import tom.apps.cache.Cache;
import tom.apps.cache.ResourceFactory;
import tom.apps.proxy.server.Plugin;
import tom.frameworks.BufferFactory;
import tom.frameworks.impl.DumbBufferFactory;
import tom.frameworks.impl.NioDispatcher;
import tom.frameworks.impl.StandardAcceptor;

public class PeregrineMain
{
	private String origin;
	private int port;
	private int threads;
	private int maxThreads;
	private int timeout;
	private int queueLength;
	private static Logger s_logger;


	public static void main(String[] args) 
	{
		new PeregrineMain().go();
	}
	
	public PeregrineMain()
	{ 
		// read in properties (port, loglevel, thread #, etc)
		init();
	}
	
	public void go()
	{
		// NIO framework initialize
		Executor executor = Executors.newCachedThreadPool();
		BufferFactory bufFactory = new DumbBufferFactory(1024);
		NioDispatcher dispatcher = null;
		StandardAcceptor acceptor = null;
	
		// prime factories
		HttpCacheFactory cacheFactory = new HttpCacheFactory();
		Cache cache = cacheFactory.getInstance(
				threads, maxThreads, timeout, queueLength);
		 
		PluginLoader loader = new PluginLoader();
		List<Plugin> plugins = loader.loadPlugins();
		
		
		ResourceFactory resourceFactory = new WebResourceFactory();
		HttpHandlerFactory handlerFactory = new HttpHandlerFactory();
		handlerFactory.setOrigin(origin);
		handlerFactory.setCache(cache);
		handlerFactory.setPlugins(plugins);
		handlerFactory.setResourceFactory(resourceFactory);

		
		try
		{
			dispatcher = new NioDispatcher(executor, bufFactory);
			acceptor = new StandardAcceptor(port, dispatcher, handlerFactory);
		}
		catch (IOException ioe)
		{
			s_logger.fatal("Peregrine failed to start with an I/O error: %s\n" + ioe.getMessage());
			throw new RuntimeException();
		}
		
		dispatcher.start();
		acceptor.newThread();
		
		s_logger.info("listening on port " + port);
	}

	private void init()
	{
		Properties props = new Properties();
		try
		{
			InputStream fis = getClass().getResourceAsStream("/peregrine.properties");
			props.load(fis);
			port = Integer.parseInt(props.getProperty("port"));
			origin = props.getProperty("origin");
			
			// logger setup
			s_logger = Logger.getLogger(PeregrineMain.class);
			InputStream log4jConfigFileStream = getClass().getResourceAsStream("/log4j.properties");
			PropertyConfigurator.configure(log4jConfigFileStream);
			
			// Thread pool configuration
			threads = Integer.parseInt(props.getProperty("threads"));
			maxThreads = Integer.parseInt(props.getProperty("max.threads"));
			timeout = Integer.parseInt(props.getProperty("timeout"));
			queueLength = Integer.parseInt(props.getProperty("queue.length"));
			
			s_logger.info("logging at level: " + props.getProperty("log.level"));
			s_logger.info("origin: " + origin);
			
			fis.close();
		}
		catch(FileNotFoundException fnfe)
		{
			System.err.printf("Cant read in properties from disk");
		}
		catch (IOException ioe)
		{
			System.err.printf("Cant read in from disk, %s", ioe.getMessage());
		}
	}
}
