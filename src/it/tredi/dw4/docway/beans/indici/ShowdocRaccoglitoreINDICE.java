package it.tredi.dw4.docway.beans.indici;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.beans.DocWayLoadingbar;
import it.tredi.dw4.docway.beans.ShowdocRaccoglitore;
import it.tredi.dw4.docway.model.Oggetto;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;
import org.dom4j.Document;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Visualizzazione (showdoc) di raccoglitori custom di tipo indice
 */
public class ShowdocRaccoglitoreINDICE extends ShowdocRaccoglitore {

	private static final boolean DEFAULT_REMOVEDOC = true;

	/**
	 * Abilitazione/Disabilitazione del pulsante di applicazione delle modifiche apportate alla lista di
	 * documenti contenuti nel raccoglitore
	 */
	private boolean showApplyModificheContenuto = false;

	/**
	 * Nrecord del documento selezionato (utilizzato dalla procedura di eliminazione di una voce dell'indice)
	 */
	private String nrecordDocSelected;
	/**
	 * Definisce se occorre eliminare definitivamente un documento in fase di rimozione del doc dall'indice o se è sufficiente eliminarlo dal
	 * contenuto del raccoglitore ma mantenerlo all'interno dell'archivio
	 */
	private boolean removeDoc = false;

	/**
	 * Visualizzazione del dialog di cancellazione di un documento
	 */
	private boolean showRemoveDocDialog = false;

	/**
	 * Visualizzazione del dialog di aggregazione di documenti
	 */
	private boolean showAggregaDocsDialog = false;
	private boolean showStampaUfficioRuolo = false;
	private boolean enableOutputDocument = false;

	/**
	 * parametri utilizzati dal drag&drop per lo spostamento delle voci di indice
	 */
	private int destinationIndex = 0;
	private int docIndex = 0;
	private int afterLevel = 0;

	/**
	 * Documento correntemente in preview
	 */
	private String previewedDocNrecord;

	private boolean divFullscreen = false;

	private boolean autoApplyOrderChanges = false;

	private boolean showOggettoStampa = false;

	public ShowdocRaccoglitoreINDICE() throws Exception {
		super();
	}

	public boolean isShowApplyModificheContenuto() {
		return showApplyModificheContenuto;
	}

	public void setShowApplyModificheContenuto(boolean showApplyModificheContenuto) {
		this.showApplyModificheContenuto = showApplyModificheContenuto;
	}

	public boolean isRemoveDoc() {
		return removeDoc;
	}

	public void setRemoveDoc(boolean removeDoc) {
		this.removeDoc = removeDoc;
	}

	public String getNrecordDocSelected() {
		return nrecordDocSelected;
	}

	public void setNrecordDocSelected(String nrecordDocSelected) {
		this.nrecordDocSelected = nrecordDocSelected;
	}

	public boolean isShowRemoveDocDialog() {
		return showRemoveDocDialog;
	}

	public void setShowRemoveDocDialog(boolean showRemoveDocDialog) {
		this.showRemoveDocDialog = showRemoveDocDialog;
	}

	public boolean isShowAggregaDocsDialog() {
		return showAggregaDocsDialog;
	}

	public void setShowAggregaDocsDialog(boolean showAggregaDocsDialog) {
		this.showAggregaDocsDialog = showAggregaDocsDialog;
	}

	public boolean isShowStampaUfficioRuolo() {
		return showStampaUfficioRuolo;
	}

	public void setShowStampaUfficioRuolo(boolean showStampaUfficioRuolo) {
		this.showStampaUfficioRuolo = showStampaUfficioRuolo;
	}

	public boolean isEnableOutputDocument() {
		return enableOutputDocument;
	}

	public void setEnableOutputDocument(boolean enableOutputDocument) {
		this.enableOutputDocument = enableOutputDocument;
	}

	public int getDestinationIndex() {
		return destinationIndex;
	}

	public void setDestinationIndex(int destinationIndex) {
		this.destinationIndex = destinationIndex;
	}

	public int getDocIndex() {
		return docIndex;
	}

	public void setDocIndex(int docIndex) {
		this.docIndex = docIndex;
	}

