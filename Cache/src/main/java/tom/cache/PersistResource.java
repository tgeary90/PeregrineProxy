package tom.cache;

import org.jdom2.Element;

public interface PersistResource 
{
	public abstract void serialize(Resource res, Element e);
	public abstract void deserialize(Resource res, Element e);
	public abstract String getRoot(Resource res);
}
