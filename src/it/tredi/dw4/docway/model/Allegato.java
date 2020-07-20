package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Allegato extends XmlEntity {

	private String text;
	private String pagine;
	
	@Override
	public XmlEntity init(Document dom) {
		this.text = XMLUtil.parseElement(dom, "allegato");
		this.pagine = XMLUtil.parseAttribute(dom, "allegato/@pagine");
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix, this.text);
    	params.put(prefix+".@pagine", this.pagine);
    	return params;
	}

	public void setText(String query) {
		this.text = query;
	}

	public String getText() {
		return text;
	}

	public void setPagine(String pagine) {
		this.pagine = pagine;
	}

	public String getPagine() {
		return pagine;
	}

}
