/**
 * @author dpranteda
 */

package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.beans.Msg;
import it.tredi.dw4.docway.doc.adapters.delibere.DelibereDocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.delibere.Organo;
import it.tredi.dw4.docway.model.delibere.SedutaUtile;
import it.tredi.dw4.docway.model.delibere.VaschettaDelibere;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class QueryTo extends DocWayQuery{
	private String xml;
	private DelibereDocDocWayQueryFormsAdapter formsAdapter;
	
	private List<Organo> organi = new ArrayList<Organo>(); //elenco degli organi di appartenenza dell'utente
	private Organo organoSelezionato; //l'organo corrente selezionato dalla vaschetta
	private String organoSelezionatoNome = ""; //la selectOneMenu è mappata con le String, quindi tengo traccia del nome dell'organo selezionato
	
	private List<Option> uor_select; //lista degli uffici
	private String selected_uor = ""; //ufficio corrente
	private String ultimo_aggiornamento_vaschette = "";
	
	// proprieta' necessarie al caricamento del popup di selezione dell'uor
	private List<Option> uor_complete_select; @SuppressWarnings("unused")
	private boolean uor_popup_active = false;

	public QueryTo() throws Exception {
		this.formsAdapter = new DelibereDocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom){
		this.xml = dom.asXML();
		initOrgani(dom);
		initUORTendina(dom);
	}
	
	/**
	 * 	
	 * @param dom
	 * @param currentOrgano il nome dell'organo da selezionare al caricamento della pagina
	 */
	public void init(Document dom,String currentOrgano) {
		this.init(dom);
		this.setOrganoSelezionatoNome(currentOrgano);
	}
	
	@Override
	public QueryFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}
	
	@Override
	public String queryPlain() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * caricamento pagina di ricerca documenti
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQGlobale_to(String queryType) throws Exception {
		try {
			// TODO perche' viene recuperato dalla sessione anziche' impostare il form di ricerca vuoto? serve per il refine?
			// per ora commendato
			//QueryGlobale queryGlobale = (QueryGlobale) getSessionAttribute("queryGlobale");
			//if (queryGlobale == null){
				formsAdapter.gotoTableQ("globale_to", false);
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				
				//istanzia il bean della pagina di ricerca dei fascicoli
				QueryGlobale_to queryGlobale_to = new QueryGlobale_to(queryType);
				//riempi il formsAdapter della pagina di destinazione
				queryGlobale_to.getFormsAdapter().fillFormsFromResponse(response);
				queryGlobale_to.init(response.getDocument());
				setSessionAttribute("queryGlobale_to", queryGlobale_to);
			//}
			
			return "query@globale_to";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;	
		}
	}
	
	/**
	 * carico le informazioni relative agli organi di appartenenza dell'utente
	 * 
	 * @param dom
	 * */
	@SuppressWarnings("unchecked")
	private void initOrgani(Document dom) {
		this.organi.clear();
		this.organi = ((ArrayList<Organo>)XMLUtil.parseSetOfElement(dom, "/response/organo", new Organo()));
		
//		List<?> listOrgani = XMLUtil.parseSetOfElement(dom, "/response/organo", new Organo());
//		for (int i=0; i < listOrgani.size(); i++) {
//			Organo organo = (Organo) listOrgani.get(i);
//			this.organi.add(organo);
//		}
		
		this.organoSelezionatoNome = "";
		if(this.organi.size() > 0)
			this.setOrganoSelezionatoNome(this.organi.get(0).getNome());
	}
	
	/**
	 * 
	 * 
	 * @param dom
	 */
	@SuppressWarnings("unchecked")
	private void initUORTendina(Document dom) {
		this.uor_select = XMLUtil.parseSetOfElement(dom, "//uor_select/option", new Option() );
		if (this.uor_select != null && this.uor_select.size() > 0) {
			for (int i=0; i<this.uor_select.size(); i++) {
				if (this.uor_select.get(i).getSelected() != null && this.uor_select.get(i).getSelected().equals("selected"))
					this.selected_uor = this.uor_select.get(i).getValue();
			}
		}
	}
	
	public String changeOrganoTendina() throws Exception {
		//return "query@to.xhtml";
		Menu docwayMenu = (Menu) getSessionAttribute("docwaymenu");
		return docwayMenu.gotoTableQTo();
	}
	
	/**
	 * 
	 * @return showtitles@docway
	 * @throws Exception
	 */
	public String gotoDocsInGestione() throws Exception {
		try {
			VaschettaDelibere vaschetta = (VaschettaDelibere)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("vaschetta");
			if (!(vaschetta.getNum().length() == 0) && !(vaschetta.getNum().equals("-1")) && !(vaschetta.getNum().equals("0")) ) {
				formsAdapter.gotoDocsInGestione(vaschetta.getSelid());
		
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				//@ FIXME dpranteda - per tenere la formAdapter della schermata principale intatta 
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				return loadDocwayTitles(response);
			}
			else
				return showMessageEmptyVaschetta();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * 
	 * @return showtitles@docway
	 * @throws Exception
	 */
	public String gotoDocsInGestioneProssimaSeduta() throws Exception {
		try {
			SedutaUtile sedutaUtile = this.organoSelezionato.getSedutaUtile();
			if (sedutaUtile != null && sedutaUtile.getSelid() != null && !sedutaUtile.getSelid().isEmpty()){
				formsAdapter.gotoDocsInGestione(sedutaUtile.getSelid());
		
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				//@ FIXME dpranteda - per tenere la formAdapter della schermata principale intatta 
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				//return loadDocwayTitles(response);
				ShowtitlesSeduta titles = new ShowtitlesSeduta();
				titles.getFormsAdapter().fillFormsFromResponse(response);
				
				titles.init(response.getDocument());
				
				setSessionAttribute("showtitlesSeduta", titles);
				
				String page = "showtitles@seduta";
				return page;
			
			}
			else
				return showMessageEmptyVaschetta();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String inserisciSeduta() throws Exception{
		try {
			formsAdapter.inserisciSeduta(this.organoSelezionato.getCod());
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//@ FIXME dpranteda - per tenere la formAdapter della schermata principale intatta 
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"),response,isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String inserisciProposta() throws Exception{
		try{
			formsAdapter.inserisciProposta(this.organoSelezionato.getCod());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			DocEditProposta docEditProposta = new DocEditProposta();
			docEditProposta.getFormsAdapter().fillFormsFromResponse(response);
			docEditProposta.init(response.getDocument());
			setSessionAttribute("docEditProposta", docEditProposta);
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			return "docEdit@proposta";
			
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String downloadModelloProposta() throws Exception{
		try{
			String id = organoSelezionato.getXwfile().getName();
			String name = organoSelezionato.getXwfile().getTitle() + "." + StringUtil.substringAfter(organoSelezionato.getXwfile().getName(), ".");
			
			formsAdapter.downloadModelloProposta(id, name);
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				response.setContentType(new MimetypesFileTypeMap().getContentType(id));
				response.setContentLength(attachFile.getContent().length);
				String mode = "attachment";
				response.setHeader("Content-Disposition", mode + "; filename=\"" + name + "\"");
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
	
	/**
	 * Ritorna la storia delle versioni di DocWayDelibere
	 * @return
	 * @throws Exception
	 */
	public String showVersioni() throws Exception {
		try {
			XMLDocumento response = formsAdapter.getDefaultForm().getVersioneApp("delibere");
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			QueryVersioni queryVersioni = new QueryVersioni(I18N.mrs("dw4.docway_delibere"));
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
	
	public String seduteConcluse() throws Exception{
		try {
			formsAdapter.seduteA(this.organoSelezionato.getCod(), this.organoSelezionato.getNome(), "chiusa", "concluse");
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//@ FIXME dpranteda - per non modificare la formAdapter della schermata principale
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			return buildSpecificShowtitlesPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"),response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String seduteAllaFirma() throws Exception{
		try {
			formsAdapter.seduteA(this.organoSelezionato.getCod(), this.organoSelezionato.getNome(), "alla firma", "alla firma");
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//@ FIXME dpranteda - per tenere la formAdapter della schermata principale intatta 
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			return buildSpecificShowtitlesPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"),response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String seduteACalendario() throws Exception{
		try {
			formsAdapter.seduteA(this.organoSelezionato.getCod(), this.organoSelezionato.getNome(), "aperta", "a calendario");
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//@ FIXME dpranteda - per tenere la formAdapter della schermata principale intatta 
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			return buildSpecificShowtitlesPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"),response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String proposteInCorso() throws Exception{
		try {
			formsAdapter.proposteInCorsoDopera(this.organoSelezionato.getCod(), this.organoSelezionato.getNome());
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//@ FIXME dpranteda - per tenere la formAdapter della schermata principale intatta 
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			Element root = response.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> tit = root.selectNodes("//titolo");
			if (null != tit && !tit.isEmpty()){
				return loadDocwayTitles(response);
			}
			else 
			{
				if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")) {
					return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
				} 
				else 
				{
					return null;
				}
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String proposteSospese() throws Exception{
		try {
			formsAdapter.proposteSospese(this.organoSelezionato.getCod(), this.organoSelezionato.getNome());
	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//@ FIXME dpranteda - per tenere la formAdapter della schermata principale intatta 
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			Element root = response.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> tit = root.selectNodes("//titolo");
			if (null != tit && !tit.isEmpty()){
				return loadDocwayTitles(response);
			}
			else 
			{
				if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")) {
					return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
				} 
				else 
				{
					return null;
				}
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String loadDocwayTitles(XMLDocumento response) throws Exception {
		DocWayTitles titles = new DocWayTitles();
		titles.getFormsAdapter().fillFormsFromResponse(response);
		
		titles.init(response.getDocument());
		
		setSessionAttribute("docwayTitles", titles);
		
		String page = "showtitles@docway";
		return page;
	}
	
	/**
	 * Strumenti di Amministrazione
	 * @throws Exception 
	 * 
	 * */
	public String gotoTableQToAdmTools() throws Exception{
		try {
			formsAdapter.gotoTableQ("to_adm_tools", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			QueryToAdmTools queryToAdmTools = new QueryToAdmTools();
			queryToAdmTools.getFormsAdapter().fillFormsFromResponse(response);
			queryToAdmTools.init(response.getDocument());
			setSessionAttribute("queryToAdmTools", queryToAdmTools);
			return "query@to_adm_tools";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;	
		}
	}
	
	/*
	 * Funzioni copia 1:1 da DocWayHome.java
	 * da analizzare succesivamente se si possono spostare di un livello
	 * */
	/**
	 * apertura di un popup di messaggio di vaschetta vuota in caso di click su
	 * vaschetta priva di record
	 *  
	 * @return
	 * @throws Exception
	 */
	private String showMessageEmptyVaschetta() throws Exception {
		Msg message = new Msg();
		message.setActive(true);
		message.setTitle(I18N.mrs("dw4.info"));
		message.setType(Const.MSG_LEVEL_INFO);
		message.setMessage(I18N.mrs("dw4.la_vaschetta_risulta_essere_vuota"));
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("msg", message);
		
		return null;
	}
	
	/**
	 * ALERT- non è copia 1:1...la init chiamata è diversa per le Delibere
	 * 
	 * Selezione di un ufficio da select delle vaschette
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String changeUORTendina() throws Exception {
		try {
			if (selected_uor != null && !(selected_uor.equals(""))) {
				if (selected_uor.equals("searchUOR")) {
					formsAdapter.showAllUOR();
					XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					}
					
					uor_complete_select = XMLUtil.parseSetOfElement(response.getDocument(), "//uor_select/option", new Option() );
					if (uor_complete_select != null && uor_complete_select.size() > 0) {
						Option empty = new Option();
						empty.setLabel("");
						empty.setValue("searchUOR");
						empty.setSelected("true");
						uor_complete_select.add(empty);
					}
					setUor_popup_active(true);
					
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				else {
					if (selected_uor.indexOf("##") == 0) {
						// selezione UOR da popup
						
						setUor_popup_active(false);
						formsAdapter.selectUORTendina(selected_uor, uor_complete_select);
						XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
						if (handleErrorResponse(response)) {
							formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
							return null;
						}

						formsAdapter.fillFormsFromResponse(response);
						init(response.getDocument(),this.organoSelezionatoNome);
					}
					else {
						formsAdapter.changeUORTendina(selected_uor);
						
						XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
						if (handleErrorResponse(response)) {
							formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
							return null;
						}

						formsAdapter.fillFormsFromResponse(response);
						init(response.getDocument(),this.organoSelezionatoNome);
					}
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
	
	public void setUor_popup_active(boolean uor_popup_active) {
		this.uor_popup_active = uor_popup_active;
	}
	
	/*
	 * getter and setter
	 * */
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}

	public List<Organo> getOrgani() {
		return organi;
	}

	public Organo getOrganoSelezionato() {
		return organoSelezionato;
	}

	public void setOrganoSelezionato(Organo organoSelezionato) {
		this.organoSelezionato = organoSelezionato;
	}

	public List<Option> getUor_select() {
		return uor_select;
	}

	public void setUor_select(List<Option> uor_select) {
		this.uor_select = uor_select;
	}

	public String getSelected_uor() {
		return selected_uor;
	}

	public void setSelected_uor(String selected_uor) {
		this.selected_uor = selected_uor;
	}

	public String getUltimo_aggiornamento_vaschette() {
		return ultimo_aggiornamento_vaschette;
	}

	public void setUltimo_aggiornamento_vaschette(
			String ultimo_aggiornamento_vaschette) {
		this.ultimo_aggiornamento_vaschette = ultimo_aggiornamento_vaschette;
	}

	public String getOrganoSelezionatoNome() {
		return organoSelezionatoNome;
	}

	public void setOrganoSelezionatoNome(String organoSelezionatoNome) {
		if(organoSelezionatoNome != null && !organoSelezionatoNome.equals(""))
		{
			this.organoSelezionatoNome = organoSelezionatoNome;
			this.setOrganoSelezionato(getOrganoFromName(this.organoSelezionatoNome));
		}
	}
	
	/**
	 * 
	 * @param nome nome dell'organo
	 * @return l'organo corrispondente
	 */
	private Organo getOrganoFromName(String nome){
		for (Organo organo : this.organi)
		{
			if(organo.getNome().equalsIgnoreCase(nome))
				return organo;
		}
		
		return null;
	}
}
