package it.tredi.dw4.acl.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Sede extends XmlEntity {

	private String codammaoo = "";
	private boolean defaultsede = false; 
	private boolean selected = false;
	
	@Override
	public XmlEntity init(Document dom) {
		this.codammaoo 		= XMLUtil.parseStrictAttribute(dom, "sede/@cod");
    	this.defaultsede 	= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "sede/@default"));
    	this.selected		= false;
    	
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getCodammaoo() {
		return codammaoo;
	}

	public void setCodammaoo(String codammaoo) {
		this.codammaoo = codammaoo;
	}

	public boolean isDefaultsede() {
		return defaultsede;
	}

	public void setDefaultsede(boolean defaultsede) {
		this.defaultsede = defaultsede;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
