package it.tredi.dw4.docway.model.delibere;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class XwFileModello extends XmlEntity {
	private String name;
	private String title;
	
	public XwFileModello(String name, String title) {
		this.title = title;
		this.name = name;
	}

	@Override
	public XmlEntity init(Document dom) {
		this.setName(XMLUtil.parseAttribute(dom, "node()[name()='xw:file']/@name"));
		this.setTitle(XMLUtil.parseAttribute(dom, "node()[name()='xw:file']/@title"));
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
