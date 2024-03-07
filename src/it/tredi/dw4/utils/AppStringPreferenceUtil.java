package it.tredi.dw4.utils;

/**
 * Metodi di util per la gestione delle preferenze dell'applicazione
 * specificate tramite l'attributo /response/@appStringPreferences
 */
public class AppStringPreferenceUtil {

	/**
	 * Recupera un valore dalle tuple di preferenze dell'applicativo
	 * 
	 * @param preferences Preferenze dell'applicativo
	 * @param position Posizione dalla quale recuperare il valore
	 * @return Valore relativo alla preferenza specificata
	 */
	public static String getAppStringPreference(String preferences, int position) {
		String value = "";
		if (preferences != null && position > -1) {
			String[] prefs = StringUtil.split(preferences, "§");
			if (position < prefs.length)
				value = prefs[position];
		}
		
		return value;
	}
	
	/**
	 * Recupera un valore dalle tuple di preferenze dell'applicativo in formato int.
	 * Ritorna -1 in caso di valore Nullo o errore di conversione in intero
	 * 
	 * @param preferences Preferenze dell'applicativo
	 * @param position Posizione dalla quale recuperare il valore
	 * @return Valore relativo alla preferenza specificata
	 */
	public static int getAppStringPreferenceIntValue(String preferences, int position) {
		String value = getAppStringPreference(preferences, position);
		if (value.length() > 0) {
			try {
				return new Integer(value).intValue();
			}
			catch (Exception e) {
				// TODO Gestire eccezione?
				return -1;
			}
		}
		else return -1;
	}
	
	/**
	 * Data una label relativa ad una preferenza restituisce la sua posizione
	 * all'interno della tupla di preferenze dell'applicativo.
	 * Ritorna -1 in caso di valore Nullo o label non trovata
	 */
	public static int decodeAppStringPreference(String val) {
		int posizione = -1;
		
		if (val != null) {
			if (val.equals("ClassificazioneAutomatica")) {
				posizione = 0;
			}
			else if (val.equals("LunghezzaMinimaOggetto")) {
				posizione = 1;
			}
			else if (val.equals("ScartoAutomatico")) {
				posizione = 2;
			}
			else if (val.equals("portaSerialeSegnaturaArrivo")) {
				posizione = 3;
			}
			else if (val.equals("portaSerialeSegnaturaPartenza")) {
				posizione = 4;
			}
			else if (val.equals("portaSerialeSegnaturaInterno")) {
				posizione = 5;
			}
			else if (val.equals("checkRipetizioniInOggetto")) {
				posizione = 6;
			}
			else if (val.equals("RTFPropostaObbligatorio")) {
				posizione = 7;
			}
			else if (val.equals("RTFComunicazioneObbligatorio")) {
				posizione = 8;
			}
			else if (val.equals("warningSeSenzaAllegato")) {
				posizione = 9;
			}
			else if (val.equals("CheckboxEmailNotifica")) {
				posizione = 10;
			}
			else if (val.equals("notificaOggettiDiversiConClassificazione")) {
				posizione = 11;
			}
			else if (val.equals("portaSerialeSegnaturaVarie")) {
				posizione = 12;
			}
			else if (val.equals("numeroRipetizioniInOggetto")) {
				//RW0052224 - fcossu - Numero ripetizione caratteri inusuali
				posizione = 13;
			}
			else if (val.equals("CheckboxEmailNotificaCapillare")) {
				//rtirabassi 11/09/2019 - ERM012596 - Notifica ad utenti selezionati.
				posizione = 14;
			}
		}
		
		return posizione;
	}
	
}
