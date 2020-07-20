package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Fasc_rpa extends XmlEntity {
	private String numero = "";
	private String num = "";
	private String oggetto = "";
    
	public Fasc_rpa() {}
    
	public Fasc_rpa(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Fasc_rpa init(Document dom) {
    	this.numero = XMLUtil.parseAttribute(dom, "fasc_rpa/@numero");
    	this.num = XMLUtil.parseAttribute(dom, "fasc_rpa/@num");
    	this.oggetto = XMLUtil.parseAttribute(dom, "fasc_rpa/@oggetto");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@numero", this.numero);
    	params.put(prefix+".@num", this.num);
    	params.put(prefix+".@oggetto", this.oggetto);
    	return params;
    }
    
    public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
    
    public String getNum() {
		return num;
	}

	public void setNum(String cod) {
		this.num = cod;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getOggetto() {
		return oggetto;
	}
}

