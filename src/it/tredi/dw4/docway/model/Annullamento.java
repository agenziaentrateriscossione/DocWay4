package it.tredi.dw4.docway.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Annullamento extends XmlEntity {

	private String operatore;
	private String cod_operatore;
	private String azione;
	private Date data;
	private String ora;
	private String motivazione;
    
	public Annullamento() {}
    
	public Annullamento(String xmlCreazione) throws Exception {
        this.init(XMLUtil.getDOM(xmlCreazione));
    }
    
    public Annullamento init(Document domCreazione) {
    	this.operatore 			= XMLUtil.parseAttribute(domCreazione, "annullamento/@operatore");
    	this.cod_operatore 		= XMLUtil.parseAttribute(domCreazione, "annullamento/@cod_operatore");
    	this.motivazione 		= XMLUtil.parseElement(domCreazione, "annullamento", false);
    	this.data 				= XMLUtil.parseAttributeDate(domCreazione, "annullamento/@data");
    	this.ora 				= XMLUtil.parseAttribute(domCreazione, "annullamento/@ora");
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
}

