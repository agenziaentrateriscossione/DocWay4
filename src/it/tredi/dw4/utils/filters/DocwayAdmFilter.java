package it.tredi.dw4.utils.filters;

import it.tredi.dw4.docway.DocwayInitAdm;
import it.tredi.dw4.utils.AppUtil;
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

public class DocwayAdmFilter implements Filter {

	@Override
	public void init(FilterConfig config) throws ServletException {
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		
		try {
			String uri = request.getRequestURI();
			if (uri.endsWith("initadm.jsf")) {
				HttpSession session = request.getSession(false);
				
				// caricamento della pagina di selezione dell'utente da impersonificare
				if (session.getAttribute("docwayInitAdm") == null) {
					Logger.info("DocwayAdmFilter.doFilter() : richiesto accesso ADM : " + uri);
					
					DocwayInitAdm docwayInitAdm = new DocwayInitAdm();
					docwayInitAdm.injectRequestAndResponse(request, (HttpServletResponse) res);
					docwayInitAdm.init(AppUtil.loadAuthenticationRemoteUser(request));
						
					session.setAttribute("docwayInitAdm", docwayInitAdm);
				}
				
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		
		chain.doFilter(req, res);
	}

	
	@Override
	public void destroy() {
	}

}
