package it.tredi.dw4.docway.model.delibere;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Categoria_select extends XmlEntity {
	
	private String cod = "";
	private String text = "";
	private String selected = "";

	@Override
	public XmlEntity init(Document dom) {
		this.cod		=	XMLUtil.parseAttribute(dom, "categoria_select/@cod");
		this.text		=	XMLUtil.parseElement(dom, "categoria_select");
		this.selected	=	XMLUtil.parseAttribute(dom, "categoria_select/@selected", "");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	/*
	 * getter / setter
	 * */
	
	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
	
}
