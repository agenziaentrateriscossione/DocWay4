package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.Appartenenza;
import it.tredi.dw4.acl.model.PersonaEsterna;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class DocEditModifyPersonaEsterna extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private PersonaEsterna persona_esterna;

	private String xml;
	
	private String label_dati_attivita = "";
	private String label_dati_personali = "";
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	
	public String getLabel_dati_attivita() {
		return label_dati_attivita;
	}

	public void setLabel_dati_attivita(String label_dati_attivita) {
		this.label_dati_attivita = label_dati_attivita;
	}

	public String getLabel_dati_personali() {
		return label_dati_personali;
	}

	public void setLabel_dati_personali(String label_dati_personali) {
		this.label_dati_personali = label_dati_personali;
	}
	
	public DocEditModifyPersonaEsterna() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.persona_esterna = new PersonaEsterna();
    	this.persona_esterna.init(domDocumento);    
    	
    	// verifica che per tutte le strutture di appertenenza sia specificato il nome
    	// della struttura. In caso di ruoli multipli su stessa struttura solo la prima istanza
    	// della struttura contiene il nome, le altre solo il codice
    	if (persona_esterna.getAppartenenza() != null 
    			&& persona_esterna.getAppartenenza().size() > 1) {
    		for (int i=0; i<persona_esterna.getAppartenenza().size(); i++) {
    			if (persona_esterna.getAppartenenza().get(i) != null && persona_esterna.getAppartenenza().get(i).getNome() != null 
    					&& persona_esterna.getAppartenenza().get(i).getNome().length() == 0 && persona_esterna.getAppartenenza().get(i).getCod_uff().length() > 0) {
    				// struttura senza nome
    				int j = 0;
    				boolean found = false;
    				while (j<persona_esterna.getAppartenenza().size() && !found) {
    					if (persona_esterna.getAppartenenza().get(j) != null && persona_esterna.getAppartenenza().get(j).getCod_uff() != null 
    							&& persona_esterna.getAppartenenza().get(j).getCod_uff().equals(persona_esterna.getAppartenenza().get(i).getCod_uff())) {
    						persona_esterna.getAppartenenza().get(i).setNome(persona_esterna.getAppartenenza().get(j).getNome());
    						persona_esterna.getAppartenenza().get(i).setPhysDoc(persona_esterna.getAppartenenza().get(j).getPhysDoc());
    						found = true;
    					}
    					j++;
    				}
    			}
    		}
    	}
    	
    	label_dati_attivita = XMLUtil.parseStrictAttribute(domDocumento, "/response/@dicitDatiAttivita");
    	label_dati_personali = XMLUtil.parseStrictAttribute(domDocumento, "/response/@dicitDatiPersonali");
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.persona_esterna.asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("persona_esterna", "list_of_struttur");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("persona_esterna", response);	
			return "showdoc@persona_esterna@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setpersona_esterna(PersonaEsterna persona_esterna) {
		this.persona_esterna = persona_esterna;
	}

	public PersonaEsterna getPersona_esterna() {
		return this.persona_esterna;
	}

	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("persona_esterna", response);
			return "showdoc@persona_esterna@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
			
	}
	
	public String lookupRecapitoComune() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		String value = (persona_esterna.getRecapito().getIndirizzo().getComune()!= null && !"".equals(persona_esterna.getRecapito().getIndirizzo().getComune())) ? persona_esterna.getRecapito().getIndirizzo().getComune() : "";
		String campi = ".recapito.indirizzo.@comune=xml,/comune/@nome ; .recapito.indirizzo.@cap=xml,/comune/@cap ; .recapito.indirizzo.@prov=xml,/comune/@prov ; .recapito.indirizzo.@nazione=xml,/comune/@nazione";
		callLookupComune(persona_esterna, campi, value);
		return null;
	}
	
	public String clearLookupRecapitoComune() throws Exception{
		String campi = ".recapito.indirizzo.@comune=xml,/comune/@nome ; .recapito.indirizzo.@cap=xml,/comune/@cap ; .recapito.indirizzo.@prov=xml,/comune/@prov ; .recapito.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, persona_esterna);
	}
	
	public String lookupRecapitoCap() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		String value = (persona_esterna.getRecapito().getIndirizzo().getCap()!= null && !"".equals(persona_esterna.getRecapito().getIndirizzo().getCap())) ? persona_esterna.getRecapito().getIndirizzo().getCap() : "";
		String campi = ".recapito.indirizzo.@comune=xml,/comune/@nome ; .recapito.indirizzo.@cap=xml,/comune/@cap ; .recapito.indirizzo.@prov=xml,/comune/@prov ; .recapito.indirizzo.@nazione=xml,/comune/@nazione";
		callLookupCap(persona_esterna, campi, value);
		return null;
	}
	
	public String clearLookupRecapitoCap() throws Exception{
		String campi = ".recapito.indirizzo.@comune=xml,/comune/@nome ; .recapito.indirizzo.@cap=xml,/comune/@cap ; .recapito.indirizzo.@prov=xml,/comune/@prov ; .recapito.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, persona_esterna);
	}
		
	public String lookupRecapitoPersonaleComune() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		String value = (persona_esterna.getRecapito_personale().getIndirizzo().getComune()!= null && !"".equals(persona_esterna.getRecapito_personale().getIndirizzo().getComune())) ? persona_esterna.getRecapito_personale().getIndirizzo().getComune() : "";
		String campi = ".recapito_personale.indirizzo.@comune=xml,/comune/@nome ; .recapito_personale.indirizzo.@cap=xml,/comune/@cap ; .recapito_personale.indirizzo.@prov=xml,/comune/@prov ; .recapito_personale.indirizzo.@nazione=xml,/comune/@nazione";
		callLookupComune(persona_esterna, campi, value);
		return null;
	}
	
	public String clearLookupRecapitoPersonaleComune() throws Exception{
		String campi = ".recapito_personale.indirizzo.@comune=xml,/comune/@nome ; .recapito_personale.indirizzo.@cap=xml,/comune/@cap ; .recapito_personale.indirizzo.@prov=xml,/comune/@prov ; .recapito_personale.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, persona_esterna);
	}
	
	public String lookupRecapitoPersonaleCap() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		String value = (persona_esterna.getRecapito_personale().getIndirizzo().getCap()!= null && !"".equals(persona_esterna.getRecapito_personale().getIndirizzo().getCap())) ? persona_esterna.getRecapito_personale().getIndirizzo().getCap() : "";
		String campi = ".recapito_personale.indirizzo.@comune=xml,/comune/@nome ; .recapito_personale.indirizzo.@cap=xml,/comune/@cap ; .recapito_personale.indirizzo.@prov=xml,/comune/@prov ; .recapito_personale.indirizzo.@nazione=xml,/comune/@nazione";
		callLookupCap(persona_esterna, campi, value);
		return null;
	}
	
	public String clearLookupRecapitoPersonaleCap() throws Exception{
		String campi = ".recapito_personale.indirizzo.@comune=xml,/comune/@nome ; .recapito_personale.indirizzo.@cap=xml,/comune/@cap ; .recapito_personale.indirizzo.@prov=xml,/comune/@prov ; .recapito_personale.indirizzo.@nazione=xml,/comune/@nazione"; //campi
		return clearField(campi, persona_esterna);
	}
	
	public String thVincolatoTitoloDeferenza() throws Exception {
		
		String value = (persona_esterna.getTitolo_deferenza()!= null && !"".equals(persona_esterna.getTitolo_deferenza())) ? persona_esterna.getTitolo_deferenza() : "";
		String fieldName = ".@titolo_deferenza";
		String chiave = "titolodeferenza";
		callThVincolato(persona_esterna, fieldName, chiave, value);
		return null;
	}

	public String thVincolatoTitolo() throws Exception {
		
		String value = (persona_esterna.getTitolo()!= null && !"".equals(persona_esterna.getTitolo())) ? persona_esterna.getTitolo() : "";
		String fieldName = ".@titolo";
		String chiave = "titolo";
		callThVincolato(persona_esterna, fieldName, chiave, value);	
		return null;
	}
	
	/**
	 * In caso di modifica del campo codice amm occorre azzerare tutte le strutture di appartenenza
	 * della persona esterna
	 * @param vce
	 */
	public void codAmmValueChange(ValueChangeEvent vce) throws Exception {
		try {
			persona_esterna.clearAppartenenza();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;		
		}
    }
	
	/**
	 * In caso di modifica del campo codice aoo occorre azzerare tutte le strutture di appartenenza
	 * della persona esterna
	 * @param vce
	 */
	public void codAooValueChange(ValueChangeEvent vce) throws Exception { 
		try {
			persona_esterna.clearAppartenenza();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;		
		}
	}

	public String lookupStrutturaAppartenenza() throws Exception {
		if (checkRequiredFieldLookupAppartenenza()) {
			clearLookupStrutturaAppartenenza();
			return null;
		}
		
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		
		Appartenenza appartenenza = (Appartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("appartenenza");
		int num = (persona_esterna.getAppartenenza().contains(appartenenza)) ? persona_esterna.getAppartenenza().indexOf(appartenenza): 0;
		
		String xq = "[struest_codammaoo]=" + persona_esterna.getCod_amm() + persona_esterna.getCod_aoo();
		
		String value = (persona_esterna.getAppartenenza()!= null && persona_esterna.getAppartenenza().get(num) != null) ? persona_esterna.getAppartenenza().get(num).getNome() : "";

		String aliasName 	= "struest_nome";
		String aliasName1 	= "";
		String titolo 		= "xml,/struttura_esterna/nome &quot;^ [csap: ~&quot; XML,/struttura_esterna/@cod_SAP &quot;~^]&quot;"; //titolo 
		String ord 			= "xml(xpart:/struttura_esterna/nome)"; //ord 
		String campi 		= ".appartenenza["+num+"].@cod_uff=xml,/struttura_esterna/@cod_uff ; .appartenenza["+num+"].@nome=xml,/struttura_esterna/nome"; //campi
		String db 			= ""; //db 
		String newRecord 	= ""; //newRecord 

		callLookup(persona_esterna, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearLookupStrutturaAppartenenza() throws Exception{
		Appartenenza appartenenza = (Appartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("appartenenza");
		int num = (persona_esterna.getAppartenenza().contains(appartenenza)) ? persona_esterna.getAppartenenza().indexOf(appartenenza): 0;
		String campi = ".appartenenza["+num+"].@cod_uff=xml,/struttura_esterna/@cod_uff ; .appartenenza["+num+"].@nome=xml,/struttura_esterna/nome"; //campi
		return clearField(campi, persona_esterna);
	}
	
	public String thVincolatoRuolo() throws Exception {
		Appartenenza appartenenza = (Appartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("appartenenza");
		int num = (persona_esterna.getAppartenenza().contains(appartenenza)) ? persona_esterna.getAppartenenza().indexOf(appartenenza): 0;
		String value = (persona_esterna.getAppartenenza()!= null && !"".equals(persona_esterna.getAppartenenza().get(num).getQualifica())) ? persona_esterna.getAppartenenza().get(num).getQualifica() : "";
		String fieldName = ".appartenenza["+num+"].@qualifica"; //fieldName 
		String chiave = "pe_ruolo"; //chiave
		callThVincolato(persona_esterna, fieldName, chiave, value);
		return null;
	}
	
	public String lookupComuneNascita() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		String value = (persona_esterna.getLuogo_nascita() != null && !"".equals(persona_esterna.getLuogo_nascita())) ? persona_esterna.getLuogo_nascita() : "";
		String campi = ".@luogo_nascita=xml,/comune/@nome";
		callLookupComune(persona_esterna, campi, value);
		return null;
	}
	
	public String clearLookupComuneNascita() throws Exception{
		String campi = ".@luogo_nascita=xml,/comune/@nome"; //campi
		return clearField(campi, persona_esterna);
	}
	
	public boolean checkRequiredFieldLookupAppartenenza() {
		boolean result = false;
		if (persona_esterna.getCod_amm() == null || "".equals(persona_esterna.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:persest_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		if (persona_esterna.getCod_aoo() == null || "".equals(persona_esterna.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:persest_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		} 
		return result;
	}
	
	public boolean checkRequiredField(){
		boolean result = false;
		if (persona_esterna.getCognome() == null || "".equals(persona_esterna.getCognome().trim())) {
			this.setErrorMessage("templateForm:demsi_cognome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.surname") + "'");
		    result = true;
		}
		if (persona_esterna.getNome() == null || "".equals(persona_esterna.getNome().trim())){
			this.setErrorMessage("templateForm:demsi_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.name") + "'");
			result = true;
		}
		if (persona_esterna.getData_nascita() != null && persona_esterna.getData_nascita().length() > 0) {
			// Verifico se la data di nascita specificata è corretta
			String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
			if (!DateUtil.isValidDate(persona_esterna.getData_nascita(), formatoData)) {
				this.setErrorMessage("templateForm:dataNascita", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.birthdate") + "': " + formatoData.toLowerCase());
				result = true;
			}
		}
		
		String toDo = formsAdapter.getDefaultForm().getParam("toDo");
		if (toDo.contains("$comunePersonaliObbligatorio$")) {
			boolean recapitoCompilato = true;
			if (persona_esterna.getRecapito_personale().getIndirizzo().getComune() == null || "".equals(persona_esterna.getRecapito_personale().getIndirizzo().getComune().trim())
					|| persona_esterna.getRecapito_personale().getIndirizzo().getCap() == null || "".equals(persona_esterna.getRecapito_personale().getIndirizzo().getCap().trim())
					|| persona_esterna.getRecapito_personale().getIndirizzo().getIndirizzo() == null || "".equals(persona_esterna.getRecapito_personale().getIndirizzo().getIndirizzo().trim()))
				recapitoCompilato = false;
			
			if (!recapitoCompilato) {
				String[] fieldIds = { "templateForm:demsi_indirizzovia", "templateForm:demsi_indirizzocomune_input", "templateForm:demsi_indirizzocap_input" };
				this.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_il_recapito_su_dati_personali"));
				result = true;
			}
		}
		if (toDo.contains("$comuneObbligatorio$")) {
			boolean recapitoCompilato = true;
			if (persona_esterna.getRecapito().getIndirizzo().getComune() == null || "".equals(persona_esterna.getRecapito().getIndirizzo().getComune().trim())
					|| persona_esterna.getRecapito().getIndirizzo().getCap() == null || "".equals(persona_esterna.getRecapito().getIndirizzo().getCap().trim())
					|| persona_esterna.getRecapito().getIndirizzo().getIndirizzo() == null || "".equals(persona_esterna.getRecapito().getIndirizzo().getIndirizzo().trim()))
				recapitoCompilato = false;
			
			if (!recapitoCompilato) {
				String[] fieldIds = { "templateForm:demsi_recapito_indirizzovia", "templateForm:demsi_recapito_indirizzocomune_input", "templateForm:demsi_recapito_indirizzocap_input" };
				this.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_il_recapito_su_dati_attivita"));
				result = true;
			}
		}
		
		if (persona_esterna.getCod_amm() == null || "".equals(persona_esterna.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:persest_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (persona_esterna.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:persest_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (persona_esterna.getCod_aoo() == null || "".equals(persona_esterna.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:persest_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (persona_esterna.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:persest_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}
		return result;
	}
}
