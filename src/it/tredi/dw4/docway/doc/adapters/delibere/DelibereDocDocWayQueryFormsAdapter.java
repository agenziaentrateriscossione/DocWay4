package it.tredi.dw4.docway.doc.adapters.delibere;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;

public class DelibereDocDocWayQueryFormsAdapter extends
		DocDocWayQueryFormsAdapter {

	public DelibereDocDocWayQueryFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	public void reset() {
		defaultForm.addParam("query", "");
	    defaultForm.addParam("stpTitle", "");
	    defaultForm.addParam("qord", "");
	    defaultForm.removeParam("ifOneShowDoc");
	}
	
	public void seduteA(String cod_o, String organo, String tipo, String titolo) {
	    defaultForm.addParam("verbo", "queryplain");
	    defaultForm.addParam("xverb", "");
	    defaultForm.addParam("selid", "");
	    defaultForm.addParam("dbTable", "@seduta");
	    defaultForm.addParam("query", "([sed_organocod]=" + cod_o + ") AND ([sed_stato]=\"" + tipo + "\")");
	    String title = "_SET_|Sedute " + titolo + " del " + organo;
	    
	    defaultForm.addParam("stpTitle", title);
	    defaultForm.addParam("qord", "xml(xpart:/seduta/@data_convocazione:d)");
	    defaultForm.addParam("ifOneShowDoc","false");
	}
	
	public void proposteInCorsoDopera(String cod_o, String organo) {
		defaultForm.addParam("verbo", "queryplain");
    	defaultForm.addParam("xverb", "");
    	defaultForm.addParam("selid", "");
    	defaultForm.addParam("query", "[doc_propostacodorgano]=\"" + cod_o + "\" AND ([doc_personalviewcod]=\"PRP\" OR \"CMZ\") AND [xml,/doc/odg_seduta/@data_convocazione]=\"\"");

    	String title = "_SET_|Proposte sospese " + " del " + organo;
    	defaultForm.addParam("stpTitle", title); 
	}
	
	public void proposteSospese(String cod_o, String organo){
		defaultForm.addParam("verbo", "queryplain");
    	defaultForm.addParam("xverb", "");
    	defaultForm.addParam("selid", "");
    	defaultForm.addParam("query", "[doc_propostacodorgano]=\"" + cod_o + "\" AND ([doc_personalviewcod]=\"PRP\" OR \"CMZ\") AND [xml,/doc/odg_seduta/@data_convocazione]=\"99991212\"");

    	String title = "_SET_|Proposte sospese " + " del " + organo;
    	defaultForm.addParam("stpTitle", title); 
	}
	
	public void gotoTOMenu() {
		defaultForm.addParam("verbo", "query");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("personalView", "");
		defaultForm.addParam("dbTable", "@to");
		defaultForm.addParam("view", "");
	}
	
	public void inserisciSeduta(String cod_o){
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("physDoc", 0);
		defaultForm.addParam("selid", "");
		defaultForm.addParam("dbTable", "@seduta");
		defaultForm.addParam("personalView", "");
		defaultForm.addParam("view", "_INS_SEDUTA_|" + cod_o);
		defaultForm.addParam("pos", 0);
	}
	
	public void downloadModelloProposta(String id, String name){
		defaultForm.addParam("verbo", "attach");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("id", id);
		defaultForm.addParam("name", name);
	}
	
	public void inserisciProposta(String cod_o){
		defaultForm.addParam("view", "");
		insTableDoc("proposta@"+cod_o);
	}
}
