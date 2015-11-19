package tom.apps.proxy;

import tom.apps.cache.Resource;
import tom.apps.cache.ResourceFactory;
import tom.apps.cache.impl.WebResource;

public class WebResourceFactory  implements ResourceFactory {

	public Resource getInstance(String url, String[] headers) {
		return new WebResource(url, headers);
	}

}
