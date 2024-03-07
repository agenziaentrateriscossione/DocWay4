package it.tredi.dw4.acl.beans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.CredenzialeFirmaRemota;
import it.tredi.dw4.acl.model.GruppoAppartenenza;
import it.tredi.dw4.acl.model.Mansione;
import it.tredi.dw4.acl.model.PersonaInterna;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.docway.model.Delega;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;

public class DocEditModifyPersonaInterna extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private PersonaInterna persona_interna;

	private String xml;

	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	public DocEditModifyPersonaInterna() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}

	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.persona_interna = new PersonaInterna();
    	this.persona_interna.init(domDocumento);
    	//tiommi 28/09/2017 - gestione deleghe vuote
    	if (this.persona_interna.getDeleghe().isEmpty())
    		this.persona_interna.getDeleghe().add(new Delega(true));
    	
    	//dpranteda 23/09/2019 - credenziali per firma remota
    	if (this.persona_interna.getFirmeRemote().isEmpty())
    		this.persona_interna.getFirmeRemote().add(new CredenzialeFirmaRemota());

    }

	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.persona_interna.asFormAdapterParams(""));

			XMLDocumento response = super._saveDocument("persona_interna", "list_of_struttur");

			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			//Loadingbar - aggiornamento delle voci di indice
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				AclLoadingbar aclLoadingbar = new AclLoadingbar();
				aclLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				aclLoadingbar.init(response);
				setLoadingbar(aclLoadingbar);
				aclLoadingbar.setActive(true);
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
			XMLDocumento response = super._clearDocument();

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
		String value = (persona_interna.getRecapito().getIndirizzo().getCap()!= null && !"".equals(persona_interna.getRecapito().getIndirizzo().getCap())) ? persona_interna.getRecapito().getIndirizzo().getCap() : "";
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
		String db	 		= ""; //db
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
		String titolo 		= "xml,/ruolo/nome \"^ (~\" xml,/ruolo/societa \"~^)\""; //titolo
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

	public boolean checkRequiredField() {
		boolean result = false;
		if (persona_interna.getMatricola() != null && persona_interna.getMatricola().length() > 0)
			persona_interna.setMatricola(persona_interna.getMatricola().trim());

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
		//tiommi 30/09/2017 - controlli su deleghe, gestiti con mappa di appoggio per notificare una sola volta negli errorMsg gli errori ripetuti
		List<Delega> deleghe = persona_interna.getDeleghe();
		List<String> matricoleProcessate = new ArrayList<String>();
		Map<String, String[]> error_fields = new HashMap<String, String[]>();
		if (deleghe != null && !deleghe.isEmpty()) {
			for (int i = 0; i < deleghe.size(); i++) {
				Delega d = deleghe.get(i);
				if (!d.isBlank()) {
					// Controllo su persona
					if (d.getNomeUfficio() == null || d.getNomeUfficio().isEmpty()) {
						String error = I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.ufficio_delegato") + "'";
						String[] fields = {"templateForm:delega:"+i+":ufficio_delegato_input"};
						addErrorFields(error_fields, error, fields);
						result = true;
					}
					if (d.getNomePersona() == null || d.getNomePersona().isEmpty()) {
						String error = I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.nome_cognome_delegato") + "'";
						String[] fields = {"templateForm:delega:"+i+":nome_delegato_input"};
						addErrorFields(error_fields, error, fields);
						result = true;
					}
					if (matricoleProcessate.contains(d.getCodPersona())) {
						String error = I18N.mrs("dw4.delega_gia_presente");
						String[] fields = { "templateForm:delega:"+i+":ufficio_delegato_input", "templateForm:delega:"+i+":nome_delegato_input"};
						addErrorFields(error_fields, error, fields);
						result = true;
					}
					if (d.getCodPersona() != null && d.getCodPersona().equals(this.persona_interna.getMatricola())) {
						String error = I18N.mrs("dw4.il_delegato_conincide_con_il_delegante");
						String[] fields = { "templateForm:delega:"+i+":ufficio_delegato_input", "templateForm:delega:"+i+":nome_delegato_input"};
						addErrorFields(error_fields, error, fields);
						result = true;
					}
					// Controllo su date
					boolean permanente = d.isPermanente();
					String startDate = d.getInizio();
					String endDate = d.getFine();
					LocalDate startLocalDate = d.inizioToLocalDate();
					LocalDate endLocalDate = d.fineToLocalDate();
					if (!permanente) {
						if (startDate == null || startDate.isEmpty()) {
							String error = I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.durata_inizio") + "'";
							String[] fields = {"templateForm:delega:"+i+":durataDelega_from"};
							addErrorFields(error_fields, error, fields);
							result = true;
						}
						if (endDate == null || endDate.isEmpty()) {
							String error = I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.durata_fine") + "'";
							String[] fields = {"templateForm:delega:"+i+":durataDelega_to"};
							addErrorFields(error_fields, error, fields);
							result = true;
						}
						if (startLocalDate != null && endLocalDate != null && startLocalDate.isAfter(endLocalDate)) {
							String error = I18N.mrs("dw4.la_data_di_inzio_e_di_fine_risultano_invertite");
							String[] fields = { "templateForm:delega:"+i+":durataDelega_from", "templateForm:delega:"+i+":durataDelega_to"};
							addErrorFields(error_fields, error, fields);
							result = true;
						}
					}
				}
				// aggiunge alle processate
				if (d.getCodPersona() != null && !d.getCodPersona().isEmpty())
					matricoleProcessate.add(d.getCodPersona());
			}
		}
		// processa mappa errori deleghe
		for (String error : error_fields.keySet()) {
			this.setErrorMessage(error_fields.get(error), error);
		}
		
		
		//dpranteda 23/09/2019 - credenziali per firma remota
		List<CredenzialeFirmaRemota> firmeRemote = this.getPersona_interna().getFirmeRemote();
		Set<String> providers = new HashSet<String>();
		Map<String, String[]> error_fields_firma = new HashMap<String, String[]>();
		
		String error = I18N.mrs("dw4.errore_provider_firma");
		for(int i=0;i<firmeRemote.size();i++) {
			if(!providers.add(firmeRemote.get(i).getProvider())){
				String[] error_fields_f = {"templateForm:credenzialeFirmaRemota:"+i+":provider"};
				addErrorFields(error_fields_firma, error, error_fields_f);
				result = true;
			}
		}
		
		for (String error_f : error_fields_firma.keySet()) {
			this.setErrorMessage(error_fields_firma.get(error_f), error_f);
		}
		
		//TODO: Controllo password e conferma
		return result;
	}

	// metodo per inserire nella mappa
	private void addErrorFields(Map<String, String[]> error_fields, String error, String[] fields) {
		if (!error_fields.containsKey(error)) {
			error_fields.put(error, fields);
		}
		else {
			List<String> fields_old = new ArrayList<String>(Arrays.asList(error_fields.get(error)));
			List<String> fields_new = new ArrayList<String>(Arrays.asList(fields));
			fields_old.addAll(fields_new);
			String[] fields_all = fields_old.toArray(new String[fields_old.size()]);
			error_fields.put(error, fields_all);
		}
	}

	public void lookupUfficioDelegato() throws Exception {
		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
		int num = (this.persona_interna.getDeleghe().contains(delega)) ? this.persona_interna.getDeleghe().indexOf(delega) : 0;

		String value 		= (delega != null) ? delega.getNomeUfficio() : "";
		String value2 		= (delega != null) ? delega.getNomePersona() : "";
		String aliasName 	= "struint_nome"; //aliasName
		String aliasName1 	= ""; //aliasName1
		String titolo 		= "xml,/struttura_interna/nome"; //titolo
		String ord 			= "xml(xpart:/struttura_interna/nome)"; //ord
		String campi 		= "deleghe["+num+"].@nomeUfficio" +
								"|deleghe["+num+"].@nomePersona" +
								"|deleghe["+num+"].@codUfficio" +
								"|deleghe["+num+"].@codPersona";
		String xq 			= "[struint_codammaoo]=" + persona_interna.getCod_amm() + persona_interna.getCod_aoo(); // xq
		String db 			= ""; //db
		String newRecord 	= ""; //newRecord

		// disattivato per il lookup il filtro su AOO perche' gia' impostato come extraQuery del lookup
		// il formsAdapter viene poi ripulito all'interno del metodo callRifintLookup()
		formsAdapter.getIndexForm().addParam("_cd", FormsAdapter.setParameterFromCustomTupleValue("skipAOORestriction", "true", formsAdapter.getIndexForm().getParam("_cd")));

		callRifintLookup(persona_interna, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value+"|"+value2);

	}

	public void lookupNomeDelegato() throws Exception {
		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
		int num = (this.persona_interna.getDeleghe().contains(delega)) ? this.persona_interna.getDeleghe().indexOf(delega) : 0;

		String value 		= (delega != null) ? delega.getNomeUfficio() : "";
		String value2 		= (delega != null) ? delega.getNomePersona() : "";
		String aliasName 	= "persint_nomcogn";
		String aliasName1 	= "";
		String titolo 		= "xml,/persona_interna/@cognome xml,/persona_interna/@nome";
		String ord 			= "xml(xpart:/persona_interna/@cognome)";
		String db 			= "";
		String newRecord 	= "";
		String campi 		= "deleghe["+num+"].@nomeUfficio" +
								"|deleghe["+num+"].@nomePersona" +
								"|deleghe["+num+"].@codUfficio" +
								"|deleghe["+num+"].@codPersona";
		String xq = "[struint_codammaoo]=" + persona_interna.getCod_amm() + persona_interna.getCod_aoo() + " OR [persint_codammaoo]=" + persona_interna.getCod_amm() + persona_interna.getCod_aoo(); //extraQuery

		// disattivato per il lookup il filtro su AOO perche' gia' impostato come extraQuery del lookup
		// il formsAdapter viene poi ripulito all'interno del metodo callRifintLookup()
		formsAdapter.getIndexForm().addParam("_cd", FormsAdapter.setParameterFromCustomTupleValue("skipAOORestriction", "true", formsAdapter.getIndexForm().getParam("_cd")));

		callRifintLookup(persona_interna, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value+"|"+value2);

	}

	/**
	 * Pulizia dei campi di lookup su persona Delegato
	 * @return
	 * @throws Exception
	 */
	public String clearLookupPersonaDelegato() throws Exception {
		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
		int num = persona_interna.getDeleghe().indexOf(delega);

		String value = delega != null ? delega.getNomePersona() : "";

		if (value.isEmpty()) {
			String campi = "deleghe["+num+"].@nomeUfficio" +
							"|deleghe["+num+"].@nomePersona" +
							"|deleghe["+num+"].@codUfficio" +
							"|deleghe["+num+"].@codPersona";

			clearFieldRifint(campi, persona_interna);
		}

		return null;
	}

	/**
	 * Pulizia dei campi di lookup su persona Delegato
	 * @return
	 * @throws Exception
	 */
	public String clearLookupUfficioDelegato() throws Exception {
		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
		int num = persona_interna.getDeleghe().indexOf(delega);

		String value = delega != null ? delega.getNomeUfficio() : "";

		if (value.isEmpty()) {
			String campi = "deleghe["+num+"].@nomeUfficio" +
							"|deleghe["+num+"].@nomePersona" +
							"|deleghe["+num+"].@codUfficio" +
							"|deleghe["+num+"].@codPersona";

			clearFieldRifint(campi, persona_interna);
		}

		return null;
	}

}
