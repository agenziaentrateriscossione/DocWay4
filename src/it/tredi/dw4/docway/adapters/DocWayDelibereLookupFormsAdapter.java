package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

import org.dom4j.DocumentException;

public class DocWayDelibereLookupFormsAdapter extends DocWayLookupFormsAdapter {

	public DocWayDelibereLookupFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
	}
	
	public void confermaRinviaSeduta(String fieldsArray, String keylistIndex){
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@conferma_rinvia_proposta");
		defaultForm.addParam("toDo", fieldsArray);
		defaultForm.addParam("keyList", keylistIndex);
	}
}
