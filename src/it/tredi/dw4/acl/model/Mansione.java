package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Mansione extends XmlEntity {
	private String cod;
	private String text;
    
	public Mansione() {}
    
	public Mansione(String xmlMansione) throws Exception {
        this.init(XMLUtil.getDOM(xmlMansione));
    }
    
    public Mansione init(Document domMansione) {
		this.cod = XMLUtil.parseAttribute(domMansione, "mansione/@cod");
		this.text = XMLUtil.parseElement(domMansione, "mansione");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@cod", this.cod);
    	params.put(prefix, this.text);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String nome) {
		this.cod = nome;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}

