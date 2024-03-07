package it.tredi.dw4.docway.beans.rep_standard;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyArrivo;
import it.tredi.dw4.docway.model.rep_standard.Richiedente;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Pagina di modifica del repertorio ALBO
 */
public class DocEditModifyArrivoALBO extends DocEditModifyArrivo {

	// campi specifici del repertorio di richiesta pubblicazione albo online
	private Richiedente richiedente = new Richiedente();
	
	public DocEditModifyArrivoALBO() throws Exception {
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
			if (checkRequiredField()) return null;
			
			boolean rifEsterniModificabili = true;
			if (!formsAdapter.checkBooleanFunzionalitaDisponibile("abilitaModificaDatiDiProtocollo", false) 
					&& getDoc().getNum_prot() != null && getDoc().getNum_prot().length() > 0 
					&& !getDoc().isBozza())
				rifEsterniModificabili = false;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", true, rifEsterniModificabili, true));
			formsAdapter.getDefaultForm().addParams(richiedente.asFormAdapterParams(".extra.richiedente"));
			if (!rifEsterniModificabili && formsAdapter.checkBooleanFunzionalitaDisponibile("DataNumProtMittenteModificabile", false)) {
				if (getDoc().getRif_esterni() != null && getDoc().getRif_esterni().get(0) != null) {
					if (getDoc().getRif_esterni().get(0).getData_prot() != null)
						formsAdapter.getDefaultForm().addParam(".rif_esterni.rif.@data_prot", getDoc().getRif_esterni().get(0).getData_prot());
					if (getDoc().getRif_esterni().get(0).getN_prot() != null)
						formsAdapter.getDefaultForm().addParam(".rif_esterni.rif.@n_prot", getDoc().getRif_esterni().get(0).getN_prot());
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
			
			ShowdocArrivoALBO showdocArrivoALBO = new ShowdocArrivoALBO();
			showdocArrivoALBO.getFormsAdapter().fillFormsFromResponse(response);
			showdocArrivoALBO.init(response.getDocument());
			showdocArrivoALBO.loadAspects("@arrivo", response, "showdoc");
			setSessionAttribute("showdocArrivoALBO", showdocArrivoALBO);
			
			return "rep_standard_showdoc@arrivo@ALBO@reload";
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
				ShowdocArrivoALBO showdocArrivoALBO = new ShowdocArrivoALBO();
				showdocArrivoALBO.getFormsAdapter().fillFormsFromResponse(response);
				showdocArrivoALBO.init(response.getDocument());
				showdocArrivoALBO.loadAspects("@arrivo", response, "showdoc");
				setSessionAttribute("showdocArrivoALBO", showdocArrivoALBO);
				
				return "rep_standard_showdoc@arrivo@ALBO@reload";
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
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	@Override
	public boolean checkRequiredField() {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;
		
		result = super.checkRequiredFieldCommon(false); // controlli comuni a tutte le tipologie di documenti
		
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
				result = true;
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
