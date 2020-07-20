package it.tredi.dw4.docway.model;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;

import it.tredi.dw4.model.XmlEntity;

public class EmailRecipient extends XmlEntity {
	
	private String email = "";
	private String name = "";
	private String full_email = "";
	
	@Override
	public XmlEntity init(Document dom) {
		return this;
	}
	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
		Map<String, String> params = new HashMap<String, String>();
    	
		// Aggiunta delle virgolette prima dell'indirizzo email
		String value = full_email;
		if (full_email != null && !full_email.equals("")) {
			if (!full_email.startsWith("\"")) {
				int index = full_email.lastIndexOf("<");
				if (index != -1) {
					if (full_email.substring(0, index).endsWith(" "))
						value = "\"" + full_email.substring(0, index-1) + "\" " + full_email.substring(index);
					else
						value = "\"" + full_email.substring(0, index) + "\" " + full_email.substring(index);
				}
			}
		}
		params.put(prefix+"", value);
    	
    	return params;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String address) {
		this.email = address;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFull_email() {
		return full_email;
	}
	
	public void setFull_email(String full_email) {
		this.full_email = full_email;
	}

}
