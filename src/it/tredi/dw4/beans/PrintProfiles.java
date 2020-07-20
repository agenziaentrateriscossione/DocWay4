package it.tredi.dw4.beans;

import it.tredi.dw4.adapters.PrintProfilesFormsAdapter;

import org.dom4j.Document;

public abstract class PrintProfiles extends Page {

	public abstract void init(Document dom);
	
	public abstract PrintProfilesFormsAdapter getFormsAdapter();
	
}
