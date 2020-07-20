package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Voce_indice extends XmlEntity {
	private String text = "";
	private String workflows = "";
    
	public Voce_indice() {}
    
	public Voce_indice(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Voce_indice init(Document dom) {
    	this.text = XMLUtil.parseElement(dom, "voce_indice");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix, this.text);
    	params.put("*" + prefix.substring(1) + ".workflows", this.workflows);
    	return params;
    }
    
    public String getText() {
		return text;
	}

	public void setText(String cod) {
		this.text = cod;
	}

	public String getWorkflows() {
		return workflows;
	}

	public void setWorkflows(String workflows) {
		this.workflows = workflows;
	}
	
}

