package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;

import org.dom4j.Document;

public class QueryPersonaInterna extends AclQuery {
	
	private String xml = "";
	
	private String persint_matricola;
	private String persint_cognome;
	private String persint_loginname;
	private String persint_soprannome;
	private String persint_qualifica;
	private String persint_competenze;
	private String persint_diritti;
	private String persint_operatore;
	private String persint_uffoperatore;
	private String persint_profilecod;
	private String persint_profilename;
	private boolean persint_dirittipersonalizzati = false;
	private String focusElement = "persint_matricola";
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryPersonaInterna() throws Exception {
		this.formsAdapter = new AclQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document dom) {
		this.xml = dom.asXML();
    }	
	
	public AclQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	//TEMP
	public String queryPlain() throws Exception {
		String query =  "";
		query +=  addQueryField("persint_matricola", persint_matricola);
		query +=  addQueryField("persint_cognome", persint_cognome);
		query +=  addQueryField("persint_loginname", persint_loginname);
		query +=  addQueryField("persint_soprannome", persint_soprannome);
		query +=  addQueryField("persint_qualifica", persint_qualifica);
		query +=  addQueryField("persint_diritti", persint_diritti);
		query +=  addQueryField("persint_competenze", persint_competenze);
		query +=  addQueryField("persint_operatore", persint_operatore);
		query +=  addQueryField("persint_uffoperatore", persint_uffoperatore);
		query +=  addQueryField("persint_profilecod", persint_profilecod);
		query +=  addQueryField("persint_profilename", persint_profilename);
		
		// mbernardini 09/07/2015 : ricerca delle sole persone interne con diritti personalizzati rispetto al profilo
		if (persint_dirittipersonalizzati)
			query += "([/persona_interna/profile/@changed]=\"true\") AND ";
		
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "[UD,/xw/@UdType]=persona_interna";
		
		// DD 31/10/2004 - RW:0019007 - Se non è una ricerca specifica di un profilo, escludo i profili dai risultati,
        // altrimenti vengono tornati anche in ricerca di persone esterne.
		query = query.trim() + " AND [/persona_interna/@tipo]=\"&null;\"";
		
		String codSedeRestriction = getCurrentCodSedeRestriction();
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_INT_AOO_RESTRICTION, false))
			query = query + " AND [persint_codammaoo]=" + this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
		else if (codSedeRestriction != null && codSedeRestriction.length() > 0)
			query = query + " AND [persint_codammaoo]=" + codSedeRestriction;
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/persona_interna/@cognome),xml(xpart:/persona_interna/@nome)"); // TODO Andrebbe specificato all'interno di un file di properties
		
		return queryPlain(query); 
	}
	
	public void setPersint_matricola(String cod) {
		this.persint_matricola = cod;
	}

	public String getPersint_matricola() {
		return persint_matricola;
	}

	public void setPersint_cognome(String descr) {
		this.persint_cognome = descr;
	}

	public String getPersint_cognome() {
		return persint_cognome;
	}

	public void setPersint_loginname(String tipo) {
		this.persint_loginname = tipo;
	}

	public String getPersint_loginname() {
		return persint_loginname;
	}

	public void setPersint_qualifica(String comune) {
		this.persint_qualifica = comune;
	}

	public String getPersint_qualifica() {
		return persint_qualifica;
	}

	public void setPersint_competenze(String competenze) {
		this.persint_competenze = competenze;
	}

	public String getPersint_competenze() {
		return persint_competenze;
	}

	public void setPersint_diritti(String provincia) {
		this.persint_diritti = provincia;
	}

	public String getPersint_diritti() {
		return persint_diritti;
	}

	public void setPersint_operatore(String operatore) {
		this.persint_operatore = operatore;
	}

	public String getPersint_operatore() {
		return persint_operatore;
	}

	public void setPersint_uffoperatore(String uff_operatore) {
		this.persint_uffoperatore = uff_operatore;
	}

	public String getPersint_uffoperatore() {
		return persint_uffoperatore;
	}
	
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.persint_matricola = null;
		this.persint_cognome = null;
		this.persint_loginname = null;
		this.persint_qualifica = null;
		this.persint_diritti = null;
		this.persint_competenze = null;
		this.persint_operatore = null;
		this.persint_uffoperatore = null;
		this.persint_profilecod = null;
		this.persint_profilename = null;
		this.persint_dirittipersonalizzati = false;
		this.persint_soprannome = null;
		return null;
	}

	public void setPersint_profilecod(String cod_profilo) {
		this.persint_profilecod = cod_profilo;
	}

	public String getPersint_profilecod() {
		return persint_profilecod;
	}

	public void setPersint_profilename(String nome_profilo) {
		this.persint_profilename = nome_profilo;
	}

	public String getPersint_profilename() {
		return persint_profilename;
	}
	
	public boolean isPersint_dirittipersonalizzati() {
		return persint_dirittipersonalizzati;
	}

	public void setPersint_dirittipersonalizzati(boolean persint_dirittipersonalizzati) {
		this.persint_dirittipersonalizzati = persint_dirittipersonalizzati;
	}

	public void setPersint_soprannome(String nickname) {
		this.persint_soprannome = nickname;
	}

	public String getPersint_soprannome() {
		return persint_soprannome;
	}
	
	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}

	public String getFocusElement() {
		return focusElement;
	}
	
	public String openIndexCodiceUnita() throws Exception {
		this.focusElement = "persint_matricola";
		this.openIndex("persint_matricola", this.persint_matricola, "0", null);
		return null;
	}
	
	public String openIndexCognome() throws Exception {
		this.focusElement = "persint_cognome";
		this.openIndex("persint_cognome", "persint_nomcogn", this.persint_cognome, "0", null);
		return null;
	}
	public String openIndexLogin() throws Exception {
		this.focusElement = "persint_loginname";
		this.openIndex("persint_loginname", this.persint_loginname, "0", null);
		return null;
	}
	public String openIndexNickname() throws Exception {
		this.focusElement = "persint_soprannome";
		this.openIndex("persint_soprannome", this.persint_soprannome, "0", null);
		return null;
	}
	public String openIndexQualifica() throws Exception {
		this.focusElement = "persint_qualifica";
		this.openIndex("persint_qualifica", this.persint_qualifica, "0", " ");
		return null;
	}
	public String openIndexCompetenze() throws Exception {
		this.focusElement = "persint_competenze";
		this.openIndex("persint_competenze", this.persint_competenze, "0", null);
		return null;
	}
	public String openIndexDiritti() throws Exception {
		this.focusElement = "persint_diritti";
		this.openIndex("persint_diritti", this.persint_diritti, "0", " ");
		return null;
	}
	public String openIndexOperatore() throws Exception {
		this.focusElement = "persint_operatore";
		this.openIndex("persint_operatore", this.persint_operatore, "0", null);
		return null;
	}
	public String openIndexUffOperatore() throws Exception {
		this.focusElement = "persint_uffoperatore";
		this.openIndex("persint_uffoperatore", this.persint_uffoperatore, "0", null);
		return null;
	}
	public String openIndexProfileCod() throws Exception {
		this.focusElement = "persint_profilecod";
		this.openIndex("persint_profilecod", this.persint_profilecod, "0", null);
		return null;
	}
	public String openIndexProfileName() throws Exception {
		this.focusElement = "persint_profilename";
		this.openIndex("persint_profilename", this.persint_profilename, "0", null);
		return null;
	}
}
