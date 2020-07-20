package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Categoria extends XmlEntity {
	private String spec;
    
	public Categoria() {}
    
	public Categoria(String xmlCategoria) throws Exception {
        this.init(XMLUtil.getDOM(xmlCategoria));
    }
    
    public Categoria init(Document domCategoria) {
		this.spec = XMLUtil.parseAttribute(domCategoria, "categoria/@spec");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	if ( null != this.spec ) params.put(prefix +".@spec", this.spec);
    	return params;
    }
    
    public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}
}

