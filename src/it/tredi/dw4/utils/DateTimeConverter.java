package it.tredi.dw4.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
 
public class DateTimeConverter implements Converter{
 
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null || value.length() == 0) 
			return "";
		
		return convertDateTime(value);
	}
 
	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
 
		return convertDateTime((String) value);
	}
	
	/**
	 * conversione di una data e ora passata in input in formato YYYYMMDDHHmmSS
	 * @param dateTime
	 * @return
	 */
	private String convertDateTime(String dateTime) {
		if (dateTime.length() == 14) {
			String formatoData = Const.DEFAULT_DATE_TIME_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
			
			String out = dateTime;
			try {
				Date temp = new SimpleDateFormat("yyyyMMddHHmmSS").parse(dateTime);
				out = new SimpleDateFormat(formatoData).format(temp);
			} 
			catch (Throwable t) {
				Logger.error(t.getMessage(), t);
			}
			
			return out;
		}
		else if (dateTime.length() == 29) {
			String formatoData = Const.DEFAULT_DATE_TIME_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
			
			String out = dateTime;
			try {
				SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ITALIAN);
				String date = dateTime.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
				
				Date temp =ISO8601DATEFORMAT.parse(date);
				out = new SimpleDateFormat(formatoData).format(temp);
			} 
			catch (Throwable t) {
				Logger.error(t.getMessage(), t);
			}
			
			return out;
		}
		else
			return dateTime;
	}
}