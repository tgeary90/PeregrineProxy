package tom.apps.proxy.bypass;

import tom.apps.proxy.server.Plugin;

public class CssBypassPlugin extends Plugin
{
	@Override
	public boolean retrieveFromCache(String[] headers)
	{
		for (String hdr : headers)
		{
			if (hdr.startsWith("GET"))
			{
				String[] toks = hdr.split("\\s+");
				if (toks[1].endsWith(".css"))
				{
					return true;
				}
			}
		}
		return false;
	}
}
