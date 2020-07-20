package it.tredi.dw4.acl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;
import org.dom4j.Element;

public class Hier extends XmlEntity {
	private String title;
	private String ndoc;
	private String depth;
	private boolean sons;
	private boolean first;
	private boolean last;
	private boolean opened;
	private String brothers;
	private String titlePath;
	private boolean virtual;
	private boolean ghost;
    
	public Hier() {}
    
	public Hier(String xmlHier) throws Exception {
        this.init(XMLUtil.getDOM(xmlHier));
    }
    
    public Hier init(Document domHier) {
    	Element hierElement = (Element)domHier.getRootElement();
    	this.virtual = false;
    	this.ghost = false;
    	this.title = hierElement.attributeValue("title", "");
    	if (title.indexOf("U!struttura_virtuale") != -1) { //se il titolo termina per '|amm' o per '|aoo' si tratta di una struttura virtuale
    		virtual = true;
    	}
    	if (title.indexOf("CP!|") != -1 && title.indexOf("RT!si") == -1) { //i nodi senza padre non marchiati come radice sono dei fantasmi
    		ghost = true;
    	}
    	this.title = this.title.substring(0, this.title.indexOf("|ET|")).trim();
    	this.ndoc = hierElement.attributeValue("ndoc", "");
    	this.depth = hierElement.attributeValue("depth", "");
    	this.sons = hierElement.attributeValue("sons", "0").equals("1");
    	this.first = hierElement.attributeValue("first", "0").equals("1");
    	this.last = hierElement.attributeValue("last", "0").equals("1");
    	this.opened = hierElement.attributeValue("opened", "0").equals("1");
    	this.brothers = hierElement.attributeValue("brothers", "");
    	this.titlePath = hierElement.attributeValue("titlePath", "");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	return params;
    }

    public boolean isGhost() {
		return ghost;
	}    
    
    public boolean isVirtual() {
		return virtual;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNdoc() {
		return ndoc;
	}

	public void setNdoc(String ndoc) {
		this.ndoc = ndoc;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public boolean getSons() {
		return sons;
	}

	public void setSons(boolean sons) {
		this.sons = sons;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public List<String> getBrothers() {
		String brother = brothers;
		ArrayList<String> images = new ArrayList<String>();
		for (int level = 2; level > 1 && brother.length() >= level; level++) {
			
			if (brother.length() == level) { //ultimo ramo
				//level - 1 poiché è una traduzione da xslt dove le stringhe partono da indice 1
				if (brother.substring(level - 1, level).equals("1")) { //seguono fratelli
					if (this.sons) { //ci sono figli
						if (this.opened) { //corner with brothers and sons opened
							images.add("cbo");
						}
						else { //corner with brothers and sons closed
							images.add("cbc");
						}
					}
					else { //leaf corner with brothers
						images.add("cb");
					}
				}
				else { //no fratelli a seguire
					if (this.sons) { //ci sono figli
						if (this.opened) { //corner without brothers, with sons opened
							images.add("co");
						}
						else { //corner without brothers, with sons closed
							images.add("cc");
						}
					}
					else { //leaf corner without brothers
						images.add("c");
					}
				}
			}
			else {
				//level - 1 poiché è una traduzione da xslt dove le stringhe partono da indice 1
				if (brother.substring(level - 1, level).equals("1")) { //seguono fratelli
					images.add("bb");
				}
				else { //nessun fratello
					images.add("b");
				}
			}
			
		}
		return images;
	}

	public void setBrothers(String brothers) {
		this.brothers = brothers;
	}

	public String getTitlePath() {
		return titlePath;
	}

	public void setTitlePath(String titlePath) {
		this.titlePath = titlePath;
	}
}

