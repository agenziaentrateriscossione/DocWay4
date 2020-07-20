package it.tredi.dw4.model;

import java.util.Map;

import org.dom4j.Document;

public abstract class XmlEntity {
	public abstract XmlEntity init(Document dom);
	public abstract Map<String, String> asFormAdapterParams(String prefix); 
}
