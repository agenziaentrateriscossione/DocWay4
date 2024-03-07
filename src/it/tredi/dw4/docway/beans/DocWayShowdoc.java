package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Contenuto_in;
import it.tredi.dw4.docway.model.Link_interno;
import it.tredi.dw4.docway.model.Rif;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.dom4j.Document;

public abstract class DocWayShowdoc extends Showdoc {

	public abstract void init(Document dom);

	public abstract DocDocWayDocumentFormsAdapter getFormsAdapter();

	public abstract String paginaTitoli() throws Exception;

	public abstract void reload() throws Exception;

	public abstract String modifyTableDoc() throws Exception;

	// modalita' di visualizzazione dei CC del documento
	private String raggruppaCC_statoIniziale = "";
	private String raggruppaCC_statoCorrente = "";
	private boolean showCCfasc = false;
	private boolean showCCfascMinuta = false;
	private String raggruppaCC_fasc_statoCorrente = "";
	private String raggruppaCC_ufficio_corrente = "";
	private String raggruppaCC_uffici_aperti = "";
	private String raggruppaCC_fasc_uffici_aperti = "";
	private String raggruppaCC_fasc_minuta_statoCorrente = "";
	private String raggruppaCC_fasc_minuta_uffici_aperti = "";

	// mbernardini 01/07/2016 : gestione cc raccoglitori
	private boolean showCCracc = false;
	private String raggruppaCC_racc_statoCorrente = "";
	private String raggruppaCC_racc_uffici_aperti = "";

	// mbernardini 11/11/2016 : gestione allegati generati tramite template (doc2attachment)
	private String doc2attachment = null;

	public String getRaggruppaCC_statoIniziale() {
		return raggruppaCC_statoIniziale;
	}

	public void setRaggruppaCC_statoIniziale(String raggruppaCC_statoIniziale) {
		this.raggruppaCC_statoIniziale = raggruppaCC_statoIniziale;
	}

	public String getRaggruppaCC_statoCorrente() {
		return raggruppaCC_statoCorrente;
	}

	public void setRaggruppaCC_statoCorrente(String raggruppaCC_statoCorrente) {
		this.raggruppaCC_statoCorrente = raggruppaCC_statoCorrente;
	}

	public boolean isShowCCfasc() {
		return showCCfasc;
	}

	public void setShowCCfasc(boolean showCCfasc) {
		this.showCCfasc = showCCfasc;
	}

	public boolean isShowCCfascMinuta() {
		return showCCfascMinuta;
	}

	public void setShowCCfascMinuta(boolean showCCfascMinuta) {
		this.showCCfascMinuta = showCCfascMinuta;
	}

	public String getRaggruppaCC_fasc_statoCorrente() {
		return raggruppaCC_fasc_statoCorrente;
	}

	public void setRaggruppaCC_fasc_statoCorrente(
			String raggruppaCC_fasc_statoCorrente) {
		this.raggruppaCC_fasc_statoCorrente = raggruppaCC_fasc_statoCorrente;
	}

	public String getRaggruppaCC_ufficio_corrente() {
		return raggruppaCC_ufficio_corrente;
	}

	public void setRaggruppaCC_ufficio_corrente(String raggruppaCC_ufficio_corrente) {
		this.raggruppaCC_ufficio_corrente = raggruppaCC_ufficio_corrente;
	}

	public String getRaggruppaCC_uffici_aperti() {
		return raggruppaCC_uffici_aperti;
	}

	public void setRaggruppaCC_uffici_aperti(String raggruppaCC_uffici_aperti) {
		this.raggruppaCC_uffici_aperti = raggruppaCC_uffici_aperti;
	}

	public String getRaggruppaCC_fasc_uffici_aperti() {
		return raggruppaCC_fasc_uffici_aperti;
	}

	public void setRaggruppaCC_fasc_uffici_aperti(
			String raggruppaCC_fasc_uffici_aperti) {
		this.raggruppaCC_fasc_uffici_aperti = raggruppaCC_fasc_uffici_aperti;
	}

	public String getRaggruppaCC_fasc_minuta_statoCorrente() {
		return raggruppaCC_fasc_minuta_statoCorrente;
	}

	public void setRaggruppaCC_fasc_minuta_statoCorrente(
			String raggruppaCC_fasc_minuta_statoCorrente) {
		this.raggruppaCC_fasc_minuta_statoCorrente = raggruppaCC_fasc_minuta_statoCorrente;
	}

	public String getRaggruppaCC_fasc_minuta_uffici_aperti() {
		return raggruppaCC_fasc_minuta_uffici_aperti;
	}

