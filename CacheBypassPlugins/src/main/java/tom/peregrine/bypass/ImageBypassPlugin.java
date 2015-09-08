package tom.peregrine.bypass;

import tom.peregrine.server.Plugin;

public class ImageBypassPlugin extends Plugin
{
	@Override
	public boolean retrieveFromCache(String[] headers)
	{
		boolean fromCache = false;
		for (String hdr : headers)
		{
			if (hdr.startsWith("GET"))
			{
				String[] toks = hdr.split("\\s+");
				String path = toks[1];
				if (
						path.endsWith("jpeg") || 
						path.endsWith("png")  || 
						path.endsWith("gif")
					)
				{
					fromCache = true;
					break;
				}
			}
		}
		return fromCache;
	}
}
