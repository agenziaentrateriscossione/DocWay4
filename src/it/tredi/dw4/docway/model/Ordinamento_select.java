package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Ordinamento_select extends XmlEntity {
	private List<Option> options;
    
	public Ordinamento_select() {}
    
	public Ordinamento_select(String xml) throws Exception {
        this.init(XMLUtil.getDOM(xml));
    }
    
    @SuppressWarnings("unchecked")
	public Ordinamento_select init(Document dom) {
    	this.options = XMLUtil.parseSetOfElement(dom, "//option", new Option());
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	return params;
    }
    
	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public List<Option> getOptions() {
		return options;
	}
	
}

