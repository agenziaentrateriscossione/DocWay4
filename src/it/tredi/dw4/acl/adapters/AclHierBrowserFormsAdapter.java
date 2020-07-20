package it.tredi.dw4.acl.adapters;

import org.dom4j.DocumentException;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.HierBrowserFormsAdapter;

public class AclHierBrowserFormsAdapter extends HierBrowserFormsAdapter {

	public AclHierBrowserFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
//		Element root = response.getRootElement();
//defaultForm.addParam("nodoCopiato", root.attributeValue("nodoCopiato", ""));
	}
	
	/* ACTIONS - DEFAULTFORM */

	public void generaRelazioniStrutture() {
		this.defaultForm.addParam("verbo", "query");
		this.defaultForm.addParam("xverb", "@buildFascRels");
	}
	
}
