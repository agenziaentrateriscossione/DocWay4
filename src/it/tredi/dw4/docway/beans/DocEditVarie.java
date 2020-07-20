package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Allegato;
import it.tredi.dw4.docway.model.Varie;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Date;

import org.dom4j.Document;

public class DocEditVarie extends DocEditDoc {
	private Varie doc = new Varie();
	
	private final String DEFAULT_VARIE_TITLE = "dw4.ins_documenti_non_protocollati";
	private final String DEFAULT_VARIE_NOPROT_TITLE = "dw4.ins_documenti";
	
	public DocEditVarie() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public boolean isDocEditModify() {
		return false;
	}
	
	@Override
	public void init(Document domDocumento) {
		this.doc = new Varie();
		this.doc.init(domDocumento);
		
		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(domDocumento);
		
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile("dataNonProtocollatiSi", false))
			this.doc.setData_prot(XMLUtil.parseAttribute(domDocumento, "response/@currDate", ""));
		
		// Imposto il titolo della pagina di creazione del documento
		setInsArrivoTitleByCodRepertorio();
		
		// gestione campi custom: in caso di sezioni obbligatorie disabilitate occorre assegnare un valore
		// di default per i dati obbligatori (solo per documenti non protocollati, sui protocolli non e' 
		// possibile nascondere parti con campi obbligatori)
		initHiddenCustomFields();
	}
	
	/**
	 * assegnazione di un valore di default per tutti i campi obbligatori del documento che sono stati nascosti 
	 * dalla pagina di inserimento (SOLO PER DOCUMENTI NON PROTOCOLLATI)
	 */
	private void initHiddenCustomFields() {
		if (getCustomfields().getHiddenFields().containsKey("dataProt") && getCustomfields().getHiddenFields().get("dataProt")) {
			// campo dataProt nascosto
			String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
			if (!getDoc().getData_prot().equals(""))
				getDoc().setData_prot(DateUtil.changeDateFormat(getDoc().getData_prot(), "yyyyMMdd", formatoData));
			else
				getDoc().setData_prot(DateUtil.getLongDate(new Date())); // TODO gestire il formato di ritornoß
		}
		if (getCustomfields().getHiddenFields().containsKey("allegati") && getCustomfields().getHiddenFields().get("allegati")) {
			// campo allegati nascosto
			if (getDoc().getAllegati().size() == 0)
				getDoc().getAllegati().add(new Allegato());
			
			String nessunAllegatoText = I18N.mrs("dw4.0_nessun_allegato");
			if (getCustomfields().getHiddenFieldsVariables().containsKey("allegatiEmpty") && !getCustomfields().getHiddenFieldsVariables().get("allegatiEmpty").equals(""))
				nessunAllegatoText = getCustomfields().getHiddenFieldsVariables().get("allegatiEmpty");
			
			getDoc().getAllegati().get(0).setText(nessunAllegatoText);
		}
		if (getCustomfields().getHiddenFields().containsKey("classificazione") && getCustomfields().getHiddenFields().get("classificazione")) {
			// campo classificazione nascosto (viene impostata la classificazione automatica)
			String classifValue = I18N.mrs("dw4.00_non_classificato");
			if (getCustomfields().getHiddenFieldsVariables().containsKey("classificazioneAutomatica") && !getCustomfields().getHiddenFieldsVariables().get("classificazioneAutomatica").equals(""))
				classifValue = getCustomfields().getHiddenFieldsVariables().get("classificazioneAutomatica");
			
			if (classifValue != null && classifValue.length() > 0) {
				getDoc().getClassif().setText(classifValue);
				int index = classifValue.indexOf(" - ");
				if (index != -1)
					getDoc().getClassif().setCod(classifValue.substring(0, index));
			}
		}
	}
	
	/**
	 * Imposta il titolo della maschera di inserimento del documento
	 */
	private void setInsArrivoTitleByCodRepertorio() {
		if (doceditRep && descrizioneRepertorio != null && descrizioneRepertorio.length() > 0)
			docEditTitle = descrizioneRepertorio + " - " + I18N.mrs("acl.insert");
		else {
			if (formsAdapter.checkBooleanFunzionalitaDisponibile("nascondiprotocollo", false))
				docEditTitle = I18N.mrs(DEFAULT_VARIE_NOPROT_TITLE);
			else
				docEditTitle = I18N.mrs(DEFAULT_VARIE_TITLE);
		}
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			// nel caso in cui gli allegati siano disattivati in inserimenti di doc non protocollati
			// si procede con l'assegnazione di ''
			if (formsAdapter.checkBooleanFunzionalitaDisponibile("disAllegato", false)) {
				if (doc.getAllegati() != null && doc.getAllegati().size() == 1 && doc.getAllegati().get(0) != null) {
					doc.getAllegati().get(0).setText("0 - nessun allegato");
				}
			}
						
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
		
		result = super.checkRequiredFieldCommon(false); // controlli comuni a tutte le tipologie di documenti
		
		// Controllo su data del documento
		String dataProtDocFieldId = "dataProtDoc";
		if (formsAdapter.checkBooleanFunzionalitaDisponibile("rppNascondiData", false))
			dataProtDocFieldId = "dataProtDocRpp";
		
		if (getDoc().getData_prot() == null || getDoc().getData_prot().length() == 0) {
			this.setErrorMessage("templateForm:" + dataProtDocFieldId, I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_doc") + "'");
			result = true;
		}
		else {
			// Controllo se il valore associato alla data e' corretto
			if (!DateUtil.isValidDate(getDoc().getData_prot(), formatoData)) {
				this.setErrorMessage("templateForm:" + dataProtDocFieldId, I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_doc") + "': " + formatoData.toLowerCase());
				result = true;
			}
		}
		
		// Imposto lo scarto automatico se non impostato
		if (getDoc().getScarto() == null || getDoc().getScarto().length() == 0)
			getDoc().setScarto(AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ScartoAutomatico")));
		
		// Controllo che l'RPA sia stato selezionato
		if (!getFormsAdapter().checkBooleanFunzionalitaDisponibile("docRPAEreditabile", false)) {
			if (getDoc().getAssegnazioneRPA() == null || 
					((getDoc().getAssegnazioneRPA().getNome_uff() == null || "".equals(getDoc().getAssegnazioneRPA().getNome_uff().trim())) &&
							(getDoc().getAssegnazioneRPA().getNome_persona() == null || "".equals(getDoc().getAssegnazioneRPA().getNome_persona().trim())))) {
				
				String[] fieldIds = { "templateForm:rpa_nome_uff_input", "templateForm:rpa_nome_persona_input", "templateForm:rpa_nome_ruolo_input" };
				this.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_il_campo_proprietario"));
				result = true;
			}
		}
				
		return result;
	}
	
}
