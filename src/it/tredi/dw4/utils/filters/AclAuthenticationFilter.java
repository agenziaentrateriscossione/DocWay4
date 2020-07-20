package it.tredi.dw4.utils.filters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Element;

public class AclAuthenticationFilter implements Filter {

	// parametri di connessione all'acl per l'autenticazione
	private FormAdapter formAdapter = null;
	private String authLogin = ""; // login da utilizzare per la ricerca dell'utente in ACL
	private String authMatricola = ""; // matricola da utilizzare per la ricerca dell'utente in ACL
	private String xpathUserid = "/persona_interna/extra/userid";
	private String xpathPassword = "/persona_interna/extra/password";
	private boolean passwordEncrypted = false;
	
	private boolean forceOneUserId = false; // se true deve esistere una sola persona in ACL con la login specificata
	
	/**
	 * caricamento dei parametri di inizializzazione del filtro (caricati da web.xml)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		try {
			AdapterConfig adapterConfig = AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService");
			formAdapter = new FormAdapter(adapterConfig.getHost(), adapterConfig.getPort(), adapterConfig.getProtocol(), adapterConfig.getResource(), adapterConfig.getUserAgent());
			
			authLogin = config.getInitParameter("authLogin");
			authMatricola = config.getInitParameter("authMatricola");
			if (authMatricola == null)
				authMatricola = "";
			if (config.getInitParameter("xpathUserid") != null && config.getInitParameter("xpathUserid").length() > 0)
				xpathUserid = config.getInitParameter("xpathUserid");
			if (config.getInitParameter("xpathUserid") != null && config.getInitParameter("xpathUserid").length() > 0)
				xpathUserid = config.getInitParameter("xpathUserid");
			
			if (DocWayProperties.readProperty("acl.authaddon.password.encryption", "no").toLowerCase().equals("si"))
				passwordEncrypted = true;
			
			if (config.getInitParameter("forceOneUserId") != null && config.getInitParameter("forceOneUserId").equals("true"))
				forceOneUserId = true;
		}
		catch (Throwable t) {
			Logger.error("AclAuthenticationFilter.init(): " + t.getMessage(), t);
		}
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		HttpSession session = request.getSession(true);
		
		if (session != null && session.getAttribute("userBean") != null) { // ricerca utente in sessione
		
			Logger.info("AclAuthenticationFilter.doFilter(), user " + ((UserBean)session.getAttribute("userBean")).getLogin() + " already found in session");
		}
		else {
			// recupero dei parametri di username e password dalla request
			String username = request.getParameter("sqluser");
			String password = request.getParameter("sqlpwd");
			
			// encrypt della password specificata dall'utente
			if (passwordEncrypted) 
				password = ecryptPassword(password);
			
			if (username != null && username.length() > 0 && password != null && password.length() > 0) {
				if (authenticateUser(username, password)) { // autenticazione utente tramite controllo di username e password
					UserBean userBean = new UserBean(username);
					session.setAttribute("userBean", userBean);
					
					Logger.info("AclAuthenticationFilter.doFilter(), login for user " + username + " is completed! User added in session");
				}
				else {
					Logger.info("AclAuthenticationFilter.doFilter(), forward to loginErrorSql.jsp");
					request.getRequestDispatcher("../loginErrorSql.jsp").forward(request, response);
					return;
				}
			}
			else {
				Logger.info("AclAuthenticationFilter.doFilter(), forward to loginSql.jsp");
				request.getRequestDispatcher("../loginSql.jsp").forward(request, response);
				return;
			}
		}
		
		request.setAttribute("authentication_mode", "SQLAUTH");
		chain.doFilter(request, response);
	}
	
	/**
	 * Controllo username e password in base chiamata al service (recupero persona interna da ACL)
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	private boolean authenticateUser(String username, String password) {
		if (authLogin == null || authLogin.length() == 0 || formAdapter == null) {
			Logger.error("AclAuthenticationFilter.authenticateUser(): empty params: " + authLogin);
			return false;
		}
		
		try {
			formAdapter.addParam("verbo", "queryplain");
			formAdapter.addParam("xverb", "");
			formAdapter.addParam("query", "([" + xpathUserid + "]=\"" + username + "\")");
			
			UserBean authUser = new UserBean(authLogin);
			if (authMatricola.length() > 0)
				authUser.setMatricola(authMatricola);
			XMLDocumento response = formAdapter.executePOST(authUser);
			
			Logger.debug("AclAuthenticationFilter.authenticateUser(): authentication response: " + response.asXML());
			
			// se non e' impostato lo userId unico si procede con il recupero del primo titolo nel caso di piu' utenti
			// trovati con lo stesso userId
			if (!forceOneUserId && response.getRootElement().selectNodes("/response/titolo").size() > 0)
				response = getUserFromTitles(response);
			
			String acluserid = "";
			Element useridElement = (Element) response.getRootElement().selectSingleNode("/response" + xpathUserid);
			if (useridElement != null)
				acluserid = useridElement.getTextTrim();
			String aclpassword = "";
			Element passwordElement = (Element) response.getRootElement().selectSingleNode("/response" + xpathPassword);
			if (passwordElement != null)
				aclpassword = passwordElement.getTextTrim();
			
			if (acluserid != null && acluserid.length() > 0
					&& aclpassword != null && aclpassword.length() > 0) {
				
				if (username.equals(acluserid) && password.equals(aclpassword))
					return true;
				else
					return false;
			}
		}
		catch (Throwable t) {
			Logger.error("AclAuthenticationFilter.authenticateUser(): " + t.getMessage(), t);
		}
		
		return false;
	}
	
	/**
	 * Dato un XML di risposta contenente una lista titoli, si procede con il caricamento
	 * del primo titolo
	 * 
	 * @param response
	 * @return
	 */
	private XMLDocumento getUserFromTitles(XMLDocumento response) throws Exception {
		// <titolo indice="1" testo="Secondo Amministratore    - " dbTable="persona_interna" db="acl"/>
		Element title = (Element) response.getRootElement().selectNodes("/response/titolo").get(0); // recupero del primo titolo
		
		int indice = 0; // caricamento del primo titolo
		String dbTable = title.attributeValue("dbTable", "");
		String db = title.attributeValue("db", "");
		
		formAdapter.addParam("selid", response.getRootElement().attributeValue("selid", ""));
		formAdapter.addParam("verbo", "showdoc");
		formAdapter.addParam("pos", indice);
		formAdapter.addParam("db", db);
		if (dbTable != null)
			formAdapter.addParam("dbTable", dbTable);
		
		UserBean authUser = new UserBean(authLogin);
		if (authMatricola.length() > 0)
			authUser.setMatricola(authMatricola);
		response = formAdapter.executePOST(authUser);
		
		return response;
	}
	
	/**
	 * Encrypt della password specificata dall'utente tramite il form di login
	 * @param password
	 * @return
	 */
	private String ecryptPassword(String password) {
		try {
			if (password != null && password.length() > 0) {
				password = DigestUtils.md5Hex(password); // TODO gestire altri algoritmi di encryption
			}
		}
		catch (Throwable t) {
			Logger.error("AclAuthenticationFilter.ecryptPassword(): " + t.getMessage(), t);
		}
		
		return password;
	}
	
	@Override
	public void destroy() {
	}

}
