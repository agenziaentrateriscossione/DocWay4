package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Oggetto Dest utilizzato nella gestione delle notifiche differite
 * 
 * @author mbernardini
 */
public class Dest extends XmlEntity {

	private String email = "";
	private String status = "";
	private String user = "";
	private String url = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.email 	= XMLUtil.parseAttribute(dom, "dest/@email");
    	this.status = XMLUtil.parseAttribute(dom, "dest/@status");
    	this.user 	= XMLUtil.parseAttribute(dom, "dest/@user");
    	this.url 	= XMLUtil.parseAttribute(dom, "dest/@url");
    	
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
