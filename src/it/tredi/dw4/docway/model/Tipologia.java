package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Tipologia extends XmlEntity {
	private String cod = "";
	private String text = ""; // TODO probabilmente non necessario in 'asFormAdapterParams()' - da verificare
    
	public Tipologia() {}
    
	public Tipologia(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Tipologia init(Document dom) {
    	this.cod = XMLUtil.parseAttribute(dom, "tipologia/@cod");
    	this.text = XMLUtil.parseElement(dom, "tipologia");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod", this.cod);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}

