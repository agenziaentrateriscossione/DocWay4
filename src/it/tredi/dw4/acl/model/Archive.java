package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Archive extends XmlEntity {
	
	private String idEc = "";
	private String owner = "";
	private String host = "";
	private String type = "";
	private String alias = "";
	private String ico = "";
	private String pne = "";
	private String structure = "";
	private String webapp = "";
	private String text = "";
	private String port = "";
	private boolean canDelete = true;
	
	@Override
	public XmlEntity init(Document dom) {
		this.idEc = XMLUtil.parseAttribute(dom, "archive/@idEc");
		this.owner = XMLUtil.parseAttribute(dom, "archive/@owner");
		this.host = XMLUtil.parseAttribute(dom, "archive/@host");
		this.port = XMLUtil.parseAttribute(dom, "archive/@port");
		this.type = XMLUtil.parseAttribute(dom, "archive/@type");
		this.alias = XMLUtil.parseAttribute(dom, "archive/@alias");
		this.ico = XMLUtil.parseAttribute(dom, "archive/@ico");
		this.pne = XMLUtil.parseAttribute(dom, "archive/@pne");
		this.structure = XMLUtil.parseAttribute(dom, "archive/@structure");
		this.webapp = XMLUtil.parseAttribute(dom, "archive/@webapp");
		this.text = XMLUtil.parseElement(dom, "archive");
		
		evaluateCanDelete();
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return asFormAdapterParams(prefix, false);
	}
	
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify){
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@idEc", this.idEc);
    	params.put(prefix+".@owner", this.owner);
    	params.put(prefix+".@host", this.host);
    	params.put(prefix+".@port", this.port);
    	params.put(prefix+".@type", this.type);
    	params.put(prefix+".@alias", this.alias);
    	params.put(prefix+".@ico", this.ico);
    	params.put(prefix+".@pne", this.pne);
    	params.put(prefix+".@structure", this.structure);
    	params.put(prefix+".@webapp", this.webapp);
    	params.put(prefix, this.text);
    	
		return params;
	}
	
	private void evaluateCanDelete(){
		if(!idEc.isEmpty() && !owner.isEmpty())
			canDelete = false;
		else
			canDelete = true;
	}

	/*
	 * getter / setter
	 * */
	public String getIdEc() {
		return idEc;
	}

	public void setIdEc(String idEc) {
		this.idEc = idEc;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getIco() {
		return ico;
	}

	public void setIco(String ico) {
		this.ico = ico;
	}

	public String getPne() {
		return pne;
	}

	public void setPne(String pne) {
		this.pne = pne;
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}

	public String getWebapp() {
		return webapp;
	}

	public void setWebapp(String webapp) {
		this.webapp = webapp;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}


