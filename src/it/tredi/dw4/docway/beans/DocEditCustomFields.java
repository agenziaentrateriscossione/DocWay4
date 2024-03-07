package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;

/**
 * Pagina di modifica dei campi custom associati ad un documento
 */
public class DocEditCustomFields extends DocWayDocedit {
	
	private static final String DEFAULT_PNCE = "list_of_doc";

	private DocDocWayDocEditFormsAdapter formsAdapter;
	private String xml = "";
	private boolean visible = false;
	
	// bean di Showdoc per il quale e' stato richiesto l'aggiornamento dei campi custom
	private ShowdocDoc showdoc;
	
	private String pne; // nome dell'elemento root del documento XML al quale sono agganciati i campi custom
	private String pnce;
	
	public DocEditCustomFields() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		setErrorFieldIds(""); // Azzero i campi di error per la scheda corrente
		this.xml = dom.asXML();
		
		// caricamento di tutti gli altri parametri necessari al salvataggio dei campi custom sul documento
		this.pne = dom.getRootElement().attributeValue("pne", "");
		this.pnce = dom.getRootElement().attributeValue("pnce", "");
		if (this.pnce.isEmpty())
			this.pnce = DEFAULT_PNCE;
		
		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(dom, pne);
	}

	@Override
	public DocEditFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			// controllo su campi obbligatori
			if (checkRequiredCustomFields()) return null;

			// caricamento dei custom field sul formsAdapter non necessario, azione gia' inclusa in _saveDocument();
			//formsAdapter.getDefaultForm().addParams(getCustomfields().asFormAdapterParams(""));
			
			// parametro necessario per permettere l'aggiornamento dei campi custom su un archivio aperto in modalita' readonly
			formsAdapter.getDefaultForm().addParam("customFieldsOnly", true);
			
			XMLDocumento response = super._saveDocument(pne, pnce);
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			// refresh della vista sul raccoglitore di tipo indice
			refreshShowdoc();

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Controllo dei valori obbligatori su campi custom del documento
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredCustomFields() {
		setErrorFieldIds(""); // Azzero i campi di error per la scheda corrente

		// in caso di salvataggio occorre impostare a false il reset dei jobs di iwx
		// perche' si potrebbe tornare alla maschera di inserimento/modifica (in caso ad esempio di
		// campi obbligatori non compilati)
		setResetJobsIWX(false);

		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione

		// validazione dei campi custom caricati nella pagina
		return getCustomfields().checkRequiredFields(true, formatoData, this);
	}
	
	/**
	 * Refresh della pagina di visualizzazione del documento
	 * @throws Exception
	 */
	private void refreshShowdoc() throws Exception {
		if (showdoc != null)
			showdoc.reload();
		setVisible(false);
	}

	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			setVisible(false);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String getXml() {
		return this.xml;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public ShowdocDoc getShowdoc() {
		return showdoc;
	}

	public void setShowdoc(ShowdocDoc showdoc) {
		this.showdoc = showdoc;
	}
	
	@Override
	public XmlEntity getModel() {
		return null;
	}

}
