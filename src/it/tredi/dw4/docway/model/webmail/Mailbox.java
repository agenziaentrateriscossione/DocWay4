package it.tredi.dw4.docway.model.webmail;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Mailbox extends XmlEntity {
	
	private String email = "";
	private String login = "";
	private String host = "";
	private int port = 0;
	private boolean current = false;

	@Override
	public XmlEntity init(Document dom) {
		this.email 		= XMLUtil.parseAttribute(dom, "mailbox/@email");
		this.login 		= XMLUtil.parseAttribute(dom, "mailbox/@login");
		this.host 		= XMLUtil.parseAttribute(dom, "mailbox/@host");
		
		String porta 	= XMLUtil.parseAttribute(dom, "mailbox/@port");
		if (StringUtil.isNumber(porta))
			this.port 	= new Integer(porta).intValue();
		
		this.current 	= StringUtil.booleanValue(XMLUtil.parseAttribute(dom, "mailbox/@current"));
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

}
