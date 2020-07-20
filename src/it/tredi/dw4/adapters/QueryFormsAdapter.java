package it.tredi.dw4.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.DocumentException;
import org.dom4j.Element;

public class QueryFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	protected FormAdapter indexForm;
	protected FormAdapter hierBrowserForm;
	
	public QueryFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
		this.indexForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
		this.hierBrowserForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}
	
	public FormAdapter getIndexForm() {
		return indexForm;
	}
	
	public FormAdapter getHierBrowserForm() {
		return hierBrowserForm;
	}
	
	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		resetForms();
		
		fillDefaultFormFromResponse(response);
		fillIndexFormFromResponse(response);
		fillHierBrowserFormFromResponse(response);
	}
	
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		Element root = response.getRootElement();
		
		defaultForm.addParam("verbo", "query");
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		defaultForm.addParam("childLast", "1");
		defaultForm.addParam("qord", "");
		defaultForm.addParam("sele", root.attributeValue("sele", ""));
		defaultForm.addParam("db", root.attributeValue("db", ""));
		
		/* memorizza lo stato di esplosione dell'albero gerarchico */
		defaultForm.addParam("hierSearchList", "");
		defaultForm.addParam("hierSearchShow", root.attributeValue("hierSearchShow", ""));
		defaultForm.addParam("cuttingBranch", "");
		
		/* per i repertori su cui ho visione completa */
		defaultForm.addParam("repVisComp", "");
		
		/* per navigazione su thesauro */ 
		defaultForm.addParam("keypath", "''");
		defaultForm.addParam("startkey", "''");
		defaultForm.addParam("relation", "''");
		defaultForm.addParam("xMode", "");
		
		/* per navigazione su thesauro vincolato */ 
		defaultForm.addParam("lookup_new", "");
		defaultForm.addParam("lookup_campi", "");
		defaultForm.addParam("lookup_fieldVal", "");
		
		/* management export documents */
		defaultForm.addParam("exclusionFields", "");
		defaultForm.addParam("inclusionFields", "");
		defaultForm.addParam("rangeDocs", "");
		defaultForm.addParam("pathFiles", "");
		
		/* uso speciale per upload di file da strumenti di amministrazione */
		defaultForm.addParam("propertyEntry", "");
		
		/* per ultimo accesso e cambio password */
		defaultForm.addParam("userInfo", root.attributeValue("userInfo", ""));
		defaultForm.addParam("pwdLock", root.attributeValue("pwdLock", ""));
		defaultForm.addParam("pwdPhysdoc", root.attributeValue("pwdPhysdoc", ""));
		defaultForm.addParam("physDoc", root.attributeValue("physDoc", ""));
		defaultForm.addParam("pwdChangeInfo", "");
		
		/* per inserimento/modifica di repertori */ 
		defaultForm.addParam("codice_rep", "");
		defaultForm.addParam("descrizione_rep", "");
		
		/* tupla per il pulsante 'back' del thesauro gerarchico */
		defaultForm.addParam("thBackPage", root.attributeValue("", "thBackPage"));
		
		defaultForm.addParam("personalView", "");
		
		/* introdotta tupla per informazioni di avviamento delle stampe Jasper [RW 0039816] */
		defaultForm.addParam("jReportInfo", "");
		defaultForm.addParam("jReportParams", "");
		
		/*  introdotta possibilita' di indicare un file xsl da usare per filtrare i documenti esportati [RW 0047738] */
		defaultForm.addParam("xslExportFilterName", "");
		
		/* per ultimo accesso e cambio password */
		String pwdInfo = "";
		if (!root.attributeValue("lastLoginDate", "").equals("") || !root.attributeValue("pwdLeftTime", "").equals(""))
			pwdInfo = root.attributeValue("lastLoginDate", "") + "|" + root.attributeValue("lastLoginTime", "") + "|" + root.attributeValue("pwdLeftTime", "");
		defaultForm.addParam("pwdInfo", pwdInfo);
		
		//inserimento in un fascicolo
		defaultForm.addParam("physDoc_infasc", root.attributeValue("physDoc_infasc", ""));
		defaultForm.addParam("diritto_infasc", root.attributeValue("diritto_infasc", ""));
		defaultForm.addParam("nome_persona_infasc", root.attributeValue("nome_persona_infasc", ""));
		defaultForm.addParam("nome_uff_infasc", root.attributeValue("nome_uff_infasc", ""));
		defaultForm.addParam("cod_persona_infasc", root.attributeValue("cod_persona_infasc", ""));
		defaultForm.addParam("cod_uff_infasc", root.attributeValue("cod_uff_infasc", ""));
	}
	
	protected void fillIndexFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(indexForm, response);
		Element root = response.getRootElement();
		
		indexForm.addParam("verbo", "showindex");		
		indexForm.addParam("xverb", "");
		indexForm.addParam("db", root.attributeValue("db", ""));
		indexForm.addParam("keypath", "");
		indexForm.addParam("fillField", "");
		indexForm.addParam("startkey", "");
		indexForm.addParam("keyCount", root.attributeValue("keyCount", ""));
		indexForm.addParam("threl", "");
		indexForm.addParam("doubleKey", "");
		indexForm.addParam("shwMode", "");
		indexForm.addParam("xMode", "");
		
		indexForm.addParam("minf", root.attributeValue("minf", ""));
		indexForm.addParam("maxf", root.attributeValue("maxf", ""));
		indexForm.addParam("xRels", "");
		indexForm.addParam("vRels", "");
		indexForm.addParam("cPath", "");
		indexForm.addParam("lRel", "");
		indexForm.addParam("sele", "");
		indexForm.addParam("destPage", "");
		indexForm.addParam("winTarget", "");
	}
	
	protected void fillHierBrowserFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(hierBrowserForm, response);
		Element root = response.getRootElement();
		
		hierBrowserForm.addParam("verbo", "hierBrowser");
		hierBrowserForm.addParam("hierStatus", root.attributeValue("hierStatus", ""));
		hierBrowserForm.addParam("host", root.attributeValue("host", ""));
		hierBrowserForm.addParam("port", root.attributeValue("port", ""));
		
		hierBrowserForm.addParam("db", root.attributeValue("db", ""));
		hierBrowserForm.addParam("user", root.attributeValue("user", ""));
		hierBrowserForm.addParam("pwd", root.attributeValue("pwd", ""));
		hierBrowserForm.addParam("docStart", root.attributeValue("docStart", ""));
		hierBrowserForm.addParam("opt", root.attributeValue("opt", ""));
		hierBrowserForm.addParam("docToggle", "0");
		hierBrowserForm.addParam("hierCount", root.attributeValue("hierCount", ""));
		hierBrowserForm.addParam("transformJava", root.attributeValue("transformJava", ""));
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
		this.indexForm.resetParams();
		this.hierBrowserForm.resetParams();
	}
	
	/* ACTIONS - DEFAULTFORM */
	
	/**
	 * caricamento di una pagina di ricerca
	 * 
	 * @param tableName
	 * @param general
	 */
	public void gotoTableQ(String tableName, boolean general) {
		defaultForm.addParam("query", "");
		defaultForm.addParam("dbTable", "@" + tableName);
		
		if (general != true)
			defaultForm.addParam("selTable", "@" + tableName);
		else
			defaultForm.addParam("selTable", "*@" + tableName);
		
		if ( tableName.equals("adm_tools") ) {
			String listOfPropertiesAdmTools = "invioEmailNotifica:Si:Si";

			// TODO da completare, personalizzazioni
			/*
			if (typeof (customSetPropertyStatus) != 'undefined') {
				listOfPropertiesAdmTools = customSetPropertyStatus(listOfPropertiesAdmTools);
			}

			if (typeof (customLoadPropertiesList) != 'undefined') {
				customLoadPropertiesList();
			}
			*/
			
			defaultForm.addParam("_cd", setParameterFromCustomTupleValue("listOfPropertiesAdmTools", listOfPropertiesAdmTools, defaultForm.getParam("_cd")));
		}
		
		if ( tableName.equals("collect") ) {
			//TODO
		}
		else {
			
			//FIXME setGlobalFormRestore('dbTable', 'personalView');
			
			int index = tableName.indexOf("#personalView="); 
			if (index != -1) {
				defaultForm.addParam("dbTable", "@" + tableName.substring(0, index));
				defaultForm.addParam("personalView", tableName.substring(index + 14));
			}
			
			/* FIXME 
			 * if ( typeof(customGotoTableQ) != 'undefined' ) {
			 *	// e' previsto che la funzione custom faccia il submit
			 *	customGotoTableQ(name);
			 *	}
			 */
			
			defaultForm.addParam("verbo", "query");
		}
		
	}
	
	/**
	 * caricamento di una pagina di ricerca per uno specifico repertorio con campi custom definiti dall'utente
	 * 
	 * @param tableName
	 * @param general
	 * @param customFieldsKey
	 */
	public void gotoTableQCustomFieldsRep(String tableName, boolean general, String customFieldsKey) {
		gotoTableQ(tableName, general);
		defaultForm.addParam("customFieldsKey", customFieldsKey);
	}
	
	public void gotoTableQCustomFieldsFasc(String tableName, boolean general, String customFieldsKey, String codFasc, String descrFasc) {
		gotoTableQ(tableName, general);
		defaultForm.addParam("codice_fasc", codFasc);
		defaultForm.addParam("descrizione_fasc", descrFasc);
		defaultForm.addParam("customFieldsKey", customFieldsKey);
	}
	
	
	/**
	 * caricamento della home page di docway
	 */
	public void loadDocWayMainPage() {
		defaultForm.addParam("verbo", "query");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("query", "");
		defaultForm.addParam("dbTable", "@qmainpage");
	}
	
	protected void svuotaSelezione() {
//		String previousDb = defaultForm.getParam("db");
		cancellaSelezioniCorrenti();
	}
	
	protected void cancellaSelezioniCorrenti() {
//		String listSelId = defaultForm.getParam("selid");
//		String listDb = defaultForm.getParam("db");
		
		//TODO
	}
	
	//functione per ricerche senza raffinamento (anche se selId è valorizzata) 
	public void findPlain(String query, String verbo, String xverb) {
		if (defaultForm.getParam("xverb").equals("@refine"))
			defaultForm.addParam("xverb", "");
		
		queryPlain(query, verbo, xverb);
	}
	
	@SuppressWarnings("unchecked")
	public void queryPlain(String query, String verbo, String xverb) {
		String repVisioneCompleta = "";
		
		if( null != lastResponse ) {
			Iterator<Element> repertorioIt = this.lastResponse.selectNodes("//repertorio/tabella[@vis='completa']").iterator();
			while( repertorioIt.hasNext()) {
				Element repertorio = repertorioIt.next();
				repVisioneCompleta += repertorio.getParent().attributeValue("codice", "");
				repVisioneCompleta += "-";
				repVisioneCompleta += repertorio.getParent().attributeValue("tipo", "");
				repVisioneCompleta += "|";
			}
		}
		
		if (!repVisioneCompleta.equals(""))
			this.defaultForm.addParam("repVisComp", repVisioneCompleta);
		
		defaultForm.addParam("query", query);
		
		//FIXME setExtensionsAndOrd();
		
		if (!defaultForm.getParam("xverb").equals("@refine")) {
			svuotaSelezione();
		}
		else {
			String tmpRefineQuery = "([?SEL]=\"" + defaultForm.getParam("selid") + "\")";
			if (!query.equals("")) {
				tmpRefineQuery = "(" + query + ") AND " + tmpRefineQuery;
			}
			
			svuotaSelezione();
			defaultForm.addParam("query", tmpRefineQuery);
			defaultForm.addParam("xverb", "");
		}
		
		if (xverb == null)
			xverb = "";
		
		if (verbo == null || verbo.equals("")) {
			verbo = "queryplain";
		}
			
		sendQuery(verbo, xverb);
	}
	
	public void insTableDoc(String tableName) {
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("pos", "0");
		defaultForm.addParam("physDoc", "0");
		defaultForm.addParam("selid", "");

		int index = tableName.indexOf("#personalView=");
		if (index != -1) { //si tratta di una personalView
			defaultForm.addParam("dbTable", "@" + tableName.substring(0, index));
			defaultForm.addParam("personalView",  tableName.substring(index + 14));
		}
		else {
			defaultForm.addParam("dbTable", "@" + tableName);
		}

		/* FIXME
		 *
		 * if ( typeof(customInsTableDoc) != 'undefined' ) {
		 * 	customInsTableDoc();
		 * 	return false;
		 * }
		*/
	}
	
	/**
	 * Nuovo inserimento da Lookup (chiamato dal passaggio
	 * da DocWay ad ACL tramite pretty faces)
	 */
	public void insTableDocLookup(String lookupNew, String lookupFieldVal) {
		defaultForm.addParam("lookup_new", lookupNew);
		String[] lookupNewParams = lookupNew.split(";");
		
		String dbTable = lookupNewParams[1].substring(lookupNewParams[1].indexOf("=") + 1);
		
		// Era presente ma non utilizzato neppure sul javascript di DocWay3
		//String fillField = lookupNewParams[2].substring(lookupNewParams[2].indexOf("=") + 1);
		
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("dbTable", "@"+dbTable);
		defaultForm.addParam("selid", "");
		defaultForm.addParam("pos", 0); // Sul javascript era impostato a vuoto (ma poi darebbe una 'java.lang.NumberFormatException')
		defaultForm.addParam("lookup_fieldVal", lookupFieldVal);
	}
	
	protected void sendQuery(String verbo, String xverb) {
		/*FIXME
		 *   if (!checkBeforeSendQuery(getForm('hxpForm').query.value))
		 *   	return;
		 */
		
		if (verbo == null)
			verbo = "query";
        	
		/*FIXME
		 * if((!isHierBrowser) && (typeof(customQueryHandler) != 'undefined')) {
         *		customQueryHandler(getForm('hxpForm').query);
    	 *	}
		 */
		
		if (xverb == null)
			xverb = "";
		
		/*FIXME
		 * return (typeof(customSendRequest) != 'undefined' ? customSendRequest(paramVerbo, toRestore, xverb) :
		 */
		
		defaultForm.addParam("verbo", verbo);
		defaultForm.addParam("xverb", xverb);
	}
	
	/* ACTIONS - INDEXFORM */
	
	public void openIndex(
			String keyPath, // identifica la chiave negli indici
			String startKey,// posizionamento
			String shwMode, // imposta la modalità di formattazione ('dt' per data)
			String common,  // parte comune (se contiene '|^|' allora la parte della chiave che precede '|^|' viene scartata)
			String threl,   // opzionalmente l'indice della relazione con cui attingere la descrizione associata alla chiave (classificazioni)
			String inputName,//l'input dove depositare l'esito della selezione
			String windowTitle, // window title
			String value, 
			boolean acceptEmptySelection) { //se la selezione è vuota forza comunque il vocabolario su quella selezione 
		
		String vocTitle = "Vocabolario";
		if (windowTitle != null && windowTitle.length() > 0)
			vocTitle = windowTitle;
		this.indexForm.addParam("verbo", "showindex");
		this.indexForm.addParam("view", vocTitle);

/* FIXME
	  	if("undefined"!=typeof(customGetKeyName))
	   		keyPath=customGetKeyName(keyPath);   		

	  if(("undefined"!=typeof(theInput))&&(""!=theInput))
	  {//element=theInput;
	   qKey=theInput;
	   element=qForm[qKey];
	  }
	  else
	  {if ("undefined"!=typeof(customGetIndexedElement))
	   {qKey=customGetIndexedElement(keyPath);
	    element=qForm[qKey];
	   }
	   else
	   {proArch="";
	    if("undefined"!=typeof(getNowArch))
	     proArch="#"+getNowArch(getForm('hxpForm').db.value);
	    qKey="["+keyPath+"]"+proArch;
	    element=qForm[qKey];
	    if(!element){
	     qKey="["+keyPath+"]";
	     element=qForm[qKey];
	    }
	   }
	  }
	  */

		if (value != null && value.length() > 0)
			startKey = value;
		if (startKey.startsWith("\""))
			startKey = startKey.substring(1);
	  
		if (common == null) common = "";
		if (startKey == null) startKey = "";
		this.indexForm.addParam("threl", threl == null? "": threl);
		this.indexForm.addParam("cPath", common == null? "": common);
		this.indexForm.addParam("doubleKey", common.equals(" ")? "1": "0");
		this.indexForm.addParam("shwMode", shwMode == null? "": shwMode);
		this.indexForm.addParam("sele", "");
		this.indexForm.addParam("startkey", common + startKey);
		this.indexForm.addParam("selid", this.defaultForm.getParam("selid")); //FIXME
		this.indexForm.addParam("acceptEmptySelection", acceptEmptySelection);

		if (inputName != null && inputName.length() > 0)
			this.indexForm.addParam("fillField", inputName);
		else
			this.indexForm.addParam("fillField", keyPath);
		
		this.indexForm.addParam("keypath", keyPath);

	  //FIXME if (!raffinamento) svuotaSelezione();
	}	
	
	/**
	 * login derivante da multilogin (scelta della matricola)
	 * @param matricola
	 */
	public void loginWithMatricola(String matricola) {
		this.defaultForm.addParam("matricola", matricola);
	}
	
	/**
	 * login derivante da multisocieta' (scelta della societa')
	 * @param codSocieta
	 * @param tutte
	 * @param matricola
	 */
	public void loginWithSocietaMatricola(String codSocieta, boolean tutte, String matricola) {
		if (tutte)
			this.defaultForm.addParam("_setMultiSoc", codSocieta);
		this.defaultForm.addParam("setMultiSoc", codSocieta);
		this.defaultForm.addParam("matricola", matricola);
	}
	
	/* ACTIONS - HIERBROWSERFORM */
	
	public void paginaBrowse(String numDoc, boolean toggle, String numRows, String aclDb) {
		if (toggle)
			hierBrowserForm.addParam("docToggle", numDoc);
		if (numRows != null && numRows.length() > 0)
			hierBrowserForm.addParam("hierCount", numRows);
		hierBrowserForm.addParam("docStart", numDoc);
		hierBrowserForm.addParam("verbo", "hierBrowser");
		hierBrowserForm.addParam("hierStatus", "");
		if (aclDb != null && aclDb.length() > 0)
			hierBrowserForm.addParam("db", aclDb);
	}
	
	public void paginaBrowse(String numDoc){
		paginaBrowse(numDoc, true, "", "");
	}
	
	/**
	 * chiamata al processo di cambio password
	 * 
	 * @param login
	 * @param password
	 * @param confirm
	 * @throws UnsupportedEncodingException if UTF-8 is not supported (we hope this will never happen)
	 */
	public void aclConfirmChangePwd(String login, String password, String confirm) throws UnsupportedEncodingException {
	    aclConfirmChangePwd(login, "#null#", password, confirm);
	}
	
	/**
	 * chiamata al processo di cambio password
	 * 
	 * @param login
	 * @param currentpassword
	 * @param newpassword
	 * @param confirmpassword
	 * @throws UnsupportedEncodingException if UTF-8 is not supported (we hope this will never happen)
	 */
	public void aclConfirmChangePwd(String login, String currentpassword, String newpassword, String confirmpassword) throws UnsupportedEncodingException {
		defaultForm.addParam("xverb", "@confirmChangePwd");
	    //getForm('hxpForm').userRights.value = "";

		String encodedLogin = Base64.encodeBase64String(login.getBytes("UTF-8"));
		String currentEncodedPassword = Base64.encodeBase64String(currentpassword.getBytes("UTF-8"));
		String newEncodedPassword = Base64.encodeBase64String(newpassword.getBytes("UTF-8"));
		String confirmEncodedPassword = Base64.encodeBase64String(confirmpassword.getBytes("UTF-8"));
		
		defaultForm.addParam("_cd", setParameterFromCustomTupleValue("forceLogin", "true", defaultForm.getParam("_cd")));
	    defaultForm.addParam("pwdChangeInfo", encodedLogin + "|||" + currentEncodedPassword + "|||" + newEncodedPassword + "|||" + confirmEncodedPassword);
	}

	public void aclAnnulChangePwd(){
	    if (defaultForm.getParam("selid").equals("")){
	    	defaultForm.addParam("pos", (defaultForm.getParamAsInt("physDoc")- 1)); 
	    }
	    defaultForm.addParam("xverb", "@aclAnnulChangePwd");
	}

	public void aclPwdChanged()
	{
	    if (defaultForm.getParam("selid").equals("")){
	    	defaultForm.addParam("pos", (defaultForm.getParamAsInt("physDoc")- 1)); 
	    }
	    
	    defaultForm.addParam("verbo", "query");
	    defaultForm.addParam("xverb", "@pwdChanged");
	    defaultForm.addParam("dbTable", "");
	    defaultForm.addParam("query", "");
	}
	
	/**
	 * Caricamento della maschera di cambio password
	 * 
	 * @param login login dell'utente per il quale e' richiesto il cambio di password
	 */
	public void goToChangePwd(String login) {
	    //setGlobalFormRestore('verbo','xverb','pwdLogin');
		defaultForm.addParam("verbo", "query");
		defaultForm.addParam("xverb", "@changePwd");
		
		if (login != null && !login.equals(""))
			defaultForm.addParam("pwdLogin", login);
	}
	
    /**
     * Lookup da report Docway (controllo di gestione)
     */
    public void customPrintLookup(String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) {
    	indexForm.addParam("verbo", "lookup_response");
		indexForm.addParam("selid", "");
		indexForm.addParam("qord", ord);
		indexForm.addParam("lookup_titolo", titolo);
		indexForm.addParam("lookup_campi", campi);
		indexForm.addParam("lookup_xq", xq == null? "": xq);
		indexForm.addParam("lookup_new", newRecord == null? "" : newRecord);
		indexForm.addParam("lookup_db", db == null? "": db);
		indexForm.addParam("lookup_fieldVal", value);
		indexForm.addParam("lookup_alias", aliasName);
		
		if (aliasName1 != null && aliasName1.length() > 0)
			indexForm.addParam("lookup_alias", aliasName + "|" + aliasName1);
    }
    
    /**
     * Stampa di report (ricerca documenti e produzione del report)
     */
    public void findAndPrint(String codSocieta, String selid, String query, String qext, String qord, String view, String jReportInfo, String jReportParams) {
    	defaultForm.addParam("view", view);
    	defaultForm.addParam("jReportInfo", jReportInfo);
    	defaultForm.addParam("jReportParams", jReportParams);
    	
    	if (codSocieta != null && codSocieta.trim().length() > 0)
    		defaultForm.addParam("_cd", setParameterFromCustomTupleValue("_CODSOCIETA_", codSocieta, defaultForm.getParam("_cd")));
    	
    	defaultForm.addParam("selid", selid);
    	defaultForm.addParam("query", query);
    	defaultForm.addParam("qext", qext);
    	defaultForm.addParam("qord", qord);
		
    	defaultForm.addParam("verbo", "queryplain");
    	defaultForm.addParam("xverb", "@print");
    }
    
    /**
     * Caricamento della pagina di ricezione mail (stile webmail)
     */
    public void gotoRicezionePosta() {
    	defaultForm.addParam("verbo", "webmail");
    	defaultForm.addParam("xverb", "@login");
    }
    
    /**
	 * Inserimento di un nuovo repertorio
	 */
	public void insTableDocRep(String tableName, String codRep, String descrRep) {
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("physDoc", "0");
		this.defaultForm.addParam("pos", "0");
		this.defaultForm.addParam("selid", "");
		this.defaultForm.addParam("dbTable", "@" + tableName);
		if (codRep.indexOf("personalView_") == 0) {
			this.defaultForm.addParam("personalView", codRep.indexOf("_") + 1);
			codRep = "";
			descrRep = "";
		}
		this.defaultForm.addParam("codice_rep", codRep);
		this.defaultForm.addParam("descrizione_rep", descrRep);
		
		this.defaultForm.addParam("selTable", "");
	}
	
	/**
	 * caricamento di una lista titoli
	 * @param selid selid della selezione da caricare
	 */
	public void gotoTitles(String selid) {
		defaultForm.addParam("verbo", "showtitles");
    	defaultForm.addParam("selid", selid);
    	defaultForm.addParam("pos", 0);
    	
    	defaultForm.addParam("toDo", "ord"); //forzo ordinamento
	}
	
    /**
     * Caricamento della pagina con la console di APV delle decretazioni
     */
    public void gotoConsoleDecretazioniApv() {
    	defaultForm.addParam("verbo", "consoleapv");
    	defaultForm.addParam("xverb", "@decretazioni");
    }
    
    /**
     * Caricamento della pagina con la console di APV delle ordini
     */
    public void gotoConsoleOrdiniApv() {
    	defaultForm.addParam("verbo", "consoleapv");
    	defaultForm.addParam("xverb", "@ordini");
    }

    /**
     * Caricamento della pagina con la console di APV delle fatture
     */
    public void gotoConsoleFattureApv() {
    	defaultForm.addParam("verbo", "consoleapv");
    	defaultForm.addParam("xverb", "@fatture");
    }
}
