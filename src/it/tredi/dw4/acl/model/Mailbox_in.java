package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Mailbox_in extends XmlEntity {
	private String email = "";
	private String host = "";
    private String login = "";
    private String password = "";
    private String protocol = "";
    private String port = "";
	
	public Mailbox_in() {}
    
	public Mailbox_in(String xmlMailbox) throws Exception {
        this.init(XMLUtil.getDOM(xmlMailbox));
    }
    
    public Mailbox_in init(Document domMailbox) {
    	this.email 		= XMLUtil.parseAttribute(domMailbox, "mailbox_in/@email");
    	this.host 		= XMLUtil.parseAttribute(domMailbox, "mailbox_in/@host");
    	this.login 		= XMLUtil.parseAttribute(domMailbox, "mailbox_in/@login");
    	this.password 	= XMLUtil.parseAttribute(domMailbox, "mailbox_in/@password");
    	this.protocol 	= XMLUtil.parseAttribute(domMailbox, "mailbox_in/@protocol");
    	this.port		= XMLUtil.parseAttribute(domMailbox, "mailbox_in/@port");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.email ) 	params.put(prefix+".@email", this.email.trim());
    	if (null != this.host ) 	params.put(prefix+".@host", this.host.trim());
    	if (null != this.login ) 	params.put(prefix+".@login", this.login.trim());
    	if (null != this.password ) params.put(prefix+".@password", this.password.trim());
    	if (null != this.protocol ) params.put(prefix+".@protocol", this.protocol.trim());
    	if (null != this.port ) 	params.put(prefix+".@port", this.port.trim());
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

