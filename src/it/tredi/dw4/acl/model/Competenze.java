package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Competenze extends XmlEntity {
	private String text;
    
	public Competenze() {}
    
	public Competenze(String xmlCompetenze) throws Exception {
        this.init(XMLUtil.getDOM(xmlCompetenze));
    }
    
    public Competenze init(Document domCompetenze) {
		this.text = XMLUtil.parseElement(domCompetenze, "competenze", false);
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if ( null != this.text ) params.put(prefix, this.text);
    	return params;
    }
    
    public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}

