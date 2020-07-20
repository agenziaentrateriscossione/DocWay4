package it.tredi.dw4.docway.beans;


import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.custom.Fattura;

import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocPartenzaFTR extends ShowdocDoc {
	
	public ShowdocPartenzaFTR() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		doc = new Fattura();
		doc.init(dom);
		
		initCommon(dom);
	}
	
	public void reload(ComponentSystemEvent e) throws Exception {
		this.reload();
	}
	
	@Override
	public void reload() throws Exception {
		super._reload("showdoc@partenza@FTR");
	}
	
	@Override
	public String nuovoDoc() throws Exception {
		return nuovoRepertorio();
	}
}
