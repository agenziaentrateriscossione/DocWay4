package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.doc.adapters.delibere.SedutaDocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.delibere.Seduta;
import it.tredi.dw4.i18n.I18N;

public class DocEditSeduta extends DocEditDocSeduta {
	
	public DocEditSeduta() throws Exception {
		super();
		this.formsAdapter = new SedutaDocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public boolean isDocEditModify() {
		return false;
	}
	
	@Override
	public Seduta getDoc() {
		return this.doc;
	}
	
	public void init(Document dom) {
		super.init(dom);
		docEditTitle = doc.getOrgano() + " - " + I18N.mrs("dw4.nuova_seduta");
	}

	public String saveDocument() throws Exception {
		return super.saveDocument();
	}

	public String clearDocument() throws Exception {
		return super.clearDocument();
	}
}
