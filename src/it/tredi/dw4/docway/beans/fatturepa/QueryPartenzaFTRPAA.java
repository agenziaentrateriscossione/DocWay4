package it.tredi.dw4.docway.beans.fatturepa;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class QueryPartenzaFTRPAA extends QueryGlobale {

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
	private boolean ftr_inviata = false;
	private boolean ftr_attesainvio = false;
	private boolean ftr_ns = false;
	private boolean ftr_dt = false;
	private boolean ftr_mc = false;
	private boolean ftr_rc = false;
	private boolean ftr_at = false;
	private boolean ftr_ne01 = false;
	private boolean ftr_ne02 = false;
	
	private List<Option> tipodocumento_list = new ArrayList<Option>();
	
	public QueryPartenzaFTRPAA() throws Exception {
		super();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		super.init(dom);
		
		setArrivo_mezzo_trasmissione_select(XMLUtil.parseSetOfElement(dom, "response/partenza_mezzo_trasmissione_select/option", new Option()));
		setArrivo_tipologia_select(XMLUtil.parseSetOfElement(dom, "response/partenza_tipologia_select/option", new Option()));
		
		forzaRepertorio();
		
		// caricamento delle opzioni da visualizzare nelle select della maschera di ricerca
		tipodocumento_list = FatturePaUtilis.getListaTipoDocumento(true);
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		forceRepertorioSelected("FTRPAA", "P");
		setCurrentCustomFieldSection("partenza_FTRPAA");
		setPartenza(true);
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
			
			// filtro su stati invio e notitiche
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
		
		if (ftr_inviata || ftr_attesainvio) {
			if (ftr_inviata)
				query1 += "([xml,/doc/extra/fatturaPA/@state]=\"SEND\") OR ";
			if (ftr_attesainvio)
				query1 += "([xml,/doc/extra/fatturaPA/@state]=\"ATTESAINVIO\") OR ";
			
			if (query1.endsWith(" OR ")) // query conclusa. nel caso termini con l'operatore OR si procede alla rimorzione
				query1 = query1.substring(0, query1.length()-3);
			
			query1 = "(" + query1 + ") AND ";
		}
		
		return query1;
	}
	
	/**
	 * creazione della subquert di ricerca sui codici delle notitiche
	 * @return
	 */
	private String addSubqueryNotifiche() {
		String query1 = "";
		
		if (ftr_ns || ftr_mc || ftr_at || ftr_rc || ftr_ne01 || ftr_ne02 || ftr_dt) {
			if (ftr_ns)
				query1 += "[xml,/doc/extra/fatturaPA/notifica/@tipo]=\"NS\" OR ";
			if (ftr_mc)
				query1 += "[xml,/doc/extra/fatturaPA/notifica/@tipo]=\"MC\" OR ";
			if (ftr_at)
				query1 += "[xml,/doc/extra/fatturaPA/notifica/@tipo]=\"AT\" OR ";
			if (ftr_rc)
				query1 += "[xml,/doc/extra/fatturaPA/notifica/@tipo]=\"RC\" OR ";
			if (ftr_ne01)
				query1 += "([xml,/doc/extra/fatturaPA/notifica/@tipo]=\"NE\" ADJ [xml,/doc/extra/fatturaPA/notifica/@esito]=\"EC01\") OR ";
			if (ftr_ne02)
				query1 += "([xml,/doc/extra/fatturaPA/notifica/@tipo]=\"NE\" ADJ [xml,/doc/extra/fatturaPA/notifica/@esito]=\"EC02\") OR ";
			if (ftr_dt)
				query1 += "[xml,/doc/extra/fatturaPA/notifica/@tipo]=\"DT\" OR ";
			
			if (query1.endsWith(" OR ")) // query conclusa. nel caso termini con l'operatore OR si procede alla rimorzione
				query1 = query1.substring(0, query1.length()-3);
			
			query1 = "(" + query1 + ") AND ";
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
		
		ftr_inviata = false;
		ftr_attesainvio = false;
		ftr_ns = false;
		ftr_dt = false;
		ftr_mc = false;
		ftr_rc = false;
		ftr_at = false;
		ftr_ne01 = false;
		ftr_ne02 = false;
		
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
	
	public List<Option> getTipodocumento_list() {
		return tipodocumento_list;
	}

	public void setTipodocumento_list(List<Option> tipodocumento_list) {
		this.tipodocumento_list = tipodocumento_list;
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

	public String getFtr_riffasesal() {
		return ftr_riffasesal;
	}

	public void setFtr_riffasesal(String ftr_riffasesal) {
		this.ftr_riffasesal = ftr_riffasesal;
	}

	public String getFtr_numddt() {
		return ftr_numddt;
	}

	public void setFtr_numddt(String ftr_numddt) {
		this.ftr_numddt = ftr_numddt;
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

	public boolean isFtr_dt() {
		return ftr_dt;
	}

	public void setFtr_dt(boolean ftr_dt) {
		this.ftr_dt = ftr_dt;
	}

	public boolean isFtr_inviata() {
		return ftr_inviata;
	}

	public void setFtr_inviata(boolean ftr_inviata) {
		this.ftr_inviata = ftr_inviata;
	}

	public boolean isFtr_attesainvio() {
		return ftr_attesainvio;
	}

	public void setFtr_attesainvio(boolean ftr_attesainvio) {
		this.ftr_attesainvio = ftr_attesainvio;
	}

	public boolean isFtr_ns() {
		return ftr_ns;
	}

	public void setFtr_ns(boolean ftr_ns) {
		this.ftr_ns = ftr_ns;
	}

	public boolean isFtr_mc() {
		return ftr_mc;
	}

	public void setFtr_mc(boolean ftr_mc) {
		this.ftr_mc = ftr_mc;
	}

	public boolean isFtr_rc() {
		return ftr_rc;
	}

	public void setFtr_rc(boolean ftr_rc) {
		this.ftr_rc = ftr_rc;
	}

	public boolean isFtr_at() {
		return ftr_at;
	}

	public void setFtr_at(boolean ftr_at) {
		this.ftr_at = ftr_at;
	}

	public boolean isFtr_ne01() {
		return ftr_ne01;
	}

	public void setFtr_ne01(boolean ftr_ne01) {
		this.ftr_ne01 = ftr_ne01;
	}

	public boolean isFtr_ne02() {
		return ftr_ne02;
	}

	public void setFtr_ne02(boolean ftr_ne02) {
		this.ftr_ne02 = ftr_ne02;
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
