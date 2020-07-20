package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.adapters.DocWayWebmailFormsAdapter;
import it.tredi.dw4.docway.model.webmail.Attach;
import it.tredi.dw4.docway.model.webmail.Message;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;

public class DocWayWebmailPreview extends Page {

	private DocWayWebmailFormsAdapter formsAdapter;
	private String xml = "";
	
	private Message message = new Message();
	private List<String> documentModels = new ArrayList<String>();
	private boolean showemail = false;
	
	private boolean messageStored = false;
	
	public DocWayWebmailPreview() throws Exception {
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
	
	public boolean isShowemail() {
		return showemail;
	}

	public void setShowemail(boolean showemail) {
		this.showemail = showemail;
	}
	
	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
	public List<String> getDocumentModels() {
		return documentModels;
	}

	public void setDocumentModels(List<String> documentModels) {
		this.documentModels = documentModels;
	}
	
	public boolean isMessageStored() {
		return messageStored;
	}

	public void setMessageStored(boolean messageStored) {
		this.messageStored = messageStored;
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
    	xml 			= dom.asXML();
    	
    	message.init(XMLUtil.createDocument(dom, "/response/message"));
    	
    	List<Element> dmodels = dom.selectNodes("/response/documentmodels/documentmodel");
    	if (dmodels != null && dmodels.size() > 0) {
    		for (int i=0; i<dmodels.size(); i++) {
    			Element dmodel = (Element) dmodels.get(i);
    			if (dmodel != null && dmodel.attributeValue("name") != null)
    				documentModels.add(dmodel.attributeValue("name"));
    		}
    	}
    	
    	messageStored 	= false;
    }
	
	/**
	 * Trasformazione di una email in documento di docway in base al document 
	 * model selezionato
	 * 
	 * @return
	 * @throws Exception
	 */
	public String storeMessage() throws Exception {
		try {
			String documentmodel = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("documentmodel");
			if (documentmodel == null || documentmodel.equals(""))
				return null; // questo caso non si dovrebbe mai verificare
			
			formsAdapter.storeMessage(documentmodel, message.getMessageNo());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			messageStored = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(response.getDocument(), "/response/@messagestored", "false"));
			if (messageStored) {
				reset();
				
				DocWayWebmail docwayWebmail = (DocWayWebmail) getSessionAttribute("docwayWebmail");
				if (docwayWebmail != null)
					return docwayWebmail.search();
			}
			
			return null;
			
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		return null;
	}
		
	public String downloadAttachment() throws Exception {
		Attach attachment = (Attach) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("attach");
		
		formsAdapter.downloadAttach(this.message.getMessageNo(), attachment.getId());
		AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
		
		if (attachFile.getContent() != null) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			formsAdapter.getDefaultForm().addParam("toDo", ""); // azzeramento del parametro 'toDo'
			
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			response.setContentType(new MimetypesFileTypeMap().getContentType(attachment.getName()));
			response.setContentLength(attachFile.getContent().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getName() + "\"");
			ServletOutputStream out;
			out = response.getOutputStream();
			out.write(attachFile.getContent());
			faces.responseComplete();
		}
		else {
			// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
			handleErrorResponse(attachFile.getXmlDocumento());
		}
		
		
//		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
//		if (handleErrorResponse(response)) {
//			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
//			return null;
//		}
		
		return null;
	}
	
	/**
	 * Chiusura dell'anteprima del messaggio email
	 * @return
	 * @throws Exception
	 */
	public String closeMessage() throws Exception {
		reset();
		return null;
	}
	
	/**
	 * Azzeramento dei dati di anteprima della mail
	 */
	private void reset() {
		message = new Message();
		showemail = false;
	}
	
}
