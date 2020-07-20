package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class PersonalView extends XmlEntity {

	private String codice = "";
	private String descrizione = "";
	private List<Tabella> list_tabelle = new ArrayList<Tabella>();
	private String template = "";
	private String icona = "";
	
	//per Verbale di seduta DocwayDelibere
	private String cod = "";
	
	@Override
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom) {
		this.codice 		= XMLUtil.parseStrictAttribute(dom, "personalView/@codice");
    	this.descrizione 	= (XMLUtil.parseStrictElement(dom, "personalView/descrizione")).trim();
    	this.list_tabelle 	= XMLUtil.parseSetOfElement(dom, "personalView/tabella", new Tabella());
        this.template		= XMLUtil.parseStrictElement(dom, "personalView/template");
        this.icona		= XMLUtil.parseStrictElement(dom, "personalView/icona");
        
        this.cod			= XMLUtil.parseStrictAttribute(dom, "personalView/@cod");
    	
    	return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	// Probabilmente non necessario
    	
    	return params;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public List<Tabella> getList_tabelle() {
		return list_tabelle;
	}

	public void setList_tabelle(List<Tabella> list_tabelle) {
		this.list_tabelle = list_tabelle;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getIcona() {
		return icona;
	}

	public void setIcona(String icona) {
		this.icona = icona;
	}

	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

}
