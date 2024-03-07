package it.tredi.dw4.docway.doc.adapters;

import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.acl.model.Mailbox;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.docway.model.ExportPersonalizzato;
import it.tredi.dw4.docway.model.HomeContentDefinition;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.VaschettaCustom;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLDocumento;

public class DocDocWayQueryFormsAdapter extends QueryFormsAdapter {

	public DocDocWayQueryFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
		Element root = response.getRootElement();
		
		//inserimento in un fascicolo
		defaultForm.addParam("physDoc_infasc", root.attributeValue("physDoc_infasc", ""));
		defaultForm.addParam("diritto_infasc", root.attributeValue("diritto_infasc", ""));
		defaultForm.addParam("nome_persona_infasc", root.attributeValue("nome_persona_infasc", ""));
		defaultForm.addParam("nome_uff_infasc", root.attributeValue("nome_uff_infasc", ""));
		defaultForm.addParam("cod_persona_infasc", root.attributeValue("cod_persona_infasc", ""));
		defaultForm.addParam("cod_uff_infasc", root.attributeValue("cod_uff_infasc", ""));
		defaultForm.addParam("tipo_uff_infasc", root.attributeValue("tipo_uff_infasc", ""));
		
		defaultForm.addParam("classif_infasc", root.attributeValue("classif_infasc", ""));
		defaultForm.addParam("classif_cod_infasc", root.attributeValue("classif_cod_infasc", ""));
		defaultForm.addParam("soggetto_infasc", root.attributeValue("soggetto_infasc", ""));
		
		//Bottone assegnazione link fascicolo-documento
		defaultForm.addParam("bAssegnaLinkFasc", root.attributeValue("bAssegnaLinkFasc", ""));
		
		//pagina intermedia per testare se IW presente (entrata da email)
		defaultForm.addParam("urlToLaunch", root.attributeValue("urlToLaunch", ""));
		
		String enableIW = root.attributeValue("enableIW", "");
		if (enableIW.equals(""))
			enableIW = isEnabledIWX(); // occorre forzare un valore per iw altrimenti non funzionerebbero le ricerche
		defaultForm.addParam("enableIW", enableIW);
		
		//per le stampe
		defaultForm.addParam("currDate", root.attributeValue("currDate", ""));
		defaultForm.addParam("currYear", root.attributeValue("currYear", ""));
		defaultForm.addParam("lookup_titolo", "");
		defaultForm.addParam("lookup_xq", "");
		defaultForm.addParam("lookup_new", root.attributeValue("lookup_new", ""));
		defaultForm.addParam("lookup_db", "");
		defaultForm.addParam("fromLookup", root.attributeValue("fromLookup", ""));
		defaultForm.addParam("lookup_alias", "");
		
		defaultForm.addParam("fascicoli_MostraGerarchia", "");
		
