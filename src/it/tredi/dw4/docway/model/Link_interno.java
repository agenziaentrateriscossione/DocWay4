package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Link_interno extends XmlEntity {
	private String href;
	private String text;
	private String remove;
	private String alias;
	private boolean fromRifiutoArrivo = false;
	
	public Link_interno() {}
    
	public Link_interno(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Link_interno init(Document dom) {
    	this.href = XMLUtil.parseAttribute(dom, "link_interno/@href");
    	this.remove = XMLUtil.parseAttribute(dom, "link_interno/@remove");
    	this.alias = XMLUtil.parseAttribute(dom, "link_interno/@alias");
    	this.text = XMLUtil.parseElement(dom, "link_interno");
    	this.fromRifiutoArrivo = Boolean.parseBoolean(XMLUtil.parseElement(dom, "link_interno/@fromRifiutoArrivo"));
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@href", this.href);
    	params.put(prefix, this.text);
    	return params;
    }
    
    public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setText(String value) {
		this.text = value;
	}

	public String getText() {
		return text;
	}
	
	public void setRemove(String remove) {
		this.remove = remove;
	}

	public String getRemove() {
		return remove;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public boolean isFromRifiutoArrivo() {
		return fromRifiutoArrivo;
	}

	public void setFromRifiutoArrivo(boolean fromRifiutoArrivo) {
		this.fromRifiutoArrivo = fromRifiutoArrivo;
	}
	
}

