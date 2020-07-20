package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclLookupFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.Lookup;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;

import org.dom4j.Document;

public class AclLookup extends Lookup {
	private AclLookupFormsAdapter formsAdapter;
	private ArrayList<Titolo> titoli;
	
	public AclLookup() throws Exception {
		this.formsAdapter = new AclLookupFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document domTitoli) {
    	xml = domTitoli.asXML();
    	this.titoli = (ArrayList<Titolo>) XMLUtil.parseSetOfElement(domTitoli, "//titolo", new Titolo());
    }	
	
	public AclLookupFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public void setTitoli(ArrayList<Titolo> titoli) {
		this.titoli = titoli;
	}

	public ArrayList<Titolo> getTitoli() {
		return titoli;
	}
	
}
