package it.tredi.dw4.addons.soginSAP;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.addons.BaseAddOn;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.soginSAP.beans.SoginSAPShowdocDoc;
import it.tredi.dw4.soginSAP.model.SAPDoc;

public class SoginDocButtons extends BaseAddOn {
	private SAPDoc doc;
	protected DocDocWayDocumentFormsAdapter formsAdapter;
	private SoginSAPShowdocDoc soginSAPShowdocDoc;

	public SoginDocButtons(String template, Page host) throws Exception {
		super(template, host);
		
		this.statusTemplate = "/soginSAP/docStatus@SAP.xhtml";
		
		this.soginSAPShowdocDoc = new SoginSAPShowdocDoc() {
			
			@Override
			public void reload() throws Exception {
				
			}
			
			@Override
			public void init(Document dom) {
				
			}
		};
	}

	@Override
	public void init(Document dom) {
		doc = new SAPDoc();
		doc.init(dom);
		
		try {
			soginSAPShowdocDoc.getFormsAdapter().fillFormsFromResponse(new XMLDocumento(dom));
			soginSAPShowdocDoc.setDoc(doc);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
		}
	}
	
	public SAPDoc getDoc() {
		return doc;
	}

	public void setDoc(SAPDoc doc) {
		this.doc = doc;
	}
	
	public String paginaTitoli() throws Exception {
		return soginSAPShowdocDoc.paginaTitoli();	
	}
	
	public String asssignToSAP() throws Exception {
		return soginSAPShowdocDoc.asssignToSAP();
	}
	
	public boolean isSapTitlesExists() {
		return soginSAPShowdocDoc.isSapTitlesExists();
	}

	@Override
	public Map<String, String> asFormAdapterParams() {
		return null;
	}

	@Override
	public void clear() {
	}
	
	@Override
	public String asQuery() {
		return null;
	}
	
}
