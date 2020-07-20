package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Registro_emergenza extends XmlEntity {
	private String data_regem;
	private String num_regem;
	private String anno_regem;
    
	public Registro_emergenza() {}
    
	public Registro_emergenza(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Registro_emergenza init(Document dom) {
    	this.data_regem = XMLUtil.parseAttribute(dom, "registro_emergenza/@data_regem");
    	this.num_regem  = XMLUtil.parseAttribute(dom, "registro_emergenza/@num_regem");
    	this.anno_regem = XMLUtil.parseAttribute(dom, "registro_emergenza/@anno_regem");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@data_regem", this.data_regem);
    	params.put(prefix+".@num_regem", this.num_regem);
    	params.put(prefix+".@anno_regem", this.anno_regem);
    	return params;
    }
    
    public String getData_regem() {
		return data_regem;
	}

	public void setData_regem(String cod) {
		this.data_regem = cod;
	}

	public void setNum_regem(String num_regem) {
		this.num_regem = num_regem;
	}

	public String getNum_regem() {
		return num_regem;
	}

	public void setAnno_regem(String anno_regem) {
		this.anno_regem = anno_regem;
	}

	public String getAnno_regem() {
		return anno_regem;
	}
}

