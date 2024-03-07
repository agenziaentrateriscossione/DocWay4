package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Stampa allegati da lista titoli: Elenco dei documenti selezionati per la stampa degli allegati contenuti
 */
public class AttachmentsToPrint extends XmlEntity {
	
	private int numDocs;
	private List<DocToPrint> docs = new ArrayList<>();

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.numDocs = Integer.parseInt(XMLUtil.parseStrictAttribute(dom, "/response/attachments/@num_docs", "0"));
		this.docs = XMLUtil.parseSetOfElement(dom, "/response/attachments/document", new DocToPrint());
		if (this.numDocs == 0)
			this.numDocs = this.docs.size();
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public int getNumDocs() {
		return numDocs;
	}

	public void setNumDocs(int numDocs) {
		this.numDocs = numDocs;
	}

	public List<DocToPrint> getDocs() {
		return docs;
	}

	public void setDocs(List<DocToPrint> docs) {
		this.docs = docs;
	}

}
