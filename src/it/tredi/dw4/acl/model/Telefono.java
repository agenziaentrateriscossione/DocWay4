package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Telefono extends XmlEntity {
	private String num;
	private String tipo;
    
	public Telefono() {}
    
	public Telefono(String xmlTelefono) throws Exception {
        this.init(XMLUtil.getDOM(xmlTelefono));
    }
    
    public Telefono init(Document domTelefono) {
    	this.num = XMLUtil.parseAttribute(domTelefono, "telefono/@num");
    	this.tipo = XMLUtil.parseAttribute(domTelefono, "telefono/@tipo");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@num", this.num);
    	params.put(prefix+".@tipo", this.tipo);
    	return params;
    }

    public String getNum() {
		return num;
	}

	public void setNum(String nome) {
		this.num = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
