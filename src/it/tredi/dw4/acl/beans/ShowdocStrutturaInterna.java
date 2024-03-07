package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.StrutturaInterna;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocStrutturaInterna extends AclShowdoc {
	private AclDocumentFormsAdapter formsAdapter;
	private StrutturaInterna struttura_interna;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ShowdocStrutturaInterna() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	struttura_interna = new StrutturaInterna();
    	struttura_interna.init(domDocumento);
    	
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
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/acl/showdoc@struttura_interna");
	}
	
	public void setStruttura_interna(StrutturaInterna struttura_interna) {
		this.struttura_interna = struttura_interna;
	}

	public StrutturaInterna getStruttura_interna() {
		return struttura_interna;
	}

	public String insTableDocPersonaInterna() throws Exception {
		try {		
			formsAdapter.insPersona(struttura_interna.getCod_uff(), struttura_interna.getCod_amm(), struttura_interna.getCod_aoo(), null);
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}	
			
			/*DocEditPersonaInterna docEditPersonaInterna = new DocEditPersonaInterna();
			docEditPersonaInterna.getFormsAdapter().fillFormsFromResponse(response);
			docEditPersonaInterna.init(response.getDocument());
			docEditPersonaInterna.loadAspects(formsAdapter.getDefaultForm().getParam("dbTable"), response, "docEdit");
			setSessionAttribute("docEditPersonaInterna", docEditPersonaInterna);*/
			buildSpecificDocEditPageAndReturnNavigationRule("persona_interna", response, isPopupPage());
			
			return "docEdit@persona_interna";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}			
	}
	
	/**
	 * Caricamento delle persone associate alla struttura interna
	 * @return
	 * @throws Exception
	 */
	public String viewPerson() throws Exception{
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([persint_coduff]="+struttura_interna.getCod_uff()+")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooStrutturaInterna()) query += " AND [/persona_interna/#cod_ammaoo/]=\"" + struttura_interna.getCod_amm() + struttura_interna.getCod_aoo() + "\"";
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
	 * Caricamento del responsabile della struttura interna
	 * @return
	 * @throws Exception
	 */
	public String viewResponsabile() throws Exception{
		try {
			AclHome aclHome = (AclHome) getSessionAttribute("aclHome");
			if (aclHome != null) {
				String query =  "([persint_matricola]="+struttura_interna.getCod_responsabile()+")";
				// mbernardini 29/10/2015 : restrizione su AOO (per eventuale accorpamento di differenti archivi ACL - rilassamento unique rule)
				if (!isEmptyCodAmmAooStrutturaInterna()) query += " AND [/persona_interna/#cod_ammaoo/]=\"" + struttura_interna.getCod_amm() + struttura_interna.getCod_aoo() + "\"";
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
	
	public String relSetRoot() throws Exception{
		try {
			getFormsAdapter().relSetRoot();
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//reload della gerarchia
			refreshHier();
				
			getFormsAdapter().fillFormsFromResponse(response);
			if("@reload".equals(getFormsAdapter().getDefaultForm().getParam("dbTable")))
				reload();
			else if("query".equals(getFormsAdapter().getDefaultForm().getParam("verbo")))
				return "show@acl_home";
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String ripetiNuovo() throws Exception{
		try {
			formsAdapter.ripetiNuovo("struttura_interna"); 
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}			
			
			formsAdapter.fillFormsFromResponse(response);
			
			DocEditStrutturaInterna docEditStrutturaInterna = new DocEditStrutturaInterna();
			docEditStrutturaInterna.getFormsAdapter().fillFormsFromResponse(response);
			docEditStrutturaInterna.init(response.getDocument());
			setSessionAttribute("docEditStrutturaInterna", docEditStrutturaInterna);
			
			return "docEdit@struttura_interna";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}	
	}
	
	public String insChild() throws Exception{
		try {		
			String physDoc = getFormsAdapter().getDefaultForm().getParam("physDoc");
			getFormsAdapter().insChild(physDoc, struttura_interna.getCod_amm(), struttura_interna.getCod_aoo());
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}			
			
			formsAdapter.fillFormsFromResponse(response);
			DocEditStrutturaInterna docEditStrutturaInterna = new DocEditStrutturaInterna();
			docEditStrutturaInterna.init(response.getDocument());
			docEditStrutturaInterna.getFormsAdapter().fillFormsFromResponse(response);
			setSessionAttribute("docEditStrutturaInterna", docEditStrutturaInterna);		
			
			//reload della gerarchia
			refreshHier();		
			
			return "docEdit@struttura_interna";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}				
	}
	
	public String insFratPrima() throws Exception{
		try {		
			String physDoc = getFormsAdapter().getDefaultForm().getParam("physDoc");
			getFormsAdapter().insFratPrima(physDoc, struttura_interna.getCod_amm(), struttura_interna.getCod_aoo());
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}	
			
			formsAdapter.fillFormsFromResponse(response);
			DocEditStrutturaInterna docEditStrutturaInterna = new DocEditStrutturaInterna();
			docEditStrutturaInterna.getFormsAdapter().fillFormsFromResponse(response);
			docEditStrutturaInterna.init(response.getDocument());
			setSessionAttribute("docEditStrutturaInterna", docEditStrutturaInterna);		
			
			//reload della gerarchia
			refreshHier();		
			
			return "docEdit@struttura_interna";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}			
	}
	
	public String insFratDopo() throws Exception{
		try {				
			String physDoc = getFormsAdapter().getDefaultForm().getParam("physDoc");
			getFormsAdapter().insFratDopo(physDoc, struttura_interna.getCod_amm(), struttura_interna.getCod_aoo());
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}			
			
			formsAdapter.fillFormsFromResponse(response);
			DocEditStrutturaInterna docEditStrutturaInterna = new DocEditStrutturaInterna();
			docEditStrutturaInterna.getFormsAdapter().fillFormsFromResponse(response);
			docEditStrutturaInterna.init(response.getDocument());
			setSessionAttribute("docEditStrutturaInterna", docEditStrutturaInterna);		
			
			//reload della gerarchia
			refreshHier();		
			
			return "docEdit@struttura_interna";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}			
	}
	
	public String copiaNodo() throws Exception {
		try {
			String physDoc = getFormsAdapter().getDefaultForm().getParam("physDoc");
			getFormsAdapter().copiaNodo(physDoc);
			
			//set session parameter x propagazione su tutti i forms adapter
			getUserBean().setServiceFormParam("nodoCopiato", physDoc);
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}			
			
			formsAdapter.fillFormsFromResponse(response);
			this.init(response.getDocument());
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}		
	}
	
	public String incollaComeFiglio() throws Exception{
		try {
			String physDoc = getFormsAdapter().getDefaultForm().getParam("physDoc");
			getFormsAdapter().incollaComeFiglio(physDoc);
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());

			//remove session parameter x propagazione su tutti i forms adapter
			getUserBean().setServiceFormParam("nodoCopiato", null);					
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}			
			
			formsAdapter.fillFormsFromResponse(response);
			this.init(response.getDocument());
			
			//reload della gerarchia
			refreshHier();		
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}			
	}
	
	public String estraDaGerarchia() throws Exception{
		try {
			String physDoc = getFormsAdapter().getDefaultForm().getParam("physDoc");
			getFormsAdapter().estraiDaGerarchia(physDoc);
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
				
			formsAdapter.fillFormsFromResponse(response);
			this.init(response.getDocument());
			
			//reload della gerarchia
			refreshHier();			
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public boolean isNodoCopiato(){
		int physDoc = getFormsAdapter().getDefaultForm().getParamAsInt("physDoc");
		
//int nodoCopiato = getFormsAdapter().getDefaultForm().getParamAsInt("nodoCopiato");
		String nodoCopiatoS = getUserBean().getServiceFormParam("nodoCopiato");
		int nodoCopiato = -1;
		if (nodoCopiatoS != null && nodoCopiatoS.length() > 0)
			nodoCopiato = Integer.parseInt(nodoCopiatoS);

		if( nodoCopiato > 0 && nodoCopiato != physDoc ) 	
			 return true;
		return false;
	}
	
	public boolean getEnableFrat() {
		String nodeType = getFormsAdapter().getDefaultForm().getParam("nodeType");
		if( nodeType.equals("ENABLE_FRAT") ) 
			return true;
		else
			return false;
	}
	
	public boolean getEnableCopy() {
		String nodeType = getFormsAdapter().getDefaultForm().getParam("nodeType");
		if( nodeType.equals("ENABLE_COPY") ) 
			return true;
		else
			return false;
	}
	
	public String apriGerarchia() throws Exception{
		try {		
			AclHome home = (AclHome)getSessionAttribute("aclHome");
			home.getFormsAdapter().paginaBrowse(getFormsAdapter().getDefaultForm().getParam("physDoc"));
			XMLDocumento response = home.getFormsAdapter().getHierBrowserForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}		
			
			//istanzia il bean della pagina della gerarchia
			AclHierBrowser hierBrowser = new AclHierBrowser();
			//riempi il formsAdapter della pagina di destinazione
			hierBrowser.getFormsAdapter().fillFormsFromResponse(response);
			hierBrowser.init(response.getDocument());
			
			setSessionAttribute("hierBrowser", hierBrowser);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public void refreshHier() throws Exception {
		//refresh della home
		AclHome aclHome = (AclHome)getSessionAttribute("aclHome");
		aclHome.init();		
	}
	
	/**
	 * Ritorna true se il cod_amm_aoo sulla struttura interna non e' settato, false altrimenti
	 */
	private boolean isEmptyCodAmmAooStrutturaInterna() {
		if (struttura_interna != null 
				&& struttura_interna.getCod_amm() != null && !struttura_interna.getCod_amm().isEmpty() 
				&& struttura_interna.getCod_aoo() != null && !struttura_interna.getCod_aoo().isEmpty())
			return false;
		else
			return true;
	}
}
