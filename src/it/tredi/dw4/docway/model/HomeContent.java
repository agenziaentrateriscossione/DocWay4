package it.tredi.dw4.docway.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class HomeContent extends XmlEntity {

	private String type = "";
	private String title = "";
	private String selid = "";
	private int count = 0;
	private List<Titolo> titoli = new ArrayList<Titolo>();
	private int numGiorniDocRecenti = 0;
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.type = XMLUtil.parseStrictAttribute(dom, "homeContent/@type");
		this.title = XMLUtil.parseStrictAttribute(dom, "homeContent/@title");
		this.titoli = XMLUtil.parseSetOfElement(dom, "homeContent/titolo", new Titolo());
		this.selid = XMLUtil.parseStrictAttribute(dom, "homeContent/@selid");
		this.count = new Integer(XMLUtil.parseStrictAttribute(dom, "homeContent/@count", "0")).intValue();
		this.numGiorniDocRecenti = new Integer(XMLUtil.parseStrictAttribute(dom, "homeContent/@numGiorni", "0")).intValue();
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSelid() {
		return selid;
	}

	public void setSelid(String selid) {
		this.selid = selid;
	}

	public List<Titolo> getTitoli() {
		return titoli;
	}

	public void setTitoli(List<Titolo> titoli) {
		this.titoli = titoli;
	}

	public int getNumGiorniDocRecenti() {
		return numGiorniDocRecenti;
	}

	public void setNumGiorniDocRecenti(int numGiorniDocRecenti) {
		this.numGiorniDocRecenti = numGiorniDocRecenti;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
