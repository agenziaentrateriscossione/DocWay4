package it.tredi.dw4.soginSAP;

import java.util.Properties;

import it.tredi.dw4.beans.Init;
import it.tredi.dw4.docway.beans.DocWayHome;
import it.tredi.dw4.docway.beans.Menu;
import it.tredi.dw4.soginSAP.beans.SoginSAPConnector;
import it.tredi.dw4.soginSAP.beans.SoginSAPQuery;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.XMLDocumento;

public class SoginSAPInit extends Init {
	
	public SoginSAPInit() throws Exception {
//		try {
//			this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
//		}
//		catch (Throwable t) {
//			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
//			setFatalLevelInErrormsg();
//			return;			
//		}		
	}
	
	protected void initDocWayHomeFromResponse(XMLDocumento response) throws Exception {
		initUserBeanData(getUserBean(), response);
		
		boolean enableIWX = isEnabledIWX(); // abilitazione o meno di IWX
		
		// mbernardini 25/11/2015 : caricamento delle properties di soginSAP tramite configuratore
		Properties soginSAPProperties = DocWayProperties.getProperties(DocWayProperties.SOGINSAP_NAMESPACE);
		setSessionAttribute("soginSAPProperties", soginSAPProperties);
		
		SoginSAPQuery soginSAPQuery = new SoginSAPQuery();
		soginSAPQuery.injectRequestAndResponse(this.request, this.response);
		soginSAPQuery.getFormsAdapter().fillFormsFromResponse(response);
		
		// disattivazione di IWX da profilo personale
		if (soginSAPQuery.getFormsAdapter().checkBooleanFunzionalitaDisponibile("disabilitaIWX", false))
			enableIWX = false;
		
		soginSAPQuery.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
		soginSAPQuery.init(response.getDocument());
		soginSAPQuery.gotoTableQGlobale();
		setSessionAttribute("soginSAPQuery", soginSAPQuery);
		
		SoginSAPConnector soginSAPConnector = new SoginSAPConnector();
		setSessionAttribute("soginSAPConnector", soginSAPConnector);
		
		if (getSessionAttribute("docwayHome") == null) {
			DocWayHome docwayHome = new DocWayHome();
			docwayHome.getFormsAdapter().fillFormsFromResponse(response);
			
			// disattivazione di IWX da profilo personale
			if (docwayHome.getFormsAdapter().checkBooleanFunzionalitaDisponibile("disabilitaIWX", false))
				enableIWX = false;
			
			docwayHome.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
			docwayHome.init(response.getDocument());
			setSessionAttribute("docwayHome", docwayHome);
		}
		
		if (getSessionAttribute("docwaymenu") == null) {
			Menu docwaymenu = new Menu();
			docwaymenu.getFormsAdapter().fillFormsFromResponse(response);
			docwaymenu.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
			setSessionAttribute("docwaymenu", docwaymenu);
		}
	}
		

	@Override
	protected String redirectFromMatricolaLogin() {
		return "home.jsf";
	}

}
