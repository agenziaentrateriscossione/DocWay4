package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Titles;
import it.tredi.dw4.docway.doc.adapters.DocDocWayTitlesFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DigitVisibilitaUtil;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.QueryUtil;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;
import org.dom4j.Element;

public class DocWayTitles extends Titles {

	protected DocDocWayTitlesFormsAdapter formsAdapter;
	private boolean selectAll = false;
	private String dbTable;
	private String nome_persona_fasc;
	private String nome_uff_fasc;
	private String classif_fasc;
	private String oggetto_fasc;
	private String num_fasc;
	private int countSelection;

	private String view = ""; // utilizzato per la gestione di verifica dei duplicati (view="verificaDuplicati")
	//e del popup ricerca doc di cui copiare gli allegati nel raccIndice

	private String xml;

	private boolean addVaschettaCustomOpened = false;
	private String nomeVaschettaCustom = "";
	private String filtroTemporaleVaschettaCustom = "";

	// etichette custom per visibilita'
	private Map<String, String> labelsVisibilita = new HashMap<String, String>();

	//dpranteda - 10/12/2014
	private boolean isSeduta;

	// identifica se si tratta di una lista documenti in cestino (cancellazione logica)
	private boolean cestino = false;

	// campi di raffinamento della ricerca
	private boolean raffinaSoloEstremi = true;
	private String raffinaSearchTerms = "";
	private String raffinaAnno = "";
	private String raffinaNumProt = "";
	
	protected boolean enableIW = true; // abilita/disabilita l'utilizzo di IW da parte del client 

	public DocWayTitles() throws Exception {
		this.formsAdapter = new DocDocWayTitlesFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(Document domTitoli) throws Exception {
		super.titoli = XMLUtil.parseSetOfElement(domTitoli, "//titolo", new Titolo());
		initSelection();
		Element root = domTitoli.getRootElement();
		this.dbTable = root.attributeValue("dbTable", "");
		this.nome_persona_fasc = root.attributeValue("nome_persona_fasc", "");
		this.nome_uff_fasc = root.attributeValue("nome_uff_fasc", "");
		this.classif_fasc = root.attributeValue("classif_fasc", "");
		this.oggetto_fasc = root.attributeValue("oggetto_fasc", "");
		this.num_fasc = root.attributeValue("num_fasc", "");
		this.view = root.attributeValue("view", "");
    	this.xml = domTitoli.asXML();

    	String qord = this.formsAdapter.getDefaultForm().getParam("qord");
    	if (qord != null && qord.length() > 0 && qord.charAt(0) == 'X')
   			ascSort = false;
    	else
    		ascSort = true;
    	this.xwOrd = qord; //XMLUtil.parseStrictAttribute(domTitoli, "response/ordinamento_select/option[@selected='selected']/@value", "");

    	// recupero delle tipolopgie di ordinamento
    	if (dbTable.equals("@fascicolo"))
    		this.ordinamentoSelect = XMLUtil.parseSetOfElement(domTitoli, "/response/ordinamentoFascicoli_select/option", new Option());
    	else
    		this.ordinamentoSelect = XMLUtil.parseSetOfElement(domTitoli, "/response/ordinamento_select/option", new Option());

    	// caricamento etichette custom per visibilita'
		DigitVisibilitaUtil digitVisibilitaUtil = new DigitVisibilitaUtil(XMLUtil.parseStrictAttribute(domTitoli, "/response/@dicitVis"));
		labelsVisibilita = digitVisibilitaUtil.getDigitSingolare();

		// verifico se la lista di documenti corrente e' relativa ai documenti in cestino
		this.cestino = true;
		int i = 0;
		while (i < titoli.size() && this.cestino) {
			Titolo titolo = titoli.get(i);
			if (titolo != null && (!titolo.getHashSplit().containsKey("TRASH") || !titolo.getHashSplit().get("TRASH").equals("si")))
					this.cestino = false;
			i++;
		}

    	setCurrentPage(this.formsAdapter.getCurrent()+"");

    	
    	if (titoli.size() > 0) {
    		Titolo first = titoli.get(0);
    		if (first != null) {
    			// mbernardini 24/05/2017 : controllo se ai titoli e' associato il physdoc dei documenti... in questo caso la selezione dei documenti
    			// verra' fatta tramite physdoc e non tramite posizione (integrazione elasticsearch)
    			this.selectionByPos = (first.getPhysdoc() > 0) ? false : true;
    			
    	//dpranteda 10/12/2014
	    		this.setSeduta(first.isNotShowSeduta() || first.isShowSeduta());
    		}
    	}

    	// mbernardini 27/03/2015 : init dei campi di raffinamento della ricerca
    	initCampiRaffinamento();
    	
    	// inizializzazione delle azioni massive su lista titoli
    	initAzioniMassive(domTitoli);
    	
    	// gestione doc informatici
    	enableIW = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(domTitoli, "/response/@enableIW"));
	}

