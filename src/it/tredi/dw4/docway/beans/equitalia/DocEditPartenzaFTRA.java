package it.tredi.dw4.docway.beans.equitalia;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditPartenza;
import it.tredi.dw4.docway.model.equitalia.ExtraFTRA;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditPartenzaFTRA  extends DocEditPartenza  {

	// campi specifici del repertori fatture di equitalia
	private ExtraFTRA extraFTRA = new ExtraFTRA();
	
	public DocEditPartenzaFTRA() throws Exception {
		super();
	}
	
	public boolean isDocEditModify() {
		return false;
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		extraFTRA.init(XMLUtil.createDocument(dom, "/response/doc/extra"));
		
		extraFTRA.setStato_invio_nav("Da inviare");
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", false, true));
			formsAdapter.getDefaultForm().addParams(extraFTRA.asFormAdapterParams(".extra"));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocPartenzaFTRA showdocPartenzaFTRA = new ShowdocPartenzaFTRA();
			showdocPartenzaFTRA.getFormsAdapter().fillFormsFromResponse(response);
			showdocPartenzaFTRA.init(response.getDocument());
			showdocPartenzaFTRA.loadAspects("@partenza", response, "showdoc");
			setSessionAttribute("showdocPartenzaFTRA", showdocPartenzaFTRA);
			
			return "equitalia_showdoc@partenza@FTRA@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public ExtraFTRA getExtraFTRA() {
		return extraFTRA;
	}

	public void setExtraFTRA(ExtraFTRA extraFTR) {
		this.extraFTRA = extraFTR;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = super.checkRequiredField();
		
		// controllo sul campo cliente
		if (getDoc().getRif_esterni().get(0).getNome() == null || getDoc().getRif_esterni().get(0).getNome().length() == 0) {
			this.setErrorMessage("templateForm:docEditDestinatari:0:nomeDestinatario_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.cliente") + "'");
			result = true;
		}
		
		// controllo sul fornitore della fattura
		if (getDoc().getRif_esterni().get(0).getPartita_iva() == null || getDoc().getRif_esterni().get(0).getPartita_iva().length() == 0
				|| getDoc().getRif_esterni().get(0).getCodice_fiscale() == null || getDoc().getRif_esterni().get(0).getCodice_fiscale().length() == 0) {
			this.setErrorMessage("templateForm:docEditDestinatari:0:nomeDestinatario_input", I18N.mrs("dw4.il_campo_cliente_e_privo_di_piva_cf"));
			result = true;
		}
		
		// controllo sul campo numero fattura
		if (getDoc().getRif_esterni().get(0).getN_prot() == null || getDoc().getRif_esterni().get(0).getN_prot().length() == 0) {
			this.setErrorMessage("templateForm:numFatturaDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.numero_fattura") + "'");
			result = true;
		}
		
		// controllo sul campo data fattura
		if (getDoc().getRif_esterni().get(0).getData_prot() == null || getDoc().getRif_esterni().get(0).getData_prot().length() == 0) {
			this.setErrorMessage("templateForm:dataFatturaDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_fattura") + "'");
			result = true;
		}
		else if (!DateUtil.isValidDate(getDoc().getRif_esterni().get(0).getData_prot(), formatoData)) {
			this.setErrorMessage("templateForm:dataFatturaDoc", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_fattura") + "': " + formatoData.toLowerCase());
			result = true;
		}
				
		return result;
	}

}
