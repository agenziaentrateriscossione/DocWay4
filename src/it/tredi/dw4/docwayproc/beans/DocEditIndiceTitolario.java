package it.tredi.dw4.docwayproc.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docwayproc.model.Indice_Titolario;
import it.tredi.dw4.docwayproc.model.Oggetto;
import it.tredi.dw4.docwayproc.model.workflow.WorkflowsTitolario;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLDocumento;

public class DocEditIndiceTitolario extends EditVoceIndice  {
	private Indice_Titolario indice_titolario = new Indice_Titolario();
	private WorkflowsTitolario workflowTitolario = new WorkflowsTitolario();

	public DocEditIndiceTitolario() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		this.indice_titolario = new Indice_Titolario();
		this.indice_titolario.init(dom);
		
		// inizializzazione ai valori di default dei campi 
		// della voce di indice (caso di ripeti nuovo)
		this.indice_titolario.setNrecord(".");
		
		this.workflowTitolario = new WorkflowsTitolario();
		this.workflowTitolario.init(dom);
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

	public Indice_Titolario getIndice_titolario() {
		return indice_titolario;
	}

	public void setIndice_titolario(Indice_Titolario indice_titolario) {
		this.indice_titolario = indice_titolario;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			formsAdapter.getDefaultForm().addParams(getIndice_titolario().asFormAdapterParams(""));
			formsAdapter.getDefaultForm().addParams(getWorkflowTitolario().asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("indice_titolario", "list_of_proc");
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocIndiceTitolario showdocIndiceTitolario = new ShowdocIndiceTitolario();
			showdocIndiceTitolario.getFormsAdapter().fillFormsFromResponse(response);
			showdocIndiceTitolario.init(response.getDocument());
			setSessionAttribute("showdocIndiceTitolario", showdocIndiceTitolario);
			
			return "docwayproc@showdoc@indice_titolario@reload";
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
		
		// campo voce di indice obbligatorio
		if (getIndice_titolario().getVoce() == null || getIndice_titolario().getVoce().length() == 0) {
			this.setErrorMessage("templateForm:voce", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.index") + "'");
			result = true;
		}
		
		// campo classificazione obbligatorio
		if (getIndice_titolario().getCompilazione_automatica().getClassif() == null 
				|| getIndice_titolario().getCompilazione_automatica().getClassif().getCod() == null 
				|| "".equals(getIndice_titolario().getCompilazione_automatica().getClassif().getCod().trim())) {
			this.setErrorMessage("templateForm:classif_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.classif") + "'");
			result = true;
		}
		
		// almeno un tipo doc deve essere selezionato in validita'
		boolean validitaSelected = false;
		for (int i=0; i<getIndice_titolario().getArrValidita().length; i++) {
			if (getIndice_titolario().getArrValidita()[i] != null && !getIndice_titolario().getArrValidita()[i].equals(""))
				validitaSelected = true;
		}
		if (!validitaSelected) {
			this.setErrorMessage("", I18N.mrs("dw4.occorre_assegnare_la_validita_del_tipo_di_documento"));
			result = true;
		}
		
		// controllo su eventuali caratteri non consentiti all'interno degli oggetti di default
		if (getIndice_titolario().getCompilazione_automatica().getOggetto().size() > 0) {
			for (int i=0; i<getIndice_titolario().getCompilazione_automatica().getOggetto().size(); i++) {
				Oggetto oggetto = getIndice_titolario().getCompilazione_automatica().getOggetto().get(i);
				if (oggetto != null && oggetto.getText() != null && !oggetto.getText().equals("")) {
					if (oggetto.getText().contains("|")) { // TODO gestire i caratteri non consentiti da file di properties
						this.setErrorMessage("templateForm:docEditOggetti:" + i + ":oggetto_text", I18N.mrs("dw4.il_campo_oggetto_contiene_un_carattere_non_valido") + ": | ");
						result = true;
					}
				}
			}
		}
		
		return result;
	}

	@Override
	public WorkflowsTitolario getWorkflowTitolario() {
		return this.workflowTitolario;
	}
	
	@Override
	public XmlEntity getModel() {
		return this.indice_titolario;
	}
	
}
