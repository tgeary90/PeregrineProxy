/**
 * File: HTTPHandler.java
 * Creation Date: 26/07/2012
 * Last Modification Date: 20/08/2012
 * 
 * @author: Tom Geary 
 * @version 1.6
 *
 * Description: The HTTPHandler implements the Handler interface. 
 * It defines a pipeline of tasks to resolve the clients request.
 * 
 * 1. Reading in the request headers
 * 2. Instantiating a resource object of type Cachable
 * 3. Populating the resource object with data from the caching layer
 * 4. Wrapping the response headers around the completed resource
 * 5. Sending the response back to the HTTP client.
 *
 */

package tom.peregrine.server.impl;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import tom.apps.framework.ChannelFacade;
import tom.apps.framework.InputHandler;
import tom.apps.framework.InputQueue;
import tom.cache.CacheClient;
import tom.cache.impl.CacheFacade;
import tom.cache.impl.WebResource;
import tom.peregrine.client.PassHandler;
import tom.peregrine.client.impl.HttpBlockingPassHandler;
import tom.peregrine.server.Plugin;

public class HttpHandler implements InputHandler, CacheClient
{
	private CacheFacade cacheFacade;
	private Date now, lastMod;
	private int c, idx, conLength;
	private StringBuilder sb;
	private String get, method, file, version, urlString;
	private String ext, conType, encoding;
	private WebResource resource;
	private String[] headers;
	private StringTokenizer st;
	private byte[] respHeaders;
	private URL url;
	private final String LINE_SEP = "\r\n";

	/* the origin web server. We're accelerating this site ... */
	private String origin;
	private final String PLATFORM = System.getProperty("os.name");
	
	private Logger logger;
	private PassHandler passHandler;
	private List<Plugin> plugins;

	/*
	 * Constructor takes a socket to complete the pipeline with, or offload to
	 * the ProxyPassHandler in the case of dynamic content
	 */
	public HttpHandler(CacheFacade cache, String origin, List<Plugin> plugins)
	{
		this.cacheFacade = cache;
		logger = Logger.getLogger(HttpHandler.class.toString());
		passHandler = new HttpBlockingPassHandler();
		this.origin = origin;
		this.plugins = plugins;
	}


	/* callback method used by Validator to signal fresh resource */
	public void validated(WebResource resource)
	{
		this.resource = resource;
	}

	public ByteBuffer nextMessage(ChannelFacade channelFacade)
	{
		InputQueue inputQueue = channelFacade.inputQueue();
		int endPoint = 0;
		byte[] endOfHeaders = { 
				(byte) '\r',
				(byte) '\n',
				(byte) '\r',
				(byte) '\n'
		};

		endPoint = inputQueue.indexAfter(endOfHeaders); 
		if (endPoint == -1) 
		{
			return null;
		}

		return (inputQueue.dequeueBytes(endPoint));
	}

	public void handleInput(ByteBuffer message, ChannelFacade channelFacade)
	{
		createResource(message);
		populateResource();
		boolean furtherProcessing = processRequestMessage(channelFacade);
		
		if (furtherProcessing)
		{
			processResponse(channelFacade);
		}
	}

	/**
	 * @param channelFacade
	 */
	private void processResponse(ChannelFacade channelFacade)
	{
		encodeResponseBytes();

		try
		{
			addResponseHeaders();
			sendResponse(channelFacade);
		} catch (UnsupportedEncodingException uee)
		{
			logger.warning("Encoding unsupported " + uee.getMessage());
		}
	}

	private void sendResponse(ChannelFacade channelFacade)
	{
		logger.fine("write http resp. to client");
		
		/* sloop out the response in one go. No chunking */
		ByteBuffer responseHeadersBuffer = ByteBuffer.wrap(respHeaders);
		channelFacade.outputQueue().enqueue(responseHeadersBuffer);
		ByteBuffer responseDataBuffer = ByteBuffer.wrap(resource.getData());
		channelFacade.outputQueue().enqueue(responseDataBuffer);
	}

