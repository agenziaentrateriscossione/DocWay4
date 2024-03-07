package it.tredi.dw4.adapters;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

public class HierBrowserFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	
	public HierBrowserFormsAdapter(AdapterConfig config) {
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
		defaultForm.addParam("db", root.attributeValue("db", ""));
		defaultForm.addParam("docCurrent", root.attributeValue("docCurrent", ""));
		defaultForm.addParam("docTitle", root.attributeValue("docTitle", ""));
		defaultForm.addParam("docToggle", root.attributeValue("docToggle", ""));
		defaultForm.addParam("backward", "");
		defaultForm.addParam("sele", root.attributeValue("sele", ""));
		defaultForm.addParam("destPage", root.attributeValue("destPage", ""));
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
	/* ACTIONS - DEFAULTFORM */
	public boolean isPaginaSuccessivaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		int hierCount = lastResponse.selectNodes("//hier").size();
		int count = defaultForm.getParamAsInt("count");		
		if (count > pos + hierCount)
			return true;
		else
			return false;
	}
	
	public boolean paginaSuccessiva() {
		if (isPaginaSuccessivaEnabled()) {
			Attribute lastDocAttr = (Attribute) lastResponse.selectSingleNode("//hier[last()]/@ndoc");
			if (lastDocAttr != null) {
				defaultForm.addParam("verbo", "hierBrowser");
				defaultForm.addParam("docStart", lastDocAttr.getValue());
				defaultForm.addParam("backward", "0");
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	public boolean isPaginaPrecedenteEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0)
			return true;
		else
			return false;
	}
	
	public boolean paginaPrecedente() {
		if (isPaginaPrecedenteEnabled()) {
			Attribute firstDocAttr = (Attribute) lastResponse.selectSingleNode("//hier[1]/@ndoc");
			if (firstDocAttr != null) {
				defaultForm.addParam("verbo", "hierBrowser");
				defaultForm.addParam("docStart", firstDocAttr.getValue());
				defaultForm.addParam("backward", "1");
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	/**
	 * caricamento in showdoc di un nodo della gerarchia
	 * @param nDoc
	 */
	public void mostraDocumento(String nDoc) {
		defaultForm.addParam("verbo", "showdoc");
		defaultForm.addParam("xverb", "@hierBrowser");
		defaultForm.addParam("pos", String.valueOf((Integer.valueOf(nDoc) -1)));
		defaultForm.addParam("selid", "");
		defaultForm.addParam("auditVisualizzazione", "true");
	}
	
	/**
	 * apertura di un nodo della gerarchia
	 * @param nDoc
	 */
	public void docToggle(String nDoc) {
		Attribute firstDocAttr = (Attribute) lastResponse.selectSingleNode("//hier[1]/@ndoc");
		if (firstDocAttr != null) {
			defaultForm.addParam("verbo", "hierBrowser");
			defaultForm.addParam("docToggle", nDoc);
			defaultForm.addParam("docStart", firstDocAttr.getValue());
		}
		
	}
	
	/**
	 * chiusura di un nodo della gerarchia
	 * @param nDoc
	 * @param nDocCurrent
	 */
	public void docToggle(String nDoc, String nDocCurrent) {
		Attribute firstDocAttr = (Attribute) lastResponse.selectSingleNode("//hier[1]/@ndoc");
		if (firstDocAttr != null) {
			defaultForm.addParam("verbo", "hierBrowser");
			defaultForm.addParam("docToggle", nDoc);
			defaultForm.addParam("docStart", firstDocAttr.getValue());
			defaultForm.addParam("docCurrent", nDocCurrent);
		}
		
	}
	
	/**
	 * refresh di un nodo, e' corretto?
	 */
	public void docCurrent(String nDocCurrent) {
		Attribute firstDocAttr = (Attribute) lastResponse.selectSingleNode("//hier[1]/@ndoc");
		if (firstDocAttr != null) {
			defaultForm.addParam("verbo", "hierBrowser");
			defaultForm.addParam("docToggle", "");
			defaultForm.addParam("docStart", firstDocAttr.getValue());
			defaultForm.addParam("docCurrent", nDocCurrent);
		}

	}
}
