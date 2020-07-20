package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class GruppoAppartenenza extends XmlEntity {
	private String cod;
	private String nome;
    
	public GruppoAppartenenza() {}
    
	public GruppoAppartenenza(String xmlGruppoAppartenenza) throws Exception {
        this.init(XMLUtil.getDOM(xmlGruppoAppartenenza));
    }
    
    public GruppoAppartenenza init(Document domGruppoAppartenenza) {
    	this.cod = XMLUtil.parseAttribute(domGruppoAppartenenza, "gruppo_appartenenza/@cod");
    	this.nome = XMLUtil.parseAttribute(domGruppoAppartenenza, "gruppo_appartenenza/@nome");
        return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@cod", this.cod);
    	params.put(prefix + ".@nome", this.nome);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
