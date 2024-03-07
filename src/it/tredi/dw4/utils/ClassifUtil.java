package it.tredi.dw4.utils;

import java.util.Arrays;
import java.util.List;

import it.tredi.dw4.beans.ClassifFormatManager;

public class ClassifUtil {
	
	private static final String[] ALPHABET = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

	/**
	 * Si occupa di formattare il codice del titolario di 
	 * classificazione (es. lookup su titolario di classificazione)
	 *  
	 * @param val Codice da formattare
	 * @return Codice del titolario di classificazione formattato
	 */
	public static String formatNumberClassifCode(String val) {
		String out = "";
		
		String classifFormat = ClassifFormatManager.getInstance().getClassifFormat();
		if (classifFormat != null && !classifFormat.isEmpty()) {
			try {
				val = ClassifUtil.unformatCodClassif(val, classifFormat);
			}
			catch (Exception e) {
				Logger.warn("ClassifUtil.formatNumberClassifCode(): unable to convert " + val + " from " + classifFormat + " to numeric format. Maybe already in numeric format...");
			}
		}
		
		if (val != null && val.length() > 0) {
			String[] splitCode = StringUtil.split(val, "/");
			
			if (splitCode != null && splitCode.length > 0) {
				for (int i=0; i<splitCode.length; i++) {
					if (splitCode[i].length() == 1)
						splitCode[i] = "0" + splitCode[i];
					
					out += "/" + splitCode[i];
				}
			}
			
			out = out.substring(1);
		}
		
		//}
			
		return out;
	}
	
