package it.tredi.dw4.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class PrintProfilesFormsAdapter extends FormsAdapter {

	protected FormAdapter defaultForm;
	
	public PrintProfilesFormsAdapter(AdapterConfig config) {
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
		defaultForm.addParam("childLast", "1");
		defaultForm.addParam("qord", "");
		defaultForm.addParam("physDoc", root.attributeValue("physDoc", ""));
		defaultForm.addParam("userInfo", root.attributeValue("userInfo", ""));
		defaultForm.addParam("pwdLock", root.attributeValue("pwdLock", ""));
		defaultForm.addParam("pwdPhysdoc", root.attributeValue("pwdPhysdoc", ""));
		defaultForm.addParam("pwdChangeInfo", "");
		
		// template per la generazione del pdf
		defaultForm.addParam("printTemplate", "");

	    addBeforeEndEmbedded(defaultForm, response);
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
	public void paginaTitoli() {
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");

		defaultForm.addParam("verbo", "showtitles");
		defaultForm.addParam("pos", (int)Math.floor(pos / pageCount) * pageCount);

		String wDbTable = defaultForm.getParam("dbTable");
		if (wDbTable.indexOf("@workflow") != -1) {
			defaultForm.addParam("dbTable", "@workflow");
		}
	}
	
	public boolean isPaginaTitoliEnabled() {
		String selid = defaultForm.getParam("selid");
		int count = defaultForm.getParamAsInt("count");
		return (selid.length() > 0 && count > 1);
	}
	
}
