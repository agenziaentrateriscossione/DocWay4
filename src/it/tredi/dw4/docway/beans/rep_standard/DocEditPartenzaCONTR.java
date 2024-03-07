package it.tredi.dw4.docway.beans.rep_standard;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditPartenza;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLDocumento;

public class DocEditPartenzaCONTR  extends DocEditPartenza {

	public DocEditPartenzaCONTR() throws Exception {
		super();
	}

	@Override
	public void init(Document domDocumento) {
		super.init(domDocumento);
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;

			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", false, true));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");

			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			ShowdocPartenzaCONTR showdocPartenzaCONTR = new ShowdocPartenzaCONTR();
			showdocPartenzaCONTR.getFormsAdapter().fillFormsFromResponse(response);
			showdocPartenzaCONTR.init(response.getDocument());
			showdocPartenzaCONTR.loadAspects("@partenza", response, "showdoc");
			setSessionAttribute("showdocPartenzaCONTR", showdocPartenzaCONTR);

			return "rep_standard_showdoc@partenza@CONTR@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
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

		// Controllo se almeno un destinatario del documento e' valorizzato
		if (getDoc().getRif_esterni().get(0).getNome() == null || getDoc().getRif_esterni().get(0).getNome().length() == 0) {
			String fieldId = "templateForm:docEditDestinatari:0:nomeDestinatario_input";
			if (!getDoc().getRif_esterni().get(0).isVincolato())
				fieldId = "templateForm:docEditDestinatari:0:nomeDestinatarioLibero";
			this.setErrorMessage(fieldId, I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.contraente") + "'");
			result = true;
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

		// controllo su tutti i campi data (verifica del formato)
		if (getDoc().getData_reale() != null && getDoc().getData_reale().length() > 0) {
			if (!DateUtil.isValidDate(getDoc().getData_reale(), formatoData)) {
				this.setErrorMessage("templateForm:dataRealeDoc", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_doc") + "': " + formatoData.toLowerCase());
				result = true;
			}
		}

		return result;
	}

}
