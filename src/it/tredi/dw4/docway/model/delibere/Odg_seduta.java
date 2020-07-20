package it.tredi.dw4.docway.model.delibere;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Odg_seduta extends XmlEntity {
	
	private String data_convocazione = "";
	private String nrecord_sed = "" ;
	private String stato = "";

	@Override
	public XmlEntity init(Document dom) {
		this.data_convocazione 				=	XMLUtil.parseAttribute(dom, "odg_seduta/@data_convocazione"); 
		this.nrecord_sed					=	XMLUtil.parseAttribute(dom, "odg_seduta/@nrecord_sed");
		this.stato							=	XMLUtil.parseAttribute(dom, "odg_seduta/@stato");
				
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * getter / setter
	 * */
	public String getData_convocazione() {
		return data_convocazione;
	}

	public void setData_convocazione(String data_convocazione) {
		this.data_convocazione = data_convocazione;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getNrecord_sed() {
		return nrecord_sed;
	}

	public void setNrecord_sed(String nrecord_sed) {
		this.nrecord_sed = nrecord_sed;
	}

}
