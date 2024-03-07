<%@ page import="it.tredi.dw4.utils.DocWayProperties" %>
<%@ page import="java.util.Enumeration" %>

<%!
/**
 * Invalida la sessione utente in caso di richiesta di forzatura del login (parametro forceLogin)
 * della request
 */
public void forceLogin(HttpServletRequest request, HttpSession session) {
	if (request.getParameter("forceLogin") != null && request.getParameter("forceLogin").toString().equals("true"))
		session.invalidate();
}

/**
 * Redirect ad una pagina JSF con passaggio dei parametri specificati nella request alla pagina JSP
 */
public String redirectToJsf(HttpServletRequest request, String jsfPage) {
	return redirectToJsf(request, jsfPage, true);
}

/**
 * Redirect ad una pagina JSF con passaggio dei parametri specificati nella request alla pagina JSP
 */
public String redirectToJsf(HttpServletRequest request, String jsfPage, boolean passForceLogin) {
	return redirectToJsf(request, jsfPage, passForceLogin, false);
}

/**
 * Redirect ad una pagina JSF con passaggio dei parametri specificati nella request alla pagina JSP
 */
public String redirectToJsf(HttpServletRequest request, String jsfPage, boolean passForceLogin, boolean multiArchEnabled) {
	String params = "";
	
	boolean dbNameByParam = false;
	
	Enumeration<?> enParams = request.getParameterNames(); 
	while(enParams.hasMoreElements()) {
		String paramName = (String) enParams.nextElement();
		if (paramName != null && paramName.length() > 0 && request.getParameter(paramName) != null && request.getParameter(paramName).length() > 0) {
			if (!paramName.equals("forceLogin") || passForceLogin)
				params = params + paramName + "=" + request.getParameter(paramName) + "&";
			
			// mbernardini 16/06/2016 : possibilita' di definire il nome dell'archivio docway di default tramite file di properties
			if (paramName.equals("db"))
				dbNameByParam = true; // archivio da utilizzare indicato tramite parametro della request
		}
	}
	if (params.endsWith("&"))
		params = params.substring(0, params.length()-1);
	
	// mbernardini 16/06/2016 : possibilita' di definire il nome dell'archivio docway di default tramite file di properties
	if (!dbNameByParam) {
		String defaultDbName = DocWayProperties.readProperty("xdocway.dbName.default", "");
		if (!defaultDbName.isEmpty()) {
			if (params.length() > 0)
				params += "&";
			params += "db=" + defaultDbName;
		}
	}
	
	// mbernardini 30/03/2015 : login multiarchivio
	if (multiArchEnabled) {
		if (params.length() > 0)
			params += "&";
		params += "mutiarchEnabled=true";
	}

	String redirectPage = jsfPage;
	if (params.length() > 0)
		redirectPage += "?" + params;
	
	return redirectPage;
}
%>