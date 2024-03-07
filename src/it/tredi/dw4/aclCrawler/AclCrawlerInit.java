package it.tredi.dw4.aclCrawler;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.AclHierBrowser;
import it.tredi.dw4.acl.beans.AclHome;
import it.tredi.dw4.acl.beans.Menu;
import it.tredi.dw4.aclCrawler.beans.AclCrawlerHierBrowser;
import it.tredi.dw4.aclCrawler.beans.AclCrawlerHome;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Init;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;

public class AclCrawlerInit extends Init {

	public AclCrawlerInit() throws Exception {
		try {
			this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			setFatalLevelInErrormsg();
			return;
		}
	}

	protected void initDocWayHomeFromResponse(XMLDocumento response) throws Exception {
		initUserBeanData(getUserBean(), response);

		boolean enableIWX = isEnabledIWX(); // abilitazione o meno di IWX

		AclCrawlerHome aclCrawlerHome = new AclCrawlerHome();
		aclCrawlerHome.injectRequestAndResponse(this.request, this.response);
		aclCrawlerHome.getFormsAdapter().fillFormsFromResponse(response);

		// disattivazione di IWX da profilo personale
		if (aclCrawlerHome.getFormsAdapter().checkBooleanFunzionalitaDisponibile("disabilitaIWX", false))
			enableIWX = false;

		aclCrawlerHome.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
		aclCrawlerHome.init(response.getDocument());
		setSessionAttribute("aclCrawlerHome", aclCrawlerHome);

		if (getSessionAttribute("aclHome") == null) {
			AclHome aclHome = new AclHome();
			aclHome.getFormsAdapter().fillFormsFromResponse(response);
			aclHome.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
			aclHome.setXml(response.asXML());
			setSessionAttribute("aclHome", aclHome);
		}

		// widget gerarchia
		String aclDb = "";
		if (formsAdapter.getDefaultForm().getParam("db") != null && formsAdapter.getDefaultForm().getParam("db").length() > 0) // caricamento gerarchia su archivio ACL con nome personalizzato
			aclDb = formsAdapter.getDefaultForm().getParam("db");
		formsAdapter.paginaBrowse("-1", true, "20", aclDb);

		XMLDocumento responseDoc = formsAdapter.getHierBrowserForm().executePOST(getUserBean());
		// TODO - non è inserito il controllo di errore

		// istanzia il bean della pagina della gerarchia
		AclHierBrowser hierBrowser = new AclCrawlerHierBrowser();
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
		return "home.jsf";
	}

}
