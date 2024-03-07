package it.tredi.dw4.docway.beans;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.adapters.DocWayRifIntFormsAdapter;
import it.tredi.dw4.docway.model.Fascicolo;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocWayFascRifInt extends DocWayDocedit {
	private DocWayRifIntFormsAdapter formsAdapter;
	private Fascicolo fascicolo;
	private boolean viewAddRPA;
	private boolean viewAddITF;
	private boolean viewAddCC;
	private Object showdoc;
	private String action;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setFascicolo(Fascicolo doc) {
		this.fascicolo = doc;
	}

	public Fascicolo getFascicolo() {
		return fascicolo;
	}

	public DocWayFascRifInt() throws Exception {
		this.formsAdapter = new DocWayRifIntFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
    	xml = dom.asXML();
		fascicolo = new Fascicolo();
		fascicolo.init(dom);

		// caricamento di eventuali preferenze dell'applicazione (es. lunghezza min. del campo oggetto)
		setAppStringPreferences(XMLUtil.parseStrictAttribute(dom, "/response/@appStringPreferences"));
		
		// stato iniziale del checkbox "Invio Email di notifica"
		String prefCheckboxMail = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("CheckboxEmailNotifica"));
		if (prefCheckboxMail != null && prefCheckboxMail.toLowerCase().equals("no"))
			fascicolo.setSendMailRifInterni(false);
		// ERM012596 - rtirabassi - notifica capillare
		String prefCheckboxMailDetail = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("CheckboxEmailNotificaCapillare"));
		if (prefCheckboxMailDetail != null && prefCheckboxMailDetail.toLowerCase().equals("si")) {
			fascicolo.setSendMailRifInterni(false);
			fascicolo.setSendMailSelectedRifInterni(true);
		}
	}	
	
	public DocWayRifIntFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		return null;			
	}

	@Override
	public String clearDocument() throws Exception {
		return null;
	}
	
	public void setShowdoc(Object showdoc) {
		this.showdoc = showdoc;
	}

	public Object getShowdoc() {
		return showdoc;
	}

	public void setViewAddRPA(boolean viewAddRPA) {
		this.viewAddRPA = viewAddRPA;
	}

	public boolean isViewAddRPA() {
		return viewAddRPA;
	}

	public void setViewAddITF(boolean viewAddOP) {
		this.viewAddITF = viewAddOP;
	}

	public boolean isViewAddITF() {
		return viewAddITF;
	}

	public void setViewAddCC(boolean viewAddCC) {
		this.viewAddCC = viewAddCC;
	}

	public boolean isViewAddCC() {
		return viewAddCC;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}
	
	public String closeAddRPA() throws Exception{
		viewAddRPA = false;
		setSessionAttribute("rifInt", null);
		return null;
	}

	public String closeAddITF() throws Exception{
		viewAddITF = false;
		setSessionAttribute("rifInt", null);
		return null;
	}
	
	public String closeAddCC() throws Exception{
		viewAddCC = false;
		setSessionAttribute("rifInt", null);
		return null;
	}
	
	/**
	 * Conferma di aggiunta di RPA sia da visualizzazione che da lista
	 * titoli (con caricamento della loadingbar)
	 * 
	 * @throws Exception
	 */
	public String confirmAddRPA() throws Exception {
		// il cambio di RPA sui fascicoli viene eseguito sempre come TRASF // TODO da verificare
		this.formsAdapter.confirmRifInt(fascicolo.isSendMailRifInterni(), fascicolo.isSendMailSelectedRifInterni(), fascicolo.getNotificheCapillariParam(), fascicolo.isCheckNomi(), fascicolo.getAssegnazioneTRASFParam());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		viewAddRPA = false;
		String verbo = response.getAttributeValue("/response/@verbo");
		if (verbo.equals("loadingbar")) {
			DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
			docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
			docWayLoadingbar.init(response);
			setLoadingbar(docWayLoadingbar);
			docWayLoadingbar.setActive(true);
			action = "assegnazioneRPA";
		}
		else{
			try {
				java.lang.reflect.Method method = showdoc.getClass().getMethod("reload");
				method.invoke(showdoc);
			} catch (SecurityException e) {
				e.fillInStackTrace();
			} catch (NoSuchMethodException e) {
				e.fillInStackTrace();
			}
			setSessionAttribute("rifInt", null);
		}
		return null;
	}
	
	public String confirmAddITF() throws Exception {
		// su DocWay3 checkNomi viene passato solo in caso di RPA // TODO è corretto
		this.formsAdapter.confirmRifInt(fascicolo.isSendMailRifInterni(), fascicolo.isSendMailSelectedRifInterni(), fascicolo.getNotificheCapillariParam(), false, fascicolo.getAssegnazioneITFParam());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		viewAddITF = false;
		try {
			java.lang.reflect.Method method = showdoc.getClass().getMethod("reload");
			method.invoke(showdoc);
		} catch (SecurityException e) {
		  e.fillInStackTrace();
		} catch (NoSuchMethodException e) {
			e.fillInStackTrace();
		}
		setSessionAttribute("rifIntFasc", null);
		return null;
	}
	
	public String confirmAddCC() throws Exception {
		// mbernardini 09/02/2015 : aggiunta loadingbar in aggiunta CC a fascicolo
		// verra' attivato il processo di aggiornamento dei CC di tutti i sottofascicoli e documenti
		// contenuti
		this.formsAdapter.confirmCConFascicolo(fascicolo.isSendMailRifInterni(), fascicolo.isSendMailSelectedRifInterni(), fascicolo.getNotificheCapillariParam(), fascicolo.getAssegnazioneCCParam());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		viewAddCC = false;
		String verbo = response.getAttributeValue("/response/@verbo");
		if (verbo.equals("loadingbar")) {
			DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
			docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
			docWayLoadingbar.init(response);
			setLoadingbar(docWayLoadingbar);
			docWayLoadingbar.setActive(true);
			action = "assegnazioneCCfasc";
		}
		else{
			try {
				java.lang.reflect.Method method = showdoc.getClass().getMethod("reload");
				method.invoke(showdoc);
			} catch (SecurityException e) {
				e.fillInStackTrace();
			} catch (NoSuchMethodException e) {
				e.fillInStackTrace();
			}
			setSessionAttribute("rifInt", null);
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
		String value 	= (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_uff() != null) ? fascicolo.getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_persona() != null) ? fascicolo.getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";
		
		rifintLookupUfficio(fascicolo, value, value2, campi);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_rpa() throws Exception {
		String value = (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_uff() != null) ? fascicolo.getAssegnazioneRPA().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_uff" +
							"|.assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_uff" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneRPA().setUfficio_completo(false);
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
		String value 	= (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_uff() != null) ? fascicolo.getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_persona() != null) ? fascicolo.getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";
		
		rifintLookupPersona(fascicolo, value, value2, campi);
				
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_rpa() throws Exception {
		String value = (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_persona() != null) ? fascicolo.getAssegnazioneRPA().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneRPA().setUfficio_completo(false);
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
		String value = (fascicolo.getAssegnazioneRPA() != null && fascicolo.getAssegnazioneRPA().getNome_uff() != null) ? fascicolo.getAssegnazioneRPA().getNome_uff() : "";
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi 		= ".assegnazioneRPA.@nome_uff=xml,/ruolo/nome"; // TODO Da completare 
		
		lookupRuolo(fascicolo, value, campi, fascicolo.getCod_amm_aoo());
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
		
		return clearField(campi, fascicolo);
	}
	
	/**
	 * RifintLookup su ufficio ITF
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_itf() throws Exception {
		String value 	= (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_uff() != null) ? fascicolo.getAssegnazioneITF().getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_persona() != null) ? fascicolo.getAssegnazioneITF().getNome_persona() : "";
		String campi 	= ".assegnazioneITF.@nome_uff" +
								"|.assegnazioneITF.@nome_persona" +
								"|.assegnazioneITF.@cod_uff" +
								"|.assegnazioneITF.@cod_persona";
		
		rifintLookupUfficio(fascicolo, value, value2, campi);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio ITF
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_itf() throws Exception {
		String value = (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_uff() != null) ? fascicolo.getAssegnazioneITF().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneITF.@nome_uff" +
							"|.assegnazioneITF.@nome_persona" +
							"|.assegnazioneITF.@cod_uff" +
							"|.assegnazioneITF.@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneITF().setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * RifintLookup su persona ITF
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_itf() throws Exception {
		String value 	= (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_uff() != null) ? fascicolo.getAssegnazioneITF().getNome_uff() : "";
		String value2 	= (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_persona() != null) ? fascicolo.getAssegnazioneITF().getNome_persona() : "";
		String campi 	= ".assegnazioneITF.@nome_uff" +
								"|.assegnazioneITF.@nome_persona" +
								"|.assegnazioneITF.@cod_uff" +
								"|.assegnazioneITF.@cod_persona";
		
		rifintLookupPersona(fascicolo, value, value2, campi);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona ITF
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_itf() throws Exception {
		String value = (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_persona() != null) ? fascicolo.getAssegnazioneITF().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneITF.@nome_persona" +
							"|.assegnazioneITF.@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneITF().setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * Lookup su ruolo ITF
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_itf() throws Exception {
		String value = (fascicolo.getAssegnazioneITF() != null && fascicolo.getAssegnazioneITF().getNome_uff() != null) ? fascicolo.getAssegnazioneITF().getNome_uff() : "";
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneITF.@nome_uff=xml,/ruolo/nome"; 

		lookupRuolo(fascicolo, value, campi, fascicolo.getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo ITF
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_itf() throws Exception {
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneITF.@nome_uff=xml,/ruolo/nome"; 
		
		return clearField(campi, fascicolo);
	}

	
	/**
	 * RifintLookup su ufficio CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		String value2 = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";
		
		rifintLookupUfficio(fascicolo, value, value2, campi);
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
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		
		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_uff" +
							"|.assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_uff" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneCC().get(num).setUfficio_completo(false);
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
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		String value2 = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";
		
		rifintLookupPersona(fascicolo, value, value2, campi);
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
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, fascicolo);
			fascicolo.getAssegnazioneCC().get(num).setUfficio_completo(false);
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
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; 

		lookupRuolo(fascicolo, value, campi, fascicolo.getCod_amm_aoo());
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
		int num = (fascicolo.getAssegnazioneCC().contains(rif)) ? fascicolo.getAssegnazioneCC().indexOf(rif): 0;
		
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; 
		return clearField(campi, fascicolo);
	}
	
	/**
	 * Cancellazione di un rif int in CC
	 * @return
	 */
	public String deleteRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		fascicolo.deleteRifintCC(rif);
		return null;
	}

	/**
	 * Aggiunta di un rif int in CC
	 * @return
	 */
	public String addRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		fascicolo.addRifintCC(rif);
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
	
	@Override
	public XmlEntity getModel() {
		return null;
	}

}
