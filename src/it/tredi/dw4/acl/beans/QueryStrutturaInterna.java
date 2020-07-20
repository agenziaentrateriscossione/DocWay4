package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;

import org.dom4j.Document;

public class QueryStrutturaInterna extends AclQuery {
	private String struint_coduff;
	private String struint_nome;
	private String struint_tipologia;
	private String struint_indirizzocomune;
	private String struint_indirizzoprov;
	private String struint_competenze;
	private String struint_operatore;
	private String struint_uffoperatore;
	private String focusElement = "input_struint_coduff";
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryStrutturaInterna() throws Exception {
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
		//TODO - chiamare con i parametri corretti
		String query =  "";
		query +=  addQueryField("struint_coduff", struint_coduff);
		query +=  addQueryField("struint_nome", struint_nome);
		query +=  addQueryField("struint_tipologia", struint_tipologia);
		query +=  addQueryField("struint_indirizzocomune", struint_indirizzocomune);
		query +=  addQueryField("struint_indirizzoprov", struint_indirizzoprov);
		query +=  addQueryField("struint_competenze", struint_competenze);
		query +=  addQueryField("struint_operatore", struint_operatore);
		query +=  addQueryField("struint_uffoperatore", struint_uffoperatore);
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "[UD,/xw/@UdType]=struttura_interna";
		
		String codSedeRestriction = getCurrentCodSedeRestriction();
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_INT_AOO_RESTRICTION, false))
			query = query.trim() + " AND [struint_codammaoo]=" + this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
		else if (codSedeRestriction != null && codSedeRestriction.length() > 0)
			query = query.trim() + " AND [struint_codammaoo]=" + codSedeRestriction;
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/struttura_interna/nome))"); // TODO Andrebbe specificato all'interno di un file di properties
		
		return queryPlain(query);
	}
	
	public void setStruint_coduff(String cod) {
		this.struint_coduff = cod;
	}

	public String getStruint_coduff() {
		return struint_coduff;
	}

	public void setStruint_nome(String descr) {
		this.struint_nome = descr;
	}

	public String getStruint_nome() {
		return struint_nome;
	}

	public void setStruint_tipologia(String tipo) {
		this.struint_tipologia = tipo;
	}

	public String getStruint_tipologia() {
		return struint_tipologia;
	}

	public void setStruint_indirizzocomune(String comune) {
		this.struint_indirizzocomune = comune;
	}

	public String getStruint_indirizzocomune() {
		return struint_indirizzocomune;
	}

	public void setStruint_competenze(String competenze) {
		this.struint_competenze = competenze;
	}

	public String getStruint_competenze() {
		return struint_competenze;
	}

	public void setStruint_indirizzoprov(String provincia) {
		this.struint_indirizzoprov = provincia;
	}

	public String getStruint_indirizzoprov() {
		return struint_indirizzoprov;
	}

	public void setStruint_operatore(String operatore) {
		this.struint_operatore = operatore;
	}

	public String getStruint_operatore() {
		return struint_operatore;
	}

	public void setStruint_uffoperatore(String uff_operatore) {
		this.struint_uffoperatore = uff_operatore;
	}

	public String getStruint_uffoperatore() {
		return struint_uffoperatore;
	}
	
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.struint_coduff = null;
		this.struint_nome = null;
		this.struint_tipologia = null;
		this.struint_indirizzocomune = null;
		this.struint_indirizzoprov = null;
		this.struint_competenze = null;
		this.struint_operatore = null;
		this.struint_uffoperatore = null;
		return null;
	}
	
	public String openIndexCodiceUnita() throws Exception {
		this.focusElement = "input_struint_coduff";
		this.openIndex("struint_coduff", this.struint_coduff, "0", null);
		return null;
	}
	
	public String openIndexNomeStruttura() throws Exception {
		this.focusElement = "input_struint_nome";
		this.openIndex("struint_nome", this.struint_nome, "0", " ");
		return null;
	}
	public String openIndexTipologia() throws Exception {
		this.focusElement = "input_struint_tipologia";
		this.openIndex("struint_tipologia", this.struint_tipologia, "0", null);
		return null;
	}
	public String openIndexCompetenze() throws Exception {
		this.focusElement = "input_struint_competenze";
		this.openIndex("struint_competenze", this.struint_competenze, "0", null);
		return null;
	}
	public String openIndexIndirizzoComune() throws Exception {
		this.focusElement = "input_struint_indirizzocomune";
		this.openIndex("struint_indirizzocomune", this.struint_indirizzocomune, " 0", " ");
		return null;
	}
	public String openIndexIndirizzoProv() throws Exception {
		this.focusElement = "input_struint_indirizzoprov";
		this.openIndex("struint_indirizzoprov", this.struint_indirizzoprov, "0", null);
		return null;
	}
	public String openIndexOperatore() throws Exception {
		this.focusElement = "input_struint_operatore";
		this.openIndex("struint_operatore", this.struint_operatore, "0", null);
		return null;
	}
	public String openIndexUffOperatore() throws Exception {
		this.focusElement = "input_struint_uffoperatore";
		this.openIndex("struint_uffoperatore", this.struint_uffoperatore, "0", null);
		return null;
	}
	

	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getFocusElement() {
		return focusElement;
	}

	

}
