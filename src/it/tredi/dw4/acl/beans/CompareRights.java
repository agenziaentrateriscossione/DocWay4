package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclCompareRightsFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.model.comparison.Comparison;

import org.dom4j.Document;

public class CompareRights extends Page {

	private String xml = "";
	private Comparison comparison = new Comparison();
	
	private boolean active = false;
	
	private AclCompareRightsFormsAdapter formsAdapter;
	
	public void init(Document dom) {
    	xml = dom.asXML();
    	
    	comparison.init(dom);
    }	
	
	public CompareRights() throws Exception {
		this.formsAdapter = new AclCompareRightsFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
		
	@Override
	public FormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String close() {
		this.active = false;
		return null;
	}
	
	public Comparison getComparison() {
		return comparison;
	}

	public void setComparison(Comparison comparison) {
		this.comparison = comparison;
	}
	
}
