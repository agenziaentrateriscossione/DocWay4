package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;

import java.util.Map;

import org.dom4j.Document;

public class Varie extends Doc {
	
	@Override
	public XmlEntity init(Document dom) {
		super.init(dom, Const.DOCWAY_TIPOLOGIA_VARIE);
		
		return null;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return this.asFormAdapterParams(prefix, false);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify) {
		return this.asFormAdapterParams(prefix, modify, false);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean isRepertorio) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = super.asFormAdapterParams(prefix, modify, isRepertorio);
    	
    	// Invio dei parametri specifici del doc non protocollato
    	if (!modify) {
    		params.put(prefix+".@tipo", Const.DOCWAY_TIPOLOGIA_VARIE);
    	}
    	
    	params.putAll(autore.asFormAdapterParams(".autore"));
    	
    	return params;
	}

}
