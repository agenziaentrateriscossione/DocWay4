package it.tredi.dw4.docway.beans;

import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.model.PersoneRuolo;

import org.dom4j.Document;

/**
 * visualizzazione delle persone appartenenti ad un ruolo
 * 
 * @author mbernardini
 */
public class DocWayShowPersoneRuolo extends Page {

	private String xml = "";
	private PersoneRuolo personeRuolo = new PersoneRuolo();
	private boolean active = false;
	
	public void init(Document dom) {		
    	xml = dom.asXML();
    	personeRuolo.init(dom);
    }
	
	public String close() {
		this.active = false;
		return null;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public PersoneRuolo getPersoneRuolo() {
		return personeRuolo;
	}

	public void setPersoneRuolo(PersoneRuolo personeRuolo) {
		this.personeRuolo = personeRuolo;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public FormsAdapter getFormsAdapter() {
		return null;
	}
	
}
