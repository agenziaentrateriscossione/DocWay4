package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Responsabile extends XmlEntity {
	private String cod_uff = "";
	private String matricola = "";
    private String nome_pers = "";
    private String nome_uff = "";
    private String nome_ruolo = "";
    private String cod_ruolo = "";
    private String daCopiaConoscenza;
    private String daDestinatario;
    private String daMittente;
	
	public Responsabile() {}
    
	public Responsabile(String xmlResponsabile) throws Exception {
        this.init(XMLUtil.getDOM(xmlResponsabile));
    }
    
    public Responsabile init(Document domResponsabile) {
    	this.cod_uff 			= XMLUtil.parseAttribute(domResponsabile, "responsabile/@cod_uff");
    	this.matricola 			= XMLUtil.parseAttribute(domResponsabile, "responsabile/@matricola");
    	this.nome_pers 			= XMLUtil.parseAttribute(domResponsabile, "responsabile/@nome_pers");
    	this.nome_uff 			= XMLUtil.parseAttribute(domResponsabile, "responsabile/@nome_uff");
    	this.nome_ruolo			= XMLUtil.parseAttribute(domResponsabile, "responsabile/@nome_ruolo");
    	this.cod_ruolo			= XMLUtil.parseAttribute(domResponsabile, "responsabile/@cod_ruolo");
    	this.daCopiaConoscenza	= XMLUtil.parseAttribute(domResponsabile, "responsabile/@daCopiaConoscenza");
    	this.daDestinatario		= XMLUtil.parseAttribute(domResponsabile, "responsabile/@daDestinatario");
    	this.daMittente			= XMLUtil.parseAttribute(domResponsabile, "responsabile/@daMittente");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.cod_uff ) 			params.put(prefix+".@cod_uff", this.cod_uff.trim());
    	if (null != this.matricola ) 		params.put(prefix+".@matricola", this.matricola.trim());
    	if (null != this.nome_pers ) 		params.put(prefix+".@nome_pers", this.nome_pers.trim());
    	if (null != this.nome_uff ) 		params.put(prefix+".@nome_uff", this.nome_uff.trim());
    	if (null != this.nome_ruolo )		params.put(prefix+".@nome_ruolo", this.nome_ruolo.trim());
    	if (null != this.cod_ruolo )		params.put(prefix+".@cod_ruolo", this.cod_ruolo.trim());
    	if (null != this.daCopiaConoscenza) params.put(prefix+".@daCopiaConoscenza", this.daCopiaConoscenza);
    	if (null != this.daDestinatario ) 	params.put(prefix+".@daDestinatario", this.daDestinatario);
    	if (null != this.daMittente) 		params.put(prefix+".@daMittente", this.daMittente);
    	return params;
    }
    
    public String getCod_uff() {
		return cod_uff;
	}

	public void setCod_uff(String cod_uff) {
		this.cod_uff = cod_uff;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getMatricola() {
		return matricola;
	}

	public void setNome_pers(String nome_pers) {
		this.nome_pers = nome_pers;
	}

	public String getNome_pers() {
		return nome_pers;
	}

	public void setNome_uff(String nome_uff) {
		this.nome_uff = nome_uff;
	}

	public String getNome_uff() {
		return nome_uff;
	}
	
	public void setNome_ruolo(String ruolo) {
		this.nome_ruolo = ruolo;
	}

	public String getNome_ruolo() {
		return nome_ruolo;
	}
	
	public void setCod_ruolo(String cod_ruolo) {
		this.cod_ruolo = cod_ruolo;
	}

	public String getCod_ruolo() {
		return cod_ruolo;
	}

	public void setDaCopiaConoscenza(String daCopiaConoscenza) {
		this.daCopiaConoscenza = daCopiaConoscenza;
	}

	public String getDaCopiaConoscenza() {
		return daCopiaConoscenza;
	}

	public void setDaDestinatario(String daDestinatario) {
		this.daDestinatario = daDestinatario;
	}

	public String getDaDestinatario() {
		return daDestinatario;
	}

	public void setDaMittente(String daMittente) {
		this.daMittente = daMittente;
	}

	public String getDaMittente() {
		return daMittente;
	}
	
}

