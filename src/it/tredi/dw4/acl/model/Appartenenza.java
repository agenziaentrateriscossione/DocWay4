package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Appartenenza extends XmlEntity {

	private String cod_uff;
	private String nomeufficio;
	private String qualifica;
	private String cod_amm;
	private String cod_aoo;
	private String nome;
	private String physDoc;
    
	public Appartenenza() {}
    
	public Appartenenza(String xmlAppartenenza) throws Exception {
        this.init(XMLUtil.getDOM(xmlAppartenenza));
    }
    
    public Appartenenza init(Document domAppartenenza) {
    	this.cod_uff = XMLUtil.parseAttribute(domAppartenenza, "appartenenza/@cod_uff");
    	this.nomeufficio = XMLUtil.parseAttribute(domAppartenenza, "appartenenza/@nomeufficio");
    	this.nome = XMLUtil.parseAttribute(domAppartenenza, "appartenenza/@nome");
    	this.qualifica = XMLUtil.parseAttribute(domAppartenenza, "appartenenza/@qualifica");
    	this.cod_amm = XMLUtil.parseAttribute(domAppartenenza, "appartenenza/@cod_amm");
    	this.cod_aoo = XMLUtil.parseAttribute(domAppartenenza, "appartenenza/@cod_aoo");
    	this.physDoc = XMLUtil.parseAttribute(domAppartenenza, "appartenenza/@physDoc");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.cod_uff )  params.put(prefix+".@cod_uff", this.cod_uff);
    	if (null != this.qualifica) params.put(prefix+".@qualifica", this.qualifica);
    	return params;
    }
    
    public String getCod_uff() {
		return cod_uff;
	}

	public void setCod_uff(String nome) {
		this.cod_uff = nome;
	}

	public void setNomeufficio(String nome_uff) {
		this.nomeufficio = nome_uff;
	}

	public String getNomeufficio() {
		return nomeufficio;
	}

	public void setQualifica(String qualifica) {
		this.qualifica = qualifica;
	}

	public String getQualifica() {
		return qualifica;
	}

	public void setCod_amm(String cod_amm) {
		this.cod_amm = cod_amm;
	}

	public String getCod_amm() {
		return cod_amm;
	}

	public void setCod_aoo(String cod_aoo) {
		this.cod_aoo = cod_aoo;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setPhysDoc(String physDoc) {
		this.physDoc = physDoc;
	}

	public String getPhysDoc() {
		return physDoc;
	}
}

