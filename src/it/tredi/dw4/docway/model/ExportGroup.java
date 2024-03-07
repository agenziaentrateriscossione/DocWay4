package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;
import org.dom4j.Element;

public class ExportGroup extends XmlEntity {
	
	private String name;
	private boolean selected;
	private String type;
	
	public ExportGroup() {
		this.name = "";
		this.type = "";
		this.selected = false;
	}

	public ExportGroup(Element group, String type, String exportType) {
		this();
		this.name = group.attributeValue("name");
		this.type = group.attributeValue("type", type);
		if (exportType != null && type.equals(exportType) && group.attributeValue("selected", "").equals("true")) {
			this.selected = true;
		}
	}

	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public XmlEntity init(Document dom) {
		this.type = XMLUtil.parseAttribute(dom, "group/@type");
		this.name = XMLUtil.parseAttribute(dom, "group/@name");
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if (null != this.type ) 	params.put(prefix + ".@type", this.type);
    	if (null != this.name ) 	params.put(prefix + ".@name", this.name);
    	
    	return params;
	}
}
