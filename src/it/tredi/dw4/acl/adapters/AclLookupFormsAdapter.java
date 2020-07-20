package it.tredi.dw4.acl.adapters;

import org.dom4j.DocumentException;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.LookupFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

public class AclLookupFormsAdapter extends LookupFormsAdapter {

	public AclLookupFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
	}
}
