package it.tredi.dw4.docwayproc.model.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class WorkflowsTitolario extends XmlEntity {
	private List<WorkflowLink> bwfLinks = new ArrayList<WorkflowLink>();

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.bwfLinks = XMLUtil.parseSetOfElement(dom, "/response/indice_titolario/workflows/bwf_link", new WorkflowLink());
		
		if (this.bwfLinks.size() == 0)
			this.bwfLinks.add(new WorkflowLink());
			
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	for (int i = 0; i < this.bwfLinks.size(); i++) {
    		WorkflowLink workflowIdentifier = this.bwfLinks.get(i);
    		params.putAll(workflowIdentifier.asFormAdapterParams(prefix + ".workflows.bwf_link[" + String.valueOf(i) + "]"));
    	}
    	if (!params.containsKey(prefix+".workflows.bwf_link[0]"))
    		params.put(prefix+".workflows.bwf_link[0]", "");
    	
    	return params;
	}

	public List<WorkflowLink> getBwf_link() {
		return this.bwfLinks;
	}
	
	public void setBwf_link(List<WorkflowLink> bwfLinks) {
		this.bwfLinks = bwfLinks;
	}
	
	/**
	 * eliminazione di un workflow
	 */
	public String deleteWorkflow() {
		WorkflowLink workflowId = (WorkflowLink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflow");
		if (workflowId != null) {
			bwfLinks.remove(workflowId);
			if (bwfLinks.isEmpty()) 
				bwfLinks.add(new WorkflowLink());
		}
		return null;
	}
	
	/**
	 * aggiunta di un workflow
	 */
	public String addWorkflow() {
		WorkflowLink workflowId = (WorkflowLink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflow");
		int index = 0;
		if (workflowId != null)
			index = bwfLinks.indexOf(workflowId);
		
		if (bwfLinks != null) {
			if (bwfLinks.size() > index)
				bwfLinks.add(index+1,  new WorkflowLink());
			else
				bwfLinks.add(new WorkflowLink());
		}
		return null;
	}
	
	/**
	 * spostamento in alto di un workflow
	 */
	public String moveUpWorkflow() {
		WorkflowLink workflowId = (WorkflowLink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflow");
		if (workflowId != null && bwfLinks != null) {
			int index = bwfLinks.indexOf(workflowId);
			if (index > 0 ) {
				bwfLinks.remove(index);
				bwfLinks.add(index-1, workflowId);
			}
		}
		return null;
	}

	/**
	 * spostamento in basso di un workflow
	 */
	public String moveDownWorkflow() {
		WorkflowLink workflowId = (WorkflowLink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflow");
		if (workflowId != null && bwfLinks != null) {
			int index = bwfLinks.indexOf(workflowId);
			if (index < bwfLinks.size()-1 ) {
				bwfLinks.remove(index);
				bwfLinks.add(index+1, workflowId);
			}
		}
		return null;
	}
}
