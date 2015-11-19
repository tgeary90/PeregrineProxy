/**
 * File: ProxyPassHandler.java 
 * Creation Date: 24/07/2012
 * Last Modification Date: 20/08/2012
 * 
 * @author: Tom Geary 
 * @version 1.1
 *
 * Description: This class retrieves all HTTP requests, on behalf of the client
 * ,from the origin server that cannot be retrieved from cache. Typically this 
 * is because the request is for dynamic content that must be built on-the-fly 
 * at the server. Also, pdf, video, sound and other formats are not currently 
 * supported via this proxy cache (They are cached at the browser though). 
 * These requests are proxied, on behalf of the client, here.
 */

package tom.apps.proxy.client.impl;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import tom.apps.proxy.server.PassHandler;
import tom.frameworks.ChannelFacade;

public class HttpBlockingPassHandler implements PassHandler
{
	public void invoke(ChannelFacade facade, URL url) {
		Logger logger = 
				Logger.getLogger(HttpBlockingPassHandler.class.toString());
		
		final String LINE_SEP = "\r\n";
		final String HEADER_END = "\r\n\r\n";
		final String platform = System.getProperty("os.name");

		/* 
		 * local variables; headers  
		 */
		int contentLength = 0;
		long contentLastModified = 0;
		String contentType = null;
		String encoding = null;
		byte[] tmpBuffer = null;
		byte[] dataBuffer = null;
		byte[] headers = null;

		/* local variables: streams */
		InputStream raw = null;
		InputStream bin = null;

		/* Protocol handler */
		HttpURLConnection httpCon = null;

		logger.info("Starting proxy pass on... "
				+ url.toString());
		try
		{
			/* open comms with origin */
			httpCon = (HttpURLConnection) url.openConnection();

			/*
			 * get the response headers for sending to client. We'll drop in a
			 * header to let the client know weve proxied later
			 */
			contentLength = httpCon.getContentLength();
			contentLastModified = httpCon.getLastModified();
			contentType = httpCon.getContentType();
			logger.fine("content type: " + contentType);
			int idx;

			/* encode reply in same format as the http resp. from origin */
			if ((idx = contentType.indexOf("charset")) != -1)
			{
				encoding = contentType.substring(idx + 8);
				logger.fine("encoding: " + encoding);
				contentType = contentType.substring(0, --idx);
			}

			if (contentLength == -1)
			{
				/* one meg byte array if content size unknown */
				contentLength = 1024 * 1024;
				tmpBuffer = new byte[contentLength];
			} else
				tmpBuffer = new byte[contentLength];
			logger.fine("content len... " + contentLength);

			/* low-level byte streams. no char based filters here */
			raw = httpCon.getInputStream();
			bin = new BufferedInputStream(raw);


			/*
			 * we use the overloaded read() call to chunk bytes into the content
			 * array at offset and for length
			 */
			int bytesRead = 0;
			int totalBytesRead = 0;
			int bytesAvailableToRead = 0;
			
			while ( (bytesAvailableToRead = bin.available() ) > 0 && bytesRead >= 0)
			{
				logger.fine(bytesAvailableToRead + " bytes are available to read from the server");
				
				bytesRead = bin.read(tmpBuffer, totalBytesRead, bytesAvailableToRead);
				if (bytesRead > 0)
				{
					totalBytesRead += bytesRead;
				}
			}
			dataBuffer = Arrays.copyOf(tmpBuffer, totalBytesRead);
			logger.fine("bytes read from server... " + totalBytesRead);

			bin.close();
			raw.close();
		} catch (FileNotFoundException fnfe)
		{
			logger.warning(url + " not at server");
		} catch (IOException ie)
		{
			logger.warning("content get from origin failed");
			ie.printStackTrace();
		} catch (NullPointerException e)
		{
			e.getMessage();
			throw new RuntimeException();
		}

		/* Now we write out our data to the client. */

		/* prepare HTTP headers for replying to clients request. */
		StringBuffer sb = new StringBuffer();
		sb.append("HTTP/1.0 200 OK" + LINE_SEP);
		Date now = new Date();

		/* sanitize the date format for finnicky clients */
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss zzz");
		String safeDate = formatter.format(now);
		sb.append("Date: " + safeDate + LINE_SEP);
		sb.append("Server: " + "Peregrin/1.0" + " (" + platform + ")"
				+ LINE_SEP);
		sb.append("Last-Modified: "
				+ formatter.format(new Date(contentLastModified))
				+ LINE_SEP);
		sb.append("Content-Type: " + contentType + LINE_SEP);
		sb.append("Content-Length: " + dataBuffer.length + LINE_SEP);
		sb.append("Connection: close" + HEADER_END);

		logger.fine("clients enc. .. " + encoding);
		logger.fine("response headers.. " + sb.toString());
		
		headers = sb.toString().getBytes(); // default encoding

		/* write headers and content out to the buffered stream */
		logger.fine("writing to client... ");

		ByteBuffer responseHeadersBuffer = ByteBuffer.wrap(headers);
		facade.outputQueue().enqueue(responseHeadersBuffer);
		ByteBuffer responseDataBuffer = ByteBuffer.wrap(dataBuffer);
		facade.outputQueue().enqueue(responseDataBuffer);		
	}
}
