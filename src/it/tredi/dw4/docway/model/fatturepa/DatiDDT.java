package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DatiDDT extends XmlEntity {
	
	private String numeroDDT = "";
	private String dataDDT = "";

	@Override
	public XmlEntity init(Document dom) {
		this.numeroDDT = XMLUtil.parseStrictAttribute(dom, "datiDDT/@numeroDDT");
		this.dataDDT = XMLUtil.parseStrictAttribute(dom, "datiDDT/@dataDDT");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.numeroDDT != null && this.numeroDDT.length() > 0)
    		params.put(prefix+".@numeroDDT", this.numeroDDT);
    	if (this.dataDDT != null && this.dataDDT.length() > 0)
    		params.put(prefix+".@dataDDT", this.dataDDT);
    	
    	return params;
	}
	
	public String getNumeroDDT() {
		return numeroDDT;
	}

	public void setNumeroDDT(String numeroDDT) {
		this.numeroDDT = numeroDDT;
	}

	public String getDataDDT() {
		return dataDDT;
	}

	public void setDataDDT(String dataDDT) {
		this.dataDDT = dataDDT;
	}

}
