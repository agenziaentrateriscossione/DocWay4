package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.adapters.DocWayBonitaWorkflowFormsAdapter;
import it.tredi.dw4.docway.model.workflow.WorkflowDefinition;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

//public class QueryWorkflow extends DocWayQuery {
//Prova cambio eredita
public class QueryWorkflow extends Page {
	private DocWayBonitaWorkflowFormsAdapter formsAdapter;
	private String xml;
	
	private String nrecord = "";
	
	private String workName;
	private List<WorkflowDefinition> workflowDefinitions = new ArrayList<WorkflowDefinition>();
	private boolean close;
	
	public String getWorkName() {
		return workName;
	}
	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public List<WorkflowDefinition> getWorkflowDefinitions() {
		return workflowDefinitions;
	}

	public void setWorkflowDefinitions(List<WorkflowDefinition> workflowDefinitions) {
		this.workflowDefinitions = workflowDefinitions;
	}
	
	public void setClose(boolean close) {
		this.close = close;
	}

	public boolean isClose() {
		return close;
	}
		
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getXml() {
		return xml;
	}
	
	public String getNrecord() {
		return nrecord;
	}
	
	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}
	
	public QueryWorkflow() throws Exception {
		this.formsAdapter = new DocWayBonitaWorkflowFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.xml 			= dom.asXML();
		workflowDefinitions	= XMLUtil.parseSetOfElement(dom, "/response/Processes/Process", new WorkflowDefinition());
    }	
	
	public DocWayBonitaWorkflowFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	/**
	 * caricamento dei workflow disponibili
	 */
	public String listWorkflowDefinitions() throws Exception {
		try {
			formsAdapter.listWorkflowDefinitions(workName);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			this.init(response.getDocument());
			this.formsAdapter.fillFormsFromResponse(response);
			
			return "showtitles@workflow";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * clear del form di ricerca
	 */
	public String resetQuery(){
		workName="";
		return null;
	}
	
	/**
	 * assegnazione di un workflow ad un documento
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doAssignWorkflow() throws Exception {
		try {
			WorkflowDefinition wfDef = (WorkflowDefinition) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflowdefinition");
			
			formsAdapter.doAssignWorkflow(wfDef.getName(), wfDef.getVersion(), nrecord);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			if (response.getAttributeValue("/response/@xverb", "").equals("_CLOSE_"))
				close = true;
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
