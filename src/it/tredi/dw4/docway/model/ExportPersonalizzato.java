package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class ExportPersonalizzato extends XmlEntity {

	private String title;
	private String query;
	private String type;
	private String num;
	private String selid;
	private String label;
	private String key;
	
	private List<ExportGroup> groups;
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.setTitle(XMLUtil.parseAttribute(dom, "export/@title"));
		this.setQuery(XMLUtil.parseAttribute(dom, "export/@query"));
		this.setType(XMLUtil.parseAttribute(dom, "export/@type"));
		this.setNum(XMLUtil.parseAttribute(dom, "export/@num"));
		this.setSelid(XMLUtil.parseAttribute(dom, "export/@selid"));
		
		setKey(XMLUtil.parseAttribute(dom, "export/@key"));
		
		this.label = getKey().substring(getKey().lastIndexOf("_") + 1);
		if (getKey().startsWith("fasc"))
			this.label = "fasc_" + this.label;
		else if (getKey().startsWith("rac_"))
			this.label = "rac_" + this.label;
		
		if (getKey().lastIndexOf("_") != -1)
			setKey(getKey().substring(0, getKey().lastIndexOf("_")));
		
		this.setGroups(XMLUtil.parseSetOfElement(dom, "export/group", new ExportGroup()));
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.title ) 	params.put(prefix + ".@title", this.title);
    	if (null != this.query ) 	params.put(prefix + ".@query", this.query);
    	if (null != this.type ) 	params.put(prefix + ".@type", this.type);
    	
    	
    	for (int i = 0; i < this.groups.size(); i++) {
    		ExportGroup group = this.groups.get(i);
    		params.putAll(group.asFormAdapterParams(prefix + ".group[" + i +"]"));
    	}
    	
    	return params;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getSelid() {
		return selid;
	}

	public void setSelid(String selid) {
		this.selid = selid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<ExportGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ExportGroup> groups) {
		this.groups = groups;
	}

	public boolean isGroupSaved(ExportGroup group) {
		for (ExportGroup saved : this.getGroups()) {
			if (saved.getName().equals(group.getName()) && saved.getType().equals(group.getType()))
				return true;
		}
		
		return false;
	}

}
