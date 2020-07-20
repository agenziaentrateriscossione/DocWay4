package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Raccoglitore;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;

import org.dom4j.Document;

public class DocEditModifyRaccoglitore extends DocWayDocedit {
	private DocDocWayDocEditFormsAdapter formsAdapter;
	private Raccoglitore raccoglitore = new Raccoglitore();
	
	private String xml = "";

	public DocEditModifyRaccoglitore() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public Raccoglitore getRaccoglitore() {
		return raccoglitore;
	}

	public void setRaccoglitore(Raccoglitore raccoglitore) {
		this.raccoglitore = raccoglitore;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public void init(Document domDocumento) {
		xml = domDocumento.asXML();
		this.raccoglitore = new Raccoglitore();
		this.raccoglitore.init(domDocumento);
		
		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(domDocumento, "raccoglitore");
    }
	
	public DocDocWayDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			formsAdapter.getDefaultForm().addParams(this.raccoglitore.asFormAdapterParams(""));
			XMLDocumento response = super._saveDocument(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, response);		
			return "showdoc@raccoglitore@reload";
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
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, response);
			return "showdoc@raccoglitore@reload";
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
	public boolean checkRequiredField(){
		boolean result = false;
		
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		
		// validazione dei campi custom caricati nella pagina
		result = getCustomfields().checkRequiredFields(true, formatoData, this);
		
		// Controllo se il campo 'oggetto' e' valorizzato
		if (raccoglitore.getOggetto() == null || "".equals(raccoglitore.getOggetto().trim())) {
			this.setErrorMessage("templateForm:racc_oggetto", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.object") + "'");
		    result = true;
		}
		else {
			// Controllo sulla presenza di caratteri non consentiti all'interno del campo oggetto
			if (raccoglitore.getOggetto().contains("|")) { // TODO gestire i caratteri non consentiti da file di properties
				this.setErrorMessage("templateForm:racc_oggetto", I18N.mrs("dw4.il_campo_oggetto_contiene_un_carattere_non_valido") + ": | ");
				result = true;
			}
		}
		
		return result;
	}
	
}
