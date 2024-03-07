package it.tredi.dw4.docway.adapters;

import org.dom4j.DocumentException;

import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.utils.XMLDocumento;

public class DocWayPrintAttachmentsFormsAdapter extends FormsAdapter {

	protected FormAdapter defaultForm;
	
	public DocWayPrintAttachmentsFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}
	
	
	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		addSessionData(defaultForm, response);
	}
	
	public String getCustomTupleName() {
		String _cd = defaultForm.getParam("_cd");
		if (_cd == null)
			_cd = "";

		return _cd;
	}
	
	/**
	 * Ritorna la stringa relativa all'attributo della response 'db'.
	 * @return Valore impostato sull'attributo db
	 */
	public String getDb() {
		return defaultForm.getParam("db");
	}

	
}
