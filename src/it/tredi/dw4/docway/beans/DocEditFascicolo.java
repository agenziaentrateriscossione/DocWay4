package it.tredi.dw4.docway.beans;

import java.util.ArrayList;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Classif;
import it.tredi.dw4.docway.model.Fascicolo;
import it.tredi.dw4.docway.model.FascicoloSpecialeInfo;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditFascicolo extends DocEditFascicoloShared {

	private DocDocWayDocEditFormsAdapter formsAdapter;
	private Fascicolo fascicolo = new Fascicolo();

	private String xml = "";

	protected final String DEFAULT_FASCICOLO_TITLE = "dw4.ins_fascicoli";
	protected String insFascicoloTitle = DEFAULT_FASCICOLO_TITLE; // Titolo della pagina di inserimento del fascicolo (fascicolo, sottofascicolo, inserto, annesso)

	// Parametri necessari alla generazione della maschera di inserimento di un fascicolo
	private boolean hideIfInsInFascWithoutClassifOrSottofasc = false;
	private boolean showIfInsInFascAndNotSottofasc = false;
	private boolean showIfInsInFascWithClassifAndNotSottofasc = false;
	private boolean hideIfInsInFascOrSottofasc = false;
	private boolean showIfSottoFasc = false;
	private boolean showIfFasc = false;

	private FascicoloSpecialeInfo fascicoloSpecialeInfo = new FascicoloSpecialeInfo();

	private String fasc_numero_sottofasc = "";
	private String classif_infasc = "";
	private String classif_cod_infasc = "";

	private String classifOD = "";

	private String personalDirCliente = ""; // aggiunta di campi personalizzati specifici per il cliente

	protected String codiceFascicoloCustom = "";
	protected String descrizioneFascicoloCustom = "";
	
	public boolean isHideIfInsInFascWithoutClassifOrSottofasc() {
		return hideIfInsInFascWithoutClassifOrSottofasc;
	}

	public void setHideIfInsInFascWithoutClassifOrSottofasc(
			boolean hideIfInsInFascWithoutClassifOrSottofasc) {
		this.hideIfInsInFascWithoutClassifOrSottofasc = hideIfInsInFascWithoutClassifOrSottofasc;
	}

	public boolean isShowIfInsInFascAndNotSottofasc() {
		return showIfInsInFascAndNotSottofasc;
	}

	public void setShowIfInsInFascAndNotSottofasc(
			boolean showIfInsInFascAndNotSottofasc) {
		this.showIfInsInFascAndNotSottofasc = showIfInsInFascAndNotSottofasc;
	}

	public boolean isShowIfInsInFascWithClassifAndNotSottofasc() {
		return showIfInsInFascWithClassifAndNotSottofasc;
	}

	public void setShowIfInsInFascWithClassifAndNotSottofasc(
			boolean showIfInsInFascWithClassifAndNotSottofasc) {
		this.showIfInsInFascWithClassifAndNotSottofasc = showIfInsInFascWithClassifAndNotSottofasc;
	}

	public boolean isHideIfInsInFascOrSottofasc() {
		return hideIfInsInFascOrSottofasc;
	}

	public void setHideIfInsInFascOrSottofasc(boolean hideIfInsInFascOrSottofasc) {
		this.hideIfInsInFascOrSottofasc = hideIfInsInFascOrSottofasc;
	}

	public boolean isShowIfSottoFasc() {
		return showIfSottoFasc;
	}

	public void setShowIfSottoFasc(boolean showIfSottoFasc) {
		this.showIfSottoFasc = showIfSottoFasc;
	}

	public boolean isShowIfFasc() {
		return showIfFasc;
	}

	public void setShowIfFasc(boolean showIfFasc) {
		this.showIfFasc = showIfFasc;
	}

	public String getClassif_infasc() {
		return classif_infasc;
	}

	public void setClassif_infasc(String classif_infasc) {
		this.classif_infasc = classif_infasc;
	}

	public String getClassif_cod_infasc() {
		return classif_cod_infasc;
	}

	public void setClassif_cod_infasc(String classif_cod_infasc) {
		this.classif_cod_infasc = classif_cod_infasc;
	}

	public String getFasc_numero_sottofasc() {
		return fasc_numero_sottofasc;
	}

	public void setFasc_numero_sottofasc(String fasc_numero_sottofasc) {
		this.fasc_numero_sottofasc = fasc_numero_sottofasc;
	}

	public FascicoloSpecialeInfo getFascicoloSpecialeInfo() {
		return fascicoloSpecialeInfo;
	}

	public void setFascicoloSpecialeInfo(FascicoloSpecialeInfo fascicoloSpecialeInfo) {
		this.fascicoloSpecialeInfo = fascicoloSpecialeInfo;
	}

	public DocEditFascicolo() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public Fascicolo getFascicolo() {
		return fascicolo;
	}

	public void setFascicolo(Fascicolo fascicolo) {
		this.fascicolo = fascicolo;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getClassifOD() {
		return classifOD;
	}

	public void setClassifOD(String classifOD) {
		this.classifOD = classifOD;
	}

	public String getPersonalDirCliente() {
		return personalDirCliente;
	}

	public void setPersonalDirCliente(String personalDirCliente) {
		this.personalDirCliente = personalDirCliente;
	}

	public String getInsFascicoloTitle() {
		if (insFascicoloTitle == null || insFascicoloTitle.length() == 0)
			insFascicoloTitle = DEFAULT_FASCICOLO_TITLE;

		int numDots = StringUtil.count(fasc_numero_sottofasc, ".");

		if (descrizioneFascicoloCustom == null || descrizioneFascicoloCustom.equals(""))
			return I18N.mrs(insFascicoloTitle);
		else if (numDots == 1) //è un sottofascicolo
			return descrizioneFascicoloCustom + " - " + I18N.mrs("dw4.ins_sottofascicoli");
		else if (numDots == 2) //è un inserto
			return descrizioneFascicoloCustom + " - " + I18N.mrs("dw4.ins_inserti");
		else if (numDots == 3) //è un annesso
			return descrizioneFascicoloCustom + " - " + I18N.mrs("dw4.ins_annessi");
		else
			return descrizioneFascicoloCustom + " - " + I18N.mrs("acl.insert");
	}

	public void setInsFascicoloTitle(String title) {
		this.insFascicoloTitle = title;
	}

	/**
	 * Imposta il titolo della maschera di inserimento del fascicolo in base al valore
	 * estratto dall'attributo 'classif_cod_infasc'
	 */
	protected void setInsFascicoloTitleByNumSottoFasc() {
		int numDots = StringUtil.count(fasc_numero_sottofasc, ".");

		if (numDots == 1)
			this.insFascicoloTitle = "dw4.ins_sottofascicoli";
		else if (numDots == 2)
			this.insFascicoloTitle = "dw4.ins_inserti";
		else if (numDots >= 3)
			this.insFascicoloTitle = "dw4.ins_annessi";
		else
			this.insFascicoloTitle = DEFAULT_FASCICOLO_TITLE;
	}

	public String getCodiceFascicoloCustom() {
		return codiceFascicoloCustom;
	}

	public void setCodiceFascicoloCustom(String codiceFascicoloCustom) {
		this.codiceFascicoloCustom = codiceFascicoloCustom;
	}

	public String getDescrizioneFascicoloCustom() {
		return descrizioneFascicoloCustom;
	}

	public void setDescrizioneFascicoloCustom(String descrizioneFascicoloCustom) {
		this.descrizioneFascicoloCustom = descrizioneFascicoloCustom;
	}

	public void init(Document domDocumento) {
		xml = domDocumento.asXML();
		this.fascicolo = new Fascicolo();
		this.fascicolo.init(domDocumento);

		initSottofascicoloFields(domDocumento);

		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(domDocumento, "fascicolo");

		// caricamento di eventuali preferenze dell'applicazione (es. lunghezza min. del campo oggetto)
		setAppStringPreferences(XMLUtil.parseStrictAttribute(domDocumento, "/response/@appStringPreferences"));

		// attributi della response necessari alla generazione
		// della maschera di inserimento di un fascicolo/sottofascicolo
		fasc_numero_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@fasc_numero_sottofasc");
		String physDoc_infasc = XMLUtil.parseAttribute(domDocumento, "response/@physDoc_infasc");
		classif_cod_infasc = XMLUtil.parseAttribute(domDocumento, "response/@classif_cod_infasc");
		classif_infasc = XMLUtil.parseAttribute(domDocumento, "response/@classif_infasc");
		String bAssegnaLinkFasc = XMLUtil.parseAttribute(domDocumento, "response/@bAssegnaLinkFasc");

		// Imposto il titolo della pagina di creazione del fascicolo
		// in base al valore dell'attributo 'fasc_numero_sottofasc'
		setInsFascicoloTitleByNumSottoFasc();

		// Imposto i parametri tramite i quali viene generata la maschera di inserimento
		// di un fascicolo

		// Non visualizza l'elemento se si e' in fase di inserimento di doc in fascicolo
		if ((physDoc_infasc.length() == 0 || bAssegnaLinkFasc.equals("true")) && fasc_numero_sottofasc.length() == 0)
			hideIfInsInFascOrSottofasc = true;

		// Non visualizza l'elemento se si e' in fase di inserimento di doc(classificato) in fascicolo oppure
		// si è in inserimento di un sottofascicolo
		if ((physDoc_infasc.length() == 0 || (physDoc_infasc.length() > 0 && classif_cod_infasc.equals(""))) && fasc_numero_sottofasc.length() == 0)
			hideIfInsInFascWithoutClassifOrSottofasc = true;

		if (physDoc_infasc.length() > 0 && fasc_numero_sottofasc.length() == 0 && !bAssegnaLinkFasc.equals("true"))
			showIfInsInFascAndNotSottofasc = true;

		if (physDoc_infasc.length() > 0 && !classif_cod_infasc.equals("") && fasc_numero_sottofasc.length() == 0)
			showIfInsInFascWithClassifAndNotSottofasc = true;

		if (showIfInsInFascAndNotSottofasc || showIfInsInFascWithClassifAndNotSottofasc) {
			// Caso di copia doc in fascicolo (completamento del bean del fascicolo per
			// il riempimento del form di inserimento)
			fascicolo.getClassif().setCod(classif_cod_infasc);
			fascicolo.getClassif().setText(classif_infasc);
			fascicolo.getAssegnazioneRPA().setNome_persona(XMLUtil.parseAttribute(domDocumento, "response/@nome_persona_infasc"));
			fascicolo.getAssegnazioneRPA().setCod_persona(XMLUtil.parseAttribute(domDocumento, "response/@cod_persona_infasc"));
			fascicolo.getAssegnazioneRPA().setNome_uff(XMLUtil.parseAttribute(domDocumento, "response/@nome_uff_infasc"));
			fascicolo.getAssegnazioneRPA().setCod_uff(XMLUtil.parseAttribute(domDocumento, "response/@cod_uff_infasc"));
			fascicolo.getAssegnazioneRPA().setTipo_uff(XMLUtil.parseAttribute(domDocumento, "response/@tipo_uff_infasc"));
			fascicolo.getAssegnazioneRPA().setDiritto(XMLUtil.parseAttribute(domDocumento, "response/@diritto_infasc"));
		}

		if (fasc_numero_sottofasc.length() > 0)
			showIfSottoFasc = true;

		if (fasc_numero_sottofasc.length() == 0)
			showIfFasc = true;

		// caricamento info su eventuale fascicolo speciale
		fascicoloSpecialeInfo.init(XMLUtil.createDocument(domDocumento, "/response/fascicolo_speciale_info"));

		// In caso sia attiva la gestione dei rif int CC e la lista sia
		// vuota carico il primo rif int CC
		if (this.showIfFasc
				&& (this.fascicolo.getAssegnazioneCC() == null || this.fascicolo.getAssegnazioneCC().size() == 0))
			this.getFascicolo().getAssegnazioneCC().add(new Rif());

		// stato iniziale del checkbox "Invio Email di notifica"
		String prefCheckboxMail = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("CheckboxEmailNotifica"));
		if (prefCheckboxMail != null && prefCheckboxMail.toLowerCase().equals("no"))
			this.getFascicolo().setSendMailRifInterni(false);

		// ERM012596 - rtirabassi - notifica capillare
		String prefCheckboxMailDetail = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("CheckboxEmailNotificaCapillare"));
		if (prefCheckboxMailDetail != null && prefCheckboxMailDetail.toLowerCase().equals("si")) {
			getFascicolo().setSendMailRifInterni(false);
			getFascicolo().setSendMailSelectedRifInterni(true);
		}
		
		//sstagni - check oggetti diversi
		classifOD = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("notificaOggettiDiversiConClassificazione"));

		// porzione di template personalizzato per il cliente
		personalDirCliente = XMLUtil.parseStrictAttribute(domDocumento, "/response/@personalDirCliente");

		codiceFascicoloCustom = XMLUtil.parseStrictAttribute(domDocumento, "/response/@codice_fasc");
		descrizioneFascicoloCustom = XMLUtil.parseStrictAttribute(domDocumento, "/response/@descrizione_fasc");
		
		this.initTags();
    }

	/**
	 * In caso di nuovo sottofascicolo i campi con i quali riempire la maschera
	 * sono contenuti direttamente sulla response. Occorre quindi eseguire la sovrascrittura
	 * del bean fascicolo
	 *
	 * @param domDocumento
	 */
	private void initSottofascicoloFields(Document domDocumento) {
		String fasc_numero_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@fasc_numero_sottofasc");
		if (fasc_numero_sottofasc != null && fasc_numero_sottofasc.length() > 0)
			this.fascicolo.setNumero(fasc_numero_sottofasc);
		String nome_persona_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@nome_persona_sottofasc");
		if (nome_persona_sottofasc != null && nome_persona_sottofasc.length() > 0)
			this.fascicolo.getAssegnazioneRPA().setNome_persona(nome_persona_sottofasc);
		String nome_uff_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@nome_uff_sottofasc");
		if (nome_uff_sottofasc != null && nome_uff_sottofasc.length() > 0)
			this.fascicolo.getAssegnazioneRPA().setNome_uff(nome_uff_sottofasc);
		String cod_persona_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@cod_persona_sottofasc");
		if (cod_persona_sottofasc != null && cod_persona_sottofasc.length() > 0)
			this.fascicolo.getAssegnazioneRPA().setCod_persona(cod_persona_sottofasc);
		String cod_uff_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@cod_uff_sottofasc");
		if (cod_uff_sottofasc != null && cod_uff_sottofasc.length() > 0)
			this.fascicolo.getAssegnazioneRPA().setCod_uff(cod_uff_sottofasc);
		String tipo_uff_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@tipo_uff_sottofasc");
		if (tipo_uff_sottofasc != null && tipo_uff_sottofasc.length() > 0)
			this.fascicolo.getAssegnazioneRPA().setTipo_uff(tipo_uff_sottofasc);
		String classif_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@classif_sottofasc");
		if (classif_sottofasc != null && classif_sottofasc.length() > 0)
			this.fascicolo.getClassif().setText(classif_sottofasc);
		String classif_cod_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@classif_cod_sottofasc");
		if (classif_cod_sottofasc != null && classif_cod_sottofasc.length() > 0)
			this.fascicolo.getClassif().setCod(classif_cod_sottofasc);
	}

	public DocDocWayDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;

			// Inserimento manuale:
			// 1) svuoto eventuali dati di classificazione da titolario residui
			// 2) formatto i dati inseriti dall'operatore tramite inserimento manuale
			if (!isClassificazioneDaTitolario() && fascicolo.getClassifNV() != null
						&& fascicolo.getClassifNV().getCod() != null && !fascicolo.getClassifNV().getCod().equals("")
						&& fascicolo.getClassifNV().getText() != null && !fascicolo.getClassifNV().getText().equals("")) {
				fascicolo.setClassif(new Classif());
				fascicolo.getClassif().setCod(fascicolo.getClassifNV().getCod());
				fascicolo.getClassif().setText(fascicolo.getClassifNV().getCod() + " - " + fascicolo.getClassifNV().getText());
				fascicolo.getClassifNV().setCod("");
				fascicolo.getClassifNV().setText("");
			}


			//20141218 fcappelli - gestione della tipologia del fascicolo
			if (codiceFascicoloCustom != null && !codiceFascicoloCustom.equals("")) {
				fascicolo.setCodiceFascicoloCustom(codiceFascicoloCustom);
				fascicolo.setDescrizioneFascicoloCustom(descrizioneFascicoloCustom);
			}

			formsAdapter.getDefaultForm().addParams(this.fascicolo.asFormAdapterParams(""));
			XMLDocumento response = super._saveDocument("fascicolo", "list_of_doc");

			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			String assignAndClose = XMLUtil.parseAttribute(response.getDocument(), "response/@assignAndClose", "false");
			if (isPopupPage() && assignAndClose.equals("true")) {
				// inserimento da popup con assegnazione di un documento al fascicolo
				formsAdapter.fillFormsFromResponse(response);
				this.init(response.getDocument());
				setSessionAttribute("docEditFascicolo", this);

				this.setPopupPage(true);
				return null;
			}
			else {
				// inserimento classico del fascicolo
				buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", response);
				return "showdoc@fascicolo@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response);
			this.init(response.getDocument());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return null;
	}

	/**
	 * Lookup su Voce di Indice
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupVoceIndice() throws Exception {
		String value = (fascicolo.getVoce_indice() != null && fascicolo.getVoce_indice().getText() != null) ? fascicolo.getVoce_indice().getText() : "";

		String aliasName 	= "tit_voce";
		String aliasName1 	= "";
		String titolo 		= "xml,/indice_titolario/@voce"; //titolo
		String ord 			= "xml(xpart:/indice_titolario/@voce)"; //ord

		String campi 		= ".voce_indice.text=xml,/indice_titolario/@voce" +
								" ; .classif.text=xml,/indice_titolario/compilazione_automatica/classif/@cod \"-\" xml,/indice_titolario/compilazione_automatica/classif" +
								" ; .classif.@cod=xml,/indice_titolario/compilazione_automatica/classif/@cod" +
								" ; .@scarto=xml,/indice_titolario/compilazione_automatica/scarto/@val" +
								" ; .classif.text_ro=xml,/indice_titolario/compilazione_automatica/classif/@cod \"-\" xml,/indice_titolario/compilazione_automatica/classif";

		String db 			= AppUtil.getXdocwayprocDbName(getFormsAdapter().getDb()); //db
		String newRecord 	= ""; //newRecord
		String xq			= "[tit_validitatipodoc]=arrivo"; //extraQuery

		// Visto che e' stato selezionato il lookup delle voci di indice
		// attivo l'inserimento della classificazione da titolario (nel caso
		// in cui l'utente avesse utilizzato la classificazione manuale)
		this.setClassificazioneDaTitolario(true);

		callLookup(fascicolo, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);

		return null;
	}

	/**
	 * Pulizia dei campi di Lookup di Voce di Indice
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearLookupVoceIndice() throws Exception {
		String campi = ".voce_indice.text=xml,/indice_titolario/@voce" +
				" ; .classif.text=xml,/indice_titolario/compilazione_automatica/classif/@cod \"-\" xml,/indice_titolario/compilazione_automatica/classif" +
				" ; .classif.@cod=xml,/indice_titolario/compilazione_automatica/classif/@cod" +
				" ; .@scarto=xml,/indice_titolario/compilazione_automatica/scarto/@val" +
				" ; .classif.text_ro=xml,/indice_titolario/compilazione_automatica/classif/@cod \"-\" xml,/indice_titolario/compilazione_automatica/classif";

		return clearField(campi, fascicolo);
	}

	/**
	 * Thesauro vincolato su titolario di classificazione
	 *
	 * @return
	 * @throws Exception
	 */
	public String thVincolatoTitolarioClassificazione() throws Exception {
		return showThesRel(false);
	}

	/**
	 * Gerarchia su titolario di classificazione
	 *
	 * @return
	 * @throws Exception
	 */
	public String gerarchiaTitolarioClassificazione() throws Exception {
		return showThesRel(true);
	}

	/**
	 * Caricamento del titolario di classificazione
	 *
	 * @param showGerarchia
	 * @return
	 * @throws Exception
	 */
	private String showThesRel(boolean showGerarchia) throws Exception {
		String keypath = "classif";
		String startkey = Const.TITOLARIO_CLASSIF_NODO_RADICE;

		// Filtro impostato su campo codice
		String value = (fascicolo.getClassif() != null && !"".equals(fascicolo.getClassif().getFiltroCod())) ? fascicolo.getClassif().getFiltroCod() : "";
		if (value.length() > 0) {
			// Devo formattare il valore passato in base alla classificazione
			value = ClassifUtil.formatNumberClassifCode(value);

			keypath = "CLASSIF_FROM_CODE";
			startkey = "lookupHierFromClassifCode";
		}

		String toDo = "";
		if (showGerarchia)
			toDo = "print_all";

		callShowThesRel(fascicolo, I18N.mrs("dw4.titolario_di_classificazione"), "NT", keypath, startkey, AppUtil.getXdocwayprocDbName(getFormsAdapter().getDb()), toDo, value);

		// Azzero il campo nel form
		this.fascicolo.getClassif().setFiltroCod("");

		return null;
	}

	/**
	 * RifintLookup su ufficio RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_rpa() throws Exception {
		String value 	= (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_uff() != null) ? fascicolo.getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_persona() != null) ? fascicolo.getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";

		rifintLookupUfficio(fascicolo, value, value2, campi);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su ufficio RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_rpa() throws Exception {
		String value = (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_uff() != null) ? fascicolo.getAssegnazioneRPA().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_uff" +
							"|.assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_uff" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneRPA().setUfficio_completo(false);
		}

		return null;
	}

	/**
	 * RifintLookup su persona RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_rpa() throws Exception {
		String value 	= (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_uff() != null) ? fascicolo.getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_persona() != null) ? fascicolo.getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";

		rifintLookupPersona(fascicolo, value, value2, campi);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su persona RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_rpa() throws Exception {
		String value = (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_persona() != null) ? fascicolo.getAssegnazioneRPA().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneRPA().setUfficio_completo(false);
		}

		return null;
	}

	/**
	 * Lookup su ruolo RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_rpa() throws Exception {
		String value = (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_uff() != null) ? fascicolo.getAssegnazioneRPA().getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi 		= ".assegnazioneRPA.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		lookupRuolo(fascicolo, value, campi, fascicolo.getCod_amm_aoo());
		return null;
	}

	/**
	 * Pulizia dei campi di lookup su ruolo RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_rpa() throws Exception {
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneRPA.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		return clearField(campi, fascicolo);
	}

	/**
	 * RifintLookup su ufficio ITF
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_itf() throws Exception {
		String value 	= (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_uff() != null) ? fascicolo.getAssegnazioneITF().getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_persona() != null) ? fascicolo.getAssegnazioneITF().getNome_persona() : "";
		String campi 	= ".assegnazioneITF.@nome_uff" +
								"|.assegnazioneITF.@nome_persona" +
								"|.assegnazioneITF.@cod_uff" +
								"|.assegnazioneITF.@cod_persona";

		rifintLookupUfficio(fascicolo, value, value2, campi);
		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su ufficio ITF
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_itf() throws Exception {
		String value = (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_uff() != null) ? fascicolo.getAssegnazioneITF().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneITF.@nome_uff" +
							"|.assegnazioneITF.@nome_persona" +
							"|.assegnazioneITF.@cod_uff" +
							"|.assegnazioneITF.@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneITF().setUfficio_completo(false);
		}
		return null;
	}

	/**
	 * RifintLookup su persona ITF
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_itf() throws Exception {
		String value 	= (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_uff() != null) ? fascicolo.getAssegnazioneITF().getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_persona() != null) ? fascicolo.getAssegnazioneITF().getNome_persona() : "";
		String campi 	= ".assegnazioneITF.@nome_uff" +
								"|.assegnazioneITF.@nome_persona" +
								"|.assegnazioneITF.@cod_uff" +
								"|.assegnazioneITF.@cod_persona";

		rifintLookupPersona(fascicolo, value, value2, campi);
		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su persona ITF
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_itf() throws Exception {
		String value = (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_persona() != null) ? fascicolo.getAssegnazioneITF().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneITF.@nome_persona" +
							"|.assegnazioneITF.@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneITF().setUfficio_completo(false);
		}
		return null;
	}

	/**
	 * Lookup su ruolo ITF
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_itf() throws Exception {
		String value = (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_uff() != null) ? fascicolo.getAssegnazioneITF().getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi 		= ".assegnazioneITF.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		lookupRuolo(fascicolo, value, campi, fascicolo.getCod_amm_aoo());
		return null;
	}

	/**
	 * Pulizia dei campi di lookup su ruolo ITF
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_itf() throws Exception {
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneITF.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		return clearField(campi, fascicolo);
	}

	/**
	 * RifintLookup su ufficio CC
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;

		String value 	= (fascicolo.getAssegnazioneCC() != null && fascicolo.getAssegnazioneCC().get(num) != null) ? fascicolo.getAssegnazioneCC().get(num).getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneCC() != null && fascicolo.getAssegnazioneCC().get(num) != null) ? fascicolo.getAssegnazioneCC().get(num).getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";

		rifintLookupUfficio(fascicolo, value, value2, campi);
		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su ufficio CC
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;

		String value = (fascicolo.getAssegnazioneCC() != null && fascicolo.getAssegnazioneCC().get(num) != null) ? fascicolo.getAssegnazioneCC().get(num).getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_uff" +
							"|.assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_uff" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneCC().get(num).setUfficio_completo(false);
		}
		return null;
	}

	/**
	 * RifintLookup su persona CC
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;

		String value	= (fascicolo.getAssegnazioneCC() != null && fascicolo.getAssegnazioneCC().get(num) != null) ? fascicolo.getAssegnazioneCC().get(num).getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneCC() != null && fascicolo.getAssegnazioneCC().get(num) != null) ? fascicolo.getAssegnazioneCC().get(num).getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";

		rifintLookupPersona(fascicolo, value, value2, campi);
		rif.setUfficio_completo(false);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su persona CC
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;

		String value = (fascicolo.getAssegnazioneCC() != null && fascicolo.getAssegnazioneCC().get(num) != null) ? fascicolo.getAssegnazioneCC().get(num).getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneCC().get(num).setUfficio_completo(false);
		}
		return null;
	}

	/**
	 * Lookup su ruolo CC
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;

		String value = (fascicolo.getAssegnazioneCC() != null && fascicolo.getAssegnazioneCC().get(num) != null) ? fascicolo.getAssegnazioneCC().get(num).getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		lookupRuolo(fascicolo, value, campi, fascicolo.getCod_amm_aoo());
		return null;
	}

	/**
	 * Pulizia dei campi di lookup su ruolo CC
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare
		return clearField(campi, fascicolo);
	}

	/**
	 * Cancellazione di un rif int in CC
	 * @return
	 */
	public String deleteRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		fascicolo.deleteRifintCC(rif);
		return null;
	}

	/**
	 * Aggiunta di un rif int in CC
	 * @return
	 */
	public String addRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		fascicolo.addRifintCC(rif);
		return null;
	}

	/**
	 * Imposta o meno l'ufficio completo per il rif int selezionato
	 */
	public String ufficioCompleto() {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		if (rif != null)
			rif.setUfficioCompleto();
		return null;
	}

	/**
	 * Controllo dei campo obbligatori
	 *
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField(){
		boolean result = false;

		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione

		// validazione dei campi custom caricati nella pagina
		result = getCustomfields().checkRequiredFields(false, formatoData, this);

		// Controllo se il campo 'oggetto' e' valorizzato
		if (fascicolo.getOggetto() == null || "".equals(fascicolo.getOggetto().trim())) {
			this.setErrorMessage("templateForm:fasc_oggetto", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.object") + "'");
		    result = true;
		}
		else {
			// Controllo sulla presenza di caratteri non consentiti all'interno del campo oggetto
			if (fascicolo.getOggetto().contains("|")) { // TODO gestire i caratteri non consentiti da file di properties
				this.setErrorMessage("templateForm:fasc_oggetto", I18N.mrs("dw4.il_campo_oggetto_contiene_un_carattere_non_valido") + ": | ");
				result = true;
			}
		}

		// Controllo se la classificazione e' valorizzata (sia in inserimento da titolario che manuale)
		if (this.isClassificazioneDaTitolario()) {
			if (fascicolo.getClassif() == null || fascicolo.getClassif().getCod() == null || "".equals(fascicolo.getClassif().getCod().trim())) {
				// Imposto la classificazione automatica
				String classifValue = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ClassificazioneAutomatica"));
				if (classifValue != null && classifValue.length() > 0) {
					fascicolo.getClassif().setText(classifValue);
					int index = classifValue.indexOf(" - ");
					if (index != -1)
						fascicolo.getClassif().setCod(classifValue.substring(0, index));
				}
			}

			// Classificazione da titolario
			if (fascicolo.getClassif() == null || fascicolo.getClassif().getCod() == null || "".equals(fascicolo.getClassif().getCod().trim())) {
				String classifField = "";
				if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("classificazioneDaCodice", false))
					classifField = "templateForm:classif_input";
				this.setErrorMessage(classifField, I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.classif") + "'");
				result = true;
			}
		}
		else {
			// Classificazione manuale
			if (fascicolo.getClassifNV() == null || fascicolo.getClassifNV().getCod() == null || "".equals(fascicolo.getClassifNV().getCod().trim())
					|| fascicolo.getClassifNV().getText() == null || "".equals(fascicolo.getClassifNV().getText().trim())) {

				ArrayList<String> fieldIds = new ArrayList<String>();
				if (fascicolo.getClassifNV() == null || fascicolo.getClassifNV().getCod() == null || "".equals(fascicolo.getClassifNV().getCod().trim()))
					fieldIds.add("templateForm:classif_cod_nv");
				if (fascicolo.getClassifNV() == null || fascicolo.getClassifNV().getText() == null || "".equals(fascicolo.getClassifNV().getText().trim()))
					fieldIds.add("templateForm:classif_text_nv");
				this.setErrorMessage((String[]) fieldIds.toArray(new String[] {}), I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.classif") + "'");
				result = true;
			}

			// Verifico che il codice di classificazione specificato sia corretto
			if (fascicolo.getClassifNV() != null && fascicolo.getClassifNV().getCod() != null && fascicolo.getClassifNV().getCod().trim().length() > 0) {
				if (!ClassifUtil.isClassifCodCorrect(fascicolo.getClassifNV().getCod())) {
					this.setErrorMessage("templateForm:classif_cod_nv", I18N.mrs("dw4.codice_classificazione_non_valido_Inserire_un_codice_nella_forma_xx_xx"));
					result = true;
				}
			}
		}

		// classificazione con motivazione
		if (fascicolo.getClassif() != null && fascicolo.getClassif().getCod() != null
				&& !(fascicolo.getClassif().getCod().equals(""))
				&& fascicolo.getClassif().getCod().trim().equals(classifOD)) {
			if (fascicolo.getMotiv_ogg_div() == null || fascicolo.getMotiv_ogg_div().length() == 0) {
				this.setErrorMessage("templateForm:classif_motivazione", I18N.mrs("dw4.e_necessario_fornire_una_motivazione_per_la_classificazione_impostata"));
				result = true;
			}
			else if (fascicolo.getMotiv_ogg_div().length() < 15) {
				this.setErrorMessage("templateForm:classif_motivazione", I18N.mrs("dw4.la_motivazione_fornita_deve_essere_lunga_almeno_15_caratteri"));
				result = true;
			}
			else if (StringUtil.containsRipetizioni(fascicolo.getMotiv_ogg_div(), 3)) { // TODO controllare il valore 3 specificato
				this.setErrorMessage("templateForm:classif_motivazione", I18N.mrs("dw4.ripetizione_inusuale_di_caratteri_per_la_motivazione_fornita"));
				result = true;
			}
		}

		// Verifico che l'anno specificato sia corretto
		if (fascicolo.getAnno() != null && fascicolo.getAnno().length() > 0) {
			if (!fascicolo.getAnno().matches("\\d{4}")) {
				this.setErrorMessage("templateForm:classif_anno", I18N.mrs("dw4.inserire_un_valore_corretto_per_il_campo_anno"));
				result = true;
			}
		}

		// Controllo che l'RPA sia stato selezionato
		if (fascicolo.getAssegnazioneRPA() == null ||
				((fascicolo.getAssegnazioneRPA().getNome_uff() == null || "".equals(fascicolo.getAssegnazioneRPA().getNome_uff().trim())) &&
						(fascicolo.getAssegnazioneRPA().getNome_persona() == null || "".equals(fascicolo.getAssegnazioneRPA().getNome_persona().trim())))) {

			String[] fieldIds = { "templateForm:rpa_nome_uff_input", "templateForm:rpa_nome_persona_input" };
			this.setErrorMessage(fieldIds, I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.proprietario") + "'");
			result = true;
		}
		return result;
	}

	/**
	 * Switch del form di inserimento da generico a specifico del fascicolo speciale
	 * @return
	 * @throws Exception
	 */
	public String switchToTableFS() throws Exception {
		try {
			formsAdapter.switchToTableFS(fascicoloSpecialeInfo.getId());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * Switch del form di inserimento da specifico del fascicolo speciale a generico
	 * @return
	 * @throws Exception
	 */
	public String switchToTableFascicolo() throws Exception {
		try {
			formsAdapter.switchToTableFascicolo();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	@Override
	public XmlEntity getModel() {
		return this.fascicolo;
	}	
	
}
