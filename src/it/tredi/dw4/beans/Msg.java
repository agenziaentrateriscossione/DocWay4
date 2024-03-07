package it.tredi.dw4.beans;

import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;


public class Msg extends Page {

	private String title;
	private String type;
	private String message;
	protected boolean active = false;

	private String xml = "";
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}		
	
    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}	
	
	public Msg() { }
	
	public void init(Document dom) {		
    	xml = dom.asXML();
    	this.title = XMLUtil.parseElement(dom, "msg/title");
    	this.message = XMLUtil.parseElement(dom, "msg/message");
    	this.type = XMLUtil.parseElement(dom, "msg/type");
    }
	
	public String close() {
		this.active = false;
		return null;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public FormsAdapter getFormsAdapter() {
		return null;
	}

}
