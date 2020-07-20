package it.tredi.dw4.model;

import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class VersioneProdotto extends XmlEntity {

	private String version_num = "";
	private String version_date = "";
	
	private List<FunzioneProdotto> news = new ArrayList<FunzioneProdotto>();
	private List<FunzioneProdotto> fixes = new ArrayList<FunzioneProdotto>();
	
	public String getVersion_num() {
		return version_num;
	}
	
	public void setVersion_num(String version_num) {
		this.version_num = version_num;
	}
	
	public String getVersion_date() {
		return version_date;
	}
	
	public void setVersion_date(String version_date) {
		this.version_date = version_date;
	}
	
	public List<FunzioneProdotto> getNews() {
		return news;
	}
	
	public void setNews(List<FunzioneProdotto> news) {
		this.news = news;
	}
	
	public List<FunzioneProdotto> getFixes() {
		return fixes;
	}
	
	public void setFixes(List<FunzioneProdotto> fixes) {
		this.fixes = fixes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.version_num 	= XMLUtil.parseStrictAttribute(dom, "version/@num");
		this.version_date 	= XMLUtil.parseStrictAttribute(dom, "version/@data");
		this.news 			= XMLUtil.parseSetOfElement(dom, "version/news/new", new FunzioneProdotto());
		this.fixes 			= XMLUtil.parseSetOfElement(dom, "version/fixes/fix", new FunzioneProdotto());
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
}
