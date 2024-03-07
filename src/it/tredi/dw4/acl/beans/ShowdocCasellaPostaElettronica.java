package it.tredi.dw4.acl.beans;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.CasellaPostaElettronica;
import it.tredi.dw4.acl.model.GestoreMailbox;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocumentFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.ReloadMsg;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class ShowdocCasellaPostaElettronica extends AclShowdoc {

	private String xml;
	
	private AclDocumentFormsAdapter formsAdapter;
	private CasellaPostaElettronica casellaPostaElettronica = new CasellaPostaElettronica();
	
	private boolean adminAcl = false; // vale true se l'utente corrente e' superUser o amministratore di ACL
	private boolean pecInGestione = false; // vale true se nell'AOO e' registrata almeno una PEC in gestione per l'utente corrente
	
	public ShowdocCasellaPostaElettronica() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		casellaPostaElettronica.init(dom);
    	
    	if (StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@adminAcl")))
    		adminAcl = true;
    	else
    		adminAcl = false;
    	
    	pecInGestione = existsPecInGestione(dom);
    	
    	// inizializzazione di componenti common
    	initCommons(dom);
	}
	
	/**
	 * Verifica se l'utente corrente ha in gestione la casella di posta in base ai seguenti controlli:
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
    	
    	if (adminAcl
				|| (user.getMatricola() != null && user.getMatricola().length() > 0 && user.getMatricola().equals(casellaPostaElettronica.getResponsabile().getMatricola()))
				|| (codUff != null && codUff.length() > 0 && codUff.equals(casellaPostaElettronica.getResponsabile().getCod_uff()))
				|| (emailUff != null && emailUff.length() > 0 && emailUff.equals(casellaPostaElettronica.getMailbox_in().getEmail()))) {
			pecInGestione = true;
		}
		else {
			if (casellaPostaElettronica.getGestoriMailbox() != null && casellaPostaElettronica.getGestoriMailbox().size() > 0) {
				int index = 0;
				while (!pecInGestione && index < casellaPostaElettronica.getGestoriMailbox().size()) {
					GestoreMailbox gestore = casellaPostaElettronica.getGestoriMailbox().get(index);
					if (gestore != null && gestore.getMatricola() != null && gestore.getMatricola().equals(user.getMatricola()))
						pecInGestione = true;
					index++;
				}
			}
		}
    	
    	return pecInGestione;
	}
	
	/**
	 * Verifica se i parametri di connessione alla casella di posta specificata sono corretti o meno (attraverso un tentativo di connessione)
	 * @param mailboxType tipologia di mailbox da controllare (mailbox_in, mailbox_out)
	 * @return
	 * @throws Exception
	 */
	public String testConnection(String mailboxType) throws Exception {
		try {
			formsAdapter.testConnectionMailbox(mailboxType);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			// lettura del messaggio di ritorno
			ReloadMsg message = new ReloadMsg();
			message.setActive(true);
			message.init(response.getDocument());
			message.setLevel(Const.MSG_LEVEL_SUCCESS);
			
			reload(); // reload del documento
			
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			session.setAttribute("reloadmsg", message);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	@Override
	public DocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public void reload() throws Exception {
		super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/acl/showdoc@casellaPostaElettronica");
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public CasellaPostaElettronica getCasellaPostaElettronica() {
		return casellaPostaElettronica;
	}

	public void setCasellaPostaElettronica(CasellaPostaElettronica casellaPostaElettronica) {
		this.casellaPostaElettronica = casellaPostaElettronica;
	}

	public boolean isAdminAcl() {
		return adminAcl;
	}

	public void setAdminAcl(boolean adminAcl) {
		this.adminAcl = adminAcl;
	}

	public boolean isPecInGestione() {
		return pecInGestione;
	}

	public void setPecInGestione(boolean pecInGestione) {
		this.pecInGestione = pecInGestione;
	}

}
