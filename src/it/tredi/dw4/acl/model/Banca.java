package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Banca extends XmlEntity {
	private String iban;
	private String bban;
    
	public Banca() {}
    
	public Banca(String xmlBanca) throws Exception {
        this.init(XMLUtil.getDOM(xmlBanca));
    }
    
    public Banca init(Document domBanca) {
    	this.iban = XMLUtil.parseAttribute(domBanca, "banca/@iban");
    	this.bban = XMLUtil.parseAttribute(domBanca, "banca/@bban");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.iban ) params.put(prefix+".@iban", this.iban);
    	if (null != this.bban ) params.put(prefix+".@bban", this.bban);
    	return params;
    }
    
    public String getIban() {
		return iban;
	}

	public void setIban(String spec) {
		this.iban = spec;
	}

	public void setBban(String bban) {
		this.bban = bban;
	}

	public String getBban() {
		return bban;
	}
}

