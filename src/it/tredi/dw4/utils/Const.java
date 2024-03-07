package it.tredi.dw4.utils;

public class Const {
	
	public final static String RESOURCE_APP_CONTEXT = "DocWay4";
	
	public final static String ACL_CONTEXT_NAME = "/acl/";
	public final static String DOCWAY_CONTEXT_NAME = "/docway/";
	public final static String DOCWAYPROC_CONTEXT_NAME = "/docwayproc/";

	public final static String ACL_PACKAGE_NAME = "it.tredi.dw4.acl";	
	public final static String DOCWAY_PACKAGE_NAME = "it.tredi.dw4.docway";
	public final static String DOCWAYPROC_PACKAGE_NAME = "it.tredi.dw4.docwayproc";
	public final static String ADDON_PACKAGE_NAME = "it.tredi.dw4.addons";
	
	public final static String TEMPLATE_DEFAULT_FILENAME = "template.xhtml";
	public final static String TEMPLATE_POPUP_FILENAME = "templatePopup.xhtml";
	public final static String TEMPLATE_NOSX_FILENAME = "templateNoSx.xhtml";
	public final static String TEMPLATE_REP_FILENAME = "../dwrep/templateREP.xhtml";
	
	public final static String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public final static String DEFUALT_JQUERY_DATEPICKER_FORMAT = "dd/mm/yy";
	public final static String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy HH.mm";
	public final static String DOCWAY_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	public final static String DATABASE_DATE_FORMAT = "yyyy-MM-dd";
	
	// Stringhe di ritorno di errore restituite da DocWay-Service
	public final static String RITORNO_ESITO_RICERCA_NULLO = "Esito della ricerca nullo.";
	public final static String RITORNO_AMBITO_DI_RICERCA_TROPPO_AMPIO = "Ambito di ricerca troppo ampio!";
	public final static String RITORNO_IMPOSSIBILE_RINVIARE_LE_PROPOSTE_NON_DISCUSSE = "Impossibile rinviare la proposte non discusse";
	
	// Stringhe restituite dal service in caso di errori gestiti dall'applicazione
	public final static String RITORNO_ESITO_INVIO_TELEMATICO_SU_DOC_SENZA_ALLEGATI = "Nessun documento informatico allegato al documento";
	public final static String RITORNO_ESITO_INVIO_MICROSOFT_DYNAMIC_NAV_CON_ERRORE = "Errore durante l'invio a Microsoft Dynamics Nav";	
	
	// Livelli di "errore" di un messaggio
	public final static String MSG_LEVEL_FATAL = "fatal";
	public final static String MSG_LEVEL_ERROR = "error";
	public final static String MSG_LEVEL_WARNING = "warning";
	public final static String MSG_LEVEL_SUCCESS = "success";
	public final static String MSG_LEVEL_INFO = "info";
	
	// Lunghezze esatte dei campi Cod Amm e Cod AOO
	public final static int COD_AMM_LENGTH = 4;
	public final static int COD_AOO_LENGTH = 3;
	
	// Nome e Codice da assegnare al profilo di default di ACL
	public final static String PROFILE_DEFAULT_NAME = "&lt; Default &gt;"; // TODO Andrebbe specificato in un file di properties
	public final static String PROFILE_DEFAULT_CODE = "-1"; 
	
	// Lunghezza del numero di protocollo
	public final static int DOCWAY_NUM_PROT_LENGTH = 7;
	
	// Nome del nodo radice del thesauro del titolario di classificazione
	public final static String TITOLARIO_CLASSIF_NODO_RADICE = "Radice";
	
	// Diritti relativi alle responsabilità su DocWay
	public final static String DOCWAY_DIRITTO_RPA = "RPA";
	public final static String DOCWAY_DIRITTO_RPAM = "RPAM";
	public final static String DOCWAY_DIRITTO_ITF = "ITF";
	public final static String DOCWAY_DIRITTO_OP = "OP";
	public final static String DOCWAY_DIRITTO_OPM = "OPM";
	public final static String DOCWAY_DIRITTO_CC = "CC";
	public final static String DOCWAY_DIRITTO_CDS = "CDS";
	public final static String DOCWAY_DIRITTO_RESO = "RESO";
	
	// Tipologie di documenti di DocWay
	public final static String DOCWAY_TIPOLOGIA_ARRIVO = "arrivo";
	public final static String DOCWAY_TIPOLOGIA_ARRIVO_DIFFERITO = "arrivo_differito";
	public final static String DOCWAY_TIPOLOGIA_PARTENZA = "partenza";
	public final static String DOCWAY_TIPOLOGIA_INTERNO = "interno";
	public final static String DOCWAY_TIPOLOGIA_VARIE ="varie";
	public final static String DOCWAY_TIPOLOGIA_ACQUISIZIONE = "acquisizione";
	public final static String DOCWAY_TIPOLOGIA_ACQUISIZIONE_QRCODE = "acquisizione_qrcode";
	public final static String DOCWAY_TIPOLOGIA_REPERTORIO = "repertorio";
	public final static String DOCWAY_TIPOLOGIA_RACCOGLITORE = "raccoglitore";
	public final static String DOCWAY_TIPOLOGIA_FASCICOLO = "fascicolo";
	public final static String DOCWAY_TIPOLOGIA_FASCICOLO_CUSTOM = "fascicolocustom";
	
	public final static String DOCWAY_EMBEDDED_APP_NAME = "embeddedApp";
	
	public final static String PROPERTY_TITOLO_LUNGHEZZA_MAX_OGGETTO = "titolo.lunghezza.max.oggetto";
	public final static String PROPERTY_TITOLO_LUNGHEZZA_MAX_SOGGETTO = "titolo.lunghezza.max.soggetto";
	
	public final static String PROPERTY_SHARED_SESSION_APPS = "shared.session.apps";
	
	// eventuali configurazioni client da utilizzare su componenti inclusi nel browser (es. IWX, applet firma, ecc.)
	public final static String DOCWAT_CLIENT_CONFIG_OBJECT = "clientConfig";
	
	// valore settato sui campi password per evitare di mostrare sul client una password registrata
	public final static String PWD_SKIP_LABEL = "*PWD_SKIP*";
	
}
