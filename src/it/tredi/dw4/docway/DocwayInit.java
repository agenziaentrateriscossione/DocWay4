package it.tredi.dw4.docway;

import it.tredi.dw4.beans.Init;
import it.tredi.dw4.docway.beans.DocWayHome;
import it.tredi.dw4.docway.beans.Menu;
import it.tredi.dw4.utils.XMLDocumento;

public class DocwayInit extends Init {

	public DocwayInit() throws Exception {}

	protected void initDocWayHomeFromResponse(XMLDocumento response) throws Exception {
		initUserBeanData(getUserBean(), response);

		boolean enableIWX = isEnabledIWX(); // abilitazione o meno di IWX

		DocWayHome docwayHome = new DocWayHome();
		docwayHome.getFormsAdapter().fillFormsFromResponse(response);

		// disattivazione di IWX da profilo personale
		if (docwayHome.getFormsAdapter().checkBooleanFunzionalitaDisponibile("disabilitaIWX", false))
			enableIWX = false;

		docwayHome.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
		docwayHome.injectRequestAndResponse(getRequest(), getResponse());
		docwayHome.init(response.getDocument());
		setSessionAttribute("docwayHome", docwayHome);

		Menu docwaymenu = new Menu();
		docwaymenu.getFormsAdapter().fillFormsFromResponse(response);
		docwaymenu.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
		setSessionAttribute("docwaymenu", docwaymenu);
	}

	@Override
	public String redirectFromMatricolaLogin() {
		return "show@docway_home";
	}


}
