package it.tredi.dw4.model.comparison;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

public class Comparison extends XmlEntity {

	private String description1 = "";
	private String description2 = "";
	private List<Difference> differences = new ArrayList<Difference>();
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.description1 = XMLUtil.parseStrictElement(dom, "/response/comparison/description1", true);
    	this.description2 = XMLUtil.parseStrictElement(dom, "/response/comparison/description2", true);
    	this.differences = XMLUtil.parseSetOfElement(dom, "/response/comparison/difference", new Difference());
    	
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getDescription1() {
		return description1;
	}

	public void setDescription1(String description1) {
		this.description1 = description1;
	}

	public String getDescription2() {
		return description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2;
	}

	public List<Difference> getDifferences() {
		return differences;
	}

	public void setDifferences(List<Difference> differences) {
		this.differences = differences;
	}

}
