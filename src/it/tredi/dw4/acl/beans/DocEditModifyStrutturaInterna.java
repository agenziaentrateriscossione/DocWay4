package it.tredi.dw4.acl.beans;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.StrutturaInterna;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditModifyStrutturaInterna extends AclDocEdit {
	
	private AclDocEditFormsAdapter formsAdapter;
	private StrutturaInterna struttura_interna;

	private String xml;
	
	// mbernardini 03/12/2014 : indicazione dell'indirizzo email del SdI al quale inviare le fatturePA attive
	private boolean abilitaFatturePA = false;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	
	public boolean isAbilitaFatturePA() {
		return abilitaFatturePA;
	}

	public void setAbilitaFatturePA(boolean abilitaFatturePA) {
		this.abilitaFatturePA = abilitaFatturePA;
	}
	
	public DocEditModifyStrutturaInterna() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.struttura_interna = new StrutturaInterna();
    	this.struttura_interna.init(domDocumento);
    	
    	this.abilitaFatturePA = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(domDocumento, "/response/@abilitaFatturePA"));
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.struttura_interna.asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("struttura_interna", "list_of_struttur");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("struttura_interna", response);		
			return "showdoc@struttura_interna@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setStruttura_interna(StrutturaInterna struttura_interna) {
		this.struttura_interna = struttura_interna;
	}

	public StrutturaInterna getStruttura_interna() {
		return this.struttura_interna;
	}
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("struttura_interna", response);
			return "showdoc@struttura_interna@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String lookupResponsabile() throws Exception {
		if (checkRequiredFieldLookup()){
			clearLookupResponsabile();
			return null;
		}
		
		String xq = "[persint_codammaoo]=" + struttura_interna.getCod_amm() + struttura_interna.getCod_aoo();
		xq = "(" + xq + ") AND [/persona_interna/@tipo]=\"&null;\"";
		
		String value = (struttura_interna.getNomeresponsabile()!= null && !"".equals(struttura_interna.getNomeresponsabile())) ? struttura_interna.getNomeresponsabile() : "*";
		
		String aliasName 	= "persint_nomcogn";
		String aliasName1 	= "persint_cognome";
		String titolo 		= "xml,/persona_interna/@cognome xml,/persona_interna/@nome \"- ~\" XML,/persona_interna/recapito/email/@addr \"- ~\" XML,/persona_interna/@soprannome";
		String ord 			= "xml(xpart:/persona_interna/@cognome)";
		String campi 		= "nomeresponsabile=xml,/persona_interna/@cognome xml,/persona_interna/@nome ; .@cod_responsabile=xml,/persona_interna/@matricola";
		String db 			= "";
		String newRecord	= "";
		
		callLookup(struttura_interna, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearLookupResponsabile() throws Exception{
		String campi = "nomeresponsabile=xml,/persona_interna/@cognome xml,/persona_interna/@nome ; .@cod_responsabile=xml,/persona_interna/@matricola"; //campi
		return clearField(campi, struttura_interna);
	}

	public String lookupComune() throws Exception {
		/*if (checkRequiredFieldLookup()){
			clearLookupComune();
			return null;
		}*/

		String value = (struttura_interna.getIndirizzo().getComune()!= null && !"".equals(struttura_interna.getIndirizzo().getComune())) ? struttura_interna.getIndirizzo().getComune() : "";
		String campi = ".indirizzo.@comune=xml,/comune/@nome ; .indirizzo.@cap=xml,/comune/@cap ; .indirizzo.@prov=xml,/comune/@prov ; .indirizzo.@nazione=xml,/comune/@nazione";
		callLookupComune(struttura_interna, campi, value);
		return null;
	}
	
	public String clearLookupComune() throws Exception{
		String campi = ".indirizzo.@comune=xml,/comune/@nome ; .indirizzo.@cap=xml,/comune/@cap ; .indirizzo.@prov=xml,/comune/@prov ; .indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, struttura_interna);
	}
	
	public String lookupCap() throws Exception {
		/*if (checkRequiredFieldLookup()){
			clearLookupCap();
			return null;
		}*/

		String value = (struttura_interna.getIndirizzo().getCap()!= null && !"".equals(struttura_interna.getIndirizzo().getCap())) ? struttura_interna.getIndirizzo().getCap() : "";
		String campi = ".indirizzo.@comune=xml,/comune/@nome ; .indirizzo.@cap=xml,/comune/@cap ; .indirizzo.@prov=xml,/comune/@prov ; .indirizzo.@nazione=xml,/comune/@nazione";
		callLookupCap(struttura_interna, campi, value);
		return null;
		
	}
	
	public String clearLookupCap() throws Exception{
		String campi = ".indirizzo.@comune=xml,/comune/@nome ; .indirizzo.@cap=xml,/comune/@cap ; .indirizzo.@prov=xml,/comune/@prov ; .indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, struttura_interna);
	}
	
	public String thVincolatoTipologia() throws Exception {
		String value = (struttura_interna.getTipologia()!= null && !"".equals(struttura_interna.getTipologia())) ? struttura_interna.getTipologia() : "";
		String fieldName = ".@tipologia";
		String chiave = "si_tipologia";
		callThVincolato(struttura_interna, fieldName, chiave, value);
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
	
	public boolean checkRequiredFieldLookup() {
		boolean result = false;
		if (struttura_interna.getCod_amm() == null || "".equals(struttura_interna.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:struint_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		if (struttura_interna.getCod_aoo() == null || "".equals(struttura_interna.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:struint_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		return result;
	}
	
	public boolean checkRequiredField() {
		boolean result = false;
		if (struttura_interna.getNome() == null || "".equals(struttura_interna.getNome().trim())) {
			this.setErrorMessage("templateForm:struint_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.description") + "'");
			result = true;
		}
		if (struttura_interna.getCod_amm() == null || "".equals(struttura_interna.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:struint_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (struttura_interna.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:struint_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (struttura_interna.getCod_aoo() == null || "".equals(struttura_interna.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:struint_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (struttura_interna.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:struint_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}
		return result;
	}
	
}
