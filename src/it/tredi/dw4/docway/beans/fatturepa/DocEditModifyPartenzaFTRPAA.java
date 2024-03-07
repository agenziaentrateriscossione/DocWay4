package it.tredi.dw4.docway.beans.fatturepa;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyPartenza;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.fatturepa.DatiPagamento;
import it.tredi.dw4.docway.model.fatturepa.DettaglioPagamento;
import it.tredi.dw4.docway.model.fatturepa.FatturaPA;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditModifyPartenzaFTRPAA extends DocEditModifyPartenza {

	// campi specifici del repertorio di fatturePA attive
	private FatturaPA fatturaPA = new FatturaPA();
	private boolean completeEntry = false; // se attivato prevede l'inserimento di tutti i dati della fattura (generazione dell'XML della fatturaPA da form)
	
	private List<Option> regimefiscale_list = new ArrayList<Option>();
	private List<Option> tipodocumento_list = new ArrayList<Option>();
	private List<Option> condizionipagamento_list = new ArrayList<Option>();
	private List<Option> modalitapagamento_list = new ArrayList<Option>();
	
	private boolean committenteVisibile = false;
	
	public DocEditModifyPartenzaFTRPAA() throws Exception {
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
		
		fatturaPA.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/fatturaPA"));
		
		if (completeEntry) {
			if (fatturaPA.getDatiFattura().get(0).getDatiPagamento().size() == 0)
				fatturaPA.getDatiFattura().get(0).getDatiPagamento().add(new DatiPagamento());
			if (fatturaPA.getDatiFattura().get(0).getDatiPagamento().get(0).getDettaglioPagamento().size() == 0)
				fatturaPA.getDatiFattura().get(0).getDatiPagamento().get(0).getDettaglioPagamento().add(new DettaglioPagamento());
			
			// caricamento delle opzioni da visualizzare nelle select della maschera di edit
			regimefiscale_list = FatturePaUtilis.getListaRegimeFiscale();
			tipodocumento_list = FatturePaUtilis.getListaTipoDocumento();
			condizionipagamento_list = FatturePaUtilis.getListaCondizioniPagamento(true);
			modalitapagamento_list = FatturePaUtilis.getListaModalitaPagamento(true);
		}
	}
	
	public boolean isDocEditModify() {
		return true;
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			boolean rifEsterniModificabili = true;
			if (!formsAdapter.checkBooleanFunzionalitaDisponibile("abilitaModificaDatiDiProtocollo", false) 
					&& getDoc().getNum_prot() != null && getDoc().getNum_prot().length() > 0 
					&& !getDoc().isBozza() 
					&& copyIfNotStandardRep)
				rifEsterniModificabili = false;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", true, rifEsterniModificabili, true));
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
		try {
			XMLDocumento response = super._clearDocument();
			
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
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = super.checkRequiredField(false);
		
		if (completeEntry) {
			// definizione dell'XML della fatturaPA tramite compilazione del form di inserimento
			if (FatturePaUtilis.checkRequiredField(getDoc(), getFatturaPA(), this))
				result = true;
		}
		else {
			// il destinatario deve essere valorizzato valorizzato
			if (getDoc().getRif_esterni().get(0).getNome() == null || getDoc().getRif_esterni().get(0).getNome().length() == 0) {
				this.setErrorMessage("templateForm:nomeDestinatario_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.destinatario") + "'");
				result = true;
			}
			
			// in modifica il controllo sui dati obbligatori del registro delle fatture
			// deve sempre essere svolto
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
