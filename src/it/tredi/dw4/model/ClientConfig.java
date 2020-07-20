package it.tredi.dw4.model;

import org.json.JSONException;
import org.json.JSONObject;

import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;

/**
 * eventuali configurazioni client da utilizzare su componenti inclusi nel browser (es. IWX, applet firma, ecc.)
 * @author mbernardini
 */
public class ClientConfig {

	private String tomcatForwardHost = "";
	private String tomcatForwardPort = "";
	
	private JSONObject json = new JSONObject();
	
	public ClientConfig() {
		tomcatForwardHost = DocWayProperties.readProperty("clientConfig.tomcatForward.host", "");
		tomcatForwardPort = DocWayProperties.readProperty("clientConfig.tomcatForward.port", "");
		
		buildJSON();
	}
	
	/**
	 * si occupa della costruzione dell'oggetto JSON di definizione dei parametri client (per inclusione in javascript)
	 */
	private void buildJSON() {
		try {
			json.put("tomcatForwardHost", tomcatForwardHost);
			json.put("tomcatForwardPort", tomcatForwardPort);
		}
		catch (JSONException je) {
			Logger.error(je.getMessage(), je);
		}
	}
	
	public String getTomcatForwardHost() {
		return tomcatForwardHost;
	}
	
	public void setTomcatForwardHost(String tomcatHost) {
		this.tomcatForwardHost = tomcatHost;
	}
	
	public String getTomcatForwardPort() {
		return tomcatForwardPort;
	}
	
	public void setTomcatForwardPort(String tomcatPort) {
		this.tomcatForwardPort = tomcatPort;
	}
	
	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}
	
}
