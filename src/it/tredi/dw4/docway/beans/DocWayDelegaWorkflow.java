package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocumentFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.PersonaInRuolo;
import it.tredi.dw4.docway.model.workflow.Task;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.context.FacesContext;
import org.dom4j.Document;

public class DocWayDelegaWorkflow extends Page {
	protected DocDocWayDocumentFormsAdapter formsAdapter;
	private boolean visible = false;
	private Showdoc showdoc;
	
	private String selectedTask;
	private ArrayList<Task> tasks = new ArrayList<Task>();
	private ArrayList<PersonaInRuolo> users = new ArrayList<PersonaInRuolo>();
	private String bonitaVersion = "";
	
	public DocWayDelegaWorkflow() throws Exception {
		formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
		visible = true;
	}
	
	public void init(Document dom) {
		// TODO Auto-generated method stub
	}
	
	public void init(Map<String, PersonaInRuolo> usersFrom, ArrayList<Task> tasksFrom, String bonitaVersion) {
		this.setUsers(new ArrayList<PersonaInRuolo>(usersFrom.values()));
		this.tasks			= tasksFrom;
		this.bonitaVersion = bonitaVersion;
	}

	/**
	 * Annullamento e ritorno sulla showdoc del documento
	 */
	public String clearDocument() throws Exception {
		visible = false;
		return null;
	}
	
	/**
	 * Conferma di delega del task
	 * @return
	 * @throws Exception
	 */
	public String delegaTaskWorkflow() throws Exception{
		PersonaInRuolo selectedUser = (PersonaInRuolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("persona");

		formsAdapter.delegaWorkflowTask(selectedTask, selectedUser.getMatricola(),bonitaVersion);
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		visible = false;
		
//		//output di un messaggio di conferma
//		ReloadMsg message = new ReloadMsg();
//		message.setActive(true);
//		message.setTitle(I18N.mrs("dw4.operazione_eseguita_con_successo"));
//		message.setMessage(I18N.mrs("dw4.delega_assegnata"));
//		message.setLevel(Const.MSG_LEVEL_SUCCESS);
		
		if (null != getShowdoc()) {
				getShowdoc().reload();
		}
		
//		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//		session.setAttribute("reloadmsg", message);
		return null;
	}
	
	/*
	 * getter/setter
	 * */

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public DocumentFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	public Showdoc getShowdoc() {
		return showdoc;
	}

	public void setShowdoc(Showdoc showdoc) {
		this.showdoc = showdoc;
	}

	public String getSelectedTask() {
		return selectedTask;
	}

	public void setSelectedTask(String selectedTask) {
		this.selectedTask = selectedTask;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	public void setTasks(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}

	public ArrayList<PersonaInRuolo> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<PersonaInRuolo> users) {
		this.users = users;
	}

	public String getBonitaVersion() {
		return bonitaVersion;
	}

	public void setBonitaVersion(String bonitaVersion) {
		this.bonitaVersion = bonitaVersion;
	}
}
