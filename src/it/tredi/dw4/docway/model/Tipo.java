package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Tipo extends XmlEntity {

	private String categoria = "";
	private List<Option> tipi = new ArrayList<Option>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		categoria 	= XMLUtil.parseStrictAttribute(dom, "tipo/@categoria");
		tipi 		= XMLUtil.parseSetOfElement(dom, "tipo/option", new Option());
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public List<Option> getTipi() {
		return tipi;
	}

	public void setTipi(List<Option> tipi) {
		this.tipi = tipi;
	}

}
