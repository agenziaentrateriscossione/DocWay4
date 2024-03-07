package it.tredi.dw4.docway.beans.indici;

import java.util.List;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.ShowdocDoc;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.indici.ImportWebURL;
import it.tredi.dw4.docway.model.indici.PreviewDoc;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class ShowdocPreviewDoc extends ShowdocDoc {

	private boolean previewVisible = false;
	private ShowdocRaccoglitoreINDICE showdocRaccIndice = null;

	// import da WEB
	private List<ImportWebURL> importUrls;
	private String importUrlSelected;
	private boolean importVisible = false;
	private boolean showOggettoStampa = false;

	public ShowdocPreviewDoc() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public boolean isPreviewVisible() {
		return previewVisible;
	}

	public void setPreviewVisible(boolean visible) {
		this.previewVisible = visible;
	}

	public ShowdocRaccoglitoreINDICE getShowdocRaccIndice() {
		return showdocRaccIndice;
	}

	public void setShowdocRaccIndice(ShowdocRaccoglitoreINDICE showdoc) {
		this.showdocRaccIndice = showdoc;
	}

	public List<ImportWebURL> getImportUrls() {
		return importUrls;
	}

	public void setImportUrls(List<ImportWebURL> importUrls) {
		this.importUrls = importUrls;
	}

	public String getImportUrlSelected() {
		return importUrlSelected;
	}

	public void setImportUrlSelected(String importUrlSelected) {
		this.importUrlSelected = importUrlSelected;
	}

	public boolean isImportVisible() {
		return importVisible;
	}

	public void setImportVisible(boolean importVisible) {
		this.importVisible = importVisible;
	}

	public boolean isShowOggettoStampa() {
		return showOggettoStampa;
	}

	public void setShowOggettoStampa(boolean showOggettoStampa) {
		this.showOggettoStampa = showOggettoStampa;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		this.doc = new PreviewDoc();
		this.doc.init(dom);

		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(dom);

		cleanOggettoDocInRaccIndice();
		setPreviewVisible(true);

		// caricamento degli URL per caricamento contenuto da WEB
		importUrls = XMLUtil.parseSetOfElement(dom, "/response/importWeb/web", new ImportWebURL());
	}

	/**
	 * Ripulisce l'oggetto del documento contenuto in un raccoglitore di tipo indice. Elimina la parte che precede il
	 * carattere # che corrisponde al titolo del raccoglitore all'interno del quale è contenuto il documento
	 */
	private void cleanOggettoDocInRaccIndice() {
		if (doc != null && doc.getOggetto() != null && !doc.getOggetto().isEmpty()) {
			int index = doc.getOggetto().indexOf("#");
			if (index != -1)
				doc.setOggetto(doc.getOggetto().substring(index+1).trim());
		}
	}

	@Override
	public void reload() throws Exception {
		_reloadWithoutNavigationRule(); // TODO corretto?
	}

	/**
	 * Caricamento della pagina di modifica di un documento aperto in preview su un raccoglitore di tipo
	 * indice (apertura di un popup modale)
	 */
	@Override
	public String modifyTableDoc() throws Exception {
		return modifyTableDoc(null);
	}

	/**
	 * Caricamento in edit del documento (modifica rapida tramite popup modale sul raccoglitore) con eventuale apertura di Sicigo2
	 * @param urlToLoad eventuale URL esterno da caricare (come nuova scheda del browser) all'apertura della maschera di modifica del documento
	 * @return
	 * @throws Exception
	 */
	private String modifyTableDoc(String urlToLoad) throws Exception {
		try {
			if (getDoc().getRepertorio() != null && getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0)
				this.formsAdapter.modifyTableDoc(getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
			else
				this.formsAdapter.modifyTableDoc();

			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			// in caso di warnings restituiti dal service (es. verifica impronta), stampo un messaggio a video
			String verbo = response.getAttributeValue("/response/@verbo", "");
			String warnings = response.getAttributeValue("/response/@warnings", "");
			if (!verbo.equals("docEdit") && warnings.length() > 0) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				setErroreResponse(warnings, Const.MSG_LEVEL_ERROR);
			}
			else {
				// apertura del dialog di aggiornamento del contenuto del doc (campi custom)
				DocEditPreviewDoc docEditPreviewDoc = new DocEditPreviewDoc();
				docEditPreviewDoc.init(response.getDocument());
				docEditPreviewDoc.getFormsAdapter().fillFormsFromResponse(response);
				docEditPreviewDoc.setShowdoc(getShowdocRaccIndice());
				docEditPreviewDoc.setShowOggettoStampa(showOggettoStampa);

				if (urlToLoad != null && !urlToLoad.isEmpty())
					docEditPreviewDoc.setUrlToLoad(urlToLoad);

				setSessionAttribute("docEditPreviewDoc", docEditPreviewDoc);

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			}

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Importazione documenti da URL web (caricamento della pagina su nuova scheda del browser).
	 * N.B. Non puo' essere implementato con caricamento dell'HTML da iFrame per problemi di cross-domain.
	 * @return
	 * @throws Exception
	 */
	public String importFromWebURL() throws Exception {
		importVisible = true;
		importUrlSelected = null;
		return null;
	}

	/**
	 * Chiusura del popup (modale) di import da URL web
	 * @return
	 * @throws Exception
	 */
	public String closeImportFromWebURL() throws Exception {
		importVisible = false;
		return null;
	}

	/**
	 * Caricamento in modifica del documento e apertura dell'applicativo Sigico2 per selezione del contenuto da includere. Attualmente realizzato in questa
	 * modalita' per problema di cross-domain (tramite caricamento di pagine HTML) e mancanza di altri strumenti di integrazione (chiamate REST, WS, ecc.)
	 * @return
	 * @throws Exception
	 */
	public String loadFromWebURL() throws Exception {
		closeImportFromWebURL();
		return modifyTableDoc(importUrlSelected);
	}

}
