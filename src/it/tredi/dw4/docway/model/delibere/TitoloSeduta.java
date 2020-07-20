package it.tredi.dw4.docway.model.delibere;

import org.dom4j.Document;

import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;
/*
 * Per adesso non usato in quanto difficile implemenetare la logica sfruttando tutto di Titolo.java...quindi è stato modificato direttamente Titolo.java
 * */

public class TitoloSeduta extends Titolo {
	
	private boolean showSeduta;
	private boolean notShowSeduta;
	private boolean sedutaSospesa;
	private boolean straordinaria;
	
	@Override
	public TitoloSeduta init(Document domTitolo) {
		super.init(domTitolo);
		
		this.setShowSeduta(Boolean.valueOf(XMLUtil.parseAttribute(domTitolo, "titolo/@showSeduta", "")));
		this.setNotShowSeduta(Boolean.valueOf(XMLUtil.parseAttribute(domTitolo, "titolo/@notShowSeduta", "")));
		
		//in xsl fa così per capre che è una seduta
		this.setVisible(String.valueOf(!(this.getVisible().equals("false")) && this.getDb().startsWith("xdocwaydoc") && this.getDbTable().isEmpty() && this.getTesto().contains("U!Seduta")));
		
		if(this.getSplit().get(15).equals("12/12/1999"))
		{
			this.setSedutaSospesa(true);
		}else
		{
			this.setSedutaSospesa(false);
		}
		
		this.setStraordinaria(StringUtil.booleanValue(this.getSplit().get(16)));
		
		return this;
	}

	public boolean isShowSeduta() {
		return showSeduta;
	}

	public void setShowSeduta(boolean showSeduta) {
		this.showSeduta = showSeduta;
	}

	public boolean isNotShowSeduta() {
		return notShowSeduta;
	}

	public void setNotShowSeduta(boolean notShowSeduta) {
		this.notShowSeduta = notShowSeduta;
	}

	public boolean isSedutaSospesa() {
		return sedutaSospesa;
	}

	public void setSedutaSospesa(boolean sedutaSospesa) {
		this.sedutaSospesa = sedutaSospesa;
	}

	public boolean isStraordinaria() {
		return straordinaria;
	}

	public void setStraordinaria(boolean straordinaria) {
		this.straordinaria = straordinaria;
	}

}
