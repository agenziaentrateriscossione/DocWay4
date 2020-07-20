package it.tredi.dw4.docway.adapters;

import org.dom4j.DocumentException;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

public class DocWayAlboExtFormsAdapter extends DocEditFormsAdapter {

	public DocWayAlboExtFormsAdapter(AdapterConfig config) {
		super(config);
	}

	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
	}
		
	/**
     * pubblica il documento su albo on-line esterno
     */
    public void pubblicaAlboExt(String dataPubblicazione, String dataDefissione){
    	defaultForm.addParam("verbo", "documento_response");
    	defaultForm.addParam("xverb", "@pubblicaAlboExt");
    	defaultForm.addParam("data_pubblicazione", dataPubblicazione);
    	defaultForm.addParam("data_defissione", dataDefissione);
    }
		
	
}
