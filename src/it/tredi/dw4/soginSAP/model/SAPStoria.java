package it.tredi.dw4.soginSAP.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class SAPStoria extends XmlEntity {
	protected String causale;
	protected String causaleCod;
	protected String docSap;
	protected String dataRegistrazione;
	protected String utente;
	protected String stato;
	
	public SAPStoria() {}
	
	public SAPStoria(String xml) {
		this.init(XMLUtil.getDOM(xml));
	}
	
	@Override
	public XmlEntity init(Document dom) {
		this.stato = XMLUtil.parseAttribute(dom, "./@stato");
		this.causaleCod = XMLUtil.parseAttribute(dom, "./causale/@cod");
		this.causale = XMLUtil.parseElement(dom, "./causale");
		this.docSap = XMLUtil.parseElement(dom, "./docSAP");
		this.dataRegistrazione = XMLUtil.parseElement(dom, "./dataRegistrazione");
		this.utente = XMLUtil.parseElement(dom, "./utente");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@stato", this.stato);
    	params.put(prefix+".causale", this.causale);
    	params.put(prefix+".causale.@cod", this.causaleCod);
    	params.put(prefix+".docSAP", this.docSap);
    	params.put(prefix+".dataRegistrazione", this.dataRegistrazione);
    	params.put(prefix+".utente", this.utente);
    	
		return params;
	}

	public String getCausale() {
		return causale;
	}

	public void setCausale(String causale) {
		this.causale = causale;
	}

	public String getDocSap() {
		return docSap;
	}

	public void setDocSap(String docSap) {
		this.docSap = docSap;
	}

	public String getDataRegistrazione() {
		return dataRegistrazione;
	}

	public void setDataRegistrazione(String dataRegistrazione) {
		this.dataRegistrazione = dataRegistrazione;
	}

	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getCausaleCod() {
		return causaleCod;
	}

	public void setCausaleCod(String causaleCod) {
		this.causaleCod = causaleCod;
	}

}
