package it.tredi.dw4.jsf.scope;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.Logger;

/**
 * Utilities per l'identificazione del tab del browser dal quale provengono le richieste ed eventuale disattivazione di schede vecchie
 * @author mbernardini
 */
public class ClientWindowUtils {
	
	public static final String CLIENT_WINDOW_ID_PARAM_NAME = "jfwid";
	
	private static final String CLIENT_WINDOW_ID_SESSIONE_ATTRIBUTE_SUFFIX = "_clientWindowId";
	
	/**
	 * Recupero dell'identificativo del client (tramite FacesContext). Possibile solo se attiva la gestione tramite 'url' 
	 * di 'javax.faces.CLIENT_WINDOW_MODE'
	 * @return
	 */
	public static String getClientId() {
		String id = null;
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			//id = context.getViewRoot().getClientId();
			ClientWindow client = context.getExternalContext().getClientWindow();
			if (client != null)
				id = client.getId();
		}
		return id;
	}
	
	/**
	 * Aggiunta del parametro 'jfwid' ad un url
	 * @param uri
	 * @return
	 */
	public static String addClientIdToUri(String uri) {
		if (uri != null && !uri.isEmpty()) {
			if (uri.indexOf(CLIENT_WINDOW_ID_PARAM_NAME) == -1) {
				// l'uri non contiene l'identificativo del client
				
				String id = getClientId();
				if (id != null) {
					// la gestione dell'identificativo del client e' attiva e il facescontext e' raggiungibile
					if (uri.contains("?"))
						uri += "&" + CLIENT_WINDOW_ID_PARAM_NAME + "=" + id;
					else
						uri += "?" + CLIENT_WINDOW_ID_PARAM_NAME + "=" + id;
					
					Logger.info("ClientWindowUtils.addClientIdToUri(): updated uri = " + uri);
				}
			}
		}
		return uri;
	}
	
	/**
	 * Verifica della validitaì del clientId tramite analisi dei dati recuperati dalla request HTTP
	 * @param request
	 * @return
	 */
	public static boolean isValidClientId(HttpServletRequest request) {
		boolean valid = true;
		if (request != null) {
			valid = isValidClientId(
					AppUtil.getAppName(request), 
					request.getSession(false), 
					request.getParameter(CLIENT_WINDOW_ID_PARAM_NAME));
		}
		return valid;
	}
	
	/**
	 * Verifica della validita' del clientId tramite analisi dei dati estratti dal FacesContext
	 * @return
	 */
	public static boolean isValidClientId() {
		boolean valid = true;
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null) {
			if (context.getExternalContext() != null) {
				valid = isValidClientId(
						AppUtil.getAppName((HttpServletRequest) context.getExternalContext().getRequest()), 
						(HttpSession) context.getExternalContext().getSession(false), 
						getClientId());
			}
		}
		return valid;
	}
	
	/**
	 * Verifica della validita' del clientId. L'identificativo derivante dalla chiamata corrisponde all'ultimo aperto tramite il browser. Vengono
	 * invalidate tutte le richieste provenienti da vecchie schede del browser (utilizzo multischeda di DocWay non previsto)
	 * @param appName
	 * @param session
	 * @param clientId
	 * @return
	 */
	private static boolean isValidClientId(String appName, HttpSession session, String clientId) {
		boolean valid = true;
		if (clientId != null && !clientId.isEmpty()) {
			if (session != null) {
				String sessionClientId = (String) session.getAttribute(appName + CLIENT_WINDOW_ID_SESSIONE_ATTRIBUTE_SUFFIX);
				
				boolean validSession = true;
				if (session.getAttribute("userBean") == null)
					validSession = false;
				
				if (sessionClientId != null && !sessionClientId.isEmpty()) {
					try {
						int currentCount = extractCounterFromClientId(clientId);
						int sessionCount = extractCounterFromClientId(sessionClientId);
						
						if (currentCount < sessionCount)
							valid = false;
					}
					catch (Exception e) {
						Logger.error("ClientWindowUtils.isValidClientId(): got exception... " + e.getMessage(), e);
						valid = false;
					}
				}
				
				// Se il clientId risulta valido ma differente da quello in sessione, aggiorno il valore in sessione
				if (valid && validSession && (sessionClientId == null || !sessionClientId.equals(clientId)))
					session.setAttribute(appName + CLIENT_WINDOW_ID_SESSIONE_ATTRIBUTE_SUFFIX, clientId);
			}
		}
		
		return valid;
	}
	
	/**
	 * Dato l'identificativo di un client (identificativo di un tab) recupera il contatore della scheda
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private static int extractCounterFromClientId(String id) throws Exception {
		int counter = 0;
		if (id != null && !id.isEmpty()) {
			int index = id.lastIndexOf(":");
			if (index != -1)
				counter = Integer.parseInt(id.substring(index+1));
			else
				throw new Exception("Unknown format for clientId " + id);
		}
		return counter;
	}
	
	/**
	 * Reset dell client id memorizzato in sessione
	 * @param session
	 * @param appName
	 */
	public static void resetSessionClienId(HttpSession session, String appName) {
		if (session != null)
			session.removeAttribute(appName + CLIENT_WINDOW_ID_SESSIONE_ATTRIBUTE_SUFFIX);
	}
	
}
