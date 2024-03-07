package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

public class QueryCasellaPostaElettronica extends AclQuery {
	
	private String casellaPostaElettronica_nome;
	private String casellaPostaElettronica_email;
	private String casellaPostaElettronica_tag = "";
	private String casellaPostaElettronica_interop;
	private String casellaPostaElettronica_uff_responsabile;
	private String casellaPostaElettronica_responsabile;
	
	private String focusElement = "casellaPostaElettronica_nome";
	
	private List<Option> tagSelect = new ArrayList<Option>();
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryCasellaPostaElettronica() throws Exception {
		this.formsAdapter = new AclQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
    	tagSelect = XMLUtil.parseSetOfElement(dom, "/response/select_tag/option", new Option());
    	
    	Option empty = new Option();
    	empty.setValue("");
    	empty.setLabel("");
    	tagSelect.add(empty);
    }	
	
	public AclQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String queryPlain() throws Exception {
		String query =  "";
		query +=  addQueryField("XML,/casellaPostaElettronica/@nome", casellaPostaElettronica_nome);
		query +=  addQueryField("XML,/casellaPostaElettronica/mailbox_in/@email", casellaPostaElettronica_email);
		query +=  addQueryField("XML,/casellaPostaElettronica/tag/@value", casellaPostaElettronica_tag);
		if (casellaPostaElettronica_interop != null && !casellaPostaElettronica_interop.isEmpty())
			query += "([XML,/casellaPostaElettronica/@interop]=\"" + casellaPostaElettronica_interop + "\") AND ";
		query +=  addQueryField("XML,/casellaPostaElettronica/responsabile/@nome_pers", casellaPostaElettronica_responsabile);
		query +=  addQueryField("XML,/casellaPostaElettronica/responsabile/@nome_uff", casellaPostaElettronica_uff_responsabile);
		
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if ("".equals(query.trim())) 
			query = "([UD,/xw/@UdType]=casellaPostaElettronica)";
		
		String codSedeRestriction = getCurrentCodSedeRestriction();
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_INT_AOO_RESTRICTION, false))
			query = query.trim() + " AND [casellaPostaElettronica_codammaoo]=" + this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
		else if (codSedeRestriction != null && codSedeRestriction.length() > 0)
			query = query.trim() + " AND [casellaPostaElettronica_codammaoo]=" + codSedeRestriction;
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/casellaPostaElettronica/@nome)");
		
		return queryPlain(query);
	}
	
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.casellaPostaElettronica_nome = null;
		this.casellaPostaElettronica_email = null;
		this.casellaPostaElettronica_tag = "";
		this.casellaPostaElettronica_interop = null;
		this.casellaPostaElettronica_uff_responsabile = null;
		this.casellaPostaElettronica_responsabile = null;
		
		return null;
	}

	
	/**
	 * Vocabolario su campo nome casellaPostaElettronica
	 * @return
	 * @throws Exception
	 */
	public String openIndexMailboxName() throws Exception {
		this.focusElement = "casellaPostaElettronica_nome";
		this.openIndex("casellaPostaElettronica_nome", "xml,/casellaPostaElettronica/@nome", this.casellaPostaElettronica_nome, "0", " ");
		return null;
	}
	
	/**
	 * Vocabolario su campo email
	 * @return
	 * @throws Exception
	 */
	public String openIndexMailboxEmail() throws Exception {
		this.focusElement = "casellaPostaElettronica_email";
		this.openIndex("casellaPostaElettronica_email", "xml,/casellaPostaElettronica/mailbox_in/@email", this.casellaPostaElettronica_email, "0", "");
		return null;
	}
	
	/**
	 * Vocabolario su campo ufficio responsabile
	 * @return
	 * @throws Exception
	 */
	public String openIndexMailboxUffResponsabile() throws Exception {
		this.focusElement = "casellaPostaElettronica_uff_responsabile";
		this.openIndex("casellaPostaElettronica_uff_responsabile", "xml,/casellaPostaElettronica/responsabile/@nome_uff", this.casellaPostaElettronica_uff_responsabile, "0", "");
		return null;
	}
	
	/**
	 * Vocabolario su campo responsabile
	 * @return
	 * @throws Exception
	 */
	public String openIndexMailboxResponsabile() throws Exception {
		this.focusElement = "casellaPostaElettronica_responsabile";
		this.openIndex("casellaPostaElettronica_responsabile", "xml,/casellaPostaElettronica/responsabile/@nome_pers", this.casellaPostaElettronica_responsabile, "0", "");
		return null;
	}
	
	public String getCasellaPostaElettronica_nome() {
		return casellaPostaElettronica_nome;
	}

	public void setCasellaPostaElettronica_nome(String casellaPostaElettronica_nome) {
		this.casellaPostaElettronica_nome = casellaPostaElettronica_nome;
	}

	public String getCasellaPostaElettronica_email() {
		return casellaPostaElettronica_email;
	}

	public void setCasellaPostaElettronica_email(String casellaPostaElettronica_email) {
		this.casellaPostaElettronica_email = casellaPostaElettronica_email;
	}

	public String getCasellaPostaElettronica_tag() {
		return casellaPostaElettronica_tag;
	}

	public void setCasellaPostaElettronica_tag(String casellaPostaElettronica_tag) {
		this.casellaPostaElettronica_tag = casellaPostaElettronica_tag;
	}

	public String getCasellaPostaElettronica_interop() {
		return casellaPostaElettronica_interop;
	}

	public void setCasellaPostaElettronica_interop(String casellaPostaElettronica_interop) {
		this.casellaPostaElettronica_interop = casellaPostaElettronica_interop;
	}

	public String getCasellaPostaElettronica_uff_responsabile() {
		return casellaPostaElettronica_uff_responsabile;
	}

	public void setCasellaPostaElettronica_uff_responsabile(String casellaPostaElettronica_uff_responsabile) {
		this.casellaPostaElettronica_uff_responsabile = casellaPostaElettronica_uff_responsabile;
	}

	public String getCasellaPostaElettronica_responsabile() {
		return casellaPostaElettronica_responsabile;
	}

	public void setCasellaPostaElettronica_responsabile(String casellaPostaElettronica_responsabile) {
		this.casellaPostaElettronica_responsabile = casellaPostaElettronica_responsabile;
	}

	public List<Option> getTagSelect() {
		return tagSelect;
	}

	public void setTagSelect(List<Option> tagSelect) {
		this.tagSelect = tagSelect;
	}

	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getFocusElement() {
		return focusElement;
	}
	
}
