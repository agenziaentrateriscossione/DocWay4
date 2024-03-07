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

public class Ruolo extends XmlEntity {
	private String nrecord = ".";
	private String nome;
	private String cod_aoo;
	private String cod_amm;
	private String id;
	private String cod;
	private String descrizione;
	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();
	private String pers_ass_count;
	private Societa societa = new Societa();

	// mbernardini 15/06/2017 : aggiunta email a ruoli
	private List<Email> emails = new ArrayList<Email>();
	private List<EmailCertificata> emailCerts = new ArrayList<EmailCertificata>();
	
	public Ruolo() {}
    
    public Ruolo(String xmlProfilo) throws Exception {
        this.init(XMLUtil.getDOM(xmlProfilo));
    }
    
	@SuppressWarnings("unchecked")
	public Ruolo init(Document domRuolo) {
		this.nrecord = 	 		XMLUtil.parseAttribute(domRuolo, "ruolo/@nrecord", ".");
		this.cod = 	 			XMLUtil.parseAttribute(domRuolo, "ruolo/@cod");
		this.cod_amm = 	 		XMLUtil.parseAttribute(domRuolo, "ruolo/@cod_amm");
		this.cod_aoo = 	 		XMLUtil.parseAttribute(domRuolo, "ruolo/@cod_aoo");
		this.pers_ass_count = 	XMLUtil.parseAttribute(domRuolo, "ruolo/@pers_ass_count");
		this.id = 	 			XMLUtil.parseAttribute(domRuolo, "ruolo/@id", ".");
    	this.nome = 	 		XMLUtil.parseElement(domRuolo, "ruolo/nome");
    	this.descrizione = 		XMLUtil.parseElement(domRuolo, "ruolo/descrizione", false);
    	this.societa.init(XMLUtil.createDocument(domRuolo, "//ruolo/societa"));
    	this.creazione.init(XMLUtil.createDocument(domRuolo, "//ruolo/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(domRuolo, "//ruolo/storia/ultima_modifica"));
    	this.modifiche = XMLUtil.parseSetOfElement(domRuolo, "//ruolo/storia/modifica", new Modifica());
    	
    	this.emails = XMLUtil.parseSetOfElement(domRuolo, "//ruolo/email", new Email());
        this.emailCerts = XMLUtil.parseSetOfElement(domRuolo, "//ruolo/email_certificata", new EmailCertificata());
    	if ( emails.size() == 0 ) emails.add(new Email());
        if ( emailCerts.size() == 0 ) emailCerts.add(new EmailCertificata());
        
        return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@id", (this.id != null) ? this.id.trim() : null);
    	params.put(prefix+".@nrecord", this.nrecord);
    	params.put(prefix+".nome", this.nome);
    	params.put(prefix+".@cod_amm", (this.cod_amm != null) ? this.cod_amm.trim() : null);
    	params.put(prefix+".@cod_aoo", (this.cod_aoo != null) ? this.cod_aoo.trim() : null);
    	params.put(prefix+".descrizione", this.descrizione);
    	params.put("_CODSOCIETA_", societa.getCod());
    	
    	for (int i = 0; i < emails.size(); i++) {
    		Email mail = (Email) emails.get(i);
    		params.putAll(mail.asFormAdapterParams(prefix+".email["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < emailCerts.size(); i++) {
    		EmailCertificata email = (EmailCertificata) emailCerts.get(i);
    		params.putAll(email.asFormAdapterParams(prefix+".email_certificata["+String.valueOf(i)+"]"));
		}
    	
    	return params;
    }
    
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String titolo) {
		this.descrizione = titolo;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setCod_aoo(String cod_aoo) {
		this.cod_aoo = cod_aoo;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public void setCod_amm(String cod_amm) {
		this.cod_amm = cod_amm;
	}

	public String getCod_amm() {
		return cod_amm;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
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

	public void setPers_ass_count(String pers_ass_count) {
		this.pers_ass_count = pers_ass_count;
	}

	public String getPers_ass_count() {
		return pers_ass_count;
	}

	public void setSocieta(Societa societa) {
		this.societa = societa;
	}

	public Societa getSocieta() {
		return societa;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public String getCod() {
		return cod;
	}
	
	public List<Email> getEmails() {
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	public List<EmailCertificata> getEmailCerts() {
		return emailCerts;
	}

	public void setEmailCerts(List<EmailCertificata> emailCerts) {
		this.emailCerts = emailCerts;
	}
	
	public boolean isRenderEmail(){
		for (Iterator<Email> iter = emails.iterator(); iter.hasNext();) {
			Email email = (Email) iter.next();
			if (null != email.getAddr() && !"".equals(email.getAddr().trim()))
				return true;
		}
		return false;
	}
	
	public boolean isRenderEmailCert(){
		for (Iterator<EmailCertificata> iter = emailCerts.iterator(); iter.hasNext();) {
			EmailCertificata email = (EmailCertificata) iter.next();
			if (null != email.getAddr() && !"".equals(email.getAddr().trim()))
				return true;
		}
		return false;
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
	
	public String deleteEmailCert(){
		EmailCertificata emailCert = (EmailCertificata) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("emailCert");
		emailCerts.remove(emailCert);
        if ( emailCerts.size() == 0 ) emailCerts.add(new EmailCertificata());
		return null;
	}
	
	public String addEmailCert(){
		EmailCertificata emailCert = (EmailCertificata) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("emailCert");
		int index = emailCerts.indexOf(emailCert);
		if (index == emailCerts.size()-1)
			emailCerts.add(new EmailCertificata());
		else
			emailCerts.add(index+1, new EmailCertificata());
		return null;
	}
	
	public String moveUpEmailCert(){
		EmailCertificata emailCert = (EmailCertificata) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("emailCert");
		int index = emailCerts.indexOf(emailCert);
		if (index > 0 ) {
			emailCerts.remove(index);
			this.emailCerts.add(index-1, emailCert);
		}
		return null;
	}
	
	public String moveDownEmailCert(){
		EmailCertificata emailCert = (EmailCertificata) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("emailCert");
		int index = emailCerts.indexOf(emailCert);
		if (index < emailCerts.size()-1 ) {
			emailCerts.remove(index);
			this.emailCerts.add(index+1, emailCert);
		}
		return null;
	}

}
