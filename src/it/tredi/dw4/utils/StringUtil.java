package it.tredi.dw4.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	public static final String EURO_HTML_ENTITY = "&#x80;";
	public static final String EURO_HTML_ENTITY_2 = "&#x020ac;";
	public static final String APICE_SINGOLO_APERTO_HTML_ENTITY = "&#x91;";
	public static final String APICE_SINGOLO_CHIUSO_HTML_ENTITY = "&#x92;";
	public static final String VIRGOLETTE_APERTE_HTML_ENTITY = "&#x93;";
	public static final String VIRGOLETTE_CHIUSE_HTML_ENTITY = "&#x94;";
	public static final String BULLET_HTML_ENTITY = "&#x95;";
	public static final String EN_DASH_HTML_ENTITY = "&#x96;";
	public static final String EM_DASH_HTML_ENTITY = "&#x97;";
	public static final String TILDE_HTML_ENTITY = "&#x98;";
	
	public static final String EURO_CODE = "\u20ac"; 
	public static final String EURO_CODE_2 = "\u0080";
	public static final String APICE_SINGOLO_APERTO_CODE = "\u0091";
	public static final String APICE_SINGOLO_CHIUSO_CODE = "\u0092";
	public static final String VIRGOLETTE_APERTE_CODE = "\u0093";
	public static final String VIRGOLETTE_CHIUSE_CODE = "\u0094";
	public static final String BULLET_CODE = "\u0095";
	public static final String EN_DASH_CODE = "\u0096";
	public static final String EM_DASH_CODE = "\u0097";
	public static final String TILDE_CODE = "\u0098";

	public static String replace(String str, String oldSub, String newSub) {
		return replace(str, oldSub, newSub, 0);
	}

	public static String replace(String str, String oldSub, String newSub,
			int fromIndex) {

		if (str == null) {
			return null;
		}
		if ((oldSub == null) || oldSub.length() == 0) {
			return str;
		}
		if (newSub == null) {
			newSub = "";
		}

		int y = str.indexOf(oldSub, fromIndex);

		if (y >= 0) {
			StringBuffer sb = new StringBuffer();

			int length = oldSub.length();
			int x = 0;

			while (x <= y) {
				sb.append(str.substring(x, y));
				sb.append(newSub);

				x = y + length;
				y = str.indexOf(oldSub, x);
			}

			sb.append(str.substring(x));

			return sb.toString();
		} else {
			return str;
		}
	}

	public static String substringAfter(String str, String separator) {
		if (str == null || str.length() == 0) {
			return "";
		}
		if (separator == null) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return "";
		}
		return str.substring(pos + separator.length());
	}

	/**
	 * Verifica se la stringa passata corrisponde ad un numero intero
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isNumber(String number) {
		try {
			Integer.parseInt(number);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	/**
	 * Converte la stringa passata in interno. In caso di errore o
	 * stringa vuota viene restituito 0
	 * 
	 * @param number
	 * @return
	 */
	public static int intValue(String number) {
		try {
			if (number == null || number.equals(""))
				return 0;
			
			return Integer.parseInt(number);
		} catch (Exception ex) {
			return 0;
		}
	}
	
	/**
	 * Verifica se la stringa passata corrisponde ad un numero decimale
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isDecimal(String number) {
		try {
			Float.parseFloat(number);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Si occupa di aggiungere un carattere ripetuto come prefisso di una
	 * stringa fino al raggiungimento della lunghezza richiesta. Utile per
	 * l'elaborazione del numero di protocollo
	 * 
	 * @param stringa Stringa da elaborare (alla quale appendere il prefisso)
	 * @param carattere Carattere da ripetere come prefisso della stringa
	 * @param lunghezza Lunghezza della stringa finale
	 * @return
	 */
	public static String fillString(String stringa, String carattere, int lunghezza) {
		int start = stringa.length();
		for (int i = start; i < lunghezza; i++)
			stringa = carattere + stringa;
		return stringa;
	}

	/**
	 * Conta le occorrenze di text all'interno della stringa str
	 * 
	 * @param str
	 * @param text
	 * @return
	 */
	public static int count(String str, String text) {
		if ((str == null) || (text == null)) {
			return 0;
		}

		int count = 0;

		int pos = str.indexOf(text);

		while (pos != -1) {
			pos = str.indexOf(text, pos + text.length());
			count++;
		}

		return count;
	}
	
	/**
	 * Esegue lo split della stringa s in base al delimitatore delimiter
	 * @param s
	 * @param delimiter
	 * @return
	 */
	public static String[] split(String s, String delimiter) {
		if (s == null || delimiter == null) {
			return new String[0];
		}
		s = s.trim();

		if (!s.endsWith(delimiter)) {
			s += delimiter;
		}

		if (s.equals(delimiter)) {
			return new String[0];
		}

		List<String> nodeValues = new ArrayList<String>();

		if (delimiter.equals("\n") || delimiter.equals("\r")) {
			try {
				BufferedReader br = new BufferedReader(new StringReader(s));

				String line = null;

				while ((line = br.readLine()) != null) {
					nodeValues.add(line);
				}

				br.close();
			} catch (IOException ioe) {
				Logger.error(ioe.getMessage(), ioe);
			}
		} else {
			int offset = 0;
			int pos = s.indexOf(delimiter, offset);

			while (pos != -1) {
				nodeValues.add(s.substring(offset, pos));

				offset = pos + delimiter.length();
				pos = s.indexOf(delimiter, offset);
			}
		}

		return (String[]) nodeValues.toArray(new String[0]);
	}

	/**
	 * Verifica che la stringa passata non contenga al suo interno lo stesso caratteri
	 * ripetuto n volte
	 * @param str Stringa da verificare
	 * @param numRipetizioni Numero massimo di ripetizioni consecutive per uno stesso carattere
	 * @return true se la stringa non contiene caratteri ripetuti, false altrimenti
	 */
	public static boolean containsRipetizioni(String str, int numRipetizioni) {
		boolean ripetizioneInusuale = false;
		if (str != null && numRipetizioni > 0) {
			char chr = 0xff;
			int i = 0;
			int count = 0;
			while (i<str.length() && !ripetizioneInusuale) {
				if (str.charAt(i) == chr) {
					count = count + 1;
					if (count >= numRipetizioni) 
						ripetizioneInusuale = true;
				}
				else {
					count = 0;
					chr = str.charAt(i);
					if (chr >= '0' && chr <= '9')
						chr = 0xff;
				}
				i = i + 1;
			}
		}
		return ripetizioneInusuale;
	}
	
	/**
	 * Converte la stringa passata come parametro in booleano
	 */
	public static boolean booleanValue(String str) {
		if (str != null) {
			if (str.toLowerCase().equals("si") || str.toLowerCase().equals("yes") 
					|| str.toLowerCase().equals("vero") || str.equals("1"))
				str = "true";
			
			return Boolean.parseBoolean(str);
		}
		return false;
	}
	
	/**
	 * Verifica se la stringa passata fa riferimento ad un indirizzo email
	 * valido
	 */
	public static boolean isValidEmailAddress(String str) {
		String email_pattern = 
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"; // regex di validazione di un indirizzo email
		
		Pattern pattern = Pattern.compile(email_pattern);
		
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	/**
	 * data una stringa converte le entity che fanno riferimento a caratteri speciali
	 * con il relativo codice unicode 
	 */
	public static String convertSpecialCharsEntitiesToCode(String str) {
		if (str != null && str.length() > 0) {
			str = str.replaceAll(StringUtil.EURO_HTML_ENTITY, StringUtil.EURO_CODE);
			str = str.replaceAll(StringUtil.APICE_SINGOLO_APERTO_HTML_ENTITY, StringUtil.APICE_SINGOLO_APERTO_CODE);
			str = str.replaceAll(StringUtil.APICE_SINGOLO_CHIUSO_HTML_ENTITY, StringUtil.APICE_SINGOLO_CHIUSO_CODE);
			str = str.replaceAll(StringUtil.VIRGOLETTE_APERTE_HTML_ENTITY, StringUtil.VIRGOLETTE_APERTE_CODE);
			str = str.replaceAll(StringUtil.VIRGOLETTE_CHIUSE_HTML_ENTITY, StringUtil.VIRGOLETTE_CHIUSE_CODE);
			str = str.replaceAll(StringUtil.BULLET_HTML_ENTITY, StringUtil.BULLET_CODE);
			str = str.replaceAll(StringUtil.EN_DASH_HTML_ENTITY, StringUtil.EN_DASH_CODE);
	        str = str.replaceAll(StringUtil.EM_DASH_HTML_ENTITY, StringUtil.EM_DASH_CODE);
	        str = str.replaceAll(StringUtil.TILDE_HTML_ENTITY, StringUtil.TILDE_CODE);
		}
		return str;
	}
	
	/**
	 * data una stringa converte i codici unicode che fanno riferimento a caratteri speciali
	 * con le relative entity
	 * @param str
	 * @return
	 */
	public static String convertSpecialCharsCodeToEntities(String str) {
		if (str != null && str.length() > 0) {
			str = str.replaceAll("(" + StringUtil.EURO_CODE + "|" + StringUtil.EURO_CODE_2 + ")", StringUtil.EURO_HTML_ENTITY);
			str = str.replaceAll(StringUtil.APICE_SINGOLO_APERTO_CODE, StringUtil.APICE_SINGOLO_APERTO_HTML_ENTITY);
			str = str.replaceAll(StringUtil.APICE_SINGOLO_CHIUSO_CODE, StringUtil.APICE_SINGOLO_CHIUSO_HTML_ENTITY);
			str = str.replaceAll(StringUtil.VIRGOLETTE_APERTE_CODE, StringUtil.VIRGOLETTE_APERTE_HTML_ENTITY);
			str = str.replaceAll(StringUtil.VIRGOLETTE_CHIUSE_CODE, StringUtil.VIRGOLETTE_CHIUSE_HTML_ENTITY);
			str = str.replaceAll(StringUtil.BULLET_CODE, StringUtil.BULLET_HTML_ENTITY);
			str = str.replaceAll(StringUtil.EN_DASH_CODE, StringUtil.EN_DASH_HTML_ENTITY);
	        str = str.replaceAll(StringUtil.EM_DASH_CODE, StringUtil.EM_DASH_HTML_ENTITY);
	        str = str.replaceAll(StringUtil.TILDE_CODE, StringUtil.TILDE_HTML_ENTITY);
		}
		return str;
	}
	
	/** @author dpranteda 16/12/2014
	 *  @return Stringa senza gli 0 iniziali
	 * */
	public static String trimzero(String str) {
		if(str != null && !str.isEmpty())
		{
			if(str.startsWith("0") && str.length() > 1)
			{
				str = trimzero(str.substring(1));
				return str;
			}else
			{
				return str;
			}
		}else{
				return "";
			}
	}
	
	/**
	 * @author dpranteda 15/01/2015
	 * @param value stringa da testare
	 * @return stringa formattata in HH:MM:SS oppure ""
	 */
	public static String timeValue(String value) {
		if (value.length() == 5){
			String HH = value.substring(0,2);
			String mm = value.substring(3);
			try{
				Integer.parseInt(HH); 
				Integer.parseInt(mm);
				return value + ":00";
			}catch(Exception e){
				return "";
			}
		}else if(value.length() == 8){
			String HH = value.substring(0,2);
			String mm = value.substring(3,5);
			String ss = value.substring(6);
			try{
				Integer.parseInt(HH); 
				Integer.parseInt(mm);
				Integer.parseInt(ss);
				return value;
			}catch(Exception e){
				return "";
			}
		}
		else
			return "";
	}
	
	/**
	 * @author dpranteda 21/01/2015
	 * @param value stringa contenente \ o '
	 * @return stringa con quoting	dei caratteri \ ' - vedi xsl */
	public static String quoteString(String text){
		String quotedString = StringUtil.replace(text, "\\", "\\\\");
		quotedString = StringUtil.replace(quotedString, "\'", "\\'");
		return quotedString;
	}
}
