package it.tredi.dw4.model.customfields;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;
import org.dom4j.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryField extends XmlEntity {

	private String label = "";
	private String helpmessage = "";
	private String type = "";
	private String xpath = "";
	
	// utilizzato per le ricerche su campi base (es. testo, textarea)
	private String value = "";
	
	// utilizzati per le ricerche su range (es. date)
	private String valuefrom = "";
	private String valueto = "";
	
	private List<Item> items = new ArrayList<Item>(); // utilizzato per campi checkbox, radio o select
	private String size = "";
	
	@Override
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom) {
		this.label 			= XMLUtil.parseStrictAttribute(dom, "field/@label");
		this.helpmessage	= XMLUtil.parseStrictAttribute(dom, "field/@helpmessage");
		this.type 			= XMLUtil.parseStrictAttribute(dom, "field/@type");
		this.size	 		= XMLUtil.parseStrictAttribute(dom, "field/input/@size");
		this.xpath 			= XMLUtil.parseStrictAttribute(dom, "field/input/@path");
		this.items 			= XMLUtil.parseSetOfElement(dom, "field/input/item", new Item());

		return this;
	}

	/**
	 * aggiunta dei valori dei campi al formAdapter per salvataggio dei 
	 * campi custom 
	 */
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	/**
	 * pulizia dei valori caricati sul campo. vengono presi in considerazione tutte le tipologie di campo
	 */
	public void cleanValue() {
		this.value = "";
		this.valuefrom = "";
		this.valueto = "";
		
		if (items != null && items.size() > 0) {
			for (int i=0; i<items.size(); i++) {
				Item item = (Item) items.get(i);
				if (item != null)
					item.setSelected(false);
			}
		}
	}
		
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	
	public String getHelpmessage() {
		return helpmessage;
	}

	public void setHelpmessage(String helpmessage) {
		this.helpmessage = helpmessage;
	}
	
	public String getValuefrom() {
		return valuefrom;
	}

	public void setValuefrom(String valuefrom) {
		this.valuefrom = valuefrom;
	}

	public String getValueto() {
		return valueto;
	}

	public void setValueto(String valueto) {
		this.valueto = valueto;
	}

}
