package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Pubblicazione extends XmlEntity {

	private String dal = "";
	private String al = "";
	private String tipo = "";
	private String dataRitiro = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.dal 	= XMLUtil.parseAttribute(dom, "pubblicazione/@dal");
    	this.al 	= XMLUtil.parseAttribute(dom, "pubblicazione/@al");
    	this.tipo 	= XMLUtil.parseAttribute(dom, "pubblicazione/@tipo");
    	this.dataRitiro = XMLUtil.parseAttribute(dom, "pubblicazione/@data_ritiro");
    			
        return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if (!this.dal.equals(""))
    		params.put(prefix+".@dal", this.dal);
    	if (!this.al.equals(""))
    		params.put(prefix+".@al", this.al);
    	if (!this.tipo.equals(""))
    		params.put(prefix+".@tipo", this.tipo);
    	if (!this.dataRitiro.equals(""))
    		params.put(prefix+".@data_ritiro", this.dataRitiro);
    	
    	return params;
	}
	
	public String getDal() {
		return dal;
	}

	public void setDal(String dal) {
		this.dal = dal;
	}

	public String getAl() {
		return al;
	}

	public void setAl(String al) {
		this.al = al;
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getDataRitiro() {
		return dataRitiro;
	}

	public void setDataRitiro(String dataRitiro) {
		this.dataRitiro = dataRitiro;
	}

}
