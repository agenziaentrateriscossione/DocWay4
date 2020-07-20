package it.tredi.dw4.docway.beans.fatturepa;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditPartenza;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.fatturepa.DatiFattura;
import it.tredi.dw4.docway.model.fatturepa.DatiPagamento;
import it.tredi.dw4.docway.model.fatturepa.DettaglioPagamento;
import it.tredi.dw4.docway.model.fatturepa.FatturaPA;
import it.tredi.dw4.docway.model.fatturepa.LineaBeniServizi;
import it.tredi.dw4.docway.model.fatturepa.RiepilogoBeniServizi;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

public class DocEditPartenzaFTRPAA extends DocEditPartenza {
	
	// campi specifici del repertorio di fatturePA attive
	private FatturaPA fatturaPA = new FatturaPA();
	private boolean completeEntry = false; // se attivato prevede l'inserimento di tutti i dati della fattura (generazione dell'XML della fatturaPA da form)
	
	private List<Option> regimefiscale_list = new ArrayList<Option>();
	private List<Option> tipodocumento_list = new ArrayList<Option>();
	private List<Option> condizionipagamento_list = new ArrayList<Option>();
	private List<Option> modalitapagamento_list = new ArrayList<Option>();
	
	private boolean committenteVisibile = false;
	
	public DocEditPartenzaFTRPAA() throws Exception {
		super();
	}
	
	public FatturaPA getFatturaPA() {
		return fatturaPA;
	}

	public void setFatturaPA(FatturaPA fatturaPA) {
		this.fatturaPA = fatturaPA;
	}
	
	public boolean isCompleteEntry() {
		return completeEntry;
	}

	public void setCompleteEntry(boolean completeEntry) {
		this.completeEntry = completeEntry;
	}
	
	public List<Option> getRegimefiscale_list() {
		return regimefiscale_list;
	}

	public void setRegimefiscale_list(List<Option> regimefiscale_list) {
		this.regimefiscale_list = regimefiscale_list;
	}
	
	public List<Option> getTipodocumento_list() {
		return tipodocumento_list;
	}

	public void setTipodocumento_list(List<Option> tipodocumento_list) {
		this.tipodocumento_list = tipodocumento_list;
	}
	
	public List<Option> getCondizionipagamento_list() {
		return condizionipagamento_list;
	}

	public void setCondizionipagamento_list(List<Option> condizionipagamento_list) {
		this.condizionipagamento_list = condizionipagamento_list;
	}

	public List<Option> getModalitapagamento_list() {
		return modalitapagamento_list;
	}

	public void setModalitapagamento_list(List<Option> modalitapagamento_list) {
		this.modalitapagamento_list = modalitapagamento_list;
	}
	
	public boolean isCommittenteVisibile() {
		return committenteVisibile;
	}

	public void setCommittenteVisibile(boolean committenteVisibile) {
		this.committenteVisibile = committenteVisibile;
	}
	
