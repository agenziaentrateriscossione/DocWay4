package it.tredi.dw4.dwrep;

import it.tredi.dw4.beans.Init;
import it.tredi.dw4.dwrep.beans.DwrepHome;
import it.tredi.dw4.utils.XMLDocumento;

public class DwrepInit extends Init {

	public DwrepInit() throws Exception {}

	@Override
	protected void initDocWayHomeFromResponse(XMLDocumento response) throws Exception {
		initUserBeanData(getUserBean(), response);

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
