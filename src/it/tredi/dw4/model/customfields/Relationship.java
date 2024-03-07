package it.tredi.dw4.model.customfields;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Definizione della relazione fra campi custom (e non)
 */
public class Relationship extends XmlEntity {

	private String fieldFrom;
	private String valueFrom;
	private String fieldTo;
	private RelationshipAction action;
	
	@Override
	public XmlEntity init(Document dom) {
		this.fieldFrom = XMLUtil.parseStrictAttribute(dom, "relationship/@field_from");
		this.valueFrom = XMLUtil.parseStrictAttribute(dom, "relationship/@value_from");
		this.fieldTo = XMLUtil.parseStrictAttribute(dom, "relationship/@field_to");
		this.action = RelationshipAction.fromString(XMLUtil.parseStrictAttribute(dom, "relationship/@action"));
		
		return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getFieldFrom() {
		return fieldFrom;
	}

	public void setFieldFrom(String fieldFrom) {
		this.fieldFrom = fieldFrom;
	}

	public String getValueFrom() {
		return valueFrom;
	}

	public void setValueFrom(String valueFrom) {
		this.valueFrom = valueFrom;
	}

	public String getFieldTo() {
		return fieldTo;
	}

	public void setFieldTo(String fieldTo) {
		this.fieldTo = fieldTo;
	}

	public RelationshipAction getAction() {
		return action;
	}

	public void setAction(RelationshipAction action) {
		this.action = action;
	}
	
}