	public int getAfterLevel() {
		return afterLevel;
	}

	public void setAfterLevel(int afterLevel) {
		this.afterLevel = afterLevel;
	}

	public String getPreviewedDocNrecord() {
		return this.previewedDocNrecord;
	}

	public boolean isDivFullscreen() {
		return divFullscreen;
	}

	public void setDivFullscreen(boolean divFullscreen) {
		this.divFullscreen = divFullscreen;
	}

	public boolean isAutoApplyOrderChanges() {
		return autoApplyOrderChanges;
	}

	public void setAutoApplyOrderChanges(boolean autoApplyOrderChanges) {
		this.autoApplyOrderChanges = autoApplyOrderChanges;
	}

	public boolean isShowOggettoStampa() {
		return showOggettoStampa;
	}

	public void setShowOggettoStampa(boolean showOggettoStampa) {
		this.showOggettoStampa = showOggettoStampa;
	}

	@Override
	public void init(Document dom) {
		// azzeramento dei bean di preview ed edit rapito di un documento
		setSessionAttribute("showdocPreviewDoc", null);
		setSessionAttribute("docEditPreviewDoc", null);

		showApplyModificheContenuto = false;
		resetRemoveDoc();

		// verifica se è abilitata o meno la generazione di un documento di output per il raccoglitore
		// di tipo indice
		if (XMLUtil.parseStrictAttribute(dom, "/response/@raccIndiceOutputDoc", "false").equalsIgnoreCase("true"))
			enableOutputDocument = true;
		else
			enableOutputDocument = false;

		//tiommi 08/06/2017
		// verifica se deve auto applicare le modifiche di ordinamento o chiedere conferma e dare la possibilià di annullare l'operazione
		autoApplyOrderChanges = XMLUtil.parseStrictAttribute(dom, "/response/@raccIndiceAutoApplyOrderChanges", "false").equalsIgnoreCase("true");

		// tiommi 20/02/2019
		// spostata logica di passaggio di visualizzazione dell'oggetto per la stampa
		showOggettoStampa = XMLUtil.parseStrictAttribute(dom, "/response/@useFormattingToolForObject", "false").equalsIgnoreCase("true");

		super.init(dom);
	}

	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}

	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/indici/showdoc@raccoglitore@INDICE");
	}

	@Override
	public Page getShowdocBean() {
		return this;
	}

	/**
	 * Apertura della pagina di modifica del raccoglitore
	 */
	@Override
	public String modifyTableDoc() throws Exception {
		try {
			getFormsAdapter().modifyTableDocRaccIndice(getRaccoglitore().getCodiceRaccoglitoreCustom(), getRaccoglitore().getDescrizioneRaccoglitoreCustom());

			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			return buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Creazione di un nuovo raccoglitore
	 * @return
	 * @throws Exception
	 */
	@Override
	public String nuovoRaccoglitore() throws Exception {
		try {
			getFormsAdapter().insTableDocRaccoglitoreIndice(getRaccoglitore().getCodiceRaccoglitoreCustom(), getRaccoglitore().getDescrizioneRaccoglitoreCustom());

			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Creazione di un nuovo raccoglitore come ripetizione del corrente
	 * @return
	 * @throws Exception
	 */
	@Override
	public String ripetiNuovoRaccoglitore() throws Exception {
		try {
			getFormsAdapter().ripetiNuovoRaccoglitoreIndice("ripetinuovo", getRaccoglitore().getCodiceRaccoglitoreCustom(), getRaccoglitore().getDescrizioneRaccoglitoreCustom());

			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Clonazione del raccoglitore di tipo indice, allegati compresi
	 * @return
	 * @throws Exception
	 */
	public String duplicaRicerca() throws Exception {
		try {
			getFormsAdapter().ripetiNuovoRaccoglitoreIndice("duplicaricerca", getRaccoglitore().getCodiceRaccoglitoreCustom(), getRaccoglitore().getDescrizioneRaccoglitoreCustom());

			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della preview del documento all'interno della pagina del raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String previewDoc() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null)
				previewDoc(doc.getCod());

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	@Override
	public String remove() throws Exception {

		// in caso il raccoglitore ha ancora dei documenti, va avviata una loadingbar per la loro eliminazione
		if (getRaccoglitore().getRif_contenuto() != null && !getRaccoglitore().getRif_contenuto().isEmpty()) {
			getFormsAdapter().startRemoveDocsFromRacc(getRaccoglitore().getNrecord());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) { // caricamento della loadingbar

				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				docWayLoadingbar.setHolderPageBean(this);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);

				setAction("deleteAllDocs");
			}

			return null;
		}
		else {
			return super.remove();
		}
	}

	public String closeDeleteLoadingbar() throws Exception {

		// refresh del raccoglitore e chiusura loadingbar
		this.closeLoadingBar();

		// in caso il raccoglitore ha ancora dei documenti torno null
		if (getRaccoglitore().getRif_contenuto() != null && !getRaccoglitore().getRif_contenuto().isEmpty())
			return null;

		// altrimenti viene rimosso il raccoglitore
		return super.remove();
	}

	/**
	 * Caricamento della preview del documento all'interno della pagina del raccoglitore
	 * @param nrecord Nrecord del documento da caricare in preview
	 * @return
	 * @throws Exception
	 */
	public String previewDoc(String nrecord) throws Exception {
		try {
			if (nrecord != null && !nrecord.isEmpty()) {

				getFormsAdapter().openUrl(nrecord, null);
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}

				// apertura della preview del documento all'interno dell'interfaccia del raccoglitore
				ShowdocPreviewDoc showdocPreviewDoc = new ShowdocPreviewDoc();
				showdocPreviewDoc.init(response.getDocument());
				showdocPreviewDoc.getFormsAdapter().fillFormsFromResponse(response);
				showdocPreviewDoc.setShowdocRaccIndice(this);
				showdocPreviewDoc.setShowOggettoStampa(showOggettoStampa);
				setSessionAttribute("showdocPreviewDoc", showdocPreviewDoc);

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form

				this.previewedDocNrecord = nrecord;
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
	 * Caricamento della pagina di showdoc di un documento contenuto nel raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String loadDoc() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null)
				return loadDoc(doc.getCod());
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di showdoc di un documento contenuto nel raccoglitore
	 * @param nrecordDoc nrecord del documento da caricare
	 * @return
	 * @throws Exception
	 */
	public String loadDoc(String nrecordDoc) throws Exception {
		try {
			if (nrecordDoc != null && !nrecordDoc.isEmpty()) {
				getFormsAdapter().openUrl(nrecordDoc, null);
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
			}
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Spostamento in alto di una voce (documento contenuto nel raccoglitore) dell'indice
	 * viene applicata anche a tutti i documenti figli
	 * @return
	 * @throws Exception
	 */
	public String moveUpVoce() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null) {
				getRaccoglitore().moveUpOggetto(doc);
				if(autoApplyOrderChanges)
					saveOrdineVoci();
				else
					showApplyModificheContenuto = true;
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
	 * Spostamento in basso di una voce (documento contenuto nel raccoglitore) dell'indice
	 * viene applicata anche a tutti i documenti figli
	 * @return
	 * @throws Exception
	 */
	public String moveDownVoce() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null) {
				getRaccoglitore().moveDownOggetto(doc);
				if(autoApplyOrderChanges)
					saveOrdineVoci();
				else
					showApplyModificheContenuto = true;
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
	 * Spostamento in fuori di una voce (documento contenuto nel raccoglitore) dell'indice (diminuisce il livello)
	 * viene applicata anche a tutti i documenti figli
	 * @return
	 * @throws Exception
	 */
	public String moveOutVoce() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null) {
				getRaccoglitore().moveOutOggetto(doc);
				if(autoApplyOrderChanges)
					saveOrdineVoci();
				else
					showApplyModificheContenuto = true;
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
	 * Spostamento dentro di una voce (documento contenuto nel raccoglitore) dell'indice (aumenta il livello)
	 * viene applicata anche a tutti i documenti figli
	 * @return
	 * @throws Exception
	 */
	public String moveInVoce() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null) {
				getRaccoglitore().moveInOggetto(doc);
				if(autoApplyOrderChanges)
					saveOrdineVoci();
				else
					showApplyModificheContenuto = true;
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
	 * Salvataggio del nuovo ordinamento sulle voci (documenti contenuti nel raccoglitore) dell'indice
	 * @return
	 * @throws Exception
	 */
	public String saveOrdineVoci() throws Exception {
		try {
			String contenutoIds = "";
			if (getRaccoglitore().getRif_contenuto() != null) {
				for (int i=0; i<getRaccoglitore().getRif_contenuto().size(); i++) {
					Oggetto contenuto = getRaccoglitore().getRif_contenuto().get(i);
					if (contenuto != null && contenuto.getCod() != null && !contenuto.getCod().isEmpty())
						contenutoIds += contenuto.getCod() + "|" + contenuto.getLevel() + ",";
				}
				if (contenutoIds.endsWith(","))
					contenutoIds = contenutoIds.substring(0, contenutoIds.length()-1);
			}
			getFormsAdapter().ordinaContenutoRaccoglitore(contenutoIds);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			getFormsAdapter().fillFormsFromResponse(response);
			showApplyModificheContenuto = false;
			refreshChanges();

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Aggiunta di una nuova voce (documento) prima di quella selezionata
	 * @return
	 * @throws Exception
	 */
	public String addDocBefore() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null)
				return openInsertDocsModal(doc.getCod()+"|"+doc.getLevel(), false, false, false);
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}


	/**
	 * Aggiunta di nuove voci (documenti) prima di quella selezionata
	 * @return
	 * @throws Exception
	 */
	public String addDocsBefore() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null)
				return openInsertDocsModal(doc.getCod()+"|"+doc.getLevel(), false, false, true);
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Aggiunta di una nuova voce (documento) dopo quella selezionata
	 * @return
	 * @throws Exception
	 */
	public String addDocAfter() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null)
				return openInsertDocsModal(doc.getCod()+"|"+doc.getLevel(), true, false, false);
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Aggiunta di nuove voci (documenti) dopo di quella selezionata
	 * @return
	 * @throws Exception
	 */
	public String addDocsAfter() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null)
				return openInsertDocsModal(doc.getCod()+"|"+doc.getLevel(), true, false, true);
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Aggiunta di una nuova voce (documento) dentro quella selezionata
	 * @return
	 * @throws Exception
	 */
	public String addDocInside() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null && doc.getLevel() < 3)
				return openInsertDocsModal(doc.getCod()+"|"+doc.getLevel(), true, true, false);
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Aggiunta  di nuove voci (documenti) all'interno di quella selezionata
	 * @return
	 * @throws Exception
	 */
	public String addDocsInside() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null && doc.getLevel() < 3)
				return openInsertDocsModal(doc.getCod()+"|"+doc.getLevel(), true, true, true);
			else
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Apertura del popup (modale) di creazione di un nuovo documento all'interno dell'indice di ricerca. Viene indicata la
	 * posizione esatta all'interno del quale inserire il nuovo doc
	 *
	 * @param nrecord Nrecord del documento selezionato dall'operatore
	 * @param after 	true se il nuovo documento deve essere posizionato dopo quello selezionato, false se deve essere posizionato prima
	 * @param inside 	true se il nuovo documento deve essere figlio di quello selezionato
	 * @param multiple 	true se vanno inseriti più documenti
	 * @return
	 * @throws Exception
	 */
	private String openInsertDocsModal(String nrecord, boolean after, boolean inside, boolean multiple) throws Exception {
		try {
			if (nrecord != null && !nrecord.isEmpty()) {

				getFormsAdapter().openNewDoc(nrecord, after, inside);
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

				DocWayRaccNewDoc raccNewDoc = new DocWayRaccNewDoc();
				raccNewDoc.getFormsAdapter().fillFormsFromResponse(response);
				raccNewDoc.init(response.getDocument());
				raccNewDoc.setContainerShowdoc(this);
				raccNewDoc.setMultiple(multiple);
				setSessionAttribute("raccNewDoc", raccNewDoc);
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
	 * Eliminazione di una voce (documento) presente nel raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String removeDoc() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null) {
				this.nrecordDocSelected = doc.getCod();
				this.removeDoc = DEFAULT_REMOVEDOC;
				this.showRemoveDocDialog = true;
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
	 * Conferma di eliminazione di una voce (documento) presente nel raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String confirmRemoveDoc() throws Exception {
		try {
			if (nrecordDocSelected != null && !nrecordDocSelected.isEmpty()) {
				getFormsAdapter().removeDocFromRaccIndice(nrecordDocSelected, removeDoc);
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());
				if (nrecordDocSelected.equals(previewedDocNrecord))
					this.previewedDocNrecord = null;
				resetRemoveDoc();
				refreshChanges();
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
	 * Annullamento dell'operazione di eliminazione di una voce (documento) presente nel raccoglitore
	 * @throws Exception
	 */
	public String cancelRemoveDoc() throws Exception {
		resetRemoveDoc();
		return null;
	}

	/**
	 * Reset dei dati necessari alla funzionalita' di eliminazione di una voce dell'indice (documento)
	 */
	private void resetRemoveDoc() {
		nrecordDocSelected = null;
		removeDoc = DEFAULT_REMOVEDOC;
		showRemoveDocDialog = false;
	}

	/**
	 * Apertura del dialog di aggregazione di documenti contenuti nel raccoglitore di tipo indice
	 */
	public String openAggregaDocs() {
		this.showAggregaDocsDialog = true;
		checkCopertina();
		return null;
	}

	/**
	 * Apertura del dialog di generazione del pdf per la stampa ufficio ruolo
	 */
	public String openStampaUfficioRuolo() {
		this.showStampaUfficioRuolo = true;
		checkCopertina();
		return null;
	}

	/**
	 * Check della Copertina attivo di default e non deselezionabile
	 */
	private void checkCopertina() {
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("copertinaByVoce", false)) {
			// in caso di copertina recuperata da primo nodo dell'indice occorre forzare il check sul nodo
			if (getRaccoglitore().getRif_contenuto() != null && getRaccoglitore().getRif_contenuto().size() > 0) {
				Oggetto copertina = getRaccoglitore().getRif_contenuto().get(0);
				if (copertina != null && copertina.getState() != null && copertina.getState().equals("ready"))
					if (this.showAggregaDocsDialog)
						copertina.setCheck(true);
					else copertina.setCheckUfficioRuolo(true);
			}
		}
	}

	/**
	 * Chiusura del dialog di aggregazione di documenti contenuti nel raccoglitore di tipo indice
	 */
	public String closeAggregaDocs() {
		this.showAggregaDocsDialog = false;
		this.showStampaUfficioRuolo = false;
		return null;
	}

	/**
	 * Avvio della procedura di aggragazione dei documenti per il download dell'aggregato PDF (anteprima)
	 * @return
	 * @throws Exception
	 */
	public String startAggregaDocs() throws Exception {
		if (showAggregaDocsDialog)
			return _startAggregaDocs(getDocsSelezionati(), false);
		if (showStampaUfficioRuolo)
			return _startStampaUfficioRuolo(getDocsSelezionati());
		else return null;
	}

	/**
	 * Avvio della procedura di aggragazione dei documenti con caricamento della loadingbar di avanzamento
	 * @param docs Elenco di nrecord dei documenti da aggregare
	 * @param buildOutputDocument true nel caso debba essere chiuso il raccoglitore e generato il doc. di output, false in caso di anteprima dell'aggregato (solo download)
	 * @return
	 * @throws Exception
	 */
	private String _startAggregaDocs(String docs, boolean buildOutputDocument) throws Exception {
		try {
			if (docs != null && !docs.isEmpty()) {
				getFormsAdapter().startAggregaDocs(docs, buildOutputDocument);
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

				this.showAggregaDocsDialog = false;

				String verbo = response.getAttributeValue("/response/@verbo");
				if (verbo.equals("loadingbar")) { // caricamento della loadingbar

					DocWayLoadingbarMergeDocs docWayLoadingbar = new DocWayLoadingbarMergeDocs();
					docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
					docWayLoadingbar.init(response);
					docWayLoadingbar.setHolderPageBean(this);
					setLoadingbar(docWayLoadingbar);
					docWayLoadingbar.setActive(true);

					setAction("mergeDocs");
				}
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
	 * Avvio della procedura di generazione della stampa per l'ufficio ruolo
	 * @param docs	Documenti selezionati per la stampa
	 * @return
	 * @throws Exception
	 */
	private String _startStampaUfficioRuolo(String docs) throws Exception {
		try {
			if (docs != null && !docs.isEmpty()) {
				getFormsAdapter().startStampaUfficioRuolo(docs);
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

				this.showStampaUfficioRuolo = false;

				String verbo = response.getAttributeValue("/response/@verbo");
				if (verbo.equals("loadingbar")) { // caricamento della loadingbar

					DocWayLoadingbarMergeDocs docWayLoadingbar = new DocWayLoadingbarMergeDocs();
					docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
					docWayLoadingbar.init(response);
					docWayLoadingbar.setHolderPageBean(this);
					setLoadingbar(docWayLoadingbar);
					docWayLoadingbar.setActive(true);

					setAction("stampaUfficioRuolo");
				}
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
	 * Aggregazione di tutti i documenti inclusi nel raccoglitore di tipo indice con generazione del documento
	 * di output e chiusura del raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String generaAggregatoDocs() throws Exception {
		try {
			if (isStoreAggregatoEnabled()) {
				return _startAggregaDocs(getAllDocsRaccoglitore(), true);
			}
			else {
				setErroreResponse(I18N.mrs("dw4.operazione_non_possibile_sul_raccoglitore_corrente"), Const.MSG_LEVEL_ERROR);
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
	 * Ritorna tutti i documenti del raccoglitore selezionati
	 */
	private String getDocsSelezionati() {
		String docs = "";
		if (getRaccoglitore().getRif_contenuto() != null && getRaccoglitore().getRif_contenuto().size() > 0) {
			for (int i=0; i<getRaccoglitore().getRif_contenuto().size(); i++) {
				Oggetto oggetto = getRaccoglitore().getRif_contenuto().get(i);
				if (oggetto != null && (this.showAggregaDocsDialog ? oggetto.isCheck() : oggetto.isCheckUfficioRuolo()))
					docs += oggetto.getCod() + ",";
			}
			if (!docs.isEmpty() && docs.endsWith(","))
				docs = docs.substring(0, docs.length()-1);
		}
		return docs;
	}

	/**
	 * Ritorna tutti i documenti contenuti nel raccoglitore
	 */
	private String getAllDocsRaccoglitore() {
		String docs = "";
		if (getRaccoglitore().getRif_contenuto() != null && getRaccoglitore().getRif_contenuto().size() > 0) {
			for (int i=0; i<getRaccoglitore().getRif_contenuto().size(); i++) {
				Oggetto oggetto = getRaccoglitore().getRif_contenuto().get(i);
				docs += oggetto.getCod() + ",";
			}
			if (!docs.isEmpty() && docs.endsWith(","))
				docs = docs.substring(0, docs.length()-1);
		}
		return docs;
	}

	/**
	 * Ritorna true se è possibile registrare su un nuovo documento l'aggregato dei documementi che compongono l'indice, false altrimenti
	 * @return
	 */
	public boolean isStoreAggregatoEnabled() {
		boolean enabled = false;
		if (enableOutputDocument
				&& getRaccoglitore().getStato() != null && getRaccoglitore().getStato().equals("aperto")) { // documento di output definito a livello di configurazione e raccoglitore aperto

			// verifico che tutti i documenti dell'indice risultino completi
			boolean incomplete = false;
			int i = 0;
			while(!incomplete && i < getRaccoglitore().getRif_contenuto().size()) {
				Oggetto oggetto = getRaccoglitore().getRif_contenuto().get(i);
				if (oggetto != null && (oggetto.getState() == null || !oggetto.getState().equals("ready")))
					incomplete = true;
				i++;
			}
			if (!incomplete)
				enabled = true;
		}
		return enabled;
	}

	/**
	 * Caricamento della copertina del raccoglitore di tipo indice, se e solo se copertina recuperata dal primo nodo del raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String loadCopertina() throws Exception {
		try {
			if (getRaccoglitore().getRif_contenuto() != null && getRaccoglitore().getRif_contenuto().size() > 0) {
				getFormsAdapter().loadCopertina();
				AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());

				String fileName = "copertina.pdf";
				if (attachFile != null && attachFile.getFilename() != null && !attachFile.getFilename().isEmpty())
					fileName = attachFile.getFilename();

				sendAttachByFacesContext(attachFile, fileName);
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
	 * Scaricamento del PDF contenente l'indice del raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String scaricaIndice() throws Exception {
		try {
			if (getRaccoglitore().getRif_contenuto() != null && getRaccoglitore().getRif_contenuto().size() > 0) {
				getFormsAdapter().scaricaIndice();
				AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());

				String fileName = "indice.pdf";
				if (attachFile != null && attachFile.getFilename() != null && !attachFile.getFilename().isEmpty())
					fileName = attachFile.getFilename();

				sendAttachByFacesContext(attachFile, fileName);
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
	 * Aggiornamento della lista degli indici nel raccoglitore a seguito di una modifica effettuata con il plug-in drag-and-drop
	 * @return
	 * @throws Exception
	 */
	public String updateListDragAndDrop() throws Exception {
		try {
			Oggetto doc = getRaccoglitore().getRif_contenuto().get(docIndex);
			if (doc != null) {
				getRaccoglitore().moveToIndex(doc, docIndex, destinationIndex, afterLevel);
				if(autoApplyOrderChanges)
					saveOrdineVoci();
				else
					showApplyModificheContenuto = true;
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
	 * Setta lo stato completato ad un documento del raccoglitore di indice
	 * @return
	 * @throws Exception
	 */
	public String setReady() throws Exception {
		try {
			Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
			if (doc != null && doc.getCod() != null && !doc.getCod().isEmpty()) {
				getFormsAdapter().setReadyDocRaccIndice(doc.getCod());
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

				refreshChanges();
			}
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	public void refreshChanges() throws Exception {
		_reloadWithoutNavigationRule();
		if (previewedDocNrecord != null && !previewedDocNrecord.isEmpty())
			previewDoc(previewedDocNrecord);
	}

	/**
	 * Setta lo stato completato di tutti i documenti del raccoglitore di indice
	 * @return
	 * @throws Exception
	 */
	public String setAllReady() throws Exception {
		try {
			if (this.getRaccoglitore() != null
					&& this.getRaccoglitore().getRif_contenuto() != null
					&& !this.getRaccoglitore().getRif_contenuto().isEmpty()) {
				getFormsAdapter().setAllReadyDocsRaccIndice(this.getRaccoglitore().getNrecord());
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

				String verbo = response.getAttributeValue("/response/@verbo");
				if (verbo.equals("loadingbar")) { // caricamento della loadingbar

					handleLoadingBar(response);
					setAction("setAllDocsReady");
				}
			}
			else {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(
						I18N.mrs("dw4.errore_raccoglitore_indice_setta_tutti_completati"),
						I18N.mrs("dw4.lista_documenti_vuota"),
						ErrormsgFormsAdapter.ERROR));
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
	 * Setta lo stato completato dei documenti marcati come selezionati nel raccoglitore di indice
	 * @return
	 * @throws Exception
	 */
	public String setSelectedReady() throws Exception {
		try {
			if (this.getRaccoglitore() != null
					&& this.getRaccoglitore().getRif_contenuto() != null
					&& !this.getRaccoglitore().getRif_contenuto().isEmpty()) {
				List<Oggetto> selectedDocs = this.getRaccoglitore().getRif_contenuto()
						.stream()
						.filter(Oggetto::isSelected)
						.collect(Collectors.toList());
				if (!selectedDocs.isEmpty()) {
					getFormsAdapter().setSelectedReadyDocsRaccIndice(this.getRaccoglitore().getNrecord(), selectedDocs);
					XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
						return null;
					}

					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

					String verbo = response.getAttributeValue("/response/@verbo");
					if (verbo.equals("loadingbar")) { // caricamento della loadingbar

						handleLoadingBar(response);
						setAction("setSelectedDocsReady");
					}
				}
				else {
					handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(
							I18N.mrs("dw4.errore_raccoglitore_indice_setta_selezionati_completati"),
							I18N.mrs("dw4.nessun_documento_selezionato"),
							ErrormsgFormsAdapter.WARNING));
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				}
			}
			else {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(
						I18N.mrs("dw4.errore_raccoglitore_indice_setta_selezionati_completati"),
						I18N.mrs("dw4.lista_documenti_vuota"),
						ErrormsgFormsAdapter.ERROR));
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

	public String openDivFullscreen() {
		this.divFullscreen = true;
		return null;
	}

	public String closeDivFullscreen() {
		this.divFullscreen = false;
		return null;
	}

	public String closeLoadingBar() throws Exception {
		this.getLoadingbar().setActive(false);
		String nrecordToPreview = ((DocWayLoadingbar) getLoadingbar()).getResoconto().getNrecordToPreview();
		if (nrecordToPreview != null && !nrecordToPreview.isEmpty())
			this.previewedDocNrecord = nrecordToPreview;
		refreshChanges();
		return null;
	}

	/**
	 * Seleziona/deseleziona il documento per la gestione delle operazioni massive.
	 */
	public String toggleDocSelection() {
		Oggetto doc = (Oggetto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("doc");
		if (doc != null) {
			doc.setSelected(!doc.isSelected());
		}
		return null;
	}

	/**
	 * Ripristina lo stato del raccoglitore a non chiuso e rimuove il flag "readOnly" a tutti i suoi documenti
	 * @return
	 */
	public String ripristinaRicerca() throws Exception {
		try {
			getFormsAdapter().ripristinaRicerca(getRaccoglitore().getNrecord());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

			String verbo = response.getAttributeValue("/response/@verbo");

			if (verbo.equals("loadingbar")) { // caricamento della loadingbar

				handleLoadingBar(response);
				setAction("ripristinaReadOnlyDocs");
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
	 * Avvia la procedura per la loadingbar di cancellazione massiva dei documenti selezionati
	 * @return
	 */
	public String deleteSelected() throws Exception {
		try {
			if (this.getRaccoglitore() != null
					&& this.getRaccoglitore().getRif_contenuto() != null
					&& !this.getRaccoglitore().getRif_contenuto().isEmpty()) {
				List<Oggetto> selectedDocs = this.getRaccoglitore().getRif_contenuto()
						.stream()
						.filter(Oggetto::isSelected)
						.collect(Collectors.toList());
				if (!selectedDocs.isEmpty()) {
					getFormsAdapter().deleteSelectedDocsRaccIndice(this.getRaccoglitore().getNrecord(), selectedDocs);
					XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
						return null;
					}

					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());
					String verbo = response.getAttributeValue("/response/@verbo");
					if (verbo.equals("loadingbar")) { // caricamento della loadingbar
						handleLoadingBar(response);
						setAction("deleteSelectedDocs");
					}
				}
				else {
					handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(
							I18N.mrs("dw4.errore_raccoglitore_indice_elimina_selezionati"),
							I18N.mrs("dw4.nessun_documento_selezionato"),
							ErrormsgFormsAdapter.WARNING));
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				}
			}
			else {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(
						I18N.mrs("dw4.errore_raccoglitore_indice_elimina_selezionati"),
						I18N.mrs("dw4.lista_documenti_vuota"),
						ErrormsgFormsAdapter.ERROR));
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
	 * Inizializzazione della loadingbar a partire dalla Response del service
	 * @param response
	 * @throws Exception
	 */
	private void handleLoadingBar(XMLDocumento response) throws Exception {
		DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
		docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
		docWayLoadingbar.init(response);
		setLoadingbar(docWayLoadingbar);
		docWayLoadingbar.setActive(true);
	}


}
