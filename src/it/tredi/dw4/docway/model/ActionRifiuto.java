package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class ActionRifiuto extends XmlEntity {

	private String operatore;
	private String cod_operatore;
	private String tipo_azione;
	private String data;
	private String ora;
	
	@Override
	public XmlEntity init(Document dom) {
		this.operatore = XMLUtil.parseAttribute(dom, "actionRifiuto/@operatore");
    	this.cod_operatore = XMLUtil.parseAttribute(dom, "actionRifiuto/@cod_operatore");
    	this.tipo_azione = XMLUtil.parseAttribute(dom, "actionRifiuto/@tipo_azione");
    	this.data = XMLUtil.parseAttribute(dom, "actionRifiuto/@data");
    	this.ora = XMLUtil.parseAttribute(dom, "actionRifiuto/@ora");
    	return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// non dovrebbe mai essere necessario passare questi dati
		return null;
	}
	
	public String getOperatore() {
		return operatore;
	}

	public void setOperatore(String operatore) {
		this.operatore = operatore;
	}

	public String getCod_operatore() {
		return cod_operatore;
	}

	public void setCod_operatore(String cod_operatore) {
		this.cod_operatore = cod_operatore;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOra() {
		return ora;
	}

	public void setOra(String ora) {
		this.ora = ora;
	}

	public String getTipo_azione() {
		return tipo_azione;
	}

	public void setTipo_azione(String tipo_azione) {
		this.tipo_azione = tipo_azione;
	}
	
}
