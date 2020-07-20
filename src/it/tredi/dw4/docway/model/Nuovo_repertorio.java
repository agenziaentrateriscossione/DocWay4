package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Nuovo_repertorio extends XmlEntity {
	private String cod = "";
    
	public Nuovo_repertorio() {}
    
	public Nuovo_repertorio(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Nuovo_repertorio init(Document dom) {
    	this.cod = XMLUtil.parseAttribute(dom, "nuovo_repertorio/@cod");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod", this.cod);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}
	
}

