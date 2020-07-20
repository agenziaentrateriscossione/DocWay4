package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.adapters.DocWayWebmailFormsAdapter;
import it.tredi.dw4.docway.model.webmail.Folder;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class DocWayWebmailFolders extends Page {

	private DocWayWebmailFormsAdapter formsAdapter;
	private String xml = "";
	
	private List<Folder> folders = new ArrayList<Folder>();
	private boolean showfolders = false;
	
	public DocWayWebmailFolders() throws Exception {
		this.formsAdapter = new DocWayWebmailFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public DocWayWebmailFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}
	
	public boolean isShowfolders() {
		return showfolders;
	}

	public void setShowfolders(boolean showfolders) {
		this.showfolders = showfolders;
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
    	xml 			= dom.asXML();
    	folders 		= XMLUtil.parseSetOfElement(dom, "/response/tree//folder", new Folder());
    }
	
	/**
	 * Chiusura del popup di visualizzazione delle cartelle della casella
	 * di posta
	 * @return
	 * @throws Exception
	 */
	public String closeFolders() throws Exception {
		reset();
		return null;
	}
	
	/**
	 * Azzeramento dei dati delle cartelle
	 */
	private void reset() {
		folders 		= new ArrayList<Folder>();
		showfolders 	= false;
	}
	
	/**
	 * Caricamento di un messaggio email
	 * @return
	 * @throws Exception
	 */
	public String loadFolder() throws Exception {
		Folder folder = (Folder) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("folder");
		return loadFolder(folder);
	}
	
	/**
	 * Selezione di una cartella dall'albero delle cartelle
	 * @return
	 * @throws Exception
	 */
	public String loadFolder(Folder folder) throws Exception {
		try {
			formsAdapter.loadFolder(folder.getFullname());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			reset(); // chiusura del popup di selezione delle cartelle
			
			// aggiornamento della pagina di webmail
			DocWayWebmail docwayWebmail = new DocWayWebmail();
			docwayWebmail.getFormsAdapter().fillFormsFromResponse(response);
			docwayWebmail.init(response.getDocument());
			docwayWebmail.setShowSxCol(false);
			setSessionAttribute("docwayWebmail", docwayWebmail);
			
			return "webmail";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
}
