package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclLoadingbarFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.LoadingbarFormsAdapter;
import it.tredi.dw4.beans.LuaStoredProcThrobber;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.utils.XMLDocumento;

/**
 * Modale di attesa di completamento della stored procedure LUA per l'applicazione ACL
 */
public class AclLuaStoredProcThrobber extends LuaStoredProcThrobber {

	private AclLoadingbarFormsAdapter formsAdapter;
	
	private Page pageBean; // Bean della pagina che contiene il modale di attesa
	private XMLDocumento document;

	public AclLuaStoredProcThrobber(Page pageBean) throws Exception {
		this.pageBean = pageBean;
		this.formsAdapter = new AclLoadingbarFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	@Override
	public void init(XMLDocumento response) {
		super.commonInit(response);
	}

	@Override
	public LoadingbarFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public XMLDocumento getDocument() {
		return document;
	}
	
	/**
	 * Refresh del modale di attesa: Verifica, tramite chiamata al service, del completamento della stored procedure LUA
	 * @return
	 * @throws Exception
	 */
	public String refresh() throws Exception {
		try {
			XMLDocumento response = super._refresh();
			formsAdapter.fillFormsFromResponse(response);
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			init(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return null;
	}
	
	/**
	 * Chiusura del modale di attesa di completamento della stored procedure LUA
	 * @return
	 */
	public String close() throws Exception {
		setActive(false);
		try {
			// Eventuale refresh del bean della pagina
			if (pageBean != null && pageBean instanceof Showdoc)
				((Showdoc) pageBean).reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
