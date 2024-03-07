package it.tredi.dw4.utils;

/**
 * metodi di utility per l'elaborazione di query su extraway
 */
public class QueryUtil {

	/**
	 * costruzione di una query globale in base ai parametri passati
	 * @param searchTerms
	 * @param estremi
	 * @param annoProt
	 * @param numProt
	 * @return
	 */
	public static String createGlobalQuery(String searchTerms, boolean estremi, String annoProt, String numProt) {
		String query = "";
		if (searchTerms.length() > 0) {
			String escapedSearchTerms = escapeQueryValue(searchTerms);
			if (!estremi) {
				query = "([@]=" + escapedSearchTerms + " OR [doc_filesfiletesto]=" + escapedSearchTerms + ") AND ([UD,/xw/@UdType/]=\"doc\")";
			}
			else {
				String numProtSearchTerms = getNumProtFromSearchTerms(escapedSearchTerms);
				if (numProtSearchTerms != null && !numProtSearchTerms.isEmpty())
					query = "[docnumprot]=\"" + numProtSearchTerms + "\"";
				else
					query = "[XML,/doc/oggetto]=" + escapedSearchTerms + " OR [XML,/doc/rif_esterni/rif/nome]=" + escapedSearchTerms + " OR [XML,/doc/rif_esterni/rif/referente/@nominativo]=" + escapedSearchTerms;
			}
		}
		
		if (numProt.length() > 0) {
			// CodeSede potrebbe essere convertito anche in questo punto (su DocWay3 l'operazione avviene lato service)
			String query1 = "[docnumprot]=" + annoProt  + "-_CODSEDE_-" + StringUtil.fillString(numProt, "0", Const.DOCWAY_NUM_PROT_LENGTH); 
			if (query.length() > 0)
				query = "(" + query + ") AND (" + query1 + ")";
			else
				query = query1;
		}
		return query;
	}
	
	/**
	 * esegue l'escaping dei valori riservati (operatori logici) nelle query su eXtraWay
	 * 
	 * @param value query da elaborare
	 * @return
	 */
	public static String escapeQueryValue(String value) {
		if (value != null && !value.equals("")) {
			String[] negazioneLogica = {"non", "not"};
			String[] operatoriLogici = {"e", "and", "o", "or"};
			String[] reservedValues = {"e", "and", "o", "or", "non", "not"};
			
			// escaping di tutti gli operatori logici e di negazione incontrati all'inizio o alla fine della stringa
			for (int i=0; i<reservedValues.length; i++) {
				if (value.startsWith(reservedValues[i] + " "))
					value = "\"" + reservedValues[i] + "\"" + value.substring(value.indexOf(" "));
				if (value.endsWith(" " + reservedValues[i]))
					value = value.substring(0, value.lastIndexOf(" ")) + " \"" + reservedValues[i] + "\"";
			}
			
			// escaping di tutti gli operatori di negazione non preceduti da un operatore logico
			for (int i=0; i<negazioneLogica.length; i++) {
				int index = value.indexOf(" " + negazioneLogica[i] + " ");
				while (index != -1) {
					boolean escapeNotValue = true;
					for (int k=0; k<operatoriLogici.length; k++) {
						if (value.substring(0, index).trim().endsWith(" " + operatoriLogici[k]))
							escapeNotValue = false;
					}
					
					if (escapeNotValue) {
						value = value.substring(0, index+1) + "\"" + negazioneLogica[i] + "\"" + value.substring(index+negazioneLogica[i].length()+1);
						index = index + negazioneLogica[i].length() + 3;
					}
					else {
						index = index + negazioneLogica[i].length() + 1;
					}
					index = value.indexOf(" " + negazioneLogica[i] + " ", index);
				}
			}
		}
		return value;
	}
	
	/**
	 * dati i termini di ricerca globali verifica (analizzando il formato dei termini) se si tratta di un 
	 * numero di protocollo e in caso affermativo restituisce il corretto formato per poter ricercare il protocollo
	 * specificato.
	 * Ritorna NULL in caso di mancato riconoscimento di un numero di protocollo
	 */
	private static String getNumProtFromSearchTerms(String terms) {
		if (terms != null && terms.length() > 0 && !terms.trim().contains(" ")) {
			int index = terms.indexOf("/");
			if (index != -1) {
				String numprot = terms.substring(0, index);
				String anno = terms.substring(index+1);
				if (StringUtil.isNumber(numprot) && anno.length() == 4 && StringUtil.isNumber(anno))
					return anno  + "-_CODSEDE_-" + StringUtil.fillString(numprot, "0", Const.DOCWAY_NUM_PROT_LENGTH);
			}
		}
		return null;
	}
	
}
