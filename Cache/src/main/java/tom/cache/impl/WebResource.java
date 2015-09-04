/**
 * Creation Date: 17/07/2012
 * @author Tom Geary
 */

package tom.cache.impl;

import tom.cache.Resource;

/**
 * The Resource class implements ICachable by encapsulating data 
 * required to be cachable (ie. retrievable, validatable, etc). It is the 
 * base unit of the cache component
 */
public class WebResource extends Resource
{
	private String url;
	private String[] headers;
	private String contentType;
	
	public WebResource()
	{
		setFresh(false);
	}

	@Override
	public String getRoot() {
		return WebResource.class.toString();
	}

	public WebResource(String url)
	{
		this.url = url;
		setFresh(false);
	}

	public WebResource(String url, String[] headers)
	{
		this.url = url;
		this.headers = headers;
		setFresh(false);
	}

	public String[] getHeaders()
	{
		return headers;
	}

	public String getContentType()
	{
		return contentType;
	}

	public String getURL()
	{
		return url;
	}

	public void setHeaders(String[] headers)
	{
		this.headers = headers;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}
}
