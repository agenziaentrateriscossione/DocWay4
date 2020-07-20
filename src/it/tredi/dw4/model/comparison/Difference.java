package it.tredi.dw4.model.comparison;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class Difference extends XmlEntity {

	private String cod = "";
	private String label = "";
	private String type = "";
	private boolean alfa = false;
	
	private String value1 = "";
	private String value2 = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.cod = XMLUtil.parseStrictAttribute(dom, "/difference/@cod", "");
    	this.label = XMLUtil.parseStrictAttribute(dom, "/difference/@label", "");
    	this.type = XMLUtil.parseStrictAttribute(dom, "/difference/@type", "");
    	this.alfa = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/difference/@alfa", "false"));
    	this.value1 = XMLUtil.parseStrictElement(dom, "/difference/value1", true);
    	this.value2 = XMLUtil.parseStrictElement(dom, "/difference/value2", true);
    	
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAlfa() {
		return alfa;
	}

	public void setAlfa(boolean alfa) {
		this.alfa = alfa;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

}


/**


<difference type="edited" cod="ACL-9" alfa="false" label="Access Control List/Persone esterne/Cancellazione">
<value1><![CDATA[FALSE]]></value1><value2><![CDATA[TRUE]]></value2></difference>

*/