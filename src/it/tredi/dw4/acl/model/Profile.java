package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Profile extends XmlEntity {
	
	private String nome = "";
	private String codice = "";
	private String descrizione = "";
	private List<Diritto> diritti = new ArrayList<Diritto>();
	
	private boolean edited = false; // Indica se i diritti associati al profilo sono stati personalizzati
	
	public Profile() {}
    
	public Profile(String xmlProfilo) throws Exception {
        this.init(XMLUtil.getDOM(xmlProfilo));
    }
    
    @SuppressWarnings("unchecked")
	public Profile init(Document domProfilo) {
    	this.nome = XMLUtil.parseAttribute(domProfilo, "profilo/@nome");
    	this.codice = XMLUtil.parseAttribute(domProfilo, "profilo/@codice");
    	this.descrizione = XMLUtil.parseElementNode(domProfilo, "profilo/descrizione");
    	this.diritti = XMLUtil.parseSetOfElement(domProfilo, "//profilo/diritto", new Diritto());
    	
    	this.edited = false;
    	
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@name", (null != this.nome && !this.edited) ? this.nome : "");
    	params.put(prefix+".@cod", (null != this.codice && !this.edited) ? this.codice : "");
    	return params;
    }
    
    public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getCodice() {
		return codice;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDiritti(List<Diritto> diritti) {
		this.diritti = diritti;
	}

	public List<Diritto> getDiritti() {
		return diritti;
	}
	
	public void setEdited(boolean edited) {
		this.edited = edited;
	}

	public boolean isEdited() {
		return edited;
	}

}
