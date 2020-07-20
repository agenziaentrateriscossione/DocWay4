package it.tredi.dw4.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class CostConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return ValutaConversion.formatValuta(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return ValutaConversion.formatValuta(value.toString());
	}
	
	

}
