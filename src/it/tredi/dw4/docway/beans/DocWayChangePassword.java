package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Errormsg;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.i18n.I18N;

import org.dom4j.Document;

public class DocWayChangePassword extends DocWayQuery {
	
	private DocDocWayQueryFormsAdapter formsAdapter;

	private String xml = "";
	private String login = "";
	private String password = "";
	private String newpassword = "";
	private String confirmPassword = "";
	
	// in caso di cambio password a popup il ritorno viene gestito con 
	// un messaggio interno alla maschera di cambio password
	private boolean passwordChanged = false;
	
	private boolean visible = false;
	
	public DocWayChangePassword() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document domDocumento) {
    	this.xml 		= domDocumento.asXML();
    	this.login 		= domDocumento.getRootElement().attributeValue("pwdLogin");
    	this.visible 	= true;
    }
	
	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(boolean passwordChanged) {
		this.passwordChanged = passwordChanged;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	/**
	 * Conferma di cambio della password
	 * 
	 * @return
	 * @throws Exception
	 */
	public String confirmChangePwd() throws Exception{
		try {
			if (checkRequiredField()) { 
				this.passwordChanged = false;
				return null;
			}
			
			formsAdapter.aclConfirmChangePwd(login, password,  newpassword, confirmPassword);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		
			if (ErrormsgFormsAdapter.isResponseErrorMessage(response)) {
				Errormsg errormsg = new Errormsg();
				errormsg.setActive(true);
				errormsg.init(response.getDocument());
				
				// viene mostrato il messaggio di errore all'interno del popup di cambio password
				this.setErrorMessage("", errormsg.getErrore().getErrtype().replaceAll("\\<.*?>","")); // eliminazione dei tag html dal messaggio di errore
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				this.passwordChanged = false;
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			this.init(response.getDocument());
			if(response.getRootElement().attributeValue("dbTable").equals("@pwdchanged")) {
				// in caso di popup viene mostrato il messaggio di successo all'interno 
				// della maschera di cambio password
				
				this.passwordChanged = true;
				return null;
			}

		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			this.passwordChanged = false;
			return null;			
		}
		return null;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = false;
	
		if (login == null || login.trim().equals("")) {
			this.setErrorMessage("templateForm:login", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.login") + "'");
			result = true;
		}
		if (password == null || password.trim().equals("")) {
			this.setErrorMessage("templateForm:password", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.current_password") + "'");
			result = true;
		}
		if (newpassword == null || newpassword.trim().equals("")) {
			this.setErrorMessage("templateForm:newpassword", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.newpassword") + "'");
			result = true;
		}
		if (confirmPassword == null || confirmPassword.trim().equals("")) {
			this.setErrorMessage("templateForm:confirmpassword", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.confirmpassword") + "'");
			result = true;
		}
		if (!newpassword.trim().equals(confirmPassword.trim())) {
			this.setErrorMessage("templateForm:confirmpassword", I18N.mrs("acl.errorconfirmpassword")+" "+I18N.mrs("acl.newpassword"));
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Chiusura del popup di cambio password
	 * 
	 * @return
	 * @throws Exception
	 */
	public String closeChangePassword() throws Exception {
		this.visible = false;
		return null;
	}
	
	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
}
