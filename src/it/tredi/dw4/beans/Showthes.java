package it.tredi.dw4.beans;

import it.tredi.dw4.adapters.ThesFormsAdapter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;

public abstract class Showthes extends Page {

	protected String windowTitle = "";
	
	protected boolean active = false;
	
	protected Object model;
	
	public abstract void init(Document dom);
	
	public abstract ThesFormsAdapter getFormsAdapter();
	
    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}
	
	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String titolo) {
		this.windowTitle = titolo;
	}
	
	public String close() {
		this.active = false;
		return null;
	}
		
	public void confirm(HashMap<String, String> campi) throws Exception {
		if (campi != null && !campi.isEmpty())
			fillFields(campi);
	}

	@SuppressWarnings("rawtypes")
	private void fillFields(HashMap<String, String> campi) throws Exception {
		Iterator it = campi.keySet().iterator(); 
		while(it.hasNext()) { 
			String xpath = (String) it.next();
			String value = (String) campi.get(xpath); 
	
			String []splitL = xpath.split("\\.");
			Object obj = model;
			for (int splitLindex = 0; splitLindex < splitL.length - 1; splitLindex++) {
				String propertyName = splitL[splitLindex];
				String index = "";
				if (propertyName.startsWith("@"))
					propertyName = propertyName.substring(1);
				if (propertyName.endsWith("]")){
					index = propertyName.substring(propertyName.indexOf("[")+1, propertyName.length()-1);
					propertyName = propertyName.substring(0, propertyName.indexOf("["));
				}
				if (propertyName.length() > 0) {
					Method getter = new PropertyDescriptor(propertyName, obj.getClass()).getReadMethod();
					obj = getter.invoke(obj);
					
					if (!"".equals(index)){
						obj = ((ArrayList)obj).get(Integer.valueOf(index));
					}
				}
			}
			String propertyName = splitL[splitL.length - 1];
			if (propertyName.startsWith("@"))
				propertyName = propertyName.substring(1);			
			Method setter = new PropertyDescriptor(propertyName, obj.getClass()).getWriteMethod();
			obj = setter.invoke(obj, value);
		}		
	}
	
}