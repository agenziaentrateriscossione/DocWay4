package it.tredi.dw4.docway.model.equitalia;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class ExtraFTRP extends XmlEntity {

	// campi del repertorio di Equitalia per fatture passive
	private String stato_invio_nav = "";
	private String numero_cig = "";
	private String data_ricezione = "";
	private String importo = "";
	private String tipo_documento = "";
	private String numero_nav = "";
	private String tipologiaDocumentazione = "";
	private String stato_richiesta = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.stato_invio_nav			= XMLUtil.parseStrictAttribute(dom, "extra/@stato_invio_nav");
		this.numero_cig 				= XMLUtil.parseStrictAttribute(dom, "extra/@numero_cig");
    	this.data_ricezione 			= XMLUtil.parseStrictAttribute(dom, "extra/@data_ricezione");
    	this.importo 					= XMLUtil.parseStrictAttribute(dom, "extra/@importo");
    	this.tipo_documento 			= XMLUtil.parseStrictAttribute(dom, "extra/@tipo_documento");
    	this.stato_richiesta			= XMLUtil.parseStrictAttribute(dom, "extra/@stato_richiesta");
    	this.numero_nav 				= XMLUtil.parseStrictAttribute(dom, "extra/@numero_nav");
    	this.tipologiaDocumentazione 	= XMLUtil.parseStrictElement(dom, "extra/tipologiaDocumentazione");
    	
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@stato_invio_nav", this.stato_invio_nav);
    	params.put(prefix+".@numero_cig", this.numero_cig);
    	params.put(prefix+".@data_ricezione", this.data_ricezione);
    	params.put(prefix+".@numero_cig", this.numero_cig);
    	params.put(prefix+".@importo", this.importo);
    	params.put(prefix+".@tipo_documento", this.tipo_documento);
    	params.put(prefix+".@stato_richiesta", this.stato_richiesta);
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

	public String getNumero_cig() {
		return numero_cig;
	}

	public void setNumero_cig(String numero_cig) {
		this.numero_cig = numero_cig;
	}

	public String getData_ricezione() {
		return data_ricezione;
	}

	public void setData_ricezione(String data_ricezione) {
		this.data_ricezione = data_ricezione;
	}

	public String getImporto() {
		return importo;
	}

	public void setImporto(String importo) {
		this.importo = importo;
	}

	public String getTipo_documento() {
		return tipo_documento;
	}

	public void setTipo_documento(String tipo_documento) {
		this.tipo_documento = tipo_documento;
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
	
	public String getStato_richiesta() {
		return stato_richiesta;
	}

	public void setStato_richiesta(String stato_richiesta) {
		this.stato_richiesta = stato_richiesta;
	}

}
