package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Partenza;
import it.tredi.dw4.docway.model.Prot_differito;
import it.tredi.dw4.docway.model.RifEsterno;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class DocEditPartenza extends DocEditDoc {
	private Partenza doc = new Partenza();
	
	private final String DEFAULT_PARTENZA_TITLE = "dw4.ins_partenza";
	
	public DocEditPartenza() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public boolean isDocEditModify() {
		return false;
	}
	
	@Override
	public void init(Document domDocumento) {
		this.doc = new Partenza();
		this.doc.init(domDocumento);
		
		// inizializzazione common per tutte le tipologie di documenti di DocWay
		initCommon(domDocumento);
		
		// mbernardini 03/04/2015 : in caso di risposta di un doc in arrivo viene riportata erroneamente sul doc in partenza la data
		// del doc in arrivo (se lasciato vuoto il campo data doc del protocollo in partenza)
		if (this.doc.getRif_esterni().size() > 0) {
			for (int i=0; i<this.doc.getRif_esterni().size(); i++) {
				if (this.doc.getRif_esterni().get(i) != null) {
					if (this.doc.getRif_esterni().get(i).getData_prot() != null)
						this.doc.getRif_esterni().get(i).setData_prot("");
					if (this.doc.getRif_esterni().get(i).getN_prot() != null)
						this.doc.getRif_esterni().get(i).setN_prot("");
				}
			}
		}
		
		// mbernardini 07/08/2015 : in caso di risposta di un doc in arrivo differito vengono erroneamente riportati sul doc in partenza
		// i dati relativi al differimento
		if (this.doc.getProt_differito() != null 
				&& this.doc.getProt_differito().getData_arrivo() != null && this.doc.getProt_differito().getData_arrivo().length() > 0) {
			this.doc.setProt_differito(new Prot_differito());
		}
		
		// Imposto il titolo della pagina di creazione del documento
		setInsPartenzaTitleByCodRepertorio();
	}
	
	/**
	 * Imposta il titolo della maschera di inserimento del documento
	 */
	private void setInsPartenzaTitleByCodRepertorio() {
		if (doceditRep && descrizioneRepertorio != null && descrizioneRepertorio.length() > 0)
			docEditTitle = descrizioneRepertorio + " - " + I18N.mrs("acl.insert");
		else
			docEditTitle = I18N.mrs(DEFAULT_PARTENZA_TITLE);
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// personalizzazione del salvataggio per il repertorio
			boolean isRepertorio = false;
			if (doceditRep)
				isRepertorio = true;
			
			formsAdapter.getDefaultForm().addParams(this.doc.asFormAdapterParams("", false, isRepertorio));
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
		
		result = super.checkRequiredFieldCommon(false); // controlli comuni a tutte le tipologie di documenti
		
		// Controllo se almeno un destinatario del documento e' valorizzato
		if (!doceditRep) { // eseguo il controllo solo se non si tratta di un repertorio
			if (getDoc().getRif_esterni().get(0).getNome() == null || getDoc().getRif_esterni().get(0).getNome().length() == 0) {
				this.setErrorMessage("templateForm:docEditDestinatari:0:nomeDestinatario_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.destinatario") + "'");
				result = true;
			}
		}
		
		// Imposto lo scarto automatico se non impostato
		if (getDoc().getScarto() == null || getDoc().getScarto().length() == 0)
			getDoc().setScarto(AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ScartoAutomatico")));
		
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
		
		// controllo su tutti i campi data (verifica del formato)
		if (getDoc().getData_reale() != null && getDoc().getData_reale().length() > 0) {
			if (!DateUtil.isValidDate(getDoc().getData_reale(), formatoData)) {
				this.setErrorMessage("templateForm:dataRealeDoc", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_doc") + "': " + formatoData.toLowerCase());
				result = true;
			}
		}
		
		// Controllo su mezzo di trasmissione
		if (checkMezzoTrasmissione())
			result = true;
				
		return result;
	}
	
}
