package it.tredi.dw4.docway.model.analisivirus;

import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

/**
 * Informazioni relative ad un file infetto rilevato sul documento
 */
public class FileInfetto extends XmlEntity {

	private String id;
	private String title;
	
	@Override
	public XmlEntity init(Document dom) {
		this.id = XMLUtil.parseStrictAttribute(dom, "file/@id");
		this.title = XMLUtil.parseStrictAttribute(dom, "file/@title");
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
}
