package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class FirmaEngineeringFile extends XmlEntity {

	private String id;
	private String name;
	private String mime;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getMime() {
		return mime;
	}

	@Override
	public XmlEntity init(Document dom) {
		id = XMLUtil.parseStrictAttribute(dom, "file/@id", "-1");
		name = XMLUtil.parseStrictAttribute(dom, "file/@name", "");
		mime = XMLUtil.parseStrictAttribute(dom, "file/@mime", "-1");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	return params;
	}

}
