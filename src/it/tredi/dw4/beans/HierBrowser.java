package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.HierBrowserFormsAdapter;

import org.dom4j.Document;

public abstract class HierBrowser extends Page {
	public abstract void init(Document dom);
	
	public abstract HierBrowserFormsAdapter getFormsAdapter();
	public abstract String mostraDocumento() throws Exception;
	public abstract String docToggle() throws Exception;
	
	public String paginaPrecedente() throws Exception {
		if (getFormsAdapter().paginaPrecedente()) {
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());			
		}
		return null;
	}	
	
	public String paginaSuccessiva() throws Exception {
		if (getFormsAdapter().paginaSuccessiva()) {
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());			
		}
		return null;
	}
	
	protected XMLDocumento _mostraDocumento(String nDoc) throws Exception {
		getFormsAdapter().mostraDocumento(nDoc);
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	protected XMLDocumento _docToggle(String nDoc) throws Exception {
		getFormsAdapter().docToggle(nDoc);
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	protected XMLDocumento _docToggle(String nDoc, String nDocCurrent) throws Exception {
		getFormsAdapter().docToggle(nDoc, nDocCurrent);
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
}
