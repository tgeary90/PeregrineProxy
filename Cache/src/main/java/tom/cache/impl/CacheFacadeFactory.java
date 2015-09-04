package tom.cache.impl;

public class CacheFacadeFactory
{
	private static CacheFacade facade;
	private static boolean unitTest;
	
	public static CacheFacade getInstance(int threads, int maxThreads,
			int timeout, int queueLength)
	{
		if ( ! unitTest) 
			return CacheFacade.getInstance(threads, maxThreads, timeout, queueLength);
		else
			if (facade == null)
				throw new RuntimeException("facade has not been set for mocking");
			else
				return facade;
	}

	public static void setFacade(CacheFacade facade)
	{
		CacheFacadeFactory.facade = facade;
	}
}
