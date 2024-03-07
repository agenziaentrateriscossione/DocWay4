package it.tredi.dw4.docway;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.Logger;

public class DocwayInitAdm extends Page {

	private DocDocWayQueryFormsAdapter formsAdapter;

	private String xml = "";

	private String archivio = ""; // identifica l'archivio al quale si sta facendo accesso
	private String login = "";

	public DocwayInitAdm() throws Exception {
		try {
			this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayAdmService"));
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			setFatalLevelInErrormsg();
			return;
		}
	}

	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getArchivio() {
		return archivio;
	}

	public void setArchivio(String db) {
		this.archivio = db;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Metodo eseguito per il caricamento di ADM e inizializzazione di tutti i bean necessari al caricamento della pagina
	 *
	 * @throws Exception
	 */
	public void init(String remoteUser) throws Exception {

		// caricamento su formsAdapter dei parametri che arrivano da url (tipo nome del db)
		if (request != null) {
			String appName = AppUtil.getAppName(request);

			String db = getDbForApp(appName);
			formsAdapter.getDefaultForm().addParam("db", db); // gestione db dell'applicazione (aggiunta del parametro al formadapter)
			archivio = db;

			// caricamento dell'eventuale parametro di debug
			String debug = (String) request.getParameter("debug");
			if (debug != null && debug.length() > 0)
				setSessionAttribute("debugMode", debug);
		}

		UserBean user = new UserBean(remoteUser);
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(user);
		Logger.info("DocwayInitAdm.init(), caricamento ADM tramite chiamata a service per l'utente " + user.getLogin());

		if (handleErrorResponse(response)) {
			Logger.info("DocwayInitAdm.init(), errore riscontrato");
			setFatalLevelInErrormsg();
		}

		this.xml = response.asXML();
		// TODO da completare, viene restituito un HTML dal service..
	}

	/**
	 * Accesso a docway tramite ADM
	 * @return
	 * @throws Exception
	 */
	public String accedi() throws Exception {
		try {
			UserBean userBean = new UserBean(login);
			setSessionAttribute("userBean", userBean);

			formsAdapter.getDefaultForm().addParam("login", login);

			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(userBean);
			Logger.info("DocwayInitAdm.accedi(), impersonificazione di " + login);

			if (handleErrorResponse(response)) {
				Logger.info("DocwayInitAdm.init(), errore riscontrato");
				setFatalLevelInErrormsg();
			}

			setSessionAttribute("admLogin", login);

			redirectToJsf("home", response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		return null;
	}

}
