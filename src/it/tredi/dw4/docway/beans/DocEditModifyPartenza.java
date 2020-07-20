package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Partenza;
import it.tredi.dw4.docway.model.RifEsterno;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class DocEditModifyPartenza extends DocEditDoc {
	private Partenza doc = new Partenza();
	
	private final String DEFAULT_PARTENZA_TITLE = "dw4.mod_partenza";
	
	public DocEditModifyPartenza() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public boolean isDocEditModify() {
		return true;
	}
	
	@Override
	public void init(Document domDocumento) {
		this.doc = new Partenza();
		this.doc.init(domDocumento);
		
		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(domDocumento);
		
		// Imposto il titolo della pagina di creazione del documento
		setInsPartenzaTitleByCodRepertorio();
	}
	
	/**
	 * Imposta il titolo della maschera di inserimento del documento
	 */
	private void setInsPartenzaTitleByCodRepertorio() {
		if (doceditRep && descrizioneRepertorio != null && descrizioneRepertorio.length() > 0)
			docEditTitle = descrizioneRepertorio + " - " + I18N.mrs("acl.modify");
		else
			docEditTitle = I18N.mrs(DEFAULT_PARTENZA_TITLE);
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
					&& !doc.isBozza() 
					&& copyIfNotStandardRep)
				rifEsterniModificabili = false;
			
			formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams("", true, rifEsterniModificabili, isRepertorio));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("partenza", response);		
			return "showdoc@partenza@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public Partenza getDoc() {
		return doc;
	}

	public void setDoc(Partenza partenza) {
		this.doc = partenza;
	}
	
	/**
	 * Lookup su numero di raccomandata
	 * @throws Exception
	 */
	public String lookupNumRaccomandata() throws Exception {
		// TODO Da completare (diverso da arrivo perche' campo multiplo)
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su numero di raccomandata
	 * @throws Exception
	 */
	public String clearLookupNumRaccomandata() throws Exception {
		// TODO Da completare (diverso da arrivo perche' campo multiplo)
		return null;
	}
	
	/**
	 * Verifica se esistono duplicati del documento in partenza che si sta 
	 * registrando
	 * 
	 * @throws Exception
	 */
	public String verificaDuplicatiDoc() throws Exception {
		return null;
	}	
	
	/**
	 * Eliminazione di un rif est al doc (destinatario del doc in partenza)
	 * @return
	 */
	public String deleteRifEst(){
		RifEsterno rifEsterno = (RifEsterno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rifEsterno");
		getDoc().deleteRifEst(rifEsterno);
		return null;
	}

	/**
	 * Aggiunta di un rif est al doc (destinatario del doc in partenza)
	 * @return
	 */
	public String addRifEst(){
		RifEsterno rifEsterno = (RifEsterno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rifEsterno");
		getDoc().addRifEst(rifEsterno);
		return null;
	}
	
	/**
	 * Lookup su destinatario del documento
	 * @throws Exception
	 */
	public String lookupDestinatario() throws Exception {
		RifEsterno rifEsterno = (RifEsterno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rifEsterno");
		return this.lookupRifStrutEst((getDoc().getRif_esterni().contains(rifEsterno)) ? getDoc().getRif_esterni().indexOf(rifEsterno): 0);
	}
	
	/**
	 * Pulizia dei campi di lookup su destinatario del documento
	 * @throws Exception
	 */
	public String clearLookupDestinatario() throws Exception {
		RifEsterno rifEsterno = (RifEsterno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rifEsterno");
		return this.clearLookupRifStrutEst((getDoc().getRif_esterni().contains(rifEsterno)) ? getDoc().getRif_esterni().indexOf(rifEsterno): 0);
	}
	
	/**
	 * Lookup su cortese attenzione del documento
	 * @throws Exception
	 */
	public String lookupCorteseAttenzione() throws Exception {
		RifEsterno rifEsterno = (RifEsterno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rifEsterno");
		return this.lookupRifPersEst((getDoc().getRif_esterni().contains(rifEsterno)) ? getDoc().getRif_esterni().indexOf(rifEsterno): 0);
	}
	
	/**
	 * Pulizia dei campi di lookup su cortese attenzione del documento
	 * @throws Exception
	 */
	public String clearLookupCorteseAttenzione() throws Exception {
		RifEsterno rifEsterno = (RifEsterno) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rifEsterno");
		return this.clearLookupRifPersEst((getDoc().getRif_esterni().contains(rifEsterno)) ? getDoc().getRif_esterni().indexOf(rifEsterno): 0);
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
		
		// Controllo se almeno un destinatario del documento e' valorizzato
		if (getDoc().getRepertorio() == null || getDoc().getRepertorio().getCod() == null || getDoc().getRepertorio().getCod().length() == 0) { // eseguo il controllo solo se non si tratta di un repertorio
			if (getDoc().getRif_esterni().get(0).getNome() == null || getDoc().getRif_esterni().get(0).getNome().length() == 0) {
				this.setErrorMessage("templateForm:docEditDestinatari:0:nomeDestinatario_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.destinatario") + "'");
				result = true;
			}
		}
		
		// Imposto lo scarto automatico se non impostato
		if (getDoc().getScarto() == null || getDoc().getScarto().length() == 0)
			getDoc().setScarto(AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ScartoAutomatico")));
		
		// controllo su tutti i campi data (verifica del formato)
		if (getDoc().getData_reale() != null && getDoc().getData_reale().length() > 0) {
			if (!DateUtil.isValidDate(getDoc().getData_reale(), formatoData)) {
				this.setErrorMessage("templateForm:dataRealeDoc", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_doc") + "': " + formatoData.toLowerCase());
				result = true;
			}
			else {
				int dataReale = new Integer(DateUtil.formatDate2XW(getDoc().getData_reale(), formatoData)).intValue();
				
				if (getDoc().getData_prot() != null && !getDoc().getData_prot().equals("") && !getDoc().getData_prot().equals(".")) {
					// Controllo che la data specificata sia antecedente alla data di protocollo
					String dataProtDocumento = getDoc().getData_prot();
					if (!DateUtil.checkNumericDate(dataProtDocumento))
						dataProtDocumento = DateUtil.formatDate2XW(dataProtDocumento, formatoData);
					if (dataReale > new Integer(dataProtDocumento).intValue()) {
						this.setErrorMessage("templateForm:dataRealeDoc", I18N.mrs("dw4.la_data_del_documento_non_puo_essere_superiore_alla_data_di_protocollazione"));
						result = true;
					}
				}
				else {
					// Controllo che la data specificata sia antecedente alla data corerente
					int dataCur = new Integer(DateUtil.getCurrentDateNorm()).intValue();
					if (dataReale > dataCur) {
						this.setErrorMessage("templateForm:dataRealeDoc", I18N.mrs("dw4.la_data_del_documento_non_puo_essere_superiore_alla_data_odierna"));
						result = true;
					}
				}
			}
		}
				
		return result;
	}
	
}
