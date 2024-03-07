package it.tredi.dw4.docway.beans;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.custom.Altro_Ordine;
import it.tredi.dw4.docway.model.custom.Contratto;
import it.tredi.dw4.docway.model.custom.Fattura;
import it.tredi.dw4.docway.model.custom.Licenza;
import it.tredi.dw4.docway.model.custom.Servizio;
import it.tredi.dw4.docway.model.custom.Tipologia_Ordine;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLDocumento;

public class DocEditModifyPartenzaFTR extends DocEditModifyPartenza {
	private Fattura doc = new Fattura();
	
	public DocEditModifyPartenzaFTR() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public Fattura getDoc() {
		return doc;
	}

	public void setDoc(Fattura fattura) {
		this.doc = fattura;
	}
	
	@Override
	public boolean isDocEditModify() {
		return true;
	}
	
	@Override
	public void init(Document domDocumento) {
		this.doc = new Fattura();
		this.doc.init(domDocumento);
		
		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(domDocumento);
		
		// Imposto il titolo della pagina di creazione del documento
		docEditTitle = descrizioneRepertorio + " - " + (getFormsAdapter().checkBooleanFunzionalitaDisponibile("trasformaByDocEdit", false) ? I18N.mrs("dw4.trasformazioneRepertorio") : I18N.mrs("acl.modify"));
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			boolean rifEsterniModificabili = true;
			if (!formsAdapter.checkBooleanFunzionalitaDisponibile("abilitaModificaDatiDiProtocollo", false) 
					&& doc.getNum_prot() != null && doc.getNum_prot().length() > 0 
					&& !doc.isBozza() 
					&& copyIfNotStandardRep)
				rifEsterniModificabili = false;
			
			formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams("", true, rifEsterniModificabili, true));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// non utilizzato buildSpecificShowdocPageAndReturnNavigationRule() perche' non presente 
			// l'attributo 'personalViewToUse' per il caricamento del bean personalizzato 
			ShowdocPartenzaFTR showdocPartenzaFTR = new ShowdocPartenzaFTR();
			showdocPartenzaFTR.getFormsAdapter().fillFormsFromResponse(response);
			showdocPartenzaFTR.init(response.getDocument());
			setSessionAttribute("showdocPartenzaFTR", showdocPartenzaFTR);
			
			return "showdoc@partenza@FTR@reload";
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
				
			// non utilizzato buildSpecificShowdocPageAndReturnNavigationRule() perche' non presente 
			// l'attributo 'personalViewToUse' per il caricamento del bean personalizzato 
			ShowdocPartenzaFTR showdocPartenzaFTR = new ShowdocPartenzaFTR();
			showdocPartenzaFTR.getFormsAdapter().fillFormsFromResponse(response);
			showdocPartenzaFTR.init(response.getDocument());
			setSessionAttribute("showdocPartenzaFTR", showdocPartenzaFTR);
			
			return "showdoc@partenza@FTR@reload";
			
			//buildSpecificShowdocPageAndReturnNavigationRule(getDoc().getTipo(), response);
			//return "showdoc@" + getDoc().getTipo() + "@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Ricalcolo dei totali degli importi dell'offerta (metodo chiamato in caso di modifica
	 * di un costo relativo all'offerta)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String calcolaOfferta() throws Exception {
		if (doc.getDatiOrdine() != null)
			doc.getDatiOrdine().calcolaTotali();
		
