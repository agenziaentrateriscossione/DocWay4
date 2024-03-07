package it.tredi.dw4.docwayproc.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocumentFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docwayproc.model.workflow.WorkflowEntity;
import it.tredi.dw4.utils.DocWayProperties;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class ShowdocWorkflow extends Showdoc {
	
	private DocDocWayDocumentFormsAdapter formsAdapter;
	private String xml = "";
	
	private WorkflowEntity workflow = new WorkflowEntity();
	
	private boolean confirmDeleteActive = false;
	
	private String urlPaginaWikiIstruzioniBonita = "";
	
	public ShowdocWorkflow() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
		this.urlPaginaWikiIstruzioniBonita = DocWayProperties.readProperty("urlPaginaWikiIstruzioniBonita", "");
	}

	@Override
	public void init(Document dom) {
		this.xml = dom.asXML();
		this.workflow.init(dom);
		
		setCurrentPage(getFormsAdapter().getCurrent()+"");
		confirmDeleteActive = false;
		
		formsAdapter.getDefaultForm().addParam("dbTable", "@workflow"); // forzo dbTable a workflow
	}

	@Override
	public DocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public WorkflowEntity getWorkflow() {
		return workflow;
	}

	public void setWorkflow(WorkflowEntity workflow) {
		this.workflow = workflow;
	}
	
	public boolean isConfirmDeleteActive() {
		return confirmDeleteActive;
	}

	public void setConfirmDeleteActive(boolean confirmDeleteActive) {
		this.confirmDeleteActive = confirmDeleteActive;
	}
	
	public String getUrlPaginaWikiIstruzioniBonita() {
		return urlPaginaWikiIstruzioniBonita;
	}

	@Override
	public String paginaTitoli() throws Exception {
		try {
			XMLDocumento response = this._paginaTitoli();	
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			DocwayprocTitles docwayprocTitles = new DocwayprocTitles();		
			docwayprocTitles.getFormsAdapter().fillFormsFromResponse(response);
			docwayprocTitles.init(response.getDocument());
			docwayprocTitles.setPopupPage(isPopupPage());
			setSessionAttribute("docwayprocTitles", docwayprocTitles);
			return "docwayproc@showtitles@workflow";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docwayproc/showdoc@workflow");
	}

	@Override
	public String modifyTableDoc() throws Exception {
		this.formsAdapter.modifyTableDoc();
		
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
	}
	
	/**
	 * aggiunta di una nuova versione di workflow (nuovo bar di Bonita)
	 * @return
	 * @throws Exception
	 */
	public String nuovaVersioneWorkflow() throws Exception {
		try {
			this.formsAdapter.modifyTableDoc();
			
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
			
			// forzo il redirect sulla maschera di upload della nuova versione
			// di workflow (upload file .bar Bonita)
			return "docwayproc@docEdit@modify@workflow@version";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * disabilitazione dell'istanza del workflow di Bonita (versione corrente del workflow)
	 * @return
	 * @throws Exception
	 */
	public String disabilitaWorkflow() throws Exception {
		try {
			formsAdapter.getDefaultForm().addParam("verbo", "workflow_response");
			formsAdapter.getDefaultForm().addParam("xverb", "@disable");
			
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
						
			_reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * abilitazione dell'istanza del workflow di Bonita (versione corrente del workflow)
	 * @return
	 * @throws Exception
	 */
	public String abilitaWorkflow() throws Exception {
		try {
			formsAdapter.getDefaultForm().addParam("verbo", "workflow_response");
			formsAdapter.getDefaultForm().addParam("xverb", "@enable");
			
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
						
			_reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * cancellazione di un workflow (precedentemente disabilitato)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String rimuoviDoc() throws Exception{
		this.formsAdapter.remove();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		String verbo = response.getAttributeValue("/response/@verbo");
		String dbTable = response.getAttributeValue("/response/@dbTable");
		
		if ("@reload".equals(dbTable)) {
			getFormsAdapter().fillFormsFromResponse(response);
			_reload();
			return null;
		}
		else if ("query".equals(verbo)) {
			return "show@docwayproc_home";
		}
		else {
			return buildSpecificShowdocPageAndReturnNavigationRule("@workflow", response);
		}
	}
	
	/**
	 * reload forzato della pagina (con passaggio di dbTable)
	 * conversione da '@bwf_entity' a '@workflow'
	 * @throws Exception
	 */
	@Override
	public void _reload() throws Exception {
		XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
		
		String navigationRule = buildSpecificShowdocPageAndReturnNavigationRule("@workflow", response);
		redirectToJsf(navigationRule, response);
	}
	
	/**
	 * apertura del popup di conferma cancellazione del flusso
	 * @return
	 */
	public String openConfirmDelete() {
		confirmDeleteActive = true;
		return null;
	}
	
	/**
	 * chiusura del popup di conferma cancellazione del flusso
	 * @return
	 */
	public String closeConfirmDelete() {
		confirmDeleteActive = false;
		return null;
	}
	
}
