package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocProfilo extends AclShowdoc {
	private AclDocumentFormsAdapter formsAdapter;
	private it.tredi.dw4.acl.model.Profilo profilo;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ShowdocProfilo() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	profilo = new it.tredi.dw4.acl.model.Profilo();
    	profilo.init(domDocumento);
    	
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
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/acl/showdoc@profilo");
	}

	public void setProfilo(it.tredi.dw4.acl.model.Profilo profilo) {
		this.profilo = profilo;
	}

	public it.tredi.dw4.acl.model.Profilo getProfilo() {
		return profilo;
	}
	
	public String ripetiNuovo() throws Exception{
		formsAdapter.ripetiNuovo("profilo"); 
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(response);
		
		DocEditProfilo docEditProfilo = new DocEditProfilo();
		docEditProfilo.getFormsAdapter().fillFormsFromResponse(response);
		docEditProfilo.init(response.getDocument());
		setSessionAttribute("docEditProfilo", docEditProfilo);
		
		return "docEdit@profilo";
	}

	/**
	 * Caricamento delle persone associate al profilo
	 * @return
	 * @throws Exception
	 */
	public String viewPerson() throws Exception{
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([persint_profilecod]="+profilo.getMatricola_profilo()+")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooProfilo()) query += " AND [/persona_interna/#cod_ammaoo/]=\"" + profilo.getCod_amm() + profilo.getCod_aoo() + "\"";
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
	 * Ritorna true se il cod_amm_aoo sul profilo non e' settato, false altrimenti
	 */
	private boolean isEmptyCodAmmAooProfilo() {
		if (profilo != null 
				&& profilo.getCod_amm() != null && !profilo.getCod_amm().isEmpty() 
				&& profilo.getCod_aoo() != null && !profilo.getCod_aoo().isEmpty())
			return false;
		else
			return true;
	}
}
