package it.tredi.dw4.acl.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class StrutturaInterna extends XmlEntity {
	
	private String nrecord = ".";
	private String nome = "";
	private String cod_aoo = "";
	private String cod_uff = "";
	private String cod_amm = "";
	private String cod_padre;
	private String tipologia;
	private String pers_ass_count;
	private String cod_responsabile;
	private String cognome_responsabile;
	private String nome_responsabile;
	private String nomcogn_responsabile;
	private String nomeresponsabile;
	private String multisocieta;
	
	private List<Telefono> telefoni;
	private List<Email> emails;
	private List<SitoWeb> siti_web;
	
	
	private Indirizzo indirizzo = new Indirizzo();
	private EmailCertificata email_certificata = new EmailCertificata();
	private Competenze competenze = new Competenze();
	private Note note = new Note();
	
	private String emailSdI = ""; // eventuale email dell'SdI da utilizzare per l'invio di fatturePA
	
	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();
	
	public StrutturaInterna() {
		this.telefoni = new ArrayList<Telefono>();
		this.emails = new ArrayList<Email>();
		this.siti_web = new ArrayList<SitoWeb>();
	}
    
    public StrutturaInterna(String xmlStrutturaInterna) throws Exception {
        this.init(XMLUtil.getDOM(xmlStrutturaInterna));
    }
    
    @SuppressWarnings("unchecked")
	public StrutturaInterna init(Document domStrutturaInterna) {
    	this.nome = 			XMLUtil.parseElement(domStrutturaInterna, "struttura_interna/nome");
    	this.nrecord = 			XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@nrecord", ".");
    	this.cod_aoo = 			XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@cod_aoo");
    	this.cod_uff = 			XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@cod_uff");
    	this.cod_amm = 			XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@cod_amm");
    	this.cod_responsabile = XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@cod_responsabile");
    	this.cognome_responsabile = XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@cognome_responsabile");
    	this.nome_responsabile = XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@nome_responsabile");
    	this.nomcogn_responsabile = XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@nomcogn_responsabile");
    	this.nomeresponsabile = XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@nomcogn_responsabile");
    	this.cod_padre = 		XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@cod_padre");
    	this.tipologia = 		XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@tipologia");
    	this.pers_ass_count =   XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/@pers_ass_count");
    	this.multisocieta = 	XMLUtil.parseElementNode(domStrutturaInterna, "//struttura_interna/multisocieta");
    	this.indirizzo.init(XMLUtil.createDocument(domStrutturaInterna, "//struttura_interna/indirizzo"));
    	this.telefoni = XMLUtil.parseSetOfElement(domStrutturaInterna, "//struttura_interna/telefono", new Telefono());
        this.emails = XMLUtil.parseSetOfElement(domStrutturaInterna, "//struttura_interna/email", new Email());
        this.modifiche = XMLUtil.parseSetOfElement(domStrutturaInterna, "//struttura_interna/storia/modifica", new Modifica());
        this.email_certificata.init(XMLUtil.createDocument(domStrutturaInterna, "//struttura_interna/email_certificata"));
        this.siti_web = XMLUtil.parseSetOfElement(domStrutturaInterna, "//struttura_interna/sito_web", new SitoWeb());
    	this.competenze.init(XMLUtil.createDocument(domStrutturaInterna, "//struttura_interna/competenze"));
    	this.note.init(XMLUtil.createDocument(domStrutturaInterna, "//struttura_interna/note"));
    	this.creazione.init(XMLUtil.createDocument(domStrutturaInterna, "//struttura_interna/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(domStrutturaInterna, "//struttura_interna/storia/ultima_modifica"));
    	
    	this.emailSdI = 		XMLUtil.parseAttribute(domStrutturaInterna, "struttura_interna/fatturaPA/@emailSdI");
    	
    	if ( telefoni.size() == 0 ) telefoni.add(new Telefono());
        if ( emails.size() == 0 ) emails.add(new Email());
        if ( siti_web.size() == 0 ) siti_web.add(new SitoWeb());
        
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".nome", this.nome);
    	params.put(prefix + ".@nrecord", this.nrecord);
    	params.put(prefix + ".@cod_aoo", this.cod_aoo);
    	params.put(prefix + ".@cod_amm", this.cod_amm);
    	params.put(prefix + ".@cod_uff", this.cod_uff);
    	params.put(prefix + ".@cod_responsabile", this.cod_responsabile);
    	params.put(prefix + ".@cod_padre", this.cod_padre);
    	params.put(prefix + ".@tipologia", this.tipologia);
    	params.put(prefix + ".multisocieta", this.multisocieta);
    	params.putAll(indirizzo.asFormAdapterParams(".indirizzo"));
    	for (int i = 0; i < telefoni.size(); i++) {
    		Telefono tel = (Telefono) telefoni.get(i);
    		params.putAll(tel.asFormAdapterParams(".telefono["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < emails.size(); i++) {
    		Email mail = (Email) emails.get(i);
    		params.putAll(mail.asFormAdapterParams(".email["+String.valueOf(i)+"]"));
		}
    	params.putAll(email_certificata.asFormAdapterParams(".email_certificata"));
    	for (int i = 0; i < siti_web.size(); i++) {
    		SitoWeb web = (SitoWeb) siti_web.get(i);
    		params.putAll(web.asFormAdapterParams(".sito_web["+String.valueOf(i)+"]"));
		}
    	params.putAll(competenze.asFormAdapterParams(".competenze"));
    	params.putAll(note.asFormAdapterParams(".note"));
    	
    	params.put(prefix + ".fatturaPA.@emailSdI", emailSdI);
    	
    	return params;
    }

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public void setCod_aoo(String cognome) {
		this.cod_aoo = cognome;
	}

	public String getCod_uff() {
		return cod_uff;
	}

	public void setCod_uff(String cod_uff) {
		this.cod_uff = cod_uff;
	}

	public String getCod_responsabile() {
		return cod_responsabile;
	}

	public void setCod_responsabile(String matricola) {
		this.cod_responsabile = matricola;
	}

	public String getCod_padre() {
		return cod_padre;
	}

	public void setCod_padre(String cod_padre) {
		this.cod_padre = cod_padre;
	}

	public String getTipologia() {
		return tipologia;
	}

	public void setTipologia(String titolo) {
		this.tipologia = titolo;
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

	public String getEmailSdI() {
		return emailSdI;
	}

	public void setEmailSdI(String emailSdI) {
		this.emailSdI = emailSdI;
	}
	
	public void setCod_amm(String nome_uff) {
		this.cod_amm = nome_uff;
	}

	public String getCod_amm() {
		return cod_amm;
	}

	public Indirizzo getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(Indirizzo indirizzo) {
		this.indirizzo = indirizzo;
	}

	public List<Telefono> getTelefoni() {
		return telefoni;
	}

	public void setTelefoni(List<Telefono> telefoni) {
		this.telefoni = telefoni;
	}

	public List<Email> getEmails() {
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	public List<SitoWeb> getSiti_web() {
		return siti_web;
	}

	public void setSiti_web(List<SitoWeb> siti_web) {
		this.siti_web = siti_web;
	}

	public void setEmail_certificata(EmailCertificata email_certificata) {
		this.email_certificata = email_certificata;
	}

	public EmailCertificata getEmail_certificata() {
		return email_certificata;
	}

	public void setPers_ass_count(String pers_ass_count) {
		this.pers_ass_count = pers_ass_count;
	}

	public String getPers_ass_count() {
		return pers_ass_count;
	}

	public void setCognome_responsabile(String cognome_responsabile) {
		this.cognome_responsabile = cognome_responsabile;
	}

	public String getCognome_responsabile() {
		return cognome_responsabile;
	}

	public void setNome_responsabile(String nome_responsabile) {
		this.nome_responsabile = nome_responsabile;
	}

	public String getNome_responsabile() {
		return nome_responsabile;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}
	
	public void setNomcogn_responsabile(String cognome_nome_responsabile) {
		this.nomcogn_responsabile = cognome_nome_responsabile;
	}

	public String getNomcogn_responsabile() {
		return nomcogn_responsabile;
	}

	public void setCreazione(Creazione creazione) {
		this.creazione = creazione;
	}

	public Creazione getCreazione() {
		return creazione;
	}

	public void setModifiche(List<Modifica> modifiche) {
		this.modifiche = modifiche;
	}

	public List<Modifica> getModifiche() {
		return modifiche;
	}

	public void setUltima_modifica(UltimaModifica ultima_modifica) {
		this.ultima_modifica = ultima_modifica;
	}

	public UltimaModifica getUltima_modifica() {
		return ultima_modifica;
	}

	public boolean isRenderTelefoni(){
		for (Iterator<Telefono> iter = telefoni.iterator(); iter.hasNext();) {
			Telefono tel = (Telefono) iter.next();
			if (null != tel.getNum() && !"".equals(tel.getNum().trim()))
				return true;
		}
		return false;
	}
	public boolean isRenderSitiWeb(){
		for (Iterator<SitoWeb> iter = siti_web.iterator(); iter.hasNext();) {
			SitoWeb sito = (SitoWeb) iter.next();
			if (null != sito.getUrl() && !"".equals(sito.getUrl().trim()))
				return true;
		}
		return false;
	}
	public boolean isRenderEmail(){
		for (Iterator<Email> iter = emails.iterator(); iter.hasNext();) {
			Email email = (Email) iter.next();
			if (null != email.getAddr() && !"".equals(email.getAddr().trim()))
				return true;
		}
		return false;
	}

	public void setNomeresponsabile(String nomeresponsabile) {
		this.nomeresponsabile = nomeresponsabile;
	}

	public String getNomeresponsabile() {
		return nomeresponsabile;
	}

	public void setMultisocieta(String multisocieta) {
		this.multisocieta = multisocieta;
	}

	public String getMultisocieta() {
		return multisocieta;
	}
	
	public String deleteTelefono(){
		Telefono telefono = (Telefono) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("telefono");
		telefoni.remove(telefono);
        if ( telefoni.size() == 0 ) telefoni.add(new Telefono());
		return null;
	}

	public String addTelefono(){
		Telefono telefono = (Telefono) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("telefono");
		int index = telefoni.indexOf(telefono);
		if (index == telefoni.size()-1)
			telefoni.add(new Telefono());
		else
			telefoni.add(index+1, new Telefono());
		return null;
	}
	
	public String deleteEmail(){
		Email email = (Email) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("email");
		emails.remove(email);
        if ( emails.size() == 0 ) emails.add(new Email());
		return null;
	}
	
	public String addEmail(){
		Email email = (Email) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("email");
		int index = emails.indexOf(email);
		if (index == emails.size()-1)
			emails.add(new Email());
		else
			emails.add(index+1, new Email());
		return null;
	}
	
	public String deleteSitoWeb(){
		SitoWeb sitoWeb = (SitoWeb) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("sitoWeb");
		siti_web.remove(sitoWeb);
        if ( siti_web.size() == 0 ) siti_web.add(new SitoWeb());
		return null;
	}
	
	public String addSitoWeb(){
		SitoWeb sitoWeb = (SitoWeb) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("sitoWeb");
		int index = siti_web.indexOf(sitoWeb);
		if (index == siti_web.size()-1)
			siti_web.add(new SitoWeb());
		else
			siti_web.add(index+1, new SitoWeb());
		return null;
	}

	public String moveUpTelefono(){
		Telefono telefono = (Telefono) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("telefono");
		int index = telefoni.indexOf(telefono);
		if (index > 0 ) {
			telefoni.remove(index);
			this.telefoni.add(index-1, telefono);
		}
		return null;
	}
	public String moveDownTelefono(){
		Telefono telefono = (Telefono) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("telefono");
		int index = telefoni.indexOf(telefono);
		if (index < telefoni.size()-1 ) {
			telefoni.remove(index);
			this.telefoni.add(index+1, telefono);
		}
		return null;
	}	

	public String moveUpEmail(){
		Email email = (Email) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("email");
		int index = emails.indexOf(email);
		if (index > 0 ) {
			emails.remove(index);
			this.emails.add(index-1, email);
		}
		return null;
	}
	public String moveDownEmail(){
		Email email = (Email) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("email");
		int index = emails.indexOf(email);
		if (index < emails.size()-1 ) {
			emails.remove(index);
			this.emails.add(index+1, email);
		}
		return null;
	}	

	
	public String moveUpSitoWeb(){
		SitoWeb sitoWeb = (SitoWeb) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("sitoWeb");
		int index = siti_web.indexOf(sitoWeb);
		if (index > 0 ) {
			siti_web.remove(index);
			this.siti_web.add(index-1, sitoWeb);
		}
		return null;
	}
	public String moveDownSitoWeb(){
		SitoWeb sitoWeb = (SitoWeb) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("sitoWeb");
		int index = siti_web.indexOf(sitoWeb);
		if (index < siti_web.size()-1 ) {
			siti_web.remove(index);
			this.siti_web.add(index+1, sitoWeb);
		}
		return null;
	}	
	
}

