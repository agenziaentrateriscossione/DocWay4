package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Societa extends XmlEntity {
	private String cod;
	private String text = "";
	private String selected;
    
	public Societa() {}
    
	public Societa(String xmlSocieta) throws Exception {
        this.init(XMLUtil.getDOM(xmlSocieta));
    }
    
    public Societa init(Document domSocieta) {
    	this.cod = XMLUtil.parseAttribute(domSocieta, "societa/@cod");
    	this.text = XMLUtil.parseElement(domSocieta, "societa");
    	this.selected = XMLUtil.parseAttribute(domSocieta, "societa/@selected");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.cod ) params.put("_CODSOCIETA_", this.cod);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getSelected() {
		return selected;
	}
}

