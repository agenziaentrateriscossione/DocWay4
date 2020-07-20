package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Node;

public class Metadata extends XmlEntity {
	private HashMap<String, String> dati = new HashMap<String, String>();
	private DocumentMetadata documentMetadata = new DocumentMetadata();
	
	private String pageCount = "";
	private String fileSize = "";

	public Metadata() {}
    
	public Metadata(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		if (dom != null) {
			List<Node> metaNodes = dom.selectNodes("node()[name()='metadata']/*");
			if (metaNodes != null && metaNodes.size() > 0) {
				for (int i=0; i<metaNodes.size(); i++) {
					Node metaNode = (Node) metaNodes.get(i);
					if (metaNode != null && metaNode.getNodeType() == Node.ELEMENT_NODE && !metaNode.getName().equals("documentMetadata")) {
						this.dati.put(metaNode.getName().toLowerCase(), metaNode.getText());
					}
				}
			}
			this.documentMetadata.init(XMLUtil.createDocument(dom, "node()[name()='metadata']/node()[name()='documentMetadata']"));
		}
		
		if (dati != null) {
			if (dati.containsKey("filesize"))
				fileSize = dati.get("filesize");
			if (dati.containsKey("pagecount"))
				pageCount = dati.get("pagecount");
			
			if (documentMetadata != null && documentMetadata.getDati() != null) {
				if (fileSize.equals("") && documentMetadata.getDati().containsKey("filesize"))
					fileSize = documentMetadata.getDati().get("filesize");
				if (pageCount.equals("") && documentMetadata.getDati().containsKey("pagecount"))
					pageCount = documentMetadata.getDati().get("pagecount");
			}
		}
		
		if (!fileSize.equals("")) {
			try {
				fileSize = (new Integer(fileSize).intValue() / 1024) + " KB";
			}
			catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	/**
	 * Ritorna le chiavi dei metadati recuperati
	 */
	public List<String> getDatiKeys() {
		List<String> keys = new ArrayList<String>();
        keys.addAll(getDati().keySet());
        return keys;
    }
	
	public HashMap<String, String> getDati() {
		return dati;
	}

	public void setDati(HashMap<String, String> dati) {
		this.dati = dati;
	}

	public DocumentMetadata getDocumentMetadata() {
		return documentMetadata;
	}

	public void setDocumentMetadata(DocumentMetadata documentMetadata) {
		this.documentMetadata = documentMetadata;
	}
	
	public String getPageCount() {
		return pageCount;
	}

	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

}
