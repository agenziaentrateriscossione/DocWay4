package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;

import java.util.Map;

import org.dom4j.Document;

public class Arrivo extends Doc {
	
	@Override
	public XmlEntity init(Document dom) {
		super.init(dom, Const.DOCWAY_TIPOLOGIA_ARRIVO);
		
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
		return this.asFormAdapterParams(prefix, modify, true, isRepertorio);
	}
	
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean modifyRifEsterni, boolean isRepertorio) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = super.asFormAdapterParams(prefix, modify, isRepertorio);
    	
    	// Invio dei parametri specifici del doc in arrivo
    	if (!modify) {
    		params.put(prefix+".@tipo", Const.DOCWAY_TIPOLOGIA_ARRIVO);
    	}
    	
    	if (modifyRifEsterni) {
    		// mbernardini 17/02/2014 : corretto bug in modifica dati mittente su documento ricevuto in 
    		// interoperabilita' (si perdevano i dati di interoperabilita')
    		if (rif_esterni.size() == 1) {
    			RifEsterno rif = (RifEsterno) rif_esterni.get(0);
	    		params.putAll(rif.asFormAdapterParams(".rif_esterni.rif"));
    		}
    		/*
	    	for (int i = 0; i < rif_esterni.size(); i++) {
	    		RifEsterno rif = (RifEsterno) rif_esterni.get(i);
	    		params.putAll(rif.asFormAdapterParams(".rif_esterni.rif["+String.valueOf(i)+"]"));
			}
			*/
    	}
    	
    	params.putAll(prot_differito.asFormAdapterParams(".prot_differito"));
    	
    	return params;
	}
	
}
