package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Oncheck extends XmlEntity {
	private String action;
	private String what;
    
	public Oncheck() {}
    
	public Oncheck(String xmlOncheck) throws Exception {
        this.init(XMLUtil.getDOM(xmlOncheck));
    }
    
    public Oncheck init(Document domOncheck) {
    	this.action = XMLUtil.parseAttribute(domOncheck, "oncheck/@action");
    	this.what = XMLUtil.parseAttribute(domOncheck, "oncheck/@what");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.action ) params.put(prefix+".@action", this.action);
    	if (null != this.what ) params.put(prefix+".@what", this.what);
    	return params;
    }
    
    public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	public String getWhat() {
		return what;
	}
}

