package it.tredi.dw4.docway.beans.equitalia;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditArrivo;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.equitalia.ExtraDCP;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditArrivoDCPA extends DocEditArrivo {

	// campi specifici del repertori documento del ciclo passivo di equitalia
	private ExtraDCP extraDCP = new ExtraDCP();
	private List<Option> tipologiaDocumentazioneSelect = new ArrayList<Option>();
	
	public DocEditArrivoDCPA() throws Exception {
		super();
	}
	
	public boolean isDocEditModify() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		super.init(dom);
		extraDCP.init(XMLUtil.createDocument(dom, "/response/doc/extra"));
		tipologiaDocumentazioneSelect = XMLUtil.parseSetOfElement(dom, "/response/select_tipologiaDocumentazione/option", new Option());
		
		extraDCP.setStato_invio_nav("Da inviare");
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
			formsAdapter.getDefaultForm().addParams(extraDCP.asFormAdapterParams(".extra"));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocArrivoDCPA showdocArrivoDCPA = new ShowdocArrivoDCPA();
			showdocArrivoDCPA.getFormsAdapter().fillFormsFromResponse(response);
			showdocArrivoDCPA.init(response.getDocument());
			showdocArrivoDCPA.loadAspects("@arrivo", response, "showdoc");
			setSessionAttribute("showdocArrivoDCPA", showdocArrivoDCPA);
			
			return "equitalia_showdoc@arrivo@DCPA@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public ExtraDCP getExtraDCP() {
		return extraDCP;
	}

	public void setExtraDCP(ExtraDCP extraDCP) {
		this.extraDCP = extraDCP;
	}
	
	public List<Option> getTipologiaDocumentazioneSelect() {
		return tipologiaDocumentazioneSelect;
	}

	public void setTipologiaDocumentazioneSelect(
			List<Option> tipologiaDocumentazioneSelect) {
		this.tipologiaDocumentazioneSelect = tipologiaDocumentazioneSelect;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = super.checkRequiredField();
		
		// controllo sul campo numero nav
		if (getExtraDCP().getNumero_nav() == null || getExtraDCP().getNumero_nav().length() == 0) {
			this.setErrorMessage("templateForm:numeroNavDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.numero_nav") + "'");
			result = true;
		}
		
		// controllo sul campo tipologia
		if (getExtraDCP().getTipologiaDocumentazione() == null || getExtraDCP().getTipologiaDocumentazione().length() == 0) {
			this.setErrorMessage("templateForm:tipologiaDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.tipologia") + "'");
			result = true;
		}
				
		return result;
	}

}
