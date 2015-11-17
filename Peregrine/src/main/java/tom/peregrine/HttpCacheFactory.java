package tom.peregrine;

import tom.cache.CacheFactory;
import tom.cache.impl.HttpCache;

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
