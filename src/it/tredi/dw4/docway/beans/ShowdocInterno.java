package it.tredi.dw4.docway.beans;


import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Interno;

public class ShowdocInterno extends ShowdocDoc {
	
	public ShowdocInterno() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		doc = new Interno();
		doc.init(dom);
		
		initCommon(dom);
	}
	
	@Override
	public void reload() throws Exception {
		super._reload("showdoc@interno");
		
	}
}
