package it.tredi.dw4.docway.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;

import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

public class MultiSocietaMap {

	// Singleton
    private static MultiSocietaMap instance = null;
    
    private Map<String, Societa> societaMap;
    
    /**
     * Costruttore privato
     */
    private MultiSocietaMap() {
    	this.societaMap = new ConcurrentHashMap<>();
    }
	
    /**
     * Ritorna l'oggetto contenente la lista di richieste pendenti
	 * @return
	 */
	public static MultiSocietaMap getInstance() {
		if (instance == null) {
			synchronized (MultiSocietaMap.class) {
				if (instance == null) {
					Logger.info(Logger.DOCWAY_LOGGER_NAME, "MultiSocietaMap instance is null... create one");
					instance = new MultiSocietaMap();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Inizializzazione della mappa in base ai dati estratti da una response ricevuta dal service
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public synchronized void init(Document response) {
		if (response != null) {
			List<Societa> list = XMLUtil.parseSetOfElement(response, "/response/societa_select/societa", new Societa());
			if (list != null && list.size() > 0) {
				for (Societa societa : list) {
					if (societa != null && societa.getCod() != null && !societa.getCod().equals("") && !containsSocieta(societa.getCod()))
						this.societaMap.put(societa.getCod(), societa);
				}
			}
		}
	}
	
	/**
	 * Aggiunta di una Societa' nella mappa
	 * @param societa Societa' da aggiungere
	 */
	public synchronized void addSocieta(Societa societa) {
		if (societa != null && societa.getCod() != null)
			societaMap.put(societa.getCod(), societa);
	}
	
	/**
	 * Eliminazione di una societa' dalla mappa
	 * @param code Codice della societa' da eliminare
	 */
	public synchronized void removeSocieta(String code) {
		if (code != null && societaMap.containsKey(code))
			societaMap.remove(code);
	}
	
	/**
	 * Ritorna true se la societa' specificata risulta gia' presente fra quelle registrate, false altrimenti
	 * @param code Codice della societa' da controllare
	 * @return
	 */
	public boolean containsSocieta(String code) {
		if (code != null)
			return societaMap.containsKey(code);
		else
			return false;
	}
	
	/**
	 * Reset della lista di societa' registrate
	 */
	public void resetList() {
		societaMap = new ConcurrentHashMap<>();
	}
	
	/**
	 * Dato il codice di una societa' restituisce il relativo oggetto. NULL in caso di codice non presente
	 * @param code
	 * @return
	 */
	public Societa getSocieta(String code) {
		return (containsSocieta(code)) ? societaMap.get(code) : null;
	}
	
}
