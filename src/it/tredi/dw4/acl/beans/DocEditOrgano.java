package it.tredi.dw4.acl.beans;

import javax.faces.context.FacesContext;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.Modello;
import it.tredi.dw4.acl.model.Organo;
import it.tredi.dw4.acl.model.PropostaOrgano;
import it.tredi.dw4.acl.model.TitoloWorkflowOrgano;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.docway.beans.DocWayShowthes;
import it.tredi.dw4.docway.model.delibere.Categoria;
import it.tredi.dw4.docway.model.delibere.Componente;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.Const;

import org.dom4j.Document;

public class DocEditOrgano extends AclDocEdit {

	private AclDocEditFormsAdapter formsAdapter;
	private String xml;
	private Organo organo;
	
	private String categorieLabel = "";
	private String categoriaLabel = "";
	private String comunicazioneMinLabel = "";
	private boolean modify = false;
	
	/* usati per sfruttare il meccanismo di visualizzionen degli xw:files con template di docway opportunamente modificati 
	 * ./acl/showdoc@xwfiles.xhtml
	 * */
	private boolean enableIW = true;
	private boolean resetJobsIWX = true;
	
	/* usati dal meccanismo di Titolario di Classificazione di Docway
	 * per non riscrivere tutto il meccanismo e quindi sfruttare bean + template esistenti */
	private boolean classificazioneDaTitolario = true;
	
