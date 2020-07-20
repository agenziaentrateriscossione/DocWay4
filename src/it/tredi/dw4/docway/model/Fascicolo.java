package it.tredi.dw4.docway.model;

import it.tredi.dw4.acl.model.Chiusura;
import it.tredi.dw4.acl.model.Creazione;
import it.tredi.dw4.acl.model.Note;
import it.tredi.dw4.acl.model.UltimaModifica;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class Fascicolo extends XmlEntity {
	private String nrecord;
	private String stato;
	private String numero;
	private String anno;
	private String cod_amm_aoo;
	private String oggetto;
	private String soggetto;
	private String scarto;
	private Nuovo_repertorio nuovo_repertorio = new Nuovo_repertorio();
	private Repertorio_precedente repertorio_precedente = new Repertorio_precedente();
	private Voce_indice voce_indice = new Voce_indice();
	private Doc_contenuti doc_contenuti = new Doc_contenuti();
	private Classif classif = new Classif();
	private Classif classifNV = new Classif(); // Utilizzata per la compilazione manuale della classificazione
	private List<Contenuto_in> contenuto_in;
	private List<GerarchiaFascicolo> gerarchiaFascicolo;
	private List<Rif> rif_interni;
	private List<Link_interno> link_interni;
	private boolean sendMailRifInterni = true; // Indica se inviare o meno la mail di avviso ai rif interni
	private boolean checkNomi = true; // controllo nomi in caso di trasferimento fascicolo
	
	private boolean archiviato = false; // indica se il fascicolo e' stato archiviato o meno
	private boolean archiviato_before_saving = false;
	
	private String codiceFascicoloCustom = "";
	private String descrizioneFascicoloCustom = "";
	
	private String motiv_ogg_div = "";
	
	// Contengono i Rif contenuti in rif_interni, ma suddivisi per diritto (rpa, itf, cc). Utilizzati
	// in fase di inserimento/modifica del fascicolo
	private Rif assegnazioneRPA = new Rif();
	private Rif assegnazioneITF = new Rif();
	private List<Rif> assegnazioneCC = new ArrayList<Rif>();
	
	// gestione CC in showdoc
	private List<Rif> cc_list = new ArrayList<Rif>();
	private List<Rif> cc_fasc_list = new ArrayList<Rif>();
	private HashMap<String, List<Rif>> cc_ufficio = new HashMap<String, List<Rif>>();
	private HashMap<String, List<Rif>> cc_fasc_ufficio = new HashMap<String, List<Rif>>();
	
	private Note note = new Note();
	private Creazione creazione = new Creazione();
	private Chiusura chiusura = new Chiusura();
	private Protocollazione protocollazione = new Protocollazione(); // non necessario su questo oggetto, ma richiesto perche' template history utilizzato con oggetti di model differenti
	private UltimaModifica ultima_modifica = new UltimaModifica();
	private List<Storia> storia;
	private List<History> history;
	
	// campi specifici dei fascicoli speciali
	private String num_pos = "";
	private Fascicolo_speciale fascicolo_speciale = new Fascicolo_speciale();
	private Nominativo nominativo = new Nominativo();
	
	// raggruppamento di cc
	private int countCcTotali;
	private int countCcFascicoloPadre;
	private int countCcTotaliPersonali;
	
	private Extra extra = new Extra(); // gestione dei campi extra interni al fascicolo
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.nrecord = 	 XMLUtil.parseAttribute(dom, "fascicolo/@nrecord", ".");
		this.stato = 	 XMLUtil.parseAttribute(dom, "fascicolo/@stato", "aperto"); // TODO Da verificare
		
		this.numero = 	 XMLUtil.parseAttribute(dom, "fascicolo/@numero", ".");
		String fascNumeroSottofasc = XMLUtil.parseAttribute(dom, "response/@fasc_numero_sottofasc");
		if (fascNumeroSottofasc != null && fascNumeroSottofasc.length() > 0 )
			this.numero = fascNumeroSottofasc;
		
		this.anno = 	 XMLUtil.parseAttribute(dom, "fascicolo/@anno");
		this.scarto = 	 XMLUtil.parseAttribute(dom, "fascicolo/@scarto");
		this.cod_amm_aoo = XMLUtil.parseAttribute(dom, "fascicolo/@cod_amm_aoo");
		this.nuovo_repertorio.init(XMLUtil.createDocument(dom, "//fascicolo/nuovo_repertorio"));
		this.repertorio_precedente.init(XMLUtil.createDocument(dom, "//fascicolo/repertorio_precedente"));
		this.voce_indice.init(XMLUtil.createDocument(dom, "//fascicolo/voce_indice"));
		this.oggetto = 	 XMLUtil.parseElement(dom, "fascicolo/oggetto");
		this.soggetto = 	 XMLUtil.parseElement(dom, "fascicolo/soggetto");
		
		this.classif.init(XMLUtil.createDocument(dom, "//fascicolo/classif"));
		this.contenuto_in = XMLUtil.parseSetOfElement(dom, "//fascicolo/rif_contenuto_in/contenuto_in", new Contenuto_in());
		this.doc_contenuti.init(XMLUtil.createDocument(dom, "//fascicolo/doc_contenuti"));
		this.rif_interni = XMLUtil.parseSetOfElement(dom, "//fascicolo/rif_interni/rif", new Rif());
		this.note.init(XMLUtil.createDocument(dom, "//fascicolo/note"));
		this.creazione.init(XMLUtil.createDocument(dom, "//fascicolo/storia/creazione"));
		this.chiusura.init(XMLUtil.createDocument(dom, "//fascicolo/storia/chiusura"));
		this.ultima_modifica.init(XMLUtil.createDocument(dom, "//fascicolo/storia/ultima_modifica"));
		this.storia = XMLUtil.parseSetOfElement(dom, "//fascicolo/storia/node()", new Storia());
		this.gerarchiaFascicolo = XMLUtil.parseSetOfElement(dom, "//fascicolo/gerarchiaFascicolo", new GerarchiaFascicolo());
		this.link_interni = XMLUtil.parseSetOfElement(dom, "//fascicolo/link_interno", new Link_interno());
		
		this.motiv_ogg_div = XMLUtil.parseElement(dom, "fascicolo/motivazione_oggetti_diversi");
		
		this.codiceFascicoloCustom = XMLUtil.parseAttribute(dom, "fascicolo/tipologia/@cod");
		this.descrizioneFascicoloCustom = XMLUtil.parseElement(dom, "fascicolo/tipologia");
		
		String strArchiviato = XMLUtil.parseAttribute(dom, "fascicolo/@archiviato");			
		if (strArchiviato != null && strArchiviato.equals("si"))
			this.archiviato = true;
		else
			this.archiviato = false;
		this.archiviato_before_saving = this.archiviato; 
		
		// Suddivido i rif_interni in base al diritto (rpa, itf, cc)
		if (this.rif_interni != null && this.rif_interni.size() > 0) {
			for (int i=0; i<this.rif_interni.size(); i++) {
				Rif tmpRif = (Rif) this.rif_interni.get(i);
				if (tmpRif != null) {
					if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_RPA)) 
						this.setAssegnazioneRPA(tmpRif);
					else if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_ITF))
						this.setAssegnazioneITF(tmpRif);
					else
						this.assegnazioneCC.add(tmpRif); // diritto = cc
				}
			}
		}
		
		// Nel caso in cui la lista di CC sia piena procedo con la suddivisione
		// dei cc fra cc specifici del fascicolo e quelli ereditati dal fascicolo padre e raggruppamento
		// dei cc in base all'ufficio di appartenenza
		if (this.assegnazioneCC.size() > 0) {
			for (int i=0; i<this.assegnazioneCC.size(); i++) {
				Rif tmpRif = (Rif) assegnazioneCC.get(i);
				if (tmpRif != null && tmpRif.getDiritto() != null && !tmpRif.getDiritto().equals("")) {
					if (tmpRif.getCod_fasc() == null || tmpRif.getCod_fasc().trim().length() == 0) { // cc specifici del documento
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
					else if (tmpRif.getCod_fasc() != null && tmpRif.getCod_fasc().trim().length() > 0) { // cc ereditati dal fascicolo
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
		
		// campi specifici dei fascicoli speciali
		num_pos = XMLUtil.parseAttribute(dom, "fascicolo/@num_pos", "");
		fascicolo_speciale.init(XMLUtil.createDocument(dom, "//fascicolo/fascicolo_speciale"));
		nominativo.init(XMLUtil.createDocument(dom, "//fascicolo/nominativo"));
		
		this.countCcTotali = dom.selectNodes("//fascicolo/rif_interni/rif[@diritto='CC'][not(@cod_fasc) or @cod_fasc='']").size();
		this.countCcFascicoloPadre = dom.selectNodes("//fascicolo/rif_interni/rif[@diritto='CC'][@cod_fasc!='']").size();
		this.countCcTotaliPersonali = dom.selectNodes("//fascicolo/rif_interni/rif[@diritto='CC' and @personale='true'][not(@cod_fasc) or @cod_fasc='']").size();
		
		extra.init(XMLUtil.createDocument(dom, "//fascicolo/extra")); // gestione dei campi extra
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public void initHistory(Document dom){
		this.history = XMLUtil.parseSetOfElement(dom, "//item", new History());
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@nrecord", nrecord);
    	
    	if (nrecord.equals(".")) { // parametri da inviare solo in caso di inserimento
	    	params.put(prefix+".@stato", stato);
	    	params.put(prefix+".@numero", numero);
	    	params.put(prefix+".@anno", anno);
	    	params.put(prefix+".@cod_amm_aoo", cod_amm_aoo);
	    	//params.put(prefix+".@data_chiusura", data_chiusura);
	    	params.putAll(nuovo_repertorio.asFormAdapterParams(".nuovo_repertorio"));
	    	params.putAll(repertorio_precedente.asFormAdapterParams(".repertorio_precedente"));
	    	params.putAll(voce_indice.asFormAdapterParams(".voce_indice"));
	    	params.putAll(classif.asFormAdapterParams(".classif"));
	    	params.putAll(classifNV.asFormAdapterParams(".classif_nv"));
	    	params.putAll(doc_contenuti.asFormAdapterParams(".doc_contenuti"));
    	}
    	
    	params.put(prefix+".oggetto", oggetto);
    	params.put(prefix+".soggetto", this.soggetto);
    	params.put(prefix+".@scarto", scarto);
    	params.putAll(note.asFormAdapterParams(".note"));
    	
    	params.put("motiv_ogg_div", this.motiv_ogg_div);
    	
    	//20141218 fcappelli - gestione fascicoli custom
    	if (codiceFascicoloCustom != null && !codiceFascicoloCustom.equals("")) {
	    	params.put(prefix+".tipologia", descrizioneFascicoloCustom);
	    	params.put(prefix+".tipologia.@cod", codiceFascicoloCustom);
    	}
    	
    	// archiviazione del fascicolo (modifica)
    	if (!nrecord.equals(".")) { // parametri da inviare solo in caso di modifica
	    	if (this.archiviato)
	    		params.put(prefix+".@archiviato", "si");
	    	else
	    		params.put(prefix+".@archiviato", "no");
	    	if (this.archiviato_before_saving)
	    		params.put("__ARCHIVIATO_BEFORE_SAVING__", "si");
	    	else
	    		params.put("__ARCHIVIATO_BEFORE_SAVING__", "no");
    	}
    	
    	// Imposto i parametri di formAdapter in base alla tipologia di rif int
		
		if (assegnazioneRPA != null && nrecord.equals(".")) { // solo inserimento
			params.putAll(assegnazioneRPA.asFormAdapterParams("*rif_interni.assegnazioneRPA"));
		}
		if (assegnazioneITF != null) {
			params.putAll(assegnazioneITF.asFormAdapterParams("*rif_interni.assegnazioneITF"));
		}
		if (assegnazioneCC != null && assegnazioneCC.size() > 0 && nrecord.equals(".")) { // solo inserimento
			for (int i=0; i<assegnazioneCC.size(); i++) {
				Rif rif = (Rif) assegnazioneCC.get(i);
				params.putAll(rif.asFormAdapterParams("*rif_interni.assegnazioneCC["+String.valueOf(i)+"]"));
			}
		}
		
		// campi specifici dei fascicoli speciali
		if (num_pos != null && num_pos.length() > 0)
			params.put(prefix+".@num_pos", num_pos);
		params.putAll(fascicolo_speciale.asFormAdapterParams(prefix+".fascicolo_speciale"));
		params.putAll(nominativo.asFormAdapterParams(prefix+".nominativo"));
		
		params.putAll(extra.asFormAdapterParams(prefix+".extra")); // gestione dei campi extra
		
		// Imposto il parametro relativo all'invio della mail di notifica ai rif int
		params.put("*sendMailRifInterni", sendMailRifInterni+"");
    	
    	return params;
	}

	public Map<String, String> getAssegnazioneRPAParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (assegnazioneRPA != null) {
			params.putAll(assegnazioneRPA.asFormAdapterParams("*rif_interni.assegnazioneRPA"));
		}
		return params;
	}
	
	/**
	 * Imposta i parametri del formAdapter necessari ad un'operazione di trasferimento
	 * responsabilita' su fascicoli (lista titoli fascicoli)
	 */
	public Map<String, String> getAssegnazioneTRASFParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (assegnazioneRPA != null) {
			params.putAll(assegnazioneRPA.asFormAdapterParams("*rif_interni.assegnazioneTRASF"));
		}
		return params;
	}
	
	public Map<String, String> getAssegnazioneITFParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (assegnazioneITF != null) {
			params.putAll(assegnazioneITF.asFormAdapterParams("*rif_interni.assegnazioneITF"));
		}
		return params;
	}
	
	public Map<String, String> getAssegnazioneCCParam(){
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (assegnazioneCC != null && assegnazioneCC.size() > 0) {
			for (int i=0; i<assegnazioneCC.size(); i++) {
				Rif rif = (Rif) assegnazioneCC.get(i);
				params.putAll(rif.asFormAdapterParams("*rif_interni.assegnazioneCC["+String.valueOf(i)+"]"));
			}
		}
		return params;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNumero() {
		return numero;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getStato() {
		return stato;
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

	public void setRif_interni(List<Rif> rif_interni) {
		this.rif_interni = rif_interni;
	}

	public List<Rif> getRif_interni() {
		return rif_interni;
	}

	public Rif getAssegnazioneRPA() {
		return assegnazioneRPA;
	}

	public void setAssegnazioneRPA(Rif assegnazioneRPA) {
		this.assegnazioneRPA = assegnazioneRPA;
	}

	public Rif getAssegnazioneITF() {
		return assegnazioneITF;
	}

	public void setAssegnazioneITF(Rif assegnazioneITF) {
		this.assegnazioneITF = assegnazioneITF;
	}

	public List<Rif> getAssegnazioneCC() {
		return assegnazioneCC;
	}

	public void setAssegnazioneCC(List<Rif> assegnazioneCC) {
		this.assegnazioneCC = assegnazioneCC;
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

	public HashMap<String, List<Rif>> getCc_fasc_ufficio() {
		return cc_fasc_ufficio;
	}

	public void setCc_fasc_ufficio(HashMap<String, List<Rif>> cc_fasc_ufficio) {
		this.cc_fasc_ufficio = cc_fasc_ufficio;
	}
	
	public HashMap<String, List<Rif>> getCc_ufficio() {
		return cc_ufficio;
	}

	public void setCc_ufficio(HashMap<String, List<Rif>> cc_ufficio) {
		this.cc_ufficio = cc_ufficio;
	}

	public void setDoc_contenuti(Doc_contenuti doc_contenuti) {
		this.doc_contenuti = doc_contenuti;
	}

	public Doc_contenuti getDoc_contenuti() {
		return doc_contenuti;
	}

	public void setSoggetto(String soggetto) {
		this.soggetto = soggetto;
	}

	public String getSoggetto() {
		return soggetto;
	}

	public void setScarto(String scarto) {
		this.scarto = scarto;
	}

	public String getScarto() {
		return scarto;
	}

	public void setNuovo_repertorio(Nuovo_repertorio nuovo_repertorio) {
		this.nuovo_repertorio = nuovo_repertorio;
	}

	public Nuovo_repertorio getNuovo_repertorio() {
		return nuovo_repertorio;
	}

	public void setRepertorio_precedente(Repertorio_precedente repertorio_precedente) {
		this.repertorio_precedente = repertorio_precedente;
	}

	public Repertorio_precedente getRepertorio_precedente() {
		return repertorio_precedente;
	}
	
	public void setVoce_indice(Voce_indice voce_indice) {
		this.voce_indice = voce_indice;
	}

	public Voce_indice getVoce_indice() {
		return voce_indice;
	}

	public void setContenuto_in(List<Contenuto_in> contenuto_in) {
		this.contenuto_in = contenuto_in;
	}

	public List<Contenuto_in> getContenuto_in() {
		return contenuto_in;
	}

	public void setStoria(List<Storia> storia) {
		this.storia = storia;
	}

	public List<Storia> getStoria() {
		return storia;
	}

	public void setCreazione(Creazione creazione) {
		this.creazione = creazione;
	}

	public Creazione getCreazione() {
		return creazione;
	}
	
	public Chiusura getChiusura() {
		return chiusura;
	}

	public void setChiusura(Chiusura chiusura) {
		this.chiusura = chiusura;
	}
	
	public void setProtocollazione(Protocollazione protocollazione) {
		this.protocollazione = protocollazione;
	}

	public Protocollazione getProtocollazione() {
		return protocollazione;
	}
	
	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public void setHistory(List<History> history) {
		this.history = history;
	}

	public List<History> getHistory() {
		return history;
	}
	
	public String getNum_pos() {
		return num_pos;
	}

	public void setNum_pos(String num_pos) {
		this.num_pos = num_pos;
	}

	public Fascicolo_speciale getFascicolo_speciale() {
		return fascicolo_speciale;
	}

	public void setFascicolo_speciale(Fascicolo_speciale fascicolo_speciale) {
		this.fascicolo_speciale = fascicolo_speciale;
	}

	public Nominativo getNominativo() {
		return nominativo;
	}

	public void setNominativo(Nominativo nominativo) {
		this.nominativo = nominativo;
	}
	
	public Extra getExtra() {
		return extra;
	}

	public void setExtra(Extra extra) {
		this.extra = extra;
	}

	public void setUltima_modifica(UltimaModifica ultima_modifica) {
		this.ultima_modifica = ultima_modifica;
	}

	public UltimaModifica getUltima_modifica() {
		return ultima_modifica;
	}

	public boolean isSendMailRifInterni() {
		return sendMailRifInterni;
	}
	
	public void setSendMailRifInterni(boolean sendMail) {
		this.sendMailRifInterni = sendMail;
	}
	
	public boolean isCheckNomi() {
		return checkNomi;
	}

	public void setCheckNomi(boolean checkNomi) {
		this.checkNomi = checkNomi;
	}
	
	public boolean isArchiviato() {
		return archiviato;
	}

	public void setArchiviato(boolean archiviato) {
		this.archiviato = archiviato;
	}
	
	public boolean isArchiviato_before_saving() {
		return archiviato_before_saving;
	}

	public void setArchiviato_before_saving(boolean archiviato_before_saving) {
		this.archiviato_before_saving = archiviato_before_saving;
	}
	
	/**
	 * Aggiunta di un nuovo Rif int in CC
	 */
	public void addRifintCC(Rif rif) {
		int index = 0;
		if (rif != null)
			index = assegnazioneCC.indexOf(rif);
		
		if (assegnazioneCC != null) {
			Rif rifToAdd = new Rif();
			if (rif != null && rif.getTipo_uff() != null && rif.getTipo_uff().equals("ruolo"))
				rifToAdd.setTipo_uff("ruolo");
			
			if (assegnazioneCC.size() > index)
				assegnazioneCC.add(index+1, rifToAdd);
			else
				assegnazioneCC.add(rifToAdd);
		}
	}
	
	/**
	 * Eliminazione di un Rif int in CC
	 */
	public void deleteRifintCC(Rif rif){
		if (rif != null) {
			assegnazioneCC.remove(rif);
			if (assegnazioneCC.isEmpty()) 
				assegnazioneCC.add(new Rif());
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
	 * Aggiunta di un nuovo Rif int vuoto in CC in ultima posizione
	 */
	public void appendEmpty_assegnazioneCC() {
		if (assegnazioneCC != null && assegnazioneCC.size() > 0) {
			Rif previous = assegnazioneCC.get(assegnazioneCC.size()-1);
			if (!previous.isEmpty()) {
				Rif rifToAdd = new Rif();
				if (previous != null && previous.getTipo_uff() != null && previous.getTipo_uff().equals("ruolo"))
					rifToAdd.setTipo_uff("ruolo");
				assegnazioneCC.add(rifToAdd);
			}
		}
	}

	public void setGerarchiaFascicolo(List<GerarchiaFascicolo> gerarchiaFascicolo) {
		this.gerarchiaFascicolo = gerarchiaFascicolo;
	}

	public List<GerarchiaFascicolo> getGerarchiaFascicolo() {
		return gerarchiaFascicolo;
	}

	public void setLink_interni(List<Link_interno> link_interni) {
		this.link_interni = link_interni;
	}

	public List<Link_interno> getLink_interni() {
		return link_interni;
	}
	
	public void setCountCcTotali(int countCcTotali) {
		this.countCcTotali = countCcTotali;
	}

	public int getCountCcTotali() {
		return countCcTotali;
	}

	public void setCountCcTotaliPersonali(int countCcTotaliPersonali) {
		this.countCcTotaliPersonali = countCcTotaliPersonali;
	}

	public int getCountCcTotaliPersonali() {
		return countCcTotaliPersonali;
	}
	
	public int getCountCcFascicoloPadre() {
		return countCcFascicoloPadre;
	}

	public void setCountCcFascicoloPadre(int countCcFascicoloPadre) {
		this.countCcFascicoloPadre = countCcFascicoloPadre;
	}

	public String getCodiceFascicoloCustom() {
		return codiceFascicoloCustom;
	}

	public void setCodiceFascicoloCustom(String codiceFascicoloCustom) {
		this.codiceFascicoloCustom = codiceFascicoloCustom;
	}

	public String getDescrizioneFascicoloCustom() {
		return descrizioneFascicoloCustom;
	}

	public void setDescrizioneFascicoloCustom(String descrizioneFascicoloCustom) {
		this.descrizioneFascicoloCustom = descrizioneFascicoloCustom;
	}
	
}
