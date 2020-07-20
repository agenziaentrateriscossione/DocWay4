package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.adapters.DocWayBonitaWorkflowFormsAdapter;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.docway.model.workflow.Task;
import it.tredi.dw4.docway.model.workflow.WorkflowInstance;

import org.dom4j.Document;

public class DocWayDocExecuteTask extends Page {
	private DocWayBonitaWorkflowFormsAdapter formsAdapter;
	private String physDoc_doc;
	private Doc doc;
	private Task task;
	private WorkflowInstance wfInstance;
	private boolean close;
	private Showdoc showdoc;
	
	private boolean viewExecuteTask;
	
	private String action;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public Doc getDoc() {
		return doc;
	}
	
	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public WorkflowInstance getWfInstance() {
		return wfInstance;
	}

	public void setWfInstance(WorkflowInstance wfInstance) {
		this.wfInstance = wfInstance;
	}

	public DocWayDocExecuteTask(Task task, WorkflowInstance wfInstance, String physDoc_doc) throws Exception {
		this.physDoc_doc = physDoc_doc;
		this.task = task;
		this.wfInstance = wfInstance;
		this.formsAdapter = new DocWayBonitaWorkflowFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
    	xml = dom.asXML();
		doc = new Doc();
		doc.init(dom);
    }	
	
	public DocWayBonitaWorkflowFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public void setShowdoc(Showdoc showdoc) {
		this.showdoc = showdoc;
	}

	public Showdoc getShowdoc() {
		return showdoc;
	}

	public void setViewExecuteTask(boolean viewExecuteTask) {
		this.viewExecuteTask = viewExecuteTask;
	}

	public boolean isViewExecuteTask() {
		return viewExecuteTask;
	}

	public void closeEditTask() throws Exception{
		viewExecuteTask = false;
		setSessionAttribute("execTask", null);
	}
	
	/**
	 * Registrazione dell'esecuzione di un task di workflow sul documento di DocWay
	 * 
	 * @return
	 * @throws Exception
	 */
	public String executeTask() throws Exception {
		try {
			String name = task.getName();
			if (task.getLabel() != null && !task.getLabel().equals(""))
				name = task.getLabel();
			
			formsAdapter.executeTask(physDoc_doc, wfInstance.getId(), task.getId(), name);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			closeEditTask();
			if (null != showdoc) {
				showdoc._reloadWithoutNavigationRule();
			}
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

}
