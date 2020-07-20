package it.tredi.dw4.docwayproc.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.ClassifUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Classif extends XmlEntity {
	private String cod = "";
	private String text = "";
	
	// Testo con primo codice in romano
	private String text_ro = "";
	
	// Filtro da impostare sul campo codice in fase di ricerca su classificazione (classificazione da codice)
	private String filtroCod = "";
    
	public Classif() {}
    
	public Classif(String xmlDiritto) throws Exception {
        this.init(XMLUtil.getDOM(xmlDiritto));
    }
    
    public Classif init(Document domDiritto) {
    	this.cod = XMLUtil.parseAttribute(domDiritto, "classif/@cod");
    	this.text = XMLUtil.parseElement(domDiritto, "classif");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod", this.cod);
    	params.put(prefix, this.text);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public void setText(String value) {
		this.text = value;
	}

	public String getText() {
		return text;
	}
	
	public void setText_ro(String value) {
		this.text_ro = value;
	}

	public String getText_ro() {
		if (text_ro != null && text_ro.length() > 0)
			return text_ro;
		else {
			try {
				String label = getText();
				if (label != null && label.length() > 0)
					return ClassifUtil.formatClassif(label);
				else
					return "";
			}
			catch (Exception e) { return ""; }
		}
	}
	
	public String getFiltroCod() {
		if (filtroCod == null)
			filtroCod = "";
		
		return filtroCod;
	}
	
	public void setFiltroCod(String cod) {
		this.filtroCod = cod;
	}
	
}

