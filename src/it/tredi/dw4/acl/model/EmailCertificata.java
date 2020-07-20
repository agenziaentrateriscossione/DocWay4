package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class EmailCertificata extends XmlEntity {
	private String addr;
	private String login;
	private String password;
    
	public EmailCertificata() {}
    
	public EmailCertificata(String xmlEmail) throws Exception {
        this.init(XMLUtil.getDOM(xmlEmail));
    }
    
    public EmailCertificata init(Document domEmail) {
    	this.addr = XMLUtil.parseAttribute(domEmail, "email_certificata/@addr");
    	this.login = XMLUtil.parseAttribute(domEmail, "email_certificata/@login");
    	this.password = XMLUtil.parseAttribute(domEmail, "email_certificata/@password");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if ( null != this.addr )  	 params.put(prefix+".@addr", this.addr);
    	if ( null != this.login )    params.put(prefix+".@login", this.login);
    	if ( null != this.password ) params.put(prefix+".@password", this.password);
    	return params;
    }

    public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

}