	public void setRaggruppaCC_fasc_minuta_uffici_aperti(
			String raggruppaCC_fasc_minuta_uffici_aperti) {
		this.raggruppaCC_fasc_minuta_uffici_aperti = raggruppaCC_fasc_minuta_uffici_aperti;
	}

	public boolean isShowCCracc() {
		return showCCracc;
	}

	public void setShowCCracc(boolean showCCracc) {
		this.showCCracc = showCCracc;
	}

	public String getRaggruppaCC_racc_statoCorrente() {
		return raggruppaCC_racc_statoCorrente;
	}

	public void setRaggruppaCC_racc_statoCorrente(String raggruppaCC_racc_statoCorrente) {
		this.raggruppaCC_racc_statoCorrente = raggruppaCC_racc_statoCorrente;
	}

	public String getRaggruppaCC_racc_uffici_aperti() {
		return raggruppaCC_racc_uffici_aperti;
	}

	public void setRaggruppaCC_racc_uffici_aperti(String raggruppaCC_racc_uffici_aperti) {
		this.raggruppaCC_racc_uffici_aperti = raggruppaCC_racc_uffici_aperti;
	}

	public String getDoc2attachment() {
		return doc2attachment;
	}

	public void setDoc2attachment(String doc2attachment) {
		this.doc2attachment = doc2attachment;
	}

