package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Arrivo;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLDocumento;

public class DocEditModifyArrivo extends DocEditDoc {
	private Arrivo doc = new Arrivo();
	
	private final String DEFAULT_ARRIVO_TITLE = "dw4.mod_arrivo";
	
	// parametri utilizzati per la verifica dei duplicati
	private String nomeMittenteInitValue = "";
	private String numProtMittenteInitValue = "";
	private String dataProtMittenteInitValue = "";
	
	public DocEditModifyArrivo() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public boolean isDocEditModify() {
		return true;
	}
	
	@Override
	public void init(Document domDocumento) {
		this.doc = new Arrivo();
		this.doc.init(domDocumento);
		
		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(domDocumento);
		
		// titolo della pagina di creazione del documento
		setInsArrivoTitleByCodRepertorio();
		
		// visualizzazione o meno dei campi di selezione della/e raccomandata/e
		setRaccomandataFields();
		
		// inizializzazione dei parametri di controllo dei duplicati
		if (this.doc.getRif_esterni() != null && this.doc.getRif_esterni().size() > 0 
				&& this.doc.getRif_esterni().get(0) != null) {
			if (this.doc.getRif_esterni().get(0).getNome() != null)
				nomeMittenteInitValue = this.doc.getRif_esterni().get(0).getNome();
			if (this.doc.getRif_esterni().get(0).getN_prot() != null)
				numProtMittenteInitValue = this.doc.getRif_esterni().get(0).getN_prot();
			if (this.doc.getRif_esterni().get(0).getData_prot() != null)
				dataProtMittenteInitValue = this.doc.getRif_esterni().get(0).getData_prot();
		}
		duplicatoVerificato = true;
	}
	
	/**
	 * Imposta il titolo della maschera di inserimento del documento
	 */
	private void setInsArrivoTitleByCodRepertorio() {
		if (doceditRep && descrizioneRepertorio != null && descrizioneRepertorio.length() > 0)
			docEditTitle = descrizioneRepertorio + " - " + (getFormsAdapter().checkBooleanFunzionalitaDisponibile("trasformaByDocEdit", false) ? I18N.mrs("dw4.trasformazioneRepertorio") : I18N.mrs("acl.modify"));
		else
			docEditTitle = I18N.mrs(DEFAULT_ARRIVO_TITLE);
	}
	
	/**
	 * Imposta se vedere o meno i campi di selezione della/e raccomandata/e (in base al 
	 * mezzo di trasmissione del documento
	 */
	private void setRaccomandataFields() {
		if (getDoc() != null && getDoc().getMezzo_trasmissione() != null 
				&& getDoc().getMezzo_trasmissione().getValue() != null 
				&& getDoc().getMezzo_trasmissione().getValue().startsWith("Raccomandata"))
			super.setShowRaccomandataFields(true);
		else
			super.setShowRaccomandataFields(false);
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// personalizzazione del salvataggio per il repertorio
			boolean isRepertorio = false;
			if (doc.getRepertorio() != null && doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
				isRepertorio = true;
			
			boolean rifEsterniModificabili = true;
			if (!formsAdapter.checkBooleanFunzionalitaDisponibile("abilitaModificaDatiDiProtocollo", false) 
					&& doc.getNum_prot() != null && doc.getNum_prot().length() > 0 
					&& !doc.isBozza())
				rifEsterniModificabili = false;
			
			formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams("", true, rifEsterniModificabili, isRepertorio));
			if (!rifEsterniModificabili && formsAdapter.checkBooleanFunzionalitaDisponibile("DataNumProtMittenteModificabile", false)) {
				if (doc.getRif_esterni() != null && doc.getRif_esterni().get(0) != null) {
					if (doc.getRif_esterni().get(0).getData_prot() != null)
						formsAdapter.getDefaultForm().addParam(".rif_esterni.rif.@data_prot", doc.getRif_esterni().get(0).getData_prot());
					if (doc.getRif_esterni().get(0).getN_prot() != null)
						formsAdapter.getDefaultForm().addParam(".rif_esterni.rif.@n_prot", doc.getRif_esterni().get(0).getN_prot());
				}
			}
			if (!formsAdapter.checkBooleanFunzionalitaDisponibile("show_customSelect1", false)) {
				// se i campi custom di tipo select non sono attivati, rimuovo dal defaultForm i 
				// parametri per essere sicuro di non inviarli
				formsAdapter.getDefaultForm().removeParam(".extra.customSelect1");
				formsAdapter.getDefaultForm().removeParam(".extra.customSelect2");
			}
			
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("arrivo", response);		
			return "showdoc@arrivo@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public Arrivo getDoc() {
		return doc;
	}

