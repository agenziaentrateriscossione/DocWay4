package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Contenuto_in;
import it.tredi.dw4.docway.model.History;
import it.tredi.dw4.docway.model.Raccoglitore;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.docway.model.Storia;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.DateConverter;
import it.tredi.dw4.utils.Logger;

public class ShowdocRaccoglitore extends DocWayShowdoc {
	private DocDocWayDocumentFormsAdapter formsAdapter;
	private String xml;
	private String view;
	
	private Raccoglitore raccoglitore;
	private boolean close;
	
	private String linkToDoc = ""; // url di accesso al documento corrente (per copia in clipboard e invio mail di notifica). ATTUALMENTE NON UTILIZZATO IN RACCOGLITORE
	
	public ShowdocRaccoglitore() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}
	
	public void setRaccoglitore(Raccoglitore raccoglitore) {
		this.raccoglitore = raccoglitore;
	}

	public Raccoglitore getRaccoglitore() {
		return raccoglitore;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	public boolean isClose() {
		return close;
	}
	
	public String getLinkToDoc() {
		return linkToDoc;
	}

	public void setLinkToDoc(String linkToDoc) {
		this.linkToDoc = linkToDoc;
	}
	
	public DocDocWayDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getPhysDoc_infasc(){
		return this.getFormsAdapter().getDefaultForm().getParam("physDoc_infasc");
	}

	public String getbAssegnaLinkFasc(){
		return this.getFormsAdapter().getDefaultForm().getParam("bAssegnaLinkFasc");
	}
	
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		raccoglitore = new Raccoglitore();
		raccoglitore.init(dom);
		//orderHistory("");
		
		setCurrentPage(getFormsAdapter().getCurrent()+"");
		
		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(dom, "raccoglitore");
	}
	
	@Override
	public void reload() throws Exception {
		super._reload("showdoc@raccoglitore");
	}
	
	@Override
	public String paginaTitoli() throws Exception {
		try {
			XMLDocumento response = this._paginaTitoli();	
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			DocWayTitles titles = new DocWayTitles();		
			titles.getFormsAdapter().fillFormsFromResponse(response);
			titles.init(response.getDocument());
			titles.setPopupPage(isPopupPage());
			setSessionAttribute("docwayTitles", titles);
			return "showtitles@raccoglitore";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Apertura della pagina di modifica del raccoglitore
	 */
	@Override
	public String modifyTableDoc() throws Exception {
		try {
			formsAdapter.modifyTableDoc();
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Creazione di un nuovo raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String nuovoRaccoglitore() throws Exception {
		try {
			formsAdapter.insTableDoc("raccoglitore"); 
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Creazione di un nuovo raccoglitore come ripetizione del corrente
	 * @return
	 * @throws Exception
	 */
	public String ripetiNuovoRaccoglitore() throws Exception {
		try {
			formsAdapter.ripetiNuovo("raccoglitore", "ripetinuovo");
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String getData_assegRPA(){
		Rif rif = this.raccoglitore.getAssegnazioneRPA();
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = raccoglitore.getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona)){
				data = element.getData();
			}
			
		}
		return data;
	}
	
	public String getInfoAssegnazione(Rif rif){
		String data = "";
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = raccoglitore.getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona)){
				data = I18N.mrs("dw4.assegnato_da") + " " + element.getOperatore();
			}
			
		}
		return data;
	}
	
	public String getInfoAssegnazioneRPA(){
		Rif rif = this.raccoglitore.getAssegnazioneRPA();
		return getInfoAssegnazione(rif);
	}
	
	public String getCheckVisto(Rif rif){
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		String nome_persona = rif.getNome_persona();
		List<Storia> storia = raccoglitore.getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			if(element.getTipo().equals(tipo) && element.getNome_persona().equals(nome_persona) && null!=element.getVisto_da() && element.getVisto_da().trim().length() > 0){
				data = "Visto da "+element.getVisto_da()+" il "+(new DateConverter().getAsString(null, null, element.getData_visto()))+ " alle "+element.getOra_visto();
			}
			
		}
		return data;
	}
	
	public String getCheckVistoRPA(){
		Rif rif = this.raccoglitore.getAssegnazioneRPA();
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
		raccoglitore.setHistory(new ArrayList<History>());
		return null;
	}
	
	public String orderHistory(String order) {
		try {
			this.formsAdapter.showHistory(order);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			raccoglitore.initHistory(response.getDocument());
			this.view = response.getAttributeValue("/response/@view", "");
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * aggiunta di un RPA al raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String addRPA() throws Exception {
		try {
			this.formsAdapter.openRifInt("RPA", false);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			DocWayDocRifInt rifInt = new DocWayDocRifInt();
			rifInt.getFormsAdapter().fillFormsFromResponse(response);
			rifInt.init(response.getDocument());
			rifInt.setViewAddRPA(true);
			rifInt.setShowdoc(this);
			setSessionAttribute("rifInt", rifInt);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * assegnazione di piu' raccoglitori ad un documento/fascicolo (non ad altri raccoglitori)
	 * @return
	 * @throws Exception
	 */
	public String doAssignRacAll() throws Exception {
		try {
			String physDoc = this.formsAdapter.getDefaultForm().getParam("physDoc");
			this.formsAdapter.doAssignRacAll(physDoc);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			getUserBean().setServiceFormParam("klRac", null);
			getUserBean().setServiceFormParam("keylist", null);
			getUserBean().setServiceFormParam("selRac", null);
			
			if (response.getAttributeValue("/response/@xverb", "").equals("_CLOSE_"))
				close = true;
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}	

	/**
	 * assegnazione di un raccoglitore ad un documento/fascicolo/altro raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String doAssignRac() throws Exception {
		try {
			String physDoc_doc = this.formsAdapter.getDefaultForm().getParam("physDoc");
			String physDoc_fasc = this.formsAdapter.getDefaultForm().getParam("physDoc_infasc");
			this.formsAdapter.doAssignRac(physDoc_fasc, physDoc_doc);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			if (response.getAttributeValue("/response/@xverb", "").equals("_CLOSE_"))
				close = true;
			
			// in caso di assegnazione di un raccoglitore ad un altro raccoglitore occorre: 
			// 1) impostare a false il popup;
			// 2) forzare il reload della pagina chiamante.
			// queste attivita' non provocano problemi se lanciate su doc o fascicoli, ma risultano necessarie
			// sui raccoglitori perche' la selezione precedente di raccoglitori non e' piu' in sessione
			// visto che e' stata sovrascritta da quella relativa alla ricerca del raccoglitore da agganciare
			this.setPopupPage(false);
			this.reload();
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	/**
	 * caricamento del contenuto del raccoglitore (documenti/fascicoli)
	 * @return
	 * @throws Exception
	 */
	public String raccontent() throws Exception{
		try{
			this.formsAdapter.racContent(this.raccoglitore.getNrecord(), this.raccoglitore.getAssegnazioneRPA().getNome_persona(), this.raccoglitore.getAssegnazioneRPA().getNome_uff(), this.raccoglitore.getOggetto());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			DocWayTitles titles = new DocWayTitles();
			titles.getFormsAdapter().fillFormsFromResponse(response);
			
			titles.init(response.getDocument());
			
			setSessionAttribute("docwayTitles", titles);
			return "showtitles@docway";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	/**
	 * chiusura del popup
	 * @return
	 */
	public String closeWindow(){
		close = true;
		return null;
	}
	
	/**
	 * Copia il collegamento al raccoglitore
	 * 
	 * @throws Exception
	 */
	public String copyLink() throws Exception {
		return this.copyLink(this.raccoglitore.getNrecord());
	}
	
	/**
	 * archiviazione del raccoglitore (chiusura)
	 * @return
	 * @throws Exception
	 */
	public String archiviaRaccoglitore() throws Exception {
		try {
			this.formsAdapter.archiviaRaccoglitore();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			this.formsAdapter.fillFormsFromResponse(response);
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
	 * apertura del raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String apriRaccoglitore() throws Exception {
		try {
			this.formsAdapter.apriRaccoglitore();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			this.formsAdapter.fillFormsFromResponse(response);
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
	 * cancellazione dal raccoglitore
	 * 
	 * @return
	 * @throws Exception
	 */
	public String removeFromRac() throws Exception{
		try{
			Contenuto_in contenuto = (Contenuto_in) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("contenuto");
			this.formsAdapter.removeFromRac(contenuto.getCod(), raccoglitore.getNrecord());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			raccoglitore.getContenuto_in().remove(contenuto);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
}
