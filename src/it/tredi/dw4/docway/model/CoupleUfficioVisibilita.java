package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class CoupleUfficioVisibilita extends XmlEntity {
	
	private String nomiVisibilita;
	private String codUfficio;
	private String codVisibilita;
	
	//gettes & setters
	public String getNomiVisibilita() {
		return nomiVisibilita;
	}
	public void setNomiVisibilita(String nomiVisibilita) {
		this.nomiVisibilita = nomiVisibilita;
	}
	public String getCodUfficio() {
		return codUfficio;
	}
	public void setCodUfficio(String codUfficio) {
		this.codUfficio = codUfficio;
	}
	public String getCodVisibilita() {
		return codVisibilita;
	}
	public void setCodVisibilita(String codVisibilita) {
		this.codVisibilita = codVisibilita;
	}
	
	//costruttore
	public CoupleUfficioVisibilita() {}
	
	@Override
	public XmlEntity init(Document dom) {
		String nodeName = "coppia";
    	this.nomiVisibilita = XMLUtil.parseAttribute(dom, nodeName + "/@nome_vis");
    	this.codUfficio = XMLUtil.parseAttribute(dom, nodeName + "/@cod_uff");
    	this.codVisibilita = XMLUtil.parseAttribute(dom, nodeName + "/@cod_vis");
		return this;
	}
	
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@cod_uff", this.codUfficio);
    	params.put(prefix+".@nome_vis", this.nomiVisibilita);
    	params.put(prefix+".@cod_vis", this.codVisibilita);
    	return params;
	}
	
	private void clear() {
    	this.nomiVisibilita = "";
    	this.codUfficio = "";
    	this.codVisibilita = "";
	}
	
	public String extractNomeVisibilitaSingolare() {
		int index = nomiVisibilita.indexOf("|");
		if (index > 0)
			return nomiVisibilita.substring(0, index);
		else return "";
	}
}
