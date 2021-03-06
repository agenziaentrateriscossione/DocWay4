package it.tredi.dw4.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
 
public class NumRepConverter implements Converter{
 
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
		String value) {
 
		String result = "";
		if (value.contains("-")){
			String last = value.substring(value.lastIndexOf("-")+1);
			String anno = last.substring(0, 4);
			String num = last.substring(4);
			result = new NumConverter().getAsString(null, null, num)+"/"+anno;
		}
		else{
		}
		return result;
	}
 
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
			return (String) getAsObject(context, component, (String)value);
 	}	
}