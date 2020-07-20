package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Aoo extends XmlEntity {
	private String cod_amm;
	private String cod_aoo;
	private String nrecord = ".";
	private String nome;
	private List<Interoperabilita> interoperabilita;
	private List<Mailbox_archiviazione> mailbox_archiviazione; 
	
	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();

    
	public Aoo() {}
    
	public Aoo(String xmlAoo) throws Exception {
        this.init(XMLUtil.getDOM(xmlAoo));
    }
    
    @SuppressWarnings("unchecked")
	public Aoo init(Document domAoo) {
    	this.cod_amm = XMLUtil.parseAttribute(domAoo, "aoo/@cod_amm");
    	this.cod_aoo = XMLUtil.parseAttribute(domAoo, "aoo/@cod_aoo");
    	this.nrecord = XMLUtil.parseAttribute(domAoo, "aoo/@nrecord", ".");
    	this.nome = XMLUtil.parseElement(domAoo, "aoo/nome");
    	this.interoperabilita = XMLUtil.parseSetOfElement(domAoo, "//aoo/interoperabilita", new Interoperabilita());
    	this.mailbox_archiviazione = XMLUtil.parseSetOfElement(domAoo, "//aoo/mailbox_archiviazione", new Mailbox_archiviazione());
    	this.modifiche = 			XMLUtil.parseSetOfElement(domAoo, "//aoo/storia/modifica", new Modifica());
    	this.creazione.init(XMLUtil.createDocument(domAoo, "//aoo/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(domAoo, "//aoo/storia/ultima_modifica"));
    	
    	if ( mailbox_archiviazione.isEmpty() ) 	mailbox_archiviazione.add(new Mailbox_archiviazione());
    	if ( interoperabilita.isEmpty() ) 		interoperabilita.add(new Interoperabilita());
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.cod_amm ) params.put(prefix+".@cod_amm", this.cod_amm);
    	if (null != this.cod_aoo ) params.put(prefix+".@cod_aoo", this.cod_aoo);
    	if (null != this.nrecord ) params.put(prefix+".@nrecord", this.nrecord);
    	if (null != this.nome ) params.put(prefix+".nome", this.nome);
		for (int i = 0; i < interoperabilita.size(); i++) {
			Interoperabilita interop = (Interoperabilita) interoperabilita.get(i);
			params.putAll(interop.asFormAdapterParams(prefix+".interoperabilita["+i+"]"));
		}
		for (int i = 0; i < mailbox_archiviazione.size(); i++) {
			Mailbox_archiviazione mailbox_arch = (Mailbox_archiviazione) mailbox_archiviazione.get(i);
			params.putAll(mailbox_arch.asFormAdapterParams(prefix+".mailbox_archiviazione["+i+"]"));
		}
    	return params;
    }
    
    public String getCod_amm() {
		return cod_amm;
	}

	public void setCod_amm(String spec) {
		this.cod_amm = spec;
	}

	public void setCod_aoo(String bban) {
		this.cod_aoo = bban;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public void setNome(String name) {
		this.nome = name;
	}

	public String getNome() {
		return nome;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}

	public void setMailbox_archiviazione(List<Mailbox_archiviazione> mailbox_archiviazione) {
		this.mailbox_archiviazione = mailbox_archiviazione;
	}

	public List<Mailbox_archiviazione> getMailbox_archiviazione() {
		return mailbox_archiviazione;
	}

	public void setInteroperabilita(List<Interoperabilita> interoperabilita) {
		this.interoperabilita = interoperabilita;
	}

	public List<Interoperabilita> getInteroperabilita() {
		return interoperabilita;
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
	
	public String addInteroperabilita(){
		Interoperabilita interop = (Interoperabilita) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("interop");
		int index = interoperabilita.indexOf(interop);
		
		// mbernardini 15/10/2015 : gestori della mailbox
		Interoperabilita empty = new Interoperabilita();
		// visto che l'aggiunta di mailbox puo' essere fatta dal solo amministratore di ACL gli posso dare i permessi di operare sulla mailbox
		empty.setChangePassword(true);
		empty.setUpdateMailbox(true);
		
		if (index < interoperabilita.size()-1)
			interoperabilita.add(index+1, empty);
		else
			interoperabilita.add(empty);

		return null;
	}
	
	public String deleteInteroperabilita(){
		Interoperabilita interop = (Interoperabilita) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("interop");
		interoperabilita.remove(interop);
    	if ( interoperabilita.isEmpty() ) 		
    		interoperabilita.add(new Interoperabilita());
		return null;
	}
	
	public String moveUpInteroperabilita(){
		Interoperabilita interop = (Interoperabilita) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("interop");
		int index = interoperabilita.indexOf(interop);
		if (index > 0 ) {
			interoperabilita.remove(index);
			this.interoperabilita.add(index-1, interop);
		}
		return null;
	}

	public String moveDownInteroperabilita(){
		Interoperabilita interop = (Interoperabilita) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("interop");
		int index = interoperabilita.indexOf(interop);
		if (index < interoperabilita.size()-1 ) {
			interoperabilita.remove(index);
			this.interoperabilita.add(index+1, interop);
		}
		return null;
	}	

	
	public String addMailbox_archiviazione(){
		Mailbox_archiviazione mailbox_arch = (Mailbox_archiviazione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int index = mailbox_archiviazione.indexOf(mailbox_arch);
		
		// mbernardini 15/10/2015 : gestori della mailbox
		Mailbox_archiviazione empty = new Mailbox_archiviazione();
		// visto che l'aggiunta di mailbox puo' essere fatta dal solo amministratore di ACL gli posso dare i permessi di operare sulla mailbox
		empty.setChangePassword(true);
		empty.setUpdateMailbox(true);
		
		if (index < mailbox_archiviazione.size()-1)
			mailbox_archiviazione.add(index+1, empty);
		else
			mailbox_archiviazione.add(empty);
		return null;
	}
	public String deleteMailbox_archiviazione(){
		Mailbox_archiviazione mailbox_arch = (Mailbox_archiviazione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		mailbox_archiviazione.remove(mailbox_arch);
		if ( mailbox_archiviazione.isEmpty() ) 	
			mailbox_archiviazione.add(new Mailbox_archiviazione());
		return null;
	}
	
	public String moveUpMailbox_archiviazione(){
		Mailbox_archiviazione mailbox_arch = (Mailbox_archiviazione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int index = mailbox_archiviazione.indexOf(mailbox_arch);
		if (index > 0 ) {
			mailbox_archiviazione.remove(index);
			this.mailbox_archiviazione.add(index-1, mailbox_arch);
		}
		return null;
	}

	public String moveDownMailbox_archiviazione(){
		Mailbox_archiviazione mailbox_arch = (Mailbox_archiviazione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int index = mailbox_archiviazione.indexOf(mailbox_arch);
		if (index < mailbox_archiviazione.size()-1 ) {
			mailbox_archiviazione.remove(index);
			this.mailbox_archiviazione.add(index+1, mailbox_arch);
		}
		return null;
	}	
}

