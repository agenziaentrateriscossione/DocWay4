package it.tredi.dw4.docway.doc.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class DocDocWayDocEditFormsAdapter extends DocEditFormsAdapter {

	public DocDocWayDocEditFormsAdapter(AdapterConfig config) {
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
		defaultForm.addParam("assignAndClose", root.attributeValue("assignAndClose", ""));
		defaultForm.addParam("soggetto_infasc", root.attributeValue("soggetto_infasc", ""));
		
		//Bottone assegnazione link fascicolo-documento
		defaultForm.addParam("bAssegnaLinkFasc", root.attributeValue("bAssegnaLinkFasc", ""));
		
		//per registrazione di protocollo differito (data corrente)
		defaultForm.addParam("currDate", root.attributeValue("currDate", ""));
		
		//per la gestione delle bozze
		defaultForm.addParam("bozzaDaProt", "");
		
		//per la verifica dei duplicati
		if (this.funzionalitaDisponibili.get("verificaDuplicati") != null)
			defaultForm.addParam("verificaDuplicati", this.funzionalitaDisponibili.get("verificaDuplicati").toString());
		
		//per foto originale
		if (this.funzionalitaDisponibili.get("foto_originale") != null && this.funzionalitaDisponibili.get("foto_originale")) {
			defaultForm.addParam("fotoOriginale", "true");
		}
		
		//introdotta possibilita' di ereditare RPA/classificazione dal fascicolo
		if (this.funzionalitaDisponibili.get("docClassifEreditabile") != null)
			defaultForm.addParam("docClassifEreditabile", this.funzionalitaDisponibili.get("docClassifEreditabile").toString());
		if (this.funzionalitaDisponibili.get("docRPAEreditabile") != null)
			defaultForm.addParam("docRPAEreditabile", this.funzionalitaDisponibili.get("docRPAEreditabile").toString());
		
		//per set automatico di default di campi in inserimento/modifica
		defaultForm.addParam("campiDefault", root.attributeValue("campiDefault", ""));
		
		//gestione endorser
		defaultForm.addParam("stampaSegnatura", root.attributeValue("stampaSegnatura", ""));
		defaultForm.addParam("fotoOriginale", "");
		
		//gestione endorscan
		defaultForm.addParam("segnaturaEndorscan", root.attributeValue("segnaturaEndorscan", ""));
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response assignAndClose.
	 * 
	 * @return Valore impostato sull'attributo fromLookup
	 */
	public String getAssignAndClose() {
		return defaultForm.getParam("assignAndClose");
	}

	/**
	 * Ricerca di un documento in base al numero di protocollo
	 */
	public void cercaProtocollo(String year, String numProt, String codSocieta) {
		defaultForm.addParam("xverb", "@cercaProtocollo");
		defaultForm.addParam(".@currYear", year);
		defaultForm.addParam(".doc.@num_prot", numProt);
		if (codSocieta != null && codSocieta.trim().length() > 0)
			defaultForm.addParam("_CODSOCIETA_", codSocieta);
	}
	
	/**
	 * Clean su maschera di acquisizione delle immagini (reload della pagina senza documento
	 * caricato)
	 */
	public void pulisciAcquisizioneImmagini(String year) {
		defaultForm.addParam(".@currYear", year);
		defaultForm.addParam(".doc.@num_prot", "");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("physDoc", "");
		defaultForm.addParam("toDo", "");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("pos", "");
	}
	
	/**
	 * Abbandono della pagina di acquisizione immagini (foto originale) e ritorno a showdoc
	 * del documento
	 */
	public void abbandonaFotoOriginale() {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "");
		String selid = defaultForm.getParam("selid");
		if (selid == null || selid.equals("")) { // occorre aprire in base a # fisico
			int toDoValue = new Integer(defaultForm.getParam("toDo")).intValue();
			defaultForm.addParam("pos", toDoValue - 1);
		}
	}

	/**
	 * Acquisizione immagini su documento
	 */
	public void acquisizioneImmagini(boolean fotoOriginale) {
		defaultForm.addParam("xverb", "@acquisizioneImmagini");
		defaultForm.addParam("physDoc", defaultForm.getParam("toDo"));
		if (fotoOriginale)
			defaultForm.addParam("fotoOriginale", "true");
	}

	/**
	 * Invio di email da resoconto doc assegnati (notifiche differite)
	 */
	public void inviaEmailResocontoDocAssegnati() {
		defaultForm.addParam("verbo", "resoconto_doc_assegnati");
		defaultForm.addParam("xverb", "@sendEMail");
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
     * Switch del form di inserimento da generico a specifico del fascicolo speciale
     * @param dbTable
     */
    public void switchToTableFS(String dbTable) {
    	defaultForm.addParam("dbTable", defaultForm.getParam("dbTable") + "@" + dbTable);
    }
	
    /**
     * Switch del form di inserimento da specifico del fascicolo speciale a generico
     */
    public void switchToTableFascicolo() {
    	defaultForm.addParam("dbTable", "@fascicolo");
    }
    
    /**
     * creazione di un nuovo workflow su Bonita
     * @param barName nome del file bar caricato
     */
    public void addWorkflow(String barName) {
    	defaultForm.addParam("verbo", "workflow_response");
		defaultForm.addParam("xverb", "@new");
		defaultForm.addParam("barfilename", barName);
		
		// TODO sembra che i parametri seguenti non vengano considerati dal WS di creazione
		defaultForm.addParam("workflowversion", "");
		defaultForm.addParam("wfuser", "");
		defaultForm.addParam("wfpassword", "");
    }
    
    public void clearDocument(String codRep, String descrRep){
    	if(!codRep.isEmpty())
    		defaultForm.addParam("codice_rep", codRep);
    	
    	if(!descrRep.isEmpty())
    		defaultForm.addParam("descrizione_rep", descrRep);
    	
    	defaultForm.addParam("verbo","docEdit");
    	defaultForm.addParam("pos",0);
    	defaultForm.addParam("selid","");

    	defaultForm.addParam("toDo","");
    }
    
}
