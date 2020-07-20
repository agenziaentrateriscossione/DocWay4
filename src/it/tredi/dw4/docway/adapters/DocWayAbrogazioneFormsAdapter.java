package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

import org.dom4j.DocumentException;

public class DocWayAbrogazioneFormsAdapter extends DocEditFormsAdapter {

	public DocWayAbrogazioneFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
		//Element root = response.getRootElement();
	}
	
	
	public void confirmAbrogazioneDoc_response(String text){
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@abroga");
		defaultForm.addParam("doc_responseText", text);
	}
}
