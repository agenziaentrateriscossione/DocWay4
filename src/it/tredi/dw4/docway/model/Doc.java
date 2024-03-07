package it.tredi.dw4.docway.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import it.tredi.dw4.acl.model.Creazione;
import it.tredi.dw4.acl.model.Note;
import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.acl.model.UltimaModifica;
import it.tredi.dw4.docway.model.analisivirus.VerificaVirus;
import it.tredi.dw4.docway.model.delibere.Odg_seduta;
import it.tredi.dw4.docway.model.delibere.Proposta;
import it.tredi.dw4.docway.model.workflow.WorkflowInstance;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Doc extends XmlEntity {

	protected String nrecord;
	protected String oggetto;
	protected String tipo;
	protected String num_prot;
	protected String data_prot;
	protected String data_reale;
	protected String anno;
	protected String cod_amm_aoo;
	protected String annullato;
	private String abrogato;
	protected String scarto;
	protected boolean personale = false;
	protected boolean bozza = false;
	protected boolean sensibile = false;
	protected int countCcTotali;
	protected int countCcFascicolo;
	protected int countCcRaccoglitore;
	protected int countCcTotaliPersonali;
	protected Autore autore = new Autore();
	protected Note note = new Note();
	protected Visibilita visibilita = new Visibilita();
	protected Classif classif = new Classif();
	protected Classif classifNV = new Classif();
	protected String motiv_ogg_div = "";
	protected Registro_emergenza registro_emergenza = new Registro_emergenza();
	protected Repertorio repertorio = new Repertorio();
	protected MezzoTrasmissione mezzo_trasmissione = new MezzoTrasmissione();
	protected Scadenza scadenza = new Scadenza();
	protected Conservazione conservazione = new Conservazione();
	protected Pubblicazione pubblicazione = new Pubblicazione();
	protected Minuta minuta = new Minuta();
	protected Voce_indice voce_indice = new Voce_indice();
	protected Impronta impronta = new Impronta();
	protected Fasc_rpa fasc_rpa = new Fasc_rpa();
	protected Fasc_rpam fasc_rpam = new Fasc_rpam();
	protected Keywords keywords = new Keywords();
	protected Riferimenti riferimenti = new Riferimenti();
	protected Tipologia tipologia = new Tipologia();
	protected boolean sendMailRifInterni = true; // Indica se inviare o meno la mail di avviso ai rif interni
	// rtirabassi - 20190911 - ERM012596 - Abilita l'invio capillare
	protected boolean sendMailSelectedRifInterni = false;
	private boolean checkNomi = true; // controllo nomi in caso di trasferimento RPA doc
	protected List<Allegato> allegati = new ArrayList<Allegato>();
	protected List<Link_interno> link_interni;
	protected List<Xlink> xlink;
	protected List<RifEsterno> rif_esterni;
	protected List<Interoperabilita> interoperabilita_multipla;
	protected List<Contenuto_in> contenuto_in;
	protected List<XwFile> files;
	protected List<XwFile> immagini;
	protected String showIwxSelectedImage = ""; // immagine selezionata (con IWX) in showdoc documento
	protected List<Fasc> fascicoli_collegati;
	protected Creazione creazione = new Creazione();
	protected Protocollazione protocollazione = new Protocollazione();
	protected Annullamento annullamento = new Annullamento();
	protected Abrogazione abrogazione = new Abrogazione();
	protected UltimaModifica ultima_modifica = new UltimaModifica();
	protected List<Storia> storia;
	protected List<History> history;
	protected List<Postit> postit;
	//gestione dell'assegnazione/visibilità del documento
	protected Rif assegnazioneRPA = new Rif();
	protected Rif assegnazioneRPAM = new Rif();
	protected Rif assegnazioneRESO = new Rif();
	protected Rif assegnazioneOP = new Rif();
	protected Rif assegnazioneOPM = new Rif();
	protected List<Rif> assegnazioneCC = new ArrayList<Rif>();
	protected List<Rif> assegnazioneCDS = new ArrayList<Rif>();
	// in caso di mezzo raccomandata riferimento al doc di raccomandata (repertorio)
	protected List<NumeroRaccomandata> numero_raccomandata;

	// gestione del protocollo pregresso (se attivato)
	private String rpp_data_prot = "";
	private String rpp_num_prot = "";
	private String rpp_num_rep = "";

	// scelta del mezzo di trasmissione in docEdit di un documento
	protected List<Option> mezzoTrasmissioneSelect = new ArrayList<Option>();
	protected List<Option> tipologiaSelect = new ArrayList<Option>();

	 // gestione multisocieta'
	protected List<Societa> societaSelect = new ArrayList<Societa>();
	protected String codSocieta = "";
	protected String societa = "";

	protected List<Tipologia> repTipologiaSelect = new ArrayList<Tipologia>(); // select di tipologie associate al repertorio (se docedit repertorio)

	// liste custom attivabili da file di properties
	protected List<Option> customSelect1 = new ArrayList<Option>();
	protected List<Option> customSelect2 = new ArrayList<Option>();

	protected boolean showCDSSection = false; // Indica se visualizzare o meno la sezione dei CDS
	protected Non_disponibile non_disponibile = new Non_disponibile();

	protected Prot_differito prot_differito = new Prot_differito();

	protected boolean agent_xml = false;
	protected boolean agent_pdf = false;
	protected boolean agent_ocr = false;
	protected boolean iagent_pdf = false;

	// utilizzati per l'aggiunta di nuovi doc informatici tramite iwx o upload classico
	protected String xwFileNamesAttached = "";
	protected String xwFileTitlesAttached = "";
	protected String xwFileIdsAttached = "";
	protected String xwImageNamesAttached = "";
	protected String xwImageTitlesAttached = "";
	protected String xwImageIdsAttached = "";

	// gestione CC in showdoc
	private List<Rif> cc_list = new ArrayList<Rif>();
	private List<Rif> cc_fasc_list = new ArrayList<Rif>();
	private List<Rif> cc_racc_list = new ArrayList<Rif>();
	private HashMap<String, List<Rif>> cc_ufficio = new HashMap<String, List<Rif>>();
	private HashMap<String, List<Rif>> cc_fasc_ufficio = new HashMap<String, List<Rif>>();
	private HashMap<String, List<Rif>> cc_racc_ufficio = new HashMap<String, List<Rif>>();

	// gestione mittente/destinatari doc in acquisizione immagini
	protected MittenteDoc mittente = new MittenteDoc();
	protected DestinatariDoc destinatari = new DestinatariDoc();

	// gestione dei libri firma (stato del documento: 'attesa firma', 'firmato')
	protected String stato_firma = "";

	// gestione workflow bonita
	protected List<WorkflowInstance> workflowInstances;

	private Extra extra = new Extra(); // gestione dei campi extra interni al documento

	private boolean filesPrenotati = false; // indica se esistono files prenotati sul documento

	private String corpoEmail = "";
	private int docInformaticiSize = 0;

	//Verbale di Seduta per Docway Delibere
	private boolean verbale = false;
	private String nrecord_Sed = "";
	private String verbale_di_seduta = "";
	private Proposta proposta = new Proposta();
	private Odg_seduta odg_seduta = new Odg_seduta();

	//data corrente
	private String currDate = "";

	private int countCCFromFasc = 0; // conta quanti CC associati al documento sono ereditati da uno o piu' fascicoli

	// dati relativi a documenti recuperati da email
	private String messageId = "";
	private String emailAttachmentIndex = ""; // split di email in piu' documenti in base agli allegati contenuti

	// cancellazione logica del documento
	private boolean cestino = false;
	private String datacestino = "";

	// rifiuto di bozze in arrivo
	private Rifiuto rifiuto = new Rifiuto();

	// mbernardini 02/03/2017 : possibilita' di settare come readOnly il documento
	private boolean readOnly = false;

	// tiommi - campi relativi la presa in carico
	private boolean richiestaPresaInCarico = false;
	private boolean effettuataPresaInCarico = false;
	private List<RifPresaInCarico> incaricatiPresaInCarico = new ArrayList<RifPresaInCarico>();
	private List<RifPresaInCarico> rifPresaInCarico = new ArrayList<RifPresaInCarico>();

	private boolean protocollabile = false;
	private boolean repertoriabile = false;
	
	// tiommi - flag per gestione ricevuti da PEC
	private boolean bozzaArrivoFromPEC = false;
	
	// mbernardini 06/02/2018 : trasformazione di un doc in repertorio tramite docEdit (ripristino del documento)
    private TrasformazioneRep trasformazioneRep = new TrasformazioneRep();
    
    // tiommi 19/04/2018 : controllo se deve essere inserito il diritto di intervento di default ai cc
    private boolean interventoDefaultCC = false;
    
    // mbernardini 09/01/2019 : gestione della verifica di virus su allegati del documento
    private VerificaVirus verificaVirus = new VerificaVirus();
	
	@Override
	public XmlEntity init(Document dom) {
		this.init(dom, "");
		return null;
	}

	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom, String tipo) {
		String xpath = "/response/doc";
		Element doc = (Element)dom.selectSingleNode(xpath);
		if( null == doc ) xpath = "/response/fascicolo";
		this.nrecord = 	 				XMLUtil.parseStrictAttribute(dom, xpath+"/@nrecord", ".");
		this.tipo = 	 				XMLUtil.parseStrictAttribute(dom, xpath+"/@tipo", tipo);
		this.num_prot =  				XMLUtil.parseStrictAttribute(dom, xpath+"/@num_prot", ".");
		this.data_prot = 				XMLUtil.parseStrictAttribute(dom, xpath+"/@data_prot");
		this.data_reale = 				XMLUtil.parseStrictAttribute(dom, xpath+"/@data_reale");
		this.anno = 	 				XMLUtil.parseStrictAttribute(dom, xpath+"/@anno");
		this.cod_amm_aoo = 				XMLUtil.parseStrictAttribute(dom, xpath+"/@cod_amm_aoo");
		this.annullato = 	 			XMLUtil.parseStrictAttribute(dom, xpath+"/@annullato");
		this.setAbrogato(XMLUtil.parseStrictAttribute(dom, xpath+"/@abrogato"));
		String strPersonale =			XMLUtil.parseStrictAttribute(dom, xpath+"/@personale");
		if (strPersonale != null && strPersonale.equals("si"))
			this.personale = true;
		else
			this.personale = false;
		String strBozza =				XMLUtil.parseStrictAttribute(dom, xpath+"/@bozza");
		if (strBozza != null && strBozza.equals("si"))
			this.bozza = true;
		else
			this.bozza = false;
		String strSensibile =			XMLUtil.parseStrictAttribute(dom, xpath+"/@sensibile");
		if (strSensibile != null && strSensibile.equals("si"))
			this.sensibile = true;
		else
			this.sensibile = false;
		this.scarto = XMLUtil.parseStrictAttribute(dom, xpath+"/@scarto");
		this.oggetto = XMLUtil.parseElement(dom, "doc/oggetto", false);
		this.autore.init(XMLUtil.createDocument(dom, xpath+"/autore"));
		this.note.init(XMLUtil.createDocument(dom, xpath+"/note"));
		this.corpoEmail = XMLUtil.parseStrictElement(dom, xpath+"/corpoEmail", false);
		this.non_disponibile.init(XMLUtil.createDocument(dom, "/response/non_disponibile"));
		this.visibilita.init(XMLUtil.createDocument(dom, xpath+"/visibilita"));
		this.classif.init(XMLUtil.createDocument(dom, xpath+"/classif"));
		this.voce_indice.init(XMLUtil.createDocument(dom, xpath+"/voce_indice"));
		this.repertorio.init(XMLUtil.createDocument(dom, xpath+"/repertorio"));
		this.trasformazioneRep.init(XMLUtil.createDocument(dom, xpath+"/trasformazioneRep"));
		this.registro_emergenza.init(XMLUtil.createDocument(dom, xpath+"/registro_emergenza"));
		this.mezzo_trasmissione.init(XMLUtil.createDocument(dom, xpath+"/mezzo_trasmissione"));
		this.minuta.init(XMLUtil.createDocument(dom, xpath+"/minuta"));
		this.scadenza.init(XMLUtil.createDocument(dom, xpath+"/scadenza"));
		this.conservazione.init(XMLUtil.createDocument(dom, xpath+"/conservazione"));
		this.pubblicazione.init(XMLUtil.createDocument(dom, xpath+"/pubblicazione"));
		this.impronta.init(XMLUtil.createDocument(dom, xpath+"/impronta"));
		this.fasc_rpa.init(XMLUtil.createDocument(dom, xpath+"/fasc_rpa"));
		this.fasc_rpam.init(XMLUtil.createDocument(dom, xpath+"/fasc_rpam"));
		this.riferimenti.init(XMLUtil.createDocument(dom, xpath+"/riferimenti"));
		this.keywords.init(XMLUtil.createDocument(dom, xpath+"/keywords"));
		this.tipologia.init(XMLUtil.createDocument(dom, xpath+"/tipologia"));
		this.link_interni = XMLUtil.parseSetOfElement(dom, xpath+"/link_interno", new Link_interno());
		this.xlink = XMLUtil.parseSetOfElement(dom, xpath+"/xlink", new Xlink());
		this.allegati = XMLUtil.parseSetOfElement(dom, xpath+"/allegato", new Allegato());
		List<Rif> rif_interni = XMLUtil.parseSetOfElement(dom, xpath+"/rif_interni/rif", new Rif());
		this.rif_esterni = XMLUtil.parseSetOfElement(dom, xpath+"/rif_esterni/rif", new RifEsterno());
		if (this.rif_esterni == null || this.rif_esterni.size() == 0) {
			this.rif_esterni = new ArrayList<RifEsterno>();
			this.rif_esterni.add(new RifEsterno());
		}
		//tiommi: gestione invio telematico multiplo con una sola email
		this.interoperabilita_multipla = XMLUtil.parseSetOfElement(dom, xpath+"/rif_esterni/interoperabilita_multipla/interoperabilita", new Interoperabilita());
		this.contenuto_in = XMLUtil.parseSetOfElement(dom, xpath+"/rif_contenuto_in/contenuto_in", new Contenuto_in());

		this.files = XMLUtil.parseSetOfElement(dom, xpath+"/files/node()[name()='xw:file']", new XwFile());
		//tiommi 18/12/2017 : ciclo i file alla ricerca di almeno un file da firmare che inibisca la protocollazione (protocollo in partenza)
		//tiommi 16/03/2018 : ciclo i file alla ricerca del file relativo al daticert.xml per capire se si tratta di una bozza arrivo da pec
		boolean fileDaFirmare = false;
		for (XwFile file : this.files) {
			//effettuo controllo file da firmare solo se richiesto da property
			if (Const.DOCWAY_TIPOLOGIA_PARTENZA.equals(tipo)
					&& Boolean.parseBoolean(XMLUtil.parseStrictAttribute(dom, "response/funzionalita_disponibili/@firmaNecessariaPerProtocollazionePartenza"))
					&& file.isDa_firmare()) {
				fileDaFirmare = true;
				break;
			}
			//controllo se esiste un file daticert.xml per capire se si tratta di un doc in arrivo da pec
			if (Const.DOCWAY_TIPOLOGIA_ARRIVO.equals(tipo)
					&& bozza
					&& file.getTitle().equalsIgnoreCase("daticert.xml")) {
				this.bozzaArrivoFromPEC = true;
				break;
			}
		}
		this.immagini = XMLUtil.parseSetOfElement(dom, xpath+"/immagini/node()[name()='xw:file']", new XwFile());
		if (this.immagini != null && this.immagini.size() > 0 && this.immagini.get(0) != null && this.immagini.get(0).getName() != null)
			this.showIwxSelectedImage = this.immagini.get(0).getName();

		this.fascicoli_collegati = XMLUtil.parseSetOfElement(dom, xpath+"/rif_interni/fascicoli_collegati/fasc", new Fasc());
		this.postit = XMLUtil.parseSetOfElement(dom, xpath+"/postit", new Postit());
		this.creazione.init(XMLUtil.createDocument(dom, xpath+"/storia/creazione"));
		this.protocollazione.init(XMLUtil.createDocument(dom, xpath+"/storia/protocollazione"));
		this.ultima_modifica.init(XMLUtil.createDocument(dom, xpath+"/storia/ultima_modifica"));
		this.annullamento.init(XMLUtil.createDocument(dom, xpath+"/storia/annullamento"));
		this.abrogazione.init(XMLUtil.createDocument(dom, xpath+"/storia/abrogazione"));
		this.storia = XMLUtil.parseSetOfElement(dom, xpath+"/storia/node()", new Storia());

		this.workflowInstances = XMLUtil.parseSetOfElement(dom, xpath+"/workflows/Instance", new WorkflowInstance());

		this.motiv_ogg_div = XMLUtil.parseStrictElement(dom, xpath+ "/motivazione_oggetti_diversi");

		this.prot_differito.init(XMLUtil.createDocument(dom, xpath+"/prot_differito"));

		this.numero_raccomandata = XMLUtil.parseSetOfElement(dom, xpath+"/numero_raccomanda", new NumeroRaccomandata()); // TODO da verificare e completare dopo aver implementato le raccomandate
		if (this.numero_raccomandata.size() == 0) this.numero_raccomandata.add(new NumeroRaccomandata());

		// TODO verificare la registrazione del protocollo pregresso dopo aver definito la modalità di attivazione
		this.rpp_data_prot =	 		XMLUtil.parseStrictAttribute(dom, xpath+"/@rpp_data_prot");
		this.rpp_num_prot =		 		XMLUtil.parseStrictAttribute(dom, xpath+"/@rpp_num_prot");
		this.rpp_num_rep =		 		XMLUtil.parseStrictAttribute(dom, xpath+"/repertorio/@rpp_num_rep");

		this.countCcTotali = dom.selectNodes(xpath+"/rif_interni/rif[@diritto='CC'][not(@cc_from_fasc) or @cc_from_fasc=''][not(@cc_from_racc) or @cc_from_racc='']").size();
		this.countCcFascicolo = dom.selectNodes(xpath+"/rif_interni/rif[@diritto='CC'][@cc_from_fasc!='' and @cc_from_fasc!='minuta_fasc']").size();
		this.countCcRaccoglitore = dom.selectNodes(xpath+"/rif_interni/rif[@diritto='CC'][@cc_from_racc!='']").size();
		this.countCcTotaliPersonali = dom.selectNodes(xpath+"/rif_interni/rif[@diritto='CC' and @personale='true'][not(@cc_from_fasc) or @cc_from_fasc=''][not(@cc_from_racc) or @cc_from_racc='']").size();
		
		this.interventoDefaultCC = Boolean.parseBoolean(XMLUtil.parseStrictAttribute(dom, "response/funzionalita_disponibili/@interventoDefaultCC"));

		// Suddivido i rif_interni in base al diritto (rpa, op, cc, cds)
		if (rif_interni != null && rif_interni.size() > 0) {
			for (int i=0; i<rif_interni.size(); i++) {
				Rif tmpRif = (Rif) rif_interni.get(i);
				if (tmpRif != null) {
					if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_RPA)) //diritto = RPA
						this.assegnazioneRPA = tmpRif;
					else if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_RPAM)) //diritto = RPA
						this.assegnazioneRPAM = tmpRif;
					else if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_RESO)) //diritto = reso
						this.assegnazioneRESO = tmpRif;
					else if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_OP)) //diritto = OP
						this.assegnazioneOP = tmpRif;
					else if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_OPM)) //diritto = OP
						this.assegnazioneOPM = tmpRif;
					else if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_CDS)) //diritto = CDS
						this.assegnazioneCDS.add(tmpRif);
					else if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_CC)) { //diritto = CC
						if (tmpRif.getCod_fasc() != null && tmpRif.getCod_fasc().length() > 0)
							countCCFromFasc++; // incremento i CC ereditati da un fascicolo
						this.assegnazioneCC.add(tmpRif); // diritto = CC
					}
				}
			}
		}

		// Nel caso in cui la lista di CC sia piena procedo con la suddivisione
		// dei cc fra cc specifici del doc e recuperati da fascicolo e raggruppamento
		// dei cc in base all'ufficio di appartenenza
		if (this.assegnazioneCC.size() > 0) {
			for (int i=0; i<this.assegnazioneCC.size(); i++) {
				Rif tmpRif = (Rif) assegnazioneCC.get(i);
				if (tmpRif != null && tmpRif.getDiritto() != null && !tmpRif.getDiritto().equals("")) {
					if ((tmpRif.getCc_from_fasc().equals("") || tmpRif.getCc_from_fasc().equals("linked_fasc")) && tmpRif.getCc_from_racc().equals("")) { // cc specifici del documento
						cc_list.add(tmpRif);
						if (tmpRif.getCod_uff() != null && !tmpRif.getCod_uff().equals("")) {
							if (cc_ufficio.containsKey(tmpRif.getCod_uff())) { // ufficio gia' presente nell'hashmap
								cc_ufficio.get(tmpRif.getCod_uff()).add(tmpRif);
							}
							else { // nuovo ufficio nell'hashmap
								ArrayList<Rif> new_list_ufficio = new ArrayList<Rif>();
								new_list_ufficio.add(tmpRif);
								cc_ufficio.put(tmpRif.getCod_uff(), new_list_ufficio);
							}
						}

					}
					// mbernardini 01/07/2016 : gestione cc raccoglitori
					else if (!tmpRif.getCc_from_racc().equals("")) { // cc ereditati dal raccoglitore
						cc_racc_list.add(tmpRif);
						if (tmpRif.getCod_uff() != null && !tmpRif.getCod_uff().equals("")) {
							if (cc_racc_ufficio.containsKey(tmpRif.getCod_uff())) { // ufficio gia' presente nell'hashmap
								cc_racc_ufficio.get(tmpRif.getCod_uff()).add(tmpRif);
							}
							else { // nuovo ufficio nell'hashmap
								ArrayList<Rif> new_list_ufficio = new ArrayList<Rif>();
								new_list_ufficio.add(tmpRif);
								cc_racc_ufficio.put(tmpRif.getCod_uff(), new_list_ufficio);
							}
						}
					}
					else if (!tmpRif.getCc_from_fasc().equals("") && !tmpRif.getCc_from_fasc().equals("minuta_fasc")) { // cc ereditati dal fascicolo (NON IN MINUTA)
						cc_fasc_list.add(tmpRif);
						if (tmpRif.getCod_uff() != null && !tmpRif.getCod_uff().equals("")) {
							if (cc_fasc_ufficio.containsKey(tmpRif.getCod_uff())) { // ufficio gia' presente nell'hashmap
								cc_fasc_ufficio.get(tmpRif.getCod_uff()).add(tmpRif);
							}
							else { // nuovo ufficio nell'hashmap
								ArrayList<Rif> new_list_ufficio = new ArrayList<Rif>();
								new_list_ufficio.add(tmpRif);
								cc_fasc_ufficio.put(tmpRif.getCod_uff(), new_list_ufficio);
							}
						}
					}
				}
			}
		}

		// Nel caso in cui la lista di CC sia vuota carico il primo rif int CC.
		// mbernardini 13/02/2015 : il caricamento del CC vuoto deve essere fatto anche nel caso in cui la lista dei CC sia
		// valorizzata con solo CC ereditati dal fascicolo (nuovo documento da fascicolo)
		if (this.assegnazioneCC.size() == 0 || this.assegnazioneCC.size() == countCCFromFasc) {
			assegnazioneCC.add(new Rif(interventoDefaultCC));
		}

		if (this.assegnazioneCDS.size() == 0) {
			Rif cds = new Rif();
			assegnazioneCDS.add(cds);
		}
		else {
			showCDSSection = true;
		}

		if (this.allegati.size() == 0) this.allegati.add(new Allegato());
		if (this.xlink.size() == 0) this.xlink.add(new Xlink());

		if (this.files.size() == 0) this.files.add(new XwFile());
		if (this.immagini.size() == 0) this.immagini.add(new XwFile());

		// Controllo la presenza di eventuali file prenotati
		int index = 0;
		while (!this.filesPrenotati && index < this.files.size()-1) {
			XwFile file = (XwFile) this.files.get(index);
			if (file != null && (file.isCheckinFile() || file.isUsedFile()))
				this.filesPrenotati = true;
			index = index + 1;
		}

		// Recupero dei valori da utilizzare per il riempimento di campi select (caso docEdit)
		mezzoTrasmissioneSelect = XMLUtil.parseSetOfElement(dom, "/response/" + tipo + "_mezzo_trasmissione_select/option", new Option());
		tipologiaSelect = XMLUtil.parseSetOfElement(dom, "/response/" + tipo + "_tipologia_select/option", new Option());
		repTipologiaSelect = XMLUtil.parseSetOfElement(dom, "/response/tipologie/tipologia", new Tipologia()); // viene valorizzato solo il parametro 'text'
		customSelect1 = XMLUtil.parseSetOfElement(dom, "/response/select_customSelect1/option", new Option()); // viene valorizzato solo il parametro 'value'
		customSelect2 = XMLUtil.parseSetOfElement(dom, "/response/select_customSelect2/option", new Option()); // viene valorizzato solo il parametro 'value'

		// Recupero delle societa' da gestire
		societaSelect = XMLUtil.parseSetOfElement(dom, "/response/societa_select/societa", new Societa());
		codSocieta = "";
		if (societaSelect != null && societaSelect.size() > 0) {
			for (int i=0; i<societaSelect.size(); i++) {
				Societa societa = (Societa) societaSelect.get(i);
				if (societa != null && societa.getSelected().equals("selected"))
					codSocieta = societa.getCod();
			}
		}
		this.societa = XMLUtil.parseStrictElement(dom, xpath + "/societa");

		// Recupero di mittente/destinatari per acquisizione immagini
		this.mittente.init(XMLUtil.createDocument(dom, xpath+"/mittente"));
		this.destinatari.init(XMLUtil.createDocument(dom, xpath+"/destinatari"));

		// gestione del libro firma (stato firma del documento)
		this.stato_firma =	XMLUtil.parseStrictAttribute(dom, xpath+"/@stato_firma");

		// documenti derivanti da email
		this.messageId = XMLUtil.parseStrictAttribute(dom, xpath+"/@messageId");
		this.emailAttachmentIndex = XMLUtil.parseStrictAttribute(dom, xpath+"/@emailAttachmentIndex");

		extra.init(XMLUtil.createDocument(dom, xpath+"/extra")); // gestione dei campi extra

		// caricamento della dimensione degli allegati del doc...
		loadDocInformaticiSize();

		//Verbale di Seduta per Docway Delibere
		this.nrecord_Sed = XMLUtil.parseStrictAttribute(dom, xpath+"/verbale_di_seduta/@nrecord_sed");
		this.verbale_di_seduta = 	 				XMLUtil.parseElement(dom, "doc/verbale_di_seduta");

		this.proposta.init(XMLUtil.createDocument(dom, xpath+"/proposta"));
		this.odg_seduta.init(XMLUtil.createDocument(dom, xpath+"/odg_seduta"));

		this.verbale = XMLUtil.countElements(dom, xpath+"/verbale_di_seduta") > 0;

		this.currDate =	XMLUtil.parseStrictAttribute(dom, "response/@currDate");

		// caricamento dei dati di cestino (cancellazione logica)
		this.cestino = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, xpath+"/@cestino", ""));
		this.datacestino = DateUtil.changeDateFormat(XMLUtil.parseStrictAttribute(dom, xpath+"/@data_cestino", ""), "yyyyMMdd", Const.DEFAULT_DATE_FORMAT); // TODO Dovrebbe essere caricato dal file di properties dell'applicazione

		// rifiuto di bozze in arrivo
		this.rifiuto.init(XMLUtil.createDocument(dom, xpath + "/rifiuto"));

		// mbernardini 02/03/2017 : possibilita' di settare come readOnly il documento
		this.readOnly = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, xpath+"/@readOnly", ""));

		// tiommi 18/07/2017 : possibilita' di settare il checkbox per la presa in carico
		if (Boolean.parseBoolean(XMLUtil.parseStrictAttribute(dom, "response/funzionalita_disponibili/@presaInCaricoAbilitata"))) {
			this.richiestaPresaInCarico = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, xpath+"/presaInCarico/@richiesta", ""));
			this.effettuataPresaInCarico = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, xpath+"/presaInCarico/@effettuata", ""));
			this.incaricatiPresaInCarico = handleRifPresaInCarico(dom.selectNodes(xpath+"/presaInCarico/incaricati/rif"));
			this.rifPresaInCarico = handleRifPresaInCarico(dom.selectNodes(xpath+"/rif_interni/rif"), XMLUtil.parseStrictAttribute(dom, "response/funzionalita_disponibili/@tipologieAssegnazione"));
		}

		// tiommi 07/08/2017 : flag per gestione pulsanti protocollazione / associazione numero di repertorio
		// 18/12/2017 : aggiunto controllo sui file da firmare (inibiscono la protocollazione) 
		if (this.bozza && !this.rifiuto.getStato().equalsIgnoreCase("rifutato") && !this.tipo.equalsIgnoreCase("varie") && !fileDaFirmare)
			this.protocollabile = true; 
		else
			this.protocollabile = false;
		
		// mbernardini 12/06/2018 : un documento e' repertoriabile anche se non in bozza ma di tipo varie
		if ((this.bozza || (this.tipo != null && this.tipo.equals("varie")))
				&& !this.rifiuto.getStato().equalsIgnoreCase("rifutato")
				&& this.repertorio != null
				&& this.repertorio.getCod() != null
				&& !this.repertorio.getCod().isEmpty()
				&& XMLUtil.parseStrictAttribute(dom, xpath+"/repertorio/@numero", "").isEmpty())
			this.repertoriabile = true;
		else
			this.repertoriabile = false;
		
		this.verificaVirus.init(XMLUtil.createDocument(dom, xpath+"/verificaVirus"));

		return this;
	}

	/**
	 * popola il rif relativo agli utenti che hanno preso in carico
	 */
	private List<RifPresaInCarico> handleRifPresaInCarico(List<Node> nodi) {
		List<RifPresaInCarico> result = new ArrayList<RifPresaInCarico>();
		if(nodi != null) {
			for(Node nodo : nodi) {
				RifPresaInCarico incaricato = new RifPresaInCarico(nodo.asXML());
				result.add(incaricato);
			}
		}
		return result;
	}

	/**
	 * popola il rif relativo ai rif che possono prendere in carico
	 * fa un controllo sul diritto per vedere se era tra i rif che potevano fare la presa in carico
	 */
	private List<Rif> handleRifPresaInCarico(List<Node> nodi, String tipologie) {
		List<String> tipologieList = Arrays.asList(tipologie.trim().split(","));
		List<Rif> result = new ArrayList<Rif>();
		if(nodi != null) {
			for(Node nodo : nodi) {
				Rif incaricato = new Rif(nodo.asXML());
				if(tipologieList.contains(incaricato.getDiritto())) {
					// skip dei cc senza diritto di intervento
					if(incaricato.getDiritto().equalsIgnoreCase("CC") && !incaricato.isIntervento())
						continue;
					result.add(incaricato);
				}
			}
		}
		return result;
	}

	/**
	 * caricamento della dimensione totale dei doc informatici allegati al documento
	 */
	private void loadDocInformaticiSize() {
		try {
			for (int i=0; i<getFiles().size(); i++) {
				if (getFiles().get(i).getDer_from().length() == 0 && getFiles().get(i).getSize() != null && getFiles().get(i).getSize().length() > 0)
					docInformaticiSize += new Integer(getFiles().get(i).getSize()).intValue();
			}
			for (int i=0; i<getImmagini().size(); i++) {
				if (getImmagini().get(i).getSize() != null && getImmagini().get(i).getSize().length() > 0)
					docInformaticiSize += new Integer(getImmagini().get(i).getSize()).intValue();
			}
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
			docInformaticiSize = 0;
		}
	}

	@SuppressWarnings("unchecked")
	public void initHistory(Document dom){
		this.history = XMLUtil.parseSetOfElement(dom, "//item", new History());
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return asFormAdapterParams(prefix, false, false);
	}

	public Map<String, String> asFormAdapterParams(String prefix, boolean modify) {
		return asFormAdapterParams(prefix, modify, false);
	}

	/**
	 * impostazione parametri su formAdapter con controlli vari:
	 * 1) distinzione fra inserimento e modifica
	 * 2) impostazione dati di repertorio
	 *
	 * @param prefix prefisso da agganciare al nome del parametro
	 * @param modify true se si sta eseguendo un salvataggio da modifica, false altrimenti
	 * @param isRepertorio true se si sta eseguendo un salvataggio di un repertorio, false altrimenti
	 * @return
	 */
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean isRepertorio) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();

    	params.put(prefix+".@nrecord", this.nrecord);
    	params.put(prefix+".@data_prot", this.data_prot);

    	if (!modify) {
    		// parametri che devono essere spediti solo in caso di inserimento

    		// gestione multisocieta'
    		if (codSocieta != null && codSocieta.trim().length() > 0)
    			params.put("_CODSOCIETA_", codSocieta);

    		if (!this.tipo.equals(Const.DOCWAY_TIPOLOGIA_VARIE))
    			params.put(prefix+".@num_prot", this.num_prot);

    		params.put(prefix+".@anno", this.anno);
    		params.put(prefix+".@cod_amm_aoo", this.cod_amm_aoo);
    		params.put(prefix+".@annullato", this.annullato);

    		if (this.bozza)
        		params.put(prefix+".@bozza", "si");
        	else
        		params.put(prefix+".@bozza", "no");
        	if (this.sensibile)
        		params.put(prefix+".@sensibile", "si");
        	else
        		params.put(prefix+".@sensibile", "no");

        	// link interni
        	for (int i = 0; i < link_interni.size(); i++) {
        		Link_interno link = (Link_interno) link_interni.get(i);
        		params.putAll(link.asFormAdapterParams(".link_interno["+String.valueOf(i)+"]"));
    		}

        	// inserimento in fascicolo
        	if (this.fasc_rpam != null && this.fasc_rpam.getNumero() != null && this.fasc_rpam.getNumero().length() > 0)
        		params.put("*inserisciInFascicolo", fasc_rpam.getNumero());
        	else if (this.fasc_rpa != null && this.fasc_rpa.getNumero() != null && this.fasc_rpa.getNumero().length() > 0)
        		params.put("*inserisciInFascicolo", fasc_rpa.getNumero());

        	// contenuto in... (gestione raccoglitori)
        	String insInRaccoglitori = "";
    		for (int i = 0; i < contenuto_in.size(); i++) {
    			Contenuto_in raccoglitore = (Contenuto_in) contenuto_in.get(i);
    			if (raccoglitore != null) {
    				insInRaccoglitori = insInRaccoglitori + raccoglitore.getCod() + "|";
    			}
    		}
    		if (!insInRaccoglitori.equals(""))
    			params.put("*inserisciInRaccoglitori", insInRaccoglitori);

        	// gestione della registrazione del protocollo pregresso
        	if (this.rpp_data_prot != null && this.rpp_data_prot.length() > 0)
        		params.put(prefix+".@rpp_data_prot", this.rpp_data_prot);
        	if (this.rpp_num_prot != null && this.rpp_num_prot.length() > 0)
        		params.put(prefix+".@rpp_num_prot", this.rpp_num_prot);
        	if (this.rpp_num_rep != null && this.rpp_num_rep.length() > 0)
        		params.put(prefix+".repertorio.@rpp_num_rep", this.rpp_num_rep);

        	// impostazione dei dati di repertorio solo se effettivamente si sta
        	// eseguedo il salvataggio di un repertorio
        	if (isRepertorio)
        		params.putAll(repertorio.asFormAdapterParams(".repertorio"));

        }
    	else {
    		// parametri che devono essere spediti solo in caso di modifica

    		if (this.tipo.equals(Const.DOCWAY_TIPOLOGIA_VARIE))
    			params.put(prefix+".@data_prot", this.data_prot); // la data di protocollo puo' essere aggiornata in modifica solo sui doc non protocollati
    		
    		if (isRepertorio)
    			params.putAll(trasformazioneRep.asFormAdapterParams(".trasformazioneRep"));
    	}

    	// gestione dei documenti personali (solo doc non protocollati)
    	if (this.tipo.equals(Const.DOCWAY_TIPOLOGIA_VARIE)) {
    		if (this.personale)
        		params.put(prefix+".@personale", "si");
        	else
        		params.put(prefix+".@personale", "no");
    	}

    	params.put(prefix+".@data_reale", this.data_reale);
    	params.put(prefix+".@scarto", this.scarto);
    	params.put(prefix+".oggetto", this.oggetto);
    	params.putAll(note.asFormAdapterParams(".note"));
    	params.put(prefix+".corpoEmail", this.corpoEmail);
    	params.putAll(visibilita.asFormAdapterParams(".visibilita"));
    	params.putAll(classif.asFormAdapterParams(".classif"));
    	params.put("motiv_ogg_div", this.motiv_ogg_div);
    	params.putAll(voce_indice.asFormAdapterParams(".voce_indice"));
    	params.putAll(registro_emergenza.asFormAdapterParams(".registro_emergenza"));
    	params.putAll(mezzo_trasmissione.asFormAdapterParams(".mezzo_trasmissione"));
    	params.putAll(scadenza.asFormAdapterParams(".scadenza"));
    	params.putAll(pubblicazione.asFormAdapterParams(".pubblicazione"));
    	params.putAll(impronta.asFormAdapterParams(".impronta"));
    	params.putAll(riferimenti.asFormAdapterParams(".riferimenti"));
    	params.putAll(keywords.asFormAdapterParams(".keywords"));
    	params.putAll(tipologia.asFormAdapterParams(".tipologia"));
    	params.putAll(conservazione.asFormAdapterParams(".conservazione"));


    	for (int i = 0; i < xlink.size(); i++) {
    		Xlink link = (Xlink) xlink.get(i);
    		params.putAll(link.asFormAdapterParams(".xlink["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < allegati.size(); i++) {
    		Allegato allegato = (Allegato) allegati.get(i);
    		params.putAll(allegato.asFormAdapterParams(".allegato["+String.valueOf(i)+"]"));
		}


    	// mbernardini 10/04/2015 : gli assegnatari del documento devono essere spediti al service solo se non sono in modifica
    	// oppure se sono in modifica di un documento in bozza (o di tipo non protocollato) e non fascicolato
    	if (!modify || ((tipo.equals(Const.DOCWAY_TIPOLOGIA_VARIE) || (bozza)) && (assegnazioneRPA.getCod_fasc() == null || assegnazioneRPA.getCod_fasc().length() == 0))) {
	    	params.putAll(getAssegnazioneRPAParam());
	    	params.putAll(getAssegnazioneOPParam());
	    	params.putAll(getAssegnazioneCCParam());
	    	params.putAll(getAssegnazioneCDSParam());
    	}

    	boolean existsFileDaFirmare = false;

    	// files
    	int index = 0;
    	boolean fileFound = false;
    	for (int i = 0; i < files.size(); i++) {
			XwFile xwFile = (XwFile) files.get(i);
			if (xwFile.getXwayId() != null && xwFile.getXwayId().length() > 0) {
				// carico il file solo se valorizzati name e title
				if (xwFile.getName() != null && xwFile.getName().length() > 0 && xwFile.getTitle() != null && xwFile.getTitle().length() > 0) {
					fileFound = true;

					params.put("files.xw:file[" + index + "].@name", xwFile.getName());
					params.put("*files.xw:file[" + index + "].@title", xwFile.getTitle());
					// caso di inserimento del doc o modifica del doc con aggiunta di un nuovo allegato
					params.put("*files.xw:file[" + index + "].@name", xwFile.getXwayId());

					params.put("*files.xw:file[" + index + "].@da_firmare", (xwFile.isDa_firmare()) ? "yes" : ""); // gestione libro firma

					// mbernardini 11/12/2015 : eliminato il passaggio dei dati relativi all'impronta perche' gia' gestito il recupero lato service
					// impronta SHA256 su singoli files
					/*
					if (xwFile.getImpronta() != null && xwFile.getImpronta().length() > 0)
						params.put("*files.xw:file[" + index + "].@impronta", xwFile.getImpronta());
					if (xwFile.getTipoImpronta() != null && xwFile.getTipoImpronta().length() > 0)
						params.put("*files.xw:file[" + index + "].@tipoImpronta", xwFile.getTipoImpronta());
					*/

					index ++;
				}
			}
			else {
				// carico il file solo se valorizzati name e title
				if (xwFile.getName() != null && xwFile.getName().length() > 0 && xwFile.getTitle() != null && xwFile.getTitle().length() > 0) {
					fileFound = true;

					// caso di modifica del doc (allegato precedentemente caricato)
					params.put("*files.xw:file[" + index + "].@title", xwFile.getTitle());
					params.put("*files.xw:file[" + index + "].@name", xwFile.getName());

					params.put("*files.xw:file[" + index + "].@da_firmare", (xwFile.isDa_firmare()) ? "yes" : ""); // gestione libro firma

					// mbernardini 11/12/2015 : eliminato il passaggio dei dati relativi all'impronta perche' gia' gestito il recupero lato service
					// impronta SHA256 su singoli files
					/*
					if (xwFile.getImpronta() != null && xwFile.getImpronta().length() > 0)
						params.put("*files.xw:file[" + index + "].@impronta", xwFile.getImpronta());
					if (xwFile.getTipoImpronta() != null && xwFile.getTipoImpronta().length() > 0)
						params.put("*files.xw:file[" + index + "].@tipoImpronta", xwFile.getTipoImpronta());
					*/

					index ++;
				}
			}

			if (xwFile.isDa_firmare())
				existsFileDaFirmare = true;
		}

    	if (!fileFound) { // necessario in caso di cancellazione da parte dell'utente di tutti i files inclusi nel documento
    		params.put("files.xw:file[0].@name", "");
    		params.put("*files.xw:file[0].@title", "");
    		params.put("*files.xw:file[0].@name", "");
    	}

		// immagini
    	index = 0;
    	boolean imageFound = false;
    	for (int i = 0; i < immagini.size(); i++) {
			XwFile xwFile = (XwFile) immagini.get(i);
			if (xwFile.getXwayId() != null && xwFile.getXwayId().length() > 0) {
				// carico il file solo se valorizzati name e title
				if (xwFile.getName() != null && xwFile.getName().length() > 0 && xwFile.getTitle() != null && xwFile.getTitle().length() > 0) {
					imageFound = true;

					// caso di inserimento del doc o modifica del doc con aggiunta di un nuovo allegato
					params.put("immagini.xw:file[" + index + "].@name", xwFile.getName());
					params.put("*immagini.xw:file[" + index + "].@title", xwFile.getTitle());
					params.put("*immagini.xw:file[" + index + "].@name", xwFile.getXwayId());

					params.put("*immagini.xw:file[" + index + "].@da_firmare", (xwFile.isDa_firmare()) ? "yes" : ""); // gestione libro firma

					// mbernardini 11/12/2015 : eliminato il passaggio dei dati relativi all'impronta perche' gia' gestito il recupero lato service
					// impronta SHA256 su singoli files
					/*
					if (xwFile.getImpronta() != null && xwFile.getImpronta().length() > 0)
						params.put("*immagini.xw:file[" + index + "].@impronta", xwFile.getImpronta());
					if (xwFile.getTipoImpronta() != null && xwFile.getTipoImpronta().length() > 0)
						params.put("*immagini.xw:file[" + index + "].@tipoImpronta", xwFile.getTipoImpronta());
					*/

					index ++;
				}
			}
			else {
				// carico il file solo se valorizzati name e title
				if (xwFile.getName() != null && xwFile.getName().length() > 0 && xwFile.getTitle() != null && xwFile.getTitle().length() > 0) {
					imageFound = true;

					// caso di modifica del doc (allegato precedentemente caricato)
					params.put("*immagini.xw:file[" + index + "].@title", xwFile.getTitle());
					params.put("*immagini.xw:file[" + index + "].@name", xwFile.getName());

					params.put("*immagini.xw:file[" + index + "].@da_firmare", (xwFile.isDa_firmare()) ? "yes" : ""); // gestione libro firma

					// mbernardini 11/12/2015 : eliminato il passaggio dei dati relativi all'impronta perche' gia' gestito il recupero lato service
					// impronta SHA256 su singoli files
					/*
					if (xwFile.getImpronta() != null && xwFile.getImpronta().length() > 0)
						params.put("*immagini.xw:file[" + index + "].@impronta", xwFile.getImpronta());
					if (xwFile.getTipoImpronta() != null && xwFile.getTipoImpronta().length() > 0)
						params.put("*immagini.xw:file[" + index + "].@tipoImpronta", xwFile.getTipoImpronta());
					*/

					index ++;
				}
			}

			if (xwFile.isDa_firmare())
				existsFileDaFirmare = true;
		}

    	if (!imageFound) { // necessario in caso di cancellazione da parte dell'utente di tutte le immagini incluse nel documento
    		params.put("immagini.xw:file[0].@name", "");
    		params.put("*immagini.xw:file[0].@title", "");
    		params.put("*immagini.xw:file[0].@name", "");
    	}

		// conversioni di files e immagini
		if (agent_xml)
			params.put("*agent_xml", "1");
		if (agent_pdf)
			params.put("*agent_pdf", "1");
		if (agent_ocr)
			params.put("*agent_ocr", "1");
		if (iagent_pdf)
			params.put("*iagent_pdf", "1");

		// se almeno un file da firmare imposto lo stato firma del documento
		if (existsFileDaFirmare)
			params.put(".@stato_firma", "attesa firma");
		else if (stato_firma.equals("attesa firma"))
			params.put(".@stato_firma", "");
		else
			params.put(".@stato_firma", stato_firma);

		for (int i = 0; i < numero_raccomandata.size(); i++) {
    		NumeroRaccomandata numRaccomandata = (NumeroRaccomandata) numero_raccomandata.get(i);
    		params.putAll(numRaccomandata.asFormAdapterParams(".numero_raccomandata["+String.valueOf(i)+"]"));
		}

		// Imposto il parametro relativo all'invio della mail di notifica ai rif int
		params.put("*sendMailRifInterni", this.sendMailRifInterni+"");
		// ERM012596 - rtirabassi - notifica capillare
		params.put("*sendMailSelectedRifInterni", this.sendMailSelectedRifInterni+"");
		if ( this.sendMailSelectedRifInterni ) {
			params.put("*invioCapillareNotifiche", this.getNotificheCapillariParam());
		}

		params.putAll(extra.asFormAdapterParams(prefix+".extra", modify)); // gestione dei campi extra

		//Verbale di Seduta per Docway Delibere
		if(verbale && !modify){
			params.put(prefix+".verbale_di_seduta.@nrecord_sed", this.nrecord_Sed);
			params.put(prefix+".verbale_di_seduta", this.verbale_di_seduta);
			params.put(prefix+".proposta.@cod_organo", this.proposta.getCod_organo());
			params.put(prefix+".odg_seduta.@data_convocazione", this.odg_seduta.getData_convocazione());
		}

		if(this.richiestaPresaInCarico)
			params.put("*handleRichiestaPresaInCarico", "true");

    	return params;
	}

	/**
	 * impostazione parametri su formAdapter in caso di acquisizione immagini
	 *
	 * @param prefix
	 * @return
	 */
	public Map<String, String> asFormAdapterImagesParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();

    	// dati di conservazione
    	params.putAll(conservazione.asFormAdapterParams(".conservazione"));

    	// immagini
		for (int i = 0; i < immagini.size(); i++) {
			XwFile xwFile = (XwFile) immagini.get(i);
			if (xwFile.getXwayId() != null && xwFile.getXwayId().length() > 0) {
				// caso di inserimento del doc o modifica del doc con aggiunta di un nuovo allegato
				params.put("immagini.xw:file[" + i + "].@name", xwFile.getName());
				params.put(".immagini.xw:file[" + i + "].@title", xwFile.getTitle());
				params.put(".immagini.xw:file[" + i + "].@name", xwFile.getXwayId());
			}
		}

		// conversioni di immagini
		if (agent_ocr)
			params.put("*agent_ocr", "1");
		if (iagent_pdf)
			params.put("*iagent_pdf", "1");

    	return params;
	}

	public Map<String, String> getAssegnazioneRPAParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		//if (assegnazioneRPA != null && assegnazioneRPA.getNome_uff() != null && assegnazioneRPA.getNome_uff().length() > 0) {
			params.putAll(assegnazioneRPA.asFormAdapterParams("*rif_interni.assegnazioneRPA"));
		//}
		return params;
	}

	public Map<String, String> getAssegnazioneRPAMParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		//if (assegnazioneRPAM != null && assegnazioneRPAM.getNome_uff() != null && assegnazioneRPAM.getNome_uff().length() > 0) {
			params.putAll(assegnazioneRPAM.asFormAdapterParams("*rif_interni.assegnazioneRPAM"));
		//}
		return params;
	}

	public Map<String, String> getAssegnazioneOPParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		//if (assegnazioneOP != null && assegnazioneOP.getNome_uff() != null && assegnazioneOP.getNome_uff().length() > 0) {
			params.putAll(assegnazioneOP.asFormAdapterParams("*rif_interni.assegnazioneOP"));
		//}
		return params;
	}

	public Map<String, String> getAssegnazioneOPMParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		//if (assegnazioneOPM != null && assegnazioneOPM.getNome_uff() != null && assegnazioneOPM.getNome_uff().length() > 0) {
			params.putAll(assegnazioneOPM.asFormAdapterParams("*rif_interni.assegnazioneOPM"));
		//}
		return params;
	}

	/**
	 * aggiunta di parametri di assegnazione dei CDS
	 * @param addEmpty true se occorre passare il parametro anche se CDS e' vuoto, false altrimenti
	 * @return
	 */
	public Map<String, String> getAssegnazioneCDSParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (assegnazioneCDS != null && assegnazioneCDS.size() > 0) {
			for (int i=0; i<assegnazioneCDS.size(); i++) {
				Rif rif = (Rif) assegnazioneCDS.get(i);
				//if (rif != null && ((rif.getNome_uff() != null && rif.getNome_uff().length() > 0) || addEmpty))
				if (rif != null)
					params.putAll(rif.asFormAdapterParams("*rif_interni.assegnazioneCDS["+String.valueOf(i)+"]"));
			}
		}
		return params;
	}

	public Map<String, String> getAssegnazioneCCParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (assegnazioneCC != null && assegnazioneCC.size() > 0) {
			for (int i=0; i<assegnazioneCC.size(); i++) {
				Rif rif = (Rif) assegnazioneCC.get(i);
				//if (rif != null && rif.getNome_uff() != null && rif.getNome_uff().length() > 0)
				if (rif != null)
					params.putAll(rif.asFormAdapterParams("*rif_interni.assegnazioneCC["+String.valueOf(i)+"]"));
			}
		}
		return params;
	}
	
	/**
	 * Codifica i riferimenti interni selezionati per l'invio al servizio
	 * @return la codifica del singolo riferimento
	 */
	private String encodeNotificaCapillare(Rif r) {
		if ( null == r ) return "" ;
		if ( r.isUfficio_completo() ) {
			return "$U$"+r.getCod_uff();
		}
		if ( "ruolo" == r.getTipo_uff() ) {
			return "$R$"+r.getNome_uff();
		}
		return "$P$"+r.getCod_persona();
	}
	
	/**
	 * Elenca i riferimenti interni selezionati per la notifica capillare in forma codificata
	 * @return 
	 */
	public String getNotificheCapillariParam(){
        String capillari = "";
		if (assegnazioneRPA.isNotifica_capillare() ) capillari += "|" + encodeNotificaCapillare(assegnazioneRPA) ;
		if (assegnazioneRPAM.isNotifica_capillare() ) capillari += "|" + encodeNotificaCapillare(assegnazioneRPAM) ;
		if (assegnazioneOP.isNotifica_capillare() ) capillari += "|" + encodeNotificaCapillare(assegnazioneOP) ;
		if (assegnazioneOPM.isNotifica_capillare() ) capillari += "|" + encodeNotificaCapillare(assegnazioneOPM) ;
		if (assegnazioneCC != null && assegnazioneCC.size() > 0) {
			for (int i=0; i<assegnazioneCC.size(); i++) {
				Rif rif = (Rif) assegnazioneCC.get(i);
				if (rif != null && rif.isNotifica_capillare()) {
					String cod = encodeNotificaCapillare(rif);
					if ( !cod.isEmpty() ) { 
						capillari += "|" + cod ;
					}
				}
			}
		}
		if (assegnazioneCDS != null && assegnazioneCDS.size() > 0) {
			for (int i=0; i<assegnazioneCDS.size(); i++) {
				Rif rif = (Rif) assegnazioneCDS.get(i);
				if (rif != null && rif.isNotifica_capillare()) {
					String cod = encodeNotificaCapillare(rif);
					if ( !cod.isEmpty() ) { 
						capillari += "|" + cod ;
					}
				}
			}
		}
		return (capillari.length() > 0 ? capillari.substring(1) : capillari);
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setClassif(Classif classif) {
		this.classif = classif;
	}

	public Classif getClassif() {
		return classif;
	}

	public void setClassifNV(Classif classif) {
		this.classifNV = classif;
	}

	public Classif getClassifNV() {
		return classifNV;
	}

	public String getMotiv_ogg_div() {
		return motiv_ogg_div;
	}

	public void setMotiv_ogg_div(String motiv_ogg_div) {
		this.motiv_ogg_div = motiv_ogg_div;
	}

	public void setStoria(List<Storia> storia) {
		this.storia = storia;
	}

	public List<Storia> getStoria() {
		return storia;
	}

	public void setWorkflowInstances(List<WorkflowInstance> workflows) {
		this.workflowInstances = workflows;
	}

	public List<WorkflowInstance> getWorkflowInstances() {
		return workflowInstances;
	}

	public void setCreazione(Creazione creazione) {
		this.creazione = creazione;
	}

	public Creazione getCreazione() {
		return creazione;
	}

	public void setProtocollazione(Protocollazione protocollazione) {
		this.protocollazione = protocollazione;
	}

	public Protocollazione getProtocollazione() {
		return protocollazione;
	}

	public void setHistory(List<History> history) {
		this.history = history;
	}

	public List<History> getHistory() {
		return history;
	}

	public void setUltima_modifica(UltimaModifica ultimaModifica) {
		this.ultima_modifica = ultimaModifica;
	}

	public UltimaModifica getUltima_modifica() {
		return ultima_modifica;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setNum_prot(String num_prot) {
		this.num_prot = num_prot;
	}

	public String getNum_prot() {
		return num_prot;
	}

	public void setData_prot(String data_prot) {
		this.data_prot = data_prot;
	}

	public String getData_prot() {
		return data_prot;
	}

	public String getConverted_data_prot() {
		if (data_prot.length() == 8)
			return data_prot.substring(6)+"/"+data_prot.substring(4, 6)+"/"+data_prot.substring(0, 4); // TODO caricare il formato della data da file di properties
		else
			return data_prot;
	}

	public void setData_reale(String data_reale) {
		this.data_reale = data_reale;
	}

	public String getData_reale() {
		return data_reale;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}

	public String getAnno() {
		return anno;
	}

	public void setCod_amm_aoo(String cod_amm_aoo) {
		this.cod_amm_aoo = cod_amm_aoo;
	}

	public String getCod_amm_aoo() {
		return cod_amm_aoo;
	}

	public void setAnnullato(String annullato) {
		this.annullato = annullato;
	}

	public String getAnnullato() {
		return annullato;
	}

	public String getAbrogato() {
		return abrogato;
	}

	public void setAbrogato(String abrogato) {
		this.abrogato = abrogato;
	}

	public void setScarto(String scarto) {
		this.scarto = scarto;
	}

	public String getScarto() {
		return scarto;
	}

	public void setFascicoli_collegati(List<Fasc> fascicoli_collegati) {
		this.fascicoli_collegati = fascicoli_collegati;
	}

	public List<Fasc> getFascicoli_collegati() {
		return fascicoli_collegati;
	}

	public void setMezzo_trasmissione(MezzoTrasmissione mezzo_trasmissione) {
		this.mezzo_trasmissione = mezzo_trasmissione;
	}

	public MezzoTrasmissione getMezzo_trasmissione() {
		return mezzo_trasmissione;
	}

	public void setScadenza(Scadenza scadenza) {
		this.scadenza = scadenza;
	}

	public Scadenza getScadenza() {
		return scadenza;
	}

	public void setConservazione(Conservazione conservazione) {
		this.conservazione = conservazione;
	}

	public Conservazione getConservazione() {
		return conservazione;
	}

	public Pubblicazione getPubblicazione() {
		return pubblicazione;
	}

	public void setPubblicazione(Pubblicazione pubblicazione) {
		this.pubblicazione = pubblicazione;
	}

	public void setImpronta(Impronta impronta) {
		this.impronta = impronta;
	}

	public Impronta getImpronta() {
		return impronta;
	}

	public void setAllegato(List<Allegato> allegato) {
		this.allegati = allegato;
	}

	public List<Allegato> getAllegati() {
		return allegati;
	}

	public void setFiles(List<XwFile> files) {
		this.files = files;
	}

	public List<XwFile> getFiles() {
		return files;
	}

	public void setImmagini(List<XwFile> immagini) {
		this.immagini = immagini;
	}

	public List<XwFile> getImmagini() {
		return immagini;
	}

	public String getShowIwxSelectedImage() {
		return showIwxSelectedImage;
	}

	public void setShowIwxSelectedImage(String showIwxSelectedImage) {
		this.showIwxSelectedImage = showIwxSelectedImage;
	}

	public void setRif_esterni(List<RifEsterno> rif_esterni) {
		this.rif_esterni = rif_esterni;
	}

	public List<RifEsterno> getRif_esterni() {
		return rif_esterni;
	}

	public void setInteroperabilita_multipla(List<Interoperabilita> interoperabilita_multipla) {
		this.interoperabilita_multipla = interoperabilita_multipla;
	}

	public List<Interoperabilita> getInteroperabilita_multipla() {
		return interoperabilita_multipla;
	}

	public void setFasc_rpa(Fasc_rpa fasc_rpa) {
		this.fasc_rpa = fasc_rpa;
	}

	public Fasc_rpa getFasc_rpa() {
		return fasc_rpa;
	}

	public void setAutore(Autore autore) {
		this.autore = autore;
	}

	public Autore getAutore() {
		return autore;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public Note getNote() {
		return note;
	}

	public void setPostit(List<Postit> postit) {
		this.postit = postit;
	}

	public List<Postit> getPostit() {
		return postit;
	}

	public void setCountCcTotali(int countCcTotali) {
		this.countCcTotali = countCcTotali;
	}

	public int getCountCcTotali() {
		return countCcTotali;
	}

	public void setCountCcFascicolo(int countCcFascicolo) {
		this.countCcFascicolo = countCcFascicolo;
	}

	public int getCountCcFascicolo() {
		return countCcFascicolo;
	}

	public int getCountCcRaccoglitore() {
		return countCcRaccoglitore;
	}

	public void setCountCcRaccoglitore(int countCcRaccoglitore) {
		this.countCcRaccoglitore = countCcRaccoglitore;
	}

	public void setCountCcTotaliPersonali(int countCcTotaliPersonali) {
		this.countCcTotaliPersonali = countCcTotaliPersonali;
	}

	public int getCountCcTotaliPersonali() {
		return countCcTotaliPersonali;
	}

	public void setContenuto_in(List<Contenuto_in> contenuto_in) {
		this.contenuto_in = contenuto_in;
	}

	public List<Contenuto_in> getContenuto_in() {
		return contenuto_in;
	}

	public void setLink_interni(List<Link_interno> link_interni) {
		this.link_interni = link_interni;
	}

	public List<Link_interno> getLink_interni() {
		return link_interni;
	}

	public void setXlink(List<Xlink> xlink) {
		this.xlink = xlink;
	}

	public List<Xlink> getXlink() {
		return xlink;
	}

	public void setPersonale(boolean personale) {
		this.personale = personale;
	}

	public boolean isPersonale() {
		return personale;
	}

	public void setBozza(boolean bozza) {
		this.bozza = bozza;
	}

	public boolean isBozza() {
		return bozza;
	}

	public void setSensibile(boolean sensibile) {
		this.sensibile = sensibile;
	}

	public boolean isSensibile() {
		return sensibile;
	}

	public void setAnnullamento(Annullamento annullamento) {
		this.annullamento = annullamento;
	}

	public Annullamento getAnnullamento() {
		return annullamento;
	}

	public Abrogazione getAbrogazione() {
		return abrogazione;
	}

	public void setAbrogazione(Abrogazione abrogazione) {
		this.abrogazione = abrogazione;
	}

	public Rif getAssegnazioneRPA() {
		return assegnazioneRPA;
	}

	public void setAssegnazioneRPA(Rif assegnazioneRPA) {
		this.assegnazioneRPA = assegnazioneRPA;
	}

	public Rif getAssegnazioneOP() {
		return assegnazioneOP;
	}

	public void setAssegnazioneOP(Rif assegnazioneOP) {
		this.assegnazioneOP = assegnazioneOP;
	}

	public Rif getAssegnazioneOPM() {
		return assegnazioneOPM;
	}

	public void setAssegnazioneOPM(Rif assegnazioneOPM) {
		this.assegnazioneOPM = assegnazioneOPM;
	}

	public List<Rif> getAssegnazioneCC() {
		return assegnazioneCC;
	}

	public void setAssegnazioneCC(List<Rif> assegnazioneCC) {
		this.assegnazioneCC = assegnazioneCC;
	}

	public List<Rif> getAssegnazioneCDS() {
		return assegnazioneCDS;
	}

	public void setAssegnazioneCDS(List<Rif> assegnazioneCDS) {
		this.assegnazioneCDS = assegnazioneCDS;
	}

	public void setAssegnazioneRESO(Rif assegnazioneRESO) {
		this.assegnazioneRESO = assegnazioneRESO;
	}

	public Rif getAssegnazioneRESO() {
		return assegnazioneRESO;
	}

	public List<Rif> getCc_list() {
		return cc_list;
	}

	public void setCc_list(List<Rif> cc_list) {
		this.cc_list = cc_list;
	}

	public List<Rif> getCc_fasc_list() {
		return cc_fasc_list;
	}

	public void setCc_fasc_list(List<Rif> cc_fasc_list) {
		this.cc_fasc_list = cc_fasc_list;
	}

	public HashMap<String, List<Rif>> getCc_ufficio() {
		return cc_ufficio;
	}

	public void setCc_ufficio(HashMap<String, List<Rif>> cc_ufficio) {
		this.cc_ufficio = cc_ufficio;
	}

	public HashMap<String, List<Rif>> getCc_fasc_ufficio() {
		return cc_fasc_ufficio;
	}

	public void setCc_fasc_ufficio(HashMap<String, List<Rif>> cc_fasc_ufficio) {
		this.cc_fasc_ufficio = cc_fasc_ufficio;
	}

	public List<Rif> getCc_racc_list() {
		return cc_racc_list;
	}

	public void setCc_racc_list(List<Rif> cc_racc_list) {
		this.cc_racc_list = cc_racc_list;
	}

	public HashMap<String, List<Rif>> getCc_racc_ufficio() {
		return cc_racc_ufficio;
	}

	public void setCc_racc_ufficio(HashMap<String, List<Rif>> cc_racc_ufficio) {
		this.cc_racc_ufficio = cc_racc_ufficio;
	}

	public void setSendMailRifInterni(boolean sendMailRifInterni) {
		this.sendMailRifInterni = sendMailRifInterni;
	}

	public boolean isSendMailRifInterni() {
		return sendMailRifInterni;
	}

	public void setSendMailSelectedRifInterni(boolean sendMailSelectedRifInterni) {
		this.sendMailSelectedRifInterni = sendMailSelectedRifInterni;
	}
	
	public boolean isSendMailSelectedRifInterni() {
		return sendMailSelectedRifInterni;
	}

	public boolean isCheckNomi() {
		return checkNomi;
	}

	public void setCheckNomi(boolean checkNomi) {
		this.checkNomi = checkNomi;
	}

	public boolean isAgent_xml() {
		return agent_xml;
	}

	public void setAgent_xml(boolean agent_xml) {
		this.agent_xml = agent_xml;
	}

	public boolean isAgent_pdf() {
		return agent_pdf;
	}

	public void setAgent_pdf(boolean agent_pdf) {
		this.agent_pdf = agent_pdf;
	}

	public boolean isAgent_ocr() {
		return agent_ocr;
	}

	public void setAgent_ocr(boolean agent_ocr) {
		this.agent_ocr = agent_ocr;
	}

	public boolean isIagent_pdf() {
		return iagent_pdf;
	}

	public void setIagent_pdf(boolean iagent_pdf) {
		this.iagent_pdf = iagent_pdf;
	}


	public String getXwImageIdsAttached() {
		return xwImageIdsAttached;
	}

	public void setXwImageIdsAttached(String xwImageIdsAttached) {
		this.xwImageIdsAttached = xwImageIdsAttached;
	}

	public String getXwImageNamesAttached() {
		return xwImageNamesAttached;
	}

	public void setXwImageNamesAttached(String xwImageNamesAttached) {
		this.xwImageNamesAttached = xwImageNamesAttached;
	}

	public String getXwImageTitlesAttached() {
		return xwImageTitlesAttached;
	}

	public void setXwImageTitlesAttached(String xwImageTitlesAttached) {
		this.xwImageTitlesAttached = xwImageTitlesAttached;
	}

	public String getXwFileIdsAttached() {
		return xwFileIdsAttached;
	}

	public void setXwFileIdsAttached(String xwFileIdsAttached) {
		this.xwFileIdsAttached = xwFileIdsAttached;
	}

	public String getXwFileNamesAttached() {
		return xwFileNamesAttached;
	}

	public void setXwFileNamesAttached(String xwFileNamesAttached) {
		this.xwFileNamesAttached = xwFileNamesAttached;
	}

	public String getXwFileTitlesAttached() {
		return xwFileTitlesAttached;
	}

	public void setXwFileTitlesAttached(String xwFileTitlesAttached) {
		this.xwFileTitlesAttached = xwFileTitlesAttached;
	}

	/**
	 * Abilita/Disabilita la sezione CDS del documento
	 * @param showCDSSection true se la sezione deve essere aperta, false se la sezione deve essere chiusa
	 */
	public void setShowCDSSection(boolean showCDSSection) {
		if (!showCDSSection) {
			// Azzero la lista dei CDS
			this.assegnazioneCDS = new ArrayList<Rif>();
			this.assegnazioneCDS.add(new Rif());
		}
		this.showCDSSection = showCDSSection;
	}

	public boolean isShowCDSSection() {
		return showCDSSection;
	}

	public void setRegistro_emergenza(Registro_emergenza registro_emergenza) {
		this.registro_emergenza = registro_emergenza;
	}

	public Registro_emergenza getRegistro_emergenza() {
		return registro_emergenza;
	}

	public void setRepertorio(Repertorio repertorio) {
		this.repertorio = repertorio;
	}

	public Repertorio getRepertorio() {
		return repertorio;
	}

	public void setAssegnazioneRPAM(Rif assegnazioneRPAM) {
		this.assegnazioneRPAM = assegnazioneRPAM;
	}

	public Rif getAssegnazioneRPAM() {
		return assegnazioneRPAM;
	}

	public void setMinuta(Minuta minuta) {
		this.minuta = minuta;
	}

	public Minuta getMinuta() {
		return minuta;
	}

	public Storia getSegnatura(){
		Storia segnatura = null;
		for (Iterator<Storia> iter = storia.iterator(); iter.hasNext();) {
			Storia item = (Storia) iter.next();
			if (item.getTipo().equals("segnatura") && item.getCod_uff_oper().trim().length() >0)
				segnatura = item;
		}
		return segnatura;
	}

	public void setVisibilita(Visibilita visibilita) {
		this.visibilita = visibilita;
	}

	public Visibilita getVisibilita() {
		return visibilita;
	}

	public List<Option> getMezzoTrasmissioneSelect() {
		return mezzoTrasmissioneSelect;
	}

	public void setMezzoTrasmissioneSelect(List<Option> mezzo_trasmissione_select) {
		this.mezzoTrasmissioneSelect = mezzo_trasmissione_select;
	}

	public List<Option> getTipologiaSelect() {
		return tipologiaSelect;
	}

	public void setTipologiaSelect(List<Option> opzioni) {
		this.tipologiaSelect = opzioni;
	}

	public List<Societa> getSocietaSelect() {
		return societaSelect;
	}

	public void setSocietaSelect(List<Societa> societa_select) {
		this.societaSelect = societa_select;
	}

	public String getCodSocieta() {
		return codSocieta;
	}

	public void setCodSocieta(String codSocieta) {
		this.codSocieta = codSocieta;
	}

	public String getSocieta() {
		return societa;
	}

	public void setSocieta(String societa) {
		this.societa = societa;
	}

	public List<Tipologia> getRepTipologiaSelect() {
		return repTipologiaSelect;
	}

	public void setRepTipologiaSelect(List<Tipologia> tipologia) {
		this.repTipologiaSelect = tipologia;
	}

	public List<Option> getCustomSelect2() {
		return customSelect2;
	}

	public void setCustomSelect2(List<Option> customSelect2) {
		this.customSelect2 = customSelect2;
	}

	public List<Option> getCustomSelect1() {
		return customSelect1;
	}

	public void setCustomSelect1(List<Option> customSelect1) {
		this.customSelect1 = customSelect1;
	}

	public List<NumeroRaccomandata> getNumero_raccomandata() {
		return numero_raccomandata;
	}

	public void setNumero_raccomandata(List<NumeroRaccomandata> numero_raccomandata) {
		this.numero_raccomandata = numero_raccomandata;
	}

	public String getRpp_num_rep() {
		return rpp_num_rep;
	}

	public void setRpp_num_rep(String rpp_num_rep) {
		this.rpp_num_rep = rpp_num_rep;
	}

	public String getRpp_num_prot() {
		return rpp_num_prot;
	}

	public void setRpp_num_prot(String rpp_num_prot) {
		this.rpp_num_prot = rpp_num_prot;
	}

	public String getRpp_data_prot() {
		return rpp_data_prot;
	}

	public void setRpp_data_prot(String rpp_data_prot) {
		this.rpp_data_prot = rpp_data_prot;
	}

	public void setFasc_rpam(Fasc_rpam fasc_rpam) {
		this.fasc_rpam = fasc_rpam;
	}

	public Fasc_rpam getFasc_rpam() {
		return fasc_rpam;
	}

	public void setVoce_indice(Voce_indice voce_indice) {
		this.voce_indice = voce_indice;
	}

	public Voce_indice getVoce_indice() {
		return voce_indice;
	}

	public MittenteDoc getMittente() {
		return mittente;
	}

	public void setMittente(MittenteDoc mittente) {
		this.mittente = mittente;
	}

	public DestinatariDoc getDestinatari() {
		return destinatari;
	}

	public void setDestinatar(DestinatariDoc destinatari) {
		this.destinatari = destinatari;
	}

	/**
	 * Imposta il parametro di invio delle mail di notifica ai rif int in base
	 * al valore assunto da bozza
	 * @param e nuovo valore associato alla proprieta' bozza
	 */
	public void setNotificaAfterBozza(ValueChangeEvent e) {
		setSendMailRifInterni(!new Boolean(e.getNewValue()+"").booleanValue());
	}

	/**
	 * Aggiunta di un nuovo Rif int in CC
	 */
	public void addRifintCC(Rif rif) {
		int index = 0;
		if (rif != null)
			index = assegnazioneCC.indexOf(rif);

		if (assegnazioneCC != null) {
			Rif rifToAdd = new Rif(interventoDefaultCC);
			if (rif != null && rif.getTipo_uff() != null && rif.getTipo_uff().equals("ruolo"))
				rifToAdd.setTipo_uff("ruolo");

			if (assegnazioneCC.size() > index)
				assegnazioneCC.add(index+1, rifToAdd);
			else
				assegnazioneCC.add(rifToAdd);
		}
	}

	/**
	 * Aggiunta di un nuovo Rif int in CDS
	 */
	public void addRifintCDS(Rif rif) {
		int index = 0;
		if (rif != null)
			index = assegnazioneCDS.indexOf(rif);

		if (assegnazioneCDS != null) {
			Rif rifToAdd = new Rif();
			if (rif != null && rif.getTipo_uff() != null && rif.getTipo_uff().equals("ruolo"))
				rifToAdd.setTipo_uff("ruolo");

			if (assegnazioneCDS.size() > index)
				assegnazioneCDS.add(index+1, rifToAdd);
			else
				assegnazioneCDS.add(rifToAdd);
		}
	}

	/**
	 * Eliminazione di un Rif int in CC
	 */
	public void deleteRifintCC(Rif rif){
		if (rif != null) {
			assegnazioneCC.remove(rif);
			// mbernardini 13/02/2015 : se la lista di CC e' vuota o se tutti i CC presenti sono ereditati da un fascicolo
			// occorre inserire una istanza vuota in modo da permettere l'inserimento all'operatore (i CC ereditati non sono modificabili)
			if (assegnazioneCC.isEmpty() || assegnazioneCC.size() == countCCFromFasc)
				assegnazioneCC.add(new Rif(interventoDefaultCC));
		}
	}

	/**
	 * Eliminazione di un Rif int in CDS
	 */
	public void deleteRifintCDS(Rif rif){
		if (rif != null) {
			assegnazioneCDS.remove(rif);
			if (assegnazioneCDS.isEmpty())
				assegnazioneCDS.add(new Rif());
		}
	}

	/**
	 * Spostamento in alto di un Rif int in CC
	 */
	public void moveUpRifintCC(Rif rif){
		if (rif != null && assegnazioneCC != null) {
			int index = assegnazioneCC.indexOf(rif);
			if (index > 0 ) {
				assegnazioneCC.remove(index);
				assegnazioneCC.add(index-1, rif);
			}
		}
	}

	/**
	 * Spostamento in basso di un Rif int in CC
	 */
	public void moveDownRifintCC(Rif rif){
		if (rif != null && assegnazioneCC != null) {
			int index = assegnazioneCC.indexOf(rif);
			if (index < assegnazioneCC.size()-1 ) {
				assegnazioneCC.remove(index);
				assegnazioneCC.add(index+1, rif);
			}
		}
	}

	/**
	 * Spostamento in alto di un Rif int in CDS
	 */
	public void moveUpRifintCDS(Rif rif){
		if (rif != null && assegnazioneCDS != null) {
			int index = assegnazioneCDS.indexOf(rif);
			if (index > 0 ) {
				assegnazioneCDS.remove(index);
				assegnazioneCDS.add(index-1, rif);
			}
		}
	}

	/**
	 * Spostamento in basso di un Rif int in CC
	 */
	public void moveDownRifintCDS(Rif rif){
		if (rif != null && assegnazioneCDS != null) {
			int index = assegnazioneCDS.indexOf(rif);
			if (index < assegnazioneCDS.size()-1 ) {
				assegnazioneCDS.remove(index);
				assegnazioneCDS.add(index+1, rif);
			}
		}
	}

	/**
	 * Aggiunta di un nuovo Rif int vuoto in CC in ultima posizione
	 */
	public void appendEmpty_assegnazioneCC() {
		if (assegnazioneCC != null && assegnazioneCC.size() > 0) {
			Rif previous = assegnazioneCC.get(assegnazioneCC.size()-1);
			if (!previous.isEmpty()) {
				Rif rifToAdd = new Rif(interventoDefaultCC);
				if (previous != null && previous.getTipo_uff() != null && previous.getTipo_uff().equals("ruolo"))
					rifToAdd.setTipo_uff("ruolo");
				assegnazioneCC.add(rifToAdd);
			}
		}
	}

	/**
	 * Aggiunta di un nuovo Rif int vuoto in CDS in ultima posizione
	 */
	public void appendEmpty_assegnazioneCDS() {
		if (assegnazioneCDS != null && assegnazioneCDS.size() > 0) {
			Rif previous = assegnazioneCDS.get(assegnazioneCDS.size()-1);
			if (!previous.isEmpty()) {
				Rif rifToAdd = new Rif();
				if (previous != null && previous.getTipo_uff() != null && previous.getTipo_uff().equals("ruolo"))
					rifToAdd.setTipo_uff("ruolo");
				assegnazioneCDS.add(rifToAdd);
			}
		}
	}

	/**
	 * Eliminazione di un allegato del doc
	 */
	public String deleteAllegato() {
		Allegato allegato = (Allegato) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("allegato");
		if (allegato != null) {
			allegati.remove(allegato);
			if (allegati.isEmpty())
				allegati.add(new Allegato());
		}
		return null;
	}

	/**
	 * Aggiunta di un allegato del doc
	 */
	public String addAllegato() {
		Allegato allegato = (Allegato) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("allegato");
		int index = 0;
		if (allegato != null)
			index = allegati.indexOf(allegato);

		if (allegati != null) {
			if (allegati.size() > index)
				allegati.add(index+1,  new Allegato());
			else
				allegati.add(new Allegato());
		}
		return null;
	}

	/**
	 * Spostamento in alto di un allegato del doc
	 */
	public String moveUpAllegato() {
		Allegato allegato = (Allegato) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("allegato");
		if (allegato != null && allegati != null) {
			int index = allegati.indexOf(allegato);
			if (index > 0 ) {
				allegati.remove(index);
				allegati.add(index-1, allegato);
			}
		}
		return null;
	}

	/**
	 * Spostamento in basso di un allegato del doc
	 */
	public String moveDownAllegato() {
		Allegato allegato = (Allegato) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("allegato");
		if (allegato != null && allegati != null) {
			int index = allegati.indexOf(allegato);
			if (index < allegati.size()-1 ) {
				allegati.remove(index);
				allegati.add(index+1, allegato);
			}
		}
		return null;
	}

	/**
	 * Eliminazione di un xlink del doc
	 */
	public String deleteXlink() {
		Xlink link = (Xlink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("link");
		if (link != null) {
			xlink.remove(link);
			if (xlink.isEmpty())
				xlink.add(new Xlink());
		}
		return null;
	}

	/**
	 * Aggiunta di un xlink del doc
	 */
	public String addXlink() {
		Xlink link = (Xlink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("link");
		int index = 0;
		if (link != null)
			index = xlink.indexOf(link);

		if (xlink != null) {
			if (xlink.size() > index)
				xlink.add(index+1,  new Xlink());
			else
				xlink.add(new Xlink());
		}
		return null;
	}

	/**
	 * Spostamento in alto di un xlink del doc
	 */
	public String moveUpXlink() {
		Xlink link = (Xlink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("link");
		if (link != null && xlink != null) {
			int index = xlink.indexOf(link);
			if (index > 0 ) {
				xlink.remove(index);
				xlink.add(index-1, link);
			}
		}
		return null;
	}

	/**
	 * Spostamento in basso di un xlink del doc
	 */
	public String moveDownXlink() {
		Xlink link = (Xlink) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("link");
		if (link != null && xlink != null) {
			int index = xlink.indexOf(link);
			if (index < xlink.size()-1 ) {
				xlink.remove(index);
				xlink.add(index+1, link);
			}
		}
		return null;
	}

	/**
	 * In base al tipo di scadenza specificato assegna la data di scadenza
	 * per il documento
	 */
	public String checkScadenza() {
		try {
			String nuovaDataScadenza = "";
			if (scadenza.getTipo() != null && !scadenza.getTipo().equals("")
					&& !scadenza.getTipo().equals("nessuna") && !scadenza.getTipo().equals("fissa")) {
				String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione

				String dataPartenza = DateUtil.getCurrentDateNorm();
				if (scadenza.getData_ultima_revisione() != null && scadenza.getData_ultima_revisione().length() > 0)
					dataPartenza = DateUtil.formatDate2XW(scadenza.getData_ultima_revisione(), formatoData);

				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				Date datePartenza = df.parse(dataPartenza);
				if (datePartenza != null) {
					GregorianCalendar gc = new GregorianCalendar();
					gc.setTime(datePartenza);

					if (scadenza.getTipo().equals("settimanale")) {
						gc.add(Calendar.DAY_OF_MONTH, 7);
					}
					else if (scadenza.getTipo().equals("mensile")) {
						gc.add(Calendar.MONTH, 1);
					}
					else if (scadenza.getTipo().equals("bimestrale")) {
						gc.add(Calendar.MONTH, 2);
					}
					else if (scadenza.getTipo().equals("trimestrale")) {
						gc.add(Calendar.MONTH, 3);
					}
					else if (scadenza.getTipo().equals("semestrale")) {
						gc.add(Calendar.MONTH, 6);
					}
					else if (scadenza.getTipo().equals("annuale")) {
						gc.add(Calendar.YEAR, 1);
					}

					nuovaDataScadenza = new SimpleDateFormat(formatoData).format(gc.getTime());
				}
			}

			scadenza.setData_scadenza(nuovaDataScadenza);
		}
		catch (Throwable t) {
			// TODO Gestire l'eccezione
		}

		return null;
	}

	public void setKeywords(Keywords keywords) {
		this.keywords = keywords;
	}

	public Keywords getKeywords() {
		return keywords;
	}

	public void setRiferimenti(Riferimenti riferimenti) {
		this.riferimenti = riferimenti;
	}

	public Riferimenti getRiferimenti() {
		return riferimenti;
	}

	public void setTipologia(Tipologia tipologia) {
		this.tipologia = tipologia;
	}

	public Tipologia getTipologia() {
		return tipologia;
	}

	public void setNon_disponibile(Non_disponibile non_disponibile) {
		this.non_disponibile = non_disponibile;
	}

	public Non_disponibile getNon_disponibile() {
		return non_disponibile;
	}

	public void setProt_differito(Prot_differito prot_differito) {
		this.prot_differito = prot_differito;
	}

	public Prot_differito getProt_differito() {
		return prot_differito;
	}

	public String getStato_firma() {
		return stato_firma;
	}

	public void setStato_firma(String stato_firma) {
		this.stato_firma = stato_firma;
	}

	public Extra getExtra() {
		return extra;
	}

	public void setExtra(Extra extra) {
		this.extra = extra;
	}

	public boolean isFilesPrenotati() {
		return filesPrenotati;
	}

	public void setFilesPrenotati(boolean filesPrenotati) {
		this.filesPrenotati = filesPrenotati;
	}

	public String getCorpoEmail() {
		return corpoEmail;
	}

	public void setCorpoEmail(String corpoEmail) {
		this.corpoEmail = corpoEmail;
	}

	public int getDocInformaticiSize() {
		return docInformaticiSize;
	}

	public void setDocInformaticiSize(int docInformaticiSize) {
		this.docInformaticiSize = docInformaticiSize;
	}

	public boolean isCestino() {
		return cestino;
	}

	public void setCestino(boolean cestino) {
		this.cestino = cestino;
	}

	public String getDatacestino() {
		return datacestino;
	}

	public void setDatacestino(String datacestino) {
		this.datacestino = datacestino;
	}

	public Rifiuto getRifiuto() {
		return rifiuto;
	}

	public void setRifiuto(Rifiuto rifiuto) {
		this.rifiuto = rifiuto;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getEmailAttachmentIndex() {
		return emailAttachmentIndex;
	}

	public void setEmailAttachmentIndex(String emailAttachmentIndex) {
		this.emailAttachmentIndex = emailAttachmentIndex;
	}

	/**
	 * ritorna la dimensione totale in KB di doc informatici allegati
	 * @return
	 */
	public String getDocInformaticiSizeKB() {
		if (docInformaticiSize > 0)
			return (docInformaticiSize / 1024) + " KB";
		else
			return "";
	}

	/**
	 * ritorna la dimensione totale in MB di doc informatici allegati
	 * @return
	 */
	public String getDocInformaticiSizeMB() {
		if (docInformaticiSize > 0) {
			double result = (double) docInformaticiSize / (1024 * 1024);
			return String.format("%.1f %s", Double.valueOf(result), "MB");
		} else
			return "";
	}

	/**
	 * Aggiunta di un nuovo Rif est del doc
	 */
	public void addRifEst(RifEsterno rifEsterno) {
		int index = 0;
		if (rifEsterno != null)
			index = rif_esterni.indexOf(rifEsterno);

		if (rif_esterni != null) {
			RifEsterno rifEsternoToAdd = new RifEsterno();

			if (rif_esterni.size() > index)
				rif_esterni.add(index+1, rifEsternoToAdd);
			else
				rif_esterni.add(rifEsternoToAdd);
		}
	}

	/**
	 * Eliminazione di un Rif est del doc
	 */
	public void deleteRifEst(RifEsterno rifEsterno){
		if (rifEsterno != null) {
			rif_esterni.remove(rifEsterno);
			if (rif_esterni.isEmpty())
				rif_esterni.add(new RifEsterno());
		}
	}

	/**
	 * Spostamento in alto di un Rif est del doc
	 */
	public void moveUpRifEst(RifEsterno rifEsterno){
		if (rifEsterno != null && rif_esterni != null) {
			int index = rif_esterni.indexOf(rifEsterno);
			if (index > 0 ) {
				rif_esterni.remove(index);
				rif_esterni.add(index-1, rifEsterno);
			}
		}
	}

	/**
	 * Spostamento in basso di un Rif est del doc
	 */
	public void moveDownRifEst(RifEsterno rifEsterno){
		if (rifEsterno != null && rif_esterni != null) {
			int index = rif_esterni.indexOf(rifEsterno);
			if (index < rif_esterni.size()-1 ) {
				rif_esterni.remove(index);
				rif_esterni.add(index+1, rifEsterno);
			}
		}
	}

	/**
	 * Aggiunta di un nuovo num raccomandata per il documento
	 */
	public void addNumeroRaccomandata(NumeroRaccomandata numeroRaccomandata) {
		int index = 0;
		if (numeroRaccomandata != null)
			index = numero_raccomandata.indexOf(numeroRaccomandata);

		if (numero_raccomandata != null) {
			NumeroRaccomandata numRaccomandataToAdd = new NumeroRaccomandata();

			if (numero_raccomandata.size() > index)
				numero_raccomandata.add(index+1, numRaccomandataToAdd);
			else
				numero_raccomandata.add(numRaccomandataToAdd);
		}
	}

	/**
	 * Eliminazione di un num raccomandata per il documento
	 */
	public void deleteNumeroRaccomandata(NumeroRaccomandata numeroRaccomandata){
		if (numeroRaccomandata != null) {
			numero_raccomandata.remove(numeroRaccomandata);
			if (numero_raccomandata.isEmpty())
				numero_raccomandata.add(new NumeroRaccomandata());
		}
	}

	/**
	 * Eliminazione di un xwFile di tipo file del doc
	 */
	public String deleteXwFile() {
		return deleteXwFile((XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file"));
	}

	/**
	 * Eliminazione di un xwFile di tipo file del doc
	 */
	public String deleteXwFile(String index) {
		return deleteXwFile(getXwFileByPosition(index));
    }

	/**
	 * Eliminazione di un xwFile di tipo file del doc
	 */
	public String deleteXwFile(XwFile xwFile) {
		if (xwFile != null) {
			files.remove(xwFile);
			if (files.isEmpty())
				files.add(new XwFile());
		}
		return null;
	}

	/**
	 * Eliminazione di tutti gli xwFile di tipo file del doc
	 */
	public String deleteAllXwFile() {
		files.clear();
		files.add(new XwFile());
		return null;
	}

	/**
	 * Aggiunta di un xwFile di tipo file del doc
	 */
	public String addXwFile() {
		XwFile xwFile = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file");
		int index = 0;
		if (xwFile != null)
			index = files.indexOf(xwFile);

		if (files != null) {
			if (files.size() > index)
				files.add(index+1,  new XwFile());
			else
				files.add(new XwFile());
		}
		return null;
	}

	/**
	 * dato un indice ritorna l'xwFile recuperandolo dalla lista
	 * @param index
	 * @return
	 */
	private XwFile getXwFileByPosition(String index) {
		XwFile xwfile = null;
		if (StringUtil.isNumber(index)) {
			int indice = StringUtil.intValue(index);
			if (indice >= 0 && indice < files.size())
				xwfile = files.get(indice);
		}
		return xwfile;
	}

	/**
	 * Spostamento in alto di un xwFile di tipo file del doc
	 */
	public String moveUpXwFile() {
		return moveUpXwFile((XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file"));

	}

	/**
	 * Spostamento in alto di un xwFile di tipo file del doc
	 */
	public String moveUpXwFile(String index) {
		return moveUpXwFile(getXwFileByPosition(index));
    }

	/**
	 * Spostamento in alto di un xwFile di tipo file del doc
	 */
	private String moveUpXwFile(XwFile xwFile) {
		if (xwFile != null && files != null) {
			int index = files.indexOf(xwFile);
			if (index > 0 ) {
				files.remove(index);
				files.add(index-1, xwFile);
			}
		}
		return null;
	}

	/**
	 * Spostamento in basso di un xwFile di tipo file del doc
	 */
	public String moveDownXwFile() {
		return moveDownXwFile((XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file"));
	}

	/**
	 * Spostamento in basso di un xwFile di tipo file del doc
	 */
	public String moveDownXwFile(String index) {
		return moveDownXwFile(getXwFileByPosition(index));
    }

	/**
	 * Spostamento in basso di un xwFile di tipo file del doc
	 */
	public String moveDownXwFile(XwFile xwFile) {
		if (xwFile != null && files != null) {
			int index = files.indexOf(xwFile);
			if (index < files.size()-1 ) {
				files.remove(index);
				files.add(index+1, xwFile);
			}
		}
		return null;
	}

	/**
	 * Eliminazione di un xwFile di tipo immagine del doc
	 */
	public String deleteXwImage() {
		return deleteXwImage((XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file"));
	}

	/**
	 * Eliminazione di un xwFile di tipo immagine del doc
	 */
	public String deleteXwImage(String index) {
		return deleteXwImage(getXwImageByPosition(index));
    }

	/**
	 * Eliminazione di un xwFile di tipo immagine del doc
	 */
	public String deleteXwImage(XwFile xwFile) {
		if (xwFile != null) {
			immagini.remove(xwFile);
			if (immagini.isEmpty())
				immagini.add(new XwFile());
		}
		return null;
	}

	/**
	 * Eliminazione di tutti gli xwFile di tipo immagine del doc
	 */
	public String deleteAllXwImage() {
		immagini.clear();
		immagini.add(new XwFile());
		return null;
	}

	/**
	 * Aggiunta di un xwFile di tipo immagine del doc
	 */
	public String addXwImage() {
		XwFile xwFile = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file");
		int index = 0;
		if (xwFile != null)
			index = immagini.indexOf(xwFile);

		if (immagini != null) {
			if (immagini.size() > index)
				immagini.add(index+1,  new XwFile());
			else
				immagini.add(new XwFile());
		}
		return null;
	}

	/**
	 * dato un indice (raltivo ad una immagine) ritorna l'xwFile recuperandolo dalla lista
	 * @param index
	 * @return
	 */
	private XwFile getXwImageByPosition(String index) {
		XwFile xwfile = null;
		if (StringUtil.isNumber(index)) {
			int indice = StringUtil.intValue(index);
			if (indice >= 0 && indice < immagini.size())
				xwfile = immagini.get(indice);
		}
		return xwfile;
	}

	/**
	 * Spostamento in alto di un xwFile di tipo immagine del doc
	 */
	public String moveUpXwImage() {
		return moveUpXwImage((XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file"));
	}

	/**
	 * Spostamento in alto di un xwFile di tipo immagine del doc
	 */
	public String moveUpXwImage(String index) {
		return moveUpXwImage(getXwImageByPosition(index));
    }

	/**
	 * Spostamento in alto di un xwFile di tipo immagine del doc
	 */
	public String moveUpXwImage(XwFile xwFile) {
		if (xwFile != null && immagini != null) {
			int index = immagini.indexOf(xwFile);
			if (index > 0 ) {
				immagini.remove(index);
				immagini.add(index-1, xwFile);
			}
		}
		return null;
	}

	/**
	 * Spostamento in basso di un xwFile di tipo immagine del doc
	 */
	public String moveDownXwImage() {
		return moveDownXwImage((XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file"));
	}

	/**
	 * Spostamento in basso di un xwFile di tipo immagine del doc
	 */
	public String moveDownXwImage(String index) {
		return moveDownXwImage(getXwImageByPosition(index));
    }

	/**
	 * Spostamento in basso di un xwFile di tipo immagine del doc
	 */
	public String moveDownXwImage(XwFile xwFile) {
		if (xwFile != null && immagini != null) {
			int index = immagini.indexOf(xwFile);
			if (index < immagini.size()-1 ) {
				immagini.remove(index);
				immagini.add(index+1, xwFile);
			}
		}
		return null;
	}

	/**
	 * Aggiunta di files al documento (dopo upload e caricamento
	 * su xway)
	 *
	 * @return
	 */
	public String addFiles() {
		if (xwFileIdsAttached != null && xwFileNamesAttached != null) {
			String[] fileIds = StringUtil.split(this.xwFileIdsAttached, "|");
			String[] fileNames = StringUtil.split(this.xwFileNamesAttached, "|");
			String[] fileTitles = StringUtil.split(this.xwFileTitlesAttached, "|");
			if (fileIds != null && fileIds.length > 0
					&& fileNames != null && fileNames.length > 0
					&& fileIds.length == fileNames.length
					&& fileTitles != null && fileTitles.length > 0
					&& fileIds.length == fileTitles.length) {

				// se l'ultima istanza e' vuota deve essere rimossa (solo
				// per scopo grafico di usabilita' per l'utente)
				if (files.get(files.size()-1).getXwayId().equals("") && files.get(files.size()-1).getName().equals(""))
					files.remove(files.size()-1);

				// per ogni file caricato viene istanziato e agganciato l'xwFile
				for (int i=0; i<fileIds.length; i++) {
					String id = fileIds[i];
					String name = fileNames[i];
					String title = fileTitles[i];
					if (id != null && !id.equals("")
							&& name != null && !name.equals("")
							&& title != null && !title.equals("")) {
						XwFile xwFile = new XwFile(id, name, title);

						if (files != null && files.size() == 1 && files.get(0) != null
								&& (files.get(0).getName() == null || files.get(0).getName().equals("")))
							files.remove(0);
						files.add(xwFile);
					}
				}

				files.add(new XwFile()); // aggiunta di una istanza vuota per migliorare usabilita' utente
			}
		}

		// azzeramento dei valori relativi a files caricati
		setXwFileIdsAttached("");
		setXwFileNamesAttached("");
		setXwFileTitlesAttached("");

		return null;
	}

	/**
	 * Aggiunta di immagini al documento (dopo upload e caricamento
	 * su xway)
	 *
	 * @return
	 */
	public String addImages() {
		if (xwImageIdsAttached != null && xwImageNamesAttached != null) {
			String[] imageIds = StringUtil.split(this.xwImageIdsAttached, "|");
			String[] imageNames = StringUtil.split(this.xwImageNamesAttached, "|");
			String[] imageTitles = StringUtil.split(this.xwImageTitlesAttached, "|");
			if (imageIds != null && imageIds.length > 0
					&& imageNames != null && imageNames.length > 0
					&& imageIds.length == imageNames.length
					&& imageTitles != null && imageTitles.length > 0
					&& imageIds.length == imageTitles.length) {

				// se l'ultima istanza e' vuota deve essere rimossa (solo
				// per scopo grafico di usabilita' per l'utente)
				if (immagini.get(immagini.size()-1).getXwayId().equals("") && immagini.get(immagini.size()-1).getName().equals(""))
					immagini.remove(immagini.size()-1);

				// per ogni immagine caricata viene istanziato e agganciato l'xwFile
				for (int i=0; i<imageIds.length; i++) {
					String id = imageIds[i];
					String name = imageNames[i];
					String title = imageTitles[i];
					if (id != null && !id.equals("")
							&& name != null && !name.equals("")
							&& title != null && !title.equals("")) {
						XwFile xwFile = new XwFile(id, name, title);

						if (immagini != null && immagini.size() == 1 && immagini.get(0) != null
								&& (immagini.get(0).getName() == null || immagini.get(0).getName().equals("")))
							immagini.remove(0);
						immagini.add(xwFile);
					}
				}

				immagini.add(new XwFile()); // aggiunta di una istanza vuota per migliorare usabilita' utente
			}
		}

		// azzeramento dei valori relativi a immagini caricate
		setXwImageIdsAttached("");
		setXwImageNamesAttached("");
		setXwImageTitlesAttached("");

		return null;
	}

	/**
	 * Ritorna true se il documento contiene almeno un file o un immagine allegato, false altrimenti
	 */
	public boolean containsFiles() {
		if ((files != null && files.size() > 0 && files.get(0) != null && files.get(0).getName() != null && files.get(0).getName().length() > 0)
				|| (immagini != null && immagini.size() > 0 && immagini.get(0) != null && immagini.get(0).getName() != null && immagini.get(0).getName().length() > 0))
			return true;
		else
			return false;
	}

	/**
	 * ritorna il tipo del documento in versione short (iniziale maiuscola)
	 * @return
	 */
	public String getTipoShort() {
		if (tipo != null) {
			if (tipo.equals("arrivo")) return "A" ;
	        else if (tipo.equals("interno")) return "I" ;
	        else if (tipo.equals("partenza")) return "P" ;
	        else if (tipo.equals("varie")) return "V" ;
		}

		return tipo;
	}

	public String getNrecord_Sed() {
		return nrecord_Sed;
	}

	public void setNrecord_Sed(String nrecord_Sed) {
		this.nrecord_Sed = nrecord_Sed;
	}

	public String getVerbale_di_seduta() {
		return verbale_di_seduta;
	}

	public void setVerbale_di_seduta(String verbale_di_seduta) {
		this.verbale_di_seduta = verbale_di_seduta;
	}

	public Proposta getProposta() {
		return proposta;
	}

	public void setProposta(Proposta proposta) {
		this.proposta = proposta;
	}

	public Odg_seduta getOdg_seduta() {
		return odg_seduta;
	}

	public void setOdg_seduta(Odg_seduta odg_seduta) {
		this.odg_seduta = odg_seduta;
	}

	public String getCurrDate() {
		return currDate;
	}

	public void setCurrDate(String currDate) {
		this.currDate = currDate;
	}

	/**
	 * Verifica se il documento corrente contiene o meno immagini che devono essere convertite (tramite
	 * FCA/FCS) e successivamente eliminate
	 * @return true nel caso in cui il documento contenga immagini da convertire ed eliminare, false altrimenti
	 */
	public boolean hasImagesToConvertAndRemove() {
		boolean imageToRemove = false;
		if (immagini != null && !immagini.isEmpty()) {
			// Cerco se almeno una delle immagini contenute nel documento e' marcata come 'da cancellare' (post conversione
			// tramite FCS)
			int i = 0;
			while (!imageToRemove && i < immagini.size()) {
				XwFile image = immagini.get(i);
				if (image != null && image.isAgent_delete())
					imageToRemove = true;
				i++;
			}
		}
		return imageToRemove;
	}

	public boolean isRichiestaPresaInCarico() {
		return richiestaPresaInCarico;
	}

	public void setRichiestaPresaInCarico(boolean richiestaPresaInCarico) {
		this.richiestaPresaInCarico = richiestaPresaInCarico;
	}

	public boolean isEffettuataPresaInCarico() {
		return effettuataPresaInCarico;
	}

	public void setEffettuataPresaInCarico(boolean effettuataPresaInCarico) {
		this.effettuataPresaInCarico = effettuataPresaInCarico;
	}

	public List<RifPresaInCarico> getIncaricatiPresaInCarico() {
		return incaricatiPresaInCarico;
	}

	public void setIncaricatiPresaInCarico(List<RifPresaInCarico> incaricatiPresaInCarico) {
		this.incaricatiPresaInCarico = incaricatiPresaInCarico;
	}

	public List<RifPresaInCarico> getRifPresaInCarico() {
		return rifPresaInCarico;
	}

	public void setRifPresaInCarico(List<RifPresaInCarico> rifPresaInCarico) {
		this.rifPresaInCarico = rifPresaInCarico;
	}

	// metodo per capire se il documento è in inserimento
	public boolean isNew() {
		return nrecord == null || nrecord.isEmpty() || nrecord.equals(".");
	}

	public boolean isProtocollabile() {
		return protocollabile;
	}

	public void setProtocollabile(boolean protocollabile) {
		this.protocollabile = protocollabile;
	}

	public boolean isRepertoriabile() {
		return repertoriabile;
	}

	public void setRepertoriabile(boolean repertoriabile) {
		this.repertoriabile = repertoriabile;
	}
	
	public TrasformazioneRep getTrasformazioneRep() {
		return trasformazioneRep;
	}

	public void setTrasformazioneRep(TrasformazioneRep trasformazioneRep) {
		this.trasformazioneRep = trasformazioneRep;
	}
	
	public boolean isBozzaArrivoFromPEC() {
		return bozzaArrivoFromPEC;
	}

	/**
	 * Ritorna true se il documento corrente può essere convertito in repertorio tramite pagina di docEdit, false
	 * altrimenti
	 * @return
	 */
	public boolean isTrasformabileInRepertorio() {
		return (getTrasformazioneRep() == null || !getTrasformazioneRep().isTrasformato()) 
					&& (
							isRepertoriabile() || 
							getRepertorio() == null || 
							getRepertorio().getCod() == null || 
							getRepertorio().getCod().isEmpty()
						);
	}
	
	/**
	 * Ritorna true se il documento corrente puo' essere ripristinato dal repertorio (post trasformazioneByDocEdit),
	 * false altrimenti
	 * @return
	 */
	public boolean isRipristinabileDaRepertorio() {
		return (getTrasformazioneRep() != null && getTrasformazioneRep().isTrasformato() && isRepertoriabile());
	}
	
	public VerificaVirus getVerificaVirus() {
		return verificaVirus;
	}

	public void setVerificaVirus(VerificaVirus verificaVirus) {
		this.verificaVirus = verificaVirus;
	}
}
