package it.tredi.dw4.docway.beans.equitalia;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

public class QueryArrivoDCPA  extends QueryGlobale {

	private boolean dati_repertorio = true;
	
	private String dcp_numeronav = "";
	private String dcp_tipologiadocumento = "";
	private String dcp_statonav = "";
	
	private List<Option> tipologiaDocumentazioneSelect = new ArrayList<Option>();
	
	public QueryArrivoDCPA() throws Exception {
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		setArrivo_mezzo_trasmissione_select(XMLUtil.parseSetOfElement(dom, "response/arrivo_mezzo_trasmissione_select/option", new Option()));
		setArrivo_tipologia_select(XMLUtil.parseSetOfElement(dom, "response/arrivo_tipologia_select/option", new Option()));
		tipologiaDocumentazioneSelect = XMLUtil.parseSetOfElement(dom, "/response/select_tipologiaDocumentazione/option", new Option());
		
		forzaRepertorio();
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		forceRepertorioSelected("DCPA", "A");
		setCurrentCustomFieldSection("arrivo_DCPA");
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
			query += "([doc_repertoriocod]=\"DCPA\") AND ";
			
			// query su campi specifici del repertorio
			
			query += addQueryField("xml,/doc/extra/@numero_nav", this.escapeQueryValue(dcp_numeronav));
			if (dcp_tipologiadocumento != null && dcp_tipologiadocumento.length() > 0)
				query += "([xml,/doc/extra/tipologiaDocumentazione]=\"" + dcp_tipologiadocumento + "\") AND ";
			if (dcp_statonav != null && dcp_statonav.length() > 0)
				query += "([xml,/doc/extra/@stato_invio_nav]=\"" + dcp_statonav + "\") AND ";
									
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
		
		dcp_numeronav = "";
		dcp_tipologiadocumento = "";
		dcp_statonav = "";
		
		forzaRepertorio();
		
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

	public String getDcp_numeronav() {
		return dcp_numeronav;
	}

	public void setDcp_numeronav(String dcp_numeronav) {
		this.dcp_numeronav = dcp_numeronav;
	}

	public String getDcp_tipologiadocumento() {
		return dcp_tipologiadocumento;
	}

	public void setDcp_tipologiadocumento(String dcp_tipologiadocumento) {
		this.dcp_tipologiadocumento = dcp_tipologiadocumento;
	}

	public String getDcp_statonav() {
		return dcp_statonav;
	}

	public void setDcp_statonav(String dcp_statonav) {
		this.dcp_statonav = dcp_statonav;
	}
	
	public List<Option> getTipologiaDocumentazioneSelect() {
		return tipologiaDocumentazioneSelect;
	}

	public void setTipologiaDocumentazioneSelect(
			List<Option> tipologiaDocumentazioneSelect) {
		this.tipologiaDocumentazioneSelect = tipologiaDocumentazioneSelect;
	}
	
}
