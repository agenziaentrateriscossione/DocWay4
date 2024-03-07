package it.tredi.dw4.acl.beans;

import org.dom4j.Document;

import it.tredi.dw4.acl.model.GestoreMailbox;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditModifyCasellaPostaElettronica extends DocEditCasellaPostaElettronica {

	private boolean adminAcl = false;
	
	public DocEditModifyCasellaPostaElettronica() throws Exception {
		super();
	}
	
	@Override
	public void init(Document dom) {
		initData(dom);
		
		// valore di default per campi password in modo da non mostrare sul client il reale valore impostato
		// mbernardini 06/07/2016 : in caso di password vuota il campo password deve essere mantenuto vuoto
		if (getCasellaPostaElettronica().getMailbox_in() != null && getCasellaPostaElettronica().getMailbox_in().getPassword() != null && !getCasellaPostaElettronica().getMailbox_in().getPassword().isEmpty())
			getCasellaPostaElettronica().getMailbox_in().setPassword(Const.PWD_SKIP_LABEL);
    	if (getCasellaPostaElettronica().getMailbox_out() != null && getCasellaPostaElettronica().getMailbox_out().getPassword() != null && !getCasellaPostaElettronica().getMailbox_out().getPassword().isEmpty())
    		getCasellaPostaElettronica().getMailbox_out().setPassword(Const.PWD_SKIP_LABEL);
    	
		this.adminAcl = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@adminAcl"));
		
		UserBean user = getUserBean();
    	String matricola = user.getMatricola();
    	String emailUff = XMLUtil.parseStrictAttribute(dom, "/response/@actionOnPecAddress");
    	String codUff = XMLUtil.parseStrictAttribute(dom, "/response/@actionOnPecCodUff");
    	
    	// mbernardini 15/10/2015 : gestori della mailbox
    	checkActionOnMailbox(this.adminAcl, matricola, codUff, emailUff);
	}
	
	/**
	 * Verifica se l'utente corrente possiede i diritti di intervento sulla mailbox (aggiornamento dati, cambio password, 
	 * ecc.). La verifica avviene nel modo seguente:
	 * 1) l'utente corrente e' amministratore di ACL
	 * 2) la matricola dell'utente corrisponde a quella indicata per un responsabile di una PEC; 
	 * 3) il coduff dell'ufficio di appartenenza dell'utente corrisponde a quello indicato per un responsabile di una PEC;
	 * 4) l'indirizzo pec dell'ufficio di appartenenza dell'utente corrisponde a quello indicato su una PEC;
	 * 5) l'utente corrente e' presente nella lista di gestori della mailbox
	 */
	private void checkActionOnMailbox(boolean adminAcl, String matricola, String codUff, String emailUff) {
		if (getCasellaPostaElettronica() != null) {
			boolean update = false;
			boolean changePwd = false;
			
			if (adminAcl
					|| (matricola != null && matricola.length() > 0 && matricola.equals(getCasellaPostaElettronica().getResponsabile().getMatricola()))
					|| (codUff != null && codUff.length() > 0 && codUff.equals(getCasellaPostaElettronica().getResponsabile().getCod_uff()))
					|| (emailUff != null && emailUff.length() > 0 && emailUff.equals(getCasellaPostaElettronica().getMailbox_in().getEmail()))) {
				update = true;
				changePwd = true;
			}
			else {
				if (getCasellaPostaElettronica().getGestoriMailbox() != null && getCasellaPostaElettronica().getGestoriMailbox().size() > 0) {
					int index = 0;
					while (index < getCasellaPostaElettronica().getGestoriMailbox().size() && !(update || changePwd)) {
						GestoreMailbox gestore = getCasellaPostaElettronica().getGestoriMailbox().get(index);
						if (gestore != null && gestore.getMatricola() != null && gestore.getMatricola().equals(matricola)) {
							changePwd = true;
							if (gestore.getLivello() != null && gestore.getLivello().equals(GestoreMailbox.LIVELLO_TITOLARE))
								update = true;
						}
						index++;
					}
				}
			}
			
			getCasellaPostaElettronica().setUpdateMailbox(update);
			getCasellaPostaElettronica().setChangePassword(changePwd);
		}
	}
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("casellaPostaElettronica", response);
			return "showdoc@casellaPostaElettronica@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public boolean isAdminAcl() {
		return adminAcl;
	}

	public void setAdminAcl(boolean adminAcl) {
		this.adminAcl = adminAcl;
	}

}
