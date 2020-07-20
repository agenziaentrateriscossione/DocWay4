package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class DocAbrogato extends XmlEntity {
	private String oggetto;
	private String dataEmanazione;

	@Override
	public XmlEntity init(Document dom) {
		this.setDataEmanazione(XMLUtil.parseAttribute(dom, "doc/extra/NA_dataEmanazioneNorma"));
		this.oggetto = 	 XMLUtil.parseElement(dom, "doc/oggetto");
		return null;
	}
	
	public void initHistory(Document dom){
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".extra.NA_dataEmanazioneNorma", this.getDataEmanazione());
    	params.put(prefix+".oggetto", this.oggetto);
    	return params;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getOggetto() {
		return oggetto;
	}

	public String getDataEmanazione() {
		return dataEmanazione;
	}

	public void setDataEmanazione(String dataEmanazione) {
		this.dataEmanazione = dataEmanazione;
	}
}
