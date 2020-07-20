package it.tredi.dw4.utils;

import it.tredi.dw4.acl.beans.UserBean;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class AppUtil {

	private static final String SUFF_SPLIT_CHAR = "-";
	
	/**
	 * Restituisce il nome dell'archivio xdocwayproc in base al nome dell'archivio corrente:
	 * Se il nome contiene un suffisso (es: '-per') il nome dell'archivio xdocwayproc deve contenere
	 * lo stesso suffisso ('xdocwayproc-per')
	 * 
	 * @param curruntDb nome del db corrente (archivio di docway, es. 'xdocwaydoc') 
	 * @return
	 */
	public static String getXdocwayprocDbName(String curruntDb) {
		String xdocwayprocDbName = "xdocwayproc";
		
		// in caso di archivio con altro nome viene caricato il 
		int index = curruntDb.indexOf(SUFF_SPLIT_CHAR);
		if (index != -1)
			xdocwayprocDbName += SUFF_SPLIT_CHAR + curruntDb.substring(index+1);
		
		return xdocwayprocDbName;
	}
	
	/**
	 * ritorna l'appName recuperando i dati necessari dal FacesContext
	 * @return
	 */
	public static String getAppNameFromFacesContex() {
		return getAppName(FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath().substring(1));
	}
	
	/**
	 * ritorna l'appName recuperando i dati dalla sessione. In caso di assenza tenta
	 * il recupero dal FacesContext
	 * 
	 * @param session
	 * @return
	 */
	public static String getAppFromSession(HttpSession session) {
		if (session != null && session.getAttribute(Const.DOCWAY_EMBEDDED_APP_NAME) != null)
			return (String) session.getAttribute(Const.DOCWAY_EMBEDDED_APP_NAME);
		else {
			try {
				return getAppNameFromFacesContex();
			}
			catch (Exception e) {
				return null;
			}
		}
	}
	
	/**
	 * ritorna l'appName corrente in base alla request
	 * @param req
	 * @return
	 */
	public static String getAppName(HttpServletRequest req) {
		String appName = "";
		if (req != null)
			appName = getAppName(req.getServletPath().substring(1));
		
		return appName;
	}
	
	/**
	 * ritorna l'appName corrente in base all'uri passato
	 * @param uri
	 * @return
	 */
	private static String getAppName(String servletpath) {
		String appName = "";
		if (servletpath != null && servletpath.indexOf("/") != -1)
			appName = servletpath.substring(0, servletpath.indexOf("/"));
		
		return appName;
	}
	
	/**
	 * verifica se l'applicazione corrente corrisponde a quella caricata in sessione.
	 * 
	 * @return true in caso affermativo, false altrimenti
	 */
	public static boolean isCurrentAppInSession(HttpSession session, HttpServletRequest req) {
		String sessionApp = (String) session.getAttribute(Const.DOCWAY_EMBEDDED_APP_NAME);
		String currentApp = AppUtil.getAppName(req);
		if (sessionApp != null && currentApp != null) {
			String sharedSessionApps = DocWayProperties.readProperty(Const.PROPERTY_SHARED_SESSION_APPS, ""); // eventuali apps embedded con sessione condivisa con DocWay
			
			if (currentApp.equals("docway") || currentApp.equals("acl") || currentApp.equals("docwayproc") || sharedSessionApps.contains(currentApp))
				currentApp = "";
			
			if (!currentApp.equals(sessionApp)) {
				Logger.info("AppUtil.isCurrentAppInSession(), app. switch: " + (sessionApp.equals("") ? "default" : sessionApp) + " -> " + (currentApp.equals("") ? "default" : currentApp));
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * ritorna true se la richiesta corrente puo' derivare da un cambio di applicazione da parte dell'utente.
	 * 
	 * Il cambi di applicazione viene identificato tramite le seguenti condizioni:
	 * 1) e' stato richiesto il caricamento di home.jsf o init.jsf
	 * 2) non si tratta di una chiamata AJAX
	 * 3) la richieste non proviene da un componente incluso nella pagina (es. SwfUpload, IWX, ecc.)
	 * 
	 * @param req
	 * @return
	 */
	public static boolean isPossibleApplicationSwitch(HttpServletRequest req) {
		String requestURI = req.getRequestURI();
		// l'aggiunta in sessione dell'applicazione corrente avviene solo al caricamento
		// della home e se non si tratta di una chiamata AJAX
		if ((requestURI.contains("home.jsf") || requestURI.contains("init.jsf")) 
				&& !AppUtil.isAjaxRequest(req)
				&& !AppUtil.isRefererSwfUpload(req)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * ritorna true se la richiesta corrente e' di tipo AJAX, false altrimenti
	 * @return
	 */
	private static boolean isAjaxRequest(HttpServletRequest req) {
		boolean ajaxCall = false;
		String xReqWith = req.getHeader("X-Requested-With");
		if (xReqWith != null && xReqWith.equals("XMLHttpRequest"))
			ajaxCall = true;
		return ajaxCall;
	}
	
	/**
	 * ritorna true se la richiesta corrente e' di tipo AJAX, false altrimenti
	 * @return
	 */
	private static boolean isRefererSwfUpload(HttpServletRequest req) {
		boolean swfuploadReferer = false;
		String referer = req.getHeader("referer");
		if (referer != null && referer.contains("swfupload.swf"))
			swfuploadReferer = true;
		return swfuploadReferer;
	}
	
	/**
	 * Gestione di redirezione da un JSF o JSP. Viene tentala la costruzione tramite
	 * URI per gestire in modo corretto il redirect in presenza, ad esempio, di template
	 * personalizzati dei clienti (es. repertori personalizzati con template in sottocartelle).
	 * Senza la costruzione in base all'URI si cercherebbe di caricare i JSF o JSP all'interno
	 * delle sottocartelle dei clienti, provocando cosi' un errore di file non trovato
	 * (es. caricamento di home.jsf, presente solo nella home directory dell'applicazione)
	 * 
	 * @param uri
	 * @param fileToRedirect
	 * @return
	 */
	public static String getSendRedirect(String uri, String fileToRedirect) {
		if (uri != null && fileToRedirect != null) {
			// gestita la costruzione dell'uri per login in caso di template personalizzati
			// presenti in sottocartelle. Si rischierebbe di caricare una login.jsp interna
			// ad una cartella personalizzata di un cliente (file non presente)
			String[] parts = uri.split("\\/");
			if (parts != null && parts.length > 2)
				if (fileToRedirect.equals("login.jsp"))
					return "/" + parts[1] + "/" + fileToRedirect;
				else			
					return "/" + parts[1] + "/" + parts[2] + "/" + fileToRedirect;
		}

		return fileToRedirect;
	}
	
	/**
	 * Caricamento del remoteUser in fase di autenticazione (gestisce sia l'autenticazione integrata che su
	 * database sql)
	 * 
	 * @param request
	 * @return
	 */
	public static String loadAuthenticationRemoteUser(HttpServletRequest request) {
		String authMode = "";
		if (request.getAttribute("authentication_mode") != null) // sistemi di autenticazione alternativi
			authMode = (String) request.getAttribute("authentication_mode");
		
		return loadAuthenticationRemoteUser(request, authMode);
	}
	
	/**
	 * Caricamento del remoteUser in fase di autenticazione (gestisce sia l'autenticazione integrata che su
	 * database sql)
	 * 
	 * @param request
	 * @param authMode
	 * @return
	 */
	public static String loadAuthenticationRemoteUser(HttpServletRequest request, String authMode) {
		String user = request.getRemoteUser();
		if (user != null && user.contains("\\")) // eliminazione dell'eventuale dominio in caso di autenticazione integrata...
			user = user.substring(user.lastIndexOf("\\") + 1);
		
		// autenticazione SQL
		HttpSession session = request.getSession(false);
		if (authMode.equals("SQLAUTH") && session.getAttribute("userBean") != null) 
			user = ((UserBean) session.getAttribute("userBean")).getLogin();
		
		// gestione ADM
		String admLogin = (String) session.getAttribute("admLogin");
		if (admLogin != null && admLogin.length() > 0 && session.getAttribute("userBean") != null && ((UserBean) session.getAttribute("userBean")).getLogin().equals(admLogin))
			user = admLogin;
		
		return user;
	}
	
}
