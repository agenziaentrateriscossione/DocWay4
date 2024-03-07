package it.tredi.dw4.docway.beans.equitalia;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyPartenza;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.equitalia.ExtraDCP;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditModifyPartenzaDCPP extends DocEditModifyPartenza {

	// campi specifici del repertori documento del ciclo passivo di equitalia
	private ExtraDCP extraDCP = new ExtraDCP();
	private List<Option> tipologiaDocumentazioneSelect = new ArrayList<Option>();
	
	public DocEditModifyPartenzaDCPP() throws Exception {
		super();
	}
	
	public boolean isDocEditModify() {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		super.init(dom);
		extraDCP.init(XMLUtil.createDocument(dom, "/response/doc/extra"));
		tipologiaDocumentazioneSelect = XMLUtil.parseSetOfElement(dom, "/response/select_tipologiaDocumentazione/option", new Option());
	}
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			ShowdocPartenzaDCPP showdocPartenzaDCPP = new ShowdocPartenzaDCPP();
			showdocPartenzaDCPP.getFormsAdapter().fillFormsFromResponse(response);
			showdocPartenzaDCPP.init(response.getDocument());
			showdocPartenzaDCPP.loadAspects("@partenza", response, "showdoc");
			setSessionAttribute("showdocPartenzaDCPP", showdocPartenzaDCPP);
			
			return "equitalia_showdoc@partenza@DCPP@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
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
			formsAdapter.getDefaultForm().addParams(extraDCP.asFormAdapterParams(".extra"));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocPartenzaDCPP showdocPartenzaDCPP = new ShowdocPartenzaDCPP();
			showdocPartenzaDCPP.getFormsAdapter().fillFormsFromResponse(response);
			showdocPartenzaDCPP.init(response.getDocument());
			showdocPartenzaDCPP.loadAspects("@partenza", response, "showdoc");
			setSessionAttribute("showdocPartenzaDCPP", showdocPartenzaDCPP);
			
			return "equitalia_showdoc@partenza@DCPP@reload";
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
		boolean result = super.checkRequiredField(true);
		
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
