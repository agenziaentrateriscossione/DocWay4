package it.tredi.dw4.docway.beans.fatturepa;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyArrivo;
import it.tredi.dw4.docway.model.fatturepa.FatturaPA;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditModifyArrivoFTRPAP extends DocEditModifyArrivo {

	// campi specifici del repertorio di fatturePA passive
	private FatturaPA fatturaPA = new FatturaPA();
	
	public DocEditModifyArrivoFTRPAP() throws Exception {
		super();
	}
	
	public FatturaPA getFatturaPA() {
		return fatturaPA;
	}

	public void setFatturaPA(FatturaPA fatturaPA) {
		this.fatturaPA = fatturaPA;
	}
	
	public boolean isDocEditModify() {
		return true;
	}

	@Override
	public void init(Document domDocumento) {
		super.init(domDocumento);
		fatturaPA.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/fatturaPA"));
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
			formsAdapter.getDefaultForm().addParams(fatturaPA.asFormAdapterParams(".extra.fatturaPA"));
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
			
			ShowdocArrivoFTRPAP showdocArrivoFTRPAP = new ShowdocArrivoFTRPAP();
			showdocArrivoFTRPAP.getFormsAdapter().fillFormsFromResponse(response);
			showdocArrivoFTRPAP.init(response.getDocument());
			showdocArrivoFTRPAP.loadAspects("@arrivo", response, "showdoc");
			setSessionAttribute("showdocArrivoFTRPAP", showdocArrivoFTRPAP);
			
			return "fatturepa_showdoc@arrivo@FTRPAP@reload";
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
	 * @return false se tutti i campi obbligatori del registro fatture sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	@Override
	public boolean checkRequiredField() {
		boolean result = super.checkRequiredField();
		
		// controllo sui campi specifici della fattura
		if (FatturePaUtilis.checkRequiredFieldRegistroFatture(getDoc(), getFatturaPA(), this, this.formsAdapter.checkBooleanFunzionalitaDisponibile("enableRegistroFatture", false)))
			result = true;
				
		return result;
	}
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			ShowdocArrivoFTRPAP showdocArrivoFTRPAP = new ShowdocArrivoFTRPAP();
			showdocArrivoFTRPAP.getFormsAdapter().fillFormsFromResponse(response);
			showdocArrivoFTRPAP.init(response.getDocument());
			showdocArrivoFTRPAP.loadAspects("@arrivo", response, "showdoc");
			setSessionAttribute("showdocArrivoFTRPAP", showdocArrivoFTRPAP);
			
			return "fatturepa_showdoc@arrivo@FTRPAP@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
}
