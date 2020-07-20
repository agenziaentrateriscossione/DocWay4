package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.AclLoadingbar;
import it.tredi.dw4.acl.beans.AclLoadingbarStampe;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.TitlesFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayHome;
import it.tredi.dw4.docway.beans.DocWayLoadingbar;
import it.tredi.dw4.docway.beans.Menu;
import it.tredi.dw4.docway.model.ExportGroup;
import it.tredi.dw4.docway.model.ExportPersonalizzato;
import it.tredi.dw4.docway.model.Option;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.dom4j.Document;
import org.dom4j.Element;

public abstract class Titles extends Page {
	
	protected List<Titolo> titoli;
	
	protected String colSort = "";
	protected String xwOrd = "";
	protected int xwOrdIndex = 0; // indice della tipologia di ordinamento selezionata (da tendina)
	protected boolean ascSort = true; 
	
	protected String theSele = "";
	protected String action;
	
	//variabli per export CSV avanzato
	protected boolean exportCVSAdvancedOpened = false;
	protected List<ExportCSVGroups> exportCSVAdvancedTabs = new ArrayList<ExportCSVGroups>();
	protected boolean exportCVSAdvancedSave = false;
	protected String nomeExportCSVAdvanced = "";
	
	// tipologie di ordinamento dei titoli
	protected List<Option> ordinamentoSelect = new ArrayList<Option>();
	
	private String currentPage = "";  // utilizzata per la navigazione dei risultati di una ricerca tramite campo testo
	
	// modalita' di visualizzazione della lista titoli
	public static final String MODE_LIST = "list";
	public static final String MODE_TABLE = "table";
	
	public abstract void init(Document dom);
	
	public abstract TitlesFormsAdapter getFormsAdapter();
	
	public abstract String mostraDocumento() throws Exception;
	
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
		String tipo = (null != db && !"".equals(db.trim())) ? db : getFormsAdapter().getDefaultForm().getParam("db");
		String dbTable = (null != table && !"".equals(table.trim())) ? table : getFormsAdapter().getDefaultForm().getParam("dbTable");
		getFormsAdapter().mostraDocumento(pos, tipo, 0, "", dbTable);
		
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
		try {
			if (StringUtil.isNumber(currentPage)) {
				int curr = new Integer(currentPage).intValue();
				if (curr > 0 && curr <= getFormsAdapter().getTotal()) {
					getFormsAdapter().paginaSpecifica(curr);
					XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
					getFormsAdapter().fillFormsFromResponse(response);
					init(response.getDocument());			
				}
				else
					currentPage = getFormsAdapter().getCurrent() + "";
			}
			else
				currentPage = getFormsAdapter().getCurrent() + "";
			
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
			this.getExportCSVAdvancedTabs().clear();
			this.nomeExportCSVAdvanced = "";
			this.exportCVSAdvancedSave = false;

			ExportPersonalizzato exportPersonalizzato = (ExportPersonalizzato) getSessionAttribute("docwayExportPersonalizzato");
			
			//estrai dalla response le colonne per popup csv
			@SuppressWarnings("unchecked")
			List<Element> exports = response.selectNodes("//exports/export");
			for (Element export : exports) {
				ExportCSVGroups groups = new ExportCSVGroups(export);
				
				if (exportPersonalizzato != null) {
					for (ExportGroup group : groups.getGroups()) {
						boolean shouldSelect = exportPersonalizzato.isGroupSaved(group);
						group.setSelected(shouldSelect);
					}
					
					this.nomeExportCSVAdvanced = exportPersonalizzato.getTitle();
				}
				
				this.getExportCSVAdvancedTabs().add(groups);
			}
			
			return "";
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
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
	
}