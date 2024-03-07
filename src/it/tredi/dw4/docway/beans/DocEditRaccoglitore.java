package it.tredi.dw4.docway.beans;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Raccoglitore;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditRaccoglitore extends DocWayDocedit {
	
	private DocDocWayDocEditFormsAdapter formsAdapter;
	
	private Raccoglitore raccoglitore = new Raccoglitore();
	
	private String xml = "";

	public DocEditRaccoglitore() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public Raccoglitore getRaccoglitore() {
		return raccoglitore;
	}

	public void setRaccoglitore(Raccoglitore raccoglitore) {
		this.raccoglitore = raccoglitore;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
		
	public void init(Document domDocumento) {
		xml = domDocumento.asXML();
		this.raccoglitore = new Raccoglitore();
		this.raccoglitore.init(domDocumento);
		
		// inizializzazione ai valori di default dei campi 
		// del raccoglitore (caso di ripeti nuovo)
		this.raccoglitore.setNrecord(".");
		this.raccoglitore.setAnno("");
		this.raccoglitore.setData_chiusura("");
		this.raccoglitore.setStato("aperto");
		
		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(domDocumento, "raccoglitore");
		
		// In caso sia attiva la gestione dei rif int CC e la lista sia vuota carico il primo rif int CC
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile("abilitaCCRaccoglitori", false) && (this.raccoglitore.getAssegnazioneCC() == null || this.raccoglitore.getAssegnazioneCC().size() == 0))
			this.raccoglitore.getAssegnazioneCC().add(new Rif());
		
		// mbernardini 30/09/2016 : gestione dei raccoglitori custom
		this.raccoglitore.setCodiceRaccoglitoreCustom(XMLUtil.parseStrictAttribute(domDocumento, "/response/@codice_racc"));
		this.raccoglitore.setDescrizioneRaccoglitoreCustom(XMLUtil.parseStrictAttribute(domDocumento, "/response/@descrizione_racc"));

		// stato iniziale del checkbox "Invio Email di notifica"
		if ( !this.formsAdapter.checkBooleanFunzionalitaDisponibile("CheckboxEmailNotifica", false) )
			this.getRaccoglitore().setSendMailRifInterni(false);

		// ERM012596 - rtirabassi - notifica capillare
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile("CheckboxEmailNotificaCapillare", false) ) {
			getRaccoglitore().setSendMailRifInterni(false);
			getRaccoglitore().setSendMailSelectedRifInterni(true);
		}
    }
	
	public DocDocWayDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			formsAdapter.getDefaultForm().addParams(this.raccoglitore.asFormAdapterParams(""));
			XMLDocumento response = super._saveDocument(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			String assignAndClose = XMLUtil.parseAttribute(response.getDocument(), "response/@assignAndClose", "false");
			if (isPopupPage() && assignAndClose.equals("true")) {
				// inserimento da popup con assegnazione di un documento al raccoglitore
				formsAdapter.fillFormsFromResponse(response);
				this.init(response.getDocument());
				setSessionAttribute("docEditRaccoglitore", this);
				
				this.setPopupPage(true);
				return null;
			}
			else {
				buildSpecificShowdocPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, response);		
				return "showdoc@raccoglitore@reload";
			}
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
	 * RifintLookup su ufficio RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_rpa() throws Exception {
		String value 	= (raccoglitore.getAssegnazioneRPA() != null && raccoglitore.getAssegnazioneRPA().getNome_uff() != null) ? raccoglitore.getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (raccoglitore.getAssegnazioneRPA() != null && raccoglitore.getAssegnazioneRPA().getNome_persona() != null) ? raccoglitore.getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";
		
		rifintLookupUfficio(raccoglitore, value, value2, campi);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_rpa() throws Exception {
		String value = (raccoglitore.getAssegnazioneRPA() != null && raccoglitore.getAssegnazioneRPA().getNome_uff() != null) ? raccoglitore.getAssegnazioneRPA().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_uff" +
							"|.assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_uff" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, raccoglitore);
			raccoglitore.getAssegnazioneRPA().setUfficio_completo(false);
		}
		
		return null;
	}
	
	/**
	 * RifintLookup su persona RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_rpa() throws Exception {
		String value 	= (raccoglitore.getAssegnazioneRPA() != null && raccoglitore.getAssegnazioneRPA().getNome_uff() != null) ? raccoglitore.getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (raccoglitore.getAssegnazioneRPA() != null && raccoglitore.getAssegnazioneRPA().getNome_persona() != null) ? raccoglitore.getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";
		
		rifintLookupPersona(raccoglitore, value, value2, campi);
				
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_rpa() throws Exception {
		String value = (raccoglitore.getAssegnazioneRPA() != null && raccoglitore.getAssegnazioneRPA().getNome_persona() != null) ? raccoglitore.getAssegnazioneRPA().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, raccoglitore);
			raccoglitore.getAssegnazioneRPA().setUfficio_completo(false);
		}
		
		return null;
	}
	
	/**
	 * Lookup su ruolo RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_rpa() throws Exception {
		String value = (raccoglitore.getAssegnazioneRPA() != null && raccoglitore.getAssegnazioneRPA().getNome_uff() != null) ? raccoglitore.getAssegnazioneRPA().getNome_uff() : "";
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi 		= ".assegnazioneRPA.@nome_uff=xml,/ruolo/nome"; // TODO Da completare 
		
		lookupRuolo(raccoglitore, value, campi, raccoglitore.getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_rpa() throws Exception {
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneRPA.@nome_uff=xml,/ruolo/nome"; // TODO Da completare 
		
		return clearField(campi, raccoglitore);
	}
	
	/**
	 * RifintLookup su ufficio CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (raccoglitore.getAssegnazioneCC().contains(rif)) ? raccoglitore.getAssegnazioneCC().indexOf(rif): 0;
		
		String value 	= (raccoglitore.getAssegnazioneCC() != null && raccoglitore.getAssegnazioneCC().get(num) != null) ? raccoglitore.getAssegnazioneCC().get(num).getNome_uff() : "";
		String value2 	= (raccoglitore.getAssegnazioneCC() != null && raccoglitore.getAssegnazioneCC().get(num) != null) ? raccoglitore.getAssegnazioneCC().get(num).getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";
		
		rifintLookupUfficio(raccoglitore, value, value2, campi);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (raccoglitore.getAssegnazioneCC().contains(rif)) ? raccoglitore.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (raccoglitore.getAssegnazioneCC() != null && raccoglitore.getAssegnazioneCC().get(num) != null) ? raccoglitore.getAssegnazioneCC().get(num).getNome_uff() : "";
		
		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_uff" +
							"|.assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_uff" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, raccoglitore);
			raccoglitore.getAssegnazioneCC().get(num).setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * RifintLookup su persona CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (raccoglitore.getAssegnazioneCC().contains(rif)) ? raccoglitore.getAssegnazioneCC().indexOf(rif): 0;
		
		String value	= (raccoglitore.getAssegnazioneCC() != null && raccoglitore.getAssegnazioneCC().get(num) != null) ? raccoglitore.getAssegnazioneCC().get(num).getNome_uff() : "";
		String value2 	= (raccoglitore.getAssegnazioneCC() != null && raccoglitore.getAssegnazioneCC().get(num) != null) ? raccoglitore.getAssegnazioneCC().get(num).getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";
		
		rifintLookupPersona(raccoglitore, value, value2, campi);
		rif.setUfficio_completo(false);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (raccoglitore.getAssegnazioneCC().contains(rif)) ? raccoglitore.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (raccoglitore.getAssegnazioneCC() != null && raccoglitore.getAssegnazioneCC().get(num) != null) ? raccoglitore.getAssegnazioneCC().get(num).getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, raccoglitore);
			raccoglitore.getAssegnazioneCC().get(num).setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * Lookup su ruolo CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (raccoglitore.getAssegnazioneCC().contains(rif)) ? raccoglitore.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (raccoglitore.getAssegnazioneCC() != null && raccoglitore.getAssegnazioneCC().get(num) != null) ? raccoglitore.getAssegnazioneCC().get(num).getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare 

		lookupRuolo(raccoglitore, value, campi, raccoglitore.getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (raccoglitore.getAssegnazioneCC().contains(rif)) ? raccoglitore.getAssegnazioneCC().indexOf(rif): 0;
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare 
		return clearField(campi, raccoglitore);
	}
	
	/**
	 * Cancellazione di un rif int in CC
	 * @return
	 */
	public String deleteRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		raccoglitore.deleteRifintCC(rif);
		return null;
	}

	/**
	 * Aggiunta di un rif int in CC
	 * @return
	 */
	public String addRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		raccoglitore.addRifintCC(rif);
		return null;
	}
	
	/**
	 * Imposta o meno l'ufficio completo per il rif int selezionato
	 */
	public String ufficioCompleto() {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		if (rif != null)
			rif.setUfficioCompleto();
		return null;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField(){
		boolean result = false;
		
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		
		// validazione dei campi custom caricati nella pagina
		result = getCustomfields().checkRequiredFields(false, formatoData, this);
		
		// Controllo se il campo 'oggetto' e' valorizzato
		if (raccoglitore.getOggetto() == null || "".equals(raccoglitore.getOggetto().trim())) {
			this.setErrorMessage("templateForm:racc_oggetto", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.object") + "'");
		    result = true;
		}
		else {
			// Controllo sulla presenza di caratteri non consentiti all'interno del campo oggetto
			if (raccoglitore.getOggetto().contains("|")) { // TODO gestire i caratteri non consentiti da file di properties
				this.setErrorMessage("templateForm:racc_oggetto", I18N.mrs("dw4.il_campo_oggetto_contiene_un_carattere_non_valido") + ": | ");
				result = true;
			}
		}
		
		return result;
	}
	
	@Override
	public XmlEntity getModel() {
		return this.raccoglitore;
	}
	
}
