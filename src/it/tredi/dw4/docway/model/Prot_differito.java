package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Prot_differito extends XmlEntity {

	private String data_arrivo;
	private String text;
	
	@Override
	public XmlEntity init(Document dom) {
		this.data_arrivo = XMLUtil.parseStrictAttribute(dom, "prot_differito/@data_arrivo");
		this.text = XMLUtil.parseElement(dom, "prot_differito");
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.data_arrivo != null && this.data_arrivo.length() > 0)
    		params.put(prefix+".@data_arrivo", this.data_arrivo);
    	if (this.text != null && this.text.length() > 0)
    		params.put(prefix, this.text);
    	
    	return params;
	}

	public void setData_arrivo(String query) {
		this.data_arrivo = query;
	}

	public String getData_arrivo() {
		return data_arrivo;
	}

	public void setText(String num) {
		this.text = num;
	}

	public String getText() {
		return text;
	}

}
