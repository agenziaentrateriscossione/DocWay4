package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.DocWayProperties;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

public class QueryFascicoloPersonale extends QueryFascicolo {
	private String fasc_nome = "";
	private String fasc_cognome = "";
	private String fasc_fsmatricola = "";
	private String fasc_categoria = "";
	private String fasc_codfiscale = "";
	private String range_fascdatanascita_from = "";
	private String range_fascdatanascita_to = "";
	private String fasc_luogonascita = "";
	private String range_fascdataassunzione_from = "";
	private String range_fascdataassunzione_to = "";
	private String range_fascdatacessazione_from = "";
	private String range_fascdatacessazione_to = "";
	
	private List<Option> categorie = new ArrayList<Option>();
	
	public QueryFascicoloPersonale() throws Exception {
		super();
		
		// caricamento delle opzioni da visualizzare nel select delle categorie
		Option empty = new Option(); // opzione vuota
		empty.setLabel("");
		empty.setValue("");
		categorie.add(empty);
		String[] categories = DocWayProperties.readProperty("fascicolo_speciale.personale.categorie", "").split(",");
		if (categories != null && categories.length > 0) {
			for (int i=0; i<categories.length; i++) {
				if (categories[i] != null && categories[i].length() > 0) {
					Option opzione = new Option();
					opzione.setLabel(categories[i]);
					opzione.setValue(categories[i]);
					categorie.add(opzione);
				}
			}
		}
	}
	
	@Override
	public void init(Document dom) {
		super.init(dom);
		
		if (getFascicoloSpecialeInfo() != null && getFascicoloSpecialeInfo().getId() != null && getFascicoloSpecialeInfo().getId().length() > 0) {
			if (getFascicoloSpecialeInfo().getClassif().getCod() != null) {
				setFasc_classif(getFascicoloSpecialeInfo().getClassif().getText());
				
				//if (getCustom_classiffasccod() != null && getCustom_classiffasccod().length() > 0) // caso di ricerca fascicoli in fascicolazione di un documento
					setCustom_classiffasccod(getFascicoloSpecialeInfo().getClassif().getCod());
			}
		}
	}
	
