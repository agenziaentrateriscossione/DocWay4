package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import it.highwaytech.apps.code.CryptoUtils;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.folderTree.DocListNode;
import it.tredi.dw4.docway.beans.folderTree.FolderTreeNode;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;
import sun.security.provider.certpath.OCSPResponse.ResponseStatus;


/**
 * ERM015806
 * 
 * bean per la gestione della lista dei documenti del fascicolo
 * @author Laco
 *
 */
public class DocWayTitlesDocumentiFascicolo extends DocWayTitles {
	private String folderId;
	private boolean visible;
	private boolean popupPage;	
	private String title;
	private List<DocListNode> documents;
	private String seleId;

	public DocWayTitlesDocumentiFascicolo() throws Exception {
		super();
	}	
	
	@Override
	public void init(Document dom) throws Exception {
		super.init(dom);
		
		Node sn = dom.selectSingleNode("//attachTree/@seleId");
		if(sn != null) {
			seleId = sn.getStringValue();
			
			// setup CryptoUtils con chiave che ariva in response
			// CryptoUtils se hano gia una chiave ignoreranno questa chiamata
			String suppKey = dom.selectSingleNode("//attachTree/@suppKey").getStringValue();
			if(suppKey != null && suppKey.length() > 0){
				CryptoUtils.setKeyFromString(suppKey);
			}
			
			documents = new ArrayList<>();
			List<Node> atcht = dom.selectNodes("//attachTree/doc");
			for(Node n : atcht){
				documents.add(new DocListNode((Element)n));
			}
		}		
	}
	
	
	public String getSeleId() {
		return seleId;
	}

	public String closeModal() throws Exception {
		visible = false;
		setSessionAttribute("docWayTitlesDocumentiFascicolo", null);
		return null;
	}
	
	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isPopupPage() {
		return popupPage;
	}

	public void setPopupPage(boolean popupPage) {
		this.popupPage = popupPage;
	}

	public List<DocListNode> getDocuments() {
		return documents;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
