package it.tredi.dw4.docway.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.Errore;
import it.tredi.dw4.beans.Errormsg;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Conservazione;
import it.tredi.dw4.docway.model.ConservazioneBridge;
import it.tredi.dw4.docway.model.CoupleUfficioVisibilita;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.docway.model.LivelloVisibilita;
import it.tredi.dw4.docway.model.MezzoTrasmissione;
import it.tredi.dw4.docway.model.NumeroRaccomandata;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.docway.model.RifEsterno;
import it.tredi.dw4.docway.model.Tipologia;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.AppStringPreferenceUtil;
import it.tredi.dw4.utils.AppUtil;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

/**
 * DocEdit di un doc generico di DocWay
 *
 * @author mbernardini
 */
public abstract class DocEditDoc extends DocWayDocedit {

	protected DocDocWayDocEditFormsAdapter formsAdapter;
	protected String xml = "";

	protected String docEditTitle = ""; // Titolo della pagina di inserimento/modifica del doc (tipo doc o descrizione del repertorio)

	// Parametri necessari alla generazione della maschera di inserimento/modifica di un doc
	protected boolean doceditRep = false;
	protected boolean copyIfNotRipetiInFascicolo = true;
	protected boolean docIsNotInFascicolo = false;
	protected boolean docIsNotInFascicoloMinuta = false;
	protected boolean copyIfNotStandardRep = false;
	protected boolean notRaccoglitore = true;

	protected String codiceRepertorio = "";
	protected String descrizioneRepertorio = "";
	protected String toDo = "";
	protected String hideDivs = "";

	protected String tipoVerificaDuplicati = ""; // Identifica la tipologia di verifica dei duplicati (tramite popup o solo esistenza)
	protected boolean duplicatiPresenti = false; // Vale true se esistono duplicati del doc corrente, false altrimenti
	protected boolean duplicatoVerificato = false; // Identifica se e' stata verificata la presenza di un eventuale duplicato del doc che si sta registrando
	protected String funzDispVerificaDuplicati = ""; // Legge il valore (in stringa) di funzionalita' disponibili di 'verificaDuplicati'

	protected boolean enableIW = true; // abilita/disabilita l'utilizzo di IW da parte del client

	// parametri necessari alla generazione della sezione di gestione doc informatici (files, immagini)
	protected boolean docInformaticiEnabled = false;
	protected boolean docInformaticiEnabledSenzaFiles = false;
	protected boolean docInformaticiEnabledConImmagini = false;

	protected boolean protocolloDifferito = false; // gestione del protocollo differito sul documento

	protected String classifOD = "";

	protected boolean warningSeNessunAllegato = false; // indica se occorre mostrare un warning all'operatore nel caso in cui il documento non contenga alcun allegato

	private boolean corpoEmailVisibile = false; // attivazione del campo Corpo Email

	private String personalViewToUse = "";

	// mbernardini 10/11/2015 : livelli di visibilita' custom
	private List<LivelloVisibilita> livelliVisibilita = new ArrayList<LivelloVisibilita>();
	
	// tiommi 15/01/2018 : coppie uffici | livello di visibilita da forzare in salvataggio (se RPA appartiene all'ufficio)
	private List<CoupleUfficioVisibilita> listaCoppieUfficioVisibilita = new ArrayList<CoupleUfficioVisibilita>();
	private boolean overrideLivelloVisibilitaNeeded = false;
	private String infoMessageLivelloVisibilitaUff = "";
	private String nomeVisibilita = "";
	
	public abstract Doc getDoc();

	// identifica se si sta eseguendo un inserimento (docEdit) o una modifica (docEditModify) di
	// un doc di DocWay
	public abstract boolean isDocEditModify();

