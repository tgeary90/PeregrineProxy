package tom.cache.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;

import tom.cache.Resource;

class Retriever implements Callable<Resource>
{
	/*
	 * Retriever: This class retrieves the resource data from the origin
	 * server and populates the resource passed to it with the remaining fields
	 * that werent created in the initial stages of the HTTPHandler pipeline
	 */

	private URL url;
	private HttpURLConnection HttpURL;
	private InputStream raw;
	private InputStream bin;
	private int contentLength, bytesRead, offset;
	private Date date;
	private WebResource resource;
	private String urlString;
	private byte[] data;
	private Logger logger;

	public Retriever(WebResource resource)
	{
		this.resource = resource;
		contentLength = bytesRead = offset = 0;
	}

	/**
	 * Produces a populated resource the leg work is farmed out to
	 * getResourceData()
	 */
	public Resource call()
	{
		return getResourceData(resource);
	}

	/**
	 * Pulls the raw data down from the origin server and populates resource
	 * object
	 */
	private Resource getResourceData(WebResource resource)
	{
		try
		{

			/* setup comms link to origin */
			urlString = resource.getURL();
			System.out.println("per_cm: getting resource.. " + urlString);
			url = new URL(urlString);
			HttpURL = (HttpURLConnection) url.openConnection();
		} catch (MalformedURLException mue)
		{
			mue.printStackTrace();
		} catch (IOException ie)
		{
			ie.printStackTrace();
		}

		try
		{
			System.out.println("per_cm: retrieving...");

			/* byte level IO stream. No filtering */
			raw = HttpURL.getInputStream();
			bin = new BufferedInputStream(raw);

			/*
			 * Unlike text based data web servers usually specify content length
			 * for binary data. We grab this information and instantiate an
			 * array to hold it.
			 */
			contentLength = HttpURL.getContentLength();
			data = new byte[contentLength];

			/* chunk the data in from the buffer */
			int bytesAvailable = 0;
			System.out.println("per_cm_retr_call: content len: "
					+ contentLength);
			while (offset < contentLength)
			{
				bytesAvailable = bin.available();
				bytesRead = bin.read(data, offset, bytesAvailable);
				offset += bytesRead;
			}

			assert (data.length == offset) : "no content to serve";

			bin.close();
			raw.close();

			/* populate the resource */

			/* First, set the last-modified header */
			Calendar calLastMod = Calendar.getInstance();
			calLastMod.setTimeInMillis(HttpURL.getLastModified());
			resource.setLastMod(calLastMod);

			System.out.println("per_cm_retr_call: last modified.. "
					+ resource.getLastMod().getTime());

			/* Second, set Content-Type and Content-Length headers */
			resource.setContentType(HttpURL.getContentType());
			resource.setContentLen(contentLength);
			System.out.println("per_cm_retr_call: set content type.. "
					+ resource.getContentType());
			System.out.println("per_cm_retr_call: set content len.. "
					+ resource.getContentLength());

		} catch (IOException ie)
		{
			System.err
					.println("FAIL-per_cm: Couldnt read in content from origin.\n"
							+ "Investigate " + url);
		}

		/* finish populating resource fields */
		resource.setData(data);

		/*
		 * In order to validate content we need to know how long it has been in
		 * the cache. We timestamp this information into the Resource object for
		 * later use, now.
		 */
		resource.cachedAt(Calendar.getInstance());

		assert (resource != null) : "resource is null";
		return resource;
	}
}
