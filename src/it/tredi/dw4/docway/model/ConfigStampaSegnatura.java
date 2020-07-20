package it.tredi.dw4.docway.model;

public class ConfigStampaSegnatura {

	private String stringPreference = "";
	private String type = "";
	private String portaCom = "";
	
	public ConfigStampaSegnatura(String stringPreferenceValue) {
		this.stringPreference = stringPreferenceValue;
		if (this.stringPreference == null)
			this.stringPreference = "";
		
		if (this.stringPreference != null && this.stringPreference.length() > 0) {
			if (this.stringPreference.toLowerCase().equals("endorscan")) {
				type = "endorscan";
			}
			else if (this.stringPreference.indexOf("COM") == 0) {
				type = "seriale";
				this.portaCom = this.stringPreference.substring(3);
			}
		}
		// else "configurazione su IWX"
	}
	
	/**
	 * ritorna il valore da impostare nel parametro xMode per la tipologia in fase 
	 * di salvataggio del profilo personale
	 * @return
	 */
	public String getXmodeValue() {
		String info = "";
		
		if (type.equals("seriale"))
			info = portaCom;
		else if (type.equals("endorscan"))
			info = "endorscan";
		else
			info = "";
		
		return info;
	}
	
	public void setStringPreference(String stringPreference) {
		this.stringPreference = stringPreference;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPortaCom(String portaCom) {
		this.portaCom = portaCom;
	}
	
	public String getStringPreference() {
		return stringPreference;
	}
	
	public String getType() {
		return type;
	}
	
	public String getPortaCom() {
		return portaCom;
	}
	
}
