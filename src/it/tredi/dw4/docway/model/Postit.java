package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Postit extends XmlEntity {
	private String operatore;
	private String cod_operatore;
	private String data;
	private String ora;
	private String text;
    
	public Postit() {}
    
	public Postit(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Postit init(Document dom) {
    	this.operatore = XMLUtil.parseAttribute(dom, "postit/@operatore");
    	this.cod_operatore = XMLUtil.parseAttribute(dom, "postit/@cod_operatore");
    	this.data = XMLUtil.parseAttribute(dom, "postit/@data");
    	this.ora = XMLUtil.parseAttribute(dom, "postit/@ora");
    	this.text = XMLUtil.parseElement(dom, "postit", false);
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@operatore", this.operatore);
    	params.put(prefix+".@cod_operatore", this.cod_operatore);
    	params.put(prefix+".@data", this.data);
    	params.put(prefix+".@ora", this.ora);
    	params.put(prefix, this.text);
    	return params;
    }
    
    public String getOperatore() {
		return operatore;
	}

	public void setOperatore(String cod) {
		this.operatore = cod;
	}

	public void setCod_operatore(String cod_operatore) {
		this.cod_operatore = cod_operatore;
	}

	public String getCod_operatore() {
		return cod_operatore;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setOra(String ora) {
		this.ora = ora;
	}

	public String getOra() {
		return ora;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}

