package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Indirizzo extends XmlEntity {
	public String getNazione() {
		return nazione;
	}

	public void setNazione(String nazione) {
		this.nazione = nazione;
	}

	public String getProv() {
		return prov;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public String getComune() {
		return comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	private String nazione;
	private String prov;
	private String comune;
	private String cap;
	private String indirizzo;
	
	public Indirizzo() {}
    
	public Indirizzo(String xmlIndirizzo) throws Exception {
        this.init(XMLUtil.getDOM(xmlIndirizzo));
    }
    
    public Indirizzo init(Document domIndirizzo) {
    	this.nazione = XMLUtil.parseAttribute(domIndirizzo, "indirizzo/@nazione");
    	this.prov = XMLUtil.parseAttribute(domIndirizzo, "indirizzo/@prov");
    	this.comune = XMLUtil.parseAttribute(domIndirizzo, "indirizzo/@comune");
    	this.cap = XMLUtil.parseAttribute(domIndirizzo, "indirizzo/@cap");
    	this.indirizzo = XMLUtil.parseElement(domIndirizzo, "indirizzo");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix, this.indirizzo);
    	params.put(prefix+".@nazione", this.nazione);
    	params.put(prefix+".@prov", this.prov);
    	params.put(prefix+".@comune", this.comune);
    	params.put(prefix+".@cap", this.cap);
    	return params;
    }

    public boolean isRenderIndirizzo(){
		if (null != this.indirizzo && !"".equals(this.indirizzo.trim()))
			return true;
		else if (null != this.nazione && !"".equals(this.nazione.trim()))
			return true;
		else if (null != this.comune && !"".equals(this.comune.trim()))
			return true;
		else if (null != this.prov && !"".equals(this.prov.trim()))
			return true;
		else if (null != this.cap && !"".equals(this.cap.trim()))
			return true;
		return false;
	}

}
