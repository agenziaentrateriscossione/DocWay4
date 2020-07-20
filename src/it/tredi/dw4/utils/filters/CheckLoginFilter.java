package it.tredi.dw4.utils.filters;

import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.Logger;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * filtro di verifica login. controlla che effettivamente l'utente risulti loggato in base
 * alle configurazioni del server (autenticazione integrata, tomcat user, autenticazione esterna
 * sql, ecc.)
 * 
 * @author mbernardini
 */
public class CheckLoginFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		//FIXME: messa a true la creazione della sessione
		HttpSession session = request.getSession(true);
		if (request.getParameter("forceLogin") != null && request.getParameter("forceLogin").equals("true"))
			invalidateSession(session);
		
		if (request.getRequestURI().contains("login.jsp")) {
			chain.doFilter(request, response);
			return;
		}
		
		String authMode = "";
		if (request.getAttribute("authentication_mode") != null) // sistemi di autenticazione alternativi
			authMode = (String) request.getAttribute("authentication_mode");
		
		String user = AppUtil.loadAuthenticationRemoteUser(request, authMode);
		
		if (session != null && (!session.isNew() || (authMode.equals("SQLAUTH")) || (user != null && !user.isEmpty()))) {
//			String user = AppUtil.loadAuthenticationRemoteUser(request, authMode);
			
//			if (user != null && user.length() > 0) { // utente riconosciuto si procede con i filtri di init...
				
				chain.doFilter(request, response);
				return;
//			}
		}
		
		Logger.info("CheckLoginFilter.doFilter(), redirect a login.jsp");
		//logout(request, response);
		
		// gestita la costruzione dell'uri per login in caso di template personalizzati
		// presenti in sottocartelle. Si rischierebbe di caricare una login.jsp interna
		// ad una cartella personalizzata di un cliente (file non presente)
		response.sendRedirect(AppUtil.getSendRedirect(request.getRequestURI(), "index.jsp"));
		//response.sendRedirect("login.jsp");
	}
	
	/**
	 * invalida la sessione corrente (cancellazione di tutti gli attributi in sessione)
	 * @param session
	 */
	private void invalidateSession(HttpSession session) {
		Logger.info("CheckLoginFilter.invalidateSession(), remove all attributes in session...");
		
		if (session != null) {
			Enumeration<?> attributes = session.getAttributeNames();
			while (attributes.hasMoreElements()) {
				String name = (String) attributes.nextElement();
				if (name != null && name.length() > 0) {
					session.removeAttribute(name);
				}
			}
		}
	}
	
	@Override
	public void destroy() {
	}

}
