package it.tredi.dw4.acl.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.AssegnatarioMailBox;
import it.tredi.dw4.acl.model.CasellaPostaElettronica;
import it.tredi.dw4.acl.model.GestoreMailbox;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditCasellaPostaElettronica extends AclDocEdit {

	private String xml;

	private AclDocEditFormsAdapter formsAdapter;
	private CasellaPostaElettronica casellaPostaElettronica;

	// mbernardini 20/11/2014 : definizione della protocollazione automatica per PEC contenenti fatturePA
	private boolean abilitaFatturePA = false;
	// mbernardini 19/01/2015 : determina se e' attiva o meno la gestione dello split degli allegati per le mail di archiviazione/interoperabilita'
	private boolean abilitaSplitAttachmentsSuMailStorage = false;
	// mbernardini 25/02/2019 : smistamento fatturaPA su mailbox corretta
	private boolean smistamentoFatturaPaSuMailboxCorretta = false;
	// mbernardini 27/05/2019 : importazione messaggi da singola casella di posta
	private boolean abilitaCasellaImportSuMailStorage = false;
	// mbernardini 19/06/2019 : archiviazione documenti tramite TAGS presenti sull'oggetto del messaggio elaborato da MSA
	private boolean abilitaArchiviazioneTramiteTags = false;
	// mbernardini 24/09/2019 : smistamento (in assegnazione) dei messaggi di MSA tramite email mittente
	private boolean abilitaSmistamentoByMittente = false;
	
	private List<Option> tagSelect = new ArrayList<Option>();

	private boolean lookupResponsabileSuPersona = true; // identifica se il lookup su responsabile e' su persona/ufficio o ruolo

	public DocEditCasellaPostaElettronica() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}

	@Override
	public void init(Document dom) {
		initData(dom);

		// Se non venirre riassegnato al punto '.' l'nrecord in questo punto verrebbe restituito errore in caso di 'ripeti nuovo'
    	this.casellaPostaElettronica.setNrecord(".");

    	this.casellaPostaElettronica.setInterop("si"); // di default creazione di caselle di interoperabilita'

    	if (this.casellaPostaElettronica.isInteroperabilita()) {
    		// casella di interoperabilita', assegno i cod_amm e cod_aoo di segnatura di default

    		if (this.casellaPostaElettronica.getCod_amm_segnatura() == null || this.casellaPostaElettronica.getCod_amm_segnatura().isEmpty())
    			this.casellaPostaElettronica.setCod_amm_segnatura(this.casellaPostaElettronica.getCod_amm());
    		if (this.casellaPostaElettronica.getCod_aoo_segnatura() == null || this.casellaPostaElettronica.getCod_aoo_segnatura().isEmpty())
    			this.casellaPostaElettronica.setCod_aoo_segnatura(this.casellaPostaElettronica.getCod_aoo());
    	}
	}

	@SuppressWarnings("unchecked")
	protected void initData(Document dom) {
		xml = dom.asXML();
    	this.casellaPostaElettronica = new CasellaPostaElettronica();
    	this.casellaPostaElettronica.init(dom);

    	this.abilitaFatturePA = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@abilitaFatturePA"));
    	this.smistamentoFatturaPaSuMailboxCorretta = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@smistamentoFatturaPaSuMailboxCorretta"));
    	this.abilitaSplitAttachmentsSuMailStorage = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@abilitaSplitAttachmentsSuMailStorage"));
    	this.abilitaCasellaImportSuMailStorage = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@abilitaCasellaImportSuMailStorage"));
    	this.abilitaArchiviazioneTramiteTags = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@abilitaArchiviazioneTramiteTags"));
    	this.abilitaSmistamentoByMittente = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@abilitaSmistamentoByMittente"));

    	tagSelect = XMLUtil.parseSetOfElement(dom, "/response/select_tag/option", new Option());
    	Option empty = new Option();
    	empty.setValue("");
    	empty.setLabel("");
    	tagSelect.add(empty);

    	// identifica se il responsabile della mailbox e' rappresentato da una persona o da un ruolo
    	if (this.casellaPostaElettronica.getResponsabile() != null
    			&& this.casellaPostaElettronica.getResponsabile().getCod_ruolo() != null && !this.casellaPostaElettronica.getResponsabile().getCod_ruolo().isEmpty())
    		this.lookupResponsabileSuPersona = false;
    	else
    		this.lookupResponsabileSuPersona = true;
	}

	@Override
	public DocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.casellaPostaElettronica.asFormAdapterParams(""));

			XMLDocumento response = super._saveDocument("casellaPostaElettronica", "list_of_struttur");

			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			buildSpecificShowdocPageAndReturnNavigationRule("casellaPostaElettronica", response);
			return "showdoc@casellaPostaElettronica@reload";
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
	 * Passaggio della casella da Interoperabilita' ad Archiviazione e viceversa
	 */
	public void interopChangeListener(ValueChangeEvent e) {
		if (e.getNewValue() != null)
			this.casellaPostaElettronica.setInterop((String) e.getNewValue());
	}

	/**
	 * Lookup su document model
	 * @return
	 * @throws Exception
	 */
	public String lookupMailboxDocumentModel() throws Exception {
		String value = (casellaPostaElettronica.getDocumentModel() != null) ? casellaPostaElettronica.getDocumentModel() : "";

		String aliasName = "docmodelname";
		String aliasName1 = "";
		String titolo = "xml,/documentModel/@name"; //titolo
		String ord = "xml(xpart:/documentModel/@name)"; //ord
		String campi = "@documentModel=xml,/documentModel/@name"; //campi
		String xq = "NOT([/documentModel/@type/]=\"webmail\")"; //xq
		String db = ""; //db
		String newRecord = ""; //newRecord
		callLookup(casellaPostaElettronica, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}

	/**
	 * Clear lookup su document model
	 * @return
	 * @throws Exception
	 */
	public String clearMailboxDocumentModel() throws Exception{
		String campi = "@documentModel=xml,/documentModel/@name"; //campi
		return clearField(campi, casellaPostaElettronica);
	}

	/**
	 * Lookup su persone interne per selezione dei gestori delle casellaPostaElettronica di archiviazione
	 */
	public String lookupGestore() throws Exception {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		int pos = casellaPostaElettronica.getGestoriMailbox().indexOf(gestore);
		String value = casellaPostaElettronica.getGestoriMailbox().get(pos).getNome_pers();

		String xq = "[persint_codammaoo]=" + casellaPostaElettronica.getCod_amm() + casellaPostaElettronica.getCod_aoo();
		xq = "(" + xq + ") AND [/persona_interna/@tipo]=\"&null;\"";

		String aliasName = "persint_nomcogn";
		String aliasName1 = "persint_cognome";
		String titolo = "xml,/persona_interna/@cognome xml,/persona_interna/@nome \"- ~\" XML,/persona_interna/recapito/email/@addr \"- ~\" XML,/persona_interna/@soprannome";
		String ord = "xml(xpart:/persona_interna/@cognome)";
		String campi = "gestoriMailbox["+pos+"].nome_pers=xml,/persona_interna/@cognome xml,/persona_interna/@nome"
								+ " ; gestoriMailbox["+pos+"].@matricola=xml,/persona_interna/@matricola";
		String db = "";
		String newRecord = "";

		callLookup(casellaPostaElettronica, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}

	/**
	 * Clear lookup su persone interne per selezione dei gestori delle casellaPostaElettronica di archiviazione
	 */
	public String clearLookupGestore() throws Exception {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		int pos = casellaPostaElettronica.getGestoriMailbox().indexOf(gestore);
		String campi = "gestoriMailbox["+pos+"].nome_pers=xml,/persona_interna/@cognome xml,/persona_interna/@nome"
						+ " ; gestoriMailbox["+pos+"].@matricola=xml,/persona_interna/@matricola";
		return clearField(campi, casellaPostaElettronica);
	}

	/**
	 * Gestione reponsabile, passaggio da lookup su uffico/persona a ruolo e viceversa
	 * @return
	 * @throws Exception
	 */
	public String changeResponsabilePersonaRuolo() throws Exception {
		if (lookupResponsabileSuPersona) {
			// passaggio del lookup su responsabile da persona a ruolo
			lookupResponsabileSuPersona = false;
			resetResponsabilePersona();
		}
		else {
			// passaggio del lookup su responsabile da ruolo a persona
			lookupResponsabileSuPersona = true;
			resetResponsabileRuolo();
		}
		return null;
	}

	/**
	 * Lookup su ufficio responsabile
	 */
	public String lookupUfficioResponsabile() throws Exception {
		String value = (casellaPostaElettronica.getResponsabile() != null && casellaPostaElettronica.getResponsabile().getNome_uff() != null) ? casellaPostaElettronica.getResponsabile().getNome_uff() : "";
		String value2 = (casellaPostaElettronica.getResponsabile() != null && casellaPostaElettronica.getResponsabile().getNome_pers() != null) ? casellaPostaElettronica.getResponsabile().getNome_pers() : "";

		String campi = "responsabile.@nome_uff" +
								"|responsabile.@nome_pers" +
								"|responsabile.@cod_uff" +
								"|responsabile.@matricola";

		String aliasName = "struint_nome,gruppi_nome";
		String aliasName1 = "";
		String titolo = "xml,/struttura_interna/nome \"^\" xml,/gruppo/nome"; //titolo
		String ord = "xml(xpart:/struttura_interna/nome),xml(xpart:/gruppo/nome)(join)"; //ord
		String db = ""; //db
		String newRecord = ""; //newRecord
		String xq = "[struint_codammaoo]=" + casellaPostaElettronica.getCod_amm() + casellaPostaElettronica.getCod_aoo(); //extraQuery

		// disattivato per il lookup il filtro su AOO perche' gia' impostato come extraQuery del lookup
		// il formsAdapter viene poi ripulito all'interno del metodo callRifintLookup()
		formsAdapter.getIndexForm().addParam("_cd", FormsAdapter.setParameterFromCustomTupleValue("skipAOORestriction", "true", formsAdapter.getIndexForm().getParam("_cd")));

		callRifintLookup(casellaPostaElettronica, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value + "|" + value2);

		// azzeramento di eventuali valori associati a ruolo
		resetResponsabileRuolo();

		return null;
	}

	/**
	 * Lookup su ufficio assegnatario
	 */
	public String lookupUfficioAssegnatario() throws Exception {
		AssegnatarioMailBox assegnatario = (AssegnatarioMailBox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("assegnatario");
		int pos = casellaPostaElettronica.getAssegnazioneCC().indexOf(assegnatario);

		String value = (assegnatario != null && assegnatario.getNome_uff() != null) ? assegnatario.getNome_uff() : "";
		String value2 = (assegnatario != null && assegnatario.getNome_pers() != null) ? assegnatario.getNome_pers() : "";

		String campi = "assegnazioneCC["+pos+"].@nome_uff" +
								"|assegnazioneCC["+pos+"].@nome_pers" +
								"|assegnazioneCC["+pos+"].@cod_uff" +
								"|assegnazioneCC["+pos+"].@matricola";

		String aliasName = "struint_nome,gruppi_nome";
		String aliasName1 = "";
		String titolo = "xml,/struttura_interna/nome \"^\" xml,/gruppo/nome"; //titolo
		String ord = "xml(xpart:/struttura_interna/nome),xml(xpart:/gruppo/nome)(join)"; //ord
		String db = ""; //db
		String newRecord = ""; //newRecord
		String xq = "[struint_codammaoo]=" + casellaPostaElettronica.getCod_amm() + casellaPostaElettronica.getCod_aoo(); //extraQuery

		// disattivato per il lookup il filtro su AOO perche' gia' impostato come extraQuery del lookup
		// il formsAdapter viene poi ripulito all'interno del metodo callRifintLookup()
		formsAdapter.getIndexForm().addParam("_cd", FormsAdapter.setParameterFromCustomTupleValue("skipAOORestriction", "true", formsAdapter.getIndexForm().getParam("_cd")));

		callRifintLookup(casellaPostaElettronica, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value + "|" + value2);

		// azzeramento di eventuali valori associati a ruolo
		resetAssegnatarioRuolo(assegnatario);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su ufficio Responsabile
	 * @return
	 * @throws Exception
	 */
	public String clearUfficioResponsabile() throws Exception {
		String value = (casellaPostaElettronica.getResponsabile() != null && casellaPostaElettronica.getResponsabile().getNome_uff() != null) ? casellaPostaElettronica.getResponsabile().getNome_uff() : "";
		if (value.length() == 0) {
			String campi = "responsabile.@nome_uff" +
								"|responsabile.@nome_pers" +
								"|responsabile.@cod_uff" +
								"|responsabile.@matricola";

			clearFieldRifint(campi, casellaPostaElettronica);
		}

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su ufficio Assegnatario
	 * @return
	 * @throws Exception
	 */
	public String clearUfficioAssegnatario() throws Exception {
		AssegnatarioMailBox assegnatario = (AssegnatarioMailBox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("assegnatario");
		int pos = casellaPostaElettronica.getAssegnazioneCC().indexOf(assegnatario);

		String value = (assegnatario != null && assegnatario.getNome_uff() != null) ? assegnatario.getNome_uff() : "";

		if (value.length() == 0) {
			String campi = "assegnazioneCC["+pos+"].@nome_uff" +
								"|assegnazioneCC["+pos+"].@nome_pers" +
								"|assegnazioneCC["+pos+"].@cod_uff" +
								"|assegnazioneCC["+pos+"].@matricola";
			clearFieldRifint(campi, casellaPostaElettronica);
		}

		return null;
	}

	/**
	 * RifintLookup su persona Responsabile
	 * @return
	 * @throws Exception
	 */
	public String lookupPersonaResponsabile() throws Exception {
		String value = (casellaPostaElettronica.getResponsabile() != null && casellaPostaElettronica.getResponsabile().getNome_uff() != null) ? casellaPostaElettronica.getResponsabile().getNome_uff() : "";
		String value2 = (casellaPostaElettronica.getResponsabile() != null && casellaPostaElettronica.getResponsabile().getNome_pers() != null) ? casellaPostaElettronica.getResponsabile().getNome_pers() : "";

		String campi = "responsabile.@nome_uff" +
								"|responsabile.@nome_pers" +
								"|responsabile.@cod_uff" +
								"|responsabile.@matricola";

		String aliasName = "persint_nomcogn";
		String aliasName1 = "";
		String titolo = "xml,/persona_interna/@cognome xml,/persona_interna/@nome"; //titolo
		String ord = "xml(xpart:/persona_interna/@cognome)"; //ord
		String db = ""; //db
		String newRecord = ""; //newRecord

		// mbernardini 21/06/2016 : corretto bug in lookup responsabile in modifica di una casella di posta elettronica su ACL
		String xq = "[struint_codammaoo]=" + casellaPostaElettronica.getCod_amm() + casellaPostaElettronica.getCod_aoo() + " OR [persint_codammaoo]=" + casellaPostaElettronica.getCod_amm() + casellaPostaElettronica.getCod_aoo(); //extraQuery

		// disattivato per il lookup il filtro su AOO perche' gia' impostato come extraQuery del lookup
		// il formsAdapter viene poi ripulito all'interno del metodo callRifintLookup()
		formsAdapter.getIndexForm().addParam("_cd", FormsAdapter.setParameterFromCustomTupleValue("skipAOORestriction", "true", formsAdapter.getIndexForm().getParam("_cd")));

		callRifintLookup(casellaPostaElettronica, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value + "|" + value2);

		// azzeramento di eventuali valori associati a ruolo
		resetResponsabileRuolo();

		return null;
	}

	/**
	 * RifintLookup su persona Assegnatario
	 * @return
	 * @throws Exception
	 */
	public String lookupPersonaAssegnatario() throws Exception {
		AssegnatarioMailBox assegnatario = (AssegnatarioMailBox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("assegnatario");
		int pos = casellaPostaElettronica.getAssegnazioneCC().indexOf(assegnatario);

		String value = (assegnatario != null && assegnatario.getNome_uff() != null) ? assegnatario.getNome_uff() : "";
		String value2 = (assegnatario != null && assegnatario.getNome_pers() != null) ? assegnatario.getNome_pers() : "";

		String campi = "assegnazioneCC["+pos+"].@nome_uff" +
								"|assegnazioneCC["+pos+"].@nome_pers" +
								"|assegnazioneCC["+pos+"].@cod_uff" +
								"|assegnazioneCC["+pos+"].@matricola";

		String aliasName = "persint_nomcogn";
		String aliasName1 = "";
		String titolo = "xml,/persona_interna/@cognome xml,/persona_interna/@nome"; //titolo
		String ord = "xml(xpart:/persona_interna/@cognome)"; //ord
		String db = ""; //db
		String newRecord = ""; //newRecord

		String xq = "[struint_codammaoo]=" + casellaPostaElettronica.getCod_amm() + casellaPostaElettronica.getCod_aoo() + " OR [persint_codammaoo]=" + casellaPostaElettronica.getCod_amm() + casellaPostaElettronica.getCod_aoo(); //extraQuery

		// disattivato per il lookup il filtro su AOO perche' gia' impostato come extraQuery del lookup
		// il formsAdapter viene poi ripulito all'interno del metodo callRifintLookup()
		formsAdapter.getIndexForm().addParam("_cd", FormsAdapter.setParameterFromCustomTupleValue("skipAOORestriction", "true", formsAdapter.getIndexForm().getParam("_cd")));

		callRifintLookup(casellaPostaElettronica, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value + "|" + value2);

		// azzeramento di eventuali valori associati a ruolo
		resetAssegnatarioRuolo(assegnatario);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su persona Responsabile
	 * @return
	 * @throws Exception
	 */
	public String clearPersonaResponsabile() throws Exception {
		String value = (casellaPostaElettronica.getResponsabile() != null && casellaPostaElettronica.getResponsabile().getNome_pers() != null) ? casellaPostaElettronica.getResponsabile().getNome_pers() : "";
		if (value.length() == 0) {
			String campi = "responsabile.@nome_uff" +
							"|responsabile.@nome_pers" +
							"|responsabile.@cod_uff" +
							"|responsabile.@matricola";

			clearFieldRifint(campi, casellaPostaElettronica);
		}

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su persona Assegnatario
	 * @return
	 * @throws Exception
	 */
	public String clearPersonaAssegnatario() throws Exception {
		AssegnatarioMailBox assegnatario = (AssegnatarioMailBox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("assegnatario");
		int pos = casellaPostaElettronica.getAssegnazioneCC().indexOf(assegnatario);

		String value = (assegnatario != null && assegnatario.getNome_pers() != null) ? assegnatario.getNome_pers() : "";

		if (value.length() == 0) {
			String campi = "assegnazioneCC["+pos+"].@nome_uff" +
							"|assegnazioneCC["+pos+"].@nome_pers" +
							"|assegnazioneCC["+pos+"].@cod_uff" +
							"|assegnazioneCC["+pos+"].@matricola";

			clearFieldRifint(campi, casellaPostaElettronica);
		}

		return null;
	}

	/**
	 * Cancellazione di tutti i valori associati ai campi responsabile ruolo
	 */
	private void resetResponsabileRuolo() {
		if (casellaPostaElettronica.getResponsabile() != null) {
			casellaPostaElettronica.getResponsabile().setCod_ruolo("");
			casellaPostaElettronica.getResponsabile().setNome_ruolo("");
		}
	}

	/**
	 * Cancellazione di tutti i valori associati ai campi assegnatario ruolo
	 */
	private void resetAssegnatarioRuolo(AssegnatarioMailBox assegnatario) {
		if (assegnatario != null) {
			assegnatario.setCod_ruolo("");
			assegnatario.setNome_ruolo("");
		}
	}

	/**
	 * Lookup su ruolo Responsabile
	 * @return
	 * @throws Exception
	 */
	public String lookupRuoloResponsabile() throws Exception {
		String value = (casellaPostaElettronica.getResponsabile() != null && casellaPostaElettronica.getResponsabile().getNome_ruolo() != null) ? casellaPostaElettronica.getResponsabile().getNome_ruolo() : "";

		String campi = "responsabile.@nome_ruolo=/ruolo/nome" +
							" ; responsabile.@cod_ruolo=/ruolo/@id";

		String aliasName = "ruoli_nome";
		String aliasName1 = "";
		String titolo = "xml,/ruolo/nome \"^ (~\" xml,/ruolo/societa \"~^)\""; //titolo
		String ord = "xml(xpart:/ruolo/nome)"; //ord
		String db = getFormsAdapter().getDefaultForm().getParam("aclDb"); //db
		String newRecord = ""; //newRecord
		String xq = "[ruoli_codammaoo]=" + casellaPostaElettronica.getCod_amm() + casellaPostaElettronica.getCod_aoo(); //extraQuery

		callLookup(casellaPostaElettronica, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);

		// azzeramento di eventuali valori associati a ufficio/persona
		resetResponsabilePersona();

		return null;
	}

	/**
	 * Lookup su ruolo Assegnatario
	 * @return
	 * @throws Exception
	 */
	public String lookupRuoloAssegnatario() throws Exception {
		AssegnatarioMailBox assegnatario = (AssegnatarioMailBox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("assegnatario");
		int pos = casellaPostaElettronica.getAssegnazioneCC().indexOf(assegnatario);

		String value = (assegnatario != null && assegnatario.getNome_ruolo() != null) ? assegnatario.getNome_ruolo() : "";

		String campi = "assegnazioneCC["+pos+"].@nome_ruolo=/ruolo/nome" +
							" ; assegnazioneCC["+pos+"].@cod_ruolo=/ruolo/@id";

		String aliasName = "ruoli_nome";
		String aliasName1 = "";
		String titolo = "xml,/ruolo/nome \"^ (~\" xml,/ruolo/societa \"~^)\""; //titolo
		String ord = "xml(xpart:/ruolo/nome)"; //ord
		String db = getFormsAdapter().getDefaultForm().getParam("aclDb"); //db
		String newRecord = ""; //newRecord
		String xq = "[ruoli_codammaoo]=" + casellaPostaElettronica.getCod_amm() + casellaPostaElettronica.getCod_aoo(); //extraQuery

		callLookup(casellaPostaElettronica, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);

		// azzeramento di eventuali valori associati a ufficio/persona
		resetAssegnatarioPersona(assegnatario);

		return null;
	}

	/**
	 * Pulizia dei campi di lookup su ruolo Responsabile
	 * @return
	 * @throws Exception
	 */
	public String clearRuoloResponsabile() throws Exception {
		String campi = "responsabile.@nome_ruolo=/ruolo/nome" +
				" ; responsabile.@cod_ruolo=/ruolo/@id";

		return clearField(campi, casellaPostaElettronica);
	}

	/**
	 * Pulizia dei campi di lookup su ruolo assegnatario
	 * @return
	 * @throws Exception
	 */
	public String clearRuoloAssegnatario() throws Exception {
		AssegnatarioMailBox assegnatario = (AssegnatarioMailBox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("assegnatario");
		int pos = casellaPostaElettronica.getAssegnazioneCC().indexOf(assegnatario);

		String campi = "assegnazioneCC["+pos+"].@nome_ruolo=/ruolo/nome" +
							" ; assegnazioneCC["+pos+"].@cod_ruolo=/ruolo/@id";

		return clearField(campi, casellaPostaElettronica);
	}

	/**
	 * Cancellazione di tutti i valori associati ai campi responsabile ufficio/persona
	 */
	private void resetResponsabilePersona() {
		if (casellaPostaElettronica.getResponsabile() != null) {
			casellaPostaElettronica.getResponsabile().setCod_uff("");
			casellaPostaElettronica.getResponsabile().setNome_uff("");
			casellaPostaElettronica.getResponsabile().setMatricola("");
			casellaPostaElettronica.getResponsabile().setNome_pers("");
		}
	}

	/**
	 * Cancellazione di tutti i valori associati ai campi assegnatario ufficio/persona
	 */
	private void resetAssegnatarioPersona(AssegnatarioMailBox assegnatario) {
		if (assegnatario != null) {
			assegnatario.setCod_uff("");
			assegnatario.setNome_uff("");
			assegnatario.setMatricola("");
			assegnatario.setNome_pers("");
		}
	}

	/**
	 * Controllo dei campi obbligatori
	 * @return
	 */
	private boolean checkRequiredField(){
		boolean result = false;
		if (casellaPostaElettronica.getNome() == null || "".equals(casellaPostaElettronica.getNome().trim())) {
			this.setErrorMessage("templateForm:casellaPostaElettronica_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.name") + "'");
			result = true;
		}

		if (checkCodAmmAoo())
			result = true;

		if (casellaPostaElettronica.getMailbox_in() == null || casellaPostaElettronica.getMailbox_in().getEmail() == null || "".equals(casellaPostaElettronica.getMailbox_in().getEmail().trim())) {
			this.setErrorMessage("templateForm:mailboxin_email", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.email") + "'");
			result = true;
		}
		
		/** Controllo dei campi documentModel e Db
		 *  I campi devono essere obligatori solo per la casella di posta INTEROPERABILITÀ
		 *  kveizi 08/01/2018 
		 */
		
		if(casellaPostaElettronica.isInteroperabilita()) {
		
			if(casellaPostaElettronica.getDocumentModel() == null || "".equals(casellaPostaElettronica.getDocumentModel().trim())) {
				this.setErrorMessage("templateForm:casellaPostaElettronica_documentModel_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.documentModel") + "'");
				result = true;
			}
		
			if(casellaPostaElettronica.getDb() == null || "".equals(casellaPostaElettronica.getDb().trim())) {
				this.setErrorMessage("templateForm:casellaPostaElettronica_db", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.db") + "'");
				result = true;
			}
	    }

		return result;
	}

	/**
	 * Controllo di cod_amm e cod_aoo
	 * @return
	 */
	private boolean checkCodAmmAoo(){
		boolean result = false;
		if (casellaPostaElettronica.getCod_amm() == null || "".equals(casellaPostaElettronica.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:casellaPostaElettronica_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (casellaPostaElettronica.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:casellaPostaElettronica_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (casellaPostaElettronica.getCod_aoo() == null || "".equals(casellaPostaElettronica.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:casellaPostaElettronica_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (casellaPostaElettronica.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:casellaPostaElettronica_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}

		// controllo su cod_amm_segnatura e cod_aoo_segnatura (solo se interoperabilita')
		if (casellaPostaElettronica.isInteroperabilita()) {
			if (casellaPostaElettronica.getCod_amm_segnatura() == null || "".equals(casellaPostaElettronica.getCod_amm_segnatura().trim())) {
				this.setErrorMessage("templateForm:casellaPostaElettronica_codamm_segnatura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm_segnatura") + "'");
				result = true;
			}
			if (casellaPostaElettronica.getCod_aoo_segnatura() == null || "".equals(casellaPostaElettronica.getCod_aoo_segnatura().trim())) {
				this.setErrorMessage("templateForm:casellaPostaElettronica_codaoo_segnatura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo_segnatura") + "'");
				result = true;
			}
		}

		return result;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public CasellaPostaElettronica getCasellaPostaElettronica() {
		return casellaPostaElettronica;
	}

	public void setCasellaPostaElettronica(CasellaPostaElettronica casellaPostaElettronica) {
		this.casellaPostaElettronica = casellaPostaElettronica;
	}

	public boolean isAbilitaFatturePA() {
		return abilitaFatturePA;
	}

	public void setAbilitaFatturePA(boolean abilitaFatturePA) {
		this.abilitaFatturePA = abilitaFatturePA;
	}

	public boolean isAbilitaSplitAttachmentsSuMailStorage() {
		return abilitaSplitAttachmentsSuMailStorage;
	}

	public void setAbilitaSplitAttachmentsSuMailStorage(boolean abilitaSplitAttachmentsSuMailStorage) {
		this.abilitaSplitAttachmentsSuMailStorage = abilitaSplitAttachmentsSuMailStorage;
	}
	
	public boolean isSmistamentoFatturaPaSuMailboxCorretta() {
		return smistamentoFatturaPaSuMailboxCorretta;
	}

	public void setSmistamentoFatturaPaSuMailboxCorretta(boolean smistamentoFatturaPaSuMailboxCorretta) {
		this.smistamentoFatturaPaSuMailboxCorretta = smistamentoFatturaPaSuMailboxCorretta;
	}

	public List<Option> getTagSelect() {
		return tagSelect;
	}

	public void setTagSelect(List<Option> tagSelect) {
		this.tagSelect = tagSelect;
	}

	public boolean isLookupResponsabileSuPersona() {
		return lookupResponsabileSuPersona;
	}

	public void setLookupResponsabileSuPersona(boolean lookupResponsabileSuPersona) {
		this.lookupResponsabileSuPersona = lookupResponsabileSuPersona;
	}
	
	public boolean isAbilitaCasellaImportSuMailStorage() {
		return abilitaCasellaImportSuMailStorage;
	}

	public void setAbilitaCasellaImportSuMailStorage(boolean casellaImport) {
		this.abilitaCasellaImportSuMailStorage = casellaImport;
	}

	public boolean isAbilitaArchiviazioneTramiteTags() {
		return abilitaArchiviazioneTramiteTags;
	}

	public void setAbilitaArchiviazioneTramiteTags(boolean abilitaArchiviazioneTramiteTags) {
		this.abilitaArchiviazioneTramiteTags = abilitaArchiviazioneTramiteTags;
	}

	public boolean isAbilitaSmistamentoByMittente() {
		return abilitaSmistamentoByMittente;
	}

	public void setAbilitaSmistamentoByMittente(boolean abilitaSmistamentoByMittente) {
		this.abilitaSmistamentoByMittente = abilitaSmistamentoByMittente;
	}
	
}
