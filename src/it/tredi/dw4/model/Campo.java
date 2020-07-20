package it.tredi.dw4.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Campo extends XmlEntity {
	private String nome = "";
	private String text = "";
    
	public Campo() {}
    
	public Campo(String xmlCampo) throws Exception {
        this.init(XMLUtil.getDOM(xmlCampo));
    }
    
    public Campo init(Document domCampo) {
    	this.nome = XMLUtil.parseAttribute(domCampo, "campo/@nome");
    	this.text = XMLUtil.parseElement(domCampo, "campo");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.nome ) params.put(prefix+".@nome", this.nome);
    	if (null != this.text ) params.put(prefix, this.text);
    	return params;
    }
    
    public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}

