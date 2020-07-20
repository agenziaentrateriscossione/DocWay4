package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DocAnnullato extends XmlEntity {
	private String oggetto;
	private String num_prot;
	private String data_prot;
	private String mitt_dest;

	@Override
	public XmlEntity init(Document dom) {
		this.num_prot =  XMLUtil.parseAttribute(dom, "doc/@num_prot");
		this.data_prot = XMLUtil.parseAttribute(dom, "doc/@data_prot");
		this.mitt_dest = XMLUtil.parseElement(dom, "doc/mitt_dest");
		this.oggetto = 	 XMLUtil.parseElement(dom, "doc/oggetto");
		return null;
	}
	
	public void initHistory(Document dom){
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@num_prot", this.num_prot);
    	params.put(prefix+".@data_prot", this.data_prot);
    	params.put(prefix+".mitt_dest", this.mitt_dest);
    	params.put(prefix+".oggetto", this.oggetto);
    	return params;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setNum_prot(String num_prot) {
		this.num_prot = num_prot;
	}

	public String getNum_prot() {
		return num_prot;
	}

	public void setData_prot(String data_prot) {
		this.data_prot = data_prot;
	}

	public String getData_prot() {
		return data_prot;
	}

	public void setMitt_dest(String bozza) {
		this.mitt_dest = bozza;
	}

	public String getMitt_dest() {
		return mitt_dest;
	}
	
}
