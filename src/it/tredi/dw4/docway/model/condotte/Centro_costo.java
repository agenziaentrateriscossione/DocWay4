package it.tredi.dw4.docway.model.condotte;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Centro_costo extends XmlEntity {

	private String name = "";
	private String cod = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.name 	= XMLUtil.parseStrictElement(dom, "centro_costo");
		this.cod 	= XMLUtil.parseStrictAttribute(dom, "centro_costo/@cod");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix, this.name);
    	params.put(prefix+".@cod", this.cod);
    	
    	return params;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

}
