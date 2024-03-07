package it.tredi.dw4.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLDocumento;

/**
 * Servlet di test di tutto lo stack applicativo: DocWay4 -> DocWay4-service -> eXtraWay 
 * @author mbernardini
 */
public class StackTesterServlet extends HttpServlet {

	private static final long serialVersionUID = -382256575834239267L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			
			String login = request.getParameter("login");
			if (login == null)
				login = "";
			String matricola = request.getParameter("matricola");
			if (matricola == null)
				matricola = "";
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("verbo", "stackTest");
			params.put("xverb", "testconnection");
			
			String testResponse = redirectRequestToService(login, matricola, params);
			
			response.getOutputStream().write(testResponse.getBytes());
			response.getOutputStream().flush();
			response.getOutputStream().close();
			
		}
		catch (Exception e) {
			Logger.error("StackTester error: got exception... " + e.getMessage(), e);
			
			response.getOutputStream().write(new String("KO").getBytes());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}

	/**
	 * Redirect della richiesta di test a DocWay4-Service
	 * 
	 * @param login login dell'utente corrente
	 * @param matricola matricola dell'utente corrente
	 * @param params parametri di tipo stringa da includere nella request
	 * 
	 * @return 'OK' in caso di successo, 'KO' in caso di errore
	 * @throws Exception
	 */
	private String redirectRequestToService(String login, String matricola, Map<String, String> params) throws Exception {
		AdapterConfig config = AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService");
		FormAdapter formAdapter = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
		
		if (params != null && !params.isEmpty())
			formAdapter.addParams(params);
		
		UserBean user = null;
		if (login != null && !login.isEmpty()) {
			user = new UserBean(login);
			if (matricola != null && !matricola.isEmpty())
				user.setMatricola(matricola);
		}
		
		XMLDocumento response = null;
		if (user != null)
			response = formAdapter.executePOST(user);
		else
			response = formAdapter.executeGET();
		
		String out = "OK";
		if (response == null || ErrormsgFormsAdapter.isResponseErrorMessage(response)) 
			out = "KO";
		
		return out;
	}
	
}
