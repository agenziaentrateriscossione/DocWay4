package it.tredi.dw4.adapters;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

public class ThesauroFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	
	public ThesauroFormsAdapter(AdapterConfig config) {
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
		
		defaultForm.addParam("verbo", "thesauro_response");
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		defaultForm.addParam("sele", root.attributeValue("sele", ""));
		defaultForm.addParam("lookup_new", root.attributeValue("lookup_new", ""));
		defaultForm.addParam("lookup_campi", root.attributeValue("lookup_campi", ""));
		defaultForm.addParam("lookup_fieldVal", root.attributeValue("lookup_fieldVal", ""));
		defaultForm.addParam("selected_campi", root.attributeValue("selected_campi", ""));
		defaultForm.addParam("relRules", root.attributeValue("relRules", ""));
		defaultForm.addParam("firstKey", root.attributeValue("firstKey", ""));
		defaultForm.addParam("lastKey", root.attributeValue("lastKey", ""));
		
		//gestione posizionamento
		defaultForm.addParam("shwMode", root.attributeValue("shwMode", "")); //modalità di presentazione: codice a 2 byte 'dt' se è una data
		defaultForm.addParam("cPath", root.attributeValue("cPath", "")); //percorso comune
		defaultForm.addParam("startkey", root.attributeValue("startkey", "")); //chiave su cui posizionarsi
		defaultForm.addParam("keylower", ""); //se leggere verso minore (pagina precedente)
		defaultForm.addParam("keypath", root.attributeValue("keypath", "")); //la chiave
		defaultForm.addParam("minFreq", root.attributeValue("minFreq", "")); //frequenza minima
		defaultForm.addParam("maxFreq", root.attributeValue("maxFreq", "")); //frequenza massima
		defaultForm.addParam("minRes", root.attributeValue("minRes", "")); //risoluzione minima
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
	/* ACTIONS - DEFAULTFORM */
	
	public boolean paginaPrecedente() {
		if (isPaginaPrecedenteEnabled()) {
			defaultForm.addParam("xverb", "@prev");
			
			//FIXME
			//sendRequest();
						
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isPaginaPrecedenteEnabled() {
		return this.lastResponse.testAttributeValue("/response/@enablePrev", "true");
	}	
	
	public boolean paginaSuccessiva() {
		if (isPaginaSuccessivaEnabled()) {
			defaultForm.addParam("xverb", "@next");
			
			//FIXME
			//sendRequest();
						
			return true;
		}
		else {
			return false;
		}		
	}

	public boolean isPaginaSuccessivaEnabled() {
		return this.lastResponse.testAttributeValue("/response/@enableNext", "true");
	}	
	
}
