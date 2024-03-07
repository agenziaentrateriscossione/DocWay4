package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.model.FascicoloSpecialeInfo;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.Tipo;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditModifyFascicoloPersonale extends DocEditModifyFascicolo {

	private final String DEFAULT_FASCICOLO_PERSONALE_TITLE = "dw4.fascicolo_del_personale_Modifica";
	
	private FascicoloSpecialeInfo fascicoloSpecialeInfo = new FascicoloSpecialeInfo();
	
	private List<Option> categorie = new ArrayList<Option>();
	private List<Option> tipi = new ArrayList<Option>();
	private List<Tipo> categorie_tipi = new ArrayList<Tipo>();
	
	public DocEditModifyFascicoloPersonale() throws Exception {
		super();
	}
	
	public FascicoloSpecialeInfo getFascicoloSpecialeInfo() {
		return fascicoloSpecialeInfo;
	}

	public void setFascicoloSpecialeInfo(FascicoloSpecialeInfo fascicoloSpecialeInfo) {
		this.fascicoloSpecialeInfo = fascicoloSpecialeInfo;
	}
	
	public List<Option> getCategorie() {
		return categorie;
	}

	public void setCategorie(List<Option> categorie) {
		this.categorie = categorie;
	}
	
	public List<Option> getTipi() {
		return tipi;
	}

	public void setTipi(List<Option> tipi) {
		this.tipi = tipi;
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		super.init(dom);
		
		setModFascicoloPersonaleTitle(XMLUtil.parseStrictAttribute(dom, "/response/@fasc_numero_sottofasc"));
		
		// caricamento della classificazione specificata per il fascicolo speciale
		fascicoloSpecialeInfo.init(XMLUtil.createDocument(dom, "/response/fascicolo_speciale_info"));
		if (fascicoloSpecialeInfo != null && fascicoloSpecialeInfo.getId() != null && fascicoloSpecialeInfo.getId().length() > 0) {
			if (fascicoloSpecialeInfo.getClassif().getCod() != null) {
				getFascicolo().getClassif().setCod(fascicoloSpecialeInfo.getClassif().getCod());
				getFascicolo().getClassif().setText(fascicoloSpecialeInfo.getClassif().getText());
				getFascicolo().getClassif().setText_ro(fascicoloSpecialeInfo.getClassif().getText_ro());
			}
		}
		
		// caricamento delle opzioni da visualizzare nel select delle categorie
		categorie = addEmptyOption(categorie, false);
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
		
		// caricamento delle opzioni relative ai tipi delle categorie
		categorie_tipi = XMLUtil.parseSetOfElement(dom, "/response/selectFascPersTypes/tipi/tipo", new Tipo());
		if (getFascicolo().getFascicolo_speciale() != null && getFascicolo().getFascicolo_speciale().getCategoria() != null)
			fillTipiInSelect(getFascicolo().getFascicolo_speciale().getCategoria());
		else
			tipi = addEmptyOption(tipi, false);
	}
	
	/**
	 * salvataggio del fascicolo del personale
	 */
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredFieldFascicoloPersonale()) return null;
			
			getFormsAdapter().getDefaultForm().addParams(getFascicolo().asFormAdapterParams(""));
			XMLDocumento response = super._saveDocument("fascicolo", "list_of_doc");
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", "", "", "@personale", response, isPopupPage());		
			return "showdoc@fascicolo@personale@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * abbandono della modifica del fascicolo del personale
	 */
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", "", "", "@personale", response, isPopupPage());
			return "showdoc@fascicolo@personale@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Imposta il titolo della maschera di inserimento del fascicolo del personale
	 * @param fasc_numero_sottofasc
	 */
	private void setModFascicoloPersonaleTitle(String fasc_numero_sottofasc) {
		setModFascicoloTitleByNumSottoFasc(fasc_numero_sottofasc);

		if (modFascicoloTitle.equals(DEFAULT_FASCICOLO_TITLE))
			modFascicoloTitle = DEFAULT_FASCICOLO_PERSONALE_TITLE;
	}
	
	/**
	 * carica i tipi del fascicolo in base alla categoria selezionata
	 * 
	 * @param e nuova categoria selezionata
	 */
	public void loadTipiAfterCategoria(ValueChangeEvent e) {
		fillTipiInSelect(e.getNewValue()+"");
	}
	
	/**
	 * caricamento dei valori dei tipi all'interno del select in base alla
	 * categoria corrente
	 * @param categoriaCorrente
	 */
	private void fillTipiInSelect(String categoriaCorrente) {
		boolean found = false;
		if (categoriaCorrente != null && categoriaCorrente.length() > 0 
				&& categorie_tipi != null && categorie_tipi.size() > 0) {
						
			int i=0;
			while (i<categorie_tipi.size() && !found) {
				if (categorie_tipi.get(i) != null && categorie_tipi.get(i).getCategoria() != null) {
					if (categoriaCorrente.equals(categorie_tipi.get(i).getCategoria())) {
						found = true;
						
						tipi = new ArrayList<Option>();
						tipi = addEmptyOption(tipi, true);
						tipi.addAll(categorie_tipi.get(i).getTipi());
					}
				}
				i++;
			}
		}
		
		// nel caso in cui non sia stata trovata alcuna corrispondenza
		// con la categoria specificata si procede con l'azzeramento del
		// select dei tipi
		if (!found) {
			tipi = new ArrayList<Option>();
			tipi = addEmptyOption(tipi, false);
		}
	}
	
	/**
	 * aggiunge un opzione vuota alla lista passata
	 */
	private List<Option> addEmptyOption(List<Option> lista, boolean emptySelected) {
		if (lista != null) {
			Option empty = new Option();
			empty.setLabel("");
			empty.setValue("");
			if (emptySelected)
				empty.setSelected("true");
			lista.add(empty);
		}
		return lista;
	}
	
	/**
	 * lookup su luogo di nascita
	 * @return
	 * @throws Exception
	 */
	public String lookupLuogoNascita() throws Exception {
		try {
			String value = (getFascicolo().getFascicolo_speciale().getLuogo_nascita() != null) ? getFascicolo().getFascicolo_speciale().getLuogo_nascita() : "";
			String aclDb = getFormsAdapter().getDefaultForm().getParam("aclDb");
			
			String aliasName 	= "comuni_nome";
			String aliasName1 	= "";
			String titolo 		= "xml,/comune/@nome"; //titolo 
			String ord 			= "xml(xpart:/comune/@nome)"; //ord 
			String campi 		= ".fascicolo_speciale.@luogo_nascita=xml,/comune/@nome"; //campi
			String db 			= aclDb; //db 
			String newRecord 	= "/base/acl/engine/acl.jsp?db=" + aclDb + ";dbTable=comune;fillField=comune.@nome;rightCode=ACL-4"; //newRecord 
			String xq			= ""; //extraQuery
			
			callLookup(getFascicolo(), aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * clear su lookup su luogo di nascita
	 * @return
	 * @throws Exception
	 */
	public String clearLookupLuogoNascita() throws Exception {
		try {
			String campi = ".fascicolo_speciale.@luogo_nascita=xml,/comune/@nome"; 
			return clearField(campi, getFascicolo());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	private boolean checkRequiredFieldFascicoloPersonale(){
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;
		
		if (isShowIfSottoFasc()) {
			// Controllo se il campo 'oggetto' e' valorizzato
			if (getFascicolo().getOggetto() == null || "".equals(getFascicolo().getOggetto().trim())) {
				this.setErrorMessage("templateForm:fasc_oggetto", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.object") + "'");
			    result = true;
			}
		}
		if (isShowIfFasc()) {
			if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("attivaCampiNomeCognomeFascPersonale", false)) {
				// Controllo se il campo 'nome' e' valorizzato
				if (getFascicolo().getNominativo() == null || getFascicolo().getNominativo().getNome() == null || "".equals(getFascicolo().getNominativo().getNome().trim())) {
					this.setErrorMessage("templateForm:fasc_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.name") + "'");
				    result = true;
				}
				
				// Controllo se il campo 'cognome' e' valorizzato
				if (getFascicolo().getNominativo() == null || getFascicolo().getNominativo().getCognome() == null || "".equals(getFascicolo().getNominativo().getCognome().trim())) {
					this.setErrorMessage("templateForm:fasc_cognome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.surname") + "'");
				    result = true;
				}
			}
			else {
				// Controllo se il campo 'nominativo' e' valorizzato
				if (getFascicolo().getOggetto() == null || "".equals(getFascicolo().getOggetto().trim())) {
					this.setErrorMessage("templateForm:fasc_nominativo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.nominativo") + "'");
				    result = true;
				}
			}
			
			// mbernardini 21/05/2018 : i controlli su campi specifici del template sono validi solo se si tratta dell'inserimento/modifica di un fascicolo radice
			
			if (!getFormsAdapter().checkBooleanFunzionalitaDisponibile("fascPersNumPos", false)) {
				// Controllo se il campo 'matricola' e' valorizzato
				if (getFascicolo().getFascicolo_speciale().getMatricola() == null || "".equals(getFascicolo().getFascicolo_speciale().getMatricola().trim())) {
					this.setErrorMessage("templateForm:fasc_matricola", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.code") + "'");
				    result = true;
				}
			}
			
			// Controllo sul formato dei campi data
			if (getFascicolo().getFascicolo_speciale().getData_nascita() != null && getFascicolo().getFascicolo_speciale().getData_nascita().length() > 0) {
				if (!DateUtil.isValidDate(getFascicolo().getFascicolo_speciale().getData_nascita(), formatoData)) {
					this.setErrorMessage("templateForm:fasc_data_nascita", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.birthdate") + "': " + formatoData.toLowerCase());
					result = true;
				}
			}
			if (getFascicolo().getFascicolo_speciale().getData_assunzione() != null && getFascicolo().getFascicolo_speciale().getData_assunzione().length() > 0) {
				if (!DateUtil.isValidDate(getFascicolo().getFascicolo_speciale().getData_assunzione(), formatoData)) {
					this.setErrorMessage("templateForm:fasc_data_assunzione", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_assunzione") + "': " + formatoData.toLowerCase());
					result = true;
				}
			}
			if (getFascicolo().getFascicolo_speciale().getData_cessazione() != null && getFascicolo().getFascicolo_speciale().getData_cessazione().length() > 0) {
				if (!DateUtil.isValidDate(getFascicolo().getFascicolo_speciale().getData_cessazione(), formatoData)) {
					this.setErrorMessage("templateForm:fasc_data_cessazione", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_cessazione") + "': " + formatoData.toLowerCase());
					result = true;
				}
			}
		}
		
		return result;
	}
	
}
