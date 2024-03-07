package it.tredi.dw4.acl.beans;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.Gruppo;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;

public class DocEditGruppo extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private Gruppo gruppo = new Gruppo();
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}

	public DocEditGruppo() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
		xml = domDocumento.asXML();
		this.gruppo = new Gruppo();
    	this.gruppo.init(domDocumento);
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if(checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.gruppo.asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("gruppo", "list_of_struttur");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
				
			ShowdocGruppo showdocGruppo = new ShowdocGruppo();
			showdocGruppo.getFormsAdapter().fillFormsFromResponse(response);
			showdocGruppo.init(response.getDocument());
			
			setSessionAttribute("showdocGruppo", showdocGruppo);		
					
			return "showdoc@gruppo@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setGruppo(Gruppo gruppo) {
		this.gruppo = gruppo;
	}

	public Gruppo getGruppo() {
		return this.gruppo;
	}

	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response);
			this.init(response.getDocument());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
		return null;
	}
	
	/**
	 * In caso di modifica del campo codice amm occorre azzerare anche il campo responsabile
	 * @param vce
	 */
	public void codAmmValueChange(ValueChangeEvent vce) throws Exception {  
        clearLookupResponsabile();
    }
	
	/**
	 * In caso di modifica del campo codice aoo occorre azzerare anche il campo responsabile
	 * @param vce
	 */
	public void codAooValueChange(ValueChangeEvent vce) throws Exception { 
		clearLookupResponsabile();
	}

	public String lookupResponsabile() throws Exception {
		if (checkRequiredFieldLookup()) {
			clearLookupResponsabile();
			return null;
		}
		
		String xq = "[persint_codammaoo]=" + gruppo.getCod_amm() + gruppo.getCod_aoo();
		xq = "(" + xq + ") AND [/persona_interna/@tipo]=\"&null;\"";
		
		String value = (gruppo.getNomeresponsabile()!= null && !"".equals(gruppo.getNomeresponsabile())) ? gruppo.getNomeresponsabile() : "*";
		
		String aliasName 	= "persint_nomcogn";
		String aliasName1 	= "persint_cognome";
		String titolo 		= "xml,/persona_interna/@cognome xml,/persona_interna/@nome \"- ~\" XML,/persona_interna/recapito/email/@addr \"- ~\" XML,/persona_interna/@soprannome";
		String ord 			= "xml(xpart:/persona_interna/@cognome)";
		String campi 		= "nomeresponsabile=xml,/persona_interna/@cognome xml,/persona_interna/@nome ; .@cod_responsabile=xml,/persona_interna/@matricola";
		String db 			= "";
		String newRecord	= "";
		
		callLookup(gruppo, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearLookupResponsabile() throws Exception{
		String campi = "nomeresponsabile=xml,/persona_interna/@cognome xml,/persona_interna/@nome ; .@cod_responsabile=xml,/persona_interna/@matricola"; //campi
		return clearField(campi, gruppo);
	}
	
	public boolean checkRequiredFieldLookup() {
		boolean result = false;
		if (gruppo.getCod_amm() == null || "".equals(gruppo.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:gruppo_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		if (gruppo.getCod_aoo() == null || "".equals(gruppo.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:gruppo_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		return result;
	}
	
	public boolean checkRequiredField(){
		boolean result = false;
		if (gruppo.getNome() == null || "".equals(gruppo.getNome().trim())) {
			this.setErrorMessage("templateForm:gruppo_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.name") + "'");
		    result = true;
		}
		if (gruppo.getCod_amm() == null || "".equals(gruppo.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:gruppo_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (gruppo.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:gruppo_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (gruppo.getCod_aoo() == null || "".equals(gruppo.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:gruppo_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (gruppo.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:gruppo_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}
		return result;
	}
}
