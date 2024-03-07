package it.tredi.dw4.docway.beans.dexia;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.docway.beans.QueryGlobale;
import it.tredi.dw4.docway.model.Tipologia;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.dom4j.Document;

public class QueryVarieNA extends QueryGlobale {

	private boolean dati_repertorio = true;
	
	private String na_tipologia = "";
	private String na_codiceNorma = "";
	private String na_releaseNorma = "";
	private String na_denominazioneNorma;
	private String na_dataEmanazioneNorma_da = "";
	private String na_dataEmanazioneNorma_a = "";
	private String na_statoNorma = "";
	private String na_disposizioni = "";
	private String na_unita = "";
	private String na_competenza = "";
	
	private List<Tipologia> select_tipologiaRepertorio = new ArrayList<Tipologia>();
	private List<SelectItem> select_na_statoNorma = new ArrayList<SelectItem>();
	private List<SelectItem> select_na_competenza = new ArrayList<SelectItem>();
	
	public QueryVarieNA() throws Exception {
		super();
		
		getSelect_na_statoNorma().add(new SelectItem("", ""));
		getSelect_na_statoNorma().add(new SelectItem("in lavorazione", "in lavorazione"));
		getSelect_na_statoNorma().add(new SelectItem("approvata", "approvata"));
		getSelect_na_statoNorma().add(new SelectItem("in revisione", "in revisione"));
		getSelect_na_statoNorma().add(new SelectItem("abrogata", "abrogata"));
		
		getSelect_na_competenza().add(new SelectItem("", ""));
		getSelect_na_competenza().add(new SelectItem("Cda", "Cda"));
		getSelect_na_competenza().add(new SelectItem("CdD", "CdD"));
		getSelect_na_competenza().add(new SelectItem("UOA", "UOA"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		forzaRepertorio();
		
		
		select_tipologiaRepertorio = XMLUtil.parseSetOfElement(dom, "/response/listof_rep/repertorio[@codice='NA']/tipologie/tipologia", new Tipologia());
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		forceRepertorioSelected("NA", "V");
		setCurrentCustomFieldSection("varie_NA");
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
			query += addQueryField("xml,/doc/extra/NA_codiceNorma", this.escapeQueryValue(na_codiceNorma));
			
			// 20150112 fcappelli - rimosso per richiesta cliente 
			// 20150120 fcappelli - rimesso per richiesta del cliente
			if (null != na_dataEmanazioneNorma_da && na_dataEmanazioneNorma_da.length() > 0) {
				String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
				if (!DateUtil.isValidDate(na_dataEmanazioneNorma_da, formatoData)) {
					this.setErrorMessage("templateForm:na_dataEmanazioneNorma_da", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dexia.data_emanazione") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
					return "error";
				}
			}
				
			if (null != na_dataEmanazioneNorma_a && na_dataEmanazioneNorma_a.length() > 0) {
				String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
				if (!DateUtil.isValidDate(na_dataEmanazioneNorma_a, formatoData)) {
					this.setErrorMessage("templateForm:na_dataEmanazioneNorma_a", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dexia.data_emanazione") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return "error";
				}
			}
			
			if ((null != na_dataEmanazioneNorma_da && na_dataEmanazioneNorma_da.length()> 0) || null != na_dataEmanazioneNorma_a && na_dataEmanazioneNorma_a.length()> 0) {
				String query1 = addDateRangeQuery("xml,/doc/extra/disposizioni/NA_disposizione/@dataemanazione", na_dataEmanazioneNorma_da, na_dataEmanazioneNorma_a);
				query += query1;
			}
			
			query += addQueryField("xml,/doc/extra/NA_releaseNorma", this.escapeQueryValue(na_releaseNorma));
			query += addQueryField("xml,/doc/oggetto", this.escapeQueryValue(na_denominazioneNorma));
			query += addQueryField("xml,/doc/extra/NA_statoNorma", this.escapeQueryValue(na_statoNorma));
			query += addQueryField("xml,/doc/extra/NA_competenza", this.escapeQueryValue(na_competenza));
			query += addQueryField("xml,/doc/extra/disposizioni/NA_disposizione", this.escapeQueryValue(na_disposizioni));
			query += addQueryField("xml,/doc/extra/unitaRichiamate/NA_unitaRichiamata/@nome_uff", this.escapeQueryValue(na_unita));
			
			// TODO aggiungere query su tipologia repertorio
			
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
		
		na_tipologia = "";
		na_codiceNorma = "";
		na_releaseNorma = "";
		na_denominazioneNorma = "";
		na_dataEmanazioneNorma_da = "";
		na_dataEmanazioneNorma_a = "";
		na_statoNorma = "";
		na_competenza = "";
		na_disposizioni = "";
		na_unita = "";
		
		forzaRepertorio();
		
		return null;
	}
	
	public String getNa_codiceNorma() {
		return na_codiceNorma;
	}

	public void setNa_codiceNorma(String na_codiceNorma) {
		this.na_codiceNorma = na_codiceNorma;
	}

	public String getNa_releaseNorma() {
		return na_releaseNorma;
	}

	public void setNa_releaseNorma(String na_releaseNorma) {
		this.na_releaseNorma = na_releaseNorma;
	}

	public String getNa_denominazioneNorma() {
		return na_denominazioneNorma;
	}

	public void setNa_denominazioneNorma(String na_denominazioneNorma) {
		this.na_denominazioneNorma = na_denominazioneNorma;
	}

	public String getNa_dataEmanazioneNorma_da() {
		return this.na_dataEmanazioneNorma_da;
	}

	public void setNa_dataEmanazioneNorma_da(String na_dataEmanazioneNorma_da) {
		this.na_dataEmanazioneNorma_da = na_dataEmanazioneNorma_da;
	}

	public String getNa_dataEmanazioneNorma_a() {
		return this.na_dataEmanazioneNorma_a;
	}

	public void setNa_dataEmanazioneNorma_a(String na_dataEmanazioneNorma_a) {
		this.na_dataEmanazioneNorma_a = na_dataEmanazioneNorma_a;
	}

	public String getNa_statoNorma() {
		return na_statoNorma;
	}

	public void setNa_statoNorma(String na_statoNorma) {
		this.na_statoNorma = na_statoNorma;
	}

	public String getNa_disposizioni() {
		return na_disposizioni;
	}

	public void setNa_disposizioni(String na_disposizioni) {
		this.na_disposizioni = na_disposizioni;
	}

	public String getNa_unita() {
		return na_unita;
	}

	public void setNa_unita(String na_unita) {
		this.na_unita = na_unita;
	}

	public List<SelectItem> getSelect_na_statoNorma() {
		return select_na_statoNorma;
	}

	public void setSelect_na_statoNorma(List<SelectItem> select_na_statoNorma) {
		this.select_na_statoNorma = select_na_statoNorma;
	}

	public boolean isDati_repertorio() {
		return dati_repertorio;
	}

	public void setDati_repertorio(boolean datiRepertorio) {
		this.dati_repertorio = datiRepertorio;
	}
	
	public String getNa_tipologia() {
		return na_tipologia;
	}

	public void setNa_tipologia(String na_tipologia) {
		this.na_tipologia = na_tipologia;
	}

	public List<Tipologia> getSelect_tipologiaRepertorio() {
		return select_tipologiaRepertorio;
	}

	public void setSelect_tipologiaRepertorio(
			List<Tipologia> select_tipologiaRepertorio) {
		this.select_tipologiaRepertorio = select_tipologiaRepertorio;
	}
	
	public String getNa_competenza() {
		return na_competenza;
	}

	public void setNa_competenza(String na_competenza) {
		this.na_competenza = na_competenza;
	}

	public List<SelectItem> getSelect_na_competenza() {
		return select_na_competenza;
	}

	public void setSelect_na_competenza(List<SelectItem> select_na_competenza) {
		this.select_na_competenza = select_na_competenza;
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
	 * apertura vocabolario su unita' richiamata
	 * @return
	 * @throws Exception
	 */
	public String openIndexUnitaRichiamate() throws Exception {
		boolean acceptEmptySelection = filtraIndiciSuRepertorio();
		
		setFocusElement("na_unita");
		this.openIndex("na_unita", "xml,/doc/extra/unitaRichiamate/NA_unitaRichiamata/@nome_uff", this.na_unita, "0", "", acceptEmptySelection);
		return null;
	}
	
	/**
	 * apertura vocabolario su disposizione
	 * @return
	 * @throws Exception
	 */
	public String openIndexDisposizione() throws Exception {
		boolean acceptEmptySelection = filtraIndiciSuRepertorio();
		
		setFocusElement("na_disposizioni");
		this.openIndex("na_disposizioni", "xml,/doc/extra/disposizioni/NA_disposizione", this.na_disposizioni, "0", "", acceptEmptySelection);
		return null;
	}
	
	/**
	 * apertura vocabolario su denominazione
	 * @return
	 * @throws Exception
	 */
	public String openIndexDenominazione() throws Exception {
		boolean acceptEmptySelection = filtraIndiciSuRepertorio();
		
		setFocusElement("na_denominazioneNorma");
		this.openIndex("na_denominazioneNorma", "xml,/doc/oggetto", this.na_denominazioneNorma, "0", "", acceptEmptySelection);
		return null;
	}
	
	/**
	 * apertura vocabolario su release norma
	 * @return
	 * @throws Exception
	 */
	public String openIndexReleaseNorma() throws Exception {
		boolean acceptEmptySelection = filtraIndiciSuRepertorio();
		
		setFocusElement("na_releaseNorma");
		this.openIndex("na_releaseNorma", "xml,/doc/extra/NA_releaseNorma", this.na_releaseNorma, "0", "", acceptEmptySelection);
		return null;
	}
	
	/**
	 * apertura vocabolario su codice norma
	 * @return
	 * @throws Exception
	 */
	public String openIndexCodiceNorma() throws Exception {
		boolean acceptEmptySelection = filtraIndiciSuRepertorio();
		
		setFocusElement("na_codiceNorma");
		this.openIndex("na_codiceNorma", "xml,/doc/extra/NA_codiceNorma", this.na_codiceNorma, "0", "", acceptEmptySelection);
		return null;
	}
	
	/**
	 * filtro degli indici sui soli record di Norme Aziendali
	 * @return
	 * @throws Exception
	 */
	private boolean filtraIndiciSuRepertorio() throws Exception {
		String repFilter = "[doc_tipo]=\"varie\" and [doc_repertoriocod]=\"NA\"";
    	
    	XMLDocumento response = super._queryPlain(repFilter, "", "");
        this.getFormsAdapter().fillFormsFromResponse(response);
        	
        return true;
	}
	
}
