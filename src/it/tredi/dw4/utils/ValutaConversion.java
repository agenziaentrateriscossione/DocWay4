package it.tredi.dw4.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Conversione ed elaborazioni su numeri di valuta
 * 
 * @author mbernardini
 */
public class ValutaConversion {

	private final static String[] conv = {"uno", "due", "tre", "quattro", "cinque", "sei", "sette", "otto", "nove", "dieci", "undici", "dodici", "tredici", 
			"quattordici", "quindici", "sedici", "diciasette", "diciotto", "diciannove", "venti",
			"ventuno", "trenta", "trentuno", "quaranta", "quarantuno", "cinquanta", "cinquantuno", "sesanta", "sesantuno",
			"settanta", "settantuno", "ottanta", "ottantuno", "novanta", "novantuno", "cento", "mille", "mila", "milione", "milioni",
			"miliardo", "miliardi", "centomila"}; // TODO gestione del multilingua
	
	
	public static void main(String []args) throws Exception {
		String num = "4999999,15";
		
		System.out.println(convertInWords(num, ".", ",", false));
	}
	
	/**
	 * Converte un numero di valuta in double
	 * 
	 * @param num valuta da convertire
	 * @return
	 */
	public static double getDoubleValuta(String num) {
		return getDoubleValuta(num, ".", ",");
	}
	
	/**
	 * Converte un numero di valuta in double
	 * 
	 * @param num valuta da convertire
	 * @param migliaiaSeparator carattere utilizzato come separatore delle migliaia
	 * @param decimalSeparator carattere utilizzato come separatore dei decimali
	 * @return
	 */
	public static double getDoubleValuta(String num, String migliaiaSeparator, String decimalSeparator) {
		try {
			if (num == null || num.equals(""))
				return 0.0;
			
			num = StringUtil.replace(num, migliaiaSeparator, "");
			num = StringUtil.replace(num, decimalSeparator, ".");
			
			return new Double(num).doubleValue();
		}
		catch (Throwable t) {
			Logger.error("Errore in conversione della valuta", t);
			return 0.0;
		}
	}
	
	/**
	 * Formatta il numero di valuta passato (seperatore delle migliaia, 2 decimali)
	 * @param num
	 * @return
	 */
	public static String formatValuta(String num) {
		java.text.NumberFormat nf = java.text.DecimalFormat.getInstance(java.util.Locale.ITALIAN); // TODO recuperare da properties
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		
		return nf.format(new Double(num).doubleValue());
	}
	
	/**
	 * Converte un numero di valuta nel corrispettivo valore 
	 * in stringa
	 * 1.150.000,50 -> unmilionecentocinquantamila/50
	 * 
	 * @param num valuta da convertire
	 * @return stringa che rappresenta il numero passato convertito in parole
	 */
	public static String convertInWords(String num) {
		return convertInWords(num, ".", ",", true);
	}
	
	/**
	 * Converte un numero di valuta nel corrispettivo valore 
	 * in stringa
	 * 1.150.000,50 -> unmilionecentocinquantamila/50
	 * 
	 * @param num valuta da convertire
	 * @param migliaiaSeparator carattere utilizzato come separatore delle migliaia
	 * @param decimalSeparator carattere utilizzato come separatore dei decimali
	 * @return stringa che rappresenta il numero passato convertito in parole
	 */
	public static String convertInWords(String num, String migliaiaSeparator, String decimalSeparator) {
		return convertInWords(num, migliaiaSeparator, decimalSeparator, true);
	}