	/**
	 * inizializzazione dei campi di raffinamento della ricerca
	 */
	private void initCampiRaffinamento() {
		this.raffinaSearchTerms = "";
    	this.raffinaSoloEstremi = true;
    	this.raffinaAnno = "";
    	this.raffinaNumProt = "";
	}

	@Override
	public DocDocWayTitlesFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public String mostraDocumento()  throws Exception{

		Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
		return mostraDocumento(titolo);
	}

	public String mostraFascicolo()  throws Exception{

		Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titleFasc");
		return mostraDocumento(titolo);
	}

	/**
	 * Caricamento della pagina di showdoc relativa ad un titolo
	 *
	 * @param titolo record selezionato dall'utente
	 * @return
	 * @throws Exception
	 */
	public String mostraDocumento(Titolo titolo) throws Exception{
		try{
			String index = titolo.getIndice();
			String dbTable = titolo.getDbTable();
			String tipo = titolo.getDb();

			XMLDocumento response = super._mostraDocumento(Integer.valueOf(index)-1, tipo, dbTable, getView());
			dbTable = response.getAttributeValue("/response/@dbTable", "");
			if (dbTable.startsWith("@")) dbTable = dbTable.substring(1);

			if (dbTable.equals("differito"))
				dbTable = "arrivo";
			return this.buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);

		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}

	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}

	public String getDbTable() {
		return dbTable;
	}

	public void setNome_persona_fasc(String nome_persona_fasc) {
		this.nome_persona_fasc = nome_persona_fasc;
	}

	public String getNome_persona_fasc() {
		return nome_persona_fasc;
	}

	public void setNome_uff_fasc(String nome_uff_fasc) {
		this.nome_uff_fasc = nome_uff_fasc;
	}

	public String getNome_uff_fasc() {
		return nome_uff_fasc;
	}

	public void setClassif_fasc(String classif_fasc) {
		this.classif_fasc = classif_fasc;
	}

	public String getClassif_fasc() {
		return classif_fasc;
	}

	public void setOggetto_fasc(String oggetto_fasc) {
		this.oggetto_fasc = oggetto_fasc;
	}

	public String getOggetto_fasc() {
		return oggetto_fasc;
	}

	public void setNum_fasc(String num_fasc) {
		this.num_fasc = num_fasc;
	}

	public String getNum_fasc() {
		return num_fasc;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getView() {
		return view;
	}

	public boolean isAddVaschettaCustomOpened() {
		return addVaschettaCustomOpened;
	}

	public void setAddVaschettaCustomOpened(boolean addVaschettaCustomOpened) {
		this.addVaschettaCustomOpened = addVaschettaCustomOpened;
	}

	public String getNomeVaschettaCustom() {
		return nomeVaschettaCustom;
	}

	public void setNomeVaschettaCustom(String nomeVaschettaCustom) {
		this.nomeVaschettaCustom = nomeVaschettaCustom;
	}

	public String getFiltroTemporaleVaschettaCustom() {
		return filtroTemporaleVaschettaCustom;
	}

	public void setFiltroTemporaleVaschettaCustom(
			String filtroTemporaleVaschettaCustom) {
		this.filtroTemporaleVaschettaCustom = filtroTemporaleVaschettaCustom;
	}

	public Map<String, String> getLabelsVisibilita() {
		return labelsVisibilita;
	}

	public void setLabelsVisibilita(Map<String, String> labelsVisibilita) {
		this.labelsVisibilita = labelsVisibilita;
	}

	public boolean isCestino() {
		return cestino;
	}

	public void setCestino(boolean cestino) {
		this.cestino = cestino;
	}

	public boolean isRaffinaSoloEstremi() {
		return raffinaSoloEstremi;
	}

	public void setRaffinaSoloEstremi(boolean raffinaSoloEstremi) {
		this.raffinaSoloEstremi = raffinaSoloEstremi;
	}

	public String getRaffinaSearchTerms() {
		return raffinaSearchTerms;
	}

	public void setRaffinaSearchTerms(String raffinaSearchTerms) {
		this.raffinaSearchTerms = raffinaSearchTerms;
	}

	public String getRaffinaAnno() {
		return raffinaAnno;
	}

	public void setRaffinaAnno(String raffinaAnno) {
		this.raffinaAnno = raffinaAnno;
	}

	public String getRaffinaNumProt() {
		return raffinaNumProt;
	}

	public void setRaffinaNumProt(String raffinaNumProt) {
		this.raffinaNumProt = raffinaNumProt;
	}
	
	public boolean isEnableIW() {
		return enableIW;
	}

	public void setEnableIW(boolean enableIW) {
		this.enableIW = enableIW;
	}

	public String queryFasc() throws Exception{
		try{
			this.formsAdapter.queryFasc(num_fasc);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (response.getAttributeValue("/response/@verbo").equals("showdoc")){
				return buildSpecificShowdocPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response);
			}
			else{
				this.init(response.getDocument());
				return "showtitles@fascicolo";
			}
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public String goToTable() throws Exception{
		try{
			String table = dbTable.startsWith("@") ? dbTable.substring(1) : dbTable;
			this.formsAdapter.gotoTableQ(table);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (response.getAttributeValue("/response/@verbo").equals("query")){
				return buildSpecificQueryPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response);
			}
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public String selectAll(ValueChangeEvent e){
		selectAll = new Boolean(e.getNewValue()+"").booleanValue();
		selectAll();
		if (selectAll){
			countSelection = formsAdapter.getCount();
			theSele = "";
			for (int i = 1; i <= countSelection; i++) {
				theSele += "}{"+i+"}{";
			}

		}
		else {
			theSele="";
			countSelection = 0;
		}
		return null;
	}
	public void selectAll(){
		for (Iterator<Titolo> iterator = super.titoli.iterator(); iterator.hasNext();) {
			Titolo titolo = (Titolo) iterator.next();
			titolo.setSelect(selectAll);
		}
	}

	/**
	 * inizializzazione della selezione di documenti
	 */
	private void initSelection() throws Exception {
		for (Iterator<Titolo> iterator = super.titoli.iterator(); iterator.hasNext();) {
			Titolo titolo = (Titolo) iterator.next();
			if (this.isTitleSelected(titolo))
				titolo.setSelect(true);
		}
	}

	/**
	 * reset della selezione di documenti
	 */
	private void resetSelection() {
		theSele = "";
		getFormsAdapter().getDefaultForm().addParam("klRac", "");

		for (Iterator<Titolo> iterator = super.titoli.iterator(); iterator.hasNext();) {
			Titolo titolo = (Titolo) iterator.next();
			titolo.setSelect(false);
		}
		countSelection = 0;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public String selectTitle(ValueChangeEvent e) throws Exception{
		try{
			boolean selected = new Boolean(e.getNewValue()+"").booleanValue();
			Titolo titolo = (Titolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
			String titleFinder = getTitoloTheSeleId(titolo);
			if (titolo.isSelect() && !selected){
				theSele = theSele.replaceFirst("\\}\\{"+titleFinder+"\\}\\{", "");
				titolo.setSelect(false);
				countSelection--;
			}
			else if (!titolo.isSelect() && selected){
				titolo.setSelect(true);
				theSele += "}{"+titleFinder+"}{";
				countSelection++;
			}
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public String selectTitleFasc(ValueChangeEvent e) throws Exception{
		try{
			boolean selected = new Boolean(e.getNewValue()+"").booleanValue();
			Titolo titolo = (Titolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titleFasc");
			String titleFinder = getTitoloTheSeleId(titolo);
			if (titolo.isSelect() && !selected){
				theSele = theSele.replaceFirst("\\}\\{"+titleFinder+"\\}\\{", "");
				titolo.setSelect(false);
				countSelection--;
			}
			else if (!titolo.isSelect() && selected){
				titolo.setSelect(true);
				theSele += "}{"+titleFinder+"}{";
				countSelection++;
			}
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * aggiornamento dei titoli selezionati dall'utente (chiamata da
	 * pulsante nascosto presente nella lista titoli)
	 * @return
	 * @throws Exception
	 */
	public String updateSelectionTitles() throws Exception {
		Titolo titolo = (Titolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
		return updateSelectionTitles(titolo);
	}

	/**
	 * aggiornamento dei titoli relativi a fascicoli selezionati dall'utente (chiamata da
	 * pulsante nascosto presente nella lista titoli)
	 * @return
	 * @throws Exception
	 */
	public String updateSelectionTitlesFasc() throws Exception {
		Titolo titolo = (Titolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titleFasc");
		return updateSelectionTitles(titolo);
	}

	/**
	 * aggiornamento dei titoli selezionati dall'utente (chiamata da
	 * pulsante nascosto presente nella lista titoli)
	 * @return
	 * @throws Exception
	 */
	private String updateSelectionTitles(Titolo titolo) throws Exception {
		try{
			String titleFinder = getTitoloTheSeleId(titolo);
			if (!titolo.isSelect()) {
				titolo.setSelected(false);
				theSele = theSele.replaceFirst("\\}\\{"+titleFinder+"\\}\\{", "");
				countSelection--;
			}
			else if (titolo.isSelect()) {
				titolo.setSelected(true);
				theSele += "}{"+titleFinder+"}{";
				countSelection++;
			}
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Esportazione CSV della lista titoli
	 * @return
	 * @throws Exception
	 */
	public String exportCSV() throws Exception{
		try{
			this.formsAdapter.esportaCSV(getSelid(), theSele);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, false, "exportCSV");

			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Stampa la lista titoli
	 * @return
	 */
	public String stampaElenco() throws Exception {
		try{
			this.formsAdapter.stampaElenco();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Stampa la selezione tramite controllo di gestione
	 * @return
	 * @throws Exception
	 */
	public String stampaCtrlGestione() throws Exception {
		try {
			formsAdapter.gotoTableQ("ctrl_gestione");
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());

			QueryCtrlGestione queryCtrlGestione = new QueryCtrlGestione();
			queryCtrlGestione.getFormsAdapter().fillFormsFromResponse(response);
			queryCtrlGestione.init(response.getDocument());
			setSessionAttribute("queryCtrlGestione", queryCtrlGestione);

			return "query@ctrl_gestione";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Stampa la selezione tramite registro
	 * @return
	 * @throws Exception
	 */
	public String stampaRegistro() throws Exception {
		try {
			formsAdapter.gotoTableQ("stampe", theSele);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());

			QueryStampe queryStampe = new QueryStampe();
			queryStampe.getFormsAdapter().fillFormsFromResponse(response);
			queryStampe.init(response.getDocument());
			setSessionAttribute("queryStampe", queryStampe);

			return "query@stampe";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Stampa profili della selezione corrente
	 * @return
	 * @throws Exception
	 */
	public String printProfiles() throws Exception {
		try {
			formsAdapter.printProfiles(theSele);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form

			DocWayPrintProfiles docwayPrintProfiles = new DocWayPrintProfiles();
			docwayPrintProfiles.getFormsAdapter().fillFormsFromResponse(response);
			docwayPrintProfiles.init(response.getDocument());
			setSessionAttribute("docwayPrintProfiles", docwayPrintProfiles);

			return "print_profiles";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public void setCountSelection(int countSelection) {
		this.countSelection = countSelection;
	}

	public int getCountSelection() {
		return countSelection;
	}

	public String store() throws Exception{
		try {
			this.formsAdapter.store();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, false, "store");

			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public String raccogli() throws Exception{
		try{
			this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			getUserBean().setServiceFormParam("klRac", theSele);
			getUserBean().setServiceFormParam("keylist", theSele);
			getUserBean().setServiceFormParam("selRac", formsAdapter.getSelid());
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(response);
			this.init(response.getDocument());
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public String removeSelFromFasc() throws Exception{
		try{
			this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			this.formsAdapter.removeSelFromFasc();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, false, "removeSelFromFasc");

			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}

	}

	public String removeSelFromFascM() throws Exception{
		try{
			this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			this.formsAdapter.removeSelFromFascM();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, false, "removeSelFromFasc");

			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Inserimento dei documenti selezionati all'interno di un fascicolo
	 * @return
	 * @throws Exception
	 */
	public String insertInFascRPA() throws Exception{
		try{
			this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			this.formsAdapter.insertInFasc("RPA");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			String personalDirCliente = response.getAttributeValue("/response/@personalDirCliente");
			if (personalDirCliente != null && personalDirCliente.length() > 0 && personalDirCliente.endsWith("/"))
				personalDirCliente = personalDirCliente.substring(0, personalDirCliente.length()-1); // eliminazione della "/" finale
			return buildSpecificQueryPageAndReturnNavigationRule("@fascicolo", "", personalDirCliente, "", response, isPopupPage());

		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Inserimento delle minute all'interno di un fascicolo
	 * @return
	 * @throws Exception
	 */
	public String insertInFascRPAM() throws Exception{
		try{
			this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			this.formsAdapter.insertInFasc("RPAM");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			String personalDirCliente = response.getAttributeValue("/response/@personalDirCliente");
			if (personalDirCliente != null && personalDirCliente.length() > 0 && personalDirCliente.endsWith("/"))
				personalDirCliente = personalDirCliente.substring(0, personalDirCliente.length()-1); // eliminazione della "/" finale
			return buildSpecificQueryPageAndReturnNavigationRule("@fascicolo", "", personalDirCliente, "", response, isPopupPage());

		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Implementazione del comando 'trasferisci' da lista
	 * titoli fascicoli
	 */
	public String addRPAFasc() throws Exception{
		try{
			this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			this.formsAdapter.openRifInt("TRASF");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			this.formsAdapter.restoreOpernRifInt();
			DocWayFascRifInt rifInt = new DocWayFascRifInt();
			rifInt.getFormsAdapter().fillFormsFromResponse(response);
			rifInt.init(response.getDocument());
			rifInt.setViewAddRPA(true);
			rifInt.setShowdoc(this);
			setSessionAttribute("rifIntFasc", rifInt);
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}

	}

	/**
	 * Aggiunta di RPA ad una selezione di documenti da lista titoli
	 *
	 * @throws Exception
	 */
	public String addRPA() throws Exception{
		return openRifInt("RPA");
	}

	/**
	 * Aggiunta di CC ad una selezione di documenti da lista titoli
	 *
	 * @throws Exception
	 */
	public String addCC() throws Exception{
		return openRifInt("CC");
	}

	/**
	 * Aggiunta di OP ad una selezione di documenti da lista titoli
	 *
	 * @throws Exception
	 */
	public String addOP() throws Exception{
		return openRifInt("OP");
	}

	/**
	 * Apertura del popup modale di assegnazione rif. interni (lookup)
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	private String openRifInt(String tipoRif) throws Exception {
		try {
			if (tipoRif == null || tipoRif.isEmpty())
				tipoRif = "CC";

			this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			this.formsAdapter.openRifInt(tipoRif);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			DocWayDocRifInt rifInt = new DocWayDocRifInt();
			rifInt.getFormsAdapter().fillFormsFromResponse(response);
			rifInt.init(response.getDocument());

			if (tipoRif.equalsIgnoreCase("rpa"))
				rifInt.setViewAddRPA(true);
			else if (tipoRif.equalsIgnoreCase("cc"))
				rifInt.setViewAddCC(true);
			else if (tipoRif.equalsIgnoreCase("op"))
				rifInt.setViewAddOP(true);

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

	public String openClassifFasc() throws Exception{
		try{
			if (this.formsAdapter.getDefaultForm().getParam("physDoc").equals("")) {
				this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			}
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
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Apre il popup dialog di cambio di classificazione (da lista titoli documenti)
	 *
	 * @throws Exception
	 */
	public String openClassifSpec() throws Exception{
		try{
			if (this.formsAdapter.getDefaultForm().getParam("physDoc").equals("")) {
				this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			}
			this.formsAdapter.openClassifSpec("@CAMBIA_CLASSIF_DOC");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			DocWayChangeClassifDoc changeClassif = new DocWayChangeClassifDoc();
			changeClassif.getFormsAdapter().fillFormsFromResponse(response);
			changeClassif.init(response.getDocument());
			changeClassif.setView(true);
			changeClassif.setShowdoc(this);
			setSessionAttribute("changeclassifdoc", changeClassif);
			getUserBean().setServiceFormParam("klRac", null);
			getUserBean().setServiceFormParam("keylist", null);
			getUserBean().setServiceFormParam("selRac", null);
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Apre il popup dialog di cambio di classificazione su minuta (da lista titoli documenti)
	 *
	 * @throws Exception
	 */
	public String openClassifSpecMinuta() throws Exception{
		try{
			if (this.formsAdapter.getDefaultForm().getParam("physDoc").equals("")) {
				this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			}
			this.formsAdapter.openClassifSpec("@CAMBIA_CLASSIF_DOC_MINUTA");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			DocWayChangeClassifDoc changeClassif = new DocWayChangeClassifDoc();
			changeClassif.getFormsAdapter().fillFormsFromResponse(response);
			changeClassif.init(response.getDocument());
			changeClassif.setView(true);
			changeClassif.setShowdoc(this);
			changeClassif.setCambioSuMinuta(true);
			setSessionAttribute("changeclassifdoc", changeClassif);
			getUserBean().setServiceFormParam("klRac", null);
			getUserBean().setServiceFormParam("keylist", null);
			getUserBean().setServiceFormParam("selRac", null);
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public String ritiraBandoSelezione() throws Exception{
		try{
			this.formsAdapter.raccogli(formsAdapter.getSelid(), theSele, false);
			this.formsAdapter.ritiraBandoSelezione();
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, false, "ritiraBandoSelezione");

			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * In caso di visualizzazione articolazione sottofascicoli, viene utilizzato per mostrare il contenuto (documenti contenuti) di
	 * un fascicolo direttamente dalla lista titoli
	 * @return
	 * @throws Exception
	 */
	public String showFascContentInFrame() throws Exception{
		try{
			Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titleFasc");
			if (titolo.isOpenSubTitles()) {
				titolo.setOpenSubTitles(false);
				return null;
			}
			String index = titolo.getIndice();
			String dbTable = titolo.getDbTable();
			String tipo = titolo.getDb();
			XMLDocumento response = super._mostraDocumento(Integer.valueOf(index)-1, tipo, dbTable);
			dbTable = response.getAttributeValue("/response/@dbTable", "");
			if (dbTable.startsWith("@")) dbTable = dbTable.substring(1);
			if ("fascicolo".equals(dbTable)){
				ShowdocFascicolo showdoc = new ShowdocFascicolo();
				showdoc.getFormsAdapter().fillFormsFromResponse(response);
				showdoc.init(response.getDocument());
				this.formsAdapter.fascContent(showdoc.getFascicolo().getNumero(), showdoc.getFascicolo().getAssegnazioneRPA().getNome_persona(), showdoc.getFascicolo().getAssegnazioneRPA().getNome_uff(), showdoc.getFascicolo().getClassif().getText(), showdoc.getFascicolo().getOggetto());
				response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				titolo.setOpenSubTitles(true);
				DocWayTitles subtitles = new DocWayTitles();
				subtitles.formsAdapter.fillFormsFromResponse(response);
				subtitles.init(response.getDocument());
				titolo.setSubtitles(subtitles);
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public boolean getNextLevel2(){
		Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titleFasc");
		int position = this.titoli.indexOf(titolo);
		for (int i = position+1; i< titoli.size(); i++){
			if (this.titoli.get(i).getFolderLevel() == 1 ) return false;
			else if (this.titoli.get(i).getFolderLevel() == 2 ) return true;
		}
		return false;
	}

	public boolean getNextLevel3(){
		Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titleFasc");
		int position = this.titoli.indexOf(titolo);
		for (int i = position+1; i< titoli.size(); i++){
			if (this.titoli.get(i).getFolderLevel() == 1 ) return false;
			else if (this.titoli.get(i).getFolderLevel() == 3 ) return true;
		}
		return false;
	}

	public boolean getNextLevel4(){
		Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titleFasc");
		int position = this.titoli.indexOf(titolo);
		for (int i = position+1; i< titoli.size(); i++){
			if (this.titoli.get(i).getFolderLevel() == 1 ) return false;
			else if (this.titoli.get(i).getFolderLevel() == 4 ) return true;
		}
		return false;
	}

	/**
	 * aggiunta della query che ha portato alla selezione corrente come vaschetta custom
	 * dell'utente
	 *
	 * @return
	 * @throws Exception
	 */
	public String addVaschetta() throws Exception {
		try{
			if (nomeVaschettaCustom != null && !nomeVaschettaCustom.equals("")) {
				this.addVaschettaCustomOpened = false;

				formsAdapter.addVaschetta(nomeVaschettaCustom, filtroTemporaleVaschettaCustom);
				XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}

				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			}
			else {
				this.setErrorMessage("templateForm:nomeVaschetta", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.nome_vaschetta") + "'");
			}

			return null;

		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * apertura del popup di aggiunta di una nuova vaschetta custom
	 * @return
	 * @throws Exception
	 */
	public String openAddVaschetta() throws Exception {
		this.nomeVaschettaCustom = "";
		this.filtroTemporaleVaschettaCustom = "tutti";
		this.addVaschettaCustomOpened = true;

		return null;
	}

	/**
	 * chiusura del popup di aggiunta di una nuova vaschetta custom
	 * @return
	 * @throws Exception
	 */
	public String closeAddVaschetta() throws Exception {
		this.addVaschettaCustomOpened = false;
		this.nomeVaschettaCustom = "";
		this.filtroTemporaleVaschettaCustom = "";

		return null;
	}

	/**
	 * recupera la lunghezza massima (espressa in num di caratteri) da impostare
	 * per la visualizzazione dell'oggetto sul titolo
	 * @return
	 * @throws Exception
	 */
	public int getLunghezzaMaxOggetto() throws Exception {
		int lunghezzaOggetto = 0;
		try {
			lunghezzaOggetto = new Integer(DocWayProperties.readProperty(Const.PROPERTY_TITOLO_LUNGHEZZA_MAX_OGGETTO, "0")).intValue();
		}
		catch (Exception e) {
			Logger.warn(e.getMessage());
		}
		return lunghezzaOggetto;
	}

	/**
	 * recupera la lunghezza massima (espressa in num di caratteri) da impostare
	 * per la visualizzazione del soggetto sul titolo
	 * @return
	 * @throws Exception
	 */
	public int getLunghezzaMaxSoggetto() throws Exception {
		int lunghezzaSoggetto = 0;
		try {
			lunghezzaSoggetto = new Integer(DocWayProperties.readProperty(Const.PROPERTY_TITOLO_LUNGHEZZA_MAX_SOGGETTO, "0")).intValue();
		}
		catch (Exception e) {
			Logger.warn(e.getMessage());
		}
		return lunghezzaSoggetto;
	}

	/**
	 * cancellazione massiva di documenti da lista titoli
	 * @return
	 * @throws Exception
	 */
	public String removeDocs() throws Exception{
		try{
			this.formsAdapter.removeDocs(getSelid(), theSele);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, true, "removeDocs");

			// azzeramento della selezione dei documenti
			resetSelection();

			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * contrassegnatura come letto massiva di documenti da lista titoli
	 * @return
	 * @throws Exception
	 */
	public String markAsReadDocs() throws Exception{
		try{
			this.formsAdapter.markAsReadDocs(getSelid(), theSele);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, true, "markAsReadDocs");

			// azzeramento della selezione dei documenti
			resetSelection();

			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * scarto massivo di documenti da lista titoli
	 * @return
	 * @throws Exception
	 */
	public String scartaDocs(String tipo) throws Exception{
		try {
			boolean scartoRuoli = false;
			if (tipo != null && tipo.equals("ruolo"))
				scartoRuoli = true;

			this.formsAdapter.scartaDocs(getSelid(), theSele, scartoRuoli);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, true, "scartaDocs");

			// azzeramento della selezione dei documenti
			resetSelection();

			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public boolean isSeduta() {
		return isSeduta;
	}

	public void setSeduta(boolean isSeduta) {
		this.isSeduta = isSeduta;
	}

	/**
	 * raffinamento della ricerca
	 * @return
	 * @throws Exception
	 */
	public String raffina() throws Exception {
		try {
			String query = QueryUtil.createGlobalQuery(raffinaSearchTerms, raffinaSoloEstremi, raffinaAnno, raffinaNumProt);

			this.formsAdapter.raffina(query);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			Element root = response.getRootElement();
			List<?> tit = root.selectNodes("//titolo");
			if (tit != null && !tit.isEmpty()) {
				DocWayTitles titles = new DocWayTitles();
				titles.getFormsAdapter().fillFormsFromResponse(response);

				titles.init(response.getDocument());

				setSessionAttribute("docwayTitles", titles);
				return "showtitles@docway";
			}
			else {
				if (response.getAttributeValue("//response/@verbo", "").equals("showdoc"))
					return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
				else
					return null;
			}

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * protocollazione massiva di documenti da lista titoli
	 * @return
	 * @throws Exception
	 */
	public String protocollaDocs() throws Exception{
		try {
			this.formsAdapter.protocollaDocs(getSelid(), theSele);
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar"))
				openLoadingbarModal(response, true, "protocollaDocs");

			// azzeramento della selezione dei documenti
			resetSelection();

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Apertura del popup modale di caricamento loadinbar per azione massiva su lista titoli
	 * @param response
	 * @param setHoderPage
	 * @param currentAction
	 * @throws Exception
	 */
	private void openLoadingbarModal(XMLDocumento response, boolean setHoderPage, String currentAction) throws Exception {
		DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
		docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
		docWayLoadingbar.init(response);
		if (setHoderPage)
			docWayLoadingbar.setHolderPageBean(this);
		setLoadingbar(docWayLoadingbar);
		docWayLoadingbar.setActive(true);
		if (currentAction != null && !currentAction.isEmpty())
			this.action = currentAction;
	}


	/**
	 * Restituisce true se il titolo passato risulta selezionato dall'operatore (tramite relativo checkbox), false altrimenti
	 * @param titolo Titolo da verificare (tramite posizione o numero fisico del documento)
	 * @return
	 * @throws Exception
	 */
	public boolean isTitleSelected(Titolo titolo) throws Exception {
		if (theSele != null && theSele.contains("}{"+getTitoloTheSeleId(titolo)+"}{"))
			return true;
		else
			return false;
	}
	
	/**
	 * Ritorna l'identificativo del titolo da registrare nella lista di selezione in caso di check su un titolo da lista titoli da parte di un 
	 * operatore. Puo' essere utilizzato il physdoc (nel caso i titoli abbiano il physdoc dei documenti associati, integrazione elasticsearch) o la 
	 * posizione del titolo nella selezione (
	 * @param titolo
	 * @return
	 * @throws Exception
	 */
	public String getTitoloTheSeleId(Titolo titolo) throws Exception {
		String id = "";
		if (titolo != null) {
			id = titolo.getIndice();
			if (!selectionByPos && !selectAll) {
				if (titolo.getPhysdoc() == 0)  // rilascio eccezione se il physdoc non e' caricato (non si dovrebbe mai verificare)
					throw new Exception("Nessun PhysDoc associato al titolo '" + titolo.getTesto() + "'");
				
				id = String.valueOf("PHYS_" + titolo.getPhysdoc());
			}
		}
		return id;
	}
	
	/**
	 * Stampa tutti gli allegati dei documenti della selezione
	 * @return
	 * @throws Exception
	 */
	public String printDocsAttachments() throws Exception {
		try {
			formsAdapter.printDocsAttachments(theSele);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form in ogni caso
			
			DocWayPrintAttachments printAttachments = new DocWayPrintAttachments();
			printAttachments.getFormsAdapter().fillFormsFromResponse(response);
			printAttachments.init(response.getDocument());
			printAttachments.setVisible(true);
			setSessionAttribute("printAttachments", printAttachments);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

}
