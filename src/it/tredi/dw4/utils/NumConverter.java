package it.tredi.dw4.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
 
public class NumConverter implements Converter{
 
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
 		int integer = Integer.parseInt(value);
		return integer;
	}
 
	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		try {
			int integer = Integer.parseInt((String)value);
			return String.valueOf(integer);
		}
		catch(NumberFormatException e) {
			Logger.warn("NumConverter.getAsString(): conversion failed (" + e.getMessage() + ")... use " + value.toString());
			return value.toString();
		}
	}	
}