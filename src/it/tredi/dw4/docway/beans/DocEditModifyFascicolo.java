package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Fascicolo;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditModifyFascicolo extends DocWayDocedit {
	private DocDocWayDocEditFormsAdapter formsAdapter;
	private Fascicolo fascicolo = new Fascicolo();
	
	protected final String DEFAULT_FASCICOLO_TITLE = "dw4.mod_fascicoli";
	protected String modFascicoloTitle = DEFAULT_FASCICOLO_TITLE; // Titolo della pagina di inserimento del fascicolo (fascicolo, sottofascicolo, inserto, annesso)
	
	// Parametri necessari alla generazione della maschera di inserimento di un fascicolo
	private boolean hideIfInsInFascWithoutClassifOrSottofasc = false;
	private boolean showIfInsInFascAndNotSottofasc = false;
	private boolean showIfInsInFascWithClassifAndNotSottofasc = false;
	private boolean hideIfInsInFascOrSottofasc = false;
	private boolean showIfSottoFasc = false;
	private boolean showIfFasc = false;
	
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
	
	private String xml = "";

	public DocEditModifyFascicolo() throws Exception {
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
	
	public String getModFascicoloTitle() {
		if (modFascicoloTitle == null || modFascicoloTitle.length() == 0)
			modFascicoloTitle = DEFAULT_FASCICOLO_TITLE;
		
		if (descrizioneFascicoloCustom == null || descrizioneFascicoloCustom.equals(""))
			return I18N.mrs(modFascicoloTitle);
		else
			return descrizioneFascicoloCustom + " - " + I18N.mrs("acl.modify");
	}

	public void setModFascicoloTitle(String title) {
		this.modFascicoloTitle = title;
	}
	
	/**
	 * Imposta il titolo della maschera di modifica del fascicolo in base al valore
	 * estratto dall'attributo 'classif_cod_infasc'
	 */
	protected void setModFascicoloTitleByNumSottoFasc(String fasc_numero_sottofasc) {
		int numDots = StringUtil.count(fasc_numero_sottofasc, ".");
		
		if (numDots == 1)
			this.modFascicoloTitle = "dw4.mod_sottofascicoli";
		else if (numDots == 2)
			this.modFascicoloTitle = "dw4.mod_inserti";
		else if (numDots >= 3)
			this.modFascicoloTitle = "dw4.mod_annessi";
		else
			this.modFascicoloTitle = DEFAULT_FASCICOLO_TITLE;
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
		
		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(domDocumento, "fascicolo");
		
		// caricamento di eventuali preferenze dell'applicazione (es. lunghezza min. del campo oggetto)
		setAppStringPreferences(XMLUtil.parseStrictAttribute(domDocumento, "/response/@appStringPreferences"));
		
		// Imposto gli altri attributi della response necessari alla generazione
		// della maschera di inserimento di un fascicolo
		String fasc_numero_sottofasc = XMLUtil.parseAttribute(domDocumento, "response/@fasc_numero_sottofasc");
		String physDoc_infasc = XMLUtil.parseAttribute(domDocumento, "response/@physDoc_infasc");
		String classif_cod_infasc = XMLUtil.parseAttribute(domDocumento, "response/@classif_cod_infasc");
		String bAssegnaLinkFasc = XMLUtil.parseAttribute(domDocumento, "response/@bAssegnaLinkFasc");
		
		// Imposto il titolo della pagina di modifica del fascicolo
		// in base al valore dell'attributo 'fasc_numero_sottofasc'
		setModFascicoloTitleByNumSottoFasc(fasc_numero_sottofasc);
		
		// Imposto i parametri tramite i quali viene generata la maschera di modifica
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
		
		if (fasc_numero_sottofasc.length() > 0)
			showIfSottoFasc = true;
		
		if (fasc_numero_sottofasc.length() == 0)
			showIfFasc = true;
		
		//sstagni - check oggetti diversi
		classifOD = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("notificaOggettiDiversiConClassificazione"));
		
		// porzione di template personalizzato per il cliente
		personalDirCliente = XMLUtil.parseStrictAttribute(domDocumento, "/response/@personalDirCliente");
		
		codiceFascicoloCustom = XMLUtil.parseStrictAttribute(domDocumento, "/response/@codice_fasc");
		descrizioneFascicoloCustom = XMLUtil.parseStrictAttribute(domDocumento, "/response/@descrizione_fasc");
    }
	
	public DocDocWayDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			formsAdapter.getDefaultForm().addParams(this.fascicolo.asFormAdapterParams(""));
			XMLDocumento response = super._saveDocument("fascicolo", "list_of_doc");
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", response);		
			return "showdoc@fascicolo@reload";
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
			buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", response);
			return "showdoc@fascicolo@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
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
		String value 		= (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_uff() != null) ? fascicolo.getAssegnazioneITF().getNome_uff() : "";
		String campi 		= ".assegnazioneITF.@nome_uff=xml,/ruolo/nome";

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
		String campi = ".assegnazioneITF.@nome_uff=xml,/ruolo/nome"; 
		
		return clearField(campi, fascicolo);
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
		result = getCustomfields().checkRequiredFields(true, formatoData, this);
		
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
		
		// classificazione con motivazione
		if (fascicolo.getClassif() != null && fascicolo.getClassif().getCod() != null 
				&& !fascicolo.getClassif().getCod().equals("") && fascicolo.getClassif().getCod().trim().equals(classifOD)) {
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
		
		return result;
	}
	
}
