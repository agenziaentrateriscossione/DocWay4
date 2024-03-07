package it.tredi.dw4.docwayproc.beans;

import javax.faces.context.FacesContext;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayDocedit;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.docwayproc.model.Indice_Titolario;
import it.tredi.dw4.docwayproc.model.workflow.WorkflowLink;
import it.tredi.dw4.docwayproc.model.workflow.WorkflowsTitolario;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.AppUtil;

public abstract class EditVoceIndice extends DocWayDocedit {

	protected DocDocWayDocEditFormsAdapter formsAdapter;
	protected String xml = "";
	
	public abstract Indice_Titolario getIndice_titolario();
	public abstract WorkflowsTitolario getWorkflowTitolario();
	
	/**
	 * caricamento del titolario di classificazione
	 * @return
	 * @throws Exception
	 */
	public String thVincolatoTitolarioClassificazione() throws Exception {
		String keypath = "classif";
		String startkey = Const.TITOLARIO_CLASSIF_NODO_RADICE;
		
		// Filtro impostato su campo codice
		String value = (getIndice_titolario().getCompilazione_automatica().getClassif() != null && !"".equals(getIndice_titolario().getCompilazione_automatica().getClassif().getFiltroCod())) ? getIndice_titolario().getCompilazione_automatica().getClassif().getFiltroCod() : "";
		if (value.length() > 0) {
			// Devo formattare il valore passato in base alla classificazione
			value = ClassifUtil.formatNumberClassifCode(value);
			
			keypath = "CLASSIF_FROM_CODE";
			startkey = "lookupHierFromClassifCode";
		}
		
		callShowThesRel(getIndice_titolario().getCompilazione_automatica(), I18N.mrs("dw4.titolario_di_classificazione"), "NT", keypath, startkey, AppUtil.getXdocwayprocDbName(getFormsAdapter().getDefaultForm().getParam("db")), "", value);
		
		// Azzero il campo nel form
		getIndice_titolario().getCompilazione_automatica().getClassif().setFiltroCod("");
		
		return null;
	}
	
