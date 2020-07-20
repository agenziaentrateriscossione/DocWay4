package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class StrutturaEsterna extends XmlEntity {
	
	private String nrecord = ".";
	private String nome = "";
	private String codice_fiscale = "";
	private String cod_uff = ".";
	private String partita_iva = "";
	private String cod_amm = "";
	private String cod_aoo = "";
	private String cod_responsabile = "";
	private String nome_responsabile = "";
	private String cognome_responsabile = "";
	private String nomeresponsabile = "";
	private String tipologia = "";
	private String cod_SAP = "";
	private String pers_ass_count = "";
	
	private Indirizzo indirizzo = new Indirizzo();
	private List<Telefono> telefoni;
	private List<Email> emails;
	private List<SitoWeb> siti_web;
	private EmailCertificata email_certificata = new EmailCertificata();
	private Categoria categoria = new Categoria();
	private Banca banca = new Banca();
	private Competenze competenze = new Competenze();
	private Note note = new Note();
	
	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();
	
	private Interop_webservice interop_webservice = new Interop_webservice();
	
	public StrutturaEsterna() {
		telefoni = new ArrayList<Telefono>();
		emails = new ArrayList<Email>();
		siti_web = new ArrayList<SitoWeb>();
	}
    
    public StrutturaEsterna(String xmlStrutturaEsterna) throws Exception {
        this.init(XMLUtil.getDOM(xmlStrutturaEsterna));
    }
    
    @SuppressWarnings("unchecked")
	public StrutturaEsterna init(Document domStrutturaEsterna) {
    	this.nrecord = 				XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@nrecord", ".");
    	this.nome = 				XMLUtil.parseElement(domStrutturaEsterna, "struttura_esterna/nome");
    	this.codice_fiscale = 		XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@codice_fiscale");
    	this.cod_SAP = 				XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@cod_SAP");
    	this.pers_ass_count = 		XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@pers_ass_count");
    	this.cod_uff = 				XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@cod_uff", ".");
    	this.partita_iva = 			XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@partita_iva");
    	this.cod_amm = 				XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@cod_amm");
    	this.cod_aoo = 				XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@cod_aoo");
    	this.cod_responsabile = 	XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@cod_responsabile");
    	this.nome_responsabile = 	XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@nome_responsabile");
    	this.cognome_responsabile = XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@cognome_responsabile");
    	this.nomeresponsabile = 	XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@nomcogn_responsabile");
    	this.tipologia = 			XMLUtil.parseAttribute(domStrutturaEsterna, "struttura_esterna/@tipologia");
    	this.telefoni = 			XMLUtil.parseSetOfElement(domStrutturaEsterna, "//struttura_esterna/telefono", new Telefono());
        this.emails = 				XMLUtil.parseSetOfElement(domStrutturaEsterna, "//struttura_esterna/email", new Email());
        this.siti_web = 			XMLUtil.parseSetOfElement(domStrutturaEsterna, "//struttura_esterna/sito_web", new SitoWeb());
        this.indirizzo.init(XMLUtil.createDocument(domStrutturaEsterna, "//struttura_esterna/indirizzo"));
        this.email_certificata.init(XMLUtil.createDocument(domStrutturaEsterna, "//struttura_esterna/email_certificata"));
        this.categoria.init(XMLUtil.createDocument(domStrutturaEsterna, "//struttura_esterna/categoria"));
        this.banca.init(XMLUtil.createDocument(domStrutturaEsterna, "//struttura_esterna/banca"));
    	this.competenze.init(XMLUtil.createDocument(domStrutturaEsterna, "//struttura_esterna/competenze"));
    	this.note.init(XMLUtil.createDocument(domStrutturaEsterna, "//struttura_esterna/note"));
    	this.interop_webservice.init(XMLUtil.createDocument(domStrutturaEsterna, "//struttura_esterna/interop_webservice"));
    	this.modifiche = XMLUtil.parseSetOfElement(domStrutturaEsterna, "//struttura_esterna/storia/modifica", new Modifica());
    	this.creazione.init(XMLUtil.createDocument(domStrutturaEsterna, "//struttura_esterna/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(domStrutturaEsterna, "//struttura_esterna/storia/ultima_modifica"));
    	
    	if ( telefoni.size() == 0 ) telefoni.add(new Telefono());
        if ( emails.size() == 0 ) emails.add(new Email());
        if ( siti_web.size() == 0 ) siti_web.add(new SitoWeb());
        if ( email_certificata == null ) email_certificata = new EmailCertificata();
        return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".nome", this.nome);
    	params.put(prefix + ".@nrecord", this.nrecord);
    	params.put(prefix + ".@codice_fiscale", this.codice_fiscale);
    	params.put(prefix + ".@cod_SAP", this.cod_SAP);
    	params.put(prefix + ".@partita_iva", this.partita_iva);
    	params.put(prefix + ".@cod_amm", this.cod_amm);
    	params.put(prefix + ".@cod_aoo", this.cod_aoo);
    	params.put(prefix + ".@cod_uff", this.cod_uff);
    	params.put(prefix + ".@cod_responsabile", this.cod_responsabile);
    	params.put(prefix + ".@tipologia", this.tipologia);
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
    	params.putAll(categoria.asFormAdapterParams(".categoria"));
    	params.putAll(banca.asFormAdapterParams(".banca"));
    	params.putAll(competenze.asFormAdapterParams(".competenze"));
    	params.putAll(note.asFormAdapterParams(".note"));
    	params.putAll(interop_webservice.asFormAdapterParams(".interop_webservice"));
    	
    	return params;
    }
    
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodice_fiscale() {
		return codice_fiscale;
	}

	public void setCodice_fiscale(String cod_fiscale) {
		this.codice_fiscale = cod_fiscale;
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

	public void setCod_responsabile(String cod_responsabile) {
		this.cod_responsabile = cod_responsabile;
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

	public void setPartita_iva(String partita_iva) {
		this.partita_iva = partita_iva;
	}

	public String getPartita_iva() {
		return partita_iva;
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

	public void setBanca(Banca banca) {
		this.banca = banca;
	}

	public Banca getBanca() {
		return banca;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}
	
	public String getNome_responsabile() {
		return nome_responsabile;
	}

	public void setNome_responsabile(String nome_responsabile) {
		this.nome_responsabile = nome_responsabile;
	}

	public String getCognome_responsabile() {
		return cognome_responsabile;
	}

	public void setCognome_responsabile(String cognome_responsabile) {
		this.cognome_responsabile = cognome_responsabile;
	}

	public void setNomeresponsabile(String nomeResponsabile) {
		this.nomeresponsabile = nomeResponsabile;
	}

	public String getNomeresponsabile() {
		return nomeresponsabile;
	}
	
	public void setCod_SAP(String cod_SAP) {
		this.cod_SAP = cod_SAP;
	}

	public String getCod_SAP() {
		return cod_SAP;
	}

	public void setPers_ass_count(String pers_ass_count) {
		this.pers_ass_count = pers_ass_count;
	}

	public String getPers_ass_count() {
		return pers_ass_count;
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
	
	public Interop_webservice getInterop_webservice() {
		return interop_webservice;
	}

	public void setInterop_webservice(Interop_webservice interop_webservice) {
		this.interop_webservice = interop_webservice;
	}
	
	public void setCreazione(Creazione creazione) {
		this.creazione = creazione;
	}

	public Creazione getCreazione() {
		return creazione;
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
}
