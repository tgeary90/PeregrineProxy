package tom.cache.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.UUID;

import org.jdom2.Attribute;
import org.jdom2.Element;

import tom.cache.CacheProperties;
import tom.cache.PersistResource;
import tom.cache.Resource;

public class AsXml implements PersistResource {
	public void serialize(Resource resource, Element e) {
		Element resourceNode = new Element(getRoot(resource));

		// add attribute
		resourceNode.setAttribute(new Attribute("id", String.valueOf(resource.getId())));

		// add child nodes
		Element keyNode = new Element("key");
		keyNode.addContent(resource.getKey());
		resourceNode.addContent(keyNode);

		Element lastModNode = new Element("lastmod");
		lastModNode.addContent(String.valueOf(resource.getLastMod().getTimeInMillis()));
		resourceNode.addContent(lastModNode);

		Element cachedDateNode = new Element("cacheddate");
		cachedDateNode.addContent(String.valueOf(resource.getCachedDate().getTimeInMillis()));
		resourceNode.addContent(cachedDateNode);

		Element contentLengthNode = new Element("contentlength");
		contentLengthNode.addContent(String.valueOf(resource.getContentLength()));
		resourceNode.addContent(contentLengthNode);

		Element freshNode = new Element("fresh");
		freshNode.addContent(String.valueOf(resource.getFresh()));
		resourceNode.addContent(freshNode);

		serializeByteArray(resource);
	}

	private void serializeByteArray(Resource resource) {
		Properties props = null;
		try {
			props = CacheProperties.getCacheProps();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		File rootDir = new File(props.getProperty("cache.dir"));
		File persistenceDir = new File(rootDir, "persist");
		if (!persistenceDir.exists())
			persistenceDir.mkdirs();

		try {
			FileOutputStream fos = new FileOutputStream(resource.getId() + ".ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(resource.getData());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public void deserialize(Resource res, Element e) 
	{
		res.setId(UUID.fromString(e.getAttributeValue("id")));
		res.setKey(e.getChildText("key"));
		
		Calendar cal = Calendar.getInstance();
		long lastModDate = Long.parseLong(e.getChildText("lastmod"));
		cal.setTimeInMillis(lastModDate);
		res.setLastMod(cal);
		
		long cachedDate = Long.parseLong(e.getChildText("cacheddate"));
		cal.setTimeInMillis(cachedDate);
		res.setCachedDate(cal);
		
		res.setContentLen(Integer.parseInt(e.getChildText("contentlength")));
		res.setFresh(Boolean.parseBoolean(e.getChildText("fresh")));
	}

	public String getRoot(Resource res) {
		return res.getRoot();
	}
}
