package it.tredi.dw4.docway.doc.adapters;

import java.net.URLEncoder;
import java.util.List;

import it.tredi.dw4.docway.model.Oggetto;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.docway.adapters.DocWayDocumentFormsAdapter;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class DocDocWayDocumentFormsAdapter extends DocWayDocumentFormsAdapter {

	public DocDocWayDocumentFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);

		Element root = response.getRootElement();

		defaultForm.addParam("sizeMaxFile", root.attributeValue("sizeMaxFile", ""));
		defaultForm.addParam("sizeMaxImg", root.attributeValue("sizeMaxImg", ""));
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
			Logger.warn("DocDocWayDocumentFormsAdapter.getSizeMaxFile(): unable to parse sizeMaxFile -> " + sizeMaxFile);
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
			Logger.warn("DocDocWayDocumentFormsAdapter.getSizeMaxImg(): unable to parse sizeMaxImg -> " + sizeMaxImg);
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
	 * inserimento di fascicoli custom
	 * @param tableName
	 * @param codFasc
	 * @param descrFasc
	 */
	public void insTableFascicoloCustom(String tableName, String codFasc, String descrFasc) {
		insTableDoc(tableName);
		if (codFasc != null && !codFasc.isEmpty())
			this.defaultForm.addParam("codice_fasc", codFasc);
		if (descrFasc != null && !descrFasc.isEmpty())
			this.defaultForm.addParam("descrizione_fasc", descrFasc);
	}

	/**
	 * Caricamento della pagina di inserimento di raccoglitore di tipo indice (raccoglitore custom)
	 * @param cod
	 * @param descr
	 */
	public void insTableDocRaccoglitoreIndice(String cod, String descr) {
		if (cod != null && !cod.isEmpty())
			defaultForm.addParam("codice_racc", cod);
		if (descr != null && !descr.isEmpty())
			defaultForm.addParam("descrizione_racc", descr);
		insTableDoc(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE);
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
	 * Inserimento di un nuovo repertorio con codice FPN/FPNALGERIA (Condotte)
	 */
	public void insTableDocRepFPN(String tableName, String codRep, String descrRep) {
		insTableDocRep(tableName, codRep,descrRep);
		this.defaultForm.addParam("verbo", "apps.xdocway.condotte.DocEditFPN");
	}

	/**
	 * Recupero delle versioni di un file del documento
	 */
	public void fileVersions(String id) {
		defaultForm.addParam("attach", id);
		defaultForm.addParam("verbo", "attach_response");
		defaultForm.addParam("xverb", "@fileVersions");
	}

	/**
	 * Generazione delle differenze fra l'ultima e la prima versione di un allegato
	 */
	public void showDiffBetweenVersions(String id, String expType) {
		defaultForm.addParam("verbo", "attach");
		defaultForm.addParam("xverb", "@showDiffFirst&LastVer#" + defaultForm.getParam("physDoc") + "#" + expType);
		defaultForm.addParam("id", id);
	}

	/**
	 * Abbandono del lock di un allegato del documento
	 */
	public void abandonChkout(String fileId) {
		defaultForm.addParam("attach", fileId);
		defaultForm.addParam("verbo", "attach_response");
		defaultForm.addParam("xverb", "@abandonChkout");
	}

	/**
	 * Checkout di un allegato
	 */
	public void checkout(String id, String title, String conversioni) {
		defaultForm.addParam("attach", id);
		defaultForm.addParam("name", title);
		defaultForm.addParam("verbo", "attach_response");
		defaultForm.addParam("xverb", "@chkout");

		//modifica di mbussetti del 21/03/2005: viene gestito un terzo argomento per la funzione, che contiene la
	    //lista delle conversioni richieste in precedenza per il file che viene prenotato
		if (conversioni != null && !conversioni.equals(""))
			defaultForm.addParam("convReq", conversioni);
	}

	/**
	 * Checkin di un allegato
	 */
	public void checkin(String id, String title, String tipo) {
		defaultForm.addParam("attach", id);
		defaultForm.addParam("name", title);
		defaultForm.addParam("verbo", "attach_response");
		String xverb = "@chkin";
		if (defaultForm.getParam("toDo").equals("@openChkinPage") || (tipo != null && tipo.equals("load")))
			xverb += "@load";
		defaultForm.addParam("xverb", xverb);

		if (defaultForm.getParam("selid").equals("")) {
			String physDoc = defaultForm.getParam("physDoc");
			if (physDoc != null && !physDoc.equals(""))
				defaultForm.addParam("pos", new Integer(physDoc).intValue() - 1);
		}
	}

	/**
	 * Chiamata a firma digitale Uniserv
	 */
	public void firmaUniserv(boolean remotaOtp) {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@firmaUniserv");
		defaultForm.addParam("remotaOtp", remotaOtp);
		
		if (defaultForm.getParam("selid").equals("")) {
			String physDoc = defaultForm.getParam("physDoc");
			if (physDoc != null && !physDoc.equals(""))
				defaultForm.addParam("pos", new Integer(physDoc).intValue() - 1);
		}
	}
	
	/**
	 * Chiamata a firma digitale Uniserv
	 */
	public void firmaUniserv(boolean remotaOtp, String fileId, String fileName, String outputFileType) {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@firmaUniserv");
		defaultForm.addParam("remotaOtp", remotaOtp);
		
		defaultForm.addParam("fileId", fileId);
		defaultForm.addParam("fileName", fileName);
		defaultForm.addParam("outputFileType", outputFileType);
		
		if (defaultForm.getParam("selid").equals("")) {
			String physDoc = defaultForm.getParam("physDoc");
			if (physDoc != null && !physDoc.equals(""))
				defaultForm.addParam("pos", new Integer(physDoc).intValue() - 1);
		}
	}
	
	/**
	 * Chiamata a firma applet Actalis
	 */
	public void firmaAppletActalis(String filename) {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@firmaAppletActalis");
		defaultForm.addParam("fileid", filename);

		if (defaultForm.getParam("selid").equals("")) {
			String physDoc = defaultForm.getParam("physDoc");
			if (physDoc != null && !physDoc.equals(""))
				defaultForm.addParam("pos", new Integer(physDoc).intValue() - 1);
		}
	}

	/**
	 * Chiamata a firma applet 3DI
	 */
	public void firmaApplet3di(String filename) {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@firmaApplet3di");
		defaultForm.addParam("fileid", filename);

		if (defaultForm.getParam("selid").equals("")) {
			String physDoc = defaultForm.getParam("physDoc");
			if (physDoc != null && !physDoc.equals(""))
				defaultForm.addParam("pos", new Integer(physDoc).intValue() - 1);
		}
	}

	/**
	 * Chiamata a firma digitale Uniserv
	 */
	public void firmaEngineering() {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@firmaEng");

		if (defaultForm.getParam("selid").equals("")) {
			String physDoc = defaultForm.getParam("physDoc");
			if (physDoc != null && !physDoc.equals(""))
				defaultForm.addParam("pos", new Integer(physDoc).intValue() - 1);
		}
	}

	/**
	 * Invio telematico multiplo agli indirizzi PEC dei destinatari di un doc in partenza
	 *
	 * @param emailFrom Eventuale indirizzo email da utilizzare per l'invio (piu' indirizzi email associati ad un unico ufficio)
	 */
	public void invioTelematicoPECMultiplo(String emailFrom) {
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@invioTelematicoPECMultiplo");
		defaultForm.addParam("emailFromInvioTelematico", emailFrom);
	}

	/**
	 * Invio telematico di un doc ad un rif esterno (doc in partenza)
	 *
	 * @param pos Posizione del rif esterno all'interno della lista di rif esterni del documento
	 * @param emailFrom Eventuale indirizzo email da utilizzare per l'invio (piu' indirizzi email associati ad un unico ufficio)
	 */
	public void invioTelematico(int pos, String emailFrom) {
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@invioTelematico");
		defaultForm.addParam("postitPos", pos); //utilizzo postitPos perchè disponibile per questa operazione
		if (emailFrom != null)
			defaultForm.addParam("emailFromInvioTelematico", emailFrom);
	}

	/**
	 * Invio della notifica di esito committente per una fatturaPA passiva ricevuta (specifiche
	 * di fatturazione elettronica per le PA)
	 *
	 * @param dettaglioEsito
	 * @param motivazione
	 */
	public void sendEsitoCommittente(String dettaglioEsito, String motivazione) {
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@invioEsitoCommittenteFatturePA");
		defaultForm.addParam("ftrpaEsito", dettaglioEsito);
		if (motivazione != null)
			defaultForm.addParam("motivazione", motivazione);
	}

	/**
	 * Generazione della fattura XML in base ai dati registrati sul documento per la fattura
	 * attiva (specifiche di fatturazione elettronica per le PA)
	 */
	public void generaXmlFatturaPA(String filenameFattura) {
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@generaXmlFatturaPA");
		defaultForm.addParam("filenameFtrPAA", filenameFattura);
	}

	/**
	 * Invio di una fatturaPA attiva in base al documento XML di definizione
	 * della fattura caricato o generato
	 *
	 * @param fileNameFatturaPA nome del file relativo alla fatturaPA da inviare
	 */
	public void inviaFatturaPAattiva(String fileNameFatturaPA) {
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@inviaFatturaPA");
		defaultForm.addParam("fileNameFatturaPA", fileNameFatturaPA);
	}

	/**
	 * Apertura in popup delle immagini del documento
	 */
	public void mostraImmaginiDocumento() {
		defaultForm.addParam("xverb", "@immagini");
		defaultForm.addParam("dbTable", "@immagini");
		defaultForm.addParam("personalView", "");

		if (defaultForm.getParam("selid").equals("")) {
			String physDoc = defaultForm.getParam("physDoc");
			if (physDoc != null && !physDoc.equals(""))
				defaultForm.addParam("pos", new Integer(physDoc).intValue() - 1);
		}
	}


    /**
     * Anteprima files di un documento
	 * inviato in notifica differita
	 *
     * @param pos indice del titolo del documento da caricare
     */
    public void showDocNotifDiff(String pos) {
    	defaultForm.addParam("verbo", "resoconto_doc_assegnati");
	    defaultForm.addParam("xverb", "@showDoc");
	    defaultForm.addParam("pos", pos);
    }

    /**
     * Caricamento della showdoc di un documento
     * da notitiche differite
     *
     * @param pos indice del titolo del documento da caricare
     */
    public void goToShowDocFromNotifDiff(String pos) {
    	defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("pos", pos);
    }

    /**
     * Rimanda alla pagina per l'invio via email degli allegati di un doc. notificato
	 * in modo differito
	 *
     * @param pos indice del titolo del documento da caricare
     */
    public void showEMailForm(String pos) {
    	defaultForm.addParam("verbo", "resoconto_doc_assegnati");
    	defaultForm.addParam("xverb", "showEMailForm");
    	defaultForm.addParam("pos", pos);
    }

    /**
     * Annullamento di un workflow di Bonita
     * @param bwfInstanceId identificativo dell'istanza di workflow
     */
    public void cancelWorkflowInstance(String bwfInstanceId, String bonitaVersion) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@cancelWorkflow");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("bwfInstanceId", bwfInstanceId);
		defaultForm.addParam("bonitaVersion", bonitaVersion);
    }

    /**
     * Caricamento delle variabili associate ad un task di una
     * istanza di workflow di bonita
     *
     * @param bwfInstanceId identificativo dell'istanza di workflow
     * @param bwfTaskId identificativo del task
     */
    public void showWfTaskVars(String bwfInstanceId, String bwfTaskId) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@showTaskVars");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("bwfInstanceId", bwfInstanceId);
		defaultForm.addParam("bwfTaskId", bwfTaskId);
    }

    /**
     * Aggiornamento dell'indirizzo PEC di un destinatario di un documento in partenza
     *
     * @param cod
     * @param ref_cod
     */
    public void updatePEC(String cod, String ref_cod) {
    	defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@updatePEC|" + cod + "|" + ref_cod);
    }

    /**
     * Condivisione dei files del documento su directory remota (percorso di rete specificato
	 * su file di properties di docway-service)
     */
    public void condividiFilesSuDirRemota() {
    	defaultForm.addParam("verbo", "attach_response");
    	defaultForm.addParam("xverb", "@condividiFiles");
    }

    /**
     * Assegnazione di un workflow ad un documento
     *
     * @param bwfId
     * @param bwfVersion
     */
    public void doAssignWorkflow(String bwfId, String bwfVersion, String nrecord, String bonitaVersion) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@assignworkflow@" + nrecord);
		defaultForm.addParam("bwfId", bwfId);
		defaultForm.addParam("bwfVersion", bwfVersion);
		defaultForm.addParam("bonitaVersion", bonitaVersion);
    }

    /**
     * caricamento della pagina di ricerca
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
			int index = tableName.indexOf("#personalView=");
			if (index != -1) {
				defaultForm.addParam("dbTable", "@" + tableName.substring(0, index));
				defaultForm.addParam("personalView", tableName.substring(index + 14));
			}

			defaultForm.addParam("verbo", "query");
		}
	}

    /**
     * invio del documento a Microsoft NAV (chiamata WS)
     * @param navType
     */
    public void sendToDynamicsNav(String navType) {
    	defaultForm.addParam("verbo", "docEdit");
    	defaultForm.addParam("xverb", "@sendToDynamicsNav@" + navType);
    }

    // dpranteda 18/03/2015 - pubblicazione su albo on-line esterno
    /**
     * rimuove il documento dalla pubblicazione in albo on-line esterno
     */
    public void rimuoviAlboExt(){
    	defaultForm.addParam("verbo", "documento_response");
    	defaultForm.addParam("xverb", "@rimuoviAlboExt");
    }

    // dpranteda 22/04/2015 - delega task del workflow
    /**
     * Aggiungo una persona tra gli attori del task
     * @param bwfInstanceId identificativo dell'istanza di workflow
     * @param taskId id del task da delegare
     * @param codPersona matricola della persona da aggiungere
     */
    public void delegaWorkflowTask(String taskId, String userId, String bonitaVersion) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@delegaWorkflowTask");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("taskId", taskId);
		defaultForm.addParam("userId", userId);
		defaultForm.addParam("bonitaVersion", bonitaVersion);
    }

    // dpranteda 23/04/2015 - prendo la lista di task in stato ready dell'istanza del flusso
    /**
     * restituisce la lista di task in stato ready dell'istanza del flusso
     * @param bwfInstanceId identificativo dell'istanza di workflow
     */
    public void getReadyTaskListAndUserListForDelega(String bwfInstanceId, String listOfCodUff, String listOfRuoli, String bonitaVersion) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@readyTaskListAndUserList");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("bwfInstanceId", bwfInstanceId);
		defaultForm.addParam("listOfCodUff", listOfCodUff);
		defaultForm.addParam("listOfRuoli", listOfRuoli);
		defaultForm.addParam("bonitaVersion", bonitaVersion);
    }

    public void getBonitaBpmPortalFormUrl(String taskId, String bonitaVersion){
    	defaultForm.addParam("verbo", "bonitaworkflow");
    	defaultForm.addParam("xverb", "@bonitaBpmPortalFormUrl");
    	defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("taskId", taskId);
		defaultForm.addParam("bonitaVersion", bonitaVersion);
    }

    public void releaseTask(String taskId, String bonitaVersion){
    	defaultForm.addParam("verbo", "bonitaworkflow");
    	defaultForm.addParam("xverb", "@rilasciaTask");
    	defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("taskId", taskId);
		defaultForm.addParam("bonitaVersion", bonitaVersion);
    }

    public void showWorkflowHistory(String bwfInstanceId, String bonitaVersion){
    	defaultForm.addParam("verbo", "bonitaworkflow");
    	defaultForm.addParam("xverb", "@showWorkflowHistory");
    	defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("bwfInstanceId", bwfInstanceId);
		defaultForm.addParam("bonitaVersion", bonitaVersion);
    }

    /**
     * apertura dell'interfaccia di creazione di un nuovo documento all'interno di un raccoglitore
     * di tipo indice
     * @param nrecordDocCorrente Nrecord del documento selezionato dall'operatore
     * @param after true se il nuovo documento deve essere posizionato dopo quello selezionato, false se deve essere posizionato prima
     * @param inside
     */
    public void openNewDoc(String nrecordDocCorrente, boolean after, boolean inside) {
    	this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@openNewDoc");
		if (inside)
			this.defaultForm.addParam("toDo", nrecordDocCorrente + "_inside");
		else if (after)
			this.defaultForm.addParam("toDo", nrecordDocCorrente + "_after");
		else
			this.defaultForm.addParam("toDo", nrecordDocCorrente + "_before");
    }

    /**
     * eliminazione di un documento da un raccoglitore di tipo indice
     * @param nrecordDoc nrecord del documento da rimuovere dal raccoglitore
     * @param deleteDoc true nel caso sia richiesta la cancellazione del documento, false in caso sia sufficiente rimuoverlo come contenuto del raccoglitore (il doc rimane comunque in archivio)
     */
    public void removeDocFromRaccIndice(String nrecordDoc, boolean deleteDoc) {
    	this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@removeDoc");
		this.defaultForm.addParam("nrecordDoc", nrecordDoc);
		if (deleteDoc)
			this.defaultForm.addParam("raccIndiceDeleteDoc", "true");
    }

    /**
     * avvio della procedura di aggregazione di tutti i documenti che compongono il raccoglitore di tipo indice
     * @param docs elenco di documenti selezionati (ad aggregare)
     * @param buildOutputDocument true se occorre generare un documento di output (con in allegato l'aggregato), false se occorre semplicemente scaricare il file aggregato
     */
    public void startAggregaDocs(String docs, boolean buildOutputDocument) {
    	this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@mergeDocs");
		this.defaultForm.addParam("docs", docs);
		this.defaultForm.addParam("buildOutputDocument", buildOutputDocument);
    }

	/**
	 * avvio della procedura di stampa ufficio ruolo (aggregazione dei doc selezionati, ma con indice completo)
	 * @param docs elenco di documenti selezionati (ad aggregare)
	 */
	public void startStampaUfficioRuolo(String docs) {
		this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@stampaUfficioRuolo");
		this.defaultForm.addParam("docs", docs);
	}

    /**
	 * Rifiuto di una bozza in arrivo da parte dell'operatore
	 */
    public void rifiutaBozzaArrivo() {
		rifiutaBozzaArrivo("");
	}
	public void rifiutaBozzaArrivo(String motivazione) {
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@rifiutaBozzaArrivo");
		defaultForm.addParam("motivazione", motivazione);
	}
	
	/**
	 * Ritiro di un bando del repertorio ALBO
	 */
	public void ritiraBando() {
		defaultForm.addParam("verbo", "fascicolo_response");
		defaultForm.addParam("xverb", "@ritiraBando");
	}

	/**
	 * Caricamento della copertina del raccoglitore di tipo indice, se e solo se copertina recuperata dal primo nodo del raccoglitore
	 */
	public void loadCopertina() {
		this.defaultForm.addParam("verbo", "attach_raccindice");
		this.defaultForm.addParam("xverb", "loadCopertina");
	}

	/**
	 * Scaricamento del PDF contenente l'indice del raccoglitore
	 */
	public void scaricaIndice() {
		this.defaultForm.addParam("verbo", "attach_raccindice");
		this.defaultForm.addParam("xverb", "scaricaIndice");
	}

	/**
	 * Avvio dalla procedura di importazione dei fascicoli contenuti nel file excel allegato al documento (funzione specifica
	 * sviluppata su DocWay3 per Equitalia)
	 * @param xlsFileId
	 * @param importClassName
	 * @param importMethodName
	 * @param loadingbarDbTable
	 */
	public void avviaImportXls(String xlsFileId, String importClassName, String importMethodName, String loadingbarDbTable) {
		this.defaultForm.addParam("verbo", "apps.generic.utils.excel_import.LaunchXlsImport");
		this.defaultForm.addParam("xverb", "@" + importClassName + "|" + importMethodName);

		if (loadingbarDbTable != null && !loadingbarDbTable.isEmpty()) {
			if (!loadingbarDbTable.startsWith("@"))
				loadingbarDbTable = "@" + loadingbarDbTable;
			this.defaultForm.addParam("dbTable", loadingbarDbTable);
		}

		String customTupleParam = this.defaultForm.getParam("_cd");
		customTupleParam = FormsAdapter.setParameterFromCustomTupleValue("xls_import_xwfile_id", xlsFileId, customTupleParam);

		// TODO non mi pare che vengano mai settati, utilizzo i valori di default
		/*
        if (loadingbarTitle != null && typeof(loadingbarTitle) != 'undefined' && loadingbarTitle.length > 0) {
            hxpS.setParameter("xls_import_loadingbar_title", loadingbarTitle);
        }

        if (loadingbarCustomDataOffset != null && typeof(loadingbarCustomDataOffset) != 'undefined' && loadingbarCustomDataOffset.length > 0) {
            hxpS.setParameter("xls_import_loadingbar_customDataOffset", loadingbarCustomDataOffset);
        }
        */

		this.defaultForm.addParam("_cd", customTupleParam);
	}

	/**
     * setta lo stato a "completato" ad un documento di un raccoglitore di tipo indice
     * @param nrecordDoc nrecord del documento da settare come completato per stampa
     */
    public void setReadyDocRaccIndice(String nrecordDoc) {
    	this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@setReadyDoc");
		this.defaultForm.addParam("nrecordDoc", nrecordDoc);
    }

    /**
     * setta lo stato a "completato" di tutti i documenti di un raccoglitore di tipo indice
     * @param nrecordRacc nrecord del raccoglitore sul quale settare i doc come completati per la stampa
     */
	public void setAllReadyDocsRaccIndice(String nrecordRacc) {
		this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@setAllReadyDocs");
		this.defaultForm.addParam("nrecordRacc", nrecordRacc);
	}

	/**
	 * prepara il formAdapter per la richiesta necessaria a settare lo stato a "completato" dei documenti selezionati
	 * di un raccoglitore di tipo indice
	 * @param nrecordRacc nrecord del raccoglitore sul quale settare i doc come completati per la stampa
	 * @param selectedDocs lista di documenti ai quali settare lo stato
	 */
	public void setSelectedReadyDocsRaccIndice(String nrecordRacc, List<Oggetto> selectedDocs) {
		this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@setSelectedReadyDocs");
		this.defaultForm.addParam("nrecordRacc", nrecordRacc);
		StringBuilder selectedNrecords = stringifyNrecords(selectedDocs);
		this.defaultForm.addParam("selectedNrecord", selectedNrecords.toString());

	}

	/**
	 * prepara il formAdapter per la richiesta di eliminazione di tutti i documenti contenuti nel raccoglitore
	 * @param nrecordRacc nrecord del raccoglitore
	 */
	public void startRemoveDocsFromRacc(String nrecordRacc) {
		this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@deleteAllDocs");
		this.defaultForm.addParam("nrecordRacc", nrecordRacc);
	}

	/**
	 * prepara il formAdapter per la richiesta di ripristino del raccoglitore di tipo INDICE e di tutti i documenti
	 * al suo interno (rimozione flag "readOnly"
	 * @param nrecordRacc nrecord del raccoglitore
	 */
	public void ripristinaRicerca(String nrecordRacc) {
		this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@ripristinaReadOnlyDocs");
		this.defaultForm.addParam("nrecordRacc", nrecordRacc);
	}

	/**
	 * prepara il formAdapter per la richiesta di elminazione dei documenti selezionati nel raccoglitore
	 * @param nrecordRacc nrecord raccoglitore
	 * @param selectedDocs Oggetti del raccoglitore selezionati
	 */
	public void deleteSelectedDocsRaccIndice(String nrecordRacc, List<Oggetto> selectedDocs) {
		this.defaultForm.addParam("verbo", "raccindice_response");
		this.defaultForm.addParam("xverb", "@deleteSelectedDocs");
		this.defaultForm.addParam("nrecordRacc", nrecordRacc);
		StringBuilder selectedNrecords = stringifyNrecords(selectedDocs);
		this.defaultForm.addParam("selectedNrecord", selectedNrecords.toString());
	}

	/**
	 * Serializza in una stringa gli nrecord degli Oggetto passati come argomento
	 * @param selectedDocs Oggetto degli elementi selezionati
	 * @return
	 */
	private StringBuilder stringifyNrecords(List<Oggetto> selectedDocs) {
		StringBuilder selectedNrecords = new StringBuilder();
		for (Oggetto doc : selectedDocs) {
			if (doc != null)
				selectedNrecords.append(doc.getCod()).append("|");
		}
		return selectedNrecords;
	}
}
