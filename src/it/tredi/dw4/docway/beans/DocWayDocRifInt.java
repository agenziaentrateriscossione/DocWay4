package it.tredi.dw4.docway.beans;

import javax.faces.context.FacesContext;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.docway.adapters.DocWayRifIntFormsAdapter;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocWayDocRifInt extends DocWayDocedit {
	private DocWayRifIntFormsAdapter formsAdapter;
	private Doc doc;
	
	private boolean viewAddRPA;
	private boolean viewAddRPAM;
	private boolean viewAddOP;
	private boolean viewAddOPM;
	private boolean viewAddCC;
	private boolean viewAddCDS;
	
	private Object showdoc;
	private String action;
	
	private boolean fromShowTitles = false; // identifica se la richiesta di assegnazione e' su una selezione di titoli o su visualizzazione (showdoc)
	private boolean trasferisciMinuta = false; // utilizzato per trasferimento RPA da lista doc. Vale true se occorre aggiornare la minuta (RPAM)
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public Doc getDoc() {
		return doc;
	}
	
	public boolean isFromShowTitles() {
		return fromShowTitles;
	}

	public void setFromShowTitles(boolean fromShowTitles) {
		this.fromShowTitles = fromShowTitles;
	}
	
	public boolean isTrasferisciMinuta() {
		return trasferisciMinuta;
	}

	public void setTrasferisciMinuta(boolean trasferisciMinuta) {
		this.trasferisciMinuta = trasferisciMinuta;
	}

	public DocWayDocRifInt() throws Exception {
		this.formsAdapter = new DocWayRifIntFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
    	xml = dom.asXML();
		doc = new Doc();
		doc.init(dom);
		
		// caricamento di eventuali preferenze dell'applicazione (es. lunghezza min. del campo oggetto)
		setAppStringPreferences(XMLUtil.parseStrictAttribute(dom, "/response/@appStringPreferences"));
		
		// stato iniziale del checkbox "Invio Email di notifica"
		String toDo = formsAdapter.getDefaultForm().getParam("toDo");
		if (toDo != null && toDo.equals("IS_BOZZA")) {
			doc.setSendMailRifInterni(false);
		}
		else {
			String prefCheckboxMail = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("CheckboxEmailNotifica"));
			if (prefCheckboxMail != null && prefCheckboxMail.toLowerCase().equals("no"))
				doc.setSendMailRifInterni(false);
		}
    }
	
	/**
	 * imposta sul doc del bean le informazioni minimali necessarie al lookup (utilizzato
	 * da showdoc)
	 * N.B. Non poteva essere assegnato direttamente l'intero doc
	 * 
	 * @param basedoc
	 */
	public void setDataForLookupFromDoc(Doc basedoc) {
		if (basedoc != null) {
			doc.getRepertorio().setCod(basedoc.getRepertorio().getCod());
			doc.setTipo(basedoc.getTipo());
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

	public void setViewAddOP(boolean viewAddOP) {
		this.viewAddOP = viewAddOP;
	}

	public boolean isViewAddOP() {
		return viewAddOP;
	}

	public void setViewAddCC(boolean viewAddCC) {
		this.viewAddCC = viewAddCC;
	}

	public boolean isViewAddCC() {
		return viewAddCC;
	}
	
	public String closeAddRPA() throws Exception{
		viewAddRPA = false;
		setSessionAttribute("rifInt", null);
		return null;
	}

	public String closeAddRPAM() throws Exception{
		viewAddRPAM = false;
		setSessionAttribute("rifInt", null);
		return null;
	}

	public String closeAddOP() throws Exception{
		viewAddOP = false;
		setSessionAttribute("rifInt", null);
		return null;
	}
	
	public String closeAddOPM() throws Exception{
		viewAddOPM = false;
		setSessionAttribute("rifInt", null);
		return null;
	}

	public String closeAddCC() throws Exception{
		viewAddCC = false;
		setSessionAttribute("rifInt", null);
		return null;
	}
	
	public String closeAddCDS() throws Exception{
		viewAddCDS = false;
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
		if (!this.formsAdapter.getDefaultForm().getParam("dbTable").equals("@raccoglitore") && trasferisciMinuta)
			this.formsAdapter.confirmRifInt(doc.isSendMailRifInterni(), false, doc.getAssegnazioneRPAParam(), false, trasferisciMinuta);
		else
			this.formsAdapter.confirmRifInt(doc.isSendMailRifInterni(), false, doc.getAssegnazioneRPAParam());
		
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
	
	/**
	 * Conferma di aggiunta di RPAM sia da visualizzazione che da lista
	 * titoli (con caricamento della loadingbar)
	 * 
	 * @throws Exception
	 */
	public String confirmAddRPAM() throws Exception{
		this.formsAdapter.confirmRifInt(doc.isSendMailRifInterni(), false, doc.getAssegnazioneRPAMParam());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		viewAddRPAM = false;
		try {
			java.lang.reflect.Method method = showdoc.getClass().getMethod("reload");
			method.invoke(showdoc);
		} catch (SecurityException e) {
		  e.fillInStackTrace();
		} catch (NoSuchMethodException e) {
			e.fillInStackTrace();
		}
		setSessionAttribute("rifInt", null);
		return null;
	}
	
	/**
	 * Conferma di aggiunta di OP da visualizzazione di un doc
	 * 
	 * @throws Exception
	 */
	public String confirmAddOP() throws Exception{
		this.formsAdapter.confirmRifInt(doc.isSendMailRifInterni(), false, doc.getAssegnazioneOPParam());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		viewAddOP = false;
		try {
			java.lang.reflect.Method method = showdoc.getClass().getMethod("reload");
			method.invoke(showdoc);
		} catch (SecurityException e) {
		  e.fillInStackTrace();
		} catch (NoSuchMethodException e) {
			e.fillInStackTrace();
		}
		setSessionAttribute("rifInt", null);
		return null;
	}
	
	/**
	 * Conferma di aggiunta di OPM da visualizzazione di un doc
	 * 
	 * @throws Exception
	 */
	public String confirmAddOPM() throws Exception{
		this.formsAdapter.confirmRifInt(doc.isSendMailRifInterni(), false, doc.getAssegnazioneOPMParam());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		viewAddOPM = false;
		try {
			java.lang.reflect.Method method = showdoc.getClass().getMethod("reload");
			method.invoke(showdoc);
		} catch (SecurityException e) {
		  e.fillInStackTrace();
		} catch (NoSuchMethodException e) {
			e.fillInStackTrace();
		}
		setSessionAttribute("rifInt", null);
		return null;
	}
	
	/**
	 * Conferma di aggiunta di CC sia da visualizzazione che da lista
	 * titoli (con caricamento della loadingbar)
	 * 
	 * @throws Exception
	 */
	public String confirmAddCC() throws Exception{
		this.formsAdapter.confirmRifInt(doc.isSendMailRifInterni(), false, doc.getAssegnazioneCCParam(), true);
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
			action = "assegnazioneCC";
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
	 * Conferma di aggiunta di CDS da visualizzazione di un doc
	 * 
	 * @throws Exception
	 */
	public String confirmAddCDS() throws Exception{
		this.formsAdapter.confirmRifInt(doc.isSendMailRifInterni(), false, doc.getAssegnazioneCDSParam());
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		viewAddCDS= false;
		try {
			java.lang.reflect.Method method = showdoc.getClass().getMethod("reload");
			method.invoke(showdoc);
		} catch (SecurityException e) {
		  e.fillInStackTrace();
		} catch (NoSuchMethodException e) {
			e.fillInStackTrace();
		}
		setSessionAttribute("rifInt", null);
		return null;
	}

	/**
	 * RifintLookup su ufficio RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_rpa() throws Exception {
		String value 	= (doc.getAssegnazioneRPA() != null && doc.getAssegnazioneRPA().getNome_uff() != null) ? doc.getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (doc.getAssegnazioneRPA() != null && doc.getAssegnazioneRPA().getNome_persona() != null) ? doc.getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";
		
		rifintLookupUfficio(doc, value, value2, campi);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_rpa() throws Exception {
		String value = (doc.getAssegnazioneRPA() != null && doc.getAssegnazioneRPA().getNome_uff() != null) ? doc.getAssegnazioneRPA().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_uff" +
							"|.assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_uff" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneRPA().setUfficio_completo(false);
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
		String value 	= (doc.getAssegnazioneRPA() != null && doc.getAssegnazioneRPA().getNome_uff() != null) ? doc.getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (doc.getAssegnazioneRPA() != null && doc.getAssegnazioneRPA().getNome_persona() != null) ? doc.getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";
		
		String xq		= "";
		if (doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + doc.getRepertorio().getCod() + "-" + doc.getTipoShort() + "-VisRep\")";
		
		rifintLookupPersona(doc, value, value2, campi, xq);
				
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona RPA
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_rpa() throws Exception {
		String value = (doc.getAssegnazioneRPA() != null && doc.getAssegnazioneRPA().getNome_persona() != null) ? doc.getAssegnazioneRPA().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneRPA().setUfficio_completo(false);
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
		String value = (doc.getAssegnazioneRPA() != null && doc.getAssegnazioneRPA().getNome_uff() != null) ? doc.getAssegnazioneRPA().getNome_uff() : "";
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi 		= ".assegnazioneRPA.@nome_uff=xml,/ruolo/nome"; // TODO Da completare 
		
		lookupRuolo(doc, value, campi, doc.getCod_amm_aoo());
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
		
		return clearField(campi, doc);
	}
	
	/**
	 * RifintLookup su ufficio RPAM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_rpam() throws Exception {
		String value 	= (doc.getAssegnazioneRPAM() != null && doc.getAssegnazioneRPAM().getNome_uff() != null) ? doc.getAssegnazioneRPAM().getNome_uff() : "";
		String value2 	= (doc.getAssegnazioneRPAM() != null && doc.getAssegnazioneRPAM().getNome_persona() != null) ? doc.getAssegnazioneRPAM().getNome_persona() : "";
		String campi 	= ".assegnazioneRPAM.@nome_uff" +
								"|.assegnazioneRPAM.@nome_persona" +
								"|.assegnazioneRPAM.@cod_uff" +
								"|.assegnazioneRPAM.@cod_persona";
		
		rifintLookupUfficio(doc, value, value2, campi);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio RPAM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_rpam() throws Exception {
		String value = (doc.getAssegnazioneRPAM() != null && doc.getAssegnazioneRPAM().getNome_uff() != null) ? doc.getAssegnazioneRPAM().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPAM.@nome_uff" +
							"|.assegnazioneRPAM.@nome_persona" +
							"|.assegnazioneRPAM.@cod_uff" +
							"|.assegnazioneRPAM.@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneRPA().setUfficio_completo(false);
		}
		
		return null;
	}
	
	/**
	 * RifintLookup su persona RPAM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_rpam() throws Exception {
		String value 	= (doc.getAssegnazioneRPAM() != null && doc.getAssegnazioneRPAM().getNome_uff() != null) ? doc.getAssegnazioneRPAM().getNome_uff() : "";
		String value2 	= (doc.getAssegnazioneRPAM() != null && doc.getAssegnazioneRPAM().getNome_persona() != null) ? doc.getAssegnazioneRPAM().getNome_persona() : "";
		String campi 	= ".assegnazioneRPAM.@nome_uff" +
								"|.assegnazioneRPAM.@nome_persona" +
								"|.assegnazioneRPAM.@cod_uff" +
								"|.assegnazioneRPAM.@cod_persona";
		
		String xq		= "";
		if (doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + doc.getRepertorio().getCod() + "-" + doc.getTipoShort() + "-VisRep\")";
		
		rifintLookupPersona(doc, value, value2, campi, xq);
				
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona RPAM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_rpam() throws Exception {
		String value = (doc.getAssegnazioneRPAM() != null && doc.getAssegnazioneRPAM().getNome_persona() != null) ? doc.getAssegnazioneRPAM().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPAM.@nome_persona" +
							"|.assegnazioneRPAM.@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneRPA().setUfficio_completo(false);
		}
		
		return null;
	}
	
	/**
	 * Lookup su ruolo RPAM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_rpam() throws Exception {
		String value = (doc.getAssegnazioneRPAM() != null && doc.getAssegnazioneRPAM().getNome_uff() != null) ? doc.getAssegnazioneRPAM().getNome_uff() : "";
		
		String campi = ".assegnazioneRPAM.@nome_uff=xml,/ruolo/nome"; 
		
		lookupRuolo(doc, value, campi, doc.getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo RPAM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_rpam() throws Exception {
		String campi = ".assegnazioneRPAM.@nome_uff=xml,/ruolo/nome"; 
		
		return clearField(campi, doc);
	}
	
	/**
	 * RifintLookup su ufficio OP
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_op() throws Exception {
		String value 	= (doc.getAssegnazioneOP() != null && doc.getAssegnazioneOP().getNome_uff() != null) ? doc.getAssegnazioneOP().getNome_uff() : "";
		String value2 	= (doc.getAssegnazioneOP() != null && doc.getAssegnazioneOP().getNome_persona() != null) ? doc.getAssegnazioneOP().getNome_persona() : "";
		String campi 	= ".assegnazioneOP.@nome_uff" +
								"|.assegnazioneOP.@nome_persona" +
								"|.assegnazioneOP.@cod_uff" +
								"|.assegnazioneOP.@cod_persona";
		
		rifintLookupUfficio(doc, value, value2, campi);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio OP
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_op() throws Exception {
		String value = (doc.getAssegnazioneOP() != null && doc.getAssegnazioneOP().getNome_uff() != null) ? doc.getAssegnazioneOP().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneOP.@nome_uff" +
							"|.assegnazioneOP.@nome_persona" +
							"|.assegnazioneOP.@cod_uff" +
							"|.assegnazioneOP.@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneOP().setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * RifintLookup su persona OP
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_op() throws Exception {
		String value 	= (doc.getAssegnazioneOP() != null && doc.getAssegnazioneOP().getNome_uff() != null) ? doc.getAssegnazioneOP().getNome_uff() : "";
		String value2 	= (doc.getAssegnazioneOP() != null && doc.getAssegnazioneOP().getNome_persona() != null) ? doc.getAssegnazioneOP().getNome_persona() : "";
		String campi 	= ".assegnazioneOP.@nome_uff" +
								"|.assegnazioneOP.@nome_persona" +
								"|.assegnazioneOP.@cod_uff" +
								"|.assegnazioneOP.@cod_persona";
		
		String xq		= "";
		if (doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + doc.getRepertorio().getCod() + "-" + doc.getTipoShort() + "-VisRep\")";
		
		rifintLookupPersona(doc, value, value2, campi, xq);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona OP
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_op() throws Exception {
		String value = (doc.getAssegnazioneOP() != null && doc.getAssegnazioneOP().getNome_persona() != null) ? doc.getAssegnazioneOP().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneOP.@nome_persona" +
							"|.assegnazioneOP.@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneOP().setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * Lookup su ruolo OP
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_op() throws Exception {
		String value = (doc.getAssegnazioneOP() != null && doc.getAssegnazioneOP().getNome_uff() != null) ? doc.getAssegnazioneOP().getNome_uff() : "";
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneOP.@nome_uff=xml,/ruolo/nome"; 

		lookupRuolo(doc, value, campi, doc.getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo OP
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_op() throws Exception {
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneOP.@nome_uff=xml,/ruolo/nome"; 
		
		return clearField(campi, doc);
	}

	/**
	 * RifintLookup su ufficio OPM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_opm() throws Exception {
		String value 	= (doc.getAssegnazioneOPM() != null && doc.getAssegnazioneOPM().getNome_uff() != null) ? doc.getAssegnazioneOPM().getNome_uff() : "";
		String value2 	= (doc.getAssegnazioneOPM() != null && doc.getAssegnazioneOPM().getNome_persona() != null) ? doc.getAssegnazioneOPM().getNome_persona() : "";
		String campi 	= ".assegnazioneOPM.@nome_uff" +
								"|.assegnazioneOPM.@nome_persona" +
								"|.assegnazioneOPM.@cod_uff" +
								"|.assegnazioneOPM.@cod_persona";
		
		rifintLookupUfficio(doc, value, value2, campi);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio OPM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_opm() throws Exception {
		String value = (doc.getAssegnazioneOPM() != null && doc.getAssegnazioneOPM().getNome_uff() != null) ? doc.getAssegnazioneOPM().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneOPM.@nome_uff" +
							"|.assegnazioneOPM.@nome_persona" +
							"|.assegnazioneOPM.@cod_uff" +
							"|.assegnazioneOPM.@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneOPM().setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * RifintLookup su persona OPM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_opm() throws Exception {
		String value 	= (doc.getAssegnazioneOPM() != null && doc.getAssegnazioneOPM().getNome_uff() != null) ? doc.getAssegnazioneOPM().getNome_uff() : "";
		String value2 	= (doc.getAssegnazioneOPM() != null && doc.getAssegnazioneOPM().getNome_persona() != null) ? doc.getAssegnazioneOPM().getNome_persona() : "";
		String campi 	= ".assegnazioneOPM.@nome_uff" +
								"|.assegnazioneOPM.@nome_persona" +
								"|.assegnazioneOPM.@cod_uff" +
								"|.assegnazioneOPM.@cod_persona";
		
		String xq		= "";
		if (doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + doc.getRepertorio().getCod() + "-" + doc.getTipoShort() + "-VisRep\")";
		
		rifintLookupPersona(doc, value, value2, campi, xq);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona OPM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_opm() throws Exception {
		String value = (doc.getAssegnazioneOPM() != null && doc.getAssegnazioneOPM().getNome_persona() != null) ? doc.getAssegnazioneOPM().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneOPM.@nome_persona" +
							"|.assegnazioneOPM.@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneOPM().setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * Lookup su ruolo OPM
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_opm() throws Exception {
		String value = (doc.getAssegnazioneOPM() != null && doc.getAssegnazioneOPM().getNome_uff() != null) ? doc.getAssegnazioneOPM().getNome_uff() : "";
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneOPM.@nome_uff=xml,/ruolo/nome"; 

		lookupRuolo(doc, value, campi, doc.getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo OPN
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_opm() throws Exception {
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneOPM.@nome_uff=xml,/ruolo/nome";
		
		return clearField(campi, doc);
	}
	
	/**
	 * RifintLookup su ufficio CC
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (doc.getAssegnazioneCC().contains(rif)) ? doc.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		String value2 = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";
		
		rifintLookupUfficio(doc, value, value2, campi);
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
		int num = (doc.getAssegnazioneCC().contains(rif)) ? doc.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		
		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_uff" +
							"|.assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_uff" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneCC().get(num).setUfficio_completo(false);
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
		int num = (doc.getAssegnazioneCC().contains(rif)) ? doc.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		String value2 = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";
		
		String xq		= "";
		if (doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + doc.getRepertorio().getCod() + "-" + doc.getTipoShort() + "-VisRep\")";
		
		rifintLookupPersona(doc, value, value2, campi, xq);
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
		int num = (doc.getAssegnazioneCC().contains(rif)) ? doc.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneCC().get(num).setUfficio_completo(false);
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
		int num = (doc.getAssegnazioneCC().contains(rif)) ? doc.getAssegnazioneCC().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare 

		lookupRuolo(doc, value, campi, doc.getCod_amm_aoo());
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
		int num = (doc.getAssegnazioneCC().contains(rif)) ? doc.getAssegnazioneCC().indexOf(rif): 0;
		
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare 
		return clearField(campi, doc);
	}
	
	/**
	 * RifintLookup su ufficio CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (doc.getAssegnazioneCDS().contains(rif)) ? doc.getAssegnazioneCDS().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		String value2 = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";
		String campi 	= ".assegnazioneCDS["+num+"].@nome_uff" +
								"|.assegnazioneCDS["+num+"].@nome_persona" +
								"|.assegnazioneCDS["+num+"].@cod_uff" +
								"|.assegnazioneCDS["+num+"].@cod_persona";
		
		rifintLookupUfficio(doc, value, value2, campi);
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su ufficio CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_cds() throws Exception {
		
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (doc.getAssegnazioneCDS().contains(rif)) ? doc.getAssegnazioneCDS().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		
		if (value.length() == 0) {
			String campi = ".assegnazioneCDS["+num+"].@nome_uff" +
							"|.assegnazioneCDS["+num+"].@nome_persona" +
							"|.assegnazioneCDS["+num+"].@cod_uff" +
							"|.assegnazioneCDS["+num+"].@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneCDS().get(num).setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * RifintLookup su persona CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (doc.getAssegnazioneCDS().contains(rif)) ? doc.getAssegnazioneCDS().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";
		String value2 = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";
		String campi 	= ".assegnazioneCDS["+num+"].@nome_uff" +
								"|.assegnazioneCDS["+num+"].@nome_persona" +
								"|.assegnazioneCDS["+num+"].@cod_uff" +
								"|.assegnazioneCDS["+num+"].@cod_persona";
		
		String xq		= "";
		if (doc.getRepertorio().getCod() != null && doc.getRepertorio().getCod().length() > 0)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + doc.getRepertorio().getCod() + "-" + doc.getTipoShort() + "-VisRep\")";
		
		rifintLookupPersona(doc, value, value2, campi, xq);
		rif.setUfficio_completo(false);
		
		return null;
	}
	
	/**
	 * Pulizia dei campi di RifintLookup su persona CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (doc.getAssegnazioneCDS().contains(rif)) ? doc.getAssegnazioneCDS().indexOf(rif): 0;
		
		String value = (null != rif.getNome_persona()) ? rif.getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCDS["+num+"].@nome_persona" +
							"|.assegnazioneCDS["+num+"].@cod_persona";
			clearFieldRifint(campi, doc);
			doc.getAssegnazioneCDS().get(num).setUfficio_completo(false);
		}
		return null;
	}
	
	/**
	 * Lookup su ruolo CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (doc.getAssegnazioneCDS().contains(rif)) ? doc.getAssegnazioneCDS().indexOf(rif): 0;
		
		String value = (null != rif.getNome_uff()) ? rif.getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCDS["+num+"].@nome_uff=xml,/ruolo/nome";

		lookupRuolo(doc, value, campi, doc.getCod_amm_aoo());
		return null;
	}
	
	/**
	 * Pulizia dei campi di lookup su ruolo CDS
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (doc.getAssegnazioneCDS().contains(rif)) ? doc.getAssegnazioneCDS().indexOf(rif): 0;
		
		
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCDS["+num+"].@nome_uff=xml,/ruolo/nome";
		return clearField(campi, doc);
	}
	
	/**
	 * Cancellazione di un rif int in CC
	 * @return
	 */
	public String deleteRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		doc.deleteRifintCC(rif);
		return null;
	}

	/**
	 * Aggiunta di un rif int in CC
	 * @return
	 */
	public String addRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		doc.addRifintCC(rif);
		return null;
	}

	/**
	 * Cancellazione di un rif int in CDS
	 * @return
	 */
	public String deleteRifInt_cds(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		doc.deleteRifintCDS(rif);
		return null;
	}

	/**
	 * Aggiunta di un rif int in CDS
	 * @return
	 */
	public String addRifInt_cds(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		doc.addRifintCDS(rif);
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

	public void setViewAddCDS(boolean viewAddCDS) {
		this.viewAddCDS = viewAddCDS;
	}

	public boolean isViewAddCDS() {
		return viewAddCDS;
	}

	public void setViewAddRPAM(boolean viewAddRPAM) {
		this.viewAddRPAM = viewAddRPAM;
	}

	public boolean isViewAddRPAM() {
		return viewAddRPAM;
	}

	public void setViewAddOPM(boolean viewAddOPM) {
		this.viewAddOPM = viewAddOPM;
	}

	public boolean isViewAddOPM() {
		return viewAddOPM;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

}
