package it.tredi.dw4.aclCrawler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Element;

public class RepSection {
	public static int COLUMN_NUM = 2;
	
	private String title;
	private List<Element> repertoriElements;
	private Map<String, List<Map<String, Object>>> repertori;

	public Map<String, List<Map<String, Object>>> getRepertori() {
		return repertori;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Element> getRepertoriElements() {
		return repertoriElements;
	}
	
	@SuppressWarnings("unchecked")
	public RepSection(String title, List<Element> repEls) {
		this.repertoriElements = repEls;
		this.title = title;
		
		this.repertori = new HashMap<String, List<Map<String,Object>>>();
		for (Integer n=0; n<AclSection.COLUMN_NUM; n++) {
			this.repertori.put(n.toString(), new ArrayList<Map<String,Object>>());
		}
		
		//calcola quanti repertori per colonna
		int numEntries = (int) Math.ceil(countReps() / ((float)RepSection.COLUMN_NUM));
		int entriesCount = 0;
		
		for (Element repEl : this.repertoriElements) {
			
			List<Element> tables = repEl.selectNodes("./tabella");
			for (Element table : tables) {
			
				Map<String, Object> repInfo = new HashMap<String, Object>();
				
				repInfo.put("label", repEl.selectSingleNode("./descrizione").getText() + " " + table.getText());
				repInfo.put("cod", "DW-" + repEl.attributeValue("codice", "") + "-" + table.attributeValue("tipo", ""));
				
				repInfo.put("checkedI", "not");
				repInfo.put("imageI", "css/images/not_checked.gif");
				repInfo.put("checkedV", "not");
				repInfo.put("imageV", "css/images/not_checked.gif");
				repInfo.put("checkedC", "not");
				repInfo.put("imageC", "css/images/not_checked.gif");
				
	//			repInfo.put("checkedI", false);
	//			repInfo.put("checkedV", false);
	//			repInfo.put("checkedC", false);
				
				Integer listIndex = (int) Math.floor(entriesCount/numEntries);  //FIXME il floor é probabilmente inutile - indagare...
				this.repertori.get(listIndex.toString()).add(repInfo);
				entriesCount++;
			}
		}
	}
	
	public void changeRepStatusI() {
		changeRepStatus("I");
	}
	public void changeRepStatusV() {
		changeRepStatus("V");
	}
	public void changeRepStatusC() {
		changeRepStatus("C");
	}
	
	@SuppressWarnings("unchecked")
	public void changeRepStatus(String postfix) {
		Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		Map<String, Object> repertorioMap = (Map<String, Object>) requestMap.get("repertorio");
		
		String checked = (String) repertorioMap.get("checked" + postfix);
		if (checked.equals("not")) {
			repertorioMap.put("checked" + postfix, "true");
			repertorioMap.put("image" + postfix, "css/images/checked.gif");
		}
		else if (checked.equals("true")) {
			repertorioMap.put("checked" + postfix, "false");
			repertorioMap.put("image" + postfix, "css/images/unchecked.gif");
		}
		else if (checked.equals("false")) {
			repertorioMap.put("checked" + postfix, "not");
			repertorioMap.put("image" + postfix, "css/images/not_checked.gif");
		}
	}
	
	protected int countReps() {
		int counter = 0;
		
		for (Element repertorio : this.repertoriElements) {
			counter += repertorio.selectNodes("./tabella").size();
		}
		
		return counter;
	}
}
