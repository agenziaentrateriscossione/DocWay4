package it.tredi.dw4.docway.beans;


import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Varie;

public class ShowdocVarie extends ShowdocDoc {
	
	public ShowdocVarie() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		doc = new Varie();
		doc.init(dom);
		
		initCommon(dom);
	}
	
	@Override
	public void reload() throws Exception {
		super._reload("showdoc@varie");
	}
}
