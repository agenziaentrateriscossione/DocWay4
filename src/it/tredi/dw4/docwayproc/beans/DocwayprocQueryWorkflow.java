package it.tredi.dw4.docwayproc.beans;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayQuery;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;

public class DocwayprocQueryWorkflow  extends DocWayQuery {

	private String xml = "";
	private DocDocWayQueryFormsAdapter formsAdapter;
	private String focusElement;

	private String bwf_name = "";
	private String bwf_version = "";
	private String bwf_label = "";
	private String bwf_description = "";
	private boolean abilitati = false;
	private boolean non_abilitati = false;
	private boolean subprocess = false;
	private boolean subprocess_no = false;

	public DocwayprocQueryWorkflow() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
		clearForm();
	}

	@Override
	public void init(Document dom) {
		xml = dom.asXML();
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

	public String getBwf_name() {
		return bwf_name;
	}

	public void setBwf_name(String bwf_name) {
		this.bwf_name = bwf_name;
	}

	public String getBwf_version() {
		return bwf_version;
	}

	public void setBwf_version(String bwf_version) {
		this.bwf_version = bwf_version;
	}

	public String getBwf_label() {
		return bwf_label;
	}

	public void setBwf_label(String bwf_label) {
		this.bwf_label = bwf_label;
	}

	public String getBwf_description() {
		return bwf_description;
	}

	public void setBwf_description(String bwf_description) {
		this.bwf_description = bwf_description;
	}

	public boolean isAbilitati() {
		return abilitati;
	}

	public void setAbilitati(boolean abilitati) {
		this.abilitati = abilitati;
	}

	public boolean isNon_abilitati() {
		return non_abilitati;
	}

	public void setNon_abilitati(boolean non_abilitati) {
		this.non_abilitati = non_abilitati;
	}

	@Override
	public String queryPlain() throws Exception {
		try {
			String query = createQuery();

			formsAdapter.findplain();
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
			ShowdocWorkflow showdocWorkflow = new ShowdocWorkflow();
			showdocWorkflow.getFormsAdapter().fillFormsFromResponse(response);
			showdocWorkflow.init(response.getDocument());
			showdocWorkflow.setPopupPage(isPopupPage());
			setSessionAttribute("showdocWorkflow", showdocWorkflow);
			return "docwayproc@showdoc@workflow";
		}
		else {
			DocwayprocTitles titles = new DocwayprocTitles();
			titles.getFormsAdapter().fillFormsFromResponse(response);
			titles.init(response.getDocument());
			titles.setPopupPage(isPopupPage());
			setSessionAttribute("docwayprocTitles", titles);
			return "docwayproc@showtitles@workflow";
		}
	}

	/**
	 * generazione della query di ricerca di workflow
	 * @return
	 */
	private String createQuery() {
		String query = "[UD,/xw/@UdType]=\"bwf_entity\" AND ";

		query +=  addQueryField("bwf_name", this.escapeQueryValue(this.bwf_name));
		query +=  addQueryField("xml,/bwf_entity/@version/", this.escapeQueryValue(this.bwf_version));
		query +=  addQueryField("bwf_label", this.escapeQueryValue(this.bwf_label));
		query +=  addQueryField("bwf_description", this.escapeQueryValue(this.bwf_description));

		if (abilitati && !non_abilitati) 	query += addQueryField("bwf_active", "true");
		if (non_abilitati && !abilitati)		query += addQueryField("bwf_active", "false");

		if (subprocess && !subprocess_no) 	query += addQueryField("xml,/bwf_entity/@subprocess/", "true");
		if (subprocess_no && !subprocess)		query += addQueryField("xml,/bwf_entity/@subprocess/", "false");

		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);

		return query;
	}

	/**
	 * reset del form
	 * @return
	 */
	public String clearForm() {
		bwf_name = "";
		bwf_version = "";
		abilitati = false;
		non_abilitati = false;
		bwf_label = "";
		bwf_description = "";

		return null;
	}

	/**
	 * apertura vocabolario su nome workflow
	 * @return
	 * @throws Exception
	 */
	public String openIndexNameWorkflow() throws Exception {
		this.focusElement = "bwf_name";
		this.openIndex("bwf_name", this.bwf_name, "0", "", false);
		return null;
	}

	/**
	 * apertura vocabolario su vocabolario workflow
	 * @return
	 * @throws Exception
	 */
	public String openIndexVersionWorkflow() throws Exception {
		this.focusElement = "bwf_version";
		this.openIndex("bwf_version", "xml,/bwf_entity/@version/", this.bwf_version, "0", "", false);
		return null;
	}

	/**
	 * apertura vocabolario su label workflow
	 * @return
	 * @throws Exception
	 */
	public String openIndexLabelWorkflow() throws Exception {
		this.focusElement = "bwf_label";
		this.openIndex("bwf_label", this.bwf_label, "0", "", false);
		return null;
	}

	/**
	 * apertura vocabolario su descrizione workflow
	 * @return
	 * @throws Exception
	 */
	public String openIndexDescriptionWorkflow() throws Exception {
		this.focusElement = "bwf_description";
		this.openIndex("bwf_description", this.bwf_description, "0", "", false);
		return null;
	}

	public boolean isSubprocess() {
		return subprocess;
	}

	public void setSubprocess(boolean subprocess) {
		this.subprocess = subprocess;
	}

	public boolean isSubprocess_no() {
		return subprocess_no;
	}

	public void setSubprocess_no(boolean subprocess_no) {
		this.subprocess_no = subprocess_no;
	}

}
