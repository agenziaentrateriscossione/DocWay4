package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * oggetto di model per recuperare l'elenco di persone associate ad un ruolo
 * 
 * @author mbernardini
 */
public class PersoneRuolo extends XmlEntity {

	private String codRuolo = "";
	private List<PersonaInRuolo> persone = new ArrayList<PersonaInRuolo>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.codRuolo 	= XMLUtil.parseStrictAttribute(dom, "/response/personeRuolo/@codRuolo", "");
		this.persone 	= XMLUtil.parseSetOfElement(dom, "/response/personeRuolo/persona", new PersonaInRuolo());
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getCodRuolo() {
		return codRuolo;
	}

	public void setCodRuolo(String codRuolo) {
		this.codRuolo = codRuolo;
	}

	public List<PersonaInRuolo> getPersone() {
		return persone;
	}

	public void setPersone(List<PersonaInRuolo> persone) {
		this.persone = persone;
	}
	
}
