package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Aoo extends XmlEntity {
	private String cod_amm;
	private String cod_aoo;
	private String nrecord = ".";
	private String nome;
	private String emailPrincipale;
	
	private List<Modifica> modifiche;
	private Creazione creazione = new Creazione();
	private UltimaModifica ultima_modifica = new UltimaModifica();

    
	public Aoo() {}
    
	public Aoo(String xmlAoo) throws Exception {
        this.init(XMLUtil.getDOM(xmlAoo));
    }
    
    @SuppressWarnings("unchecked")
	public Aoo init(Document domAoo) {
    	this.cod_amm = XMLUtil.parseAttribute(domAoo, "aoo/@cod_amm");
    	this.cod_aoo = XMLUtil.parseAttribute(domAoo, "aoo/@cod_aoo");
    	this.nrecord = XMLUtil.parseAttribute(domAoo, "aoo/@nrecord", ".");
    	this.nome = XMLUtil.parseElement(domAoo, "aoo/nome");
    	this.emailPrincipale = XMLUtil.parseAttribute(domAoo, "aoo/email_principale/@addr");
    	this.modifiche = 			XMLUtil.parseSetOfElement(domAoo, "//aoo/storia/modifica", new Modifica());
    	this.creazione.init(XMLUtil.createDocument(domAoo, "//aoo/storia/creazione"));
    	this.ultima_modifica.init(XMLUtil.createDocument(domAoo, "//aoo/storia/ultima_modifica"));
    	
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.cod_amm ) params.put(prefix+".@cod_amm", (this.cod_amm != null) ? this.cod_amm.trim() : null);
    	if (null != this.cod_aoo ) params.put(prefix+".@cod_aoo", (this.cod_aoo != null) ? this.cod_aoo.trim() : null);
    	if (null != this.nrecord ) params.put(prefix+".@nrecord", this.nrecord);
    	if (null != this.nome ) params.put(prefix+".nome", this.nome);
    	if (null != this.emailPrincipale ) params.put(prefix+".email_principale.@addr", this.emailPrincipale);
    	
    	return params;
    }
    
    public String getCod_amm() {
		return cod_amm;
	}

	public void setCod_amm(String spec) {
		this.cod_amm = spec;
	}

	public void setCod_aoo(String bban) {
		this.cod_aoo = bban;
	}

	public String getCod_aoo() {
		return cod_aoo;
	}

	public void setNome(String name) {
		this.nome = name;
	}

	public String getNome() {
		return nome;
	}

	public void setNrecord(String nrecord) {
		this.nrecord = nrecord;
	}

	public String getNrecord() {
		return nrecord;
	}
	
	public String getEmailPrincipale() {
		return emailPrincipale;
	}

	public void setEmailPrincipale(String emailPrincipale) {
		this.emailPrincipale = emailPrincipale;
	}

	public void setModifiche(List<Modifica> modifiche) {
		this.modifiche = modifiche;
	}

	public List<Modifica> getModifiche() {
		return modifiche;
	}

	public void setCreazione(Creazione creazione) {
		this.creazione = creazione;
	}

	public Creazione getCreazione() {
		return creazione;
	}

	public void setUltima_modifica(UltimaModifica ultima_modifica) {
		this.ultima_modifica = ultima_modifica;
	}

	public UltimaModifica getUltima_modifica() {
		return ultima_modifica;
	}
	
}

