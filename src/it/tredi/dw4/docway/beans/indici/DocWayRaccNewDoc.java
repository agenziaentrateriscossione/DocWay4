package it.tredi.dw4.docway.beans.indici;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.adapters.DocWayRaccIndiceFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayDocedit;
import it.tredi.dw4.docway.beans.DocWayLoadingbar;
import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.beans.ShowdocRaccoglitore;
import it.tredi.dw4.docway.model.indici.NewDocInRacc;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLDocumento;

/**
 * Creazione di uno o più nuovi documenti da interfaccia di visualizzazione di un raccoglitore di tipo indice (widget voci dell'indice)
 * @author mbernardini
 */
public class DocWayRaccNewDoc extends DocWayDocedit {

	private DocWayRaccIndiceFormsAdapter formsAdapter;

	private NewDocInRacc doc;
	private Showdoc containerShowdoc;
	private boolean active = false;

	private String xml;

	// dati per inserimenti multipli
	private boolean multiple;
	private List<NewDocInRacc> newDocs = new ArrayList<NewDocInRacc>();
	private int numDocsToInsert = 1;

	private boolean scrollIntoSelected = false;

	public DocWayRaccNewDoc() throws Exception {
		this.formsAdapter = new DocWayRaccIndiceFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public NewDocInRacc getDoc() {
		return doc;
	}

	public void setDoc(NewDocInRacc doc) {
		this.doc = doc;
	}

	public Showdoc getContainerShowdoc() {
		return containerShowdoc;
	}

	public void setContainerShowdoc(Showdoc containerShowdoc) {
		this.containerShowdoc = containerShowdoc;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public List<NewDocInRacc> getNewDocs() {
		return newDocs;
	}

	public void setNewDocs(List<NewDocInRacc> newDocs) {
		this.newDocs = newDocs;
	}

	public int getNumDocsToInsert() {
		return numDocsToInsert;
	}

	public void setNumDocsToInsert(int numDocsToInsert) {
		this.numDocsToInsert = numDocsToInsert;
	}

	public boolean isScrollIntoSelected() {
		return scrollIntoSelected;
	}

	public void setScrollIntoSelected(boolean scrollIntoSelected) {
		this.scrollIntoSelected = scrollIntoSelected;
	}

	@Override
	public void init(Document dom) {
		doc = new NewDocInRacc(dom);
		newDocs.add(new NewDocInRacc());
		active = true;
	}

	@Override
	public DocWayRaccIndiceFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;

			// logica inserimento doc multipli
			if (this.multiple) {
				this.formsAdapter.getDefaultForm().addParams(getAllNewDocsAsFormAdapterParams());
				this.formsAdapter.addAllNewDocsInRaccoglitore(this.numDocsToInsert);
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

				String verbo = response.getAttributeValue("/response/@verbo");
				if (verbo.equals("loadingbar")) { // caricamento della loadingbar

					DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
					docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
					docWayLoadingbar.init(response);
					containerShowdoc.setLoadingbar(docWayLoadingbar);
					docWayLoadingbar.setActive(true);
					((ShowdocRaccoglitore) containerShowdoc).setAction("saveMultipleNewDocs");
				}

				this.active = false;
				return null;
			}
			// vecchia logica
			else {
				this.formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams(""));
				this.formsAdapter.addNewDocInRaccoglitore();
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}

				// in caso di successo viene chiuso il popup modale e chiamato il reload della pagina contenitore
				this.active = false;
				if (containerShowdoc != null) {
					containerShowdoc._reloadWithoutNavigationRule();
					// viene chiamata anche la visualizzazione del doc appena inserito
					if (response != null) {
						String nrecordNewDoc = response.getAttributeValue("response/@previewByNrecord");
						if (nrecordNewDoc != null && !nrecordNewDoc.isEmpty())
							((ShowdocRaccoglitoreINDICE) containerShowdoc).previewDoc(nrecordNewDoc);
					}
				}

				//containerShowdoc.reload();
				return null;
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Trasformazione della lista di NewDocInRacc in formAdapterParams (ordinata)
	 * @return
	 */
	private Map<String, String> getAllNewDocsAsFormAdapterParams() {
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (newDocs != null && newDocs.size() > 0) {
			for (int i = 0; i < newDocs.size(); i++) {
				NewDocInRacc newDoc = newDocs.get(i);
				if (newDoc.getOggetto() == null || newDoc.getOggetto().isEmpty())
					newDoc.setOggetto(I18N.mrs("dw4.titolo_da_modificare"));
				newDoc.setTipoDocSelected(this.doc.getTipoDocSelected());
				params.putAll(newDoc.asFormAdapterParams("newDocs["+i+"]."));
			}
		}
		return params;
	}

	/**
	 * Chiusura del popup (modale) di creazione rapida del documento
	 * @return
	 * @throws Exception
	 */
	@Override
	public String clearDocument() throws Exception {
		this.active = false;
		return null;
	}

	/**
	 * Controllo dei campo obbligatori
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = false;

		// controllo su campo oggetto
		if ((getDoc().getOggetto() == null || getDoc().getOggetto().isEmpty()) && !this.multiple) {
			this.setErrorMessage("templateForm:newDocInRacc_oggetto", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.object") + "'");
			result = true;
		}

		// controllo su campo tipoDoc (dovrebbe sempre essere valorizzato)
		if (getDoc().getTipoDocSelected() == null || getDoc().getTipoDocSelected().isEmpty()) {
			this.setErrorMessage("templateForm:newDocInRacc_tipoDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.type") + "'");
			result = true;
		}

		// controllo su allegati da importare (se checkato)
		if (getDoc().isImportAllegati() && (getDoc().getOggetto_lookup() == null && !this.multiple
				|| getDoc().getOggetto_lookup().isEmpty()
				|| getDoc().getNrecord_lookup() == null
				|| getDoc().getNrecord_lookup().isEmpty())) {
			this.setErrorMessage("templateForm:copyFromDoc", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.documento_importa_allegati") + "'");
			result = true;
		}

		return result;
	}

	/**
	 * Apre il pop-up di ricerca dei documenti
	 *
	 * @throws Exception
	 */
	public String searchDocumento() throws Exception {
		try {
			getFormsAdapter().searchDocumento();
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			if (handleErrorResponse(response)) {
				return null;
			}

			//istanzia il bean della pagina di ricerca dei fascicoli
			QueryGlobale queryGlobale = new QueryGlobale();
			//riempi il formsAdapter della pagina di destinazione
			queryGlobale.getFormsAdapter().fillFormsFromResponse(response);
			queryGlobale.init(response.getDocument());
			queryGlobale.setPopupPage(true);
			setSessionAttribute("queryGlobale", queryGlobale);

			return "query@globale";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Aggiorna la lunghezza dell'ArrayList dei nuovi documenti da inserire
	 * @return
	 */
	public String updateFormsNumber() {
		// controlla che il numero inserito sia valido
		if (checkNumberNewDocs()) return null;

		// aggiornamento meno invasivo possibile (senza azzeramento dei valori temporanei)
		int difference = this.numDocsToInsert - this.newDocs.size();

		if (difference == 0) return null;

		// aggiunta elementi in fondo
		if (difference > 0) {
			for (int i = 0; i < difference; i++) {
				this.newDocs.add(new NewDocInRacc());
			}
		}
		// rimozione elementi dalla coda
		else {
			this.newDocs.subList(this.newDocs.size() + difference, this.newDocs.size()).clear();
		}
		return null;

	}

	/**
	 * Controlla se il numero dei nuovi documenti inserito dall'utente sia un numero valido, in caso contrario fornisce
	 * un errore di validazione
	 * @return
	 */
	private boolean checkNumberNewDocs() {
		boolean result = false;
		// da 1 a 99
		if (this.numDocsToInsert < 1 || this.numDocsToInsert > 99) {
			this.setErrorMessage("templateForm:newDocInRacc_numDocsToInsert", I18N.mrs("dw4.numero_non_valido") + " '" + I18N.mrs("dw4.numero_documenti") + "'");
			result = true;
		}

		return result;
	}

	/**
	 * Rimuove il documento da inserire selezionato dalla lista dei nuovi documenti da inserire e decremento il numero
	 * dei documenti da inserire, a meno che non sia rimasto un solo elemento, in questo caso lo reinizializzo.
	 * @return
	 */
	public String deleteNewDoc() {
		NewDocInRacc doc = (NewDocInRacc) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
		if (doc != null) {
			this.newDocs.remove(doc);
			if (numDocsToInsert > 1) {
				this.numDocsToInsert--;
			}
			else {
				this.newDocs.add(new NewDocInRacc());
			}
		}
		return null;
	}
	
	@Override
	public XmlEntity getModel() {
		return this.doc;
	}
}
