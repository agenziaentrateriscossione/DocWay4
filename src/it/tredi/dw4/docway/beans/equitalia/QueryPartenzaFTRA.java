package it.tredi.dw4.docway.beans.equitalia;

import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class QueryPartenzaFTRA extends QueryGlobale {

	private boolean dati_repertorio = true;
	
	private String ftrp_statonav = "";
	private String ftrp_numeronav = "";
		
	public QueryPartenzaFTRA() throws Exception {
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		setArrivo_mezzo_trasmissione_select(XMLUtil.parseSetOfElement(dom, "response/partenza_mezzo_trasmissione_select/option", new Option()));
		setArrivo_tipologia_select(XMLUtil.parseSetOfElement(dom, "response/partenza_tipologia_select/option", new Option()));
		
		forzaRepertorio();
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		forceRepertorioSelected("FTRA", "P");
		setCurrentCustomFieldSection("partenza_FTRA");
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
			
			// viene forzata la ricerca sul repertorio
			query += "([doc_repertoriocod]=\"FTRA\") AND ";
			
			// query su campi specifici del repertorio
			
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
		
		ftrp_statonav = "";
		ftrp_numeronav = "";
		
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
	
}
