package tom.apps.cache;

public interface ResourceFactory {
	Resource getInstance(String url, String[] headers);
}
