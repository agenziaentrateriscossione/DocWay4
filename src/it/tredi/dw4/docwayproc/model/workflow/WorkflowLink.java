package it.tredi.dw4.docwayproc.model.workflow;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class WorkflowLink extends XmlEntity {

	private String name = "";
	private String label = "";
	private String bonitaVersion = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.name = XMLUtil.parseStrictAttribute(dom, "/bwf_link/@name");
		this.label = XMLUtil.parseStrictAttribute(dom, "/bwf_link/label");
		this.bonitaVersion = XMLUtil.parseStrictAttribute(dom, "/bwf_link/@bonitaVersion");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.name != null && this.name.length() > 0) {
    		params.put(prefix + ".@name", this.name);
    		params.put(prefix + ".label", this.label);
    		params.put(prefix + ".@bonitaVersion", this.bonitaVersion);
    	}
    	
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

	public String getBonitaVersion() {
		return bonitaVersion;
	}

	public void setBonitaVersion(String bonitaVersion) {
		this.bonitaVersion = bonitaVersion;
	}
}
