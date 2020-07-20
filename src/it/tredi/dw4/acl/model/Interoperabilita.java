package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Interoperabilita extends XmlEntity {
	
	private String cod_amm;
	private String cod_aoo;
	private String db;
	private String documentModel;
	private boolean protocollaFattura = false;
	private boolean splitByAttachments = false;
	private String oper;
	private String uff_oper;
	private Mailbox_in mailbox_in = new Mailbox_in();
	private Mailbox_out mailbox_out = new Mailbox_out();
	private Responsabile responsabile = new Responsabile();
	private Notify notify = new Notify();
	
	// definizione di persone che possono operare sulla casella (modifica dati, cambio password, ecc.)
	private List<GestoreMailbox> gestoriMailbox = new ArrayList<GestoreMailbox>();
	private boolean updateMailbox = false; // indica se l'utente corrente possiede i diritti di aggiornamento dati della mailbox
	private boolean changePassword = false; // indica se l'utente corrente possiede i diritti di aggiornamento della password di scaricamento posta
    
	public Interoperabilita() {
		// aggiunta di una istanza vuota di gestore mailbox
		this.gestoriMailbox.add(new GestoreMailbox());
	}
    
	public Interoperabilita(String xmlInteroperabilita) throws Exception {
        this.init(XMLUtil.getDOM(xmlInteroperabilita));
    }
    
    @SuppressWarnings("unchecked")
	public Interoperabilita init(Document domInteroperabilita) {
    	this.cod_amm 				= XMLUtil.parseAttribute(domInteroperabilita, "interoperabilita/@cod_amm");
    	this.cod_aoo 				= XMLUtil.parseAttribute(domInteroperabilita, "interoperabilita/@cod_aoo");
    	this.db 					= XMLUtil.parseAttribute(domInteroperabilita, "interoperabilita/@db");
    	this.documentModel 			= XMLUtil.parseAttribute(domInteroperabilita, "interoperabilita/@documentModel");
    	if (XMLUtil.parseAttribute(domInteroperabilita, "interoperabilita/@protocollaFattura").toLowerCase().equals("true"))
    		this.protocollaFattura 	= true;
    	else
    		this.protocollaFattura 	= false;
    	if (XMLUtil.parseAttribute(domInteroperabilita, "interoperabilita/@splitByAttachments").toLowerCase().equals("true"))
    		this.splitByAttachments 	= true;
    	else
    		this.splitByAttachments 	= false;
    	this.oper 					= XMLUtil.parseAttribute(domInteroperabilita, "interoperabilita/@oper");
    	this.uff_oper 				= XMLUtil.parseAttribute(domInteroperabilita, "interoperabilita/@uff_oper");
    	
    	mailbox_in.init(XMLUtil.createDocument(domInteroperabilita, "//interoperabilita/mailbox_in"));
    	mailbox_out.init(XMLUtil.createDocument(domInteroperabilita, "//interoperabilita/mailbox_out"));
    	responsabile.init(XMLUtil.createDocument(domInteroperabilita, "//interoperabilita/responsabile"));
    	notify.init(XMLUtil.createDocument(domInteroperabilita, "//interoperabilita/notify"));
    	
    	// mbernardini 15/10/2015 : gestori della mailbox
    	this.gestoriMailbox = XMLUtil.parseSetOfElement(domInteroperabilita, "//interoperabilita/gestori_mailbox/gestore", new GestoreMailbox());
    	if (this.gestoriMailbox.size() == 0) this.gestoriMailbox.add(new GestoreMailbox());
    	
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.cod_amm ) 		params.put(prefix+".@cod_amm", this.cod_amm.trim());
    	if (null != this.cod_aoo ) 		params.put(prefix+".@cod_aoo", this.cod_aoo.trim());
    	if (null != this.db ) 			params.put(prefix+".@db", this.db.trim());
    	if (null != this.documentModel) params.put(prefix+".@documentModel", this.documentModel);
    	if (null != this.oper ) 		params.put(prefix+".@oper", this.oper.trim());
    	if (null != this.uff_oper ) 	params.put(prefix+".@uff_oper", this.uff_oper.trim());
    	if (this.protocollaFattura)		params.put(prefix+".@protocollaFattura", "true");
    	if (this.splitByAttachments)	params.put(prefix+".@splitByAttachments", "true");
    	params.putAll(mailbox_in.asFormAdapterParams(prefix+".mailbox_in"));
    	params.putAll(mailbox_out.asFormAdapterParams(prefix+".mailbox_out"));
    	params.putAll(responsabile.asFormAdapterParams(prefix+".responsabile"));
    	params.putAll(notify.asFormAdapterParams(prefix+".notify"));
    	
    	// mbernardini 15/10/2015 : gestori della mailbox
    	int index = 0;
    	for (int i = 0; i < this.gestoriMailbox.size(); i++) {
    		GestoreMailbox gestoreMail = (GestoreMailbox) this.gestoriMailbox.get(i);
    		if (gestoreMail != null && gestoreMail.getMatricola() != null && gestoreMail.getMatricola().length() > 0) {
    			params.putAll(gestoreMail.asFormAdapterParams(prefix+".gestori_mailbox.gestore["+String.valueOf(index)+"]"));
    			index++;
    		}
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

	public void setDb(String db) {
		this.db = db;
	}

	public String getDb() {
		return db;
	}

	public void setDocumentModel(String documentModel) {
		this.documentModel = documentModel;
	}

	public String getDocumentModel() {
		return documentModel;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}

	public String getOper() {
		return oper;
	}

	public void setUff_oper(String uff_oper) {
		this.uff_oper = uff_oper;
	}

	public String getUff_oper() {
		return uff_oper;
	}

	public void setMailbox_in(Mailbox_in mailbox_in) {
		this.mailbox_in = mailbox_in;
	}

	public Mailbox_in getMailbox_in() {
		return mailbox_in;
	}

	public void setMailbox_out(Mailbox_out mailbox_out) {
		this.mailbox_out = mailbox_out;
	}

	public Mailbox_out getMailbox_out() {
		return mailbox_out;
	}

	public void setResponsabile(Responsabile responsabile) {
		this.responsabile = responsabile;
	}

	public Responsabile getResponsabile() {
		return responsabile;
	}

	public void setNotify(Notify notify) {
		this.notify = notify;
	}

	public Notify getNotify() {
		return notify;
	}
	
	public boolean isProtocollaFattura() {
		return protocollaFattura;
	}

	public void setProtocollaFattura(boolean protocollaFattura) {
		this.protocollaFattura = protocollaFattura;
	}
	
	public boolean isSplitByAttachments() {
		return splitByAttachments;
	}

	public void setSplitByAttachments(boolean splitByAttachments) {
		this.splitByAttachments = splitByAttachments;
	}
	
	public List<GestoreMailbox> getGestoriMailbox() {
		return gestoriMailbox;
	}

	public void setGestoriMailbox(List<GestoreMailbox> gestoriMailbox) {
		this.gestoriMailbox = gestoriMailbox;
	}
	
	public boolean isUpdateMailbox() {
		return updateMailbox;
	}

	public void setUpdateMailbox(boolean updateMailbox) {
		this.updateMailbox = updateMailbox;
	}

	public boolean isChangePassword() {
		return changePassword;
	}

	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}
	
	/**
	 * Eliminazione di un gestore della mailbox
	 */
	public String removeGestore() {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		if (gestore != null) {
			gestoriMailbox.remove(gestore);
			if (gestoriMailbox.isEmpty()) 
				gestoriMailbox.add(new GestoreMailbox());
		}
		return null;
	}
	
	/**
	 * Aggiunta di un gestore della mailbox
	 */
	public String addGestore() {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		int index = 0;
		if (gestore != null)
			index = gestoriMailbox.indexOf(gestore);
		
		if (gestoriMailbox != null) {
			if (gestoriMailbox.size() > index)
				gestoriMailbox.add(index+1,  new GestoreMailbox());
			else
				gestoriMailbox.add(new GestoreMailbox());
		}
		return null;
	}
	
}

