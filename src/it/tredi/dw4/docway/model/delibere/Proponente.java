package it.tredi.dw4.docway.model.delibere;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Proponente extends XmlEntity {

	private String nome_persona = "";
	private String nome_uff = "";
	
	@Override
	public XmlEntity init(Document dom) {
		
		this.nome_persona = 		XMLUtil.parseAttribute(dom, "proponente/@nome_persona");
		this.nome_uff = 		XMLUtil.parseAttribute(dom, "proponente/@nome_uff");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".proposta.proponente.@nome_persona", this.nome_persona);
    	params.put(prefix + ".proposta.proponente.@nome_uff", this.nome_uff);
		return params;
	}
	
	/*
	 * getter / setter
	 * */
	public String getNome_persona() {
		return nome_persona;
	}

	public void setNome_persona(String nome_persona) {
		this.nome_persona = nome_persona;
	}

	public String getNome_uff() {
		return nome_uff;
	}

	public void setNome_uff(String nome_uff) {
		this.nome_uff = nome_uff;
	}

}
