package tom.apps.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CacheProperties 
{
	private static Properties cacheProps;

	private CacheProperties() {}
	
	private static void load() throws IOException
	{
		InputStream fis = CacheProperties.class.getResourceAsStream("peregrine.properties");
		cacheProps.load(fis);
	}
	
	public static Properties getCacheProps() throws IOException
	{
		if (cacheProps == null)
		{
			load();
		}
		return cacheProps;
	}
}
