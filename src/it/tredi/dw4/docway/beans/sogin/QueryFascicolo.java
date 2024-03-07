package it.tredi.dw4.docway.beans.sogin;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;

public class QueryFascicolo extends it.tredi.dw4.docway.beans.QueryFascicolo {

	private String fasc_tipologia = "";
	private String fasc_num_gara = "";
	private String fasc_num_oda_sap = "";
	private String range_fasc_dataapertura_from = "";
	private String range_fasc_dataapertura_to = "";
	private String range_fasc_datachiusura_from = "";
	private String range_fasc_datachiusura_to = "";
	private String fasc_fornitore = "";
	private String fasc_fornitore_codice = "";
	
	public QueryFascicolo() throws Exception {
		super();
	}
	
	public String getFasc_tipologia() {
		return fasc_tipologia;
	}

	public void setFasc_tipologia(String fasc_tipologia) {
		this.fasc_tipologia = fasc_tipologia;
	}
	
	public String getFasc_num_gara() {
		return fasc_num_gara;
	}

	public void setFasc_num_gara(String fasc_num_gara) {
		this.fasc_num_gara = fasc_num_gara;
	}

	public String getFasc_num_oda_sap() {
		return fasc_num_oda_sap;
	}

	public void setFasc_num_oda_sap(String fasc_num_oda_sap) {
		this.fasc_num_oda_sap = fasc_num_oda_sap;
	}

	public String getRange_fasc_dataapertura_from() {
		return range_fasc_dataapertura_from;
	}

	public void setRange_fasc_dataapertura_from(String range_fasc_dataapertura_from) {
		this.range_fasc_dataapertura_from = range_fasc_dataapertura_from;
	}

	public String getRange_fasc_dataapertura_to() {
		return range_fasc_dataapertura_to;
	}

	public void setRange_fasc_dataapertura_to(String range_fasc_dataapertura_to) {
		this.range_fasc_dataapertura_to = range_fasc_dataapertura_to;
	}

	public String getRange_fasc_datachiusura_from() {
		return range_fasc_datachiusura_from;
	}

	public void setRange_fasc_datachiusura_from(String range_fasc_datachiusura_from) {
		this.range_fasc_datachiusura_from = range_fasc_datachiusura_from;
	}

	public String getRange_fasc_datachiusura_to() {
		return range_fasc_datachiusura_to;
	}

	public void setRange_fasc_datachiusura_to(String range_fasc_datachiusura_to) {
		this.range_fasc_datachiusura_to = range_fasc_datachiusura_to;
	}
	
	public String getFasc_fornitore() {
		return fasc_fornitore;
	}

	public void setFasc_fornitore(String fasc_fornitore) {
		this.fasc_fornitore = fasc_fornitore;
	}

	public String getFasc_fornitore_codice() {
		return fasc_fornitore_codice;
	}

	public void setFasc_fornitore_codice(String fasc_fornitore_codice) {
		this.fasc_fornitore_codice = fasc_fornitore_codice;
	}
	
