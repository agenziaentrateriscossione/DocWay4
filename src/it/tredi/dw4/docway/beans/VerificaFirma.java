package it.tredi.dw4.docway.beans;

import it.tredi.dw4.docway.model.Signer;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.Element;

public class VerificaFirma {
	private Boolean visible;
	
	private String fileName;
	private List<Signer> signers;
	
    public VerificaFirma() {
		this.setVisible(false);
		this.signers = new ArrayList<Signer>();
	}
    
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		Element root = (Element) dom.selectSingleNode("//verificaFirma");
		if (root != null) {
			this.fileName = root.selectSingleNode("./fileName").getText();
			
			List<Element> signers = root.selectNodes("//signer");
			for (Element signerEl : signers) {
				Signer signer = new Signer();
				signer.init(signerEl);
				this.signers.add(signer);
			}
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public List<Signer> getSigners() {
		return signers;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	
	public String close() throws Exception {
		visible = false;
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("docwayVerificaFirma", null);
		return null;
	}
}
