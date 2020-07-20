package it.tredi.dw4.docway.model.workflow;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class TaskVariable extends XmlEntity {

	private String name = "";
	private String type = "";
	private String label = ""; // es. java.lang.String
	private String scope = ""; // es. task, instance
	private String description = "";
	private String value = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.name 			= XMLUtil.parseStrictAttribute(dom, "Variable/@name");
		this.type			= XMLUtil.parseStrictAttribute(dom, "Variable/@type");
		this.label 			= XMLUtil.parseStrictAttribute(dom, "Variable/@label");
		this.scope 			= XMLUtil.parseStrictAttribute(dom, "Variable/@scope");
		this.description 	= XMLUtil.parseStrictElement(dom, "Variable/Description");
		this.value		 	= XMLUtil.parseStrictElement(dom, "Variable/Value");
	
		return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
