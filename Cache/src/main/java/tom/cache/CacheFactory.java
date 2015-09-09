package tom.cache;

public interface CacheFactory {
	Cache getInstance(int threads, int maxThreads,
			int timeout, int queueLength);
}
