package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Element;

public abstract class Init extends Page {
	
	protected DocDocWayQueryFormsAdapter formsAdapter;
	
	private List<Map<String,String>> matricoleL;
	private List<Societa> societaL;
	private boolean allSocieta = true;
	private String societaSelezionata = "";
	private String currentLoginStep = "";
	
	private String archivio = ""; // identifica l'archivio al quale si sta facendo accesso

	public Init() throws Exception {
		try {
			this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			setFatalLevelInErrormsg();
			return;			
		}		
	}
	
	/**
	 * Metodo eseguito per il login dell'utente (con scelta della matricola) e inizializzazione di tutti 
	 * i bean necessari al caricamento della pagina richiesta (in base all'URL utilizzato per la chiamata)
	 * 
	 * @return false in caso di redirect su init.jsf (login con scelta matricola), true in caso di caricamento di qualsiasi altra pagina
	 * @throws Exception
	 */
	public boolean init() throws Exception {
		//formsAdapter.getDefaultForm().addParam("verbo", "");
		//formsAdapter.getDefaultForm().addParam("query", "");
		
		String forceLogin = ""; // forzatura della login (azzeramento di qualsiasi sessione utente)
		
		// caricamento su formsAdapter dei parametri che arrivano da url (tipo nome del db)
		if (request != null) {
			String appName = AppUtil.getAppName(request);
			setEmbeddedAppInSession(appName); // aggiunta dell'applicazione in sessione
			
			String db = getDbForApp(appName);
			formsAdapter.getDefaultForm().addParam("db", db); // gestione db dell'applicazione (aggiunta del parametro al formadapter)
			archivio = db;
			
			// interfaccia utente specifica per la gestione di un repertorio (CRUD)
			if (appName.equals("dwrep")) {
				String repCode = (String) request.getParameter("repCode");
				if (repCode == null || repCode.length() == 0)
					repCode = DocWayProperties.readProperty("dwrep.codici_repertori", ""); // tentativo di caricamento da file di properties
				
				if (repCode.length() > 0) {
					formsAdapter.getDefaultForm().addParam("verbo", appName);
					formsAdapter.getDefaultForm().addParam("dwRepCode", repCode);
					setSessionAttribute("repCode", repCode);
				}
			}
			
			// caricamento dell'eventuale parametro di debug
			String debug = (String) request.getParameter("debug");
			if (debug != null && debug.length() > 0)
				setSessionAttribute("debugMode", debug);
			
			isEnabledIWX(); // abilitazione o meno di IWX
			
			// caricamento dell'eventuale parametro di forceLogin
			forceLogin = (String) request.getParameter("forceLogin");
		}
		
		UserBean user = getUserBean();
		if (forceLogin != null && forceLogin.length() > 0 && forceLogin.equals("true"))
			formsAdapter.getDefaultForm().addParam("renewLogin", "true");
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(user);
		Logger.info("Init.init(), chiamata a service per l'utente " + user.getLogin() + " [matricola: " + user.getMatricola() + "]");
		
		// azzeramento del parametro forceLogin
		formsAdapter.getDefaultForm().removeParam("renewLogin");
		
		// gestione multilogin
		if (handleSceltaLoginResponse(response)) {
			Logger.info("Init.init(), login multipla, scelta dell'utente");
			
			if (user.getMatricola() == null || user.getMatricola().equals(""))
				return false; //multilogin -> scelta persona
			else {
				formsAdapter.loginWithMatricola(user.getMatricola());
				response = formsAdapter.getDefaultForm().executePOST(user);
			}
		}
		
		// gestione multisocieta'
		if (handleSceltaMultisocietaResponse(response)) {
			Logger.info("Init.init(), multisocieta, scelta della societa' dell'utente");
			return false; //multisocieta -> scelta societa'
		}
		
		if (handleErrorResponse(response)) {
			Logger.info("Init.init(), errore riscontrato");
			
			setFatalLevelInErrormsg();
			return false;
		}
		
		if (user.getMatricola() == null || user.getMatricola().equals("")) {
			String matricola = response.getRootElement().attributeValue("opCodPersona");
			if (matricola != null) {
				user.setMatricola(matricola);
				setSessionAttribute("userBean", user);
				
				Logger.info("Init.init(), assegnazione della matricola " + matricola + " a bean utente " + user.getLogin() + " da cache del service");
			}
		}
	
		//si va alla home page
		initDocWayHomeFromResponse(response);
		return true;
	}
	
	/**
	 * aggiunta dell'applicazione in sessione
	 * @param request
	 * @param appName
	 */
	private void setEmbeddedAppInSession(String appName) {
		// l'aggiunta in sessione dell'applicazione corrente avviene solo al caricamento
		// della home e se non si tratta di una chiamata AJAX
		if (AppUtil.isPossibleApplicationSwitch(request)) {
			String sharedSessionApps = DocWayProperties.readProperty(Const.PROPERTY_SHARED_SESSION_APPS, ""); // eventuali apps embedded con sessione condivisa con DocWay
			
			if (!appName.equals("docway") && !appName.equals("acl") && !appName.equals("docwayproc") && !sharedSessionApps.contains(appName)) {
				formsAdapter.getDefaultForm().addParam(Const.DOCWAY_EMBEDDED_APP_NAME, appName); // gestione di applicazioni embedded interne a DocWay (es. crud su repertori)
				setSessionAttribute(Const.DOCWAY_EMBEDDED_APP_NAME, appName);
			}
			else {
				formsAdapter.getDefaultForm().addParam(Const.DOCWAY_EMBEDDED_APP_NAME, "");
				setSessionAttribute(Const.DOCWAY_EMBEDDED_APP_NAME, "");
			}
		}
	}
	
