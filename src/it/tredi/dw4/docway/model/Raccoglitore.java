package it.tredi.dw4.docway.model;

import it.tredi.dw4.acl.model.Creazione;
import it.tredi.dw4.acl.model.Note;
import it.tredi.dw4.acl.model.UltimaModifica;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

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
	
	protected Non_disponibile non_disponibile = new Non_disponibile();
	
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
		this.postit = XMLUtil.parseSetOfElement(dom, xpath+"/postit", new Postit());
		this.creazione.init(XMLUtil.createDocument(dom, xpath+"/storia/creazione"));
		this.ultima_modifica.init(XMLUtil.createDocument(dom, xpath+"/storia/ultima_modifica"));
		this.annullamento.init(XMLUtil.createDocument(dom, xpath+"/storia/annullamento"));
		this.storia = XMLUtil.parseSetOfElement(dom, xpath+"/storia/node()", new Storia());
		
		if (rif_interni != null && rif_interni.size() > 0) {
			for (int i=0; i<rif_interni.size(); i++) {
				Rif tmpRif = (Rif) rif_interni.get(i);
				if (tmpRif != null) {
					if (tmpRif.getDiritto().toUpperCase().equals(Const.DOCWAY_DIRITTO_RPA)) //diritto = RPA
						this.assegnazioneRPA = tmpRif;
				}
			}
		}
		
		if (this.xlink.size() == 0) this.xlink.add(new Xlink()); 
		
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
    	
    	return params;
	}
	
	public Map<String, String> getAssegnazioneRPAParam(){
		Map<String, String> params = new HashMap<String, String>();
		if (assegnazioneRPA != null && assegnazioneRPA.getNome_uff() != null && assegnazioneRPA.getNome_uff().length() > 0) {
			params.putAll(assegnazioneRPA.asFormAdapterParams("*rif_interni.assegnazioneRPA"));
		}
		return params;
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
	
}
