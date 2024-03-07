package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Mailbox_out extends XmlEntity {
	private String email = "";
	private String host = "";
    private String login = "";
    private String password = "";
    private String protocol = "";
    private String port = "";
	
	public Mailbox_out() {}
    
	public Mailbox_out(String xmlMailbox) throws Exception {
        this.init(XMLUtil.getDOM(xmlMailbox));
    }
    
    public Mailbox_out init(Document domMailbox) {
    	this.email 		= XMLUtil.parseAttribute(domMailbox, "mailbox_out/@email");
    	this.host 		= XMLUtil.parseAttribute(domMailbox, "mailbox_out/@host");
    	this.login 		= XMLUtil.parseAttribute(domMailbox, "mailbox_out/@login");
    	this.password 	= XMLUtil.parseAttribute(domMailbox, "mailbox_out/@password");
    	this.protocol 	= XMLUtil.parseAttribute(domMailbox, "mailbox_out/@protocol");
    	this.port	 	= XMLUtil.parseAttribute(domMailbox, "mailbox_out/@port");
        
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.email ) 	params.put(prefix+".@email", this.email.trim());
    	if (null != this.host ) 	params.put(prefix+".@host", this.host.trim());
    	if (null != this.login ) 	params.put(prefix+".@login", this.login.trim());
    	
    	// mbernardini 06/07/2016 : impossibilita' di svuotare il campo password sulle caselle di posta elettronica
    	if (this.password != null) params.put("*mailbox_out.password", this.password.trim());
    	
    	if (null != this.protocol ) params.put(prefix+".@protocol", this.protocol.trim());
    	if (null != this.port ) params.put(prefix+".@port", this.port.trim());
    	return params;
    }
    
    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
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

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPort() {
		return port;
	}
}

