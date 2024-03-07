package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Stampa allegati da lista titoli: Definizione dei dati relativi ad un singolo allegato di un documento
 */
public class AttachToPrint extends XmlEntity {
	
	private String name;
	private String title;

	@Override
	public XmlEntity init(Document dom) {
		this.name = XMLUtil.parseStrictAttribute(dom, "attachment/@name");
		this.title = XMLUtil.parseStrictAttribute(dom, "attachment/@title");
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
