package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class ArchiveGroup extends XmlEntity {
	
	private String name = "";
	private List<Archive> listOfArchive = new ArrayList<Archive>();
	private boolean enabled = false;
	private boolean canDelete = true;
	
	public ArchiveGroup(boolean enabled) {
		this.enabled = enabled;
		addArchive();
	}
	
	public ArchiveGroup() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.setName(XMLUtil.parseAttribute(dom, "archiveGroup/@name"));
		this.setListOfArchive(XMLUtil.parseSetOfElement(dom, "//archiveGroup/archive", new Archive()));
		this.enabled = false;
		
		if(listOfArchive == null || listOfArchive.size()==0){
			listOfArchive = new ArrayList<Archive>();
			listOfArchive.add(new Archive());
		}
		
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
    	
    	params.put(prefix+".@name", this.name);
    	
    	for(int i=0;i<listOfArchive.size();i++){
    		Archive archive = listOfArchive.get(i);
    		params.putAll(archive.asFormAdapterParams(prefix+".archive[" + i + "]"));
    	}
    	
		return params;
	}
	
	public void evaluateCanDelete(){
		if(listOfArchive.size() == 1 && listOfArchive.get(0).getText().isEmpty())
			setCanDelete(true);
		else
			setCanDelete(false);
	}
	
	public String addArchive(){
		Archive archive = (Archive) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archive");
		int index = 0;
		if (archive != null)
			index = this.getListOfArchive().indexOf(archive);
		
		if (this.getListOfArchive() != null) {
			if (this.getListOfArchive().size() > index)
				this.getListOfArchive().add(index+1,  new Archive());
			else
				this.getListOfArchive().add(new Archive());
		}
		
		this.enabled = true;
		evaluateCanDelete();
		
		return null;
	}
	
	public String deleteArchive(){
		Archive archive = (Archive) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archive");
		if (archive != null) {
			this.getListOfArchive().remove(archive);
			if (this.getListOfArchive().isEmpty()) 
				this.getListOfArchive().add(new Archive());
		}
		
		this.enabled = true;
		evaluateCanDelete();
		
		return null;
	}

	/*
	 * getter / setter
	 * */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Archive> getListOfArchive() {
		return listOfArchive;
	}

	public void setListOfArchive(List<Archive> listOfArchive) {
		this.listOfArchive = listOfArchive;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
	
	/**
	 * restituisce il nome dell' archiveGroup escapato per l'inserimento in javascript
	 * @return
	 */
	public String getEscapedName() {
		if (name == null || name.length() == 0)
			return name;
		else
			return name.replaceAll("'", "\\\\\\'");
	}
}

