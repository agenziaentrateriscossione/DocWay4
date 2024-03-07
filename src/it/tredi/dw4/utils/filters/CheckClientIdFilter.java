package it.tredi.dw4.utils.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.tredi.dw4.jsf.scope.ClientWindowUtils;
import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.DocWayProperties;

public class CheckClientIdFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		if (DocWayProperties.readProperty("abilitaCheckClientWindowId", "si").toLowerCase().equals("si")) {
			String uri = request.getRequestURI();
			
			// In caso di richiesta get ad una pagina .jsf viene rimosso il parametro jfwid se presente come unico parametro (probabile copia/incolla di un url da un tab ad un altro)
			if (request.getMethod().toLowerCase().equals("get")) {
				String params = request.getQueryString();
				if (uri != null && params != null) {
					// Se viene richiesto il caricamento in get di una pagina .jsf con passaggio del solo parametro relativo al clientId eseguo un 
					// redirect a home senza l'indicazione del parametro (probabile copia di un url da una scheda del browser su una nuova)
					
					if (uri.endsWith(".jsf") && params.indexOf(ClientWindowUtils.CLIENT_WINDOW_ID_PARAM_NAME) != -1 && params.indexOf("&") == -1) 
						response.sendRedirect(AppUtil.getSendRedirect(request.getRequestURI(), "index.jsp"));
				}
			}
			else if (uri.endsWith(".jsf") && !uri.endsWith("javax.faces.resource/jsf.js.jsf") && !ClientWindowUtils.isValidClientId(request)) {
				response.sendRedirect(AppUtil.getSendRedirect(request.getRequestURI(), "wrongClientId.jsp"));
			}
		}
		
		chain.doFilter(request, response);
		return;
	}
	
}
