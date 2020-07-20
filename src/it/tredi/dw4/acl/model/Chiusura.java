package it.tredi.dw4.acl.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Chiusura extends XmlEntity {

	private String oper;
	private String cod_oper;
	private String cod_uff_oper;
	private String azione;
	private Date data;
	private String ora;
	private String uff_oper;
    
	public Chiusura() {}
    
	public Chiusura(String xmlChiusura) throws Exception {
        this.init(XMLUtil.getDOM(xmlChiusura));
    }
    
    public Chiusura init(Document domChiusura) {
    	this.oper 			= XMLUtil.parseAttribute(domChiusura, "chiusura/@oper");
    	this.cod_oper 		= XMLUtil.parseAttribute(domChiusura, "chiusura/@cod_oper");
    	this.uff_oper 		= XMLUtil.parseAttribute(domChiusura, "chiusura/@uff_oper");
    	this.cod_uff_oper 	= XMLUtil.parseAttribute(domChiusura, "chiusura/@cod_uff_oper");
    	this.azione 		= XMLUtil.parseAttribute(domChiusura, "chiusura/@azione");
    	this.data 			= XMLUtil.parseAttributeDate(domChiusura, "chiusura/@data");
    	this.ora 			= XMLUtil.parseAttribute(domChiusura, "chiusura/@ora");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	return new HashMap<String, String>();
    }
    
    public String getOper() {
		return oper;
	}

	public void setOper(String nome) {
		this.oper = nome;
	}

	public void setCod_oper(String nome_uff) {
		this.cod_oper = nome_uff;
	}

	public String getCod_oper() {
		return cod_oper;
	}

	public void setCod_uff_oper(String qualifica) {
		this.cod_uff_oper = qualifica;
	}

	public String getCod_uff_oper() {
		return cod_uff_oper;
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

	public void setUff_oper(String uff_oper) {
		this.uff_oper = uff_oper;
	}

	public String getUff_oper() {
		return uff_oper;
	}
}

