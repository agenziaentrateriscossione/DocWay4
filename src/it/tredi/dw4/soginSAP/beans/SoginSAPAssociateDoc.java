package it.tredi.dw4.soginSAP.beans;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditDoc;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.soginSAP.model.SAPDoc;
import it.tredi.dw4.utils.XMLDocumento;

public class SoginSAPAssociateDoc extends DocEditDoc {
	private SAPDoc doc = new SAPDoc();

	public SoginSAPAssociateDoc() throws Exception {
		super();
	}

	@Override
	public void init(Document domDocumento) {
		this.doc = new SAPDoc();
		this.doc.init(domDocumento);
		
		try {
			DocDocWayDocumentFormsAdapter documentFormsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
			documentFormsAdapter.fillFormsFromResponse(new XMLDocumento(domDocumento));
			
			if (doc.getRepertorio() != null && doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
				documentFormsAdapter.modifyTableDoc(doc.getRepertorio().getCod(), doc.getRepertorio().getText());
			else
				documentFormsAdapter.modifyTableDoc();
			
			XMLDocumento response = documentFormsAdapter.getDefaultForm().executePOST(getUserBean());
			this.formsAdapter.fillFormsFromResponse(response);
			
			if (!handleErrorResponse(response)) {
				// inizializzazione common per tutte le tipologie di documenti di DocWay
				initCommon(response.getDocument());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public Doc getDoc() {
		return this.doc;
	}

	@Override
	public boolean isDocEditModify() {
		return true;
	}
	
	public String saveDocument(Map<String, String> params) throws Exception {
		try {
			// personalizzazione del salvataggio per il repertorio
			boolean isRepertorio = false;
			if (doc.getRepertorio() != null && doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
				isRepertorio = true;
			
			formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams("", true, isRepertorio));
			
			formsAdapter.getDefaultForm().addParams(params);
			
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return response.asXML();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	@Override
	public String saveDocument() throws Exception {
		return null;
	}
	
	@Override
	public XmlEntity getModel() {
		return this.doc;
	}
}
