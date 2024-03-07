package it.tredi.dw4.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.addons.BaseAddOn;
import it.tredi.dw4.docway.DocwayInit;
import it.tredi.dw4.docway.beans.DocEditDoc;
import it.tredi.dw4.docway.utils.MultiSocietaMap;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.ClientConfig;
import it.tredi.dw4.model.azionimassive.AzioniMassive;
import it.tredi.dw4.model.customfields.CustomFields;
import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DisattivazioneStampeSettings;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public abstract class Page {

	// Diritti su restrizioni ad anagrafica interna/esterna della
	// propria AOO
	public final String DIRITTO_INT_AOO_RESTRICTION = "acl_int_aoo_restriction";
	public final String DIRITTO_EXT_AOO_RESTRICTION = "acl_ext_aoo_restriction";

	public abstract FormsAdapter getFormsAdapter();

	protected HttpServletRequest request = null;
	protected HttpServletResponse response = null;

	private Lookup lookup; //lookup page bean
	private CustomFieldsLookup customfieldsLookup; // lookup su campi custom
	private RifintLookup rifintLookup; //rifintLookup page bean
	private Thvincolato thvincolato; //thesauro vincolato page bean
	private Showindex showindex; //vocabolario page bean
	private Showthes showthes; //thesauro di classificazione
	private Loadingbar loadingbar; //loadingbar page bean
	private LoadingbarStampe loadingbarstampe; //loadingbar stampe page bean

	private String errorFieldIds = ""; //utilizzato per la marcatura dei campi obbligatori (o con altri errori di validazione) interni a ui:repeat

	private String requiredVersionIWX = DocWayProperties.readProperty("requiredVersionIWX", ""); // versione richiesta di IWX
	private String currentVersionIWX = DocWayProperties.readProperty("currentVersionIWX", ""); // versione corrente di IWX

	private String customDirCss = DocWayProperties.readProperty("docway.custom.dir.css", ""); // eventuale CSS custom per DocWay

	private String clientIdentifier = "";

	protected boolean debugMode = false; // modalita' debug dell'interfaccia (textarea contenente l'xml restituito dal service)

	private List<BaseAddOn> aspects = new ArrayList<BaseAddOn>();
	private List<BaseAddOn> buttons = new ArrayList<BaseAddOn>();

	private CustomFields customfields = new CustomFields(); // gestione dei campi custom (definibiti dall'utente in base a definizione XML)

	//variabili per gestione dei tipi file in iwx e swfuploader
	protected String docInformaticiFileDescription;
	protected String docInformaticiFileTypes;
	protected String docInformaticiArchiveTypes;
	protected String iwxFileTypes;
	protected Map<String, List<String>> fileFilters;
	protected String docInformaticiImageDescription;
	protected String docInformaticiImageTypes;
	protected String iwxImageTypes;
	protected Map<String, List<String>> imageFilters;

	public static final String docInformaticiFileDescriptionDefault = I18N.mrs("dw4.all_files");
	public static final String docInformaticiFileTypesDefault = "*.*";
	public static final String docInformaticiArchiveTypesDefault = "*.zip;";
	public static final String docInformaticiImageTypesDescriptionDefault = I18N.mrs("dw4.image_files");
	public static final String docInformaticiImageTypesDefault = "*.tif; *.tiff; *.jpg; *.jpeg; *.bmp; *.png";
	public static final String iwxFileTypesDefault = I18N.mrs("dw4.all_files") + " (*.*),*.*";
	public static final String iwxImageTypesDefault = I18N.mrs("dw4.image_files") + " (TIFF;JPEG;BITMAP;PNG),*.tif;*.tiff;*.jpg;*.jpeg;*.bmp;*.png,TIFF (*.tif;*.tiff),*.tif;*.tiff,JPEG (*.jpg;*.jpeg),*.jpg;*.jpeg,BMP (*.bmp),*.bmp,PNG (*.png),*.png";
	
	// Informazioni relative ad azioni massive (stored procedure LUA) disponibili da lista titoli
	private AzioniMassive azioniMassive;
	

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void injectRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public Loadingbar getLoadingbar() {
		return loadingbar;
	}

	public void setLoadingbar(Loadingbar loadingbar) {
		this.loadingbar = loadingbar;
	}

	public LoadingbarStampe getLoadingbarstampe() {
		return loadingbarstampe;
	}

	public void setLoadingbarstampe(LoadingbarStampe loadingbarstampe) {
		this.loadingbarstampe = loadingbarstampe;
	}

	/**
	 * applicazione caricata in debug mode?
	 * @return
	 */
	public boolean isDebugMode() {
		boolean debugFromSession = false;
		try {
			String debug = (String) getSessionAttribute("debugMode");
			if (debug != null && debug.equalsIgnoreCase("true"))
				debugFromSession = true;
		}
		catch (Exception e) { }

		if (DocWayProperties.readProperty("debugMode", "no").equalsIgnoreCase("si") || debugFromSession)
			return true;
		else
			return false;
	}

	/**
	 * ritorna l'applicazione caricata attualmente (gestione applicazioni embedded interne a docway)
	 * @return
	 */
	public String getEmbeddedApp() {
		String app = (String) getSessionAttribute(Const.DOCWAY_EMBEDDED_APP_NAME);
		if (app == null)
			app = "";

		return app;
	}

	private boolean popupPage = false; // Definisce se la pagina e' visualizzata o meno a popup
	private boolean showSxCol = true; // Definisce se deve essere utilizzato il template con la colonna di sx (vaschette)

	public boolean isLoadingbarActive() {
		return this.loadingbar == null? false : this.loadingbar.isActive();
	}

	public boolean isLoadingbarstampeActive() {
		return this.loadingbarstampe == null? false : this.loadingbarstampe.isActive();
	}

	public Lookup getLookup() {
		return lookup;
	}

	public void setLookup(Lookup lookup) {
		this.lookup = lookup;
	}

	public boolean isLookupActive() {
		return this.lookup == null? false : this.lookup.isActive();
	}

	public CustomFieldsLookup getCustomfieldsLookup() {
		return customfieldsLookup;
	}

	public void setCustomfieldsLookup(CustomFieldsLookup customfieldsLookup) {
		this.customfieldsLookup = customfieldsLookup;
	}

	public boolean isCustomfieldsLookupActive() {
		return this.customfieldsLookup == null? false : this.customfieldsLookup.isActive();
	}

	/**
	 * recupero di eventuali configurazioni client da utilizzare su componenti inclusi nel browser (es. IWX, applet firma, ecc.)
	 * @return
	 */
	public ClientConfig getClientconfig() {
		ClientConfig clientconfig = (ClientConfig) getSessionAttribute(Const.DOCWAT_CLIENT_CONFIG_OBJECT);
		if (clientconfig == null) {
			clientconfig = new ClientConfig();
			setSessionAttribute(Const.DOCWAT_CLIENT_CONFIG_OBJECT, clientconfig);
		}

		return clientconfig;
	}

	public RifintLookup getRifintLookup() {
		return rifintLookup;
	}

	public void setRifintLookup(RifintLookup lookup) {
		this.rifintLookup = lookup;
	}

	public boolean isRifintLookupActive() {
		return this.rifintLookup == null? false : this.rifintLookup.isActive();
	}

	public Thvincolato getThvincolato() {
		return this.thvincolato;
	}

	public void setThvincolato(Thvincolato thvincolato) {
		this.thvincolato = thvincolato;
	}

	public boolean isThvincolatoActive() {
		return this.thvincolato == null? false : this.thvincolato.isActive();
	}

	public Showthes getShowthes() {
		return this.showthes;
	}

	public void setShowthes(Showthes showthes) {
		this.showthes = showthes;
	}

	public boolean isShowthesActive() {
		return this.showthes == null? false : this.showthes.isActive();
	}

	public Showindex getShowindex() {
		return showindex;
	}

	public void setShowindex(Showindex showindex) {
		this.showindex = showindex;
	}

	public boolean isShowindexActive() {
		return this.showindex == null? false : this.showindex.isActive();
	}

	public void setPopupPage(boolean popupPage) {
		this.popupPage = popupPage;
	}

	public boolean isPopupPage() {
		return this.popupPage;
	}

	public void setShowSxCol(boolean showSxCol) {
		this.showSxCol = showSxCol;
	}

	public boolean isShowSxCol() {
		return this.showSxCol;
	}

	public String getRequiredVersionIWX() {
		return requiredVersionIWX;
	}

	public void setRequiredVersionIWX(String version) {
		this.requiredVersionIWX = version;
	}

	public String getCurrentVersionIWX() {
		return currentVersionIWX;
	}

	public void setCurrentVersionIWX(String version) {
		this.currentVersionIWX = version;
	}

	public String getCustomDirCss() {
		return customDirCss;
	}

	public void setCustomDirCss(String customDirCss) {
		this.customDirCss = customDirCss;
	}

	public String getPageTemplate() {
		if (this.isPopupPage())
			return Const.TEMPLATE_POPUP_FILENAME;
		else if (!this.isShowSxCol())
			return Const.TEMPLATE_NOSX_FILENAME;
		else if (getEmbeddedApp().equals("dwrep"))
			return Const.TEMPLATE_REP_FILENAME;
		else if (getFormsAdapter() != null && getFormsAdapter().getCustomTemplate() != null && !getFormsAdapter().getCustomTemplate().equals(""))
			return getFormsAdapter().getCustomTemplate();
		else
			return Const.TEMPLATE_DEFAULT_FILENAME;
	}

	public String getErrorFieldIds() {
		return errorFieldIds;
	}

	public void setErrorFieldIds(String fieldIds) {
		this.errorFieldIds = fieldIds;
	}

	/**
	 * Analizza la response XML e, in caso di errore, imposta il
	 * popup di visualizzazione dell'errore
	 *
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public boolean handleErrorResponse(XMLDocumento response) throws Exception {
		return handleErrorResponse(response, null);
	}

	/**
	 * Analizza la response XML e, in caso di errore, imposta il
	 * popup di visualizzazione dell'errore
	 *
	 * @param response
	 * @param level utilizzato per forzare il livello di errore
	 * @return
	 * @throws Exception
	 */
	public boolean handleErrorResponse(XMLDocumento response, String level) throws Exception {
		if (ErrormsgFormsAdapter.isResponseErrorMessage(response)) {
			Errormsg errormsg = new Errormsg();
			errormsg.setActive(true);
			errormsg.init(response.getDocument());

			if (errormsg.getErrore().getErrtype().contains("Documento cancellato")) // TODO e' corretto gestirlo in questo modo in questo punto?
				level = Const.MSG_LEVEL_WARNING;

			// forzatura del livello di errore
			if (level != null && level.length() > 0 && errormsg.getErrore() != null)
				errormsg.getErrore().setLevel(level);

			HttpSession session = null;
			if (FacesContext.getCurrentInstance() != null)
				session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			else if (this.request != null)
				session = this.request.getSession(false);

			session.setAttribute("errormsg", errormsg);

			return true;
		}
		// TODO gestire pagina di scelta login...

		return false;
	}
	
	/**
	 * Imposta il popup di visualizazione di un errore dato il messaggio
	 * e il livello di errore
	 *
	 * @param message
	 * @param level
	 * @throws Exception
	 */
	protected void setErroreResponse(String message, String level) throws Exception {
		setErroreResponse(message, null, level);
	}

	/**
	 * Imposta il popup di visualizazione di un errore dato il messaggio
	 * e il livello di errore
	 *
	 * @param message
	 * @param details
	 * @param level
	 * @throws Exception
	 */
	protected void setErroreResponse(String message, String details, String level) throws Exception {
		if (message != null && !message.equals("")) {
			if (level == null || level.equals(""))
				level = Const.MSG_LEVEL_ERROR;

			Errormsg errormsg = new Errormsg();
			errormsg.setActive(true);
			Errore error = new Errore();
			error.setLevel(level);
			error.setErrtype(message);
			if (details != null && !details.isEmpty())
				error.setExtra(details);
			errormsg.setErrore(error);

			HttpSession session = null;
			if (FacesContext.getCurrentInstance() != null)
				session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			else if (this.request != null)
				session = this.request.getSession(false);

			session.setAttribute("errormsg", errormsg);
		}
	}

	public Object getSessionAttribute(String id) {
		HttpSession session = null;

		if (FacesContext.getCurrentInstance() != null)
			session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		else if (this.request != null)
			session = this.request.getSession(false);
		
		return session.getAttribute(id);
	}

	public void setSessionAttribute(String id, Object obj) {
		HttpSession session = null;
		
		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null)
			session = (HttpSession) context.getExternalContext().getSession(false);
		else if (this.request != null)
			session = this.request.getSession(false);
		
		session.setAttribute(id, obj);
	}
	
	public void removeSessionAttribute(String id) {
		HttpSession session = null;

		if (FacesContext.getCurrentInstance() != null)
			session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		else if (this.request != null)
			session = this.request.getSession(false);

		session.removeAttribute(id);
	}

	/**
	 * Rimuove dalla sessione tutti i bean di init ad esclusione di quello dell'applicazione indicata come parametro (app corrente)
	 * @param excludeAppName
	 */
	public void removeAppInitSessionAttribute(String excludeAppName) {
		String[] initBeans = DocWayProperties.readProperty("appInitBeans", "").split(",");
		for(int i = 0; i < initBeans.length; i++) {
			if(excludeAppName == null || !(excludeAppName+"Init").equals(initBeans[i]))
				removeSessionAttribute(initBeans[i]);
		}

	}


	/**
	 * set di un cookie nella response
	 * @param cookieName nome del cookie da settare
	 * @param value valore da impostare
	 * @param domain dominio del cookie
	 * @param path path del cookie
	 * @throws Exception
	 */
	public void setCookie(String cookieName, String value, String domain, String path) throws Exception {
		if (cookieName != null && !cookieName.isEmpty() && value != null && !value.isEmpty()) {
			HttpServletResponse httpResponse = null;
			if (FacesContext.getCurrentInstance() != null)
				httpResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			else
				httpResponse = this.response;

			if (httpResponse != null) {
				Cookie cookie = new Cookie(cookieName, value);
				if (domain != null && !domain.isEmpty())
					cookie.setDomain(domain);
				if (path != null && !path.isEmpty())
					cookie.setPath(path);
				//cookie.setMaxAge(28800); // tempo espresso in secondi
				httpResponse.addCookie(cookie);
			}
			else
				throw new Exception("Impossible to set cookie '" + cookieName + "=" + value + "; domain=" + domain + "; path=" + path + "'. Response is NULL!");
		}
	}

	public UserBean getUserBean() {
		return (UserBean)getSessionAttribute("userBean");
	}

	public UserBean getDelegatoBean() {
		return (UserBean)getSessionAttribute("delegatoBean");
	}

	public String buildSpecificShowdocPageAndReturnNavigationRule(String dbTable, XMLDocumento response, boolean popup) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, response, "showdoc", popup);
	}

	public String buildSpecificShowdocPageAndReturnNavigationRule(String dbTable, XMLDocumento response) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, response, "showdoc", popupPage);
	}

	public String buildSpecificShowdocPageAndReturnNavigationRule(String dbTable, String dirTemplate, String personalPackage, String suffix, XMLDocumento response, boolean popup) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, dirTemplate, personalPackage, suffix, response, "showdoc", popup);
	}

	public String buildSpecificQueryPageAndReturnNavigationRule(String dbTable, XMLDocumento response) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, response, "query", popupPage);
	}

	public String buildSpecificQueryPageAndReturnNavigationRule(String dbTable, String dirTemplate, String personalPackage, String suffix, XMLDocumento response, boolean popup) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, dirTemplate, personalPackage, suffix, response, "query", popup);
	}

	public String buildSpecificDocEditPageAndReturnNavigationRule(String dbTable, XMLDocumento response, boolean popup) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, response, "docEdit", popup);
	}

	public String buildSpecificDocEditPageAndReturnNavigationRule(String dbTable, String dirTemplate, String personalPackage, String suffix, XMLDocumento response, boolean popup) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, dirTemplate, personalPackage, suffix, response, "docEdit", popup);
	}

	public String buildSpecificDocEditModifyPageAndReturnNavigationRule(String dbTable, XMLDocumento response, boolean popup) throws Exception {
		String pageToLoad = buildSpecificPageAndReturnNavigationRule(dbTable, response, "docEditModify", popup);
		if (pageToLoad != null) {
			return pageToLoad.replaceAll("docEditModify", "docEdit@modify");
		}
		else
			return null;
	}

	/*docway delibere*/
	public String buildSpecificShowtitlesPageAndReturnNavigationRule(String dbTable, XMLDocumento response) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, "", "", "", "", response, "showtitles", popupPage);
	}

	/**
	 * Costruisce una pagina in base ai parametri specificati ritorna la corretta regola
	 * di navigazione
	 *
	 * @param dbTable Risorsa da gestire
	 * @param response Response restituita dal service
	 * @param pageType Tipologia di pagina da caricare (showdoc, docEdit, ecc.)
	 * @param popup Vale true se il caricamento deve avvenire nel template di popup, false altrimenti
	 *
	 * @return Regola di navigazione da applicare
	 * @throws Exception
	 */
	private String buildSpecificPageAndReturnNavigationRule(String dbTable, XMLDocumento response, String pageType, boolean popup) throws Exception {
		String personalViewToUse = response.getAttributeValue("/response/@personalViewToUse"); // formato di personalViewToUse: @ALBO*rep_standard
		String personalDirCliente = response.getAttributeValue("/response/@personalDirCliente"); // formato di personalDirCliente: directory/

		// formato di personalViewToUse: @ALBO*rep_standard
		String personalPackage = "";
		String suffix = "";
		String dirTemplate = "";
		if (personalViewToUse != null && !personalViewToUse.equals("")) {
			int index = personalViewToUse.indexOf("*");
			if (index == -1) {
				suffix = personalViewToUse;
				dirTemplate = "";
			}
			else {
				suffix = personalViewToUse.substring(0, index);
				dirTemplate = personalViewToUse.substring(index+1);
				if (!dirTemplate.equals("")) {
					personalPackage = dirTemplate;
					dirTemplate = dirTemplate + "_";
				}
			}
		}
		else if (personalDirCliente != null && !personalDirCliente.equals("")) {
			personalPackage = personalDirCliente.substring(0, personalDirCliente.length()-1);
		}
		else { // TODO da testare molto attentamente... esistono casi di dbTable formattati in questo modo per i quali non e' presente un template personalizzato?
			// ricerca di un eventuale suffisso all'interno di dbTable (es. @fascicolo@personale)
			int index = dbTable.lastIndexOf("@");
			if (index > 0) {
				suffix = dbTable.substring(index);
				dbTable = dbTable.substring(0, index);
			}
		}

		return buildSpecificPageAndReturnNavigationRule(dbTable, dirTemplate, personalPackage, suffix, response, pageType, popup);
	}

	/**
	 * external context redirect con controllo di eventuali sottodirectory
	 * da gestire nell'url (es. template personalizzati con sottocartelle)
	 */
	public void redirectToJsf(String navigationRule, XMLDocumento response) throws Exception {
		String personalViewToUse = response.getAttributeValue("/response/@personalViewToUse"); // formato di personalViewToUse: @ALBO*rep_standard
		String dirTemplate = "";
		if (personalViewToUse != null && !personalViewToUse.equals("")) {
			int index = personalViewToUse.indexOf("*");
			if (index != -1) {
				dirTemplate = personalViewToUse.substring(index+1);
				if (!dirTemplate.equals("")) {
					// mbernardini 15/02/2018 : sostituito indexOf con lastIndexOf per problemi di caricamento di personalView interne alla dir rep_standard
					int index1 = navigationRule.lastIndexOf("_");
					if (index1 != -1)
						navigationRule = dirTemplate + "/" + navigationRule.substring(index1+1);
				}
			}
		}

		String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		String appName = AppUtil.getAppNameFromFacesContex();

		FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + "/" + appName + "/" + navigationRule + ".jsf");
	}

	/** gestione degli aspects (plugin template docway)
	 *
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void loadAspects(String dbTable, XMLDocumento response, String pageType) throws Exception {
		List<Element> aspectsList = response.selectNodes("/response/addons/addon[@type = 'aspect']");

		for (Element aspectEl : aspectsList) {
			String aspectName = aspectEl.attributeValue("name", "").trim();
			if (!aspectName.equals("")) {

				if (dbTable.startsWith("@"))
					dbTable = dbTable.substring(1);

				String className = pageType.substring(0, 1).toUpperCase() + pageType.substring(1);
				String[] arr = dbTable.split("_");
				for (int i=0; i<arr.length; i++) {
					className += arr[i].substring(0, 1).toUpperCase() + arr[i].substring(1);
				}
				className += "Aspect";

				String packageName = Const.ADDON_PACKAGE_NAME + "." + aspectName;
				String templateName = "/" + aspectName + "/aspect@" + pageType + "@" + dbTable + "@" + aspectName + ".xhtml";

				//create object instance
				Class<?>[] partypes = new Class[2];
				partypes[0] = String.class;
				partypes[1] = Page.class;
				Object[] arglist = new Object[2];
				arglist[0] = templateName;
				arglist[1] = this;
				Class<?> theClass = Class.forName(packageName + "." + className);
				Object theObject = theClass.getConstructor(partypes).newInstance(arglist);

				partypes = new Class[1];
		        partypes[0] = Document.class;
				Method theMethod = theClass.getMethod("init", partypes);
				arglist = new Object[1];
				arglist[0] = response.getDocument();
				theMethod.invoke(theObject, arglist);

				addAspect((BaseAddOn) theObject);
			}
		}
	}

	/** gestione degli addon per la pulsantiera (plugin template docway)
	 *
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void loadButtons(String dbTable, XMLDocumento response, String pageType) throws Exception {
		List<Element> buttonsList = response.selectNodes("/response/addons/addon[@type = 'buttons']");

		for (Element buttonsEl : buttonsList) {
			String buttonsName = buttonsEl.attributeValue("name", "").trim();
			if (!buttonsName.equals("")) {

				if (dbTable.startsWith("@"))
					dbTable = dbTable.substring(1);

				String className = pageType.substring(0, 1).toUpperCase() + pageType.substring(1);
				String[] arr = dbTable.split("_");
				for (int i=0; i<arr.length; i++) {
					className += arr[i].substring(0, 1).toUpperCase() + arr[i].substring(1);
				}
				className += "Buttons";

				String packageName = Const.ADDON_PACKAGE_NAME + "." + buttonsName;
				String templateName = "/" + buttonsName + "/buttons@" + pageType + "@" + dbTable + "@" + buttonsName + ".xhtml";

				//create object instance
				Class<?>[] partypes = new Class[2];
				partypes[0] = String.class;
				partypes[1] = Page.class;
				Object[] arglist = new Object[2];
				arglist[0] = templateName;
				arglist[1] = this;
				Class<?> theClass = Class.forName(packageName + "." + className);
				Object theObject = theClass.getConstructor(partypes).newInstance(arglist);

				partypes = new Class[1];
		        partypes[0] = Document.class;
				Method theMethod = theClass.getMethod("init", partypes);
				arglist = new Object[1];
				arglist[0] = response.getDocument();
				theMethod.invoke(theObject, arglist);

				addButtons((BaseAddOn) theObject);
			}
		}
	}

	/**
	 * Costruisce una pagina in base ai parametri specificati ritorna la corretta regola
	 * di navigazione
	 *
	 * @param dbTable Risorsa da gestire
	 * @param dirTemplate percorso alla directory che contiene il template di visualizzazione della risorsa
	 * @param personalPackage package specifico per una personalizzazione
	 * @param suffix suffisso da applicare al bean di gestione della risorsa e al template di visualizzazione
	 * @param response Response restituita dal service
	 * @param pageType Tipologia di pagina da caricare (showdoc, docEdit, ecc.)
	 * @param popup Vale true se il caricamento deve avvenire nel template di popup, false altrimenti
	 *
	 * @return Regola di navigazione da applicare
	 * @throws Exception
	 */
	protected String buildSpecificPageAndReturnNavigationRule(String dbTable, String dirTemplate, String personalPackage, String suffix, XMLDocumento response, String pageType, boolean popup) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, dirTemplate, "", personalPackage, suffix, response, pageType, popup);
	}

	/**
	 * Costruisce una pagina in base ai parametri specificati ritorna la corretta regola
	 * di navigazione
	 *
	 * @param dbTable Risorsa da gestire
	 * @param dirTemplate percorso alla directory che contiene il template di visualizzazione della risorsa
	 * @param personalPackage package specifico per una personalizzazione
	 * @param suffix suffisso da applicare al bean di gestione della risorsa e al template di visualizzazione
	 * @param response Response restituita dal service
	 * @param pageType Tipologia di pagina da caricare (showdoc, docEdit, ecc.)
	 * @param popup Vale true se il caricamento deve avvenire nel template di popup, false altrimenti
	 *
	 * @return Regola di navigazione da applicare
	 * @throws Exception
	 */
	protected String buildSpecificPageAndReturnNavigationRule(String dbTable, String dirTemplate, String packageName, String personalPackage, String suffix, XMLDocumento response, String pageType, boolean popup) throws Exception {
		try {
			if (personalPackage == null)
				personalPackage = "";
			if (suffix == null)
				suffix = "";
			if (dirTemplate == null)
				dirTemplate = "";

			String classSuffix = "";
			if (suffix.length() > 0) {
				classSuffix = suffix.substring(1);
				classSuffix = classSuffix.substring(0, 1).toUpperCase() + classSuffix.substring(1); // forza la maiuscola come primo carattere del suffisso della classe
			}

			FacesContext ctx = FacesContext.getCurrentInstance();
			String uri = ctx.getExternalContext().getRequestServletPath();
			String uriService = XMLUtil.parseStrictAttribute(response.getDocument(), "/response/@uri");
			if (packageName == null || packageName.trim().equals("")) {
				if (uri.contains(Const.ACL_CONTEXT_NAME) || uriService.contains("acl.jsp")) {
					packageName = Const.ACL_PACKAGE_NAME;
				}
				else if (uri.contains(Const.DOCWAYPROC_CONTEXT_NAME)) {
					packageName = Const.DOCWAYPROC_PACKAGE_NAME;
				}
				else if (uri.contains(Const.DOCWAY_CONTEXT_NAME)  || uriService.contains("xdocway.jsp")) {
					packageName = Const.DOCWAY_PACKAGE_NAME;
				}

				packageName += ".beans";
			}

			if (dbTable.startsWith("@"))
				dbTable = dbTable.substring(1);

			Logger.info("Page.buildSpecificPageAndReturnNavigationRule() : params :  dbTable = " + dbTable + ", dirTemplate = " + dirTemplate + ", packageName = " + packageName + ", personalPackage = " + personalPackage + ", pageType = " + pageType + ", suffix = " + suffix);

			String className = pageType.substring(0, 1).toUpperCase() + pageType.substring(1);
			String[] arr = dbTable.split("_");
			for (int i=0; i<arr.length; i++) {
				if (arr[i] != null && arr[i].length() > 0)
					className += arr[i].substring(0, 1).toUpperCase() + arr[i].substring(1);
			}
			className = className.replaceAll("-", "");
			String beanName = className.substring(0, 1).toLowerCase() + className.substring(1);

			if (personalPackage.length() > 0)
				packageName += "." + personalPackage;

			//create object instance
			Class<?> theClass = Class.forName(packageName + "." + className + classSuffix);
			Object theObject = theClass.newInstance();

			//fillFormsFromResponse
			Method theMethod = theClass.getMethod("getFormsAdapter");
	//		Method theMethod = theClass.getMethod("getFormsAdapter", null);
			Object formsAdapterObject = theMethod.invoke(theObject);
			@SuppressWarnings("rawtypes")
			Class[] partypes = new Class[1];
	        partypes[0] = XMLDocumento.class;
			theMethod = formsAdapterObject.getClass().getMethod("fillFormsFromResponse", partypes);
			Object[] arglist = new Object[1];
			arglist[0] = response;
			theMethod.invoke(formsAdapterObject, arglist);

			//init
			partypes = new Class[1];
	        partypes[0] = Document.class;
			theMethod = theClass.getMethod("init", partypes);
			arglist = new Object[1];
			arglist[0] = response.getDocument();
			theMethod.invoke(theObject, arglist);

			// TODO non possibile il caricamento di addons su profilo ACL
			// in molte chiamate viene utilizzato dbTable = persona_interna anche per il profilo, si genera
			// quindi del conflitto. andrebbe rivista meglio la gestione addons. FIX temporaneo per correggere bugs
			// su addon "Gestione Archivi" su persone interne (x-Dams)
			if (!dbTable.equals("profilo")) {
				//carica gli enventuali bean di "aspetto"
				partypes = new Class[3];
		        partypes[0] = String.class;
		        partypes[1] = XMLDocumento.class;
		        partypes[2] = String.class;
				theMethod = theClass.getMethod("loadAspects", partypes);
				arglist = new Object[3];
				arglist[0] = dbTable;
				arglist[1] = response;
				arglist[2] = pageType;
				theMethod.invoke(theObject, arglist);

				//carica gli enventuali bean di "bottoni"
				partypes = new Class[3];
		        partypes[0] = String.class;
		        partypes[1] = XMLDocumento.class;
		        partypes[2] = String.class;
				theMethod = theClass.getMethod("loadButtons", partypes);
				arglist = new Object[3];
				arglist[0] = dbTable;
				arglist[1] = response;
				arglist[2] = pageType;
				theMethod.invoke(theObject, arglist);
			}

			// Imposto il corretto template di visualizzazione
			if (popup) {
				partypes = new Class[1];
		        partypes[0] = boolean.class;
				theMethod = theClass.getMethod("setPopupPage", partypes);
				arglist = new Object[1];
				arglist[0] = popup;
				theMethod.invoke(theObject, arglist);
			}

			setSessionAttribute(beanName + classSuffix, theObject);

			return dirTemplate + pageType + "@" + dbTable + suffix;
		}
		catch (Throwable t) {
			Logger.error(t.getMessage(), t);
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(I18N.mrs("dw4.errore_nel_caricamento_del_template"), "", ErrormsgFormsAdapter.ERROR));

			return null;
		}
	}

	/**
	 * Imposta il messaggio di errore (su validazione) per un campo di un form
	 *
	 * @param fieldId Identificativo del campo sul quale è stato individuato l'errore
	 * @param message Messaggio di errore da mostrare per il form
	 */
	public void setErrorMessage(String fieldId, String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
		if (fieldId != null && fieldId.length() > 0) {
			setInputFieldNotValid(fieldId);
		}
	}

	/**
	 * @author dpranteda
	 * Imposta il messaggio di errore (su validazione) per un campo di un form che non sia un InputField (Data, Select...)
	 *
	 * @param fieldId Identificativo del campo sul quale è stato individuato l'errore
	 * @param message Messaggio di errore da mostrare per il form
	 */
	public void setErrorMessageNoInputField(String fieldId, String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
		if (fieldId != null && fieldId.length() > 0) {
			if (errorFieldIds == null || errorFieldIds.length() == 0)
				errorFieldIds = fieldId;
			else
				errorFieldIds = errorFieldIds + "," + fieldId;
		}
	}

	/**
	 * Imposta il messaggio di errore (su validazione) per un campo di un form
	 *
	 * @param fieldId Identificativo del campo sul quale è stato individuato l'errore
	 * @param message Messaggio di errore da mostrare per il form
	 */
	public void setErrorMessage(String[] fieldIds, String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
		if (fieldIds != null && fieldIds.length > 0) {
			for (int i=0; i<fieldIds.length; i++) {
				if (fieldIds[i] != null && fieldIds[i].length() > 0) {
					setInputFieldNotValid(fieldIds[i]);
				}
			}
		}
	}

	/**
	 * Imposta come non valido il campo input avente come id quello passato
	 *
	 * @param fieldId Identificativo del campo input da rendere non valido
	 */
	protected void setInputFieldNotValid(String fieldId) {
		UIInput input = null;
		if (StringUtil.count(fieldId, ":") > 1) {
			// se il campo al quale fa riferimento l'errore e' contenuto in un ui:repeat non e' possibile utilizzare
			// la procedura sottostante per invalidare il campo. In questo caso viene registrato all'interno di una
			// proprieta' e successivamente elaborato il campo tramite jQuery
			if (errorFieldIds == null || errorFieldIds.length() == 0)
				errorFieldIds = fieldId;
			else
				errorFieldIds = errorFieldIds + "," + fieldId;
		}
		else {
			input = (UIInput) FacesContext.getCurrentInstance().getViewRoot().findComponent(fieldId);

			if (input != null)
				input.setValid(false);
		}
	}

	/**
	 * Imposta un messaggio a livello INFO
	 *
	 * @param message Messaggio di info da mostrare
	 */
	public void setInfoMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, message, null));
	}

	public void addAspect(BaseAddOn aspect) {
		this.aspects.add(aspect);
	}

	public void addButtons(BaseAddOn buttons) {
		this.buttons.add(buttons);
	}

	public List<BaseAddOn> getAspects() {
		return this.aspects;
	}

	public List<BaseAddOn> getButtons() {
		return this.buttons;
	}

	/**
	 * Identificazione della richiesta client.
	 * Utilizzato per identificare la chiamata su un host da uno specifico browser di un utente per eseguire specifiche azioni su
	 * quella pagina (es. reset dei jobs di IWX su un DocWay su uno specifico browser)
	 * @return
	 */
	public String getClientIdentifier() {
		if (clientIdentifier == null || clientIdentifier.equals("")) {
			try {
				if (request == null)
					request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

				UserBean user = getUserBean();
				clientIdentifier = DigestUtils.md5Hex(request.getServerName() + "|" + request.getServerPort() + "|" + request.getContextPath() + "|" + user.getLogin() + "|" + user.getMatricola() + "|" + request.getHeader("user-agent"));
			}
			catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}
		return clientIdentifier;
	}

	/**
	 * identifica la tipologia di lingua utilizzata (Left to Right o Right To Left)
	 * @return
	 */
	public String getLanguageOrientation() {
		String orientation = "ltr";

		try {
			Locale currentLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
			if (currentLocale != null) {
				String lang = currentLocale.getLanguage();
				if (lang.equals("iw") || lang.equals("ar") || lang.equals("fa") || lang.equals("ur"))
					orientation = "rtl";
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}

		return orientation;
	}

	/**
	 * Ritorna true se occorre utilizzare IWX in modalita' legacy (ver. 2.x), false nel caso debba essere caricata la
	 * nuova versione di IWX (ver. 3.x)
	 * @return
	 */
	public boolean isIwxLegacyMode() {
		return StringUtil.booleanValue(DocWayProperties.readProperty("iwx.legacyMode", "false"));
	}

	/**
	 * Ritorna true se sono abilitate le showdoc-section collapsable
	 * @return
	 */
	public boolean isShowdocSectionCollapsable() {
		return StringUtil.booleanValue(DocWayProperties.readProperty("showdocSectionCollapsable", "false"));
	}

	/**
	 * Ritorna se le showdoc-section collapsable devono essere aperte di default o meno
	 * @return
	 */
	public boolean isShowdocSectionDefault() {
		return StringUtil.booleanValue(DocWayProperties.readProperty("showdocSectionDefault", "true"));
	}

	public CustomFields getCustomfields() {
		return customfields;
	}

	public void setCustomfields(CustomFields customfields) {
		this.customfields = customfields;
	}

	/**
	 * verifica se IWX e' supportato sul browser corrente
	 *
	 * @return true se IWX deve essere attivato, false altrimenti
	 */
	public boolean isEnabledIWX() {
		boolean enableIWX = false;

		String enable = (String) getSessionAttribute("enableIWX");
		if (enable == null || enable.equals("")) {
			// verifica l'abilitazione di IWX tramite controllo dell'header del browser
			if (request != null && request.getHeader("user-agent") != null && request.getHeader("user-agent").toLowerCase().indexOf("windows") != -1) // TODO gestione IWX - a fine sviluppo di IWX occorre probabilmente modificare il controllo
				enableIWX = true;

			// verifica se iwx deve essere disabilitato perche' forzato da properties
			if (DocWayProperties.readProperty("abilitaControlloIWX", "Si").equals("No"))
				enableIWX = false;

			setSessionAttribute("enableIWX", enableIWX+"");
		}
		else {
			// recupera l'abilitazione di iwx dalla sessione utente
			enableIWX = StringUtil.booleanValue(enable);
		}

		return enableIWX;
	}

	public Map<String, List<String>> parseUploaderFileFilter(String fileFilter) {
		Map<String, List<String>> fileFilters = new HashMap<String, List<String>>();
		String[] splitByRepertori = fileFilter.split("\\|");

		for (String filterString : splitByRepertori) {
			String[] splitFilter = filterString.split(",");
			if ((splitFilter.length -1) % 2 == 0) {
				String repertorio = splitFilter[0];
				List<String> filter = Arrays.asList(Arrays.copyOfRange(splitFilter, 1, splitFilter.length)); //rimuovi il codice del repertorio
				fileFilters.put(repertorio, filter);
			}
			else
				Logger.error("Errore nel formato specificato come filtro per l'upload di files");
		}

		return fileFilters;
	}

	public void setUploadFilters(String id) {
		List<String> fileFilter;
		if (this.fileFilters.containsKey(id))
			fileFilter = this.fileFilters.get(id);
		else
			fileFilter = this.fileFilters.get("");

		docInformaticiFileDescription = "";
		docInformaticiFileTypes = "";
		docInformaticiArchiveTypes = DocEditDoc.docInformaticiArchiveTypesDefault;
		iwxFileTypes = "";

		if (fileFilter == null) {
			docInformaticiFileDescription = DocEditDoc.docInformaticiFileDescriptionDefault;
			docInformaticiFileTypes = DocEditDoc.docInformaticiFileTypesDefault;
			iwxFileTypes = DocEditDoc.iwxFileTypesDefault;
		}
		else {
			for (int i = 0; i < fileFilter.size(); i+=2) {
				String description = fileFilter.get(i);

				if (docInformaticiFileDescription.equals(""))
					docInformaticiFileDescription = I18N.mrs(description);
				if (docInformaticiFileTypes.equals(""))
					docInformaticiFileTypes = fileFilter.get(i + 1);

				iwxFileTypes += I18N.mrs(description) + "," + fileFilter.get(i + 1);
				if (i != fileFilter.size() - 2) {
					iwxFileTypes += ",";
				}
			}
		}

		if (this.fileFilters.containsKey(id))
			fileFilter = this.imageFilters.get(id);
		else
			fileFilter = this.imageFilters.get("");

		docInformaticiImageDescription = "";
		docInformaticiImageTypes = "";
		iwxImageTypes = "";

		if (fileFilter == null) {
			docInformaticiImageDescription = DocEditDoc.docInformaticiImageTypesDescriptionDefault;
			docInformaticiImageTypes = DocEditDoc.docInformaticiImageTypesDefault;
			iwxImageTypes = DocEditDoc.iwxImageTypesDefault;
		}
		else {
			for (int i = 0; i < fileFilter.size(); i+=2) {
				String description = fileFilter.get(i);

				if (docInformaticiImageDescription.equals(""))
					docInformaticiImageDescription = I18N.mrs(description);
				if (docInformaticiImageTypes.equals(""))
					docInformaticiImageTypes = fileFilter.get(i + 1);

				iwxImageTypes += I18N.mrs(description) + "," + fileFilter.get(i + 1);
				if (i != fileFilter.size() - 2) {
					iwxImageTypes += ",";
				}
			}
		}
	}

	public String getDocInformaticiFileDescription() {
		return docInformaticiFileDescription;
	}

	public String getDocInformaticiFileTypes() {
		return docInformaticiFileTypes;
	}
	
	public String getDocInformaticiArchiveTypes() {
		return docInformaticiArchiveTypes;
	}

	public String getIwxFileTypes() {
		return iwxFileTypes;
	}

	public String getDocInformaticiImageDescription() {
		return docInformaticiImageDescription;
	}

	public String getDocInformaticiImageTypes() {
		return docInformaticiImageTypes;
	}

	public String getIwxImageTypes() {
		return iwxImageTypes;
	}

	protected void setFatalLevelInErrormsg() {
		Errormsg errormsg = (Errormsg) getSessionAttribute("errormsg");
		if (errormsg != null)
			errormsg.getErrore().setLevel(ErrormsgFormsAdapter.FATAL);
	}

	/**
	 * ritorna true se e' stato fatto accesso da ADM, false altrimenti
	 */
	public boolean isAccessoADM() {
		String admLogin = (String) getSessionAttribute("admLogin");
		if (admLogin != null && admLogin.length() > 0 && getUserBean() != null && getUserBean().getLogin() != null && admLogin.equals(getUserBean().getLogin()))
			return true;
		else
			return false;
	}

	/**
	 * ritorna true se e' stato fatto accesso da delega, false altrimenti
	 */
	public boolean isAccessoDelega() {
		String delegaLogin = (String) getSessionAttribute("delegaLogin");
		if (delegaLogin != null && delegaLogin.length() > 0 && getUserBean() != null && getUserBean().getLogin() != null && delegaLogin.equals(getUserBean().getLogin()))
			return true;
		else
			return false;
	}

	/**
	 * recupero del db in inizializzazione dell'applicazione
	 * @param request
	 * @param appName
	 */
	protected String getDbForApp(String appName) {
		String db = "";

		boolean dbcondiviso = false;
		String sharedSessionApps = DocWayProperties.readProperty(Const.PROPERTY_SHARED_SESSION_APPS, ""); // eventuali apps embedded con sessione condivisa con DocWay
		if (appName.equals("docway") || sharedSessionApps.contains(appName))
			dbcondiviso = true;

		// richiesta di un db specifico tramite parametro della request
		db = (String) request.getParameter("db");
		if (db != null && db.length() > 0) {
			setSessionAttribute(appName + "reqDb", db);
			if (dbcondiviso)
				setSessionAttribute("sharedDb", db);
		}
		else {
			// caricamento di un eventuale db custom da sessione dell'utente
			String dbInSession = (String) getSessionAttribute(appName + "reqDb");
			if (dbInSession != null && !dbInSession.equals(""))
				db = dbInSession;
			else if (dbcondiviso) {
				dbInSession = (String) getSessionAttribute("sharedDb");
				if (dbInSession != null && !dbInSession.equals(""))
					db = dbInSession;
			}
		}

		return db;
	}

	/**
	 * ritorna la versione di docway impostata sul file docway.properties
	 * @return
	 */
	public String getVersioneDocway() {
		return DocWayProperties.readProperty("versioneDocway", "");
	}

	/**
	 * Apertura di un popup di messaggio in caso di warning ricevuto
	 * @param warningMessage Messaggio da mostrare all'interno del popup modale
	 * @return
	 * @throws Exception
	 */
	public String showMessageWarning(String warningMessage) throws Exception {
		return showMessageWarning(warningMessage, Const.MSG_LEVEL_INFO);
	}

	/**
	 * Apertura di un popup di messaggio in caso di warning ricevuto
	 * @param warningMessage Messaggio da mostrare all'interno del popup modale
	 * @param level livello di warning del messaggio (success, info, warning, error, fatal) - default = info
	 * @return
	 * @throws Exception
	 */
	public String showMessageWarning(String warningMessage, String level) throws Exception {
		Msg message = new Msg();
		message.setActive(true);
		message.setTitle(I18N.mrs("dw4.avviso"));
		if (level == null || level.isEmpty())
			level = Const.MSG_LEVEL_INFO;
		message.setType(level);
		message.setMessage(warningMessage);

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("msg", message);

		return null;
	}

	/**
	 * ritorna true se deve essere visaulizzato il pulsante di logout da ACL (o Archivio Procedimenti), false
	 * se invece il pulsante di logout deve essere visibile solo da DocWay
	 */
	public boolean isLogoutFromACL() {
		return StringUtil.booleanValue(DocWayProperties.readProperty("abilitaLogoutFromACL", "si"));
	}

	/**
	 * Ritorna il formato del codice di classificazione da adottare
	 * @return
	 */
	public String getClassifFormat() {
		return ClassifFormatManager.getInstance().getClassifFormat();
	}

	/**
	 * Scaricamento di un file (invio al client come attachment) attraverso il FacesContext
	 * @param attachFile Oggetto file da inviare al client
	 * @param fileName Nome del file da restituire al client
	 * @throws Exception
	 */
	public void sendAttachByFacesContext(AttachFile attachFile, String fileName) throws Exception {
		sendAttachByFacesContext(attachFile, fileName, null);
	}

	/**
	 * Scaricamento di un file (invio al client come attachment) attraverso il FacesContext
	 * @param attachFile Oggetto file da inviare al client
	 * @param fileName Nome del file da restituire al client
	 * @param errorLevel Eventuale livello di errore da settare in caso di eccezioni riscontrate (default = WARNING)
	 * @throws Exception
	 */
	// TODO codice replicato in molti punti, andrebbe generalizzato e chiamato questo metodo (opportunamente modificato per gestire tutti i possibili casi)
	public void sendAttachByFacesContext(AttachFile attachFile, String fileName, String errorLevel) throws Exception {

		// TODO e' corretto il restore del form per tutti i casi?
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());

		if (attachFile != null && attachFile.getContent() != null) {
			if (fileName == null || fileName.isEmpty())
				fileName = attachFile.getFilename(); // tento il caricamento del nome file dall'allegato

			if (fileName == null || fileName.isEmpty()) // se il filename e' ancora nullo, assegno un nome di default
				fileName = "unknown.dat"; // TODO corretto?

			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
			response.setContentLength(attachFile.getContent().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			ServletOutputStream out;
			out = response.getOutputStream();
			out.write(attachFile.getContent());

			faces.responseComplete();
		}
		else {
			// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
			if (errorLevel == null || errorLevel.isEmpty())
				errorLevel = Const.MSG_LEVEL_WARNING;

			handleErrorResponse(attachFile.getXmlDocumento(), errorLevel);
		}
	}
	
	/**
	 * Inizializzazione di tutte le informazioni relative ad azioni massive (stored procedure LUA) disponibili
	 * da lista titoli
	 * @param dom
	 */
	public void initAzioniMassive(Document dom) {
		this.azioniMassive = new AzioniMassive();
		if (dom != null)
			this.azioniMassive.init(dom);
	}
	
	public AzioniMassive getAzioniMassive() {
		return azioniMassive;
	}

	public void setAzioniMassive(AzioniMassive azioniMassive) {
		this.azioniMassive = azioniMassive;
	}
	
	/**
	 * Ritorna true se sono disponibili azioni massive per la pagina corrente, false altrimenti
	 * @return
	 */
	public boolean isAzioniMassiveAvailable() {
		return azioniMassive != null && azioniMassive.getAzioni() != null && !azioniMassive.getAzioni().isEmpty();
	}
	
	//tiommi : gestione deleghe - swappa i bean di sessione relativi agli utenti, di fatto effettuando un accesso in delega
 	public String loginInDelega(String matricola, String login) throws Exception {
		try {

			//gestione per rilascio vecchia delega (caso di login da url)
			if(getDelegatoBean() != null)
				logOutDelega(false);

			//sposta il vecchio userBean prima di sovrascriverlo (necessario per il ripristino)
			UserBean delegatoBean = getUserBean();
			setSessionAttribute("delegatoBean", delegatoBean);


			UserBean userBean = new UserBean(login);
			userBean.setMatricola(matricola);
			setSessionAttribute("userBean", userBean);
			setSessionAttribute("delegaLogin", login);

			//svuota dati sessione di precedenti accessi acl, docwayproc...
			removeAppInitSessionAttribute((String)getSessionAttribute("appName"));

			DocwayInit init = new DocwayInit();
			return init.loginWithDelega();

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
		return null;
	}

 	//tiommi : gestione deleghe - rilascia la delega correntemente utilizzata
 	public String logOutDelega(boolean reinit) throws Exception {
 		try {
			//ripristina l'utente precedentemente loggato
			UserBean orginalBean = getDelegatoBean();
			setSessionAttribute("userBean", orginalBean);

			//pulisce la sessione
			removeSessionAttribute("delegatoBean");
			removeSessionAttribute("delegaLogin");

			//svuota dati sessione di precedenti accessi acl, docwayproc...
			removeAppInitSessionAttribute((String)getSessionAttribute("appName"));

			//reinit
			if(reinit) {
				DocwayInit init = new DocwayInit();
				return init.resetLoginAfterDelega();
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
		return null;
 	}
 	
 	/**
 	 * Dato il codice di una societa' ne restituisce il nome (multisocieta abilitato)
 	 * @param code
 	 * @return
 	 */
 	public String getSocietaName(String code) {
 		Societa soc = MultiSocietaMap.getInstance().getSocieta(code);
 		if (soc != null)
 			return soc.getText();
 		else
 			return code; // In caso di societa' non trovata viene restituito il codice
 	}

 	/**
 	 * Ritorna la dimensione della colonna oggetto in una lista titoli di tipo Table in caso di risultato
 	 * di tipo fascicolo o raccoglitore
 	 * @return
 	 */
 	public int getColspanOggettoTitlesTableFascOrRacc() {
 		int colspan = 5;
 		if (!getFormsAdapter().checkBooleanFunzionalitaDisponibile("nascondiprotocollo", false))
 			colspan++;
 		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("multiSocieta", false))
 			colspan++;
 		return colspan;
 	}
 	
 	/**
 	 * Apertura di un modale di visualizzazione di eventuali warning restituti dalla componente service in fase
 	 * di elaborazione di una richiesta
 	 * @param warnings Messaggio di waring restituito dal service
 	 * @return
 	 * @throws Exception
 	 */
	public String openWarningsModal(String warnings) {
		if (warnings != null && !warnings.isEmpty()) {
			Msg message = new Msg();
			message.setActive(true);
			message.setTitle(I18N.mrs("dw4.warning"));
			message.setType(Const.MSG_LEVEL_WARNING);
			message.setMessage(warnings);
	
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			session.setAttribute("msg", message);
		}
		return null;
	}
	
	/**
	 * Ritorna true se tutti i pulsanti relativi a stampe o esportazioni devono essere abilitati, false altrimenti
	 * @return
	 */
	public boolean isStampeEnabled() {
		return DisattivazioneStampeSettings.getInstance().isStampaEnabled();
	}
	
	/**
	 * Ritorna il messaggio di disattivazione delle stampe ed esportazioni in modo da dare indicazioni all'operatore in merito alla
	 * riattivazione delle funzioni.
	 * @return
	 */
	public String getMessageStampeDisabled() {
		return DisattivazioneStampeSettings.getInstance().getActivationMessage();
	}
	
}
