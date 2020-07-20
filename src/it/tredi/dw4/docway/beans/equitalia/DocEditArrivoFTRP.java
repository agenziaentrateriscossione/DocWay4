package it.tredi.dw4.docway.beans.equitalia;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditArrivo;
import it.tredi.dw4.docway.model.equitalia.ExtraFTRP;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditArrivoFTRP  extends DocEditArrivo  {

	// campi specifici del repertori fatture di equitalia
	private ExtraFTRP extraFTRP = new ExtraFTRP();
	
	public DocEditArrivoFTRP() throws Exception {
		super();
	}
	
	public boolean isDocEditModify() {
		return false;
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		extraFTRP.init(XMLUtil.createDocument(dom, "/response/doc/extra"));
		
		extraFTRP.setStato_invio_nav("Da inviare");
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// verifica diplicato in data mittente (se attivo! verificaDuplicati == 'Si' o verificaDuplicati == 'Fine')
			if ((getDoc().getRif_esterni().get(0).getData_prot() == null || getDoc().getRif_esterni().get(0).getData_prot().equals(""))
					&& (getDoc().getRif_esterni().get(0).getN_prot() == null || getDoc().getRif_esterni().get(0).getN_prot().equals(""))) {
				// nel caso in cui i dati di prot del mittente (data e numero) non siano stati compilati
				// viene forzata a true la verifica dei duplicati
				duplicatoVerificato = true;
			}
			if (funzDispVerificaDuplicati != null 
					&& (funzDispVerificaDuplicati.toLowerCase().equals("si") || funzDispVerificaDuplicati.toLowerCase().equals("fine"))
					&& !duplicatoVerificato) {
				return null;
			}
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", false, true));
			formsAdapter.getDefaultForm().addParams(extraFTRP.asFormAdapterParams(".extra"));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocArrivoFTRP showdocArrivoFTRP = new ShowdocArrivoFTRP();
			showdocArrivoFTRP.getFormsAdapter().fillFormsFromResponse(response);
			showdocArrivoFTRP.init(response.getDocument());
			showdocArrivoFTRP.loadAspects("@arrivo", response, "showdoc");
			setSessionAttribute("showdocArrivoFTRP", showdocArrivoFTRP);
			
			return "equitalia_showdoc@arrivo@FTRP@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public ExtraFTRP getExtraFTRP() {
		return extraFTRP;
	}

	public void setExtraFTRP(ExtraFTRP extraFTR) {
		this.extraFTRP = extraFTR;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = super.checkRequiredField();
		
		// controllo sul fornitore della fattura
		if (getDoc().getRif_esterni().get(0).getPartita_iva() == null || getDoc().getRif_esterni().get(0).getPartita_iva().length() == 0
				|| getDoc().getRif_esterni().get(0).getCodice_fiscale() == null || getDoc().getRif_esterni().get(0).getCodice_fiscale().length() == 0) {
			this.setErrorMessage("templateForm:nomeMittente_input", I18N.mrs("dw4.il_campo_fornitore_e_privo_di_piva_cf"));
			result = true;
		}
		
		// controllo sul campo numero fattura
		if (getDoc().getRif_esterni().get(0).getN_prot() == null || getDoc().getRif_esterni().get(0).getN_prot().length() == 0) {
			this.setErrorMessage("templateForm:protMittente", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.numero_fattura") + "'");
			result = true;
		}
		
		// controllo sul campo data fattura
		if (getDoc().getRif_esterni().get(0).getData_prot() == null || getDoc().getRif_esterni().get(0).getData_prot().length() == 0) {
			this.setErrorMessage("templateForm:dataProtMittente", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_fattura") + "'");
			result = true;
		}
		else if (!DateUtil.isValidDate(getDoc().getRif_esterni().get(0).getData_prot(), formatoData)) {
			this.setErrorMessage("templateForm:dataProtMittente", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_fattura") + "': " + formatoData.toLowerCase());
			result = true;
		}
		
		// controllo sul campo data ricezione
		if (getExtraFTRP().getData_ricezione() == null || getExtraFTRP().getData_ricezione().length() == 0) {
			this.setErrorMessage("templateForm:dataRicezioneDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_ricezione") + "'");
			result = true;
		}
		else if (!DateUtil.isValidDate(getExtraFTRP().getData_ricezione(), formatoData)) {
			this.setErrorMessage("templateForm:dataRicezioneDoc", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_ricezione") + "': " + formatoData.toLowerCase());
			result = true;
		}
		
		// controllo sul campo numero CIG
		if (getExtraFTRP().getNumero_cig() == null || getExtraFTRP().getNumero_cig().length() == 0) {
			this.setErrorMessage("templateForm:numeroCigDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.numero_cig") + "'");
			result = true;
		}
		
		// controllo sul campo importo
		if (getExtraFTRP().getImporto() == null || getExtraFTRP().getImporto().length() == 0) {
			this.setErrorMessage("templateForm:importoDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.importo") + "'");
			result = true;
		}
		else if (!StringUtil.isDecimal(getExtraFTRP().getImporto())) {
			this.setErrorMessage("templateForm:importoDoc", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " '" + I18N.mrs("dw4.importo") + "' ");
			result = true;
		}
		
		// controllo sul campo tipo documento
		if (getExtraFTRP().getTipo_documento() == null || getExtraFTRP().getTipo_documento().length() == 0) {
			this.setErrorMessage("templateForm:tipoDocumentoDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.tipodocumento") + "'");
			result = true;
		}
				
		return result;
	}

}
