package it.tredi.dw4.docway.model;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;

public class Interno extends Doc {

	@Override
	public XmlEntity init(Document dom) {
		super.init(dom, Const.DOCWAY_TIPOLOGIA_INTERNO);
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
    	
    	// Invio dei parametri specifici del doc tra uffici
    	if (!modify) {
    		params.put(prefix+".@tipo", Const.DOCWAY_TIPOLOGIA_INTERNO);
    	}
    	
    	params.putAll(minuta.asFormAdapterParams(".minuta"));
    	
    	// mbernardini 10/04/2015 : gli assegnatari del documento devono essere spediti al service solo se non sono in modifica
    	// oppure se sono in modifica di un documento in bozza (o di tipo non protocollato) e non fascicolato
    	if (!modify || (bozza && (assegnazioneRPAM.getCod_fasc() == null || assegnazioneRPAM.getCod_fasc().length() == 0))) {
	    	params.putAll(getAssegnazioneRPAMParam());
	    	params.putAll(getAssegnazioneOPMParam());
    	}
    	
    	return params;
	}

}
