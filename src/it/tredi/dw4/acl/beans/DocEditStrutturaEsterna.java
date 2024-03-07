package it.tredi.dw4.acl.beans;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.StrutturaEsterna;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditStrutturaEsterna extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private StrutturaEsterna struttura_esterna;

	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	public DocEditStrutturaEsterna() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.struttura_esterna = new StrutturaEsterna();
    	this.struttura_esterna.init(XMLUtil.createDocument(domDocumento, "//struttura_esterna"));
    	// Se non venirre riassegnato al punto '.' l'nrecord in questo punto verrebbe restituito errore in caso di 'ripeti nuovo'
    	this.struttura_esterna.setNrecord(".");
    	this.struttura_esterna.setCod_uff(".");
    	// Anche i campi 'codice fiscale' e 'partita iva' devono essere azzerati per evitare problemi 
    	// nel salvataggio in caso di 'ripeti nuovo'
    	this.struttura_esterna.setCodice_fiscale("");
    	this.struttura_esterna.setPartita_iva("");
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.struttura_esterna.asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("struttura_esterna", "list_of_struttur");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			String fromLookup = formsAdapter.getDefaultForm().getParam("fromLookup");
			if (fromLookup != null && fromLookup.equals("true")) {
				this.getFormsAdapter().fillFormsFromResponse(response);
				this.init(response.getDocument());
				
				setSessionAttribute("docEditStrutturaEsterna", this);
				this.setPopupPage(true);
				
				return null;
			}
			else {
				buildSpecificShowdocPageAndReturnNavigationRule("struttura_esterna", response);		
				return "showdoc@struttura_esterna@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setStruttura_esterna(StrutturaEsterna struttura_esterna) {
		this.struttura_esterna = struttura_esterna;
	}

	public StrutturaEsterna getStruttura_esterna() {
		return this.struttura_esterna;
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
		
		String xq = "[persest_codammaoo]=" + struttura_esterna.getCod_amm() + struttura_esterna.getCod_aoo();
		
		String value = (struttura_esterna.getNomeresponsabile()!= null && !"".equals(struttura_esterna.getNomeresponsabile())) ? struttura_esterna.getNomeresponsabile() : "*";
		
		String aliasName 	= "persest_nomcogn"; //aliasName 
		String aliasName1 	= "persest_cognome"; //aliasName1 
		String titolo 		= "xml,/persona_esterna/@cognome xml,/persona_esterna/@nome \"- ~\" XML,/persona_esterna/recapito/email/@addr \"- ~\" XML,/persona_esterna/@soprannome"; //titolo 
		String ord 			= "xml(xpart:/persona_esterna/@cognome)"; //ord 
		String campi 		= "nomeresponsabile=xml,/persona_esterna/@cognome xml,/persona_esterna/@nome ; .@cod_responsabile=xml,/persona_esterna/@matricola"; //campi
		String db 			= ""; //db 
		String newRecord 	= ""; //newRecord
		
		callLookup(struttura_esterna, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearLookupResponsabile() throws Exception{
		String campi = "nomeresponsabile=xml,/persona_esterna/@cognome xml,/persona_esterna/@nome ; .@cod_responsabile=xml,/persona_esterna/@matricola"; //campi
		return clearField(campi, struttura_esterna);
	}

	public String lookupComune() throws Exception {
		String value = (struttura_esterna.getIndirizzo().getComune()!= null && !"".equals(struttura_esterna.getIndirizzo().getComune())) ? struttura_esterna.getIndirizzo().getComune() : "";
		String campi = ".indirizzo.@comune=xml,/comune/@nome ; .indirizzo.@cap=xml,/comune/@cap ; .indirizzo.@prov=xml,/comune/@prov ; .indirizzo.@nazione=xml,/comune/@nazione"; //campi
		callLookupComune(struttura_esterna, campi, value);
		return null;
	}
	
	public String clearLookupComune() throws Exception{
		String campi = ".indirizzo.@comune=xml,/comune/@nome ; .indirizzo.@cap=xml,/comune/@cap ; .indirizzo.@prov=xml,/comune/@prov ; .indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, struttura_esterna);
	}
	
	public String lookupCap() throws Exception {
		String value = (struttura_esterna.getIndirizzo().getCap()!= null && !"".equals(struttura_esterna.getIndirizzo().getCap())) ? struttura_esterna.getIndirizzo().getCap() : "";
		String campi = ".indirizzo.@comune=xml,/comune/@nome ; .indirizzo.@cap=xml,/comune/@cap ; .indirizzo.@prov=xml,/comune/@prov ; .indirizzo.@nazione=xml,/comune/@nazione"; //campi
		callLookupCap(struttura_esterna, campi, value);
		return null;
	}
	
	public String clearLookupCap() throws Exception{
		String campi = ".indirizzo.@comune=xml,/comune/@nome ; .indirizzo.@cap=xml,/comune/@cap ; .indirizzo.@prov=xml,/comune/@prov ; .indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, struttura_esterna);
	}
	
	public String thVincolatoTipologia() throws Exception {
		String value = (struttura_esterna.getTipologia()!= null && !"".equals(struttura_esterna.getTipologia())) ? struttura_esterna.getTipologia() : "";
		String fieldName = ".@tipologia"; //fieldName
		String chiave = "se_tipologia"; //chiave
		callThVincolato(struttura_esterna, fieldName, chiave, value);
		return null;
	}
	
	public boolean checkRequiredFieldLookup() {
		boolean result = false;
		if (struttura_esterna.getCod_amm() == null || "".equals(struttura_esterna.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:struest_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		if (struttura_esterna.getCod_aoo() == null || "".equals(struttura_esterna.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:struest_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		} 
		return result;
	}
	
	public boolean checkRequiredField() {
		boolean result = false;
		if (struttura_esterna.getNome() == null || "".equals(struttura_esterna.getNome().trim())) {
			this.setErrorMessage("templateForm:struest_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.description") + "'");
			result = true;
		}
		if (struttura_esterna.getCod_amm() == null || "".equals(struttura_esterna.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:struest_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (struttura_esterna.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:struest_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (struttura_esterna.getCod_aoo() == null || "".equals(struttura_esterna.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:struest_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (struttura_esterna.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:struest_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}
		return result;
	}

}
