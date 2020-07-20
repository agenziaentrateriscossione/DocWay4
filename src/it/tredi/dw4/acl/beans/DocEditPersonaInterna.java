package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.GruppoAppartenenza;
import it.tredi.dw4.acl.model.Mansione;
import it.tredi.dw4.acl.model.PersonaInterna;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class DocEditPersonaInterna extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private PersonaInterna persona_interna;

	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	public DocEditPersonaInterna() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.persona_interna = new PersonaInterna();
    	this.persona_interna.init(domDocumento.getDocument());
    	// Se non venirre riassegnato al punto '.' l'nrecord in questo punto verrebbe restituito errore in caso di 'ripeti nuovo'
    	this.persona_interna.setNrecord(".");
    	this.persona_interna.setMatricola(".");
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// In caso di matricola vuota imposto il campo matricola a punto '.'
			// Lo faccio in questo punto per evitare che nella maschera di inserimento venga 
			// mostrato il punto
			if (persona_interna.getMatricola() == null || persona_interna.getMatricola().trim().length() == 0)
				persona_interna.setMatricola(".");
			
			formsAdapter.getDefaultForm().addParams(this.persona_interna.asFormAdapterParams(""));
			XMLDocumento response = super._saveDocument("persona_interna", "list_of_struttur");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("persona_interna", response);
			return "showdoc@persona_interna@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setPersona_interna(PersonaInterna struttura_interna) {
		this.persona_interna = struttura_interna;
	}

	public PersonaInterna getPersona_interna() {
		return this.persona_interna;
	}

	@Override
	public String clearDocument() throws Exception {
		try {
			setStrutturaPersonaInClear();
			
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
	 * In caso di 'Pulisci' della persona creata a partire da una struttura occorre mantenere anche i 
	 * valori relativi alla struttura di appartenenza (in realtà la prima struttura di appartenenza
	 * viene sempre mantenuta in caso di pulisci - stesso comportamento anche in ACL3)
	 */
	private void setStrutturaPersonaInClear() {
		if (persona_interna.getCod_uff() != null) {
			formsAdapter.insPersona(persona_interna.getCod_uff(), persona_interna.getCod_amm(), persona_interna.getCod_aoo(), null);
		}
	}

	public String lookupRecapitoComune() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		String value = (persona_interna.getRecapito().getIndirizzo().getComune()!= null && !"".equals(persona_interna.getRecapito().getIndirizzo().getComune())) ? persona_interna.getRecapito().getIndirizzo().getComune() : "";
		String campi = ".recapito.indirizzo.@comune=xml,/comune/@nome ; .recapito.indirizzo.@cap=xml,/comune/@cap ; .recapito.indirizzo.@prov=xml,/comune/@prov ; .recapito.indirizzo.@nazione=xml,/comune/@nazione";//campi
		callLookupComune(persona_interna, campi, value);
		return null;
	}
	
	public String clearLookupRecapitoComune() throws Exception{
		String campi = ".recapito.indirizzo.@comune=xml,/comune/@nome ; .recapito.indirizzo.@cap=xml,/comune/@cap ; .recapito.indirizzo.@prov=xml,/comune/@prov ; .recapito.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, persona_interna);
	}
	
	public String lookupRecapitoCap() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		String value = (persona_interna.getRecapito().getIndirizzo().getCap()!= null && !"".equals(persona_interna.getRecapito().getIndirizzo().getCap())) ? persona_interna.getRecapito().getIndirizzo().getCap() : "";
		String campi = ".recapito.indirizzo.@comune=xml,/comune/@nome ; .recapito.indirizzo.@cap=xml,/comune/@cap ; .recapito.indirizzo.@prov=xml,/comune/@prov ; .recapito.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		callLookupCap(persona_interna, campi, value);
		return null;
	}
	
	public String clearLookupRecapitoCap() throws Exception{
		String campi = ".recapito.indirizzo.@comune=xml,/comune/@nome ; .recapito.indirizzo.@cap=xml,/comune/@cap ; .recapito.indirizzo.@prov=xml,/comune/@prov ; .recapito.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, persona_interna);
	}
	
	
	public String lookupRecapitoPersonaleComune() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		String value = (persona_interna.getRecapito_personale().getIndirizzo().getComune()!= null && !"".equals(persona_interna.getRecapito_personale().getIndirizzo().getComune())) ? persona_interna.getRecapito_personale().getIndirizzo().getComune() : "";
		String campi = ".recapito_personale.indirizzo.@comune=xml,/comune/@nome ; .recapito_personale.indirizzo.@cap=xml,/comune/@cap ; .recapito_personale.indirizzo.@prov=xml,/comune/@prov ; .recapito_personale.indirizzo.@nazione=xml,/comune/@nazione";
		callLookupComune(persona_interna, campi, value);
		return null;
	}
	
	public String clearLookupRecapitoPersonaleComune() throws Exception{
		String campi = ".recapito_personale.indirizzo.@comune=xml,/comune/@nome ; .recapito_personale.indirizzo.@cap=xml,/comune/@cap ; .recapito_personale.indirizzo.@prov=xml,/comune/@prov ; .recapito_personale.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, persona_interna);
	}
	
	public String lookupRecapitoPersonaleCap() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		String value = (persona_interna.getRecapito_personale().getIndirizzo().getCap()!= null && !"".equals(persona_interna.getRecapito_personale().getIndirizzo().getCap())) ? persona_interna.getRecapito_personale().getIndirizzo().getCap() : "";
		String campi = ".recapito_personale.indirizzo.@comune=xml,/comune/@nome ; .recapito_personale.indirizzo.@cap=xml,/comune/@cap ; .recapito_personale.indirizzo.@prov=xml,/comune/@prov ; .recapito_personale.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		callLookupCap(persona_interna, campi, value);
		return null;
	}
	
	public String clearLookupRecapitoPersonaleCap() throws Exception{
		String campi = ".recapito_personale.indirizzo.@comune=xml,/comune/@nome ; .recapito_personale.indirizzo.@cap=xml,/comune/@cap ; .recapito_personale.indirizzo.@prov=xml,/comune/@prov ; .recapito_personale.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, persona_interna);
	}
	
	/**
	 * In caso di modifica del campo codice amm occorre azzerare la struttura di appartenenza, 
	 * i ruoli/mansioni e i gruppi della persona esterna
	 * @param vce
	 */
	public void codAmmValueChange(ValueChangeEvent vce) throws Exception {
		try {
			clearLookupAppartenenza();
			persona_interna.clearMansione();
			persona_interna.clearGruppoAppartenenza();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;		
		}
    }
	
	/**
	 * In caso di modifica del campo codice aoo occorre azzerare la struttura di appartenenza, 
	 * i ruoli/mansioni e i gruppi della persona esterna
	 * @param vce
	 */
	public void codAooValueChange(ValueChangeEvent vce) throws Exception { 
		try {
			clearLookupAppartenenza();
			persona_interna.clearMansione();
			persona_interna.clearGruppoAppartenenza();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;		
		}
	}
	
	public String lookupAppartenenza() throws Exception {
		if (checkRequiredFieldLookup()) {
			clearLookupAppartenenza();
			return null;
		}
		
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		
		String value 		= (persona_interna.getNomeufficio()!= null && !"".equals(persona_interna.getNomeufficio())) ? persona_interna.getNomeufficio() : "";
		String aliasName 	= "struint_nome"; //aliasName 
		String aliasName1 	= ""; //aliasName1 
		String titolo 		= "xml,/struttura_interna/nome"; //titolo 
		String ord 			= "xml(xpart:/struttura_interna/nome)"; //ord 
		String campi 		= "nomeufficio=xml,/struttura_interna/nome ; .@cod_uff=xml,/struttura_interna/@cod_uff"; //campi
		String xq 			= "[struint_codammaoo]=" + persona_interna.getCod_amm() + persona_interna.getCod_aoo(); // xq
		String db 			= ""; //db 
		String newRecord 	= ""; //newRecord 
		
		callLookup(persona_interna, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearLookupAppartenenza() throws Exception{
		String campi = "nomeufficio=xml,/struttura_interna/nome ; .@cod_uff=xml,/struttura_interna/@cod_uff"; //campi
		return clearField(campi, persona_interna);
	}
	
	public String thVincolatoTitoloDeferenza() throws Exception {
		
		String value = (persona_interna.getTitolo_deferenza()!= null && !"".equals(persona_interna.getTitolo_deferenza())) ? persona_interna.getTitolo_deferenza() : "";
		String fieldName = ".@titolo_deferenza"; //fieldName 
		String chiave = "titolodeferenza"; //chiave 
		callThVincolato(persona_interna, fieldName, chiave, value);
		return null;
	}

	public String thVincolatoTitolo() throws Exception {
		
		String value = (persona_interna.getTitolo()!= null && !"".equals(persona_interna.getTitolo())) ? persona_interna.getTitolo() : "";
		String fieldName = ".@titolo"; //fieldName 
		String chiave = "titolo"; //chiave 
		callThVincolato(persona_interna, fieldName, chiave, value);
		return null;
	}

	public String thVincolatoQualifica() throws Exception {
		
		String value = (persona_interna.getQualifica().getText()!= null && !"".equals(persona_interna.getQualifica().getText())) ? persona_interna.getQualifica().getText() : "";
		String fieldName = ".@qualifica.text"; //fieldName 
		String chiave = "pi_qualifica"; //chiave 
		callThVincolato(persona_interna, fieldName, chiave, value);
		return null;
	}
	
	public String lookupGruppoAppartenenza() throws Exception {
		if (checkRequiredFieldLookup()) {
			clearLookupGruppoAppartenenza();
			return null;
		}
		
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		
		GruppoAppartenenza gruppo = (GruppoAppartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gruppo_appartenenza");
		int num = (persona_interna.getGruppo_appartenenza().contains(gruppo)) ? persona_interna.getGruppo_appartenenza().indexOf(gruppo): 0;
		
		String value 		= (persona_interna.getGruppo_appartenenza()!= null && !"".equals(persona_interna.getGruppo_appartenenza().get(num))) ? persona_interna.getGruppo_appartenenza().get(num).getNome() : "";
		String aliasName 	= "gruppi_nome"; //aliasName 
		String aliasName1 	= ""; //aliasName1 
		String titolo 		= "xml,/gruppo/nome"; //titolo 
		String ord 			= "xml(xpart:/gruppo/nome)"; //ord 
		String campi 		= ".gruppo_appartenenza["+num+"].@cod=xml,/gruppo/@id ; gruppo_appartenenza["+num+"].nome=xml,/gruppo/nome"; //campi
		String xq 			= "[gruppi_codammaoo]=" + persona_interna.getCod_amm() + persona_interna.getCod_aoo(); // xq 
		String db 			= ""; //db 
		String newRecord 	= ""; //newRecord
		
		callLookup(persona_interna, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearLookupGruppoAppartenenza() throws Exception{
		GruppoAppartenenza gruppo = (GruppoAppartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gruppo_appartenenza");
		int num = (persona_interna.getGruppo_appartenenza().contains(gruppo)) ? persona_interna.getGruppo_appartenenza().indexOf(gruppo): 0;
		String campi = ".gruppo_appartenenza["+num+"].@cod=xml,/gruppo/@id ; gruppo_appartenenza["+num+"].nome=xml,/gruppo/nome"; //campi
		return clearField(campi, persona_interna);
	}
	
	public String lookupMansione() throws Exception {
		if (checkRequiredFieldLookup()) {
			clearLookupGruppoAppartenenza();
			return null;
		}
		
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		
		Mansione mansione = (Mansione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mansione");
		int num = (persona_interna.getMansione().contains(mansione)) ? persona_interna.getMansione().indexOf(mansione): 0;
		
		String value 		= (persona_interna.getMansione()!= null && !"".equals(persona_interna.getMansione().get(num))) ? persona_interna.getMansione().get(num).getText() : "";
		String aliasName 	= "ruoli_nome"; //aliasName 
		String aliasName1 	= ""; //aliasName1 
		String titolo 		= "xml,/ruolo/nome &quot;^ (~&quot; xml,/ruolo/societa &quot;~^)&quot;"; //titolo 
		String ord 			= "xml(xpart:/ruolo/nome)"; //ord 
		String campi 		= ".mansione["+num+"].@cod=xml,/ruolo/@id ; .mansione["+num+"].@text=xml,/ruolo/nome"; //campi
		String xq 			= "[ruoli_codammaoo]=" + persona_interna.getCod_amm() + persona_interna.getCod_aoo(); // xq  
		String db 			= ""; //db 
		String newRecord 	= ""; //newRecord
		
		callLookup(persona_interna, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearLookupMansione() throws Exception{
		Mansione mansione = (Mansione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mansione");
		int num = (persona_interna.getMansione().contains(mansione)) ? persona_interna.getMansione().indexOf(mansione): 0;
		String campi = ".mansione["+num+"].@cod=xml,/ruolo/@id ; .mansione["+num+"].@text=xml,/ruolo/nome"; //campi
		return clearField(campi, persona_interna);
	}
	
	public boolean checkRequiredFieldLookup() {
		boolean result = false;
		if (persona_interna.getCod_amm() == null || "".equals(persona_interna.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:persint_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		if (persona_interna.getCod_aoo() == null || "".equals(persona_interna.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:persint_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		} 
		return result;
	}

	public boolean checkRequiredField(){
		boolean result = false;
		
		if (persona_interna.getCognome() == null || "".equals(persona_interna.getCognome().trim())) {
			this.setErrorMessage("templateForm:demsi_cognome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.surname") + "'");
			result = true;
		}
		if (persona_interna.getNome() == null || "".equals(persona_interna.getNome().trim())) {
			this.setErrorMessage("templateForm:demsi_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.name") + "'");
			result = true;
		}
		if (persona_interna.getCod_uff() == null || "".equals(persona_interna.getCod_uff().trim())) {
			this.setErrorMessage("templateForm:demsi_appartenenza_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.assigned") + "'");
			result = true;
		}
		if (persona_interna.getCod_amm() == null || "".equals(persona_interna.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:persint_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (persona_interna.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:persint_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (persona_interna.getCod_aoo() == null || "".equals(persona_interna.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:persint_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (persona_interna.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:persint_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}
		//TODO: Controllo password e conferma
		return result;
	}
}
