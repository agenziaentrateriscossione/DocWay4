package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;

import org.dom4j.Document;

public class QueryComune extends AclQuery {
	private String comuni_nome;
	private String comuni_prov;
	private String comuni_regione;
	private String comuni_prefisso;
	private String comuni_nazione;
	private String comuni_codiceistat;
	private String focusElement = "comuni_nome";
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryComune() throws Exception {
		this.formsAdapter = new AclQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document dom) {
    	//DO NOTHING
    }	
	
	public AclQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	//TEMP
	public String queryPlain() throws Exception {
		String query =  "";
		query +=  addQueryField("comuni_nome", comuni_nome);
		query +=  addQueryField("comuni_prov", comuni_prov);
		query +=  addQueryField("comuni_regione", comuni_regione);
		query +=  addQueryField("comuni_prefisso", comuni_prefisso);
		query +=  addQueryField("comuni_nazione", comuni_nazione);
		query +=  addQueryField("comuni_codiceistat", comuni_codiceistat);
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "[UD,/xw/@UdType]=comune";
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/comune/@nome),xml(xpart:/comune/@cap)"); // TODO Andrebbe specificato all'interno di un file di properties
		
		return queryPlain(query);
	}
	
	
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.comuni_nome = null;
		this.comuni_prov = null;
		this.comuni_codiceistat = null;
		this.comuni_nazione = null;
		this.comuni_prefisso = null;
		this.comuni_regione = null;
		return null;
	}

	public void setComuni_nome(String cod_profilo) {
		this.comuni_nome = cod_profilo;
	}

	public String getComuni_nome() {
		return comuni_nome;
	}

	public void setComuni_prov(String nome_profilo) {
		this.comuni_prov = nome_profilo;
	}

	public String getComuni_prov() {
		return comuni_prov;
	}

	public void setComuni_regione(String regione) {
		this.comuni_regione = regione;
	}

	public String getComuni_regione() {
		return comuni_regione;
	}

	public void setComuni_nazione(String nazione) {
		this.comuni_nazione = nazione;
	}

	public String getComuni_nazione() {
		return comuni_nazione;
	}

	public void setComuni_prefisso(String pref_tel) {
		this.comuni_prefisso = pref_tel;
	}

	public String getComuni_prefisso() {
		return comuni_prefisso;
	}

	public void setComuni_codiceistat(String codice_istat) {
		this.comuni_codiceistat = codice_istat;
	}

	public String getComuni_codiceistat() {
		return comuni_codiceistat;
	}

	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getFocusElement() {
		return focusElement;
	}
	
	public String openIndexNome() throws Exception {
		this.focusElement = "comuni_nome";
		this.openIndex("comuni_nome", this.comuni_nome, "0", " ");
		return null;
	}
	public String openIndexProvincia() throws Exception {
		this.focusElement = "comuni_prov";
		this.openIndex("comuni_prov", this.comuni_prov, "0", null);
		return null;
	}
	public String openIndexRegione() throws Exception {
		this.focusElement = "comuni_regione";
		this.openIndex("comuni_regione", this.comuni_regione, "0", " ");
		return null;
	}
	public String openIndexNazione() throws Exception {
		this.focusElement = "comuni_nazione";
		this.openIndex("comuni_nazione", this.comuni_nazione, "0", null);
		return null;
	}
	public String openIndexPrefisso() throws Exception {
		this.focusElement = "comuni_prefisso";
		this.openIndex("comuni_prefisso", this.comuni_prefisso, "0", null);
		return null;
	}
	public String openIndexCodiceIstat() throws Exception {
		this.focusElement = "comuni_codiceistat";
		this.openIndex("comuni_codiceistat", this.comuni_codiceistat, "0", null);
		return null;
	}

}
