package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Responsabilita extends XmlEntity {
	private String nome;
	private String physDoc;
    
	public Responsabilita() {}
    
	public Responsabilita(String xmlLogin) throws Exception {
        this.init(XMLUtil.getDOM(xmlLogin));
    }
    
    public Responsabilita init(Document domResponsabilita) {
		this.nome = XMLUtil.parseAttribute(domResponsabilita, "responsabilita/@nome");
		this.physDoc = XMLUtil.parseAttribute(domResponsabilita, "responsabilita/@physDoc");
        return this;
    }
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@nome", this.nome);
    	return params;
    }
    public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setPhysDoc(String physDoc) {
		this.physDoc = physDoc;
	}

	public String getPhysDoc() {
		return physDoc;
	}
}

