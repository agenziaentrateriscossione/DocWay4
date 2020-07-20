package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Autore extends XmlEntity {
	private String text;
    
	public Autore() {}
    
	public Autore(String xmlAutore) throws Exception {
        this.init(XMLUtil.getDOM(xmlAutore));
    }
    
    public Autore init(Document domAutore) {
		this.text = XMLUtil.parseElement(domAutore, "autore", false);
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix, this.text);
    	return params;
    }
    
    public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}