	/**
	 * Ritorno alla pagina dei titoli
	 * @param template template xhtml da caricare per la presentazione dei titoli
	 *
	 * @throws Exception
	 */
	public String _paginaTitoli(String template) throws Exception {
		try{
			XMLDocumento response = this._paginaTitoli();
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			DocWayTitles titles = new DocWayTitles();
			titles.getFormsAdapter().fillFormsFromResponse(response);
			titles.init(response.getDocument());
			titles.setPopupPage(isPopupPage());
			setSessionAttribute("docwayTitles", titles);
			return template;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Inserimento di un documento/fascicolo all'interno di un raccoglitore (apertura
	 * del popup di ricerca dei raccoglitori)
	 *
	 * @throws Exception
	 */
	public String insInRaccoglitore() throws Exception {
		try {
			getFormsAdapter().insInRaccoglitore(getFormsAdapter().getDefaultForm().getParam("physDoc"));
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			//istanzia il bean della pagina di ricerca dei fascicoli
			QueryRaccoglitore queryRaccoglitore = new QueryRaccoglitore();
			//riempi il formsAdapter della pagina di destinazione
			queryRaccoglitore.getFormsAdapter().fillFormsFromResponse(response);
			queryRaccoglitore.init(response.getDocument());
			queryRaccoglitore.setPopupPage(true);
			setSessionAttribute("queryRaccoglitore", queryRaccoglitore);

			return "query@raccoglitore";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Copia il collegamento alla risorsa (documento/fascicolo)
	 *
	 * @throws Exception
	 */
	public String copyLink(String nrecord) throws Exception{
		try {
			getFormsAdapter().copyLink(nrecord);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			}

			// TODO e' corretto questo modo di gestire il copia/incolla di documenti?
			// set session parameter x propagazione su tutti i forms adapter
			getUserBean().setForcedServiceFormParam(FormsAdapter.customTupleName, FormsAdapter.setParameterFromCustomTupleValue("copyLink", nrecord, getFormsAdapter().getDefaultForm().getParam(FormsAdapter.customTupleName)));

			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form

			//getFormsAdapter().fillFormsFromResponse(response);
			//this.init(response.getDocument());

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Incolla un collegamento precedentemente copiato
	 *
	 * @throws Exception
	 */
	public String pasteLink() throws Exception{
		try {
			getFormsAdapter().pasteLink();
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			}

			// TODO e' corretto questo modo di gestire il copia/incolla di documenti?
			// remove session parameter x propagazione su tutti i forms adapter
			getUserBean().setForcedServiceFormParam(FormsAdapter.customTupleName, null);

			getFormsAdapter().fillFormsFromResponse(response);
			reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Apertura di un link interno (es. ad altro documento collegato)
	 */
	public String navigateInternalLink() throws Exception{
		try{
			Link_interno link_interno = (Link_interno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("link_interno");
			getFormsAdapter().openUrl(link_interno.getHref(), link_interno.getAlias());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Cancellazione di un link interno (link fra risorse)
	 *
	 * @throws Exception
	 */
	public String removeInternalLink() throws Exception{
		try {
			Link_interno link_interno = (Link_interno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("link_interno");
			getFormsAdapter().removeInternalLink(link_interno.getHref());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			}
			getFormsAdapter().fillFormsFromResponse(response);
			reload();

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Apertura della showdoc di un documento anche se si trova in un personalPackage
	 *
	 * @param value
	 * @param aliasName
	 * @param personalPackage
	 * @return
	 * @throws Exception
	 */

	public String doProjection(String value, String aliasName, String personalPackage) throws Exception{
		try{
			getFormsAdapter().openUrl(value, aliasName);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			//dpranteda [27/02/2015] - bugfix nel caso la stessa proposta venga inserita per sbaglio in più sedute
			if(response.isXPathFound("response/titolo")){
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());
				ShowtitlesSeduta titles = new ShowtitlesSeduta();
				titles.getFormsAdapter().fillFormsFromResponse(response);
				titles.init(response.getDocument());
				setSessionAttribute("showtitlesSeduta", titles);
				return "showtitles@seduta";
			}

			if(personalPackage != null && !personalPackage.equals("")){
				return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), "", personalPackage, "", response, isPopupPage());
			}

			return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Modifica lo stato di visualizzazione dei CC del documento
	 *
	 * @return
	 */
	public String hideShowCC() {
		if (raggruppaCC_statoIniziale.equals("compact")) {
			if (raggruppaCC_statoCorrente.equals("")) {
				raggruppaCC_statoCorrente = "expand";
			}
			else if (raggruppaCC_statoCorrente.equals("expand")) {
				raggruppaCC_statoCorrente = "minimized";
			}
			else if (raggruppaCC_statoCorrente.equals("minimized")) {
				raggruppaCC_statoCorrente = "";
				raggruppaCC_uffici_aperti = "";
			}
		}
		else if (raggruppaCC_statoIniziale.equals("minimized")) {
			if (raggruppaCC_statoCorrente.equals("")) {
				raggruppaCC_statoCorrente = "minimized";
			}
			else if (raggruppaCC_statoCorrente.equals("minimized")) {
				raggruppaCC_statoCorrente = "";
				raggruppaCC_uffici_aperti = "";
			}
		}

		return null;
	}

	/**
	 * Visualizza/Nasconde i CC del documento ereditati da un fascicolo
	 * @return
	 */
	public String hideShowCCfromFasc() {
		if (showCCfasc)
			showCCfasc = false;
		else
			showCCfasc = true;
		return null;
	}

	/**
	 * Visualizza/Nasconde i CC del documento ereditati da un fascicolo in minuta
	 * @return
	 */
	public String hideShowCCfromFascMinuta() {
		if (showCCfascMinuta)
			showCCfascMinuta = false;
		else
			showCCfascMinuta = true;
		return null;
	}

	/**
	 * Visualizza/Nasconde i CC del documento ereditati da un raccoglitore
	 * @return
	 */
	public String hideShowCCfromRacc() {
		if (showCCracc)
			showCCracc = false;
		else
			showCCracc = true;
		return null;
	}

	/**
	 * Modifica lo stato di visualizzazione dei CC recuperati dal fascicolo
	 * nel quale e' inserito il documento
	 *
	 * @return
	 */
	public String hideShowCCfasc() {
		if (raggruppaCC_statoIniziale.equals("compact")) {
			if (raggruppaCC_fasc_statoCorrente.equals("")) {
				raggruppaCC_fasc_statoCorrente = "expand";
			}
			else if (raggruppaCC_fasc_statoCorrente.equals("expand")) {
				raggruppaCC_fasc_statoCorrente = "minimized";
			}
			else if (raggruppaCC_fasc_statoCorrente.equals("minimized")) {
				raggruppaCC_fasc_statoCorrente = "";
				raggruppaCC_fasc_uffici_aperti = "";
			}
		}
		else if (raggruppaCC_statoIniziale.equals("minimized")) {
			if (raggruppaCC_fasc_statoCorrente.equals("")) {
				raggruppaCC_fasc_statoCorrente = "minimized";
			}
			else if (raggruppaCC_fasc_statoCorrente.equals("minimized")) {
				raggruppaCC_fasc_statoCorrente = "";
				raggruppaCC_fasc_uffici_aperti = "";
			}
		}

		return null;
	}

	/**
	 * Modifica lo stato di visualizzazione dei CC recuperati dal fascicolo
	 * nel quale e' inserita la minuta
	 *
	 * @return
	 */
	public String hideShowCCfascMinuta() {
		if (raggruppaCC_statoIniziale.equals("compact")) {
			if (raggruppaCC_fasc_minuta_statoCorrente.equals("")) {
				raggruppaCC_fasc_minuta_statoCorrente = "expand";
			}
			else if (raggruppaCC_fasc_statoCorrente.equals("expand")) {
				raggruppaCC_fasc_minuta_statoCorrente = "minimized";
			}
			else if (raggruppaCC_fasc_statoCorrente.equals("minimized")) {
				raggruppaCC_fasc_minuta_statoCorrente = "";
				raggruppaCC_fasc_minuta_uffici_aperti = "";
			}
		}
		else if (raggruppaCC_statoIniziale.equals("minimized")) {
			if (raggruppaCC_fasc_minuta_statoCorrente.equals("")) {
				raggruppaCC_fasc_minuta_statoCorrente = "minimized";
			}
			else if (raggruppaCC_fasc_statoCorrente.equals("minimized")) {
				raggruppaCC_fasc_minuta_statoCorrente = "";
				raggruppaCC_fasc_minuta_uffici_aperti = "";
			}
		}

		return null;
	}

	/**
	 * Modifica lo stato di visualizzazione dei CC recuperati dal raccoglitore
	 * nel quale e' inserito il documento
	 *
	 * @return
	 */
	public String hideShowCCracc() {
		if (raggruppaCC_statoIniziale.equals("compact")) {
			if (raggruppaCC_racc_statoCorrente.equals("")) {
				raggruppaCC_racc_statoCorrente = "expand";
			}
			else if (raggruppaCC_racc_statoCorrente.equals("expand")) {
				raggruppaCC_racc_statoCorrente = "minimized";
			}
			else if (raggruppaCC_racc_statoCorrente.equals("minimized")) {
				raggruppaCC_racc_statoCorrente = "";
				raggruppaCC_racc_uffici_aperti = "";
			}
		}
		else if (raggruppaCC_statoIniziale.equals("minimized")) {
			if (raggruppaCC_racc_statoCorrente.equals("")) {
				raggruppaCC_racc_statoCorrente = "minimized";
			}
			else if (raggruppaCC_fasc_statoCorrente.equals("minimized")) {
				raggruppaCC_racc_statoCorrente = "";
				raggruppaCC_racc_uffici_aperti = "";
			}
		}

		return null;
	}

	/**
	 * Lettura dell'attributo raggruppaCC_ufficio_corrente passato come attributo attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerUfficioCorrente(ActionEvent event){
		raggruppaCC_ufficio_corrente = (String) event.getComponent().getAttributes().get("raggruppaCC_ufficio_corrente");
	}

	/**
	 * Modifica lo stato di visualizzazione dei CC di un ufficio/ruolo
	 * del documento
	 *
	 * @return
	 */
	public String hideShowCCufficio() {
		if (raggruppaCC_ufficio_corrente != null && !raggruppaCC_ufficio_corrente.equals("")) {
			if (raggruppaCC_uffici_aperti.contains(raggruppaCC_ufficio_corrente + ","))
				raggruppaCC_uffici_aperti = raggruppaCC_uffici_aperti.replaceAll(raggruppaCC_ufficio_corrente + ",", "");
			else
				raggruppaCC_uffici_aperti = raggruppaCC_uffici_aperti + raggruppaCC_ufficio_corrente + ",";
		}
		return null;
	}

	/**
	 * Modifica lo stato di visualizzazione dei CC di un ufficio/ruolo
	 * recuperato dal fascicolo
	 *
	 * @return
	 */
	public String hideShowCCufficioFasc() {
		if (raggruppaCC_ufficio_corrente != null && !raggruppaCC_ufficio_corrente.equals("")) {
			if (raggruppaCC_fasc_uffici_aperti.contains(raggruppaCC_ufficio_corrente + ","))
				raggruppaCC_fasc_uffici_aperti = raggruppaCC_fasc_uffici_aperti.replaceAll(raggruppaCC_ufficio_corrente + ",", "");
			else
				raggruppaCC_fasc_uffici_aperti = raggruppaCC_fasc_uffici_aperti + raggruppaCC_ufficio_corrente + ",";
		}
		return null;
	}

	/**
	 * Modifica lo stato di visualizzazione dei CC di un ufficio/ruolo
	 * recuperato dal raccoglitore
	 *
	 * @return
	 */
	public String hideShowCCufficioRacc() {
		if (raggruppaCC_ufficio_corrente != null && !raggruppaCC_ufficio_corrente.equals("")) {
			if (raggruppaCC_racc_uffici_aperti.contains(raggruppaCC_ufficio_corrente + ","))
				raggruppaCC_racc_uffici_aperti = raggruppaCC_racc_uffici_aperti.replaceAll(raggruppaCC_ufficio_corrente + ",", "");
			else
				raggruppaCC_racc_uffici_aperti = raggruppaCC_racc_uffici_aperti + raggruppaCC_ufficio_corrente + ",";
		}
		return null;
	}

	/**
	 * Eliminazione del diritto di intervento ad un rif interno del documento
	 * @return
	 * @throws Exception
	 */
	public String rimuoviIntervento() throws Exception{
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		return rimuoviIntervento(rif);
	}

	/**
	 * Eliminazione del diritto di intervento ad un rif interno del documento
	 * @return
	 * @throws Exception
	 */
	public String rimuoviIntervento(Rif rif) throws Exception{
		try {
			getFormsAdapter().rimuoviIntervento(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			getFormsAdapter().fillFormsFromResponse(response);
			_reloadWithoutNavigationRule();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Assegnazione del diritto di intervento ad un rif interno del documento
	 * @return
	 * @throws Exception
	 */
	public String assegnaIntervento() throws Exception{
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		return assegnaIntervento(rif);
	}

	/**
	 * Assegnazione del diritto di intervento ad un rif interno del documento
	 * @return
	 * @throws Exception
	 */
	public String assegnaIntervento(Rif rif) throws Exception{
		try {
			getFormsAdapter().assegnaIntervento(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			getFormsAdapter().fillFormsFromResponse(response);
			_reloadWithoutNavigationRule();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Scarto del documento per un rif interno
	 * @return
	 * @throws Exception
	 */
	public String scartaDoc() throws Exception{
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		return scartaDoc(rif);
	}

	/**
	 * Scarto del documento per un rif interno
	 * @return
	 * @throws Exception
	 */
	public String scartaDoc(Rif rif) throws Exception{
		try {
			getFormsAdapter().scartaDoc(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			// viene caricato il record successivo al corrente.. per lo scarto delle vaschette
			this.init(response.getDocument());
			getFormsAdapter().fillFormsFromResponse(response);
			_reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Eliminazione del rif interno in CC dal doc/fascicolo
	 * @return
	 * @throws Exception
	 */
	public String removeRifCC() throws Exception{
		try{
			Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
			getFormsAdapter().removeRifInt(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response); //restore delle form
			_reloadWithoutNavigationRule();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Ritorna la tipologia di diritto
	 * @param diritto
	 * @return
	 */
	protected String getType(String diritto){
		String tipo = "";
		if ("RPA".equals(diritto)) tipo = "responsabilita";
		else if ("CC".equals(diritto)) tipo = "assegnazione_cc";
		else if ("ITF".equals(diritto)) tipo = "assegnazione_itf";
		else if ("RPAM".equals(diritto)) tipo = "responsabilita_minuta";
		else if ("CDS".equals(diritto)) tipo = "assegnazione_cds";
		else if ("OP".equals(diritto)) tipo = "assegnazione_op";
		else if ("OPM".equals(diritto)) tipo = "assegnazione_opm";
		return tipo;
	}


	/**
	 * apertura della scheda del raccoglitore che contiene il doc/fascicolo/raccoglitore corrente
	 */
	public String navigateToRac() throws Exception {
		try {
			Contenuto_in contenuto = (Contenuto_in) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("contenuto");
			getFormsAdapter().queryRacc(contenuto.getCod());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificShowdocPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable", ""),response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * cancellazione di un documento.
	 */
	@Override
	public String remove() throws Exception{
		String outpage = super.remove();
		if ("show@acl_home".equals(outpage))
			outpage = "show@docway_home";

		return outpage;
	}

	/**
	 * caricamento dell'elenco di persone associate ad uno specifico ruolo
	 *
	 * @param codRuolo codice del ruolo da verificare
	 * @return
	 * @throws Exception
	 */
	public String showPersoneRuolo(String codRuolo) throws Exception {
		try {
			if (codRuolo != null && codRuolo.length() > 0) {
				getFormsAdapter().showPersoneRuolo(codRuolo);
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}

				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

				DocWayShowPersoneRuolo docwayShowPersoneRuolo = new DocWayShowPersoneRuolo();
				docwayShowPersoneRuolo.setActive(true);
				docwayShowPersoneRuolo.init(response.getDocument());
				setSessionAttribute("docwayShowPersoneRuolo", docwayShowPersoneRuolo);
			}

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
}
