package tom.apps.cache.impl;

import tom.apps.cache.Resource;

public class WebResource extends Resource {

	public WebResource(String url, String[] headers) {
		super(url, headers);
	}

	@Override
	public String getRoot() {
		return "WebResource";
	}

}
