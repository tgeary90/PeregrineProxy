package tom.apps.proxy;

import java.util.List;

import tom.apps.cache.Cache;
import tom.apps.cache.ResourceFactory;
import tom.apps.proxy.server.Plugin;
import tom.apps.proxy.server.impl.HttpHandler;
import tom.frameworks.InputHandler;
import tom.frameworks.InputHandlerFactory;

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
