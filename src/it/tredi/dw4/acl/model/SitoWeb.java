package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class SitoWeb extends XmlEntity {
	private String url;
    
	public SitoWeb() {}
    
	public SitoWeb(String xmlSitoWeb) throws Exception {
        this.init(XMLUtil.getDOM(xmlSitoWeb));
    }
    
    public SitoWeb init(Document domSitoWeb) {
    	this.url = XMLUtil.parseAttribute(domSitoWeb, "sito_web/@url");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@url", this.url);
    	return params;
    }
    
    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
