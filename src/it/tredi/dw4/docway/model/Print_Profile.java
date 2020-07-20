package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Print_Profile extends XmlEntity {

	private String title = "";
	private String template = "";
	private boolean defaultProfile = false;
	private String checkProperties = "";
	private String lang = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.title = XMLUtil.parseAttribute(dom, "print_profile/@title");
		this.template = XMLUtil.parseAttribute(dom, "print_profile/@template");
		this.defaultProfile = StringUtil.booleanValue(XMLUtil.parseAttribute(dom, "print_profile/@default"));
		this.checkProperties = XMLUtil.parseAttribute(dom, "print_profile/@checkProperties");
		this.lang = XMLUtil.parseAttribute(dom, "print_profile/@lang");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public boolean isDefaultProfile() {
		return defaultProfile;
	}

	public void setDefaultProfile(boolean defaultProfile) {
		this.defaultProfile = defaultProfile;
	}

	public String getCheckProperties() {
		return checkProperties;
	}

	public void setCheckProperties(String checkProperties) {
		this.checkProperties = checkProperties;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
}
