package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Interno;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.AppUtil;

import org.dom4j.Document;

public class DocEditInterno extends DocEditDoc {
	private Interno doc = new Interno();
	
	private final String DEFAULT_INTERNO_TITLE = "dw4.ins_tra_uffici";
	
	public DocEditInterno() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public boolean isDocEditModify() {
		return false;
	}
	
	@Override
	public void init(Document domDocumento) {
		this.doc = new Interno();
		this.doc.init(domDocumento);
		
		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(domDocumento);
		
		// Imposto il titolo della pagina di creazione del documento
		setInsArrivoTitleByCodRepertorio();
		
		if (copyIfNotRipetiInFascicolo) {
			doc.getMinuta().getMittente().setNome_persona("_MINUTAMITTENTENOME_");
			doc.getMinuta().getMittente().setNome_uff("_MINUTAMITTENTEUFF_");
		}
	}
	
	/**
	 * Imposta il titolo della maschera di inserimento del documento
	 */
	private void setInsArrivoTitleByCodRepertorio() {
		if (doceditRep && descrizioneRepertorio != null && descrizioneRepertorio.length() > 0)
			docEditTitle = descrizioneRepertorio + " - " + I18N.mrs("acl.insert");
		else
			docEditTitle = I18N.mrs(DEFAULT_INTERNO_TITLE);
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// personalizzazione del salvataggio per il repertorio
			boolean isRepertorio = false;
			if (doceditRep)
				isRepertorio = true;
			
			formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams("", false, isRepertorio));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("interno", response);		
			return "showdoc@interno@reload";

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public Interno getDoc() {
		return doc;
	}

	public void setDoc(Interno interno) {
		this.doc = interno;
	}
	
	/**
	 * Thesauro vincolato su titolario di classificazione per la minuta
	 * 
	 * @return
	 * @throws Exception
	 */
	public String thVincolatoTitolarioClassificazioneMinuta() throws Exception {
		return showThesRelMinuta(false);
	}
	
	/**
	 * Gerarchia su titolario di classificazione per la minuta
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gerarchiaTitolarioClassificazioneMinuta() throws Exception {
		return showThesRelMinuta(true);
	}
	
	/**
	 * Caricamento del titolario di classificazione per la minuta
	 * 
	 * @param showGerarchia
	 * @return
	 * @throws Exception
	 */
	private String showThesRelMinuta(boolean showGerarchia) throws Exception {
		String keypath = "classif";
		String startkey = Const.TITOLARIO_CLASSIF_NODO_RADICE;
		
		// Filtro impostato su campo codice
		String value = (getDoc().getMinuta().getClassif() != null && !"".equals(getDoc().getMinuta().getClassif().getFiltroCod())) ? getDoc().getMinuta().getClassif().getFiltroCod() : "";
		if (value.length() > 0) {
			// Devo formattare il valore passato in base alla classificazione
			value = ClassifUtil.formatClassifCode(value);
			
			keypath = "CLASSIF_FROM_CODE";
			startkey = "lookupHierFromClassifCode";
		}
		
		String toDo = "";
		if (showGerarchia)
			toDo = "print_all";
		
		callShowThesRel(doc, I18N.mrs("dw4.titolario_di_classificazione"), "NT", keypath, startkey, AppUtil.getXdocwayprocDbName(getFormsAdapter().getDb()), toDo, value, ".minuta");
		
		// Azzero il campo nel form
		getDoc().getMinuta().getClassif().setFiltroCod("");
		
		return null;
	}
	
	/**
	 * Lookup su Voce di Indice
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupMinutaVoceIndice() throws Exception {
		String value = (getDoc().getMinuta() != null && getDoc().getMinuta().getVoce_indice() != null && getDoc().getMinuta().getVoce_indice().getText() != null) ? getDoc().getMinuta().getVoce_indice().getText() : "";

		String aliasName 	= "tit_voce";
		String aliasName1 	= "";
		String titolo 		= "xml,/indice_titolario/@voce"; //titolo 
		String ord 			= "xml(xpart:/indice_titolario/@voce)"; //ord 
		String campi 		= ".minuta.voce_indice.text=xml,/indice_titolario/@voce" +
								" ; .minuta.classif.text=xml,/indice_titolario/compilazione_automatica/classif/@cod \"-\" xml,/indice_titolario/compilazione_automatica/classif" +
								" ; .minuta.classif.@cod=xml,/indice_titolario/compilazione_automatica/classif/@cod" +
								" ; .minuta.@scarto=xml,/indice_titolario/compilazione_automatica/scarto/@val" +
								" ; m_scarto_ro=xml,/indice_titolario/compilazione_automatica/scarto/@val" +
								" ; .minuta.classif.text_ro=xml,/indice_titolario/compilazione_automatica/classif/@cod \"-\" xml,/indice_titolario/compilazione_automatica/classif";
		String db 			= AppUtil.getXdocwayprocDbName(getFormsAdapter().getDb()); //db 
		String newRecord 	= ""; //newRecord 
		String xq			= ""; //extraQuery
		
		callLookup(getDoc(), aliasName, aliasName1, titolo, ord, campi, campi, xq, db, newRecord, value, getDoc().getTipo(), formsAdapter.getDefaultForm().getParam("xverb"));
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di Lookup di Voce di Indice
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearLookupMinutaVoceIndice() throws Exception {
		// In questo caso non occorre azzerare nulla 
		
		return null;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = false;
		
		result = super.checkRequiredFieldCommon(false); // controlli comuni a tutte le tipologie di documenti
		
		// Imposto lo scarto automatico se non impostato
		if (getDoc().getMinuta().getScarto() == null || getDoc().getMinuta().getScarto().length() == 0)
			getDoc().getMinuta().setScarto(AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ScartoAutomatico")));
		
		// Controllo che l'RPAM sia stato selezionato (mittente)
		if (!getFormsAdapter().checkBooleanFunzionalitaDisponibile("docRPAEreditabile", false)) {
			if (getDoc().getAssegnazioneRPAM() == null || 
					((getDoc().getAssegnazioneRPAM().getNome_uff() == null || "".equals(getDoc().getAssegnazioneRPAM().getNome_uff().trim())) &&
							(getDoc().getAssegnazioneRPAM().getNome_persona() == null || "".equals(getDoc().getAssegnazioneRPAM().getNome_persona().trim())))) {
				
				String[] fieldIds = { "templateForm:rpam_nome_uff_input", "templateForm:rpam_nome_persona_input" };
				this.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_il_campo_proprietario_minuta"));
				result = true;
			}
		
			// Controllo che l'RPA sia stato selezionato (destinatario)
			if (getDoc().getAssegnazioneRPA() == null || 
					((getDoc().getAssegnazioneRPA().getNome_uff() == null || "".equals(getDoc().getAssegnazioneRPA().getNome_uff().trim())) &&
							(getDoc().getAssegnazioneRPA().getNome_persona() == null || "".equals(getDoc().getAssegnazioneRPA().getNome_persona().trim())))) {
				
				String[] fieldIds = { "templateForm:rpa_nome_uff_input", "templateForm:rpa_nome_persona_input" };
				this.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_il_campo_proprietario"));
				result = true;
			}
		}
		
		// Controllo su mezzo di trasmissione
		if (checkMezzoTrasmissione())
			result = true;
		
		return result;
	}
	
}
