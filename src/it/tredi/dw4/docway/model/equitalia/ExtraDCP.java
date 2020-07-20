package it.tredi.dw4.docway.model.equitalia;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class ExtraDCP extends XmlEntity {

	// campi del repertorio di Equitalia per documento del ciclo passivo
	private String stato_invio_nav = "";
	private String numero_nav = "";
	private String tipologiaDocumentazione = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.stato_invio_nav 			= XMLUtil.parseStrictAttribute(dom, "extra/@stato_invio_nav");
		this.numero_nav 				= XMLUtil.parseStrictAttribute(dom, "extra/@numero_nav");
		this.tipologiaDocumentazione 	= XMLUtil.parseStrictElement(dom, "extra/tipologiaDocumentazione");
    	
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@stato_invio_nav", this.stato_invio_nav);
    	params.put(prefix+".@numero_nav", this.numero_nav);
    	params.put(prefix+".tipologiaDocumentazione", this.tipologiaDocumentazione);
    	
    	return params;
	}
	
	public String getStato_invio_nav() {
		return stato_invio_nav;
	}

	public void setStato_invio_nav(String stato_invio_nav) {
		this.stato_invio_nav = stato_invio_nav;
	}
	
	public String getNumero_nav() {
		return numero_nav;
	}

	public void setNumero_nav(String numero_nav) {
		this.numero_nav = numero_nav;
	}
	
	public String getTipologiaDocumentazione() {
		return tipologiaDocumentazione;
	}

	public void setTipologiaDocumentazione(String tipologiaDocumentazione) {
		this.tipologiaDocumentazione = tipologiaDocumentazione;
	}
	
}