	/**
	 * Esegue la formattazione di un codice di classificazione in base alla configurazione dell'applicativo (numero romano, lettere, ecc.)
	 * @param cod
	 * @param classifFormat
	 * @return
	 * @throws Exception
	 */
	public static String formatCodClassif(String cod, String classifFormat) throws Exception {
		if (classifFormat == null || classifFormat.equals(""))
			classifFormat = "R/D";
		String[] formatoClassif = classifFormat.split("/");
		
		String out = cod;
		if (cod.length() > 0) {
			String[] arrCodici = StringUtil.split(cod, "/");
			if (arrCodici != null && arrCodici.length > 0) {
				for (int i=0; i<arrCodici.length; i++) {
					if (i == 0) {
						if (formatoClassif[0].equals("A"))
							out = classifToA(arrCodici[i]);
						else if (formatoClassif[0].equals("AA"))
							out = classifToAA(arrCodici[i]);
						else if (formatoClassif[0].equals("D"))
							out = classifToD(arrCodici[i]);
						else if (formatoClassif[0].equals("DD"))
							out = classifToDD(arrCodici[i]);
						else // R
							out = classifToR(arrCodici[i]);
					}
					else {
						if (formatoClassif[1].equals("A"))
							out = out + "/" + classifToA(arrCodici[i]);
						else if (formatoClassif[1].equals("AA"))
							out = out + "/" + classifToAA(arrCodici[i]);
						else if (formatoClassif[1].equals("D"))
							out = out + "/" + classifToD(arrCodici[i]);
						else if (formatoClassif[1].equals("DD"))
							out = out + "/" + classifToDD(arrCodici[i]);
						else // R
							out = out + "/" + classifToR(arrCodici[i]);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * Ripristina la formattazione numerica di un codice di classificazione data la formattazione definita a livello di 
	 * applicativo (numero romano, lettere, ecc.)
	 * @param cod
	 * @param classifFormat
	 * @return
	 * @throws Exception
	 */
	public static String unformatCodClassif(String cod, String classifFormat) throws Exception {
		if (classifFormat == null || classifFormat.equals(""))
			classifFormat = "R/D";
		String[] formatoClassif = classifFormat.split("/");
		
		String out = cod;
		if (cod.length() > 0) {
			String[] arrCodici = StringUtil.split(cod, "/");
			if (arrCodici != null && arrCodici.length > 0) {
				for (int i=0; i<arrCodici.length; i++) {
					if (i == 0) {
						if (formatoClassif[0].equals("A"))
							out = classifFromA(arrCodici[i]);
						else if (formatoClassif[0].equals("AA"))
							out = classifFromAA(arrCodici[i]);
						else if (formatoClassif[0].equals("D"))
							out = classifFromD(arrCodici[i]);
						else if (formatoClassif[0].equals("DD"))
							out = classifFromDD(arrCodici[i]);
						else // R
							out = classifFromR(arrCodici[i]);
					}
					else {
						if (formatoClassif[1].equals("A"))
							out = out + "/" + classifFromA(arrCodici[i]);
						else if (formatoClassif[1].equals("AA"))
							out = out + "/" + classifFromAA(arrCodici[i]);
						else if (formatoClassif[1].equals("D"))
							out = out + "/" + classifFromD(arrCodici[i]);
						else if (formatoClassif[1].equals("DD"))
							out = out + "/" + classifFromDD(arrCodici[i]);
						else // R
							out = out + "/" + classifFromR(arrCodici[i]);
					}
				}
			}
		}
		return out;
	}
	
	/**
	 * Dato un titolo di classificazione esegue la conversione dei codici (numero romano
	 * per il primo ed eliminazione del carattere 0 come prefisso di tutti gli altri 
	 * codici
	 * 
	 * @param classif
	 * @param classifFormat
	 * @return
	 */
	public static String formatClassif(String classif, String classifFormat) throws Exception {
		String title = "";
		
		if (classif != null && classif.length() > 0) {
			int index = classif.indexOf("-");
			if (index != -1) {
				String codFormattato = formatCodClassif(classif.substring(0, index).trim(), classifFormat);
				if (codFormattato != null && !codFormattato.isEmpty())
					title = codFormattato + " - " + classif.substring(index + 1).trim();
				else
					title = classif.substring(index + 1).trim();
			}
			else
				title = classif;
		}
		return title;
	}
	
	/**
	 * Dato un titolo (o codice) di classificazione formattato in base alla configurazione dell'applicativo, restituisce
	 * il relativo valore numerico di classificazione
	 * @param classif
	 * @param classifFormat
	 * @return
	 * @throws Exception
	 */
	public static String unformatClassif(String classif, String classifFormat) throws Exception {
		String value = "";
		
		if (classif != null && classif.length() > 0) {
			int index = classif.indexOf("-");
			if (index != -1) {
				String codFormattato = unformatCodClassif(classif.substring(0, index).trim(), classifFormat);
				if (codFormattato != null && !codFormattato.isEmpty())
					value = codFormattato + " - " + classif.substring(index + 1).trim();
				else
					value = classif.substring(index + 1).trim();
			}
			else
				value = classif;
		}
		return value;
	}
	
	/**
	 * Conversione di un valore di classificazione in formato D
	 * @param value
	 * @return
	 */
	private static String classifToD(String value) {
		return new Integer(value).toString(); // viene verificata la correttezza dell'intero
	}
	
	/**
	 * Conversione di un valore di classificazione da formato D
	 * @param value
	 * @return
	 */
	private static String classifFromD(String value) {
		return new Integer(value).toString(); // viene verificata la correttezza dell'intero
	}
	
	/**
	 * Conversione di un valore di classificazione in formato DD
	 * @param value
	 * @return
	 */
	private static String classifToDD(String value) {
		String dd = classifToD(value);
		if (dd.length() == 1)
			dd = "0" + dd;
		return dd;
	}
	
	/**
	 * Conversione di un valore di classificazione da formato DD
	 * @param value
	 * @return
	 */
	private static String classifFromDD(String value) {
		String dd = classifFromD(value);
		if (dd.startsWith("0") && !dd.equals("0"))
			dd = dd.substring(1);
		return dd;
	}
	
	/**
	 * Conversione di un valore di classificazione in formato R
	 * @param value
	 * @return
	 */
	private static String classifToR(String value) {
		return RomanConversion.binaryToRoman(new Integer(value).intValue());
	}
	
	/**
	 * Conversione di un valore di classificazione da formato R
	 * @param value
	 * @return
	 */
	private static String classifFromR(String value) {
		return String.valueOf(RomanConversion.valueOf(value));
	}
	
	/**
	 * Conversione di un valore di classificazione in formato A
	 * @param value
	 * @return
	 */
	private static String classifToA(String value) {
		int intvalue = new Integer(value).intValue();
	    if (intvalue > ALPHABET.length)
	        return value;
	    else
	    	return ALPHABET[intvalue-1];
	}
	
	/**
	 * Conversione di un valore di classificazione da formato A
	 * @param value
	 * @return
	 */
	private static String classifFromA(String value) {
		if (value != null && value.length() == 1) {
			int pos = Arrays.asList(ALPHABET).indexOf(value.toUpperCase());
			if (pos != -1)
				String.valueOf(pos + 1);
		}
		return value;
	}
	
	/**
	 * Conversione di un valore di classificazione in formato AA
	 * @param value
	 * @return
	 */
	private static String classifToAA(String value) {
		int intvalue = new Integer(value).intValue();
		int count = ALPHABET.length;
		if (intvalue > count*count)
			return value;
		if (intvalue == 0) 
			return "0";
		
		int first = (intvalue-1) / count;
		int second = (intvalue-1) % count;
		return classifToA((first+1)+"") + classifToA((second+1)+"");
	}
	
	/**
	 * Conversione di un valore di classificazione in formato AA
	 * @param value
	 * @return
	 */
	private static String classifFromAA(String value) {
		if (value != null && value.length() == 2) {
			List<String> chars = Arrays.asList(ALPHABET);
			int num1 = chars.indexOf(value.substring(0, 1).toUpperCase());
			int num2 = chars.indexOf(value.substring(1, 2).toUpperCase());
			
			if (num1 != -1 && num2 != -1)
				value = String.valueOf((num1 * ALPHABET.length) + num2 + 1);
		}
		return String.valueOf(value);
	}
	
	/**
	 * Conversione di un valore di classificazione su uno specifico formato
	 * @param value
	 * @param format
	 * @return
	 */
	public static String classifNumToFormat(String value, String format) {
		if (value != null && !value.isEmpty()) {
			if (format == null || format.isEmpty())
				format = "R";
			
			if (format.equals("D"))
				value = classifToD(value);
			else if (format.equals("DD"))
				value = classifToDD(value);
			else if (format.equals("A"))
				value = classifToA(value);
			else if (format.equals("AA"))
				value = classifToAA(value);
			else // R
				value = classifToR(value);
		}
		return value;
	}
	
	/**
	 * Ritorna il formato del codice di classificazione da impostare, splittato su primo livello e successivi
	 * @return
	 */
	public static String[] getSplittedClassifFormat() {
		String classifFormat = ClassifFormatManager.getInstance().getClassifFormat();
		if (classifFormat == null || classifFormat.equals(""))
			classifFormat = "R/D";
		return classifFormat.split("/");
	}
	
	/**
	 * Verifica se il formato del codice di classificazione e' corretto (##/##)
	 * 
	 * @param cod codice di classificazione da verificare
	 * @return true se il formato e' corretto, false altrimenti
	 */
	public static boolean isClassifCodCorrect(String cod) {
		boolean isCorrect = false;
		if (cod != null && cod.length() > 0) {
			String regex = "\\d{2}/\\d{2}(/\\d{2})*";
			if (cod.matches(regex))
				isCorrect = true;
		}
		return isCorrect;
	}
}
