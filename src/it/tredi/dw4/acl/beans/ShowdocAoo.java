package it.tredi.dw4.acl.beans;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class ShowdocAoo extends AclShowdoc {
	private String xml;
	
	private AclDocumentFormsAdapter formsAdapter;
	private it.tredi.dw4.acl.model.Aoo aoo;
	
	private boolean adminAcl = false; // vale true se l'utente corrente e' superUser o amministratore di ACL
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ShowdocAoo() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	aoo = new it.tredi.dw4.acl.model.Aoo();
    	aoo.init(domDocumento);
    	
    	if (StringUtil.booleanValue(XMLUtil.parseStrictAttribute(domDocumento, "/response/@adminAcl")))
    		adminAcl = true;
    	else
    		adminAcl = false;
    	
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
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/acl/showdoc@aoo");
	}

	public void setAoo(it.tredi.dw4.acl.model.Aoo aoo) {
		this.aoo = aoo;
	}

	public it.tredi.dw4.acl.model.Aoo getAoo() {
		return this.aoo;
	}
	
	public boolean isAdminAcl() {
		return adminAcl;
	}

	public void setAdminAcl(boolean admin) {
		this.adminAcl = admin;
	}

	public String ripetiNuovo() throws Exception{
		formsAdapter.ripetiNuovo("aoo"); 
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(response);
		
		DocEditAoo docEditAoo = new DocEditAoo();
		docEditAoo.getFormsAdapter().fillFormsFromResponse(response);
		docEditAoo.init(response.getDocument());
		setSessionAttribute("docEditAoo", docEditAoo);
		
		return "docEdit@aoo";
	}
	
	/**
	 * Creazione di una nuova casella di posta elettronica (mailbox di interoperabilita'/archiviazione gestita da mailArchiver)
	 * @return
	 * @throws Exception
	 */
	public String insTableDocCasellaPostaElettronica() throws Exception {
		try {
			formsAdapter.insCasellaPostaElettronica(aoo.getCod_amm(), aoo.getCod_aoo());
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			DocEditCasellaPostaElettronica docEditCasellaPostaElettronica = new DocEditCasellaPostaElettronica();
			docEditCasellaPostaElettronica.getFormsAdapter().fillFormsFromResponse(response);
			docEditCasellaPostaElettronica.init(response.getDocument());
			
			setSessionAttribute("docEditCasellaPostaElettronica", docEditCasellaPostaElettronica);
			
			return "docEdit@casellaPostaElettronica";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

}
