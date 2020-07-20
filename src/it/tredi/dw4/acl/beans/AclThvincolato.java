package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclThesauroFormsAdapter;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.Thvincolato;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;

import org.dom4j.Document;

public class AclThvincolato extends Thvincolato {
	private AclThesauroFormsAdapter formsAdapter;
	private ArrayList<Titolo> titoli;
	
	private String xml = "";
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public AclThvincolato() throws Exception {
		this.formsAdapter = new AclThesauroFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document domTitoli) {		
    	xml = domTitoli.asXML();
    	this.titoli = (ArrayList<Titolo>) XMLUtil.parseSetOfElement(domTitoli, "//titolo", new Titolo());
    }	
	
	public AclThesauroFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public void setTitoli(ArrayList<Titolo> titoli) {
		this.titoli = titoli;
	}

	public ArrayList<Titolo> getTitoli() {
		return titoli;
	}
	
}
