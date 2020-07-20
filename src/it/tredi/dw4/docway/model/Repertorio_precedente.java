package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Repertorio_precedente extends XmlEntity {
	private String cod = "";
    
	public Repertorio_precedente() {}
    
	public Repertorio_precedente(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Repertorio_precedente init(Document dom) {
    	this.cod = XMLUtil.parseAttribute(dom, "repertorio_precedente/@cod");
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

