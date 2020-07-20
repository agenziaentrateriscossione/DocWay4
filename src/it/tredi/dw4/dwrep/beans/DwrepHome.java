package it.tredi.dw4.dwrep.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayQuery;
import it.tredi.dw4.docway.beans.DocWayTitles;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.PersonalView;
import it.tredi.dw4.docway.model.TitoloRepertorio;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.dom4j.Document;
import org.dom4j.Element;

public class DwrepHome extends DocWayQuery {
	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private String repCodes = "";
	
	private String searchTerms = "";
	private String query = "";
	private boolean estremi = false;
	
	private List<PersonalView> listof_personalView = new ArrayList<PersonalView>();
	private boolean showRicercaGenerica = false;
	private List<TitoloRepertorio> listof_rep = new ArrayList<TitoloRepertorio>();
	
	private String tipoTabella = "";
	private String textTabella = "";
	
	private String excludeInsertRepertori = "";
	
	public DwrepHome() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		
		listof_personalView = XMLUtil.parseSetOfElement(dom, "/response/listof_personalView/personalView", new PersonalView());
		listof_rep = XMLUtil.parseSetOfElement(dom, "/response/listof_rep/repertorio", new TitoloRepertorio());
		repCodes = XMLUtil.parseStrictAttribute(dom, "/response/@dwRepCode");
		
		String[] codes = repCodes.split(",");
		if (codes != null && codes.length != listof_personalView.size()) // verifica se esiste una personalView per ogni repertorio richiesto ...
			showRicercaGenerica = true; // ... se alcuni repertori non prevedono una personalView occorre caricare anche la pagina di ricerca generica 
		
		tipoTabella = "";
		textTabella = "";
		
