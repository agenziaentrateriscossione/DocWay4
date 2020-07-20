package it.tredi.dw4.docway.beans.fatturepa;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class QueryArrivoFTRPAP extends QueryGlobale {

	private boolean dati_repertorio = true;
	
	private String ftr_tipodocumento = "";
	private String ftr_data_from = "";
	private String ftr_data_to = "";
	private String ftr_numero = "";
	private String ftr_oggettofornitura = "";
	private String ftr_datascadenza_from = "";
	private String ftr_datascadenza_to = "";
	private String ftr_estremiimpegno = "";
	private String ftr_divisa = "";
	private String ftr_descrlinea = "";
	private String ftr_codcup = "";
	private String ftr_codcig = "";
	private String ftr_codcupPA = "";
	private String ftr_codcigPA = "";
	private String ftr_codcommessaPA = "";
	private String ftr_riffasesal = "";
	private String ftr_numddt = "";
	private String ftr_dataddt_from = "";
	private String ftr_dataddt_to = "";
	
	// filtri su stato (notifiche)
	private boolean ftr_attesainvio = false;
	private boolean ftr_dt = false;
	private boolean ftr_ec01 = false;
	private boolean ftr_ec02 = false;
	private boolean ftr_se = false;
	
	private List<Option> tipodocumento_list = new ArrayList<Option>();
	
	public QueryArrivoFTRPAP() throws Exception {
		super();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		super.init(dom);
		
		setArrivo_mezzo_trasmissione_select(XMLUtil.parseSetOfElement(dom, "response/arrivo_mezzo_trasmissione_select/option", new Option()));
		setArrivo_tipologia_select(XMLUtil.parseSetOfElement(dom, "response/arrivo_tipologia_select/option", new Option()));
		
		forzaRepertorio();
		
		// caricamento delle opzioni da visualizzare nelle select della maschera di ricerca
		tipodocumento_list = FatturePaUtilis.getListaTipoDocumento(true);
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		forceRepertorioSelected("FTRPAP", "A");
		setArrivo(true);
	}
	
	@Override
	public String getPageTemplate() {
		return "../" + super.getPageTemplate();
	}
	
	/**
	 * creazione della query di ricerca specifica per il repertorio
	 * @return
	 */
	@Override
	public String createQuery() throws Exception {
		String query = super.createQuery();
		
		if (query != null) {
			if (query.length() > 0) // nel caso sia presente una query di base dei documenti viene riaggiunto l'operatore finale
				query += " AND ";
			
			
			// query su campi specifici del repertorio
			
			// campo del registro delle fatture
			
			query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiRegistroFatture/@numFattura", this.escapeQueryValue(ftr_numero));
			
			if (ftr_data_from != null && !ftr_data_from.equals("")) {
				String query1 = "";
				if (ftr_data_to != null && !ftr_data_to.equals(""))
					query1 = "{" + DateUtil.formatDate2XW(ftr_data_from, null) + "|" + DateUtil.formatDate2XW(ftr_data_to, null) + "}";
				else
					query1 = DateUtil.formatDate2XW(ftr_data_from, null);
				
				query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiRegistroFatture/@dataEmissioneFattura", query1);
			}
			
			if (ftr_datascadenza_from != null && !ftr_datascadenza_from.equals("")) {
				String query1 = "";
				if (ftr_datascadenza_to != null && !ftr_datascadenza_to.equals(""))
					query1 = "{" + DateUtil.formatDate2XW(ftr_datascadenza_from, null) + "|" + DateUtil.formatDate2XW(ftr_datascadenza_to, null) + "}";
				else
					query1 = DateUtil.formatDate2XW(ftr_datascadenza_from, null);
				
				query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiRegistroFatture/@dataScadenzaFattura", query1);
			}
			
			query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiRegistroFatture/oggettoFornitura", this.escapeQueryValue(ftr_oggettofornitura));
			
			query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiRegistroFatture/cig", this.escapeQueryValue(ftr_codcig));
			query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiRegistroFatture/cup", this.escapeQueryValue(ftr_codcup));
			
			// campi specifici delle fatturePA passive
			
			if (ftr_tipodocumento != null && ftr_tipodocumento.trim().length() > 0)
				query += "([/doc/extra/fatturaPA/datiFattura/datiGeneraliDocumento/@tipoDocumento]=\"" + ftr_tipodocumento + "\") AND ";
			
			query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiGeneraliDocumento/@divisa", this.escapeQueryValue(ftr_divisa));
			
			query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiBeniServizi/linea", this.escapeQueryValue(ftr_descrlinea));
			
			query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiDDT/@numeroDDT", this.escapeQueryValue(ftr_numddt));
			if (ftr_dataddt_from != null && !ftr_dataddt_from.equals("")) {
				String query1 = "";
				if (ftr_dataddt_to != null && !ftr_dataddt_to.equals(""))
					query1 = "{" + DateUtil.formatDate2XW(ftr_dataddt_from, null) + "|" + DateUtil.formatDate2XW(ftr_dataddt_to, null) + "}";
				else
					query1 = DateUtil.formatDate2XW(ftr_dataddt_from, null);
				
				query += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiDDT/@dataDDT", query1);
			}
			
			if (ftr_codcigPA != null && ftr_codcigPA.length() > 0)
				query += addSubqueryRiferimenti("@codiceCIG", ftr_codcigPA) + " AND ";
			if (ftr_codcupPA != null && ftr_codcupPA.length() > 0)
				query += addSubqueryRiferimenti("@codiceCUP", ftr_codcupPA) + " AND ";
			if (ftr_codcommessaPA != null && ftr_codcommessaPA.length() > 0)
				query += addSubqueryRiferimenti("@codiceCommessaConvenzione", ftr_codcommessaPA) + " AND ";
			
			// filtro sulle notitiche
			query += addSubqueryStatoInvio();
			query += addSubqueryNotifiche();
			
			if (query.endsWith(" AND ")) // query conclusa. nel caso termini con l'operatore AND si procede alla rimorzione
				query = query.substring(0, query.length()-4);
		}
		
		return query;
	}
	
	/**
	 * creazione della subquert di ricerca sui codici dello stato di invio
	 * @return
	 */
	private String addSubqueryStatoInvio() {
		String query1 = "";
		if (ftr_attesainvio)
			query1 = "([xml,/doc/extra/fatturaPA/@state]=\"ATTESA\") AND ";
		return query1;
	}
	
	/**
	 * creazione della subquert di ricerca sui codici delle notitiche
	 * @return
	 */
	private String addSubqueryNotifiche() {
		String query1 = "";
		
		if (ftr_ec01 || ftr_ec02 || ftr_se || ftr_dt) {
			if (ftr_ec01)
				query1 += "([xml,/doc/extra/fatturaPA/notifica/@tipo]=\"EC\" ADJ [xml,/doc/extra/fatturaPA/notifica/@esito]=\"EC01\") OR ";
			if (ftr_ec02)
				query1 += "([xml,/doc/extra/fatturaPA/notifica/@tipo]=\"EC\" ADJ [xml,/doc/extra/fatturaPA/notifica/@esito]=\"EC02\") OR ";
			if (ftr_se)
				query1 += "[xml,/doc/extra/fatturaPA/notifica/@tipo]=\"SE\" OR ";
			if (ftr_dt)
				query1 += "[xml,/doc/extra/fatturaPA/notifica/@tipo]=\"DT\" OR ";
			
			if (query1.endsWith(" OR ")) // query conclusa. nel caso termini con l'operatore AND si procede alla rimorzione
				query1 = query1.substring(0, query1.length()-3);
			
			query1 = "(" + query1 + ") AND ";
		}
		
		return query1;
	}
	
	/**
	 * creazione della subquery di ricerca codici dai riferimenti della fattura (codice cig, codice cup, ecc.)
	 * 
	 * @param chiaveCodice
	 * @param valoreCodice
	 * @return
	 */
	private String addSubqueryRiferimenti(String chiaveCodice, String valoreCodice) {
		String query1 = "";
		
		if (chiaveCodice != null && chiaveCodice.length() > 0
				&& valoreCodice != null && valoreCodice.length() > 0) {
			valoreCodice = escapeQueryValue(valoreCodice);
			
			query1 += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiOrdineAcquisto/" + chiaveCodice, valoreCodice, "OR");
			query1 += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiContratto/" + chiaveCodice, valoreCodice, "OR");
			query1 += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiConvenzione/" + chiaveCodice, valoreCodice, "OR");
			query1 += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiRicezione/" + chiaveCodice, valoreCodice, "OR");
			query1 += addQueryField("xml,/doc/extra/fatturaPA/datiFattura/datiFattureCollegate/" + chiaveCodice, valoreCodice, "OR");
			
			if (query1.endsWith(" OR ")) // query conclusa. nel caso termini con l'operatore AND si procede alla rimorzione
				query1 = query1.substring(0, query1.length()-3);
			
			query1 = "(" + query1 + ") ";
		}
		
		return query1;
	}
	
	/**
	 * reset del form di ricerca
	 */
	@Override
	public String resetQuery() {
		super.resetQuery();
		
		ftr_tipodocumento = "";
		ftr_data_from = "";
		ftr_data_to = "";
		ftr_datascadenza_from = "";
		ftr_datascadenza_to = "";
		ftr_oggettofornitura = "";
		ftr_estremiimpegno = "";
		ftr_numero = "";
		ftr_divisa = "";
		ftr_descrlinea = "";
		ftr_codcup = "";
		ftr_codcig = "";
		ftr_codcupPA = "";
		ftr_codcigPA = "";
		ftr_codcommessaPA = "";
		ftr_riffasesal = "";
		ftr_numddt = "";
		ftr_dataddt_from = "";
		ftr_dataddt_to = "";
		
		ftr_attesainvio = false;
		ftr_se = false;
		ftr_dt = false;
		ftr_ec01 = false;
		ftr_ec02 = false;
		
		forzaRepertorio();
		
		return null;
	}
	
	public boolean isDati_repertorio() {
		return dati_repertorio;
	}

	public void setDati_repertorio(boolean datiRepertorio) {
		this.dati_repertorio = datiRepertorio;
	}
	
	public String getFtr_tipodocumento() {
		return ftr_tipodocumento;
	}

	public void setFtr_tipodocumento(String ftr_tipodocumento) {
		this.ftr_tipodocumento = ftr_tipodocumento;
	}

	public String getFtr_data_from() {
		return ftr_data_from;
	}

	public void setFtr_data_from(String ftr_data) {
		this.ftr_data_from = ftr_data;
	}
	
	public String getFtr_data_to() {
		return ftr_data_to;
	}

	public void setFtr_data_to(String ftr_data) {
		this.ftr_data_to = ftr_data;
	}

	public String getFtr_numero() {
		return ftr_numero;
	}

	public void setFtr_numero(String ftr_numero) {
		this.ftr_numero = ftr_numero;
	}

	public String getFtr_divisa() {
		return ftr_divisa;
	}

	public void setFtr_divisa(String ftr_divisa) {
		this.ftr_divisa = ftr_divisa;
	}

	public String getFtr_descrlinea() {
		return ftr_descrlinea;
	}

	public void setFtr_descrlinea(String ftr_descrlinea) {
		this.ftr_descrlinea = ftr_descrlinea;
	}

	public String getFtr_codcupPA() {
		return ftr_codcupPA;
	}

	public void setFtr_codcupPA(String ftr_codcupPA) {
		this.ftr_codcupPA = ftr_codcupPA;
	}

	public String getFtr_codcigPA() {
		return ftr_codcigPA;
	}

	public void setFtr_codcigPA(String ftr_codcigPA) {
		this.ftr_codcigPA = ftr_codcigPA;
	}

	public String getFtr_codcommessaPA() {
		return ftr_codcommessaPA;
	}

	public void setFtr_codcommessaPA(String ftr_codcommessaPA) {
		this.ftr_codcommessaPA = ftr_codcommessaPA;
	}

	public String getFtr_codcup() {
		return ftr_codcup;
	}

	public void setFtr_codcup(String ftr_codcup) {
		this.ftr_codcup = ftr_codcup;
	}

	public String getFtr_codcig() {
		return ftr_codcig;
	}

	public void setFtr_codcig(String ftr_codcig) {
		this.ftr_codcig = ftr_codcig;
	}
	
	public String getFtr_numddt() {
		return ftr_numddt;
	}

	public void setFtr_numddt(String ftr_numddt) {
		this.ftr_numddt = ftr_numddt;
	}
	
	public String getFtr_riffasesal() {
		return ftr_riffasesal;
	}

	public void setFtr_riffasesal(String ftr_riffasesal) {
		this.ftr_riffasesal = ftr_riffasesal;
	}
	
	public String getFtr_dataddt_from() {
		return ftr_dataddt_from;
	}

	public void setFtr_dataddt_from(String ftr_dataddt_from) {
		this.ftr_dataddt_from = ftr_dataddt_from;
	}

	public String getFtr_dataddt_to() {
		return ftr_dataddt_to;
	}

	public void setFtr_dataddt_to(String ftr_dataddt_to) {
		this.ftr_dataddt_to = ftr_dataddt_to;
	}
	
	public boolean isFtr_attesainvio() {
		return ftr_attesainvio;
	}

	public void setFtr_attesainvio(boolean ftr_attesainvio) {
		this.ftr_attesainvio = ftr_attesainvio;
	}
	
	public boolean isFtr_dt() {
		return ftr_dt;
	}

	public void setFtr_dt(boolean ftr_dt) {
		this.ftr_dt = ftr_dt;
	}

	public boolean isFtr_ec01() {
		return ftr_ec01;
	}

	public void setFtr_ec01(boolean ftr_ec01) {
		this.ftr_ec01 = ftr_ec01;
	}

	public boolean isFtr_ec02() {
		return ftr_ec02;
	}

	public void setFtr_ec02(boolean ftr_ec02) {
		this.ftr_ec02 = ftr_ec02;
	}
	
	public boolean isFtr_se() {
		return ftr_se;
	}

	public void setFtr_se(boolean ftr_se) {
		this.ftr_se = ftr_se;
	}
	
	public String getFtr_oggettofornitura() {
		return ftr_oggettofornitura;
	}

	public void setFtr_oggettofornitura(String ftr_oggettofornitura) {
		this.ftr_oggettofornitura = ftr_oggettofornitura;
	}

	public String getFtr_datascadenza_from() {
		return ftr_datascadenza_from;
	}

	public void setFtr_datascadenza_from(String ftr_datascadenza_from) {
		this.ftr_datascadenza_from = ftr_datascadenza_from;
	}

	public String getFtr_datascadenza_to() {
		return ftr_datascadenza_to;
	}

	public void setFtr_datascadenza_to(String ftr_datascadenza_to) {
		this.ftr_datascadenza_to = ftr_datascadenza_to;
	}

	public String getFtr_estremiimpegno() {
		return ftr_estremiimpegno;
	}

	public void setFtr_estremiimpegno(String ftr_estremiimpegno) {
		this.ftr_estremiimpegno = ftr_estremiimpegno;
	}
	
	public List<Option> getTipodocumento_list() {
		return tipodocumento_list;
	}

	public void setTipodocumento_list(List<Option> tipodocumento_list) {
		this.tipodocumento_list = tipodocumento_list;
	}
		
	/**
	 * apertura/chiusura della sezione relativa ai dati di repertorio
	 * @return
	 */
	public String openCloseWidgetDatiRepertorio(){
		dati_repertorio = !dati_repertorio;
		return null;
	}

}
