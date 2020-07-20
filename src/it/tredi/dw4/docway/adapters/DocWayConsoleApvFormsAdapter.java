package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.adapters.FormsAdapter;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class DocWayConsoleApvFormsAdapter extends FormsAdapter  {
	protected FormAdapter defaultForm;
	
	public DocWayConsoleApvFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}

	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		
		fillDefaultFormFromResponse(response);
	}
	
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		
		Element root = response.getRootElement();
		
		defaultForm.addParam("verbo", root.attributeValue("verbo", ""));
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		
		//defaultForm.addParam("currentmailbox", root.attributeValue("currentmailbox", ""));
	}
	
    /**
     * Caricamento del contenuto atto dell'istanza di processo passata
     */
    public void loadContenutoAtto(String processInstanceId) {
    	defaultForm.addParam("verbo", "consoleapv");
    	defaultForm.addParam("xverb", "@loadcontenutoatto");
		defaultForm.addParam("processinstanceid", processInstanceId);
    }

    /**
     * Caricamento del contenuto atto dell'istanza di processo passata
     */
    public void invioEmailSollecito(String matricole, String docNrecord, String oggettoEmail, String testoEmail) {
    	defaultForm.addParam("verbo", "consoleapv");
    	defaultForm.addParam("xverb", "@inviosollecito");
		defaultForm.addParam("matricole", matricole);
		defaultForm.addParam("docnrecord", docNrecord);
		defaultForm.addParam("oggettoemail", oggettoEmail);
		defaultForm.addParam("testoemail", testoEmail);
    }
	
	/**
	 * Filtro email su casella di posta
	 */
	/*public void search(String colSort, boolean ascSort) {
		defaultForm.addParam("xverb", "@search");
		defaultForm.addParam("sortfield", colSort);
		defaultForm.addParam("sortasc", ascSort);
	}*/
	
	
}