	public void setDoc(Arrivo arrivo) {
		this.doc = arrivo;
	}
	
	public String getNomeMittenteInitValue() {
		return nomeMittenteInitValue;
	}

	public void setNomeMittenteInitValue(String nomeMittenteInitValue) {
		this.nomeMittenteInitValue = nomeMittenteInitValue;
	}

	public String getNumProtMittenteInitValue() {
		return numProtMittenteInitValue;
	}

	public void setNumProtMittenteInitValue(String numProtMittenteInitValue) {
		this.numProtMittenteInitValue = numProtMittenteInitValue;
	}

	public String getDataProtMittenteInitValue() {
		return dataProtMittenteInitValue;
	}

	public void setDataProtMittenteInitValue(String dataProtMittenteInitValue) {
		this.dataProtMittenteInitValue = dataProtMittenteInitValue;
	}
		
	/**
	 * Lookup su numero di raccomandata
	 * @throws Exception
	 */
	public String lookupNumRaccomandata() throws Exception {
		String value = (getDoc().getVoce_indice() != null && getDoc().getVoce_indice().getText() != null) ? getDoc().getVoce_indice().getText() : "";

		String aliasName 	= "docnumprot";
		String aliasName1 	= "";
		String titolo 		= "xml,/doc/extra/raccomandata/@numero"; //titolo 
		String ord 			= "xml(xpart:/doc/extra/raccomandata/@numero)"; //ord 
		String campi 		= ".numero_raccomandata[0].text=xml,/doc/extra/raccomandata/@numero"; //campi
		String db 			= ""; //db 
		String newRecord 	= ""; //newRecord 
		String xq			= "([doc_tipo]=arrivo) AND [doc_repertoriocod]=RCMD"; //extraQuery
		
		callLookup(doc, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su numero di raccomandata
	 * @throws Exception
	 */
	public String clearLookupNumRaccomandata() throws Exception {
		String campi = ".numero_raccomandata[0].text=xml,/doc/extra/raccomandata/@numero"; 
		
		return clearField(campi, doc);
	}
	
	/**
	 * Lookup su mittente del documento
	 * @throws Exception
	 */
	public String lookupMittente() throws Exception {
		return this.lookupRifStrutEst(0);
	}
	
	/**
	 * Pulizia dei campi di lookup su mittente del documento
	 * @throws Exception
	 */
	public String clearLookupMittente() throws Exception {
		return this.clearLookupRifStrutEst(0);
	}
	
	/**
	 * Lookup su firmatario del documento
	 * @throws Exception
	 */
	public String lookupFirmatario() throws Exception {
		return this.lookupRifPersEst(0);
	}
	
	/**
	 * Pulizia dei campi di lookup su firmatario del documento
	 * @throws Exception
	 */
	public String clearLookupFirmatario() throws Exception {
		return this.clearLookupRifPersEst(0);
	}
	
	/**
	 * Lookup su fax del mittente
	 * @throws Exception
	 */
	public String lookupFax() throws Exception {
		String value = (getDoc().getRif_esterni() != null && getDoc().getRif_esterni().size() > 0 && getDoc().getRif_esterni().get(0).getFax() != null) ? getDoc().getRif_esterni().get(0).getFax() : "";
		// Settato in questo punto perche' anche getDoc().getRif_esterni().get(0).getFax() potrebbe assumere
		// valore vuoto ('')
		if (value.equals(""))
			value = "*";

		String aliasName 	= "struest_telnum";
		String aliasName1 	= "struest_telnum";
		String titolo 		= "\"^lkp_fax:\" /struttura_esterna/telefono/@num \"^|\" /struttura_esterna/telefono/@tipo \"^ - \" xml,/struttura_esterna/nome \"^ (~\" xml,/struttura_esterna/indirizzo/@comune \"~^)\" \"^ [csap: ~\" xml,/struttura_esterna/@cod_SAP \"~^]\" \"^ - P.IVA ~\" xml,/struttura_esterna/@partita_iva \"~^|\" xml,/persona_esterna/@cognome xml,/persona_esterna/@nome"; //titolo 
		String ord 			= "xml(xpart:/struttura_esterna/telefono/@num),xml(xpart:/struttura_esterna/nome)"; //ord
		String campi 		= ".rif_esterni.rif[0].nome=xml,/struttura_esterna/nome xml,/persona_esterna/@cognome xml,/persona_esterna/@nome" +
								" ; .rif_esterni.rif[0].indirizzo=xml,/struttura_esterna/indirizzo \"~-\" xml,/struttura_esterna/indirizzo/@cap xml,/struttura_esterna/indirizzo/@comune \"^ (~\" xml,/struttura_esterna/indirizzo/@prov \"~^)\" \" -~\" xml,/struttura_esterna/indirizzo/@nazione xml,/persona_esterna/recapito/indirizzo \"-~\" xml,/persona_esterna/recapito/indirizzo/@cap xml,/persona_esterna/recapito/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito/indirizzo/@nazione" +
								" ; .rif_esterni.rif[0].@cod=xml,/struttura_esterna/@cod_uff xml,/persona_esterna/@matricola" +
								" ; .rif_esterni.rif[0].@email_certificata=xml,/struttura_esterna/email_certificata/@addr" +
								" ; .rif_esterni.rif[0].@email=xml,/struttura_esterna/email/@addr xml,/persona_esterna/recapito/email/@addr" +
								" ; .rif_esterni.rif[0].@tel=xml,/" +
								" ; .rif_esterni.rif[0].@fax=xml,/" +
								" ; .rif_esterni.rif[0].referente.@cod=xml,/" +
								" ; .rif_esterni.rif[0].referente.@nominativo=xml,/" +
								" ; telefono=xml,/struttura_esterna/telefono/@num \"~^!\" xml,/struttura_esterna/telefono/@tipo xml,/persona_esterna/recapito/telefono/@num \"^!\" xml,/persona_esterna/recapito/telefono/@tipo" +
								" ; lookup_xq_.rif_esterni.rif.referente.@nominativo=xml,/" +
								" ; .rif_esterni.rif[0].indirizzo_personale=xml,/persona_esterna/recapito_personale/indirizzo \"-~\" xml,/persona_esterna/recapito_personale/indirizzo/@cap xml,/persona_esterna/recapito_personale/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito_personale/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito_personale/indirizzo/@nazione" +
								" ; telefono_personale=xml,/persona_esterna/recapito_personale/telefono/@num \"^!\" xml,/persona_esterna/recapito_personale/telefono/@tipo" +
								" ; .rif_esterni.rif[0].referente.@ruolo=xml,/" +
								" ; sx=xml,/" +
								" ; .rif_esterni.rif[0].@codice_fiscale=xml,//@codice_fiscale" +
								" ; .rif_esterni.rif[0].@cod_SAP=xml,/struttura_esterna/@cod_SAP" +
								" ; .rif_esterni.rif[0].@partita_iva=xml,//@partita_iva";
		String db 			= getFormsAdapter().getDefaultForm().getParam("aclDb"); //db 
		String newRecord 	= "/base/acl/engine/acl.jsp?db=" + getFormsAdapter().getDefaultForm().getParam("aclDb") + ";dbTable=struttura_esterna;fillField=struttura_esterna.nome;rightCode=ACL-6"; //newRecord
		String xq			= " ADJ ([/struttura_esterna/telefono/@tipo/]=\"fax\")"; //extraQuery
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("acl_ext_aoo_restriction", false))
			xq				= xq + " AND ([struest_codammaoo]=" + getDoc().getCod_amm_aoo() + ")";
		
		callLookup(doc, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su fax del mittente
	 * @throws Exception
	 */
	public String clearLookupFax() throws Exception {
		String campi = ".rif_esterni[0].nome=xml,/struttura_esterna/nome xml,/persona_esterna/@cognome xml,/persona_esterna/@nome" +
				" ; .rif_esterni[0].indirizzo=xml,/struttura_esterna/indirizzo \"~-\" xml,/struttura_esterna/indirizzo/@cap xml,/struttura_esterna/indirizzo/@comune \"^ (~\" xml,/struttura_esterna/indirizzo/@prov \"~^)\" \" -~\" xml,/struttura_esterna/indirizzo/@nazione xml,/persona_esterna/recapito/indirizzo \"-~\" xml,/persona_esterna/recapito/indirizzo/@cap xml,/persona_esterna/recapito/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito/indirizzo/@nazione" +
				" ; .rif_esterni[0].@cod=xml,/struttura_esterna/@cod_uff xml,/persona_esterna/@matricola" +
				" ; .rif_esterni[0].@email_certificata=xml,/struttura_esterna/email_certificata/@addr" +
				" ; .rif_esterni[0].@email=xml,/struttura_esterna/email/@addr xml,/persona_esterna/recapito/email/@addr" +
				" ; .rif_esterni[0].@tel=xml,/" +
				" ; .rif_esterni[0].@fax=xml,/" +
				" ; .rif_esterni[0].referente.@cod=xml,/" +
				" ; .rif_esterni[0].referente.@nominativo=xml,/" +
				" ; .rif_esterni[0].indirizzo_personale=xml,/persona_esterna/recapito_personale/indirizzo \"-~\" xml,/persona_esterna/recapito_personale/indirizzo/@cap xml,/persona_esterna/recapito_personale/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito_personale/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito_personale/indirizzo/@nazione" +
				" ; .rif_esterni[0].@codice_fiscale=xml,//@codice_fiscale" +
				" ; .rif_esterni[0].@cod_SAP=xml,/struttura_esterna/@cod_SAP" +
				" ; .rif_esterni[0].@partita_iva=xml,//@partita_iva";
		
		return clearField(campi, doc);
	}

	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;
		
		result = super.checkRequiredFieldCommon(true); // controlli comuni a tutte le tipologie di documenti
		
		// Controllo se il mittente del documento e' valorizzato
		if (getDoc().getRif_esterni().get(0).getNome() == null || getDoc().getRif_esterni().get(0).getNome().length() == 0) {
			this.setErrorMessage("templateForm:nomeMittente_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.mittente") + "'");
			result = true;
		}
		
		// Controllo l'eventuale data di protocollo del mittente
		if (getDoc().getRif_esterni().get(0).getData_prot() != null && getDoc().getRif_esterni().get(0).getData_prot().length() > 0) {
			// Controllo che il formato sia corretto
			if (!DateUtil.isValidDate(getDoc().getRif_esterni().get(0).getData_prot(), formatoData)) {
				this.setErrorMessage("templateForm:dataProtMittente", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_doc") + "': " + formatoData.toLowerCase());
				result = true;
			}
			else { 
				// Controllo che la data specificata sia antecedente alla data corerente
				int dataCur = new Integer(DateUtil.getCurrentDateNorm()).intValue();
				int dataProtMittente = new Integer(DateUtil.formatDate2XW(getDoc().getRif_esterni().get(0).getData_prot(), formatoData)).intValue();
				if (dataCur < dataProtMittente) {
					this.setErrorMessage("templateForm:dataProtMittente", I18N.mrs("dw4.la_data_di_arrivo_deve_essere_antecedente_alla_data_di_registrazione"));
					result = true;
				}
			}
		}
		
		// Imposto lo scarto automatico se non impostato
		if (getDoc().getScarto() == null || getDoc().getScarto().length() == 0)
			getDoc().setScarto(AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ScartoAutomatico")));
		
		// Controllo su mezzo di trasmissione
		if (checkMezzoTrasmissione())
			result = true;
		
		return result;
	}
	
	/**
	 * Verifica se esistono duplicati del documento che si sta 
	 * registrando
	 * 
	 * @throws Exception
	 */
	public String verificaDuplicatiDoc() throws Exception {
		String theCod = "";
		
		if (getDoc().getRif_esterni().get(0).getCod() != null && getDoc().getRif_esterni().get(0).getCod().length() > 0) {
			theCod = "[doc_rifesternirifnomecod]=" + getDoc().getRif_esterni().get(0).getCod() + " OR ";
		}
		
		// TODO Da completare multisocieta'
		/*
		var _CODSEDE_ = "_CODSEDE_";
		var hxpS = new _hxpSession(iForm);
		var ms = hxpS.getParameter("ssInMS");
		if (ms != null && ms.length > 0) {
			_CODSEDE_ = ms;
		}
	    */
		
		String query = "[doc_tipo]=arrivo AND [doc_codammaoo]=\"" +
						getDoc().getCod_amm_aoo() +
						"\" AND ( " +
						theCod +
						"[doc_rifesternirifnome]=\" " + 
						getDoc().getRif_esterni().get(0).getNome().replace("\"", "\\\"") +
						"\") AND " +
						"NOT([/doc/@nrecord/]=\"" + getDoc().getNrecord() +"\")";
		
		if (getDoc().getRif_esterni().get(0).getData_prot() != null && getDoc().getRif_esterni().get(0).getData_prot().length() > 0) {
			query += "AND ([docrifesternirifdataprot]=" +
					getDoc().getRif_esterni().get(0).getData_prot() +
						") ";
		}
		
		// Federico 17/02/09: nel protocollo mittente possono essere presenti anche spazi --> affinche'
		// la ricerca non fallisca, tale valore deve essere messo tra "". Inoltre, eventuali " presenti
		// nel prot. mittente devono essere eliminate (dato che negli indici non figurano) [M 0000096]
		if (getDoc().getRif_esterni().get(0).getN_prot() != null && getDoc().getRif_esterni().get(0).getN_prot().length() > 0) {
			query += "AND ([doc_rifestrenirifnprot]=\"" +
					getDoc().getRif_esterni().get(0).getN_prot().replace("\"", "") +
						"\") ";
		}
		
		if (tipoVerificaDuplicati.equals("popup")) {
			// verifica dei duplicati con apertura del popup
			return verificaDuplicati(query);
		}
		else {
			// verifica dei duplicati con semplice verifica dell'esistenza
			existsDuplicati(query);
			
			if (!duplicatiPresenti && duplicatoVerificato) {
				// aggiornamento dei valori di init
				dataProtMittenteInitValue = getDoc().getRif_esterni().get(0).getData_prot();
				numProtMittenteInitValue = getDoc().getRif_esterni().get(0).getN_prot();
				nomeMittenteInitValue = getDoc().getRif_esterni().get(0).getNome();
			}			
			
			return null;
		}
	}
	
	@Override
	public XmlEntity getModel() {
		return this.doc;
	}
	
}
