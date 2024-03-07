package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.GruppoAppartenenza;
import it.tredi.dw4.acl.model.Login;
import it.tredi.dw4.acl.model.Mansione;
import it.tredi.dw4.acl.model.PersonaInterna;
import it.tredi.dw4.acl.model.Responsabilita;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.utils.Const;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocPersonaInterna extends AclShowdoc {
	
	private AclDocumentFormsAdapter formsAdapter;
	private PersonaInterna persona_interna;
	
	private boolean profilo_assegnato = false;
	
	private String matricolaPersonaSelezionata = null;
	
	private CompareRights compareRights;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public boolean isProfilo_assegnato() {
		return profilo_assegnato;
	}

	public void setProfilo_assegnato(boolean profilo_assegnato) {
		this.profilo_assegnato = profilo_assegnato;
	}
	
	public String getMatricolaPersonaSelezionata() {
		return matricolaPersonaSelezionata;
	}

	public void setMatricolaPersonaSelezionata(String matricolaPersonaSelezionata) {
		this.matricolaPersonaSelezionata = matricolaPersonaSelezionata;
	}

	public ShowdocPersonaInterna() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	persona_interna = new PersonaInterna();
    	persona_interna.init(domDocumento);
    	
    	if (persona_interna != null 
    			&& persona_interna.getProfile_cod() != null
    			&& persona_interna.getProfile_cod().length() > 0
    			&& !(persona_interna.getProfile_cod().equals(Const.PROFILE_DEFAULT_CODE)))
    		setProfilo_assegnato(true);
    	
    	// mbernardini 09/07/2015 : verifico se e' stata selezionata una persona interna per il confronto dei diritti
    	matricolaPersonaSelezionata = getMatricolaFromPersIntPerConfronto();
    	
    	// inizializzazione di componenti common
    	initCommons(domDocumento);
    }	
	
	public AclDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public void reload(ComponentSystemEvent event) throws Exception {
		reload();
	}
	
	public void reload() throws Exception {
		// Richiamo il _reload generico perchè non è detto che debba essere caricata la showdoc
		// della persona esterna (in caso di inserimento da struttura esterna viene caricata la 
		// showdoc della struttura esterna)
		super._reload();
		//super._reload("showdoc@persona_interna");
	}

	public void setPersona_interna(PersonaInterna persona_interna) {
		this.persona_interna = persona_interna;
	}

	public PersonaInterna getPersona_interna() {
		return persona_interna;
	}
	
	public String ripetiNuovo() throws Exception{
		formsAdapter.ripetiNuovo("persona_interna"); 
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(response);
		
		/*DocEditPersonaInterna docEditPersonaInterna = new DocEditPersonaInterna();
		docEditPersonaInterna.getFormsAdapter().fillFormsFromResponse(response);
		docEditPersonaInterna.init(response.getDocument());
		setSessionAttribute("docEditPersonaInterna", docEditPersonaInterna);*/
		buildSpecificDocEditPageAndReturnNavigationRule("persona_interna", response, isPopupPage());
		
		return "docEdit@persona_interna";
	}
	
	public String navigateResponsabilita() throws Exception{
		Responsabilita responsabilita = (Responsabilita) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("responsabilita");
		int physDoc = (null != responsabilita.getPhysDoc() && responsabilita.getPhysDoc().trim().length() > 0 ) ? Integer.valueOf(responsabilita.getPhysDoc()) : -1;
		formsAdapter.seekDoc(physDoc, true);
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(response);
		if (response.getRootElement().attributeValue("dbTable", "").equals("@struttura_interna")){
			ShowdocStrutturaInterna showdocStrutturaInterna = new ShowdocStrutturaInterna();
			showdocStrutturaInterna.getFormsAdapter().fillFormsFromResponse(response);
			showdocStrutturaInterna.init(response.getDocument());
			setSessionAttribute("showdocStrutturaInterna", showdocStrutturaInterna);
			
			return "showdoc@struttura_interna";
		}
		else if (response.getRootElement().attributeValue("dbTable", "").equals("@gruppo")){
			ShowdocGruppo showdocGruppo = new ShowdocGruppo();
			showdocGruppo.getFormsAdapter().fillFormsFromResponse(response);
			showdocGruppo.init(response.getDocument());
			setSessionAttribute("showdocGruppo", showdocGruppo);
			
			return "showdoc@gruppo";
		}
		return null;
	}

	public String navigateAppartenenza() throws Exception{
		int physDoc = (null != persona_interna.getPhysDoc() && persona_interna.getPhysDoc().trim().length() > 0 ) ? Integer.valueOf(persona_interna.getPhysDoc()) : -1;
		formsAdapter.seekDoc(physDoc, true);
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(response);
		
		ShowdocStrutturaInterna showdocStrutturaInterna = new ShowdocStrutturaInterna();
		showdocStrutturaInterna.getFormsAdapter().fillFormsFromResponse(response);
		showdocStrutturaInterna.init(response.getDocument());
		setSessionAttribute("showdocStrutturaInterna", showdocStrutturaInterna);
		
		return "showdoc@struttura_interna";
	}
	
	/**
	 * Caricamento dei gruppi ai quali appartiene la persona interna
	 * @return
	 * @throws Exception
	 */
	public String navigateGroup() throws Exception {
		try {
			GruppoAppartenenza gruppo = (GruppoAppartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gruppo_appartenenza");
			
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([gruppi_id]="+gruppo.getCod()+")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooPersonaInterna()) query += " AND [/gruppo/#cod_ammaoo/]=\"" + persona_interna.getCod_amm() + persona_interna.getCod_aoo() + "\"";
					
				return aclHome.queryPlain(query);
			}
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
		return null;
	}

	/**
	 * Caricamento dei profili ai quali appartiene la persona interna
	 * @return
	 * @throws Exception
	 */
	public String navigateProfile() throws Exception{
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([profilo_matricola]="+persona_interna.getProfile_cod()+")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooPersonaInterna()) query += " AND [/persona_interna/#cod_ammaoo/]=\"" + persona_interna.getCod_amm() + persona_interna.getCod_aoo() + "\"";
				return aclHome.queryPlain(query);
			}
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
		return null;
	}

	/**
	 * Caricamento dei ruoli assegnati alla persona interna
	 * @return
	 * @throws Exception
	 */
	public String navigateRuolo() throws Exception{
		try {
			Mansione mansione = (Mansione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mansione");
			
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([ruoli_id]="+mansione.getCod()+")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooPersonaInterna()) query += " AND [/ruolo/#cod_ammaoo/]=\"" + persona_interna.getCod_amm() + persona_interna.getCod_aoo() + "\"";
				return aclHome.queryPlain(query);
			}
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
		return null;
	}

	public String changePassword() throws Exception{
		Login login = (Login) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("login");
		formsAdapter.goToChangePwd(login.getName());
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
//		formsAdapter.fillFormsFromResponse(response);
		
		ChangePassword changePassword = new ChangePassword();
		changePassword.getFormsAdapter().fillFormsFromResponse(response);
		changePassword.init(response.getDocument());
		setSessionAttribute("changePassword", changePassword);
		
		return "changePassword";
	}
	
	/**
	 * Selezione di una persona corrente per un successivo confronto dei diritti
	 * @return
	 * @throws Exception
	 */
	public String selezionaPersona() throws Exception {
		// aggiunta in sessione della matricola della persona selezionata per il 
		// confronto dei diritti
		if (persona_interna.getMatricola() != null && persona_interna.getMatricola().length() > 0) {
			String persIntPerConfronto = persona_interna.getMatricola();
			if (persona_interna.getCod_amm() != null && persona_interna.getCod_aoo() != null)
				persIntPerConfronto += "," + persona_interna.getCod_amm() + persona_interna.getCod_aoo();
			setSessionAttribute("persIntPerConfronto", persIntPerConfronto);
			
			matricolaPersonaSelezionata = persona_interna.getMatricola();
		}
		return null;
	}
	
	public CompareRights getCompareRights() {
		return compareRights;
	}

	public void setCompareRights(CompareRights compareRights) {
		this.compareRights = compareRights;
	}	
	
	public boolean isCompareRightsActive() {
		return this.compareRights == null ? false : this.compareRights.isActive();
	}
	
	/**
	 * Confronto della sezione diritti fra due persone interne selezionate
	 * @return
	 * @throws Exception
	 */
	public String confrontaConPersona() throws Exception {
		if (matricolaPersonaSelezionata != null && matricolaPersonaSelezionata.length() > 0
											&& persona_interna.getMatricola() != null && persona_interna.getMatricola().length() > 0
											&& !matricolaPersonaSelezionata.equals(persona_interna.getMatricola())) {
			try {
				formsAdapter.controntaPersoneInterne((String) getSessionAttribute("persIntPerConfronto"), persona_interna.getMatricola() + "," + persona_interna.getCod_amm() + persona_interna.getCod_aoo());
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());

				CompareRights compareRights = new CompareRights();
				compareRights.getFormsAdapter().fillFormsFromResponse(response);
				compareRights.init(response.getDocument());
				compareRights.setActive(true);
				
				this.setCompareRights(compareRights);
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			}
			catch (Throwable t) {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			}
		}
		return null;
	}
	
	/**
	 * Confronto della sezione diritti fra una persona interna e il proprio
	 * profilo di appartenenza
	 * 
	 * @return
	 * @throws Exception
	 */
	public String confrontaConProfilo() throws Exception {
		if (persona_interna.getProfile_cod() != null && persona_interna.getProfile_cod().length() > 0
											&& persona_interna.getMatricola() != null && persona_interna.getMatricola().length() > 0) {
			try {
				formsAdapter.controntaProfiloPersonaInterna(persona_interna.getProfile_cod() + "," + persona_interna.getCod_amm() + persona_interna.getCod_aoo(), persona_interna.getMatricola() + "," + persona_interna.getCod_amm() + persona_interna.getCod_aoo());
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());

				CompareRights compareRights = new CompareRights();
				compareRights.getFormsAdapter().fillFormsFromResponse(response);
				compareRights.init(response.getDocument());
				compareRights.setActive(true);

				this.setCompareRights(compareRights);

				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			} catch (Throwable t) {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
			}
		}
		return null;
	}
	
	/**
	 * Ritorna la matricola della persona interna selezionata per il confronto
	 */
	private String getMatricolaFromPersIntPerConfronto() {
		String matricola = (String) getSessionAttribute("persIntPerConfronto");
		if (matricola != null) {
			int index = matricola.indexOf(",");
			if (index != -1)
				matricola.substring(0,  index);
		}
		return matricola;
	}
	
	/**
	 * Ritorna true se il cod_amm_aoo sulla persona interna non e' settato, false altrimenti
	 */
	private boolean isEmptyCodAmmAooPersonaInterna() {
		if (persona_interna != null 
				&& persona_interna.getCod_amm() != null && !persona_interna.getCod_amm().isEmpty() 
				&& persona_interna.getCod_aoo() != null && !persona_interna.getCod_aoo().isEmpty())
			return false;
		else
			return true;
	}
	
}
