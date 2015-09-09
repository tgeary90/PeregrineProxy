package tom.cache;

import tom.cache.Resource;

public interface Cache {
	void retrieve(Resource resource, CacheClient client);
}
