package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Contenuto_in;
import it.tredi.dw4.docway.model.Fascicolo;
import it.tredi.dw4.docway.model.GerarchiaFascicolo;
import it.tredi.dw4.docway.model.History;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.docway.model.Storia;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateConverter;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class ShowdocFascicolo extends DocWayShowdoc {
	
	private DocDocWayDocumentFormsAdapter formsAdapter;
	private String xml;
	private String view;
	private Fascicolo fascicolo;
	private boolean close = false;
	private String numFasc = "";
	private String action;
	
	private String linkToDoc = ""; // url di accesso al documento corrente (per copia in clipboard e invio mail di notifica). ATTUALMENTE NON UTILIZZATO IN FASCICOLO
	
	private String personalDirCliente = ""; // aggiunta di campi personalizzati specifici per il cliente
	
	private String archivioDepositoUrl = ""; // url di accesso all'archivio di deposito
	
	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}
	
	public String getLinkToDoc() {
		return linkToDoc;
	}

	public void setLinkToDoc(String linkToDoc) {
		this.linkToDoc = linkToDoc;
	}

	public String getPersonalDirCliente() {
		return personalDirCliente;
	}

	public void setPersonalDirCliente(String personalDirCliente) {
		this.personalDirCliente = personalDirCliente;
	}

	public void setFascicolo(Fascicolo fascicolo) {
		this.fascicolo = fascicolo;
	}

	public Fascicolo getFascicolo() {
		return fascicolo;
	}
	
	public String getArchivioDepositoUrl() {
		return archivioDepositoUrl;
	}

	public void setArchivioDepositoUrl(String archivioDepositoUrl) {
		this.archivioDepositoUrl = archivioDepositoUrl;
	}
	
	public ShowdocFascicolo() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public DocDocWayDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		fascicolo = new Fascicolo();
		fascicolo.init(dom);
		//orderHistory("");
		
		setRaggruppaCC_statoIniziale(XMLUtil.parseStrictAttribute(dom, "/response/funzionalita_disponibili/@raggruppaCC_statoIniziale"));
		
		setCurrentPage(getFormsAdapter().getCurrent()+"");
		
		//tiommi 05/03/2018 richiesta di url di accesso per fascicoli (per copia link e invio notifica)
		if (fascicolo != null && fascicolo.getNrecord() != null && !fascicolo.getNrecord().isEmpty())
			linkToDoc = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/loaddoc.pf?db=" + formsAdapter.getDb() + "&alias=fasc_nrecord&value=" + fascicolo.getNrecord();
		
		// porzione di template personalizzato per il cliente
		personalDirCliente = XMLUtil.parseStrictAttribute(dom, "/response/@personalDirCliente");
		
		// gestione doc2attachment
		setDoc2attachment(XMLUtil.parseStrictAttribute(dom, "/response/@doc2attachment", ""));
		
		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(dom, "fascicolo");
		
		archivioDepositoUrl = XMLUtil.parseStrictAttribute(dom, "/response/funzionalita_disponibili/@depositoUrl", "");
		
		// inizializzazione di componenti common
		initCommons(dom);
	}
	
	@Override
	public String paginaTitoli() throws Exception {
		return this._paginaTitoli("showtitles@fascicolo");
	}

	@Override
	public void reload() throws Exception {
		try{
			super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/showdoc@fascicolo");
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
	}

	@Override
	public String modifyTableDoc() throws Exception {
		try{
			// fcappelli 20150109 - aggiunta la gestione della tipologia fascicoli anche in modifica
			formsAdapter.getDefaultForm().addParam("codice_fasc", fascicolo.getCodiceFascicoloCustom());
			formsAdapter.getDefaultForm().addParam("descrizione_fasc", fascicolo.getDescrizioneFascicoloCustom());
			
			// mbernardini 21/05/2018 : aggiunta del parametro 'fasc_numero_sottofasc' per correggere il template di modifica di fascicoli custom
			if (fascicolo.getNumero() != null && !fascicolo.getNumero().isEmpty())
				formsAdapter.getDefaultForm().addParam("fasc_numero_sottofasc", fascicolo.getNumero());
			
			XMLDocumento response = super._modifyTableDoc();
			return buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String removeFromRac() throws Exception{
		try{
			Contenuto_in contenuto = (Contenuto_in) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("contenuto");
			this.formsAdapter.removeFromRac(contenuto.getCod(), this.fascicolo.getNrecord());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			this.fascicolo.getContenuto_in().remove(contenuto);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String removeRifInt() throws Exception{
		try{
			Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
			this.formsAdapter.removeRifInt(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			this.fascicolo.getRif_interni().remove(rif);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
			
	public String getData_asseg_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		String data = "";
		List<Storia> storia = fascicolo.getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			
			// mbernardini 29/09/2017 : corretto bug in recupero info di assegnazione.
			// non e' sufficiente verificare il nome della persona, occorre controllare anche l'ufficio (potrebbe essere stata aggiunta in CC
			// la stessa persona piu' volte con associato l'ufficio di appartenenza o responsabilita')
			if(element.getTipo().equals("assegnazione_cc") && element.getNome_uff().equals(rif.getNome_uff()) && element.getNome_persona().equals(rif.getNome_persona())){
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
		List<Storia> storia = fascicolo.getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			
			// mbernardini 29/09/2017 : corretto bug in recupero info di assegnazione.
			// non e' sufficiente verificare il nome della persona, occorre controllare anche l'ufficio (potrebbe essere stata aggiunta in CC
			// la stessa persona piu' volte con associato l'ufficio di appartenenza o responsabilita')
			if(element.getTipo().equals(tipo) && element.getNome_uff().equals(rif.getNome_uff()) && element.getNome_persona().equals(rif.getNome_persona())){
				data = I18N.mrs("dw4.assegnato_da") + " " + element.getOperatore();
			}
			
		}
		return data;
	}
	
	public String getInfoAssegnazioneCC() {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		String data = "";
		List<Storia> storia = fascicolo.getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			
			// mbernardini 29/09/2017 : corretto bug in recupero info di assegnazione.
			// non e' sufficiente verificare il nome della persona, occorre controllare anche l'ufficio (potrebbe essere stata aggiunta in CC
			// la stessa persona piu' volte con associato l'ufficio di appartenenza o responsabilita')
			if(element.getTipo().equals("assegnazione_cc") && element.getNome_uff().equals(rif.getNome_uff()) && element.getNome_persona().equals(rif.getNome_persona())){
				data = I18N.mrs("dw4.assegnato_da") + " " + element.getOperatore();
			}
			
		}
		return data;
	}
	
	public String getInfoAssegnazioneITF() {
		Rif rif = this.fascicolo.getAssegnazioneITF();
		String data = "";
		List<Storia> storia = fascicolo.getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			
			// mbernardini 29/09/2017 : corretto bug in recupero info di assegnazione.
			// non e' sufficiente verificare il nome della persona, occorre controllare anche l'ufficio (potrebbe essere stata aggiunta in CC
			// la stessa persona piu' volte con associato l'ufficio di appartenenza o responsabilita')
			if(element.getTipo().equals("incaricato_tenuta_fascicolo") && element.getNome_uff().equals(rif.getNome_uff()) && element.getNome_persona().equals(rif.getNome_persona())){
				data = I18N.mrs("dw4.assegnato_da") + " " + element.getOperatore();
			}
			
		}
		return data;
	}
	
	public String getCheckVistoCC(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
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
		fascicolo.setHistory(new ArrayList<History>());
		return null;
	}

	public String orderHistory(String order) {
		try {
			this.formsAdapter.showHistory(order);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			fascicolo.initHistory(response.getDocument());
			this.view = response.getAttributeValue("/response/@view", "");
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
		}
		catch (Throwable t) {
			try {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			} catch (Exception e) {}
			return null;			
		}
		return null;
	}
	
	public String orderTipo(){
		return orderHistory("@tipo");
	}
	public String orderPersonaUfficio(){
		return orderHistory("@nome_persona");
	}
	public String orderOperatore(){
		return orderHistory("@operatore");
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getView() {
		return view;
	}
	
	/**
	 * caricamento dei documenti contenuti nel fascicolo
	 * @return
	 * @throws Exception
	 */
	public String fascContent() throws Exception{
		try{
			String nome_persona = "";
			String nome_uff = "";
			for (Iterator<Rif> iterator = fascicolo.getRif_interni().iterator(); iterator.hasNext();) {
				Rif rif_interno = (Rif) iterator.next();
				if (rif_interno.getDiritto().equals("RPA")) {
					nome_persona = rif_interno.getNome_persona();
					nome_uff = rif_interno.getNome_uff();
				}
			}
			this.formsAdapter.fascContent(fascicolo.getNumero(), nome_persona, nome_uff, fascicolo.getClassif().getText(), fascicolo.getOggetto());
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
	
	public String getData_assegRPA(){
		Rif rif = this.fascicolo.getAssegnazioneRPA();
		return getData_asseg(rif);
	}

	
	public String getData_assegITF(){
		Rif rif = this.fascicolo.getAssegnazioneITF();
		return getData_asseg(rif);
	}

	public String getData_asseg(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		return getData_asseg(rif);
	}

	public String getData_asseg(Rif rif){
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		List<Storia> storia = fascicolo.getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			
			// mbernardini 29/09/2017 : corretto bug in recupero data di assegnazione.
			// non e' sufficiente verificare il nome della persona, occorre controllare anche l'ufficio (potrebbe essere stata aggiunta in CC
			// la stessa persona piu' volte con associato l'ufficio di appartenenza o responsabilita')
			if(element.getTipo().equals(tipo) && element.getNome_uff().equals(rif.getNome_uff()) && element.getNome_persona().equals(rif.getNome_persona())){
				data = element.getData();
			}
			
		}
		return data;
	}
	
	public String getCheckVisto(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		return getCheckVisto(rif);
	}
	
	public String getCheckVisto(Rif rif){
		String diritto = rif.getDiritto();
		String tipo = getType(diritto);
		String data = "";
		List<Storia> storia = fascicolo.getStoria();
		for (int i = 0; i < storia.size(); i++) {
			Storia element = (Storia)storia.get(i);
			
			// mbernardini 29/09/2017 : corretto bug in recupero info di visto.
			// non e' sufficiente verificare il nome della persona, occorre controllare anche l'ufficio (potrebbe essere stata aggiunta in CC
			// la stessa persona piu' volte con associato l'ufficio di appartenenza o responsabilita')
			if(element.getTipo().equals(tipo) && element.getNome_uff().equals(rif.getNome_uff()) && element.getNome_persona().equals(rif.getNome_persona()) && null!=element.getVisto_da() && element.getVisto_da().trim().length() > 0){
				data = "Visto da "+element.getVisto_da()+" il "+(new DateConverter().getAsString(null, null, element.getData_visto()))+ " alle "+element.getOra_visto();
			}
			
		}
		return data;
	}
	
	public String getCheckVistoRPA(){
		Rif rif = this.fascicolo.getAssegnazioneRPA();
		return getCheckVisto(rif);
	}
	
	public String getCheckVistoITF(){
		Rif rif = this.fascicolo.getAssegnazioneITF();
		return getCheckVisto(rif);
	}
	
	public String removeRifITF() throws Exception{
		try{
			this.formsAdapter.removeRifInt(this.fascicolo.getAssegnazioneITF().getCod_persona(), this.fascicolo.getAssegnazioneITF().getCod_uff(), this.fascicolo.getAssegnazioneITF().getDiritto());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			this.fascicolo.setAssegnazioneITF(new Rif());
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String addRPA() throws Exception{
		try{
			this.formsAdapter.openRifInt("TRASF", false);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			DocWayFascRifInt rifInt = new DocWayFascRifInt();
			rifInt.getFormsAdapter().fillFormsFromResponse(response);
			rifInt.init(response.getDocument());
			rifInt.setViewAddRPA(true);
			rifInt.setShowdoc(this);
			setSessionAttribute("rifIntFasc", rifInt);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public String addCC() throws Exception{
		try{
			this.formsAdapter.openRifInt("CC", false);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			DocWayFascRifInt rifInt = new DocWayFascRifInt();
			rifInt.getFormsAdapter().fillFormsFromResponse(response);
			rifInt.init(response.getDocument());
			rifInt.setViewAddCC(true);
			// tiommi: rimosso perchè inserito direttamente in fascicolo se l'array è vuoto
			// rifInt.getFascicolo().addRifintCC(new Rif());
			rifInt.setShowdoc(this);
			setSessionAttribute("rifIntFasc", rifInt);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String queryFasc() throws Exception{
		try{
			GerarchiaFascicolo gerarchiaFascicolo = (GerarchiaFascicolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fasc");
			this.formsAdapter.queryFasc(gerarchiaFascicolo.getNumero());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			QueryFascicolo queryFascicolo = new QueryFascicolo();
			// mbernardini 08/02/2019 : mantenimento dello stato 'popup' in caso di navigazione gerarchia fascicolo in popup di fascicolazione di un documento
			queryFascicolo.setPopupPage(this.isPopupPage()); 
			return queryFascicolo.navigateResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String queryLink() throws Exception{
		try{
			GerarchiaFascicolo gerarchiaFascicolo = (GerarchiaFascicolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fasc");
			this.formsAdapter.queryFasc(gerarchiaFascicolo.getNumero());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			return new QueryFascicolo().navigateResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String assignFasc() throws Exception{
		try {
			this.formsAdapter.doAssignFasc(this.formsAdapter.getDefaultForm().getParam("physDoc_infasc"), this.formsAdapter.getDefaultForm().getParam("physDoc"));
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			close = true;
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String closeInsInFasc() throws Exception{
		try {
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
	 * Assegna tutti i documento selezionati da una lista titoli al fascicolo
	 * corrente (processo tramite loadingbar)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doAssignFascAll() throws Exception{
		try {
			this.formsAdapter.doAssignFascAll(this.formsAdapter.getDefaultForm().getParam("physDoc"));
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form

			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
				action = "doAssignFascAll";
				getUserBean().setServiceFormParam("klRac", null);
				getUserBean().setServiceFormParam("keylist", null);
				getUserBean().setServiceFormParam("selRac", null);
			}
			return null;

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String assegnaFascicoloCollegato(){
		numFasc = fascicolo.getNumero();
		close = true;
		return null;

	}

	public void setClose(boolean close) {
		this.close = close;
	}

	public boolean isClose() {
		return close;
	}

	public void setNumFasc(String numFasc) {
		this.numFasc = numFasc;
	}

	public String getNumFasc() {
		return numFasc;
	}
	
	
	public int getContaDocumentiRaccolti() {
		String kl = this.getFormsAdapter().getDefaultForm().getParam("klRac");
	    String separator = "}{";
	    int count = 0;
	    int index = 0;
	    while ((index=kl.indexOf(separator, index)) != -1) {
	        count++;
	        index++;
	    }
	    return count / 2;
	}
	
	public String getPhysDoc_infasc(){
		return this.getFormsAdapter().getDefaultForm().getParam("physDoc_infasc");
	}

	public String getbAssegnaLinkFasc(){
		return this.getFormsAdapter().getDefaultForm().getParam("bAssegnaLinkFasc");
	}

	public String getClassif_cod_infasc(){
		return this.getFormsAdapter().getDefaultForm().getParam("classif_cod_infasc");
	}
	
	public int getLevel(){
		int livello =0;
		String numero = fascicolo.getNumero();
		if (null != numero && numero.contains("-")){
			String value = numero.substring(numero.indexOf("-")+1);
			if (null != value && value.contains("-")){
				String classifAndNum = value.substring(value.indexOf("-")+1);
				if (classifAndNum.contains("/"))
					classifAndNum = classifAndNum.substring(classifAndNum.indexOf("/")+1);
				else classifAndNum = "."+classifAndNum;
				while (classifAndNum.contains(".")){
					livello++;
					classifAndNum = classifAndNum.substring(classifAndNum.indexOf(".")+1);
				}
			}
		}
		return livello;
		
	}
	
	public String insDoc(String tipoDoc) throws Exception{
		try {
			this.formsAdapter.ripetiNuovo(tipoDoc, "nuovoinfascicolo");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			return buildSpecificDocEditPageAndReturnNavigationRule(tipoDoc, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public String insArrivo() throws Exception{
		return insDoc("arrivo");
	}
	public String insPartenza() throws Exception{
		return insDoc("partenza");
	}
	public String insInterno() throws Exception{
		return insDoc("interno");
	}
	public String insVarie() throws Exception{
		return insDoc("varie");
	}
	public String insRepertorio() throws Exception{
		return insDoc("repertorio");
	}
	
	/**
	 * Inserimento di un nuovo fascicolo del personale
	 * @return
	 * @throws Exception
	 */
	public String insTableDocFascicolo() throws Exception {
		try {
			// mbernardini 20/12/2016 : corretta ridirezione della pagina di inserimento in caso di fascicoli personalizzati
			if (fascicolo.getCodiceFascicoloCustom() != null && !fascicolo.getCodiceFascicoloCustom().isEmpty())
				formsAdapter.insTableFascicoloCustom(Const.DOCWAY_TIPOLOGIA_FASCICOLO, fascicolo.getCodiceFascicoloCustom(), fascicolo.getDescrizioneFascicoloCustom());
			else
				formsAdapter.insTableDoc(Const.DOCWAY_TIPOLOGIA_FASCICOLO); 
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_FASCICOLO, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;	
		}
	}
	
	/**
	 * inserimento di un nuovo sottofascicolo
	 * @return
	 * @throws Exception
	 */
	public String insSottofascicolo() throws Exception{
		try {
			if (fascicolo.getCodiceFascicoloCustom() != null && !fascicolo.getCodiceFascicoloCustom().isEmpty())
				this.formsAdapter.getDefaultForm().addParam("codice_fasc", fascicolo.getCodiceFascicoloCustom());
			if (fascicolo.getDescrizioneFascicoloCustom() != null && !fascicolo.getDescrizioneFascicoloCustom().isEmpty())
				this.formsAdapter.getDefaultForm().addParam("descrizione_fasc", fascicolo.getDescrizioneFascicoloCustom());
			
			this.formsAdapter.insertSottoFasc(fascicolo.getNumero(), this.getFormsAdapter().getDefaultForm().getParam("physDoc"), this.fascicolo.getAssegnazioneRPA().getNome_persona(), this.fascicolo.getAssegnazioneRPA().getNome_uff(), this.fascicolo.getAssegnazioneRPA().getCod_persona(), this.fascicolo.getAssegnazioneRPA().getCod_uff(), this.fascicolo.getClassif().getText(), this.fascicolo.getClassif().getCod(), this.fascicolo.getAssegnazioneRPA().getTipo_uff(), fascicolo.getCodiceFascicoloCustom(), fascicolo.getDescrizioneFascicoloCustom());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule("fascicolo", response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * pulsante ripeti nuovo del fascicolo
	 * 
	 * @return
	 * @throws Exception
	 */
	public String ripetiNuovo() throws Exception{
		try {
			// mbernardini 20/12/2016 : corretta ridirezione della pagina di inserimento in caso di fascicoli personalizzati
			if (fascicolo.getCodiceFascicoloCustom() != null && !fascicolo.getCodiceFascicoloCustom().isEmpty())
				formsAdapter.ripetiNuovoSottoFasc("fascicolo", fascicolo.getNumero(), -1, this.fascicolo.getAssegnazioneRPA().getNome_persona(), this.fascicolo.getAssegnazioneRPA().getNome_uff(), this.fascicolo.getAssegnazioneRPA().getCod_persona(), this.fascicolo.getAssegnazioneRPA().getCod_uff(), this.fascicolo.getClassif().getText(), this.fascicolo.getClassif().getCod(), this.fascicolo.getAssegnazioneRPA().getTipo_uff(), fascicolo.getCodiceFascicoloCustom(), fascicolo.getDescrizioneFascicoloCustom());
			else
				formsAdapter.ripetiNuovoSottoFasc("fascicolo", fascicolo.getNumero(), -1, this.fascicolo.getAssegnazioneRPA().getNome_persona(), this.fascicolo.getAssegnazioneRPA().getNome_uff(), this.fascicolo.getAssegnazioneRPA().getCod_persona(), this.fascicolo.getAssegnazioneRPA().getCod_uff(), this.fascicolo.getClassif().getText(), this.fascicolo.getClassif().getCod(), this.fascicolo.getAssegnazioneRPA().getTipo_uff());
			
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule("fascicolo", response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	/**
	 * Copia il collegamento al fascicolo
	 * 
	 * @throws Exception
	 */
	public String copyLink() throws Exception {
		return this.copyLink(this.fascicolo.getNrecord());
	}
	
	public String changeclassif() throws Exception{
		try {
			this.formsAdapter.openClassifSpec("@CAMBIA_CLASSIF_FASC");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			DocWayChangeClassif changeClassif = new DocWayChangeClassif();
			changeClassif.getFormsAdapter().fillFormsFromResponse(response);
			changeClassif.init(response.getDocument());
			changeClassif.setView(true);
			changeClassif.setShowdoc(this);
			setSessionAttribute("changeclassif", changeClassif);
			getUserBean().setServiceFormParam("klRac", null);
			getUserBean().setServiceFormParam("keylist", null);
			getUserBean().setServiceFormParam("selRac", null);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String navigateRepPrecedente() throws Exception{
		try {
			this.formsAdapter.queryFasc(fascicolo.getRepertorio_precedente().getCod());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			return new QueryFascicolo().navigateResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	
	public String navigateNuovoRep() throws Exception{
		try {
			this.formsAdapter.queryFasc(fascicolo.getNuovo_repertorio().getCod());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			return new QueryFascicolo().navigateResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	/**
	 * Ripristino della classificazione di un fascicolo (pulsante in showdoc che viene visualizzato dopo 
	 * un'operazione di cambio classificazione sul fascicolo)
	 * 
	 * @throws Exception
	 */
	public String restoreclassif() throws Exception{
		try {
			this.formsAdapter.doRestoreClassif();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
				action = "restoreclassif";
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public String closeView() throws Exception{
		setLoadingbar(null);
		this.reload();
		return null;
	}
	
	public String remove() throws Exception{
		try {
			formsAdapter.remove();
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			String verbo = response.getAttributeValue("/response/@verbo");
			String dbTable = response.getAttributeValue("/response/@dbTable");
			
			if ("@reload".equals(dbTable)) {
				getFormsAdapter().fillFormsFromResponse(response);
				reload();
				return null;
			}
			else if ("query".equals(verbo))
				return "show@docway_home";
			else
				return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}
	
	/**
	 * Archiviazione di un fascicolo
	 * @return
	 * @throws Exception
	 */
	public String archiviaFascicolo() throws Exception {
		try {
			this.formsAdapter.archiviaFascicolo();
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
	 * Riapertura di un fascicolo
	 * @return
	 * @throws Exception
	 */
	public String apriFascicolo() throws Exception {
		try {
			this.formsAdapter.apriFascicolo();
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
	 * eliminazione di un CC dal fascicolo (prevede l'apertura della
	 * loadingbar di eliminazione dei cc ereditati dai documenti/sottofascicoli)
	 * @return
	 * @throws Exception
	 */
	@Override
	public String removeRifCC() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		try {
			getFormsAdapter().removeRifIntCConFascicolo(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
				action = "aggiornamentoCCfasc";
			}
			else {
				reload();
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * eliminazione del diritto di intervento ad un rif interno al fascicolo (prevede l'apertura della
	 * loadingbar di aggiornamento del diritto di intervento anche sui cc ereditati dai documenti/sottofascicoli)
	 * @return
	 * @throws Exception
	 */
	@Override
	public String rimuoviIntervento() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		try {
			if (rif.getDiritto().toLowerCase().equals("cc")) {
				getFormsAdapter().rimuoviInterventoCConFascicolo(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				
				String verbo = response.getAttributeValue("/response/@verbo");
				if (verbo.equals("loadingbar")) {
					DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
					docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
					docWayLoadingbar.init(response);
					setLoadingbar(docWayLoadingbar);
					docWayLoadingbar.setActive(true);
					action = "aggiornamentoCCfasc";
				}
				else {
					reload();
				}
			}
			else {
				getFormsAdapter().rimuoviIntervento(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				
				getFormsAdapter().fillFormsFromResponse(response);
				_reloadWithoutNavigationRule();
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * assegnazione del diritto di intervento ad un rif interno al fascicolo (prevede l'apertura della
	 * loadingbar di aggiornamento del diritto di intervento anche sui cc ereditati dai documenti/sottofascicoli)
	 * @return
	 * @throws Exception
	 */
	@Override
	public String assegnaIntervento() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		try {
			if (rif.getDiritto().toLowerCase().equals("cc")) {
				getFormsAdapter().assegnaInterventoCConFascicolo(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				
				String verbo = response.getAttributeValue("/response/@verbo");
				if (verbo.equals("loadingbar")) {
					DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
					docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
					docWayLoadingbar.init(response);
					setLoadingbar(docWayLoadingbar);
					docWayLoadingbar.setActive(true);
					action = "aggiornamentoCCfasc";
				}
				else {
					reload();
				}
			}
			else {
				getFormsAdapter().assegnaIntervento(rif.getCod_persona(), rif.getCod_uff(), rif.getDiritto());
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				
				getFormsAdapter().fillFormsFromResponse(response);
				_reloadWithoutNavigationRule();
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
}
