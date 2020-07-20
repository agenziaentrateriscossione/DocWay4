package it.tredi.dw4.model;

import it.tredi.dw4.utils.XMLUtil;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

public class FunzioneProdotto extends XmlEntity {

	private String title = "";
	private String details = "";

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDetails() {
		return details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}

	@Override
	public XmlEntity init(Document dom) {
		this.title = 		XMLUtil.parseElement(dom, "title");
		
		this.details = 		"";
		Element elemDetails = XMLUtil.loadElement(dom, "//detail");
		if (elemDetails != null) {
			this.details = 	elemDetails.asXML();
			this.details = 	this.details.replaceAll("<detail>", "");
			this.details = 	this.details.replaceAll("</detail>", "");
		}
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
}
