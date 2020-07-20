package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Fasc extends XmlEntity {
	private String cod;
	private String oggetto;
	private String cod_amm_aoo;
	private String cod_persona;
	private String cod_uff;
	private String nome_persona;
	private String nome_uff;
	private String dir_intervento;
    
	public Fasc() {}
    
	public Fasc(String xmlDiritto) throws Exception {
        this.init(XMLUtil.getDOM(xmlDiritto));
    }
    
    public Fasc init(Document domDiritto) {
    	this.cod = 			XMLUtil.parseAttribute(domDiritto, "fasc/@cod");
    	this.oggetto = 		XMLUtil.parseAttribute(domDiritto, "fasc/@oggetto");
    	this.cod_amm_aoo = 	XMLUtil.parseAttribute(domDiritto, "fasc/@cod_amm_aoo");
    	this.cod_persona = 	XMLUtil.parseAttribute(domDiritto, "fasc/@cod_persona");
    	this.cod_uff = 		XMLUtil.parseAttribute(domDiritto, "fasc/@cod_uff");
    	this.nome_persona = XMLUtil.parseAttribute(domDiritto, "fasc/@nome_persona");
    	this.nome_uff = 	XMLUtil.parseAttribute(domDiritto, "fasc/@nome_uff");
    	this.dir_intervento=XMLUtil.parseAttribute(domDiritto, "fasc/@dir_intervento");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod", this.cod);
    	params.put(prefix+".@oggetto", this.oggetto);
    	params.put(prefix+".@cod_amm_aoo", this.cod_amm_aoo);
    	params.put(prefix+".@cod_persona", this.cod_persona);
    	params.put(prefix+".@cod_uff", this.cod_uff);
    	params.put(prefix+".@nome_persona", this.nome_persona);
    	params.put(prefix+".@nome_uff", this.nome_uff);
    	params.put(prefix+".@dir_intervento", this.dir_intervento);
    	return params;
    }
    
    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public void setOggetto(String value) {
		this.oggetto = value;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setCod_amm_aoo(String cod_amm_aoo) {
		this.cod_amm_aoo = cod_amm_aoo;
	}

	public String getCod_amm_aoo() {
		return cod_amm_aoo;
	}

	public void setCod_persona(String cod_persona) {
		this.cod_persona = cod_persona;
	}

	public String getCod_persona() {
		return cod_persona;
	}

	public void setCod_uff(String cod_uff) {
		this.cod_uff = cod_uff;
	}

	public String getCod_uff() {
		return cod_uff;
	}

	public void setNome_uff(String nome_uff) {
		this.nome_uff = nome_uff;
	}

	public String getNome_uff() {
		return nome_uff;
	}

	public void setNome_persona(String nome_persona) {
		this.nome_persona = nome_persona;
	}

	public String getNome_persona() {
		return nome_persona;
	}

	public void setDir_intervento(String dir_intervento) {
		this.dir_intervento = dir_intervento;
	}

	public String getDir_intervento() {
		return dir_intervento;
	}
}