	private void addResponseHeaders() throws UnsupportedEncodingException
	{
		sb = new StringBuilder();
		sb.append("HTTP/1.1 200 OK" + LINE_SEP);

		/*
		 * clients like the LastModified: header to be in a certain format -
		 * the same as Apache - as it happens. To be nice we do this for
		 * them
		 */
		now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss zzz");
		String safeDate = formatter.format(now);

		/* build the HTTP headers */
		sb.append("Date: " + safeDate + LINE_SEP);
		sb.append("Server: " + "Peregrin/1.0" + " (" + PLATFORM + ")"
				+ LINE_SEP);
		lastMod = resource.getLastMod().getTime();
		String safeMod = formatter.format(lastMod);
		sb.append("Last-Modified: " + safeMod + LINE_SEP);
		sb.append("Content-Length: " + conLength + LINE_SEP);
		sb.append("Content-Type: " + conType + LINE_SEP);
		sb.append("Connection: close" + LINE_SEP + LINE_SEP);

		respHeaders = sb.toString().getBytes(encoding);
	}

	private void encodeResponseBytes()
	{
		/*
		 * we send the client response out in the same encoding that we received
		 * from the origin. The encoding is sent to us in the ContentType:
		 * header
		 */
		conType = resource.getContentType();
		conLength = resource.getContentLength();
		logger.fine("content type... " + conType);
		logger.fine("content len: " + conLength);
		if ((idx = conType.indexOf(";")) != -1)
		{
			encoding = conType.substring(idx);
			conType = conType.substring(0, idx);
		} 
		else
		{
			encoding = "UTF-8";
		}
	}

	/**
	 * process the request messsage and return whether the resource should
	 * be retrieved from cache or direct from origin. If origin boolean indicates
	 * further processing is not required
	 * @param channelFacade
	 * @return boolean - further processing required.
	 */
	private boolean processRequestMessage(ChannelFacade channelFacade)
	{
		for (Plugin plugin : plugins)
		{
			if (plugin.retrieveFromCache(headers))
			{
				retrieveItemFromCache();
				return true;
			}
		}
		
		passHandler.invoke(channelFacade, url);

		return false;
	}


	/**
	 * @return
	 */
	private boolean retrieveItemFromCache()
	{
		/*
		 * 3 contd. We call on the services of the caching layer to
		 * populate the remaining fields of the resource object before
		 * sending out to the client.
		 * 
		 * Asynchronously drop a request on the cache. ie. producer
		 * element of Producer-Consumer pattern.
		 */
		cacheFacade.retrieve(resource, this);

		/* wait until we have a resource */
		while (!resource.getFresh())
		{
			try
			{
				synchronized (this)
				{
					wait();
				}
			} catch (InterruptedException ie)
			{
				/* fallthrough, we need that resource */
			}
		}
		logger.info("resource fresh: " + resource.getKey()
				+ " - retrieved ok");
		return true;
	}

	private void populateResource()
	{
		/*
		 * the index for the resource object. This is used by the cache for
		 * storage/retrieval operations
		 */
		resource.setKey(file);

		idx = file.indexOf(".");
		assert (idx > 0) : idx;
		ext = file.substring(++idx).toLowerCase();
		logger.fine("file extension: " + ext);

		/*
		 * Can we process this content? if its not a HTTP GET request or for
		 * image,css or js content, its a no
		 */
		try
		{
			url = new URL(urlString);
		} 
		catch (MalformedURLException mue)
		{
			logger.warning(urlString + " no good :(");
		}
	}

	private void createResource(ByteBuffer message)
	{
		byte[] getLineBytes = new byte[message.capacity()];
		message.get(getLineBytes);
		get = new String(getLineBytes);
		logger.info("getting... " + get);

		/* the method is split up for further processing */
		st = new StringTokenizer(get);
		method = st.nextToken();
		file = st.nextToken();
		if (file.equals("/"))
			file = "/index.html";
		logger.fine("file: " + file);
		version = st.nextToken();

		/*
		 * we prepare the call to the origin here, directly from the client
		 * http request.
		 */
		urlString = "http://" + origin + file;
		logger.fine("url... " + urlString);
		
		String[] toks = get.split("\\r?\\n"); 
		headers = new String[toks.length];
		for (int i = 0; i < toks.length; i++)
		{
			headers[i] = toks[i];
		}
		
		// populate the headers
		// TODO this should be a factory call (DI)
		resource = new WebResource(urlString, headers);
	}

	public void starting(ChannelFacade channelFacade)
	{
		// TODO Auto-generated method stub
		
	}

	public void started(ChannelFacade channelFacade)
	{
		// TODO Auto-generated method stub
		
	}

	public void stopping(ChannelFacade channelFacade)
	{
		// TODO Auto-generated method stub
		
	}

	public void stopped(ChannelFacade channelFacade)
	{
		// TODO Auto-generated method stub
		
	}

	public void validated(tom.cache.Resource resource) {
		// TODO Auto-generated method stub
		
	}
}