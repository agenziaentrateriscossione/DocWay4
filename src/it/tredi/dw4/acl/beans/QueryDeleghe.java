package it.tredi.dw4.acl.beans;

import org.dom4j.Document;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.utils.XMLDocumento;

public class QueryDeleghe extends AclQuery {
	
	private String xml;
	//campi di ricerca
	private boolean attiva;
	private boolean nonattiva;
	private boolean sostituto;
	private boolean nonsostituto;
	private boolean permanente;
	private boolean uneditable;
	private boolean nonuneditable;
	private String cod_delegante;
	private String cogn_nome_delegante;
	private String cod_delegato;
	private String cogn_nome_delegato;
	private String durata_inizio_from;
	private String durata_inizio_to;
	private String durata_fine_from;
	private String durata_fine_to;
	private String focusElement = "cod_delegante";
	
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public boolean isAttiva() {
		return attiva;
	}
	public void setAttiva(boolean attiva) {
		this.attiva = attiva;
	}
	public boolean isNonattiva() {
		return nonattiva;
	}
	public void setNonattiva(boolean nonattiva) {
		this.nonattiva = nonattiva;
	}
	public boolean isSostituto() {
		return sostituto;
	}
	public void setSostituto(boolean sostituto) {
		this.sostituto = sostituto;
	}
	public boolean isNonsostituto() {
		return nonsostituto;
	}
	public void setNonsostituto(boolean nonsostituto) {
		this.nonsostituto = nonsostituto;
	}
	public boolean isPermanente() {
		return permanente;
	}
	public void setPermanente(boolean permanente) {
		this.permanente = permanente;
	}
	public boolean isUneditable() {
		return uneditable;
	}
	public void setUneditable(boolean uneditable) {
		this.uneditable = uneditable;
	}
	public boolean isNonuneditable() {
		return nonuneditable;
	}
	public void setNonuneditable(boolean nonuneditable) {
		this.nonuneditable = nonuneditable;
	}
	public String getCod_delegante() {
		return cod_delegante;
	}
	public void setCod_delegante(String cod_delegante) {
		this.cod_delegante = cod_delegante;
	}
	public String getCogn_nome_delegante() {
		return cogn_nome_delegante;
	}
	public void setCogn_nome_delegante(String cogn_nome_delegante) {
		this.cogn_nome_delegante = cogn_nome_delegante;
	}
	public String getCod_delegato() {
		return cod_delegato;
	}
	public void setCod_delegato(String cod_delegato) {
		this.cod_delegato = cod_delegato;
	}
	public String getCogn_nome_delegato() {
		return cogn_nome_delegato;
	}
	public void setCogn_nome_delegato(String cogn_nome_delegato) {
		this.cogn_nome_delegato = cogn_nome_delegato;
	}
	public String getDurata_inizio_from() {
		return durata_inizio_from;
	}
	public void setDurata_inizio_from(String durata_inizio_from) {
		this.durata_inizio_from = durata_inizio_from;
	}
	public String getDurata_inizio_to() {
		return durata_inizio_to;
	}
	public void setDurata_inizio_to(String durata_inizio_to) {
		this.durata_inizio_to= durata_inizio_to;
	}
	public String getDurata_fine_from() {
		return durata_fine_from;
	}
	public void setDurata_fine_from(String durata_fine_from) {
		this.durata_fine_from = durata_fine_from;
	}
	public String getDurata_fine_to() {
		return durata_fine_to;
	}
	public void setDurata_fine_to(String durata_fine_to) {
		this.durata_fine_to= durata_fine_to;
	}
	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}
	public String getFocusElement() {
		return focusElement;
	}

	public void init(Document dom) {
		this.xml = dom.asXML();
    }

	private AclQueryFormsAdapter formsAdapter;
	
	@Override
	public AclQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public QueryDeleghe() throws Exception {
		this.formsAdapter = new AclQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}

	@Override
	public String queryPlain() throws Exception {
		String query =  "";
		query += addQueryField("persint_matricola", this.cod_delegante);
		query += addQueryField("persint_cognome", this.cogn_nome_delegante);
		query += addQueryField("/persona_interna/deleghe/delega/@cod_persona", this.cod_delegato);
		query += addQueryField("/persona_interna/deleghe/delega/@nome_persona", this.cogn_nome_delegato);
		if (this.permanente) {
			query += addQueryField("/persona_interna/deleghe/delega/@permanente", "true");
		}
		else {
			if (this.durata_inizio_from.trim().length() > 0 || this.durata_inizio_to.trim().length() > 0){
	        	String qDataInizio = addDateRangeQuery("/persona_interna/deleghe/delega/@data_inizio", this.durata_inizio_from, this.durata_inizio_to, "AND");
	    		query += qDataInizio;
	        }
			if (this.durata_fine_from.trim().length() > 0 || this.durata_fine_to.trim().length() > 0){
				String qDataFine = addDateRangeQuery("/persona_interna/deleghe/delega/@data_fine", this.durata_fine_from, this.durata_fine_to, "AND");
				query += qDataFine;
			}
		}
		if (this.attiva && !this.nonattiva) query += addQueryField("/persona_interna/deleghe/delega/@attiva", "true");
		if (!this.attiva && this.nonattiva) query += addQueryField("/persona_interna/deleghe/delega/@attiva", "false");
		if (this.sostituto && !this.nonsostituto) query += addQueryField("/persona_interna/deleghe/delega/@sostituto", "true");
		if (!this.sostituto && this.nonsostituto) query += addQueryField("/persona_interna/deleghe/delega/@sostituto", "false");
		if (this.uneditable && !this.nonuneditable) query += addQueryField("/persona_interna/deleghe/delega/@uneditable", "true");
		if (!this.uneditable && this.nonuneditable) query += addQueryField("/persona_interna/deleghe/delega/@uneditable", "false");
		
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		
		try {
			formsAdapter.gotoControlloDeleghe(query);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			DelegheTitles delegheTitles = new DelegheTitles();
			delegheTitles.getFormsAdapter().fillFormsFromResponse(response);
			delegheTitles.init(response.getDocument());
			setSessionAttribute("delegheTitles", delegheTitles);
			return "acl_showdeleghetitles";

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String openIndexCodiceDelegante() throws Exception {
		this.focusElement = "cod_delegante";
		this.openIndex("cod_delegante", "persint_matricola", this.cod_delegante, "0", null);
		return null;
	}
	public String openIndexNomeDelegante() throws Exception {
		this.focusElement = "cogn_nome_delegante";
		this.openIndex("cogn_nome_delegante", "persint_nomcogn", this.cogn_nome_delegante, "0", null);
		return null;
	}
	public String openIndexCodiceDelegato() throws Exception {
		this.focusElement = "cod_delegato";
		this.openIndex("cod_delegato", "persint_matricola", this.cod_delegato, "0", null);
		return null;
	}
	public String openIndexNomeDelegato() throws Exception {
		this.focusElement = "cogn_nome_delegato";
		this.openIndex("cogn_nome_delegato", "persint_nomcogn", this.cogn_nome_delegato, "0", null);
		return null;
	}
	
	public void resetQuery() {
		this.attiva = false;
		this.nonattiva = false;
		this.sostituto = false;
		this.nonsostituto = false;
		this.permanente = false;
		this.uneditable = false;
		this.nonuneditable = false;
		this.cod_delegante = null;
		this.cogn_nome_delegante = null;
		this.cod_delegato = null;
		this.cogn_nome_delegato = null;
		this.durata_inizio_from = null;
		this.durata_inizio_to = null;
		this.durata_fine_from = null;
		this.durata_fine_to = null;
		this.focusElement = "cod_delegante";
	}
	public void cleanDurata() {
		this.durata_inizio_from = null;
		this.durata_inizio_from = null;
		this.durata_fine_from = null;
		this.durata_fine_to = null;
	}
}
