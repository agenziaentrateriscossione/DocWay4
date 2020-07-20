package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.GestoreMailbox;
import it.tredi.dw4.acl.model.Interoperabilita;
import it.tredi.dw4.acl.model.Mailbox_archiviazione;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocAoo extends AclShowdoc {
	private String xml;
	
	private AclDocumentFormsAdapter formsAdapter;
	private it.tredi.dw4.acl.model.Aoo aoo;
	
	private boolean adminAcl = false; // vale true se l'utente corrente e' superUser o amministratore di ACL
	private boolean pecInGestione = false; // vale true se nell'AOO e' registrata almeno una PEC in gestione per l'utente corrente
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ShowdocAoo() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	aoo = new it.tredi.dw4.acl.model.Aoo();
    	aoo.init(domDocumento);
    	
    	if (StringUtil.booleanValue(XMLUtil.parseStrictAttribute(domDocumento, "/response/@adminAcl")))
    		adminAcl = true;
    	else
    		adminAcl = false;
    	
    	pecInGestione = existsPecInGestione(domDocumento);
    }
	
	/**
	 * Verifica se per l'aoo esistono delle PEC in gestione all'utente corrente in base ai seguenti controlli:
	 * 1) l'utente corrente e' amministratore di ACL
	 * 2) la matricola dell'utente corrisponde a quella indicata per un responsabile di una PEC; 
	 * 3) il coduff dell'ufficio di appartenenza dell'utente corrisponde a quello indicato per un responsabile di una PEC;
	 * 4) l'indirizzo pec dell'ufficio di appartenenza dell'utente corrisponde a quello indicato su una PEC (mail in scaricamento);
	 * 5) l'utente corrente e' presente nella lista di gestori della mailbox.
	 * 
	 * @return true se esistono PEC in gestione all'utente, false altrimenti
	 */
	private boolean existsPecInGestione(Document domDocumento) {
		boolean pecInGestione = false;
		
		UserBean user = getUserBean();
    	String emailUff = XMLUtil.parseStrictAttribute(domDocumento, "/response/@actionOnPecAddress");
    	String codUff = XMLUtil.parseStrictAttribute(domDocumento, "/response/@actionOnPecCodUff");
    	
    	int i = 0;
    	if (aoo.getMailbox_archiviazione().size() > 0) {
	    	while (!pecInGestione && i<aoo.getMailbox_archiviazione().size()) {
	    		Mailbox_archiviazione archiviazione = (Mailbox_archiviazione) aoo.getMailbox_archiviazione().get(i);
	    		if (archiviazione != null) {
	    			if (adminAcl
	    					|| (user.getMatricola() != null && user.getMatricola().length() > 0 && user.getMatricola().equals(archiviazione.getResponsabile().getMatricola()))
	    					|| (codUff != null && codUff.length() > 0 && codUff.equals(archiviazione.getResponsabile().getCod_uff()))
	    					|| (emailUff != null && emailUff.length() > 0 && emailUff.equals(archiviazione.getMailbox().getEmail()))) {
	    				pecInGestione = true;
	    			}
	    			else {
	    				if (archiviazione.getGestoriMailbox() != null && archiviazione.getGestoriMailbox().size() > 0) {
	    					int index = 0;
	    					while (!pecInGestione && index < archiviazione.getGestoriMailbox().size()) {
	    						GestoreMailbox gestore = archiviazione.getGestoriMailbox().get(index);
	    						if (gestore != null && gestore.getMatricola() != null && gestore.getMatricola().equals(user.getMatricola()))
	    							pecInGestione = true;
	    						index++;
	    					}
	    				}
	    			}
	    		}
	    		i++;
	    	}
    	}
    	i = 0;
    	if (!pecInGestione && aoo.getInteroperabilita().size() > 0) {
    		while (!pecInGestione && i<aoo.getInteroperabilita().size()) {
    			Interoperabilita interop = (Interoperabilita) aoo.getInteroperabilita().get(i);
	    		if (interop != null) { 
	    			if (adminAcl
	    					|| (user.getMatricola() != null && user.getMatricola().length() > 0 && user.getMatricola().equals(interop.getResponsabile().getMatricola()))
	    					|| (codUff != null && codUff.length() > 0 && codUff.equals(interop.getResponsabile().getCod_uff()))
	    					|| (emailUff != null && emailUff.length() > 0 && emailUff.equals(interop.getMailbox_in().getEmail()))) {
	    				pecInGestione = true;
	    			}
	    			else {
	    				if (interop.getGestoriMailbox() != null && interop.getGestoriMailbox().size() > 0) {
	    					int index = 0;
	    					while (!pecInGestione && index < interop.getGestoriMailbox().size()) {
	    						GestoreMailbox gestore = interop.getGestoriMailbox().get(index);
	    						if (gestore != null && gestore.getMatricola() != null && gestore.getMatricola().equals(user.getMatricola())) {
	    							pecInGestione = true;
	    						}
	    						index++;
	    					}
	    				}
	    			}
	    		}
	    		i++;
	    	}
    	}
    	
    	return pecInGestione;
	}
	
	public AclDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public void reload(ComponentSystemEvent event) throws Exception {
		reload();
	}
	
	public void reload() throws Exception {
		super._reload("showdoc@aoo");
	}

	public void setAoo(it.tredi.dw4.acl.model.Aoo aoo) {
		this.aoo = aoo;
	}

	public it.tredi.dw4.acl.model.Aoo getAoo() {
		return this.aoo;
	}
	
	public boolean isAdminAcl() {
		return adminAcl;
	}

	public void setAdminAcl(boolean admin) {
		this.adminAcl = admin;
	}

	public boolean isPecInGestione() {
		return pecInGestione;
	}

	public void setPecInGestione(boolean pecInGestione) {
		this.pecInGestione = pecInGestione;
	}
	
	public String ripetiNuovo() throws Exception{
		formsAdapter.ripetiNuovo("aoo"); 
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		formsAdapter.fillFormsFromResponse(response);
		
		DocEditAoo docEditAoo = new DocEditAoo();
		docEditAoo.getFormsAdapter().fillFormsFromResponse(response);
		docEditAoo.init(response.getDocument());
		setSessionAttribute("docEditAoo", docEditAoo);
		
		return "docEdit@aoo";
	}

}
