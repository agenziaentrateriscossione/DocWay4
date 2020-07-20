package it.tredi.dw4.docwayproc.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.LoadingbarFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.beans.Loadingbar;
import it.tredi.dw4.docway.adapters.DocWayLoadingbarFormsAdapter;
import it.tredi.dw4.docway.model.Resoconto;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class DocwayprocLoadingbar extends Loadingbar {
	private DocWayLoadingbarFormsAdapter formsAdapter;
	
	private XMLDocumento document;
	
	private Resoconto resoconto = new Resoconto();
	private String customData0 = "";
	private String customData1 = "";
	private String customData2 = "";
	private String customData3 = "";
	private String customData4 = "";
	private String customData5 = "";
	
	public DocwayprocLoadingbar() throws Exception {
		this.formsAdapter = new DocWayLoadingbarFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public void init(XMLDocumento response) {
		this.document = response;
		this.resoconto.init(XMLUtil.createDocument(this.document.getDocument(), "/response/resoconto"));
		this.customData0 = XMLUtil.parseElementNode(this.document.getDocument(), "/response/customData0");
		this.customData1 = XMLUtil.parseElementNode(this.document.getDocument(), "/response/customData1");
		this.customData2 = XMLUtil.parseElementNode(this.document.getDocument(), "/response/customData2");
		this.customData3 = XMLUtil.parseElementNode(this.document.getDocument(), "/response/customData3");
		this.customData4 = XMLUtil.parseElementNode(this.document.getDocument(), "/response/customData4");
		this.customData5 = XMLUtil.parseElementNode(this.document.getDocument(), "/response/customData");
	}	
	
	public LoadingbarFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}	
	
	public String refresh() throws Exception {
		try {
			XMLDocumento response = super._refresh();
			document = response;
			formsAdapter.fillFormsFromResponse(response);
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("showdoc")) {
				setActive(false);
				
				Logger.info(response.asXML());
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
	
	public String queryPage() throws Exception {
		try {
			formsAdapter.queryPage();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			setActive(false);
			DocwayprocHome docwayprocHome = new DocwayprocHome();
			docwayprocHome.getFormsAdapter().fillFormsFromResponse(response);
			docwayprocHome.init(response.getDocument());
			setSessionAttribute("docwayprocHome", docwayprocHome);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return "show@docwayproc_home";		
	}
	
	public String close() {
		setActive(false);
		return null;
	}
	
	public int getPercentage() throws Exception {
		if (document == null)
			return 0;
		else {
			int total = Integer.parseInt(document.getElementText("//nStat", "0").trim());
			int current = Integer.parseInt(document.getElementText("//nDoc", "0").trim());
			if (current <1 || total < 1) return 0;
			return (current * 100) / total; 
		}
	}
	
	public boolean isCompleted() throws Exception {
		if ( error ) return true;
		else 		 return getStopDate().length() > 0;
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
	
	public String getDbTable() {
		if (document == null)
			return "";
		else {
			return XMLUtil.parseStrictAttribute(document.getDocument(), "/response/@dbTable");
		}
	}
	
	public boolean isExportLoadingBar() {
		if (getExportFilePathFile().equals(""))
			return false;
		else
			return true;
	}
	
	public String getExportFilePathFile() {
		if (document == null)
			return "";
		else
			return document.getAttributeValue("//exportfile/@pathFile", "");
	}
	
	public String getExportFilePathAttachments() {
		if (document == null)
			return "";
		else
			return document.getAttributeValue("//exportfile/@pathAttachments", "");
	}
	
	/**
	 * Downloa del file prodotto dal processo associato alla loadingbar 
	 * @param event
	 */
	public void downloadFile(ActionEvent event) throws Exception {
		try {
			String filename = getExportFilePathFile();
			getFormsAdapter().downloadFile(filename);
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
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
	}
	
	/**
	 * Generazione del report prodotto dal processo associato alla loadingbar (download)
	 * @throws Exception
	 */
	public void generaReport(ActionEvent event) throws Exception {
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
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
	}
	
	/**
	 * Caricamento della pagina di destinazione post processo legato 
	 * a loadingbar (es. notifiche differite)
	 * 
	 * @throws Exception
	 */
	public String loadDestPage() throws Exception {
		this.active = false; // chiusura del popup di loadingbar
		try {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// caricamento della pagina post elaborazione loadingbar (es. notifiche differite: query@notif_diff.html)
			// TODO e' corretto il modo di identificare il template di ritorno? 
			String verbo = response.getAttributeValue("/response/@verbo");
			String dbTable = response.getAttributeValue("/response/@dbTable");
			if (verbo.equals("showdoc"))
				return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response, false);
			else
				return buildSpecificQueryPageAndReturnNavigationRule(dbTable, response);
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public void setResoconto(Resoconto resoconto) {
		this.resoconto = resoconto;
	}

	public Resoconto getResoconto() {
		return resoconto;
	}
	
	public String getCustomData0() {
		return customData0;
	}

	public void setCustomData0(String customData0) {
		this.customData0 = customData0;
	}

	public String getCustomData1() {
		return customData1;
	}

	public void setCustomData1(String customData1) {
		this.customData1 = customData1;
	}

	public String getCustomData2() {
		return customData2;
	}

	public void setCustomData2(String customData2) {
		this.customData2 = customData2;
	}

	public String getCustomData3() {
		return customData3;
	}

	public void setCustomData3(String customData3) {
		this.customData3 = customData3;
	}

	public String getCustomData4() {
		return customData4;
	}

	public void setCustomData4(String customData4) {
		this.customData4 = customData4;
	}

	public String getCustomData5() {
		return customData5;
	}

	public void setCustomData5(String customData5) {
		this.customData5 = customData5;
	}
	
	public String getXml(){
		if (document==null) 
			return "";
		else				
			return document.asXML();
	}
	
	public void setXml(String xml){}
	
}
