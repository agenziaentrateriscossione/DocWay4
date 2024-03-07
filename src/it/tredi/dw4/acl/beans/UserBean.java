package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.AppUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


public class UserBean {
	
	@SuppressWarnings("unused")
	private static String ACL_CONTEXT_NAME = "/acl/";
	@SuppressWarnings("unused")
	private static String DOCWAY_CONTEXT_NAME = "/docway/";
	
	private String login = "";
	private String matricola = ""; // Valorizzato in caso di login multiutente
	private String codSocieta = ""; // Valorizzato in caso in caso di multilogin
	private boolean allSocieta = false; // Valorizzato in caso in caso di multilogin
	private String userInfo; 
	private String codSede = ""; // cod_amm_aoo di appartenenza dell'utente corrente
	
	// condivisione di parametri fra diversi formsAdapter:
	// nel caso in cui il parametro sia gia' definito a livello del
	// formAdapter non viene sostituito da quello specificato in serviceFormParams
	private Map<String, String> serviceFormParams = new HashMap<String, String>();
	
	// condivisione di parametri fra diversi formsAdapter:
	// ogni parametro specificato in forcedServiceFormParams sostituisce i parametri
	// definiti a livello di formAdapter (anche se valorizzati)
	private Map<String, String> forcedServiceFormParams = new HashMap<String, String>();
	
	// Conferma da parte dell'utente del check di alert a tutte le persone dell'ufficio
	// per i form di inserimento/modifica su DocWay
	private boolean alertForTuttiConfirm = true;
		
	public UserBean(String login) {
		this.login = login;
	}

	public String getLogin() {
		return this.login;
	}
	
	public String getMatricola() {
		return this.matricola;
	}
	
	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}
	
	public String getCodSocieta() {
		return codSocieta;
	}

	public void setCodSocieta(String codSocieta) {
		this.codSocieta = codSocieta;
	}

	public boolean isAllSocieta() {
		return allSocieta;
	}

	public void setAllSocieta(boolean allSocieta) {
		this.allSocieta = allSocieta;
	}
	
	public void logout() throws IOException {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		HttpSession session = (HttpSession) context.getSession(false);

		String appName = AppUtil.getAppNameFromFacesContex();
		
		String db = null;
		if (session.getAttribute(appName+"reqDb") != null)
			db = (String) session.getAttribute(appName+"reqDb");
		
		String repCode = "";
		if (session.getAttribute("repCode") != null)
			repCode = (String) session.getAttribute("repCode");
		
		String mutiarchEnabled = "";
		if (session.getAttribute("mutiarchEnabled") != null)
			mutiarchEnabled = (String) session.getAttribute("mutiarchEnabled");
		
		String logoutUrl = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/logout.jsp?app=" + appName;
		if (db != null && !db.equals(""))
			logoutUrl += "&db=" + db;
		if (repCode != null && !repCode.equals(""))
			logoutUrl += "&repCode=" + repCode;
		
		if (appName.equals("docway") && mutiarchEnabled != null && !mutiarchEnabled.isEmpty())
			logoutUrl += "&mutiarchEnabled=" + mutiarchEnabled;
		
		session.invalidate();
		context.redirect(logoutUrl);
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public String getUserInfo() {
		return userInfo;
	}
	
	public String getCodSede() {
		return codSede;
	}

	public void setCodSede(String codSede) {
		this.codSede = codSede;
	}
	
	/**
	 * condivisione di parametri fra diversi formsAdapter:
	 * nel caso in cui il parametro sia gia' definito a livello del 
	 * formAdapter non viene sostituito da quello specificato in serviceFormParams
	 * 
	 * @param param nome del parametro 
	 * @param value valore da assegnare al parametro (null per rimuovere il parametro dalla mappa)
	 */
	public void setServiceFormParam(String param, String value) {
		if (value == null)
			serviceFormParams.remove(param);
		else
			serviceFormParams.put(param, value);
	}
	
	public String getServiceFormParam(String param) {
		return serviceFormParams.get(param);
	}

	public Map<String, String> getServiceFormParams() {
		return serviceFormParams;
	}

	/**
	 * condivisione di parametri fra diversi formsAdapter: 
	 * ogni parametro specificato in forcedServiceFormParams sostituisce i parametri 
	 * definiti a livello di formAdapter (anche se valorizzati)
	 * 
	 * @param param nome del parametro 
	 * @param value valore da assegnare al parametro (null per rimuovere il parametro dalla mappa)
	 */
	public void setForcedServiceFormParam(String param, String value) {
		if (value == null)
			forcedServiceFormParams.remove(param);
		else
			forcedServiceFormParams.put(param, value);
	}
	
	public String getForcedServiceFormParam(String param) {
		return forcedServiceFormParams.get(param);
	}

	public Map<String, String> getForcedServiceFormParams() {
		return forcedServiceFormParams;
	}
	
	/**
	 * eliminazione di tutti i parametri "persistenti" dei formsAdapter
	 */
	public void emptyForcedServiceFormParam() {
		forcedServiceFormParams = new HashMap<String, String>();
	}

	public boolean isAlertForTuttiConfirm() {
		return alertForTuttiConfirm;
	}

	public void setAlertForTuttiConfirm(boolean alertForTuttiConfirm) {
		this.alertForTuttiConfirm = alertForTuttiConfirm;
	}
	
}
