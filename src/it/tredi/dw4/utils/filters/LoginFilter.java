package it.tredi.dw4.utils.filters;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.beans.Init;
import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginFilter implements Filter {
	
	private static String DEFAULT_INIT_PACKAGE = "it.tredi.dw4";

	@Override
	public void destroy() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(false);
		session = invalidatePreviousSession(session, request);
		
		String user = AppUtil.loadAuthenticationRemoteUser(request);
		Logger.info("LoginFilter.doFilter(), remoteUser caricato: " + user);
		
		UserBean userBean = (UserBean) session.getAttribute("userBean");

		if (request.getRequestURI().endsWith(".pf")) {
			Map<String, String[]> rewritedUriParams = (Map<String, String[]>) session.getAttribute("rewritedUriParams");
			if (rewritedUriParams == null)
				rewritedUriParams = new HashMap<String, String[]>();
			rewritedUriParams.putAll(request.getParameterMap());
			rewritedUriParams.remove("forceLogin");
			session.setAttribute("rewritedUriParams", rewritedUriParams);
		}
		
		String uri = request.getRequestURI();
		
		if (!uri.endsWith("initadm.jsf") && !uri.endsWith("javax.faces.resource/jsf.js.jsf")) {
			if (userBean == null || !userBean.getLogin().equals(user)) {
				Logger.info("LoginFilter.doFilter(), bean utente nullo, occorre caricarlo tramite init");
				
				// bean utente non in sessione - primo accesso (login)
				userBean = new UserBean(user);
				session.setAttribute("userBean", userBean);
				
				boolean skipInit = recreateInitBean(request, response);
				if (!skipInit) {
					// multilogin - redirect a pagina di scelta utente (identificazione matricola)
					if (uri.endsWith(".pf")) {
						String queryParams = getRequestQueryString(request);
						if (queryParams != null && !queryParams.equals(""))
							session.setAttribute("rewritedUri", uri + "?" + queryParams);
						else
							session.setAttribute("rewritedUri", uri);
					}
					
					response.sendRedirect(AppUtil.getSendRedirect(request.getRequestURI(), "init.jsf"));
					/*String[] parts = uri.split("\\/");
					if (parts != null && parts.length > 2)
						response.sendRedirect("/" + parts[1] + "/" + parts[2] + "/init.jsf");
					else
						response.sendRedirect("init.jsf");*/
				}
				else {
					// login singola - accesso a home o a pagina specifica (pretty faces)
					if (request.getRequestURI().endsWith(".pf"))
						chain.doFilter(request, response);
					else {
						// gestita la costruzione dell'uri per login in caso di template personalizzati
						// presenti in sottocartelle. Si rischierebbe di caricare una home.jsf interna
						// ad una cartella personalizzata di un cliente (file non presente)
						response.sendRedirect(AppUtil.getSendRedirect(request.getRequestURI(), "home.jsf"));
						//response.sendRedirect("home.jsf");
					}
				}
			}
			else {
				boolean skipInit = recreateInitBean(request, response);
				if (!skipInit) {
					// multilogin - redirect a pagina di scelta utente (identificazione matricola)
					
					if (uri.endsWith(".pf")) {
						String queryParams = getRequestQueryString(request);
						if (queryParams != null && !queryParams.equals(""))
							session.setAttribute("rewritedUri", uri + "?" + queryParams);
						else
							session.setAttribute("rewritedUri", uri);
					}
					
					response.sendRedirect(AppUtil.getSendRedirect(request.getRequestURI(), "init.jsf"));
					/*String[] parts = uri.split("\\/");
					if (parts != null && parts.length > 2)
						response.sendRedirect("/" + parts[1] + "/" + parts[2] + "/init.jsf");
					else
						response.sendRedirect("init.jsf");*/
				}
				else {
					String url = request.getRequestURL().toString();
					if (url.endsWith("javax.faces.resource/jsf.js.jsf") || url.endsWith("init.jsf") || isUserLoggedIn(userBean)) {
						chain.doFilter(request, response);
					}
					else {
						// richiesta di url interno all'applicazione senza aver identificato in maniera 
						// corretta l'utente (matricola vuota). Possibile caso di multilogin bypassata (apertura
						// di piu' schede del browser??)
						Logger.info("LoginFilter.doFilter(), force redirect to init.jsf (matricola is null): " + userBean.getLogin() + " : " + url);
						
						response.sendRedirect(AppUtil.getSendRedirect(request.getRequestURI(), "init.jsf"));
					}
				}
			}
		}
		else {
			chain.doFilter(request, response);
		}
			
		return;
	}
	
	/**
	 * verifica se l'utente corrente e' effettivamente loggato (in caso di login multipla
	 * se e' stato selezionato un nominativo dall'elenco)
	 */
	private boolean isUserLoggedIn(UserBean user) {
		return (user != null 
				&& user.getLogin() != null && !user.getLogin().isEmpty()
				&& user.getMatricola() != null && !user.getMatricola().isEmpty());
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}
	
	/**
	 * Estrazione del nome dell'applicazione (webapp) in base all'uri richiesto
	 * 
	 * @param uri
	 * @return
	 */
	private String extractAppName(String uri) {
		String[] parts = uri.split("\\/");
		if (parts != null && parts.length > 2 && !parts[2].equals("javax.faces.resource"))
			return parts[2];
		else
			return null;
	}
	
	/**
	 * Inizializza (o lo carica dalla sessione se presente) il bean dell'applicazione corrente 
	 * 
	 * @param request
	 * @param response
	 * @return false in caso di redirect su init.jsf (login con scelta matricola), true in caso di caricamento di qualsiasi altra pagina
	 */
	private boolean recreateInitBean(HttpServletRequest request, HttpServletResponse response) {
		boolean skipInit = true;
		
		String uri = request.getRequestURI();
		String[] parts = uri.split("\\/");
		
		String packageName = "";
		if (parts != null && parts.length > 2)
			packageName = parts[2];
		
		if (!packageName.equals("")) {
			try {
				String initBeanName = packageName + "Init";
				
				Init initBean = null;
				HttpSession session = request.getSession(false);
				if (session != null) {
					String appName = (String) session.getAttribute("appName");
					String curAppName = extractAppName(request.getRequestURI());
					
					// Reinizializza il bean solo se l'utente e' nullo (login) o e' 
					// cambiata l'applicazione (es. passaggio da docway ad acl)
					if (curAppName != null && (appName == null || !appName.equals(curAppName))) {
						Logger.info("LoginFilter.recreateInitBean(), primo caricamento dell'applicazione " + curAppName);
						
						if (session.getAttribute(initBeanName) != null) {
							initBean = (Init) session.getAttribute(initBeanName);
							
							Logger.info("LoginFilter.recreateInitBean(), applicazione " + curAppName + " in sessione, init non necessario");
						}
						
						if (initBean == null) {
							String className = Character.toUpperCase(initBeanName.charAt(0)) + initBeanName.substring(1);
							Class<?> initBeanClass = Class.forName(LoginFilter.DEFAULT_INIT_PACKAGE + "." + packageName + "." + className);
							
							Logger.info("LoginFilter.recreateInitBean(), init dell'applicazione " + curAppName + " [" + LoginFilter.DEFAULT_INIT_PACKAGE + "." + packageName + "." + className + "]");
							
							initBean = (Init) initBeanClass.newInstance();
							initBean.injectRequestAndResponse(request, response);
							skipInit = initBean.init();
							
							session.setAttribute(initBeanName, initBean);
						}
						
						session.setAttribute("appName", curAppName);
					}
				}
				
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}
		
		return skipInit;
	}
	
	/**
	 * invalida sessioni utente precedentemente aperte in caso di passaggio
	 * da un'applicazione ad un'altra
	 */
	private HttpSession invalidatePreviousSession(HttpSession session, HttpServletRequest request) {
		// viene invalidata la sessione nel caso in cui si passi da una applicazione ad un altra
		if (session != null && session.getAttribute(Const.DOCWAY_EMBEDDED_APP_NAME) != null) {
			// questa attivita' deve essere svolta solo in caso di caricamento dell'applicazione (accesso
			// a home.jsf o init.jsf). Tutti gli altri url possono essere condivisi fra applicazioni
			// differenti.
			// Questa attivita' deve inoltre essere svolta solamente se la chiamata non e' AJAX (tramite JSF e' possibile
			// che vengano effettuate chiamate AJAX alla root dell'applicazione)
			if (AppUtil.isPossibleApplicationSwitch(request)) {
				if (!AppUtil.isCurrentAppInSession(session, request)) {
					Logger.info("LoginFilter.invalidatePreviousSession(), INVALIDATE CURRENT SESSION");
					session.invalidate();
					session = request.getSession(true);
				}
			}
		}
		
		return session;
	}
	
	/**
	 * restituisce i parametri specificati nella request, ripuliti da eventuali parametri 
	 * di servizio (es. 'forceLogin' per forzare il caricamento dell'utente - possibile loop con prettyfaces
	 * se mantenuto 'forceLogin')
	 *  
	 * @param request
	 * @return
	 */
	private String getRequestQueryString(HttpServletRequest request) {
		String queryParams = "";
		if (request != null) {
			String params = request.getQueryString();
			if (params != null && params.length() > 0) {
				String[] paramsarray = params.split("&");
				if (paramsarray != null && paramsarray.length > 0) {
					for (int i=0; i<paramsarray.length; i++) {
						if (!paramsarray[i].startsWith("forceLogin"))
							queryParams += paramsarray[i] + "&";
						
						// FIXME gestire eventuali altri parametri da eliminare dalla request
					}
					
					if (queryParams.length() > 0)
						queryParams = queryParams.substring(0, queryParams.length()-1);
				}
			}
		}
		return queryParams;
	}
	
}
