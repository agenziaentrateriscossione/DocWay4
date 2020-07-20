package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Scadenza extends XmlEntity {
	private String tipo = "";
	private String data_scadenza = "";
	private String data_ultima_revisione = "";
    
	public Scadenza() {}
    
	public Scadenza(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Scadenza init(Document dom) {
    	this.tipo = XMLUtil.parseAttribute(dom, "scadenza/@tipo");
    	this.data_scadenza = XMLUtil.parseAttribute(dom, "scadenza/@data_scadenza");
    	this.data_ultima_revisione = XMLUtil.parseAttribute(dom, "scadenza/@data_ultima_revisione");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@tipo", this.tipo);
    	params.put(prefix+".@data_scadenza", this.data_scadenza);
    	params.put(prefix+".@data_ultima_revisione", this.data_ultima_revisione);
    	return params;
    }
    
    public String getTipo() {
		return tipo;
	}

	public void setTipo(String cod) {
		this.tipo = cod;
	}

	public void setData_scadenza(String data_scadenza) {
		this.data_scadenza = data_scadenza;
	}

	public String getData_scadenza() {
		return data_scadenza;
	}
	
	public String getData_ultima_revisione() {
		return data_ultima_revisione;
	}

	public void setData_ultima_revisione(String data_ultima_revisione) {
		this.data_ultima_revisione = data_ultima_revisione;
	}
	
}

