package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.acl.model.Mailbox;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.ConfigStampaSegnatura;
import it.tredi.dw4.docway.model.HomeContentDefinition;
import it.tredi.dw4.docway.model.ExportPersonalizzato;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.VaschettaCustom;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class QueryProfiloPersonale extends DocWayQuery {
	private String xml;
	
	private String r_0048 = ""; // preavviso scadenza
	private boolean r_0049 = false; // immagini in popup
	private String r_0050 = ""; // visualizzazione CC
	private boolean r_0051 = false; // evidenzia CC personali
	private boolean r_0052 = false; // elimina frequenze
	private boolean r_0053 = false; // gestione endorser
	
	private boolean r_iwx01 = false; // disabilita IWX
	
	private boolean enableIW = false;
	
	private List<Mailbox> mailboxes = new ArrayList<Mailbox>(); // configurazione delle mailboxes da profilo personale
	private String defaulthost = "";
	private String defaultport = "";
	private String defaultprotocol = "";
	private List<Option> protocols = new ArrayList<Option>();
	
	// configurazioni di stampa della segnatura
	private ConfigStampaSegnatura segnaturaArrivo = null;
	private ConfigStampaSegnatura segnaturaPartenza = null;
	private ConfigStampaSegnatura segnaturaInterno = null;
	private ConfigStampaSegnatura segnaturaVarie = null;
	
	private List<VaschettaCustom> vaschetteCustom = new ArrayList<VaschettaCustom>();
	
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private List<ExportPersonalizzato> esportazioni;
	
	private String titlesMode = ""; // modalita' di visualizzazione della lista titoli
	
	private HomeContentDefinition homeContent = new HomeContentDefinition(); // contenuto della home di docway
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.xml = dom.asXML();
		
		this.r_0048 = XMLUtil.parseStrictAttribute(dom, "/response/preavviso_scadenza/@val");
		if (XMLUtil.parseStrictAttribute(dom, "/response/immagini_in_popup/@val").equals("true"))
			this.r_0049 = true;
		else
			this.r_0049 = false;
		this.r_0050 = XMLUtil.parseStrictAttribute(dom, "/response/cc_vis_stato_iniziale/@val");
		if (XMLUtil.parseStrictAttribute(dom, "/response/cc_vis_evidenzia_cc_personali/@val").equals("true"))
			this.r_0051 = true;
		else
			this.r_0051 = false;
		if (XMLUtil.parseStrictAttribute(dom, "/response/elimina_frequenze/@val").equals("true"))
			this.r_0052 = true;
		else
			this.r_0052 = false;
		if (XMLUtil.parseStrictAttribute(dom, "/response/abilita_gestione_endorser/@val").equals("true"))
			this.r_0053 = true;
		else
			this.r_0053 = false;
		
		if (XMLUtil.parseStrictAttribute(dom, "/response/disabilita_iwx/@val").equals("true"))
			this.r_iwx01 = true;
		else
			this.r_iwx01 = false;
		
		if (XMLUtil.parseStrictAttribute(dom, "/response/@enableIW").equals("true"))
			this.enableIW = true;
		else
			this.enableIW = false;
		
		// caricamento delle mailbox personali dell'utente
		this.mailboxes = XMLUtil.parseSetOfElement(dom, "/response/mailboxes/mailbox", new Mailbox());
		this.defaultprotocol = XMLUtil.parseStrictAttribute(dom, "/response/mailboxes/@defaultProtocol");
		this.defaulthost = XMLUtil.parseStrictAttribute(dom, "/response/mailboxes/@defaultHost");
		this.defaultport = XMLUtil.parseStrictAttribute(dom, "/response/mailboxes/@defaultPort");
		this.protocols = XMLUtil.parseSetOfElement(dom, "/response/mailboxes/available-protocols/option", new Option());
		
		if (mailboxes.size() == 0) { 
			Mailbox emptyMailbox = new Mailbox();
			emptyMailbox.setHost(defaulthost);
			emptyMailbox.setPort(defaultport);
			emptyMailbox.setProtocol(defaultprotocol);
			mailboxes.add(emptyMailbox);
		}
		
		// caricamento delle vaschette custom definite dall'utente
		this.vaschetteCustom = XMLUtil.parseSetOfElement(dom, "/response/vaschetteCustom/vaschetta", new VaschettaCustom());

		// caricamento delle esportazioni custom
		this.esportazioni = XMLUtil.parseSetOfElement(dom, "/response/exports/export", new ExportPersonalizzato());
		
		// caricamento delle impostazioni di stampa della segnatura (per ogni tipologia di documento)
		String prefs = XMLUtil.parseStrictAttribute(dom, "/response/@appStringPreferences");
		if (prefs.length() > 0) {
			this.segnaturaArrivo = new ConfigStampaSegnatura(AppStringPreferenceUtil.getAppStringPreference(prefs, AppStringPreferenceUtil.decodeAppStringPreference("portaSerialeSegnaturaArrivo")));
			this.segnaturaPartenza = new ConfigStampaSegnatura(AppStringPreferenceUtil.getAppStringPreference(prefs, AppStringPreferenceUtil.decodeAppStringPreference("portaSerialeSegnaturaPartenza")));
			this.segnaturaInterno = new ConfigStampaSegnatura(AppStringPreferenceUtil.getAppStringPreference(prefs, AppStringPreferenceUtil.decodeAppStringPreference("portaSerialeSegnaturaInterno")));
			this.segnaturaVarie = new ConfigStampaSegnatura(AppStringPreferenceUtil.getAppStringPreference(prefs, AppStringPreferenceUtil.decodeAppStringPreference("portaSerialeSegnaturaVarie")));
		}
		
		// modalita' di visualizzazione delle pagine di titoli
		this.titlesMode = XMLUtil.parseStrictAttribute(dom, "/response/titlesMode/@val");
		
		// caricamento della configurazione dell'home page di docway
		this.homeContent.init(XMLUtil.createDocument(dom, "/response/homeContent"));
		if ((this.homeContent.getType() == null || this.homeContent.getType().isEmpty())
				&& (this.homeContent.getTitle() == null || this.homeContent.getTitle().isEmpty()))
			this.homeContent.setTitle(I18N.mrs("dw4.documenti_recenti"));
    }	
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getR_0048() {
		return r_0048;
	}

	public void setR_0048(String preavviso_scadenza) {
		this.r_0048 = preavviso_scadenza;
	}
	
	public boolean isR_0049() {
		return r_0049;
	}

	public void setR_0049(boolean checkboxImmaginiInPopup) {
		this.r_0049 = checkboxImmaginiInPopup;
	}
	
	public String getR_0050() {
		return r_0050;
	}

	public void setR_0050(String visualizzazioneCC) {
		this.r_0050 = visualizzazioneCC;
	}
	
	public boolean isR_0051() {
		return r_0051;
	}

	public void setR_0051(boolean checkboxEvidenziaCCPersonali) {
		this.r_0051 = checkboxEvidenziaCCPersonali;
	}
	
	public boolean isR_0052() {
		return r_0052;
	}

	public void setR_0052(boolean checkboxEliminaFrequenze) {
		this.r_0052 = checkboxEliminaFrequenze;
	}

	public boolean isR_0053() {
		return r_0053;
	}

	public void setR_0053(boolean checkboxEndorser) {
		this.r_0053 = checkboxEndorser;
	}
	
	public boolean isR_iwx01() {
		return r_iwx01;
	}

	public void setR_iwx01(boolean disabilitaIWX) {
		this.r_iwx01 = disabilitaIWX;
	}
		
	public boolean isEnableIW() {
		return enableIW;
	}

	public void setEnableIW(boolean enableIW) {
		this.enableIW = enableIW;
	}
	
	public List<Mailbox> getMailboxes() {
		return mailboxes;
	}

	public void setMailboxes(List<Mailbox> mailboxes) {
		this.mailboxes = mailboxes;
	}
	
	public String getDefaulthost() {
		return defaulthost;
	}

	public void setDefaulthost(String defaulthost) {
		this.defaulthost = defaulthost;
	}

	public String getDefaultport() {
		return defaultport;
	}

	public void setDefaultport(String defaultport) {
		this.defaultport = defaultport;
	}

	public String getDefaultprotocol() {
		return defaultprotocol;
	}

	public void setDefaultprotocol(String defaultprotocol) {
		this.defaultprotocol = defaultprotocol;
	}

	public List<Option> getProtocols() {
		return protocols;
	}

	public void setProtocols(List<Option> protocols) {
		this.protocols = protocols;
	}
	
	public List<VaschettaCustom> getVaschetteCustom() {
		return vaschetteCustom;
	}

	public void setVaschetteCustom(List<VaschettaCustom> vaschetteCustom) {
		this.vaschetteCustom = vaschetteCustom;
	}
	
	public ConfigStampaSegnatura getSegnaturaArrivo() {
		return segnaturaArrivo;
	}

	public void setSegnaturaArrivo(ConfigStampaSegnatura segnaturaArrivo) {
		this.segnaturaArrivo = segnaturaArrivo;
	}

	public ConfigStampaSegnatura getSegnaturaPartenza() {
		return segnaturaPartenza;
	}

	public void setSegnaturaPartenza(ConfigStampaSegnatura segnaturaPartenza) {
		this.segnaturaPartenza = segnaturaPartenza;
	}

	public ConfigStampaSegnatura getSegnaturaInterno() {
		return segnaturaInterno;
	}

	public void setSegnaturaInterno(ConfigStampaSegnatura segnaturaInterno) {
		this.segnaturaInterno = segnaturaInterno;
	}

	public ConfigStampaSegnatura getSegnaturaVarie() {
		return segnaturaVarie;
	}

	public void setSegnaturaVarie(ConfigStampaSegnatura segnaturaVarie) {
		this.segnaturaVarie = segnaturaVarie;
	}
	
	public String getTitlesMode() {
		return titlesMode;
	}

	public void setTitlesMode(String titlesMode) {
		this.titlesMode = titlesMode;
	}

	public HomeContentDefinition getHomeContent() {
		return homeContent;
	}

	public void setHomeContent(HomeContentDefinition homeContent) {
		this.homeContent = homeContent;
	}

	public QueryProfiloPersonale() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	/**
	 * Salvataggio del profilo personale
	 * 
	 * @return
	 * @throws Exception
	 */
	public String saveProfilo() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// per ogni campo password non impostato viene settato il 
			// valore '*PWD_SKIP*' che indica che il campo sull'XML non deve
			// essere aggiornato con il valore passato nel parametro della request
			for (int i=0; i<mailboxes.size(); i++) {
				if (mailboxes.get(i) != null 
							&& mailboxes.get(i).getPassword() != null
							&& mailboxes.get(i).getPassword().equals(""))
					mailboxes.get(i).setPassword(Const.PWD_SKIP_LABEL); // TODO attualmente non utilizzato
			}
			
			// configurazioni di stampa per la segnatura
			String info = getProfiliStampaInfo();
			
			String rights = "0048=" + r_0048; // giorni di preavviso
			rights += ";0049=" + r_0049; // immagini in popup
			if (formsAdapter.checkBooleanFunzionalitaDisponibile("raggruppaCC", false))
				rights += ";0050=" + r_0050; // visualizzazione CC
			rights += ";0051=" + r_0051; // evidenzia CC personali
			rights += ";0052=" + r_0052; // elimina frequenze
			if (formsAdapter.checkBooleanFunzionalitaDisponibile("abilitaGestioneEndorser", false))
				rights += ";0053=" + r_0053; // gestione endorser
			rights += ";IWX01=" + r_iwx01; // disabilita iwx
			
			// aggiornamento in sessione della modalita' di caricamento dei titoli
			if (titlesMode != null && !titlesMode.isEmpty())
				setSessionAttribute("titlesmode", titlesMode);
			
			UserBean userBean = getUserBean();
			formsAdapter.saveProfilo(rights, info, titlesMode, homeContent, mailboxes, vaschetteCustom, esportazioni);	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(userBean);
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// set session parameter x propagazione su tutti i forms adapter
			// TODO corretto? non viene mai azzerato in questo modo...
			getUserBean().setForcedServiceFormParam("appStringPreferences", XMLUtil.parseStrictAttribute(response.getDocument(), "/response/@appStringPreferences"));
			
			DocWayHome docwayHome = new DocWayHome();
			docwayHome.getFormsAdapter().fillFormsFromResponse(response);
			docwayHome.init(response.getDocument());
			setSessionAttribute("docwayHome", docwayHome);
			
			return "show@docway_home";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * ritorna il valore da impostare nel parametro xMode per il salvataggio dei profili di 
	 * stampa di segnatura (salvataggio da profilo personale)
	 * @return
	 */
	private String getProfiliStampaInfo() {
		String info = "";
		//DD 19/04/06  Errori salvataggio profilo personale (RW: 0036209)
	    
		if (segnaturaArrivo != null && segnaturaArrivo.getType() != null
				&& segnaturaPartenza != null && segnaturaPartenza.getType() != null
				&& segnaturaInterno != null && segnaturaInterno.getType() != null
				&& segnaturaVarie != null && segnaturaVarie.getType() != null) {
			
			// Federico 03/11/08: mancava la gestione della stampante per la segnatura dei non protocollati [RW 0050012]
			info = "portaSerialeSegnaturaArrivo=$ARR;portaSerialeSegnaturaPartenza=$PART;portaSerialeSegnaturaInterno=$INT;portaSerialeSegnaturaVarie=$VAR";
			
			info = info.replace("$ARR", segnaturaArrivo.getXmodeValue());
			info = info.replace("$PART", segnaturaPartenza.getXmodeValue());
			info = info.replace("$INT", segnaturaInterno.getXmodeValue());
			info = info.replace("$VAR", segnaturaVarie.getXmodeValue());
		}
		
		return info;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = false;
		
		// controllo sui campi obbligatori delle mailbox configurate
		if (mailboxes != null && mailboxes.size() > 0) {
			for (int i=0; i<mailboxes.size(); i++) {
				Mailbox mailbox = (Mailbox) mailboxes.get(i);
				if (mailbox != null && mailbox.getEmail() != null && !mailbox.getEmail().equals("")) {
					// se e' stato valorizzato il campo login della 
					// mailbox si procede con il controllo di tutti i campi presenti
					
					// controllo su campo email mailbox
					if (mailbox.getLogin() == null || mailbox.getLogin().equals("")) {
						this.setErrorMessage("templateForm:mailboxes:" + i + ":mailboxEmail", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.email") + "'");
						result = true;
					}
					
					// controllo su campo login mailbox
					if (mailbox.getLogin() == null || mailbox.getLogin().equals("")) {
						this.setErrorMessage("templateForm:mailboxes:" + i + ":mailboxLogin", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.login") + "'");
						result = true;
					}
					
					// controllo su campo password mailbox
					if (mailbox.getPassword() == null || mailbox.getPassword().equals("")) {
						this.setErrorMessage("templateForm:mailboxes:" + i + ":mailboxPassword", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.password") + "'");
						result = true;
					}
					
					// controllo su campo protocollo mailbox
					if (mailbox.getProtocol() == null || mailbox.getProtocol().equals("")) {
						this.setErrorMessage("", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.protocol") + "'");
						result = true;
					}
					
					// controllo su campo host mailbox
					if (mailbox.getHost() == null || mailbox.getHost().equals("")) {
						this.setErrorMessage("templateForm:mailboxes:" + i + ":mailboxHost", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.host") + "'");
						result = true;
					}
					
					// controllo su campo porta mailbox
					if (mailbox.getPort() == null || mailbox.getPort().equals("")) {
						this.setErrorMessage("templateForm:mailboxes:" + i + ":mailboxPort", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.port") + "'");
						result = true;
					}
					else if (!StringUtil.isNumber(mailbox.getPort())) {
						this.setErrorMessage("templateForm:mailboxes:" + i + ":mailboxPort", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " '" + I18N.mrs("acl.port") + "'");
						result = true;
					}
				}
			}
		}
		
		// controllo sui campi delle configurazioni di stampa
		if (segnaturaArrivo != null && segnaturaArrivo.getType() != null && segnaturaArrivo.getType().equals("seriale")) {
			if (segnaturaArrivo.getPortaCom() == null || segnaturaArrivo.getPortaCom().equals("")) {
				this.setErrorMessage("templateForm:segnaturaArrivoPortaCom", I18N.mrs("acl.requiredfield") + " 'COM'");
				result = true;
			}
			else if (!StringUtil.isNumber(segnaturaArrivo.getPortaCom())) {
				this.setErrorMessage("templateForm:segnaturaArrivoPortaCom", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " 'COM'");
				result = true;
			}
		}
		if (segnaturaPartenza != null && segnaturaPartenza.getType() != null && segnaturaPartenza.getType().equals("seriale")) {
			if (segnaturaPartenza.getPortaCom() == null || segnaturaPartenza.getPortaCom().equals("")) {
				this.setErrorMessage("templateForm:segnaturaPartenzaPortaCom", I18N.mrs("acl.requiredfield") + " 'COM'");
				result = true;
			}
			else if (!StringUtil.isNumber(segnaturaPartenza.getPortaCom())) {
				this.setErrorMessage("templateForm:segnaturaPartenzaPortaCom", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " 'COM'");
				result = true;
			}
		}
		if (segnaturaInterno != null && segnaturaInterno.getType() != null && segnaturaInterno.getType().equals("seriale")) {
			if (segnaturaInterno.getPortaCom() == null || segnaturaInterno.getPortaCom().equals("")) {
				this.setErrorMessage("templateForm:segnaturaInternoPortaCom", I18N.mrs("acl.requiredfield") + " 'COM'");
				result = true;
			}
			else if (!StringUtil.isNumber(segnaturaInterno.getPortaCom())) {
				this.setErrorMessage("templateForm:segnaturaInternoPortaCom", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " 'COM'");
				result = true;
			}
		}
		if (segnaturaVarie != null && segnaturaVarie.getType() != null && segnaturaVarie.getType().equals("seriale")) {
			if (segnaturaVarie.getPortaCom() == null || segnaturaVarie.getPortaCom().equals("")) {
				this.setErrorMessage("templateForm:segnaturaVariePortaCom", I18N.mrs("acl.requiredfield") + " 'COM'");
				result = true;
			}
			else if (!StringUtil.isNumber(segnaturaVarie.getPortaCom())) {
				this.setErrorMessage("templateForm:segnaturaVariePortaCom", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " 'COM'");
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * Caricamento della pagina di modifica della password utente
	 * 
	 * @return
	 * @throws Exception
	 */
	public String goToChangePwd() throws Exception {
		try {
			formsAdapter.goToChangePwd("");	
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			DocWayChangePassword docwayChangePassword = new DocWayChangePassword();
			docwayChangePassword.getFormsAdapter().fillFormsFromResponse(response);
			docwayChangePassword.init(response.getDocument());
			setSessionAttribute("docwayChangePassword", docwayChangePassword);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Aggiunta di una nuova mailbox
	 * @return
	 * @throws Exception
	 */
	public String addMailbox() throws Exception {
		Mailbox mailbox = (Mailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int index = mailboxes.indexOf(mailbox);
		if (index == mailboxes.size()-1)
			mailboxes.add(index+1, new Mailbox());
		else 
			mailboxes.add(new Mailbox());
		
		return null;
	}
	
	/**
	 * Cancellazione di una mailbox precedentemente configurata
	 * @return
	 * @throws Exception
	 */
	public String deleteMailbox() throws Exception {
		Mailbox mailbox = (Mailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		mailboxes.remove(mailbox);
		if (mailboxes.size() == 0) mailboxes.add(new Mailbox());
		
		return null;
	}
	
	/**
	 * Spostamento in alto di una mailbox configurata
	 * @return
	 * @throws Exception
	 */
	public String moveUpMailbox() throws Exception {
		Mailbox mailbox = (Mailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int index = mailboxes.indexOf(mailbox);
		if (index > 0 ) {
			mailboxes.remove(index);
			mailboxes.add(index-1, mailbox);
		}
		return null;
	}
	
	/**
	 * Spostamento in basso di una mailbox configurata
	 * @return
	 * @throws Exception
	 */
	public String moveDownMailbox() throws Exception {
		Mailbox mailbox = (Mailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int index = mailboxes.indexOf(mailbox);
		if (index < mailboxes.size()-1 ) {
			mailboxes.remove(index);
			mailboxes.add(index+1, mailbox);
		}
		return null;
	}
	
	/**
	 * Aggiunta di una nuova vaschetta custom
	 * @return
	 * @throws Exception
	 */
	public String addVaschettaCustom() throws Exception {
		VaschettaCustom vaschetta = (VaschettaCustom) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("vaschetta");
		int index = vaschetteCustom.indexOf(vaschetta);
		if (index == vaschetteCustom.size()-1)
			vaschetteCustom.add(index+1, new VaschettaCustom());
		else 
			vaschetteCustom.add(new VaschettaCustom());
		
		return null;
	}
	
	/**
	 * Cancellazione di una vaschetta custom precedentemente configurata
	 * @return
	 * @throws Exception
	 */
	public String deleteVaschettaCustom() throws Exception {
		VaschettaCustom vaschetta = (VaschettaCustom) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("vaschetta");
		vaschetteCustom.remove(vaschetta);
		
		return null;
	}
	
	/**
	 * Cancellazione di un'esportazione custom precedentemente configurata
	 * @return
	 * @throws Exception
	 */
	public String deleteEsportazione() throws Exception {
		ExportPersonalizzato esportazione = (ExportPersonalizzato) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("esportazione");
		this.esportazioni.remove(esportazione);
		
		return null;
	}
	
	/**
	 * Spostamento in alto di una vaschetta configurata
	 * @return
	 * @throws Exception
	 */
	public String moveUpVaschettaCustom() throws Exception {
		VaschettaCustom vaschetta = (VaschettaCustom) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("vaschetta");
		int index = vaschetteCustom.indexOf(vaschetta);
		if (index > 0 ) {
			vaschetteCustom.remove(index);
			vaschetteCustom.add(index-1, vaschetta);
		}
		return null;
	}
	
	/**
	 * Spostamento in basso di una vaschetta configurata
	 * @return
	 * @throws Exception
	 */
	public String moveDownVaschettaCustom() throws Exception {
		VaschettaCustom vaschetta = (VaschettaCustom) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("vaschetta");
		int index = vaschetteCustom.indexOf(vaschetta);
		if (index < vaschetteCustom.size()-1 ) {
			vaschetteCustom.remove(index);
			vaschetteCustom.add(index+1, vaschetta);
		}
		return null;
	}
	
	/**
	 * Spostamento in alto di un'esportazione configurata
	 * @return
	 * @throws Exception
	 */
	public String moveUpEsportazione() throws Exception {
		ExportPersonalizzato esportazione = (ExportPersonalizzato) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("esportazione");
		int index = esportazioni.indexOf(esportazione);
		if (index > 0 ) {
			esportazioni.remove(index);
			esportazioni.add(index-1, esportazione);
		}
		return null;
	}
	
	/**
	 * Spostamento in basso di un'esportazione configurata
	 * @return
	 * @throws Exception
	 */
	public String moveDownEsportazione() throws Exception {
		ExportPersonalizzato esportazione = (ExportPersonalizzato) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("esportazione");
		int index = esportazioni.indexOf(esportazione);
		if (index < esportazioni.size()-1 ) {
			esportazioni.remove(index);
			esportazioni.add(index+1, esportazione);
		}
		return null;
	}
	
	public List<ExportPersonalizzato> getEsportazioni() {
		return esportazioni;
	}
	
	/**
	 * indica se visualizzare o meno il pulsante di switch della modalita' di visualizzazione dei titoli (da
	 * lista a tabella o viceversa)
	 * 
	 * @return true se il pulsante deve essere visualizzato, false altrimenti
	 */
	public boolean isTitlesSwitchEnabled() {
		if (DocWayProperties.readProperty("titles.mode.switch.enabled", "no").toLowerCase().equals("si"))
			return true;
		else
			return false;
	}
	
}
