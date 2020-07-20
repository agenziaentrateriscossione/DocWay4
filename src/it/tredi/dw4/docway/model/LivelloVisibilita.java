package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Definizione di un livello di visibilita' su un documento
 * @author mbernardini
 */
public class LivelloVisibilita extends XmlEntity {

	private String cod = "";
	private String text = "";
	private String labelSingolare = "";
	private String labelPlurale = "";
	private boolean custom = false;
	private boolean selected = false; // indica se il livello e' stato selezionato dall'utente (es. filtro in maschera di ricerca)
	
	@Override
	public XmlEntity init(Document dom) {
		this.cod = XMLUtil.parseStrictAttribute(dom, "visibilita/@cod", "");
		this.text = XMLUtil.parseStrictAttribute(dom, "visibilita/@text", "");
		this.custom = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "visibilita/@custom", "false"));
		this.labelSingolare = XMLUtil.parseStrictElement(dom, "visibilita/singolare", true);
		this.labelPlurale = XMLUtil.parseStrictElement(dom, "visibilita/plurale", true);
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
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
	
	public String getLabelSingolare() {
		return labelSingolare;
	}

	public void setLabelSingolare(String labelSingolare) {
		this.labelSingolare = labelSingolare;
	}

	public String getLabelPlurale() {
		return labelPlurale;
	}

	public void setLabelPlurale(String labelPlurale) {
		this.labelPlurale = labelPlurale;
	}

	public boolean isCustom() {
		return custom;
	}

	public void setCustom(boolean custom) {
		this.custom = custom;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
