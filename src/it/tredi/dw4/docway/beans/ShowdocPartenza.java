package it.tredi.dw4.docway.beans;


import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Partenza;

public class ShowdocPartenza extends ShowdocDoc {
	
	public ShowdocPartenza() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		doc = new Partenza();
		doc.init(dom);
		
		initCommon(dom);
	}
	
	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/showdoc@partenza");
	}
	
}
