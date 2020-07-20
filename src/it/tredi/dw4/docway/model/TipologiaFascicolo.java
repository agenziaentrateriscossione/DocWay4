package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

public class TipologiaFascicolo extends XmlEntity {
	private String codice;
	private String descrizione;
    
	public TipologiaFascicolo() {}
    
	public TipologiaFascicolo(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
	public TipologiaFascicolo init(Document dom) {
    	this.codice 		= XMLUtil.parseAttribute(dom, "tipologia/@codice");
    	this.descrizione 	= (XMLUtil.parseElement(dom, "tipologia/descrizione")).trim();
        
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	// Probabilmente non necessario
    	
    	return params;
    }
    
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
	}
}
