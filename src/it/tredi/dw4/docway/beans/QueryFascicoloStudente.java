package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLDocumento;

/**
 * Pagina di ricerca su fascicoli degli studenti
 */
public class QueryFascicoloStudente extends QueryFascicolo {

	private String fasc_fsmatricola = "";
	private String fasc_codfiscale = "";
	private String range_fascdatanascita_from = "";
	private String range_fascdatanascita_to = "";
	private String range_fascdataimmatricolazione_from = "";
	private String range_fascdataimmatricolazione_to = "";

	public QueryFascicoloStudente() throws Exception {
		super();
	}

	@Override
	public void init(Document dom) {
		super.init(dom, "studente");

		if (getFascicoloSpecialeInfo() != null && getFascicoloSpecialeInfo().getId() != null && getFascicoloSpecialeInfo().getId().length() > 0) {
			if (getFascicoloSpecialeInfo().getClassif().getCod() != null) {
				setFasc_classif(getFascicoloSpecialeInfo().getClassif().getText());

				//if (getCustom_classiffasccod() != null && getCustom_classiffasccod().length() > 0) // caso di ricerca fascicoli in fascicolazione di un documento
					setCustom_classiffasccod(getFascicoloSpecialeInfo().getClassif().getCod());
			}
		}
	}

	/**
	 * Inserimento di un nuovo fascicolo degli studenti
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

			query +=  addQueryField("fasc_fsmatricola", this.escapeQueryValue(fasc_fsmatricola));

			query +=  addQueryField("fasc_codfiscale", this.escapeQueryValue(fasc_codfiscale));

			// data di nascita
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

			// data di immatricolazione
			if (range_fascdataimmatricolazione_from != null && range_fascdataimmatricolazione_from.length() > 0) {
				if (!DateUtil.isValidDate(range_fascdataimmatricolazione_from, formatoData)) {
					this.setErrorMessage("templateForm:range_fascdataimmatricolazione_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_immatr") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if (range_fascdataimmatricolazione_to != null && range_fascdataimmatricolazione_to.length() > 0) {
				if (!DateUtil.isValidDate(range_fascdataimmatricolazione_to, formatoData)) {
					this.setErrorMessage("templateForm:range_fascdataimmatricolazione_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_immatr") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
			}
			if ((range_fascdataimmatricolazione_from != null && range_fascdataimmatricolazione_from.length() > 0) || (range_fascdataimmatricolazione_to != null && range_fascdataimmatricolazione_to.length() > 0))
				query +=  addDateRangeQuery("fascdataimmatricolazione", range_fascdataimmatricolazione_from, range_fascdataimmatricolazione_to) + " AND ";

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

		fasc_fsmatricola = "";
		fasc_codfiscale = "";
		range_fascdatanascita_from = "";
		range_fascdatanascita_to = "";
		range_fascdataimmatricolazione_from = "";
		range_fascdataimmatricolazione_to = "";

		return null;
	}

	public String getFasc_fsmatricola() {
		return fasc_fsmatricola;
	}

	public void setFasc_fsmatricola(String fasc_fsmatricola) {
		this.fasc_fsmatricola = fasc_fsmatricola;
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

	public String getRange_fascdataimmatricolazione_from() {
		return range_fascdataimmatricolazione_from;
	}

	public void setRange_fascdataimmatricolazione_from(String range_fascdataimmatr_from) {
		this.range_fascdataimmatricolazione_from = range_fascdataimmatr_from;
	}

	public String getRange_fascdataimmatricolazione_to() {
		return range_fascdataimmatricolazione_to;
	}

	public void setRange_fascdataimmatricolazione_to(String range_fascdataimmatr_to) {
		this.range_fascdataimmatricolazione_to = range_fascdataimmatr_to;
	}

}
