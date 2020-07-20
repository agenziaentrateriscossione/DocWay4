package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;

import org.dom4j.Document;

public class ChangePassword extends AclQuery {
	private AclQueryFormsAdapter formsAdapter;

	private String xml;
	private String login;
	private String newpassword;
	private String confirmPassword;
	
	// in caso di cambio password a popup il ritorno viene gestito con 
	// un messaggio interno alla maschera di cambio password
	private boolean passwordChanged = false; 
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	public ChangePassword() throws Exception {
		this.formsAdapter = new AclQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.login = domDocumento.getRootElement().attributeValue("pwdLogin");
    }	
	
	public AclQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public String aclConfirmChangePwd() throws Exception{
		if (login == null || "".equals(login.trim())) {
			this.setErrorMessage("templateForm:login", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.login") + "'");
			this.passwordChanged = false;
			return null;
		}
		if (newpassword == null || "".equals(newpassword.trim())) {
			this.setErrorMessage("templateForm:newpassword", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.newpassword") + "'");
			this.passwordChanged = false;
			return null;
		}
		if (confirmPassword == null || "".equals(confirmPassword.trim())) {
			this.setErrorMessage("templateForm:confirmpassword", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.confirmpassword") + "'");
			this.passwordChanged = false;
			return null;
		}
		if (!newpassword.trim().equals(confirmPassword.trim())) {
			this.setErrorMessage("templateForm:confirmpassword", I18N.mrs("acl.errorconfirmpassword")+" "+I18N.mrs("acl.newpassword"));
			this.passwordChanged = false;
			return null;
		}
		try {
			formsAdapter.aclConfirmChangePwd(login, newpassword, confirmPassword);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				this.passwordChanged = false;
				return null;
			}
			formsAdapter.fillFormsFromResponse(response);
			this.init(response.getDocument());
			if(response.getRootElement().attributeValue("dbTable").equals("@pwdchanged")) {
				
				if (this.isPopupPage()) {
					// in caso di popup viene mostrato il messaggio di successo all'interno 
					// della maschera di cambio password
					
					this.passwordChanged = true;
					return null;
				}
				else {
					// se non popup viene caricata la scheda della persona interna
					
					formsAdapter.aclPwdChanged();
					response = formsAdapter.getDefaultForm().executePOST(getUserBean());
					
					if (handleErrorResponse(response)) {
						formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
						return null;
					}
					this.init(response.getDocument());
					ShowdocPersonaInterna showdocPersonaInterna = new ShowdocPersonaInterna();
					showdocPersonaInterna.getFormsAdapter().fillFormsFromResponse(response);
					showdocPersonaInterna.init(response.getDocument());
					showdocPersonaInterna.loadAspects(response.getAttributeValue("/response/@dbTable"), response, "showdoc");
					showdocPersonaInterna.setInfoMessage(I18N.mrs("acl.changepasswordconfirm"));
					setSessionAttribute("showdocPersonaInterna", showdocPersonaInterna);
					
					return "showdoc@persona_interna";
				}
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
	
	public String aclAnnulChangePwd() throws Exception{
		
		try {
			formsAdapter.aclAnnulChangePwd();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			ShowdocPersonaInterna showdocPersonaInterna = new ShowdocPersonaInterna();
			showdocPersonaInterna.getFormsAdapter().fillFormsFromResponse(response);
			showdocPersonaInterna.init(response.getDocument());
			showdocPersonaInterna.loadAspects(response.getAttributeValue("/response/@dbTable"), response, "showdoc");
			setSessionAttribute("showdocPersonaInterna", showdocPersonaInterna);
		
			return "showdoc@persona_interna";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
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
	
	public boolean isPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(boolean passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	@Override
	public String queryPlain() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