	/**
	 * avvio ricerca di fascicoli
	 */
	@Override
	public String queryPlain() throws Exception {
		try {
			String query = createQueryPersonale();
			if (query != null) {
				getFormsAdapter().findplain();
				return queryPlain(query);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * creazione della query di ricerca fascicoli del personale
	 * @return
	 */
	private String createQueryPersonale() throws Exception {
		String query = createQuery();
		
		if (query != null) {
			String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
			
			if (query.length() > 0) // nel caso sia presente una query di base dei fascicoli viene riaggiunto l'operatore finale
				query += " AND ";
			
			if (fasc_tipologia != null && fasc_tipologia.trim().length() > 0)
				query += "([xml,/fascicolo/extra/tipologia_fascicolo]=\"" + fasc_tipologia + "\") AND ";
			
			query +=  addQueryField("xml,/fascicolo/extra/numero_gara", this.escapeQueryValue(fasc_num_gara));
			query +=  addQueryField("xml,/fascicolo/extra/numero_oda_sap", this.escapeQueryValue(fasc_num_oda_sap));
			
			query +=  addQueryField("fasc_fornitore", this.escapeQueryValue(fasc_fornitore));
			query +=  addQueryField("fasc_fornitore_codice", this.escapeQueryValue(fasc_fornitore_codice));
			
			if (range_fasc_dataapertura_from != null && range_fasc_dataapertura_from.length() > 0) {
				if (!DateUtil.isValidDate(range_fasc_dataapertura_from, formatoData)) {
					this.setErrorMessage("templateForm:range_fasc_dataapertura_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_apertura") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if (range_fasc_dataapertura_to != null && range_fasc_dataapertura_to.length() > 0) {
				if (!DateUtil.isValidDate(range_fasc_dataapertura_to, formatoData)) {
					this.setErrorMessage("templateForm:range_fasc_dataapertura_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_apertura") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if ((range_fasc_dataapertura_from != null && range_fasc_dataapertura_from.length() > 0) || (range_fasc_dataapertura_to != null && range_fasc_dataapertura_to.length() > 0))
				query +=  addDateRangeQuery("fascaperturadata", range_fasc_dataapertura_from, range_fasc_dataapertura_to) + " AND ";
			
			if (range_fasc_datachiusura_from != null && range_fasc_datachiusura_from.length() > 0) {
				if (!DateUtil.isValidDate(range_fasc_datachiusura_from, formatoData)) {
					this.setErrorMessage("templateForm:range_fasc_datachiusura_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_chiusura") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if (range_fasc_datachiusura_to != null && range_fasc_datachiusura_to.length() > 0) {
				if (!DateUtil.isValidDate(range_fasc_datachiusura_to, formatoData)) {
					this.setErrorMessage("templateForm:range_fasc_datachiusura_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_chiusura") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if ((range_fasc_datachiusura_from != null && range_fasc_datachiusura_from.length() > 0) || (range_fasc_datachiusura_to != null && range_fasc_datachiusura_to.length() > 0))
				query +=  addDateRangeQuery("fascchiusuradata", range_fasc_datachiusura_from, range_fasc_datachiusura_to) + " AND ";
			
			if (query.endsWith(" AND ")) // query conclusa. nel caso termini con l'operatore AND si procede alla rimorzione
				query = query.substring(0, query.length()-4);
		}
		
		return query;
	}
	
	/**
	 * reset del form di ricerca (pulsante 'pulisci')
	 */
	public String resetQuery() {
		super.resetQuery();
		
		fasc_tipologia = "";
		fasc_num_gara = "";
		fasc_num_oda_sap = "";
		range_fasc_dataapertura_from = "";
		range_fasc_dataapertura_to = "";
		range_fasc_datachiusura_from = "";
		range_fasc_datachiusura_to = "";
		fasc_fornitore = "";
		fasc_fornitore_codice = "";
		
		return null;
	}
	
	/**
	 * apertura vocabolario num gara fascicolo
	 * @return
	 * @throws Exception
	 */
	public String openIndexNumGaraFascicolo() throws Exception {
		setFocusElement("fasc_num_gara");
		this.openIndex("fasc_num_gara", "xml,/fascicolo/extra/numero_gara", this.fasc_num_gara, "0", "", false);
		return null;
	}
	
	/**
	 * apertura vocabolario num oda sap fascicolo
	 * @return
	 * @throws Exception
	 */
	public String openIndexNumOdaSapFascicolo() throws Exception {
		setFocusElement("fasc_num_oda_sap");
		this.openIndex("fasc_num_oda_sap", "xml,/fascicolo/extra/numero_oda_sap", this.fasc_num_oda_sap, "0", "", false);
		return null;
	}
	
	/**
	 * apertura vocabolario su fornitore fascicolo
	 * @return
	 * @throws Exception
	 */
	public String openIndexFascFornitore() throws Exception {
		setFocusElement("fasc_fornitore");
		this.openIndex("fasc_fornitore", "xml,/fascicolo/extra/fornitori/fornitore", this.fasc_fornitore, "0", "", false);
		return null;
	}
	
	/**
	 * apertura vocabolario su codice fornitore fascicolo
	 * @return
	 * @throws Exception
	 */
	public String openIndexFascFornitoreCodice() throws Exception {
		setFocusElement("fasc_fornitore_codice");
		this.openIndex("fasc_fornitore_codice", "xml,/fascicolo/extra/fornitori/fornitore/@codice", this.fasc_fornitore_codice, "0", "", false);
		return null;
	}

}
