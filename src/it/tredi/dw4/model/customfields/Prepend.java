package it.tredi.dw4.model.customfields;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Prepend extends XmlEntity {

	private String value = "";
	private boolean fill = false;
	
	@Override
	public XmlEntity init(Document dom) {
		this.value = XMLUtil.parseStrictAttribute(dom, "prepend/@value");
		this.fill = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "prepend/@value", "false"));
		
		return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isFill() {
		return fill;
	}

	public void setFill(boolean fill) {
		this.fill = fill;
	}
	
}
