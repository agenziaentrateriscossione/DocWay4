package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Stampa allegati da lista titoli: Documento selezionato
 */
public class DocToPrint extends XmlEntity {
	
	private String physdoc;
	private String nrecord;
	private List<AttachToPrint> attachments = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.physdoc = XMLUtil.parseStrictAttribute(dom, "document/@physdoc");
		this.nrecord = XMLUtil.parseStrictAttribute(dom, "document/@nrecord");
		this.attachments = XMLUtil.parseSetOfElement(dom, "document/attachment", new AttachToPrint());
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getPhysdoc() {
		return physdoc;
	}

	public void setPhysdoc(String physdoc) {
		this.physdoc = physdoc;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}
	
	public List<AttachToPrint> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<AttachToPrint> attachments) {
		this.attachments = attachments;
	}

}
