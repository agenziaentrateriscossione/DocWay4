package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.model.FascicoloSpecialeInfo;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Pagina di modifica di fascicoli degli studenti
 */
public class DocEditModifyFascicoloStudente extends DocEditModifyFascicolo {
	
	private final String DEFAULT_FASCICOLO_STUDENTE_TITLE = "dw4.fascicolo_degli_studenti_Modifica";

	private FascicoloSpecialeInfo fascicoloSpecialeInfo = new FascicoloSpecialeInfo();
	
	public DocEditModifyFascicoloStudente() throws Exception {
		super();
	}

	public FascicoloSpecialeInfo getFascicoloSpecialeInfo() {
		return fascicoloSpecialeInfo;
	}

	public void setFascicoloSpecialeInfo(FascicoloSpecialeInfo fascicoloSpecialeInfo) {
		this.fascicoloSpecialeInfo = fascicoloSpecialeInfo;
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		setModFascicoloPersonaleTitle(XMLUtil.parseStrictAttribute(dom, "/response/@fasc_numero_sottofasc"));
		
		// caricamento della classificazione specificata per il fascicolo speciale
		fascicoloSpecialeInfo.init(XMLUtil.createDocument(dom, "/response/fascicolo_speciale_info"));
		if (fascicoloSpecialeInfo != null && fascicoloSpecialeInfo.getId() != null && fascicoloSpecialeInfo.getId().length() > 0) {
			if (fascicoloSpecialeInfo.getClassif().getCod() != null) {
				getFascicolo().getClassif().setCod(fascicoloSpecialeInfo.getClassif().getCod());
				getFascicolo().getClassif().setText(fascicoloSpecialeInfo.getClassif().getText());
				getFascicolo().getClassif().setText_ro(fascicoloSpecialeInfo.getClassif().getText_ro());
			}
		}
	}
	
	/**
	 * salvataggio del fascicolo del personale
	 */
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredFieldFascicoloPersonale()) return null;
			
			getFormsAdapter().getDefaultForm().addParams(getFascicolo().asFormAdapterParams(""));
			XMLDocumento response = super._saveDocument("fascicolo", "list_of_doc");
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", "", "", "@studente", response, isPopupPage());		
			return "showdoc@fascicolo@studente@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * abbandono della modifica del fascicolo del personale
	 */
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", "", "", "@studente", response, isPopupPage());
			return "showdoc@fascicolo@studente@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Imposta il titolo della maschera di inserimento del fascicolo del personale
	 * @param fasc_numero_sottofasc
	 */
	private void setModFascicoloPersonaleTitle(String fasc_numero_sottofasc) {
		setModFascicoloTitleByNumSottoFasc(fasc_numero_sottofasc);

		if (modFascicoloTitle.equals(DEFAULT_FASCICOLO_TITLE))
			modFascicoloTitle = DEFAULT_FASCICOLO_STUDENTE_TITLE;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	private boolean checkRequiredFieldFascicoloPersonale(){
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;
		
		if (isShowIfSottoFasc()) {
			// Controllo se il campo 'oggetto' e' valorizzato
			if (getFascicolo().getOggetto() == null || "".equals(getFascicolo().getOggetto().trim())) {
				this.setErrorMessage("templateForm:fasc_oggetto", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.object") + "'");
			    result = true;
			}
		}
		if (isShowIfFasc()) {
			// Controllo se il campo 'nominativo' e' valorizzato
			if (getFascicolo().getOggetto() == null || "".equals(getFascicolo().getOggetto().trim())) {
				this.setErrorMessage("templateForm:fasc_nominativo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.nominativo") + "'");
			    result = true;
			}
			
			// mbernardini 21/05/2018 : i controlli su campi specifici del template sono validi solo se si tratta dell'inserimento/modifica di un fascicolo radice
			
			// Controllo sul formato dei campi data
			if (getFascicolo().getFascicolo_speciale().getData_nascita() != null && getFascicolo().getFascicolo_speciale().getData_nascita().length() > 0) {
				if (!DateUtil.isValidDate(getFascicolo().getFascicolo_speciale().getData_nascita(), formatoData)) {
					this.setErrorMessage("templateForm:fasc_data_nascita", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.birthdate") + "': " + formatoData.toLowerCase());
					result = true;
				}
			}
			if (getFascicolo().getFascicolo_speciale().getData_immatricolazione() != null && getFascicolo().getFascicolo_speciale().getData_immatricolazione().length() > 0) {
				if (!DateUtil.isValidDate(getFascicolo().getFascicolo_speciale().getData_immatricolazione(), formatoData)) {
					this.setErrorMessage("templateForm:fasc_data_immatricolazione", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_immatr") + "': " + formatoData.toLowerCase());
					result = true;
				}
			}
		}
		
		return result;
	}
	
}
