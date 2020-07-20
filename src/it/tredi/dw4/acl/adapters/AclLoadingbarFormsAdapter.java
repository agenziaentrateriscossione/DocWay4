package it.tredi.dw4.acl.adapters;

import org.dom4j.DocumentException;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.LoadingbarFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

public class AclLoadingbarFormsAdapter extends LoadingbarFormsAdapter {

	public AclLoadingbarFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
	}
	
	/**
	 * chiamata a reload della gerarchia delle strutture interne di ACL
	 * da conclusione di una loadingbar di rigenerazione della gerarchia
	 */
	public void reloadAclHierBrowser() {
		defaultForm.addParam("docToggle", "-1");
		defaultForm.addParam("hierCount", "20");
		defaultForm.addParam("docStart", "-1");
		defaultForm.addParam("verbo", "hierBrowser");
		defaultForm.addParam("hierStatus", "");
	}
	
}
