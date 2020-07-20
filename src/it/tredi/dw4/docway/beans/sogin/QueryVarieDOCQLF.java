package it.tredi.dw4.docway.beans.sogin;

import org.dom4j.Document;

import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

public class QueryVarieDOCQLF extends QueryGlobale {

	private boolean dati_repertorio = true;
	
	private String ragione_sociale_fornitore = "";
	private String codice_fornitore = "";
	
	public QueryVarieDOCQLF() throws Exception {
		super();
	}
	
	@Override
	@SuppressWarnings("unchecked")
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
		forceRepertorioSelected("DOCQLF", "V");
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
			query += addQueryField("xml,/doc/extra/fornitori/fornitore", this.escapeQueryValue(ragione_sociale_fornitore));
			query += addQueryField("xml,/doc/extra/fornitori/fornitore/@codice", this.escapeQueryValue(codice_fornitore));
			
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
		
		ragione_sociale_fornitore = "";
		codice_fornitore = "";
		
		forzaRepertorio();
		
		return null;
	}

	public boolean isDati_repertorio() {
		return dati_repertorio;
	}

	public void setDati_repertorio(boolean datiRepertorio) {
		this.dati_repertorio = datiRepertorio;
	}

	public String getRagione_sociale_fornitore() {
		return ragione_sociale_fornitore;
	}

	public void setRagione_sociale_fornitore(String ragione_sociale) {
		this.ragione_sociale_fornitore = ragione_sociale;
	}

	public String getCodice_fornitore() {
		return codice_fornitore;
	}

	public void setCodice_fornitore(String codice) {
		this.codice_fornitore = codice;
	}
	
	/**
	 * apertura/chiusura della sezione relativa ai dati di repertorio
	 * @return
	 */
	public String openCloseWidgetDatiRepertorio(){
		dati_repertorio = !dati_repertorio;
		return null;
	}
	
	/**
	 * apertura vocabolario ragione sociale fornitore
	 * @return
	 * @throws Exception
	 */
	public String openIndexRagioneSocialeFornitore() throws Exception {
		setFocusElement("ragione_sociale_fornitore");
		this.openIndex("ragione_sociale_fornitore", "xml,/doc/extra/fornitori/fornitore", this.ragione_sociale_fornitore, "0", "", false);
		return null;
	}
	
	/**
	 * apertura vocabolario codice fornitore
	 * @return
	 * @throws Exception
	 */
	public String openIndexCodiceFornitore() throws Exception {
		setFocusElement("codice_fornitore");
		this.openIndex("codice_fornitore", "xml,/doc/extra/fornitori/fornitore/@codice", this.codice_fornitore, "0", "", false);
		return null;
	}
	
}
