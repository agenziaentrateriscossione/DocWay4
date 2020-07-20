package it.tredi.dw4.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.adapters.FormsAdapter;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class ThEditFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	
	public ThEditFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}
	
	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		resetForms();
		
		fillDefaultFormFromResponse(response);
	}
	
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		Element root = response.getRootElement();
		
		defaultForm.addParam("keypath", root.attributeValue("keypath", ""));
		defaultForm.addParam("name", root.attributeValue("name", ""));
		defaultForm.addParam("prev", root.attributeValue("prev", ""));
		defaultForm.addParam("next", root.attributeValue("next", ""));
		defaultForm.addParam("rels", root.attributeValue("rels", ""));
		defaultForm.addParam("relDescr", root.attributeValue("relDescr", ""));
		defaultForm.addParam("relRules", root.attributeValue("relRules", ""));
		defaultForm.addParam("relIndex", root.attributeValue("relIndex", ""));
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("verbo", root.attributeValue("verbo", ""));
		defaultForm.addParam("vRels", "");
		defaultForm.addParam("xRels", "");
		defaultForm.addParam("startkey", "");
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
}
