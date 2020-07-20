package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Notify extends XmlEntity {
	private String httpHost;
	private String uri;
	private boolean rpa;
	
	public Notify() {}
    
	public Notify(String xmlNotify) throws Exception {
        this.init(XMLUtil.getDOM(xmlNotify));
    }
    
    public Notify init(Document domNotify) {
    	this.httpHost 	= XMLUtil.parseAttribute(domNotify, "notify/@httpHost");
    	this.uri 		= XMLUtil.parseAttribute(domNotify, "notify/@uri");
    	this.rpa 		= new Boolean(XMLUtil.parseAttribute(domNotify, "notify/@rpa")).booleanValue();
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.httpHost ) 	params.put(prefix+".@httpHost", this.httpHost);
    	if (null != this.uri ) 	params.put(prefix+".@uri", this.uri);
    	params.put(prefix+".@rpa", this.rpa+"");
    	return params;
    }
    
    public String getHttpHost() {
		return httpHost;
	}

	public void setHttpHost(String host) {
		this.httpHost = host;
	}

	public void setRpa(boolean rpa) {
		this.rpa = rpa;
	}

	public boolean getRpa() {
		return rpa;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
}

