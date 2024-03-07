package it.tredi.dw4.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.acl.beans.AclLoadingbar;
import it.tredi.dw4.acl.beans.AclLoadingbarStampe;
import it.tredi.dw4.acl.beans.AclLuaStoredProcThrobber;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.TitlesFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayHome;
import it.tredi.dw4.docway.beans.DocWayLoadingbar;
import it.tredi.dw4.docway.beans.Menu;
import it.tredi.dw4.docway.model.ExportGroup;
import it.tredi.dw4.docway.model.ExportPersonalizzato;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.model.azionimassive.Azione;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;

public abstract class Titles extends Page {

	protected List<Titolo> titoli;

	protected String colSort = "";
	protected String xwOrd = "";
	protected int xwOrdIndex = 0; // indice della tipologia di ordinamento selezionata (da tendina)
	protected boolean ascSort = true;

	protected String theSele = "";
	protected boolean selectionByPos = true;
	protected String action;

	//variabli per export CSV avanzato
	protected boolean exportCVSAdvancedOpened = false;
	protected List<ExportCSVGroups> exportCSVAdvancedTabs = new ArrayList<ExportCSVGroups>();
	protected boolean exportCVSAdvancedSave = false;
	protected String nomeExportCSVAdvanced = "";
	private String exportType;

	// tipologie di ordinamento dei titoli
	protected List<Option> ordinamentoSelect = new ArrayList<Option>();

	private String currentPage = "";  // utilizzata per la navigazione dei risultati di una ricerca tramite campo testo
	
	// modalita' di visualizzazione della lista titoli
	public static final String MODE_LIST = "list";
	public static final String MODE_TABLE = "table";

	public abstract void init(Document dom) throws Exception;
	
	public abstract TitlesFormsAdapter getFormsAdapter();

	public abstract String mostraDocumento() throws Exception;

	public abstract String getDbTable();
	
	public void setTitles(List<Titolo> titoli) {
		this.titoli = titoli;
	}

	public List<Titolo> getTitles() {
		return this.titoli;
	}

	public String getXwOrd() {
		return xwOrd;
	}

	public void setXwOrd(String ord) {
		this.xwOrd = ord;
	}

	public int getXwOrdIndex() {
		return xwOrdIndex;
	}

	public void setXwOrdIndex(int xwOrdIndex) {
		this.xwOrdIndex = xwOrdIndex;
	}

	public String getColSort() {
		return colSort;
	}

	public void setColSort(String colSort) {
		this.colSort = colSort;
	}

	public boolean isAscSort() {
		return ascSort;
	}

	public void setAscSort(boolean ascSort) {
		this.ascSort = ascSort;
	}

	public List<Option> getOrdinamentoSelect() {
		return ordinamentoSelect;
	}

	public void setOrdinamentoSelect(List<Option> ordinamentoSelect) {
		this.ordinamentoSelect = ordinamentoSelect;
	}
	
	protected XMLDocumento _mostraDocumento(int pos, String db, String table) throws Exception {
		return _mostraDocumento(pos, db, table, "");
	}

	protected XMLDocumento _mostraDocumento(int pos, String db, String table, String view) throws Exception {
		String tipo = (null != db && !"".equals(db.trim())) ? db : getFormsAdapter().getDefaultForm().getParam("db");
		String dbTable = (null != table && !"".equals(table.trim())) ? table : getFormsAdapter().getDefaultForm().getParam("dbTable");
		getFormsAdapter().mostraDocumento(pos, tipo, 0, "", dbTable, view);

		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}

	public String primaPagina() throws Exception {
		if (getFormsAdapter().primaPagina()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
		}
		return null;
	}

	public String paginaPrecedente() throws Exception {
		if (getFormsAdapter().paginaPrecedente()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
		}
		return null;
	}

	public String paginaSuccessiva() throws Exception {
		if (getFormsAdapter().paginaSuccessiva()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
		}
		return null;
	}

	public String ultimaPagina() throws Exception {
		if (getFormsAdapter().ultimaPagina()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
		}
		return null;
	}

	/**
	 * Caricamento di una specifica pagina della lista titoli
	 * @return
	 * @throws Exception
	 */
	public String paginaSpecifica() throws Exception {
			if (StringUtil.isNumber(currentPage)) {
				int curr = new Integer(currentPage).intValue();
				if (curr > 0 && curr <= getFormsAdapter().getTotal()) {
				return paginaSpecifica(curr);			
				}
				else
					currentPage = getFormsAdapter().getCurrent() + "";
			}
			else
				currentPage = getFormsAdapter().getCurrent() + "";

			return null;
		}
	
