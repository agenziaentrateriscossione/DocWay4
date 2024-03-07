package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Tag associato ad una risorsa
 */
public class Tag extends XmlEntity {

	/**
	 * Valore del tag
	 */
	private String value;
	
	/**
	 * Costruttore vuote
	 */
	public Tag() {
	}
	
	/**
	 * Costruttore
	 * @param value Valore da associare al tag
	 */
	public Tag(String value) {
		this.value = value;
	}

	@Override
	public XmlEntity init(Document dom) {
		this.value = XMLUtil.parseAttribute(dom, "tag/@value");
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@value", this.value);
    	return params;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
