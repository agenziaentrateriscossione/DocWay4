package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.Comune;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;

import org.dom4j.Document;

public class DocEditModifyComune extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private Comune comune;

	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	public DocEditModifyComune() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.comune = new Comune();
    	this.comune.init(domDocumento);    			
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.comune.asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("comune", "list_of_struttur");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificShowdocPageAndReturnNavigationRule("comune", response);
			return "showdoc@comune@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setComune(Comune comune) {
		this.comune = comune;
	}

	public Comune getComune() {
		return this.comune;
	}

	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("comune", response);	
			return "showdoc@comune@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
			
	}
	
	public String thVincolatoProv() throws Exception {
		String value = (comune.getProv() != null && !"".equals(comune.getProv())) ? comune.getProv() : "";
		String fieldName = ".@prov";
		String chiave = "provincia";
		callThVincolato(comune, fieldName, chiave, value);
		return null;
	}
	
	public String thVincolatoRegione() throws Exception {
		String value = (comune.getRegione() != null && !"".equals(comune.getRegione())) ? comune.getRegione() : "";
		String fieldName = ".@regione";
		String chiave = "regione";
		callThVincolato(comune, fieldName, chiave, value);
		return null;
	}

	public String thVincolatoNazione() throws Exception {
		String value = (comune.getNazione() != null && !"".equals(comune.getNazione())) ? comune.getNazione() : "";
		String fieldName = ".@nazione";
		String chiave = "nazione";
		callThVincolato(comune, fieldName, chiave, value);
		return null;
	}
	
	public boolean checkRequiredField(){
		boolean result = false;
		if (comune.getNome() == null || "".equals(comune.getNome().trim())) {
			this.setErrorMessage("templateForm:comune_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.town") + "'");
			result = true;
		}
		if (comune.getProv() == null || "".equals(comune.getProv().trim())) {
			this.setErrorMessage("templateForm:comune_prov_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.province") + "'");
			result = true;
		}
		if (comune.getCap() == null || "".equals(comune.getCap().trim())) {
			this.setErrorMessage("templateForm:comune_cap", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.zip") + "'");
			result = true;
		}
		return result;
	}
}
