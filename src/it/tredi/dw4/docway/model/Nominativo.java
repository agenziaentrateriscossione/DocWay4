package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Nominativo extends XmlEntity {

	private String nome = "";
	private String cognome = "";
	
	@Override
	public XmlEntity init(Document dom) {
		nome 	= XMLUtil.parseAttribute(dom, "nominativo/@nome", "");
		cognome = XMLUtil.parseAttribute(dom, "nominativo/@cognome", "");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (nome != null && nome.length() > 0)
    		params.put(prefix+".@nome", nome);
    	if (cognome != null && cognome.length() > 0)
    		params.put(prefix+".@cognome", cognome);
    	
    	return params;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

}
