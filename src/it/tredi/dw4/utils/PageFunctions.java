package it.tredi.dw4.utils;

import java.net.URLEncoder;

/**
 * Metodi di utility che possono essere utilizzati all'interno delle pagine JSF tramite EL
 * @author mbernardini
 */
public class PageFunctions {

	/**
	 * URL encoding di un valore da utilizzare come parametro GET di un link
	 * @param value valore da codificare
	 * @return
	 */
	public static String urlEncode(String value) {
		try {
			if (value == null)
				value = "";
			value = URLEncoder.encode(value, "UTF-8");
		}
		catch(Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return value;
	}
}
