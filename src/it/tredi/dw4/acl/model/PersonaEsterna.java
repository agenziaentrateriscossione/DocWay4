package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;

import javax.faces.context.FacesContext;

import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class PersonaEsterna extends XmlEntity {
	private String nrecord = ".";
	private String nome = "";
	private String cognome = "";
	private String data_nascita;
	private String luogo_nascita;
	private String cod_amm = "";
	private String cod_aoo = "";
	private String sesso;
	private String codice_fiscale;
	private String partita_iva;
	private String matricola = ".";
	private String titolo;
	private String titolo_deferenza;
	private String secondo_nome;
	private String soprannome;
	
	private Recapito recapito = new Recapito();
	private RecapitoPersonale recapito_personale = new RecapitoPersonale();
	private List<Appartenenza> appartenenza = new ArrayList<Appartenenza>();
	private List<Responsabilita> responsabilita = new ArrayList<Responsabilita>();
	private Competenze competenze = new Competenze();
	private Note note = new Note();
	
	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();
	
	public PersonaEsterna() {}
    
    public PersonaEsterna(String xmlPersonaEsterna) throws Exception {
        this.init(XMLUtil.getDOM(xmlPersonaEsterna));
    }
    
    @SuppressWarnings("unchecked")
	public PersonaEsterna init(Document domPersonaEsterna) {
    	this.nrecord = 			XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@nrecord", ".");
    	this.nome = 			XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@nome");
    	this.cognome = 			XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@cognome");
    	this.data_nascita = 	XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@data_nascita");
    	this.luogo_nascita = 	XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@luogo_nascita");
    	this.cod_amm = 			XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@cod_amm");
    	this.cod_aoo = 			XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@cod_aoo");
    	this.codice_fiscale = 	XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@codice_fiscale");
    	this.partita_iva  = 	XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@partita_iva");
    	this.matricola = 		XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@matricola", ".");
    	this.sesso = 			XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@sesso", "M");
    	this.titolo = 			XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@titolo");
    	this.titolo_deferenza = XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@titolo_deferenza");
    	this.secondo_nome = 	XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@secondo_nome");
    	this.soprannome = 		XMLUtil.parseAttribute(domPersonaEsterna, "persona_esterna/@soprannome");
    	this.recapito.init(XMLUtil.createDocument(domPersonaEsterna, "//persona_esterna/recapito"));
    	this.recapito_personale.init(XMLUtil.createDocument(domPersonaEsterna, "//persona_esterna/recapito_personale"));
    	this.competenze.init(XMLUtil.createDocument(domPersonaEsterna, "//persona_esterna/competenze"));
    	this.note.init(XMLUtil.createDocument(domPersonaEsterna, "//persona_esterna/note"));
    	this.responsabilita = XMLUtil.parseSetOfElement(domPersonaEsterna, "//persona_esterna/responsabilita", new Responsabilita());
    	this.appartenenza = XMLUtil.parseSetOfElement(domPersonaEsterna, "//persona_esterna/appartenenza", new Appartenenza());
    	this.modifiche = XMLUtil.parseSetOfElement(domPersonaEsterna, "//persona_esterna/storia/modifica", new Modifica());
    	this.creazione.init(XMLUtil.createDocument(domPersonaEsterna, "//persona_esterna/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(domPersonaEsterna, "//persona_esterna/storia/ultima_modifica"));
    	
    	if ( appartenenza.isEmpty() ) appartenenza.add(new Appartenenza());
    	
    	return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@nrecord", this.nrecord);
    	params.put(prefix + ".@nome", this.nome);
    	params.put(prefix + ".@cognome", this.cognome);
    	params.put(prefix + ".@data_nascita", this.data_nascita);
    	params.put(prefix + ".@luogo_nascita", this.luogo_nascita);
    	params.put(prefix + ".@cod_amm", (this.cod_amm != null) ? this.cod_amm.trim() : null);
    	params.put(prefix + ".@cod_aoo", (this.cod_aoo != null) ? this.cod_aoo.trim() : null);
    	params.put(prefix + ".@codice_fiscale", this.codice_fiscale);
    	params.put(prefix + ".@partita_iva", this.partita_iva);
    	params.put(prefix + ".@matricola", (this.matricola != null) ? this.matricola.trim() : null);
    	params.put(prefix + ".@sesso", this.sesso);
    	params.put(prefix + ".@titolo", this.titolo);
    	params.put(prefix + ".@titolo_deferenza", this.titolo_deferenza);
    	params.put(prefix + ".@secondo_nome", this.secondo_nome);
    	params.put(prefix + ".@soprannome", this.soprannome);
    	params.putAll(recapito.asFormAdapterParams(".recapito"));
    	params.putAll(recapito_personale.asFormAdapterParams(".recapito_personale"));
    	params.putAll(competenze.asFormAdapterParams(".competenze"));
    	params.putAll(note.asFormAdapterParams(".note"));
    	for (int i = 0; i < appartenenza.size(); i++) {
    		Appartenenza app = (Appartenenza) appartenenza.get(i);
    		params.putAll(app.asFormAdapterParams(".appartenenza["+String.valueOf(i)+"]"));
		}
    	return params;
    }
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getMatricola() {
		return matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getTitolo_deferenza() {
		return titolo_deferenza;
	}

	public void setTitolo_deferenza(String titolo_deferenza) {
		this.titolo_deferenza = titolo_deferenza;
	}

	public String getSecondo_nome() {
		return secondo_nome;
	}

	public void setSecondo_nome(String secondo_nome) {
		this.secondo_nome = secondo_nome;
	}

	public String getSoprannome() {
		return soprannome;
	}

	public void setSoprannome(String soprannome) {
		this.soprannome = soprannome;
	}

	
	public Recapito getRecapito() {
		return recapito;
	}

	public void setRecapito(Recapito recapito) {
		this.recapito = recapito;
	}

	public RecapitoPersonale getRecapito_personale() {
		return recapito_personale;
	}

	public void setRecapito_personale(RecapitoPersonale recapito_personale) {
		this.recapito_personale = recapito_personale;
	}

	public Competenze getCompetenze() {
		return competenze;
	}

	public void setCompetenze(Competenze competenze) {
		this.competenze = competenze;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public void setData_nascita(String data_nascita) {
		this.data_nascita = data_nascita;
	}

	public String getData_nascita() {
		return data_nascita;
	}

	public void setLuogo_nascita(String luogo_nascita) {
		this.luogo_nascita = luogo_nascita;
	}

	public String getLuogo_nascita() {
		return luogo_nascita;
	}
	
	public void setCod_amm(String cod_amm) {
		this.cod_amm = cod_amm;
	}

	public String getCod_amm() {
		return cod_amm;
	}

	public void setCod_aoo(String cod_aoo) {
		this.cod_aoo = cod_aoo;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public void setCodice_fiscale(String codice_fiscale) {
		this.codice_fiscale = codice_fiscale;
	}

	public String getCodice_fiscale() {
		return codice_fiscale;
	}

	public void setPartita_iva(String partita_iva) {
		this.partita_iva = partita_iva;
	}

	public String getPartita_iva() {
		return partita_iva;
	}

	public void setAppartenenza(List<Appartenenza> appartenenza) {
		this.appartenenza = appartenenza;
	}

	public List<Appartenenza> getAppartenenza() {
		return appartenenza;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setResponsabilita(List<Responsabilita> responsabilita) {
		this.responsabilita = responsabilita;
	}

	public List<Responsabilita> getResponsabilita() {
		return responsabilita;
	}

	public void setModifiche(List<Modifica> modifiche) {
		this.modifiche = modifiche;
	}

	public List<Modifica> getModifiche() {
		return modifiche;
	}

	public void setCreazione(Creazione creazione) {
		this.creazione = creazione;
	}

	public Creazione getCreazione() {
		return creazione;
	}

	public void setUltima_modifica(UltimaModifica ultima_modifica) {
		this.ultima_modifica = ultima_modifica;
	}

	public UltimaModifica getUltima_modifica() {
		return ultima_modifica;
	}
	
	public void clearAppartenenza() {
		if (appartenenza != null) {
			appartenenza.clear();
			if (appartenenza.isEmpty())
				this.addAppartenenza();
		}
	}
	
	public String addAppartenenza(){
		Appartenenza appart = (Appartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("appartenenza");
		int index = appartenenza.indexOf(appart);
		if (appartenenza.size() > index)
			appartenenza.add(index+1, new Appartenenza());
		else
			appartenenza.add(new Appartenenza());
		return null;
	}
	
	public String deleteAppartenenza(){
		Appartenenza appart = (Appartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("appartenenza");
		this.appartenenza.remove(appart);
		if (appartenenza.isEmpty()) appartenenza.add(new Appartenenza());
		return null;
	}

	public String moveUpAppartenenza(){
		Appartenenza appart = (Appartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("appartenenza");
		int index = appartenenza.indexOf(appart);
		if (index > 0 ) {
			appartenenza.remove(index);
			this.appartenenza.add(index-1, appart);
		}
		return null;
	}

	public String moveDownAppartenenza(){
		Appartenenza appart = (Appartenenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("appartenenza");
		int index = appartenenza.indexOf(appart);
		if (index < appartenenza.size()-1 ) {
			appartenenza.remove(index);
			this.appartenenza.add(index+1, appart);
		}
		return null;
	}	
}
