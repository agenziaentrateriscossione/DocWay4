package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclLoadingbarFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.LoadingbarFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.beans.Loadingbar;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class AclLoadingbar extends Loadingbar {
	private AclLoadingbarFormsAdapter formsAdapter;

	private XMLDocumento document;
	
	private boolean buildrels = false; // true se identifica una loadingbar di rigenerazione della gerarchia di acl, false altrimenti
	
	public AclLoadingbar() throws Exception {
		this.formsAdapter = new AclLoadingbarFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}

	public void init(XMLDocumento response) {
		this.document = response;
		
		if (document != null && document.getAttributeValue("/response/@xverb", "").equals("@buildFascRels"))
			this.buildrels = true;
		else
			this.buildrels = false;
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
			
			//Aggiornamento delle voci di indice da modifica di persona interna. Se la response è una showdoc -> caricamento della persona interne
			//Aggiornamento delle persone interne da modifica di profilo. Se la response è una showdoc -> caricamento del profilo
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("showdoc")) {
				setActive(false);
				if (response.isXPathFound("/response/persona_interna[@tipo='profilo']")) {
					ShowdocProfilo showdocProfilo = new ShowdocProfilo();
					showdocProfilo.getFormsAdapter().fillFormsFromResponse(response);
					showdocProfilo.init(response.getDocument());
					setSessionAttribute("showdocProfilo", showdocProfilo);		
					return "showdoc@profilo@reload";
				}
				else {
					ShowdocPersonaInterna showdocPersonaInterna = new ShowdocPersonaInterna();
					showdocPersonaInterna.getFormsAdapter().fillFormsFromResponse(response);
					showdocPersonaInterna.init(response.getDocument());
					setSessionAttribute("showdocPersonaInterna", showdocPersonaInterna);					
					return "showdoc@persona_interna@reload";					
				}
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
			AclHome aclHome = new AclHome();
			aclHome.getFormsAdapter().fillFormsFromResponse(response);
			aclHome.init(response.getDocument());
			setSessionAttribute("aclHome", aclHome);	
			
			if (buildrels) {
				formsAdapter.reloadAclHierBrowser();
				response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (!handleErrorResponse(response)) {
					// se la chiamata non ha restituito errore aggiorno il bean hierBrowser
					AclHierBrowser hierBrowser = new AclHierBrowser();
					// riempi il formsAdapter della pagina di destinazione
					hierBrowser.getFormsAdapter().fillFormsFromResponse(response);
					hierBrowser.init(response.getDocument());

					setSessionAttribute("hierBrowser", hierBrowser);
				}
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return "show@acl_home";		
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
	
	public boolean isBuildrels() {
		return buildrels;
	}

	public void setBuildrels(boolean buildrels) {
		this.buildrels = buildrels;
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
	
}
