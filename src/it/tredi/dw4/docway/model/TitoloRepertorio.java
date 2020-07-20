package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class TitoloRepertorio extends XmlEntity {
	private String codice;
	private String numero;
	private String descrizione;
	private List<Tabella> list_tabelle = new ArrayList<Tabella>();
    
	public TitoloRepertorio() {}
    
	public TitoloRepertorio(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    @SuppressWarnings("unchecked")
	public TitoloRepertorio init(Document dom) {
    	this.codice 		= XMLUtil.parseAttribute(dom, "repertorio/@codice");
    	this.numero 		= XMLUtil.parseAttribute(dom, "repertorio/@numero", ".");
    	this.list_tabelle 	= XMLUtil.parseSetOfElement(dom, "repertorio/tabella", new Tabella());
    	this.descrizione 	= (XMLUtil.parseElement(dom, "repertorio/descrizione")).trim();
        
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	// Probabilmente non necessario
    	
    	return params;
    }
    
	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
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

	public List<Tabella> getList_tabelle() {
		return list_tabelle;
	}

	public void setList_tabelle(List<Tabella> list_tabelle) {
		this.list_tabelle = list_tabelle;
	}
	
}
