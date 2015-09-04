package tom.peregrine.server.impl;

import java.util.List;

import tom.apps.framework.InputHandler;
import tom.apps.framework.InputHandlerFactory;
import tom.cache.impl.CacheFacade;
import tom.peregrine.server.Plugin;

public class HttpHandlerFactory implements InputHandlerFactory
{
	private CacheFacade cache = null;
	private String origin = null;
	private List<Plugin> plugins = null;
	
	public InputHandler newHandler() throws IllegalAccessException,
			InstantiationException
	{
		return new HttpHandler(cache, origin, plugins);
	}

	public void setPlugins(List<Plugin> plugins)
	{
		this.plugins = plugins;
	}

	public void setCache(CacheFacade cache)
	{
		this.cache = cache;
	}

	public void setOrigin(String origin)
	{
		this.origin = origin;
	}
}