	/**
	 * Caricamento di una specifica pagina della lista titoli
	 * @param pageNumber
	 * @return
	 * @throws Exception
	 */
	public String paginaSpecifica(int pageNumber) throws Exception {
		try {
			if (pageNumber <= 0)
				pageNumber = 1;
			else if (pageNumber > getFormsAdapter().getTotal())
				pageNumber = getFormsAdapter().getTotal();
			
			getFormsAdapter().paginaSpecifica(pageNumber);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			init(response.getDocument());
			
			return null;
		} 
		catch (Throwable t){
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	public String getFirstPosition(){
		return String.valueOf(getFormsAdapter().getFirstPosition());
	}

	public String getLastPosition(){
		return String.valueOf(getFormsAdapter().getLastPosition());
	}

	public String getCount(){
		return String.valueOf(getFormsAdapter().getCount());
	}

	public String getSelid(){
		return getFormsAdapter().getSelid();
	}

	public String getCurrent() {
		return String.valueOf(getFormsAdapter().getCurrent());
	}

	public String getTotal() {
		return String.valueOf(getFormsAdapter().getTotal());
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	/**
	 * Esportazione CSV dei titoli risultanti da una ricerca
	 *
	 * @return
	 * @throws Exception
	 */
	protected String _esportaCSV() throws Exception {
		getFormsAdapter().esportaCSV();
		XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}

		// Ripristino la form per la gestione classica di Titles
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form

		//Loadingbar - esportazione titoli in CSV
		String verbo = response.getAttributeValue("/response/@verbo");
		if (verbo.equals("loadingbar")) {
			AclLoadingbar aclLoadingbar = new AclLoadingbar();
			aclLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
			aclLoadingbar.init(response);
			setLoadingbar(aclLoadingbar);
			aclLoadingbar.setActive(true);
			return null;
		}

		return null;
	}

	/**
	 * Esportazione dei diritti degli utenti risultanti da una ricerca avanzata
	 * su persone interne
	 *
	 * @return
	 * @throws Exception
	 */
	protected String _printPIRights() throws Exception {
		XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}

		//LoadingbarStampe - aggiornamento delle voci di indice
		String verbo = response.getAttributeValue("/response/@verbo");
		if (verbo.equals("loadingbar")) {
			AclLoadingbarStampe aclLoadingbarStampe = new AclLoadingbarStampe();
			aclLoadingbarStampe.getFormsAdapter().fillFormsFromResponse(response);
			aclLoadingbarStampe.init(response);
			setLoadingbarstampe(aclLoadingbarStampe);
			aclLoadingbarStampe.setActive(true);
			return null;
		}

		return null;
	}

	public String nuovaRicerca()  throws Exception {
		getFormsAdapter().nuovaRicerca();
		XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
		if (response.getAttributeValue("/response/@verbo", "").equals("query")){
			DocWayHome docwayHome = new DocWayHome();
			docwayHome.getFormsAdapter().fillFormsFromResponse(response);
			docwayHome.init(response.getDocument());
			setSessionAttribute("docwayHome", docwayHome);

			Menu docwaymenu = new Menu();
			docwaymenu.getFormsAdapter().fillFormsFromResponse(response);
			setSessionAttribute("docwaymenu", docwaymenu);
			return "show@docway_home";
		}

		return null;
	}

	/**
	 * Lettura dell'attributo colSort passato come attributo attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerSortHeader(ActionEvent event){
		this.colSort = (String) event.getComponent().getAttributes().get("colSort");
	}

	/**
	 * Ordinamento della selezione in base a selezione di una colonna della tabella dei risultati
	 */
	public String sortByCol() throws Exception {
		try {
			if (colSort.toLowerCase().equals(xwOrd.toLowerCase())) {
				return invertOrder();
			}
			else {
				getFormsAdapter().sort(colSort, "", 0);
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				getFormsAdapter().fillFormsFromResponse(response);
				this.init(response.getDocument());
				return null;
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Lettura dell'attributo xwOrdIndex passato come attributo attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerSortTitles(ActionEvent event){
		this.xwOrdIndex = new Integer(event.getComponent().getAttributes().get("xwOrdIndex") + "").intValue();
	}

	/**
	 * Ordinamento della selezione in base ad un criterio di scelta specificato dall'utente
	 */
	public String sort() throws Exception {
		Option option = (Option) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("option");
		return sort(option);
	}

	/**
	 * Ordinamento della selezione in base ad un criterio di scelta specificato dall'utente
	 */
	public String sort(Option sortOption) throws Exception {
		try {
			if (sortOption != null) {
				if (sortOption.getValue().equals(xwOrd)) {
					return invertOrder();
				}
				else {
					getFormsAdapter().sort("", sortOption.getValue(), xwOrdIndex);
					XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
						return null;
					}
					getFormsAdapter().fillFormsFromResponse(response);
					this.init(response.getDocument());
					return null;
				}
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
	 * Inverte l'ordinamento della selezione
	 */
	public String invertOrder() throws Exception {
		try{
			getFormsAdapter().invertOrder();
			XMLDocumento response = this.getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response);
			this.init(response.getDocument());
			return null;
		} catch (Throwable t){
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Esportazione CSV della lista titoli
	 * @return
	 * @throws Exception
	 */
	public String exportCSVAdvanced() throws Exception{
		try{

			String exportsXML = "<exports>";
			for (ExportCSVGroups export : this.getExportCSVAdvancedTabs()) {
				exportsXML += "<export type=\"" + export.getType() + "\">";
				for (ExportGroup group : export.getGroups()) {
					if (group.isSelected()) {
						exportsXML += "<group name=\"" + group.getName() + "\" />";
					}
				}
				exportsXML += "</export>";
			}
			exportsXML += "</exports>";


			this.getFormsAdapter().esportaCSVAdvanced(getSelid(), theSele, exportsXML, exportCVSAdvancedSave, nomeExportCSVAdvanced);

			XMLDocumento response = this.getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
				action = "exportCSV";

				closeExportCSVAdvanced();
			}

			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Esportazione CSV avanzata della lista titoli
	 * @return
	 * @throws Exception
	 */
	public String startExportCSVAdvanced() throws Exception{
		try{
			this.getFormsAdapter().exportCSVAdvancedQueryColumns();

			XMLDocumento response = this.getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}

			openExportCSVAdvanced();

			//pulisci i tab usati precedentemente
			this.exportCSVAdvancedTabs.clear();
			this.nomeExportCSVAdvanced = "";
			this.exportCVSAdvancedSave = false;

			ExportPersonalizzato exportPersonalizzato = (ExportPersonalizzato) getSessionAttribute("docwayExportPersonalizzato");

			//estrai dalla response le colonne per popup csv
			this.exportType = getTipoExportByFirstTitle();
			List<Element> exports = response.selectNodes("//exports/export");
			for (Element export : exports) {
				ExportCSVGroups groups = new ExportCSVGroups(export, this.exportType);

				if (exportPersonalizzato != null) {
					for (ExportGroup group : groups.getGroups()) {
						boolean shouldSelect = exportPersonalizzato.isGroupSaved(group);
						group.setSelected(shouldSelect);
					}

					this.nomeExportCSVAdvanced = exportPersonalizzato.getTitle();
				}

				this.exportCSVAdvancedTabs.add(groups);
			}

			// se non e' stato possibile riconosce un tipo per l'esportazione, setto la prima tipologia definita
			if ((this.exportType == null || this.exportType.isEmpty()) && this.exportCSVAdvancedTabs.size() > 0)
				this.exportType = this.exportCSVAdvancedTabs.get(0).getType();
			
			return "";
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Ritorna la tipologia di record da esportare in base all'analisi del primo titolo della selezione
	 * @return
	 */
	public String getTipoExportByFirstTitle() {
		String type = null;
		String dbTable = this.getDbTable(); // Recupero il dbTable relativo alla pagina di ricerca
		if (dbTable == null || dbTable.isEmpty() || dbTable.equals("@globale")) {
			// Se vuoto il dbTable sulla pagina tento il riconoscimento in base al valore impostato sul primo titolo restituito
			if (this.titoli != null && this.titoli.size() > 0) {
				Titolo first = this.titoli.get(0);
				if (first != null)
					dbTable = first.getDbTable();
			}
		}
		
		if (dbTable != null && !dbTable.isEmpty()) {
			if (dbTable.startsWith("@"))
				type = dbTable.substring(1);
			else
				type = dbTable;
		
			if (type.equals("arrivo") || type.equals("differito") || type.equals("partenza") || type.equals("interno") || type.equals("varie"))
				type = "doc";
		}
		return type;
	}
	
	//funzioni d'aiuto per esportazione avanzata CSV
	public void openExportCSVAdvanced() {
		this.exportCVSAdvancedOpened = true;
	}

	public void closeExportCSVAdvanced() {
		this.exportCVSAdvancedOpened = false;
		setSessionAttribute("docwayExportPersonalizzato", null);
	}

	public boolean isExportCVSAdvancedOpened() {
		return this.exportCVSAdvancedOpened;
	}

	public List<ExportCSVGroups> getExportCSVAdvancedTabs() {
		return exportCSVAdvancedTabs;
	}

	public boolean isExportCVSAdvancedSave() {
		return exportCVSAdvancedSave;
	}

	public void setExportCVSAdvancedSave(boolean exportCVSAdvancedSave) {
		this.exportCVSAdvancedSave = exportCVSAdvancedSave;
	}

	public String getNomeExportCSVAdvanced() {
		return nomeExportCSVAdvanced;
	}

	public void setNomeExportCSVAdvanced(String nomeExportCSVAdvanced) {
		this.nomeExportCSVAdvanced = nomeExportCSVAdvanced;
	}

	public String getExportType() {
		return exportType;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public String getTheSele(){
		theSele = theSele.replaceAll("\\}\\{\\}\\{", "");
		return theSele;
	}

	/**
	 * caricamento da sessione (o eventualmente da file di properties) della modalita' di visualizzazione
	 * dei titoli
	 */
	public String getMode() {
		String mode = (String) getSessionAttribute("titlesmode");
		if (mode == null || (!mode.equals(MODE_LIST) && !mode.equals(MODE_TABLE)))
			mode = DocWayProperties.readProperty("titles.mode", MODE_LIST);
		return mode;
	}

	/**
	 * modifica la modalita' di visualizzazione dei titoli
	 * @param newmode nuova modalita' da impostare
	 * @return
	 * @throws Exception
	 */
	public String changeTitlesMode(String newmode) throws Exception {
		if (newmode != null && (newmode.equals(MODE_LIST) || newmode.equals(MODE_TABLE))) {
			setSessionAttribute("titlesmode", newmode);
		}
		return null;
	}

	/**
	 * indica se visualizzare o meno il pulsante di switch della modalita' di visualizzazione dei titoli (da
	 * lista a tabella o viceversa)
	 *
	 * @return true se il pulsante deve essere visualizzato, false altrimenti
	 */
	public boolean isTitlesSwitchEnabled() {
		if (DocWayProperties.readProperty("titles.mode.switch.enabled", "no").toLowerCase().equals("si"))
			return true;
		else
			return false;
	}

	/**
	 * indica se e' stata attivata o meno la visualizzazione espansa delle icone relative alle info su una lista titoli: Tutte le icone delle info visualizzate
	 * direttamente all'interno della riga relativa al titolo.
	 *
	 * @return true se le info devono essere espanse, false se devono essere raggruppate sotto un'unica icona e visualizzate sull'hover (default)
	 */
	public boolean isTitlesInfoExpanded() {
		if (DocWayProperties.readProperty("titles.info.expanded", "no").toLowerCase().equals("si"))
			return true;
		else
			return false;
	}
	
	/**
	 * Avvio di una specifica azione massiva (selezionata dall'operatore) da lista titoli
	 * @return
	 * @throws Exception
	 */
	public String startAzioneMassiva() throws Exception {
		try {
			Azione azione = (Azione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("azione");
			if (azione != null) {
				// Se l'azione massiva prevede l'inserimento di parametri di input mostro l'apposito popup 
				// modale, altrimenti chiamo la stored procedure richiesta
				if (azione.getParametriInput() != null && !azione.getParametriInput().isEmpty()) {
					AzioneMassivaInputParams azioneMassivaInputParams = new AzioneMassivaInputParams(azione, this); 
					setSessionAttribute("azioneMassivaInputParams", azioneMassivaInputParams);
				}
				else {
					// chiamata a stored procedure richiesta
					return startAzioneMassiva(azione);
				}
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
	 * Avvio di una specifica azione massiva (selezionata dall'operatore) da lista titoli
	 * @param azione Azione da eseguire
	 * @return
	 * @throws Exception
	 */
	public String startAzioneMassiva(Azione azione) throws Exception {
		try {
			AzioneMassivaInputParams azioneMassivaInputParams = (AzioneMassivaInputParams) getSessionAttribute("azioneMassivaInputParams");
			if (azioneMassivaInputParams != null)
				azioneMassivaInputParams.close();
			
			getFormsAdapter().startAzioneMassiva(theSele, azione, Const.DEFAULT_DATE_FORMAT); // TODO Il formato data dovrebbe essere caricato dal file di properties dell'applicazione
			XMLDocumento response = this.getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			
			// caricamento del modale di attesa dell'esecuzione di una stored procedure LUA 
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				AclLuaStoredProcThrobber luaStoredProcThrobber = new AclLuaStoredProcThrobber(this);
				luaStoredProcThrobber.getFormsAdapter().fillFormsFromResponse(response);
				luaStoredProcThrobber.init(response);
				luaStoredProcThrobber.setActive(true);
				setSessionAttribute("luaStoredProcThrobber", luaStoredProcThrobber);
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