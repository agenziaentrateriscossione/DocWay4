package it.tredi.dw4.i18n;

import it.tredi.configurator.reader.ResourceBundleReader;
import it.tredi.dw4.utils.Const;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class I18N extends ResourceBundleReader {

	private static final String DEFAULT_BUNDLE="i18n";
	
	private static final String BUNDLE_NAME = "it.tredi.dw4.i18n.language";
	
	public I18N() {
		super(Const.RESOURCE_APP_CONTEXT, BUNDLE_NAME, FacesContext.getCurrentInstance().getViewRoot().getLocale());
	}
			
	/**
     * Restituisce il messaggio con valore key prelevato dalla resource bundle passata come parametro secondo il locale utilizzato
     * Comportamento identico a getMessageResourceString, con nome abbreviato (Stile macro C, ci piacerebbe ma non si può)
     * @param key chiave del messaggio
     * @return Restituisce il messaggio con valore key prelevato dalla resource bundle dell'applicativo secondo il locale utilizzato
     */
	public static String mrs(String key) {
		return getMessageResourceString(I18N.DEFAULT_BUNDLE, key, null);
	}
	
	/**
     * Restituisce il messaggio con valore key prelevato dalla resource bundle passata come parametro secondo il locale utilizzato
     * Comportamento identico a getMessageResourceString, con nome abbreviato (Stile macro C, ci piacerebbe ma non si può)
     * @param key chiave del messaggio
     * @param params parametri per il messaggio
     * @return Restituisce il messaggio con valore key prelevato dalla resource bundle dell'applicativo secondo il locale utilizzato
     */
	public static String mrs(String key, Object params[]) {
		return getMessageResourceString(I18N.DEFAULT_BUNDLE, key, params);
	}
	
	/**
     * Restituisce il messaggio con valore key prelevato dalla resource bundle passata come parametro secondo il locale utilizzato
     * @param key chiave del messaggio
     * @param params parametri per il messaggio
     * @return Restituisce il messaggio con valore key prelevato dalla resource bundle dell'applicativo secondo il locale utilizzato
     */
	public static String getMessageBundleString(String key, Object params[]){
        String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle(); //utilizza la resource bundle definita nel faces-config
        return getMessageResourceString(bundleName, key, params);
    }        
    
    /**
     * Restituisce il messaggio con valore key prelevato dalla resource bundle definita per l'applicativo secondo il locale utilizzato
     * @param key chiave del messaggio
     * @param params parametri per il messaggio
     * @return Restituisce il messaggio con valore key prelevato dalla resource bundle dell'applicativo secondo il locale utilizzato
     */
	private static String getMessageResourceString(String bundleName, String key, Object params[]){
        String text = null;
        
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
	        Locale locale = ctx.getViewRoot().getLocale(); //locale utilizzato per la view
	        ResourceBundle bundle = ctx.getApplication().getResourceBundle(ctx, bundleName);
	        
	        try {
	            text = bundle.getString(key);
	        }
	        catch (MissingResourceException e) {
	            text = "?? key " + key + " not found ??";
	        }
	        if (params != null) {
	            MessageFormat mf = new MessageFormat(text, locale);
	            text = mf.format(params, new StringBuffer(), null).toString();
	        }
        }
        else {
        	// in caso di mancato caricamento del FacesContext viene restituita la 
        	// chiave passata
        	text = key;
        }
        
        return text;
    }
}
