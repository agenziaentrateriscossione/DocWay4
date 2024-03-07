package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class HomeContentDefinition extends XmlEntity {
	
	private String type = "";
	private String title = "";
	private String customQuery = "";

	@Override
	public XmlEntity init(Document dom) {
		this.type = XMLUtil.parseStrictAttribute(dom, "homeContent/@type");
		this.title = XMLUtil.parseStrictAttribute(dom, "homeContent/@title");
		this.customQuery = XMLUtil.parseStrictElement(dom, "homeContent/query");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCustomQuery() {
		return customQuery;
	}

	public void setCustomQuery(String customQuery) {
		this.customQuery = customQuery;
	}

}
