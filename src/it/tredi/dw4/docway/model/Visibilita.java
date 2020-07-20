package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Visibilita extends XmlEntity {
	private String tipo = "";
	private String fino_al = "";
    
	public Visibilita() {}
    
	public Visibilita(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Visibilita init(Document dom) {
    	this.tipo = XMLUtil.parseAttribute(dom, "visibilita/@tipo");
    	this.fino_al = XMLUtil.parseAttribute(dom, "visibilita/@fino_al");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	
    	if (this.tipo != null && this.tipo.equals("Pubblico")) this.tipo = ""; // Svuoto il campo tipo in caso di valore = 'Pubblico'
    	if (!this.tipo.equals("Riservato")) this.fino_al = ""; // Inserisco la data 'fino_al' solo se il tipo e' 'Riservato'
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@tipo", this.tipo);
    	params.put(prefix+".@fino_al", this.fino_al);
    	return params;
    }
    
    public String getTipo() {
    	if (tipo == null || tipo.length() == 0)
    		tipo = "Pubblico"; // Se lascio il tipo vuoto ottengo un errore nel template JSF (motivo?)
		return tipo;
	}

	public void setTipo(String cod) {
		this.tipo = cod;
	}

	public void setFino_al(String fino_al) {
		this.fino_al = fino_al;
	}

	public String getFino_al() {
		return fino_al;
	}
}

