package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Keywords extends XmlEntity {
	private String text = "";
    
	public Keywords() {}
    
	public Keywords(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Keywords init(Document dom) {
    	this.text = XMLUtil.parseElement(dom, "keywords");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix, this.text);
    	return params;
    }
    
    public String getText() {
		return text;
	}

	public void setText(String cod) {
		this.text = cod;
	}
	
}

