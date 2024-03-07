package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.Gruppo;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocGruppo extends AclShowdoc {
	private AclDocumentFormsAdapter formsAdapter;
	private Gruppo gruppo;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ShowdocGruppo() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	gruppo = new Gruppo();
    	gruppo.init(domDocumento);
    	
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
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/acl/showdoc@gruppo");
	}

	public void setGruppo(Gruppo gruppo) {
		this.gruppo = gruppo;
	}

	public Gruppo getGruppo() {
		return gruppo;
	}
	
	/**
	 * Caricamento delle persone associate al gruppo
	 * @return
	 * @throws Exception
	 */
	public String viewPerson() throws Exception {
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([persint_gruppoappartenenzacod]="+gruppo.getId()+")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooGruppo()) query += " AND [/persona_interna/#cod_ammaoo/]=\"" + gruppo.getCod_amm() + gruppo.getCod_aoo() + "\"";
				aclHome.getFormsAdapter().getDefaultForm().addParam("qord", "xml(xpart:/persona_interna/@cognome),xml(xpart:/persona_interna/@nome)"); // TODO Andrebbe specificato all'interno di un file di properties
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
	 * Caricamento del responsabile del gruppo
	 * @return
	 * @throws Exception
	 */
	public String viewResponsabile() throws Exception {
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query = "([persint_matricola]="+gruppo.getCod_responsabile()+")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooGruppo()) query += " AND [/persona_interna/#cod_ammaoo/]=\"" + gruppo.getCod_amm() + gruppo.getCod_aoo() + "\"";
				
				aclHome.getFormsAdapter().getDefaultForm().addParam("qord", "xml(xpart:/persona_interna/@cognome),xml(xpart:/persona_interna/@nome)"); // TODO Andrebbe specificato all'interno di un file di properties
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
	 * Ritorna true se il cod_amm_aoo sul gruppo non e' settato, false altrimenti
	 */
	private boolean isEmptyCodAmmAooGruppo() {
		if (gruppo != null 
				&& gruppo.getCod_amm() != null && !gruppo.getCod_amm().isEmpty() 
				&& gruppo.getCod_aoo() != null && !gruppo.getCod_aoo().isEmpty())
			return false;
		else
			return true;
	}

}
