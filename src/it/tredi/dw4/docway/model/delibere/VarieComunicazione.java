package it.tredi.dw4.docway.model.delibere;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.docway.model.Varie;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class VarieComunicazione extends Varie {
	//campi specifici del doc varie di tipo Comunicazione
	private DatiProposta datiProposta = new DatiProposta();

	@Override
	public XmlEntity init(Document dom) {
		super.init(dom);
		
		// inizializzazione dei parametri specifici della proposta
		this.getDatiProposta().init(XMLUtil.createDocument(dom, "/response/doc"));
		return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return this.asFormAdapterParams(prefix, false);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify) {
		return this.asFormAdapterParams(prefix, false, false);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean isRepertorio) {
		
		if (null == prefix) prefix = "";
    	Map<String, String> params = super.asFormAdapterParams(prefix, modify, isRepertorio);
    	
    	// invio dei parametri comuni tra proposta e comunicazione della proposta
    	params.putAll(datiProposta.asFormAdapterParams(prefix, modify, isRepertorio));
    	
    	// invio dei parametri specifici della comunicazione
    	params.put(prefix+".@data_prot", getCurrDate());
    	
    	if(!modify) {
    		params.put(prefix + ".personalView.@cod", "CMZ");
    		params.put(prefix + ".autore", "_PROPONENTE_");
    		params.put(prefix + "wfActionX", "_WF_ACTIVATE_");
    		params.put(prefix + "bwf_Id", datiProposta.getProposta().getWorkflow().getName());
    		params.put(prefix + "bonitaVersion", datiProposta.getProposta().getWorkflow().getBonitaVersion());
    	}
    	
    	return params;
	}
	
	/*
	 * getter / setter
	 * */
	public DatiProposta getDatiProposta() {
		return datiProposta;
	}

	public void setDatiProposta(DatiProposta datiProposta) {
		this.datiProposta = datiProposta;
	}
}
