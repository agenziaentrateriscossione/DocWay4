package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

public class NumeroRaccomandata extends XmlEntity {
	private String text = "";
    
	public NumeroRaccomandata() {}
    
	public NumeroRaccomandata(String xmlNumeroRaccomandata) throws Exception {
        this.init(XMLUtil.getDOM(xmlNumeroRaccomandata));
    }
    
    public NumeroRaccomandata init(Document domNumeroRaccomandata) {
		this.text = XMLUtil.parseElement(domNumeroRaccomandata, "numero_raccomandata");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix, this.text);
    	return params;
    }

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
