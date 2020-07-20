package it.tredi.dw4.docwayproc.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayDocedit;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docwayproc.model.workflow.WorkflowEntity;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.UploadFileUtil;

import java.io.File;
import java.util.HashMap;

import org.dom4j.Document;

public class DocEditWorkflow extends DocWayDocedit {

	protected DocDocWayDocEditFormsAdapter formsAdapter;
	protected String xml = "";
	
	private WorkflowEntity workflow = new WorkflowEntity();
	
	public DocEditWorkflow() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		workflow.init(dom);
	}
	
	@Override
	public DocEditFormsAdapter getFormsAdapter() {
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
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			UserBean userBean = getUserBean();
			try {
				HashMap<String, File> files = new HashMap<String, File>();
				files.put("barfile", UploadFileUtil.getUserTempFile(workflow.getFileNameBar(), userBean.getLogin(), userBean.getMatricola()));
				if (workflow.getFileNameImage() != null && !workflow.getFileNameImage().equals(""))
					files.put("imagefile", UploadFileUtil.getUserTempFile(workflow.getFileNameImage(), userBean.getLogin(), userBean.getMatricola()));
				
				formsAdapter.getDefaultForm().addParams(workflow.asFormAdapterParams(""));
				formsAdapter.saveDocument("bwf_entity", "list_of_bwf_entity");
				XMLDocumento response = getFormsAdapter().getDefaultForm().executeUploadRequestToXMLDocumento(userBean, files);
				if (handleErrorResponse(response)) {
					workflow.setFileNameBar("");
					workflow.setFileNameImage("");
					
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				ShowdocWorkflow showdocWorkflow = new ShowdocWorkflow();
				showdocWorkflow.getFormsAdapter().fillFormsFromResponse(response);
				showdocWorkflow.init(response.getDocument());
				setSessionAttribute("showdocWorkflow", showdocWorkflow);
				
				return "docwayproc@showdoc@workflow@reload";
			}
			finally {
				UploadFileUtil.deleteTempUserFolder(userBean.getLogin(), userBean.getMatricola());
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;		
			}
			
			getFormsAdapter().fillFormsFromResponse(response);
			this.init(response.getDocument());
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = false;
		
		// campo file bar
		if (getWorkflow().getFileNameBar() == null || getWorkflow().getFileNameBar().length() == 0) {
			this.setErrorMessage("", I18N.mrs("dw4.occorre_selezionare_il_file_bar_da_importare"));
			result = true;
		}
		
		return result;
	}
	
}
