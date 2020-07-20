package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;

import org.dom4j.Document;

public class QueryGruppo extends AclQuery {
	private String gruppi_nome;
	private String gruppi_descrizionegr;
	private String focusElement = "gruppi_nome";
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryGruppo() throws Exception {
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
		query +=  addQueryField("gruppi_nome", gruppi_nome);
		query +=  addQueryField("gruppi_descrizionegr", gruppi_descrizionegr);
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "[UD,/xw/@UdType]=gruppo";
		
		String codSedeRestriction = getCurrentCodSedeRestriction();
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_INT_AOO_RESTRICTION, false))
			query = query.trim() + " AND [gruppi_codammaoo]=" + this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
		else if (codSedeRestriction != null && codSedeRestriction.length() > 0)
			query = query.trim() + " AND [gruppi_codammaoo]=" + codSedeRestriction;
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/gruppo/nome)"); // TODO Andrebbe specificato all'interno di un file di properties
		
		return queryPlain(query);
	}
	
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.gruppi_nome = null;
		this.gruppi_descrizionegr = null;
		return null;
	}

	public void setGruppi_nome(String nome) {
		this.gruppi_nome = nome;
	}

	public String getGruppi_nome() {
		return gruppi_nome;
	}

	public void setGruppi_descrizionegr(String descrizione) {
		this.gruppi_descrizionegr = descrizione;
	}

	public String getGruppi_descrizionegr() {
		return gruppi_descrizionegr;
	}

	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getFocusElement() {
		return focusElement;
	}
	
	public String openIndexNome() throws Exception {
		this.focusElement = "gruppi_nome";
		this.openIndex("gruppi_nome", this.gruppi_nome, "0", " ");
		return null;
	}
	public String openIndexDescrizione() throws Exception {
		this.focusElement = "gruppi_descrizionegr";
		this.openIndex("gruppi_descrizionegr", this.gruppi_descrizionegr, "0", null);
		return null;
	}

}
