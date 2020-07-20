package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Referente extends XmlEntity {
	private String nominativo = "";
	private String cod = "";
	private String ruolo = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.nominativo = 	XMLUtil.parseAttribute(dom, "referente/@nominativo");
		this.cod = 			XMLUtil.parseAttribute(dom, "referente/@cod");
		this.ruolo = 		XMLUtil.parseAttribute(dom, "referente/@ruolo");
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@nominativo", this.nominativo);
    	params.put(prefix+".@cod", this.cod);
    	params.put(prefix+".@ruolo", this.ruolo);
    	return params;
	}

	public void setNominativo(String value) {
		this.nominativo = value;
	}

	public String getNominativo() {
		return nominativo;
	}

	public void setCod(String num) {
		this.cod = num;
	}

	public String getCod() {
		return cod;
	}

	public void setRuolo(String label) {
		this.ruolo = label;
	}

	public String getRuolo() {
		return ruolo;
	}

}