	@Override
	public void init(Document domDocumento) {
		super.init(domDocumento);
		
		// forzo il documento come bozza
		getDoc().setBozza(true);
		
		fatturaPA.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/fatturaPA"));
		
		if (completeEntry) {
			// definizione dell'XML della fatturaPA tramite compilazione del form di inserimento 
			// FIXME da completare, predisposta solo per demo preliminare
			
			if (fatturaPA.getDatiTrasmissione().getIdPaese().length() == 0)
				fatturaPA.getDatiTrasmissione().setIdPaese("IT");
			if (fatturaPA.getDatiCedentePrestatore().getIdPaese().length() == 0)
				fatturaPA.getDatiCedentePrestatore().setIdPaese("IT");
			
			if (fatturaPA.getDatiFattura().size() == 0) { 
				fatturaPA.getDatiFattura().add(new DatiFattura()); // inserimento di una istanza vuota di fattura
				if (fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().getDivisa().length() == 0)
					fatturaPA.getDatiFattura().get(0).getDatiGeneraliDocumento().setDivisa("EUR");
				
				if (fatturaPA.getDatiFattura().get(0).getDatiBeniServizi().getLineaBeniServizi().size() == 0)
					fatturaPA.getDatiFattura().get(0).getDatiBeniServizi().getLineaBeniServizi().add(new LineaBeniServizi());
				if (fatturaPA.getDatiFattura().get(0).getDatiBeniServizi().getRiepilogoBeniServizi().size() == 0)
					fatturaPA.getDatiFattura().get(0).getDatiBeniServizi().getRiepilogoBeniServizi().add(new RiepilogoBeniServizi());
				
				if (fatturaPA.getDatiFattura().get(0).getDatiPagamento().size() == 0)
					fatturaPA.getDatiFattura().get(0).getDatiPagamento().add(new DatiPagamento());
				if (fatturaPA.getDatiFattura().get(0).getDatiPagamento().get(0).getDettaglioPagamento().size() == 0)
					fatturaPA.getDatiFattura().get(0).getDatiPagamento().get(0).getDettaglioPagamento().add(new DettaglioPagamento());
			}
			
			if ((fatturaPA.getDatiCessionarioCommittente().getDenominazione() == null || fatturaPA.getDatiCessionarioCommittente().getDenominazione().equals(""))
									&& (fatturaPA.getDatiCessionarioCommittente().getCognome() == null || fatturaPA.getDatiCessionarioCommittente().getCognome().equals(""))) {
	
				// TODO i dati del committente andrebbero recuperati da ACL, per il momento li recupero da properties
				fatturaPA.getDatiCessionarioCommittente().setDenominazione(DocWayProperties.readProperty("fatturepa.committente.denominazione", ""));
				fatturaPA.getDatiCessionarioCommittente().setIndirizzo(DocWayProperties.readProperty("fatturepa.committente.sede.indirizzo", ""));
				fatturaPA.getDatiCessionarioCommittente().setNumeroCivico(DocWayProperties.readProperty("fatturepa.committente.sede.numcivico", ""));
				fatturaPA.getDatiCessionarioCommittente().setComune(DocWayProperties.readProperty("fatturepa.committente.sede.comune", ""));
				fatturaPA.getDatiCessionarioCommittente().setCap(DocWayProperties.readProperty("fatturepa.committente.sede.cap", ""));
				fatturaPA.getDatiCessionarioCommittente().setProvincia(DocWayProperties.readProperty("fatturepa.committente.sede.provincia", ""));
				fatturaPA.getDatiCessionarioCommittente().setNazione(DocWayProperties.readProperty("fatturepa.committente.sede.nazione", ""));
				
				// TODO da gestire..
				/*
				if (fatturaPA.getDatiCessionarioCommittente().getIdPaese().length() == 0)
					fatturaPA.getDatiCessionarioCommittente().setIdPaese("IT");
				*/
			}
			
			// caricamento delle opzioni da visualizzare nelle select della maschera di edit
			regimefiscale_list = FatturePaUtilis.getListaRegimeFiscale();
			tipodocumento_list = FatturePaUtilis.getListaTipoDocumento();
			condizionipagamento_list = FatturePaUtilis.getListaCondizioniPagamento(true);
			modalitapagamento_list = FatturePaUtilis.getListaModalitaPagamento(true);
		}
		else {
			// inserimento di fatture cartacee o caricamento di fatturaPA tramite upload di file XML gia' compilati
			
			// almeno una fattura deve essere inserita
			if (fatturaPA.getDatiFattura() == null || fatturaPA.getDatiFattura().size() == 0)
				fatturaPA.addDatiFattura();
		}
	}
	
	public boolean isDocEditModify() {
		return false;
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			// in caso di allegati non indicati viene impostato di default il test "0 - nessun allegato" 
			if (getDoc().getAllegati().get(0).getText() == null || getDoc().getAllegati().get(0).getText().length() == 0)
				getDoc().getAllegati().get(0).setText(I18N.mrs("dw4.0_nessun_allegato"));
			
			if (checkRequiredField()) return null;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", false, true));
			formsAdapter.getDefaultForm().addParams(fatturaPA.asFormAdapterParams(".extra.fatturaPA"));
			
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocPartenzaFTRPAA showdocPartenzaFTRPAA = new ShowdocPartenzaFTRPAA();
			showdocPartenzaFTRPAA.getFormsAdapter().fillFormsFromResponse(response);
			showdocPartenzaFTRPAA.init(response.getDocument());
			showdocPartenzaFTRPAA.loadAspects("@partenza", response, "showdoc");
			setSessionAttribute("showdocPartenzaFTRPAA", showdocPartenzaFTRPAA);
			
			return "fatturepa_showdoc@partenza@FTRPAA@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	@Override
	public String clearDocument() throws Exception {
		super.clearDocument();
		
		return null;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	@Override
	public boolean checkRequiredField() {
		boolean result = super.checkRequiredField();
		
		// controllo sui campi specifici della fattura
		
		if (completeEntry) {
			// definizione dell'XML della fatturaPA tramite compilazione del form di inserimento
			if (FatturePaUtilis.checkRequiredField(getDoc(), getFatturaPA(), this))
				result = true;
		}
		else if (fatturaPA.isFatturaCartacea()) {
			// il destinatario deve essere valorizzato valorizzato
			if (getDoc().getRif_esterni().get(0).getNome() == null || getDoc().getRif_esterni().get(0).getNome().length() == 0) {
				this.setErrorMessage("templateForm:nomeDestinatario_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.destinatario") + "'");
				result = true;
			}
			
			// fattura cartacea. controllo sui campi di registro delle fatture obbligatori
			if (FatturePaUtilis.checkRequiredFieldRegistroFatture(getDoc(), getFatturaPA(), this, this.formsAdapter.checkBooleanFunzionalitaDisponibile("enableRegistroFatture", false)))
				result = true;
		}
				
		return result;
	}
	
	/**
	 * abilitazione del campo 'corpo email' in docedi di un documento
	 * @return
	 */
	public String attivaCommittente() {
		committenteVisibile = !committenteVisibile;
		
		return null;
	}

}
