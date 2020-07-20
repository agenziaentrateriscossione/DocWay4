package it.tredi.dw4.docway;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.beans.Init;
import it.tredi.dw4.docway.beans.DocWayHome;
import it.tredi.dw4.docway.beans.Menu;

public class DocwayInit extends Init {
	
	public DocwayInit() throws Exception {}
	
	protected void initDocWayHomeFromResponse(XMLDocumento response) throws Exception {
		UserBean userbean = getUserBean();
		if (null != userbean && null == userbean.getUserInfo()) { 
			if (userbean.getMatricola() == null || userbean.getMatricola().equals(""))
				userbean.setMatricola(response.getRootElement().attributeValue("matricola", "")); // assegnazione della matricola
			userbean.setUserInfo(response.getRootElement().attributeValue("userInfo", null));
			setSessionAttribute("userBean", userbean);
		}
		
		boolean enableIWX = isEnabledIWX(); // abilitazione o meno di IWX
		
		DocWayHome docwayHome = new DocWayHome();
		docwayHome.getFormsAdapter().fillFormsFromResponse(response);
		
		// disattivazione di IWX da profilo personale
		if (docwayHome.getFormsAdapter().checkBooleanFunzionalitaDisponibile("disabilitaIWX", false))
			enableIWX = false;
		
		docwayHome.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
		docwayHome.init(response.getDocument());
		setSessionAttribute("docwayHome", docwayHome);
		
		Menu docwaymenu = new Menu();
		docwaymenu.getFormsAdapter().fillFormsFromResponse(response);
		docwaymenu.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
		setSessionAttribute("docwaymenu", docwaymenu);
	}

	@Override
	protected String redirectFromMatricolaLogin() {
		return "show@docway_home";
	}
	
	
}
