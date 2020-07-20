package it.tredi.dw4.docway.beans;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.adapters.DocWayBonitaWorkflowFormsAdapter;
import it.tredi.dw4.docway.model.workflow.Ex_Action;
import it.tredi.dw4.docway.model.workflow.TaskVariable;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

public class DocWayWfTaskVars extends Page {

	private DocWayBonitaWorkflowFormsAdapter formsAdapter;
	private String xml = "";
	
	private Ex_Action ex_action = null;
	private boolean visible = false;
	private List<TaskVariable> variables = new ArrayList<TaskVariable>();
	
	public DocWayWfTaskVars() throws Exception {
		this.formsAdapter = new DocWayBonitaWorkflowFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public List<TaskVariable> getVariables() {
		return variables;
	}

	public void setVariables(List<TaskVariable> variables) {
		this.variables = variables;
	}
	
	public Ex_Action getEx_action() {
		return ex_action;
	}

	public void setEx_action(Ex_Action ex_action) {
		this.ex_action = ex_action;
	}

	public DocWayBonitaWorkflowFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
    	xml 		= dom.asXML();
    	
    	// recupero delle sole variabili che non iniziano con 'dw__' (utilizzate per 
    	// elaborazioni particolari su bonita)
    	variables	= XMLUtil.parseSetOfElement(dom, "//Variables/Variable[not(starts-with(@name, 'dw__'))]", new TaskVariable());
    }
	
	/**
	 * chiusura del popup modale di visualizzazione delle variabili del
	 * task di workflow
	 * @return
	 * @throws Exception
	 */
	public String closeView() throws Exception{
		visible = false;
		setSessionAttribute("docwayWfTaskVars", null);
		
		return null;
	}
	
}
