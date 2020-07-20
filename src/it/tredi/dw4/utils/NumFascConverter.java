package it.tredi.dw4.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class NumFascConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		String result = "";
		
		if (value != null && !value.equals("")) {
			if (value.contains("/")) {
				// caso di numero formattato in questo modo: 2013-3DINBOL-05/06.00010
				// viene trasformato in: 2013-V/6.10
				
				String first = value.substring(0, value.indexOf("/"));
				String last = value.substring(value.indexOf("/") + 1);
				String[] tmp = first.split("-");
				int roman = Integer.parseInt(tmp[2]);
				result = tmp[0] + "-" + RomanConversion.binaryToRoman(roman) + "/";
				tmp = last.split("\\.");
				for (int i = 0; i < tmp.length; i++) {
					String num = tmp[i];
					result += new NumConverter().getAsString(null, null, num) + ".";
				}
				if (result.endsWith("."))
					result = result.substring(0, result.length() - 1);
				
			} else {
				// caso di numero formattato in questo modo: 2013-3DINBOL-02.00002
				// viene trasformato in: 2013-2.2
				
				String[] tmp = value.split("-");
				result = tmp[0] + "-";
				tmp = tmp[2].split("\\.");
				for (int i = 0; i < tmp.length; i++) {
					String num = tmp[i];
					result += new NumConverter().getAsString(null, null, num) + ".";
				}
				if (result.endsWith("."))
					result = result.substring(0, result.length() - 1);
			}
		}
		
		return result;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return (String) getAsObject(context, component, (String) value);
	}
}