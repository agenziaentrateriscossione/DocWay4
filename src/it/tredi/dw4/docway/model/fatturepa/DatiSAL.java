package it.tredi.dw4.docway.model.fatturepa;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DatiSAL extends XmlEntity {
	
	private String riferimentoFase = "";

	@Override
	public XmlEntity init(Document dom) {
		this.riferimentoFase = XMLUtil.parseStrictAttribute(dom, "datiSAL/@riferimentoFase");
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (this.riferimentoFase != null && this.riferimentoFase.length() > 0)
    		params.put(prefix+".@riferimentoFase", this.riferimentoFase);
    	
    	return params;
	}
	
	public String getRiferimentoFase() {
		return riferimentoFase;
	}

	public void setRiferimentoFase(String riferimentoFase) {
		this.riferimentoFase = riferimentoFase;
	}

}
