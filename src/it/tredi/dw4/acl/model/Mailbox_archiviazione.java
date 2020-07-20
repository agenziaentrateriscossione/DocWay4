package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Mailbox_archiviazione extends XmlEntity {
	
	private String nome;
	private String documentModel;
	private boolean splitByAttachments = false;
	private String oper;
	private String uff_oper;
	private Mailbox mailbox = new Mailbox();
	private Responsabile responsabile = new Responsabile();
	
	// definizione di persone che possono operare sulla casella (modifica dati, cambio password, ecc.)
	private List<GestoreMailbox> gestoriMailbox = new ArrayList<GestoreMailbox>();
	private boolean updateMailbox = false; // indica se l'utente corrente possiede i diritti di aggiornamento dati della mailbox
	private boolean changePassword = false; // indica se l'utente corrente possiede i diritti di aggiornamento della password di scaricamento posta
    
	public Mailbox_archiviazione() {
		// aggiunta di una istanza vuota di gestore mailbox
		this.gestoriMailbox.add(new GestoreMailbox());
	}
    
	public Mailbox_archiviazione(String xmlMailbox_archiviazione) throws Exception {
        this.init(XMLUtil.getDOM(xmlMailbox_archiviazione));
    }
    
    @SuppressWarnings("unchecked")
	public Mailbox_archiviazione init(Document domMailbox) {
    	this.nome 		= XMLUtil.parseAttribute(domMailbox, "mailbox_archiviazione/@nome");
    	this.documentModel 	= XMLUtil.parseAttribute(domMailbox, "mailbox_archiviazione/@documentModel");
    	if (XMLUtil.parseAttribute(domMailbox, "mailbox_archiviazione/@splitByAttachments").toLowerCase().equals("true"))
    		this.splitByAttachments 	= true;
    	else
    		this.splitByAttachments 	= false;
    	this.oper 			= XMLUtil.parseAttribute(domMailbox, "mailbox_archiviazione/@oper");
    	this.uff_oper 		= XMLUtil.parseAttribute(domMailbox, "mailbox_archiviazione/@uff_oper");
    	mailbox.init(XMLUtil.createDocument(domMailbox, "//mailbox_archiviazione/mailbox"));
    	responsabile.init(XMLUtil.createDocument(domMailbox, "//mailbox_archiviazione/responsabile"));
    	
    	// mbernardini 15/10/2015 : gestori della mailbox
    	this.gestoriMailbox = XMLUtil.parseSetOfElement(domMailbox, "//mailbox_archiviazione/gestori_mailbox/gestore", new GestoreMailbox());
    	if (this.gestoriMailbox.size() == 0) this.gestoriMailbox.add(new GestoreMailbox());
    	
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.nome ) 		params.put(prefix+".@nome", this.nome.trim());
    	if (null != this.documentModel) params.put(prefix+".@documentModel", this.documentModel);
    	if (this.splitByAttachments)	params.put(prefix+".@splitByAttachments", "true");
    	if (null != this.oper ) 		params.put(prefix+".@oper", this.oper.trim());
    	if (null != this.uff_oper ) 	params.put(prefix+".@uff_oper", this.uff_oper.trim());
    	params.putAll(mailbox.asFormAdapterParams(prefix+".mailbox"));
    	params.putAll(responsabile.asFormAdapterParams(prefix+".responsabile"));
    	
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
    
    public String getNome() {
		return nome;
	}

	public void setNome(String spec) {
		this.nome = spec;
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

	public void setResponsabile(Responsabile responsabile) {
		this.responsabile = responsabile;
	}

	public Responsabile getResponsabile() {
		return responsabile;
	}

	public void setMailbox(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public Mailbox getMailbox() {
		return mailbox;
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

