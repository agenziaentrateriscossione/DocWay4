<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="it.tredi.dw4.utils.DocWayProperties" %>
<%@ page contentType="text/html; charset=UTF-8"%>

<%
// recupero del nome dell'applicazione corrente
String appName = "";
if (request.getParameter("app") != null)
	appName = (String) request.getParameter("app");
if (appName.equals(""))
	appName = "docway";

// recupero di eventuali altri parametri da utilizzare (es. db specifico per l'applicazione)
String db = "";
if (request.getParameter("db") != null)
	db = (String) request.getParameter("db");
String repCode = "";
if (request.getParameter("repCode") != null)
	repCode = (String) request.getParameter("repCode");

String extraParams = "";
if (!db.equals(""))
	extraParams = "db=" + db + "&";
if (!repCode.equals(""))
	extraParams = "repCode=" + repCode + "&";

if (extraParams.length() > 0)
	extraParams = extraParams.substring(0, extraParams.length()-1);

// eventuale css personalizzato per la pagina di login
String customDir = DocWayProperties.readProperty("docway.custom.dir.css", "");
%>

<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<c:set var="direction" value="${(fn:startsWith(language, 'iw') or fn:startsWith(language, 'ar') or fn:startsWith(language, 'fa') or fn:startsWith(language, 'ur')) ? 'rtl' : 'ltr'}" />
<fmt:setBundle basename="it.tredi.dw4.i18n.language" />

<html xmlns="http://www.w3.org/1999/xhtml" lang="${language}">
    <head>
        <title>Logout</title>
        
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        
        <!-- javascript -->
		<script src="<%= request.getContextPath() %>/common/js/jquery.js"></script>
		<script src="<%= request.getContextPath() %>/bootstrap/js/bootstrap.js"></script>
		
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
	    	<div class="form-signin">
				<div class='row <% if (db.length() > 0) { out.print("logout-archive-" + db); } %>' style="text-align:center; margin-bottom:20px;">
					<img src='<%= request.getContextPath() %>/common/css/<%= (customDir.length() > 0) ? customDir+"/" : "" %>images/logo/docway-big.png' alt="DocWay" />
				</div>
				
				<div class="alert alert-success">
					<fmt:message key="dw4.logout_completato_con_successo" />
					<%
					if (db.length() > 0) {
					%>
						&#160; <fmt:message key="dw4.from" /> &#160; <strong><%= db %></strong>
					<%
					}
					%>
				</div>
				
				<p>
					<fmt:message key="dw4.torna_alla" /> &#160; <a class="loginLinkBlack" href="<%= request.getContextPath() %>/<%= appName %><%= (extraParams.length() > 0) ? "/home.jsf?" + extraParams : "" %>"><fmt:message key="dw4.pagina_di_login" /></a>
				</p>
			</div>
		</div>
	</body>
</html>
