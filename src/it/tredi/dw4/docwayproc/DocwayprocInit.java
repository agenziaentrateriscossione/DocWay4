package it.tredi.dw4.docwayproc;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.beans.Init;
import it.tredi.dw4.docwayproc.beans.DocwayprocHome;
import it.tredi.dw4.docwayproc.beans.Menu;

public class DocwayprocInit extends Init {

	public DocwayprocInit() throws Exception {}
	
	@Override
	protected void initDocWayHomeFromResponse(XMLDocumento response) throws Exception {
		UserBean userbean = getUserBean();
		if (null != userbean && null == userbean.getUserInfo()) {
			if (userbean.getMatricola() == null || userbean.getMatricola().equals(""))
				userbean.setMatricola(response.getRootElement().attributeValue("matricola", "")); // assegnazione della matricola
			userbean.setUserInfo(response.getRootElement().attributeValue("userInfo", null));
			setSessionAttribute("userBean", userbean);
		}
		
		boolean enableIWX = isEnabledIWX(); // abilitazione o meno di IWX
		
		DocwayprocHome docwayprocHome = new DocwayprocHome();
		docwayprocHome.getFormsAdapter().fillFormsFromResponse(response);
		
		// disattivazione di IWX da profilo personale
		if (docwayprocHome.getFormsAdapter().checkBooleanFunzionalitaDisponibile("disabilitaIWX", false))
			enableIWX = false;
		
		docwayprocHome.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
		docwayprocHome.init(response.getDocument());
		setSessionAttribute("docwayprocHome", docwayprocHome);
		
		Menu docwayprocMenu = new Menu();
		docwayprocMenu.getFormsAdapter().fillFormsFromResponse(response);
		docwayprocMenu.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
		setSessionAttribute("docwayprocMenu", docwayprocMenu);
	}

	@Override
	protected String redirectFromMatricolaLogin() {
		return "show@docwayproc_home";
	}

}
