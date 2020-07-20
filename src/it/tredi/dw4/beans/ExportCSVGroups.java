package it.tredi.dw4.beans;

import it.tredi.dw4.docway.model.ExportGroup;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

public class ExportCSVGroups {
	
	private String name;
	private String type;
	private List<ExportGroup> groups = new ArrayList<ExportGroup>();

	@SuppressWarnings("unchecked")
	public ExportCSVGroups(Element exportDefinition) {
		this.name = exportDefinition.attributeValue("name");
		this.type = exportDefinition.attributeValue("type");
		List<Element> groups = exportDefinition.selectNodes("./groups/group");
		for (Element group : groups) {
			this.groups.add(new ExportGroup(group, type));
		}
	}

	public String getName() {
		return name;
	}

	public List<ExportGroup> getGroups() {
		return groups;
	}

	public String getType() {
		return type;
	}
}
