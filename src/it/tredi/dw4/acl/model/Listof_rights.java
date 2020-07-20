package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Listof_rights extends XmlEntity {
	private String info;
	private Applicativo applicativo = new Applicativo();
	private List<Group> groups;
	private List<Group> groups_gr;
	private List<Db> dbs;
	
	public Listof_rights() {}
    
	public Listof_rights(String xmlListof_rights) throws Exception {
        this.init(XMLUtil.getDOM(xmlListof_rights));
    }
    
    @SuppressWarnings("unchecked")
	public Listof_rights init(Document domListof_rights) {
    	this.setInfo(XMLUtil.parseElement(domListof_rights, "listof_rights/info"));
    	this.applicativo.init(XMLUtil.createDocument(domListof_rights, "//listof_rights/applicativo[1]"));
    	this.dbs = XMLUtil.parseSetOfElement(domListof_rights, "//db", new Db());
    	this.groups = XMLUtil.parseSetOfElement(domListof_rights, "//listof_rights/common/group", new Group());
    	groups.addAll(XMLUtil.parseSetOfElement(domListof_rights, "//listof_rights/group", new Group()));
    	this.groups_gr = XMLUtil.parseSetOfElement(domListof_rights, "//listof_rights/common_gr/group", new Group());
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	return params;
    }

    public void setInfo(String info) {
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	public void setDbs(List<Db> dbs) {
		this.dbs = dbs;
	}

	public List<Db> getDbs() {
		return dbs;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setApplicativo(Applicativo applicativo) {
		this.applicativo = applicativo;
	}

	public Applicativo getApplicativo() {
		return applicativo;
	}

	public void setGroups_gr(List<Group> groups_gr) {
		this.groups_gr = groups_gr;
	}

	public List<Group> getGroups_gr() {
		return groups_gr;
	}
}

