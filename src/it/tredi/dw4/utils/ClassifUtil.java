package it.tredi.dw4.utils;

public class ClassifUtil {

	/**
	 * Si occupa di formattare il codice del titolario di 
	 * classificazione (es. lookup su titolario di classificazione)
	 *  
	 * @param val Codice da formattare
	 * @return Codice del titolario di classificazione formattato
	 */
	public static String formatClassifCode(String val) {
		String out = "";
		
		// TODO Ancora da riportare
		/* DD 13/02/2006 (RW: 0033152) */
		/*if(getClassifFormat() != null){
			val = unformatClassif(val);
		} else {*/
		
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
	 * Dato un titolo di classificazione esegue la conversione dei codici (numero romano
	 * per il primo ed eliminazione del carattere 0 come prefisso di tutti gli altri 
	 * codici
	 * 
	 * @param classif
	 * @return
	 */
	public static String formatClassif(String classif) throws Exception {
		String title = "";
		if (classif != null && classif.length() > 0) {
			String codici = "";
			int index = classif.indexOf("-");
			if (index != -1) {
				codici = classif.substring(0, index).trim();
				if (codici.length() > 0) {
					String[] arrCodici = StringUtil.split(codici, "/");
					if (arrCodici != null && arrCodici.length > 0) {
						for (int i=0; i<arrCodici.length; i++) {
							if (i == 0) 
								title = RomanConversion.binaryToRoman(new Integer(arrCodici[i]).intValue());
							else
								title = title + "/" + new Integer(arrCodici[i]).intValue();
						}
					}
				}
				
				if (title.length() > 0)
					title = title + " - " + classif.substring(index + 1).trim();
				else
					title = classif.substring(index + 1).trim();
			}
			else
				title = classif;
		}
		return title;
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
