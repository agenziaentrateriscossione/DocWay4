package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class TrasformazioneRep extends XmlEntity {
	
	private boolean trasformato;
    private String previousCodRep;
    
	public TrasformazioneRep() {}
    
	public TrasformazioneRep(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public TrasformazioneRep init(Document dom) {
    	String strTrasformato = XMLUtil.parseAttribute(dom, "trasformazioneRep/@trasformato");
		if (strTrasformato != null && strTrasformato.equals("si"))
			this.trasformato = true;
		else
			this.trasformato = false;
    	this.previousCodRep = XMLUtil.parseAttribute(dom, "trasformazioneRep/@previousCodRep");
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (this.trasformato)
    		params.put(prefix+".@trasformato", "si");
    	else
    		params.put(prefix+".@trasformato", "no");
    	params.put(prefix+".@previousCodRep", this.previousCodRep);
    	return params;
    }

	public boolean isTrasformato() {
		return trasformato;
	}

	public void setTrasformato(boolean trasformato) {
		this.trasformato = trasformato;
	}

	public String getPreviousCodRep() {
		return previousCodRep;
	}

	public void setPreviousCodRep(String previousCodRep) {
		this.previousCodRep = previousCodRep;
	}
    
}

