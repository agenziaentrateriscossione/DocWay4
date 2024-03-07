package it.tredi.dw4.docway.beans;


import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.custom.Offerta;
import it.tredi.dw4.utils.Const;

import org.dom4j.Document;

public class ShowdocPartenzaOFF extends ShowdocDoc {
	
	public ShowdocPartenzaOFF() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		doc = new Offerta();
		doc.init(dom);
		
		initCommon(dom);
	}
	
	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/showdoc@partenza@OFF");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
	/**
	 * Generazione di un ordine a partire dall'offerta
	 * @return
	 * @throws Exception
	 */
	public String generaOrdine() throws Exception {
		try {
			this.formsAdapter.getDefaultForm().addParam("codice_rep", "ORD");
			this.formsAdapter.getDefaultForm().addParam("descrizione_rep", "Ordine");
			
			this.formsAdapter.ripetiNuovo(Const.DOCWAY_TIPOLOGIA_ARRIVO, "ripetinuovo");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_ARRIVO, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
