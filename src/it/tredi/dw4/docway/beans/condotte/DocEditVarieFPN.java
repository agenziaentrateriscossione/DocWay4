package it.tredi.dw4.docway.beans.condotte;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditVarie;
import it.tredi.dw4.docway.model.RifEsterno;
import it.tredi.dw4.docway.model.condotte.Dati_approvazione;
import it.tredi.dw4.docway.model.condotte.Dati_contabili;
import it.tredi.dw4.docway.model.condotte.Dati_fattura;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditVarieFPN extends DocEditVarie {

	// campi specifici del repertorio fatture passive di condotte
	private Dati_fattura dati_fattura = new Dati_fattura();
	private Dati_contabili dati_contabili = new Dati_contabili();
	private Dati_approvazione dati_approvazione = new Dati_approvazione();
	
	private String hideDivs = ""; // elenco di div da nascondere in fase di inserimento
	
	public DocEditVarieFPN() throws Exception {
		super();
	}
	
	public boolean isDocEditModify() {
		return false;
	}
	
	public Dati_fattura getDati_fattura() {
		return dati_fattura;
	}

	public void setDati_fattura(Dati_fattura dati_fattura) {
		this.dati_fattura = dati_fattura;
	}
	
	public Dati_contabili getDati_contabili() {
		return dati_contabili;
	}

	public void setDati_contabili(Dati_contabili dati_contabili) {
		this.dati_contabili = dati_contabili;
	}

	public Dati_approvazione getDati_approvazione() {
		return dati_approvazione;
	}

	public void setDati_approvazione(Dati_approvazione dati_approvazione) {
		this.dati_approvazione = dati_approvazione;
	}
	
	public String getHideDivs() {
		return hideDivs;
	}

	public void setHideDivs(String hideDivs) {
		this.hideDivs = hideDivs;
	}
	
	@Override
	public void init(Document domDocumento) {
		super.init(domDocumento);
		
		// init dei dati di fatture di condotte
		dati_fattura.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/dati_fattura"));
		dati_contabili.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/dati_contabili"));
		dati_approvazione.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/dati_approvazione"));
		
		// caricamento di eventuali sezioni da nascondere in fase di inserimento di una fattura
		String toDo = XMLUtil.parseStrictAttribute(domDocumento, "/response/@toDo");
		if (toDo.startsWith("hideDivs")) {
			hideDivs = toDo.substring(9, toDo.length()-1);
			if (hideDivs.length() > 0 && !hideDivs.endsWith(","))
				hideDivs += ",";
		}
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			// occorre impostare dei valori di default per gli allegati ('0 - nessun allegato')
			// e la classificazione ('00/00 - non classificato')
			if (getDoc().getAllegati().get(0).getText() == null || getDoc().getAllegati().get(0).getText().length() == 0)
				getDoc().getAllegati().get(0).setText("0 - nessun allegato");
			if (getDoc().getClassif().getCod() == null || getDoc().getClassif().getCod().length() == 0) {
				getDoc().getClassif().setCod("00/00");
				getDoc().getClassif().setText("00/00 - non classificato");
			}
			
			if (checkRequiredField()) return null;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", false, true));
			
			formsAdapter.getDefaultForm().addParams(dati_fattura.asFormAdapterParams(".extra.dati_fattura"));
			formsAdapter.getDefaultForm().addParams(dati_contabili.asFormAdapterParams(".extra.dati_contabili"));
			formsAdapter.getDefaultForm().addParams(dati_approvazione.asFormAdapterParams(".extra.dati_approvazione"));
			
			// occorre settare anche il rif_esterno perche' altrimenti non sarebbe spedito per un documento di tipo varie
			if (getDoc().getRif_esterni().size() == 1) {
    			RifEsterno rif = (RifEsterno) getDoc().getRif_esterni().get(0);
    			formsAdapter.getDefaultForm().addParams(rif.asFormAdapterParams(".rif_esterni.rif"));
    		}
			
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocVarieFPN showdocVarieFPN = new ShowdocVarieFPN();
			showdocVarieFPN.getFormsAdapter().fillFormsFromResponse(response);
			showdocVarieFPN.init(response.getDocument());
			showdocVarieFPN.loadAspects("@varie", response, "showdoc");
			setSessionAttribute("showdocVarieFPN", showdocVarieFPN);
			
			return "condotte_showdoc@varie@FPN@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * personalizzazione del pulsante pulisci (deve essere rieseguito l'init del model delle fatture)
	 */
	@Override
	public String clearDocument() throws Exception {
		try {
			getFormsAdapter().insTableDocRepFPN(Const.DOCWAY_TIPOLOGIA_VARIE, getCodiceRepertorio(), getDescrizioneRepertorio());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			clearDocumentoIfIsNotDocEditModify(response);
			
			Document dom = response.getDocument();
			dati_fattura.init(XMLUtil.createDocument(dom, "/response/doc/extra/dati_fattura"));
			dati_contabili.init(XMLUtil.createDocument(dom, "/response/doc/extra/dati_contabili"));
			dati_approvazione.init(XMLUtil.createDocument(dom, "/response/doc/extra/dati_approvazione"));
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * lookup su centro di costo
	 * @return
	 * @throws Exception
	 */
	public String lookupCentroCosto() throws Exception {
		String value = (getDati_fattura().getCentro_costo().getName() != null) ? getDati_fattura().getCentro_costo().getName() : "";

		String aliasName 	= "struint_nome";
		String aliasName1 	= "";
		String titolo 		= "xml,/struttura_interna/nome"; //titolo 
		String ord 			= "xml(xpart:/struttura_interna/nome)"; //ord
		String campi 		= ".centro_costo.name=xml,/struttura_interna/nome" +
								" ; .centro_costo.@cod=xml,/struttura_interna/@cod_uff";
		String db 			= getFormsAdapter().getDefaultForm().getParam("aclDb"); //db 
		String newRecord 	= ""; //newRecord
		String xq			= ""; //extraQuery
		
		callLookup(getDati_fattura(), aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		
		return null;
	}
	
	/**
	 * clear lookup su centro di costo
	 * @return
	 * @throws Exception
	 */
	public String clearLookupCentroCosto() throws Exception {
		String campi = ".centro_costo.name=xml,/struttura_interna/nome" +
								" ; .centro_costo.@cod=xml,/struttura_interna/@cod_uff";
		return clearField(campi, getDati_fattura());
	}
	
	/**
	 * lookup su ragione sociale del fornitore
	 * @throws Exception
	 */
	public String lookupFornitore() throws Exception {
		return this.lookupRifStrutEst(0);
	}
	
	/**
	 * pulizia dei campi di lookup su ragione sociale del fornitore
	 * @throws Exception
	 */
	public String clearLookupFornitore() throws Exception {
		return this.clearLookupRifStrutEst(0);
	}
	
	/**
	 * controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campi obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	@Override
	public boolean checkRequiredField() {
		boolean result = false;
		
		// Controllo che l'RPA sia stato selezionato
		if (!getFormsAdapter().checkBooleanFunzionalitaDisponibile("docRPAEreditabile", false)) {
			if (getDoc().getAssegnazioneRPA() == null || 
					((getDoc().getAssegnazioneRPA().getNome_uff() == null || "".equals(getDoc().getAssegnazioneRPA().getNome_uff().trim())) &&
							(getDoc().getAssegnazioneRPA().getNome_persona() == null || "".equals(getDoc().getAssegnazioneRPA().getNome_persona().trim())))) {
				
				String[] fieldIds = { "templateForm:rpa_nome_uff_input", "templateForm:rpa_nome_persona_input", "templateForm:rpa_nome_ruolo_input" };
				this.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_il_campo_proprietario"));
				result = true;
			}
		}
		
		// controllo sui campi specifici del repertorio
		
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		
		// ragione sociale del fornitore
		if (getDoc().getRif_esterni().get(0).getNome() == null || getDoc().getRif_esterni().get(0).getNome().length() == 0) {
			this.setErrorMessage("templateForm:ragioneSocialeFornitore_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.ragione_sociale_fornitore") + "'");
			result = true;
		}
		
		// data di emissione della fattura
		if (getDati_fattura() == null || getDati_fattura().getData_fatt().length() == 0) {
			this.setErrorMessage("templateForm:dataEmissioneFattura", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_emissione_fattura") + "'");
			result = true;
		}
		else {
			// Controllo se il valore associato alla data e' corretto
			if (!DateUtil.isValidDate(getDoc().getData_prot(), formatoData)) {
				this.setErrorMessage("templateForm:dataEmissioneFattura", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_emissione_fattura") + "': " + formatoData.toLowerCase());
				result = true;
			}
		}
				
		return result;
	}

}
