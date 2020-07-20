package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Right extends XmlEntity {
	private String cod;
	private String label;
	private String value;
	private String size;
	private String type;
	private boolean disable = false;
	private boolean defaultValue;
	private List<Oncheck> onchecks;
	private List<Onuncheck> onunchecks;
	private Disabled disabled = new Disabled();
	
	public Right() {}
    
	public Right(String xmlRight) throws Exception {
        this.init(XMLUtil.getDOM(xmlRight));
    }
    
    @SuppressWarnings("unchecked")
	public Right init(Document domRight) {
    	this.cod 			= XMLUtil.parseAttribute(domRight, "right/@cod");
    	this.label 			= XMLUtil.parseAttribute(domRight, "right/@label");
    	this.size 			= XMLUtil.parseAttribute(domRight, "right/@size");
    	this.type 			= XMLUtil.parseAttribute(domRight, "right/@type");
    	this.value 			= XMLUtil.parseElement(domRight, "right");
    	this.defaultValue 	= Boolean.valueOf(XMLUtil.parseAttribute(domRight, "right/@default"));
    	this.disabled.init(XMLUtil.createDocument(domRight, "//right/disabled"));
    	onchecks			= XMLUtil.parseSetOfElement(domRight, "//right/oncheck", new Oncheck());
    	onunchecks			= XMLUtil.parseSetOfElement(domRight, "//right/onuncheck", new Onuncheck());
    	
    	if("".equals(value) && !this.type.equals("alfa")) value= String.valueOf(defaultValue);
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@cod", this.cod);
    	params.put(prefix + ".@label", this.label);
    	params.put(prefix + ".@size", this.size);
    	params.put(prefix + ".@type", this.type);
    	params.put(prefix + ".@default", String.valueOf(this.defaultValue));
    	params.put(prefix, this.value);
    	return params;
    }
    public String getCod() {
		return cod;
	}

	public void setCod(String spec) {
		this.cod = spec;
	}

	public void setLabel(String bban) {
		this.label = bban;
	}

	public String getLabel() {
		return label;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSize() {
		return size;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setOnchecks(List<Oncheck> onchecks) {
		this.onchecks = onchecks;
	}

	public List<Oncheck> getOnchecks() {
		return onchecks;
	}

	public void setOnunchecks(List<Onuncheck> onunchecks) {
		this.onunchecks = onunchecks;
	}

	public List<Onuncheck> getOnunchecks() {
		return onunchecks;
	}

	public void setDisabled(Disabled disabled) {
		this.disabled = disabled;
	}

	public Disabled getDisabled() {
		return disabled;
	}
	public boolean getSelected(){
		if (this.type.equals("alfa") && this.value.trim().length() > 0 && !this.value.equals("*NHL*"))
			return true;
		return Boolean.valueOf(this.value);
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	public boolean isDisable() {
		return disable;
	}
	
}

