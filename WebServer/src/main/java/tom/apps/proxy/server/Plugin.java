package tom.apps.proxy.server;

/**
 * Abstract base plugin. Defines contract for whether
 * the cache is bypassed given the HTTP headers.
 * @author tom
 *
 */
public abstract class Plugin
{
	private String name;
	
	public abstract boolean retrieveFromCache(String[] headers);

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
}
