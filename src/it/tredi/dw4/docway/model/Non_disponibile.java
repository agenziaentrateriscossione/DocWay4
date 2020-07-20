package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Non_disponibile extends XmlEntity {
	private String estremi = "";
	private String text = ""; // Utilizzato dalla procedura di riempimento select in docEdit (di norma e' lo stesso valore di cod)
    
	public Non_disponibile() {}
    
	public Non_disponibile(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Non_disponibile init(Document dom) {
    	this.estremi = XMLUtil.parseAttribute(dom, "non_disponibile/@estremi");
    	this.text = XMLUtil.parseElement(dom, "non_disponibile");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	return params;
    }
    
    public String getEstremi() {
		return estremi;
	}

	public void setEstremi(String cod) {
		this.estremi = cod;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String value) {
		this.text = value;
	}
	
}

