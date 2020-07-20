package it.tredi.dw4.docway.model.delibere;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class SedutaUtile extends XmlEntity {
	
	private String num;
	private String selid;
	private String data_convocazione;
	private String data_presentazione;
	private String ora_presentazione;

	@Override
	public XmlEntity init(Document dom) {
		this.setNum(XMLUtil.parseAttribute(dom, "seduta_utile/@num"));
		this.setSelid(XMLUtil.parseAttribute(dom, "seduta_utile/@selid"));
		this.setData_convocazione(XMLUtil.parseAttribute(dom, "seduta_utile/@data_convocazione"));
		this.setData_presentazione(XMLUtil.parseAttribute(dom, "seduta_utile/@data_presentazione"));
		this.setOra_presentazione(XMLUtil.parseAttribute(dom, "seduta_utile/@ora_presentazione"));
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getSelid() {
		return selid;
	}

	public void setSelid(String selid) {
		this.selid = selid;
	}

	public String getData_convocazione() {
		return data_convocazione;
	}

	public void setData_convocazione(String data_convocazione) {
		this.data_convocazione = data_convocazione;
	}

	public String getData_presentazione() {
		return data_presentazione;
	}

	public void setData_presentazione(String data_presentazione) {
		this.data_presentazione = data_presentazione;
	}

	public String getOra_presentazione() {
		return ora_presentazione;
	}

	public void setOra_presentazione(String ora_presentazione) {
		this.ora_presentazione = ora_presentazione;
	}

}