		defaultForm.addParam("rolesManagement", this.checkBooleanFunzionalitaDisponibile("rolesManagement", false)+"");
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'toDo'.
	 * @return Valore impostato sull'attributo toDo
	 */
	public String getToDo() {
		return defaultForm.getParam("toDo");
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'db'.
	 * @return Valore impostato sull'attributo db
	 */
	public String getDb() {
		return defaultForm.getParam("db");
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'aclDb'.
	 * @return Valore impostato sull'attributo aclDb
	 */
	public String getAclDb() {
		return defaultForm.getParam("aclDb");
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
	 * Ritorna il valore impostato come sizeMaxFile (dimensione massima dei file da uploadare)
	 * @return
	 */
	public int getSizeMaxFile() {
		int size = 0;
		String sizeMaxFile = defaultForm.getParam("sizeMaxFile");
		try {
			if (sizeMaxFile == null || sizeMaxFile.isEmpty())
				sizeMaxFile = "0";
			size = Integer.parseInt(sizeMaxFile);
		}
		catch (Exception e) {
			Logger.warn("DocDocWayQueryFormsAdapter.getSizeMaxFile(): unable to parse sizeMaxFile -> " + sizeMaxFile);
			size = 0;
		}
		return size;
	}
	
	/**
	 * Ritorna il valore impostato come sizeMaxImg (dimensione massima delle immagini da uploadare)
	 * @return
	 */
	public int getSizeMaxImg() {
		int size = 0;
		String sizeMaxImg = defaultForm.getParam("sizeMaxImg");
		try {
			if (sizeMaxImg == null || sizeMaxImg.isEmpty())
				sizeMaxImg = "0";
			size = Integer.parseInt(sizeMaxImg);
		}
		catch (Exception e) { 
			Logger.warn("DocDocWayQueryFormsAdapter.getSizeMaxImg(): unable to parse sizeMaxImg -> " + sizeMaxImg);
			size = 0;
		}
		return size;
	}
	
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'attach'.
	 * @return Valore impostato sull'attributo attach
	 */
	public String getAttach() {
		return defaultForm.getParam("attach");
	}
	
	/**
	 * caricamento di una lista titoli da vaschette dell'homepage di docway
	 * @param selid selid della selezione da caricare
	 */
    public void gotoDocsInGestione(String selid) {
        //setGlobalFormRestore('verbo', 'selid', 'pos', 'toDo');
    	gotoTitles(selid);

        // Federico 15/05/08: se la property 'mostraArticolazioneSottofascicoli' e' attiva, la gerarchia dei fasc. deve
        // essere mostrata anche quando si arriva dalle vaschette dei fasc. [RW 0050851]
    	defaultForm.addParam("_cd", setParameterFromCustomTupleValue("coming_from_main_page", "true", defaultForm.getParam("_cd")));
    }
    
    /**
	 * caricamento di una lista titoli da vaschette relative a fascicoli dell'homepage di docway
	 * @param selid selid della selezione da caricare
	 */
    public void gotoFascInGestione(String selid) {
    	defaultForm.addParam("xverb", "@fascicolo");
    	defaultForm.addParam("dbTable", "@fascicolo");
        gotoDocsInGestione(selid);
    }

    /**
	 * caricamento di una lista titoli da vaschette relative a raccoglitori dell'homepage di docway
	 * @param selid selid della selezione da caricare
	 */
    public void gotoRacInGestione(String selid) {
    	defaultForm.addParam("xverb", "@raccoglitore");
		defaultForm.addParam("dbTable", "@raccoglitore");
        gotoDocsInGestione(selid);
    }
    
    public void refineQuery(String id) {
    	if (null != id)
    		defaultForm.addParam("selid", id);
//    	else if (("" == defaultForm.getParam("selid")) && (null != currentSet))
//    		defaultForm.addParam("selid", currentSet);
    	if (defaultForm.getParam("db").equals(defaultForm.getParam("selid")))
    		defaultForm.addParam("selid", "");
    	defaultForm.addParam("xverb", "@refine");
    }

    public void cancellaSelezioniCorrenti() {
    	defaultForm.addParam("selid", "");
    	indexForm.addParam("selid", "");
    	
    	// TODO preso da javascript DocWay3, mancano comunque molte altre parti del javascript. 
    	// Con solo queste due righe di codice non funzionerebbe la gestione del campio di database, 
    	// azzerando il nome del db in questo punto, tutte le ricerche lato service avvengono sul
    	// database di default 'xdocwaydoc'
    	//defaultForm.addParam("db", ""); 
    	//indexForm.addParam("db", "");
    }
    
    public void svuotaSelezione() {
//    	thepreviousdb = defaultForm.addParam("db.value;
    	cancellaSelezioniCorrenti();
//    	impostaSelezioneCorrente('', null, thepreviousdb);
    }
    
    public void findplain() {
    	findplain("");
    }
    
    public void findplain(String codSocieta, String filtroLetto, String filtroScartato) {
    	findplain(codSocieta);
    	
    	// aggiunta del parametro di applicazione filtri su doc letti/non letti, scartati/non scartati
    	if (filtroLetto != null && !filtroLetto.isEmpty())
    		defaultForm.addParam("filtroLetto", filtroLetto);
    	if (filtroScartato != null && !filtroScartato.isEmpty())
    		defaultForm.addParam("filtroScartato", filtroScartato);
    }
    
    public void findplain(String codSocieta) {
    	svuotaSelezione();

    	if (codSocieta != null && codSocieta.trim().length() > 0)
    		defaultForm.addParam("_cd", setParameterFromCustomTupleValue("_CODSOCIETA_", codSocieta, defaultForm.getParam("_cd")));
    	
    	if (defaultForm.getParam("xverb") == "@refine")
    		defaultForm.addParam("xverb" ,"");

//    	return queryplain(listaRepVisCompleta, verbo, xverb);
    }
    
    public void queryplain(String listaRepVisCompleta){
    	defaultForm.addParam("repVisComp", listaRepVisCompleta);
    }

    public void escludiUOR(){
    	String toDo = defaultForm.getParam("toDo");
    	if( !toDo.contains("%escludi_uor%") ) 	
    		toDo += "%escludi_uor%";
    	else
    		toDo = toDo.replaceAll("%escludi_uor%", "");
		defaultForm.addParam("toDo", toDo);
    }
    
    public void escludiRPA(){
    	String toDo = defaultForm.getParam("toDo");
    	if( !toDo.contains("%escludi_rpa%") ) 	
    		toDo += "%escludi_rpa%";
    	else
    		toDo = toDo.replaceAll("%escludi_rpa%", "");
    	defaultForm.addParam("toDo", toDo);
    }
 
    public void workflowAttivi(String wfDb) {
        defaultForm.addParam("verbo", "workflow_query");
        defaultForm.addParam("xverb", "");
        defaultForm.addParam("selid", "");
        defaultForm.addParam("dbTable", "");
        defaultForm.addParam("query", "[wf_entryState]=\"ACTIVATED\" AND ([WE_SEARCHKEY]=%WE_SEARCH_KEYS%)");

        // ordinamento per modello di wf e, a parita' di modello, per id dello step corrente
        defaultForm.addParam("qord", "xml(xpart:/wf_entity/entity),xml(xpart:/wf_entity/currentStep/step-id)");

        if (wfDb != null && wfDb.length() > 0) {
            if (!defaultForm.getParam("db").equals(wfDb)) {
                // se il db dei flussi e' diverso da quello corrente, memorizzo quello corrente nella tupla _cd [RW 0037105]
                defaultForm.addParam("_cd", setParameterFromCustomTupleValue("activeWfDocsDb", defaultForm.getParam("db"), defaultForm.getParam("_cd")));
            }

            defaultForm.addParam("db", wfDb);
        }
    }
    
    /**
     * Selezione di un ufficio da vaschette di DocWay
     * 
     * @param uor ufficio/gruppo per il quale caricare le vaschette
     */
    public void changeUORTendina(String uor) {
    	defaultForm.addParam("dbTable", "");
    	defaultForm.addParam("xverb", "@renewCache");
    	defaultForm.addParam("personalView", "");
    	defaultForm.addParam("uorInGestioneSelected", uor);
    	
    	// parametri necessari se e' stata caricata una lista relativa ad un ufficio e poi si cambia la vista delle vaschette
    	defaultForm.addParam("verbo", "query");
    	defaultForm.addParam("selid", ""); 
    }
    
    /**
     * Selezione di un ufficio da popup delle vaschette
     * 
     * @param selectedUor UOR selezionato
     * @param uorList Lista completa degli UOR mostrati in popup
     */
    public void selectUORTendina(String selectedUor, List<Option> uorList) {
    	String uor = selectedUor.substring(2);
		
		int i = 0;
		String uorDescription = "";
		while (uorDescription.equals("") && i < uorList.size()) {
			if (uorList.get(i) != null && uorList.get(i).getValue().equals(selectedUor))
				uorDescription = uorList.get(i).getLabel();
			i++;
		}
		
		defaultForm.addParam("xverb", "@renewCache");
		defaultForm.addParam("uorInGestioneSelected", uor);
		defaultForm.addParam("docInGestioneUOR", "#|#ç" + uor + "|" + uorDescription);
    }
    
    /**
     * Caricamento di tutte le uor che l'utente puo' selezionare per la visualizzazione
     * delle vaschette
     */
    public void showAllUOR() {
    	defaultForm.addParam("verbo", "query");
    	defaultForm.addParam("xverb", "");
    	defaultForm.addParam("dbTable", "@searchUOR");
    }
    
    /**
     * Refresh delle vaschette di DocWay (sia personali che di ufficio)
     */
    public void refreshVaschette() {
    	indexForm.addParam("verbo", "query");
    	indexForm.addParam("xverb", "");
    	indexForm.addParam("dbTable", "");
    	
    	// mbernardini 17/05/2016 : aggiunto parametro per forzare il refresh delle vaschette anche se il timeout della cache non e' trascorso
    	indexForm.addParam("xverb", "@renewCache"); 
    }
    
    /**
     * Logout da applicazione DocWay
     */
    public void logout() {
    	defaultForm.addParam("verbo", "logout");
    }
    
    /**
     * Cambio di modalita' di accesso agli strumenti di amministrazione
     */
    public void changeAdmMode() {
    	defaultForm.addParam("xverb", "@changeAdministrationMode");
    }
    
    /**
     * Aggiornamento di una proprieta' dell'applicazione (strumenti di amministrazione)
     * 
     * @param propName nome della proprieta' da aggiornare
     * @param propVal valore da assegnare alla proprieta'
     */
    public void changePropertyValue(String propName, String propVal) {
    	defaultForm.addParam("verbo", "query");
    	defaultForm.addParam("xverb", "@setPropertyValue:" + propName + ":" + propVal);
    	defaultForm.addParam("dbTable", "@adm_tools");
	}
    
    /**
     * Salvataggio del profilo personale
     * 
     * @param rights
     * @param info
     * @param titlesMode modalita' di visualizzazione titoli per l'utente (lista o tabella)
     * @param homeContent definizione del contenuto da mostrare sull'homepage di docway per l'utente
     * @param mailboxes caselle di posta personali dell'utente (definite da profilo personale)
     * @param vaschette vaschette personalizzate dell'utente
     */
    public void saveProfilo(String rights, String info, String titlesMode, HomeContentDefinition homeContent, List<Mailbox> mailboxes, List<VaschettaCustom> vaschette, List<ExportPersonalizzato> esportazioni) {
    	defaultForm.addParam("toDo", rights);
    	defaultForm.addParam("xMode", info);
    	defaultForm.addParam("dbTable", "");
    	defaultForm.addParam("xverb", "@salvaProfilo");
    	
    	// salvataggio di modalita' di visualizzazione titoli e configurazione homepage di docway
    	if (titlesMode != null && !titlesMode.isEmpty())
    		defaultForm.addParam("dwTitlesMode", titlesMode);
    	if (homeContent != null && homeContent.getType() != null && !homeContent.getType().isEmpty()) {
    		defaultForm.addParam("dwHomeContent.type", homeContent.getType());
    		defaultForm.addParam("dwHomeContent.title", homeContent.getTitle());
    		defaultForm.addParam("dwHomeContent.query", homeContent.getCustomQuery());
    	}
    	
		// invio dei parametri di configurazione della mailbox (solo se configurata dall'utente)
    	if (mailboxes != null && mailboxes.size() > 0) {
    		for (int i=0; i<mailboxes.size(); i++) {
    			Mailbox mailbox = (Mailbox) mailboxes.get(i);
    			if (mailbox != null
    						&& mailbox.getEmail() != null && !mailbox.getEmail().equals("")) {
    				defaultForm.addParams(mailbox.asFormAdapterParams(".mailbox[" + i + "]"));
    			}
    		}
    	}
    	
    	// invio dei parametri di configurazione delle vaschette personalizzate
    	if (vaschette != null && vaschette.size() > 0) {
    		for (int i=0; i<vaschette.size(); i++) {
    			VaschettaCustom vaschetta = (VaschettaCustom) vaschette.get(i);
    			if (vaschetta != null
    						&& vaschetta.getTitle() != null && !vaschetta.getTitle().equals("")
    						&& vaschetta.getQuery() != null && !vaschetta.getQuery().equals("")) {
    				defaultForm.addParams(vaschetta.asFormAdapterParams(".vaschette_personalizzate.vaschetta[" + i + "]"));
    			}
    		}
    	}
    	
    	// invio dei parametri di configurazione delle esportazioni personalizzate
    	if (esportazioni != null && esportazioni.size() > 0) {
    		for (int i=0; i<esportazioni.size(); i++) {
    			ExportPersonalizzato esportazione = esportazioni.get(i);
    			if (esportazione.getTitle() != null && !esportazione.getTitle().trim().equals("")
    					&& esportazione.getQuery() != null && !esportazione.getQuery().trim().equals("")) {
    				defaultForm.addParams(esportazione.asFormAdapterParams(".exports.export[" + i + "]"));
    			}
    		}
    	}
    }
    
    /**
     * Trasferimento massivo di responsabilita' di fascicoli e documenti
     * 
     * @param enc_string
     */
    public void trasferimentoMassivo(String enc_string) {
    	indexForm.addParam("verbo", "query");
    	indexForm.addParam("xverb", "@trasferimentoMassivo");
    	indexForm.addParam("toDo", enc_string);
    }
    
    /**
     * Rigenera relazioni tra i fascicoli
     */
    public void generaRelazioniFascicoli() {
    	defaultForm.addParam("verbo", "fascicolo_response");
    	defaultForm.addParam("xverb", "@buildFascRels");
    }
    
    /**
     * Download del file di segnatura dagli strumenti di amministrazione
     * 
     * @param tipoDoc
     * @param print
     * @param tipoarc
     * @param tipoarc_custom
     */
    public void downloadFileSegnatura(String tipoDoc, String print, String tipoarc, String tipoarc_custom) {
    	String extension = "_" + print;
		
		// Federico 17/02/09: esteso meccanismo di upload/download file segnatura introducendo l'archivio a cui
	    // la segnatura e' associata [RW 0056370]
		String dbName = tipoarc;
		if (dbName.equals("centrale"))
			dbName = "";
		else if (dbName.equals("custom"))
			dbName = tipoarc_custom;
		
    	defaultForm.addParam("verbo", "attach");
    	defaultForm.addParam("propertyEntry", "segnatura_" + tipoDoc + ((dbName != null && dbName.length() > 0) ? "_" + dbName : "") + extension);
    }
    
    /**
     * Upload del file di segnatura dagli strumenti di amministrazione
     * 
     * @param tipoDoc
     * @param print
     * @param tipoarc
     * @param tipoarc_custom
     */
    public void uploadFileSegnatura(String tipoDoc, String print, String tipoarc, String tipoarc_custom) {
    	downloadFileSegnatura(tipoDoc, print, tipoarc, tipoarc_custom);
    	defaultForm.addParam("xverb", "@upload");
    }
    
    /**
     * Download di file
     * 
     * @param propertyName
     */
    public void downloadSpecificFile(String propertyName) {
    	defaultForm.addParam("verbo", "attach");
    	defaultForm.addParam("propertyEntry", propertyName);
    }
    
    /**
     * Upload di file
     * 
     * @param propertyName
     */
    public void uploadSpecificFile(String propertyName) {
    	defaultForm.addParam("verbo", "attach");
    	defaultForm.addParam("xverb", "@upload");
    	defaultForm.addParam("propertyEntry", propertyName);
    }
    
    /**
     * Importazione documenti da registro di emergenza
     */
    public void importRegistroEmergenza(String cod_sede_import) {
    	defaultForm.addParam("verbo", "importdocs_response");
    	defaultForm.addParam("xverb", "@re");
    	defaultForm.addParam("cod_sede_import", cod_sede_import);
    }
    
    /**
     * Caricamento dei documenti da smistare (dopo upload da registro
	 * di emergenza)
     */
    public void regDocsDaSmistare() {
    	defaultForm.addParam("verbo", "queryplain");
    	defaultForm.addParam("xverb", "");
    	defaultForm.addParam("query", "[docregemnumprot]=* AND NOT([doc_rifinternirifdirittonomepersona]=RPA|*)");
    }
    
    /**
     * Importazione di documenti tramite file ZIP
     * 
     * @param chkImpAttach true se occorre importare gli allegati del documento, false altrimenti
     */
    public void importDocs(boolean chkImpAttach) {
    	defaultForm.addParam("verbo", "importdocs_response");
    	defaultForm.addParam("xverb", "");
    	if (chkImpAttach)
    		defaultForm.addParam("rangeDocs", "true");
    	else
    		defaultForm.addParam("rangeDocs", "false");
    }
    
    /**
     * Avvio generazione di un report (controllo di gestione)
     */
    public void findAndPrintCtrlGestione(String codSocieta, String selid, String query, String qext, String requiredJasperFormat, String jReportParams) {
    	defaultForm.addParam("selid", selid);
    	defaultForm.addParam("query", query);
    	defaultForm.addParam("qext", qext);
		
    	defaultForm.addParam("verbo", "queryplain");
    	defaultForm.addParam("xverb", "@ctrl_gestione");
    	
    	if (codSocieta != null && codSocieta.trim().length() > 0)
    		defaultForm.addParam("_cd", setParameterFromCustomTupleValue("_CODSOCIETA_", codSocieta, defaultForm.getParam("_cd")));
		
    	defaultForm.addParam("_cd", setParameterFromCustomTupleValue("requiredJasperFormat", requiredJasperFormat, defaultForm.getParam("_cd")));
    	defaultForm.addParam("jReportParams", jReportParams);
    }
    
    /**
     * Gestione report - Stampa repertorio fascicoli
     */
    public void stampaRepertorioFascicoli(String jReportInfo, String jReportParams, String query) {
    	defaultForm.addParam("jReportInfo", jReportInfo);
    	defaultForm.addParam("jReportParams", jReportParams);
    	defaultForm.addParam("verbo", "queryplain");
    	defaultForm.addParam("xverb", "@genericPrint");
    	defaultForm.addParam("dbTable", "@stampe");
    	defaultForm.addParam("query", query);
    }
    
    /**
     * Generazione del report di registro giornaliero
     */
    public void findAndPrintRegistroGiornaliero(String view, String query, String qord) {
    	defaultForm.addParam("verbo", "queryplain");
		defaultForm.addParam("xverb", "@genericPrint");
		defaultForm.addParam("dbTable", "@print_sel");
		defaultForm.addParam("view", view);
		defaultForm.addParam("query", query);
		defaultForm.addParam("qord", qord);
		defaultForm.addParam("jReportInfo", "Stampe/xdocway/JasperReport/Stampe_Docway/registro_protocollo_dataprot.jasper%jasper%pdf%JRXW#");
		defaultForm.addParam("jReportParams", "boolean#REQ_ID=false&boolean#REQ_UOR=false&boolean#REQ_POSTIT=false&boolean#REQ_CLASSIF=false");
    }
    
    /**
     * Generazione del report di registro delle fatture
     */
    public void findAndPrintRegistroFatture(String cicloFattura, String codSocieta, String view, String selid, String query, String qord, String printFormat) {
    	defaultForm.addParam("verbo", "queryplain");
		defaultForm.addParam("xverb", "@genericPrint");
		defaultForm.addParam("dbTable", "@print_sel");
		
		defaultForm.addParam("view", view);
		defaultForm.addParam("selid", selid);
		defaultForm.addParam("query", query);
		defaultForm.addParam("qord", qord);
		
		if (codSocieta != null && codSocieta.trim().length() > 0)
    		defaultForm.addParam("_cd", setParameterFromCustomTupleValue("_CODSOCIETA_", codSocieta, defaultForm.getParam("_cd")));
		
		String jReportInfo = "Stampe/xdocway/JasperReport/Stampe_Docway/registro_fatture.jasper%jasper%pdf%JRXW#";
		String jReportParams = "string#CICLO_FATTURA=" + cicloFattura.toUpperCase() + (printFormat.equals("html") ? "&boolean#IS_IGNORE_PAGINATION=true" : "");
		
		defaultForm.addParam("jReportInfo", jReportInfo);
		defaultForm.addParam("jReportParams", jReportParams);
    }
    
    /**
     * Caricamento di una lista titoli da query e altri parametri passati
     * tramite prettyfaces (es. link in report html di controllo di gestione)
     */
    public void loadTitlesFromQuery(String query, String qord, String verbo, String db) {
    	if (verbo == null || verbo.equals(""))
    		verbo = "queryplain";
    	defaultForm.addParam("verbo", verbo);
    	defaultForm.addParam("xverb", "");
    	defaultForm.addParam("query", query);
    	if (qord != null && !qord.equals(""))
    		defaultForm.addParam("qord", qord);
    	if (db != null && !db.equals(""))
    		defaultForm.addParam("db", db);
    	defaultForm.addParam("selid", "");
    }
    
    /**
     * Invio di notifiche differite
     */
    public void invioNotifDifferite(String alias) {
    	defaultForm.addParam("verbo", "notif_differite");
    	defaultForm.addParam("xverb", "");
    	defaultForm.addParam("selid", "");
    	defaultForm.addParam("dbTable", "");
    	defaultForm.addParam("query", "[" + alias + "]=*");
    	defaultForm.addParam("qord", "");
    }
    
    /**
     * Switch del form di ricerca da generico a specifico del fascicolo speciale
     * @param dbTable
     */
    public void switchToTableFS(String dbTable) {
    	defaultForm.addParam("dbTable", defaultForm.getParam("dbTable") + "@" + dbTable);
    }
    
    /**
     * apertura in edit del titolario di classificazione
     */
    public void openEditTitolario() {
    	defaultForm.addParam("verbo", "thEdit");
    	defaultForm.addParam("xverb", "");
		defaultForm.addParam("rels", "");
		defaultForm.addParam("name", "Radice");
    }
    
    /**
     * caricamento di un documento presente in homepage (es. documenti recenti)
     */
    public void mostraDocumento(String selid, int count, int posizione, String db, String dbTable) {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "");
		
		defaultForm.addParam("pos", posizione);
		defaultForm.addParam("db", db);
		if (dbTable != null)
			defaultForm.addParam("dbTable", dbTable);
		
		defaultForm.addParam("selid", selid);
		defaultForm.addParam("count", count);

		defaultForm.addParam("auditVisualizzazione", "true");
	}
    
    /**
     * caricamento della pagina di showdoc di un organo 
     * */
    public void mostraDocumentoQ(String posizione, String sel, String db){
    	if(db.equals("acl") || db.indexOf("acl-")== 0){
    		String uri = defaultForm.getParam("uri").replaceAll("application", "base");
    		String tipo = "";
    		
    		if(uri.indexOf("xdocwayaooadm") != -1){
    			tipo = "xdocwayaooadm";
    		}else if(uri.indexOf("xdocwayadm") != -1){
    			tipo = "xdocwayadm";
    		}else {
    			tipo = "xdocway";
    		}
    		
    		uri = uri.replaceAll("xdocwayaooadm", "acl");
    		uri = uri.replaceAll("xdocwayadm", "acl");
    		uri = uri.replaceAll("xdocway", "acl");
    		
    		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("bckOrgUrl", db + "|" + tipo, getCustomTupleName()));
    		
    		//defaultForm.addParam("uri", uri);
    		defaultForm.addParam("verbo", "showdoc");
    		defaultForm.addParam("pos", posizione);
    		defaultForm.addParam("selid", sel);
    		defaultForm.addParam("db", db);
    		defaultForm.addParam("userRights", "");
    	}else{
    		defaultForm.addParam("verbo", "showdoc");
    		defaultForm.addParam("pos", posizione);
    		defaultForm.addParam("selid", sel);
    		defaultForm.addParam("db", db);
    	}
    }
    
    public void inserisciDocInACL(String table){
    	String uri = defaultForm.getParam("uri").replaceAll("application", "base");
		String tipo = "";
		
		if(uri.indexOf("xdocwayaooadm") != -1){
			tipo = "xdocwayaooadm";
		}else if(uri.indexOf("xdocwayadm") != -1){
			tipo = "xdocwayadm";
		}else {
			tipo = "xdocway";
		}
		
		uri = uri.replaceAll("xdocwayaooadm", "acl");
		uri = uri.replaceAll("xdocwayadm", "acl");
		uri = uri.replaceAll("xdocway", "acl");
		
		defaultForm.addParam("_cd",FormsAdapter.setParameterFromCustomTupleValue("bckOrgUrl", getDb() + "|" + tipo, defaultForm.getParam("_cd")));
		
		defaultForm.addParam("uri", uri);
		defaultForm.addParam("verbo", "docedit");
		defaultForm.addParam("pos", "");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", "");
		defaultForm.addParam("db", getAclDb());
		defaultForm.addParam("dbTable", table);
		defaultForm.addParam("userRights", "");
    }
    
    /**
     * Creazione di documenti tramite drag&drop di files
     */
    public void creaDocByDragAndDrop(String fileIds, String fileTitles) {
    	defaultForm.addParam("verbo", "draganddropfiles");
    	defaultForm.addParam("xverb", "createDoc");
    	defaultForm.addParam("pos", "");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", "");
    	defaultForm.addParam("fileids", fileIds);
    	defaultForm.addParam("filetitles", fileTitles);
    }
    
    /**
     * Individuazione degli errori presenti all'interno degli assegnatari definiti sulle voci d'indice dell'archivio
	 * procedimenti
     */
    public void evaluateAssegnatariVociIndice() {
    	defaultForm.addParam("verbo", "vociindice_response");
    	defaultForm.addParam("xverb", "evaluateAssegnatari");
    }
    
    /*
     * Controlla se è possibile effettuare il login con le credenziali passate e in caso restituisce la username/login
     */
	public void checkDelegaAndGetLogin(String codDelegante, String codDelegato) {
		defaultForm.addParam("verbo", "deleghe");
		defaultForm.addParam("xverb", "checkOkDelega");
		defaultForm.addParam("codDelegante", codDelegante);
		defaultForm.addParam("codDelegato", codDelegato);
	}

}
