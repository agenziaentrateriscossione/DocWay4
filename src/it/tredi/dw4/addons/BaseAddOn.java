package it.tredi.dw4.addons;

import it.tredi.dw4.beans.Page;

import java.util.Map;

import org.dom4j.Document;

public abstract class BaseAddOn {
	protected String template = "";
	protected String statusTemplate = "";
	private Page host = null; 
	
	public BaseAddOn(String template, Page host) {
		this.template = template;
		this.host = host;
	}
	
	public abstract void init(Document dom); // init da dom
	
	public abstract Map<String, String> asFormAdapterParams(); // eventuale salvataggio da pagina host, set dei parametri su formAdapter
	
	public abstract void clear(); // eventuale clear di un form chiamato da pagina host (clear di docEdit/docEditModify o resetQuery di query)
	
	public abstract String asQuery(); // costruzione di una eventuale query di ricerca dai campi gestiti dall'addon
	
	public String getTemplate() {
		return this.template;
	}
	
	public String getStatusTemplate() {
		return this.statusTemplate;
	}
	
	public Page getHost() {
		return host;
	}

	public void setHost(Page host) {
		this.host = host;
	}
	
}
