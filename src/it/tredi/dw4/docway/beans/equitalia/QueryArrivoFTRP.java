package it.tredi.dw4.docway.beans.equitalia;

import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class QueryArrivoFTRP extends QueryGlobale {

	private boolean dati_repertorio = true;
	
	private String ftrp_importo = "";
	private String ftrp_tipodocumento = "";
	private String ftrp_statonav = "";
	private String ftrp_numeronav = "";
	private String ftrp_dataricezione = "";
	private String ftrp_numerocig = "";
	
	private boolean ftrp_richiesta_daautorizzare = false;
	private boolean ftrp_richiesta_autorizzo = false;
	private boolean ftrp_richiesta_nonautorizzo = false;
	private boolean ftrp_richiesta_insospeso = false;
		
	public QueryArrivoFTRP() throws Exception {
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		setArrivo_mezzo_trasmissione_select(XMLUtil.parseSetOfElement(dom, "response/arrivo_mezzo_trasmissione_select/option", new Option()));
		setArrivo_tipologia_select(XMLUtil.parseSetOfElement(dom, "response/arrivo_tipologia_select/option", new Option()));
		
		forzaRepertorio();
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		forceRepertorioSelected("FTRP", "A");
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
			
			// viene forzata la ricerca sul repertorio
			query += "([doc_repertoriocod]=\"FTRP\") AND ";
			
			// query su campi specifici del repertorio
			
			query += addQueryField("xml,/doc/extra/@numero_cig", this.escapeQueryValue(ftrp_numerocig));
			query += addQueryField("xml,/doc/extra/@data_ricezione", this.escapeQueryValue(ftrp_dataricezione));
			query += addQueryField("xml,/doc/extra/@importo", this.escapeQueryValue(ftrp_importo));
			if (ftrp_tipodocumento != null && ftrp_tipodocumento.length() > 0)
				query += "([xml,/doc/extra/@tipo_documento]=\"" + ftrp_tipodocumento + "\") AND ";
			
			String filtro_statorichiesta = "";
			if (ftrp_richiesta_daautorizzare) filtro_statorichiesta += " \"Da autorizzare\" OR ";
			if (ftrp_richiesta_autorizzo) filtro_statorichiesta += " \"Autorizzo\" OR ";
			if (ftrp_richiesta_nonautorizzo) filtro_statorichiesta += " \"Non autorizzo\" OR ";
			if (ftrp_richiesta_insospeso) filtro_statorichiesta += " \"In sospeso\" OR ";
			
			if (filtro_statorichiesta.length() > 0) {
				if (filtro_statorichiesta.endsWith(" OR "))
					filtro_statorichiesta = filtro_statorichiesta.substring(0, filtro_statorichiesta.length()-3);
				
				query += "([xml,/doc/extra/@stato_richiesta]=" + filtro_statorichiesta + ") AND ";
			}
			
			if (ftrp_statonav != null && ftrp_statonav.length() > 0)
				query += "([xml,/doc/extra/@stato_invio_nav]=\"" + ftrp_statonav + "\") AND ";
			query += addQueryField("xml,/doc/extra/@numero_nav", this.escapeQueryValue(ftrp_numeronav));
			
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
		
		ftrp_importo = "";
		ftrp_tipodocumento = "";
		ftrp_statonav = "";
		ftrp_numeronav = "";
		ftrp_dataricezione = "";
		ftrp_numerocig = "";
		ftrp_richiesta_daautorizzare = false;
		ftrp_richiesta_autorizzo = false;
		ftrp_richiesta_nonautorizzo = false;
		ftrp_richiesta_insospeso = false;
		
		forzaRepertorio();
		
		return null;
	}
	
	public boolean isDati_repertorio() {
		return dati_repertorio;
	}

	public void setDati_repertorio(boolean dati_repertorio) {
		this.dati_repertorio = dati_repertorio;
	}
	
	/**
	 * apertura/chiusura della sezione relativa ai dati di repertorio
	 * @return
	 */
	public String openCloseWidgetDatiRepertorio(){
		dati_repertorio = !dati_repertorio;
		return null;
	}
	
	public String getFtrp_importo() {
		return ftrp_importo;
	}

	public void setFtrp_importo(String ftrp_importo) {
		this.ftrp_importo = ftrp_importo;
	}

	public String getFtrp_tipodocumento() {
		return ftrp_tipodocumento;
	}

	public void setFtrp_tipodocumento(String ftrp_tipodocumento) {
		this.ftrp_tipodocumento = ftrp_tipodocumento;
	}

	public boolean isFtrp_richiesta_daautorizzare() {
		return ftrp_richiesta_daautorizzare;
	}

	public void setFtrp_richiesta_daautorizzare(boolean ftrp_richiesta_daautorizzare) {
		this.ftrp_richiesta_daautorizzare = ftrp_richiesta_daautorizzare;
	}

	public boolean isFtrp_richiesta_autorizzo() {
		return ftrp_richiesta_autorizzo;
	}

	public void setFtrp_richiesta_autorizzo(boolean ftrp_richiesta_autorizzo) {
		this.ftrp_richiesta_autorizzo = ftrp_richiesta_autorizzo;
	}

	public boolean isFtrp_richiesta_nonautorizzo() {
		return ftrp_richiesta_nonautorizzo;
	}

	public void setFtrp_richiesta_nonautorizzo(boolean ftrp_richiesta_nonautorizzo) {
		this.ftrp_richiesta_nonautorizzo = ftrp_richiesta_nonautorizzo;
	}

	public boolean isFtrp_richiesta_insospeso() {
		return ftrp_richiesta_insospeso;
	}

	public void setFtrp_richiesta_insospeso(boolean ftrp_richiesta_insospeso) {
		this.ftrp_richiesta_insospeso = ftrp_richiesta_insospeso;
	}

	public String getFtrp_statonav() {
		return ftrp_statonav;
	}

	public void setFtrp_statonav(String ftrp_statonav) {
		this.ftrp_statonav = ftrp_statonav;
	}

	public String getFtrp_numeronav() {
		return ftrp_numeronav;
	}

	public void setFtrp_numeronav(String ftrp_numeronav) {
		this.ftrp_numeronav = ftrp_numeronav;
	}
	
	public String getFtrp_dataricezione() {
		return ftrp_dataricezione;
	}

	public void setFtrp_dataricezione(String ftrp_dataricezione) {
		this.ftrp_dataricezione = ftrp_dataricezione;
	}

	public String getFtrp_numerocig() {
		return ftrp_numerocig;
	}

	public void setFtrp_numerocig(String ftrp_numerocig) {
		this.ftrp_numerocig = ftrp_numerocig;
	}
	
}
