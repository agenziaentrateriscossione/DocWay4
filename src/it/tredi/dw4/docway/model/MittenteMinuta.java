package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class MittenteMinuta extends XmlEntity {
	private String nome_persona;
	private String nome_uff;
    
	public MittenteMinuta() {}
    
	public MittenteMinuta(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public MittenteMinuta init(Document dom) {
    	this.nome_persona = XMLUtil.parseAttribute(dom, "mittente/@nome_persona");
    	this.nome_uff = XMLUtil.parseAttribute(dom, "mittente/@nome_uff");

        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@nome_persona", this.nome_persona);
    	params.put(prefix+".@nome_uff", this.nome_uff);
    	return params;
    }
    
	public void setNome_persona(String codice_fiscale) {
		this.nome_persona = codice_fiscale;
	}

	public String getNome_persona() {
		return nome_persona;
	}

	public void setNome_uff(String partita_iva) {
		this.nome_uff = partita_iva;
	}

	public String getNome_uff() {
		return nome_uff;
	}

}

