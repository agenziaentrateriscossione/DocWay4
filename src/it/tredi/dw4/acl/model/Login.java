package it.tredi.dw4.acl.model;

import java.util.HashMap;
import java.util.Map;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class Login extends XmlEntity {
	private String name;
    
	public Login() {}
    
	public Login(String xmlLogin) throws Exception {
        this.init(XMLUtil.getDOM(xmlLogin));
    }
    
    public Login init(Document domLogin) {
		this.name = XMLUtil.parseAttribute(domLogin, "login/@name");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	params.put(prefix + ".@name", this.name);
    	return params;
    }
    
    public String getName() {
		return name;
	}

	public void setName(String nome) {
		this.name = nome;
	}
}

