package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.model.PersonaIncaricata;
import it.tredi.dw4.utils.XMLUtil;

/**
 * visualizzazione delle persone che possono prendere in carico il documento
 *
 * @author tiommi
 */
public class DocWayShowPersonePresaInCarico extends Page {

	private String xml = "";
	private List<PersonaIncaricata> personeIncaricate = new ArrayList<PersonaIncaricata>();
	private boolean active = false;
	private boolean modalitaGruppo = false;

	@SuppressWarnings("unchecked")
	public void init(Document dom) {
    	xml = dom.asXML();
    	personeIncaricate = XMLUtil.parseSetOfElement(dom, "/response/personeIncaricate/persona", new PersonaIncaricata());
    	String modalita = XMLUtil.parseElement(dom, "modalitaPresaInCarico");
    	if (modalita != null && modalita.equalsIgnoreCase("gruppo"))
    		modalitaGruppo = true;
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

	public List<PersonaIncaricata> getPersoneIncaricate() {
		return personeIncaricate;
	}

	public void setPersoneRuolo(List<PersonaIncaricata> persone) {
		this.personeIncaricate = persone;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isModalitaGruppo() {
		return modalitaGruppo;
	}

	public void setModalitaGruppo(boolean modalitaGruppo) {
		this.modalitaGruppo = modalitaGruppo;
	}

	@Override
	public FormsAdapter getFormsAdapter() {
		return null;
	}

}
