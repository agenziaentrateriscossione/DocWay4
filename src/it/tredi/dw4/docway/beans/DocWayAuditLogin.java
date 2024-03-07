package it.tredi.dw4.docway.beans;

import java.net.URLEncoder;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.beans.Query;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Pagina di login alla console di audit tramite token temporaneo di autenticazione
 */
public class DocWayAuditLogin extends Query {
	
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private String authToken = null;
	private String url = null;

	public DocWayAuditLogin() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		this.url = XMLUtil.parseStrictAttribute(dom, "/response/auditConsole/@url", "");
		this.authToken = XMLUtil.parseStrictAttribute(dom, "/response/auditConsole/@token", "");
	}
	
	@Override
	public QueryFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}
	
	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getAuthToken() {
		return authToken;
	}
	
	public String getUrlEncodedToken() {
		try {
			return (authToken != null) ? URLEncoder.encode(authToken, "UTF-8") : "";
		}
		catch (Exception e) {
			Logger.error("DocWayAuditLogin.getUrlEncodedToken(): got exception... " + e.getMessage(), e);
			return authToken;
		}
	}

}
