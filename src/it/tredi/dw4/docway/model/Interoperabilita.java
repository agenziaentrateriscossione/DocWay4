package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Interoperabilita extends XmlEntity {
	private String name = "";
	private String title = "";
	private String data = "";
	private String ora = "";
	private String info = "";
	
	public Interoperabilita() {}
    
	public Interoperabilita(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Interoperabilita init(Document dom) {
    	this.name = XMLUtil.parseAttribute(dom, "interoperabilita/@name");
    	this.title = XMLUtil.parseAttribute(dom, "interoperabilita/@title");
    	this.data = XMLUtil.parseAttribute(dom, "interoperabilita/@data");
    	this.ora = XMLUtil.parseAttribute(dom, "interoperabilita/@ora");
    	this.info = XMLUtil.parseAttribute(dom, "interoperabilita/@info");

        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	// non utilizzato (oggetto di model gestito in showdoc)
    	return new HashMap<String, String>();
    	
    	/*
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.name != null && this.name.length() > 0)
    		params.put(prefix+".@name", this.name);
    	if (this.title != null && this.title.length() > 0)
    		params.put(prefix+".@title", this.title);
    	if (this.data != null && this.data.length() > 0)
    		params.put(prefix+".@data", this.data);
    	if (this.ora != null && this.ora.length() > 0)
    		params.put(prefix+".@ora", this.ora);
    	if (this.info != null && this.info.length() > 0)
    		params.put(prefix+".@info", this.info);
    	
    	return params;
    	*/
    }
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public String getOra() {
		return ora;
	}
	
	public void setOra(String ora) {
		this.ora = ora;
	}
	
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	
}