	/**
	 * Chiamata a titolario di classificazione
	 * 
	 * @param entity
	 * @param windowTitle
	 * @throws Exception
	 */
	@Override
	protected void callShowThesRel(XmlEntity entity, String windowTitle, String relation, String keypath, String startkey, String db, String toDo, String value, String classifPrefix) throws Exception {
		try {
			DocwayprocShowthes docwayprocShowthes = new DocwayprocShowthes(); 
			setShowthes(docwayprocShowthes);
			docwayprocShowthes.setModel(entity);
			docwayprocShowthes.setWindowTitle(windowTitle);
			if (classifPrefix != null)
				docwayprocShowthes.setClassifPrefix(classifPrefix);
			
			UserBean userBean = getUserBean();
			getFormsAdapter().showthesRel(userBean.getLogin(), userBean.getMatricola(), relation, keypath, startkey, db, false, toDo, value); // TODO modificato a causa del problema cache in login (verificare meglio, si tratta solo di un workaround)
		
			XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(userBean);
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return;
			}
			
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
			
			docwayprocShowthes.getFormsAdapter().fillFormsFromResponse(response);		
			docwayprocShowthes.init(response.getDocument());
			
			docwayprocShowthes.setActive(true);
			docwayprocShowthes.selectThes();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;			
		}
	}
	
	/**
	 * cancellazione di un rif int in CC
	 * @return
	 */
	public String deleteRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		getIndice_titolario().getCompilazione_automatica().deleteRifintCC(rif);
		return null;
	}

	/**
	 * aggiunta di un rif int in CC
	 * @return
	 */
	public String addRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		getIndice_titolario().getCompilazione_automatica().addRifintCC(rif);
		return null;
	}
	
	/**
	 * cancellazione di un rif int in CDS
	 * @return
	 */
	public String deleteRifInt_cds(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		getIndice_titolario().getCompilazione_automatica().deleteRifintCDS(rif);
		return null;
	}

	/**
	 * aggiunta di un rif int in CDS
	 * @return
	 */
	public String addRifInt_cds(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		getIndice_titolario().getCompilazione_automatica().addRifintCDS(rif);
		return null;
	}
	
	/**
	 * RifintLookup su ufficio RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_rpa() throws Exception {
		String value 	= (getIndice_titolario().getCompilazione_automatica().getRpa() != null && getIndice_titolario().getCompilazione_automatica().getRpa().getNome_uff() != null) ? getIndice_titolario().getCompilazione_automatica().getRpa().getNome_uff() : "";
		String value2 	= (getIndice_titolario().getCompilazione_automatica().getRpa() != null && getIndice_titolario().getCompilazione_automatica().getRpa().getNome_persona() != null) ? getIndice_titolario().getCompilazione_automatica().getRpa().getNome_persona() : "";
		String campi 	= ".rpa.@nome_uff" +
								"|.rpa.@nome_persona" +
								"|.rpa.@cod_uff" +
								"|.rpa.@cod_persona";
		
		rifintLookupUfficio(getIndice_titolario().getCompilazione_automatica(), value, value2, campi);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_rpa() throws Exception {
		String value = (getIndice_titolario().getCompilazione_automatica().getRpa() != null && getIndice_titolario().getCompilazione_automatica().getRpa().getNome_uff() != null) ? getIndice_titolario().getCompilazione_automatica().getRpa().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".rpa.@nome_uff" +
							"|.rpa.@nome_persona" +
							"|.rpa.@cod_uff" +
							"|.rpa.@cod_persona";
			clearFieldRifint(campi, getIndice_titolario().getCompilazione_automatica().getRpa());
			getIndice_titolario().getCompilazione_automatica().getRpa().setUfficio_completo(false);
		}
		
		return null;
	}
	
	/**
	 * RifintLookup su persona RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_rpa() throws Exception {
		String value 	= (getIndice_titolario().getCompilazione_automatica().getRpa() != null && getIndice_titolario().getCompilazione_automatica().getRpa().getNome_uff() != null) ? getIndice_titolario().getCompilazione_automatica().getRpa().getNome_uff() : "";
		String value2 	= (getIndice_titolario().getCompilazione_automatica().getRpa() != null && getIndice_titolario().getCompilazione_automatica().getRpa().getNome_persona() != null) ? getIndice_titolario().getCompilazione_automatica().getRpa().getNome_persona() : "";
		String campi 	= ".rpa.@nome_uff" +
								"|.rpa.@nome_persona" +
								"|.rpa.@cod_uff" +
								"|.rpa.@cod_persona";
		
		rifintLookupPersona(getIndice_titolario().getCompilazione_automatica(), value, value2, campi);
				
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_rpa() throws Exception {
		String value = (getIndice_titolario().getCompilazione_automatica().getRpa() != null && getIndice_titolario().getCompilazione_automatica().getRpa().getNome_persona() != null) ? getIndice_titolario().getCompilazione_automatica().getRpa().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".rpa.@nome_persona" +
							"|.rpa.@cod_persona";
			clearFieldRifint(campi, getIndice_titolario().getCompilazione_automatica());
			getIndice_titolario().getCompilazione_automatica().getRpa().setUfficio_completo(false);
		}
		
		return null;
	}
	
	/**
	 * Lookup su ruolo RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_rpa() throws Exception {
		String value = (getIndice_titolario().getCompilazione_automatica().getRpa() != null && getIndice_titolario().getCompilazione_automatica().getRpa().getNome_uff() != null) ? getIndice_titolario().getCompilazione_automatica().getRpa().getNome_uff() : "";
		String campi = ".rpa.@nome_uff=xml,/ruolo/nome";  
		
		lookupRuolo(getIndice_titolario().getCompilazione_automatica(), value, campi, getIndice_titolario().getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_rpa() throws Exception {
		String campi = ".rpa.@nome_uff=xml,/ruolo/nome";
		
		return clearField(campi, getIndice_titolario().getCompilazione_automatica());
	}

	/**
	 * RifintLookup su ufficio CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCc().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCc().indexOf(rif): 0;
		
		String value 	= (getIndice_titolario().getCompilazione_automatica().getCc() != null && getIndice_titolario().getCompilazione_automatica().getCc().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCc().get(num).getNome_uff() : "";
		String value2 	= (getIndice_titolario().getCompilazione_automatica().getCc() != null && getIndice_titolario().getCompilazione_automatica().getCc().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCc().get(num).getNome_persona() : "";
		String campi 	= ".cc["+num+"].@nome_uff" +
								"|.cc["+num+"].@nome_persona" +
								"|.cc["+num+"].@cod_uff" +
								"|.cc["+num+"].@cod_persona";
		
		rifintLookupUfficio(getIndice_titolario().getCompilazione_automatica(), value, value2, campi);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCc().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCc().indexOf(rif): 0;
		
		String value = (getIndice_titolario().getCompilazione_automatica().getCc() != null && getIndice_titolario().getCompilazione_automatica().getCc().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCc().get(num).getNome_uff() : "";
		
		if (value.length() == 0) {
			String campi = ".cc["+num+"].@nome_uff" +
							"|.cc["+num+"].@nome_persona" +
							"|.cc["+num+"].@cod_uff" +
							"|.cc["+num+"].@cod_persona";
			clearFieldRifint(campi, getIndice_titolario().getCompilazione_automatica());
			getIndice_titolario().getCompilazione_automatica().getCc().get(num).setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * RifintLookup su persona CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCc().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCc().indexOf(rif): 0;
		
		String value	= (getIndice_titolario().getCompilazione_automatica().getCc() != null && getIndice_titolario().getCompilazione_automatica().getCc().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCc().get(num).getNome_uff() : "";
		String value2 	= (getIndice_titolario().getCompilazione_automatica().getCc() != null && getIndice_titolario().getCompilazione_automatica().getCc().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCc().get(num).getNome_persona() : "";
		String campi 	= ".cc["+num+"].@nome_uff" +
								"|.cc["+num+"].@nome_persona" +
								"|.cc["+num+"].@cod_uff" +
								"|.cc["+num+"].@cod_persona";
		
		rifintLookupPersona(getIndice_titolario().getCompilazione_automatica(), value, value2, campi);
		rif.setUfficio_completo(false);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCc().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCc().indexOf(rif): 0;
		
		String value = (getIndice_titolario().getCompilazione_automatica().getCc() != null && getIndice_titolario().getCompilazione_automatica().getCc().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCc().get(num).getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".cc["+num+"].@nome_persona" +
							"|.cc["+num+"].@cod_persona";
			clearFieldRifint(campi, getIndice_titolario().getCompilazione_automatica());
			getIndice_titolario().getCompilazione_automatica().getCc().get(num).setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * Lookup su ruolo CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCc().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCc().indexOf(rif): 0;
		
		String value = (getIndice_titolario().getCompilazione_automatica().getCc() != null && getIndice_titolario().getCompilazione_automatica().getCc().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCc().get(num).getNome_uff() : "";

		String campi = ".cc["+num+"].@nome_uff=xml,/ruolo/nome"; 

		lookupRuolo(getIndice_titolario().getCompilazione_automatica(), value, campi, getIndice_titolario().getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCc().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCc().indexOf(rif): 0;
		String campi = ".cc["+num+"].@nome_uff=xml,/ruolo/nome";
		
		return clearField(campi, getIndice_titolario().getCompilazione_automatica());
	}
	
	/**
	 * RifintLookup su ufficio CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCds().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCds().indexOf(rif): 0;
		
		String value 	= (getIndice_titolario().getCompilazione_automatica().getCds() != null && getIndice_titolario().getCompilazione_automatica().getCds().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCds().get(num).getNome_uff() : "";
		String value2 	= (getIndice_titolario().getCompilazione_automatica().getCds() != null && getIndice_titolario().getCompilazione_automatica().getCds().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCds().get(num).getNome_persona() : "";
		String campi 	= ".cds["+num+"].@nome_uff" +
								"|.cds["+num+"].@nome_persona" +
								"|.cds["+num+"].@cod_uff" +
								"|.cds["+num+"].@cod_persona";
		
		rifintLookupUfficio(getIndice_titolario().getCompilazione_automatica(), value, value2, campi);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCds().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCds().indexOf(rif): 0;
		
		String value = (getIndice_titolario().getCompilazione_automatica().getCds() != null && getIndice_titolario().getCompilazione_automatica().getCds().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCds().get(num).getNome_uff() : "";
		
		if (value.length() == 0) {
			String campi = ".cds["+num+"].@nome_uff" +
							"|.cds["+num+"].@nome_persona" +
							"|.cds["+num+"].@cod_uff" +
							"|.cds["+num+"].@cod_persona";
			clearFieldRifint(campi, getIndice_titolario().getCompilazione_automatica());
			getIndice_titolario().getCompilazione_automatica().getCds().get(num).setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * RifintLookup su persona CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCds().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCds().indexOf(rif): 0;
		
		String value	= (getIndice_titolario().getCompilazione_automatica().getCds() != null && getIndice_titolario().getCompilazione_automatica().getCds().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCds().get(num).getNome_uff() : "";
		String value2 	= (getIndice_titolario().getCompilazione_automatica().getCds() != null && getIndice_titolario().getCompilazione_automatica().getCds().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCds().get(num).getNome_persona() : "";
		String campi 	= ".cds["+num+"].@nome_uff" +
								"|.cds["+num+"].@nome_persona" +
								"|.cds["+num+"].@cod_uff" +
								"|.cds["+num+"].@cod_persona";
		
		rifintLookupPersona(getIndice_titolario().getCompilazione_automatica(), value, value2, campi);
		rif.setUfficio_completo(false);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCds().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCds().indexOf(rif): 0;
		
		String value = (getIndice_titolario().getCompilazione_automatica().getCds() != null && getIndice_titolario().getCompilazione_automatica().getCds().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCds().get(num).getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".cds["+num+"].@nome_persona" +
							"|.cds["+num+"].@cod_persona";
			clearFieldRifint(campi, getIndice_titolario().getCompilazione_automatica());
			getIndice_titolario().getCompilazione_automatica().getCds().get(num).setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * Lookup su ruolo CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCds().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCds().indexOf(rif): 0;
		
		String value = (getIndice_titolario().getCompilazione_automatica().getCds() != null && getIndice_titolario().getCompilazione_automatica().getCds().get(num) != null) ? getIndice_titolario().getCompilazione_automatica().getCds().get(num).getNome_uff() : "";
		String campi = ".cds["+num+"].@nome_uff=xml,/ruolo/nome";

		lookupRuolo(getIndice_titolario().getCompilazione_automatica(), value, campi, getIndice_titolario().getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getIndice_titolario().getCompilazione_automatica().getCds().contains(rif)) ? getIndice_titolario().getCompilazione_automatica().getCds().indexOf(rif): 0;
		String campi = ".cds["+num+"].@nome_uff=xml,/ruolo/nome";
		
		return clearField(campi, getIndice_titolario().getCompilazione_automatica());
	}
	
	/**
	 * Lookup su workflow
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupWorkflow() throws Exception {
		WorkflowLink workflowId = (WorkflowLink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflow");
		int num = getWorkflowTitolario().getBwf_link().contains(workflowId)? getWorkflowTitolario().getBwf_link().indexOf(workflowId) : 0;
		
		String aliasName 	= "bwf_label";
		String aliasName1 	= "";
		String titolo 		= "xml,/bwf_entity/label"; //titolo 
		String ord 			= "xml(xpart:/bwf_entity/label)"; //ord 
		String campi 		= ".bwf_link[" + num + "].@name=xml,/bwf_entity/@name" + //campi
							  " ; .bwf_link[" + num + "].label=xml,/bwf_entity/label" +
							  " ; .bwf_link[" + num + "].@bonitaVersion=xml,/bwf_entity/@bonitaVersion";
		String db 			= "xdocwayproc"; //db 
		String newRecord 	= ""; //newRecord 
		String xq			= ""; //extraQuery
		
		callLookup(getWorkflowTitolario(), aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, workflowId.getLabel());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su workflow
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearLookupWorkflow() throws Exception {
		WorkflowLink workflowId = (WorkflowLink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflow");
		int num = getWorkflowTitolario().getBwf_link().contains(workflowId)? getWorkflowTitolario().getBwf_link().indexOf(workflowId) : 0;
		String campi = ".bwf_link[" + num + "].@name=xml,/bwf_entity/@name" + //campi
				  " ; .bwf_link[" + num + "].label=xml,/bwf_entity/label";
		
		return clearField(campi, getWorkflowTitolario());
	}
	
}
