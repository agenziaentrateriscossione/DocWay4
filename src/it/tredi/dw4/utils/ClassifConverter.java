package it.tredi.dw4.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import it.tredi.dw4.beans.ClassifFormatManager;
 
public class ClassifConverter implements Converter{
 
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value)  {
		//Correzione bug eccezione in visualizzazione classificazione: "08/05 - Verifica veicoli commerciali circolanti nella comunita' (direttiva n 2000/3/CEE)"
		String result = "";
		try {
			Logger.debug("ClassifConverter.getAsObject(): input value = " + value);
			
			String classifFormat = (String) component.getAttributes().get("classifFormat");
			Logger.debug("ClassifConverter.getAsObject(): classifFormat by attribute = " + classifFormat);
			
			// in caso di classificazione non specificata da attributo viene caricata quella globale definita a livello di applicazione
			if (classifFormat == null || classifFormat.isEmpty()) 
				classifFormat = ClassifFormatManager.getInstance().getClassifFormat();
			
			result = ClassifUtil.formatClassif(value, classifFormat);
		}
		catch (Exception e) {
			Logger.warn("ClassifConverter.getAsObject(): conversion failed (" + e.getMessage() + ")... use " + value);
			
			//lo mostro com'e'
			result = value;
		}
		return result;
		
		/*
		String result = "";
		if (value != null && value.length() > 0) {
			String[] parts = value.split("/");
			if (parts.length > 1){
				String first = parts[0];
				int roman = Integer.parseInt(first);
				result = RomanConversion.binaryToRoman(roman);
				for (int i= 1; i< parts.length; i++) {
					String part = parts[i];
					if (parts.length == i+1 && part.contains(" ")){
						String num = part.substring(0, part.indexOf(" "));
						String label = part.substring(part.indexOf(" "));
						
						int number = Integer.parseInt(num);
						result += "/"+String.valueOf(number)+label;
					}
					else{
						int number = Integer.parseInt(part);
						result += "/"+String.valueOf(number);
					}
				}
			}
			else if (value.contains(" ")){
				String first = value.substring(0, value.indexOf(" "));
				String label = value.substring(value.indexOf(" ")+1);
				
				int roman = Integer.parseInt(first);
				result = RomanConversion.binaryToRoman(roman)+" "+label;
			}
		}
		return result;*/
	}
 
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
			return (String) getAsObject(context, component, (String)value);
 	}	
}