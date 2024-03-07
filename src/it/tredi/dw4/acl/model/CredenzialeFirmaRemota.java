package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class CredenzialeFirmaRemota extends XmlEntity {
	private String username;
	private String provider;
    
	public CredenzialeFirmaRemota() {}
    
	public CredenzialeFirmaRemota(String xmlFirmaRemota) throws Exception {
        this.init(XMLUtil.getDOM(xmlFirmaRemota));
    }
    
    public CredenzialeFirmaRemota init(Document domFirmaRemota) {
    	this.username = XMLUtil.parseAttribute(domFirmaRemota, "credenzialeFirmaRemota/@username");
    	this.provider = XMLUtil.parseAttribute(domFirmaRemota, "credenzialeFirmaRemota/@provider");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@username", this.username);
    	params.put(prefix+".@provider", this.provider);
    	return params;
    }

    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
