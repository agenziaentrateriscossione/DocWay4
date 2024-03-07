package it.tredi.dw4.docway.beans;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.adapters.DocWayRifIntFormsAdapter;
import it.tredi.dw4.docway.model.Raccoglitore;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocWayRaccRifInt extends DocWayDocedit {
	private DocWayRifIntFormsAdapter formsAdapter;
	private Raccoglitore raccoglitore;
	
	private boolean viewAddRPA;
	private boolean viewAddCC;
	private Object showdoc;
	private String action;
	
	private boolean fromShowTitles = false; // identifica se la richiesta di assegnazione e' su una selezione di titoli o su visualizzazione (showdoc)
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public boolean isFromShowTitles() {
		return fromShowTitles;
	}

	public void setFromShowTitles(boolean fromShowTitles) {
		this.fromShowTitles = fromShowTitles;
	}

	public void setRaccoglitore(Raccoglitore doc) {
		this.raccoglitore = doc;
	}

	public Raccoglitore getRaccoglitore() {
		return raccoglitore;
	}

	public DocWayRaccRifInt() throws Exception {
		this.formsAdapter = new DocWayRifIntFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
    	xml = dom.asXML();
    	raccoglitore = new Raccoglitore();
    	raccoglitore.init(dom);

		// caricamento di eventuali preferenze dell'applicazione (es. lunghezza min. del campo oggetto)
		setAppStringPreferences(XMLUtil.parseStrictAttribute(dom, "/response/@appStringPreferences"));
		
		// stato iniziale del checkbox "Invio Email di notifica"
		String prefCheckboxMail = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("CheckboxEmailNotifica"));
		if (prefCheckboxMail != null && prefCheckboxMail.toLowerCase().equals("no"))
			raccoglitore.setSendMailRifInterni(false);
		// ERM012596 - rtirabassi - notifica capillare
		String prefCheckboxMailDetail = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("CheckboxEmailNotificaCapillare"));
		if (prefCheckboxMailDetail != null && prefCheckboxMailDetail.toLowerCase().equals("si")) {
			raccoglitore.setSendMailRifInterni(false);
			raccoglitore.setSendMailSelectedRifInterni(true);
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
	
	public String closeAddCC() throws Exception{
		viewAddCC = false;
		setSessionAttribute("rifInt", null);
		return null;
	}
	
	public String confirmAddRPA() throws Exception {
		this.formsAdapter.confirmRifInt(raccoglitore.isSendMailRifInterni(), raccoglitore.isSendMailSelectedRifInterni(), raccoglitore.getNotificheCapillariParam(), raccoglitore.isCheckNomi(), raccoglitore.getAssegnazioneRPAParam());
		
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
			// mbernardini 13/10/2016 : set del bean della pagina per correggere il ritorno dalla loadingbar in caso di raccoglitori di tipo indice
			docWayLoadingbar.setHolderPageBean((Page) showdoc);
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
	
	public String confirmAddCC() throws Exception {
		// mbernardini 09/02/2015 : aggiunta loadingbar in aggiunta CC a raccoglitore
		// verra' attivato il processo di aggiornamento dei CC di tutti i sottofascicoli e documenti
		// contenuti
		this.formsAdapter.confirmCConRaccoglitore(raccoglitore.isSendMailRifInterni(), raccoglitore.isSendMailSelectedRifInterni(), raccoglitore.getNotificheCapillariParam(), raccoglitore.getAssegnazioneCCParam());
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
			// mbernardini 13/10/2016 : set del bean della pagina per correggere il ritorno dalla loadingbar in caso di raccoglitori di tipo indice
			docWayLoadingbar.setHolderPageBean((Page) showdoc);
			setLoadingbar(docWayLoadingbar);
			docWayLoadingbar.setActive(true);
			action = "assegnazioneCCracc";
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
		
		String xq		= "";
		
		rifintLookupPersona(raccoglitore, value, value2, campi, xq);
				
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
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		String value2 = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";
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
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		
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
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		String value2 = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";
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
		
		String value = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";

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
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; 

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
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; 
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
	
	@Override
	public XmlEntity getModel() {
		return null;
	}

}
