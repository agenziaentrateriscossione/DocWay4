package it.tredi.dw4.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import it.tredi.dw4.beans.ClassifFormatManager;
 
public class CodClassifConverter implements Converter{
 
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value)  {
		//Correzione bug eccezione in visualizzazione classificazione: "08/05 - Verifica veicoli commerciali circolanti nella comunita' (direttiva n 2000/3/CEE)"
		String result = "";
		try {
			Logger.debug("CodClassifConverter.getAsObject(): input value = " + value);
			
			String classifFormat = (String) component.getAttributes().get("classifFormat");
			Logger.debug("CodClassifConverter.getAsObject(): classifFormat by attribute = " + classifFormat);
			
			// in caso di classificazione non specificata da attributo viene caricata quella globale definita a livello di applicazione
			if (classifFormat == null || classifFormat.isEmpty()) 
				classifFormat = ClassifFormatManager.getInstance().getClassifFormat();
			
			result = ClassifUtil.formatCodClassif(value, classifFormat);
		}
		catch (Exception e) {
			Logger.warn("CodClassifConverter.getAsObject(): conversion failed (" + e.getMessage() + ")... use " + value);
			
			//lo mostro com'e'
			result = value;
		}
		return result;
	}
 
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
			return (String) getAsObject(context, component, (String)value);
 	}	
}