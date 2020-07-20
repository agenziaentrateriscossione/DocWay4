package it.tredi.dw4.docway.beans.condotte;

import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class QueryVarieFPNALGERIA extends QueryGlobale {

	private boolean dati_repertorio = true;
	
	private String fpn_nfattura = "";
	private String fpndatafattura_from = "";
	private String fpndatafattura_to = "";
	private String fpnimporto_from = "";
	private String fpnimporto_to = "";
	private String fpn_centrocosto = "";
	private String fpn_statofattura = "";
	private String fpn_protiva = "";
	private String fpn_codfornitore = "";
	private String fpn_ragsocfornitore = "";
	private String fpn_scadenza = "";
	private String fpn_note = "";
	private String fpn_noteappr = "";
	private String fpn_timestamp = "";
	
	public QueryVarieFPNALGERIA() throws Exception {
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		setArrivo_mezzo_trasmissione_select(XMLUtil.parseSetOfElement(dom, "response/varie_mezzo_trasmissione_select/option", new Option()));
		setArrivo_tipologia_select(XMLUtil.parseSetOfElement(dom, "response/varie_tipologia_select/option", new Option()));
		
		forzaRepertorio();
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		forceRepertorioSelected("FPNALGERIA", "V");
		setVarie(true);
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
			
			query +=  addQueryField("fpn_nfattura", this.escapeQueryValue(this.fpn_nfattura));
			if ((fpndatafattura_from != null && fpndatafattura_from.trim().length() > 0) || (fpndatafattura_to != null && fpndatafattura_to.trim().length() > 0)) {
	        	String query1 = addDateRangeQuery("fpndatafattura", fpndatafattura_from, fpndatafattura_to, "AND");
	    		query +=  query1;
	        }
			if ((fpnimporto_from != null && fpnimporto_from.trim().length() > 0) || (fpnimporto_to != null && fpnimporto_to.trim().length() > 0)) {
	        	String query1 = addRangeQuery("fpnimporto", fpnimporto_from, fpnimporto_to, "AND");
	    		query +=  query1;
	        }
			query +=  addQueryField("fpn_centrocosto", this.escapeQueryValue(this.fpn_centrocosto));
			query +=  addQueryField("fpn_statofattura", this.escapeQueryValue(this.fpn_statofattura));
			query +=  addQueryField("fpn_protiva", this.escapeQueryValue(this.fpn_protiva));
			query +=  addQueryField("fpn_codfornitore", this.escapeQueryValue(this.fpn_codfornitore));
			query +=  addQueryField("fpn_ragsocfornitore", this.escapeQueryValue(this.fpn_ragsocfornitore));
			query +=  addQueryField("fpn_scadenza", this.escapeQueryValue(this.fpn_scadenza));
			query +=  addQueryField("fpn_note", this.escapeQueryValue(this.fpn_note));
			query +=  addQueryField("fpn_noteappr", this.escapeQueryValue(this.fpn_noteappr));
			query +=  addQueryField("fpn_timestamp", this.escapeQueryValue(this.fpn_timestamp));
									
			if (query.endsWith(" AND ")) // query conclusa. nel caso termini con l'operatore AND si procede alla rimorzione
				query = query.substring(0, query.length()-4);
		}
		
		return query;
	}
	
	/**
	 * reset del form di ricerca
	 */
	@Override
	public String resetQuery() {
		super.resetQuery();
		
		// campi specifici del repertorio
		fpn_nfattura = "";
		fpndatafattura_from = "";
		fpndatafattura_to = "";
		fpnimporto_from = "";
		fpnimporto_to = "";
		fpn_centrocosto = "";
		fpn_statofattura = "";
		fpn_protiva = "";
		fpn_codfornitore = "";
		fpn_ragsocfornitore = "";
		fpn_scadenza = "";
		fpn_note = "";
		fpn_noteappr = "";
		fpn_timestamp = "";
		
		forzaRepertorio();
		
		return null;
	}
	
	/**
	 * vocabolario sul numero della fattura
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnNFattura() throws Exception {
		setFocusElement("fpn_nfattura");
		this.openIndex("fpn_nfattura", this.fpn_nfattura, "0", "", false);
		return null;
	}
	
	/**
	 * vocabolario sul centro di costo
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnCentroCosto() throws Exception {
		setFocusElement("fpn_centrocosto");
		this.openIndex("fpn_centrocosto", this.fpn_centrocosto, "0", "", false);
		return null;
	}
	
	/**
	 * vocabolario sullo stato della fattura
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnStatoFattura() throws Exception {
		setFocusElement("fpn_statofattura");
		this.openIndex("fpn_statofattura", this.fpn_statofattura, "0", "", false);
		return null;
	}
	
	/**
	 * vocabolario sul protocollo IVA
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnProtIva() throws Exception {
		setFocusElement("fpn_protiva");
		this.openIndex("fpn_protiva", this.fpn_protiva, "0", "", false);
		return null;
	}
	
	/**
	 * vocabolario sul codice del fornitore
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnCodFornitore() throws Exception {
		setFocusElement("fpn_codfornitore");
		this.openIndex("fpn_codfornitore", this.fpn_codfornitore, "0", "", false);
		return null;
	}
	
	/**
	 * vocabolario sulla ragione sociale del fornitore
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnRagSocFornitore() throws Exception {
		setFocusElement("fpn_ragsocfornitore");
		this.openIndex("fpn_ragsocfornitore", this.fpn_ragsocfornitore, "0", "", false);
		return null;
	}
	
	/**
	 * vocabolario sulla scadenza
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnScadenza() throws Exception {
		setFocusElement("fpn_scadenza");
		this.openIndex("fpn_scadenza", this.fpn_scadenza, "0", "", false);
		return null;
	}
	
	/**
	 * vocabolario su note
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnNote() throws Exception {
		setFocusElement("fpn_note");
		this.openIndex("fpn_note", this.fpn_note, "0", "", false);
		return null;
	}
	
	/**
	 * vocabolario su note di approvazione
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnNoteAppr() throws Exception {
		setFocusElement("fpn_noteappr");
		this.openIndex("fpn_noteappr", this.fpn_noteappr, "0", "", false);
		return null;
	}
	
	/**
	 * vocabolario su timestamp
	 * @return
	 * @throws Exception
	 */
	public String openIndexFpnTimestamp() throws Exception {
		setFocusElement("fpn_timestamp");
		this.openIndex("fpn_timestamp", this.fpn_timestamp, "0", "", false);
		return null;
	}
	
	/**
	 * apertura/chiusura della sezione relativa ai dati di repertorio
	 * @return
	 */
	public String openCloseWidgetDatiRepertorio(){
		dati_repertorio = !dati_repertorio;
		return null;
	}
	
	public boolean isDati_repertorio() {
		return dati_repertorio;
	}

	public void setDati_repertorio(boolean dati_repertorio) {
		this.dati_repertorio = dati_repertorio;
	}
	
	public String getFpn_nfattura() {
		return fpn_nfattura;
	}

	public void setFpn_nfattura(String fpn_nfattura) {
		this.fpn_nfattura = fpn_nfattura;
	}

	public String getFpndatafattura_from() {
		return fpndatafattura_from;
	}

	public void setFpndatafattura_from(String fpndatafattura_from) {
		this.fpndatafattura_from = fpndatafattura_from;
	}

	public String getFpndatafattura_to() {
		return fpndatafattura_to;
	}

	public void setFpndatafattura_to(String fpndatafattura_to) {
		this.fpndatafattura_to = fpndatafattura_to;
	}

	public String getFpnimporto_from() {
		return fpnimporto_from;
	}

	public void setFpnimporto_from(String fpnimporto_from) {
		this.fpnimporto_from = fpnimporto_from;
	}

	public String getFpnimporto_to() {
		return fpnimporto_to;
	}

	public void setFpnimporto_to(String fpnimporto_to) {
		this.fpnimporto_to = fpnimporto_to;
	}

	public String getFpn_centrocosto() {
		return fpn_centrocosto;
	}

	public void setFpn_centrocosto(String fpn_centrocosto) {
		this.fpn_centrocosto = fpn_centrocosto;
	}

	public String getFpn_statofattura() {
		return fpn_statofattura;
	}

	public void setFpn_statofattura(String fpn_statofattura) {
		this.fpn_statofattura = fpn_statofattura;
	}

	public String getFpn_protiva() {
		return fpn_protiva;
	}

	public void setFpn_protiva(String fpn_protiva) {
		this.fpn_protiva = fpn_protiva;
	}

	public String getFpn_codfornitore() {
		return fpn_codfornitore;
	}

	public void setFpn_codfornitore(String fpn_codfornitore) {
		this.fpn_codfornitore = fpn_codfornitore;
	}

	public String getFpn_ragsocfornitore() {
		return fpn_ragsocfornitore;
	}

	public void setFpn_ragsocfornitore(String fpn_ragsocfornitore) {
		this.fpn_ragsocfornitore = fpn_ragsocfornitore;
	}

	public String getFpn_scadenza() {
		return fpn_scadenza;
	}

	public void setFpn_scadenza(String fpn_scadenza) {
		this.fpn_scadenza = fpn_scadenza;
	}

	public String getFpn_note() {
		return fpn_note;
	}

	public void setFpn_note(String fpn_note) {
		this.fpn_note = fpn_note;
	}

	public String getFpn_noteappr() {
		return fpn_noteappr;
	}

	public void setFpn_noteappr(String fpn_noteappr) {
		this.fpn_noteappr = fpn_noteappr;
	}

	public String getFpn_timestamp() {
		return fpn_timestamp;
	}

	public void setFpn_timestamp(String fpn_timestamp) {
		this.fpn_timestamp = fpn_timestamp;
	}

}
