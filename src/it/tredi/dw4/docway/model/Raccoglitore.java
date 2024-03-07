package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.acl.model.Creazione;
import it.tredi.dw4.acl.model.Note;
import it.tredi.dw4.acl.model.UltimaModifica;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

public class Raccoglitore extends XmlEntity {
	protected String nrecord;
	protected String oggetto;
	protected String stato;
	protected String anno;
	protected String cod_amm_aoo;
	protected String data_chiusura;
	private List<Contenuto_in> contenuto_in;
	protected boolean pubblico = false;
	protected Note note = new Note();

	protected Keywords keywords = new Keywords();
	protected Riferimenti riferimenti = new Riferimenti();
	protected List<Link_interno> link_interni;
	protected List<Xlink> xlink;
	protected List<Oggetto> rif_contenuto;
	protected Creazione creazione = new Creazione();
	protected Protocollazione protocollazione = new Protocollazione(); // non necessario su questo oggetto, ma richiesto perche' template history utilizzato con oggetti di model differenti
	protected Annullamento annullamento = new Annullamento();
	protected UltimaModifica ultima_modifica = new UltimaModifica();
	protected List<Storia> storia;
	protected List<History> history;
	protected List<Postit> postit;
	//gestione dell'assegnazione/visibilità del documento
	protected Rif assegnazioneRPA = new Rif();
	protected Rif assegnazioneRESO = new Rif();
	private List<Rif> assegnazioneCC = new ArrayList<Rif>();

	private boolean sendMailRifInterni = true; // Indica se inviare o meno la mail di avviso ai rif interni
	// rtirabassi - 20190911 - ERM012596 - Abilita l'invio capillare
	protected boolean sendMailSelectedRifInterni = false;
	private boolean checkNomi = true; // controllo nomi in caso di trasferimento raccoglitore

	private int numeroDocContenuti = 0;

	// gestione CC in showdoc
	private List<Rif> cc_list = new ArrayList<Rif>();
	private HashMap<String, List<Rif>> cc_ufficio = new HashMap<String, List<Rif>>();

	// mbernardini 30/09/2016 : gestione dei raccoglitori custom
	private String codiceRaccoglitoreCustom = "";
	private String descrizioneRaccoglitoreCustom = "";

	protected Non_disponibile non_disponibile = new Non_disponibile();
	
	// tiommi 19/04/2018 : controllo se deve essere inserito il diritto di intervento di default ai cc
    private boolean interventoDefaultCC = false;

    // tiommi 18/01/2019 : salvataggio del vecchio valore del numero di registro nel duplicaRicerca
	private String oldNumeroRegistro = "";

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		String xpath = "/response/raccoglitore";
		this.nrecord = 	 		XMLUtil.parseStrictAttribute(dom, xpath+"/@nrecord", ".");
		this.anno = 	 		XMLUtil.parseStrictAttribute(dom, xpath+"/@anno");
		this.cod_amm_aoo = 		XMLUtil.parseStrictAttribute(dom, xpath+"/@cod_amm_aoo");
		this.data_chiusura = 	XMLUtil.parseStrictAttribute(dom, xpath+"/@data_chiusura");
		this.stato =	 		XMLUtil.parseStrictAttribute(dom, xpath+"/@stato", "aperto");
		this.oggetto = 	 		XMLUtil.parseElement(dom, "raccoglitore/oggetto");
		String pubblic =		XMLUtil.parseStrictAttribute(dom, xpath+"/@pubblico");
		if (pubblic != null && pubblic.equals("si"))
			this.pubblico = true;
		else
			this.pubblico = false;
		this.contenuto_in = XMLUtil.parseSetOfElement(dom, xpath+"/rif_contenuto_in/contenuto_in", new Contenuto_in());
		this.note.init(XMLUtil.createDocument(dom, xpath+"/note"));
		this.non_disponibile.init(XMLUtil.createDocument(dom, "/response/non_disponibile"));
		this.riferimenti.init(XMLUtil.createDocument(dom, xpath+"/riferimenti"));
		this.keywords.init(XMLUtil.createDocument(dom, xpath+"/keywords"));
		this.link_interni = XMLUtil.parseSetOfElement(dom, xpath+"/link_interno", new Link_interno());
		this.xlink = XMLUtil.parseSetOfElement(dom, xpath+"/xlink", new Xlink());
		List<Rif> rif_interni = XMLUtil.parseSetOfElement(dom, xpath+"/rif_interni/rif", new Rif());
		this.rif_contenuto = XMLUtil.parseSetOfElement(dom, xpath+"/rif_contenuto/oggetto", new Oggetto());

