package it.tredi.dw4.soginSAP.beans;

import java.util.Map;

import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@SessionScoped

public class SoginSAPConnector {
	private String keySap;
	private String idDoc;
	private String docSap;
	private String causale;
	private String dataRegistrazione;
	private String utente;
	
	public SoginSAPConnector() {
		this.keySap = null;
		this.idDoc = null;
		this.docSap = null;
		this.causale = null;
		this.dataRegistrazione = null;
		this.utente = null;
	}

	public String getCausale() {
		if (causale == null) {
			String tmpValue = checkPostParameter("CAUSALE");
			if (tmpValue != null)
				this.causale = tmpValue;
		}
		return causale;
	}
	public void setCausale(String causale) {
		this.causale = causale;
		cleanPostParameter("CAUSALE");
	}
	public String getDataRegistrazione() {
		if (dataRegistrazione == null) {
			String tmpValue = checkPostParameter("DATAR");
			if (tmpValue != null)
				this.dataRegistrazione = tmpValue;
		}
		return dataRegistrazione;
	}
	public void setDataRegistrazione(String dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
		cleanPostParameter("DATAR");
	}
	public String getUtente() {
		if (utente == null) {
			String tmpValue = checkPostParameter("UTENTE");
			if (tmpValue != null)
				this.utente = tmpValue;
		}
		return utente;
	}
	public void setUtente(String utente) {
		this.utente = utente;
		cleanPostParameter("UTENTE");
	}
	public String getDocSap() {
		if (docSap == null) {
			String tmpValue = checkPostParameter("DOCSAP");
			if (tmpValue != null)
				this.docSap = tmpValue;
		}
		return docSap;
	}
	public void setDocSap(String docSap) {
		this.docSap = docSap;
		cleanPostParameter("DOCSAP");
	}
	public String getIdDoc() {
		if (idDoc == null) {
			String tmpValue = checkPostParameter("IDDOC");
			if (tmpValue != null)
				this.idDoc = tmpValue;
		}
		return idDoc;
	}
	public void setIdDoc(String idDoc) {
		this.idDoc = idDoc;
		cleanPostParameter("KEYSAP");
	}
	public String getKeySap() {
		if (keySap == null) {
			String tmpValue = checkPostParameter("KEYSAP");
			if (tmpValue != null)
				this.keySap = tmpValue;
		}
		return keySap;
	}
	public void setKeySap(String keySap) {
		this.keySap = keySap;
		cleanPostParameter("KEYSAP");
	}
	
	@SuppressWarnings("unchecked")
	private String checkPostParameter(String key) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		Map<String, String[]> paramsMap = (Map<String, String[]>) session.getAttribute("rewritedUriParams");
		if (paramsMap != null) {
			String[] param = paramsMap.get(key);
			if (param != null) {
				paramsMap.put(key, null);
				return param[0];
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void cleanPostParameter(String key) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		Map<String, String[]> paramsMap = (Map<String, String[]>) session.getAttribute("rewritedUriParams");
		if (paramsMap != null) {
			paramsMap.put(key, null);
		}
	}
}
