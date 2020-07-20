package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class ApprovazioneDoc extends XmlEntity {

	private String stato = "";
	private String cod_oper = "";
	private String oper = "";
	private String cod_uff = "";
	private String uff_oper = "";
	private String data = "";
	private String ora = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.stato = XMLUtil.parseStrictAttribute(dom, "approvazione/@stato");
		this.cod_oper = XMLUtil.parseStrictAttribute(dom, "approvazione/@cod_oper");
		this.oper = XMLUtil.parseStrictAttribute(dom, "approvazione/@oper");
		this.cod_uff = XMLUtil.parseStrictAttribute(dom, "approvazione/@cod_uff");
		this.uff_oper = XMLUtil.parseStrictAttribute(dom, "approvazione/@uff_oper");
		this.data = XMLUtil.parseStrictAttribute(dom, "approvazione/@data");
		this.ora = XMLUtil.parseStrictAttribute(dom, "approvazione/@ora");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getCod_oper() {
		return cod_oper;
	}

	public void setCod_oper(String cod_oper) {
		this.cod_oper = cod_oper;
	}

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public String getCod_uff() {
		return cod_uff;
	}

	public void setCod_uff(String cod_uff) {
		this.cod_uff = cod_uff;
	}

	public String getUff_oper() {
		return uff_oper;
	}

	public void setUff_oper(String uff_oper) {
		this.uff_oper = uff_oper;
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

}
