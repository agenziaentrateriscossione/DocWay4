package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Xlink extends XmlEntity {
	private String href;
	private String text;
	private String remove;
	private String alias;
	
	public Xlink() {}
    
	public Xlink(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Xlink init(Document dom) {
    	this.href = XMLUtil.parseAttribute(dom, "xlink/@href");
    	this.remove = XMLUtil.parseAttribute(dom, "xlink/@remove");
    	this.alias = XMLUtil.parseAttribute(dom, "xlink/@alias");
    	this.text = XMLUtil.parseElement(dom, "xlink");
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

	public void setHref(String cod) {
		this.href = cod;
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
	
}

