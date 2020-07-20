package it.tredi.dw4.docway.model.webmail;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Map;

import org.dom4j.Document;

public class Folder extends XmlEntity {

	private String fullname = "";
	private String name = "";
	private boolean defaultfolder = false;
	private String type = "";
	private int messagecount = 0;
	
	@Override
	public XmlEntity init(Document dom) {
		this.fullname 			= XMLUtil.parseElement(dom, "folder");
		this.name 				= XMLUtil.parseAttribute(dom, "folder/@name");
		this.defaultfolder 		= StringUtil.booleanValue(XMLUtil.parseAttribute(dom, "folder/@default"));
		this.type 				= XMLUtil.parseAttribute(dom, "folder/@type");
		
		String count 			= XMLUtil.parseAttribute(dom, "folder/@messageCount");
		if (StringUtil.isNumber(count))
			this.messagecount 	= new Integer(count).intValue();
		
		return this;	
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefaultfolder() {
		return defaultfolder;
	}

	public void setDefaultfolder(boolean defaultfolder) {
		this.defaultfolder = defaultfolder;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMessagecount() {
		return messagecount;
	}

	public void setMessagecount(int messagecount) {
		this.messagecount = messagecount;
	}

}
