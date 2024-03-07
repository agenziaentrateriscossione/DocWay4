package it.tredi.dw4.docway.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Abrogazione extends XmlEntity {

	private String operatore;
	private String cod_operatore;
	private String azione;
	private Date data;
	private String ora;
	private String motivazione;
	private String delegato;

	public Abrogazione() {}

	public Abrogazione(String xmlCreazione) throws Exception {
        this.init(XMLUtil.getDOM(xmlCreazione));
    }

    public Abrogazione init(Document domCreazione) {
    	this.operatore 			= XMLUtil.parseAttribute(domCreazione, "abrogazione/@operatore");
    	this.cod_operatore 		= XMLUtil.parseAttribute(domCreazione, "abrogazione/@cod_operatore");
    	this.motivazione 		= XMLUtil.parseElement(domCreazione, "abrogazione");
    	this.data 				= XMLUtil.parseAttributeDate(domCreazione, "abrogazione/@data");
    	this.ora 				= XMLUtil.parseAttribute(domCreazione, "abrogazione/@ora");
    	this.delegato			= XMLUtil.parseAttribute(domCreazione, "abrogazione/@delegato");
        return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	return new HashMap<String, String>();
    }

    public String getOperatore() {
		return operatore;
	}

	public void setOperatore(String nome) {
		this.operatore = nome;
	}

	public void setCod_operatore(String nome_uff) {
		this.cod_operatore = nome_uff;
	}

	public String getCod_operatore() {
		return cod_operatore;
	}

	public void setAzione(String azione) {
		this.azione = azione;
	}

	public String getAzione() {
		return azione;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getData() {
		return data;
	}

	public String getDateLong(){
		if ( null != data ) return DateUtil.getLongDate(data);
		else				return null;
	}


	public void setOra(String ora) {
		this.ora = ora;
	}

	public String getOra() {
		return ora;
	}

	public void setMotivazione(String uff_oper) {
		this.motivazione = uff_oper;
	}

	public String getMotivazione() {
		return motivazione;
	}

	public String getDelegato() {
		return delegato;
	}

	public void setDelegato(String delegato) {
		this.delegato = delegato;
	}
}