	/**
	 * Inserimento di un nuovo fascicolo del personale
	 * @return
	 * @throws Exception
	 */
	@Override
	public String insTableDocFascicolo() throws Exception {
		try {
			getFormsAdapter().insTableDoc(Const.DOCWAY_TIPOLOGIA_FASCICOLO + "@" + getFascicoloSpecialeInfo().getId()); 
	
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_FASCICOLO, "", "", "@" + getFascicoloSpecialeInfo().getId(), response, this.isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String getFasc_nome() {
		return fasc_nome;
	}

	public void setFasc_nome(String fasc_nome) {
		this.fasc_nome = fasc_nome;
	}

	public String getFasc_cognome() {
		return fasc_cognome;
	}

	public void setFasc_cognome(String fasc_cognome) {
		this.fasc_cognome = fasc_cognome;
	}
	
	public String getFasc_fsmatricola() {
		return fasc_fsmatricola;
	}

	public void setFasc_fsmatricola(String fasc_fsmatricola) {
		this.fasc_fsmatricola = fasc_fsmatricola;
	}
	
	public String getFasc_categoria() {
		return fasc_categoria;
	}

	public void setFasc_categoria(String fasc_categoria) {
		this.fasc_categoria = fasc_categoria;
	}
	
	public String getFasc_codfiscale() {
		return fasc_codfiscale;
	}

	public void setFasc_codfiscale(String fasc_codfiscale) {
		this.fasc_codfiscale = fasc_codfiscale;
	}

	public String getRange_fascdatanascita_from() {
		return range_fascdatanascita_from;
	}

	public void setRange_fascdatanascita_from(String range_fascdatanascita_from) {
		this.range_fascdatanascita_from = range_fascdatanascita_from;
	}

	public String getRange_fascdatanascita_to() {
		return range_fascdatanascita_to;
	}

	public void setRange_fascdatanascita_to(String range_fascdatanascita_to) {
		this.range_fascdatanascita_to = range_fascdatanascita_to;
	}

	public String getFasc_luogonascita() {
		return fasc_luogonascita;
	}

	public void setFasc_luogonascita(String fasc_luogonascita) {
		this.fasc_luogonascita = fasc_luogonascita;
	}

	public String getRange_fascdataassunzione_from() {
		return range_fascdataassunzione_from;
	}

	public void setRange_fascdataassunzione_from(
			String range_fascdataassunzione_from) {
		this.range_fascdataassunzione_from = range_fascdataassunzione_from;
	}

	public String getRange_fascdataassunzione_to() {
		return range_fascdataassunzione_to;
	}

	public void setRange_fascdataassunzione_to(String range_fascdataassunzione_to) {
		this.range_fascdataassunzione_to = range_fascdataassunzione_to;
	}

	public String getRange_fascdatacessazione_from() {
		return range_fascdatacessazione_from;
	}

	public void setRange_fascdatacessazione_from(
			String range_fascdatacessazione_from) {
		this.range_fascdatacessazione_from = range_fascdatacessazione_from;
	}

	public String getRange_fascdatacessazione_to() {
		return range_fascdatacessazione_to;
	}

	public void setRange_fascdatacessazione_to(String range_fascdatacessazione_to) {
		this.range_fascdatacessazione_to = range_fascdatacessazione_to;
	}
	
	public List<Option> getCategorie() {
		return categorie;
	}

	public void setCategorie(List<Option> categorie) {
		this.categorie = categorie;
	}

	/**
	 * apertura vocabolario sul campo nome del fasciolo del personale
	 * @return
	 * @throws Exception
	 */
	public String openIndexNomeFascicoloPersonale() throws Exception {
		try {
			setFocusElement("fasc_nome");
			this.openIndex("fasc_nome", "xml,/fascicolo/nominativo/@nome", fasc_nome, "0", "", false);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * apertura vocabolario sul campo nome del fasciolo del personale
	 * @return
	 * @throws Exception
	 */
	public String openIndexCognomeFascicoloPersonale() throws Exception {
		try {
			setFocusElement("fasc_cognome");
			this.openIndex("fasc_cognome", "xml,/fascicolo/nominativo/@cognome", fasc_cognome, "0", "", false);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * apertura vocabolario sul campo matricola del fasciolo del personale
	 * @return
	 * @throws Exception
	 */
	public String openIndexMatricolaFascicoloPersonale() throws Exception {
		try {
			setFocusElement("fasc_fsmatricola");
			this.openIndex("fasc_fsmatricola", fasc_fsmatricola, "0", "", false);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * apertura vocabolario sul campo cod. fiscale del fasciolo del personale
	 * @return
	 * @throws Exception
	 */
	public String openIndexCodFiscaleFascicoloPersonale() throws Exception {
		try {
			setFocusElement("fasc_codfiscale");
			this.openIndex("fasc_codfiscale", fasc_codfiscale, "0", "", false);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * apertura vocabolario sul campo luogo di nascita del fasciolo del personale
	 * @return
	 * @throws Exception
	 */
	public String openIndexLuogoNascitaFascicoloPersonale() throws Exception {
		try {
			setFocusElement("fasc_luogonascita");
			this.openIndex("fasc_luogonascita", "xml,/fascicolo/fascicolo_speciale/@luogo_nascita", fasc_luogonascita, "0", "", false);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	@Override
	public String queryPlain() throws Exception {
		try {
			String query = createQueryPersonale();
			if (query != null) {
				getFormsAdapter().findplain();
				return queryPlain(query);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * creazione della query di ricerca fascicoli del personale
	 * @return
	 */
	private String createQueryPersonale() throws Exception {
		String query = createQuery(true);
		
		if (query != null) {
			String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
			
			if (query.length() > 0) // nel caso sia presente una query di base dei fascicoli viene riaggiunto l'operatore finale
				query += " AND ";
			
			query +=  addQueryField("xml,/fascicolo/nominativo/@nome", this.escapeQueryValue(fasc_nome));
			query +=  addQueryField("xml,/fascicolo/nominativo/@cognome", this.escapeQueryValue(fasc_cognome));
			
			query +=  addQueryField("fasc_fsmatricola", this.escapeQueryValue(fasc_fsmatricola));
			
			if (fasc_categoria != null && fasc_categoria.trim().length() > 0)
				query += "([fasc_categoria]=\"" + fasc_categoria + "\") AND ";
			
			query +=  addQueryField("fasc_codfiscale", this.escapeQueryValue(fasc_codfiscale));
			query +=  addQueryField("xml,/fascicolo/fascicolo_speciale/@luogo_nascita", this.escapeQueryValue(fasc_luogonascita));
			
			if (range_fascdatanascita_from != null && range_fascdatanascita_from.length() > 0) {
				if (!DateUtil.isValidDate(range_fascdatanascita_from, formatoData)) {
					this.setErrorMessage("templateForm:range_fascdatanascita_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.birthdate") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if (range_fascdatanascita_to != null && range_fascdatanascita_to.length() > 0) {
				if (!DateUtil.isValidDate(range_fascdatanascita_to, formatoData)) {
					this.setErrorMessage("templateForm:range_fascdatanascita_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.birthdate") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if ((range_fascdatanascita_from != null && range_fascdatanascita_from.length() > 0) || (range_fascdatanascita_to != null && range_fascdatanascita_to.length() > 0))
				query +=  addDateRangeQuery("fascdatanascita", range_fascdatanascita_from, range_fascdatanascita_to) + " AND ";
			
			if (range_fascdataassunzione_from != null && range_fascdataassunzione_from.length() > 0) {
				if (!DateUtil.isValidDate(range_fascdataassunzione_from, formatoData)) {
					this.setErrorMessage("templateForm:range_fascdataassunzione_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_assunzione") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if (range_fascdataassunzione_to != null && range_fascdataassunzione_to.length() > 0) {
				if (!DateUtil.isValidDate(range_fascdataassunzione_to, formatoData)) {
					this.setErrorMessage("templateForm:range_fascdataassunzione_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_assunzione") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if ((range_fascdataassunzione_from != null && range_fascdataassunzione_from.length() > 0) || (range_fascdataassunzione_to != null && range_fascdataassunzione_to.length() > 0))
				query +=  addDateRangeQuery("fascdataassunzione", range_fascdataassunzione_from, range_fascdataassunzione_to) + " AND ";
			
			if (range_fascdatacessazione_from != null && range_fascdatacessazione_from.length() > 0) {
				if (!DateUtil.isValidDate(range_fascdatacessazione_from, formatoData)) {
					this.setErrorMessage("templateForm:range_fascdatacessazione_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_cessazione") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if (range_fascdatacessazione_to != null && range_fascdatacessazione_to.length() > 0) {
				if (!DateUtil.isValidDate(range_fascdatacessazione_to, formatoData)) {
					this.setErrorMessage("templateForm:range_fascdatacessazione_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_cessazione") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if ((range_fascdatacessazione_from != null && range_fascdatacessazione_from.length() > 0) || (range_fascdatacessazione_to != null && range_fascdatacessazione_to.length() > 0))
				query +=  addDateRangeQuery("fascdatacessazione", range_fascdatacessazione_from, range_fascdatacessazione_to) + " AND ";
			
			if (query.endsWith(" AND ")) // query conclusa. nel caso termini con l'operatore AND si procede alla rimorzione
				query = query.substring(0, query.length()-4);
		}
		
		return query;
	}

	/**
	 * reset del form di ricerca (pulsante 'pulisci')
	 */
	public String resetQuery() {
		setFasc_oggetto("");
		setFasc_anno("");
		setCustom_da_numfasc("");
		setCustom_a_numfasc("");
		setFasc_note("");
		
		fasc_nome = "";
		fasc_cognome = "";
		fasc_fsmatricola = "";
		fasc_categoria = "";
		fasc_codfiscale = "";
		range_fascdatanascita_from = "";
		range_fascdatanascita_to = "";
		fasc_luogonascita = "";
		range_fascdataassunzione_from = "";
		range_fascdataassunzione_to = "";
		range_fascdatacessazione_from = "";
		range_fascdatacessazione_to = "";
		
		return null;
	}
	
}
