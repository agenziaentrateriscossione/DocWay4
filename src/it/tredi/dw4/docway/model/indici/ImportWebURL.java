package it.tredi.dw4.docway.model.indici;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class ImportWebURL extends XmlEntity {

	private String url;
	private String text;
	
	@Override
	public XmlEntity init(Document dom) {
		this.url = XMLUtil.parseStrictAttribute(dom, "web/@url", "");
		this.text = XMLUtil.parseStrictElement(dom, "web", true);
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
