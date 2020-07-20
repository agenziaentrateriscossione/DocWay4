package it.tredi.dw4.docway.model.workflow;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class WorkflowDefinition extends XmlEntity {
	
	/*
	 <Process name="MyProcess" label="MyProcess" version="1.0" uuid="MyProcess--1.0" state="ENABLED">
	 	<Description></Description>
	 	<Deployed by="admin" date="Wed Sep 18 16:13:16 CEST 2013"/>
	 </Process>
	 */

	private String name = "";
	private String label = "";
	private String version = "";
	private String id = "";
	private String state = "";
	private String description = "";
	private String deployedBy = "";
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public String getDeployedBy() {
		return deployedBy;
	}

	public void setDeployedBy(String deployedBy) {
		this.deployedBy = deployedBy;
	}
	
	@Override
	public XmlEntity init(Document dom) {
		this.name 			= XMLUtil.parseStrictAttribute(dom, "Process/@name");
		this.label			= XMLUtil.parseStrictAttribute(dom, "Process/@label");
		this.version 		= XMLUtil.parseStrictAttribute(dom, "Process/@version");
		this.id 			= XMLUtil.parseStrictAttribute(dom, "Process/@uuid");
		this.state 			= XMLUtil.parseStrictAttribute(dom, "Process/@state");
		this.description 	= XMLUtil.parseStrictElement(dom, "Process/Description");
		this.deployedBy 	= XMLUtil.parseStrictAttribute(dom, "Process/Deployed/@admin");

		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

}
