package it.tredi.dw4.acl.model.custom;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

public class Archivio extends XmlEntity {

	private String id = "";
	private String descr = "";
	private String level = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.descr = XMLUtil.parseStrictElement(dom, "archivio");
		this.id = XMLUtil.parseStrictAttribute(dom, "archivio/@id");
		this.level = XMLUtil.parseStrictAttribute(dom, "archivio/@level");
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
		Map<String, String> params = new HashMap<String, String>();
		
		if (descr.length() > 0)
			params.put(prefix+"", descr);
		if (id.length() > 0)
			params.put(prefix+".@id", id);
		if (level.length() > 0)
			params.put(prefix+".@level", level);

		return params;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
}
