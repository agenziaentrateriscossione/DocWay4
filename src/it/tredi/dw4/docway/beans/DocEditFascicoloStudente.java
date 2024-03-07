package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Pagina di inserimento di fascicoli degli studenti
 */
public class DocEditFascicoloStudente extends DocEditFascicolo {
	
	private final String DEFAULT_FASCICOLO_STUDENTE_TITLE = "dw4.fascicolo_degli_studenti_Inserimento";

	public DocEditFascicoloStudente() throws Exception {
		super();
	}

	@Override
	public void init(Document dom) {
		super.init(dom);
		
		// tutti i fascicoli del personale non devono avere la sezione relativi
		// ai responsabili, viene quindi azzerata in questo punto
		// N.B.: caso riscontrato in "nuovo" da fascicolazione di un documento 
		getFascicolo().setAssegnazioneRPA(new Rif());
		getFascicolo().setSoggetto("");
		
		setInsFascicoloPersonaleTitle();
		
		// caricamento della classificazione specificata per il fascicolo speciale
		if (getFascicoloSpecialeInfo() != null && getFascicoloSpecialeInfo().getId() != null && getFascicoloSpecialeInfo().getId().length() > 0) {
			if (getFascicoloSpecialeInfo().getClassif().getCod() != null) {
				getFascicolo().getClassif().setCod(getFascicoloSpecialeInfo().getClassif().getCod());
				getFascicolo().getClassif().setText(getFascicoloSpecialeInfo().getClassif().getText());
				getFascicolo().getClassif().setText_ro(getFascicoloSpecialeInfo().getClassif().getText_ro());
			}
		}
	}
	
	/**
	 * Imposta il titolo della maschera di inserimento del fascicolo del personale
	 * @param fasc_numero_sottofasc
	 */
	private void setInsFascicoloPersonaleTitle() {
		setInsFascicoloTitleByNumSottoFasc();

		if (insFascicoloTitle.equals(DEFAULT_FASCICOLO_TITLE))
			insFascicoloTitle = DEFAULT_FASCICOLO_STUDENTE_TITLE;
	}
	
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
			
			// TODO controllare tutti i casi di ritorno
			
			String assignAndClose = XMLUtil.parseAttribute(response.getDocument(), "response/@assignAndClose", "false");
			if (isPopupPage() && assignAndClose.equals("true")) {
				// inserimento da popup con assegnazione di un documento al fascicolo
				getFormsAdapter().fillFormsFromResponse(response);
				this.init(response.getDocument());
				setSessionAttribute("docEditFascicolo", this);
				
				this.setPopupPage(true);
				return null;
			}
			else {
				// inserimento classico del fascicolo
				buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", "", "", "@studente", response, this.isPopupPage());		
				return "showdoc@fascicolo@studente@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
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
		}
		
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
		
		return result;
	}

}
