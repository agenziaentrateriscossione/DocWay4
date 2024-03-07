package it.tredi.dw4.docway.doc.adapters;

import java.net.URLEncoder;
import java.util.Iterator;

import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.TitlesFormsAdapter;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class DocDocWayTitlesFormsAdapter extends TitlesFormsAdapter {

	public DocDocWayTitlesFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
		Element root = response.getRootElement();
		defaultForm.addParam("num_fasc", root.attributeValue("num_fasc", ""));
		defaultForm.addParam("classif_fasc", root.attributeValue("classif_fasc", ""));
		defaultForm.addParam("oggetto_fasc", root.attributeValue("oggetto_fasc", ""));
		defaultForm.addParam("nome_persona_fasc", root.attributeValue("nome_persona_fasc", ""));
		defaultForm.addParam("nome_uff_fasc", root.attributeValue("nome_uff_fasc", ""));
		
		//trasmissione dell'informazione sull'archiviazione del fascicolo
		defaultForm.addParam("archiviato_fasc", root.attributeValue("archiviato_fasc", ""));
		
		//inserimento in un fascicolo
		defaultForm.addParam("physDoc_infasc", root.attributeValue("physDoc_infasc", ""));
		defaultForm.addParam("diritto_infasc", root.attributeValue("diritto_infasc", ""));
		defaultForm.addParam("nome_persona_infasc", root.attributeValue("nome_persona_infasc", ""));
		defaultForm.addParam("nome_uff_infasc", root.attributeValue("nome_uff_infasc", ""));
		defaultForm.addParam("cod_persona_infasc", root.attributeValue("cod_persona_infasc", ""));
		defaultForm.addParam("cod_uff_infasc", root.attributeValue("cod_uff_infasc", ""));
		
		//introdotto tipo del valore contenuto nei campi ufficio
		defaultForm.addParam("tipo_uff_infasc", root.attributeValue("tipo_uff_infasc", ""));
		
		defaultForm.addParam("classif_infasc", root.attributeValue("classif_infasc", ""));
		defaultForm.addParam("classif_cod_infasc", root.attributeValue("classif_cod_infasc", ""));
		defaultForm.addParam("soggetto_infasc", root.attributeValue("soggetto_infasc", ""));
		
		//Permette di attivare il bottone di assegnazione link fascicolo-documento
		defaultForm.addParam("bAssegnaLinkFasc", root.attributeValue("bAssegnaLinkFasc", ""));
		
		//Attiva la gerarchia nei fascicoli
		defaultForm.addParam("fascicoli_MostraGerarchia", root.attributeValue("fascicoli_MostraGerarchia", ""));
		defaultForm.addParam("fascicoli_MostraGerarchia_selIdOriginale", root.attributeValue("fascicoli_MostraGerarchia_selIdOriginale", ""));
		
		//introdotto "Nuovo CC" su una selezione di documenti
		if (this.funzionalitaDisponibili.get("interventoConfigurabileSuDoc") != null)
			defaultForm.addParam("interventoConfigurabileSuDoc", this.funzionalitaDisponibili.get("interventoConfigurabileSuDoc").toString());
	}
	
	public void queryFasc(String num_fasc){
//	    setGlobalFormRestore('verbo','selid','query');

	    defaultForm.addParam("verbo", "queryplain");
	    defaultForm.addParam("selid", "");

	    if (num_fasc.startsWith("RAC_"))
	        defaultForm.addParam("query",  "[rac_nrecord]=" + num_fasc.substring(4));
	    else
	        defaultForm.addParam("query",  "[fasc_numero]=" + num_fasc);
	}
	
	public void gotoTableQ(String name, String keylist) {
		gotoTableQwithRefine(name, null);
		if (keylist != null && !keylist.equals(""))
			defaultForm.addParam("keylist", keylist);
	}
	
	public void gotoTableQ(String name) {
		gotoTableQwithRefine(name, null);
	}
	
	private void gotoTableQwithRefine(String name, String refine) {
//		setGlobalFormRestore('verbo', 'xverb', 'selid', 'dbTable');

		 defaultForm.addParam("query", "");

		if (name != "collect" && name != "insCollect" && refine!= null && refine == "R")
			defaultForm.addParam("xverb", "@refine");

		if (name.length() > 0) {
			int index;

			if ((index = name.indexOf("#personalView=")) != -1) {
				defaultForm.addParam("dbTable", "@" + name.substring(0, index));
				defaultForm.addParam("personalView", name.substring(index + 14));
			} else {
				if (name != "fascicolo")  
					defaultForm.addParam("dbTable", "@" + name);
			}
		} else {
			defaultForm.addParam("dbTable", "");
			defaultForm.addParam("selid", "");
			defaultForm.addParam("personalView", "");
		}

		if (name == "collect" || name == "insCollect") {
			// TODO: gestione delle raccolte
//			collection(name);
		} else {
			defaultForm.addParam("verbo", "query");
		}
	}
	
	public void esportaCSV(String selRac, String klRac) {
		defaultForm.addParam("selRac", selRac);
		defaultForm.addParam("klRac", klRac);
		esportaCSV();
	}
	
	/**
	 * cancellazione massiva di documenti da lista titoli
	 * 
	 * @param selRac selId relativa alla lista titoli
	 * @param klRac documenti selezionati (tramite check) per l'eliminazione
	 */
	public void removeDocs(String selRac, String klRac) {
		defaultForm.addParam("verbo", "docsselection_response");
		defaultForm.addParam("xverb", "@removeDocs");
		defaultForm.addParam("selRac", selRac);
		defaultForm.addParam("klRac", klRac);
	}
	
	/**
	 * marcatura massiva di documenti come letti da lista titoli
	 * 
	 * @param selRac selId relativa alla lista titoli
	 * @param klRac documenti selezionati (tramite check) per la contrassegnatura come letto
	 */
	public void markAsReadDocs(String selRac, String klRac) {
		defaultForm.addParam("verbo", "docsselection_response");
		defaultForm.addParam("xverb", "@markAsReadDocs");
		defaultForm.addParam("selRac", selRac);
		defaultForm.addParam("klRac", klRac);
	}
	
	/**
	 * protocollazione massiva di documenti da lista titoli
	 * 
	 * @param selRac selId relativa alla lista titoli
	 * @param klRac documenti selezionati (tramite check) per la protocollazione
	 */
	public void protocollaDocs(String selRac, String klRac) {
		defaultForm.addParam("verbo", "docsselection_response");
		defaultForm.addParam("xverb", "@protocollaDocs");
		defaultForm.addParam("selRac", selRac);
		defaultForm.addParam("klRac", klRac);
	}
	
	/**
	 * scarto massivo di documenti da lista titoli
	 * 
	 * @param selRac selId relativa alla lista titoli
	 * @param klRac documenti selezionati (tramite check) per lo scarto
	 * @param scartoRuoli true se occorre effettuare lo scarto per ruoli, false in caso di scarto di assegnazioni personali
	 */
	public void scartaDocs(String selRac, String klRac, boolean scartoRuoli) {
		defaultForm.addParam("verbo", "docsselection_response");
		defaultForm.addParam("xverb", "@scartaDocs");
		defaultForm.addParam("selRac", selRac);
		defaultForm.addParam("klRac", klRac);
		defaultForm.addParam("scartoMassivoRuoli", scartoRuoli);
	}
	
	public void stampaElenco(){
	   defaultForm.addParam("toDo", "__STAMPA_ELENCO__|__DISABLE_TITSHOWDOC__|__DISABLE_TITQUERYFASC__");
	}

	public void store() {
		defaultForm.addParam("verbo", "queryplain");
		defaultForm.addParam("xverb", "store");
	}
	
	
	public void raccogli(String selezione, String keylist, boolean svuota) {
		defaultForm.addParam("klRac", keylist);
		if (svuota) defaultForm.addParam("keylist", "");
		else		defaultForm.addParam("keylist", keylist);
		defaultForm.addParam("selRac", selezione);
	}
	
	public void removeSelFromFasc() {
		defaultForm.addParam("verbo", "fascicolo_response");
		defaultForm.addParam("xverb", "@removeSelFormFasc");
	}

	public void removeSelFromFascM() {
		defaultForm.addParam("verbo", "fascicolo_response");
		defaultForm.addParam("xverb", "@removeSelFormFascM");
	}
	
	public void insertInFasc(String diritto) {
	    if (diritto == null)
	        diritto = "RPA";
	    defaultForm.addParam("verbo", "query");
	    defaultForm.addParam("xverb", "");
	    defaultForm.addParam("dbTable", "@fascicolo");

	    if (diritto.equals("RPAM"))
	    	this.defaultForm.addParam(customTupleName, setParameterFromCustomTupleValue("insSelMInFasc", "true", defaultForm.getParam(customTupleName)));
	    else
	    	this.defaultForm.addParam(customTupleName, setParameterFromCustomTupleValue("insSelMInFasc",  null,  defaultForm.getParam(customTupleName)));
	}
	
	public void openRifInt(String addType){
		this.defaultForm.addParam("verbo", "rifint_response");
		this.defaultForm.addParam("xverb", "@openRif");
		this.defaultForm.addParam("rifInt", addType);
	    if (this.defaultForm.getParam("dbTable").equals("@fascicolo"))
	    	this.defaultForm.addParam("toDo", "from_fasc_titles");
    	else
    		this.defaultForm.addParam("toDo", "from_titles");
	    if (!this.defaultForm.getParam("dbTable").equals("@raccoglitore"))	
	    	this.defaultForm.addParam("dbTable", "");
	}
	public void restoreOpernRifInt(){
    	if (this.defaultForm.getParam("selid").length() == 0) //occorre aprire in base a # fisico
    		this.defaultForm.addParam("pos",  this.defaultForm.getParamAsInt("physDoc")- 1);
	}
	
	public void openClassifSpec(String operation) {
		this.defaultForm.addParam(customTupleName, setParameterFromCustomTupleValue("operationToDo", operation, defaultForm.getParam(customTupleName)));
		this.defaultForm.addParam("selid", "");

		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("dbTable", "@classifspec");

	}
	
	public void ritiraBandoSelezione(){
		this.defaultForm.addParam("verbo", "fascicolo_response");
		this.defaultForm.addParam("xverb", "@ritiraBandoSelezione");
	}

	public void fascContent(String numfasc, String nome_persona, String nome_uff, String classif, String oggetto){

		defaultForm.addParam("verbo", "queryplain");

		defaultForm.addParam("xverb", "@fasccontent");

		defaultForm.addParam("num_fasc", numfasc);
		defaultForm.addParam("oggetto_fasc", oggetto);
		defaultForm.addParam("nome_persona_fasc", nome_persona);
		defaultForm.addParam("nome_uff_fasc", nome_uff);
		defaultForm.addParam("classif_fasc", classif);

	    defaultForm.addParam("selid", "");
		String query = "[doc_rifinternirifcodfasc]=" + numfasc + " OR [doc_fascicolocollegato]=" + numfasc;
		defaultForm.addParam("query", query);
	}
	
	/**
	 * Stampa profili della selezione corrente
	 */
	public void printProfiles(String keylist) {
		defaultForm.addParam("verbo", "print_profiles");
		defaultForm.addParam("dbTable", "");
		if (keylist != null && !keylist.equals(""))
			defaultForm.addParam("keylist", keylist);
	}
	
	/**
	 * creazione di una vaschetta personalizzata
	 * 
	 * @param nomeVaschetta nome da assegnare alla vaschetta custom
	 */
	public void addVaschetta(String nomeVaschetta, String filtroVaschetta) {
		defaultForm.addParam("verbo", "queryplain");
		defaultForm.addParam("xverb", "addvaschetta");
		defaultForm.addParam("nomeVaschetta", nomeVaschetta);
		defaultForm.addParam("filtroVaschetta", filtroVaschetta);
	}
	
	/**
	 * raffinamento della ricerca
	 * @param query
	 */
	public void raffina(String query) {
		String repVisioneCompleta = "";
		if( null != lastResponse ) {
			Iterator<?> repertorioIt = this.lastResponse.selectNodes("//repertorio/tabella[@vis='completa']").iterator();
			while( repertorioIt.hasNext()) {
				Element repertorio = (Element) repertorioIt.next();
				repVisioneCompleta += repertorio.getParent().attributeValue("codice", "");
				repVisioneCompleta += "-";
				repVisioneCompleta += repertorio.getParent().attributeValue("tipo", "");
				repVisioneCompleta += "|";
			}
		}
		if (!repVisioneCompleta.equals(""))
			this.defaultForm.addParam("repVisComp", repVisioneCompleta);
		
		String tmpRefineQuery = "([?SEL]=\"" + defaultForm.getParam("selid") + "\")";
		if (!query.equals("")) {
			tmpRefineQuery = "(" + query + ") AND " + tmpRefineQuery;
		}
			
		defaultForm.addParam("query", tmpRefineQuery);
		defaultForm.addParam("verbo", "queryplain");
		defaultForm.addParam("xverb", "");
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'db'.
	 * @return Valore impostato sull'attributo db
	 */
	public String getDb() {
		return defaultForm.getParam("db");
	}
	
	/**
	 * Ritorna il parametro _cd del formsAdapter (necessario all'upload di files in presenza
	 * di multisocieta')
	 * @return
	 */
	public String getCustomTupleName() {
		String _cd = defaultForm.getParam("_cd");
		if (_cd == null)
			_cd = "";

		return _cd;
	}
	
	/**
	 * Ritorna il parametro _cd del formsAdapter (necessario all'upload di files in presenza
	 * di multisocieta')
	 * @return
	 */
	public String getUrlEncodedCustomTupleName() {
		String physDoc = getCustomTupleName();
		try {
			physDoc = URLEncoder.encode(physDoc, "UTF-8");
		}
		catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}

		return physDoc;
	}
	
	/**
	 * Stampa tutti gli allegati dei documenti della selezione
	 * @param keylist Selezione di documenti da stampare
	 */
	public void printDocsAttachments(String keylist) {
		printDocsAttachments(keylist, null, null);
	}
	
	/**
	 * Stampa tutti gli allegati dei documenti della selezione
	 * @param keylist Selezione di documenti da stampare
	 * @param verbo
	 * @param query
	 */
	public void printDocsAttachments(String keylist, String verbo, String query) {
		if (verbo == null || verbo.isEmpty())
			verbo = "get_docs_attach";
		defaultForm.addParam("verbo", verbo);
		
		if (query != null && !query.isEmpty()) {
			defaultForm.addParam("query", query);
			defaultForm.addParam("selid", "");
		}
		
		if (keylist != null && !keylist.equals(""))
			defaultForm.addParam("keylist", keylist);
		
		// TODO parametro 'customClassForAdditionalInfo'
		
		defaultForm.addParam("transformJava", "ajax");
		defaultForm.addParam("enableIW", "true");
	}
}