		// caricamento dei repertori da escludere dalla pagina di inserimento (lettura
		// da properties di DocWay4)
		excludeInsertRepertori = DocWayProperties.readProperty("exclude.insert.codici_repertori", "");
		if (excludeInsertRepertori.length() > 0 && !excludeInsertRepertori.endsWith(","))
			excludeInsertRepertori = excludeInsertRepertori + ",";
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}
	
	public String getRepCodes() {
		return repCodes;
	}

	public void setRepCodes(String repCodes) {
		this.repCodes = repCodes;
	}
	
	public List<PersonalView> getListof_personalView() {
		return listof_personalView;
	}

	public void setListof_personalView(List<PersonalView> listof_personalView) {
		this.listof_personalView = listof_personalView;
	}
	
	public String getTipoTabella() {
		return tipoTabella;
	}

	public void setTipoTabella(String tipoTabella) {
		this.tipoTabella = tipoTabella;
	}

	public String getTextTabella() {
		return textTabella;
	}

	public void setTextTabella(String textTabella) {
		this.textTabella = textTabella;
	}
	
	public List<TitoloRepertorio> getListof_rep() {
		return listof_rep;
	}

	public void setListof_rep(List<TitoloRepertorio> listof_rep) {
		this.listof_rep = listof_rep;
	}
	
	public boolean isShowRicercaGenerica() {
		return showRicercaGenerica;
	}

	public void setShowRicercaGenerica(boolean showRicercaGenerica) {
		this.showRicercaGenerica = showRicercaGenerica;
	}
	
	public String getSearchTerms() {
		return searchTerms;
	}

	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public boolean isEstremi() {
		return estremi;
	}

	public void setEstremi(boolean estremi) {
		this.estremi = estremi;
	}
	
	public String getExcludeInsertRepertori() {
		return excludeInsertRepertori;
	}

	public void setExcludeInsertRepertori(String excludeInsertRepertori) {
		this.excludeInsertRepertori = excludeInsertRepertori;
	}

	@Override
	public QueryFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	/**
	 * caricamento della pagina di ricerca su un repertorio specifico (interfaccia con vista specifica
	 * di un singolo repertorio)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQRepertorioSpecifico() throws Exception {
		PersonalView personalView = (PersonalView) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("personalView");
		return gotoTableQ(personalView);
	}
	
	/**
	 * caricamento pagina di ricerca di una specifica personalView
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQ(PersonalView personalView) throws Exception {
		try {
			String tableName = "";
			if (tipoTabella.equals("A"))
				tableName = Const.DOCWAY_TIPOLOGIA_ARRIVO;
			else if (tipoTabella.equals("P"))
				tableName = Const.DOCWAY_TIPOLOGIA_PARTENZA;
			else if (tipoTabella.equals("I")) 
				tableName = Const.DOCWAY_TIPOLOGIA_INTERNO;
			else if (tipoTabella.equals("V"))
				tableName = Const.DOCWAY_TIPOLOGIA_VARIE;
			
			getFormsAdapter().gotoTableQ(tableName + "#personalView=" + personalView.getTemplate(), false);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificQueryPageAndReturnNavigationRule(tableName, response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * caricamento della pagina di ricerca generica (in assenza di form specifico per il repertorio)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQRicercaGenerica() throws Exception {
		try {
			formsAdapter.gotoTableQ("globale", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			DwrepQueryGlobale dwrepQueryGlobale = new DwrepQueryGlobale();
			dwrepQueryGlobale.setRepCodes(repCodes);
			dwrepQueryGlobale.getFormsAdapter().fillFormsFromResponse(response);
			dwrepQueryGlobale.init(response.getDocument());
			setSessionAttribute("dwrepQueryGlobale", dwrepQueryGlobale);
			
			return "dwrep_query@globale";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Lettura degli attributi tipoTabella e textTabella passati come attributi attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerSelectPersonalView(ActionEvent event){
		tipoTabella = (String) event.getComponent().getAttributes().get("tipoTabella");
		textTabella = (String) event.getComponent().getAttributes().get("textTabella");
	}
		
	/**
	 * caricamento della pagina di inserimento su un repertorio specifico (interfaccia con vista specifica
	 * di un singolo repertorio)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insTableRepertorioSpecifico() throws Exception {
		TitoloRepertorio titoloRepertorio = (TitoloRepertorio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titoloRepertorio");
		return insTableDocRep(titoloRepertorio);
	}
	
	/**
	 * selezione di un repertorio dall'elenco (redirect a docEdit)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insTableDocRep(TitoloRepertorio titoloRepertorio) throws Exception{
		try {
			String tableName = "";
			if (tipoTabella.equals("A"))
				tableName = Const.DOCWAY_TIPOLOGIA_ARRIVO;
			else if (tipoTabella.equals("P"))
				tableName = Const.DOCWAY_TIPOLOGIA_PARTENZA;
			else if (tipoTabella.equals("I")) 
				tableName = Const.DOCWAY_TIPOLOGIA_INTERNO;
			else if (tipoTabella.equals("V"))
				tableName = Const.DOCWAY_TIPOLOGIA_VARIE;
			
			getFormsAdapter().insTableDocRep(tableName, titoloRepertorio.getCodice(), titoloRepertorio.getDescrizione() + " " + textTabella);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(tableName, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * ricerca libera su repertori
	 * @return
	 * @throws Exception
	 */
	public String search() throws Exception {
		try {
			this.query = ""; // Azzero il valore di query in caso di search (caso di precedente query che aveva restituito 'nessun risultato')
			
			// Imposto a vuoto il selId visto che potrebbe essere stato valorizzato
			// attraverso le vaschette
			formsAdapter.getDefaultForm().addParam("selid", "");
			
			String escapedSearchTerms = this.escapeQueryValue(this.searchTerms);
			if (escapedSearchTerms.length() > 0)
				this.query = (!estremi) ? "([@]=" + escapedSearchTerms + " OR [doc_filesfiletesto]=" + escapedSearchTerms + ") AND ([UD,/xw/@UdType/]=\"doc\")" : "[XML,/doc/oggetto]=" + escapedSearchTerms + " OR [XML,/doc/rif_esterni/rif/nome]=" + escapedSearchTerms + " OR [XML,/doc/rif_esterni/rif/referente/@nominativo]=" + escapedSearchTerms;
			
			// forzatura su repertori da gestire..
			String queryRep = "";
			String codes[] = repCodes.split(",");
			for (int i=0; i<codes.length; i++) {
				queryRep = queryRep + "[/doc/repertorio/@cod/]=\"" + codes[i] + "\"";
				if (i < codes.length-1)
					queryRep = queryRep + " OR ";
				
			}
			if (!queryRep.equals(""))
				if (this.query.length() > 0)
					this.query = this.query + "AND (" + queryRep + ")";
				else
					this.query = queryRep;
			
			// Imposto l'ordinamento dei risultati
			String ord = "XML(xpart:/doc/@data_prot:d),XML(xpart:/doc/@num_prot),XML(xpart:/doc/repertorio/@numero)";
			this.formsAdapter.getDefaultForm().addParam("qord", ord); // TODO Andrebbe specificato all'interno di un file di properties
			
			XMLDocumento response = this._queryPlain(this.query, "", "");
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}	
			
			this.xml = response.asXML();
			clearForm();
			
			Element root = response.getRootElement();
			List<?> tit = root.selectNodes("//titolo");
			if (null != tit && !tit.isEmpty()) {
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
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String clearForm() {
		this.searchTerms = "";
		this.estremi = this.formsAdapter.checkBooleanFunzionalitaDisponibile("soloEstremi", false);
		this.query = "";
		
		return null;
	}

}
