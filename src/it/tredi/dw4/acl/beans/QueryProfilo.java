package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;

import org.dom4j.Document;

public class QueryProfilo extends AclQuery {
	private String profilo_matricola;
	private String profilo_nome;
	private String focusElement = "cod_profilo";
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryProfilo() throws Exception {
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
		query +=  addQueryField("profilo_matricola", profilo_matricola);
		query +=  addQueryField("profilo_nome", profilo_nome);
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "([UD,/xw/@UdType]=persona_interna) and ([persint_tipo]=profilo)";
		
		String codSedeRestriction = getCurrentCodSedeRestriction();
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_INT_AOO_RESTRICTION, false))
			query = query.trim() + " AND [persint_codammaoo]=" + this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
		else if (codSedeRestriction != null && codSedeRestriction.length() > 0)
			query = query.trim() + " AND [persint_codammaoo]=" + codSedeRestriction;
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/persona_interna/@nome_profilo)"); // TODO Andrebbe specificato all'interno di un file di properties
		
		return queryPlain(query);
	}
	
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.profilo_matricola = null;
		this.profilo_nome = null;
		return null;
	}

	public void setProfilo_matricola(String cod_profilo) {
		this.profilo_matricola = cod_profilo;
	}

	public String getProfilo_matricola() {
		return profilo_matricola;
	}

	public void setProfilo_nome(String nome_profilo) {
		this.profilo_nome = nome_profilo;
	}

	public String getProfilo_nome() {
		return profilo_nome;
	}
	
	public String openIndexProfileCod() throws Exception {
		this.focusElement = "profilo_matricola";
		this.openIndex("profilo_matricola", this.profilo_matricola, "0", null);
		return null;
	}
	public String openIndexProfileName() throws Exception {
		this.focusElement = "profilo_nome";
		this.openIndex("profilo_nome", this.profilo_nome, "0", " ");
		return null;
	}

	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getFocusElement() {
		return focusElement;
	}

}
