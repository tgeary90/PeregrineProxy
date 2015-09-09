package tom.peregrine;

import tom.cache.Resource;
import tom.cache.ResourceFactory;
import tom.cache.impl.WebResource;

public class WebResourceFactory  implements ResourceFactory {

	public Resource getInstance(String url, String[] headers) {
		return new WebResource(url, headers);
	}

}
