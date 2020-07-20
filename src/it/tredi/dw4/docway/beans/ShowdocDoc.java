package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.beans.Errore;
import it.tredi.dw4.beans.Errormsg;
import it.tredi.dw4.beans.Msg;
import it.tredi.dw4.beans.ReloadMsg;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Contenuto_in;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.docway.model.Fasc;
import it.tredi.dw4.docway.model.History;
import it.tredi.dw4.docway.model.Interoperabilita;
import it.tredi.dw4.docway.model.PersonaInRuolo;
import it.tredi.dw4.docway.model.Postit;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.docway.model.RifEsterno;
import it.tredi.dw4.docway.model.Storia;
import it.tredi.dw4.docway.model.TitoloRepertorio;
import it.tredi.dw4.docway.model.TitoloWorkflow;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.docway.model.fatturepa.Notifica;
import it.tredi.dw4.docway.model.workflow.Ex_Action;
import it.tredi.dw4.docway.model.workflow.Task;
import it.tredi.dw4.docway.model.workflow.WorkflowInstance;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateConverter;
import it.tredi.dw4.utils.DigitVisibilitaUtil;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public abstract class ShowdocDoc extends DocWayShowdoc {
	
	protected DocDocWayDocumentFormsAdapter formsAdapter;
	protected String xml;
	protected String view;
	protected Doc doc;
	protected String numFascCollegato;
	
	protected String uniservLink = ""; // url per la chiamata a Uniserv (firma digitale)
	
	protected boolean verificaDuplicati = false; // non si puo' utilizzare view perche' utilizzato e sovrascritto per history del documento
	
	protected boolean viewSelectRaccoglitori = false;
	
	private String linkToDoc = ""; // url di accesso al documento corrente (per copia in clipboard e invio mail di notifica)
	
	private boolean enableIW = false;
	
	private String extrawayWorkflowWsUrl = "";
	
	private boolean serviziFirmaMultipli = false; // vale true se sono attivi piu' di un servizio di firma (Uniser, FirmaWeb di Eng, ecc.)
	
	// parametri per la copia di allegati al documento su percorso di rete
	private boolean condivisioneFilesEnabled = false;
	private String labelCondivisioneFiles = "";
	
	private List<TitoloRepertorio> listof_rep = new ArrayList<TitoloRepertorio>();
	
	private List<TitoloWorkflow> listofWorkflows = new ArrayList<TitoloWorkflow>();
	
	private String personalView = "";
	
	private boolean showSectionStatiDocumento = false;
	
	// stampa info, segnatura e qrcode tramite IWX
	private String testoStampaInfo = "";
	private String testoStampaSegnatura = "";
	private String testoStampaQrcode = "";
	
	private String classifFormat = ""; // formato di visualizzazione della classificazione
	
	private String siTesto = ""; // testo da aggiungere in fase di stampa delle immagini con IWX
	
	// etichette custom per visibilita'
	private Map<String, String> labelsVisibilita = new HashMap<String, String>();
	
	public String getUniservLink() {
		return uniservLink;
	}

	public void setUniservLink(String uniservLink) {
		this.uniservLink = uniservLink;
	}

	public String getLinkToDoc() {
		return linkToDoc;
	}

	public void setLinkToDoc(String linkToDoc) {
		this.linkToDoc = linkToDoc;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public Doc getDoc() {
		return doc;
	}
	
	public String getExtrawayWorkflowWsUrl() {
		return extrawayWorkflowWsUrl;
	}

	public void setExtrawayWorkflowWsUrl(String eXtraWayWorkflowWSUrl) {
		this.extrawayWorkflowWsUrl = eXtraWayWorkflowWSUrl;
	}
	
	public boolean isServiziFirmaMultipli() {
		return serviziFirmaMultipli;
	}

	public void setServiziFirmaMultipli(boolean serviziFirmaMultipli) {
		this.serviziFirmaMultipli = serviziFirmaMultipli;
	}
	
	public boolean isCondivisioneFilesEnabled() {
		return condivisioneFilesEnabled;
	}

	public void setCondivisioneFilesEnabled(boolean condivisioneFilesEnabled) {
		this.condivisioneFilesEnabled = condivisioneFilesEnabled;
	}

	public String getLabelCondivisioneFiles() {
		return labelCondivisioneFiles;
	}

	public void setLabelCondivisioneFiles(String labelCondivisioneAllegati) {
		this.labelCondivisioneFiles = labelCondivisioneAllegati;
	}
	
	public List<TitoloRepertorio> getListof_rep() {
		return listof_rep;
	}

	public void setListof_rep(List<TitoloRepertorio> listof_rep) {
		this.listof_rep = listof_rep;
	}
	
	public List<TitoloWorkflow> getListofWorkflows() {
		return listofWorkflows;
	}

	public void setListofWorkflows(List<TitoloWorkflow> listofWorkflows) {
		this.listofWorkflows = listofWorkflows;
	}
	
	public String getPersonalView() {
		return personalView;
	}

	public void setPersonalView(String personalView) {
		this.personalView = personalView;
	}
	
	public boolean isShowSectionStatiDocumento() {
		return showSectionStatiDocumento;
	}

	public void setShowSectionStatiDocumento(boolean showSectionStatiDocumento) {
		this.showSectionStatiDocumento = showSectionStatiDocumento;
	}

	public String getTestoStampaInfo() {
		return testoStampaInfo;
	}

	public void setTestoStampaInfo(String testoStampaInfo) {
		this.testoStampaInfo = testoStampaInfo;
	}

	public String getTestoStampaSegnatura() {
		return testoStampaSegnatura;
	}

	public void setTestoStampaSegnatura(String testoStampaSegnatura) {
		this.testoStampaSegnatura = testoStampaSegnatura;
	}
	
	public String getTestoStampaQrcode() {
		return testoStampaQrcode;
	}

	public void setTestoStampaQrcode(String testoStampaQrcode) {
		this.testoStampaQrcode = testoStampaQrcode;
	}
	
	public String getClassifFormat() {
		return classifFormat;
	}

	public void setClassifFormat(String classifFormat) {
		this.classifFormat = classifFormat;
	}
	
	public String getSiTesto() {
		return siTesto;
	}

	public void setSiTesto(String siTesto) {
		this.siTesto = siTesto;
	}
	
	public Map<String, String> getLabelsVisibilita() {
		return labelsVisibilita;
	}

	public void setLabelsVisibilita(Map<String, String> labelsVisibilita) {
		this.labelsVisibilita = labelsVisibilita;
	}

	public ShowdocDoc() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public DocDocWayDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public abstract void init(Document dom);
	
	/**
	 * Init common per tutte le tipologie di documenti
	 */
	@SuppressWarnings("unchecked")
	public void initCommon(Document dom) {
		//orderHistory("");
		
		setUniservLink("");
		enableIW = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@enableIW"));
		
		// generazione dell'URL di accesso al documento (per copia link e invio notifica)
		if (getDoc() != null && getDoc().getNrecord() != null && !getDoc().getNrecord().equals(""))
			linkToDoc = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/loaddoc.pf?db=" + formsAdapter.getDb() + "&alias=docnrecord&value=" + getDoc().getNrecord();
		
		extrawayWorkflowWsUrl = XMLUtil.parseStrictAttribute(dom, "/response/@extrawayWorkflowWsUrl"); // URL necessario alla chiamata dei webservices di workflow di bonita
		
		setRaggruppaCC_statoIniziale(XMLUtil.parseStrictAttribute(dom, "/response/funzionalita_disponibili/@raggruppaCC_statoIniziale"));
		
		setCurrentPage(getFormsAdapter().getCurrent()+"");
		
		// copia di xwfiles del documento su percorso di rete
		if (getDoc().containsFiles() 
				&& (getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_VARIE) 
						|| (getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_PARTENZA) && getDoc().isBozza()))) { // pulsante visibile sono se ci sono files da condividere e doc in partenza in bozza o doc non protocollato 
			condivisioneFilesEnabled = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@condivisioneFilesEnabled"));
			labelCondivisioneFiles = XMLUtil.parseStrictAttribute(dom, "/response/@labelCondivisioneFiles");
			if (condivisioneFilesEnabled && (labelCondivisioneFiles == null || labelCondivisioneFiles.equals("")))
				labelCondivisioneFiles = I18N.mrs("dw4.condividi_su_cartella_remota");
		}
		
		// TODO questa sezione deve essere modificata in caso di aggiunta di ulteriori servizi di firma
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("abilitaFirmaUniserv", false) && getFormsAdapter().checkBooleanFunzionalitaDisponibile("abilitaFirmaEng", false))
			serviziFirmaMultipli = true;
		
		listof_rep = XMLUtil.parseSetOfElement(dom, "/response/listof_rep/repertorio", new TitoloRepertorio());
		
		// eliminazione dei workflow gia' avviati sul documento corrente che attualmente non risultano conclusi o annullati
		removeOrphanedWorkflows(dom);
		
		// eventuale personalView da utilizzare per la costruzione del template
		personalView = XMLUtil.parseStrictAttribute(dom, "/response/@personalViewToUse");
		
		// verifica delle condizioni (comuni a tutte le tipologie di documenti) per le quali occorre mostrare 
		// la sezione di stati del documento.
		// N.B.: EVENTUALI PERSONALIZZAZIONI DI REPERTORI DEVONO ESSERE GESTITE ALL'INTERNO DEL TEMPLATE DEL REPERTORIO
		Storia segnaturaDoc = getDoc().getSegnatura();
		if ((getDoc().getProt_differito() != null && getDoc().getProt_differito().getData_arrivo() != null && getDoc().getProt_differito().getData_arrivo().length() > 0)
				|| (segnaturaDoc != null && segnaturaDoc.getCod_uff_oper() != null && segnaturaDoc.getCod_uff_oper().length() > 0)
				|| (getDoc().getAnnullato() != null && getDoc().getAnnullato().toLowerCase().equals("si"))
				|| (getDoc().getExtra() != null && getDoc().getExtra().getConservazione() != null && getDoc().getExtra().getConservazione().size() > 0)
				|| (getDoc().getExtra() != null && getDoc().getExtra().getStato_deposito() != null && getDoc().getExtra().getStato_deposito().length() > 0)
				|| (getDoc().getExtra() != null && getDoc().getExtra().getStato_deposito_minuta() != null && getDoc().getExtra().getStato_deposito_minuta().length() > 0)
				|| getDoc().isBozza()
				|| getDoc().isPersonale()
				|| (getDoc().getVisibilita() != null && getDoc().getVisibilita().getTipo() != null && getDoc().getVisibilita().getTipo().length() > 0 && !getDoc().getVisibilita().getTipo().toLowerCase().equals("pubblico"))
				|| getDoc().isFilesPrenotati()
				|| (getDoc().getMessageId() != null && getDoc().getMessageId().length() > 0 && getDoc().getEmailAttachmentIndex() != null && getDoc().getEmailAttachmentIndex().length() > 0)
				|| getDoc().isCestino()) {
			showSectionStatiDocumento = true;
		}
		else {
			showSectionStatiDocumento = false;
		}
		
		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(dom, "doc");
		
		// caricamento dei testi di stampa info, segnatura e qrcode per IWX
		testoStampaInfo = XMLUtil.parseStrictElement(dom, "/response/stampa_info");
		testoStampaSegnatura = XMLUtil.parseStrictElement(dom, "/response/stampa_segnatura");
		testoStampaQrcode = XMLUtil.parseStrictElement(dom, "/response/stampa_qrcode");
		
		// formato da applicare in visualizzazione della classificazione
		classifFormat = XMLUtil.parseStrictAttribute(dom, "/response/@classifFormat", "");
		
		// testo da aggiungere in fase di stampa delle immagini con IWX
		siTesto = XMLUtil.parseStrictAttribute(dom, "/response/@SITesto", "");
		if (siTesto.length() > 0) {
			try {
				siTesto = new String(new Base64().decode(siTesto.getBytes(FormAdapter.ENCODING_ISO_8859_1)), FormAdapter.ENCODING_ISO_8859_1);
			} catch (Exception e) {
				Logger.error(e.getMessage());
			}
		}
		
		// caricamento etichette custom per visibilita'
		DigitVisibilitaUtil digitVisibilitaUtil = new DigitVisibilitaUtil(XMLUtil.parseStrictAttribute(dom, "/response/@dicitVis"));
		labelsVisibilita = digitVisibilitaUtil.getDigitSingolare();
		
		// mbernardini 25/04/2015 : escape di caratteri speciali
		getDoc().setOggetto(StringUtil.convertSpecialCharsCodeToEntities(getDoc().getOggetto()));
		if (getDoc().getNote() != null)
			getDoc().getNote().setText(StringUtil.convertSpecialCharsCodeToEntities(getDoc().getNote().getText())); 
		// TODO gestire tutti i possibili campi (andrebbe utilizzata una libreria specifica per comprendere tutti i caratteri non gestiti)
	}
	
	@SuppressWarnings("unchecked")
	private void removeOrphanedWorkflows(Document dom) {
		listofWorkflows= XMLUtil.parseSetOfElement(dom, "/response/workflows/workflow", new TitoloWorkflow());
		
		TreeSet<Integer> wfToRemove = new TreeSet<Integer>();
		/*
		 * aalberghini: eliminazione in due passaggi: prima si determinano gli
		 * indici degli elementi da rimuovere...
		 */
		if (listofWorkflows != null && listofWorkflows.size() > 0 
				&& getDoc().getWorkflowInstances() != null && getDoc().getWorkflowInstances().size() > 0) {
			for (int i=0; i<listofWorkflows.size(); i++) {
				TitoloWorkflow workflowTitle = (TitoloWorkflow) listofWorkflows.get(i);
				if (workflowTitle != null) {
					for (int j=0; j<getDoc().getWorkflowInstances().size(); j++) {
						WorkflowInstance workflowInstance = (WorkflowInstance) getDoc().getWorkflowInstances().get(j);
						if (workflowInstance != null
								&& workflowInstance.getProcess() != null
								&& workflowTitle.getName() != null
								&& (!workflowInstance.getStatus().equals("finished") && !workflowInstance.getStatus().equals("cancelled"))
								&& workflowInstance.getProcess().equals(workflowTitle.getName())) {
							wfToRemove.add(i);
						}
					}
				}
			}
		}
		
		/*
		 * aalberghini: ... poi si rimuovono in un colpo solo
		 */
		if (!wfToRemove.isEmpty()) {
			for (int wfIdx : wfToRemove.descendingSet()) {
				listofWorkflows.remove(wfIdx);
			}
		}
		
	}
	
	@Override
	public String paginaTitoli() throws Exception {
		return this._paginaTitoli("showtitles@docway");
	}

	public abstract void reload() throws Exception;
	
	@Override
	public String modifyTableDoc() throws Exception {
		if (getDoc().getRepertorio() != null && getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0)
			this.formsAdapter.modifyTableDoc(getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
		else
			this.formsAdapter.modifyTableDoc();
		
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		// mbernardini 14/05/2015 : in caso di warnings restituiti dal service (es. verifica impronta), stampo
		// un messaggio a video
		String verbo = response.getAttributeValue("/response/@verbo", "");
		String warnings = response.getAttributeValue("/response/@warnings", "");
		if (!verbo.equals("docEdit") && warnings.length() > 0) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			setErroreResponse(warnings, Const.MSG_LEVEL_ERROR);
			return null;
		}
		else {
			return buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}

	}
	
	public String removeRifOP() throws Exception{
		this.formsAdapter.removeRifInt(this.getDoc().getAssegnazioneOP().getCod_persona(), this.getDoc().getAssegnazioneOP().getCod_uff(), this.getDoc().getAssegnazioneOP().getDiritto());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(response); //restore delle form
		_reloadWithoutNavigationRule();
		return null;
	}
	
	public String removeRifOPM() throws Exception{
		this.formsAdapter.removeRifInt(this.getDoc().getAssegnazioneOPM().getCod_persona(), this.getDoc().getAssegnazioneOPM().getCod_uff(), this.getDoc().getAssegnazioneOPM().getDiritto());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(response); //restore delle form
		_reloadWithoutNavigationRule();
		return null;
	}
	
	public String scartaDocOP() throws Exception{
		return scartaDoc(this.getDoc().getAssegnazioneOP());
	}

	public String scartaDocOPM() throws Exception{
		return scartaDoc(this.getDoc().getAssegnazioneOPM());
	}

	public String getData_asseg(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = getDoc().getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona)){
				data = element.getData();
			}
			
		}
		return data;
	}

	
	public String getData_assegRPA(){
		Rif rif = this.getDoc().getAssegnazioneRPA();
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = getDoc().getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona)){
				data = element.getData();
			}
			
		}
		return data;
	}
	
	public String getData_assegRPAM(){
		Rif rif = this.getDoc().getAssegnazioneRPAM();
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = getDoc().getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona)){
				data = element.getData();
			}
			
		}
		return data;
	}
	
	public String getData_assegOP(){
		Rif rif = this.getDoc().getAssegnazioneOP();
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = getDoc().getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona)){
				data = element.getData();
			}
			
		}
		return data;
	}
	
	public String getData_assegOPM(){
		Rif rif = this.getDoc().getAssegnazioneOPM();
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = getDoc().getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona)){
				data = element.getData();
			}
			
		}
		return data;
	}

	public String getInfoAssegnazione(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		return getInfoAssegnazione(rif);
	}

	public String getInfoAssegnazione(Rif rif){
		String data = "";
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = getDoc().getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona)){
				data = I18N.mrs("dw4.assegnato_da") + " " + element.getOperatore();
			}
			
		}
		return data;
	}
	
	public String getInfoAssegnazioneRPA(){
		Rif rif = this.getDoc().getAssegnazioneRPA();
		return getInfoAssegnazione(rif);
	}
	
	public String getInfoAssegnazioneRPAM(){
		Rif rif = this.getDoc().getAssegnazioneRPAM();
		return getInfoAssegnazione(rif);
	}
	
	public String getInfoAssegnazioneOP(){
		Rif rif = this.getDoc().getAssegnazioneOP();
		return getInfoAssegnazione(rif);
	}
	
	public String getInfoAssegnazioneOPM(){
		Rif rif = this.getDoc().getAssegnazioneOPM();
		return getInfoAssegnazione(rif);
	}
	
	public String getCheckVisto(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		return getCheckVisto(rif);
	}
	
	public String getCheckVisto(Rif rif){
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = getDoc().getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona) && null!=element.getVisto_da() && element.getVisto_da().trim().length() > 0){
				data = "Visto da "+element.getVisto_da()+" il "+(new DateConverter().getAsString(null, null, element.getData_visto()))+ " alle "+element.getOra_visto();
			}
			
		}
		return data;
	}
	
	public String getCheckVistoRPA(){
		Rif rif = this.getDoc().getAssegnazioneRPA();
		return getCheckVisto(rif);
	}
	
	public String getCheckVistoRPAM(){
		Rif rif = this.getDoc().getAssegnazioneRPAM();
		return getCheckVisto(rif);
	}
	
	public String getCheckVistoOP(){
		Rif rif = this.getDoc().getAssegnazioneOP();
		return getCheckVisto(rif);
	}

	public String getCheckVistoOPM(){
		Rif rif = this.getDoc().getAssegnazioneOPM();
		return getCheckVisto(rif);
	}
	
	/**
	 * Caricamento della storia del documento
	 * @return
	 */
	public String openHistory(){
		return orderHistory("");
	}
	
	/**
	 * Chiusura della storia del documento
	 * @return
	 */
	public String closeHistory() {
		getDoc().setHistory(new ArrayList<History>());
		return null;
	}

	/**
	 * Caricamento della storia del documento con definizione dell'ordinamento
	 * @param order Campi su cui ordinare la storia
	 * @return
	 */
	public String orderHistory(String order){
		try {
			this.formsAdapter.showHistory(order);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			getDoc().initHistory(response.getDocument());
			this.view = response.getAttributeValue("/response/@view", "");
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * Caricamento della storia del documento con ordinamento su tipo
	 * @return
	 */
	public String orderTipo(){
		return orderHistory("@tipo");
	}
	
	/**
	 * Caricamento della storia del documento con ordinamento su persona/ufficio
	 * @return
	 */
	public String orderPersonaUfficio(){
		return orderHistory("@nome_persona");
	}
	
	/**
	 * Caricamento della storia del documento con ordinamento su operatore
	 * @return
	 */
	public String orderOperatore(){
		return orderHistory("@operatore");
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getView() {
		return view;
	}
	
	public boolean isEnableIW() {
		return enableIW;
	}

	public void setEnableIW(boolean enableIW) {
		this.enableIW = enableIW;
	}

	public void setVerificaDuplicati(boolean verificaDuplicati) {
		this.verificaDuplicati = verificaDuplicati;
	}

	public boolean isVerificaDuplicati() {
		return verificaDuplicati;
	}
	
	public String removeLinkFasc() throws Exception{
		Fasc fasc = (Fasc) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fasc");
		this.formsAdapter.removeLinkFasc(fasc.getCod());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(response); //restore delle form
		reload();
		return null;
	}
	
	/**
	 * Download del file
	 */
	public String downloadFile(String fileId, String fileName, boolean inline) throws Exception {
		try {
			String id = fileId;
			String name = fileName;
			
			name = name.replaceAll("'", "_");
			
			String ext_id = "";
			if (id.lastIndexOf(".") != -1)
				ext_id = id.substring(id.lastIndexOf(".")+1);
			String ext_name = "";
			if (name.lastIndexOf(".") != -1)
				ext_name = name.substring(name.lastIndexOf(".")+1);
			
			if (ext_name.equals("")) // se il titolo (name) non ha estensione gli viene assegnato quello dell'id
				name += "." + ext_id;
			
			if (!ext_id.equals("") && !ext_name.equals("") && !ext_id.endsWith(ext_name))
				name += "." + ext_id;
			
			getFormsAdapter().downloadFile(fileId, fileName);
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				formsAdapter.getDefaultForm().addParam("toDo", ""); // azzeramento del parametro 'toDo'
				
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				response.setContentType(new MimetypesFileTypeMap().getContentType(id));
				response.setContentLength(attachFile.getContent().length);
				String mode = "attachment";
				if (inline)
					mode = "inline";
				response.setHeader("Content-Disposition", mode + "; filename=\"" + name + "\"");
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
		
		return null;
	}
	
	/**
	 * Download di doc informatico di tipo file
	 * @throws Exception
	 */
	public String openFile() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		downloadFile(file.getName(), file.getTitle(), true);
		
		return null;
	}
	
	/**
	 * Download di doc informatico di tipo file
	 * @throws Exception
	 */
	public String downloadFile() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		downloadFile(file.getName(), file.getTitle(), false);
		
		return null;
	}
	
	public String verificaFirma() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		verificaFirma(file.getName(), file.getTitle());
		return null;
	}
	
	public String verificaFirma(String fileId, String fileName) throws Exception {
		try {
			getFormsAdapter().fillFormsFromResponse(formsAdapter.getLastResponse());
			getFormsAdapter().verificaFirma(fileId, fileName);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			formsAdapter.fillFormsFromResponse(response);
			init(response.getDocument());
			
			VerificaFirma verificaFirma = new VerificaFirma();
			verificaFirma.init(response.getDocument());
			verificaFirma.setVisible(true);
			setSessionAttribute("docwayVerificaFirma", verificaFirma);
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		
		return null;
	}
	
	/**
	 * Download di doc informatico di tipo immagine
	 * @throws Exception
	 */
	public String downloadImage() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("image");
		downloadFile(file.getName(), file.getTitle(), false);
		
		return null;
	}
	
	/**
	 * Download del file convertito
	 */
	public String downloadFileConvertito() throws Exception {
		XwFile fileConvertito = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("derFromXwfile");
		downloadFile(fileConvertito.getName(), fileConvertito.getTitle(), false);
		
		return null;
	}
	
	/**
	 * Download del file di interoperabilita'
	 * @throws Exception
	 */
	public String downloadInteroperabilita() throws Exception {
		Interoperabilita interop = (Interoperabilita) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("interop");
		downloadFile(interop.getName(), interop.getTitle(), false);
		
		return null;
	}
	
	/**
	 * Download di un file di notifica su fatturaPA
	 * @return
	 * @throws Exception
	 */
	public String downloadNotificaFattura() throws Exception {
		Notifica notifica = (Notifica) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("notifica");
		if (notifica != null)
			downloadFile(notifica.getName(), notifica.getTitle(), false);
		
		return null;
	}
	
	/**
	 * Download del file di differenze fra l'ultima e la prima versione di un allegato, generato
	 * attraverso una chiamata al File Conversion Service
	 * @throws Exception
	 */
	public String showDiffBetweenVersionsPDF() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		showDiffBetweenVersions(file, "pdf");
		
		return null;
	}
	
	/**
	 * Download del file di differenze fra l'ultima e la prima versione di un allegato, generato
	 * attraverso una chiamata al File Conversion Service
	 * @throws Exception
	 */
	public String showDiffBetweenVersionsOD() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		showDiffBetweenVersions(file, "od");
		
		return null;
	}
	
	/**
	 * Download del file di differenze fra l'ultima e la prima versione di un allegato, generato
	 * attraverso una chiamata al File Conversion Service
	 * 
	 * @param xwfile file per il quale generare le differenze
	 * @param expType tipo di file da restituire (pdf, od)
	 * @throws Exception
	 */
	public String showDiffBetweenVersions(XwFile xwfile, String expType) throws Exception {
		try {
			getFormsAdapter().showDiffBetweenVersions(xwfile.getName(), expType);
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				response.setContentType(new MimetypesFileTypeMap().getContentType(attachFile.getFilename()));
				response.setContentLength(attachFile.getContent().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + attachFile.getFilename() + "\"");
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
		
		return null;
	}
	
	/**
	 * Visualizzazione delle versioni di un file 
	 * @return
	 * @throws Exception
	 */
	public String fileVersions() throws Exception {
		try {
			XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
			
			this.formsAdapter.fileVersions(file.getName());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			ShowdocHistory showdocHistory = new ShowdocHistory();
			showdocHistory.getFormsAdapter().fillFormsFromResponse(response);
			showdocHistory.init(response.getDocument());
			showdocHistory.setPopupPage(true);
			setSessionAttribute("showdocHistory", showdocHistory);
			
			return "doc_reponse@fileversions";
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	public String removeFromFasc() throws Exception{
		this.formsAdapter.removeFromFasc();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		_reloadWithoutNavigationRule();
		return null;
	}
	
	public String removeFromFascMinuta() throws Exception{
		this.formsAdapter.removeFromFascMinuta();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		_reloadWithoutNavigationRule();
		return null;
	}

	public String rimuoviInterventoRPAM() throws Exception{
		return rimuoviIntervento(getDoc().getAssegnazioneRPAM());
	}

	public String assegnaInterventoRPAM() throws Exception{
		return assegnaIntervento(this.getDoc().getAssegnazioneRPAM());
	}

	public String queryFasc() throws Exception{
		this.formsAdapter.queryFasc(getDoc().getFasc_rpa().getNum());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return new QueryFascicolo().navigateResponse(response);
	}
	
	public String queryFascMinuta() throws Exception{
		this.formsAdapter.queryFasc(getDoc().getFasc_rpam().getNum());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return new QueryFascicolo().navigateResponse(response);
	}
	
	public String queryFascCollegati() throws Exception{
		Fasc fasc = (Fasc) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fasc");
		this.formsAdapter.queryFasc(fasc.getCod());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return new QueryFascicolo().navigateResponse(response);
	}
	
	public String scartaRuoli() throws Exception{
		this.formsAdapter.scartaRuoli();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		// viene caricato il record successivo al corrente.. per lo scarto delle vaschette
		this.init(response.getDocument());
		getFormsAdapter().fillFormsFromResponse(response);
		_reload(); 
		return null;
	}
	
	public String delPostit() throws Exception{
		Postit postit_element = (Postit) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("postit");
		int postitPos = getDoc().getPostit().indexOf(postit_element);
		this.formsAdapter.delPostit(postitPos);
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		this.getDoc().getPostit().remove(postitPos);
		return null;
	}

	public String modPostit() throws Exception{
		Postit postit_element = (Postit) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("postit");
		int postitPos = getDoc().getPostit().indexOf(postit_element);
		String text = postit_element.getText();
		this.formsAdapter.modPostit(postitPos, text);
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayPostit postit = new DocWayPostit();
		postit.getFormsAdapter().fillFormsFromResponse(response);
		postit.init(response.getDocument());
		postit.setShowdoc(this);
		setSessionAttribute("docwayPostit", postit);
		return null;
	}
	
	public String addPostit() throws Exception{
		this.formsAdapter.addPostit("", "");
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayPostit postit = new DocWayPostit();
		postit.getFormsAdapter().fillFormsFromResponse(response);
		postit.init(response.getDocument());
		postit.setShowdoc(this);
		setSessionAttribute("docwayPostit", postit);
		return null;
	}
	
	/**
	 * rigetta il documento corrente con aggiunta del postit sul documento
	 * @return
	 * @throws Exception
	 */
	public String rigetta() throws Exception{
		this.formsAdapter.rigettaPostit();
		this.formsAdapter.addPostit("", "");
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayPostit postit = new DocWayPostit();
		postit.getFormsAdapter().fillFormsFromResponse(response);
		postit.init(response.getDocument());
		postit.setShowdoc(this);
		setSessionAttribute("docwayPostit", postit);
		return null;
	}

	/**
	 * rigetta il documento corrente
	 * @return
	 * @throws Exception
	 */
	public String rigettaDoc() throws Exception{
		this.formsAdapter.rigetta();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		this.init(response.getDocument());
		formsAdapter.fillFormsFromResponse(response);
		reload();
		return null;
	}
	
	public String addRPA() throws Exception{
		this.formsAdapter.openRifInt("RPA", getDoc().isBozza());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayDocRifInt rifInt = new DocWayDocRifInt();
		rifInt.getFormsAdapter().fillFormsFromResponse(response);
		rifInt.init(response.getDocument());
		rifInt.setDataForLookupFromDoc(getDoc());
		rifInt.setViewAddRPA(true);
		rifInt.setShowdoc(this);
		setSessionAttribute("rifInt", rifInt);
		return null;
	}

	public String addRPAM() throws Exception{
		this.formsAdapter.openRifInt("RPAM", getDoc().isBozza());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayDocRifInt rifInt = new DocWayDocRifInt();
		rifInt.getFormsAdapter().fillFormsFromResponse(response);
		rifInt.init(response.getDocument());
		rifInt.setDataForLookupFromDoc(getDoc());
		rifInt.setViewAddRPAM(true);
		rifInt.setShowdoc(this);
		setSessionAttribute("rifInt", rifInt);
		return null;
	}

	public String addOP() throws Exception{
		this.formsAdapter.openRifInt("OP", getDoc().isBozza());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayDocRifInt rifInt = new DocWayDocRifInt();
		rifInt.getFormsAdapter().fillFormsFromResponse(response);
		rifInt.init(response.getDocument());
		rifInt.setDataForLookupFromDoc(getDoc());
		rifInt.setViewAddOP(true);
		rifInt.setShowdoc(this);
		setSessionAttribute("rifInt", rifInt);
		return null;
	}
	
	public String addOPM() throws Exception{
		this.formsAdapter.openRifInt("OPM", getDoc().isBozza());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayDocRifInt rifInt = new DocWayDocRifInt();
		rifInt.getFormsAdapter().fillFormsFromResponse(response);
		rifInt.init(response.getDocument());
		rifInt.setDataForLookupFromDoc(getDoc());
		rifInt.setViewAddOPM(true);
		rifInt.setShowdoc(this);
		setSessionAttribute("rifInt", rifInt);
		return null;
	}

	public String addCC() throws Exception{
		this.formsAdapter.openRifInt("CC", getDoc().isBozza());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayDocRifInt rifInt = new DocWayDocRifInt();
		rifInt.getFormsAdapter().fillFormsFromResponse(response);
		rifInt.init(response.getDocument());
		rifInt.setDataForLookupFromDoc(getDoc());
		rifInt.setViewAddCC(true);
		rifInt.setShowdoc(this);
		setSessionAttribute("rifInt", rifInt);
		return null;
	}

	public String showFormExecuteTask() throws Exception {
		Task task = (Task) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("task");
		WorkflowInstance wfInstance = (WorkflowInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflowInstance");
		

		/*this.formsAdapter.openRifInt("CC", getDoc().isBozza());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());*/
		String physDoc_doc = formsAdapter.getDefaultForm().getParam("physDoc");
		DocWayDocExecuteTask execTask = new DocWayDocExecuteTask(task, wfInstance, physDoc_doc);
		//execTask.getFormsAdapter().fillFormsFromResponse(response);
		//execTask.init(response.getDocument());
		execTask.setViewExecuteTask(true);
		execTask.setShowdoc(this);
		setSessionAttribute("execTask", execTask);
		return null;
	}
	
	/**
	 * Caricamento delle variabili associate ad un task di una istanza
	 * di workflow di bonita
	 * 
	 * @return
	 * @throws Exception
	 */
	public String showWfTaskVars() throws Exception {
		try {
			Ex_Action ex_action = (Ex_Action) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("ex_action");
			WorkflowInstance wfInstance = (WorkflowInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflowInstance");
			
			if (ex_action != null 
					&& ex_action.getTaskId() != null 
					&& !ex_action.getTaskId().equals("")) {
				
				formsAdapter.showWfTaskVars(wfInstance.getId(), ex_action.getTaskId());
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				
				DocWayWfTaskVars docwayWfTaskVars = new DocWayWfTaskVars();
				docwayWfTaskVars.setEx_action(ex_action);
				docwayWfTaskVars.init(response.getDocument());
				docwayWfTaskVars.getFormsAdapter().fillFormsFromResponse(response);
				docwayWfTaskVars.setVisible(true);
				setSessionAttribute("docwayWfTaskVars", docwayWfTaskVars);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String addCDS() throws Exception{
		this.formsAdapter.openRifInt("CDS", getDoc().isBozza());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayDocRifInt rifInt = new DocWayDocRifInt();
		rifInt.getFormsAdapter().fillFormsFromResponse(response);
		rifInt.init(response.getDocument());
		rifInt.setViewAddCDS(true);
		rifInt.setDataForLookupFromDoc(getDoc());
		rifInt.getDoc().setAssegnazioneRPA(getDoc().getAssegnazioneRPA());
		if ( getDoc().getAssegnazioneCDS().size() > 0 )
			rifInt.getDoc().setAssegnazioneCDS(getDoc().getAssegnazioneCDS());
		rifInt.setShowdoc(this);
		setSessionAttribute("rifInt", rifInt);
		return null;
	}

	/**
	 * Controllo dell'impronta degli allegati del documento
	 * @return
	 * @throws Exception
	 */
	public String checkImpronta() throws Exception {
		return _checkImpronta(null);
	}
	
	/**
	 * Controllo dell'impronta su un singolo allegato di tipo file del documento (nuova normativa con impronta
	 * SHA-256 per ogni allegato)
	 * @return
	 * @throws Exception
	 */
	public String checkImprontaFile() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		return _checkImpronta(file.getName());
	}
	
	/**
	 * Controllo dell'impronta su un singolo allegato di tipo immagine del documento (nuova normativa con impronta
	 * SHA-256 per ogni allegato)
	 * @return
	 * @throws Exception
	 */
	public String checkImprontaImage() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("image");
		return _checkImpronta(file.getName());
	}
	
	/**
	 * Verifica dell'impronta su tutto il documento (SHA1) o su un singolo file (nuova normativa
	 * basata su SHA256)
	 * @param fileid id del file sul quale verificare l'impronta (se null o vuoto, vecchia gestione con verifica dell'impronta dell'intero documento)
	 * @return
	 * @throws Exception
	 */
	private String _checkImpronta(String fileid) throws Exception {
		try {
			this.formsAdapter.checkImpronta(fileid);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			Msg message = new Msg();
			message.setActive(true);
			message.init(response.getDocument());
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			session.setAttribute("msg", message);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * nuovo documento da showdoc
	 * @return
	 * @throws Exception
	 */
	public String nuovoDoc() throws Exception {
		try {
			formsAdapter.insTableDoc(getDoc().getTipo()); 
			
			XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(responseDoc)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(getDoc().getTipo(), responseDoc, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * nuovo documento da showdoc repertorio (nuovo repertorio)
	 * @return
	 * @throws Exception
	 */
	public String nuovoRepertorio() throws Exception {
		try {
			formsAdapter.insTableDocRep(getDoc().getTipo(), getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
			
			XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(responseDoc)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(getDoc().getTipo(), responseDoc, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String ripetiNuovo() throws Exception{
		if (getDoc().getRepertorio() != null && getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0)
			this.formsAdapter.ripetiNuovoRep(getDoc().getTipo(), "ripetinuovo", getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
		else
			this.formsAdapter.ripetiNuovo(getDoc().getTipo(), "ripetinuovo");
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		String tipoDoc = response.getAttributeValue("/response/doc/@tipo", "");
		return buildSpecificDocEditPageAndReturnNavigationRule(tipoDoc, response, false);
	}
	
	public String removeFromRac() throws Exception{
		Contenuto_in contenuto = (Contenuto_in) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("contenuto");
		this.formsAdapter.removeFromRac(contenuto.getCod(), this.getDoc().getNrecord());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		this.getDoc().getContenuto_in().remove(contenuto);
		return null;
	}

	/**
	 * fascicolazione del documento
	 * @return
	 * @throws Exception
	 */
	public String insInFasc() throws Exception {
		try {
			Rif rif = this.getDoc().getAssegnazioneRPA();
			this.formsAdapter.insInFasc("RPA", this.formsAdapter.getDefaultForm().getParam("physDoc"), rif.getNome_persona(), rif.getNome_uff(), rif.getCod_persona(), rif.getCod_uff(), getDoc().getClassif().getText(), getDoc().getClassif().getCod(), getDoc().getRif_esterni().get(0).getNome(), rif.getTipo_uff());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			String personalDirCliente = response.getAttributeValue("/response/@personalDirCliente");
			if (personalDirCliente != null && personalDirCliente.length() > 0 && personalDirCliente.endsWith("/"))
				personalDirCliente = personalDirCliente.substring(0, personalDirCliente.length()-1); // eliminazione della "/" finale
			return buildSpecificQueryPageAndReturnNavigationRule("@fascicolo", "", personalDirCliente, "", response, true);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * fascicolazione della minuta
	 * @return
	 * @throws Exception
	 */
	public String insInFascMinuta() throws Exception {
		try {
			Rif rif = this.getDoc().getAssegnazioneRPA();
			this.formsAdapter.insInFasc("RPAM", this.formsAdapter.getDefaultForm().getParam("physDoc"), rif.getNome_persona(), rif.getNome_uff(), rif.getCod_persona(), rif.getCod_uff(), getDoc().getMinuta().getClassif().getText(), getDoc().getMinuta().getClassif().getCod(), getDoc().getRif_esterni().get(0).getNome(), rif.getTipo_uff());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			String personalDirCliente = response.getAttributeValue("/response/@personalDirCliente");
			if (personalDirCliente != null && personalDirCliente.length() > 0 && personalDirCliente.endsWith("/"))
				personalDirCliente = personalDirCliente.substring(0, personalDirCliente.length()-1); // eliminazione della "/" finale
			return buildSpecificQueryPageAndReturnNavigationRule("@fascicolo", "", personalDirCliente, "", response, true);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public String ripetiNuovoInFasc() throws Exception{
		if (getDoc().getRepertorio() != null && getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0)
			this.formsAdapter.ripetiNuovoRep(getDoc().getTipo(), "ripetiinfascicolo", getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
		else
			this.formsAdapter.ripetiNuovo(getDoc().getTipo(), "ripetiinfascicolo");
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return buildSpecificDocEditPageAndReturnNavigationRule(getDoc().getTipo(), response, false);
	}
	
	public String nuovoInFasc() throws Exception{
		if (getDoc().getRepertorio() != null && getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0)
			this.formsAdapter.ripetiNuovoRep(getDoc().getTipo(), "nuovoinfascicolo", getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
		else
			this.formsAdapter.ripetiNuovo(getDoc().getTipo(), "nuovoinfascicolo");
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return buildSpecificDocEditPageAndReturnNavigationRule(getDoc().getTipo(), response, false);
	}
	
	public String ripetiInPiuRaccoglitori(){
		viewSelectRaccoglitori = true;
		return null;
	}

	public void setViewSelectRaccoglitori(boolean viewSelectRaccoglitori) {
		this.viewSelectRaccoglitori = viewSelectRaccoglitori;
	}

	public boolean isViewSelectRaccoglitori() {
		return viewSelectRaccoglitori;
	}

	public String closeViewSelectRaccoglitori() {
		this.viewSelectRaccoglitori = false;
		return null;
	}
	
	public String confirmInsRaccoglitori() throws Exception {
		viewSelectRaccoglitori = false;
		String valueChecked = getValuesFromCheckboxList(); 
		if (valueChecked.equals("")) return null;
		String tipoOp = "ripetiinraccoglitore";
		String sep = "|";
		
	   	tipoOp += sep + valueChecked;
	   	
	   	if (getDoc().getRepertorio() != null && getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0)
			this.formsAdapter.ripetiNuovoRep(getDoc().getTipo(), tipoOp, getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
		else
			this.formsAdapter.ripetiNuovo(getDoc().getTipo(), tipoOp);
		
	   	XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return buildSpecificDocEditPageAndReturnNavigationRule(getDoc().getTipo(), response, false);
	}

	public String ripetiInRaccoglitore() throws Exception {
		String tipoOp = "ripetiinraccoglitore";
		String sep = "|";
	   	tipoOp += sep + getDoc().getContenuto_in().get(0).getCod()+sep;
		
	   	if (getDoc().getRepertorio() != null && getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0)
			this.formsAdapter.ripetiNuovoRep(getDoc().getTipo(), tipoOp, getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
		else
			this.formsAdapter.ripetiNuovo(getDoc().getTipo(), tipoOp);
	   	
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return buildSpecificDocEditPageAndReturnNavigationRule(getDoc().getTipo(), response, false);
	}

	public String getValuesFromCheckboxList(){
		String sep = "|";
		String tipo = "";
		for (Iterator<Contenuto_in> iterator = getDoc().getContenuto_in().iterator(); iterator.hasNext();) {
			Contenuto_in contenuto = (Contenuto_in) iterator.next();
			if (contenuto.isCheck()) tipo += contenuto.getCod()+sep;
		}
		return tipo;
	}
	
	public String rispondi() throws Exception {
		boolean cod_fasc = false;
		if (getDoc().getAssegnazioneRPA().getCod_fasc().trim().length()>0) cod_fasc = true;
		if (!cod_fasc || !this.formsAdapter.getFunzionalitaDisponibili().get("ripetiinfascicolo")){
			this.formsAdapter.ripetiNuovo((getDoc().getTipo().equals("arrivo")) ? "partenza" : "arrivo", "ripetinuovo");
		}
		else if (cod_fasc && this.formsAdapter.getFunzionalitaDisponibili().get("ripetiinfascicolo")){
			this.formsAdapter.ripetiNuovo((getDoc().getTipo().equals("arrivo")) ? "partenza" : "arrivo", "ripetiinfascicolo");
		}
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		return buildSpecificDocEditPageAndReturnNavigationRule((getDoc().getTipo().equals("arrivo")) ? "partenza" : "arrivo", response, false);
	}
	
	/**
	 * Inoltro del documento
	 */
	public String inoltraDoc() throws Exception {
		boolean cod_fasc = false;
		
		if (getDoc().getAssegnazioneRPA().getCod_fasc().trim().length()>0) cod_fasc = true;
		if (!cod_fasc || !this.formsAdapter.getFunzionalitaDisponibili().get("ripetiinfascicolo")){
			this.formsAdapter.ripetiNuovo("partenza", "inoltranuovo");
		}
		else if (cod_fasc && this.formsAdapter.getFunzionalitaDisponibili().get("ripetiinfascicolo")){
			this.formsAdapter.ripetiNuovo("partenza", "inoltrainfascicolo");
		}
		
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		return buildSpecificDocEditPageAndReturnNavigationRule("partenza", response, false);
	}
	
	/**
	 * Replica del documento
	 * @return
	 * @throws Exception
	 */
	public String replicaDoc() throws Exception{
		this.formsAdapter.replicaDoc();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		String tipoDoc = response.getAttributeValue("/response/doc/@tipo", "");
		return buildSpecificDocEditPageAndReturnNavigationRule(tipoDoc, response, false);
	}
	
	/**
	 * Trasformazione di un doc non protocollato in protocollo in partenza/tra uffici
	 * @return
	 * @throws Exception
	 */
	public String trasfNonProtocollato() throws Exception {
		try {
			Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String tipoDoc = params.get("tipoDoc");
			
			this.formsAdapter.trasfNonProtocollato(tipoDoc);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// sostituzione del tipo di doc da replicare nell'xml di ritorno (da verie al
			// tipo richiesto nella trasformazione)
			//((Element) response.getRootElement().selectSingleNode("/response/doc")).addAttribute("tipo", tipogetDoc().substring(1)); // substring(1) = viene eliminato il carattere @
			
			return buildSpecificDocEditPageAndReturnNavigationRule(tipoDoc, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Trasformazione di un doc in arrivo in bozza in documento non protocollato
	 * 
	 * @return
	 * @throws Exception
	 */
	public String trasfInV() throws Exception{
		this.formsAdapter.trasfInV();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		String dbTable = response.getAttributeValue("/response/@dbTable");
		if (dbTable.equals("@reload")) {
			getFormsAdapter().fillFormsFromResponse(response);
			super._reload();
			return null;
		}
		else
			return buildSpecificShowdocPageAndReturnNavigationRule("varie", response, false); // si da per scontato che il tipo doc sia non protocollato
	}
	
	/**
	 * cancellazione di un documento
	 * 
	 * @return
	 * @throws Exception
	 */
	public String rimuoviDoc() throws Exception {
		try {
			this.formsAdapter.remove();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return redirectAfterDoceditAction(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * ripristino di un documento da cestino (cancellazione logica)
	 * @return
	 * @throws Exception
	 */
	public String ripristinaDaCestino() throws Exception {
		try {
			this.formsAdapter.ripristinaDaCestino();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return redirectAfterDoceditAction(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * redirect derivante da un'attivita' di docedit (codice comune a removeDoc e ripristinoDaCestino)
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private String redirectAfterDoceditAction(XMLDocumento response) throws Exception {
		String verbo = response.getAttributeValue("/response/@verbo");
		String dbTable = response.getAttributeValue("/response/@dbTable");
		
		if ("@reload".equals(dbTable)) {
			getFormsAdapter().fillFormsFromResponse(response);
			super._reload();
			return null;
		}
		else if ("query".equals(verbo)) {
			String embeddedApp = getEmbeddedApp();
			if (embeddedApp.equals("")) // in caso di applicazione embedded occorre caricare la home specifica dell'applicazione
				return "show@docway_home";
			else
				return "show@" + embeddedApp + "_home";
		}
		else
			return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
	}

	/**
	 * apertura del popup di annullamento del documento
	 * @return
	 * @throws Exception
	 */
	public String annullaDoc() throws Exception{
		this.formsAdapter.annullaDoc();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		DocWayAnnullamentoDoc annullamentoDoc = new DocWayAnnullamentoDoc();
		annullamentoDoc.getFormsAdapter().fillFormsFromResponse(response);
		annullamentoDoc.init(response.getDocument());
		annullamentoDoc.setShowdoc(this);
		setSessionAttribute("docwayAnnullamentoDoc", annullamentoDoc);
		return null;
	}

	/**
	 * inserimento di un link a fascicolo dal documento
	 * @return
	 * @throws Exception
	 */
	public String insLinkFasc() throws Exception {
		Rif rif = this.getDoc().getAssegnazioneRPA();
		if (null == rif)
			this.formsAdapter.insLinkFasc(this.formsAdapter.getDefaultForm().getParam("physDoc"), "", "", "", "", doc.getClassif().getText(), doc.getClassif().getCod(), doc.getRif_esterni().get(0).getNome(), "");
		else 
			this.formsAdapter.insLinkFasc(this.formsAdapter.getDefaultForm().getParam("physDoc"), rif.getNome_persona(), rif.getNome_uff(), rif.getCod_persona(), rif.getCod_uff(), getDoc().getClassif().getText(), getDoc().getClassif().getCod(), getDoc().getRif_esterni().get(0).getNome(), rif.getTipo_uff());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		
		String personalDirCliente = response.getAttributeValue("/response/@personalDirCliente");
		if (personalDirCliente != null && personalDirCliente.length() > 0 && personalDirCliente.endsWith("/"))
			personalDirCliente = personalDirCliente.substring(0, personalDirCliente.length()-1); // eliminazione della "/" finale
		return buildSpecificQueryPageAndReturnNavigationRule("@fascicolo", "", personalDirCliente, "", response, true);
	}
	
	/**
	 * Copia il collegamento al documento
	 * 
	 * @throws Exception
	 */
	public String copyLink() throws Exception {
		return this.copyLink(this.getDoc().getNrecord());
	}
	
	/**
	 * protocollazione di un documento
	 * 
	 * @return
	 * @throws Exception
	 */
	public String protocolla() throws Exception {
		try {
			boolean isRepertorio = false;
			String codRepertorio = "";
			String descrRepertorio = "";
			if (getDoc().getRepertorio() != null 
								&& getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0
								&& getDoc().getRepertorio().getText() != null && getDoc().getRepertorio().getText().length() > 0) {
				isRepertorio = true;
				codRepertorio = getDoc().getRepertorio().getCod();
				descrRepertorio = getDoc().getRepertorio().getText();
			}
			
			this.formsAdapter.protocolla(isRepertorio, "doc", "list_of_doc", codRepertorio, descrRepertorio);
			
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			this.formsAdapter.fillFormsFromResponse(response);
			
			reload();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		return null;
	}
	
	/**
	 * stampa ricevuta del documento
	 * @return
	 * @throws Exception
	 */
	public String stampaRicevuta() throws Exception{
		this.formsAdapter.stampaRicevutaSenzaIW();
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
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		return null;
	}
	
	/**
	 * stampa info del documento
	 * @return
	 * @throws Exception
	 */
	public String stampaInfo() throws Exception{
		this.formsAdapter.stampaSegnaturaSenzaIW(false, "Info");
		
		try {
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				String contentType = "text/html";
				response.setContentType(contentType);
				Logger.debug("ShowdocDoc.stampaInfo(): content-type impostato a " + contentType);
				response.setContentLength(attachFile.getContent().length);
//				response.setHeader("Content-Disposition", "inline; filename=" + attachFile.getFilename());
				ServletOutputStream out;
				out = response.getOutputStream();
				out.write(attachFile.getContent());
				faces.responseComplete();
			}
			else {
				// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
				handleErrorResponse(attachFile.getXmlDocumento());
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		return null;
	}
	
	/**
	 * Redirect ad acquisizione immagini per il documento corrente
	 * @return
	 * @throws Exception
	 */
	public String fotoOriginale() throws Exception {
		try {
			this.formsAdapter.fotoOriginale();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			DocEditAcquisizione docEditAcquisizione = new DocEditAcquisizione();
			docEditAcquisizione.getFormsAdapter().fillFormsFromResponse(response);
			docEditAcquisizione.init(response.getDocument());
			setSessionAttribute("docEditAcquisizione", docEditAcquisizione);
			return "docEdit@acquisizione";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * repertorio "Richiesta Pubblicazione AOL". da doc protocollato generazione della richiesta
	 * di pubblicazione Albo Online
	 *  
	 * @return
	 * @throws Exception
	 */
	public String richiestaPubblicazioneInAlboOnline() throws Exception {
		try {
			formsAdapter.richiestaPubblicazioneInAlboOnline();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// reindirizzamento alla pagina di inserimento di documenti RAOL
			String tipoDoc = response.getAttributeValue("/response/doc/@tipo", "");
			return buildSpecificDocEditPageAndReturnNavigationRule(tipoDoc, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * repertorio "Richiesta Pubblicazione AOL". pubblicazione in albo online (generazione di un doc
	 * varie di repertorio "Albo Online")
	 * 
	 * @return
	 * @throws Exception
	 */
	public String pubblicaInAlboOnline() throws Exception {
		try {
			formsAdapter.pubblicaInAlboOnline();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	
	public String publishWithThePublisher() throws Exception{
		this.formsAdapter.publishWithThePublisher();
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return null;
	}

	/**
	 * Stampa della segnatura del documento (stampa segnatura)
	 * @return
	 * @throws Exception
	 */
	public String stampaSegnatura(boolean save) throws Exception {
		try {
			formsAdapter.stampaSegnatura(save, null);
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				String contentType = "text/html";
				response.setContentType(contentType);
				Logger.debug("ShowdocDoc.stampaSegnatura(): content-type impostato a " + contentType);
				response.setContentLength(attachFile.getContent().length);
				//response.setHeader("Content-Disposition", "attachment; filename=" + attachFile.getFilename());
				ServletOutputStream out;
				out = response.getOutputStream();
				out.write(attachFile.getContent());
				faces.responseComplete();
			}
			else {
				// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
				handleErrorResponse(attachFile.getXmlDocumento());
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		
		return null;
	}
	
	/**
	 * Salvataggio della segnatura del documento (segnatura manuale)
	 * @return
	 * @throws Exception
	 */
	public String salvaSegnatura() throws Exception {
		try {
			formsAdapter.salvaSegnatura();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(response);
			
			reload();
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		return null;
	}
	
	public String submit() throws Exception{
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		this.formsAdapter.fillFormsFromResponse(response);
		this.init(response.getDocument());
		return null;
	}
	
	public String assegnaFascicoloCollegato() throws Exception{
		this.formsAdapter.linkFasc(this.numFascCollegato);
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		reload();
		return null;
	}


	public void setNumFascCollegato(String numFascCollegato) {
		this.numFascCollegato = numFascCollegato;
	}

	public String getNumFascCollegato() {
		return numFascCollegato;
	}
	
	/**
	 * Abbandono del lock su un allegato del documento
	 * @return
	 */
	public String abandonChkout() throws Exception {
		try {
			XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
			
			this.formsAdapter.abandonChkout(file.getName());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			//init(response.getDocument());
			reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Checkout di un allegato del documento
	 * @throws Exception
	 */
	public String checkout() throws Exception {
		try {
			XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
			
			String conversioni = ""; // TODO esistono altri agent da gestire?
			if (file.getAgent_meta() != null && !file.getAgent_meta().equals(""))
				conversioni = conversioni + "meta;";
			if (file.getAgent_pdf() != null && !file.getAgent_pdf().equals(""))
				conversioni = conversioni + "pdf;";
			if (file.getAgent_xml() != null && !file.getAgent_xml().equals(""))
				conversioni = conversioni + "xml;";
			this.formsAdapter.checkout(file.getName(), file.getTitle(), conversioni);
			
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// mbernardini 14/05/2015 : in caso di warnings restituiti dal service (es. verifica impronta), stampo
			// un messaggio a video
			String warnings = response.getAttributeValue("/response/@warnings", "");
			if (warnings.length() > 0) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				setErroreResponse(warnings, Const.MSG_LEVEL_WARNING);
			}
			else {
				formsAdapter.fillFormsFromResponse(response);
				reload();
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Checkin di un allegato del documento
	 * @throws Exception
	 */
	public String checkin() throws Exception {
		try {
			XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
			
			this.formsAdapter.checkin(file.getName(), file.getTitle(), null);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			formsAdapter.getDefaultForm().addParam("toDo", ""); // azzeramento del parametro 'toDo'

			DocWayCheckinAttach checkinAttach = new DocWayCheckinAttach();
			checkinAttach.getFormsAdapter().fillFormsFromResponse(response);
			checkinAttach.init(response.getDocument());
			checkinAttach.setShowdoc(this);
			setSessionAttribute("docwayCheckinAttach", checkinAttach);
			
			//return "doc_response@openChkinPage"; // TODO caso popup classico
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Firma digitale con Uniserv
	 * @throws Exception
	 */
	public String firmaUniserv() throws Exception {
		try {
			this.formsAdapter.firmaUniserv();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				// in caso di errore viene verificato se occorre aggiornare il testo e 
				// il livello dell'errore
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				Errormsg errormsg = (Errormsg) session.getAttribute("errormsg");
				if (errormsg != null && errormsg.getErrore() != null) {
					Errore errore = errormsg.getErrore();
					if (errore != null) {
						if (errore.getHttpStatusCode().equals("500")) {
							errore.setExtra(errore.getErrtype());
							errore.setErrtype(I18N.mrs("dw4.si_e_verificato_un_errore_durante_la_fase_di_consegna_dei_file_da_firmare"));
						}
						errore.setLevel(ErrormsgFormsAdapter.ERROR); // porto il livello di errore da FATAL a ERROR
						errormsg.setErrore(errore);
						session.setAttribute("errormsg", errormsg);
					}
				}
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// recupero dell'url di accesso ad Uniserv
			uniservLink = XMLUtil.parseStrictAttribute(response.getDocument(), "/response/funzionalita_disponibili/@uniservLink");
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Firma digitale con applet Actalis
	 * @return
	 * @throws Exception
	 */
	public String firmaImageAppletActalis() throws Exception {
		XwFile image = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("image");
		return firmaAppletActalis(image, true);
	}
	
	/**
	 * Firma digitale con applet Actalis
	 * @return
	 * @throws Exception
	 */
	public String firmaAppletActalis() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		return firmaAppletActalis(file, false);
	}
	
	/**
	 * Firma digitale con applet Actalis
	 * @return
	 * @throws Exception
	 */
	private String firmaAppletActalis(XwFile file, boolean isImage) throws Exception {
		try {
			if (file != null) {
				this.formsAdapter.firmaAppletActalis(file.getName());
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				
				List<XwFile> files = new ArrayList<XwFile>();
				files.add(file); // aggiunta del file selezionato alla lista file che possono essere firmati
				// aggiunta dei der to del file (conversioni sul file) alla lista dei file che possono essere firmati
				for (int i=0; i<getDoc().getFiles().size(); i++) {
					XwFile derTo = (XwFile) getDoc().getFiles().get(i);
					if (derTo != null 
							&& derTo.getDer_from().equals(file.getName()) 
							&& !derTo.getExtension().equals("txt")) { // si escludono i file TXT
						files.add(derTo);
					}
				}
				for (int i=0; i<getDoc().getImmagini().size(); i++) {
					XwFile derTo = (XwFile) getDoc().getImmagini().get(i);
					if (derTo != null 
							&& derTo.getDer_from().equals(file.getName()) 
							&& !derTo.getExtension().equals("txt")) { // si escludono i file TXT
						files.add(derTo);
					}
				}
				
				DocWayFirmaDigitale docwayFirmaDigitale = new DocWayFirmaDigitale(files, isImage);
				docwayFirmaDigitale.getFormsAdapter().fillFormsFromResponse(response);
				docwayFirmaDigitale.init(response.getDocument());
				docwayFirmaDigitale.setPopupPage(true);
				setSessionAttribute("docwayFirmaDigitale", docwayFirmaDigitale);
				
				return "firmadigitale@actalis";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		return null;
	}
	
	/**
	 * apertura popup di firma digitale con applet 3di
	 */
	public String firmaApplet3di(String outputFileType) throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		try {
			if (file != null) {
				this.formsAdapter.firmaApplet3di(file.getName());
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				
				List<XwFile> files = new ArrayList<XwFile>();
				files.add(file); // aggiunta del file selezionato alla lista file che possono essere firmati
				// aggiunta dei der to del file (conversioni sul file) alla lista dei file che possono essere firmati
				for (int i=0; i<getDoc().getFiles().size(); i++) {
					XwFile derTo = (XwFile) getDoc().getFiles().get(i);
					if (derTo != null 
							&& derTo.getDer_from().equals(file.getName()) 
							&& !derTo.getExtension().equals("txt")) { // si escludono i file TXT
						files.add(derTo);
					}
				}
				for (int i=0; i<getDoc().getImmagini().size(); i++) {
					XwFile derTo = (XwFile) getDoc().getImmagini().get(i);
					if (derTo != null 
							&& derTo.getDer_from().equals(file.getName()) 
							&& !derTo.getExtension().equals("txt")) { // si escludono i file TXT
						files.add(derTo);
					}
				}
				
				DocWayFirmaDigitale docwayFirmaDigitale = new DocWayFirmaDigitale(files, false);
				docwayFirmaDigitale.setOutputFileType(outputFileType);
				docwayFirmaDigitale.getFormsAdapter().fillFormsFromResponse(response);
				docwayFirmaDigitale.init(response.getDocument());
				docwayFirmaDigitale.setPopupPage(true);
				setSessionAttribute("docwayFirmaDigitale", docwayFirmaDigitale);
				
				return "firmadigitale@3di";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		return null;
	}
	
	/**
	 * Firma digitale con Engineering
	 * @throws Exception
	 */
	public String firmaEngineering() throws Exception {
		try {
			this.formsAdapter.firmaEngineering();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			DocWayFirmaDigitaleEngineering docwayFirmaDigitale = new DocWayFirmaDigitaleEngineering();
			docwayFirmaDigitale.getFormsAdapter().fillFormsFromResponse(response);
			docwayFirmaDigitale.init(response.getDocument());
			docwayFirmaDigitale.setPopupPage(true);
			setSessionAttribute("docwayFirmaDigitaleEngineering", docwayFirmaDigitale);
			
			return "firmadigitale@engineering";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Cambia l'immagine corrente caricata sul documento (selezione immagini da IWX)
	 * @param e nuovo valore associato alla proprieta' 'showIwxSelectedImage'
	 */
	public void changeCurrentImage(ValueChangeEvent e) {
		getDoc().setShowIwxSelectedImage(e.getNewValue().toString());
	}

	/**
	 * Invio telematico a destinatari multipli
	 * 
	 * @throws Exception
	 */
	public String invioTelematicoPECMultiplo() throws Exception {
		try {
			formsAdapter.invioTelematicoPECMultiplo();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			return generaRitornoInvioTelematico(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Invio telematico a singolo destinatario
	 * 
	 * @throws Exception
	 */
	public String invioTelematico() throws Exception {
		try {
			RifEsterno rifEsterno = (RifEsterno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rifEsterno");
			if (rifEsterno != null) {
				int pos = new Integer(rifEsterno.getIdx()).intValue();
				
				formsAdapter.invioTelematico(pos);
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				return generaRitornoInvioTelematico(response);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Invio telematico tramite web service
	 * @return
	 * @throws Exception
	 */
	public String invioTelematicoWS() throws Exception {
		try {
			RifEsterno rifEsterno = (RifEsterno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rifEsterno");
			if (rifEsterno != null) {
				int pos = new Integer(rifEsterno.getIdx()).intValue();
				
				formsAdapter.invioTelematico(pos);
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				return generaRitornoInvioTelematico(response);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Gestione del ritorno di un operazione di invio telematico
	 * 
	 * @param response Response dell'attivita' di invio telematico
	 * @return
	 * @throws Exception
	 */
	private String generaRitornoInvioTelematico(XMLDocumento response) throws Exception {
		if (handleErrorResponse(response)) {
			// in caso di errore viene verificato se occorre aggiornare il testo e 
			// il livello dell'errore
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			Errormsg errormsg = (Errormsg) session.getAttribute("errormsg");
			if (errormsg != null && errormsg.getErrore() != null) {
				Errore errore = errormsg.getErrore();
				if (errore != null) {
					if (errore.getErrtype().contains(Const.RITORNO_ESITO_INVIO_TELEMATICO_SU_DOC_SENZA_ALLEGATI)) {
						errore.setExtra(""); // elimino i dettagli dell'eccezione in modo da non mostrarli all'operatore (errore gestito)
						errore.setLevel(ErrormsgFormsAdapter.WARNING);
					}
					else {
						errore.setLevel(ErrormsgFormsAdapter.ERROR); // porto il livello di errore da FATAL a ERROR
					}
					errormsg.setErrore(errore);
					session.setAttribute("errormsg", errormsg);
				}
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		// lettura del messaggio di ritorno
		ReloadMsg message = new ReloadMsg();
		message.setActive(true);
		message.init(response.getDocument());
		message.setLevel(Const.MSG_LEVEL_SUCCESS);
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		
		reload(); // reload del documento
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("reloadmsg", message);
		
		return null;
	}
	
	/**
	 * Aggiornamento dell'indirizzo PEC di un destinatario di un documento in partenza
	 * 
	 * @throws Exception
	 */
	public String updatePEC() throws Exception {
		try {
			RifEsterno rifEsterno = (RifEsterno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rifEsterno");
			if (rifEsterno != null) {
				String cod = rifEsterno.getCod();
				String ref_cod = "";
				if (rifEsterno.getReferente() != null && rifEsterno.getReferente().getCod() != null)
					ref_cod = rifEsterno.getReferente().getCod();
				
				if (cod == null || cod.length() == 0) {
					this.setErroreResponse(I18N.mrs("dw4.impossibile_procedere_con_l_operazione_Il_destinatario_selezionato_e_privo_del_codice_identificativo"), Const.MSG_LEVEL_WARNING);
					return null;
				}
				
				formsAdapter.updatePEC(cod, ref_cod);
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				
				// lettura del messaggio di ritorno
				ReloadMsg message = new ReloadMsg();
				message.setActive(true);
				message.init(response.getDocument());
				message.setLevel(Const.MSG_LEVEL_SUCCESS);
				
				reload(); // reload del documento
				
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				session.setAttribute("reloadmsg", message);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Apertura in popup delle immagini del documento
	 * 
	 * @return
	 * @throws Exception
	 */
	public String mostraImmaginiDocumento() throws Exception {
		try {
			formsAdapter.mostraImmaginiDocumento();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			ShowdocImmagini showdocImmagini = new ShowdocImmagini();
			showdocImmagini.getFormsAdapter().fillFormsFromResponse(response);
			showdocImmagini.init(response.getDocument());
			showdocImmagini.setPopupPage(true);
			//showdocImmagini.setShowdoc(this);
			setSessionAttribute("showdocImmagini", showdocImmagini);
			
			return "showdoc@immagini";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	

	/**
	 * aggancio di un documento ad un workflow (apertura
	 * del popup )
	 * 
	 * @throws Exception
	 */
	public String agganciaWorkflow() throws Exception {
		try {
			getFormsAdapter().agganciaWorkflow();
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
						
			QueryWorkflow queryWorkflow = new QueryWorkflow();
			queryWorkflow.setPopupPage(true);
			queryWorkflow.setNrecord(getDoc().getNrecord());
			queryWorkflow.getFormsAdapter().fillFormsFromResponse(response);
			queryWorkflow.init(response.getDocument());
			setSessionAttribute("queryWorkflow", queryWorkflow);
			
			return "query@workflow";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;	
		}
	}
	
	/**
	 * aggancio di un documento ad un workflow (apertura
	 * del popup )
	 * 
	 * @throws Exception
	 */
	public String avviaWorkflow() throws Exception {
		try {
			TitoloWorkflow titoloWorkflow = (TitoloWorkflow) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflow");
			
			formsAdapter.doAssignWorkflow(titoloWorkflow.getName(), titoloWorkflow.getVersion(), getDoc().getNrecord());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			_reloadWithoutNavigationRule();
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Annullamento di un workflow di Bonita
	 * @return
	 * @throws Exception
	 */
	public String cancelWorkflowInstance() throws Exception {
		try {
			WorkflowInstance wfInstance = (WorkflowInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflowInstance");
			
			formsAdapter.cancelWorkflowInstance(wfInstance.getId());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			reload();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Condivisione dei files del documento su directory remota (percorso di rete specificato
	 * su file di properties di docway-service)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String condividiFilesSuDirRemota() throws Exception {
		try {
			formsAdapter.condividiFilesSuDirRemota();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			// lettura del messaggio di ritorno
			Msg message = new Msg();
			message.setActive(true);
			message.init(response.getDocument());
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			session.setAttribute("msg", message);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * trasformazione del documento corrente in repertorio
	 * 
	 * @return
	 * @throws Exception
	 */
	public String trasformaInRep() throws Exception {
		try {
			TitoloRepertorio repertorio = (TitoloRepertorio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("repertorio");
			if (repertorio != null) {
				formsAdapter.trasformaInRep(repertorio.getCodice());
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				// reload del documento
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				
				// lettura del messaggio di ritorno
				ReloadMsg message = new ReloadMsg();
				message.init(response.getDocument());
				if (message.getMessage() != null && message.getMessage().length() > 0) {
					message.setActive(true);
					message.setLevel(Const.MSG_LEVEL_WARNING);
					HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
					session.setAttribute("reloadmsg", message);
				}
				else {
					// trasformazione in repertorio eseguita con successo
					_reload();
				}
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * replica del documento corrente (solo non protocollato gia' repertoriato) in un doc non protocollato su 
	 * altro repertorio
	 * 
	 * @return
	 * @throws Exception
	 */
	public String replicaInRep() throws Exception {
		try {
			TitoloRepertorio repertorio = (TitoloRepertorio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("repertorio");
			if (repertorio != null) {
				this.formsAdapter.replicaDoc(repertorio.getCodice(), repertorio.getDescrizione());
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				String tipoDoc = response.getAttributeValue("/response/doc/@tipo", "");
				return buildSpecificDocEditPageAndReturnNavigationRule(tipoDoc, response, false);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	// dpranteda 18/03/2015 - pubblicazione albo esterno
	/**
	 * pubblica il documento su albo on-line esterno
	 * @throws Exception 
	 * */
	public String pubblicaAlboExt() throws Exception{
		DocWayAlboExt alboExt = new DocWayAlboExt();
		alboExt.getFormsAdapter().fillFormsFromResponse(formsAdapter.getLastResponse());
		alboExt.setShowdoc(this);
		setSessionAttribute("docwayAlboExt", alboExt);
		return null;
	}
	
	/**
	 * rimuove il documento dalla pubblicazione su albo on-line esterno
	 * N.B. se non e' gia'  passato il bridge che ha modificato il valore di /albo_ext/@stato
	 * @throws Exception 
	 * */
	public String rimuoviAlboExt() throws Exception{
		try {
				this.formsAdapter.rimuoviAlboExt();
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				//reload del documento
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				
				// lettura del messaggio di ritorno
				ReloadMsg message = new ReloadMsg();
				message.setActive(true);
				message.init(response.getDocument());
				message.setLevel(Const.MSG_LEVEL_SUCCESS);
				
				_reloadWithoutNavigationRule();
				
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				session.setAttribute("reloadmsg", message);
				return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Delega di un task di un workflow di Bonita
	 * @return
	 * @throws Exception
	 */
	//dpranteda 22/04/2015 - delega task del workflow
	public String delegaWorkflow() throws Exception {
		try {
			//Workflow cliccato
			WorkflowInstance wfInstance = (WorkflowInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("workflowInstance");
			
			//parametri da passare al bean del popup
			Map<String, PersonaInRuolo> users = new TreeMap<String, PersonaInRuolo>();//uso la Map in modo tale da non avere doppioni
			ArrayList<Task> tasks = new ArrayList<Task>();
			
			//questo metodo analizza i rif interni e mi restituisce gli aventi diritto di intervento(lista utenti, codici uffici, codici ruolo)
			Object[] params = getListFromRifInt();
			
			users = (TreeMap<String,PersonaInRuolo>)params[0];
			String listCodUff = (String)params[1];
			String listRuoli = (String)params[2];
			
			//chiamata al service che restituisce la lista dei task ready e le persone appartenenti agli uffici e/o ruoli
			XMLDocumento delegaWF = getReadyTaskListAndUserListForDelega(wfInstance.getId(), listCodUff, listRuoli);
			if(delegaWF != null){
				//parsing dei task
				List<Element> taskList = delegaWF.selectNodes("//Tasks/Instance/Task");
				for(Element task : taskList){
					Task t = new Task();
					Document dom = DocumentHelper.createDocument(task.createCopy());
					t.init(dom);
					tasks.add(t);
				}
				
				//parsing delle persone
				List<Element> persone = delegaWF.selectNodes("//persone/persona");
				for(Element persona : persone){
					PersonaInRuolo p = new PersonaInRuolo();
					Document dom = DocumentHelper.createDocument(persona.createCopy());
					p.init(dom);
					users.put(p.getMatricola(), p);
				}
				
				if(tasks.isEmpty()){
					ReloadMsg message = new ReloadMsg();
					message.setActive(true);
					message.setTitle(I18N.mrs("dw4.impossibile_delegare_azione"));
					message.setMessage(I18N.mrs("dw4.nessun_task_ready_presente"));
					message.setLevel(Const.MSG_LEVEL_WARNING);
					_reloadWithoutNavigationRule();
					
					HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
					session.setAttribute("reloadmsg", message);
					return null;
				}else if(users.isEmpty()){
					ReloadMsg message = new ReloadMsg();
					message.setActive(true);
					message.setTitle(I18N.mrs("dw4.impossibile_delegare_azione"));
					message.setMessage(I18N.mrs("dw4.nessun_utente_da_delegare"));
					message.setLevel(Const.MSG_LEVEL_WARNING);
					_reloadWithoutNavigationRule();
					
					HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
					session.setAttribute("reloadmsg", message);
					return null;
				}
				
				DocWayDelegaWorkflow delegaWFBean = new DocWayDelegaWorkflow();
				delegaWFBean.init(users, tasks);
				delegaWFBean.getFormsAdapter().fillFormsFromResponse(formsAdapter.getLastResponse());
				delegaWFBean.setShowdoc(this);
				setSessionAttribute("delegaWF", delegaWFBean);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Chiamata al service che restutuisce XML contenente i task 'ready' e la lista di utenti che possono essere delegati
	 * @param wfInstanceId - Id dell'istanza del flusso
	 * @param listCodUff - lista dei codici ufficio (serve per restituire tutte le persone appartanenti a quel ufficio)
	 * @param listRuoli - lista dei codici Ruoli (serve per restituire tutte le persone appartanenti a quel ruolo)
	 * @return XMLDocumento
	 * @throws Exception
	 * */
	private XMLDocumento getReadyTaskListAndUserListForDelega(String wfInstanceId, String listCodUff, String listRuoli) throws Exception{
		try{
			formsAdapter.getReadyTaskListAndUserListForDelega(wfInstanceId, listCodUff, listRuoli);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return response;
		}catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
	/**
	 * Analizza i rif interni e mi restituisce gli aventi diritto di intervento
	 * @return params params[0]=lista utenti, params[1]=codici uffici, params[2]=codici ruolo
	 * */
	private Object[] getListFromRifInt(){	
		Map<String, PersonaInRuolo> users = new TreeMap<String, PersonaInRuolo>();
		Set<String> listCodUff = new TreeSet<String>();
		Set<String> listRuoli = new TreeSet<String>();
		
		Rif rpa 		= doc.getAssegnazioneRPA();
		Rif rpam 		= doc.getAssegnazioneRPAM();
		Rif op			= doc.getAssegnazioneOP();
		Rif opm 		= doc.getAssegnazioneOPM();
		List<Rif> cc 	= doc.getAssegnazioneCC();
		List<Rif> cds 	= doc.getAssegnazioneCDS();
		
		/*	RPA - OP - RPAM - OPM : hanno sempre diritto di intervento e quindi li aggiungo
		 * 	CC - CDS : e' necessario controllare il diritto di intervento
		 * */
		
		//RPA
		if(!rpa.isEmpty()){
			if(!rpa.isUfficio_completo()){
				PersonaInRuolo p = new PersonaInRuolo();
				p.setMatricola(rpa.getCod_persona());
				p.setNome(rpa.getNome_persona());
				p.setCodUff(rpa.getCod_uff());
				users.put(p.getMatricola(), p);
			}
			else if(rpa.getTipo_uff().equals("ruolo")){
				listRuoli.add(rpa.getCod_uff());
			}else{
				listCodUff.add(rpa.getCod_uff());
			}
		}
		
		//OP
		if(!op.isEmpty()){
			if(!op.isUfficio_completo())
			{
				PersonaInRuolo p = new PersonaInRuolo();
				p.setMatricola(op.getCod_persona());
				p.setNome(op.getNome_persona());
				p.setCodUff(op.getCod_uff());
				users.put(p.getMatricola(), p);
			}
			else if(op.getTipo_uff().equals("ruolo")){
				listRuoli.add(op.getCod_uff());
			}else{
				listCodUff.add(op.getCod_uff());
			}
		}
		
		//RPAM
		if(!rpam.isEmpty()){
			if(!rpam.isUfficio_completo())
			{
				PersonaInRuolo p = new PersonaInRuolo();
				p.setMatricola(rpam.getCod_persona());
				p.setNome(rpam.getNome_persona());
				p.setCodUff(rpam.getCod_uff());
				users.put(p.getMatricola(), p);
			}
			else if(rpam.getTipo_uff().equals("ruolo")){
				listRuoli.add(rpam.getCod_uff());
			}else{
				listCodUff.add(rpam.getCod_uff());
			}
		}
		
		//OPM
		if(!opm.isEmpty()){
			if(!opm.isUfficio_completo())
			{
				PersonaInRuolo p = new PersonaInRuolo();
				p.setMatricola(opm.getCod_persona());
				p.setNome(opm.getNome_persona());
				p.setCodUff(opm.getCod_uff());
				users.put(p.getMatricola(), p);
			}
			else if(opm.getTipo_uff().equals("ruolo")){
				listRuoli.add(opm.getCod_uff());
			}else{
				listCodUff.add(opm.getCod_uff());
			}
		}
		
		//CC
		if(cc.size() > 0){
			for(Rif rif : cc){
				if(rif.isIntervento()){
					if(!rif.isUfficio_completo())
					{
						PersonaInRuolo p = new PersonaInRuolo();
						p.setMatricola(rif.getCod_persona());
						p.setNome(rif.getNome_persona());
						p.setCodUff(rif.getCod_uff());
						users.put(p.getMatricola(), p);
					}
					else if(rif.getTipo_uff().equals("ruolo")){
						listRuoli.add(rif.getCod_uff());
					}else{
						listCodUff.add(rif.getCod_uff());
					}
				}
			}
		}
		
		//CDS
		if(cds.size() > 0){
			for(Rif rif : cc){
				if(rif.isIntervento()){
					if(!rif.isUfficio_completo())
					{
						PersonaInRuolo p = new PersonaInRuolo();
						p.setMatricola(rif.getCod_persona());
						p.setNome(rif.getNome_persona());
						p.setCodUff(rif.getCod_uff());
						users.put(p.getMatricola(), p);
					}
					else if(rif.getTipo_uff().equals("ruolo")){
						listRuoli.add(rif.getCod_uff());
					}else{
						listCodUff.add(rif.getCod_uff());
					}
				}
			}
		}
			
		Object[] params = new Object[3];
		params[0] = users;
		params[1] = listCodUff.toString();
		params[2] = listRuoli.toString();
		
		return params;
	}
}
