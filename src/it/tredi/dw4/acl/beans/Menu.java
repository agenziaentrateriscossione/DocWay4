package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.acl.beans.DelegheTitles;
import it.tredi.dw4.docway.beans.QueryVersioni;
import it.tredi.dw4.utils.DocWayProperties;

public class Menu extends Page {

	private AclQueryFormsAdapter formsAdapter;

	private String urlPaginaWikiManualeACL = ""; // pagina wiki di manuale online per docway

	public AclQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public Menu() throws Exception {
		this.formsAdapter = new AclQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));

		this.urlPaginaWikiManualeACL = DocWayProperties.readProperty("urlPaginaWikiManualeACL", "");
	}

	public String getUrlPaginaWikiManualeACL() {
		return urlPaginaWikiManualeACL;
	}

	public void setUrlPaginaWikiManualeACL(String urlPaginaWikiManualeACL) {
		this.urlPaginaWikiManualeACL = urlPaginaWikiManualeACL;
	}

	public String gotoTableQStrutturaInterna() throws Exception {
		formsAdapter.gotoTableQ("struttura_interna", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificQueryPageAndReturnNavigationRule("struttura_interna", responseDoc);
	}

	public String gotoTableQStrutturaEsterna() throws Exception {
		formsAdapter.gotoTableQ("struttura_esterna", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());

		return buildSpecificQueryPageAndReturnNavigationRule("struttura_esterna", responseDoc);
	}

	public String gotoTableQPersonaInterna() throws Exception {
		formsAdapter.gotoTableQ("persona_interna", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());

		return buildSpecificQueryPageAndReturnNavigationRule("persona_interna", responseDoc);
	}

	public String gotoTableQPersonaEsterna() throws Exception {
		formsAdapter.gotoTableQ("persona_esterna", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificQueryPageAndReturnNavigationRule("persona_esterna", responseDoc);
	}

	public String gotoTableQProfilo() throws Exception {
		formsAdapter.gotoTableQ("persona_interna", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificQueryPageAndReturnNavigationRule("profilo", responseDoc);
	}

	public String gotoTableQRuolo() throws Exception {
		formsAdapter.gotoTableQ("ruolo", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificQueryPageAndReturnNavigationRule("ruolo", responseDoc);
	}

	public String gotoTableQGruppo() throws Exception {
		formsAdapter.gotoTableQ("gruppo", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificQueryPageAndReturnNavigationRule("gruppo", responseDoc);
	}

	public String gotoTableQComune() throws Exception {
		formsAdapter.gotoTableQ("comune", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificQueryPageAndReturnNavigationRule("comune", responseDoc);
	}

	public String gotoTableQAoo() throws Exception {
		formsAdapter.gotoTableQ("aoo", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificQueryPageAndReturnNavigationRule("aoo", responseDoc);
	}

	public String gotoTableQCasellaPostaElettronica() throws Exception {
		formsAdapter.gotoTableQ("casellaPostaElettronica", false);

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificQueryPageAndReturnNavigationRule("casellaPostaElettronica", responseDoc);
	}

	public String paginaBrowseGerarchia() throws Exception {
		formsAdapter.paginaBrowse("0", true, "20", formsAdapter.getDefaultForm().getParam("db"));

		XMLDocumento responseDoc = formsAdapter.getHierBrowserForm().executePOST(getUserBean());

		//istanzia il bean della pagina della gerarchia
		AclHierBrowser hierBrowser = new AclHierBrowser();
		//riempi il formsAdapter della pagina di destinazione
		hierBrowser.getFormsAdapter().fillFormsFromResponse(responseDoc);
		hierBrowser.init(responseDoc.getDocument());

		setSessionAttribute("hierBrowser", hierBrowser);

		return "hierBrowser";
	}

	public String insTableDocStrutturaInterna() throws Exception {
		formsAdapter.insTableDoc("struttura_interna");

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("struttura_interna", responseDoc, false);
	}

	public String insTableDocStrutturaEsterna() throws Exception {
		formsAdapter.insTableDoc("struttura_esterna");

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());

		return buildSpecificDocEditPageAndReturnNavigationRule("struttura_esterna", responseDoc, false);
	}

	public String insTableDocPersonaInterna() throws Exception {
		formsAdapter.insTableDoc("persona_interna");

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("persona_interna", responseDoc, false);
	}

	public String insTableDocPersonaEsterna() throws Exception {
		formsAdapter.insTableDoc("persona_esterna");

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("persona_esterna", responseDoc, false);
	}

	public String insTableDocProfilo() throws Exception {
		formsAdapter.insTableDoc("profilo");

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("profilo", responseDoc, false);
	}

	public String insTableDocRuolo() throws Exception {
		formsAdapter.insTableDoc("ruolo");

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("ruolo", responseDoc, false);
	}

	public String insTableDocGruppo() throws Exception {
		formsAdapter.insTableDoc("gruppo");

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("gruppo", responseDoc, false);
	}

	public String insTableDocComune() throws Exception {
		formsAdapter.insTableDoc("comune");

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("comune", responseDoc, false);
	}

	public String insTableDocAoo() throws Exception {
		formsAdapter.insTableDoc("aoo");

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(responseDoc)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return buildSpecificDocEditPageAndReturnNavigationRule("aoo", responseDoc, false);
	}

	public String queryAccount() throws Exception{
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "[UD,/xw/@UdType/]=account";
				return aclHome.queryPlain(query);
			}
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return null;
	}

	/**
	 * Ritorna la storia delle versioni di DocWay
	 * @return
	 * @throws Exception
	 */
	public String showVersioni() throws Exception {
		try {
			XMLDocumento response = formsAdapter.getDefaultForm().getVersioneApp("xdocway");
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			QueryVersioni queryVersioni = new QueryVersioni();
			queryVersioni.getFormsAdapter().fillFormsFromResponse(response);
			queryVersioni.init(response.getDocument());
			setSessionAttribute("queryVersioni", queryVersioni);

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Apertura del menù relativo alla gestione del controllo gestione deleghe (AMMINISTRAZIONE)
	 *
	 * @return
	 * @throws Exception
	 */
	public String gotoControlloDeleghe() throws Exception {
		try {
			formsAdapter.gotoControlloDeleghe();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			DelegheTitles delegheTitles = new DelegheTitles();
			delegheTitles.getFormsAdapter().fillFormsFromResponse(response);
			delegheTitles.init(response.getDocument());
			setSessionAttribute("delegheTitles", delegheTitles);
			return "acl_showdeleghetitles";

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
}
