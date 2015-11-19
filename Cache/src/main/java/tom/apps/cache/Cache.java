package tom.apps.cache;

import tom.apps.cache.Resource;

public interface Cache {
	void retrieve(Resource resource, CacheClient client);
}
