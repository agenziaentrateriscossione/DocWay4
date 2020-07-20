package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

import org.dom4j.DocumentException;

public class DocWayAnnullamentoFormsAdapter extends DocEditFormsAdapter {

	public DocWayAnnullamentoFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
		//Element root = response.getRootElement();
	}
	
	
	public void confirmAnnulDoc_response(String text){
		defaultForm.addParam("verbo", "documento_response");
		defaultForm.addParam("xverb", "@annulla");
		defaultForm.addParam("doc_responseText", text);
	}
}
