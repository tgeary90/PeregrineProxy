package tom.peregrine;

import java.util.List;

import tom.apps.framework.InputHandler;
import tom.apps.framework.InputHandlerFactory;
import tom.cache.Cache;
import tom.cache.ResourceFactory;
import tom.peregrine.server.Plugin;
import tom.peregrine.server.impl.HttpHandler;

public class HttpHandlerFactory implements InputHandlerFactory
{
	private Cache cache = null;
	private String origin = null;
	private List<Plugin> plugins = null;
	private ResourceFactory resourceFactory;
	
	public InputHandler newHandler() throws IllegalAccessException,
			InstantiationException
	{
		return new HttpHandler(cache, origin, plugins, resourceFactory);
	}

	public void setPlugins(List<Plugin> plugins)
	{
		this.plugins = plugins;
	}

	public void setCache(Cache cache)
	{
		this.cache = cache;
	}

	public void setOrigin(String origin)
	{
		this.origin = origin;
	}

	public void setResourceFactory(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}
}