	public DocEditDoc() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	/**
	 * Inizializzazione della docEdit common (per tutte le tipologie
	 * di documenti di DocWay)
	 *
	 * @param domDocumento
	 */
	protected void initCommon(Document domDocumento) {
		setErrorFieldIds(""); // Azzero i campi di error per la scheda corrente

		xml = domDocumento.asXML();

		// Imposto gli altri attributi della response necessari alla generazione
		// della maschera di inserimento di un fascicolo
		codiceRepertorio = XMLUtil.parseStrictAttribute(domDocumento, "/response/@codice_rep");
		descrizioneRepertorio = XMLUtil.parseStrictAttribute(domDocumento, "/response/@descrizione_rep");

		setToDo(XMLUtil.parseStrictAttribute(domDocumento, "/response/@toDo"));

		// elenco di div da nascondere nella maschera di inserimento (per il momento gestito solo 'indirizzo_fax')
		if (toDo != null && toDo.startsWith("#hideDivs"))
			hideDivs = toDo.substring(10, toDo.length()-1);

		if (XMLUtil.countElements(domDocumento, "/response/doc/rif_interni/rif[@diritto='RPA']/@cod_fasc") == 0)
			docIsNotInFascicolo = true;

		if (XMLUtil.countElements(domDocumento, "/response/doc/rif_interni/rif[@diritto='RPAM']/@cod_fasc") == 0)
			docIsNotInFascicoloMinuta = true;

		if (codiceRepertorio == null || codiceRepertorio.length() == 0)
			doceditRep = false;
		else
			doceditRep = true;

		if (getToDo() != null && (getToDo().equals("ripetiinfascicolo") || getToDo().equals("replicaDocInfascicolo")))
			copyIfNotRipetiInFascicolo = false;

		String dbTable = XMLUtil.parseStrictAttribute(domDocumento, "/response/@dbTable");
		if (dbTable != null && dbTable.length() > 0 && dbTable.equals("@raccoglitore"))
			notRaccoglitore = false;

		if (codiceRepertorio == null || codiceRepertorio.equals("") || codiceRepertorio.equals("PP")
				|| codiceRepertorio.startsWith("MDx") || codiceRepertorio.startsWith("CONTR") || codiceRepertorio.startsWith("PCONTR")
				|| codiceRepertorio.startsWith("PMDx") || codiceRepertorio.equals("CO_AR") || codiceRepertorio.equals("CO_AM")
				|| codiceRepertorio.equals("NOL") || codiceRepertorio.equals("F_ATT") || codiceRepertorio.equals("DIS_BA")
				|| codiceRepertorio.equals("DEN_FIS") || codiceRepertorio.equals("ORD_ACQ") || codiceRepertorio.equals("OFF")
				|| codiceRepertorio.equals("ORD") || codiceRepertorio.equals("FTR")
				|| codiceRepertorio.equals("FTRPAA")) {
			copyIfNotStandardRep = true;
		}

		getDoc().setCod_amm_aoo(XMLUtil.parseStrictAttribute(domDocumento, "/response/@cod_sede"));
		getDoc().setAnno(XMLUtil.parseStrictAttribute(domDocumento, "/response/@currYear"));

		// caricamento di eventuali preferenze dell'applicazione (es. lunghezza min. del campo oggetto)
		setAppStringPreferences(XMLUtil.parseStrictAttribute(domDocumento, "/response/@appStringPreferences"));

		// controllo di verifica dei duplicati
		setFunzDispVerificaDuplicati(XMLUtil.parseStrictAttribute(domDocumento, "/response/funzionalita_disponibili/@verificaDuplicati"));

		// gestione doc informatici
		enableIW = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(domDocumento, "/response/@enableIW"));
		setResetJobsIWX(true); // in fase di init il reset dei jobs di IWX deve essere sempre fatto
		int numFiles = XMLUtil.countElements(domDocumento, "/response/doc/files/xw:file");
		int numImmagini = XMLUtil.countElements(domDocumento, "/response/doc/immagini/xw:file");
		if (getDoc().isBozza() || getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_VARIE)
				|| formsAdapter.checkBooleanFunzionalitaDisponibile("forceAddAttach", false) || numImmagini == 0
				|| formsAdapter.getDefaultForm().getParam("toDo").equals("replicaDoc")
				|| formsAdapter.getDefaultForm().getParam("toDo").equals("replicaDocInfascicolo")) // in caso di trasformazione da doc non protocollato deve sempre essere possibile operare sugli allegati
			docInformaticiEnabled = true;
		if (getDoc().isBozza() || getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_VARIE)
				|| formsAdapter.checkBooleanFunzionalitaDisponibile("forceAddAttach", false) || numFiles == 0
				|| formsAdapter.getDefaultForm().getParam("toDo").equals("replicaDoc")
				|| formsAdapter.getDefaultForm().getParam("toDo").equals("replicaDocInfascicolo")) // in caso di trasformazione da doc non protocollato deve sempre essere possibile operare sugli allegati
			docInformaticiEnabledSenzaFiles = true;
		if (numImmagini > 0)
			docInformaticiEnabledConImmagini = true;

		// verifica di protocollo differito (caso di ripeti nuovo da protocollo differito)
		if (getDoc().getProt_differito() != null && getDoc().getProt_differito().getData_arrivo() != null && getDoc().getProt_differito().getData_arrivo().length() > 0)
			protocolloDifferito = true;

		// stato iniziale del checkbox "Invio Email di notifica"
		String prefCheckboxMail = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("CheckboxEmailNotifica"));
		if (prefCheckboxMail != null && prefCheckboxMail.toLowerCase().equals("no"))
			getDoc().setSendMailRifInterni(false);

		// ERM012596 - rtirabassi - notifica capillare
		String prefCheckboxMailDetail = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("CheckboxEmailNotificaCapillare"));
		if (prefCheckboxMailDetail != null && prefCheckboxMailDetail.toLowerCase().equals("si")) {
			getDoc().setSendMailRifInterni(false);
			getDoc().setSendMailSelectedRifInterni(true);
		}
		
		// verifica se per il documento corrente e' attivo o meno il warning all'operatore
		// in caso di nessun allegato aggiunto al documento
		String warningSeSenzaAllegato = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("warningSeSenzaAllegato"));
		if (warningSeSenzaAllegato.toLowerCase().contains(dbTable.substring(1, 2).toLowerCase()))
			warningSeNessunAllegato = true;
		else
			warningSeNessunAllegato = false;

		//sstagni - check oggetti diversi
		classifOD = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("notificaOggettiDiversiConClassificazione"));

		// caricamento dei livelli di visibilita' del documento
		livelliVisibilita = XMLUtil.parseSetOfElement(domDocumento, "/response/livelliVisibilita/visibilita", new LivelloVisibilita());

		// controlli sul documento derivanti da ripeti nuovo, nuovo da fasciolo, ecc.
		if (!isDocEditModify()) {
			// mbernardini 21/11/2013 : controllo sul valore di default da associare al check 'bozza'
			boolean bozzeDefValue = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(domDocumento, "/response/@checkBozzeDefaultValue"));
			if (bozzeDefValue
					&& getFormsAdapter().checkBooleanFunzionalitaDisponibile("abilitaBozze", false)
					&& !getFormsAdapter().checkBooleanFunzionalitaDisponibile("disabilitaBozzeInInserimento", false)) {
				getDoc().setBozza(true);
				getDoc().setSendMailRifInterni(false);
			}

			// azzeramento di eventuali dati di conservazione (da non riportare sul nuovo documento)
			getDoc().setConservazione(new Conservazione());
			getDoc().getExtra().setConservazione(new ArrayList<ConservazioneBridge>());

			// in caso di nuovo da fascicolo vengono azzerati i cc del doc, l'oggetto ed eventuali note (che potrebbe portare dal fascicolo)
			/*if (!copyIfNotRipetiInFascicolo) {
				List<Rif> assegnazioneCC = new ArrayList<Rif>();
				assegnazioneCC.add(new Rif());
				getDoc().setAssegnazioneCC(assegnazioneCC);

				getDoc().setOggetto("");

				if (getDoc().getNote() != null)
					getDoc().getNote().setText("");
			}*/

			// assegnazione dei valori di default ai campi select (solo in caso di inserimento)
			if (getDoc().getMezzoTrasmissioneSelect() != null && getDoc().getMezzoTrasmissioneSelect().size() > 0) { // campo mezzo trasmissione
				for (Iterator<Option> iterator = getDoc().getMezzoTrasmissioneSelect().iterator(); iterator.hasNext();) {
					Option option = (Option) iterator.next();
					if (option.getSelected().length()>0) {
						MezzoTrasmissione mezzoTrasmissione = new MezzoTrasmissione();
						mezzoTrasmissione.setCod(option.getValue());
						mezzoTrasmissione.setValue(option.getLabel());
						getDoc().setMezzo_trasmissione(mezzoTrasmissione);
					}
				}
			}
			if (getDoc().getTipologiaSelect() != null && getDoc().getTipologiaSelect().size() > 0) { // campo tipologia
				for (Iterator<Option> iterator = getDoc().getTipologiaSelect().iterator(); iterator.hasNext();) {
					Option option = (Option) iterator.next();
					if (option.getSelected().length()>0) {
						Tipologia tipologia = new Tipologia();
						tipologia.setCod(option.getValue());
						tipologia.setText(option.getLabel());
						getDoc().setTipologia(tipologia);
					}
				}
			}

			// imposto il diritto di intervento su tutti i CDS se richiesto da properties
			if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("interventoConfigurabileSuDoc", false)
					&& getFormsAdapter().checkBooleanFunzionalitaDisponibile("interventoCDSAttivo", false) && getDoc().getAssegnazioneCDS().size() > 0) {
				for (int i=0; i<getDoc().getAssegnazioneCDS().size(); i++) {
					Rif cds = (Rif) getDoc().getAssegnazioneCDS().get(i);
					if (cds != null)
						cds.setIntervento(true);
				}
			}
		}

		// imposta i campi di default di tipo check per il documento
		setDefaultFields(domDocumento);

		// nel caso l'ultimo file/immagini non sia vuoto viene aggiunta una istanza
		// vuota per generare correttamente l'interfaccia di upload file/immagini
		// della sezione doc informatici
		if (getDoc().getFiles().get(getDoc().getFiles().size()-1).getName() != null && !getDoc().getFiles().get(getDoc().getFiles().size()-1).getName().equals(""))
			getDoc().getFiles().add(new XwFile());
		if (getDoc().getImmagini().get(getDoc().getImmagini().size()-1).getName() != null && !getDoc().getImmagini().get(getDoc().getImmagini().size()-1).getName().equals(""))
			getDoc().getImmagini().add(new XwFile());


		// inizializzazione del campi custom da caricare nella pagina
		getCustomfields().init(domDocumento, "doc");

		// fcappelli 20140729 - aggiunta gestione in iwx e sqf uploader dei tipi di file permessi
		String iwxFileFilter = XMLUtil.parseStrictAttribute(domDocumento, "/response/funzionalita_disponibili/@iwxFileFilter");
		fileFilters = parseUploaderFileFilter(iwxFileFilter);
		String iwxImageFilter = XMLUtil.parseStrictAttribute(domDocumento, "/response/funzionalita_disponibili/@iwxImageFilter");
		imageFilters = parseUploaderFileFilter(iwxImageFilter);

		if (codiceRepertorio != null && codiceRepertorio.length() > 0)
			setUploadFilters(codiceRepertorio);
		else if (dbTable != null && dbTable.length() > 0)
			setUploadFilters(dbTable);
		else {
			docInformaticiFileDescription = Page.docInformaticiFileDescriptionDefault;
			docInformaticiFileTypes = Page.docInformaticiFileTypesDefault;
			docInformaticiArchiveTypes = Page.docInformaticiArchiveTypesDefault;
			iwxFileTypes = Page.iwxFileTypesDefault;
			docInformaticiImageDescription = Page.docInformaticiImageTypesDescriptionDefault;
			docInformaticiImageTypes = Page.docInformaticiImageTypesDefault;
			iwxImageTypes = Page.iwxImageTypesDefault;
		}

		// gestione corpo email in doc in partenza
		if (getDoc().getCorpoEmail() != null && getDoc().getCorpoEmail().length() > 0)
			corpoEmailVisibile = true;
		// dpranteda - docwayDelibere
		this.personalViewToUse 		= XMLUtil.parseStrictAttribute(domDocumento, "/response/@personalViewToUse");

		// mbernardini 18/02/2015 : escape di caratteri speciali
		getDoc().setOggetto(StringUtil.convertSpecialCharsEntitiesToCode(getDoc().getOggetto()));
		if (getDoc().getNote() != null)
			getDoc().getNote().setText(StringUtil.convertSpecialCharsEntitiesToCode(getDoc().getNote().getText()));
		// TODO gestire tutti i possibili campi (andrebbe utilizzata una libreria specifica per comprendere tutti i caratteri non gestiti)
		
		//tiommi: 20/11/2017 task #13064 Flag default in Bozza Repertorio
		if(doceditRep && !isDocEditModify() && formsAdapter.checkBooleanFunzionalitaDisponibile("repertorioInBozzaByDefault", false)) // mbernardini 14/03/2018 : il controllo su repertorioInBozzaByDefault deve essere fatto solo in inserimento
			this.getDoc().setBozza(true);
		
		//tiommi: 15/01/2018 funzionalità di aggiunta automatica di visibilità forzata per determinati uffici in RPA
		listaCoppieUfficioVisibilita = XMLUtil.parseSetOfElement(domDocumento, "/response/coppie_ufficio_visibilita/coppia", new CoupleUfficioVisibilita());
		
		// controlla se è necessaria la modifica (entering docEdit)
		showAlertOnCodUffChange();
		
		// mbernardini 07/01/2019 : caricamento di eventuali warning rilevati dalla componente service
		openWarningsModal(domDocumento.getRootElement().attributeValue("warnings", ""));
	}

	/**
	 * Impostazione dei campi di default su docedit di un documento
	 * (utilizzato per i campi boolean)
	 */
	private void setDefaultFields(Document domDocumento) {
		String campiDefault = XMLUtil.parseStrictAttribute(domDocumento, "/response/@campiDefault");
		if (campiDefault != null && !campiDefault.equals("")) {
			String[] campi_v = campiDefault.split("\\|");
			if (campi_v != null && campi_v.length > 0) {
				for (int i=0; i<campi_v.length; i++) {
					String entry = campi_v[i];
		            String campo = entry.substring(0, entry.indexOf(";"));
		            String valore = entry.substring(entry.indexOf(";") + 1);

		            // gestione di casi particolari (non associati ad un campo effettivo del doc)
		            if (campo.equals("*agent_pdf")) {
		            	if (valore.equals("checked"))
		            		getDoc().setAgent_pdf(true);
		            }
		            else if (campo.equals("*iagent_pdf")) {
		            	if (valore.equals("checked"))
		            		getDoc().setIagent_pdf(true);
		            }
		            else if (campo.equals("*agent_xml")) {
		            	if (valore.equals("checked"))
		            		getDoc().setAgent_xml(true);
		            }
		            else if (campo.equals("*agent_ocr")) {
		            	if (valore.equals("checked"))
		            		getDoc().setAgent_ocr(true);
		            }
		            else if (!isDocEditModify()) {
		            	// gestione di campi del doc (boolean e non) da gestire solo in caso di inserimento
		            	try {
							String []splitL = campo.split("\\.");
							Object obj = getDoc();
							for (int splitLindex = 1; splitLindex < splitL.length - 1; splitLindex++) { // parto da 1 perche' il primo livello e' 'doc' (documento stesso)
								String propertyName = splitL[splitLindex];
								String index = "";
								if (propertyName.startsWith("@"))
									propertyName = propertyName.substring(1);
								if (propertyName.endsWith("]")){
									index = propertyName.substring(propertyName.indexOf("[")+1, propertyName.length()-1);
									propertyName = propertyName.substring(0, propertyName.indexOf("["));
								}
								if (propertyName.length() > 0) {
									Method getter = new PropertyDescriptor(propertyName, obj.getClass()).getReadMethod();
									obj = getter.invoke(obj);

									if (!"".equals(index)){
										obj = ((ArrayList<?>)obj).get(Integer.valueOf(index));
									}
								}
							}
							String propertyName = splitL[splitL.length - 1];
							if (propertyName.startsWith("@"))
								propertyName = propertyName.substring(1);

							Method setter = new PropertyDescriptor(propertyName, obj.getClass()).getWriteMethod();

							if (valore.equals("checked"))
								obj = setter.invoke(obj, true);
							else
								obj = setter.invoke(obj, valore);
						}
						catch (Throwable t) {
							// Errore nel formato del campo di default specificato nel file
							// di properties di docway
							Logger.error("errore nell'assegnazione di un campo di default in docEdit: " + entry, t);
						}
		            }
				}
			}
		}
	}
	
	@Override
	protected XMLDocumento _saveDocument(String pne, String pnce) throws Exception {
		// mbernardini 07/02/2018 : trasformazione di un doc in repertorio tramite docEdit
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("trasformaByDocEdit", false)) {
			getFormsAdapter().getDefaultForm().addParam("trasformaByDocEdit", true);
			
			if (getDoc() != null && getDoc().getRepertorio() != null)
				getFormsAdapter().getDefaultForm().addParams(getDoc().getRepertorio().asFormAdapterParamsTrasformaRep(".repertorio"));
		}
		return super._saveDocument(pne, pnce);
	}

	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();

			if (!isDocEditModify()) {
				return clearDocumentoIfIsNotDocEditModify(response);
			}
			else {
				// caso di modifica di un doc
				buildSpecificShowdocPageAndReturnNavigationRule(getDoc().getTipo(), response);
				return "showdoc@" + getDoc().getTipo() + "@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Annullamento dell'azione di trasformazione di un documento in repertorio (tramite pagina di docEdit)
	 * @return
	 * @throws Exception
	 */
	public String clearTrasformaRep() throws Exception {
		try {
			setClassificazioneDaTitolario(true);
			setShowRaccomandataFields(false);
			
			getFormsAdapter().clearTrasformaRep();
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			
			String navigationRule = buildSpecificShowdocPageAndReturnNavigationRule(getDoc().getTipo(), response);
			if (navigationRule != null && !navigationRule.endsWith("@reload"))
				navigationRule += "@reload";
			return navigationRule;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	protected String clearDocumentoIfIsNotDocEditModify(XMLDocumento response) throws Exception {
		// azzeramento delle variabili di verifica dei duplicati
		duplicatiPresenti = false;
		duplicatoVerificato = false;

		// azzeramento delle variabili di inserimenti non standard
		doceditRep = false;
		docIsNotInFascicolo = false;
		docIsNotInFascicoloMinuta = false;
		copyIfNotStandardRep = false;
		notRaccoglitore = true;

		// caso di inserimento di un doc
		getFormsAdapter().fillFormsFromResponse(response);
		this.init(response.getDocument());

		// forzatura di azzeramento di alcune varibili di inserimento in caso di tasto 'Pulisci'
		copyIfNotRipetiInFascicolo = true;

		return null;
	}

	@Override
	public DocDocWayDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public boolean isDuplicatoVerificato() {
		return duplicatoVerificato;
	}

	public void setDuplicatoVerificato(boolean duplicatoVerificato) {
		this.duplicatoVerificato = duplicatoVerificato;
	}

	public String getFunzDispVerificaDuplicati() {
		return funzDispVerificaDuplicati;
	}

	public void setFunzDispVerificaDuplicati(String value) {
		this.funzDispVerificaDuplicati = value;
	}

	public String getTipoVerificaDuplicati() {
		return tipoVerificaDuplicati;
	}

	public void setTipoVerificaDuplicati(String tipoVerificaDuplicati) {
		this.tipoVerificaDuplicati = tipoVerificaDuplicati;
	}

	public boolean isDuplicatiPresenti() {
		return duplicatiPresenti;
	}

	public void setDuplicatiPresenti(boolean existsDuplicati) {
		this.duplicatiPresenti = existsDuplicati;
	}

	public String getCodiceRepertorio() {
		return codiceRepertorio;
	}

	public void setCodiceRepertorio(String codiceRepertorio) {
		this.codiceRepertorio = codiceRepertorio;
	}

	public String getDescrizioneRepertorio() {
		return descrizioneRepertorio;
	}

	public void setDescrizioneRepertorio(String descrizioneRepertorio) {
		this.descrizioneRepertorio = descrizioneRepertorio;
	}

	public String getToDo() {
		return toDo;
	}

	public void setToDo(String toDo) {
		this.toDo = toDo;
	}

	public String getHideDivs() {
		return hideDivs;
	}

	public void setHideDivs(String hideDivs) {
		this.hideDivs = hideDivs;
	}

	public boolean isEnableIW() {
		return enableIW;
	}

	public void setEnableIW(boolean enableIW) {
		this.enableIW = enableIW;
	}

	public boolean isWarningSeNessunAllegato() {
		return warningSeNessunAllegato;
	}

	public void setWarningSeNessunAllegato(boolean warningSeNessunAllegato) {
		this.warningSeNessunAllegato = warningSeNessunAllegato;
	}

	public List<LivelloVisibilita> getLivelliVisibilita() {
		return livelliVisibilita;
	}

	public void setLivelliVisibilita(List<LivelloVisibilita> livelliVisibilita) {
		this.livelliVisibilita = livelliVisibilita;
	}
	
	public List<CoupleUfficioVisibilita> getListaCoppieUfficioVisibilita() {
		return listaCoppieUfficioVisibilita;
	}

	public void setListaCoppieUfficioVisibilita(List<CoupleUfficioVisibilita> listaCoppieUfficioVisibilita) {
		this.listaCoppieUfficioVisibilita = listaCoppieUfficioVisibilita;
	}
	
	public boolean isOverrideLivelloVisibilitaNeeded() {
		return overrideLivelloVisibilitaNeeded;
	}

	public void setOverrideLivelloVisibilitaNeeded(boolean overrideLivelloVisibilitaNeeded) {
		this.overrideLivelloVisibilitaNeeded = overrideLivelloVisibilitaNeeded;
	}

	public boolean isCorpoEmailVisibile() {
		return corpoEmailVisibile;
	}

	public void setCorpoEmailVisibile(boolean corpoEmailVisibile) {
		this.corpoEmailVisibile = corpoEmailVisibile;
	}

	public boolean isProtocolloDifferito() {
		return protocolloDifferito;
	}

	public void setProtocolloDifferito(boolean protocolloDifferito) {
		this.protocolloDifferito = protocolloDifferito;
	}

	public String getClassifOD() {
		return classifOD;
	}

	public void setClassifOD(String classifOD) {
		this.classifOD = classifOD;
	}

	public boolean isDoceditRep() {
		return doceditRep;
	}

	public void setDoceditRep(boolean doceditRep) {
		this.doceditRep = doceditRep;
	}

	public boolean isCopyIfNotRipetiInFascicolo() {
		return copyIfNotRipetiInFascicolo;
	}

	public void setCopyIfNotRipetiInFascicolo(boolean copyIfNotRipetiInFascicolo) {
		this.copyIfNotRipetiInFascicolo = copyIfNotRipetiInFascicolo;
	}

	public boolean isNotRaccoglitore() {
		return notRaccoglitore;
	}

	public void setNotRaccoglitore(boolean notRaccoglitore) {
		this.notRaccoglitore = notRaccoglitore;
	}

	public boolean isDocIsNotInFascicolo() {
		return docIsNotInFascicolo;
	}

	public void setDocIsNotInFascicolo(boolean docIsNotInFascicolo) {
		this.docIsNotInFascicolo = docIsNotInFascicolo;
	}

	public boolean isDocIsNotInFascicoloMinuta() {
		return docIsNotInFascicoloMinuta;
	}

	public void setDocIsNotInFascicoloMinuta(boolean docIsNotInFascicoloMinuta) {
		this.docIsNotInFascicoloMinuta = docIsNotInFascicoloMinuta;
	}

	public boolean isCopyIfNotStandardRep() {
		return copyIfNotStandardRep;
	}

	public void setCopyIfNotStandardRep(boolean copyIfNotStandardRep) {
		this.copyIfNotStandardRep = copyIfNotStandardRep;
	}

	public String getDocEditTitle() {
		if (docEditTitle == null)
			docEditTitle = ""; // non si dovrebbe mai verificare

		return docEditTitle;
	}

	public void setDocEditTitle(String title) {
		this.docEditTitle = title;
	}

	public boolean isDocInformaticiEnabled() {
		return docInformaticiEnabled;
	}

	public void setDocInformaticiEnabled(boolean docInformaticiEnabled) {
		this.docInformaticiEnabled = docInformaticiEnabled;
	}

	public boolean isDocInformaticiEnabledSenzaFiles() {
		return docInformaticiEnabledSenzaFiles;
	}

	public void setDocInformaticiEnabledSenzaFiles(
			boolean docInformaticiEnabledSenzaFiles) {
		this.docInformaticiEnabledSenzaFiles = docInformaticiEnabledSenzaFiles;
	}

	public boolean isDocInformaticiEnabledConImmagini() {
		return docInformaticiEnabledConImmagini;
	}

	public void setDocInformaticiEnabledConImmagini(
			boolean docInformaticiEnabledConImmagini) {
		this.docInformaticiEnabledConImmagini = docInformaticiEnabledConImmagini;
	}
	
	public String getInfoMessageLivelloVisibilitaUff() {
		return infoMessageLivelloVisibilitaUff;
	}

	public void setInfoMessageLivelloVisibilitaUff(String infoMessageLivelloVisibilitaUff) {
		this.infoMessageLivelloVisibilitaUff = infoMessageLivelloVisibilitaUff;
	}
	
	public String getNomeVisibilita() {
		return nomeVisibilita;
	}

	public void setNomeVisibilita(String nomeVisibilita) {
		this.nomeVisibilita = nomeVisibilita;
	}

	/**
	 * Protocollazione del documento
	 * @return
	 * @throws Exception
	 */
	public String protocollaDoc() throws Exception {
		try {
			if (getDoc().isBozza()) { // ulteriore controllo che il doc sia effettivamente in stato di bozza
				boolean isRepertorio = false;
				String codRepertorio = "";
				String descrRepertorio = "";
				if (getDoc().getRepertorio() != null) {
					if (getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0) {
						isRepertorio = true;
						codRepertorio = getDoc().getRepertorio().getCod();
					}
					if (getDoc().getRepertorio().getDescrizione() != null && getDoc().getRepertorio().getDescrizione().length() > 0)
						descrRepertorio = getDoc().getRepertorio().getDescrizione();
				}

				String bozzaDaProt = "DOC";
				if (isRepertorio) {
					bozzaDaProt = bozzaDaProt + ".REP";
				}

				getFormsAdapter().protocollaDoc(bozzaDaProt, codRepertorio, descrRepertorio);

				return saveDocument();
			}
			else {
				// TODO caso da gestire?
				return null;
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}


	/**
	 * Lookup su Voce di Indice
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupVoceIndice() throws Exception {
		String value = (getDoc().getVoce_indice() != null && getDoc().getVoce_indice().getText() != null) ? getDoc().getVoce_indice().getText() : "";

		String aliasName 	= "tit_voce";
		String aliasName1 	= "";
		String titolo 		= "xml,/indice_titolario/@voce"; //titolo
		String ord 			= "xml(xpart:/indice_titolario/@voce)"; //ord
		String campi 		= "validita_tabella=xml,/indice_titolario/validita/@tipodoc" +
								" ; .voce_indice.text=xml,/indice_titolario/@voce" +
								" ; .classif.text=xml,/indice_titolario/compilazione_automatica/classif/@cod \"-\" xml,/indice_titolario/compilazione_automatica/classif" +
								" ; .classif.@cod=xml,/indice_titolario/compilazione_automatica/classif/@cod" +
								" ; .@scarto=xml,/indice_titolario/compilazione_automatica/scarto/@val" +
								" ; scarto_ro=xml,/indice_titolario/compilazione_automatica/scarto/@val" +
								" ; .classif.text_ro=xml,/indice_titolario/compilazione_automatica/classif/@cod \"-\" xml,/indice_titolario/compilazione_automatica/classif" +
								" ; tit_rpa_cc=xml,/indice_titolario/compilazione_automatica/rif/@nome_persona \"^!\" xml,/indice_titolario/compilazione_automatica/rif/@nome_uff \"^!\" xml,/indice_titolario/compilazione_automatica/rif/@diritto \"^!\" xml,/indice_titolario/compilazione_automatica/rif/@tipo_uff" +
								" ; .voce_indice.workflows=xml,/indice_titolario/workflows/bwf_link/@name";
		if (!isDocEditModify() || getFormsAdapter().checkBooleanFunzionalitaDisponibile("oggettoModificabile", false))
			campi 			= campi + " ; .oggetto=xml,/indice_titolario/compilazione_automatica/oggetto";
		String db 			= AppUtil.getXdocwayprocDbName(getFormsAdapter().getDb()); //db
		String newRecord 	= ""; //newRecord
		String xq			= ""; //extraQuery

		// Visto che e' stato selezionato il lookup delle voci di indice
		// attivo l'inserimento della classificazione da titolario (nel caso
		// in cui l'utente avesse utilizzato la classificazione manuale)
		this.setClassificazioneDaTitolario(true);

		callLookup(getDoc(), aliasName, aliasName1, titolo, ord, campi, campi, xq, db, newRecord, value, getDoc().getTipo(), formsAdapter.getDefaultForm().getParam("xverb"));

		return null;
	}

	/**
	 * Pulizia dei campi di Lookup di Voce di Indice
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearLookupVoceIndice() throws Exception {
		// In questo caso non occorre azzerare nulla

		return null;
	}

	/**
	 * Thesauro vincolato su titolario di classificazione
	 *
	 * @return
	 * @throws Exception
	 */
	public String thVincolatoTitolarioClassificazione() throws Exception {
		return showThesRel(false);
	}

	/**
	 * Gerarchia su titolario di classificazione
	 *
	 * @return
	 * @throws Exception
	 */
	public String gerarchiaTitolarioClassificazione() throws Exception {
		return showThesRel(true);
	}

	/**
	 * Caricamento del titolario di classificazione
	 *
	 * @param showGerarchia
	 * @return
	 * @throws Exception
	 */
	private String showThesRel(boolean showGerarchia) throws Exception {
		// in caso di caricamento della gerarchia occorre impostare a false il reset dei jobs di iwx
		// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
		// caricate si perderebbero le anteprime delle immagini
		setResetJobsIWX(false);

		String keypath = "classif";
		String startkey = Const.TITOLARIO_CLASSIF_NODO_RADICE;

		// Filtro impostato su campo codice
		String value = (getDoc().getClassif() != null && !"".equals(getDoc().getClassif().getFiltroCod())) ? getDoc().getClassif().getFiltroCod() : "";
		if (value.length() > 0) {
			// Devo formattare il valore passato in base alla classificazione
			value = ClassifUtil.formatNumberClassifCode(value);

			keypath = "CLASSIF_FROM_CODE";
			startkey = "lookupHierFromClassifCode";
		}

		String toDo = "";
		if (showGerarchia)
			toDo = "print_all";

		callShowThesRel(getDoc(), I18N.mrs("dw4.titolario_di_classificazione"), "NT", keypath, startkey, AppUtil.getXdocwayprocDbName(getFormsAdapter().getDb()), toDo, value);

		// Azzero il campo nel form
		getDoc().getClassif().setFiltroCod("");

		return null;
	}

	/**
	 * RifintLookup su ufficio RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_rpa() throws Exception {
		String value 	= (getDoc().getAssegnazioneRPA() != null && getDoc().getAssegnazioneRPA().getNome_uff() != null) ? getDoc().getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneRPA() != null && getDoc().getAssegnazioneRPA().getNome_persona() != null) ? getDoc().getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";

		// tiommi 17/01/2018 aggiunta logica per trigger sulla modifica del campo cod_uff dell'RPA 
		// per logica di override della visibilità
		if (!listaCoppieUfficioVisibilita.isEmpty()) {
			rifintLookupUfficio(getDoc(), value, value2, campi, this, "showAlertOnCodUffChange");
		}
		else {
			rifintLookupUfficio(getDoc(), value, value2, campi);
		}
		
		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su ufficio RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_rpa() throws Exception {
		String value = (getDoc().getAssegnazioneRPA() != null && getDoc().getAssegnazioneRPA().getNome_uff() != null) ? getDoc().getAssegnazioneRPA().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_uff" +
							"|.assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_uff" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneRPA().setUfficio_completo(false);
		}
		
		overrideLivelloVisibilitaNeeded = false;
		nomeVisibilita = "";
		
		return null;
	}

	/**
	 * RifintLookup su persona RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_rpa() throws Exception {
		String value 	= (getDoc().getAssegnazioneRPA() != null && getDoc().getAssegnazioneRPA().getNome_uff() != null) ? getDoc().getAssegnazioneRPA().getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneRPA() != null && getDoc().getAssegnazioneRPA().getNome_persona() != null) ? getDoc().getAssegnazioneRPA().getNome_persona() : "";
		String campi 	= ".assegnazioneRPA.@nome_uff" +
								"|.assegnazioneRPA.@nome_persona" +
								"|.assegnazioneRPA.@cod_uff" +
								"|.assegnazioneRPA.@cod_persona";

		String xq		= "";
		if (doceditRep)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + codiceRepertorio + "-" + getDoc().getTipoShort() + "-VisRep\")";
		
		// tiommi 17/01/2018 aggiunta logica per trigger sulla modifica del campo cod_uff dell'RPA 
		// per logica di override della visibilità
		if (!listaCoppieUfficioVisibilita.isEmpty()) {
			rifintLookupPersona(getDoc(), value, value2, campi, xq, this, "showAlertOnCodUffChange");
		}
		else {
			rifintLookupPersona(getDoc(), value, value2, campi, xq);
		}
		
		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su persona RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_rpa() throws Exception {
		String value = (getDoc().getAssegnazioneRPA() != null && getDoc().getAssegnazioneRPA().getNome_persona() != null) ? getDoc().getAssegnazioneRPA().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPA.@nome_persona" +
							"|.assegnazioneRPA.@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneRPA().setUfficio_completo(false);
		}
		
		overrideLivelloVisibilitaNeeded = false;
		nomeVisibilita = "";

		return null;
	}

	/**
	 * Lookup su ruolo RPA
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_rpa() throws Exception {
		String value = (getDoc().getAssegnazioneRPA() != null && getDoc().getAssegnazioneRPA().getNome_uff() != null) ? getDoc().getAssegnazioneRPA().getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi 		= ".assegnazioneRPA.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		lookupRuolo(getDoc(), value, campi, getDoc().getCod_amm_aoo());
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

		return clearField(campi, getDoc());
	}

	/**
	 * RifintLookup su ufficio RPAM
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_rpam() throws Exception {
		String value 	= (getDoc().getAssegnazioneRPAM() != null && getDoc().getAssegnazioneRPAM().getNome_uff() != null) ? getDoc().getAssegnazioneRPAM().getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneRPAM() != null && getDoc().getAssegnazioneRPAM().getNome_persona() != null) ? getDoc().getAssegnazioneRPAM().getNome_persona() : "";
		String campi 	= ".assegnazioneRPAM.@nome_uff" +
								"|.assegnazioneRPAM.@nome_persona" +
								"|.assegnazioneRPAM.@cod_uff" +
								"|.assegnazioneRPAM.@cod_persona";

		rifintLookupUfficio(getDoc(), value, value2, campi);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su ufficio RPAM
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_rpam() throws Exception {
		String value = (getDoc().getAssegnazioneRPAM() != null && getDoc().getAssegnazioneRPAM().getNome_uff() != null) ? getDoc().getAssegnazioneRPAM().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPAM.@nome_uff" +
							"|.assegnazioneRPAM.@nome_persona" +
							"|.assegnazioneRPAM.@cod_uff" +
							"|.assegnazioneRPAM.@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneRPAM().setUfficio_completo(false);
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
		String value 	= (getDoc().getAssegnazioneRPAM() != null && getDoc().getAssegnazioneRPAM().getNome_uff() != null) ? getDoc().getAssegnazioneRPAM().getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneRPAM() != null && getDoc().getAssegnazioneRPAM().getNome_persona() != null) ? getDoc().getAssegnazioneRPAM().getNome_persona() : "";
		String campi 	= ".assegnazioneRPAM.@nome_uff" +
								"|.assegnazioneRPAM.@nome_persona" +
								"|.assegnazioneRPAM.@cod_uff" +
								"|.assegnazioneRPAM.@cod_persona";

		String xq		= "";
		if (doceditRep)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + codiceRepertorio + "-" + getDoc().getTipoShort() + "-VisRep\")";

		rifintLookupPersona(getDoc(), value, value2, campi, xq);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su persona RPAM
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_rpam() throws Exception {
		String value = (getDoc().getAssegnazioneRPAM() != null && getDoc().getAssegnazioneRPAM().getNome_persona() != null) ? getDoc().getAssegnazioneRPAM().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneRPAM.@nome_persona" +
							"|.assegnazioneRPAM.@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneRPAM().setUfficio_completo(false);
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
		String value = (getDoc().getAssegnazioneRPAM() != null && getDoc().getAssegnazioneRPAM().getNome_uff() != null) ? getDoc().getAssegnazioneRPAM().getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi 		= ".assegnazioneRPAM.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		lookupRuolo(getDoc(), value, campi, getDoc().getCod_amm_aoo());
		return null;
	}

	/**
	 * Pulizia dei campi di lookup su ruolo RPAM
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_rpam() throws Exception {
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneRPAM.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		return clearField(campi, getDoc());
	}

	/**
	 * RifintLookup su ufficio CC
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_cc() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getDoc().getAssegnazioneCC().contains(rif)) ? getDoc().getAssegnazioneCC().indexOf(rif): 0;

		String value 	= (getDoc().getAssegnazioneCC() != null && getDoc().getAssegnazioneCC().get(num) != null) ? getDoc().getAssegnazioneCC().get(num).getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneCC() != null && getDoc().getAssegnazioneCC().get(num) != null) ? getDoc().getAssegnazioneCC().get(num).getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";

		rifintLookupUfficio(getDoc(), value, value2, campi);
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
		int num = (getDoc().getAssegnazioneCC().contains(rif)) ? getDoc().getAssegnazioneCC().indexOf(rif): 0;

		String value = (getDoc().getAssegnazioneCC() != null && getDoc().getAssegnazioneCC().get(num) != null) ? getDoc().getAssegnazioneCC().get(num).getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_uff" +
							"|.assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_uff" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneCC().get(num).setUfficio_completo(false);
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
		int num = (getDoc().getAssegnazioneCC().contains(rif)) ? getDoc().getAssegnazioneCC().indexOf(rif): 0;

		String value	= (getDoc().getAssegnazioneCC() != null && getDoc().getAssegnazioneCC().get(num) != null) ? getDoc().getAssegnazioneCC().get(num).getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneCC() != null && getDoc().getAssegnazioneCC().get(num) != null) ? getDoc().getAssegnazioneCC().get(num).getNome_persona() : "";
		String campi 	= ".assegnazioneCC["+num+"].@nome_uff" +
								"|.assegnazioneCC["+num+"].@nome_persona" +
								"|.assegnazioneCC["+num+"].@cod_uff" +
								"|.assegnazioneCC["+num+"].@cod_persona";

		String xq		= "";
		if (doceditRep)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + codiceRepertorio + "-" + getDoc().getTipoShort() + "-VisRep\")";

		rifintLookupPersona(getDoc(), value, value2, campi, xq);
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
		int num = (getDoc().getAssegnazioneCC().contains(rif)) ? getDoc().getAssegnazioneCC().indexOf(rif): 0;

		String value = (getDoc().getAssegnazioneCC() != null && getDoc().getAssegnazioneCC().get(num) != null) ? getDoc().getAssegnazioneCC().get(num).getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCC["+num+"].@nome_persona" +
							"|.assegnazioneCC["+num+"].@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneCC().get(num).setUfficio_completo(false);
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
		int num = (getDoc().getAssegnazioneCC().contains(rif)) ? getDoc().getAssegnazioneCC().indexOf(rif): 0;

		String value = (getDoc().getAssegnazioneCC() != null && getDoc().getAssegnazioneCC().get(num) != null) ? getDoc().getAssegnazioneCC().get(num).getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		lookupRuolo(getDoc(), value, campi, getDoc().getCod_amm_aoo());
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
		int num = (getDoc().getAssegnazioneCC().contains(rif)) ? getDoc().getAssegnazioneCC().indexOf(rif): 0;

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCC["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare
		return clearField(campi, getDoc());
	}

	/**
	 * Cancellazione di un rif int in CC
	 * @return
	 */
	public String deleteRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		getDoc().deleteRifintCC(rif);
		return null;
	}

	/**
	 * Aggiunta di un rif int in CC
	 * @return
	 */
	public String addRifInt_cc(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		getDoc().addRifintCC(rif);
		return null;
	}

	/**
	 * Aggiunta di un numero di raccomandata per il doc
	 * @throws Exception
	 */
	public String addNumRaccomandata() throws Exception {
		NumeroRaccomandata numRaccomandata = (NumeroRaccomandata) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("numRaccomandata");
		getDoc().addNumeroRaccomandata(numRaccomandata);
		return null;
	}

	/**
	 * Cancellazione di un numero di raccomandata per il doc
	 * @throws Exception
	 */
	public String deleteNumRaccomandata() throws Exception {
		NumeroRaccomandata numRaccomandata = (NumeroRaccomandata) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("numRaccomandata");
		getDoc().deleteNumeroRaccomandata(numRaccomandata);
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
	 * RifintLookup su ufficio CDS
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_cds() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (getDoc().getAssegnazioneCDS().contains(rif)) ? getDoc().getAssegnazioneCDS().indexOf(rif): 0;

		String value 	= (getDoc().getAssegnazioneCDS() != null && getDoc().getAssegnazioneCDS().get(num) != null) ? getDoc().getAssegnazioneCDS().get(num).getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneCDS() != null && getDoc().getAssegnazioneCDS().get(num) != null) ? getDoc().getAssegnazioneCDS().get(num).getNome_persona() : "";
		String campi 	= ".assegnazioneCDS["+num+"].@nome_uff" +
								"|.assegnazioneCDS["+num+"].@nome_persona" +
								"|.assegnazioneCDS["+num+"].@cod_uff" +
								"|.assegnazioneCDS["+num+"].@cod_persona";

		rifintLookupUfficio(getDoc(), value, value2, campi);
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
		int num = (getDoc().getAssegnazioneCDS().contains(rif)) ? getDoc().getAssegnazioneCDS().indexOf(rif): 0;

		String value = (getDoc().getAssegnazioneCDS() != null && getDoc().getAssegnazioneCDS().get(num) != null) ? getDoc().getAssegnazioneCDS().get(num).getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCDS["+num+"].@nome_uff" +
							"|.assegnazioneCDS["+num+"].@nome_persona" +
							"|.assegnazioneCDS["+num+"].@cod_uff" +
							"|.assegnazioneCDS["+num+"].@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneCDS().get(num).setUfficio_completo(false);
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
		int num = (getDoc().getAssegnazioneCDS().contains(rif)) ? getDoc().getAssegnazioneCDS().indexOf(rif): 0;

		String value	= (getDoc().getAssegnazioneCDS() != null && getDoc().getAssegnazioneCDS().get(num) != null) ? getDoc().getAssegnazioneCDS().get(num).getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneCDS() != null && getDoc().getAssegnazioneCDS().get(num) != null) ? getDoc().getAssegnazioneCDS().get(num).getNome_persona() : "";
		String campi 	= ".assegnazioneCDS["+num+"].@nome_uff" +
								"|.assegnazioneCDS["+num+"].@nome_persona" +
								"|.assegnazioneCDS["+num+"].@cod_uff" +
								"|.assegnazioneCDS["+num+"].@cod_persona";

		String xq		= "";
		if (doceditRep)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + codiceRepertorio + "-" + getDoc().getTipoShort() + "-VisRep\")";

		rifintLookupPersona(getDoc(), value, value2, campi, xq);
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
		int num = (getDoc().getAssegnazioneCDS().contains(rif)) ? getDoc().getAssegnazioneCDS().indexOf(rif): 0;

		String value = (getDoc().getAssegnazioneCDS() != null && getDoc().getAssegnazioneCDS().get(num) != null) ? getDoc().getAssegnazioneCDS().get(num).getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneCDS["+num+"].@nome_persona" +
							"|.assegnazioneCDS["+num+"].@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneCDS().get(num).setUfficio_completo(false);
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
		int num = (getDoc().getAssegnazioneCDS().contains(rif)) ? getDoc().getAssegnazioneCDS().indexOf(rif): 0;

		String value = (getDoc().getAssegnazioneCDS() != null && getDoc().getAssegnazioneCDS().get(num) != null) ? getDoc().getAssegnazioneCDS().get(num).getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCDS["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		lookupRuolo(getDoc(), value, campi, getDoc().getCod_amm_aoo());
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
		int num = (getDoc().getAssegnazioneCDS().contains(rif)) ? getDoc().getAssegnazioneCDS().indexOf(rif): 0;

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneCDS["+num+"].@nome_uff=xml,/ruolo/nome"; // TODO Da completare
		return clearField(campi, getDoc());
	}

	/**
	 * Cancellazione di un rif int in CDS
	 * @return
	 */
	public String deleteRifInt_cds(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		getDoc().deleteRifintCDS(rif);
		return null;
	}

	/**
	 * Aggiunta di un rif int in CDS
	 * @return
	 */
	public String addRifInt_cds(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("interventoCDSAttivo", false))
			rif.setIntervento(true);
		getDoc().addRifintCDS(rif);
		return null;
	}

	/**
	 * RifintLookup su ufficio OP
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_op() throws Exception {
		String value 	= (getDoc().getAssegnazioneOP() != null && getDoc().getAssegnazioneOP().getNome_uff() != null) ? getDoc().getAssegnazioneOP().getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneOP() != null && getDoc().getAssegnazioneOP().getNome_persona() != null) ? getDoc().getAssegnazioneOP().getNome_persona() : "";
		String campi 	= ".assegnazioneOP.@nome_uff" +
								"|.assegnazioneOP.@nome_persona" +
								"|.assegnazioneOP.@cod_uff" +
								"|.assegnazioneOP.@cod_persona";

		rifintLookupUfficio(getDoc(), value, value2, campi);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su ufficio OP
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_op() throws Exception {
		String value = (getDoc().getAssegnazioneOP() != null && getDoc().getAssegnazioneOP().getNome_uff() != null) ? getDoc().getAssegnazioneOP().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneOP.@nome_uff" +
							"|.assegnazioneOP.@nome_persona" +
							"|.assegnazioneOP.@cod_uff" +
							"|.assegnazioneOP.@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneOP().setUfficio_completo(false);
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
		String value 	= (getDoc().getAssegnazioneOP() != null && getDoc().getAssegnazioneOP().getNome_uff() != null) ? getDoc().getAssegnazioneOP().getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneOP() != null && getDoc().getAssegnazioneOP().getNome_persona() != null) ? getDoc().getAssegnazioneOP().getNome_persona() : "";
		String campi 	= ".assegnazioneOP.@nome_uff" +
								"|.assegnazioneOP.@nome_persona" +
								"|.assegnazioneOP.@cod_uff" +
								"|.assegnazioneOP.@cod_persona";

		String xq		= "";
		if (doceditRep)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + codiceRepertorio + "-" + getDoc().getTipoShort() + "-VisRep\")";

		rifintLookupPersona(getDoc(), value, value2, campi, xq);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su persona OP
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_op() throws Exception {
		String value = (getDoc().getAssegnazioneOP() != null && getDoc().getAssegnazioneOP().getNome_persona() != null) ? getDoc().getAssegnazioneOP().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneOP.@nome_persona" +
							"|.assegnazioneOP.@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneOP().setUfficio_completo(false);
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
		String value = (getDoc().getAssegnazioneOP() != null && getDoc().getAssegnazioneOP().getNome_uff() != null) ? getDoc().getAssegnazioneOP().getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi 		= ".assegnazioneOP.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		lookupRuolo(getDoc(), value, campi, getDoc().getCod_amm_aoo());
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
		String campi = ".assegnazioneOP.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		return clearField(campi, getDoc());
	}

	/**
	 * RifintLookup su ufficio OPM
	 *
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_opm() throws Exception {
		String value 	= (getDoc().getAssegnazioneOPM() != null && getDoc().getAssegnazioneOPM().getNome_uff() != null) ? getDoc().getAssegnazioneOPM().getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneOPM() != null && getDoc().getAssegnazioneOPM().getNome_persona() != null) ? getDoc().getAssegnazioneOPM().getNome_persona() : "";
		String campi 	= ".assegnazioneOPM.@nome_uff" +
								"|.assegnazioneOPM.@nome_persona" +
								"|.assegnazioneOPM.@cod_uff" +
								"|.assegnazioneOPM.@cod_persona";

		rifintLookupUfficio(getDoc(), value, value2, campi);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su ufficio OPM
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_opm() throws Exception {
		String value = (getDoc().getAssegnazioneOPM() != null && getDoc().getAssegnazioneOPM().getNome_uff() != null) ? getDoc().getAssegnazioneOPM().getNome_uff() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneOPM.@nome_uff" +
							"|.assegnazioneOPM.@nome_persona" +
							"|.assegnazioneOPM.@cod_uff" +
							"|.assegnazioneOPM.@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneOPM().setUfficio_completo(false);
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
		String value 	= (getDoc().getAssegnazioneOPM() != null && getDoc().getAssegnazioneOPM().getNome_uff() != null) ? getDoc().getAssegnazioneOPM().getNome_uff() : "";
		String value2 	= (getDoc().getAssegnazioneOPM() != null && getDoc().getAssegnazioneOPM().getNome_persona() != null) ? getDoc().getAssegnazioneOPM().getNome_persona() : "";
		String campi 	= ".assegnazioneOPM.@nome_uff" +
								"|.assegnazioneOPM.@nome_persona" +
								"|.assegnazioneOPM.@cod_uff" +
								"|.assegnazioneOPM.@cod_persona";

		String xq		= "";
		if (doceditRep)
			xq			= "([/persona_interna/personal_rights/right/@cod/]=\"_SPECIFICAPPCODE_-" + codiceRepertorio + "-" + getDoc().getTipoShort() + "-VisRep\")";

		rifintLookupPersona(getDoc(), value, value2, campi, xq);

		return null;
	}

	/**
	 * Pulizia dei campi di RifintLookup su persona OPM
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_opm() throws Exception {
		String value = (getDoc().getAssegnazioneOPM() != null && getDoc().getAssegnazioneOPM().getNome_persona() != null) ? getDoc().getAssegnazioneOPM().getNome_persona() : "";

		if (value.length() == 0) {
			String campi = ".assegnazioneOPM.@nome_persona" +
							"|.assegnazioneOPM.@cod_persona";
			clearFieldRifint(campi, getDoc());
			getDoc().getAssegnazioneOP().setUfficio_completo(false);
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
		String value = (getDoc().getAssegnazioneOPM() != null && getDoc().getAssegnazioneOPM().getNome_uff() != null) ? getDoc().getAssegnazioneOPM().getNome_uff() : "";

		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi 		= ".assegnazioneOPM.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		lookupRuolo(getDoc(), value, campi, getDoc().getCod_amm_aoo());
		return null;
	}

	/**
	 * Pulizia dei campi di lookup su ruolo OPM
	 *
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_opm() throws Exception {
		// *rif_interni.assegnazioneRPA.@nome_uff=xml,/ruolo/nome ; fake_rif_interni_ruoloRPA=xml,/ruolo/nome
		String campi = ".assegnazioneOPM.@nome_uff=xml,/ruolo/nome"; // TODO Da completare

		return clearField(campi, getDoc());
	}

	/**
	 * In caso di modifica del campo validita' occorre controllare se e' necessario
	 * azzerare il valore del campo di data di 'fino al' della validita'
	 * @param vce
	 */
	public void validitaValueChange(ValueChangeEvent vce) throws Exception {
		if (getDoc().getVisibilita() != null && getDoc().getVisibilita().getTipo() != null && getDoc().getVisibilita().getTipo().equals("Riservato"))
			getDoc().getVisibilita().setFino_al("");
    }

	/**
	 * Lettura dell'attributo tipoVerificaDuplicati passato come attributo attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerVerificaDuplicati(ActionEvent event){
		this.tipoVerificaDuplicati = (String) event.getComponent().getAttributes().get("tipoVerificaDuplicati");
	}

	/**
	 * Verifica se esistono duplicati del documento che si sta
	 * registrando in base alla query passata
	 *
	 * @throws Exception
	 */
	public String verificaDuplicati(String query) throws Exception {
		try {
			getFormsAdapter().verificaDuplicatiDoc(query);
			XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
			if (handleErrorResponse(response) ) {
				// Verifico se l'errore corrente si riferisce ad un esito di ricerca nullo. In caso contrario
				// mostro l'errore all'operatore
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				Errormsg errormsg = (Errormsg) session.getAttribute("errormsg");

				if (!errormsg.getErrore().isUnexpected() && Const.RITORNO_ESITO_RICERCA_NULLO.equals(errormsg.getErrore().getErrtype().trim()))
					session.removeAttribute("errormsg");
				else
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
			}

			Element root = response.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> tit = root.selectNodes("//titolo");
			if (null != tit && !tit.isEmpty()) {
				DocWayTitles titles = new DocWayTitles();
				titles.getFormsAdapter().fillFormsFromResponse(response);

				titles.init(response.getDocument());
				titles.setPopupPage(true);

				setSessionAttribute("docwayTitles", titles);
				return "showtitles@docway";
			}
			else if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")) {
				return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response, true);
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
		return "emptyVerificaDuplicati@docway";
	}

	/**
	 * Verifica l'esistenza di eventuali duplicati del documento che si sta
	 * registrando in base alla query passata
	 *
	 * @return true se esistono duplicati del documento, false altrimenti
	 * @throws Exception
	 */
	public void existsDuplicati(String query) throws Exception {
		try {
			getFormsAdapter().verificaDuplicatiDoc(query);
			XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
			if (handleErrorResponse(response) ) {
				// Verifico se l'errore corrente si riferisce ad un esito di ricerca nullo. In caso contrario
				// mostro l'errore all'operatore
				HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
				Errormsg errormsg = (Errormsg) session.getAttribute("errormsg");

				if (!errormsg.getErrore().isUnexpected() && Const.RITORNO_ESITO_RICERCA_NULLO.equals(errormsg.getErrore().getErrtype().trim()))
					session.removeAttribute("errormsg");
				else
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
			}

			Element root = response.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> tit = root.selectNodes("//titolo");
			if (null != tit && !tit.isEmpty()) {
				duplicatiPresenti = true;
				duplicatoVerificato = false;
			}
			else if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")) {
				duplicatiPresenti = true;
				duplicatoVerificato = false;
			}
			else {
				duplicatiPresenti = false;
				duplicatoVerificato = true;
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form

			duplicatiPresenti = false;
			duplicatoVerificato = false;
		}
	}

	/**
	 * Lookup su struttura esterna (per mittente doc in arrivo o destinatario doc in partenza)
	 * @throws Exception
	 */
	public String lookupRifStrutEst(int num) throws Exception {
		String value = (getDoc().getRif_esterni() != null && getDoc().getRif_esterni().size() > num && getDoc().getRif_esterni().get(num).getNome() != null) ? getDoc().getRif_esterni().get(num).getNome() : "";

		// controllo su lunghezza minima del filtro di lookup su rif esterni
		if (!checkRifestLookup(value))
			return null;

		String aliasName 	= "struest_nome,persest_nomcogn";
		String aliasName1 	= "struest_nome,persest_cognome";
		String titolo 		= "xml,/struttura_esterna/nome \"^ (~\" xml,/struttura_esterna/indirizzo/@comune \"~^)\" \"^ [csap: ~\" xml,/struttura_esterna/@cod_SAP \"~^]\" \"^ - P.IVA ~\" xml,/struttura_esterna/@partita_iva \"~^|\" \"^ - C.F. ~\" xml,/struttura_esterna/@codice_fiscale \"~^|\" xml,/persona_esterna/@cognome xml,/persona_esterna/@nome"; //titolo
		String ord 			= "xml(xpart:/struttura_esterna/nome),xml(xpart:/persona_esterna/@cognome)(join),xml(xpart:/persona_esterna/@nome)"; //ord
		String campiLookup	= ".rif_esterni.rif[" + num + "].nome=xml,/struttura_esterna/nome xml,/persona_esterna/@cognome xml,/persona_esterna/@nome" +
								" ; .rif_esterni.rif[" + num + "].indirizzo=xml,/struttura_esterna/indirizzo \"~-\" xml,/struttura_esterna/indirizzo/@cap xml,/struttura_esterna/indirizzo/@comune \"^ (~\" xml,/struttura_esterna/indirizzo/@prov \"~^)\" \" -~\" xml,/struttura_esterna/indirizzo/@nazione xml,/persona_esterna/recapito/indirizzo \"-~\" xml,/persona_esterna/recapito/indirizzo/@cap xml,/persona_esterna/recapito/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito/indirizzo/@nazione" +
								" ; .rif_esterni.rif[" + num + "].@cod=xml,/struttura_esterna/@cod_uff xml,/persona_esterna/@matricola" +
								" ; .rif_esterni.rif[" + num + "].@email_certificata=xml,/struttura_esterna/email_certificata/@addr  xml,/persona_esterna/recapito/email_certificata/@addr" +
								" ; .rif_esterni.rif[" + num + "].@email=xml,/struttura_esterna/email/@addr xml,/persona_esterna/recapito/email/@addr" +
								" ; .rif_esterni.rif[" + num + "].@tel=xml,/" +
								" ; .rif_esterni.rif[" + num + "].@fax=xml,/" +
								" ; .rif_esterni.rif[" + num + "].referente.@cod=xml,/" +
								" ; .rif_esterni.rif[" + num + "].referente.@nominativo=xml,/" +
								" ; telefono=xml,/struttura_esterna/telefono/@num \"~^!\" xml,/struttura_esterna/telefono/@tipo xml,/persona_esterna/recapito/telefono/@num \"^!\" xml,/persona_esterna/recapito/telefono/@tipo" +
								" ; lookup_xq_.rif_esterni.rif.referente.@nominativo=xml,/" +
								" ; .rif_esterni.rif[" + num + "].indirizzo_personale=xml,/persona_esterna/recapito_personale/indirizzo \"-~\" xml,/persona_esterna/recapito_personale/indirizzo/@cap xml,/persona_esterna/recapito_personale/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito_personale/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito_personale/indirizzo/@nazione" +
								" ; telefono_personale=xml,/persona_esterna/recapito_personale/telefono/@num \"^!\" xml,/persona_esterna/recapito_personale/telefono/@tipo" +
								" ; .rif_esterni.rif[" + num + "].referente.@ruolo=xml,/" +
								" ; sx=xml,/" +
								" ; .rif_esterni.rif[" + num + "].@codice_fiscale=xml,//@codice_fiscale" +
								" ; .rif_esterni.rif[" + num + "].@cod_SAP=xml,/struttura_esterna/@cod_SAP" +
								" ; .rif_esterni.rif[" + num + "].@partita_iva=xml,//@partita_iva";
		String campiClear 	= ".rif_esterni.rif[" + num + "].nome=xml,/struttura_esterna/nome xml,/persona_esterna/@cognome xml,/persona_esterna/@nome";

		String db 			= getFormsAdapter().getDefaultForm().getParam("aclDb"); //db
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("SAPLookup", false)) // TODO da verficare lookup su SAP
			db				= "generic-lookup-call;sap_lookup_mitt-dest_class";

		String newRecord 	= "/base/acl/engine/acl.jsp?db=" + getFormsAdapter().getDefaultForm().getParam("aclDb") + ";dbTable=struttura_esterna;fillField=struttura_esterna.nome;rightCode=ACL-6"; //newRecord
		String xq			= ""; //extraQuery
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("acl_ext_aoo_restriction", false))
			xq				= "[struest_codammaoo]=" + getDoc().getCod_amm_aoo() + " OR [persest_codammaoo]=" + getDoc().getCod_amm_aoo();

		callLookupWithClearFields(getDoc(), aliasName, aliasName1, titolo, ord, campiLookup, campiClear, xq, db, newRecord, value);

		return null;
	}

	/**
	 * Validazione del valore inserito all'interno del campo di lookup su rif esterni (strutture/persone)
	 *
	 * @param val valore sul quale deve essere eseguito il filtro di lookup
	 * @return true se il valore sul quale filtrare e' corretto, false altrimenti (es. stringa troppo corta)
	 * @throws Exception
	 */
	protected boolean checkRifestLookup(String val) throws Exception {
		while (val.indexOf("*") == 0)
			val = val.substring(1);
		if (val.length() < 2) {
			Errormsg errormsg = new Errormsg();
			errormsg.setActive(true);
			Errore error = new Errore();
			error.setLevel("warning");
			error.setErrtype(I18N.mrs("dw4.ambito_di_ricerca_troppo_ampio_Si_prega_di_digitare_almento_2_caratteri"));
			errormsg.setErrore(error);

			setSessionAttribute("errormsg", errormsg);
			return false;
		}

		return true;
	}

	/**
	 * Pulizia dei campi di lookup struttura esterna (per mittente doc in arrivo o
	 * destinatario doc in partenza)
	 * @throws Exception
	 */
	public String clearLookupRifStrutEst(int num) throws Exception {
		String campi = ".rif_esterni[" + num + "].nome=xml,/struttura_esterna/nome xml,/persona_esterna/@cognome xml,/persona_esterna/@nome";

		String corteseAttenzione = (getDoc().getRif_esterni() != null && getDoc().getRif_esterni().size() > num && getDoc().getRif_esterni().get(num).getReferente() != null && getDoc().getRif_esterni().get(num).getReferente().getNominativo() != null) ? getDoc().getRif_esterni().get(num).getReferente().getNominativo() : "";
		if (corteseAttenzione.length() == 0) {
			campi = ".rif_esterni[" + num + "].nome=xml,/struttura_esterna/nome xml,/persona_esterna/@cognome xml,/persona_esterna/@nome" +
					" ; .rif_esterni[" + num + "].indirizzo=xml,/struttura_esterna/indirizzo \"~-\" xml,/struttura_esterna/indirizzo/@cap xml,/struttura_esterna/indirizzo/@comune \"^ (~\" xml,/struttura_esterna/indirizzo/@prov \"~^)\" \" -~\" xml,/struttura_esterna/indirizzo/@nazione xml,/persona_esterna/recapito/indirizzo \"-~\" xml,/persona_esterna/recapito/indirizzo/@cap xml,/persona_esterna/recapito/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito/indirizzo/@nazione" +
					" ; .rif_esterni[" + num + "].@cod=xml,/struttura_esterna/@cod_uff xml,/persona_esterna/@matricola" +
					" ; .rif_esterni[" + num + "].@email_certificata=xml,/struttura_esterna/email_certificata/@addr xml,/persona_esterna/recapito/email_certificata/@addr" +
					" ; .rif_esterni[" + num + "].@email=xml,/struttura_esterna/email/@addr xml,/persona_esterna/recapito/email/@addr" +
					" ; .rif_esterni[" + num + "].@tel=xml,/" +
					" ; .rif_esterni[" + num + "].@fax=xml,/" +
					" ; .rif_esterni[" + num + "].referente.@cod=xml,/" +
					" ; .rif_esterni[" + num + "].referente.@nominativo=xml,/" +
					" ; .rif_esterni[" + num + "].indirizzo_personale=xml,/persona_esterna/recapito_personale/indirizzo \"-~\" xml,/persona_esterna/recapito_personale/indirizzo/@cap xml,/persona_esterna/recapito_personale/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito_personale/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito_personale/indirizzo/@nazione" +
					" ; .rif_esterni[" + num + "].@codice_fiscale=xml,//@codice_fiscale" +
					" ; .rif_esterni[" + num + "].@cod_SAP=xml,/struttura_esterna/@cod_SAP" +
					" ; .rif_esterni[" + num + "].@partita_iva=xml,//@partita_iva";
		}

		return clearField(campi, getDoc());
	}

	/**
	 * Lookup su persona esterna (per firmatario doc in arrivo o c.a. doc in partenza)
	 * @throws Exception
	 */
	public String lookupRifPersEst(int num) throws Exception {
		String value = (getDoc().getRif_esterni() != null && getDoc().getRif_esterni().size() > 0 && getDoc().getRif_esterni().get(num).getReferente() != null && getDoc().getRif_esterni().get(num).getReferente().getNominativo() != null) ? getDoc().getRif_esterni().get(num).getReferente().getNominativo() : "";

		// controllo su lunghezza minima del filtro di lookup su rif esterni
		String codAppartenenza = (getDoc().getRif_esterni() != null && getDoc().getRif_esterni().size() > 0 && getDoc().getRif_esterni().get(num).getCod() != null) ? getDoc().getRif_esterni().get(num).getCod() : "";
		if (codAppartenenza.equals("")) { // eseguo il controllo solo se non e' stata selezionata prima una struttura di appartenenza del rif esterno
			if (!checkRifestLookup(value))
				return null;
		}

		String aliasName 	= "persest_nomcogn,persest_appartenenzaqualifica";
		String aliasName1 	= "persest_cognome,persest_appartenenzaqualifica";
		String titolo 		= "xml,/persona_esterna/@cognome xml,/persona_esterna/@nome"; //titolo
		String ord 			= "xml(xpart:/persona_esterna/@cognome),xml(xpart:/persona_esterna/@nome)"; //ord
		String campiLookup	= ".rif_esterni.rif[" + num + "].referente.@nominativo=xml,/persona_esterna/@cognome xml,/persona_esterna/@nome" +
								" ; .rif_esterni.rif[" + num + "].indirizzo=xml,/persona_esterna/recapito/indirizzo \"-~\" xml,/persona_esterna/recapito/indirizzo/@cap xml,/persona_esterna/recapito/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito/indirizzo/@nazione" +
								" ; .rif_esterni.rif[" + num + "].referente.@cod=xml,/persona_esterna/@matricola" +
								" ; .rif_esterni.rif[" + num + "].@email_certificata=xml,/persona_esterna/recapito/email_certificata/@addr" +
								" ; .rif_esterni.rif[" + num + "].@email=xml,/persona_esterna/recapito/email/@addr" +
								" ; .rif_esterni.rif[" + num + "].@tel=xml,/" +
								" ; .rif_esterni.rif[" + num + "].@fax=xml,/" +
								" ; telefono=xml,/persona_esterna/recapito/telefono/@num \"^!\" xml,/persona_esterna/recapito/telefono/@tipo" +
								" ; lookup_xq_.rif_esterni.rif.referente.@nominativo=xml,/" +
								" ; .rif_esterni.rif[" + num + "].indirizzo_personale=xml,/persona_esterna/recapito_personale/indirizzo \"-~\" xml,/persona_esterna/recapito_personale/indirizzo/@cap xml,/persona_esterna/recapito_personale/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito_personale/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito_personale/indirizzo/@nazione" +
								" ; telefono_personale=xml,/persona_esterna/recapito_personale/telefono/@num \"^!\" xml,/persona_esterna/recapito_personale/telefono/@tipo" +
								" ; appartenenza=xml,/persona_esterna/appartenenza/@cod_uff" +
								" ; ruolo=xml,/persona_esterna/appartenenza/@qualifica" +
								" ; .rif_esterni.rif[" + num + "].referente.@ruolo=xml,/" +
								" ; dx=xml,/" +
								" ; .rif_esterni.rif[" + num + "].@codice_fiscale=xml,//@codice_fiscale" +
								" ; .rif_esterni.rif[" + num + "].@cod_SAP=xml,/struttura_esterna/@cod_SAP" +
								" ; .rif_esterni.rif[" + num + "].@partita_iva=xml,//@partita_iva";
		String campiClear 	= ".rif_esterni.rif[" + num + "].referente.@nominativo=xml,/persona_esterna/@cognome xml,/persona_esterna/@nome";
		String db 			= getFormsAdapter().getDefaultForm().getParam("aclDb"); //db
		String newRecord 	= "/base/acl/engine/acl.jsp?db=" + getFormsAdapter().getDefaultForm().getParam("aclDb") + ";dbTable=persona_esterna;fillField=persona_esterna.@cognome;rightCode=ACL-8"; //newRecord
		if (getDoc().getRif_esterni() != null
				&& getDoc().getRif_esterni().get(num) != null
				&& getDoc().getRif_esterni().get(num).getTipo() != null
				&& getDoc().getRif_esterni().get(num).getTipo().equals(RifEsterno.TIPORIF_PERSONA_ESTERNA)) {
			newRecord 		= "";
		}
		String xq			= ""; //extraQuery
		if (getDoc().getRif_esterni() != null
				&& getDoc().getRif_esterni().get(num) != null
				&& getDoc().getRif_esterni().get(num).getCod() != null
				&& getDoc().getRif_esterni().get(num).getCod().length() > 0) {
			xq = "[persest_appartenenzacoduff]=" + getDoc().getRif_esterni().get(num).getCod();
		}
		else if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("acl_ext_aoo_restriction", false)) {
			xq = "[persest_codammaoo]=" + getDoc().getCod_amm_aoo(); // filtro su cod sede
		}

		callLookupWithClearFields(getDoc(), aliasName, aliasName1, titolo, ord, campiLookup, campiClear, xq, db, newRecord, value);

		return null;
	}

	/**
	 * Pulizia dei campi di lookup su persona esterna (per firmatario doc in arrivo
	 * o c.a. doc in partenza)
	 * @throws Exception
	 */
	public String clearLookupRifPersEst(int num) throws Exception {
		String campi = ".rif_esterni[" + num + "].referente.@nominativo=xml,/persona_esterna/@cognome xml,/persona_esterna/@nome" +
				" ; .rif_esterni[" + num + "].indirizzo=xml,/persona_esterna/recapito/indirizzo \"-~\" xml,/persona_esterna/recapito/indirizzo/@cap xml,/persona_esterna/recapito/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito/indirizzo/@nazione" +
				" ; .rif_esterni[" + num + "].referente.@cod=xml,/persona_esterna/@matricola" +
				" ; .rif_esterni[" + num + "].@email_certificata=xml,/persona_esterna/recapito/email_certificata/@addr" +
				" ; .rif_esterni[" + num + "].@email=xml,/persona_esterna/recapito/email/@addr" +
				" ; .rif_esterni[" + num + "].@tel=xml,/" +
				" ; .rif_esterni[" + num + "].@fax=xml,/" +
				" ; .rif_esterni[" + num + "].indirizzo_personale=xml,/persona_esterna/recapito_personale/indirizzo \"-~\" xml,/persona_esterna/recapito_personale/indirizzo/@cap xml,/persona_esterna/recapito_personale/indirizzo/@comune \"^ (~\" xml,/persona_esterna/recapito_personale/indirizzo/@prov \"~^)\" \" -~\" xml,/persona_esterna/recapito_personale/indirizzo/@nazione" +
				" ; .rif_esterni[" + num + "].referente.@ruolo=xml,/" +
				" ; .rif_esterni[" + num + "].@codice_fiscale=xml,//@codice_fiscale" +
				" ; .rif_esterni[" + num + "].@cod_SAP=xml,/struttura_esterna/@cod_SAP" +
				" ; .rif_esterni[" + num + "].@partita_iva=xml,//@partita_iva";

		return clearField(campi, getDoc());
	}

	/**
	 * Controllo dei campo obbligatori comune per tutte le tipologie di documenti di DocWay
	 *
	 * @param modify true se si devono eseguire i controlli su una modifica, false in caso di inserimento
	 *
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredFieldCommon(boolean modify) {
		setErrorFieldIds(""); // Azzero i campi di error per la scheda corrente

		// in caso di salvataggio occorre impostare a false il reset dei jobs di iwx
		// perche' si potrebbe tornare alla maschera di inserimento/modifica (in caso ad esempio di
		// campi obbligatori non compilati)
		setResetJobsIWX(false);

		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		boolean result = false;

		// validazione dei campi custom caricati nella pagina
		result = getCustomfields().checkRequiredFields(modify, formatoData, this);
		
		//tiommi 19/12/2017 : controllo su doc. in partenza (non in bozza) che non ci siano file da firmare se attiva property firmaNecessariaPerProtocollazionePartenza=si
		//FIXME metodo commentato perchè non chiaro se necessario in validazione
		// la richiesta riguardava solo il passaggio da bozza a protocollato o non deve essere possibile avere una situazione in cui ci siano richieste di firma in doc. partenza protocollati??
		// in caso di seconda opzione riabilitare il valodatore
//		String tipoDoc = getDoc().getTipo();
//		if (!getDoc().isBozza() 
//				&& formsAdapter.checkBooleanFunzionalitaDisponibile("firmaNecessariaPerProtocollazionePartenza", false)
//				&& Const.DOCWAY_TIPOLOGIA_PARTENZA.equals(tipoDoc)) {
//			List<XwFile> files = getDoc().getFiles();
//			for (int i = 0; i < files.size(); i++) {
//				if (files.get(i).isDa_firmare()) {
//					this.setErrorMessage("templateForm:xwFiles_"+i+"_fileTitle", I18N.mrs("dw4.impossibile_protocollare_file_non_firmato"));
//					result = true;
//				}
//			}
//		}
		
		if (!modify) {
			// controlli da eseguire solo in caso di inserimento di un doc

			// controlli su registrazione protocolli pregressi (se attivo)
			if (formsAdapter.checkBooleanFunzionalitaDisponibile("registrazioneProtPreg", false)
					&& (getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_ARRIVO) || getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_PARTENZA)
							|| getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_INTERNO))) {

				// controllo su data di protocollo
				if (getDoc().getRpp_data_prot() == null || getDoc().getRpp_data_prot().length() == 0) {
					this.setErrorMessage("templateForm:dataProtPreg", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.dataprot") + "'");
					result = true;
				}
				else {
					// controllo sul formato della data di protocollo
					if (!DateUtil.isValidDate(getDoc().getRpp_data_prot(), formatoData)) {
						this.setErrorMessage("templateForm:dataProtPreg", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.dataprot") + "': " + formatoData.toLowerCase());
						result = true;
					}
					// TODO controllo su data specificata antecedente la data corrente?
				}

				// controllo su num di protocollo
				if (getDoc().getRpp_num_prot() == null || getDoc().getRpp_num_prot().length() == 0) {
					this.setErrorMessage("templateForm:numProtPreg", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.nprot") + "'");
					result = true;
				}
				else {
					// controllo sul formato del num di protocollo
					if (!StringUtil.isNumber(getDoc().getRpp_num_prot())) {
						this.setErrorMessage("templateForm:numProtPreg", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " '" + I18N.mrs("dw4.nprot") + "' ");
						result = true;
					}
				}

				if (doceditRep) { // se docEdit repertorio

					// controllo su num repertorio
					if (getDoc().getRpp_num_rep() == null || getDoc().getRpp_num_rep().length() == 0) {
						this.setErrorMessage("templateForm:numRepPreg", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.num_rep") + "'");
						result = true;
					}
					else {
						// controllo sul formato del num di repertorio
						if (!StringUtil.isNumber(getDoc().getRpp_num_rep())) {
							this.setErrorMessage("templateForm:numRepPreg", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " '" + I18N.mrs("dw4.num_rep") + "' ");
							result = true;
						}
					}
				}
			}

		}

		// Controllo il campo classificazione
		if (getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_INTERNO) && !isProposta()) {
			if (getDoc().getMinuta() == null || getDoc().getMinuta().getClassif() == null || getDoc().getMinuta().getClassif().getCod() == null || "".equals(getDoc().getMinuta().getClassif().getCod().trim())) {
				// Imposto la classificazione automatica
				String classifValue = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ClassificazioneAutomatica"));
				if (classifValue != null && classifValue.length() > 0) {
					getDoc().getMinuta().getClassif().setText(classifValue);
					int index = classifValue.indexOf(" - ");
					if (index != -1)
						getDoc().getMinuta().getClassif().setCod(classifValue.substring(0, index));
				}

				if (!getFormsAdapter().checkBooleanFunzionalitaDisponibile("docClassifEreditabile", false)) {
					if (getDoc().getMinuta().getClassif().getCod() == null || "".equals(getDoc().getMinuta().getClassif().getCod().trim())) {
						String classifField = "";
						if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("classificazioneDaCodice", false))
							classifField = "templateForm:minuta_classif_input";
						this.setErrorMessage(classifField, I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.classif") + "'");
						result = true;
					}
				}
			}

			if (getDoc().getClassif() == null || getDoc().getClassif().getCod() == null || "".equals(getDoc().getClassif().getCod().trim())) {
				// Imposto la classificazione automatica su originale
				String classifValue = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ClassificazioneAutomatica"));
				if (classifValue != null && classifValue.length() > 0) {
					getDoc().getClassif().setText(classifValue);
					int index = classifValue.indexOf(" - ");
					if (index != -1)
						getDoc().getClassif().setCod(classifValue.substring(0, index));
				}
			}
		}
		else {
			if (getDoc().getClassif() == null || getDoc().getClassif().getCod() == null || "".equals(getDoc().getClassif().getCod().trim())) {
				// Imposto la classificazione automatica
				String classifValue = AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("ClassificazioneAutomatica"));
				if (classifValue != null && classifValue.length() > 0) {
					getDoc().getClassif().setText(classifValue);
					int index = classifValue.indexOf(" - ");
					if (index != -1)
						getDoc().getClassif().setCod(classifValue.substring(0, index));
				}

				if (!getFormsAdapter().checkBooleanFunzionalitaDisponibile("docClassifEreditabile", false)) {
					if (getDoc().getClassif().getCod() == null || "".equals(getDoc().getClassif().getCod().trim())) {
						String classifField = "";
						if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("classificazioneDaCodice", false))
							classifField = "templateForm:classif_input";
						this.setErrorMessage(classifField, I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.classif") + "'");
						result = true;
					}
				}
			}
		}

		// classificazione con motivazione su minuta
		if (getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_INTERNO) && getDoc().getMinuta() != null && getDoc().getMinuta().getClassif() != null
							&& getDoc().getMinuta().getClassif().getCod() != null
							&& !(getDoc().getMinuta().getClassif().getCod().equals(""))
							&& getDoc().getMinuta().getClassif().getCod().trim().equals(classifOD)) {
			if (getDoc().getMinuta().getMotiv_ogg_div() == null || getDoc().getMinuta().getMotiv_ogg_div().length() == 0) {
				this.setErrorMessage("templateForm:minuta_classif_motivazione", I18N.mrs("dw4.e_necessario_fornire_una_motivazione_per_la_classificazione_della_minuta_impostata"));
				result = true;
			}
			else if (getDoc().getMinuta().getMotiv_ogg_div().length() < 15) {
				this.setErrorMessage("templateForm:minuta_classif_motivazione", I18N.mrs("dw4.la_motivazione_fornita_deve_essere_lunga_almeno_15_caratteri"));
				result = true;
			}
			else if (StringUtil.containsRipetizioni(getDoc().getMinuta().getMotiv_ogg_div(), 3)) { // TODO controllare il valore 3 specificato
				this.setErrorMessage("templateForm:minuta_classif_motivazione", I18N.mrs("dw4.ripetizione_inusuale_di_caratteri_per_la_motivazione_fornita"));
				result = true;
			}
		}

		// classificazione con motivazione
		if (getDoc().getClassif() != null && getDoc().getClassif().getCod() != null
							&& !(getDoc().getClassif().getCod().equals(""))
							&& getDoc().getClassif().getCod().trim().equals(classifOD)) {
			if (getDoc().getMotiv_ogg_div() == null || getDoc().getMotiv_ogg_div().length() == 0) {
				this.setErrorMessage("templateForm:classif_motivazione", I18N.mrs("dw4.e_necessario_fornire_una_motivazione_per_la_classificazione_impostata"));
				result = true;
			}
			else if (getDoc().getMotiv_ogg_div().length() < 15) {
				this.setErrorMessage("templateForm:classif_motivazione", I18N.mrs("dw4.la_motivazione_fornita_deve_essere_lunga_almeno_15_caratteri"));
				result = true;
			}
			else if (StringUtil.containsRipetizioni(getDoc().getMotiv_ogg_div(), 3)) { // TODO controllare il valore 3 specificato
				this.setErrorMessage("templateForm:classif_motivazione", I18N.mrs("dw4.ripetizione_inusuale_di_caratteri_per_la_motivazione_fornita"));
				result = true;
			}
		}

		// Controllo sul campo oggetto
		if (getDoc().getOggetto() == null || getDoc().getOggetto().length() == 0) {
			this.setErrorMessage("templateForm:doc_oggetto", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.object") + "'");
			result = true;
		}
		else {
			// Controllo sulla presenza di caratteri non consentiti all'interno del campo oggetto
			if (getDoc().getOggetto().contains("|")) { // TODO gestire i caratteri non consentiti da file di properties
				this.setErrorMessage("templateForm:doc_oggetto", I18N.mrs("dw4.il_campo_oggetto_contiene_un_carattere_non_valido") + ": | ");
				result = true;
			}

			// Controllo della lunghezza dell'oggetto per tutti i documenti tranne i non protocollati
			if (!getDoc().getTipo().equals(Const.DOCWAY_TIPOLOGIA_VARIE)) {
				int minLenOggetto = AppStringPreferenceUtil.getAppStringPreferenceIntValue(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("LunghezzaMinimaOggetto"));
				if (minLenOggetto > -1 && getDoc().getOggetto().length() < minLenOggetto) {
					this.setErrorMessage("templateForm:doc_oggetto", I18N.mrs("dw4.il_campo_oggetto_deve_essere_lungo_almeno") + " " + minLenOggetto + " " + I18N.mrs("dw4.caratteri"));
					result = true;
				}
				else {
					// check se caratteri ripetuti
					if (AppStringPreferenceUtil.getAppStringPreference(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("checkRipetizioniInOggetto")).toLowerCase().equals("si")) {
						//RW0052224 - fcossu - Numero ripetizione caratteri inusuali
						int numRipetizioni = AppStringPreferenceUtil.getAppStringPreferenceIntValue(this.getAppStringPreferences(), AppStringPreferenceUtil.decodeAppStringPreference("numeroRipetizioniInOggetto"));
						if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("oggettoModificabile", false) && numRipetizioni > -1) {
							if (StringUtil.containsRipetizioni(getDoc().getOggetto(), numRipetizioni)) {
								this.setErrorMessage("templateForm:doc_oggetto", I18N.mrs("dw4.ripetizione_inusuale_di_caratteri_Verificare_il_contenuto_del_campo_oggetto"));
								result = true;
							}
						}
					}
				}
			}
		}

		// Controllo che almeno una istanza di allegato sia valorizzata
		if (getDoc().getAllegati().get(0) == null || getDoc().getAllegati().get(0).getText() == null || getDoc().getAllegati().get(0).getText().trim().length() == 0) {
			this.setErrorMessage("templateForm:docEditAllegati:0:allegatoText", I18N.mrs("dw4.occorre_valorizzare_almeno_una_istanza_di_allegato"));
			result = true;
		}

		// Verifico che la data di visibilita' del documento sia in formato corretto
		if (getDoc().getVisibilita() != null && getDoc().getVisibilita().getTipo() != null && getDoc().getVisibilita().getTipo().equals("Riservato")) {
			if (getDoc().getVisibilita().getFino_al() != null && getDoc().getVisibilita().getFino_al().length() > 0) {
				if (!DateUtil.isValidDate(getDoc().getVisibilita().getFino_al(), formatoData)) {
					this.setErrorMessage("templateForm:dataVisibilitaRiservato", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.riservato_fino_al") + "': " + formatoData.toLowerCase());
					result = true;
				}
			}
		}

		// Controllo su data di scadenza
		if (getDoc().getScadenza().getTipo() != null && getDoc().getScadenza().getTipo().equals("fissa")) {
			if (getDoc().getScadenza().getData_scadenza() == null || getDoc().getScadenza().getData_scadenza().length() == 0) {
				// Data di scadenza non valorizzata
				this.setErrorMessage("templateForm:dataScadenzaDoc", I18N.mrs("dw4.occorre_compilare_la_data_di_scadenza"));
				result = true;
			}
			else {
				// Controllo se e' stato inserito un valore valido nel campo data
				if (!DateUtil.isValidDate(getDoc().getScadenza().getData_scadenza(), formatoData)) {
					this.setErrorMessage("templateForm:dataScadenzaDoc", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_scadenza") + "': " + formatoData.toLowerCase());
					result = true;
				}
			}
		}

		// controllo sul valore specificato nel campo motivazione_ogg_div
		// (se necessario, in base a valore impostato in classificazione)
		
		// tiommi 15/01/2018 - ovverride finale sul campo VISIBILITÀ 
		// se l'ufficio dell'RPA è tra quelli da sovrascrivere con un altro livello di visibilità
		overrideLivelloVisibilita(true);
		
		return result;
	}

	// metodo di supporto per verificare se occorre effettuare un cambio di visibilità in base all'ufficio dell'RPA
	// in base al flag effettuaOverride effettua anche la modifica o meno
	// ritorna true se la modifica è necessaria, false altrimenti
	private void overrideLivelloVisibilita(boolean effettuaOverride) {
		String cod_uff_rpa = getDoc().getAssegnazioneRPA().getCod_uff();
		if (cod_uff_rpa != null && !cod_uff_rpa.isEmpty()) {
			for (CoupleUfficioVisibilita coppia : listaCoppieUfficioVisibilita) {
				if (cod_uff_rpa.equals(coppia.getCodUfficio())) {
					if (effettuaOverride) {
						getDoc().getVisibilita().setTipo(coppia.getCodVisibilita());
					}
					overrideLivelloVisibilitaNeeded = true;
					nomeVisibilita = coppia.extractNomeVisibilitaSingolare();
					return;
				}
			}
		}
		overrideLivelloVisibilitaNeeded = false;
		nomeVisibilita = "";
	}

	/**
	 * Controllo sul campo mezzo di trasmissione (inserito a parte perche' non presente per tutte
	 * le tipologie di documenti)
	 *
	 * @return true se il campo e' obbligatorio e non e' compilato, false altrimenti
	 */
	public boolean checkMezzoTrasmissione() {
		boolean result = false;

		// Federico 19/01/07: controllo se il mezzo di trasmissione e' vuoto solo se l'opportuna
		// property e' stata attivata [RW 0041728]
		boolean abilitaControlloMezzoTrasmissione = StringUtil.booleanValue(FormsAdapter.getParameterFromCustomTupleValue("abilitaControlloMezzoTrasmissione", formsAdapter.getDefaultForm().getParam("_cd")));
		if (abilitaControlloMezzoTrasmissione) {
			if (getDoc().getMezzo_trasmissione() == null || getDoc().getMezzo_trasmissione().getCod() == null || getDoc().getMezzo_trasmissione().getCod().trim().length() == 0) {
				this.setErrorMessage("templateForm:mezzoTrasmissioneDoc", I18N.mrs("dw4.occorre_selezionare_un_mezzo_di_trasmissione"));
				result = true;
			}
		}

		return result;
	}

	/**
	 * abilitazione del campo 'corpo email' in docedi di un documento
	 * @return
	 */
	public String attivaCorpoEmail() {
		if (getDoc().getCorpoEmail() == null || getDoc().getCorpoEmail().length() == 0) // possibile solo se il corpo non e' valorizzato
			corpoEmailVisibile = !corpoEmailVisibile;

		return null;
	}

	/**
	 * controlla se il primo file ha una restrizione sull'estensione
	 * @param prop : la proprietà da cercare nelle appStringPreferences (es. RTFPropostaObbligatorio...);
	 * */
	public boolean checkPrimoAllegatoRTF(String prop){
		String rtfObbl = AppStringPreferenceUtil.getAppStringPreference(formsAdapter.getDefaultForm().getParam("appStringPreferences"), AppStringPreferenceUtil.decodeAppStringPreference(prop));

		if(StringUtil.booleanValue(rtfObbl))
		{
			String filename = getDoc().getFiles().get(0).getName();
			if((filename == null) || (filename != null && filename.indexOf(".rtf") != filename.length() - 4 ))
				{
					String[] fieldIds = { "templateForm:xwFiles_0_fileTitle"};
					this.setErrorMessage(fieldIds, I18N.mrs("dw4.il_primo_file_deve_essere_rtf"));
					return true;
				}
		}

		return false;
	}

	public String getPersonalViewToUse() {
		return personalViewToUse;
	}

	public void setPersonalViewToUse(String personalViewToUse) {
		this.personalViewToUse = personalViewToUse;
	}

	public boolean isProposta(){
		if(personalViewToUse.equals("@proposta") || personalViewToUse.equals("@comunicazione"))
			return true;
		return false;
	}
	
	//tiommi metodo per il controllo se mostrare alert possibile cambio visibilità 
	public String showAlertOnCodUffChange() {
		overrideLivelloVisibilita(false);
		String ufficioRPA = getDoc().getAssegnazioneRPA().getNome_uff();
		
		infoMessageLivelloVisibilitaUff = I18N.mrs("dw4.ufficio_visibilita_override", new String[]{ ufficioRPA, nomeVisibilita });
		return null;
	}
	
	//tiommi 24/01/2018 : aggiunta possibilità di aprire modale per inserimento di file ZIP da scompattare
	public String openUploadArchiviCompressi() throws Exception {
		
		DocWayUploadArchive docWayUploadArchive = new DocWayUploadArchive();
		docWayUploadArchive.setVisible(true);
		docWayUploadArchive.setDocEditDoc(this);
		setSessionAttribute("docWayUploadArchive", docWayUploadArchive);
		
		return null;
	}

}
