package it.tredi.dw4.beans;

import it.tredi.dw4.utils.Logger;

/**
 * Formato del codice di classificazione da adottare sull'applicatazione
 */
public class ClassifFormatManager {
	
	// Singleton
    private static ClassifFormatManager instance = null;
    
    String classifFormat; // formato della classificazione
    
    private static final String DEFAULT_CLASSIF_FORMAT = "R/D";
    
    /**
	 * Costruttore
	 */
	private ClassifFormatManager() {
	}

	/**
	 * Utilizzato per istanziare l'oggetto singleton
	 * @return Oggetto singleton
	 */
	public static ClassifFormatManager getInstance() {
		if(instance == null) {
			Logger.info("ClassifFormatManager instance is null... create one");
			instance = new ClassifFormatManager();
		}
		return instance;
	}
	
	
	public String getClassifFormat() {
		if (classifFormat == null || classifFormat.isEmpty())
			return DEFAULT_CLASSIF_FORMAT;
		else
			return classifFormat;
	}

	public void setClassifFormat(String classifFormat) {
		this.classifFormat = classifFormat;
	}
	
}
