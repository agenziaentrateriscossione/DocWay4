package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.adapters.FormsAdapter;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class DocWayWebmailFormsAdapter extends FormsAdapter  {
	protected FormAdapter defaultForm;
	
	public DocWayWebmailFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}

	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		
		fillDefaultFormFromResponse(response);
	}
	
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		
		Element root = response.getRootElement();
		
		defaultForm.addParam("verbo", root.attributeValue("verbo", ""));
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		
		defaultForm.addParam("currentmailbox", root.attributeValue("currentmailbox", ""));
		defaultForm.addParam("currentfolder", root.attributeValue("currentfolder", ""));
		defaultForm.addParam("currentprotocol", root.attributeValue("currentprotocol", ""));
		defaultForm.addParam("messagestorage", "");
	}
	
	/**
	 * Filtro email su casella di posta
	 */
	public void search(String colSort, boolean ascSort) {
		defaultForm.addParam("xverb", "@search");
		defaultForm.addParam("sortfield", colSort);
		defaultForm.addParam("sortasc", ascSort);
	}
	
	/**
	 * Caricamento di una nuova mailbox
	 * @param newmailbox
	 */
	public void loadMailbox(String newmailbox) {
		defaultForm.addParam("newmailbox", newmailbox);
		defaultForm.addParam("xverb", "@loadmailbox");
	}
	
	/**
	 * Logout casella di posta
	 */
	public void logout() {
		defaultForm.addParam("xverb", "@logout");
	}
	
	/**
	 * Caricamento dell'anteprima di un messaggio email
	 * @param messageNo
	 */
	public void loadMessage(String messageNo) {
		defaultForm.addParam("xverb", "@loadmessage");
		defaultForm.addParam("messageNo", messageNo);
	}
	
	/**
	 * Trasformazione di un messaggio email in documento di docway (in base al documentmodel selezionato)
	 * @param documentmodel
	 * @param messageNo
	 */
	public void storeMessage(String documentmodel, String messageNo) {
		defaultForm.addParam("xverb", "@storemessage");
		defaultForm.addParam("documentModel", documentmodel);
		defaultForm.addParam("messageNo", messageNo);
	}
	
	/**
	 * Caricamento dell'albero delle directory della casella di posta
	 */
	public void loadFoldersTree() {
		defaultForm.addParam("xverb", "@folderstree");
	}
	
	/**
	 * Selezione di una cartella dall'albero delle cartelle
	 */
	public void loadFolder(String foldername) {
		defaultForm.addParam("xverb", "@loadfolder");
		defaultForm.addParam("folderName", foldername);
	}
	
	/**
	 * Downaload di un allegato
	 */
	public void downloadAttach(String messageNo, String attachmentNo) {
		defaultForm.addParam("xverb", "@downloadAttachment");
		defaultForm.addParam("messageNo", messageNo);
		defaultForm.addParam("attachmentNo", attachmentNo);
	}
}
