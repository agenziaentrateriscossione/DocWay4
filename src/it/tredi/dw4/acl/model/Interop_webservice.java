package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Interop_webservice extends XmlEntity {

	private Remote_administration remote_administration = new Remote_administration();
	
	@Override
	public XmlEntity init(Document dom) {
		this.remote_administration.init(XMLUtil.createDocument(dom, "interop_webservice/remote_administration"));
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.putAll(remote_administration.asFormAdapterParams(prefix+".remote_administration"));
    	
    	return params;
	}
	
	public Remote_administration getRemote_administration() {
		return remote_administration;
	}

	public void setRemote_administration(Remote_administration remote_administration) {
		this.remote_administration = remote_administration;
	}

}
