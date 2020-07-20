package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocRuolo extends AclShowdoc {
	private AclDocumentFormsAdapter formsAdapter;
	private it.tredi.dw4.acl.model.Ruolo ruolo;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ShowdocRuolo() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	ruolo = new it.tredi.dw4.acl.model.Ruolo();
    	ruolo.init(domDocumento);
    }	
	
	public AclDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public void reload(ComponentSystemEvent event) throws Exception {
		reload();
	}
	
	public void reload() throws Exception {
		super._reload("showdoc@ruolo");
	}

	public void setRuolo(it.tredi.dw4.acl.model.Ruolo ruolo) {
		this.ruolo = ruolo;
	}

	public it.tredi.dw4.acl.model.Ruolo getRuolo() {
		return ruolo;
	}
	
	/**
	 * Caricamento delle persone associate al ruolo
	 * @return
	 * @throws Exception
	 */
	public String viewPerson() throws Exception {
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([persint_mansionecod]="+ruolo.getId()+")";
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
	
}
