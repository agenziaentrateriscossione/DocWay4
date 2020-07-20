package it.tredi.dw4.aclCrawler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Element;

public class AclGroup {
	private static List<String> nonBooleanRights = new ArrayList<String>();
	
	public static void initNonBooleanRights(List<String> nonBooleanRights) {
		AclGroup.nonBooleanRights.clear();
		AclGroup.nonBooleanRights.addAll(nonBooleanRights);
	}
	
	public static List<String> getNonBooleanRights() {
		return AclGroup.nonBooleanRights;
	}
	
	private String title = "";
	private List<Map<String, Object>> rights;

	public List<Map<String, Object>> getRights() {
		return rights;
	}
	public String getTitle() {
		return title;
	}

	@SuppressWarnings("unchecked")
	public AclGroup(Element group) {
		this.rights = new ArrayList<Map<String, Object>>();
		
		this.title = group.attributeValue("label", "");
		
		Iterator<Element> rightsIt = group.selectNodes("./right").iterator();
		while (rightsIt.hasNext()) {
			Element rightEl = rightsIt.next();
			
			Map<String, Object> rightMap = new HashMap<String, Object>();
			rightMap.put("label", rightEl.attributeValue("label", ""));
			rightMap.put("cod", rightEl.attributeValue("cod", ""));
			rightMap.put("checked", "not");
			rightMap.put("image", "css/images/not_checked.gif");
			
			//questo è per i diritti testuali (non vero/falso) come il campo altre UOR
			if (AclGroup.nonBooleanRights.contains(rightEl.attributeValue("cod", ""))) {
				rightMap.put("text", rightEl.getText());
				rightMap.put("nonBoolean", true); 
			}
			else {
				rightMap.put("text", "");
				rightMap.put("nonBoolean", false);
			}
			
			this.rights.add(rightMap);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void changeRightStatus() throws Exception {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		Map<String, Object> rightMap = (Map<String, Object>) requestMap.get("right");
		
		String checked = (String) rightMap.get("checked");
		if (checked.equals("not")) {
			rightMap.put("checked", "true");
			rightMap.put("image", "css/images/checked.gif");
		}
		else if (checked.equals("true")) {
			rightMap.put("checked", "false");
			rightMap.put("image", "css/images/unchecked.gif");
		}
		else if (checked.equals("false")) {
			rightMap.put("checked", "not");
			rightMap.put("image", "css/images/not_checked.gif");
		}
	}
}
