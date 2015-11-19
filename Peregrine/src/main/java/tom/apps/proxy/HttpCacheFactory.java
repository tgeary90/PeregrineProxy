package tom.apps.proxy;

import tom.apps.cache.CacheFactory;
import tom.apps.cache.impl.HttpCache;

public class HttpCacheFactory implements CacheFactory
{
	private HttpCache facade;
	private boolean unitTest;
	
	public HttpCache getInstance(int threads, int maxThreads,
			int timeout, int queueLength)
	{
		if ( ! unitTest) 
			return HttpCache.getInstance(threads, maxThreads, timeout, queueLength);
		else
			if (facade == null)
				throw new RuntimeException("facade has not been set for mocking");
			else
				return facade;
	}

	public void setFacade(HttpCache facade)
	{
		this.facade = facade;
	}
}
