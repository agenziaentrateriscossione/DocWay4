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


public class Recapito extends XmlEntity {
	private Indirizzo indirizzo = new Indirizzo();
	private List<Telefono> telefoni = new ArrayList<Telefono>();
	private List<Email> emails = new ArrayList<Email>();
	private EmailCertificata email_certificata = new EmailCertificata();
	private List<SitoWeb> siti_web = new ArrayList<SitoWeb>();
    
	public Recapito() {}
    
	public Recapito(String xmlRecapito) throws Exception {
		this.init(XMLUtil.getDOM(xmlRecapito));
    }
    
    @SuppressWarnings("unchecked")
	public Recapito init(Document domRecapito) {
        this.indirizzo.init(XMLUtil.createDocument(domRecapito, "//recapito/indirizzo"));
        this.telefoni = XMLUtil.parseSetOfElement(domRecapito, "//recapito/telefono", new Telefono());
        this.emails = XMLUtil.parseSetOfElement(domRecapito, "//recapito/email", new Email());
        this.email_certificata.init(XMLUtil.createDocument(domRecapito, "//recapito/email_certificata"));
        this.siti_web = XMLUtil.parseSetOfElement(domRecapito, "//recapito/sito_web", new SitoWeb());
        if ( telefoni.isEmpty() ) telefoni.add(new Telefono());
        if ( emails.isEmpty() ) emails.add(new Email());
        if ( siti_web.isEmpty() ) siti_web.add(new SitoWeb());
        return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.putAll(indirizzo.asFormAdapterParams(prefix+".indirizzo"));
    	for (int i = 0; i < telefoni.size(); i++) {
    		Telefono tel = (Telefono) telefoni.get(i);
    		params.putAll(tel.asFormAdapterParams(prefix+".telefono["+String.valueOf(i)+"]"));
		}
    	for (int i = 0; i < emails.size(); i++) {
    		Email mail = (Email) emails.get(i);
    		params.putAll(mail.asFormAdapterParams(prefix+".email["+String.valueOf(i)+"]"));
		}
    	params.putAll(email_certificata.asFormAdapterParams(prefix+".email_certificata"));
    	for (int i = 0; i < siti_web.size(); i++) {
    		SitoWeb web = (SitoWeb) siti_web.get(i);
    		params.putAll(web.asFormAdapterParams(prefix+".sito_web["+String.valueOf(i)+"]"));
		}
    	return params;
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
	
	public EmailCertificata getEmail_certificata() {
		return email_certificata;
	}

	public void setEmail_certificata(EmailCertificata email_certificata) {
		this.email_certificata = email_certificata;
	}

	public List<SitoWeb> getSiti_web() {
		return siti_web;
	}

	public void setSiti_web(List<SitoWeb> siti_web) {
		this.siti_web = siti_web;
	}
 
	public String deleteTelefono(){
		Telefono telefono = (Telefono) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("telefono");
		telefoni.remove(telefono);
        if ( telefoni.isEmpty() ) telefoni.add(new Telefono());
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
        if ( emails.isEmpty() ) emails.add(new Email());
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
        if ( siti_web.isEmpty() ) siti_web.add(new SitoWeb());
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

