package it.tredi.dw4.docway.beans;


import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Arrivo;

public class ShowdocArrivo extends ShowdocDoc {
	
	public ShowdocArrivo() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		Element root = dom.getRootElement();
		
		xml = dom.asXML();
		if (root.attributeValue("view", "").equals("verificaDuplicati"))
			this.verificaDuplicati = true;
		doc = new Arrivo();
		doc.init(dom);
		
		initCommon(dom);
	}
	
	@Override
	public void reload() throws Exception {
		super._reload("showdoc@arrivo");
		
	}
}
