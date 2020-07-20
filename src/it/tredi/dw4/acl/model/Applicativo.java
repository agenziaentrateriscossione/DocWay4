package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Applicativo extends XmlEntity {
	private String cod;
	private String text;
    
	public Applicativo() {}
    
	public Applicativo(String xmlApplicativo) throws Exception {
        this.init(XMLUtil.getDOM(xmlApplicativo));
    }
    
    public Applicativo init(Document domApplicativo) {
    	this.cod = XMLUtil.parseAttribute(domApplicativo, "applicativo/@cod");
    	this.text = XMLUtil.parseElement(domApplicativo, "applicativo");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.cod ) params.put(prefix+".@cod", this.cod);
    	if (null != this.text ) params.put(prefix, this.text);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String spec) {
		this.cod = spec;
	}

	public void setText(String bban) {
		this.text = bban;
	}

	public String getText() {
		return text;
	}
}

