package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

public class Errore {
	private String errtype;
	private String extra;
	private String emailDetail;
	private String level;
	private String httpStatusCode;
	private boolean unexpected;
    
	public Errore() {}
    
	public Errore(String xmlErrore) throws Exception {
        this.init(XMLUtil.getDOM(xmlErrore));
    }
    
    public Errore init(Document domErrore) {
    	this.errtype  = XMLUtil.parseElement(domErrore, "errore/errtype");
    	this.extra    = XMLUtil.parseElement(domErrore, "errore/extra");
    	this.emailDetail = XMLUtil.parseElement(domErrore, "errore/emailDetail");
    	this.level = XMLUtil.parseAttribute(domErrore, "errore/@level");
    	this.httpStatusCode = XMLUtil.parseAttribute(domErrore, "errore/@httpStatusCode");
    	this.unexpected = Boolean.parseBoolean(XMLUtil.parseAttribute(domErrore, "errore/@unexpected"));
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	return new HashMap<String, String>();
    }
    
	public void setErrtype(String message) {
		this.errtype = message;
	}

	public String getErrtype() {
		return errtype;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getExtra() {
		return extra;
	}

	public String getEmailDetail() {
		return emailDetail;
	}

	public void setEmailDetail(String emailDetail) {
		this.emailDetail = emailDetail;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public boolean isUnexpected() {
		return unexpected;
	}

	public void setUnexpected(boolean unexpected) {
		this.unexpected = unexpected;
	}

	public String getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(String httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}
	
}

