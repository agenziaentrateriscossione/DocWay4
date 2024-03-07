package it.tredi.dw4.acl.adapters;

import org.dom4j.DocumentException;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.TitlesFormsAdapter;

public class AclTitlesFormsAdapter extends TitlesFormsAdapter {

	public AclTitlesFormsAdapter(AdapterConfig config) {
		super(config);
	}
	
	@Override
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		super.fillDefaultFormFromResponse(response);
		
//		Element root = response.getRootElement();
//defaultForm.addParam("nodoCopiato", root.attributeValue("nodoCopiato", ""));
	}

	// apre il form di query deleghe
	public void gotoTableQDeleghe() {
		defaultForm.addParam("query", "");
		defaultForm.addParam("dbTable", "@deleghe");
		defaultForm.addParam("verbo", "query");
	}

	// apre il pannello di gestione delle deleghe della persona interna
	public void gotoDeleghePersonaInterna(String matricolaPersInt, int pos, String titleFormat) {
		defaultForm.addParam("matricolaPersonaInt", matricolaPersInt);
		defaultForm.addParam("verbo", "deleghe");
		defaultForm.addParam("xverb", "@openDeleghePersona");
		defaultForm.addParam("pos", pos);
		defaultForm.addParam("ripropz", titleFormat);
	}
}
