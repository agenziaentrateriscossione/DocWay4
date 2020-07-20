package it.tredi.dw4.docway.beans;


import javax.faces.event.ComponentSystemEvent;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.custom.Ordine;
import it.tredi.dw4.utils.Const;

import org.dom4j.Document;
import org.dom4j.Element;

public class ShowdocArrivoORD extends ShowdocDoc {
	
	public ShowdocArrivoORD() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		Element root = dom.getRootElement();
		
		xml = dom.asXML();
		if (root.attributeValue("view", "").equals("verificaDuplicati"))
			this.verificaDuplicati = true;
		doc = new Ordine();
		doc.init(dom);
		
		initCommon(dom);
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
	
	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload("showdoc@arrivo@ORD");
		
	}
	
	/**
	 * Generazione di una fattura a partire dall'ordine
	 * @return
	 * @throws Exception
	 */
	public String generaFattura() throws Exception {
		try {
			this.formsAdapter.getDefaultForm().addParam("codice_rep", "FTR");
			this.formsAdapter.getDefaultForm().addParam("descrizione_rep", "Fattura");
			
			this.formsAdapter.ripetiNuovo(Const.DOCWAY_TIPOLOGIA_PARTENZA, "ripetinuovo");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_PARTENZA, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
