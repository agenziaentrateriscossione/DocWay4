package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;


public class Errormsg extends Page {

	private ErrormsgFormsAdapter formsAdapter;
	private Errore errore;
	protected boolean active = false;
	protected boolean errorEmailSent = false;
	protected boolean errorEmailSentOK = false;

	private String xml = "";
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}		
	
    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}	
	
	public Errormsg() throws Exception {
//FIXME - non dovrebbe essere il service di ACL		
		this.formsAdapter = new ErrormsgFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
		errorEmailSent = false;
		errorEmailSentOK = false;		
	}	
	
	public void init(Document domError) {		
    	xml = domError.asXML();
    	this.errore = new Errore();
    	this.errore.init(XMLUtil.createDocument(domError, "//errore"));
    }
	
	public String close() {
		this.active = false;
		return null;
	}

	public void setErrore(Errore errore) {
		this.errore = errore;
	}

	public Errore getErrore() {
		return errore;
	}	
	
	//variabili da considerare
	//unexpected=true deve mostrare messaggio hardcoded in template (come in vecchio docway) e permettere l'invio del messaggio di errore per email (il dettaglio deve essere
	//    visibile solamente a richiesta)
	// unexpected = false si tratta di un messaggio gestito e mostrare messaggio e dettaglio sempre aperti
	//level - deve essere utilizzato per icona e evidenziazione gravità dell'accaduto
	//se level=FATAL impedire la chiusura del popup e mostrare messaggio che dice che l'errore non permette di proseguire e che occorre contattare l'amministratore

	public boolean isErrorEmailSent() {
		return errorEmailSent;
	}

	public boolean isErrorEmailSentOK() {
		return errorEmailSentOK;
	}

	public String sendErrorEmail() throws Exception {
		try {
			errorEmailSent = true;

			String emailDetail = errore.getEmailDetail();
			formsAdapter.sendErrorEmail(emailDetail);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			
			if (response.isXPathFound("/response/errore") || response.testAttributeValue("/response/@verbo", "scelta_login"))
				errorEmailSentOK = false;
			else
				errorEmailSentOK = true;
		}
		catch (Throwable t) {
			errorEmailSentOK = false;
		}
		return null;
	}

	@Override
	public FormsAdapter getFormsAdapter() {
		return null;
	}
	
}
