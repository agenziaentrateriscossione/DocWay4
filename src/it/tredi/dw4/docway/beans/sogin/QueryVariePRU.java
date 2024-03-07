package it.tredi.dw4.docway.beans.sogin;

import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

public class QueryVariePRU extends QueryGlobale {

	private boolean dati_repertorio = true;
	
	private String codifica_elaborato = "";
	private String titolo_elaborato = "";
	private String sede_archivio = "";
	private String zona_archivio = "";
	private String note_archivio = "";
	
	private List<Option> select_customSelectSedeArchivio = new ArrayList<Option>();
	
	public QueryVariePRU() throws Exception {
		super();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		super.init(dom);
		
		select_customSelectSedeArchivio = XMLUtil.parseSetOfElement(dom, "/response/select_customSelectSedeArchivio/option", new Option());
		
		setArrivo_mezzo_trasmissione_select(XMLUtil.parseSetOfElement(dom, "response/varie_mezzo_trasmissione_select/option", new Option()));
		setArrivo_tipologia_select(XMLUtil.parseSetOfElement(dom, "response/varie_tipologia_select/option", new Option()));

		forzaRepertorio();
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		forceRepertorioSelected("PRU", "V");
		setCurrentCustomFieldSection("varie_PRU");
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
			query += addQueryField("xml,/doc/extra/ET_ClassificaElaborato", this.escapeQueryValue(codifica_elaborato));
			query += addQueryField("xml,/doc/extra/ET_TitoloElaborato", this.escapeQueryValue(titolo_elaborato));
			if (sede_archivio != null && sede_archivio.trim().length() > 0)
				query += "([/doc/extra/customSelectSedeArchivio]=\"" + sede_archivio + "\") AND ";
			query += addQueryField("xml,/doc/extra/ET_ZonaArchivio", this.escapeQueryValue(zona_archivio));
			query += addQueryField("xml,/doc/extra/ET_NoteArchivio", this.escapeQueryValue(note_archivio));
			
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
		
		codifica_elaborato = "";
		titolo_elaborato = "";
		sede_archivio = "";
		zona_archivio = "";
		note_archivio = "";
		
		forzaRepertorio();
		
		return null;
	}
	
	public boolean isDati_repertorio() {
		return dati_repertorio;
	}

	public void setDati_repertorio(boolean datiRepertorio) {
		this.dati_repertorio = datiRepertorio;
	}
	
	public String getCodifica_elaborato() {
		return codifica_elaborato;
	}

	public void setCodifica_elaborato(String codifica_elaborato) {
		this.codifica_elaborato = codifica_elaborato;
	}

	public String getTitolo_elaborato() {
		return titolo_elaborato;
	}

	public void setTitolo_elaborato(String titolo_elaborato) {
		this.titolo_elaborato = titolo_elaborato;
	}

	public String getSede_archivio() {
		return sede_archivio;
	}

	public void setSede_archivio(String sede_archivio) {
		this.sede_archivio = sede_archivio;
	}

	public String getZona_archivio() {
		return zona_archivio;
	}

	public void setZona_archivio(String zona_archivio) {
		this.zona_archivio = zona_archivio;
	}

	public String getNote_archivio() {
		return note_archivio;
	}

	public void setNote_archivio(String note_archivio) {
		this.note_archivio = note_archivio;
	}
	
	public List<Option> getSelect_customSelectSedeArchivio() {
		return select_customSelectSedeArchivio;
	}

	public void setSelect_customSelectSedeArchivio(
			List<Option> select_customSelectSedeArchivio) {
		this.select_customSelectSedeArchivio = select_customSelectSedeArchivio;
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
