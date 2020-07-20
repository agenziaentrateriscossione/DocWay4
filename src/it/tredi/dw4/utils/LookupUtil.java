package it.tredi.dw4.utils;

import java.lang.reflect.Method;

/**
 * metodi di utility per la gestione del lookup
 * 
 * @author mbernardini
 */
public class LookupUtil {

	//private static final String RIFINT_TYPE_CC = "cc";
	//private static final String RIFINT_TYPE_CDS = "cds";
	
	/**
	 * dato il nome della property restituisce il tipo di rif
	 * 
	 * @param propertyName nome del parametro di lookup
	 * @return
	 */
	/*public static String getRifIntType(String propertyName) {
		String type = "";
		if (propertyName != null && propertyName.length() > 0) {
			if (propertyName.contains("assegnazioneCC"))
				type = RIFINT_TYPE_CC;
			else if (propertyName.contains("assegnazioneCDS"))
				type = RIFINT_TYPE_CDS;
			
			// TODO eventualmente si possono gestire anche gli altri casi: RPA, RPAM, OP, ...
		}
		return type;
	}*/
	
	/**
	 * aggiunta di una nuova istanza di CC o CDS (rif interni multiistanza)
	 * 
	 * @param objdoc oggetto di model del documento
	 * @param type tipologia di rif da aggiungere
	 */
	/*public static void addRowRifInt(Object objdoc, String type) {
		if (type != null && (type.equals(RIFINT_TYPE_CC) || type.equals(RIFINT_TYPE_CDS))) {
			try {
				String methodName = "";
				if (type.equals(RIFINT_TYPE_CC)) // nuova riga CC
					methodName = "appendEmptyRifintCC";
				else { // nuova riga CDS
					methodName = "appendEmptyRifintCDS";
				}
				
				if (methodName.length() > 0) {
					Class<?>[] args = new Class[0];
					Method theMethod = objdoc.getClass().getMethod(methodName, args);
					Object[] arglist = new Object[0];
					theMethod.invoke(objdoc, arglist);
				}
			}
			catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}
	}*/
	
	/**
	 * aggiunta di una nuova riga in caso di lookup su multiriga. L'oggetto di model passato deve contenere un metodo 
	 * pubblico formattato in questo modo: appendEmpty_[instanceName]()
	 * 
	 * @param obj oggetto di model al quale aggiungere l'istanza sul lookup
	 * @param instanceName nome dell'istanza da replicare
	 */
	public static void addRowOnLookup(Object obj, String instanceName) {
		if (instanceName != null && !instanceName.equals("")) {
			try {
				Class<?>[] args = new Class[0];
				Method theMethod = obj.getClass().getMethod("appendEmpty_" + instanceName, args);
				Object[] arglist = new Object[0];
				theMethod.invoke(obj, arglist);
			}
			catch (Exception e) {
				Logger.warn("LookupUtil.addRowOnLookup(): metodo non gestito: " + e.getMessage());
			}
		}
	}
	
}