	/**
	 * Chiamata a logout (inattiva la cache del service)
	 * @return
	 * @throws Exception
	 */
	public boolean logout(UserBean userBean) throws Exception {
		formsAdapter.logout();
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(userBean);
		if (handleErrorResponse(response)) {
			return false;
		}
		return true;
	}
	
	/**
	 * selezione login da pagina di multilogin
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String loginWithMatricola() throws Exception {
		try {
			Map<String,String> map = (Map<String,String>)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("item");

			UserBean user = getUserBean();
			
			formsAdapter.loginWithMatricola(map.get("matr"));
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(user);
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				setFatalLevelInErrormsg();
				return null;
			}
			
			user.setMatricola(map.get("matr"));
			setSessionAttribute("userBean", user);
			
			// gestione multisocieta'
			if (handleSceltaMultisocietaResponse(response)) {
				Logger.info("Init.init(), multisocieta, scelta della societa' dell'utente");
				return null; //multisocieta -> scelta societa'
			}
			
			initDocWayHomeFromResponse(response);
			
			String rewritedUri = (String) getSessionAttribute("rewritedUri");
			if (rewritedUri != null && rewritedUri.length() > 0) {
				setSessionAttribute("rewritedUri", null);
				FacesContext.getCurrentInstance().getExternalContext().redirect(rewritedUri);
				return null;
			}
			
			return redirectFromMatricolaLogin();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			setFatalLevelInErrormsg();
			return null;			
		}
	}
	
	/**
	 * selezione societa' da pagina di multisocieta'
	 * @return
	 * @throws Exception
	 */
	public String loginWithSocieta() throws Exception {
		try {
			UserBean user = getUserBean();
			
			formsAdapter.loginWithSocietaMatricola(societaSelezionata, allSocieta, user.getMatricola());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(user);
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				setFatalLevelInErrormsg();
				return null;
			}
			
			user.setCodSocieta(societaSelezionata);
			user.setAllSocieta(allSocieta);
			setSessionAttribute("userBean", user);
			
			initDocWayHomeFromResponse(response);
			
			String rewritedUri = (String) getSessionAttribute("rewritedUri");
			if (rewritedUri != null && rewritedUri.length() > 0) {
				setSessionAttribute("rewritedUri", null);
				FacesContext.getCurrentInstance().getExternalContext().redirect(rewritedUri);
				return null;
			}
			
			return redirectFromMatricolaLogin();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			setFatalLevelInErrormsg();
			return null;			
		}
	}
	
	/**
	 * richiesta all'utente la scelta della login (multilogin)
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected boolean handleSceltaLoginResponse(XMLDocumento response) throws Exception {
		if (response.testAttributeValue("/response/@verbo", "scelta_login")) { //nel caso di multilogin propone la lista delle matricole
			matricoleL = new ArrayList<Map<String,String>>();
			List<Element> els = response.selectNodes("/response/login");
			for (int i=0; i<els.size(); i++) {
				Element el = (Element)els.get(i);
				Map<String, String> map = new HashMap<String, String>();
				matricoleL.add(map);
				map.put("matr", el.attributeValue("matr"));
				map.put("descr", el.attributeValue("descr"));
			}
			
			currentLoginStep = "login";
			
			return true;
		}
		return false;
	}
	
	/**
	 * richiesta all'utente la scelta della societa' (multisocieta')
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected boolean handleSceltaMultisocietaResponse(XMLDocumento response) throws Exception {
		if (response.testAttributeValue("/response/@verbo", "scelta_societa")) { //nel caso di multisocieta propone la lista delle societa'
			
			societaL = XMLUtil.parseSetOfElement(response.getDocument(), "/response/societa_select/societa", new Societa());
			Societa emptySocieta = new Societa();
			emptySocieta.setCod("_");
			emptySocieta.setText("");
			emptySocieta.setSelected("true");
			societaL.add(0, emptySocieta);
			
			allSocieta = true;
			currentLoginStep = "societa";
			
			return true;
		}
		return false;
	}
	
	/**
	 * selezione di una societa (change sulla select) in caso di multisocieta
	 */
	public void societaChangeListener(ValueChangeEvent e) {
		if (e.getNewValue() != null && !e.getNewValue().equals("_"))
			this.allSocieta = false;
		else
			this.allSocieta = true;
	}
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public void setFormsAdapter(DocDocWayQueryFormsAdapter formsAdapter) {
		this.formsAdapter = formsAdapter;
	}
	
	public List<Map<String, String>> getMatricoleL() {
		return matricoleL;
	}
	
	public List<Societa> getSocietaL() {
		return societaL;
	}

	public boolean isAllSocieta() {
		return allSocieta;
	}
	
	public void setAllSocieta(boolean allSocieta) {
		this.allSocieta = allSocieta;
	}
	
	public String getCurrentLoginStep() {
		return currentLoginStep;
	}
	
	public String getSocietaSelezionata() {
		return societaSelezionata;
	}

	public void setSocietaSelezionata(String societaSelezionata) {
		this.societaSelezionata = societaSelezionata;
	}
	
	public String getArchivio() {
		return archivio;
	}

	public void setArchivio(String db) {
		this.archivio = db;
	}
	
	abstract protected void initDocWayHomeFromResponse(XMLDocumento response) throws Exception;
	abstract protected String redirectFromMatricolaLogin();
}