	/**
	 * Converte un numero di valuta nel corrispettivo valore 
	 * in stringa
	 * 1.150.000,50 -> unmilionecentocinquantamila/50
	 * 
	 * @param num valuta da convertire
	 * @param migliaiaSeparator carattere utilizzato come separatore delle migliaia
	 * @param decimalSeparator carattere utilizzato come separatore dei decimali
	 * @param lowerCase true se l'iniziale deve essere minuscola, false in caso di iniziale maiuscola
	 * @return stringa che rappresenta il numero passato convertito in parole
	 */
	public static String convertInWords(String num, String migliaiaSeparator, String decimalSeparator, boolean lowerCase) {
		if (num == null || num.equals(""))
			return "";
		
		num = StringUtil.replace(num, migliaiaSeparator, "");
		
		// suddivisione della parte intera dai decimali
		int index = num.indexOf(decimalSeparator);
		String intEuro = num;
		String strCents = "00";
		if (index != -1) {
			intEuro = num.substring(0, index);
			
			strCents = num.substring(index+1);
			if (strCents.length() == 0)
				strCents = "00";
			else if (strCents.length() == 1)
				strCents = strCents + "0";
		}
		
		int n = new Integer(intEuro).intValue();
		
		String lettera = "";
		if (n < 21) 
			lettera = getNumVenti(n);
		else if (n >= 21 && n < 100) 
			lettera = getNumCento(n);
		else if (n >= 100 && n < 1000)
			lettera = getNumMille(n);
		else if (n >= 1000 && n < 100000)
			lettera = getNumCentomila(n, 0);
		else if (n >= 100000 && n < 1000000)
			lettera = getNumMilione(n);
		else if (n >= 1000000 && n < 1000000000)
			lettera = getNumMiliardo(n);
		else
			lettera = n + "";

		if (lowerCase)
			return lettera + "/" + strCents;
		else
			return lettera.substring(0, 1).toUpperCase() + lettera.substring(1) + "/" + strCents;
	}
	
	
	private static String getNumVenti(int num) {
		return conv[num - 1];
	}
	
	private static String getNumCento(int num) {
		String numCento = "";
		if (num > 0 && num < 21) {
			numCento = getNumVenti(num);
		}
		else {
			int num1 = num / 10;
			if (num == 21 + (10 * (num1 - 2))) 
				numCento = conv[(21 + (2 * (num1 - 2))) - 1];
			else {
				numCento = conv[(20 + (2 * (num1 - 1) - 2)) - 1];
				int num2 = num - (num1 * 10);
				if (num2 > 0)
					numCento = numCento + conv[num2-1];
			}
		}
		return numCento;
	}
	
	private static String getNumMille(int num) {
		String numMille = "";
		if (num == 100)
			numMille = conv[35];
		else if (num > 100 && num < 200) {
			int num1 = num - 100;
			numMille = conv[35];
			numMille = numMille + getNumCento(num1);
		}
		else if (num >= 200 && num < 1000) {
			int num1 = num / 100;
			numMille = conv[num1-1] + conv[35];
			int num2 = num - (num1 * 100);
			if (num != (100 * num1))
				numMille = numMille + getNumCento(num2);
		}
		return numMille;
	}
	
	private static String getNumCentomila(int num, int flag) {
		String numCentomila = "";
		
		int num1 = num / 1000;
		if (num1 == 1 && num == 1000) {
			if (flag == 0) {
				numCentomila = conv[36];
			}
			else {
				numCentomila = conv[0];
				numCentomila = numCentomila + conv[37];
			}
		}
		else if (num1 == 1 && num != 1000) {
			if (flag == 0) {
				numCentomila = conv[36];
			}
			else {
				numCentomila = conv[0];
				numCentomila = numCentomila + conv[37];
			}
			int num2 = num - 1000;
			if (num2 < 100)
				numCentomila = numCentomila + getNumCento(num2);
			else
				numCentomila = numCentomila + getNumMille(num2);
		}
		else if (num1 > 1 && num1 <= 21) {
			numCentomila = conv[num1-1];
			numCentomila = numCentomila + conv[37];
			int num2 = num - (num1 * 1000);
			if (num2 >= 1 && num2 < 100)
				numCentomila = numCentomila + getNumCento(num2);
			else if (num2 >= 100)
				numCentomila = numCentomila + getNumMille(num2);
		}
		else if (num1 > 21 && num1 < 100) {
			numCentomila = getNumCento(num1);
			numCentomila = numCentomila + conv[37];
			int num2 = num - (num1 * 1000);
			if (num2 >= 1 && num2 < 100)
				numCentomila = numCentomila + getNumCento(num2);
			else
				numCentomila = numCentomila + getNumMille(num2);
		}
		
		return numCentomila;
	}
	
