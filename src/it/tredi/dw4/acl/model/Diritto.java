package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Diritto extends XmlEntity {
	private String cod;
	private String value;
    
	public Diritto() {}
    
	public Diritto(String xmlDiritto) throws Exception {
        this.init(XMLUtil.getDOM(xmlDiritto));
    }
    
    public Diritto init(Document domDiritto) {
    	this.cod = XMLUtil.parseAttribute(domDiritto, "diritto/@cod");
    	this.value = XMLUtil.parseAttribute(domDiritto, "diritto/@val");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}

