package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * dettagli di conservazione tramite Bridge estratti da extra/conservazione
 * @author mbernardini
 */
public class ConservazioneBridge extends XmlEntity {
	
	private String id = "";
	private String servizio = "";
	private String stato = "";
	private String tipoServizio = "";

	@Override
	public XmlEntity init(Document dom) {
		this.id 			= XMLUtil.parseAttribute(dom, "conservazione/@id");
		this.servizio	 	= XMLUtil.parseAttribute(dom, "conservazione/@servizio");
		this.stato 			= XMLUtil.parseAttribute(dom, "conservazione/@stato");
		this.tipoServizio	= XMLUtil.parseAttribute(dom, "conservazione/@tipo_servizio");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".@id", this.id);
    	params.put(prefix+".@servizio", this.servizio);
    	params.put(prefix+".@stato", this.stato);
    	params.put(prefix+".@tipo_servizio", this.tipoServizio);
    	
    	return params;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServizio() {
		return servizio;
	}

	public void setServizio(String servizio) {
		this.servizio = servizio;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getTipoServizio() {
		return tipoServizio;
	}

	public void setTipoServizio(String tipoServizio) {
		this.tipoServizio = tipoServizio;
	}
	
}
