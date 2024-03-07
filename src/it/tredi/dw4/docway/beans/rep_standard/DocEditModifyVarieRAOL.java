package it.tredi.dw4.docway.beans.rep_standard;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyVarie;
import it.tredi.dw4.docway.model.RifEsterno;
import it.tredi.dw4.docway.model.rep_standard.Richiedente;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditModifyVarieRAOL extends DocEditModifyVarie {
	
	// campi specifici del repertorio di richiesta pubblicazione albo online
	private Richiedente richiedente = new Richiedente();
	
	public DocEditModifyVarieRAOL() throws Exception {
		super();
	}
	
	public boolean isDocEditModify() {
		return true;
	}

	@Override
	public void init(Document domDocumento) {
		super.init(domDocumento);
		
		richiedente.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/richiedente")); // richiedente interno
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredFieldRAOL()) return null;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", false, true));
			formsAdapter.getDefaultForm().addParams(richiedente.asFormAdapterParams(".extra.richiedente"));
			
			// per i doc di tipo vario non vengono aggiunti i rif esterni (essendo una personalizzazione di questo
			// repertorio li aggiungo a questo livello)
			if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("abilitaRichiedenteEsterno", false) || getFormsAdapter().checkBooleanFunzionalitaDisponibile("abilitaRichiedenteInternoEsterno", false)) {
    			RifEsterno rif = (RifEsterno) getDoc().getRif_esterni().get(0);
    			formsAdapter.getDefaultForm().addParams(rif.asFormAdapterParams(".rif_esterni.rif"));
    		}
			
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocVarieRAOL showdocVarieRAOL = new ShowdocVarieRAOL();
			showdocVarieRAOL.getFormsAdapter().fillFormsFromResponse(response);
			showdocVarieRAOL.init(response.getDocument());
			showdocVarieRAOL.loadAspects("@varie", response, "showdoc");
			setSessionAttribute("showdocVarieRAOL", showdocVarieRAOL);
			
			return "rep_standard_showdoc@varie@RAOL@reload";
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
			
			if (!isDocEditModify()) { 
				return clearDocumentoIfIsNotDocEditModify(response);
			}
			else {
				// caso di modifica di un doc
				ShowdocVarieRAOL showdocVarieRAOL = new ShowdocVarieRAOL();
				showdocVarieRAOL.getFormsAdapter().fillFormsFromResponse(response);
				showdocVarieRAOL.init(response.getDocument());
				showdocVarieRAOL.loadAspects("@varie", response, "showdoc");
				setSessionAttribute("showdocVarieRAOL", showdocVarieRAOL);
				
				return "rep_standard_showdoc@varie@RAOL@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public Richiedente getRichiedente() {
		return richiedente;
	}

	public void setRichiedente(Richiedente richiedente) {
		this.richiedente = richiedente;
	}
	
	/**
	 * lookup su richiedente interno
	 * @return
	 * @throws Exception
	 */
	public String lookupRichiedenteInterno() throws Exception {
		try {
			String value = (getRichiedente().getNome() != null) ? getRichiedente().getNome() : "";
			
			String aliasName 	= "struint_nome,gruppi_nome,ruoli_nome,persint_nomcogn";
			String aliasName1 	= "";
			String titolo 		= "xml,/*/nome \"^\" xml,/persona_interna/@cognome xml,/persona_interna/@nome"; 
			String ord 			= "xml(xpart:/struttura_interna/nome),xml(xpart:/gruppo/nome)(join),xml(xpart:/ruolo/nome)(join),xml(xpart:/persona_interna/@cognome)(join),xml(xpart:/persona_interna/@nome)"; 
			String campi 		= ".nome=xml,/*/nome xml,/persona_interna/@cognome xml,/persona_interna/@nome ; .@cod=xml,/struttura_interna/@cod_uff xml,/gruppo/@id xml,/ruolo/@id xml,/persona_interna/@matricola ; .@tipo=UD,/xw/@UdType";
			String db 			= getFormsAdapter().getDefaultForm().getParam("aclDb"); 
			String newRecord 	= ""; 
			String xq			= "NOT([PERSINT_TIPO]=\"profilo\")";
			
			callLookup(getRichiedente(), aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * clear su lookup su richiedente interno
	 * @return
	 * @throws Exception
	 */
	public String clearLookupRichiedenteInterno() throws Exception {
		try {
			String campi = ".nome=xml,/*/nome xml,/persona_interna/@cognome xml,/persona_interna/@nome ; .@cod=xml,/struttura_interna/@cod_uff xml,/gruppo/@id xml,/ruolo/@id xml,/persona_interna/@matricola ; .@tipo=UD,/xw/@UdType"; 
			return clearField(campi, getRichiedente());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Lookup su richiedente esterno
	 * @throws Exception
	 */
	public String lookupRichiedenteEsterno() throws Exception {
		return this.lookupRifStrutEst(0);
	}
	
	/**
	 * Pulizia dei campi di lookup su richiedente esterno
	 * @throws Exception
	 */
	public String clearLookupRichiedenteEsterno() throws Exception {
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
	 * Lookup su fax del richiedente esterno
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
		
		callLookup(getDoc(), aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su fax del richiedente esterno
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
		
		return clearField(campi, getDoc());
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredFieldRAOL() {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;
		
		result = super.checkRequiredField();
		
		// controllo su campo tipo documento
		if (getDoc().getExtra() == null || getDoc().getExtra().getTipo_repertorio() == null || getDoc().getExtra().getTipo_repertorio().isEmpty()) {
			this.setErrorMessage("templateForm:repTipoDocumento", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.tipodocumento") + "'");
			result = true;
		}
		
		// controllo su data dipubblicazione dal
		boolean dataDalValida = true;
		if (getDoc().getPubblicazione().getDal() == null || getDoc().getPubblicazione().getDal().length() == 0) {
			this.setErrorMessage("templateForm:dataPubblicazioneDal", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.pubblicazione_dal") + "'");
			result = true;
			dataDalValida = false;
		}
		else {
			// controllo se il valore associato alla data e' corretto
			if (!DateUtil.isValidDate(getDoc().getPubblicazione().getDal(), formatoData)) {
				this.setErrorMessage("templateForm:dataPubblicazioneDal", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.pubblicazione_dal") + "': " + formatoData.toLowerCase());
				result = true;
				dataDalValida = false;
			}
		}
		
		// controllo su data dipubblicazione al
		boolean dataAlValida = true;
		if (getDoc().getPubblicazione().getAl() == null || getDoc().getPubblicazione().getAl().length() == 0) {
			this.setErrorMessage("templateForm:dataPubblicazioneAl", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.pubblicazione_al") + "'");
			result = true;
			dataAlValida = false;
		}
		else {
			// controllo se il valore associato alla data e' corretto
			if (!DateUtil.isValidDate(getDoc().getPubblicazione().getAl(), formatoData)) {
				this.setErrorMessage("templateForm:dataPubblicazioneAl", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.pubblicazione_al") + "': " + formatoData.toLowerCase());
				result = true;
				dataAlValida = false;
			}
		}
		
		// controllo la validita' dell'intervallo di date di pubblicazione
		if (dataDalValida && dataAlValida) {
			int pubblicazioneDal = Integer.parseInt(DateUtil.formatDate2XW(getDoc().getPubblicazione().getDal(), formatoData));
			int pubblicazioneAl = Integer.parseInt(DateUtil.formatDate2XW(getDoc().getPubblicazione().getAl(), formatoData));
			
			// 'data pubblicazione al' successiva al valore specificato su 'data pubblicazione dal'
			if (pubblicazioneAl < pubblicazioneDal) {
				this.setErrorMessage("templateForm:dataPubblicazioneAl", I18N.mrs("dw4.la_data_di_fine_pubblicazione_non_puo_essere_antecedente_a_quella_di_inzio_pubblicazione"));
			}
		}
		
		// controllo su richiedente
		String[] richiedenteFields = { "templateForm:richiedenteInterno_input", "templateForm:richiedenteEsterno_input" };
		if ((getRichiedente().getCod() != null && getRichiedente().getCod().length() > 0) 
				&& (getDoc().getRif_esterni().get(0).getCod() != null && getDoc().getRif_esterni().get(0).getCod().length() > 0)) {
			this.setErrorMessage(richiedenteFields, I18N.mrs("dw4.occorre_valorizzare_solo_un_richiedente_Interno_o_Esterno"));
			result = true;
		}
		else if ((getRichiedente().getCod() == null || getRichiedente().getCod().length() == 0) 
				&& (getDoc().getRif_esterni().get(0).getCod() == null || getDoc().getRif_esterni().get(0).getCod().length() == 0)) {
			if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("abilitaRichiedenteInternoEsterno", false))
				this.setErrorMessage(richiedenteFields, I18N.mrs("dw4.occorre_valorizzare_un_richiedente_interno_o_esterno"));
			else 
				this.setErrorMessage(richiedenteFields, I18N.mrs("dw4.occorre_valorizzare_il_richiedente"));
			result = true;
		}
				
		return result;
	}
	
}