		// tiommi 29/01/2019 - selezione delle sottovoci degli elementi in precheck per la generazione della stampa ufficio ruolo
		selectSottovociUfficioRuolo();

		this.postit = XMLUtil.parseSetOfElement(dom, xpath+"/postit", new Postit());
		this.creazione.init(XMLUtil.createDocument(dom, xpath+"/storia/creazione"));
		this.ultima_modifica.init(XMLUtil.createDocument(dom, xpath+"/storia/ultima_modifica"));
		this.annullamento.init(XMLUtil.createDocument(dom, xpath+"/storia/annullamento"));
		this.storia = XMLUtil.parseSetOfElement(dom, xpath+"/storia/node()", new Storia());
		
		this.interventoDefaultCC = Boolean.parseBoolean(XMLUtil.parseStrictAttribute(dom, "response/funzionalita_disponibili/@interventoDefaultCC"));

		if (rif_interni != null && rif_interni.size() > 0) {
			for (int i=0; i<rif_interni.size(); i++) {
				Rif tmpRif = (Rif) rif_interni.get(i);
				if (tmpRif != null) {
					if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_RPA)) //diritto = RPA
						this.assegnazioneRPA = tmpRif;
					else
						this.assegnazioneCC.add(tmpRif); // diritto = cc
				}
			}
		}

		// Nel caso in cui la lista di CC sia piena procedo con il raggruppamento dei cc in base all'ufficio di appartenenza
		if (this.assegnazioneCC.size() > 0) {
			for (int i=0; i<this.assegnazioneCC.size(); i++) {
				Rif tmpRif = (Rif) assegnazioneCC.get(i);
				if (tmpRif != null && tmpRif.getDiritto() != null && !tmpRif.getDiritto().equals("")) {
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
			}
		}
		
		// tiommi aggiunto per settare intervento default
		if (this.assegnazioneCC.size() == 0) {
			assegnazioneCC.add(new Rif(interventoDefaultCC));
		}

		this.oldNumeroRegistro = XMLUtil.parseElement(dom, "raccoglitore/extra/indicericerca/numregistro");

		if (this.xlink.size() == 0) this.xlink.add(new Xlink());

		// mbernardini 04/07/2016 : recupero del numero di documenti (doc/fascicoli/raccoglitori) inclusi all'interno del raccoglitore
		List<?> docContenuti = dom.selectNodes(xpath + "/rif_contenuto/oggetto");
		if (docContenuti != null)
			numeroDocContenuti = docContenuti.size();
		else
			numeroDocContenuti = 0;

		// mbernardini 30/09/2016 : gestione dei raccoglitori custom
		this.codiceRaccoglitoreCustom = XMLUtil.parseStrictAttribute(dom, xpath + "/tipologia/@cod");
		this.descrizioneRaccoglitoreCustom = XMLUtil.parseStrictElement(dom, xpath + "/tipologia");

		return this;
	}

	/**
	 * Riscandisce la lista di rif_contenuto per la selezione di tutte le sottovoci di elementi pre checkati
	 */
	private void selectSottovociUfficioRuolo() {
		if (this.rif_contenuto != null && !this.rif_contenuto.isEmpty()) {
			boolean selectSottovoci = false;
			int currentLevel = 0;
			for (Oggetto oggetto : rif_contenuto) {
				if (oggetto.isCheckUfficioRuolo()) {
					selectSottovoci = true;
					currentLevel = oggetto.getLevel();
				}
				else {
					if (selectSottovoci && oggetto.getLevel() > currentLevel) {
						if (oggetto.getState().equalsIgnoreCase("ready"))
							oggetto.setCheckUfficioRuolo(true);
					}
					if (oggetto.getLevel() <= currentLevel) {
						selectSottovoci = false;
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void initHistory(Document dom){
		this.history = XMLUtil.parseSetOfElement(dom, "//item", new History());
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();

    	params.put(prefix+".@nrecord", this.nrecord);
    	params.put(prefix+".@anno", this.anno);
    	params.put(prefix+".@data_chiusura", this.data_chiusura);
    	params.put(prefix+".@cod_amm_aoo", this.cod_amm_aoo);
    	if (this.pubblico)
    		params.put(prefix+".@pubblico", "si");
    	else
    		params.put(prefix+".@pubblico", "no");
    	params.put(prefix+".@stato", this.stato);
    	params.put(prefix+".oggetto", this.oggetto);
    	params.putAll(note.asFormAdapterParams(prefix+".note"));

    	params.putAll(getAssegnazioneRPAParam());
    	if (assegnazioneCC != null && assegnazioneCC.size() > 0 && nrecord.equals(".")) { // solo inserimento
			for (int i=0; i<assegnazioneCC.size(); i++) {
				Rif rif = (Rif) assegnazioneCC.get(i);
				params.putAll(rif.asFormAdapterParams("*rif_interni.assegnazioneCC["+String.valueOf(i)+"]"));
			}
		}

    	// tiommi 16/01/2019 : duplicazione ricerca
		if (rif_contenuto != null && !rif_contenuto.isEmpty() && nrecord.equals(".")) { // solo inserimento
			params.put("*rif_contenuto.size", String.valueOf(rif_contenuto.size()));
			for (int i=0; i<rif_contenuto.size(); i++) {
				Oggetto oggetto = rif_contenuto.get(i);
				params.putAll(oggetto.asFormAdapterParams("*rif_contenuto.oggetto["+i+"]"));
			}
			// passaggio del vecchio numero di registro
			params.put("oldNumRegistroRaccIndici", this.oldNumeroRegistro);
		}

    	// mbernardini 30/09/2016 : gestione dei raccoglitori custom
    	if (codiceRaccoglitoreCustom != null && !codiceRaccoglitoreCustom.isEmpty()) {
	    	params.put(prefix+".tipologia", descrizioneRaccoglitoreCustom);
	    	params.put(prefix+".tipologia.@cod", codiceRaccoglitoreCustom);
    	}

    	// blocco commentato perche' il metodo e' chiamato in inserimento/modifica di un
    	// raccoglitore e i dati commentati non sono oggetto della modifica in nessun caso
    	/*
    	params.putAll(riferimenti.asFormAdapterParams(".riferimenti"));
    	params.putAll(keywords.asFormAdapterParams(".keywords"));
    	for (int i = 0; i < xlink.size(); i++) {
    		Xlink link = (Xlink) xlink.get(i);
    		params.putAll(link.asFormAdapterParams(".xlink["+String.valueOf(i)+"]"));
		}
    	*/

		// Imposto il parametro relativo all'invio della mail di notifica ai rif int
		params.put("*sendMailRifInterni", this.sendMailRifInterni+"");
		params.put("*sendMailSelectedRifInterni", this.sendMailSelectedRifInterni+"");
		if ( this.sendMailSelectedRifInterni ) {
			params.put("*invioCapillareNotifiche", this.getNotificheCapillariParam());
		}

    	return params;
	}

	public Map<String, String> getAssegnazioneRPAParam(){
		Map<String, String> params = new HashMap<String, String>();
		if (assegnazioneRPA != null && assegnazioneRPA.getNome_uff() != null && assegnazioneRPA.getNome_uff().length() > 0) {
			params.putAll(assegnazioneRPA.asFormAdapterParams("*rif_interni.assegnazioneRPA"));
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

	public void setRif_contenuto(List<Oggetto> contenuto_in) {
		this.rif_contenuto = contenuto_in;
	}

	public List<Oggetto> getRif_contenuto() {
		return rif_contenuto;
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

	public void setPubblico(boolean bozza) {
		this.pubblico = bozza;
	}

	public boolean isPubblico() {
		return pubblico;
	}

	public void setAnnullamento(Annullamento annullamento) {
		this.annullamento = annullamento;
	}

	public Annullamento getAnnullamento() {
		return annullamento;
	}

	public Rif getAssegnazioneRPA() {
		return assegnazioneRPA;
	}

	public void setAssegnazioneRPA(Rif assegnazioneRPA) {
		this.assegnazioneRPA = assegnazioneRPA;
	}

	public void setAssegnazioneRESO(Rif assegnazioneRESO) {
		this.assegnazioneRESO = assegnazioneRESO;
	}

	public Rif getAssegnazioneRESO() {
		return assegnazioneRESO;
	}

	public List<Rif> getAssegnazioneCC() {
		return assegnazioneCC;
	}

	public void setAssegnazioneCC(List<Rif> assegnazioneCC) {
		this.assegnazioneCC = assegnazioneCC;
	}

	public void setStato(String stato_deposito) {
		this.stato = stato_deposito;
	}

	public String getStato() {
		return stato;
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

	public void setNon_disponibile(Non_disponibile non_disponibile) {
		this.non_disponibile = non_disponibile;
	}

	public Non_disponibile getNon_disponibile() {
		return non_disponibile;
	}

	public void setData_chiusura(String data_chiusura) {
		this.data_chiusura = data_chiusura;
	}

	public String getData_chiusura() {
		return data_chiusura;
	}

	public void setContenuto_in(List<Contenuto_in> contenuto_in) {
		this.contenuto_in = contenuto_in;
	}

	public List<Contenuto_in> getContenuto_in() {
		return contenuto_in;
	}

	public boolean isSendMailRifInterni() {
		return sendMailRifInterni;
	}

	public void setSendMailRifInterni(boolean sendMailRifInterni) {
		this.sendMailRifInterni = sendMailRifInterni;
	}

	public boolean isSendMailSelectedRifInterni() {
		return sendMailSelectedRifInterni;
	}

	public void setSendMailSelectedRifInterni(boolean sendMailSelectedRifInterni) {
		this.sendMailSelectedRifInterni = sendMailSelectedRifInterni;
	}

	public boolean isCheckNomi() {
		return checkNomi;
	}

	public void setCheckNomi(boolean checkNomi) {
		this.checkNomi = checkNomi;
	}

	public int getNumeroDocContenuti() {
		return numeroDocContenuti;
	}

	public void setNumeroDocContenuti(int numeroDocContenuti) {
		this.numeroDocContenuti = numeroDocContenuti;
	}

	public List<Rif> getCc_list() {
		return cc_list;
	}

	public void setCc_list(List<Rif> cc_list) {
		this.cc_list = cc_list;
	}

	public HashMap<String, List<Rif>> getCc_ufficio() {
		return cc_ufficio;
	}

	public void setCc_ufficio(HashMap<String, List<Rif>> cc_ufficio) {
		this.cc_ufficio = cc_ufficio;
	}

	public String getCodiceRaccoglitoreCustom() {
		return codiceRaccoglitoreCustom;
	}

	public void setCodiceRaccoglitoreCustom(String codiceRaccoglitoreCustom) {
		this.codiceRaccoglitoreCustom = codiceRaccoglitoreCustom;
	}

	public String getDescrizioneRaccoglitoreCustom() {
		return descrizioneRaccoglitoreCustom;
	}

	public void setDescrizioneRaccoglitoreCustom(String descrizioneRaccoglitoreCustom) {
		this.descrizioneRaccoglitoreCustom = descrizioneRaccoglitoreCustom;
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
	 * Eliminazione di un Rif int in CC
	 */
	public void deleteRifintCC(Rif rif){
		if (rif != null) {
			assegnazioneCC.remove(rif);
			if (assegnazioneCC.isEmpty())
				assegnazioneCC.add(new Rif(interventoDefaultCC));
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
				Rif rifToAdd = new Rif(interventoDefaultCC);
				if (previous != null && previous.getTipo_uff() != null && previous.getTipo_uff().equals("ruolo"))
					rifToAdd.setTipo_uff("ruolo");
				assegnazioneCC.add(rifToAdd);
			}
		}
	}

	/**
	 * Spostamento in alto di un documento collegato al raccoglitore (e di tutti i documenti figli)
	 */
	public void moveUpOggetto(Oggetto contenuto) {
		if (contenuto != null && rif_contenuto != null) {
			int index = rif_contenuto.indexOf(contenuto);
			int level = contenuto.getLevel();
			if (index > 0) {
				int prevIndex;
				boolean canMoveUp = true;
				int prevNumChildren = 0;
				List<Oggetto> removed = new ArrayList<Oggetto>();
				//ricerca in alto nella lista un documento di pari livello con cui effetturare lo spostamento
				//N.B. si può spostare in alto solo se non si tratta del primo figlio
				for (prevIndex = index-1; prevIndex >= 0; prevIndex--) {
					Oggetto prevContenuto = rif_contenuto.get(prevIndex);
					if(prevContenuto.getLevel() < level) {
						//è il primo figlio
						canMoveUp = false;
						break;
					}
					if(prevContenuto.getLevel() == level) {
						//trovato
						break;
					}
					prevNumChildren++;
				}
				if (canMoveUp) {
					//viene rimosso il documento e tutti i suoi figli (ordinati in senso contrario)
					int newIndex = (index-1)-prevNumChildren;
					removed.add(rif_contenuto.remove(index));
					while(index < rif_contenuto.size()) {
						Oggetto childContenuto = rif_contenuto.get(index);
						if(childContenuto.getLevel() <= level) {
							//finiti i figli
							break;
						}
						removed.add(rif_contenuto.remove(index));
					}
					//vengono riaggiunti tutti i documenti rimossi nella posizione corretta
					rif_contenuto.addAll(newIndex, removed);
				}
			}
		}
	}

	/**
	 * Spostamento in basso di un documento collegato al raccoglitore (e di tutti i documenti figli)
	 */
	public void moveDownOggetto(Oggetto contenuto) {
		if (contenuto != null && rif_contenuto != null) {
			int index = rif_contenuto.indexOf(contenuto);
			int level = contenuto.getLevel();
			if (index < rif_contenuto.size()-1) {
				int childIndex= index+1;
				boolean canMoveDown = false;
				List<Oggetto> removed = new ArrayList<Oggetto>();
				//ricerca in basso nella lista un documento di pari livello con cui effetturare lo spostamento
				//e rimozione del documento e di tutti i suoi figli dalla lista
				//N.B. si può spostare in basso solo se non si tratta dell'ultimo figlio
				while(childIndex < rif_contenuto.size()) {
					Oggetto childContenuto = rif_contenuto.get(childIndex);
					if(childContenuto.getLevel() <= level) {
						//finiti i figli
						if (childContenuto.getLevel() == level) {
							//NON è l'ultimo figlio
							canMoveDown = true;
						}
						break;
					}
					removed.add(rif_contenuto.remove(childIndex));
				}
				if (canMoveDown) {
					//viene rimosso anche il documento corrente e aggiungo in cima alla lista dei rimossi
					removed.add(0, rif_contenuto.remove(index));
					//in index si trova ora il documento successivo, per trovare il punto in cui reinserire i rimossi
					//viene calcolato il numero dei sui figli
					int nextIndex = index+1;
					int nextNumChildren = 0;
					while(nextIndex < rif_contenuto.size()) {
						Oggetto nextContenutoChild = rif_contenuto.get(nextIndex);
						if(nextContenutoChild.getLevel() <= level) {
							//finiti i figli
							break;
						}
						nextNumChildren++;
						nextIndex++;
					}
					int newIndex = (index+1)+nextNumChildren;
					//vengono riaggiunti tutti i documenti rimossi ordinati al contrario
					rif_contenuto.addAll(newIndex, removed);
				}
				else {
					//vengono riaggiunti i documenti rimossi dove erano
					rif_contenuto.addAll(childIndex, removed);
				}
			}
		}
	}

	/**
	 * Spostamento di un livello in alto di un documento collegato al raccoglitore (e di tutti i documenti figli)
	 */
	public void moveInOggetto(Oggetto contenuto) {
		if (contenuto != null && rif_contenuto != null) {
			int index = rif_contenuto.indexOf(contenuto);
			int level = contenuto.getLevel();
			if (index > 0 && level < 3) {
				Oggetto prevContenuto = rif_contenuto.get(index-1);
				if (prevContenuto.getLevel() != level-1) {
					contenuto.setLevel(level+1);
					//aggiornamento dei figli
					int nextIndex;
					for(nextIndex = index+1; nextIndex < rif_contenuto.size(); nextIndex++) {
						Oggetto nextContenuto = rif_contenuto.get(nextIndex);
						if(nextContenuto.getLevel() <= level) {
							//figli finiti
							break;
						}
						else {
							if(nextContenuto.getLevel() < 3)
								nextContenuto.setLevel(nextContenuto.getLevel()+1);
						}
					}
				}
			}
		}
	}

	/**
	 * Spostamento di un livello in basso di un documento collegato al raccoglitore (e di tutti i documenti figli)
	 */
	public void moveOutOggetto(Oggetto contenuto) {
		if (contenuto != null && rif_contenuto != null) {
			int index = rif_contenuto.indexOf(contenuto);
			int level = contenuto.getLevel();
			if(index > 0 && level > 0) {
				contenuto.setLevel(level-1);
				//aggiornamento dei figli
				int nextIndex;
				for(nextIndex = index+1; nextIndex < rif_contenuto.size(); nextIndex++) {
					Oggetto nextContenuto = rif_contenuto.get(nextIndex);
					if(nextContenuto.getLevel() <= level) {
						//figli finiti
						break;
					}
					else {
						nextContenuto.setLevel(nextContenuto.getLevel()-1);
					}
				}
			}
		}
	}

	/**
	 * Spostamento nell'indice di destinazione del documento collegato al raccoglitore (e di tutti i documenti figli)
	 * viene automaticamente gestito il livello in base al livello del documento successivo alla nuova destinazione
	 */
	public void moveToIndex(Oggetto contenuto, int docIndex, int destinazione, int afterLevel) {
		if (contenuto != null && rif_contenuto != null) {
			int level = contenuto.getLevel();
			//salto di livello da applicare a tutti i figli (livello massimo rimane 3)
			//positivo se si passa da un livello inferiore ad uno superiore, negativo viceversa
			int jumpLevel = afterLevel - level;
			//viene temporanemente rimosso il documento e i figli dopo aver loro applicato l'opportuno livello
			contenuto.setLevel(afterLevel);
			List<Oggetto> toBeAdd = new ArrayList<Oggetto>();
			toBeAdd.add(rif_contenuto.remove(docIndex));
			int nextIndex = docIndex;
			Oggetto nextContenuto;
			while(nextIndex < rif_contenuto.size()) {
				nextContenuto = rif_contenuto.get(nextIndex);
				if(nextContenuto != null && nextContenuto.getLevel() > level) {
					nextContenuto.setLevel(nextContenuto.getLevel() + jumpLevel >= 3 ? 3 : nextContenuto.getLevel() + jumpLevel);
					toBeAdd.add(rif_contenuto.remove(nextIndex));
				}
				else {
					//figli finiti
					break;
				}
			}
			//finito di rimuovere il documento e i suoi figli viene riaggiunto tutto nella posizione esatta
			rif_contenuto.addAll(destinazione, toBeAdd);
		}
	}
}
