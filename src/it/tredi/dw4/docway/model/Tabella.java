package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Tabella extends XmlEntity {
	private String tipo = "";
	private String vis = "";
	private String text = "";
    
	private boolean selected = false; // true se la tabella e' selezionata (form di ricerca avanzata, selezione dei repertori sui quali eseguire la ricerca)
	
	public Tabella() {}
    
	public Tabella(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public Tabella init(Document dom) {
    	this.tipo = XMLUtil.parseAttribute(dom, "tabella/@tipo");
    	this.vis = XMLUtil.parseAttribute(dom, "tabella/@vis");
    	this.text = XMLUtil.parseElement(dom, "tabella");
    	
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	return params;
    }
    
    public String getTipo() {
		return tipo;
	}

	public void setTipo(String cod) {
		this.tipo = cod;
	}
	
	public String getVis() {
		return vis;
	}

	public void setVis(String value) {
		this.vis = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setSelected(boolean select) {
		this.selected = select;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public String getTipoName() {
		if ( "A".equals(tipo) ) return "arrivo" ;
        else if ( "I".equals(tipo) ) return "interno" ;
        else if ( "P".equals(tipo) ) return "partenza" ;
        else if ( "V".equals(tipo) ) return "varie" ;
		
		return "";
	}
}

