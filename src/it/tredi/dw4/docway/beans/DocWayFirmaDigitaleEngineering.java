package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocumentFormsAdapter;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.model.FirmaEngineeringFile;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocWayFirmaDigitaleEngineering extends Showdoc {
	
	private DocumentFormsAdapter formsAdapter;
	private String xml = "";
	
	private String user;
	private String nRecord;
	private String url;
	private String urlRiconsegna;
	private String tipoRiconsegna;
	private List<FirmaEngineeringFile> files = new ArrayList<FirmaEngineeringFile>();
	
    public DocWayFirmaDigitaleEngineering() throws Exception {
    	this.formsAdapter 	= new DocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
    
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		xml = dom.asXML();
		
		nRecord = XMLUtil.parseStrictAttribute(dom, "/response/firmaEng/nrecord");
		user = XMLUtil.parseStrictAttribute(dom, "/response/firmaEng/user");
		url = XMLUtil.parseStrictAttribute(dom, "/response/firmaEng/url");
		urlRiconsegna = XMLUtil.parseStrictAttribute(dom, "/response/firmaEng/urlRiconsegna");
		tipoRiconsegna = XMLUtil.parseStrictAttribute(dom, "/response/firmaEng/tipoRiconsegna");
		
		files = XMLUtil.parseSetOfElement(dom, "/response/firmaEng/file", new FirmaEngineeringFile());
	}
	
	public DocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	@Override
	public String paginaTitoli() throws Exception {
		return null;
	}

	@Override
	public void reload() throws Exception {
	}

	@Override
	public String modifyTableDoc() throws Exception {
		return null;
	}

	public String getUser() {
		return user;
	}

	public String getnRecord() {
		return nRecord;
	}

	public String getUrl() {
		return url;
	}

	public String getUrlRiconsegna() {
		return urlRiconsegna;
	}

	public String getTipoRiconsegna() {
		return tipoRiconsegna;
	}

	public List<FirmaEngineeringFile> getFiles() {
		return files;
	}

	
}
