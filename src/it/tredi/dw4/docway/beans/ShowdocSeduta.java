package it.tredi.dw4.docway.beans;

import java.util.ArrayList;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.docway.doc.adapters.delibere.SedutaDocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.History;
import it.tredi.dw4.docway.model.delibere.Cat_container;
import it.tredi.dw4.docway.model.delibere.Componente;
import it.tredi.dw4.docway.model.delibere.PropostaOdg;
import it.tredi.dw4.docway.model.delibere.Seduta;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class ShowdocSeduta extends DocWayShowdoc {
	
	private SedutaDocDocWayDocumentFormsAdapter formsAdapter;
	private String xml; 
	private Seduta doc = new Seduta();
	private String comunicazioneLabel = "";
	private String currentPage = "";
	
	// per utilizzare i template realizzati per documenti docway
	private String view;
	private String linkToDoc = ""; // url di accesso al documento corrente (per copia in clipboard e invio mail di notifica)
	
	//funzionalitaDisponibili['@mascRis']
	private String[] proposteRadioButtons = {"A", "M", "N", "R", "T", "D"};
	private String[] comunicazioniRadioButtons = {"P", "D"};
	private String test = "";
	private int posUsed = 0;
	
	//error handling 
	private String focusId = "";
	
	//funzionalitaDisponibili['@mascDelib']
	private boolean downloadTestoDelibera = false;
	private String fileNameTestoDelibera = "";
	private String fileIdTestoDelibera = "";
	private boolean error = false;
	
	private LookupSeduta lookupSeduta;

	public ShowdocSeduta() throws Exception {
		this.formsAdapter = new SedutaDocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		this.setXml(dom.asXML());
		this.setDoc((Seduta) XMLUtil.parseElement(dom, "/response/seduta" , new Seduta()));
		this.setComunicazioneLabel(FormsAdapter.getParameterFromCustomTupleValue("dicitComunicazione", formsAdapter.getDefaultForm().getParam("_cd")));
		if(!this.comunicazioneLabel.isEmpty())
			this.comunicazioneLabel = StringUtil.substringAfter(this.comunicazioneLabel, "|");
		
		setCurrentPage(getFormsAdapter().getCurrent()+"");
		
		if(formsAdapter.checkBooleanFunzionalitaDisponibile("maschRis", false)){
			initMaschRis();
		}
		if(formsAdapter.checkBooleanFunzionalitaDisponibile("maschDelib", false)){
			initMaschDelib();
		}
}
	
	public void initMaschRis(){
		posUsed = 0;
	}
	
	public String initMaschDelib(){
		setDownloadTestoDelibera(false);
		setFileNameTestoDelibera("");
		setFileIdTestoDelibera("");
		setError(false);
		return null;
	}
	
	public void reset(){
		this.doc = new Seduta();
	}
	
	@Override
	public SedutaDocDocWayDocumentFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	/*
	 * Metodi per navigazione ./NavigationBar.xhtml
	 * */
	@Override
	public String paginaTitoli() throws Exception {
		return this._paginaTitoli("");
	}
	
	@Override
	public String _paginaTitoli(String template) throws Exception {
		try{
			XMLDocumento response = this._paginaTitoli();	
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificShowtitlesPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"),response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	@Override
	public String primaPagina() throws Exception {
		if (getFormsAdapter().primaPagina()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			String dbTable = response.getAttributeValue("/response/@dbTable");
			return this.buildSpecificShowdocPageAndReturnNavigationRule(dbTable,response);
		}
		return null;
	};
	
	@Override
	public String ultimaPagina() throws Exception {
		if (getFormsAdapter().ultimaPagina()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			String dbTable = response.getAttributeValue("/response/@dbTable");
			return this.buildSpecificShowdocPageAndReturnNavigationRule(dbTable,response);
		}
		return null;
		
	};
	
	@Override
	public String paginaSuccessiva() throws Exception {
		if (getFormsAdapter().paginaSuccessiva()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			String dbTable = response.getAttributeValue("/response/@dbTable");
			return this.buildSpecificShowdocPageAndReturnNavigationRule(dbTable,response);
		}
		return null;
	};
	
	@Override
	public String paginaPrecedente() throws Exception {
		if (getFormsAdapter().paginaPrecedente()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			String dbTable = response.getAttributeValue("/response/@dbTable");
			return this.buildSpecificShowdocPageAndReturnNavigationRule(dbTable,response);
		}
		return null;
	}	
	
	@Override
	public String paginaSpecifica() throws Exception {
		try {
			if (StringUtil.isNumber(this.currentPage)) {
				int curr = new Integer(currentPage).intValue();
				if (curr > 0 && curr <= getFormsAdapter().getDefaultForm().getParamAsInt("count")) {
					getFormsAdapter().paginaSpecifica(curr);
					XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
						return null;
					}
					
					String dbTable = response.getAttributeValue("/response/@dbTable");
					return this.buildSpecificShowdocPageAndReturnNavigationRule(dbTable,response);
				}
				else
					currentPage = getFormsAdapter().getCurrent() + "";
			}
			else
				currentPage = getFormsAdapter().getCurrent() + "";
			
			return null;
		} 
		catch (Throwable t){
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	@Override
	public void reload() throws Exception {
		this._reload("showdoc@seduta");
	}
	
	/*
	 * //TODO
	 * Tasti Menu Laterale
	 * */
	@Override
	public String modifyTableDoc() throws Exception {
		try{
			formsAdapter.modifyTableDoc();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			return buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response,isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * cancellazione di un documento
	 * 
	 * @return
	 * @throws Exception
	 */
	public String rimuoviDoc() throws Exception{
		this.formsAdapter.remove();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		String verbo = response.getAttributeValue("/response/@verbo");
		String dbTable = response.getAttributeValue("/response/@dbTable");
		
		if ("@reload".equals(dbTable)) {
			getFormsAdapter().fillFormsFromResponse(response);
			super._reload("showdoc@seduta");
			return null;
		}else if ("query".equals(verbo)) {
			String embeddedApp = getEmbeddedApp();
			if (embeddedApp.equals("")) // in caso di applicazione embedded occorre caricare la home specifica dell'applicazione
				return "query@to";//siamo dentro Docway Delibere e quindi ricarico la home di DD [dpranteda]
			else
				return "show@" + embeddedApp + "_home";
		}
		else
			return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
	} 
	
	public String risultatiSeduta() throws Exception{
		try {
			formsAdapter.risultatiSeduta();
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			
			init(response.getDocument());
			return ("showdoc@seduta");
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String presentiAssenti() throws Exception{
		try {
			formsAdapter.presentiAssenti();
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			
			init(response.getDocument());
			return ("showdoc@seduta");
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public void produciDelibere(){
		try {
			produciDelibere("");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//TODO
	private String produciDelibere(String nrecord_prop) throws Exception{
		try {
			formsAdapter.produciDelibere(nrecord_prop);
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//da valutare...potrebbe essere necessario il restore del formsaAdapter
			formsAdapter.fillFormsFromResponse(response);
			
			init(response.getDocument());
			return ("showdoc@seduta");
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	//TODO
	public String produciVerbale() throws Exception{
		try {
			formsAdapter.produciVerbale(doc.getOrgano_cod(),doc.getNrecord(),doc.getData_convocazione());
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			//init(response.getDocument());
			//gestire la response con il bean appropriato
			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String tornaDaRisultati() throws Exception{
		try {
			formsAdapter.tornaDaRisultati();
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			
			init(response.getDocument());
			return ("showdoc@seduta");
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Invio file modello di organi
	 * @throws Exception 
	 */
	public void invioModelloOdg(){
		try {
			invioModello("modelloODG");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void invioModelloRisultati(){
		try {
			invioModello("modelloRisultati");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void invioModelloVerbale(){
		try {
			invioModello("modelloVerbale");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String invioModello(String modello) throws Exception{
		try {
			formsAdapter.invioModelloALista(modello, this.doc.getNrecord());
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Download file modello di organi
	 * @throws Exception 
	 */
	public void downloadModelloOdg(){
		try {
			downloadModello("ODG", "odg.rtf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void downloadModelloRisultati(){
		try {
			downloadModello("Risultati", "risultati.rtf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void downloadModelloVerbale(){
		try {
			downloadModello("Verbale", "verbale.rtf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String downloadModello(String modello, String title) throws Exception{
		try{
			String id = StringUtil.substringAfter(title, ".");
			
			formsAdapter.downloadModello(modello, doc.getNrecord(), title);
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				response.setContentType(new MimetypesFileTypeMap().getContentType(id));
				response.setContentLength(attachFile.getContent().length);
				String mode = "attachment";
				response.setHeader("Content-Disposition", mode + "; filename=\"" + title + "\"");
				ServletOutputStream out;
				out = response.getOutputStream();
				out.write(attachFile.getContent());
				
				faces.responseComplete();
			}
			else {
				// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
				//handleErrorResponse(attachFile.getXmlDocumento());
				showMessageWarning(I18N.mrs("dw4.errore_download_file"));
			}
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		
		return null;
	}
	
//	private String doProjection(String value, String aliasName) throws Exception{
//		try{
//			getFormsAdapter().openUrl(value, aliasName);
//			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
//			if (handleErrorResponse(response)) {
//				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
//				return null;
//			}
//			return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
//		}
//		catch (Throwable t) {
//			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
//			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
//			return null;			
//		}
//	}
	
	public String doProjectionVerbale() throws Exception {
		return doProjection(doc.getNrecord_verbale(), "docnrecord","");
	}
	
	/*
	 * Azioni in PropostaOdg
	 * */
	public String spostaPropostaSu() throws Exception{
		try {
			PropostaOdg proposta = (PropostaOdg)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
		
			formsAdapter.spostaProposta(proposta.getNrecord_prop(), "su");
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String spostaPropostaGiu() throws Exception{
		try {
			PropostaOdg proposta = (PropostaOdg)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
			
			formsAdapter.spostaProposta(proposta.getNrecord_prop(), "giu");
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/*
	 * Link Azioni in PropostaOdg
	 * //TODO
	 * mancano ancora generaPDF - vedi - delibera - .... - vedere *.xhtml
	 * */
	public String rinviaProposta() throws Exception{
		try {
			PropostaOdg proposta = (PropostaOdg)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
			
			formsAdapter.rinviaProposta(proposta.getNrecord_prop(), doc.getOrgano_cod(), doc.getNrecord());
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			lookupSeduta = new LookupSeduta();
			lookupSeduta.getFormsAdapter().fillFormsFromResponse(response);
			lookupSeduta.init(response.getDocument());
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			//reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public void produciDelibereDaProposta(){
		try {
			PropostaOdg proposta = (PropostaOdg)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
			produciDelibere(proposta.getNrecord_prop());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String produciDeliberaSedutaStante() throws Exception{
		try {
			PropostaOdg proposta = (PropostaOdg)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
			formsAdapter.produciDeliberaSedutaStante(proposta.getNrecord_prop());
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String generaPDF() throws Exception{
		try {
			PropostaOdg proposta = (PropostaOdg)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
			formsAdapter.generaPDF(proposta.getNrecord_prop());
			
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			String title="proposta.pdf";
			if (attachFile.getContent() != null) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				response.setContentType(new MimetypesFileTypeMap().getContentType(title));
				response.setContentLength(attachFile.getContent().length);
				String mode = "attachment";
				response.setHeader("Content-Disposition", mode + "; filename=\"" + title + "\"");
				ServletOutputStream out;
				out = response.getOutputStream();
				out.write(attachFile.getContent());
				
				faces.responseComplete();
			}
			else {
				// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
				handleErrorResponse(attachFile.getXmlDocumento());
			}
	
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String doProjectionProposta() throws Exception{
		PropostaOdg proposta = (PropostaOdg)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
		return doProjection(proposta.getNrecord_prop(), "docnrecord","");
	}
	
	/*
	 * Metodi duplicati da ShowdocDoc 
	 */
	 
	/**
	 * Init common per tutte le tipologie di documenti
	 */
	public void initCommon(Document dom) {
		//orderHistory("");
	/*
		setUniservLink("");
		enableIW = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@enableIW"));
	*/
		// generazione dell'URL di accesso al documento (per copia link e invio notifica)
		if (getDoc() != null && getDoc().getNrecord() != null && !getDoc().getNrecord().equals(""))
			linkToDoc = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/loaddoc.pf?db=" + formsAdapter.getDb() + "&alias=docnrecord&value=" + getDoc().getNrecord();
	/*
		extrawayWorkflowWsUrl = XMLUtil.parseStrictAttribute(dom, "/response/@extrawayWorkflowWsUrl"); // URL necessario alla chiamata dei webservices di workflow di bonita
		
		setRaggruppaCC_statoIniziale(XMLUtil.parseStrictAttribute(dom, "/response/funzionalita_disponibili/@raggruppaCC_statoIniziale"));
	*/
		setCurrentPage(getFormsAdapter().getCurrent()+"");
	/*	
		// copia di xwfiles del documento su percorso di rete
		if (getDoc().containsFiles() 
				&& (getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_VARIE) 
						|| (getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_PARTENZA) && getDoc().isBozza()))) { // pulsante visibile sono se ci sono files da condividere e doc in partenza in bozza o doc non protocollato 
			condivisioneFilesEnabled = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@condivisioneFilesEnabled"));
			labelCondivisioneFiles = XMLUtil.parseStrictAttribute(dom, "/response/@labelCondivisioneFiles");
			if (condivisioneFilesEnabled && (labelCondivisioneFiles == null || labelCondivisioneFiles.equals("")))
				labelCondivisioneFiles = I18N.mrs("dw4.condividi_su_cartella_remota");
		}
		
		// TODO questa sezione deve essere modificata in caso di aggiunta di ulteriori servizi di firma
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("abilitaFirmaUniserv", false) && getFormsAdapter().checkBooleanFunzionalitaDisponibile("abilitaFirmaEng", false))
			serviziFirmaMultipli = true;
		
		listof_rep = XMLUtil.parseSetOfElement(dom, "/response/listof_rep/repertorio", new TitoloRepertorio());
		
		listofWorkflows= XMLUtil.parseSetOfElement(dom, "/response/workflows/workflow", new TitoloWorkflow());

		// eliminazione dei workflow gia' avviati sul documento corrente che attualmente non risultano conclusi o annullati
		if (listofWorkflows != null && listofWorkflows.size() > 0 
				&& getDoc().getWorkflowInstances() != null && getDoc().getWorkflowInstances().size() > 0) {
			for (int i=0; i<listofWorkflows.size(); i++) {
				TitoloWorkflow workflowTitle = (TitoloWorkflow) listofWorkflows.get(i);
				if (workflowTitle != null) {
					for (int j=0; j<getDoc().getWorkflowInstances().size(); j++) {
						WorkflowInstance workflowInstance = (WorkflowInstance) getDoc().getWorkflowInstances().get(j);
						if (workflowInstance != null
								&& workflowInstance.getProcess() != null
								&& workflowTitle.getName() != null
								&& (!workflowInstance.getStatus().equals("finished") && !workflowInstance.getStatus().equals("cancelled"))
								&& workflowInstance.getProcess().equals(workflowTitle.getName())) {
							listofWorkflows.remove(i);
						}
					}
				}
			}
		}
		
		// eventuale personalView da utilizzare per la costruzione del template
		personalView = XMLUtil.parseStrictAttribute(dom, "/response/@personalViewToUse");
		
		// verifica delle condizioni (comuni a tutte le tipologie di documenti) per le quali occorre mostrare 
		// la sezione di stati del documento.
		// N.B.: EVENTUALI PERSONALIZZAZIONI DI REPERTORI DEVONO ESSERE GESTITE ALL'INTERNO DEL TEMPLATE DEL REPERTORIO
		Storia segnaturaDoc = getDoc().getSegnatura();
		if ((getDoc().getProt_differito() != null && getDoc().getProt_differito().getData_arrivo() != null && getDoc().getProt_differito().getData_arrivo().length() > 0)
				|| (segnaturaDoc != null && segnaturaDoc.getCod_uff_oper() != null && segnaturaDoc.getCod_uff_oper().length() > 0)
				|| (getDoc().getAnnullato() != null && getDoc().getAnnullato().toLowerCase().equals("si"))
				|| (getDoc().getExtra() != null && getDoc().getExtra().getConservazione_id() != null && getDoc().getExtra().getConservazione_id().length() > 0)
				|| (getDoc().isBozza())
				|| (getDoc().isPersonale())
				|| (getDoc().getVisibilita() != null && getDoc().getVisibilita().getTipo() != null && getDoc().getVisibilita().getTipo().length() > 0 && !getDoc().getVisibilita().getTipo().toLowerCase().equals("pubblico"))
				|| (getDoc().isFilesPrenotati())) {
			showSectionStatiDocumento = true;
		}
		else {
			showSectionStatiDocumento = false;
		}
		
		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(dom, "doc");
		
		// caricamento dei testi di stampa info e segnatura per IWX
		testoStampaInfo = XMLUtil.parseStrictElement(dom, "/response/stampa_info");
		testoStampaSegnatura = XMLUtil.parseStrictElement(dom, "/response/stampa_segnatura");
		
		// formato da applicare in visualizzazione della classificazione
		classifFormat = XMLUtil.parseStrictAttribute(dom, "/response/@classifFormat", "");
		
		// testo da aggiungere in fase di stampa delle immagini con IWX
		siTesto = XMLUtil.parseStrictAttribute(dom, "/response/@SITesto", "");
		if (siTesto.length() > 0) {
			try {
				siTesto = new String(new Base64().decode(siTesto.getBytes(FormAdapter.ENCODING_ISO_8859_1)), FormAdapter.ENCODING_ISO_8859_1);
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		
		// caricamento etichette custom per visibilita'
		DigitVisibilitaUtil digitVisibilitaUtil = new DigitVisibilitaUtil(XMLUtil.parseStrictAttribute(dom, "/response/@dicitVis"));
		labelRiservato = digitVisibilitaUtil.getDigitRiservatoSingolare();
		labelAltamenteConfidenziale = digitVisibilitaUtil.getDigitAltConfSingolare();
		labelSegreto = digitVisibilitaUtil.getDigitSegretoSingolare();
		*/
	}
	
	/**
	 * Caricamento della storia del documento
	 * @return
	 */
	public String openHistory(){
		return orderHistory("");
	}
	
	/**
	 * Chiusura della storia del documento
	 * @return
	 */
	public String closeHistory() {
		getDoc().setHistory(new ArrayList<History>());
		return null;
	}
	
	/**
	 * Caricamento della storia del documento con definizione dell'ordinamento
	 * @param order Campi su cui ordinare la storia
	 * @return
	 */
	public String orderHistory(String order){
		try {
			this.formsAdapter.showHistory(order);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			getDoc().initHistory(response.getDocument());
			this.view = response.getAttributeValue("/response/@view", "");
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Caricamento della storia del documento con ordinamento su tipo
	 * @return
	 */
	public String orderTipo(){
		return orderHistory("@tipo");
	}
	
	/**
	 * Caricamento della storia del documento con ordinamento su persona/ufficio
	 * @return
	 */
	public String orderPersonaUfficio(){
		return orderHistory("@nome_persona");
	}
	
	/**
	 * Caricamento della storia del documento con ordinamento su operatore
	 * @return
	 */
	public String orderOperatore(){
		return orderHistory("@operatore");
	}
	
	
	/*
	 * //TODO
	 * Tasti Menu Laterale nelle varie @maschRis - @maschComp - @maschDelib 
	 * */
	public String confermaRisultati() throws Exception{
		setErrorFieldIds(""); 
		setFocusId("");
		if(chkSubmitRisultati()){
			try {
					String result = result_confermaRisultati();
					formsAdapter.confermaRisultati(result);
			
					XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
						return null;
					}
					
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
					reload();
					return null;
			}
			catch (Throwable t) {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
		}
		return null;
	}
	
	public void pulisciRisultati(){
		for(Cat_container cat_container : doc.getOdg())
			for(PropostaOdg proposta : cat_container.getProposte())
			{
				proposta.setPosField("");
				proposta.setRadioField("");
				if(proposta.getCommento_comunicazioni() != null)
					proposta.setCommento_comunicazioni("");
				if(proposta.getNota_risultato() != null)
					proposta.setNota_risultato("");
			}
		posUsed = 0;
		setErrorFieldIds("");
	}
	
	public String confermaComponenti() throws Exception{
		try {
			String result = result_confermaComponenti();
			formsAdapter.confermaComponenti(result);
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	//TODO
	public String confermaDelibera() throws Exception{
		setError(false);
		if(chkSubmitDelibera()){
			try {
				String result = result_setAllegato();
				formsAdapter.confermaDelibera(doc.getProposte_da_deliberare().get(0).getNrecord_prop(), result, fileIdTestoDelibera);
		
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				formsAdapter.fillFormsFromResponse(response);
				reload();
				return null;
			}
			catch (Throwable t) {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
		}
		return null;
	}
	
	/*
	 * START - RISULTATI */
	public void checkItem(ValueChangeEvent e){
		boolean newValue = e.getNewValue()!= null ? true : false;
		
		if(newValue){
			//elaborazione dello javascript ttsorg.js
			PropostaOdg proposta = (PropostaOdg) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
			if(proposta.getTipo().equals("delibera")){
				if(e.getNewValue().toString().equals("D") || e.getNewValue().toString().equals("T")){
					if(proposta.getPosField() != null && !proposta.getPosField().isEmpty() && !proposta.getPosField().equals("-")){
						int posField = Integer.parseInt(proposta.getPosField());
						if(posUsed > posField){
							Cat_container cat_container = (Cat_container) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("cat_container");
							for(PropostaOdg prop : cat_container.getProposte()){
								if(prop.getPosField() != null && !prop.getPosField().isEmpty() && !prop.getPosField().equals("-") && Integer.parseInt(prop.getPosField()) > posField){
									prop.setPosField(  String.valueOf(Integer.parseInt(prop.getPosField()) - 1) );
								}
							}
						}
						--posUsed;
						if(posUsed < 0)
							posUsed = 0;
					}
					proposta.setPosField("-");
				}else if(proposta.getPosField() == null || proposta.getPosField().isEmpty() || proposta.getPosField().equals("-"))
				{
					++posUsed;
					proposta.setPosField(String.valueOf(posUsed));
				}
			}
		}
	}
	
	public boolean chkSubmitRisultati(){
		int maxPosRis = 129;
		int higherPos = 0;
		int pos = 0;
		int nonDelib = 0;
		
		for ( Cat_container cat_container : doc.getOdg() ){
			if(cat_container.getTipo().equalsIgnoreCase("comunicazione")){
				//se è una comunicazione controllo solo che sia stata scelta un'opzione
				for (PropostaOdg proposta : cat_container.getProposte())
					if(proposta.getRadioField() == null || proposta.getRadioField().isEmpty()){
						this.setErrorMessage(proposta.getPos(),I18N.mrs("dw4.errore_mancato_risultato") + " " + getComunicazioneLabel(),false);
						return false;
					}
			}else if (cat_container.getTipo().equalsIgnoreCase("delibera")){
					//se è una proposta controllo che sia stata scelta un'opzione e quindi un valore per l'ordinamento
					for(int i=0;i<cat_container.getProposte().size();i++){
						PropostaOdg proposta = cat_container.getProposte().get(i);
						if(proposta.getPosField() == null || proposta.getPosField().isEmpty() ||
							proposta.getRadioField() == null || proposta.getRadioField().isEmpty()){
								this.setErrorMessage(proposta.getPos(),I18N.mrs("dw4.errore_mancato_risultato_proposta"),false);
								return false;
						}//if
					
						//controllo che la posizione assegnata sia conforme con la checkbox scelta
						try{
							pos = Integer.parseInt(proposta.getPosField());
						}catch (NumberFormatException e)
						{
							pos = -1;
						}
					
						if( ((pos <= 0) && !proposta.getRadioField().equalsIgnoreCase("T") && !proposta.getRadioField().equalsIgnoreCase("D"))
							|| ((pos > 0) && (proposta.getRadioField().equalsIgnoreCase("T") || proposta.getRadioField().equalsIgnoreCase("D"))) )
						{
							this.setErrorMessage(proposta.getPos(),I18N.mrs("dw4.errore_posizione_proposta"),true);
							return false;
						}
					
						//segno la poszione massima assegnata fin'ora
						if(pos > higherPos)
							higherPos = pos;
						else if (pos <= 0)
							++nonDelib;
						
						//controllo che la posizione assegnata non sia già presente per le proposte successive
						if(pos > 0){
							for(int j = i+1;j<cat_container.getProposte().size();j++){
								PropostaOdg propostaNext = cat_container.getProposte().get(j);
								int posNext = 0;
								try{
									posNext = Integer.parseInt(propostaNext.getPosField());
								}catch (NumberFormatException e){
									posNext = -1;
								}
								
								if(posNext == pos){
									this.setErrorMessage(propostaNext.getPos(),I18N.mrs("dw4.errore_posizione_proposta_duplicata"),true);
									return false;
								}
							}
						}
				}//for
				if ( higherPos > maxPosRis - nonDelib )  {
					this.setErrorMessage("",I18N.mrs("dw4.errore_ordine_proposte"));
					return false;
			    }
				    
			}//else-if
			
		}//for
		
		return true;
	}
	
	public void setErrorMessage(String fieldId, String message, boolean focused) {
		super.setErrorMessage(fieldId, message);
		this.setErrorFieldIds(this.getErrorFieldIds() + "," + fieldId);
		if (focused)
			this.setFocusId(fieldId);
	}

	private String result_confermaRisultati(){
		String result = "";
		String bS = "*|*";
		String lS = "-|-";
		if(chkSubmitRisultati()){
			String posFieldValue = "-";
			String radioFieldValue = "";
			String nrecordFieldValue = "";
			String notaRis = "";
			
			for(Cat_container cat_container : doc.getOdg())
				for(PropostaOdg proposta : cat_container.getProposte()){
					notaRis = "";
					posFieldValue = "-";
					nrecordFieldValue = proposta.getNrecord_prop();
					if(proposta.getTipo().equalsIgnoreCase("delibera")){
						posFieldValue = proposta.getPosField();
						radioFieldValue = getValueForRadio(proposta.getRadioField(), false);
						notaRis = proposta.getNota_risultato().isEmpty() ? "" : proposta.getNota_risultato();
					}else{
						if(!proposta.getCommento_comunicazioni().isEmpty())
							posFieldValue = "*" + proposta.getCommento_comunicazioni();
						radioFieldValue = getValueForRadio(proposta.getRadioField(), true);
					}
					
					result += bS + nrecordFieldValue + lS + posFieldValue + lS + radioFieldValue + lS + notaRis;					
				}
			
			result = result.substring(bS.length());
		}
		return result;
	}
	
	private String getValueForRadio(String radioValue, boolean comunicazione){
		if(comunicazione){
			if(radioValue.equals("P"))
				return "Preso atto";
			else if(radioValue.equals("D"))
				return "Non discussa";
			else return "";
		}else{
			if(radioValue.equals("A"))
				return "Approvata";
			else if(radioValue.equals("M"))
				return "Approvata con modifiche";
			else if(radioValue.equals("N"))
				return "Non accolta";
			else if(radioValue.equals("R"))
				return "Rinviata";
			else if(radioValue.equals("T"))
				return "Ritirata";
			else if(radioValue.equals("D"))
				return "Non discussa";
			else return "";
		}
	}
	/*
	 * FINE - RISULTATI
	 * */
	
	/*
	 * START - COMPONENTI */
	private String result_confermaComponenti(){
		String result = "";
		String bS = "*|*";
		String lS = "-|-";
		
		for(Componente componente : doc.getComponenti())
			result += bS + componente.getNominativo() + lS + componente.getDelega() + lS + getRadioValueForComponente(componente.getPresenza());
	
		result = result.substring(bS.length());
		return result;
	}
	
	private String getRadioValueForComponente(String radio){
		if (radio.equalsIgnoreCase("P"))
			return "Presente";
		else if (radio.equalsIgnoreCase("G"))
			return "Assente giustificato";
		else if (radio.equalsIgnoreCase("A"))
			return "Assente non giustificato";
		else return "";
	}
	/* FINE - COMPONENTI
	 * */
	
	/*
	 * START - DELIBERE */
	public String setAllegato() throws Exception{
		setDownloadTestoDelibera(false);
		setError(false);
		try {
			String result = result_setAllegato();
			formsAdapter.setAllegato(doc.getProposte_da_deliberare().get(0).getNrecord_prop(), doc.getProposte_da_deliberare().get(0).getCod_categoria(), result);
	
			String title = "testoDelibera.rtf";
			
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			if (attachFile.getContent() != null) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				response.setContentType(new MimetypesFileTypeMap().getContentType("rtf"));
				response.setContentLength(attachFile.getContent().length);
				String mode = "attachment";
				response.setHeader("Content-Disposition", mode + "; filename=\"" + title + "\"");
				ServletOutputStream out;
				out = response.getOutputStream();
				out.write(attachFile.getContent());
				
				faces.responseComplete();
				
				setDownloadTestoDelibera(true);
			}
			else {
				// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
				handleErrorResponse(attachFile.getXmlDocumento());
				this.setErroreResponse(I18N.mrs("dw4.errore_download_testo_delibera"), Const.MSG_LEVEL_ERROR);
				setError(true);
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			setError(true);
			return null;
		}
		return null;
	}
	
	private String result_setAllegato(){
		String result = "";
		String bS = "*|*";
		String lS = "-|-";
		
		for(Componente componente : doc.getComponenti())
			result += bS + componente.getNominativo() + lS + componente.getIncarico() + lS + componente.getDelega() + lS + getValueForRadioDelibere(componente);
	
		result = result.substring(bS.length());
		return result;
	}
		
	private String getValueForRadioDelibere(Componente componente){
		String radio = componente.getDelibera();
		if(radio != null && !radio.isEmpty()){
			if(radio.equalsIgnoreCase("F"))
				return "Favorevole";
			else if(radio.equalsIgnoreCase("C"))
				return "Contrario";
			else if(radio.equalsIgnoreCase("S"))
				return "Astenuto";
			else if(radio.equalsIgnoreCase("GD"))
				return "Assente giustificato";
			else if(radio.equalsIgnoreCase("AD"))
				return "Assente non giustificato";
			else
				return "";
		}else{
			return componente.getPresenza();
		}
	}
	
	private boolean chkSubmitDelibera(){
		//upload del file già fatto attraverso SWFUpload [initDeliberaSWFU]
		try {
			if(!isDownloadTestoDelibera()){
				this.setErroreResponse(I18N.mrs("dw4.alert_produrre_delibera"), Const.MSG_LEVEL_WARNING);
				setError(true);
				return false;
			}
			
			if (fileNameTestoDelibera == null || fileNameTestoDelibera.equals("")) {
				this.setErroreResponse(I18N.mrs("dw4.occorre_selezionare_il_file_da_importare"), Const.MSG_LEVEL_WARNING);
				setError(true);
				return false;
			}
			
			if(fileIdTestoDelibera == null || fileIdTestoDelibera.equals("")){
				this.setErroreResponse(I18N.mrs("dw4.errore_upload_testo_delibera"), Const.MSG_LEVEL_ERROR);
				setError(true);
				return false;
			}
			
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	/* FINE - DELIBERE
	 * */
	
	/*
	 * getter / setter
	 * */
	public Seduta getDoc() {
		return doc;
	}

	public void setDoc(Seduta seduta) {
		this.doc = seduta;
	}

	public void setFormsAdapter(SedutaDocDocWayDocumentFormsAdapter formsAdapter) {
		this.formsAdapter = formsAdapter;
	}
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getComunicazioneLabel() {
		return comunicazioneLabel;
	}

	public void setComunicazioneLabel(String comunicazioneLabel) {
		this.comunicazioneLabel = comunicazioneLabel;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getLinkToDoc() {
		return linkToDoc;
	}

	public void setLinkToDoc(String linkToDoc) {
		this.linkToDoc = linkToDoc;
	}
	
	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public String[] getProposteRadioButtons() {
		return proposteRadioButtons;
	}

	public void setProposteRadioButtons(String[] proposteRadioButtons) {
		this.proposteRadioButtons = proposteRadioButtons;
	}

	public String[] getComunicazioniRadioButtons() {
		return comunicazioniRadioButtons;
	}

	public void setComunicazioniRadioButtons(String[] comunicazioniRadioButtons) {
		this.comunicazioniRadioButtons = comunicazioniRadioButtons;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public String getFocusId() {
		return focusId;
	}

	public void setFocusId(String focusId) {
		this.focusId = focusId;
	}

	public boolean isDownloadTestoDelibera() {
		return downloadTestoDelibera;
	}

	public void setDownloadTestoDelibera(boolean downloadTestoDelibera) {
		this.downloadTestoDelibera = downloadTestoDelibera;
	}
	
	public void invalidateDownloadTestoDelibera(){
		setDownloadTestoDelibera(false);
	}

	public String getFileNameTestoDelibera() {
		return fileNameTestoDelibera;
	}

	public void setFileNameTestoDelibera(String fileNameTestoDelibera) {
		this.fileNameTestoDelibera = fileNameTestoDelibera;
	}
	
	public String getFileIdTestoDelibera() {
		return fileIdTestoDelibera;
	}

	public void setFileIdTestoDelibera(String fileIdTestoDelibera) {
		this.fileIdTestoDelibera = fileIdTestoDelibera;
	}
	
	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public LookupSeduta getLookupSeduta() {
		return lookupSeduta;
	}

	public void setLookupSeduta(LookupSeduta lookupSeduta) {
		this.lookupSeduta = lookupSeduta;
	}
	
	public boolean isLookupSedutaActive() {
		return this.lookupSeduta == null ? false : this.lookupSeduta.isActive();
	}
}
