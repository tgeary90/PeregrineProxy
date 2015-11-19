package tom.apps.proxy.bypass;

import tom.apps.proxy.server.Plugin;

/**
 * Implementation of the Plugin for deciding on whether
 * content is HTML in nature.
 * @author tom
 *
 */
public class HtmlBypassPlugin extends Plugin
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
				if (toks[1].endsWith(".html") || toks[1].endsWith(".htm"))
				{
					fromCache = true;
					break;
				}
			}
		}
		return fromCache;
	}
}
