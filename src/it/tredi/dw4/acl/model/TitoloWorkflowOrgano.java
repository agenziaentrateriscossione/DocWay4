package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class TitoloWorkflowOrgano extends XmlEntity {
	private String name = "";
	private String label = "";

	@Override
	public XmlEntity init(Document dom) {
		this.name = XMLUtil.parseAttribute(dom, "workflow/@id");
		this.label = XMLUtil.parseElement(dom, "workflow");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
     	params.put(prefix + ".@id", this.name);
    	params.put(prefix, this.label);
    	
    	return params;
	}

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
}
