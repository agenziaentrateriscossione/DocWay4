package it.tredi.dw4.docwayproc.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Oggetto extends XmlEntity {

	private String text = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.text = XMLUtil.parseStrictElement(dom, "oggetto");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.text != null && this.text.length() > 0)
    		params.put(prefix, this.text);
    	
    	return params;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
