package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class GerarchiaFascicolo extends XmlEntity {
	private String numero;
	private String oggetto;
	private String livello;
	private String haFratelli;
	private String selezionato;
	private String ricerca;
	private String stato;
    
	public GerarchiaFascicolo() {}
    
	public GerarchiaFascicolo(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public GerarchiaFascicolo init(Document dom) {
    	this.numero = XMLUtil.parseAttribute(dom, "gerarchiaFascicolo/@numero");
    	this.oggetto = XMLUtil.parseAttribute(dom, "gerarchiaFascicolo/@oggetto");
    	this.livello = XMLUtil.parseAttribute(dom, "gerarchiaFascicolo/@livello");
    	this.haFratelli = XMLUtil.parseAttribute(dom, "gerarchiaFascicolo/@haFratelli");
    	this.selezionato = XMLUtil.parseAttribute(dom, "gerarchiaFascicolo/@selezionato");
    	this.ricerca = XMLUtil.parseAttribute(dom, "gerarchiaFascicolo/@ricerca");
    	this.stato = XMLUtil.parseAttribute(dom, "gerarchiaFascicolo/@stato");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	return params;
    }
    
    public String getNumero() {
		return numero;
	}

	public void setNumero(String cod) {
		this.numero = cod;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setLivello(String livello) {
		this.livello = livello;
	}

	public String getLivello() {
		return livello;
	}

	public void setSelezionato(String selezionato) {
		this.selezionato = selezionato;
	}

	public String getSelezionato() {
		return selezionato;
	}

	public void setHaFratelli(String haFratelli) {
		this.haFratelli = haFratelli;
	}

	public String getHaFratelli() {
		return haFratelli;
	}

	public void setRicerca(String ricerca) {
		this.ricerca = ricerca;
	}

	public String getRicerca() {
		return ricerca;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getStato() {
		return stato;
	}
}

