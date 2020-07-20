package it.tredi.dw4.docway.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

public class ConfigFirmaDigitale extends XmlEntity {

	private String baseUrl = "";
	private String postUrl = "";
	private String fileId = "";
	private String tipoRiconsegna = "";
	private String tipoRiconsegnaPDF = "";
	private boolean abilitaFirmaSuDerTo = false;
	private HashMap<String, String> params = new HashMap<String, String>(); // parametri di configurazione della firma digitale
	
	@Override
	@SuppressWarnings("unchecked")
	public XmlEntity init(Document dom) {
		this.baseUrl 				= XMLUtil.parseStrictAttribute(dom, "configFirmaDigitale/@baseUrl");
		this.postUrl 				= XMLUtil.parseStrictAttribute(dom, "configFirmaDigitale/@postUrl");
		this.fileId 				= XMLUtil.parseStrictAttribute(dom, "configFirmaDigitale/@fileid");
		this.tipoRiconsegna 		= XMLUtil.parseStrictAttribute(dom, "configFirmaDigitale/@tipoRiconsegna");
		this.tipoRiconsegnaPDF		= XMLUtil.parseStrictAttribute(dom, "configFirmaDigitale/@tipoRiconsegnaPDF");
		this.abilitaFirmaSuDerTo 	= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "configFirmaDigitale/@abilitaFirmaSuDerTo"));
		
		List<Element> elements = dom.selectNodes("configFirmaDigitale/param");
		for (int i=0; i<elements.size(); i++) {
			if (elements.get(i) != null) {
				String name = elements.get(i).attributeValue("name");
				String value = elements.get(i).attributeValue("value");
				if (name != null && name.trim().length() > 0) {
					if (value == null)
						value = "";
					params.put(name, value);
				}
			}
		}
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public HashMap<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getPostUrl() {
		return postUrl;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl = postUrl;
	}
	
	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public String getTipoRiconsegna() {
		return tipoRiconsegna;
	}

	public void setTipoRiconsegna(String tipoRiconsegna) {
		this.tipoRiconsegna = tipoRiconsegna;
	}
	
	public boolean isAbilitaFirmaSuDerTo() {
		return abilitaFirmaSuDerTo;
	}

	public void setAbilitaFirmaSuDerTo(boolean abilitaFirmaSuDerTo) {
		this.abilitaFirmaSuDerTo = abilitaFirmaSuDerTo;
	}
	
	public String getTipoRiconsegnaPDF() {
		return tipoRiconsegnaPDF;
	}

	public void setTipoRiconsegnaPDF(String tipoRiconsegnaPDF) {
		this.tipoRiconsegnaPDF = tipoRiconsegnaPDF;
	}
	
}
