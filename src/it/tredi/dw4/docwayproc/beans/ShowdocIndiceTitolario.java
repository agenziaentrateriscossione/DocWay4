package it.tredi.dw4.docwayproc.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docwayproc.model.Indice_Titolario;
import it.tredi.dw4.docwayproc.model.workflow.WorkflowsTitolario;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class ShowdocIndiceTitolario extends Showdoc {
	private DocDocWayDocumentFormsAdapter formsAdapter;
	private String xml = "";
	
	private Indice_Titolario indice_titolario = new Indice_Titolario();
	private WorkflowsTitolario workflowTitolario = new WorkflowsTitolario();
	
	public ShowdocIndiceTitolario() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		this.xml = dom.asXML();
		this.indice_titolario.init(dom);
		this.workflowTitolario.init(dom);
		
		setCurrentPage(getFormsAdapter().getCurrent()+"");
	}

	@Override
	public DocDocWayDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public Indice_Titolario getIndice_titolario() {
		return indice_titolario;
	}

	public void setIndice_titolario(Indice_Titolario indice_titolario) {
		this.indice_titolario = indice_titolario;
	}

	public WorkflowsTitolario getWorkflowTitolario() {
		return workflowTitolario;
	}

	public void setWorkflowTitolario(WorkflowsTitolario workflowTitolario) {
		this.workflowTitolario = workflowTitolario;
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
			return "docwayproc@showtitles";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docwayproc/showdoc@indice_titolario");
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
	 * cancellazione di un documento
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
			super._reload();
			return null;
		}
		else if ("query".equals(verbo))
			return "show@docwayproc_home";
		else
			return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
	}

}
