package it.tredi.dw4.utils;

import it.tredi.dw4.beans.ClassifFormatManager;

/**
 * Utilities di gestione del numero dei fascicoli
 */
public class NumFascUtils {

	/**
	 * Formattazione di un numero di fascicolo (da formato completo a vista utente)
	 * @param value
	 * @return
	 */
	public static String format(String value) {
		String result = value;
		try {
			if (value != null && !value.equals("")) {
				if (value.contains("/")) {
					// caso di numero formattato in questo modo: 2013-3DINBOL-05/06.00010
					// viene trasformato in: 2013-V/6.10
					
					// mbernardini 16/12/2016 : gestione del formato della classificazione
					int index = value.lastIndexOf("-");
					int index1 = value.indexOf(".");
					String prefix = value.substring(0, index);
					String classif = value.substring(index+1, index1);
					String num = value.substring(index1+1);
					
					// formattazione anno
					index = prefix.indexOf("-");
					if (index != -1)
						prefix = prefix.substring(0, index);
					
					// formattazione classificazione
					classif = ClassifUtil.formatCodClassif(classif, ClassifFormatManager.getInstance().getClassifFormat());
					
					// formattazione numero
					String[] tmp = num.split("\\.");
					num = "";
					for (int i = 0; i < tmp.length; i++) {
						String val = tmp[i];
						num += new NumConverter().getAsString(null, null, val) + ".";
					}
					if (num.endsWith("."))
						num = num.substring(0, num.length() - 1);
					
					result = prefix + "-" + classif + "." + num;
					
					/*
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
					*/
					
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
		}
		catch (Exception e) {
			Logger.warn("NumFascUtils.format(): conversion failed (" + e.getMessage() + ")... use " + value);
			
			// in caso di eccezione in fase di conversione del numero del fascicolo viene restituito esattamente il 
			// valore passato in input
			result = value;
		}
		
		return result;
	}
	
}
