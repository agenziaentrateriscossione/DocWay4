package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Conservazione extends XmlEntity {

	private String tipo = " "; // TODO problema di validazione con stringa vuota in jsf h:selectOneRadio. Per il momento valore trimmato in asFormAdapterParams()
	
	public Conservazione() {}
    
	public Conservazione(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Conservazione init(Document dom) {
    	this.tipo = XMLUtil.parseAttribute(dom, "conservazione/@tipo");
    	
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@tipo", this.tipo.trim());
    	
    	return params;
    }

	public String getTipo() {
		if (tipo.equals(""))
			tipo = " ";
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
}
