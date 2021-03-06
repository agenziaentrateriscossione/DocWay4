package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Chkin extends XmlEntity {
	private String operatore;
	private String cod_operatore;
	private String data;
	private String ora;
    
	public Chkin() {}
    
	public Chkin(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Chkin init(Document dom) {
    	this.operatore = XMLUtil.parseAttribute(dom, "chkin/@operatore");
    	this.cod_operatore = XMLUtil.parseAttribute(dom, "chkin/@cod_operatore");
    	this.data = XMLUtil.parseAttribute(dom, "chkin/@data");
    	this.ora = XMLUtil.parseAttribute(dom, "chkin/@ora");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
//    	params.put(prefix+".@operatore", this.operatore);
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
}

