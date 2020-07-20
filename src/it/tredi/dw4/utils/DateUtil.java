/**
 * Creata il 16/07/2012
 */
package it.tredi.dw4.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

/**
 * @author Carmine Zappavigna
 */
public class DateUtil {
	protected static Logger log = Logger.getRootLogger();

	public static String getCurrentDateNorm() {
		return new SimpleDateFormat("yyyyMMdd").format(new Date());
	}

	public static String getCurrentDateNorm(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	public static String getDateNorm(Date date) {
		return new SimpleDateFormat("yyyyMMdd").format(date);
	}

	public static String getLongDate(Date date) {
		return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}

	public static Date getDateByDateNorm(String norm) {
		if (null == norm)
			return null;
		Date retValue = null;
		try {
			retValue = new SimpleDateFormat("yyyyMMdd").parse(norm);
		} catch (Exception e) {
			log.warn(e, e);
			log.debug(e, e);
		}
		return retValue;
	}

	public static Date getDateByLong(String norm) {
		if (null == norm)
			return null;
		Date retValue = null;
		try {
			retValue = new SimpleDateFormat("dd/MM/yyyy").parse(norm);
		} catch (Exception e) {
			log.warn(e, e);
			log.debug(e, e);
		}
		return retValue;
	}
	
	public static Date getDateByString(String norm){
		if ( null == norm ) return null;
		if (norm.length() == 10) return getDateByLong(norm);
		if (norm.length() == 8) return getDateByDateNorm(norm);
		else return null;
	}

	public static String getCurrentTimeNorm() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	public static String getTimeNorm(Date date) {
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}

	public static Date getDateByTimeNorm(String norm) {
		Date retValue = null;
		try {
			retValue = new SimpleDateFormat("yyyyMMddHH:mm:ss").parse(norm);
		} catch (Exception e) {
			log.warn(e, e);
			log.debug(e, e);
		}
		return retValue;
	}

	/**
	 * Verifica se la stringa passata corrisponde ad una data corretta nel formato
	 * specificato
	 * 
	 * @param inDate Stringa da verificare
	 * @param format Formato della data da verificare
	 * @return true se la stringa rappresenta una data valida, false altrimenti
	 */
	public static boolean isValidDate(String inDate, String format) {
		if (inDate == null)
			return false;

		SimpleDateFormat dateFormat = new SimpleDateFormat(format);

		if (inDate.trim().length() != dateFormat.toPattern().length())
			return false;

		dateFormat.setLenient(false);

		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	/**
	 * Controlla se la data specificata in solo formato numerico (yyyymmdd, ddmmyyyy, ddmmyy) 
	 * rappresenta una data reale
	 * 
	 * @param inDate Data da verificare in formato stringa
	 * @return true se la stringa rappresenta una data valida, false altrimenti
	 */
	public static boolean checkNumericDate(String inDate) {
		boolean isValid = false;
		
		if (inDate != null && inDate.length() > 0) {
			inDate = inDate.trim();
			try {
				Integer.parseInt(inDate);
			} catch (Exception ex) {
				return false;
			}

			if (inDate.length() == 8) {
				
				// Riconoscimento di una data in formato ddmmyyyy o yyyymmdd
				isValid = isValidDate(inDate, "yyyyMMdd");
				if (!isValid)
					isValid = isValidDate(inDate, "ddMMyyyy");
				
			} else if (inDate.length() == 6) {
				
				// Data nel formato ddmmyy
				isValid = isValidDate(inDate, "ddMMyy");
				
			}
		}
		
		return isValid;
	}
	
	/**
	 * Converte una stringa rappresentativa di una data 
	 * al formato "aaaammgg" (formato extraway) 
	 * 
	 * @param inDate Data da convertire
	 * @param format Formato della data da convertire
	 */
	public static String formatDate2XW(String inDate, String format) {
		String xwDate = "";
		
		if (inDate != null && inDate.length() > 0) {
			try {
				if (format == null || format.length() == 0)
					format = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
				
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				dateFormat.setLenient(false);
			
				return getDateNorm(dateFormat.parse(inDate));
			}
			catch (Exception e) {
				log.debug(e, e);
			}
		}
		
		return xwDate;
	}
	
	/**
	 * Restituisce l'anno corrente
	 */
	public static int getCurrentYear() {
		GregorianCalendar gc = new GregorianCalendar();
		return gc.get(Calendar.YEAR);
	}
	
	/**
	 * Converte due stringhe reppresentative di due date in base al formato indicato
	 * e restituisce la differenza fra le due date.
	 * 
	 * @param from
	 * @param to
	 * @param format
	 * @return
	 */
	public static int getDateDifference(String from, String to, String format) throws Exception {
		if (from != null && !from.equals("") && to != null && !to.equals("")) {
			SimpleDateFormat df = new SimpleDateFormat(format);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");

			int dfrom = new Integer(df2.format(df.parse(from))).intValue();
			int dto = new Integer(df2.format(df.parse(to))).intValue();
			
			return dto - dfrom;
		}
		return 0;
	}
	
	/**
	 * In base ad una data in formato stringa (e il suo formato corrente), restituisce la data in base al 
	 * nuovo formato specificato
	 * 
	 * @param date data in formato stringa
	 * @param currentFormat formato corrente della data 
	 * @param newFormat nuovo formato in base al quale formattare la data
	 * @return
	 */
	public static String changeDateFormat(String date, String currentFormat, String newFormat) {
		return changeDateFormat(date, currentFormat, newFormat, "");
	}
	
	/**
	 * In base ad una data in formato stringa (e il suo formato corrente), restituisce la data in base al 
	 * nuovo formato specificato. Prima di procedere alla conversione del formato verifica la data da convertire in 
	 * base alla regEx passata
	 * 
	 * @param date data in formato stringa
	 * @param currentFormat formato corrente della data 
	 * @param newFormat nuovo formato in base al quale formattare la data
	 * @param regExCheckFormat regular expression tramite la quale verificare la data da convertire (facoltativo)
	 * @return
	 */
	public static String changeDateFormat(String date, String currentFormat, String newFormat, String regExCheckFormat) {
		
		// richiesto il controllo del formato tramite regEx
		if (regExCheckFormat != null && regExCheckFormat.length() > 0) {
			try {
			if (!date.matches(regExCheckFormat))
				return date; // se non viene verificata la regEx si restituisce la stringa passata in input
			}
			catch (Exception e) {
				log.error(e.getMessage(), e);
				return date; // in caso di errore nella regEx si restituisce comunque la stringa passata in input
			}
		}
		
		// conversione della data in base ai formati indicati
		if (date != null && !date.equals("") 
				&& currentFormat != null && !currentFormat.equals("") 
				&& newFormat != null && !newFormat.equals("")) {
			try {
				SimpleDateFormat dfFrom = new SimpleDateFormat(currentFormat);
				SimpleDateFormat dfTo = new SimpleDateFormat(newFormat);
				
				date = dfTo.format(dfFrom.parse(date));
			}
			catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		
		return date;
	}

}
