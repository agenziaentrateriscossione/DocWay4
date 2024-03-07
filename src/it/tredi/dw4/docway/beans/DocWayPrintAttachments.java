package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.adapters.DocWayPrintAttachmentsFormsAdapter;
import it.tredi.dw4.docway.model.AttachmentsToPrint;

/**
 * Stampa degli allegati di una selezione di documenti (se e solo se IWX risulta installato e abilitato)
 */
public class DocWayPrintAttachments extends Page {
	
	private DocWayPrintAttachmentsFormsAdapter formsAdapter;
	
	private AttachmentsToPrint attachmentsToPrint;
	
	private boolean visible;
	
	public DocWayPrintAttachments() throws Exception {
		this.formsAdapter = new DocWayPrintAttachmentsFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public FormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}
	
	public void init(Document dom) {
    	attachmentsToPrint = new AttachmentsToPrint();
		attachmentsToPrint.init(dom);
    }
	
	public AttachmentsToPrint getAttachmentsToPrint() {
		return attachmentsToPrint;
	}

	public void setAttachmentsToPrint(AttachmentsToPrint attachmentsToPrint) {
		this.attachmentsToPrint = attachmentsToPrint;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * Chiusura del modale di stampa allegati della selezione di documenti
	 * @return
	 */
	public String close() {
		setVisible(false);
		return null;
	}

}
