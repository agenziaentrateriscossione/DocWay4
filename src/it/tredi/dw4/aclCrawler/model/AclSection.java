package it.tredi.dw4.aclCrawler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

public class AclSection {
	public static int COLUMN_NUM = 3;
	
	private String title = "";
	private Map<String, List<AclGroup>> groups;

	public Map<String, List<AclGroup>> getGroups() {
		return groups;
	}
	public String getTitle() {
		return title;
	}

	@SuppressWarnings("unchecked")
	public AclSection(Element groupSection) {
		this.groups = new HashMap<String, List<AclGroup>>();
		for (Integer n=0; n<AclSection.COLUMN_NUM; n++) {
			this.groups.put(n.toString(), new ArrayList<AclGroup>());
		}
		
		if (groupSection != null) {
			this.title = groupSection.attributeValue("label", "");
			
			//calcola quanti diritti per colonna
			int numEntries = (int) Math.ceil(groupSection.selectNodes("./group").size() / ((float)AclSection.COLUMN_NUM));
			int entriesCount = 0;
			
			Iterator<Element> groupsIt = groupSection.selectNodes("./group").iterator();
			while (groupsIt.hasNext()) {
				Element groupEl = groupsIt.next();
				
				Integer listIndex = (int) Math.floor(entriesCount/numEntries);  //FIXME il floor è probabilmente inutile - indagare...
				this.groups.get(listIndex.toString()).add(new AclGroup(groupEl));
				entriesCount++;
			}
		}
	}
}