	public DocEditOrgano() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));	
	}
	
	@Override
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.organo = new Organo();
    	this.organo.init(domDocumento);   
    	
    	String dicitCategoria 		=	FormsAdapter.getParameterFromCustomTupleValue("dicitCategoria", formsAdapter.getDefaultForm().getParam("_cd")); 
		if(!dicitCategoria.equals("")){
			this.setCategorieLabel(dicitCategoria.substring(dicitCategoria.indexOf("|") +1 ));
			this.setCategoriaLabel(dicitCategoria.substring(0,dicitCategoria.indexOf("|")));
		}
		
		String dicitComunicazione = 	FormsAdapter.getParameterFromCustomTupleValue("dicitComunicazione", formsAdapter.getDefaultForm().getParam("_cd"));
		if(!dicitComunicazione.equals(""))
			this.setComunicazioneMinLabel(dicitComunicazione.substring(0,dicitComunicazione.indexOf("|")));
		
		this.modify = false;
		this.docInformaticiFileDescription = I18N.mrs("dw4.organo_FileDescription");
		this.docInformaticiFileTypes = I18N.mrs("dw4.organo_fileTypes");
	}	

	@Override
	public DocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try{
			if(checkRequiredField()) return null;
			
			formsAdapter.saveOrgano(this.organo.asFormAdapterParams("", modify), enableIW);
			
			XMLDocumento response = super._saveDocument("organo", "list_of_struttur");
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			//formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			buildSpecificShowdocPageAndReturnNavigationRule("organo", response);
			return "showdoc@organo@reload";
			
		}catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	@Override
	public String clearDocument() throws Exception {
		try{
			classificazioneDaTitolario = true;
			XMLDocumento response = super._clearDocument();
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			if(!modify){
				return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
			}else{
				buildSpecificShowdocPageAndReturnNavigationRule("organo", response, isPopupPage());
				return "showdoc@organo@reload"; 
			}
				
		}catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public boolean checkRequiredField(){
		boolean result = false;
		
		//organo
		if(this.organo.getCod().isEmpty()){
			setErrorMessage("templateForm:organo_cod", I18N.mrs("dw4.manca_campo") + " " + I18N.mrs("acl.cod"));
			result = true;
		}
		
		if(this.organo.getNome().isEmpty()){
			setErrorMessage("templateForm:organo_nome", I18N.mrs("dw4.manca_campo") + " " + I18N.mrs("acl.name"));
			result = true;
		}
		
		if(this.organo.getClassif().getCod().isEmpty()){
			setErrorMessageNoInputField("templateForm:classif_verbale_Fields", I18N.mrs("dw4.manca_campo") + " " + I18N.mrs("dw4.classif_verbale"));
			result = true;
		}
		
		//categorie
		for(int i=0; i<this.organo.getCategorie().size();i++){
			Categoria categoria=this.organo.getCategorie().get(i);
			if(!categoria.getCod().isEmpty() && categoria.getNome().isEmpty()){
				setErrorMessage("templateForm:categoria:"+ i +":nome", I18N.mrs("dw4.manca_descrizione_campo") + " " + getCategoriaLabel());
				result = true;
			}else if(categoria.getCod().isEmpty() && !categoria.getNome().isEmpty()){
				setErrorMessage("templateForm:categoria:"+ i +":cod", I18N.mrs("dw4.manca_codice_campo") + " " + getCategoriaLabel());
				result = true;
			}else if(categoria.getCod().isEmpty() && categoria.getNome().isEmpty()){
				categoria.setTipo("");
			}
		}
		
		//componenti
		for(int i=0; i<this.organo.getComponenti().size();i++){
			Componente componente=this.organo.getComponenti().get(i);
			if(!componente.getNominativo().isEmpty() && componente.getIncarico().isEmpty()){
				setErrorMessage("templateForm:componente:"+ i +":incarico", I18N.mrs("dw4.manca_incarico_componente"));
				result = true;
			}else if(componente.getNominativo().isEmpty() && !componente.getIncarico().isEmpty()){
				setErrorMessage("templateForm:componente:"+ i +":nominativo", I18N.mrs("dw4.manca_nominativo_componente"));
				result = true;
			}
		}

		//proposte
		String codici = "";
		for(int i=0; i<this.organo.getProposte().size();i++){
			PropostaOrgano proposta=this.organo.getProposte().get(i);
			boolean tempResult = true;
			if(!proposta.getText().isEmpty() && proposta.getCod().isEmpty()){
				setErrorMessage("templateForm:proposta:"+ i +":cod", I18N.mrs("dw4.manca_cod_proposta"));
				tempResult = true;
				result = true;
			}else if(proposta.getText().isEmpty() && !proposta.getCod().isEmpty()){
				setErrorMessage("templateForm:proposta:"+ i +":descrizione", I18N.mrs("dw4.manca_descrizione_proposta"));
				tempResult = true;
				result = true;
			}else if(codici.indexOf("|" + proposta.getCod() + "|")!= -1){
				setErrorMessage("templateForm:proposta:"+ i +":cod", I18N.mrs("dw4.cod_proposta_duplicato"));
				tempResult = true;
				result = true;
			}
			
			TitoloWorkflowOrgano workflow = proposta.getWorkflow();
			if(!proposta.getText().isEmpty() && !proposta.getCod().isEmpty() && workflow.getName().isEmpty()){
				setErrorMessage("templateForm:proposta:"+ i +":workflow__input", I18N.mrs("dw4.manca_workflow_proposta"));
				tempResult = true;
				result = true;
			}
			
			if(tempResult)
				codici += "|" + proposta.getCod() + "|";
		}
		
		if(codici.equals("||")){
			setErrorMessage("templateForm:proposta:"+ 0 +":cod", I18N.mrs("dw4.manca_proposta"));
			result = true;
		}
		
		//modelli - inserimento/modifica
		for(int i=0; i<this.organo.getModelli().size();i++){
			Modello modello=this.organo.getModelli().get(i);
			if(!modello.getFile().getTitle().isEmpty()){
				if( (modello.getFile().getName().lastIndexOf(this.docInformaticiFileTypes.substring(1))) != (modello.getFile().getName().length() - 4) ){
					setErrorMessage("swfupload-" + modello.getNome() + "-control", I18N.mrs("dw4.formato_modello") + " " + modello.getNome() + " " + I18N.mrs("dw4.deve_essere") + " " + this.docInformaticiFileTypes);
					result = true;
				}
			}
		}
		
		return result;
	}
	
	/*
	 * porting da docway per implementare il classif_verbale
	 * */
	/**
	 * Imposta per la pagina l'inserimento manuale della classificazione
	 * @return
	 */
	public String passaInserimentoManualeClassif() {
		this.setClassificazioneDaTitolario(false);
		return null;
	}
	
	/**
	 * Imposta per la pagina l'inserimento da titolario della classificazione
	 * @return
	 */
	public String passaInserimentoTitolarioClassif() {
		this.setClassificazioneDaTitolario(true);
		return null;
	}
	
	/**
	 * Thesauro vincolato su titolario di classificazione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String thVincolatoTitolarioClassificazione() throws Exception {
		return showThesRel(false);
	}
	
	/**
	 * Gerarchia su titolario di classificazione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gerarchiaTitolarioClassificazione() throws Exception {
		return showThesRel(true);
	}
	
	/**
	 * Caricamento del titolario di classificazione
	 * 
	 * @param showGerarchia
	 * @return
	 * @throws Exception
	 */
	private String showThesRel(boolean showGerarchia) throws Exception {
		// in caso di caricamento della gerarchia occorre impostare a false il reset dei jobs di iwx
		// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
		// caricate si perderebbero le anteprime delle immagini
		setResetJobsIWX(false);
					
		String keypath = "classif";
		String startkey = Const.TITOLARIO_CLASSIF_NODO_RADICE;
		
		// Filtro impostato su campo codice
		String value = (organo.getClassif() != null && !"".equals(organo.getClassif().getFiltroCod())) ? organo.getClassif().getFiltroCod() : "";
		if (value.length() > 0) {
			// Devo formattare il valore passato in base alla classificazione
			value = ClassifUtil.formatNumberClassifCode(value);
			
			keypath = "CLASSIF_FROM_CODE";
			startkey = "lookupHierFromClassifCode";
		}
		
		String toDo = "";
		if (showGerarchia)
			toDo = "print_all";
		
		callShowThesRel(organo, I18N.mrs("dw4.titolario_di_classificazione"), "NT", keypath, startkey, AppUtil.getXdocwayprocDbName(getFormsAdapter().getDb()), toDo, value);
		
		// Azzero il campo nel form
		organo.getClassif().setFiltroCod("");
		
		return null;
	}
	
	/**
	 * Chiamata a titolario di classificazione
	 * 
	 * @param entity
	 * @param windowTitle
	 * @throws Exception
	 */
	protected void callShowThesRel(XmlEntity entity, String windowTitle, String relation, String keypath, String startkey, String db, String toDo, String value) throws Exception {
		callShowThesRel(entity, windowTitle, relation, keypath, startkey, db, toDo, value, "");
	}
	
	/**
	 * Chiamata a titolario di classificazione
	 * 
	 * @param entity
	 * @param windowTitle
	 * @throws Exception
	 */
	protected void callShowThesRel(XmlEntity entity, String windowTitle, String relation, String keypath, String startkey, String db, String toDo, String value, String classifPrefix) throws Exception {
		try {
			DocWayShowthes docwayShowthes = new DocWayShowthes(); 
			setShowthes(docwayShowthes);
			docwayShowthes.setModel(entity);
			docwayShowthes.setWindowTitle(windowTitle);
			if (classifPrefix != null)
				docwayShowthes.setClassifPrefix(classifPrefix);
			
			UserBean userBean = getUserBean();
			getFormsAdapter().showthesRel(userBean.getLogin(), userBean.getMatricola(), relation, keypath, startkey, db, false, toDo, value); // TODO modificato a causa del problema cache in login (verificare meglio, si tratta solo di un workaround)
		
			XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(userBean);
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return;
			}
			
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
			
			docwayShowthes.getFormsAdapter().fillFormsFromResponse(response);		
			docwayShowthes.init(response.getDocument());
			
			docwayShowthes.setActive(true);
			docwayShowthes.selectThes();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;			
		}
	}
	
	/*
	 * lookup workflow
	 * */
	public String lookupWorkflow() throws Exception {
		//TODO - effettuare tutti i controlli del caso (prima erano nei js custom)
		
		PropostaOrgano proposta = (PropostaOrgano) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
		int num = (organo.getProposte().contains(proposta)) ? organo.getProposte().indexOf(proposta): 0;
		
//		String value 		= (organo.getProposte()!= null && !"".equals(organo.getProposte().get(num))) ? organo.getProposte().get(num).getWorkflow().getName() : "";
//		String aliasName 	= "wf_nome"; //aliasName 
//		String aliasName1 	= ""; //aliasName1 
//		String titolo 		= "xml,/wf_entity/entity"; //titolo 
//		String ord 			= "xml(xpart:/wf_entity/entity)"; //ord 
//		String campi 		= ".workflow.@name=xml,/wf_entity/entity ; .workflow.@nrecord=xml,/wf_entity/entity/@id"; //campi
//		String xq 			= "[wf_tipo]=model"; // xq  
//		String db 			= "xdocwayproc"; //db 
//		String newRecord 	= ""; //newRecord
		
		String value 		= (organo.getProposte()!= null && !"".equals(organo.getProposte().get(num))) ? organo.getProposte().get(num).getWorkflow().getName() : "";
		String aliasName 	= "bwf_label"; //aliasName 
		String aliasName1 	= ""; //aliasName1 
		String titolo 		= "xml,/bwf_entity/label"; //titolo 
		String ord 			= "xml(xpart:/bwf_entity/label)"; //ord 
		String campi 		= ".workflow.@name=xml,/bwf_entity/@name ; .workflow.@label=xml,/bwf_entity/label ; .workflow.@bonitaVersion=xml,/bwf_entity/@bonitaVersion"; //campi
		String xq 			= ""; // xq  
		String db 			= "xdocwayproc"; //db 
		String newRecord 	= ""; //newRecord
		
		
		callLookup(proposta, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearLookupWorkflow() throws Exception{
		PropostaOrgano proposta = (PropostaOrgano) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("proposta");
		int num = (organo.getProposte().contains(proposta)) ? organo.getProposte().indexOf(proposta): 0;
		String campi = ".workflow.@name=xml,/bwf_entity/@name ; .workflow.@label=xml,/bwf_entity/label"; //campi
		return clearField(campi, proposta.getWorkflow());
	}
	
	
	
	
	/*
	 * getter /setter
	 * */
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}

	public Organo getOrgano() {
		return organo;
	}

	public void setOrgano(Organo organo) {
		this.organo = organo;
	}

	public String getCategorieLabel() {
		return categorieLabel;
	}

	public void setCategorieLabel(String categorieLabel) {
		this.categorieLabel = categorieLabel;
	}

	public String getCategoriaLabel() {
		return categoriaLabel;
	}

	public void setCategoriaLabel(String categoriaLabel) {
		this.categoriaLabel = categoriaLabel;
	}

	public String getComunicazioneMinLabel() {
		return comunicazioneMinLabel;
	}

	public void setComunicazioneMinLabel(String comunicazioneMinLabel) {
		this.comunicazioneMinLabel = comunicazioneMinLabel;
	}

	public boolean isEnableIW() {
		return enableIW;
	}

	public void setEnableIW(boolean enableIW) {
		this.enableIW = enableIW;
	}

	public boolean isResetJobsIWX() {
		return resetJobsIWX;
	}

	public void setResetJobsIWX(boolean resetJobsIWX) {
		this.resetJobsIWX = resetJobsIWX;
	}

	public boolean isModify() {
		return modify;
	}

	public void setModify(boolean modify) {
		this.modify = modify;
	}

	public boolean isClassificazioneDaTitolario() {
		return classificazioneDaTitolario;
	}

	public void setClassificazioneDaTitolario(boolean classificazioneDaTitolario) {
		this.classificazioneDaTitolario = classificazioneDaTitolario;
	}
}