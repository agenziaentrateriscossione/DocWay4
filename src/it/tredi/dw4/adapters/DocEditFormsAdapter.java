package it.tredi.dw4.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.utils.Logger;

import java.net.URLEncoder;
import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class DocEditFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	protected FormAdapter indexForm;
	
	public DocEditFormsAdapter(AdapterConfig config) {
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
		
		defaultForm.addParam("verbo", root.attributeValue("verbo", ""));
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		defaultForm.addParam("nextVerb", "");
		defaultForm.addParam("nextXVerb", "");
		defaultForm.addParam("pne", root.attributeValue("pne", ""));
		defaultForm.addParam("pnce", root.attributeValue("pnce", ""));
		defaultForm.addParam("lock", root.attributeValue("lock", ""));
		defaultForm.addParam("xDoc", "");
		defaultForm.addParam("asText", root.attributeValue("asText", ""));
		defaultForm.addParam("destPage", root.attributeValue("destPage", ""));
		defaultForm.addParam("lookup_titolo", "");
		defaultForm.addParam("lookup_campi","");
		defaultForm.addParam("lookup_xq", "");
		defaultForm.addParam("lookup_new", root.attributeValue("lookup_new", ""));
		defaultForm.addParam("lookup_db", "");
		defaultForm.addParam("lookup_fieldVal", "");
		defaultForm.addParam("fromLookup", root.attributeValue("fromLookup", ""));
		defaultForm.addParam("lookup_alias","");
		defaultForm.addParam("qord", "");
		defaultForm.addParam("physDoc", root.attributeValue("physDoc", ""));
		
		/* per la creazione di sottofascicoli */
		defaultForm.addParam("fasc_numero_sottofasc", root.attributeValue("fasc_numero_sottofasc", ""));
		defaultForm.addParam("physDoc_sottofasc", root.attributeValue("physDoc_sottofasc", ""));
		defaultForm.addParam("nome_persona_sottofasc", root.attributeValue("nome_persona_sottofasc", ""));
		defaultForm.addParam("nome_uff_sottofasc", root.attributeValue("nome_uff_sottofasc", ""));
		defaultForm.addParam("cod_persona_sottofasc", root.attributeValue("cod_persona_sottofasc", ""));
		defaultForm.addParam("cod_uff_sottofasc", root.attributeValue("cod_uff_sottofasc", ""));
		
		/* introdotto tipo del valore contenuto nei campi ufficio [RW 0043770] */
		defaultForm.addParam("tipo_uff_sottofasc", root.attributeValue("tipo_uff_sottofasc", ""));
		
		defaultForm.addParam("classif_sottofasc", root.attributeValue("classif_sottofasc", ""));
		defaultForm.addParam("classif_cod_sottofasc", root.attributeValue("classif_cod_sottofasc", ""));
		
		/* per iw */
		defaultForm.addParam("fileList", root.attributeValue("fileList", ""));
		defaultForm.addParam("extraView","");
		defaultForm.addParam("selectedIndex", root.attributeValue("selectedIndex", ""));
		
		/* per lookup su thesauro */
		defaultForm.addParam("keypath", "");
		defaultForm.addParam("startkey", "");
		defaultForm.addParam("relation", "");
		
		/* per inserimento/modifica di repertori */
		defaultForm.addParam("codice_rep", root.attributeValue("codice_rep", ""));
		defaultForm.addParam("descrizione_rep", root.attributeValue("descrizione_rep", ""));
		
		/* per fix di modifica di documenti con aggiunta di immagini tramite iw */
		defaultForm.addParam("countImmagini", root.selectNodes("//immagini/*[name()='xw:file']").size());
		
		/* per rif_interni */
		defaultForm.addParam("countRifs", root.selectNodes("//rif_interni/rif").size() + root.selectNodes("//compilazione_automatica/rif").size());
		
		defaultForm.addParam("personalView", root.attributeValue("personalViewToUse", ""));
		
		defaultForm.addParam("sizeMaxFile", root.attributeValue("sizeMaxFile", ""));
		defaultForm.addParam("sizeMaxImg", root.attributeValue("sizeMaxImg", ""));
		defaultForm.addParam("thumbMaxDim", root.attributeValue("thumbMaxDim", ""));
		defaultForm.addParam("filesWithSize", "");
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

	@SuppressWarnings("unchecked")
	public void saveDocument(String pne, String pnce) {
		String codiceRepertorio = null;
		String descrizioneRepertorio = null;
		
		if (defaultForm.getParam("codice_rep") != null && defaultForm.getParam("codice_rep").length() > 0) {
			codiceRepertorio = defaultForm.getParam("codice_rep");
			descrizioneRepertorio = defaultForm.getParam("descrizione_rep");
		}
		
		/* TODO
		 * if (typeof(customGetHtmlNodes) != 'undefined') {
		 * ....
		 */
		
		boolean canSave = true;
		
		/* TODO
		 * if ((typeof(ceremonyEnabled)=='undefined' || ceremonyEnabled==true) && senselectEnabled() && hasSignData()) {
		 * ...
		 */
		
		/* TODO
		 * if ( typeof(document.IW) != 'undefined' ) {
		 * ...
		 */
		
		if (null != this.lastResponse ){
		Iterator<Element> rightsIt = this.lastResponse.selectNodes("//listof_rights//right[@type='num']").iterator();
			while (rightsIt.hasNext()) {
				Element right = rightsIt.next();
				String code = right.attributeValue("cod", "");
				if (!code.equals("")) {
					//TODO
				}
			}
		}
		
		if (canSave) {
			this.defaultForm.addParam("revSel", "1");
			this.defaultForm.addParam("nextVerb", "showdoc");
			this.defaultForm.addParam("nextXVerb", "@child");
			this.defaultForm.addParam("xverb", "@save");
			this.defaultForm.addParam("pne", pne);
			this.defaultForm.addParam("pnce", pnce);
			
			if (codiceRepertorio != null)
				this.defaultForm.addParam("codice_rep", codiceRepertorio);
			if (descrizioneRepertorio != null)
				this.defaultForm.addParam("descrizione_rep", descrizioneRepertorio);
		}
	}
	
	public void clearDocument() {
	    if (!insertMode()) { //pagina di modifica
	        unlockAndGoTo("showdoc", true, false);
	    }
	    else { //inserimento
	    	
	    	//gestire i 2 parametri che venivano passati dall'xsl: codRep,descrRep
	    	
	    	/*
				<xsl:if test="$hxpdata/@codice_rep!=''">
					<xsl:value-of select="concat('&#34;',$hxpdata/@codice_rep,'&#34;')" />
					<xsl:text>,</xsl:text>
					<xsl:value-of select="concat('&#34;',$hxpdata/@descrizione_rep,'&#34;')"/>
				</xsl:if>
	    	 */
	    	
//TODO - completare	    	
	    }
	}
	
	protected boolean insertMode() {
		String selid = this.defaultForm.getParam("selid");
		String pos = this.defaultForm.getParam("pos");
		String physDoc = this.defaultForm.getParam("physDoc");		
		return (selid.length() == 0 && pos.equals("0") && physDoc.length() == 0) || (physDoc.equals("0"));
	}
	
	//usePersonalView era facoltativo (se omesso deve essere impostato a false)
	public void unlockAndGoTo(String verbo, boolean useTable, boolean usePersonalView) {
	    // FIXME
	    //if ( typeof(customConfirmUnlockAndGoTo) != 'undefined' && customConfirmUnlockAndGoTo() == false )
	    //    return false;

	    if (!useTable) {
	    	this.defaultForm.addParam("dbTable", "");
	    }
	    else {
	    	//FIXME
	    	//if (typeof(customDbTableForUnlockAndGoTo)!='undefined')
	    		//getForm("hxpForm").dbTable.value=customDbTableForUnlockAndGoTo(getForm("hxpForm").dbTable.value);
	    }
	        
	    //le pagine di inserimento/modifica/visualizzazione sono diversificate
	    //per i modelli e le istanze (dbTable=@workflow_model/@workflow_instance) ma la pagina di ricerca è comune,
	    //quindi bisogna modificare il dbTable
	    String wDbTable = defaultForm.getParam("dbTable");
	    if (wDbTable.indexOf("@workflow") != -1 && insertMode()) {
	    	this.defaultForm.addParam("dbTable", "@workflow");
	    }

	    if (!usePersonalView )
	    	this.defaultForm.addParam("personalView", "");

	    this.defaultForm.addParam("destPage", verbo);
	    if (insertMode()){ //pagina di inserimento
	    	this.defaultForm.addParam("toDo", "");
	    	this.defaultForm.addParam("verbo", verbo);
	    }
	    else { //modifica
	        unlockDocument();
	    }
	}
	
	protected void unlockDocument() {
		if (!insertMode()) { //check se si è effettivamente in modifica
			this.defaultForm.addParam("xverb", "@unlock");
			this.defaultForm.addParam("revSel", "1"); //per viewer
			this.defaultForm.addParam("nextVerb", "showdoc"); //per viewer
			this.defaultForm.addParam("nextXVerb", "@child"); //per viewer
	    }
	}
	
	/* ACTIONS - INDEXFORM */
	
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
	
	public void showthesRel(String login, String matricola, String rel, String keypath, String startkey, String db, boolean forceradix, String toDo, String value) {
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
		
		// TODO modificato a causa del problema cache in login (verificare meglio, si tratta solo di un workaround)
		this.indexForm.addParam("login", login); 
		this.indexForm.addParam("matricola", matricola);
		
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
	
	/**
	 * Verifica dei duplicati di un documento (fase di inserimento di un doc)
	 * 
	 * @param query Query da utilizzare per la verifica
	 */
	public void verificaDuplicatiDoc(String query) {
		this.indexForm.addParam("view", "verificaDuplicati");
		this.indexForm.addParam("query", query);
		this.indexForm.addParam("verbo", "queryplain");
		this.indexForm.addParam("xverb", "");
		this.indexForm.addParam("selid", ""); // senza questo parametro non funziona la verifica di duplicati 
												// da una modifica (viene passato il selid della lista titoli risultante dalla ricerca)
	}
	
	/**
	 * Protocollazione di un documento in bozza da maschera di modifica (docEditModify)
	 * 
	 * @param bozzaDaProt
	 * @param codRepertorio
	 * @param descrRepertorio
	 */
	public void protocollaDoc(String bozzaDaProt, String codRepertorio, String descrRepertorio) {
		this.defaultForm.addParam("bozzaDaProt", bozzaDaProt);
		if (codRepertorio != null && !codRepertorio.equals(""))
			this.defaultForm.addParam("codice_rep", codRepertorio);
		if (descrRepertorio != null && !descrRepertorio.equals(""))
			this.defaultForm.addParam("descrizione_rep", descrRepertorio);
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
	 * Ritorna il parametro physDoc del formsAdapter (necessario per l'apertura inline di PDF da showdoc documento con attiva la segnatura) 
	 * @return
	 */
	public String getPhysDoc() {
		String physDoc = defaultForm.getParam("physDoc");
		if (physDoc == null)
			physDoc = "";
		
		return physDoc; 
	}
	
	/**
	 * Ritorna il parametro selid del formsAdapter (necessario per l'apertura inline di PDF da showdoc documento con attiva la segnatura) 
	 * @return
	 */
	public String getSelid() {
		String selid = defaultForm.getParam("selid");
		if (selid == null)
			selid = "";
		
		return selid; 
	}
	
	/**
	 * Ritorna il parametro pos del formsAdapter (necessario per l'apertura inline di PDF da showdoc documento con attiva la segnatura) 
	 * @return
	 */
	public String getPos() {
		String pos = defaultForm.getParam("pos");
		if (pos == null)
			pos = "";
		
		return pos; 
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'attach'.
	 * @return Valore impostato sull'attributo attach
	 */
	public String getAttach() {
		return defaultForm.getParam("attach");
	}
	
	/**
	 * Inserimento di un nuovo repertorio con codice FPN/FPNALGERIA (Condotte)
	 */
	public void insTableDocRepFPN(String tableName, String codRep, String descrRep) {
		insTableDocRep(tableName, codRep,descrRep);
		this.defaultForm.addParam("verbo", "apps.xdocway.condotte.DocEditFPN");
	}

	public void insTableFascicoloCustom(String tableName, String codFasc, String descrFasc) {
		this.defaultForm.addParam("verbo", "docEdit");
		this.defaultForm.addParam("xverb", "");
		this.defaultForm.addParam("physDoc", "0");
		this.defaultForm.addParam("pos", "0");
		this.defaultForm.addParam("selid", "");
		this.defaultForm.addParam("dbTable", "@" + tableName);
		this.defaultForm.addParam("codice_fasc", codFasc);
		this.defaultForm.addParam("descrizione_fasc", descrFasc);
	}
	
}
