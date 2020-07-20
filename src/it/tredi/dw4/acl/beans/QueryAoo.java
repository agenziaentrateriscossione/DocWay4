package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;

import org.dom4j.Document;

public class QueryAoo extends AclQuery {
	private String aoo_nome;
	private String focusElement = "aoo_nome";
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryAoo() throws Exception {
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
		query +=  addQueryField("XML,/aoo/nome", aoo_nome);
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "([UD,/xw/@UdType]=aoo)";
		
		String codSedeRestriction = getCurrentCodSedeRestriction();
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_INT_AOO_RESTRICTION, false))
			query = query.trim() + " AND [aoo_codammaoo]=" + this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
		else if (codSedeRestriction != null && codSedeRestriction.length() > 0)
			query = query.trim() + " AND [aoo_codammaoo]=" + codSedeRestriction;
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/aoo/nome)"); // TODO Andrebbe specificato all'interno di un file di properties
		
		return queryPlain(query);
	}
	
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.aoo_nome = null;
		return null;
	}

	public void setAoo_nome(String nome_profilo) {
		this.aoo_nome = nome_profilo;
	}

	public String getAoo_nome() {
		return aoo_nome;
	}
	
	public String openIndexAooName() throws Exception {
		this.focusElement = "aoo_nome";
		this.openIndex("aoo_nome", "xml,/aoo/nome", this.aoo_nome, "0", "");
		return null;
	}

	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getFocusElement() {
		return focusElement;
	}

}
