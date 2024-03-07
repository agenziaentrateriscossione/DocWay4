package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class AssegnatarioMailBox extends XmlEntity {

	private String cod_uff = "";
	private String matricola = "";
    private String nome_pers = "";
    private String nome_uff = "";
    private String nome_ruolo = "";
    private String cod_ruolo = "";
    private String tipo_uff = "";
    private String daCopiaConoscenza;
    private String daDestinatario;
    private String daMittente;

    private boolean intervento = false;
    private boolean ufficio_completo = false;

	public AssegnatarioMailBox() {}

	public AssegnatarioMailBox(String xmlAssegnatario) throws Exception {
        this.init(XMLUtil.getDOM(xmlAssegnatario));
    }

	public AssegnatarioMailBox init(Document domAssegnatario) {
    	this.cod_uff 			= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@cod_uff");
    	this.matricola 			= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@matricola");
    	this.nome_pers 			= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@nome_pers");
    	this.nome_uff 			= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@nome_uff");
    	this.nome_ruolo			= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@nome_ruolo");
    	this.cod_ruolo			= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@cod_ruolo");
    	this.daCopiaConoscenza	= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@daCopiaConoscenza");
    	this.daDestinatario		= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@daDestinatario");
    	this.daMittente			= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@daMittente");
    	this.tipo_uff 			= XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@tipo_uff");

    	if (XMLUtil.parseAttribute(domAssegnatario, "assegnatario/@intervento").toLowerCase().equals("si"))
    		this.intervento = true;
    	if (this.nome_pers.equals("Tutti"))
    		this.ufficio_completo = true;

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
    	if (null != this.tipo_uff )			params.put(prefix+".@tipo_uff", this.tipo_uff.trim());
    	if (null != this.daCopiaConoscenza) params.put(prefix+".@daCopiaConoscenza", this.daCopiaConoscenza);
    	if (null != this.daDestinatario ) 	params.put(prefix+".@daDestinatario", this.daDestinatario);
    	if (null != this.daMittente) 		params.put(prefix+".@daMittente", this.daMittente);
    	if (this.intervento)
    		params.put(prefix+".@intervento", "si");
    	else
    		params.put(prefix+".@intervento", "no");
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

	public String getTipo_uff() {
		return tipo_uff;
	}

	public void setTipo_uff(String tipo_uff) {
		this.tipo_uff = tipo_uff;
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

	public boolean isIntervento() {
		return intervento;
	}

	public void setIntervento(boolean intervento) {
		this.intervento = intervento;
	}

	public boolean isUfficio_completo() {
		return ufficio_completo;
	}

	public void setUfficio_completo(boolean ufficio_completo) {
		this.ufficio_completo = ufficio_completo;
	}

	private void clear() {
		this.cod_uff = "";
		this.matricola = "";
	    this.nome_pers = "";
	    this.nome_uff = "";
	    this.nome_ruolo = "";
	    this.cod_ruolo = "";
	    this.tipo_uff = "";
	    this.daCopiaConoscenza = null;
	    this.daDestinatario = null;
	    this.daMittente = null;
	    this.intervento = false;
	    this.ufficio_completo = false;
    }

	/**
	 * Imposta il tipo di assegnatario a Ruolo
	 */
	public void changeToRuolo() {
		this.clear();
		this.setTipo_uff("ruolo");
    }

	/**
	 * Imposta il tipo di assegnatario a Ufficio/Persona
	 */
	public void changeToUfficio() {
		this.clear();
		this.setTipo_uff("");
    }

	/**
	 * Imposta o meno l'ufficio completo per l'assegnatario corrente
	 */
	public void setUfficioCompleto() {
		if (ufficio_completo) {
			this.setNome_pers("Tutti");
			this.setMatricola("");
		}
		else {
			this.setNome_pers("");
			this.setMatricola("");
		}
	}
}
