package it.tredi.dw4.docway.model.fatturepa;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Map;

import org.dom4j.Document;

public class Notifica extends XmlEntity {
	
	private String numeroFattura = "";
	private String annoFattura = "";
	private String name = "";
	private String title = "";
	private String tipo = "";
	private String data = "";
	private String ora = "";
	private String esito = "";
	private String info = "";
	private String riferita = "";
	private String note = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.numeroFattura 	= XMLUtil.parseStrictAttribute(dom, "notifica/@numeroFattura");
		this.annoFattura 	= XMLUtil.parseStrictAttribute(dom, "notifica/@annoFattura");
		this.name 			= XMLUtil.parseStrictAttribute(dom, "notifica/@name");
		this.title 			= XMLUtil.parseStrictAttribute(dom, "notifica/@title");
		this.tipo 			= XMLUtil.parseStrictAttribute(dom, "notifica/@tipo");
		this.data 			= XMLUtil.parseStrictAttribute(dom, "notifica/@data");
		this.ora 			= XMLUtil.parseStrictAttribute(dom, "notifica/@ora");
		this.esito 			= XMLUtil.parseStrictAttribute(dom, "notifica/@esito");
		this.info 			= XMLUtil.parseStrictAttribute(dom, "notifica/@info");
		this.riferita		= XMLUtil.parseStrictAttribute(dom, "notifica/@riferita");
		this.note			= XMLUtil.parseStrictElement(dom, "notifica/note");
    	
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOra() {
		return ora;
	}

	public void setOra(String ora) {
		this.ora = ora;
	}

	public String getEsito() {
		return esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
	}
	
	public String getNumeroFattura() {
		return numeroFattura;
	}

	public void setNumeroFattura(String numeroFattura) {
		this.numeroFattura = numeroFattura;
	}

	public String getAnnoFattura() {
		return annoFattura;
	}

	public void setAnnoFattura(String annoFattura) {
		this.annoFattura = annoFattura;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getRiferita() {
		return riferita;
	}

	public void setRiferita(String riferita) {
		this.riferita = riferita;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}
