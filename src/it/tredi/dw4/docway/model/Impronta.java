package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Impronta extends XmlEntity {
	private String valore;
	private String tipo;
    
	public Impronta() {}
    
	public Impronta(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Impronta init(Document dom) {
    	this.valore = XMLUtil.parseAttribute(dom, "impronta/@valore");
    	this.tipo = XMLUtil.parseAttribute(dom, "impronta/@tipo");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (this.valore != null && this.valore.length() > 0)
    		params.put(prefix+".@valore", this.valore);
    	if (this.tipo != null && this.tipo.length() > 0)
    		params.put(prefix+".@tipo", this.tipo);
    	return params;
    }
    
    public String getValore() {
		return valore;
	}

	public void setValore(String cod) {
		this.valore = cod;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}
}

