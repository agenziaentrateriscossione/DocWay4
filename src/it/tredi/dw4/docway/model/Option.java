package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Option extends XmlEntity {

	private String value;
	private String selected;
	private String label;
	
	@Override
	public XmlEntity init(Document dom) {
		this.value = XMLUtil.parseAttribute(dom, "option/@value");
		this.selected = XMLUtil.parseAttribute(dom, "option/@selected");
		this.label = XMLUtil.parseElement(dom, "option");
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setSelected(String bool) {
		this.selected = bool;
	}

	public String getSelected() {
		return selected;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
