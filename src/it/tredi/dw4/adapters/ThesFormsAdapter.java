package it.tredi.dw4.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class ThesFormsAdapter extends FormsAdapter {

	protected FormAdapter defaultForm;
	
	public ThesFormsAdapter(AdapterConfig config) {
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
		
		defaultForm.addParam("verbo", root.attributeValue("verbo", ""));
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		
		defaultForm.addParam("sele", root.attributeValue("sele", ""));
		defaultForm.addParam("pos", root.attributeValue("pos", ""));
		defaultForm.addParam("count", root.attributeValue("count", ""));
		
		defaultForm.addParam("cPath", root.attributeValue("cPath", "")); 
		defaultForm.addParam("startkey", root.attributeValue("startkey", ""));
		defaultForm.addParam("startkey", root.attributeValue("relation", ""));
		
		defaultForm.addParam("xRels", root.attributeValue("xRels", ""));
		defaultForm.addParam("cRels", root.attributeValue("cRels", ""));
		defaultForm.addParam("vRels", root.attributeValue("vRels", ""));
		defaultForm.addParam("lRel", root.attributeValue("lRel", ""));
		defaultForm.addParam("xMode", root.attributeValue("xMode", ""));
		
		defaultForm.addParam("relation", root.attributeValue("relation", ""));
		
		defaultForm.addParam("keypath", root.attributeValue("keypath", ""));
		defaultForm.addParam("keypath", root.attributeValue("keylist", ""));
		defaultForm.addParam("minFreq", root.attributeValue("minFreq", ""));
		defaultForm.addParam("maxFreq", root.attributeValue("maxFreq", ""));
		
		defaultForm.addParam("administrationMode", root.attributeValue("administrationMode", ""));
		defaultForm.addParam("bAssegnaLinkFasc", root.attributeValue("bAssegnaLinkFasc", ""));
		defaultForm.addParam("qext_rifint_lookup", root.attributeValue("qext_rifint_lookup", ""));
	}
	
	public void showThesRel(String keypath, String startkey, String cPath) {
		defaultForm.addParam("verbo", "showthes");
		defaultForm.addParam("keypath", keypath); // Canale del thes
		defaultForm.addParam("startkey", startkey);
		defaultForm.addParam("cPath", cPath);
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
}
