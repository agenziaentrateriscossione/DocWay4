package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Remote_administration extends XmlEntity {

	private String cod_amm_aoo = "";
	
	@Override
	public XmlEntity init(Document dom) {
		cod_amm_aoo = XMLUtil.parseAttribute(dom, "remote_administration/@cod_amm_aoo", "");
        
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (cod_amm_aoo != null && cod_amm_aoo.length() > 0)
    		params.put(prefix + ".@cod_amm_aoo", cod_amm_aoo);
    	
    	return params;
	}
	
	public String getCod_amm_aoo() {
		return cod_amm_aoo;
	}

	public void setCod_amm_aoo(String cod_amm_aoo) {
		this.cod_amm_aoo = cod_amm_aoo;
	}

}
