package it.tredi.dw4.adapters;


import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.model.azionimassive.Azione;
import it.tredi.dw4.utils.XMLDocumento;

public class DocumentFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	protected FormAdapter indexForm;

	public DocumentFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
		this.indexForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}

	public FormAdapter getDefaultForm() {
		return defaultForm;
	}

	public FormAdapter getIndexForm() {
		return indexForm;
	}


	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		resetForms();

		fillDefaultFormFromResponse(response);
		fillIndexFormFromResponse(response);
	}

	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		Element root = response.getRootElement();

		defaultForm.addParam("sele", root.attributeValue("sele", ""));
		defaultForm.addParam("verbo", root.attributeValue("verbo", ""));
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));

		//l'opzione "(lt:1)" in xDoc non viene piu' usata dai tempi del Nif e degrada pure le prestazioni,
		//oltre a creare problemi con le eventuali entity &#... a causa delle processing instruction inserite da xw [RW 0052875]
		defaultForm.addParam("xDoc", "");

		defaultForm.addParam("asText", root.attributeValue("asText", ""));
		defaultForm.addParam("pne", root.attributeValue("pne", ""));
		defaultForm.addParam("qord", root.attributeValue("qord", ""));
		defaultForm.addParam("destPage", root.attributeValue("destPage", ""));
		defaultForm.addParam("physDoc", root.attributeValue("physDoc", ""));
		defaultForm.addParam("physDoc_infasc", root.attributeValue("physDoc_infasc", ""));
		defaultForm.addParam("rifInt", root.attributeValue("rifInt", "")); //esclusivo per i riferimenti interni
		defaultForm.addParam("attach", root.attributeValue("attach", "")); //esclusivo per allegati
		defaultForm.addParam("id", root.attributeValue("id", "")); //esclusivo per allegati
		defaultForm.addParam("name", root.attributeValue("name", "")); //esclusivo per allegati
		defaultForm.addParam("postitPos", ""); //posizione del postit
		defaultForm.addParam("postitText", ""); //testo del postit (modifica)
		defaultForm.addParam("dbToUse", ""); //db da utilizzare (nel caso di proiezioni)

		//per la creazione di sottofascicoli
		defaultForm.addParam("fasc_numero_sottofasc", "");
		defaultForm.addParam("physDoc_sottofasc", root.attributeValue("physDoc_sottofasc", ""));
		defaultForm.addParam("nome_persona_sottofasc", root.attributeValue("nome_persona_sottofasc", ""));
		defaultForm.addParam("nome_uff_sottofasc", root.attributeValue("nome_uff_sottofasc", ""));
		defaultForm.addParam("cod_persona_sottofasc", root.attributeValue("cod_persona_sottofasc", ""));
		defaultForm.addParam("cod_uff_sottofasc", root.attributeValue("cod_uff_sottofasc", ""));

		//introdotto tipo del valore contenuto nei campi ufficio [RW 0043770]
		defaultForm.addParam("tipo_uff_sottofasc", root.attributeValue("tipo_uff_sottofasc", ""));

		defaultForm.addParam("classif_sottofasc", root.attributeValue("classif_sottofasc", ""));
		defaultForm.addParam("classif_cod_sottofasc", root.attributeValue("classif_cod_sottofasc", ""));

		//per inserimento/modifica di repertori
		defaultForm.addParam("codice_rep", "");
		defaultForm.addParam("descrizione_rep", "");

		//per visualizzazione della storia
		defaultForm.addParam("oldTable", root.attributeValue("oldTable", ""));

		//tuple per l'apertura del thesauro dalle molliche relative alla classificazione del documento
		defaultForm.addParam("keypath", root.attributeValue("keypath", "")); //la chiave
		defaultForm.addParam("startkey", root.attributeValue("startkey", ""));
		defaultForm.addParam("xMode", root.attributeValue("xMode", ""));
		defaultForm.addParam("relation", root.attributeValue("relation", "")); //stringa che identifica la relazione richiesta
		defaultForm.addParam("cPath", root.attributeValue("cPath", "")); //percorso comune

		defaultForm.addParam("personalView", root.attributeValue("personalViewToUse", ""));

		defaultForm.addParam("convReq", root.attributeValue("convReq", "")); //riporta le conversioni richieste sull'ultima versione

		//contiene le informazioni necessarie per visualizzare i doc. collegati a quello visualizzato
		defaultForm.addParam("showLinkedDocsInfo", root.attributeValue("showLinkedDocsInfo", ""));

		defaultForm.addParam("nodeType", root.attributeValue("nodeType", ""));

	    addBeforeEndEmbedded(defaultForm, response);
	}

	protected void fillIndexFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(indexForm, response);
		Element root = response.getRootElement();

		indexForm.addParam("verbo", root.attributeValue("showindex", ""));
		indexForm.addParam("xverb", "");
		indexForm.addParam("keyCount", root.attributeValue("keyCount", ""));
		indexForm.addParam("minf", root.attributeValue("minf", ""));
		indexForm.addParam("maxf", root.attributeValue("maxf", ""));
		indexForm.addParam("destPage", root.attributeValue("destPage", ""));
		indexForm.addParam("lookup_new", root.attributeValue("lookup_new", ""));
		indexForm.addParam("fromLookup", root.attributeValue("fromLookup", ""));
		indexForm.addParam("pageCount", root.attributeValue("pageCount", ""));
		indexForm.addParam("fillField", "");
		indexForm.addParam("startkey", "");
		indexForm.addParam("threl", "");
		indexForm.addParam("doubleKey", "");
		indexForm.addParam("shwMode", "");
		indexForm.addParam("xMode", "");
		indexForm.addParam("xRels", "");
		indexForm.addParam("vRels", "");
		indexForm.addParam("cPath", "");
		indexForm.addParam("lRel", "");
		indexForm.addParam("sele", "");
		indexForm.addParam("lookup_titolo", "");
		indexForm.addParam("lookup_campi", "");
		indexForm.addParam("lookup_xq", "");
		indexForm.addParam("lookup_db", "");
		indexForm.addParam("lookup_fieldVal", "");
		indexForm.addParam("qord", "");
		indexForm.addParam("lookup_alias", "");
		indexForm.addParam("relation", "");
	}

	protected void resetForms() {
		this.defaultForm.resetParams();
		this.indexForm.resetParams();
	}

	/* ACTIONS - DEFAULTFORM */

	public void paginaTitoli() {
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");

		defaultForm.addParam("verbo", "showtitles");
		defaultForm.addParam("pos", (int)Math.floor(pos / pageCount) * pageCount);

		String wDbTable = defaultForm.getParam("dbTable");
		if (wDbTable.indexOf("@workflow") != -1) {
			defaultForm.addParam("dbTable", "@workflow");
		}
	}

	public boolean isPaginaTitoliEnabled() {
		String selid = defaultForm.getParam("selid");
		int count = defaultForm.getParamAsInt("count");
		return (selid.length() > 0 && count > 1);
	}

	public boolean primaPagina() {
		if (isPrimaPaginaEnabled()) {
			defaultForm.addParam("verbo", "showdoc");
			defaultForm.addParam("pos", 0);
			defaultForm.addParam("auditVisualizzazione", "true");

			setXverbForViewer();
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isPrimaPaginaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0)
			return true;
		else
			return false;
	}

	public boolean paginaPrecedente() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0) {
			defaultForm.addParam("verbo", "showdoc");
			defaultForm.addParam("pos", pos - 1);
			defaultForm.addParam("auditVisualizzazione", "true");

			setXverbForViewer();
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isPaginaPrecedenteEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0)
			return true;
		else
			return false;
	}

	public boolean isIndietroRapidoEnabled() {
		return isPaginaPrecedenteEnabled();
	}

	/**
	 * Caricamento di una pagina specifica della lista titoli
	 * @param pos
	 * @return
	 */
	public void paginaSpecifica(int pos) {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("pos", pos - 1);
		defaultForm.addParam("auditVisualizzazione", "true");

		setXverbForViewer();
	}

	public boolean paginaSuccessiva() {
		int pos = defaultForm.getParamAsInt("pos");
		int count = defaultForm.getParamAsInt("count");
		if (count > pos + 1) {
			defaultForm.addParam("verbo", "showdoc");
			defaultForm.addParam("pos", pos + 1);
			defaultForm.addParam("auditVisualizzazione", "true");

			setXverbForViewer();
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isPaginaSuccessivaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		int count = defaultForm.getParamAsInt("count");
		if (count > pos + 1)
			return true;
		else
			return false;
	}

	public boolean isAvantiRapidoEnabled() {
		return isPaginaSuccessivaEnabled();
	}

	public boolean ultimaPagina() {
		if (isUltimaPaginaEnabled()) {
			int count = defaultForm.getParamAsInt("count");
			defaultForm.addParam("verbo", "showdoc");
			defaultForm.addParam("pos", count - 1);
			defaultForm.addParam("auditVisualizzazione", "true");

			setXverbForViewer();
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isUltimaPaginaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		int count = defaultForm.getParamAsInt("count");
		if (count > pos + 1)
			return true;
		else
			return false;
	}

	public int getCurrent() {
		return defaultForm.getParamAsInt("pos") + 1;
	}

	public int getTotal() {
		return defaultForm.getParamAsInt("count");
	}

	/**
	 * Ritorna true se gli indici sono gestiti tramite elasticsearch, false in caso di gestione tramite extraway
	 * @return
	 */
	public boolean isElasticsearchEnabled() {
		String elasticsearch = defaultForm.getParam("elasticsearch");
		if (elasticsearch != null && elasticsearch.toLowerCase().equals("true"))
			return true;
		else
			return false;
	}

	private void setXverbForViewer() {
		String xverb = defaultForm.getParam("xverb");
		if (xverb.indexOf("child") == -1)
			defaultForm.addParam("xverb", "");
	}

	public void lookup(String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) {
		_lookup("lookup_response", aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
	}

	public void rifintLookup(String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) {
		_lookup("rifintlookup_response", aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
	}

	private void _lookup(String verbo, String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) {
		if (db != null && db.equals("acl") && this.defaultForm.getParam("aclDb").length() > 0) {
				db = this.defaultForm.getParam("aclDb");
		}

		this.indexForm.addParam("verbo", verbo);
		this.indexForm.addParam("selid", "");
		this.indexForm.addParam("qord", ord);
		this.indexForm.addParam("lookup_titolo", titolo);
		this.indexForm.addParam("lookup_campi", campi);
		this.indexForm.addParam("lookup_xq", xq == null? "": xq);
		this.indexForm.addParam("lookup_new", newRecord == null? "" : newRecord);
		this.indexForm.addParam("lookup_db", db == null? "": db);
		this.indexForm.addParam("lookup_fieldVal", value);
		this.indexForm.addParam("lookup_alias", aliasName);

		if (aliasName1 != null && aliasName1.length() > 0)
			this.indexForm.addParam("lookup_alias", aliasName + "|" + aliasName1);

		//FIXME
		/*
		if (typeof(customPreLookup) != 'undefined')	{
	        //r = customPreLookup(iForm, inputName, val); Fabio 10/09/2004
	        r = customPreLookup(sourceForm, inputName, val);
	        if ( r == true ) { //Fabio 10/09/2004
	            requestForm.selid.value           =sourceForm.selid.value          ;
	            requestForm.qord.value            =sourceForm.qord.value           ;
	            requestForm.lookup_titolo.value   =sourceForm.lookup_titolo.value  ;
	            requestForm.lookup_campi.value    =sourceForm.lookup_campi.value   ;
	            requestForm.lookup_xq.value       =sourceForm.lookup_xq.value      ;
	            requestForm.lookup_new.value      =sourceForm.lookup_new.value     ;
	            requestForm.lookup_db.value       =sourceForm.lookup_db.value      ;
	            requestForm.lookup_fieldVal.value =sourceForm.lookup_fieldVal.value;
	            requestForm.lookup_alias.value    =sourceForm.lookup_alias.value   ;
	        }
	    }
		*/

	    // introdotta possibilità di invocare una funzione custom prima del lookup senza
	    // entrare in conflitto con il meccanismo di 'customPreLookup' (per evitare i dannosi assegnamenti
	    // requestForm.* = sourceForm.*) [M 0000253]
		/*
	    if (typeof(customBeforeLookup) != 'undefined') {
	        r = customBeforeLookup(iForm, inputName, val);
	    }
	    */
	}

	public void thVincolato(String numTitles4Page, String fieldName, String radice, String relazione, String chiave, String insRight, String delRight, String windowTitle, boolean iscascade, String value) {

		//FIXME
		/*
	    if(field.indexOf(parsedInstanceKey)==0)
	    {field=field.substring(parsedInstanceKey.length);
	    }
	    */

	    String thesTitle = "Thesauro Vincolato";
	    if (windowTitle != null && windowTitle.length() > 0)
	    	thesTitle = windowTitle;
	    this.indexForm.addParam("view", thesTitle);

	    boolean useChkBox = false;

	    //FIXME
	    /*
	        if (typeof(customUseChkBoxMultistDefault) != 'undefined') {
	            useChkBox = customUseChkBoxMultistDefault(field);
	        }
	    }
	    */

	    if (numTitles4Page.equals("DEF"))
	        numTitles4Page = "15";

	    //FIXME
	    /*
	    if(iscascade) {
	        //alert (radice.substring(0,9));
	        var tempradix=radice;
	        radice=getistantThRadix(radice,instNumber,fatherInstNumber);
	        //alert(radice);
	        if(radice=="") {
	            alert('Selezionare un valore per '+tempradix);
	            return;
	        }
	    }
	     */

	    this.indexForm.addParam("lookup_campi", radice + "|" + relazione + "|" + chiave + "|" + fieldName);
	    this.indexForm.addParam("lookup_fieldVal", value);

	    if (insRight != null) {
	    	this.indexForm.addParam("lookup_new", insRight);
	        if (delRight != null)
	        	this.indexForm.addParam("lookup_new", insRight + "|" + delRight);
	    }
	    else {
	    	this.indexForm.addParam("lookup_new", "");
	    }

	    this.indexForm.addParam("verbo", "thesauro_response");
	    this.indexForm.addParam("xverb", "");
	    this.indexForm.addParam("pageCount", numTitles4Page);
	    this.indexForm.addParam("keypath", chiave);

	    if (useChkBox)
	    	this.indexForm.addParam("thVOptions", "useChkBox");
	    else
	    	this.indexForm.addParam("thVOptions", "");

	}

	public void showthesRel(String rel, String keypath, String startkey, String db, boolean forceradix, String toDo, String value) {
		/*if ( typeof(field) != 'undefined' && field.length > 0 ) {
		    if ( field.indexOf('[$hxp_instance()]') != -1 ) {
		        thesDad = inputNode.parentNode;

		        var childName = thesDad.childNodes.item(0).name;
		        if ( typeof(childName) == 'undefined' )	//bug fix temporaneo per mozilla
		            childName = thesDad.childNodes.item(1).name;

		        var instNumber = childName.substring(childName.lastIndexOf('[') + 1, childName.lastIndexOf(']'));
		        field = field.replace('$hxp_instance()', instNumber);

		        //caso di istanza figlia di multistanza
		        if ( typeof(field.indexOf('$hxp_fatherinstance()')) != 'undefined' ) {
		            var fatherInstNumber = childName.substring(childName.indexOf('[') + 1, childName.indexOf(']'));
		            field = field.replace('$hxp_fatherinstance()', fatherInstNumber);
		        }

		        for ( var i = 0; i < thesDad.childNodes.length; i++ ) {
		            if ( thesDad.childNodes.item(i).name == field)
		                val = thesDad.childNodes.item(i).value;
		        }
		    }
		    else { //no multistanza
		        thesDad = null;
		        val = getForm("hxpForm")[field].value;
		    }
		    thesField = field;
		}*/

		this.indexForm.addParam("verbo", "showthes");
		this.indexForm.addParam("xverb", "@" + rel);

		this.indexForm.addParam("db", db); // dovrebbe arrivare gia' il db di xdocwayproc

		this.indexForm.addParam("keypath", keypath); // Canale del thes

		if (value != null && value.length() > 0 && !forceradix)
			this.indexForm.addParam("startkey", value);
		else
			this.indexForm.addParam("startkey", startkey); // Primo termine della relazione

		this.indexForm.addParam("toDo", toDo);

		this.indexForm.addParam("relation", rel);

	}

	/**
	 * Ritorna la stringa relativa all'attributo della response fromLookup.
	 *
	 * @return Valore impostato sull'attributo fromLookup
	 */
	public String getFromLookup() {
		return defaultForm.getParam("fromLookup");
	}

	/**
	 * Ritorna la stringa relativa all'attributo della response lookup_new.
	 *
	 * @return Valore impostato sull'attributo lookup_new
	 */
	public String getLookupNew() {
		return defaultForm.getParam("lookup_new");
	}
	public void modifyTableDoc() {
		Element repertorioEl = (Element) this.lastResponse.selectSingleNode("//repertorio");

		String codiceRepertorio = null;
		String descrizioneRepertorio = null;

		if (repertorioEl != null) {
			codiceRepertorio = repertorioEl.attributeValue("cod", null);
			descrizioneRepertorio = repertorioEl.getText();
		}

		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@modify");

		if (codiceRepertorio != null)
			this.defaultForm.addParam("codice_rep", codiceRepertorio);

		if (descrizioneRepertorio != null)
			this.defaultForm.addParam("descrizione_rep", descrizioneRepertorio);

		/* TODO
		 * if ( typeof(customModifyTableDoc) != 'undefined' ) {
		 *	customModifyTableDoc();
		 *	}
		 */
	}

	public void ripetiNuovo(String dbTable) {
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "@ripetinuovo");
		this.defaultForm.addParam("pos", "0");
		this.defaultForm.addParam("selid", "");
		if (dbTable != null && dbTable.length() > 0)
			this.defaultForm.addParam("dbTable", "@" + dbTable);
	}

	public void insChild(String physDoc, String cod_amm, String cod_aoo){
		this.insRel(physDoc, "insChild", cod_amm, cod_aoo);
	}

	public void insRel(String physDoc, String tipo, String cod_amm, String cod_aoo){

		this.defaultForm.addParam("verbo", "docEdit");

		String xverbVal="";
		if( null != cod_amm && !"".equals(cod_amm.trim())) xverbVal = "@" + cod_amm + "|" + cod_aoo;
		this.defaultForm.addParam("xverb", xverbVal);

		this.defaultForm.addParam("physDoc", "0");
		this.defaultForm.addParam("pos", "0");
		this.defaultForm.addParam("selid", "");
		this.defaultForm.addParam("physDocOrigine", physDoc);
		this.defaultForm.addParam("relType", tipo);
	}

	public void insFratPrima(String physDoc, String cod_amm, String cod_aoo)
	{
		this.insRel(physDoc, "insFratPrima", cod_amm, cod_aoo);
	}

	public void insFratDopo(String physDoc, String cod_amm, String cod_aoo)
	{
		this.insRel(physDoc, "insFratDopo", cod_amm, cod_aoo);
	}

	public void relSetRoot(){
		this.defaultForm.addParam("xverb", "_SET_ROOT_");
	}

	public void copiaNodo(String physDoc){
//this.defaultForm.addParam("nodoCopiato", physDoc);
		if ( this.defaultForm.getParam("selid").length() == 0 ) //occorre aprire in base a # fisico
			this.defaultForm.addParam("pos",  String.valueOf((this.defaultForm.getParamAsInt("physDoc") - 1)));
	}

	public void estraiDaGerarchia(String physDoc){
		this.defaultForm.addParam("physDocOrigine", physDoc);	//indica nodo destinazione a dispetto del nome
		this.defaultForm.addParam("relType", "estraiDaGerarchia");

		if ( this.defaultForm.getParam("selid").length() == 0 ) //occorre aprire in base a # fisico
			this.defaultForm.addParam("pos",  String.valueOf((this.defaultForm.getParamAsInt("physDoc") - 1)));
	}

	public void incollaComeFiglio(String physDoc){
		if ( this.defaultForm.getParam("nodoCopiato").equals(physDoc)) {
			//alert("Non è possibile inserire un documento come figlio di se stesso");
			return;
		}

		this.defaultForm.addParam("physDocOrigine", physDoc);	//indica nodo destinazione a dispetto del nome
		this.defaultForm.addParam("relType", "incollaComeFiglio");

		if ( this.defaultForm.getParam("selid").length() == 0 ) //occorre aprire in base a # fisico
			this.defaultForm.addParam("pos", String.valueOf(this.defaultForm.getParamAsInt("physDoc") - 1));
	}

	public void seekDoc(int docNumber, boolean phys){
		//setGlobalFormRestore('verbo','pos','selid','count');
		this.defaultForm.addParam("verbo", "showdoc");
		this.defaultForm.addParam("pos", docNumber);
		if ( phys ) {
			this.defaultForm.addParam("selid", "");
			this.defaultForm.addParam("count", 1);
	  }
	 }

	/**
	 * cancellazione di un documento
	 */
	public void remove() {
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("xverb", "@remove");
	}

	/**
	 * ripristino di un documento dal cestino (cancellazione logica)
	 */
	public void ripristinaDaCestino() {
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("xverb", "@ripristinaDaCestino");
	}

	public void goToChangePwd(String login)
	{
	    //setGlobalFormRestore('verbo','xverb','pwdLogin');
		defaultForm.addParam("verbo", "query");
		defaultForm.addParam("xverb", "@changePwd");
		defaultForm.addParam("pwdLogin", login);
	}

	/**
	 * Confronto della sezione diritti fra due persone interne selezionate
	 */
	public void controntaPersoneInterne(String matricola1, String matricola2) {
		defaultForm.addParam("verbo", "comparedocs");
		defaultForm.addParam("xverb", "@rightsPersintPersint");
		if (matricola1 == null)
			matricola1 = "";
		if (matricola2 == null)
			matricola2 = "";
		defaultForm.addParam("codici", matricola1 + "|" + matricola2);
	}

	/**
	 * Confronto della sezione diritti fra una persona interna e il proprio profilo di appartenenza
	 */
	public void controntaProfiloPersonaInterna(String codProfilo, String matricola) {
		defaultForm.addParam("verbo", "comparedocs");
		defaultForm.addParam("xverb", "@rightsProfiloPersint");
		if (codProfilo == null)
			codProfilo = "";
		if (matricola == null)
			matricola = "";
		defaultForm.addParam("codici", codProfilo + "|" + matricola);
	}


	/**
	 *  Richiesta di presa in carico per il documento corrente
	 */
	public void richiediPresaInCarico(String nrecord) {
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("xverb", "@requestTakeCharge");
		defaultForm.addParam("nrecordDoc", nrecord);
	}

	/**
	 *  Presa in carico per il documento corrente
	 */
	public void presaInCarico(String nrecord) {
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("xverb", "@takeCharge");
		defaultForm.addParam("nrecordDoc", nrecord);
	}

	/**
	 * Avvio di una specifica azione massiva da showdoc tramite stored procedure LUA
	 * @param azione
	 * @param dateFieldFormat
	 */
	public void startAzioneMassiva(Azione azione, String dateFieldFormat) throws Exception {
		defaultForm.addParam("verbo", "azionimassive_response");
		defaultForm.addParam("xverb", "@startAzione");
		defaultForm.addParam("stored_azione", azione.getStored());
		defaultForm.addParam("params_azione", azione.getXmlParametriInputForStoredProcedureInvocation(dateFieldFormat));
	}
	
}
