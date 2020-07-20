package it.tredi.dw4.utils;

import java.util.HashMap;
import java.util.Map;

public class DigitVisibilitaUtil {
	
	private final String TYPE_PUBBLICO = "dicitPubblico";
	private final String TYPE_RISERVATO = "dicitRiservato";
	private final String TYPE_ALTCONF = "dicitAltConf";
	private final String TYPE_SEGRETO = "dicitSegreto";
	
	private final String KEY_ALTCONF = "Altamente confidenziale";
	private final String KEY_SEGRETO = "Segretissimo";

	private Map<String, String> digitSingolare = new HashMap<String, String>();
	private Map<String, String> digitPlurale = new HashMap<String, String>();
	
	public DigitVisibilitaUtil(String digitVis) {
		// es. formato: dicitPubblico=Aziendale|Aziendali&dicitRiservato=Riservato Aziendale|Riservati Aziendali&dicitAltConf=Uso Ristretto|Uso Ristretto&dicitSegreto=Riservato a Risorse Umane|Riservati a Risorse Umane
		
		if (digitVis == null || digitVis.trim().length() == 0)
			return;
		
		String[] digitVisTypes = digitVis.split("&");
		if (digitVisTypes != null && digitVisTypes.length > 0) {
			for (int i=0; i<digitVisTypes.length; i++) {
				fillDigitMaps(digitVisTypes[i]);
			}
		}
	}
	
	/**
	 * Dato un valore passato come attributo dicitVis riempie le mappe contenenti i termini singolari e plurali
	 * @param dicit
	 */
	private void fillDigitMaps(String dicit) {
		if (dicit != null && !dicit.isEmpty()) {
			int index = dicit.indexOf("=");
			if (index != -1) {
				String key = dicit.substring(0, index);
				if (key.equals(TYPE_ALTCONF))
					key = KEY_ALTCONF;
				else if (key.equals(TYPE_SEGRETO))
					key = KEY_SEGRETO;
				else if (key.equals(TYPE_PUBBLICO) || key.equals(TYPE_RISERVATO))
					key = key.replace("dicit", "");
					
				String[] values = dicit.substring(index+1).split("\\|");
				if (values != null && values.length == 2) {
					digitSingolare.put(key, values[0]);
					digitPlurale.put(key, values[1]);
				}
			}
		}
	}

	public Map<String, String> getDigitSingolare() {
		return digitSingolare;
	}

	public void setDigitSingolare(Map<String, String> digitSingolare) {
		this.digitSingolare = digitSingolare;
	}

	public Map<String, String> getDigitPlurale() {
		return digitPlurale;
	}

	public void setDigitPlurale(Map<String, String> digitPlurale) {
		this.digitPlurale = digitPlurale;
	}
	
}
