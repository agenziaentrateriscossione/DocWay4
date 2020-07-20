package it.tredi.dw4.docway.model.condotte;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Fornitore extends XmlEntity {

	private String codice = "";
	private String rag_sociale = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.codice 	= XMLUtil.parseStrictAttribute(dom, "fornitore/@codice");
		this.rag_sociale 	= XMLUtil.parseStrictAttribute(dom, "fornitore/@rag_sociale");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@codice", this.codice);
    	params.put(prefix+".@rag_sociale", this.rag_sociale);
    	
    	return params;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getRag_sociale() {
		return rag_sociale;
	}

	public void setRag_sociale(String rag_sociale) {
		this.rag_sociale = rag_sociale;
	}
	
}
