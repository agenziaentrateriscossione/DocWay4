package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Disabled extends XmlEntity {
	private String ifVar;
    
	public Disabled() {}
    
	public Disabled(String xmlDisabled) throws Exception {
        this.init(XMLUtil.getDOM(xmlDisabled));
    }
    
    public Disabled init(Document domDisabled) {
    	this.ifVar = XMLUtil.parseAttribute(domDisabled, "disabled/@if");
    	this.ifVar = this.ifVar.replaceAll("%", "rights.get(\"").replaceAll("\\.checked", "\").selected");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.ifVar ) params.put(prefix+".@if", this.ifVar);
    	return params;
    }
    
    public String getIfVar() {
		return ifVar;
	}

	public void setIfVar(String spec) {
		this.ifVar = spec;
	}
}

