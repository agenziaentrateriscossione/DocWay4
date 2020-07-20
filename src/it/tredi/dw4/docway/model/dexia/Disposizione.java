package it.tredi.dw4.docway.model.dexia;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.DateConverter;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Disposizione extends XmlEntity {

	private String text = "";
	private String dataEmanazione = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.text = XMLUtil.parseElement(dom, "NA_disposizione");
		this.dataEmanazione = XMLUtil.parseAttribute(dom, "NA_disposizione/@dataemanazione"); 
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix, this.text);
    	params.put(prefix+".@dataemanazione", this.dataEmanazione);
    	return params;
	}

	public void setText(String query) {
		this.text = query;
	}

	public String getText() {
		return text;
	}

	public String getDataEmanazione() {
		return new DateConverter().getAsString(null, null, this.dataEmanazione);
	}

	public void setDataEmanazione(String dataEmanazione) {
		this.dataEmanazione =  DateUtil.formatDate2XW(dataEmanazione, null);;
	}
}
