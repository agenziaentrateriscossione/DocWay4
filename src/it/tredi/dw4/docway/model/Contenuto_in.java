package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Contenuto_in extends XmlEntity {
	private boolean check;
	private String cod;
	private String text;
    
	public Contenuto_in() {}
    
	public Contenuto_in(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Contenuto_in init(Document dom) {
    	this.cod = XMLUtil.parseAttribute(dom, "contenuto_in/@cod");
    	this.text = XMLUtil.parseElement(dom, "contenuto_in");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod", this.cod);
    	params.put(prefix, this.text);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public void setText(String value) {
		this.text = value;
	}

	public String getText() {
		return text;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean isCheck() {
		return check;
	}
}

