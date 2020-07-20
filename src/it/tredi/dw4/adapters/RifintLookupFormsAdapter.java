package it.tredi.dw4.adapters;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

public class RifintLookupFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	
	public RifintLookupFormsAdapter(AdapterConfig config) {
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
		
		defaultForm.addParam("verbo", "rifintlookup_response");
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		defaultForm.addParam("sele", root.attributeValue("sele", ""));
		defaultForm.addParam("ripropz", root.attributeValue("ripropz", ""));
		defaultForm.addParam("xDoc", "");
		defaultForm.addParam("qord", root.attributeValue("qord", ""));
		defaultForm.addParam("destPage", root.attributeValue("destPage", ""));
		defaultForm.addParam("lookup_titolo", root.attributeValue("lookup_titolo", ""));
		defaultForm.addParam("lookup_campi", root.attributeValue("lookup_campi", ""));
		defaultForm.addParam("lookup_alias", root.attributeValue("lookup_alias", ""));
		defaultForm.addParam("lookup_db", root.attributeValue("lookup_db", ""));
		defaultForm.addParam("lookup_new", root.attributeValue("lookup_new", ""));
		defaultForm.addParam("lookup_xq", root.attributeValue("lookup_xq", ""));
		defaultForm.addParam("lookup_fieldVal", root.attributeValue("lookup_fieldVal", ""));
		defaultForm.addParam("selected_campi", root.attributeValue("selected_campi", ""));
		defaultForm.addParam("qext_rifint_lookup", root.attributeValue("qext_rifint_lookup", ""));
	}
	
	/**
	 * Imposta i parametri per il secondo lookup di rifint
	 */
	public void doSecondLookup(String alias, String xverb, String fieldVal) {
		defaultForm.addParam("selid", "");
		defaultForm.addParam("verbo", "rifintlookup_response");
		defaultForm.addParam("lookup_alias", alias);
		defaultForm.addParam("xverb", xverb);
		defaultForm.addParam("lookup_fieldVal", fieldVal);
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
	/* ACTIONS - DEFAULTFORM */
	
	public boolean primaPagina() {
		if (isPrimaPaginaEnabled()) {
			defaultForm.addParam("verbo", "rifintlookup_response");
			defaultForm.addParam("pos", 0);
			
			//FIXME
			//sendRequest();
			
			return true;
		}
		else {
			return false;	
		}
	}
	
	public boolean isPrimaPaginaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0)
			return true;
		else
			return false;
	}
	
	public boolean paginaPrecedente() {
		if (isPaginaPrecedenteEnabled()) {
			int pos = defaultForm.getParamAsInt("pos");
			defaultForm.addParam("verbo", "rifintlookup_response");
			int pageCount = defaultForm.getParamAsInt("pageCount");
			defaultForm.addParam("pos", pos - pageCount < 0? 0 : pos - pageCount);
			
			//FIXME
			//sendRequest();
						
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isPaginaPrecedenteEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0)
			return true;
		else
			return false;
	}	
	
	public boolean paginaSuccessiva() {
		if (isPaginaSuccessivaEnabled()) {
			int pos = defaultForm.getParamAsInt("pos");
			int pageCount = defaultForm.getParamAsInt("pageCount");
			defaultForm.addParam("verbo", "rifintlookup_response");
			defaultForm.addParam("pos", pos + pageCount);
			
			//FIXME
			//sendRequest();
						
			return true;
		}
		else {
			return false;
		}		
	}

	public boolean isPaginaSuccessivaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int count = defaultForm.getParamAsInt("count");		
		if (count > pos + pageCount)
			return true;
		else
			return false;
	}	
	
	public boolean ultimaPagina() {
		if (isUltimaPaginaEnabled()) {
			int pageCount = defaultForm.getParamAsInt("pageCount");
			int count = defaultForm.getParamAsInt("count");			
			defaultForm.addParam("verbo", "rifintlookup_response");
			defaultForm.addParam("pos", (int)Math.floor((count - 1) / pageCount) * pageCount);
			
			//FIXME
			//sendRequest();
						
			return true;
		}
		else {
			return false;
		}	
	}	
	
	public boolean isUltimaPaginaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int count = defaultForm.getParamAsInt("count");		
		if (count > pos + pageCount)
			return true;
		else
			return false;
	}
	
	public int getCurrent() {
		int pos = defaultForm.getParamAsInt("pos") + 1;
		int pageCount = defaultForm.getParamAsInt("pageCount");
		
		if (pos % pageCount == 0)
			return pos/pageCount;
		else
			return (pos/pageCount) + 1;
	}
	
	public int getTotal() {
		int count = defaultForm.getParamAsInt("count");	
		int pageCount = defaultForm.getParamAsInt("pageCount");
		
		if (count % pageCount == 0)
			return count/pageCount;
		else
			return (count/pageCount) + 1;
	}
	
}
