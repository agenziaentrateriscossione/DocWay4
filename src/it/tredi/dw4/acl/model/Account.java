package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Account extends XmlEntity{
	private String id = "";
	private String descrAccount = "";
	private List<ArchiveGroup> listOfArchiveGroup = new ArrayList<ArchiveGroup>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.id = XMLUtil.parseAttribute(dom, "account/@id");
		this.descrAccount = XMLUtil.parseAttribute(dom, "account/@descrAccount");
		this.listOfArchiveGroup = XMLUtil.parseSetOfElement(dom, "//account/archiveGroup", new ArchiveGroup());
		
		if(listOfArchiveGroup == null || listOfArchiveGroup.size()==0){
			ArchiveGroup ag = new ArchiveGroup();
			Archive a = new Archive();
			
			ag.setListOfArchive(new ArrayList<Archive>());
			ag.getListOfArchive().add(a);
			
			listOfArchiveGroup = new ArrayList<ArchiveGroup>();
			listOfArchiveGroup.add(ag);
		}
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return asFormAdapterParams(prefix, false);
	}
	
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify){
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@id", this.id);
    	params.put(prefix+".@descrAccount", this.descrAccount);
    	
    	for(int i=0;i<listOfArchiveGroup.size();i++){
    		ArchiveGroup archiveGroup = listOfArchiveGroup.get(i);
    		params.putAll(archiveGroup.asFormAdapterParams(prefix+".archiveGroup[" + i + "]"));
    	}
    	
		return params;
	}
	
	public String addArchiveGroup(){
		ArchiveGroup archiveGroup = (ArchiveGroup) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archiveGroup");
		int index = 0;
		if (archiveGroup != null)
			index = this.getListOfArchiveGroup().indexOf(archiveGroup);
		
		if (this.getListOfArchiveGroup() != null) {
			if (this.getListOfArchiveGroup().size() > index)
				this.getListOfArchiveGroup().add(index+1,  new ArchiveGroup(true));
			else
				this.getListOfArchiveGroup().add(new ArchiveGroup(true));
		}
		
		return null;
	}
	
	public String deleteArchiveGroup(){
		ArchiveGroup archiveGroup = (ArchiveGroup) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archiveGroup");
		if (archiveGroup != null) {
			this.getListOfArchiveGroup().remove(archiveGroup);
			if (this.getListOfArchiveGroup().isEmpty()) 
				this.getListOfArchiveGroup().add(new ArchiveGroup(true));
		}
		
		return null;
	}	

	// getter / setter
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescrAccount() {
		return descrAccount;
	}

	public void setDescrAccount(String descrAccount) {
		this.descrAccount = descrAccount;
	}

	public List<ArchiveGroup> getListOfArchiveGroup() {
		return listOfArchiveGroup;
	}

	public void setListOfArchiveGroup(List<ArchiveGroup> listOfArchiveGroup) {
		this.listOfArchiveGroup = listOfArchiveGroup;
	}
}
