package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Varie;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;

import org.dom4j.Document;

public class DocEditModifyVarie extends DocEditDoc {
	private Varie doc = new Varie();
	
	private final String DEFAULT_VARIE_TITLE = "dw4.mod_documenti_non_protocollati";
	private final String DEFAULT_VARIE_NOPROT_TITLE = "dw4.mod_documenti";
	
	public DocEditModifyVarie() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public boolean isDocEditModify() {
		return true;
	}
	
	@Override
	public void init(Document domDocumento) {
		this.doc = new Varie();
		this.doc.init(domDocumento);
		
		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(domDocumento);
		
		// Imposto il titolo della pagina di creazione del documento
		setInsArrivoTitleByCodRepertorio();
	}
	
	/**
	 * Imposta il titolo della maschera di inserimento del documento
	 */
	private void setInsArrivoTitleByCodRepertorio() {
		if (doceditRep && descrizioneRepertorio != null && descrizioneRepertorio.length() > 0)
			docEditTitle = descrizioneRepertorio + " - " + I18N.mrs("acl.modify");
		else
			if (formsAdapter.checkBooleanFunzionalitaDisponibile("nascondiprotocollo", false))
				docEditTitle = I18N.mrs(DEFAULT_VARIE_NOPROT_TITLE);
			else
				docEditTitle = I18N.mrs(DEFAULT_VARIE_TITLE);
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// personalizzazione del salvataggio per il repertorio
			boolean isRepertorio = false;
			if (doc.getRepertorio() != null && doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
				isRepertorio = true;
			
			formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams("", true, isRepertorio));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("varie", response);		
			return "showdoc@varie@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public Varie getDoc() {
		return doc;
	}

	public void setDoc(Varie varie) {
		this.doc = varie;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;
		
		result = super.checkRequiredFieldCommon(true); // controlli comuni a tutte le tipologie di documenti
		
		// Controllo su data del documento
		if (getDoc().getData_prot() == null || getDoc().getData_prot().length() == 0) {
			this.setErrorMessage("templateForm:dataProtDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_doc") + "'");
			result = true;
		}
		else {
			// Controllo se il valore associato alla data e' corretto
			if (!DateUtil.isValidDate(getDoc().getData_prot(), formatoData)) {
				this.setErrorMessage("templateForm:dataProtDoc", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_doc") + "': " + formatoData.toLowerCase());
				result = true;
			}
		}
		
		// Imposto lo scarto automatico se non impostato
		if (getDoc().getScarto() == null || getDoc().getScarto().length() == 0)
			getDoc().setScarto(AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ScartoAutomatico")));
				
		return result;
	}
	
}
