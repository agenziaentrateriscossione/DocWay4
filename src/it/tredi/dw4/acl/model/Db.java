package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Db extends XmlEntity {
	private String nome;
	private String cod;
	private List<Group> groups;
	private boolean opened = false;
	
	public Db() {}
    
	public Db(String xmlRight) throws Exception {
        this.init(XMLUtil.getDOM(xmlRight));
    }
    
    @SuppressWarnings("unchecked")
	public Db init(Document domRight) {
    	this.nome 		= XMLUtil.parseAttribute(domRight, "db/@nome");
    	this.cod 		= XMLUtil.parseAttribute(domRight, "db/@cod");
    	this.groups 	= XMLUtil.parseSetOfElement(domRight, "//db/group", new Group());
    	return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@nome", this.nome);
    	params.put(prefix + ".@cod", this.cod);
    	return params;
    }

    public void setNome(String bban) {
		this.nome = bban;
	}

	public String getNome() {
		return nome;
	}

	public void setCod(String type) {
		this.cod = type;
	}

	public String getCod() {
		return cod;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Group> getGroups() {
		return groups;
	}

	
	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public boolean isOpened() {
		return opened;
	}
	
	public String changeOpened(){
		opened = ! opened;
		return null;
	}
}

