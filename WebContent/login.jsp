<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="it.tredi.dw4.utils.DocWayProperties" %>
<%@ page contentType="text/html; charset=UTF-8"%> 

<%
// azzeramento in sessione dell'attributo appName (applicazione corrente)
session.setAttribute("appName", "");

// eventuale css personalizzato per la pagina di login
String customDir = DocWayProperties.readProperty("docway.custom.dir.css", "");

//recupero di eventuali altri parametri da utilizzare (es. db specifico per l'applicazione)
String db = "";
if (request.getParameter("db") != null)
	db = (String) request.getParameter("db");
%>

<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<c:set var="direction" value="${(fn:startsWith(language, 'iw') or fn:startsWith(language, 'ar') or fn:startsWith(language, 'fa') or fn:startsWith(language, 'ur')) ? 'rtl' : 'ltr'}" />
<fmt:setBundle basename="it.tredi.dw4.i18n.language" />

<html xmlns="http://www.w3.org/1999/xhtml" lang="${language}"><!--manifest="cache.manifest">-->
    <head>
        <title>Login</title>
        
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		
        <!-- javascript -->
		<script src="<%= request.getContextPath() %>/common/js/jquery.js"></script>
		<script src="<%= request.getContextPath() %>/bootstrap/js/bootstrap.js"></script>
		<script src="<%= request.getContextPath() %>/common/js/login.js"></script>
		
		<!-- styles -->
		<link href="<%= request.getContextPath() %>/bootstrap/css/bootstrap.css" rel="stylesheet" />
		<c:if test="${direction eq 'rtl'}">
			<link href="<%= request.getContextPath() %>/bootstrap/css/bootstrap-rtl.css" rel="stylesheet" />
		</c:if>
		<link href="<%= request.getContextPath() %>/bootstrap/css/bootstrap-theme.css" rel="stylesheet" />
		<link href="<%= request.getContextPath() %>/common/css/login.css" rel="stylesheet" />
		
		<% if (customDir.length() > 0) { %>
        	<link type="text/css" href="<%= request.getContextPath() %>/common/css/<%= customDir %>/login.css" rel="stylesheet" />
        <% } %>
    </head>
    
    <body class="${direction}">
    
    	<div class="container">
    		<form action="j_security_check" method="post" class="form-signin">
    			<div class='row <% if (db.length() > 0) { out.print("login-archive-" + db); } %>' style="text-align:center;">
    				<img src='<%= request.getContextPath() %>/common/css/<%= (customDir.length() > 0) ? customDir+"/" : "" %>images/logo/docway-big.png' alt="DocWay" />
    			</div>
    			
    			<div class="login-form" id="loginform">	
	    			<% if (request.getParameter("loginError") != null && request.getParameter("loginError").equals("true")) { %>
	    				<div class="alert alert-danger"><fmt:message key="dw4.username_o_password_non_corretti" /></div>
	    			<% } %>
	    			
	    			<label for="login" class="string control-label"><fmt:message key="dw4.username" /></label>
	    			<input type="text" class="form-control" id="login" autofocus="autofocus" placeholder="username" required="required" name="j_username" />
	    			
	    			<label for="password" class="string control-label"><fmt:message key="dw4.password" /></label>
	    			<input type="password" class="form-control" id="password" placeholder="password" required="required" name="j_password" />
	    			
	    			<button type="submit" class="btn btn-lg btn-primary btn-block">
	    				<fmt:message key="dw4.login" />
	    			</button>
	    		</div>
	    		
	    		<div class="login-error" id="loginerror" style="display:none;">	    		
	    			<div class="alert alert-danger" style="margin-top:30px;">
	    				<p><fmt:message key="dw4.il_browser_utilizzato_non_e_supportato" /></p>
	    				<p>
	    					<fmt:message key="dw4.il_supporto_su_ie_e_garantito_dalla_versione_9_o_successiva" />&#160;
	    					<strong><fmt:message key="dw4.se_non_si_dispone_di_una_versioni_supportate_utilizzare_un_altro_browser" /></strong>
	    				</p>
	    			</div>
	    		</div>
	    		
	    		<script type="text/javascript">
	    		detectDocWaySupport();
	    		</script>
    		</form> 
    	</div>

	</body>
</html>
