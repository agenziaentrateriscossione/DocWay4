package it.tredi.dw4.docway.beans;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

import it.tredi.dw4.docway.model.Signer;
import it.tredi.dw4.utils.XMLUtil;

public class VerificaFirma {
	
	private boolean visible = false;
	
	private String fileName = "";
	private String errorMessage = ""; // eventuale messaggio di errore riscontrato nel processo di verifica di firma
	private List<Signer> signers;

	private boolean validityEnabled = false;
	private boolean serialEnabled = false;
	
    public VerificaFirma() {
		this.setVisible(false);
	}
    
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		fileName = XMLUtil.parseStrictAttribute(dom, "/response/verificaFirma/@fileName");
		errorMessage = XMLUtil.parseStrictElement(dom, "/response/verificaFirma/error");
		signers = XMLUtil.parseSetOfElement(dom, "/response/verificaFirma/signer", new Signer());
		
		// abilitazione delle colonne dell'interfaccia di 
		// verifica della firma
		List<?> nodes = dom.selectNodes("/response/verificaFirma/signer/@valid");
		if (nodes != null && nodes.size() > 0)
			this.validityEnabled = true;
		else
			this.validityEnabled = false;
		nodes = dom.selectNodes("/response/verificaFirma/signer/serial");
		if (nodes != null && nodes.size() > 0)
			this.serialEnabled = true;
		else
			this.serialEnabled = false;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<Signer> getSigners() {
		return signers;
	}

	public void setSigners(List<Signer> signers) {
		this.signers = signers;
	}

	public boolean isValidityEnabled() {
		return validityEnabled;
	}

	public void setValidityEnabled(boolean validityEnabled) {
		this.validityEnabled = validityEnabled;
	}

	public boolean isSerialEnabled() {
		return serialEnabled;
	}

	public void setSerialEnabled(boolean serialEnabled) {
		this.serialEnabled = serialEnabled;
	}

	/**
	 * Chiusura del popup di visualizzazione delle informazioni di firma digitale
	 */
	public String close() throws Exception {
		visible = false;
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("docwayVerificaFirma", null);
		return null;
	}
}
