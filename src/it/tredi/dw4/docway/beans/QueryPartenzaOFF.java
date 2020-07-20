package it.tredi.dw4.docway.beans;

import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class QueryPartenzaOFF extends QueryGlobale {

	private boolean dati_repertorio = true;
	
	private String doc_ordinetipologia = "";
	private String doc_ordineservizio = "";
	private String doc_ordinelicenza = "";
	private String doc_ordinecontratto = "";
	private String doc_ordinealtro = "";
	
	public QueryPartenzaOFF() throws Exception {
		super();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		super.init(dom);
		
		setEstremi_protocollo(false); // chiusura della sezione relativa agli estremi di protocollo
		
		setArrivo_mezzo_trasmissione_select(XMLUtil.parseSetOfElement(dom, "response/partenza_mezzo_trasmissione_select/option", new Option()));
		setArrivo_tipologia_select(XMLUtil.parseSetOfElement(dom, "response/partenza_tipologia_select/option", new Option()));
		
		forzaRepertorio();
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		forceRepertorioSelected("OFF", "P");
		setPartenza(true);
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
			query += "([doc_repertoriocod]=\"OFF\") AND ";
			
			// query su campi specifici del repertorio
			query +=  addQueryField("doc_ordinetipologia", this.escapeQueryValue(this.doc_ordinetipologia));
			query +=  addQueryField("doc_ordineservizio", this.escapeQueryValue(this.doc_ordineservizio));
			query +=  addQueryField("doc_ordinelicenza", this.escapeQueryValue(this.doc_ordinelicenza));
			query +=  addQueryField("doc_ordinecontratto", this.escapeQueryValue(this.doc_ordinecontratto));
			query +=  addQueryField("doc_ordinealtro", this.escapeQueryValue(this.doc_ordinealtro));
			
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
		
		// reset dei campi specifici del protocollo
		doc_ordinetipologia = "";
		doc_ordineservizio = "";
		doc_ordinelicenza = "";
		doc_ordinecontratto = "";
		doc_ordinealtro = "";
		
		forzaRepertorio();
		
		return null;
	}
	
	/**
	 * vocabolario su tipologia ordine
	 * @return
	 * @throws Exception
	 */
	public String openIndexDocOrdineTipologia() throws Exception {
		setFocusElement("doc_ordinetipologia");
		this.openIndex("doc_ordinetipologia", this.doc_ordinetipologia, "0", " ", false);
		return null;
	}
	
	/**
	 * vocabolario su servizio ordine
	 * @return
	 * @throws Exception
	 */
	public String openIndexDocOrdineServizio() throws Exception {
		setFocusElement("doc_ordineservizio");
		this.openIndex("doc_ordineservizio", this.doc_ordineservizio, "0", " ", false);
		return null;
	}
	
	/**
	 * vocabolario su licenza ordine
	 * @return
	 * @throws Exception
	 */
	public String openIndexDocOrdineLicenza() throws Exception {
		setFocusElement("doc_ordinelicenza");
		this.openIndex("doc_ordinelicenza", this.doc_ordinelicenza, "0", " ", false);
		return null;
	}
	
	/**
	 * vocabolario su contratto ordine
	 * @return
	 * @throws Exception
	 */
	public String openIndexDocOrdineContratto() throws Exception {
		setFocusElement("doc_ordinecontratto");
		this.openIndex("doc_ordinecontratto", this.doc_ordinecontratto, "0", " ", false);
		return null;
	}
	
	/**
	 * vocabolario su altro
	 * @return
	 * @throws Exception
	 */
	public String openIndexDocOrdineAltro() throws Exception {
		setFocusElement("doc_ordinealtro");
		this.openIndex("doc_ordinealtro", this.doc_ordinealtro, "0", " ", false);
		return null;
	}
	
	public boolean isDati_repertorio() {
		return dati_repertorio;
	}

	public void setDati_repertorio(boolean datiRepertorio) {
		this.dati_repertorio = datiRepertorio;
	}
	
	/**
	 * apertura/chiusura della sezione relativa ai dati di repertorio
	 * @return
	 */
	public String openCloseWidgetDatiRepertorio(){
		dati_repertorio = !dati_repertorio;
		return null;
	}
	
	public String getDoc_ordinetipologia() {
		return doc_ordinetipologia;
	}

	public void setDoc_ordinetipologia(String doc_ordinetipologia) {
		this.doc_ordinetipologia = doc_ordinetipologia;
	}

	public String getDoc_ordineservizio() {
		return doc_ordineservizio;
	}

	public void setDoc_ordineservizio(String doc_ordineservizio) {
		this.doc_ordineservizio = doc_ordineservizio;
	}

	public String getDoc_ordinelicenza() {
		return doc_ordinelicenza;
	}

	public void setDoc_ordinelicenza(String doc_ordinelicenza) {
		this.doc_ordinelicenza = doc_ordinelicenza;
	}

	public String getDoc_ordinecontratto() {
		return doc_ordinecontratto;
	}

	public void setDoc_ordinecontratto(String doc_ordinecontratto) {
		this.doc_ordinecontratto = doc_ordinecontratto;
	}

	public String getDoc_ordinealtro() {
		return doc_ordinealtro;
	}

	public void setDoc_ordinealtro(String doc_ordinealtro) {
		this.doc_ordinealtro = doc_ordinealtro;
	}

}
