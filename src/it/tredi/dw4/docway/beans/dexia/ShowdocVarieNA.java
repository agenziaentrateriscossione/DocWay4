package it.tredi.dw4.docway.beans.dexia;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.ShowdocVarie;

import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

public class ShowdocVarieNA extends ShowdocVarie {

	public ShowdocVarieNA() throws Exception {
		super();
	}

	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/dexia/showdoc@varie@NA"); // in questo caso il prefisso "dexia_" non deve essere messo
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
	/**
	 * abrogazione del documento
	 * @return
	 * @throws Exception
	 */
	public String abrogaDoc() throws Exception {
		try {
			this.formsAdapter.abrogaDoc();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			DocWayAbrogazioneDoc abrogazioneDoc = new DocWayAbrogazioneDoc();
			abrogazioneDoc.getFormsAdapter().fillFormsFromResponse(response);
			abrogazioneDoc.init(response.getDocument());
			abrogazioneDoc.setShowdoc(this);
			setSessionAttribute("docwayAbrogazioneDoc", abrogazioneDoc);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * caricamento della pagina specifica di ricerca norme aziendali (personalView)
	 * @return
	 * @throws Exception
	 */
	public String gotoRicercaNorme() throws Exception {
		try {
			String tableName = "globale";
			if (getPersonalView() != null && getPersonalView().length() > 0)
				tableName = getDoc().getTipo() + "#personalView=" + getPersonalView();
			
			formsAdapter.gotoTableQ(tableName, false);
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return buildSpecificQueryPageAndReturnNavigationRule(getDoc().getTipo(), response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
