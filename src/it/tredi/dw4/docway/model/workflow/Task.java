package it.tredi.dw4.docway.model.workflow;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Task extends XmlEntity {
	
	/*
	 <Task name="Step1" label="Step1" id="MyProcess--1.0--1--Step1--it1--mainActivityInstance--noLoop" state="READY"><Description></Description></Task>
	 */

	private String name = "";
	private String label = "";
	private String id = "";
	private String state = "";
	private String description = "";
	//private String deployedDate = "";
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	
	@Override
	public XmlEntity init(Document dom) {
		this.name 			= XMLUtil.parseStrictAttribute(dom, "Task/@name");
		this.label			= XMLUtil.parseStrictAttribute(dom, "Task/@label");
		this.id 			= XMLUtil.parseStrictAttribute(dom, "Task/@id");
		this.state 			= XMLUtil.parseStrictAttribute(dom, "Task/@state");
		this.description 	= XMLUtil.parseStrictElement(dom, "Task/Description");

		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

}
