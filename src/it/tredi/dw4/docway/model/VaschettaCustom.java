package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class VaschettaCustom extends XmlEntity {

	private String title = "";
	private String query = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.title = XMLUtil.parseStrictAttribute(dom, "vaschetta/@title");
		this.query = XMLUtil.parseStrictElement(dom, "vaschetta");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.title ) 	params.put(prefix+".@title", this.title);
    	if (null != this.query ) 	params.put(prefix, this.query);
    	return params;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String nome) {
		this.title = nome;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}
