package it.tredi.dw4.docway.beans.indici;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditDoc;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Oggetto;
import it.tredi.dw4.docway.model.indici.PreviewDoc;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLDocumento;

/**
 * Inserimento/Modifica di documenti da interfaccia di visualizzazione di un raccoglitore di tipo
 * indice (edit di solo alcuni campi relativi al documento - es. allegati, campi custom)
 * @author mbernardini
 */
public class DocEditPreviewDoc extends DocEditDoc {

	private PreviewDoc doc = null;
	private String statoDocInRacc = "";
	private boolean visible = false;
	private ShowdocRaccoglitoreINDICE showdoc = null;
	private boolean showOggettoStampa = false;

	// eventuale URL esterno da caricare in una nuova scheda all'apertura della modifica di un documento
	private String urlToLoad;

	public DocEditPreviewDoc() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document domDocumento) {
		this.doc = new PreviewDoc();
		this.doc.init(domDocumento);

		// tiommi : introdotto anche in docEditPreview
		// TODO spacca niente?
		cleanOggettoDocInRaccIndice();

		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(domDocumento);

		// mbernardini 17/01/2017 : stato "in lavorazione" per tutti i documenti del raccoglitore di tipo indice
		// se lo stato di lavoro del documento sull'indice non e' settato lo imposto a "in lavorazione"
		if (this.doc.getStatoRaccIndice() == null || this.doc.getStatoRaccIndice().isEmpty())
			this.doc.setStatoRaccIndice("lavorazione");

		setVisible(true);
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
	public PreviewDoc getDoc() {
		return this.doc;
	}

	public ShowdocRaccoglitoreINDICE getShowdoc() {
		return showdoc;
	}

	public void setShowdoc(ShowdocRaccoglitoreINDICE showdoc) {
		this.showdoc = showdoc;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getStatoDocInRacc() {
		return statoDocInRacc;
	}

	public void setStatoDocInRacc(String statoDocInRacc) {
		this.statoDocInRacc = statoDocInRacc;
	}

	public String getUrlToLoad() {
		if (urlToLoad == null)
			return "";
		else
			return urlToLoad;
	}

	public void setUrlToLoad(String urlToLoad) {
		this.urlToLoad = urlToLoad;
	}

	public boolean isShowOggettoStampa() {
		return showOggettoStampa;
	}

	public void setShowOggettoStampa(boolean showOggettoStampa) {
		this.showOggettoStampa = showOggettoStampa;
	}

	@Override
	public boolean isDocEditModify() {
		if (this.doc.getNrecord() != null && !this.doc.getNrecord().isEmpty() && !this.doc.getNrecord().equals("."))
			return true;
		else
			return false;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (super.checkRequiredFieldCommon(isDocEditModify())) return null;

			// personalizzazione del salvataggio per il repertorio
			boolean isRepertorio = false;
			if (doceditRep)
				isRepertorio = true;

			formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams("", isDocEditModify(), isRepertorio));
			if (!formsAdapter.checkBooleanFunzionalitaDisponibile("show_customSelect1", false)) {
				// se i campi custom di tipo select non sono attivati, rimuovo dal defaultForm i
				// parametri per essere sicuro di non inviarli
				formsAdapter.getDefaultForm().removeParam(".extra.customSelect1");
				formsAdapter.getDefaultForm().removeParam(".extra.customSelect2");
			}

			// TODO se sono stati caricati dei file si potrebbe aggiornare lo 'statoRaccIndice' del documento

			XMLDocumento response = super._saveDocument("doc", "list_of_doc");

			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			// refresh della vista sul raccoglitore di tipo indice
			refreshViewRaccIndice();

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public String saveDocumentPerStampa() throws Exception {
		this.doc.setStatoRaccIndice("completato");
		return saveDocument();
	}

	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

//			// refresh della vista sul raccoglitore di tipo indice
//			refreshViewRaccIndice();
			setVisible(false);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Refresh della vista sul raccoglitore di tipo indice
	 * @throws Exception
	 */
	private void refreshViewRaccIndice() throws Exception {
		if (showdoc != null) {
			if (isDocEditModify()) {
				showdoc.previewDoc(doc.getNrecord());

				// verifico se occorre aggiornare lo stato della voce dell'indice in base allo stato
				// di lavorazione del documento
				if (doc.getStatoRaccIndice() != null && !doc.getStatoRaccIndice().isEmpty()) {
					Oggetto voce = null;
					int i = 0;
					while (voce == null && i < showdoc.getRaccoglitore().getRif_contenuto().size()) {
						Oggetto temp = showdoc.getRaccoglitore().getRif_contenuto().get(i);
						if (temp != null && temp.getCod().equals(doc.getNrecord()))
							voce = temp;
						i++;
					}
					if (voce != null) {
						if (doc.getStatoRaccIndice().equals("completato"))
							voce.setState("ready");
						else
							voce.setState(""); // TODO corretto?
						// tiommi 14/06/2017 aggiorno anche l'oggetto se cambiato
						voce.setText(doc.getOggetto());
					}
				}
			}
			showdoc.refreshChanges();
		}
		setVisible(false);
	}
	
	@Override
	public XmlEntity getModel() {
		return this.doc;
	}

}
