package it.tredi.dw4.model.azionimassive;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Possibili valori da associare ad un parametro di una specifica azione massiva (option di un campo select)
 */
public class ParametroOption extends XmlEntity {

	private String value = "";
	private String label = "";
	private boolean selected = false;
	
	
	@Override
	public XmlEntity init(Document dom) {
		this.value = XMLUtil.parseAttribute(dom, "option/@value");
		this.label = XMLUtil.parseAttribute(dom, "option/@label");
		this.selected = StringUtil.booleanValue(XMLUtil.parseAttribute(dom, "option/@selected"));
		
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

	public void setSelected(boolean bool) {
		this.selected = bool;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
}