		return null;
	}
	
	/**
	 * Recupero tipologia dell'offerta da thesauro
	 * @throws Exception
	 */
	public String thVincolatoTipologia() throws Exception {
		Tipologia_Ordine tipologia = (Tipologia_Ordine) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("tipologia");
		int num = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getTipologia_ordine().contains(tipologia)) ? doc.getDatiOrdine().getTipologia_ordine().indexOf(tipologia) : 0;
		String value = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getTipologia_ordine() != null && !"".equals(doc.getDatiOrdine().getTipologia_ordine().get(num).getText())) ? doc.getDatiOrdine().getTipologia_ordine().get(num).getText() : "";
		String fieldName = ".datiOrdine.tipologia_ordine["+num+"].text"; //fieldName 
		String chiave = "tipologia_ordine"; //chiave
		callThVincolato(doc, fieldName, chiave, value);
		return null;
	}
	
	/**
	 * Recupero servizio dell'offerta da thesauro
	 * @throws Exception
	 */
	public String thVincolatoServizio() throws Exception {
		Servizio servizio = (Servizio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("servizio");
		int num = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getServizio().contains(servizio)) ? doc.getDatiOrdine().getServizio().indexOf(servizio) : 0;
		String value = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getServizio() != null && !"".equals(doc.getDatiOrdine().getServizio().get(num).getText())) ? doc.getDatiOrdine().getServizio().get(num).getText() : "";
		String fieldName = ".datiOrdine.servizio["+num+"].text"; //fieldName 
		String chiave = "servizi_ordine"; //chiave
		callThVincolato(doc, fieldName, chiave, value);
		return null;
	}
	
	/**
	 * Recupero licenza dell'offerta da thesauro
	 * @throws Exception
	 */
	public String thVincolatoLicenza() throws Exception {
		Licenza licenza = (Licenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("licenza");
		int num = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getLicenza().contains(licenza)) ? doc.getDatiOrdine().getLicenza().indexOf(licenza) : 0;
		String value = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getLicenza() != null && !"".equals(doc.getDatiOrdine().getLicenza().get(num).getText())) ? doc.getDatiOrdine().getLicenza().get(num).getText() : "";
		String fieldName = ".datiOrdine.licenza["+num+"].text"; //fieldName 
		String chiave = "licenze_ordine"; //chiave
		callThVincolato(doc, fieldName, chiave, value);
		return null;
	}
	
	/**
	 * Recupero contratto dell'offerta da thesauro
	 * @throws Exception
	 */
	public String thVincolatoContratto() throws Exception {
		Contratto contratto = (Contratto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("contratto");
		int num = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getContratto().contains(contratto)) ? doc.getDatiOrdine().getContratto().indexOf(contratto) : 0;
		String value = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getContratto() != null && !"".equals(doc.getDatiOrdine().getContratto().get(num).getText())) ? doc.getDatiOrdine().getContratto().get(num).getText() : "";
		String fieldName = ".datiOrdine.contratto["+num+"].text"; //fieldName 
		String chiave = "contratti_ordine"; //chiave
		callThVincolato(doc, fieldName, chiave, value);
		return null;
	}
	
	/**
	 * Recupero altro dell'offerta da thesauro
	 * @throws Exception
	 */
	public String thVincolatoAltro() throws Exception {
		Altro_Ordine altro = (Altro_Ordine) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("altro");
		int num = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getAltro().contains(altro)) ? doc.getDatiOrdine().getAltro().indexOf(altro) : 0;
		String value = (doc.getDatiOrdine() != null && doc.getDatiOrdine().getAltro() != null && !"".equals(doc.getDatiOrdine().getAltro().get(num).getText())) ? doc.getDatiOrdine().getAltro().get(num).getText() : "";
		String fieldName = ".datiOrdine.altro["+num+"].text"; //fieldName 
		String chiave = "altro_ordine"; //chiave
		callThVincolato(doc, fieldName, chiave, value);
		return null;
	}
	
	/**
	 * Aggiunta di una tipologia di offerta per il doc
	 * @throws Exception
	 */
	public String addTipologia() throws Exception {
		Tipologia_Ordine tipologia = (Tipologia_Ordine) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("tipologia");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().addTipologia(tipologia);
		return null;
	}
	
	/**
	 * Cancellazione di un tipologia di offerta per il doc
	 * @throws Exception
	 */
	public String deleteTipologia() throws Exception {
		Tipologia_Ordine tipologia = (Tipologia_Ordine) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("tipologia");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().deleteTipologia(tipologia);
		return null;
	}
	
	/**
	 * Aggiunta di un servizio di offerta per il doc
	 * @throws Exception
	 */
	public String addServizio() throws Exception {
		Servizio servizio = (Servizio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("servizio");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().addServizio(servizio);
		return null;
	}
	
	/**
	 * Cancellazione di un servizio di offerta per il doc
	 * @throws Exception
	 */
	public String deleteServizio() throws Exception {
		Servizio servizio = (Servizio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("servizio");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().deleteServizio(servizio);
		
		calcolaOfferta(); // in caso di eliminazione di una voce occorre ricalcolare il totale
		return null;
	}
	
	/**
	 * Aggiunta di una licenza di offerta per il doc
	 * @throws Exception
	 */
	public String addLicenza() throws Exception {
		Licenza licenza = (Licenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("licenza");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().addLicenza(licenza);
		return null;
	}
	
	/**
	 * Cancellazione di una licenza di offerta per il doc
	 * @throws Exception
	 */
	public String deleteLicenza() throws Exception {
		Licenza licenza = (Licenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("licenza");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().deleteLicenza(licenza);
		
		calcolaOfferta(); // in caso di eliminazione di una voce occorre ricalcolare il totale
		return null;
	}
	
	/**
	 * Aggiunta di un contratto di offerta per il doc
	 * @throws Exception
	 */
	public String addContratto() throws Exception {
		Contratto contratto = (Contratto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("contratto");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().addContratto(contratto);
		return null;
	}
	
	/**
	 * Cancellazione di un contratto di offerta per il doc
	 * @throws Exception
	 */
	public String deleteContratto() throws Exception {
		Contratto contratto = (Contratto) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("contratto");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().deleteContratto(contratto);
		
		calcolaOfferta(); // in caso di eliminazione di una voce occorre ricalcolare il totale
		return null;
	}
	
	/**
	 * Aggiunta di un altro di offerta per il doc
	 * @throws Exception
	 */
	public String addAltro() throws Exception {
		Altro_Ordine altro = (Altro_Ordine) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("altro");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().addAltro(altro);
		return null;
	}
	
	/**
	 * Cancellazione di un altro di offerta per il doc
	 * @throws Exception
	 */
	public String deleteAltro() throws Exception {
		Altro_Ordine altro = (Altro_Ordine) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("altro");
		if (getDoc().getDatiOrdine() != null)
			getDoc().getDatiOrdine().deleteAltro(altro);
		
		calcolaOfferta(); // in caso di eliminazione di una voce occorre ricalcolare il totale
		return null;
	}
		
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		return super.checkRequiredField(true);
	}
	
}
