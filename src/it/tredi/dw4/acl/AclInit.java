package it.tredi.dw4.acl;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.AclHierBrowser;
import it.tredi.dw4.acl.beans.AclHome;
import it.tredi.dw4.acl.beans.Menu;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Init;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;

public class AclInit extends Init {

	public AclInit() throws Exception {
		try {
			this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			setFatalLevelInErrormsg();
			return;
		}
	}

	@Override
	protected void initDocWayHomeFromResponse(XMLDocumento response)
			throws Exception {
		UserBean userbean = getUserBean();
		if (null != userbean && null == userbean.getUserInfo()) {
			if (userbean.getMatricola() == null || userbean.getMatricola().equals(""))
				userbean.setMatricola(response.getRootElement().attributeValue("matricola", "")); // assegnazione della matricola
			userbean.setUserInfo(response.getRootElement().attributeValue("userInfo", null));
			setSessionAttribute("userBean", userbean);
		}

		AclHome aclHome = new AclHome();
		aclHome.getFormsAdapter().fillFormsFromResponse(response);
		aclHome.init(response.getDocument());
		setSessionAttribute("aclHome", aclHome);

		// widget gerarchia
		String aclDb = "";
		if (formsAdapter.getDefaultForm().getParam("db") != null && formsAdapter.getDefaultForm().getParam("db").length() > 0) // caricamento gerarchia su archivio ACL con nome personalizzato
			aclDb = formsAdapter.getDefaultForm().getParam("db");
		formsAdapter.paginaBrowse("-1", true, "20", aclDb);

		XMLDocumento responseDoc = formsAdapter.getHierBrowserForm().executePOST(getUserBean());
		// TODO - non è inserito il controllo di errore

		// istanzia il bean della pagina della gerarchia
		AclHierBrowser hierBrowser = new AclHierBrowser();
		// riempi il formsAdapter della pagina di destinazione
		hierBrowser.getFormsAdapter().fillFormsFromResponse(responseDoc);
		hierBrowser.init(responseDoc.getDocument());

		setSessionAttribute("hierBrowser", hierBrowser);

		Menu menu = new Menu();
		menu.getFormsAdapter().fillFormsFromResponse(response);
		setSessionAttribute("menu", menu);
	}

	@Override
	protected String redirectFromMatricolaLogin() {
		return "show@acl_home";
	}

}
