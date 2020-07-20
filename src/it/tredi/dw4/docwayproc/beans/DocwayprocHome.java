package it.tredi.dw4.docwayproc.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayQuery;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.Ordinamento_select;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Iterator;

import org.dom4j.Document;

public class DocwayprocHome extends DocWayQuery {

	private String xml = "";
	private DocDocWayQueryFormsAdapter formsAdapter;
	private String focusElement;
	
	// campi di ricerca su voci di indice
	private String search_globale = "";
	private String search_tit_voce = "";
	private String search_uor = "";
	private String search_rpa = "";
	private String ordinamento = "";
	private Ordinamento_select ordinamentoIndiceTitolario_select = new Ordinamento_select();
	
	public DocwayprocHome() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
		clearForm();
	}
	
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		
		ordinamentoIndiceTitolario_select.init(XMLUtil.createDocument(dom, "/response/ordinamentoIndiceTitolario_select"));
		for (Iterator<Option> iterator = ordinamentoIndiceTitolario_select.getOptions().iterator(); iterator.hasNext();) {
			Option option = (Option) iterator.next();
			if (option.getSelected().length()>0)
				ordinamento = option.getValue();
		}
	}

	@Override
	public QueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getFocusElement() {
		return focusElement;
	}

	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getSearch_globale() {
		return search_globale;
	}

	public void setSearch_globale(String search_globale) {
		this.search_globale = search_globale;
	}

	public String getSearch_tit_voce() {
		return search_tit_voce;
	}

	public void setSearch_tit_voce(String search_tit_voce) {
		this.search_tit_voce = search_tit_voce;
	}

	public String getSearch_uor() {
		return search_uor;
	}

	public void setSearch_uor(String search_uor) {
		this.search_uor = search_uor;
	}

	public String getSearch_rpa() {
		return search_rpa;
	}

	public void setSearch_rpa(String search_rpa) {
		this.search_rpa = search_rpa;
	}
	
	public String getOrdinamento() {
		return ordinamento;
	}

	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}

	public Ordinamento_select getOrdinamentoIndiceTitolario_select() {
		return ordinamentoIndiceTitolario_select;
	}

	public void setOrdinamentoIndiceTitolario_select(
			Ordinamento_select ordinamentoIndiceTitolario_select) {
		this.ordinamentoIndiceTitolario_select = ordinamentoIndiceTitolario_select;
	}
	
	/**
	 * costruzione della query su voci di indice
	 * @return
	 */
	private String createQuery() {
		String query = "[UD,/xw/@UdType]=\"indice_titolario\" AND ";
		
		query +=  addQueryField("@", this.search_globale);
		query +=  addQueryField("tit_voce", this.escapeQueryValue(this.search_tit_voce));
		
		if (this.search_uor != null && this.search_uor.length() > 0) {
			query += "(";
			if (this.search_uor.contains(" or "))
				query += addRangeQuery("tit_rifinternirifnomeuff", this.search_uor.split(" or "), "ADJ");
			else
				query += addQueryField("tit_rifinternirifnomeuff", this.search_uor, "ADJ");
			query += "([tit_rifinternirifdiritto]=RPA)";
			query += ") AND ";
		}
		if (this.search_rpa != null && this.search_rpa.length() > 0) {
			query += "(";
			if (this.search_rpa.contains(" or "))
				query += addRangeQuery("tit_rifinternirifnomepersona", this.search_rpa.split(" or "), "ADJ");
			else
				query += addQueryField("tit_rifinternirifnomepersona", this.search_uor, "ADJ");
			query += "([tit_rifinternirifdiritto]=RPA)";
			query += ") AND ";
		}
		
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		
		this.formsAdapter.getDefaultForm().addParam("qord", ordinamento);
		
		return query;
	}
	
	@Override
	public String queryPlain() throws Exception {
		try {
			String query = createQuery();
			if("error".equals(query)) return null;
			formsAdapter.findplain();
			return queryPlain(query);
			
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Esecuzione della query di ricerca costruita
	 */
	public String queryPlain(String query) throws Exception {
		try {
			XMLDocumento response = super._queryPlain(query, "", "");
			if (handleErrorResponse(response)) 
				return null;
			
			return navigateResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Data la response derivante da una ricerca carica la pagina di destinazione 
	 * corretta: pagina dei titoli in caso di piu' risultati, showdoc in caso di un 
	 * solo documento restituito
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	 @Override
	public String navigateResponse(XMLDocumento response) throws Exception{
		if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")) {
			ShowdocIndiceTitolario showdocIndiceTitolario = new ShowdocIndiceTitolario();
			showdocIndiceTitolario.getFormsAdapter().fillFormsFromResponse(response);
			showdocIndiceTitolario.init(response.getDocument());
			showdocIndiceTitolario.setPopupPage(isPopupPage());
			setSessionAttribute("showdocIndiceTitolario", showdocIndiceTitolario);
			return "docwayproc@showdoc@indice_titolario";
		}
		else {
			DocwayprocTitles titles = new DocwayprocTitles();
			titles.getFormsAdapter().fillFormsFromResponse(response);
			titles.init(response.getDocument());
			titles.setPopupPage(isPopupPage());
			setSessionAttribute("docwayprocTitles", titles);
			return "docwayproc@showtitles";
		}
	}
	
	/**
	 * reset del form
	 * @return
	 */
	public String clearForm() {
		search_globale = "";
		search_tit_voce = "";
		search_uor = "";
		search_rpa = "";
		
		return null;
	}
	
	/**
	 * apertura del vocabolario su voce di indice
	 * @return
	 * @throws Exception
	 */
	public String openIndexTitVoce() throws Exception {
		this.focusElement = "tit_voce";
		this.openIndex("search_tit_voce", "tit_voce", this.search_tit_voce, "0", " ", false);
		return null;
	}
	
	/**
	 * apertura del vocabolario su UOR
	 * @return
	 * @throws Exception
	 */
	public String openIndexUor() throws Exception {
		this.focusElement = "search_uor";
		this.openIndex("search_uor", "tit_rifinternirifdirittonomeuff", this.search_uor, "0", "RPA|^| ", false);
		return null;
	}
	
	/**
	 * apertura del vocabolario su RPA
	 * @return
	 * @throws Exception
	 */
	public String openIndexRpa() throws Exception {
		this.focusElement = "search_rpa";
		this.openIndex("search_rpa", "tit_rifinternirifdirittonomepersona", this.search_rpa, "0", "RPA|^| ", false);
		return null;
	}

}
