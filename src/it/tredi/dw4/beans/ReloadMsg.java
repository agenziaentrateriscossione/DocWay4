package it.tredi.dw4.beans;

import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class ReloadMsg extends Page {
	private String title = "";
	private String message = "";
	private String level = "";
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
	
	public ReloadMsg() {
	}	
	
	public void init(Document dom) {		
    	xml = dom.asXML();
    	this.title 		= I18N.mrs("dw4.operazione_eseguita_con_successo");
    	this.message 	= XMLUtil.parseStrictAttribute(dom, "/response/@warnings");
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

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public FormsAdapter getFormsAdapter() {
		return null;
	}
	
}
