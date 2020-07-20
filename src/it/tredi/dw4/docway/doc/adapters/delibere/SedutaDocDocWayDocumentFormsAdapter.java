package it.tredi.dw4.docway.doc.adapters.delibere;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;

public class SedutaDocDocWayDocumentFormsAdapter extends
		DocDocWayDocumentFormsAdapter {

	public SedutaDocDocWayDocumentFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	public void tornaDaRisultati(){
		defaultForm.addParam("verbo", "showdoc");
	    defaultForm.addParam("xverb", "");
	}
	
	public void risultatiSeduta(){
		defaultForm.addParam("verbo", "showdoc");
	    defaultForm.addParam("xverb", "@risultati");
	}
	
	public void presentiAssenti(){
		defaultForm.addParam("verbo", "showdoc");
	    defaultForm.addParam("xverb", "@componenti");
	}
	
	public void produciDelibere(String nrecord_prop){
		defaultForm.addParam("verbo", "showdoc");
	    defaultForm.addParam("xverb", "@delibere");
	   
	    if (nrecord_prop == null)
	        nrecord_prop = "";
	    
	    defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("deliberaDiretta", nrecord_prop, defaultForm.getParam("_cd")));
	}
	
	public void produciDeliberaSedutaStante(String nrecord_prop){
		defaultForm.addParam("verbo", "xdocwaydoc_organi");
	    defaultForm.addParam("xverb", "@produciDeliberaSedutaStante");
	    defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("nrecord_prop", nrecord_prop, defaultForm.getParam("_cd")));
	}
	
	public void invioModelloALista(String tipo, String nrecord_sed){
	    //setGlobalFormRestore('verbo','xverb','_cd');
		defaultForm.addParam("verbo", "xdocwaydoc_organi");
		defaultForm.addParam("xverb", "@" + tipo);
		
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("invioALista", "true", defaultForm.getParam("_cd")));
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("nrecord_sed", nrecord_sed, defaultForm.getParam("_cd")));
	}
	
	public void downloadModello(String xverb, String nrecord_sed, String title){
		defaultForm.addParam("verbo", "xdocwaydoc_organi");
		defaultForm.addParam("xverb", "@modello" + xverb);
		defaultForm.addParam("nrecord_sed",nrecord_sed);
		defaultForm.addParam("title",title);
	}
	public void spostaProposta(String nrecord_prop, String verso) {
		defaultForm.addParam("verbo", "showdoc");
	    defaultForm.addParam("xverb", "@spostaProposta");
	    defaultForm.addParam("toDo", nrecord_prop + "|" + verso);
	}
	
	public void rinviaProposta(String nrecord_prop, String cod_organo, String nrecord_cur_sed){
		//setGlobalFormRestore('verbo','xverb','_cd','toDo');
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@rinvia_proposta");
		defaultForm.addParam("toDo", nrecord_prop + "|" + cod_organo + "|" + nrecord_cur_sed);

		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("selSed", getSelid(), defaultForm.getParam("_cd")));
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("posSed", getPos(), defaultForm.getParam("_cd")));
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("countSed", String.valueOf(Integer.parseInt(defaultForm.getParam("count"))), defaultForm.getParam("_cd")));
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("revPosSed", String.valueOf(Integer.parseInt(defaultForm.getParam("count")) - Integer.parseInt(getPos()) - 1), defaultForm.getParam("_cd")));
	}
	
	public void generaPDF(String nrecord_prop){
		defaultForm.addParam("verbo", "xdocwaydoc_organi");
		defaultForm.addParam("xverb", "@generaPDF");
		defaultForm.addParam("nrecord_prop",nrecord_prop);
		defaultForm.addParam("title","proposta.pdf");
	}
	
	public void insTableDocRep(String cod_organo, String nrecord_sed, String data_sed){
		//setGlobalFormRestore('verbo','xverb','physDoc','pos','selid','dbTable','codice_rep','descrizione_rep','toDo');
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("physDoc", "0");
		defaultForm.addParam("pos", "0");
		defaultForm.addParam("selid","");
		defaultForm.addParam("dbTable", "@partenza");
		defaultForm.addParam("toDo", "produzioneVerbale");
		defaultForm.addParam("codice_rep", "VxDx" + cod_organo.substring(cod_organo.indexOf("-") + 1));
		defaultForm.addParam("descrizione_rep", "");
		
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("nrecord_sed", nrecord_sed, defaultForm.getParam("_cd")));
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("data_sed", data_sed, defaultForm.getParam("_cd")));
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("cod_organo", cod_organo, defaultForm.getParam("_cd")));
	}
	
	public void produciVerbale(String cod_organo, String nrecord_sed, String data_sed){
	    insTableDocRep(cod_organo, nrecord_sed, data_sed);
	}
	
	public void confermaRisultati(String result){
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@conferma_risultati");
		defaultForm.addParam("toDo", result);
	}
	
	public void confermaComponenti(String result){
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@conferma_componenti");
		defaultForm.addParam("toDo", result);
	}
	
	public void setAllegato(String nrecord_prop, String n_odg, String pareri){
		defaultForm.addParam("verbo", "xdocwaydoc_organi");
		defaultForm.addParam("xverb", "produciDelibera");
		defaultForm.addParam("nrecord_prop", nrecord_prop);
	    defaultForm.addParam("n_odg", n_odg);
	    defaultForm.addParam("pareri", pareri);
	}
	
	public void confermaDelibera(String nrecord_prop, String result, String delibId){
		defaultForm.addParam("verbo", "xdocwaydoc_organi");
		defaultForm.addParam("xverb", "@confermaDelibera");
		defaultForm.addParam("toDo", result);
		
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("nrecord_prop", nrecord_prop, defaultForm.getParam("_cd")));
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("delibId", delibId, defaultForm.getParam("_cd")));
	}
	
	public void modifyTableDoc(){
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("xverb", "@modify");
	}
}