	private static String getNumMilione(int num) {
		String numMilione = "";
		
		int num1 = num / 100000;
		if (num1 == 1 && num == 100000) {
			numMilione = conv[42];
		}
		else if (num1 == 1 && num != 100000) {
			numMilione = conv[35];
			int num2 = num - 100000;
			if (num2 >= 1 && num2 < 100) {
				numMilione = numMilione + conv[37];
				numMilione = numMilione + getNumCento(num2);
			}
			else if (num2 >= 100 && num2 < 1000) {
				numMilione = numMilione + conv[37];
				numMilione = numMilione + getNumMille(num2);
			}
			else {
				numMilione = numMilione + getNumCentomila(num2, 1);
			}
		}
		else if (num1 > 1) {
			numMilione = conv[num1 - 1];
			numMilione = numMilione + conv[35];
			int num2 = num - (100000 * num1);
			if (num2 > 0 && num2 < 22) {
				numMilione = numMilione + conv[37];
				numMilione = numMilione + getNumVenti(num2);
			}
			else if (num2 >= 22 && num2 < 1000) {
				numMilione = numMilione + conv[37];
				numMilione = numMilione + getNumMille(num2);
			}
			else {
				numMilione = numMilione + getNumCentomila(num2, 1);
			}
		}
		
		return numMilione;
	}
	
	private static String getNumMiliardo(int num) {
		String numMiliardo = "";
		
		int num1 = num / 1000000;
		if (num1 == 1 && num == 1000000) {
			numMiliardo = "un" + conv[38]; // TODO gestione del multilingua
		}
		else if (num1 == 1 && num != 1000000) {
			numMiliardo = "un" + conv[38]; // TODO gestione del multilingua
			int num2 = num - 1000000;
			if (num2 >= 1 && num2 < 100) {
				numMiliardo = numMiliardo + getNumCento(num2);
			}
			else if (num2 >= 100 && num2 < 1000) {
				numMiliardo = numMiliardo + getNumMille(num2);
			}
			else if (num2 >= 1000 && num2 < 100000) {
				numMiliardo = numMiliardo + getNumCentomila(num2, 1);
			}
			else {
				numMiliardo = numMiliardo + getNumMilione(num2);
			}
		}
		else if (num1 > 1) {
			if (num1 > 21)
				numMiliardo = getNumCento(num1);
			else
				numMiliardo = conv[num1-1];
			numMiliardo = numMiliardo + conv[39];
			
			int num2 = num - (1000000 * num1);
			if (num2 >= 1 && num2 < 100) {
				numMiliardo = numMiliardo + getNumCento(num2);	
			}
			else if (num2 >= 100 && num2 < 1000) {
				numMiliardo = numMiliardo + getNumMille(num2);
			}
			else if (num2 >= 1000 && num2 < 100000) {
				numMiliardo = numMiliardo + getNumCentomila(num2, 1);
			}
			else {
				numMiliardo = numMiliardo + getNumMilione(num2);
			}
		}
		
		return numMiliardo;
	}
	
	/**
	 * formattazione di un importo (2 decimali) con decimali separati da punto
	 * 
	 * @param value
	 * @return
	 */
	public static String formatImporto(double value) {
		try {
			DecimalFormat df = new DecimalFormat("#.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
			return df.format(value);
		}
		catch (Exception ex) {
			Logger.error("Fattura.formatImporto(): " + ex.getMessage());
			return value + "";
		}
	}

}
