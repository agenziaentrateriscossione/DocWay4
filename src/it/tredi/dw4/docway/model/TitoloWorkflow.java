package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class TitoloWorkflow extends XmlEntity {
	private String name = "";
	private String label = "";
	private String version = "";
	private String nrecord = "";

	@Override
	public XmlEntity init(Document dom) {
		this.name = XMLUtil.parseAttribute(dom, "workflow/@name");
		this.nrecord = XMLUtil.parseAttribute(dom, "workflow/@nrecord");
		this.version = XMLUtil.parseAttribute(dom, "workflow/@version");
		
		this.label = XMLUtil.parseElement(dom, "workflow");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	// Probabilmente non necessario
    	
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

}
