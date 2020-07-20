package it.tredi.dw4.acl.beans;

import it.tredi.dw4.acl.adapters.AclIndexFormsAdapter;
import it.tredi.dw4.acl.model.Vocabolario;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.beans.Showindex;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;

import org.dom4j.Document;

public class AclShowindex extends Showindex {
	private AclIndexFormsAdapter formsAdapter;
	
	private String xml = "";
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public AclShowindex() throws Exception {
		this.formsAdapter = new AclIndexFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
		this.vocabolariSelezionati = new HashMap<String, Vocabolario>();
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document domTitoli) {		
    	xml = domTitoli.asXML();
		this.vocabolari = (ArrayList<Vocabolario>) XMLUtil.parseSetOfElement(domTitoli, "//vocabolario", new Vocabolario());
		
		// TODO Soluzione migliorabile con passaggio di parametri (ancora non funzionante)
		if (vocabolari != null && vocabolari.size() > 0) {
			for (int i=0; i<vocabolari.size(); i++) {
				Vocabolario vocabolario = (Vocabolario) vocabolari.get(i);
				if (vocabolario != null && vocabolariSelezionati != null && vocabolariSelezionati.containsKey(vocabolario.getChiave()))
					vocabolario.setSelezionato(true);
			}
		}
    }	
	
	public AclIndexFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

}
