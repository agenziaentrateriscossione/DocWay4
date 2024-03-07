package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class CookieBonita extends XmlEntity {

	private String name = "";
	private String value = "";
	private String path = "";
	private String domain = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.name = XMLUtil.parseStrictAttribute(dom, "cookieBonita/@name");
		this.value = XMLUtil.parseStrictAttribute(dom, "cookieBonita/@value");
		this.path = XMLUtil.parseStrictAttribute(dom, "cookieBonita/@path");
		this.domain = XMLUtil.parseStrictAttribute(dom, "cookieBonita/@domain");
    	
        return this;
	}
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
}
