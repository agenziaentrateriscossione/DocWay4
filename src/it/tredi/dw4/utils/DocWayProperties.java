package it.tredi.dw4.utils;


import it.tredi.configurator.reader.PropertiesReader;

import java.util.List;
import java.util.Properties;

public class DocWayProperties {
	
	// Definizione delle chiavi da utilizzare per il caricamento delle properties (valore = nome del file senza estensione)
	public static final String DOCWAY_NAMESPACE = "/docway.properties";
	public static final String ADAPTERS_NAMESPACE = "/adapters.properties";
	public static final String SOGINSAP_NAMESPACE = "/it/tredi/dw4/soginSAP/soginSAP.properties";
	
	/**
	 * Lettura di una property dell'interfaccia di DocWay
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String readProperty(String key, String defaultValue) {
		return readProperty(DOCWAY_NAMESPACE, key, defaultValue);
	}

	/**
	 * Lettura di una property dell'interfaccia di DocWay
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String readProperty(String namespace, String key, String defaultValue) {
		return PropertiesReader.getInstance(Const.RESOURCE_APP_CONTEXT).getConfig(namespace, key, defaultValue);
	}
	
	/**
     * Estrae una serie di proprieta' numerate che costituiscono una lista. 
     * Per esempio se propName=prop allora vengono estratte le proprietà prop.1, prop.2 fino a che esistenti
     * 
     * @param propName
     * @return lista
     */
	public static List<String> getPropertyList(String propName) {
		return getPropertyList(DOCWAY_NAMESPACE, propName);
	}
		
    /**
     * Estrae una serie di proprieta' numerate che costituiscono una lista. 
     * Per esempio se propName=prop allora vengono estratte le proprietà prop.1, prop.2 fino a che esistenti
     * 
     * @param propName
     * @return lista
     */
	public static List<String> getPropertyList(String namespace, String propName) {
		return PropertiesReader.getInstance(Const.RESOURCE_APP_CONTEXT).getConfigArray(namespace, propName);
	}
	
	/**
	 * Carica l'interno set di properties relative ad un namespace (intero file di properties)
	 * @param namespace
	 * @return
	 */
	public static Properties getProperties(String namespace) {
		return PropertiesReader.getInstance(Const.RESOURCE_APP_CONTEXT).getAllConfig(namespace);
	}
	
}
