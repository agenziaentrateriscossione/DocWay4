package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import it.highwaytech.apps.code.CryptoUtils;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.folderTree.DocListNode;
import it.tredi.dw4.docway.beans.folderTree.FolderTreeNode;
import it.tredi.dw4.docway.beans.folderTree.PagingHandler;
import it.tredi.dw4.docway.model.GerarchiaFascicolo;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;
import sun.security.provider.certpath.OCSPResponse.ResponseStatus;


/**
 * ERM015806
 * 
 * bean per la gestione del albero di fascicoli
 * @author Laco
 *
 */
public class ShowdocFascicoloAlbero extends ShowdocFascicolo {
	private String folderId;
	private FolderTreeNode folderTreeRoot;
	private String currentFolderIdForDocList;
	private boolean popupPage;
	protected String action;
	protected boolean exportFilterPresent;
	
	
	private PagingHandler phnd;
	
	public boolean canExport() {
		// check if first node have some counter
		return Integer.parseInt(folderTreeRoot.getCounter()) > 0;
	}
	
	// se ce frasi di ricerca per il filtro
	public boolean canExportFiltered() {
		// check if first node have some counter
		return exportFilterPresent;
	}
	
	public boolean isExportEmpty() {
		// check if first node have some counter
		return !folderTreeRoot.getChecked();
	}
	
	public boolean isPopupPage() {
		return popupPage;
	}

	public void setPopupPage(boolean popupPage) {
		this.popupPage = popupPage;
	}
	
	public String getFolderId() {
		return folderId;
	}
	
	public FolderTreeNode getFolderTree() {
		return folderTreeRoot;
	}
	
	public ShowdocFascicoloAlbero() throws Exception {
		super();
	}	
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		this.folderId = XMLUtil.parseStrictAttribute(dom, "/response/@loadFolderTree_folderId", "-----");
		
		// setup CryptoUtils con chiave che ariva in response
		// CryptoUtils se hano gia una chiave ignoreranno questa chiamata
		String suppKey = dom.selectSingleNode("//folderTree/@suppKey").getStringValue();
		if(suppKey != null && suppKey.length() > 0){
			CryptoUtils.setKeyFromString(suppKey);
		}
		
		// read exportFilterPresent
		String expFp = dom.selectSingleNode("//folderTree/@exportFilterPresent").getStringValue();
		if(expFp != null && expFp.equals("true")){
			exportFilterPresent = true;
		}
		
		Element elFt = XMLUtil.loadElement(dom, "//folderTree/folder");
		
		if(elFt != null){
			folderTreeRoot = new FolderTreeNode(new XMLDocumento(elFt.createCopy()).getRootElement());
		}
	}
	
	public void  showDocDetail(String folderId, boolean isRoot) throws Exception{
		this.currentFolderIdForDocList = folderId;
		
		List<String> flds = new ArrayList<>();
		flds.add(folderId);
		if(isRoot) {
			flds.add(folderId + "*");
		}
		loadDocuments(flds); // this will call server
	}
	
	private void loadAllDocuments() throws Exception {
		List<String> flds = new ArrayList<>();
		folderTreeRoot.getSelectedFolders(flds);
		loadDocuments(flds);
	}
	
	public void showDocFirstPage() throws Exception {
		if(phnd == null) return; // just do nothing
		getFormsAdapter().loadFolderTreeDocumentsFirstPage(phnd);
		callDocumentsPage();
	}
	
	public void showDocPrevPage() throws Exception {
		if(phnd == null) return; // just do nothing
		getFormsAdapter().loadFolderTreeDocumentsPrevPage(phnd);
		callDocumentsPage();
	}
	
	public void showDocNextPage() throws Exception {
		if(phnd == null) return; // just do nothing
		getFormsAdapter().loadFolderTreeDocumentsNextPage(phnd);
		callDocumentsPage();
	}
	
	public void showDocLastPage() throws Exception {
		if(phnd == null) return; // just do nothing
		getFormsAdapter().loadFolderTreeDocumentsLastPage(phnd);
		callDocumentsPage();
	}
	
	// prende page dal Paginghandler che e stato bindato su form
	public void showDocSpecificPage() throws Exception {
		if(phnd == null) return; // just do nothing
		getFormsAdapter().loadFolderTreeDocumentsCurrentPage(phnd);
		callDocumentsPage();
	}	
	
	public PagingHandler getPhnd() {
		return phnd;
	}

	private void loadDocuments(List<String> flds) throws Exception {
		getFormsAdapter().loadFolderTreeDocuments(flds);
		callDocumentsPage();
	}
	
	private void callDocumentsPage() throws Exception {
		try {
			
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return;
			}
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());
			DocWayTitlesDocumentiFascicolo bean = new DocWayTitlesDocumentiFascicolo();
			bean.getFormsAdapter().fillFormsFromResponse(response);
			bean.init(response.getDocument());
			bean.setVisible(true);	
			bean.setFolderId(currentFolderIdForDocList);
			bean.setTitle(folderTreeRoot.getTitle());
			
			// manage paging
			phnd = new PagingHandler((Element)response.selectSingleNode("//attachTree"));
			
			setSessionAttribute("docWayTitlesDocumentiFascicolo", bean); // add eventually bean to session
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
	}
	
	public String queryFasc() throws Exception{
		try{
			getFormsAdapter().queryFasc(folderId);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (response.getAttributeValue("/response/@verbo").equals("showdoc")){
				return buildSpecificShowdocPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response);
			}
			else{
				this.init(response.getDocument());
				return "showtitles@fascicolo";
			}
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	
	public void exportFolderTree()  throws Exception {
		exportFolderTree(false);
	}
	
	public void exportFolderTree(boolean applyFilter)  throws Exception {
		
		try{
			
			List<String> flds = new ArrayList<>();
			
			
			
			folderTreeRoot.getSelectedFolders(flds);
			
			if(flds.size() == 0) {
				return;
			}
			
			getFormsAdapter().exportFolderTree(flds, applyFilter);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			}
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, false, "exportFolderTree");
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
		
		
		
	}
	
	/**
	 * Apertura del popup modale di caricamento loadinbar per azione massiva su lista titoli
	 * @param response
	 * @param setHoderPage
	 * @param currentAction
	 * @throws Exception
	 */
	private void openLoadingbarModal(XMLDocumento response, boolean setHoderPage, String currentAction) throws Exception {
		DocWayLoadingbarFolderExport docWayLoadingbar = new DocWayLoadingbarFolderExport();
		docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
		docWayLoadingbar.init(response);
		if (setHoderPage)
			docWayLoadingbar.setHolderPageBean(this);
		setLoadingbar(docWayLoadingbar);
		docWayLoadingbar.setActive(true);
		if (currentAction != null && !currentAction.isEmpty())
			this.action = currentAction;
	}

	public String getAction() {
		return action;
	}
	
}
