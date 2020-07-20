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
	String params = "";
	Enumeration<?> enParams = request.getParameterNames(); 
	while(enParams.hasMoreElements()) {
		String paramName = (String) enParams.nextElement();
		if (paramName != null && paramName.length() > 0 && request.getParameter(paramName) != null && request.getParameter(paramName).length() > 0) {
			if (!paramName.equals("forceLogin") || passForceLogin)
				params = params + paramName + "=" + request.getParameter(paramName) + "&";
		}
	}
	if (params.endsWith("&"))
		params = params.substring(0, params.length()-1);

	String redirectPage = jsfPage;
	if (params.length() > 0)
		redirectPage += "?" + params;
	
	return redirectPage;
}
%>