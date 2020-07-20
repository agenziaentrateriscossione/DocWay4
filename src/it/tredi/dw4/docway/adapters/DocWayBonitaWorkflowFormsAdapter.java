package it.tredi.dw4.docway.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.adapters.FormsAdapter;

import org.dom4j.DocumentException;
import org.dom4j.Element;

public class DocWayBonitaWorkflowFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	
	public DocWayBonitaWorkflowFormsAdapter(AdapterConfig config) {
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
		
		defaultForm.addParam("physDoc", root.attributeValue("physDoc", ""));
		
		defaultForm.addParam("bwfId", root.attributeValue("bwfId", ""));
		defaultForm.addParam("bwfVersion", root.attributeValue("bwfVersion", ""));
		defaultForm.addParam("bwfInstanceId", root.attributeValue("bwfInstanceId", ""));
		defaultForm.addParam("bwfTaskId", root.attributeValue("bwfTaskId", ""));
		defaultForm.addParam("bwfTaskName", root.attributeValue("bwfTaskName", ""));
	}

	/**
	 * Esecuzione di un task su una istanza di workflow (registrazione task sul documento)
	 * 
	 * @param physDoc
	 * @param bwfInstanceId
	 * @param bwfTaskId
	 * @param bwfTaskName
	 */
    public void executeTask(String physDoc, String bwfInstanceId, String bwfTaskId, String bwfTaskName) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@executeTask");
		
		defaultForm.addParam("selid", "");
		defaultForm.addParam("physDoc", physDoc);
		
		defaultForm.addParam("bwfInstanceId", bwfInstanceId);
		defaultForm.addParam("bwfTaskId", bwfTaskId);
		defaultForm.addParam("bwfTaskName", bwfTaskName);
    }
    
    /**
     * Elenco dei workflow disponibili
     * 
     * @param workflowName filtro su nome del workflow
     */
    public void listWorkflowDefinitions(String workflowName) {
    	defaultForm.addParam("query", workflowName);
    	
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@searchWorkFlowDefinition");
    }
    
    /**
     * Assegnazione di un workflow ad un documento
     * 
     * @param bwfId
     * @param bwfVersion
     */
    public void doAssignWorkflow(String bwfId, String bwfVersion, String nrecord) {
    	defaultForm.addParam("verbo", "bonitaworkflow");
		defaultForm.addParam("xverb", "@assignworkflow@" + nrecord);
		defaultForm.addParam("selid", "");
		defaultForm.addParam("bwfId", bwfId);
		defaultForm.addParam("bwfVersion", bwfVersion);
    }
    
}
