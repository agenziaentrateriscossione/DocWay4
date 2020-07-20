package it.tredi.dw4.docwayproc.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Scarto extends XmlEntity {

	private String val = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.val = XMLUtil.parseStrictAttribute(dom, "scarto/@val", "");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.val != null && this.val.length() > 0)
    		params.put(prefix+".@val", this.val);
    	
    	return params;
	}
	
	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

}
