package it.tredi.dw4.docwayproc.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;

public class Menu extends Page {
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	public Menu() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}
	
	public String getAclDb() {
		return formsAdapter.getDefaultForm().getParam("aclDb");
	}

	/**
	 * caricamento pagina di ricerca delle voci di indice
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQIndiceTitolario() throws Exception {
		try {
			formsAdapter.gotoTableQ("indice_titolario", false);
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			DocwayprocHome docwayprocHome = new DocwayprocHome();
			docwayprocHome.getFormsAdapter().fillFormsFromResponse(response);
			docwayprocHome.init(response.getDocument());
			setSessionAttribute("docwayprocHome", docwayprocHome);
			
			return "show@docwayproc_home";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;	
		}
	}
	
	/**
	 * caricamento pagina di inserimento voce di indice
	 * @return
	 * @throws Exception
	 */
	public String insTableDocIndiceTitolario() throws Exception {
		try {
			getFormsAdapter().insTableDoc("indice_titolario"); 
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response);
			
			DocEditIndiceTitolario docEditIndiceTitolario = new DocEditIndiceTitolario();
			docEditIndiceTitolario.getFormsAdapter().fillFormsFromResponse(response);
			docEditIndiceTitolario.init(response.getDocument());
			setSessionAttribute("docEditIndiceTitolario", docEditIndiceTitolario);
			
			return "docwayproc@docEdit@indice_titolario";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;	
		}
	}
	
	/**
	 * apertura in edit del titolario di classificazione
	 * @return
	 * @throws Exception
	 */
	public String openTitolario() throws Exception {
		try {
			getFormsAdapter().openEditTitolario(); 
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response);
			
			ThEditTitolarioClassificazione thEditTitolarioClassificazione = new ThEditTitolarioClassificazione();
			thEditTitolarioClassificazione.getFormsAdapter().fillFormsFromResponse(response);
			thEditTitolarioClassificazione.init(response.getDocument());
			setSessionAttribute("thEditTitolarioClassificazione", thEditTitolarioClassificazione);
			
			return "docwayproc@thEditTitolarioClassificazione";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;	
		}
	}
	
	/**
	 * caricamento pagina di ricerca dei workflow
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQWorkflow() throws Exception {
		try {
			formsAdapter.gotoTableQ("workflow", false);
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			DocwayprocQueryWorkflow docwayprocQueryWorkflow = new DocwayprocQueryWorkflow();
			docwayprocQueryWorkflow.getFormsAdapter().fillFormsFromResponse(response);
			docwayprocQueryWorkflow.init(response.getDocument());
			setSessionAttribute("docwayprocQueryWorkflow", docwayprocQueryWorkflow);
			
			return "docwayproc@query@workflow";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;	
		}
	}
	
	
	/**
	 * caricamento pagina di inserimento di un workflow
	 * @return
	 * @throws Exception
	 */
	public String insTableDocWorkflow() throws Exception {
		try {
			getFormsAdapter().insTableDoc("workflow"); 
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response);
			
			DocEditWorkflow docEditWorkflow = new DocEditWorkflow();
			docEditWorkflow.getFormsAdapter().fillFormsFromResponse(response);
			docEditWorkflow.init(response.getDocument());
			setSessionAttribute("docEditWorkflow", docEditWorkflow);
			
			return "docwayproc@docEdit@workflow";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;	
		}
	}
	
}
