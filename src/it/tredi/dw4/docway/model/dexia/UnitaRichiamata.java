package it.tredi.dw4.docway.model.dexia;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class UnitaRichiamata extends XmlEntity {
	private String cod_uff = "";
	private String nome_uff = "";

	@Override
	public XmlEntity init(Document dom) {
		this.cod_uff = XMLUtil.parseAttribute(dom, "NA_unitaRichiamata/@cod_uff");
    	this.nome_uff = XMLUtil.parseAttribute(dom, "NA_unitaRichiamata/@nome_uff");
		
    	return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod_uff", this.cod_uff);
    	params.put(prefix+".@nome_uff", this.nome_uff);
    	
    	return params;
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
}
