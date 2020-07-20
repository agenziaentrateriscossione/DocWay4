package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Email extends XmlEntity {
	private String addr;
    
	public Email() {}
    
	public Email(String xmlEmail) throws Exception {
        this.init(XMLUtil.getDOM(xmlEmail));
    }
    
    public Email init(Document domEmail) {
    	this.addr = XMLUtil.parseAttribute(domEmail, "email/@addr");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@addr", this.addr);
    	return params;
    }

    public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

}
