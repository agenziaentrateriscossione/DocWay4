package it.tredi.dw4.docway.beans;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Doc;

import org.dom4j.Document;

/**
 * Showdoc immagini del documento (apertura immagini a popup)
 * 
 * @author mbernardini
 */
public class ShowdocImmagini extends ShowdocDoc {

	public ShowdocImmagini() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		doc = new Doc();
		doc.init(dom);
		
		initCommon(dom);
	}

	@Override
	public void reload() throws Exception {
		// TODO non necessario in apertura immagini da popup
	}
}
