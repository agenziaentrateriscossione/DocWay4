package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclDocumentFormsAdapter;
import it.tredi.dw4.acl.model.Comune;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;

import javax.faces.event.ComponentSystemEvent;

import org.dom4j.Document;

public class ShowdocComune extends AclShowdoc {
	private AclDocumentFormsAdapter formsAdapter;
	private Comune comune;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ShowdocComune() throws Exception {
		this.formsAdapter = new AclDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	comune = new Comune();
    	comune.init(domDocumento);
    }	
	
	public AclDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public void reload(ComponentSystemEvent event) throws Exception {
		reload();
	}
	
	public void reload() throws Exception {
		super._reload("showdoc@comune");
	}

	public void setComune(Comune comune) {
		this.comune = comune;
	}

	public Comune getComune() {
		return comune;
	}
	
}
