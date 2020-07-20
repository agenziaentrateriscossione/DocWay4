package it.tredi.dw4.docway.doc.adapters.delibere;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;

public class PropostaDocDocWayDocEditFormsAdapter extends
		DocDocWayDocEditFormsAdapter {

	public PropostaDocDocWayDocEditFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	public void inserisciProposta(String cod_p, String cod_o, String tipo_p, String workflow){
	    defaultForm.addParam("verbo", "docEdit");
	    defaultForm.addParam("xverb", "");
	    defaultForm.addParam("physDoc", 0);
	    defaultForm.addParam("selid", "");
	    defaultForm.addParam("dbTable", tipo_p.equals("delibera") ? "@interno" : "@varie");
	    defaultForm.addParam("personalView", tipo_p.equals("delibera") ? "@proposta" : "@comunicazione");
	    defaultForm.addParam("view", "_INS_PROPOSTA_|" + cod_o + "|" + cod_p);
	}

}
