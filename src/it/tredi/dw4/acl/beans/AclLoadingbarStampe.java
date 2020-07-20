package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclLoadingbarFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.LoadingbarFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.beans.LoadingbarStampe;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class AclLoadingbarStampe extends LoadingbarStampe {
	private AclLoadingbarFormsAdapter formsAdapter;

	private XMLDocumento document;
	
	public AclLoadingbarStampe() throws Exception {
		this.formsAdapter = new AclLoadingbarFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}

	public void init(XMLDocumento response) {
		this.document = response;
	}	
	
	public LoadingbarFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}	
	
	public String refresh() throws Exception {
		try {
			XMLDocumento response = super._refresh();
			formsAdapter.fillFormsFromResponse(response);
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			init(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return null;
	}
	
	public String close() {
		setActive(false);
		return null;
	}
	
	public int getPercentage() throws Exception {
		if (document == null)
			return 0;
		else {
			int total = Integer.parseInt(document.getElementText("//nStat", "0").trim()) - 1;
			int current = Integer.parseInt(document.getElementText("//nDoc", "0").trim());
			return (current * 100) / total; 
		}
	}
	
	public boolean isCompleted() throws Exception {
		return getStopDate().length() > 0;
	}
	
	public String getStartDate() {
		if (document == null)
			return "";
		else {
			String s = document.getElementText("//startDate", "").trim();
			if (s.length() == 0)
				return "";
			else {
				return s;
			}
		}
	}
	
	public String getStopDate() {
		if (document == null)
			return "";
		else {
			String s = document.getElementText("//stopDate", "").trim();
			if (s.length() == 0)
				return "";
			else {
				return s;
			}
		}
	}
	
	public String getTitle() {
		if (document == null)
			return "";
		else {
			return document.getElementText("//title", "").trim();
		}
	}	
	
	public String getStatus() {
		if (document == null)
			return "";
		else {
			return document.getElementText("//status", "").trim();
		}
	}
	
	public String getExceptions() {
		if (document == null)
			return "";
		else {
			return document.getElementText("//exceptions", "").trim();
		}
	}	
	
	public String getProgress() {
		if (document == null)
			return "";
		else {
			return document.getElementText("//progress", "").trim();
		}
	}
	
	/**
	 * Downloa del file prodotto dal processo associato alla loadingbar 
	 * @param event
	 */
	public void downloadReport(ActionEvent event) throws Exception {
		try {
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				response.setContentType(new MimetypesFileTypeMap().getContentType(attachFile.getFilename()));
				response.setContentLength(attachFile.getContent().length);
				response.setHeader("Content-Disposition", "attachment; filename=" + attachFile.getFilename());
				ServletOutputStream out;
				out = response.getOutputStream();
				out.write(attachFile.getContent());
				faces.responseComplete();
			}
			else {
				// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
				handleErrorResponse(attachFile.getXmlDocumento());
			}
			
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
	}
	
}
