package it.tredi.dw4.docway.doc.adapters;

import java.net.URLEncoder;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.docway.adapters.DocWayDocumentFormsAdapter;
import it.tredi.dw4.utils.Logger;

import org.dom4j.DocumentException;

public class DocDocWayDocumentFormsAdapter extends DocWayDocumentFormsAdapter {

	public DocDocWayDocumentFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
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
	public void firmaUniserv() {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@firmaUniserv");
		
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
	 */
	public void invioTelematicoPECMultiplo() {
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@invioTelematicoPECMultiplo");
	}
	
	/**
	 * Invio telematico di un doc ad un rif esterno (doc in partenza)
	 * 
	 * @param pos Posizione del rif esterno all'interno della lista di rif esterni del documento
	 */
	public void invioTelematico(int pos) {
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@invioTelematico");
		defaultForm.addParam("postitPos", pos); //utilizzo postitPos perchè disponibile per questa operazione
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
    public void cancelWorkflowInstance(String bwfInstanceId) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@cancelWorkflow");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("bwfInstanceId", bwfInstanceId);
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
    public void doAssignWorkflow(String bwfId, String bwfVersion, String nrecord) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@assignworkflow@" + nrecord);
		defaultForm.addParam("bwfId", bwfId);
		defaultForm.addParam("bwfVersion", bwfVersion);
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
    public void delegaWorkflowTask(String taskId, String userId) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@delegaWorkflowTask");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("taskId", taskId);
		defaultForm.addParam("userId", userId);
    }
    
    // dpranteda 23/04/2015 - prendo la lista di task in stato ready dell'istanza del flusso 
    /**
     * restituisce la lista di task in stato ready dell'istanza del flusso
     * @param bwfInstanceId identificativo dell'istanza di workflow
     */
    public void getReadyTaskListAndUserListForDelega(String bwfInstanceId, String listOfCodUff, String listOfRuoli) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@readyTaskListAndUserList");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", defaultForm.getParam("physDoc"));
		defaultForm.addParam("bwfInstanceId", bwfInstanceId);
		defaultForm.addParam("listOfCodUff", listOfCodUff);
		defaultForm.addParam("listOfRuoli", listOfRuoli);
    }
}
