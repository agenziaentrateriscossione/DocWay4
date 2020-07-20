package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;

import org.dom4j.Document;

public class QueryStrutturaEsterna extends AclQuery {
	private String struest_coduff;
	private String struest_codsap;
	private String struest_nome;
	private String struest_tipologia;
	private String struest_codfisc;
	private String struest_piva;
	private String struest_indirizzocomune;
	private String struest_indirizzoprov;
	private String struest_competenze;
	private String struest_operatore;
	private String struest_uffoperatore;
	private String struestcreazione_da;
	private String struestcreazione_a;
	
	private AclQueryFormsAdapter formsAdapter;
	
	public QueryStrutturaEsterna() throws Exception {
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
		if (!checkFields()) return null;
		
		//TODO - chiamare con i parametri corretti
		String query =  "";
		query +=  addQueryField("struest_coduff", struest_coduff);
		query +=  addQueryField("struest_codsap", struest_codsap);
		query +=  addQueryField("struest_nome", struest_nome);
		query +=  addQueryField("struest_tipologia", struest_tipologia);
		query +=  addQueryField("struest_codfisc", struest_codfisc);
		query +=  addQueryField("struest_piva", struest_piva);
		query +=  addQueryField("struest_indirizzocomune", struest_indirizzocomune);
		query +=  addQueryField("struest_indirizzoprov", struest_indirizzoprov);
		query +=  addQueryField("struest_competenze", struest_competenze);
		query +=  addQueryField("struest_operatore", struest_operatore);
		query +=  addQueryField("struest_uffoperatore", struest_uffoperatore);
		query +=  addDateRangeQuery("struestcreazione", struestcreazione_da, struestcreazione_a, "AND");
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "[UD,/xw/@UdType]=struttura_esterna";
		
		String codSedeRestriction = getCurrentCodSedeRestriction();
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_EXT_AOO_RESTRICTION, false))
			query = query.trim() + " AND [struest_codammaoo]=" + this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
		else if (codSedeRestriction != null && codSedeRestriction.length() > 0)
			query = query.trim() + " AND [struest_codammaoo]=" + codSedeRestriction;
		
		this.formsAdapter.getDefaultForm().addParam("qord", "xml(xpart:/struttura_esterna/nome)"); // TODO Andrebbe specificato all'interno di un file di properties
		
		return queryPlain(query);
	}
	
	public void setStruest_coduff(String cod) {
		this.struest_coduff = cod;
	}

	public String getStruest_coduff() {
		return struest_coduff;
	}

	public void setStruest_nome(String descr) {
		this.struest_nome = descr;
	}

	public String getStruest_nome() {
		return struest_nome;
	}

	public void setStruest_tipologia(String tipo) {
		this.struest_tipologia = tipo;
	}

	public String getStruest_tipologia() {
		return struest_tipologia;
	}

	public void setStruest_indirizzocomune(String comune) {
		this.struest_indirizzocomune = comune;
	}

	public String getStruest_indirizzocomune() {
		return struest_indirizzocomune;
	}

	public void setStruest_competenze(String competenze) {
		this.struest_competenze = competenze;
	}

	public String getStruest_competenze() {
		return struest_competenze;
	}

	public void setStruest_indirizzoprov(String provincia) {
		this.struest_indirizzoprov = provincia;
	}

	public String getStruest_indirizzoprov() {
		return struest_indirizzoprov;
	}

	public void setStruest_operatore(String operatore) {
		this.struest_operatore = operatore;
	}

	public String getStruest_operatore() {
		return struest_operatore;
	}

	public void setStruest_uffoperatore(String uff_operatore) {
		this.struest_uffoperatore = uff_operatore;
	}

	public String getStruest_uffoperatore() {
		return struest_uffoperatore;
	}
	
	public String openIndexCodiceUnita() throws Exception {
		this.openIndex("struest_coduff", this.struest_coduff, "0", null);
		return null;
	}
	public String openIndexCodiceSap() throws Exception {
		this.openIndex("struest_codsap", this.struest_codsap, "0", null);
		return null;
	}
	
	public String openIndexNomeStruttura() throws Exception {
		this.openIndex("struest_nome", this.struest_nome, "0", null);
		return null;
	}
	public String openIndexTipologia() throws Exception {
		this.openIndex("struest_tipologia", this.struest_tipologia, "0", null);
		return null;
	}
	public String openIndexPiva() throws Exception {
		this.openIndex("struest_piva", this.struest_piva, "0", null);
		return null;
	}
	public String openIndexCodFisc() throws Exception {
		this.openIndex("struest_codfisc", this.struest_codfisc, "0", null);
		return null;
	}
	public String openIndexCompetenze() throws Exception {
		this.openIndex("struest_competenze", this.struest_competenze, "0", null);
		return null;
	}
	public String openIndexIndirizzoComune() throws Exception {
		this.openIndex("struest_indirizzocomune", this.struest_indirizzocomune, "0", " ");
		return null;
	}
	public String openIndexIndirizzoProv() throws Exception {
		this.openIndex("struest_indirizzoprov", this.struest_indirizzoprov, "0", null);
		return null;
	}
	public String openIndexOperatore() throws Exception {
		this.openIndex("struest_operatore", this.struest_operatore, "0", null);
		return null;
	}
	public String openIndexUffOperatore() throws Exception {
		this.openIndex("struest_uffoperatore", this.struest_uffoperatore, "0", null);
		return null;
	}
	public String resetQuery() {
		super.resetAddonsQuery();
		
		this.struest_coduff = null;
		this.struest_codsap = null;
		this.struest_nome = null;
		this.struest_tipologia = null;
		this.struest_piva = null;
		this.struest_codfisc = null;
		this.struest_indirizzocomune = null;
		this.struest_indirizzoprov = null;
		this.struest_competenze = null;
		this.struest_operatore = null;
		this.struest_uffoperatore = null;
		this.struestcreazione_da = "";
		this.struestcreazione_a = "";
		return null;
	}

	public void setStruest_codsap(String sap) {
		this.struest_codsap = sap;
	}

	public String getStruest_codsap() {
		return struest_codsap;
	}

	public void setStruest_piva(String partita_iva) {
		this.struest_piva = partita_iva;
	}

	public String getStruest_piva() {
		return struest_piva;
	}

	public void setStruest_codfisc(String codice_fiscale) {
		this.struest_codfisc = codice_fiscale;
	}

	public String getStruest_codfisc() {
		return struest_codfisc;
	}

	public void setStruestcreazione_da(String struestcreazione_da) {
		this.struestcreazione_da = struestcreazione_da;
	}

	public String getStruestcreazione_da() {
		return struestcreazione_da;
	}

	public void setStruestcreazione_a(String struestcreazione_a) {
		this.struestcreazione_a = struestcreazione_a;
	}

	public String getStruestcreazione_a() {
		return struestcreazione_a;
	}
	
	/**
	 * Verifico se all'interno dei campi di tipo data sono stati impostati dei valori corretti
	 * @return true se anche un solo campo non è compilato correttamente, false altrimenti
	 */
	public boolean checkFields(){
		boolean result = true;
		
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
		
		if (struestcreazione_da != null && struestcreazione_da.length() > 0) {
			if (!DateUtil.isValidDate(struestcreazione_da, formatoData)) {
				this.setErrorMessage("templateForm:struestCreazioneFrom", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.create_date_from") + "': " + formatoData.toLowerCase());
				result = false;
			}
		}
		if (struestcreazione_a != null && struestcreazione_a.length() > 0) {
			if (!DateUtil.isValidDate(struestcreazione_a, formatoData)) {
				this.setErrorMessage("templateForm:struestCreazioneTo", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.create_date_to") + "': " + formatoData.toLowerCase());
				result = false;
			}
		}
		return result;
	}
}
