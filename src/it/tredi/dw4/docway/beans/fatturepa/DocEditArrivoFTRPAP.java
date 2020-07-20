package it.tredi.dw4.docway.beans.fatturepa;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditArrivo;
import it.tredi.dw4.docway.model.fatturepa.FatturaPA;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditArrivoFTRPAP extends DocEditArrivo {

	// campi specifici del repertorio di fatturePA passive
	private FatturaPA fatturaPA = new FatturaPA();
	
	public DocEditArrivoFTRPAP() throws Exception {
		super();
	}
	
	public FatturaPA getFatturaPA() {
		return fatturaPA;
	}

	public void setFatturaPA(FatturaPA fatturaPA) {
		this.fatturaPA = fatturaPA;
	}
	
	public boolean isDocEditModify() {
		return false;
	}

	@Override
	public void init(Document domDocumento) {
		super.init(domDocumento);
		fatturaPA.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/fatturaPA"));
		
		// almeno una fattura deve essere inserita
		if (fatturaPA.getDatiFattura() == null || fatturaPA.getDatiFattura().size() == 0)
			fatturaPA.addDatiFattura();
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// verifica diplicato in data mittente (se attivo! verificaDuplicati == 'Si' o verificaDuplicati == 'Fine')
			if ((getDoc().getRif_esterni().get(0).getData_prot() == null || getDoc().getRif_esterni().get(0).getData_prot().equals(""))
					&& (getDoc().getRif_esterni().get(0).getN_prot() == null || getDoc().getRif_esterni().get(0).getN_prot().equals(""))) {
				// nel caso in cui i dati di prot del mittente (data e numero) non siano stati compilati
				// viene forzata a true la verifica dei duplicati
				duplicatoVerificato = true;
			}
			if (funzDispVerificaDuplicati != null 
					&& (funzDispVerificaDuplicati.toLowerCase().equals("si") || funzDispVerificaDuplicati.toLowerCase().equals("fine"))
					&& !duplicatoVerificato) {
				return null;
			}
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", false, true));
			formsAdapter.getDefaultForm().addParams(fatturaPA.asFormAdapterParams(".extra.fatturaPA"));
			
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

}
