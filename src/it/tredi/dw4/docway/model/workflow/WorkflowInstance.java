package it.tredi.dw4.docway.model.workflow;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DateConverter;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class WorkflowInstance extends XmlEntity {
	
	private String process = "";
	private String id = "";
	private String startedBy = "";
	private String endedBy = "";
	private String label = "";
	private String description = "";
	private String status = "";
	private String startedDate = "";
	private String endedDate = "";
	
	private String bonitaVersion = "";
	
	private List<Task> tasks;
	private List<Ex_Action> ex_actions = new ArrayList<Ex_Action>();
	
	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}
	
	public String getEndedBy() {
		return endedBy;
	}

	public void setEndedBy(String endedBy) {
		this.endedBy = endedBy;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public List<Ex_Action> getEx_actions() {
		return ex_actions;
	}

	public void setEx_actions(List<Ex_Action> ex_actions) {
		this.ex_actions = ex_actions;
	}

	@Override
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom) {
		this.id 			= XMLUtil.parseStrictAttribute(dom, "Instance/@instanceId");
		this.startedBy 		= XMLUtil.parseStrictAttribute(dom, "Instance/@startedBy");
		this.endedBy 		= XMLUtil.parseStrictAttribute(dom, "Instance/@endedBy");
		this.process		= XMLUtil.parseStrictAttribute(dom, "Instance/@process");
		this.label 			= XMLUtil.parseStrictAttribute(dom, "Instance/@label");
		this.description 	= XMLUtil.parseStrictElement(dom, "Instance/Description");
		this.status 		= XMLUtil.parseStrictAttribute(dom, "Instance/@status");
		
		this.bonitaVersion  = XMLUtil.parseStrictAttribute(dom, "Instance/@bonitaVersion");
		this.startedDate 		= XMLUtil.parseStrictAttribute(dom, "Instance/@startedDate");
		this.endedDate 		= XMLUtil.parseStrictAttribute(dom, "Instance/@endedDate");
		
		this.tasks 			= XMLUtil.parseSetOfElement(dom, "Instance/Tasks/Task", new Task());
		this.ex_actions 	= XMLUtil.parseSetOfElement(dom, "Instance/ex_action", new Ex_Action());

		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getBonitaVersion() {
		return bonitaVersion;
	}

	public void setBonitaVersion(String bonitaVersion) {
		this.bonitaVersion = bonitaVersion;
	}

	public String getStartedDate() {
		return startedDate;
	}

	public void setStartedDate(String startedDate) {
		this.startedDate = startedDate;
	}

	public String getEndedDate() {
		return endedDate;
	}

	public void setEndedDate(String endedDate) {
		this.endedDate = endedDate;
	}
}
