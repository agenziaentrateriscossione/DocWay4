package it.tredi.dw4.soginSAP.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class SAPDoc extends Doc {
	protected String causale;
	protected String idDoc;
	protected String docSap;
	protected String dataRegistrazione;
	protected String utente;
	protected String stato;
	private String fileDownloadUrl;
	
	List<SAPStoria> storiaSAP;
	
	public String getFileDownloadUrl() {
		return fileDownloadUrl;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getCausale() {
		return causale;
	}

	public void setCausale(String causale) {
		this.causale = causale;
	}

	public String getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(String idDoc) {
		this.idDoc = idDoc;
	}

	public String getDocSap() {
		return docSap;
	}

	public void setDocSap(String docSap) {
		this.docSap = docSap;
	}

	public String getDataRegistrazione() {
		return dataRegistrazione;
	}

	public void setDataRegistrazione(String dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}

	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}
	
	public List<SAPStoria> getStoriaSAP() {
		return this.storiaSAP;
	}

	public SAPDoc() {
		ExternalContext extctx = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) extctx.getSession(false);
		UserBean userBean = (UserBean) session.getAttribute("userBean");
		
		String userLogin = userBean.getLogin();
		 //se utente per sbaglio mette / o \ in coda web-server lo accetta --> occorre toglierlo
        if (userLogin != null && (userLogin.endsWith("\\") || userLogin.endsWith("/"))) {
        	userLogin = userLogin.substring(0, userLogin.length() - 1);
        }
        //elimino eventuale nome del dominio (indicato sia con / che con \)
        if (userLogin != null && userLogin.indexOf("\\") != -1) {
        	userLogin = userLogin.substring(userLogin.lastIndexOf("\\") + 1);
        }
        if (userLogin != null && userLogin.indexOf("/") != -1) {
        	userLogin = userLogin.substring(userLogin.lastIndexOf("/") + 1);
        }
		
		fileDownloadUrl = extctx.getRequestServerName() + ":" + extctx.getRequestServerPort() + extctx.getRequestContextPath()
						  + "/filedownload?login=" + userLogin + "&matricola=" + userBean.getMatricola() + "&name=";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom, String tipo) {
		super.init(dom, tipo);
		String xpath = "/response/doc";
		
		this.causale = XMLUtil.parseStrictAttribute(dom, xpath + "/extra/soginSAP/causale");
		this.idDoc = XMLUtil.parseStrictAttribute(dom, xpath + "/@nrecord");
		this.docSap = XMLUtil.parseStrictAttribute(dom, xpath + "/extra/soginSAP/docSAP");
		this.dataRegistrazione = XMLUtil.parseStrictAttribute(dom, xpath + "/extra/soginSAP/dataRegistrazione");
		this.utente = XMLUtil.parseStrictAttribute(dom, xpath + "/extra/soginSAP/utente");
		this.stato = XMLUtil.parseStrictAttribute(dom, xpath + "/extra/soginSAP/@stato");
		
		this.storiaSAP = XMLUtil.parseSetOfElement(dom, xpath+"/extra/storiaSAP/modifica", new SAPStoria());
		if (this.storiaSAP == null) {
			this.storiaSAP = new ArrayList<SAPStoria>();
		}
		
		return null;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return this.asFormAdapterParams(prefix, false);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify) {
		return this.asFormAdapterParams(prefix, modify, false);
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix, boolean modify, boolean isRepertorio) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = super.asFormAdapterParams(prefix, modify, isRepertorio);
    	
    	for (int i = 0; i < storiaSAP.size(); i++) {
    		SAPStoria sapStoria = this.storiaSAP.get(i); 
    		params.putAll(sapStoria.asFormAdapterParams(".extra.storiaSAP.modifica[" + i + "]"));
    	}
    	
    	return params;
	}
}
