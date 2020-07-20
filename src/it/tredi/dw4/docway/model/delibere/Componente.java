package it.tredi.dw4.docway.model.delibere;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Componente extends XmlEntity {
	
	private String pos = "";
	private String presenza = "";
	private String nominativo = "";
	private String incarico = "";
	private String delega = "";
	private String delibera = "";
	private String parere = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.setPos(XMLUtil.parseAttribute(dom, "componente/@pos"));
		this.setPresenza(XMLUtil.parseAttribute(dom, "componente/@presenza"));
		this.setNominativo(XMLUtil.parseElement(dom, "componente/nominativo"));
		this.setIncarico(XMLUtil.parseElement(dom, "componente/incarico"));
		this.setDelega(XMLUtil.parseElement(dom, "componente/delega"));
		this.setDelibera("");
		this.setParere(XMLUtil.parseAttribute(dom, "componente/@parere"));
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix+".nominativo", this.nominativo);
    	params.put(prefix+".incarico", this.incarico);
		return params;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getPresenza() {
		return presenza;
	}

	public void setPresenza(String presenza) {
		this.presenza = presenza;
	}

	public String getNominativo() {
		return nominativo;
	}

	public void setNominativo(String nominativo) {
		this.nominativo = nominativo;
	}

	public String getIncarico() {
		return incarico;
	}

	public void setIncarico(String incarico) {
		this.incarico = incarico;
	}

	public String getDelega() {
		return delega;
	}
	
	public void setDelega(String delega) {
		this.delega = delega;
	}
	
	public String getDelibera() {
		return delibera;
	}

	public void setDelibera(String delibera) {
		this.delibera = delibera;
	}

	public String getParere() {
		return parere;
	}

	public void setParere(String parere) {
		this.parere = parere;
	}
}
