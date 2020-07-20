package it.tredi.dw4.docway.model.condotte;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Dati_approvazione extends XmlEntity {

	private String note = "";
	private String timestamp = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.note = XMLUtil.parseStrictElement(dom, "dati_approvazione/note");
		this.timestamp = XMLUtil.parseStrictElement(dom, "dati_approvazione/timestamp");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix + ".note", this.note);
    	params.put(prefix + ".timestamp", this.timestamp);
    	
    	return params;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
