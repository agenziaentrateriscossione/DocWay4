package it.tredi.dw4.docway.model;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Rifiuto extends XmlEntity {

	private String stato;
	private List<ActionRifiuto> actions;

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.stato = XMLUtil.parseAttribute(dom, "rifiuto/@stato");
		this.actions = XMLUtil.parseSetOfElement(dom, "/rifiuto/actionRifiuto", new ActionRifiuto());
    	return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// non dovrebbe mai essere necessario passare questi dati
		return null;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public List<ActionRifiuto> getActions() {
		return actions;
	}

	public void setActions(List<ActionRifiuto> actions) {
		this.actions = actions;
	}
	
}
