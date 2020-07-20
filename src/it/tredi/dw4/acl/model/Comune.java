package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Comune extends XmlEntity {
	private String nrecord = ".";
	private String nome;
	private String prov;
	private String codice_istat;
	private String cap;
	private String prefisso;
	private String regione;
	private String nazione;
	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();

    
	public Comune() {}
    
	public Comune(String xmlComune) throws Exception {
        this.init(XMLUtil.getDOM(xmlComune));
    }
    
    @SuppressWarnings("unchecked")
	public Comune init(Document domComune) {
    	this.nrecord = 		XMLUtil.parseAttribute(domComune, "comune/@nrecord", ".");
    	this.nome = 		XMLUtil.parseAttribute(domComune, "comune/@nome");
    	this.prov = 		XMLUtil.parseAttribute(domComune, "comune/@prov");
    	this.codice_istat = XMLUtil.parseAttribute(domComune, "comune/@codice_istat");
    	this.cap = 			XMLUtil.parseAttribute(domComune, "comune/@cap");
    	this.prefisso = 	XMLUtil.parseAttribute(domComune, "comune/@prefisso");
    	this.regione = 		XMLUtil.parseAttribute(domComune, "comune/@regione");
    	this.nazione = 		XMLUtil.parseAttribute(domComune, "comune/@nazione");
    	
    	this.modifiche = XMLUtil.parseSetOfElement(domComune, "//comune/storia/modifica", new Modifica());
    	this.creazione.init(XMLUtil.createDocument(domComune, "//comune/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(domComune, "//comune/storia/ultima_modifica"));
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@nrecord", this.nrecord);
    	params.put(prefix+".@nome", this.nome);
    	params.put(prefix+".@prov", this.prov);
    	params.put(prefix+".@codice_istat", this.codice_istat);
    	params.put(prefix+".@cap", this.cap);
    	params.put(prefix+".@prefisso", this.prefisso);
    	params.put(prefix+".@regione", this.regione);
    	params.put(prefix+".@nazione", this.nazione);
    	return params;
    }
    
    public String getNome() {
		return nome;
	}

	public void setNome(String addr) {
		this.nome = addr;
	}

	public String getProv() {
		return prov;
	}

	public void setProv(String prov) {
		this.prov = prov;
	}

	public String getCodice_istat() {
		return codice_istat;
	}

	public void setCodice_istat(String codice_istat) {
		this.codice_istat = codice_istat;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getPrefisso() {
		return prefisso;
	}

	public void setPrefisso(String prefisso) {
		this.prefisso = prefisso;
	}

	public String getRegione() {
		return regione;
	}

	public void setRegione(String regione) {
		this.regione = regione;
	}

	public String getNazione() {
		return nazione;
	}

	public void setNazione(String nazione) {
		this.nazione = nazione;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
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

}
