package it.tredi.dw4.dwrep;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.beans.Init;
import it.tredi.dw4.dwrep.beans.DwrepHome;

public class DwrepInit extends Init {

	public DwrepInit() throws Exception {}
	
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
		
		boolean enableIWX = isEnabledIWX(); // abilitazione o meno di IWX
		
		DwrepHome dwrepHome = new DwrepHome();
		dwrepHome.getFormsAdapter().fillFormsFromResponse(response);
		
		// disattivazione di IWX da profilo personale
		if (dwrepHome.getFormsAdapter().checkBooleanFunzionalitaDisponibile("disabilitaIWX", false))
			enableIWX = false;
		
		dwrepHome.getFormsAdapter().getDefaultForm().addParam("enableIW", enableIWX);
		dwrepHome.init(response.getDocument());
		setSessionAttribute("dwrepHome", dwrepHome);
	}

	@Override
	protected String redirectFromMatricolaLogin() {
		return "show@dwrep_home";
	}

}
