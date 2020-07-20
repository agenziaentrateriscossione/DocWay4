package it.tredi.dw4.docway.beans;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.model.VersioneProdotto;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

public class QueryVersioni extends DocWayQuery {

	private String xml = "";
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private String appName = "";
	
	private boolean visible = false;
	private List<VersioneProdotto> versions = new ArrayList<VersioneProdotto>();
		
	public QueryVersioni() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public QueryVersioni(String appName) throws Exception {
		this();
		this.appName = appName;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		this.xml 		= dom.asXML();
		this.versions 	= XMLUtil.parseSetOfElement(dom, "versions/version", new VersioneProdotto());
		this.visible 	= true;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public List<VersioneProdotto> getVersions() {
		return versions;
	}

	public void setVersions(List<VersioneProdotto> versions) {
		this.versions = versions;
	}

	@Override
	public QueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	/**
	 * chiusura del popup di visualizzazione delle versioni
	 * 
	 * @return
	 * @throws Exception
	 */
	public String closeVersioni() throws Exception {
		visible = false;
		return null;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

}
