package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Dati relativi ad un gestore di una mailbox di archiviazione/interoperabilita'
 */
public class GestoreMailbox extends XmlEntity {
	
	// livelli di gestione di una mailbox
	public final static String LIVELLO_TITOLARE = "titolare"; // diritto di modifica su tutti i dati della mailbox
	public final static String LIVELLO_CAMBIO_PASSWORD = "cambiopwd"; // diritto di cambio password

	private String matricola = "";
    private String nome_pers = "";
    private String livello = "";
	
	@Override
	public XmlEntity init(Document dom) {
    	this.matricola 	= XMLUtil.parseAttribute(dom, "gestore/@matricola");
    	this.nome_pers 	= XMLUtil.parseAttribute(dom, "gestore/@nome_pers");
    	this.livello 	= XMLUtil.parseAttribute(dom, "gestore/@livello");
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (null != this.matricola ) 	params.put(prefix+".@matricola", this.matricola.trim());
    	if (null != this.nome_pers ) 	params.put(prefix+".@nome_pers", this.nome_pers.trim());
    	if (null != this.livello ) 		params.put(prefix+".@livello", this.livello.trim());
    	
    	return params;
	}

	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getNome_pers() {
		return nome_pers;
	}

	public void setNome_pers(String nome_pers) {
		this.nome_pers = nome_pers;
	}
	
	public String getLivello() {
		return livello;
	}

	public void setLivello(String livello) {
		this.livello = livello;
	}
	
}
