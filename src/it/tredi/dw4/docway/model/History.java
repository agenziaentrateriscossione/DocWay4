package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class History extends XmlEntity {

	private String tipo;
	private String azione;
	private String oper;
	private String operatore;
	private String cod_oper;
	private String cod_operatore;
	private String cod_uff;
	private String nome_uff;
	private String cod_persona;
	private String nome_persona;
	private String cod_uff_oper;
	private String data;
	private String ora;
	private String uff_oper;
	private String delegato;

	public History() {}

	public History(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }

    public History init(Document dom) {
    	this.tipo 	 		= XMLUtil.parseAttribute(dom, "item/@tipo");
    	this.azione	 		= XMLUtil.parseAttribute(dom, "item/@azione");
    	this.oper 			= XMLUtil.parseAttribute(dom, "item/@oper");
    	this.operatore		= XMLUtil.parseAttribute(dom, "item/@operatore");
    	this.cod_oper 		= XMLUtil.parseAttribute(dom, "item/@cod_oper");
    	this.cod_operatore	= XMLUtil.parseAttribute(dom, "item/@cod_operatore");
    	this.cod_uff 		= XMLUtil.parseAttribute(dom, "item/@cod_uff");
    	this.nome_uff 		= XMLUtil.parseAttribute(dom, "item/@nome_uff");
    	this.cod_persona	= XMLUtil.parseAttribute(dom, "item/@cod_persona");
    	this.nome_persona	= XMLUtil.parseAttribute(dom, "item/@nome_persona");
    	this.uff_oper 		= XMLUtil.parseAttribute(dom, "item/@uff_oper");
    	this.cod_uff_oper 	= XMLUtil.parseAttribute(dom, "item/@cod_uff_oper");
    	this.data 			= XMLUtil.parseAttribute(dom, "item/@data");
    	this.ora 			= XMLUtil.parseAttribute(dom, "item/@ora");
    	this.delegato 		= XMLUtil.parseAttribute(dom, "item/@delegato");
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

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
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

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}

	public String getOperatore() {
		return operatore;
	}

	public void setCod_operatore(String cod_operatore) {
		this.cod_operatore = cod_operatore;
	}

	public String getCod_operatore() {
		return cod_operatore;
	}

	public void setCod_uff(String cod_uff) {
		this.cod_uff = cod_uff;
	}

	public String getCod_uff() {
		return cod_uff;
	}

	public void setCod_persona(String cod_persona) {
		this.cod_persona = cod_persona;
	}

	public String getCod_persona() {
		return cod_persona;
	}

	public void setNome_uff(String nome_uff) {
		this.nome_uff = nome_uff;
	}

	public String getNome_uff() {
		return nome_uff;
	}

	public void setNome_persona(String nome_persona) {
		this.nome_persona = nome_persona;
	}

	public String getNome_persona() {
		return nome_persona;
	}

	public String getAzione() {
		return azione;
	}

	public void setAzione(String azione) {
		this.azione = azione;
	}

	public String getDelegato() {
		return delegato;
	}

	public void setDelegato(String delegato) {
		this.delegato = delegato;
	}
}

