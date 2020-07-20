package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Group extends XmlEntity {
	private String label;
	private String tipo;
	private String exclusive;
	private String explain;
	private List<Right> rights;
	private List<Group> groups;
	private boolean opened = false;
	
	public Group() {}
    
	public Group(String xmlRight) throws Exception {
        this.init(XMLUtil.getDOM(xmlRight));
    }
    
    @SuppressWarnings("unchecked")
	public Group init(Document domRight) {
    	this.label 		= XMLUtil.parseAttribute(domRight, "group/@label");
    	this.tipo 		= XMLUtil.parseAttribute(domRight, "group/@tipo");
    	this.exclusive	= XMLUtil.parseAttribute(domRight, "group/@exclusive");
    	this.explain	= XMLUtil.parseElementNode(domRight, "/group/explain");
    	groups			= XMLUtil.parseSetOfElement(domRight, "//group/group", new Group());
    	rights			= XMLUtil.parseSetOfElement(domRight, "/group/right", new Right());
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@label", this.label);
    	params.put(prefix + ".@tipo", this.tipo);
    	return params;
    }

    public void setLabel(String bban) {
		this.label = bban;
	}

	public String getLabel() {
		return label;
	}

	public void setTipo(String type) {
		this.tipo = type;
	}

	public String getTipo() {
		return tipo;
	}

	public void setRights(List<Right> rights) {
		this.rights = rights;
	}

	public List<Right> getRights() {
		return rights;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public String getExplain() {
		return explain;
	}

	public void setExclusive(String exclusive) {
		this.exclusive = exclusive;
	}

	public String getExclusive() {
		return exclusive;
	}
	
	public boolean isEnabled(){
		if (this.groups.size() > 0) return true;
		else {
			boolean enabled = false;
			for (Iterator<Right> iter = this.rights.iterator(); iter.hasNext();) {
				Right diritto = (Right) iter.next();
				if (Boolean.valueOf(diritto.getValue())) enabled = true;
			}
			return enabled;
		}
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public boolean isOpened() {
		return opened;
	}
	
	public String changeOpened(){
		opened = ! opened;
		return null;
	}
	
	/**
	 * restituisce la label del gruppo escapata per l'inserimento in javascript
	 * @return
	 */
	public String getEscapedLabel() {
		if (label == null || label.length() == 0)
			return label;
		else
			return label.replaceAll("'", "\\\\\\'");
	}
	
}

