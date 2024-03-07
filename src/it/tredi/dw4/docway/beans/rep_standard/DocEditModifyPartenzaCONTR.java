package it.tredi.dw4.docway.beans.rep_standard;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyPartenza;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLDocumento;

public class DocEditModifyPartenzaCONTR extends DocEditModifyPartenza {

	public DocEditModifyPartenzaCONTR() throws Exception {
		super();
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			boolean rifEsterniModificabili = true;
			if (!formsAdapter.checkBooleanFunzionalitaDisponibile("abilitaModificaDatiDiProtocollo", false) 
					&& getDoc().getNum_prot() != null && getDoc().getNum_prot().length() > 0 
					&& !getDoc().isBozza() 
					&& copyIfNotStandardRep)
				rifEsterniModificabili = false;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", true, rifEsterniModificabili, true));
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
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
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
		
		result = super.checkRequiredFieldCommon(true); // controlli comuni a tutte le tipologie di documenti
		
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
		
		// controllo su tutti i campi data (verifica del formato)
		if (getDoc().getData_reale() != null && getDoc().getData_reale().length() > 0) {
			if (!DateUtil.isValidDate(getDoc().getData_reale(), formatoData)) {
				this.setErrorMessage("templateForm:dataRealeDoc", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_doc") + "': " + formatoData.toLowerCase());
				result = true;
			}
			else {
				int dataReale = new Integer(DateUtil.formatDate2XW(getDoc().getData_reale(), formatoData)).intValue();
				
				if (getDoc().getData_prot() != null && !getDoc().getData_prot().equals("") && !getDoc().getData_prot().equals(".")) {
					// Controllo che la data specificata sia antecedente alla data di protocollo
					String dataProtDocumento = getDoc().getData_prot();
					if (!DateUtil.checkNumericDate(dataProtDocumento))
						dataProtDocumento = DateUtil.formatDate2XW(dataProtDocumento, formatoData);
					if (dataReale > new Integer(dataProtDocumento).intValue()) {
						this.setErrorMessage("templateForm:dataRealeDoc", I18N.mrs("dw4.la_data_del_documento_non_puo_essere_superiore_alla_data_di_protocollazione"));
						result = true;
					}
				}
				else {
					// Controllo che la data specificata sia antecedente alla data corerente
					int dataCur = new Integer(DateUtil.getCurrentDateNorm()).intValue();
					if (dataReale > dataCur) {
						this.setErrorMessage("templateForm:dataRealeDoc", I18N.mrs("dw4.la_data_del_documento_non_puo_essere_superiore_alla_data_odierna"));
						result = true;
					}
				}
			}
		}
				
		return result;
	}

}
