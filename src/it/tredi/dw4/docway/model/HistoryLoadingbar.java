package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class HistoryLoadingbar extends XmlEntity {

	private String data;
	private String ora;
	private String descr;
	private String esito;
    
	public HistoryLoadingbar() {}
    
	public HistoryLoadingbar(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    public HistoryLoadingbar init(Document dom) {
    	this.descr	= XMLUtil.parseAttribute(dom, "item/@descr");
    	this.esito 	= XMLUtil.parseAttribute(dom, "item/@esito");
    	this.data 	= XMLUtil.parseAttribute(dom, "item/@data");
    	this.ora 	= XMLUtil.parseAttribute(dom, "item/@ora");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	return new HashMap<String, String>();
    }
    
	public void setEsito(String nome_uff) {
		this.esito = nome_uff;
	}

	public String getEsito() {
		return esito;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setOra(String ora) {
		this.ora = ora;
	}

	public String getOra() {
		return ora;
	}

	public void setDescr(String operatore) {
		this.descr = operatore;
	}

	public String getDescr() {
		return descr;
	}
}

