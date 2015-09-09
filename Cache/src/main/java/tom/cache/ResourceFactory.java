package tom.cache;

public interface ResourceFactory {
	Resource getInstance(String url, String[] headers);
}
