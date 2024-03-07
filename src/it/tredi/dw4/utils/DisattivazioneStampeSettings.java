package it.tredi.dw4.utils;

import java.time.LocalDateTime;

import it.tredi.dw4.i18n.I18N;

/**
 * Gestione della disabilitazione delle stampe in fasce orarie di maggiore sovraccarico del sistema
 */
public class DisattivazioneStampeSettings {
	
	private static final String DISABLE_STAMPE_AND_EXPORT_PROPERTY = "disableStampeAndExport";

	// Singleton
    private static DisattivazioneStampeSettings instance = null;
    
	/**
	 * Orario di avvio della disattivazione delle stampe
	 */
	private int hourFrom = 0;
	
	/**
	 * Orario di conclusione della disattivazione delle stampe
	 */
	private int hourTo = 0;
	
	/**
	 * Lettura della configurazione di disattivazione delle stampe
	 */
	private DisattivazioneStampeSettings() {
		String settings = DocWayProperties.readProperty(DISABLE_STAMPE_AND_EXPORT_PROPERTY, "");
		if (!settings.isEmpty()) {
			try {
				String[] values = settings.split(":");
				if (values.length == 2) {
					this.hourFrom = Integer.valueOf(values[0]);
					this.hourTo = Integer.valueOf(values[1]);
					
					if (this.hourFrom > this.hourTo)
						throw new Exception("Start time [" + this.hourFrom + "] is after end time [" + this.hourTo + "]");
					
					if (this.hourFrom == this.hourTo)
						this.hourTo = this.hourTo + 1;
				}
			}
			catch(Exception e) {
				Logger.error("Unable to read property '" + DISABLE_STAMPE_AND_EXPORT_PROPERTY + "'... " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Inizializzazione del singleton di disattivazione delle stampe (lettura della configurazione)
	 * @return Oggetto singleton
	 */
	public static DisattivazioneStampeSettings getInstance() {
		if(instance == null) {
			Logger.info("DisattivazioneStampeSettings instance is null... create one");
			instance = new DisattivazioneStampeSettings();
		}
		return instance;
	}
	
	/**
	 * Ritorna true se le stampe (o esportazioni) sono abilitate, false altrimenti
	 * @return
	 */
	public boolean isStampaEnabled() {
		boolean enabled = true;
		if (this.hourFrom > 0 && this.hourFrom > 0) {
			int hour = LocalDateTime.now().getHour();
			if ((hour >= this.hourFrom && hour < this.hourTo))
				enabled = false;
		}
		return enabled;
	}
	
	/**
	 * Ritorna le informazioni di attivazione delle funzioni di stampe ed esportazioni (fascia oraria all'interno della quele risultano abilitate 
	 * le stampe)
	 * @return
	 */
	public String getActivationMessage() {
		return I18N.mrs("dw4.funzione_disabilitata_per_evitare_sovraccarichi_nel_sistema_Riprovare_l_attivita_dopo_le") + " " + this.hourTo + ":00";
	}
	
}
