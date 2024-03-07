package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Oggetto extends XmlEntity {
	private boolean check;
	private boolean checkUfficioRuolo;
	private String cod;
	private String text;
	private String state;
	private Integer level;
	private boolean hasContent;
	private boolean selected = false;

	public Oggetto() {}

	public Oggetto(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }

    public Oggetto init(Document dom) {
    	this.cod = XMLUtil.parseStrictAttribute(dom, "oggetto/@codice");
    	this.text = XMLUtil.parseStrictElement(dom, "oggetto");
    	this.state = XMLUtil.parseStrictAttribute(dom, "oggetto/@state");
    	this.checkUfficioRuolo = Boolean.parseBoolean(XMLUtil.parseAttribute(dom, "oggetto/@checkUfficioRuolo", "false"));
    	try {
    		this.level = Integer.parseInt(XMLUtil.parseStrictAttribute(dom, "oggetto/@level", "0"));
    	}
    	catch (NumberFormatException ex) {
    		this.level = 0;
    	}
    	this.hasContent = XMLUtil.parseStrictAttribute(dom, "oggetto/@hasContent", "").toUpperCase().equals("YES") ?  true : false;
        return this;
    }

    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix+".@codice", this.cod);
    	if (level != null)
    		params.put(prefix+".@level", Integer.toString(this.level));
    	params.put(prefix, this.text);
    	return params;
    }

    public String getCod() {
		return cod;
	}

	public void setCod(String cod) {
		this.cod = cod;
	}

	public void setText(String value) {
		this.text = value;
	}

	public String getText() {
		return text;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean isCheck() {
		return check;
	}

	public boolean isCheckUfficioRuolo() {
		return checkUfficioRuolo;
	}

	public void setCheckUfficioRuolo(boolean checkUfficioRuolo) {
		this.checkUfficioRuolo = checkUfficioRuolo;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public boolean getHasContent() {
		return hasContent;
	}

	public void setHasContent(boolean hasContent) {
		this.hasContent = hasContent;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}

