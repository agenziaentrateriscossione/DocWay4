package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class MezzoTrasmissione extends XmlEntity {
	private String cod = "";
	private String value = ""; // Utilizzato dalla procedura di riempimento select in docEdit (di norma e' lo stesso valore di cod)
	private String costo = "";
	private String valuta = "";
    
	public MezzoTrasmissione() {}
    
	public MezzoTrasmissione(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public MezzoTrasmissione init(Document dom) {
    	this.cod = XMLUtil.parseAttribute(dom, "mezzo_trasmissione/@cod");
    	this.costo = XMLUtil.parseAttribute(dom, "mezzo_trasmissione/@costo");
    	this.valuta = XMLUtil.parseAttribute(dom, "mezzo_trasmissione/@valuta", "euro"); // valore default: euro
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod", this.cod);
    	params.put(prefix+".@costo", this.costo);
    	if (this.costo != null && !this.costo.equals(""))
    		params.put(prefix+".@valuta", this.valuta);
    	else
    		params.put(prefix+".@valuta", "");
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}
	
	public String getValue() {
		if ((value == null || value.equals("")) && cod != null && !cod.equals(""))
			return getCod();
		else
			return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getCosto() {
		return costo;
	}

	public void setCosto(String costo) {
		this.costo = costo;
	}
	
	public String getValuta() {
		return valuta;
	}

	public void setValuta(String valuta) {
		this.valuta = valuta;
	}
	
}

