package tom.peregrine.bypass;

import tom.peregrine.server.Plugin;

public class JavascriptBypassPlugin extends Plugin
{
	@Override
	public boolean retrieveFromCache(String[] headers)
	{
		for (String hdr : headers)
		{
			if (hdr.startsWith("GET"))
			{
				String[] toks = hdr.split("\\s+");
				if (toks[1].endsWith(".js"))
				{
					return true;
				}
			}
		}
		return false;
	}
}
