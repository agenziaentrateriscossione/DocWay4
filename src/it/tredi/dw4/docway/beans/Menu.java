package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.DelegheOptions;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.beans.fatturepa.QueryRegistroFatture;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.DocWayProperties;

import java.util.Calendar;

import javax.faces.event.ActionEvent;

public class Menu extends Page {

	private DocDocWayQueryFormsAdapter formsAdapter;

	private String tipoDoc = ""; // tipologia di documento da caricare

	private String urlPaginaWikiManualeDocWay = ""; // pagina wiki di manuale online per docway

	public Menu() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));

		this.urlPaginaWikiManualeDocWay = DocWayProperties.readProperty("urlPaginaWikiManualeDocWay", "");
	}

	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public String getUrlPaginaWikiManualeDocWay() {
		return urlPaginaWikiManualeDocWay;
	}

	public void setUrlPaginaWikiManualeDocWay(String urlPaginaWikiManualeDocWay) {
		this.urlPaginaWikiManualeDocWay = urlPaginaWikiManualeDocWay;
	}

	public String getAclDb() {
		return formsAdapter.getDefaultForm().getParam("aclDb");
	}

	/**
	 * caricamento della home page
	 * @return
	 * @throws Exception
	 */
	public String loadDocWayMainPage() throws Exception {
		try {
			formsAdapter.loadDocWayMainPage();

			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			DocWayHome docwayHome = new DocWayHome();
			docwayHome.getFormsAdapter().fillFormsFromResponse(response);
			docwayHome.init(response.getDocument());
			setSessionAttribute("docwayHome", docwayHome);

			return "show@docway_home";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * caricamento pagina di ricerca fascicoli
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQFascicolo() throws Exception {
		try {
			formsAdapter.gotoTableQ("fascicolo", false);

			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			return buildSpecificQueryPageAndReturnNavigationRule("@fascicolo", response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * caricamento pagina di ricerca documenti
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQGlobale() throws Exception {
		try {
			// TODO perche' viene recuperato dalla sessione anziche' impostare il form di ricerca vuoto? serve per il refine?
			// per ora commendato
			//QueryGlobale queryGlobale = (QueryGlobale) getSessionAttribute("queryGlobale");
			//if (queryGlobale == null){
				formsAdapter.gotoTableQ("globale", false);
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}

				//istanzia il bean della pagina di ricerca dei fascicoli
				QueryGlobale queryGlobale = new QueryGlobale();
				//riempi il formsAdapter della pagina di destinazione
				queryGlobale.getFormsAdapter().fillFormsFromResponse(response);
				queryGlobale.init(response.getDocument());
				setSessionAttribute("queryGlobale", queryGlobale);
			//}

			return "query@globale";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * caricamento pagina di ricerca protocolli in arrivo (sostituita da globale)
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public String gotoTableQArrivo() throws Exception {
		try {
			formsAdapter.gotoTableQ("arrivo", false);

			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			//istanzia il bean della pagina di ricerca dei fascicoli
			QueryArrivo queryArrivo = new QueryArrivo();
			//riempi il formsAdapter della pagina di destinazione
			queryArrivo.getFormsAdapter().fillFormsFromResponse(response);
			queryArrivo.init(response.getDocument());
			setSessionAttribute("queryArrivo", queryArrivo);

			return "query@arrivo";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * caricamento pagina di ricerca protocolli in partenza (sostituita da globale)
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public String gotoTableQPartenza() throws Exception {
		try {
			formsAdapter.gotoTableQ("partenza", false);

			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			//istanzia il bean della pagina di ricerca dei fascicoli
			QueryPartenza queryPartenza = new QueryPartenza();
			//riempi il formsAdapter della pagina di destinazione
			queryPartenza.getFormsAdapter().fillFormsFromResponse(response);
			queryPartenza.init(response.getDocument());
			setSessionAttribute("queryPartenza", queryPartenza);

			return "query@partenza";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * caricamento pagina di ricerca raccoglitori
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQRaccoglitore() throws Exception {
		try {
			formsAdapter.gotoTableQ("raccoglitore", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			//istanzia il bean della pagina di ricerca dei fascicoli
			QueryRaccoglitore queryRaccoglitore = new QueryRaccoglitore();
			//riempi il formsAdapter della pagina di destinazione
			queryRaccoglitore.getFormsAdapter().fillFormsFromResponse(response);
			queryRaccoglitore.init(response.getDocument());
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
	 * Ricerca di un raccoglitore custom di tipo indice
	 * @param cod Codice del raccoglitore da ricercare
	 * @param descr Descrizione del raccoglitore da ricercare
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQRaccoglitoreIndice(String cod, String descr) throws Exception {
		formsAdapter.gotoTableQRaccoglitoreIndice(cod, descr);
		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		return buildSpecificQueryPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, responseDoc);
	}

	/**
	 * Inserimento di un nuovo fascicolo
	 * @return
	 * @throws Exception
	 */
	public String insTableDocFascicolo() throws Exception {
		try {
			formsAdapter.insTableDoc(Const.DOCWAY_TIPOLOGIA_FASCICOLO);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_FASCICOLO, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Inserimento di un nuovo raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String insTableDocRaccoglitore() throws Exception {
		formsAdapter.insTableDoc(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, responseDoc, this.isPopupPage());
	}

	/**
	 * Inserimento di un nuovo raccoglitore custom di tipo indice
	 * @param cod Codice del raccoglitore da creare
	 * @param descr Descrizione del raccoglitore da creare
	 * @return
	 * @throws Exception
	 */
	public String insTableDocRaccoglitoreIndice(String cod, String descr) throws Exception {
		formsAdapter.insTableDocRaccoglitoreIndice(cod, descr);
		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, responseDoc, this.isPopupPage());
	}

	/**
	 * Inserimento di un nuovo documento in arrivo
	 * @return
	 * @throws Exception
	 */
	public String insTableDocArrivo() throws Exception {
		return insTableDoc(Const.DOCWAY_TIPOLOGIA_ARRIVO);
	}

	/**
	 * Inserimento di un nuovo documento in arrivo differito
	 * @return
	 * @throws Exception
	 */
	public String insTableDocArrivoDifferito() throws Exception {
		try {
			getFormsAdapter().insTableDoc("arrivo_differito");
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response);

			DocEditArrivo docEditArrivo = new DocEditArrivo();
			docEditArrivo.setProtocolloDifferito(true); // protocollo differito
			docEditArrivo.getFormsAdapter().fillFormsFromResponse(response);
			docEditArrivo.init(response.getDocument());

			setSessionAttribute("docEditArrivo", docEditArrivo);

			return "docEdit@arrivo";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Inserimento di un nuovo documento in partenza
	 * @return
	 * @throws Exception
	 */
	public String insTableDocPartenza() throws Exception {
		return insTableDoc(Const.DOCWAY_TIPOLOGIA_PARTENZA);
	}

	/**
	 * Inserimento di un nuovo documento tra uffici (interno)
	 * @return
	 * @throws Exception
	 */
	public String insTableDocInterno() throws Exception {
		return insTableDoc(Const.DOCWAY_TIPOLOGIA_INTERNO);
	}

	/**
	 * Inserimento di un nuovo documento non protocollato (varie)
	 * @return
	 * @throws Exception
	 */
	public String insTableDocVarie() throws Exception {
		return insTableDoc(Const.DOCWAY_TIPOLOGIA_VARIE);
	}

	/**
	 * Inserimento di un nuovo documento di acquisizione immagini
	 * @return
	 * @throws Exception
	 */
	public String insTableDocAcquisizione() throws Exception {
		return insTableDoc(Const.DOCWAY_TIPOLOGIA_ACQUISIZIONE);
	}

	/**
	 * Acquisizione massiva di documenti tramite qrcode (IWX)
	 * @return
	 * @throws Exception
	 */
	public String insTableDocAcquisizioneQrcode() throws Exception {
		insTableDoc(Const.DOCWAY_TIPOLOGIA_ACQUISIZIONE);
		return "docEdit@acquisizioneQrcode";
	}

	/**
	 * Lettura dell'attributo tipoDoc passato come attributo attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerTipoDoc(ActionEvent event){
		this.tipoDoc = (String) event.getComponent().getAttributes().get("tipoDoc");
	}

	/**
	 * Inserimento di un nuovo documento in base a
	 * parametro tipoDoc (passato ad es. come attributo da un commandLink)
	 *
	 * @return
	 * @throws Exception
	 */
	public String insTableDoc() throws Exception {
		if (this.tipoDoc != null && this.tipoDoc.length() > 0)
			return insTableDoc(this.tipoDoc);
		else
			return null;
	}

	/**
	 * Inserimento di un nuovo documento
	 * @param tipologia tipologia del documento da creare
	 * @return
	 * @throws Exception
	 */
	public String insTableDoc(String tipologia) throws Exception {
		try {
			formsAdapter.insTableDoc(tipologia);

			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			return buildSpecificDocEditPageAndReturnNavigationRule(tipologia, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Inserimento di un repertorio (selezione della tipologia di repertorio)
	 * @return
	 * @throws Exception
	 */
	public String insTableDocAllReps() throws Exception {
		formsAdapter.getDefaultForm().addParam("repToInsert", "");
		return insTableDoc(Const.DOCWAY_TIPOLOGIA_REPERTORIO);
	}

	/**
	 * Inserimento di un repertorio (selezione della tipologia di repertorio)
	 * @return
	 * @throws Exception
	 */
	public String insTableDocAllFascicoliCustom() throws Exception {
		return insTableDoc(Const.DOCWAY_TIPOLOGIA_FASCICOLO_CUSTOM);
	}

	/**
	 * Caricamento della pagina di strumenti di amministrazione
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQAdmTools() throws Exception {
		try {
			formsAdapter.gotoTableQ("adm_tools", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			QueryAdmTools queryAdmTools = new QueryAdmTools();
			queryAdmTools.getFormsAdapter().fillFormsFromResponse(response);
			queryAdmTools.init(response.getDocument());
			setSessionAttribute("queryAdmTools", queryAdmTools);

			return "query@adm_tools";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di controllo di gestione
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQCtrlGestione() throws Exception {
		try {
			formsAdapter.gotoTableQ("ctrl_gestione", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			QueryCtrlGestione queryCtrlGestione = new QueryCtrlGestione();
			queryCtrlGestione.getFormsAdapter().fillFormsFromResponse(response);
			queryCtrlGestione.init(response.getDocument());
			setSessionAttribute("queryCtrlGestione", queryCtrlGestione);

			return "query@ctrl_gestione";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di stampa del registro delle fatture
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQRegistroFatture() throws Exception {
		try {
			formsAdapter.gotoTableQ("registro_fatture", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			QueryRegistroFatture queryRegistroFatture = new QueryRegistroFatture();
			queryRegistroFatture.getFormsAdapter().fillFormsFromResponse(response);
			queryRegistroFatture.init(response.getDocument());
			setSessionAttribute("queryRegistroFatture", queryRegistroFatture);

			return "fatturepa_query@registro_fatture";

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di stampe protocolli
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQStampe() throws Exception {
		try {
			formsAdapter.gotoTableQ("stampe", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			QueryStampe queryStampe = new QueryStampe();
			queryStampe.getFormsAdapter().fillFormsFromResponse(response);
			queryStampe.init(response.getDocument());
			setSessionAttribute("queryStampe", queryStampe);

			return "query@stampe";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di stampe repertori
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQStampe_rep() throws Exception {
		try {
			formsAdapter.gotoTableQ("stampe_rep", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			QueryStampe_rep queryStampe_rep = new QueryStampe_rep();
			queryStampe_rep.getFormsAdapter().fillFormsFromResponse(response);
			queryStampe_rep.init(response.getDocument());
			setSessionAttribute("queryStampe_rep", queryStampe_rep);

			return "query@stampe_rep";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di stampa repertori fascicoli
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQRep_fasc() throws Exception {
		try {
			formsAdapter.gotoTableQ("rep_fasc", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			QueryRep_fasc queryRep_fasc = new QueryRep_fasc();
			queryRep_fasc.getFormsAdapter().fillFormsFromResponse(response);
			queryRep_fasc.init(response.getDocument());
			setSessionAttribute("queryRep_fasc", queryRep_fasc);

			return "query@rep_fasc";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di profilo personale
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQProfiloPersonale() throws Exception {
		try {
			formsAdapter.gotoTableQ("profilo", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			QueryProfiloPersonale queryProfiloPersonale = new QueryProfiloPersonale();
			queryProfiloPersonale.getFormsAdapter().fillFormsFromResponse(response);
			queryProfiloPersonale.init(response.getDocument());
			setSessionAttribute("queryProfiloPersonale", queryProfiloPersonale);

			return "query@profilo_personale";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Stampa del registro giornaliero
	 * @return
	 * @throws Exception
	 */
	public String findAndPrintRegistroGiornaliero() throws Exception {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			String query = "([doc_tipo]=arrivo OR partenza OR interno) AND ([docdataprot]=" + DateUtil.getDateNorm(cal.getTime()) + ") AND NOT([doc_bozza]=\"si\")";
			String qord = "xml(xpart:/doc/@data_prot:d),xml(xpart:/doc/@num_prot)";
			String view = getFormsAdapter().getDefaultForm().getParam("view") + "DATA_INT|";

			formsAdapter.findAndPrintRegistroGiornaliero(view, query, qord);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());

			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) { // caricamento della loadingbar

				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
			}

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Invio di notifiche differite
	 *
	 * @return
	 * @throws Exception
	 */
	public String invioNotifDifferite() throws Exception {
		try {
			// if (typeof(alias) == 'undefined' || alias.length == 0) alias = 'doc_notificadifferita';
			formsAdapter.invioNotifDifferite("doc_notificadifferita");
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());

			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) { // caricamento della loadingbar

				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
			}

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Ritorna la storia delle versioni di DocWay
	 * @return
	 * @throws Exception
	 */
	public String showVersioni() throws Exception {
		try {
			XMLDocumento response = formsAdapter.getDefaultForm().getVersioneApp("xdocway");
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			QueryVersioni queryVersioni = new QueryVersioni();
			queryVersioni.getFormsAdapter().fillFormsFromResponse(response);
			queryVersioni.init(response.getDocument());
			setSessionAttribute("queryVersioni", queryVersioni);

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di scaricamento posta (interfaccia "webmail")
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoRicezionePosta() throws Exception {
		try {
			formsAdapter.gotoRicezionePosta();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			DocWayWebmail docwayWebmail = new DocWayWebmail();
			docwayWebmail.getFormsAdapter().fillFormsFromResponse(response);
			docwayWebmail.init(response.getDocument());
			docwayWebmail.setShowSxCol(false);
			setSessionAttribute("docwayWebmail", docwayWebmail);

			return "webmail";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di ricerca su altri documenti (personalView)
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQPersonalView() throws Exception {
		try {
			formsAdapter.gotoTableQ("personalView", false);

			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			QueryPersonalView queryPersonalView = new QueryPersonalView();
			queryPersonalView.getFormsAdapter().fillFormsFromResponse(response);
			queryPersonalView.init(response.getDocument());
			setSessionAttribute("queryPersonalView", queryPersonalView);

			return "query@personalView";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina di ricerca su altri documenti (personalView)
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQFascicoliCustom() throws Exception {
		try {
			formsAdapter.gotoTableQ(Const.DOCWAY_TIPOLOGIA_FASCICOLO_CUSTOM, false);

			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			QueryFascicoloCustom queryFascicoloCustom = new QueryFascicoloCustom();
			queryFascicoloCustom.getFormsAdapter().fillFormsFromResponse(response);
			queryFascicoloCustom.init(response.getDocument());
			setSessionAttribute("queryFascicoloCustom", queryFascicoloCustom);

			return "query@fascicoliCustom";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento pagina di ricerca di una specifica personalView
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQSpecificPersonalView(String tableName, String personalViewTemplate) throws Exception {
		try {
			formsAdapter.gotoTableQ(tableName + "#personalView=" + personalViewTemplate, false);

			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return buildSpecificQueryPageAndReturnNavigationRule(tableName, response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della pagina iniziale di Docway Delibere
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQTo() throws Exception {
		try {
			formsAdapter.gotoTableQ("to", false);
			formsAdapter.getDefaultForm().addParam("dw4customTemplate", "templateTo.xhtml"); //dpranteda -  il service è stato modificato per rimandarlo indietro
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //dpranteda - restore delle form - in modo tale da non influenzare DocWay Home con il customTemplate

			QueryTo queryTo = (QueryTo) getSessionAttribute("queryTo");
			String currentOrgano = "";

			if(queryTo != null)
			{
				currentOrgano = queryTo.getOrganoSelezionatoNome();
			}

			queryTo = new QueryTo();
			queryTo.getFormsAdapter().fillFormsFromResponse(response);
			queryTo.init(response.getDocument(),currentOrgano);
			setSessionAttribute("queryTo", queryTo);

			return "query@to";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della console di visualizzazione delle decretazioni di APV
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoConsoleDecretazioniApv() throws Exception {
		try {
			formsAdapter.gotoConsoleDecretazioniApv();
			boolean loadFromFile = false;
			XMLDocumento response = null;
			if(loadFromFile) {

				//response =
			} else {

				response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				//testFileWrite(response.asXML(), "c:\\zzResponse.xml", "UTF-8");

				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
			}

			DocWayConsoleDecretazioniApv docwayDecretazioni = new DocWayConsoleDecretazioniApv();
			docwayDecretazioni.getFormsAdapter().fillFormsFromResponse(response);
			docwayDecretazioni.init(response.getDocument());
			docwayDecretazioni.setShowSxCol(false);
			setSessionAttribute("docwayConsoleDecretazioniApv", docwayDecretazioni);

			return "consoledecretazioniapv";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della console di visualizzazione degli ordini di APV
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoConsoleOrdiniApv() throws Exception {
		try {
			formsAdapter.gotoConsoleOrdiniApv();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			DocWayConsoleOrdiniApv docwayOrdini = new DocWayConsoleOrdiniApv();
			docwayOrdini.getFormsAdapter().fillFormsFromResponse(response);
			docwayOrdini.init(response.getDocument());
			docwayOrdini.setShowSxCol(false);
			setSessionAttribute("docwayConsoleOrdiniApv", docwayOrdini);

			return "consoleordiniapv";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Caricamento della console di visualizzazione delle fatture di APV
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoConsoleFattureApv() throws Exception {
		try {
			formsAdapter.gotoConsoleFattureApv();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			DocWayConsoleFattureApv docwayFatture = new DocWayConsoleFattureApv();
			docwayFatture.getFormsAdapter().fillFormsFromResponse(response);
			docwayFatture.init(response.getDocument());
			docwayFatture.setShowSxCol(false);
			setSessionAttribute("docwayConsoleFattureApv", docwayFatture);

			return "consolefattureapv";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Apertura del menù relativo alla gestione delle deleghe
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQDeleghe() throws Exception {
		try {
			formsAdapter.gotoDelegheOptions();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			DelegheOptions optionsDeleghe = new DelegheOptions("docwayService");
			optionsDeleghe.getFormsAdapter().fillFormsFromResponse(response);
			optionsDeleghe.init(response.getDocument());
			setSessionAttribute("optionsDeleghe", optionsDeleghe);

			redirectToJsf("options@deleghe", response);

			return null;

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Caricamento della pagina di accesso alla console di audit (generazione del token di autenticazione)
	 * @return
	 * @throws Exception
	 */
	public String gotoAudit() throws Exception {
		try {
			formsAdapter.gotoAudit();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			DocWayAuditLogin auditLogin = new DocWayAuditLogin();
			auditLogin.getFormsAdapter().fillFormsFromResponse(response);
			auditLogin.init(response.getDocument());
			setSessionAttribute("auditLogin", auditLogin);
			
			return "auditLogin";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
