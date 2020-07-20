package it.tredi.dw4.docway.model.custom;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Servizio extends XmlEntity {

	private String text = "";
	private String costo = "";

	@Override
	public XmlEntity init(Document dom) {
		this.text = XMLUtil.parseElement(dom, "servizio");
		this.costo = XMLUtil.parseAttribute(dom, "servizio/@costo");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@costo", this.costo);
    	params.put(prefix, this.text);
    	return params;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCosto() {
		if (costo != null && costo.length() > 0 && costo.indexOf(",") == -1)
			costo = costo + ",00"; // aggiunta dei decimali nel caso in cui non siano presenti
		return costo;
	}

	public void setCosto(String costo) {
		this.costo = costo;
	}
}
