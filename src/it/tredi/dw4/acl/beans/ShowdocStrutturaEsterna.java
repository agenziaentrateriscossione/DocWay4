package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.StrutturaEsterna;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocStrutturaEsterna extends AclShowdoc {
	private AclDocumentFormsAdapter formsAdapter;
	private StrutturaEsterna struttura_esterna;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ShowdocStrutturaEsterna() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	struttura_esterna = new StrutturaEsterna();
    	struttura_esterna.init(domDocumento); 
    	
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
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/acl/showdoc@struttura_esterna");
	}

	public void setStruttura_esterna(StrutturaEsterna struttura_esterna) {
		this.struttura_esterna = struttura_esterna;
	}

	public StrutturaEsterna getStruttura_esterna() {
		return struttura_esterna;
	}
	
	/**
	 * Caricamento delle persone associate alla struttura esterna
	 * @return
	 * @throws Exception
	 */
	public String viewPerson() throws Exception {
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([persest_appartenenzacoduff]=\""+struttura_esterna.getCod_uff()+"\")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooStrutturaEsterna()) query += " AND [/persona_esterna/#cod_ammaoo/]=\"" + struttura_esterna.getCod_amm() + struttura_esterna.getCod_aoo() + "\"";
				aclHome.getFormsAdapter().getDefaultForm().addParam("qord", "xml(xpart:/persona_esterna/@cognome),xml(xpart:/persona_esterna/@nome)"); // TODO Andrebbe specificato all'interno di un file di properties
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
	 * Caricamento del responsabile della struttura esterna
	 * @return
	 * @throws Exception
	 */
	public String viewResponsabile() throws Exception {
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([persest_matricola]=\""+struttura_esterna.getCod_responsabile()+"\")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooStrutturaEsterna()) query += " AND [/persona_esterna/#cod_ammaoo/]=\"" + struttura_esterna.getCod_amm() + struttura_esterna.getCod_aoo() + "\"";
				aclHome.getFormsAdapter().getDefaultForm().addParam("qord", "xml(xpart:/persona_esterna/@cognome),xml(xpart:/persona_esterna/@nome)"); // TODO Andrebbe specificato all'interno di un file di properties
				return aclHome.queryPlain(query);
			}
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
		return null;
	}

	public String ripetiNuovo() throws Exception{
		formsAdapter.ripetiNuovo("struttura_esterna"); 
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(response);
		
		DocEditStrutturaEsterna docEditStrutturaEsterna = new DocEditStrutturaEsterna();
		docEditStrutturaEsterna.getFormsAdapter().fillFormsFromResponse(response);
		docEditStrutturaEsterna.init(response.getDocument());
		setSessionAttribute("docEditStrutturaEsterna", docEditStrutturaEsterna);
		
		return "docEdit@struttura_esterna";
	}
	
	public String insTableDocPersonaEsterna() throws Exception {
		formsAdapter.insPersona(struttura_esterna.getCod_uff(), struttura_esterna.getCod_amm(), struttura_esterna.getCod_aoo(), null);
		
		XMLDocumento responseDoc = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
		
		DocEditPersonaEsterna docEditPersonaEsterna = new DocEditPersonaEsterna();
		docEditPersonaEsterna.getFormsAdapter().fillFormsFromResponse(responseDoc);
		docEditPersonaEsterna.init(responseDoc.getDocument());
		
		setSessionAttribute("docEditPersonaEsterna", docEditPersonaEsterna);
		
		return "docEdit@persona_esterna";
	}
	
	public String apriGerarchia() throws Exception{
		AclHome home = (AclHome)getSessionAttribute("aclHome");
		home.getFormsAdapter().paginaBrowse(getFormsAdapter().getDefaultForm().getParam("physDoc"));
		XMLDocumento responseDoc = home.getFormsAdapter().getHierBrowserForm().executePOST(getUserBean());
		
		//istanzia il bean della pagina della gerarchia
		AclHierBrowser hierBrowser = new AclHierBrowser();
		//riempi il formsAdapter della pagina di destinazione
		hierBrowser.getFormsAdapter().fillFormsFromResponse(responseDoc);
		hierBrowser.init(responseDoc.getDocument());
		
		setSessionAttribute("hierBrowser", hierBrowser);
		return null;
	}
	
	/**
	 * Ritorna true se il cod_amm_aoo sulla struttura esterna non e' settato, false altrimenti
	 */
	private boolean isEmptyCodAmmAooStrutturaEsterna() {
		if (struttura_esterna != null 
				&& struttura_esterna.getCod_amm() != null && !struttura_esterna.getCod_amm().isEmpty() 
				&& struttura_esterna.getCod_aoo() != null && !struttura_esterna.getCod_aoo().isEmpty())
			return false;
		else
			return true;
	}
	
}
