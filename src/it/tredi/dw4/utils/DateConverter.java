package it.tredi.dw4.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
 
public class DateConverter implements Converter {
 
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null || value.length() == 0) 
			return "";
		
		if (value.length() == 8)
			return value.substring(6)+"/"+value.substring(4, 6)+"/"+value.substring(0, 4);
		else
			return value;
	}
 
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
 
		if (((String)value).length() == 8)
			return ((String)value).substring(6)+"/"+((String)value).substring(4, 6)+"/"+((String)value).substring(0, 4);
		else
			return (String)value;
	}	
}