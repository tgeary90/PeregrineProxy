/**
 * File: Cachable.java
 * Creation Date: 31/07/2012
 * Last Modification Date: 07/08/2012
 * @author: Tom Geary 
 * @version 1.1
 *
 * Description: Defines an interface that all Resource classes should 
 * implement if they are to be compatible with the CacheManager class
 */

package tom.apps.cache;

import java.util.Calendar;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;

import tom.apps.cache.impl.AsXml;
import tom.helpers.Jdom2XmlHelper;

public abstract class Resource
{
	private PersistResource serialized = new AsXml();
	private UUID id;
	private String key;
	private Calendar lastMod;
	private Calendar cachedDate;
	private byte[] data;
	private int contentLength;
	private boolean fresh;
	private String url;
	private String[] headers;
	private String contentType;
	
	public abstract String getRoot();
	
	public Resource(String url, String[] headers) {
		this.url = url;
		this.headers = headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	

	
    public PersistResource getSerialized() {
		return serialized;
	}
	public void setSerialized(PersistResource serialized) {
		this.serialized = serialized;
	}
	
	public void serialize()
	{
		Document doc = new Document();
		Element root = new Element(this.getRoot());
		serialized.serialize(this, root);
		Jdom2XmlHelper.outputToFile(this.getId() + ".xml", doc);
	}
	
	public void deserialize()
	{
		Document doc = Jdom2XmlHelper.inputFromFile("resources.xml");
		Element me = doc.getRootElement();
		serialized.deserialize(this, me);
	}
	
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public Calendar getLastMod() {
		return lastMod;
	}
	public Calendar getCachedDate() {
		return cachedDate;
	}
	public byte[] getData() {
		return data;
	}
	public boolean getFresh() {
		return fresh;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	public void setCachedDate(Calendar cachedDate) {
		this.cachedDate = cachedDate;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public void setLastMod(Calendar lastMod) {
		this.lastMod = lastMod;
	}
	public void cachedAt(Calendar cachedDate) {
		this.cachedDate = cachedDate;
	}
	public void setContentLen(int contentLength) {
		this.contentLength = contentLength;
	}
	public void setFresh(boolean fresh) {
		this.fresh = fresh;
	}
}