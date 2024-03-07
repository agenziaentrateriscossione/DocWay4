<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page isErrorPage="true" %>
<%@ page import="java.util.Date" %>
<%@ page import="it.tredi.dw4.utils.DocWayProperties" %>
<%@ page contentType="text/html; charset=UTF-8"%>

<%
final String requestURL = request.getRequestURL().toString();
final String requestURI = request.getRequestURI();
final int index = requestURL.indexOf(requestURI);
final String retryURL = requestURL.substring(0, index) +
request.getAttribute("javax.servlet.error.request_uri");

//eventuale css personalizzato per la pagina di login
String customDir = DocWayProperties.readProperty("docway.custom.dir.css", "");
%>

<c:set var="language" value="${not empty param.language ? param.language : not empty language ? language : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" />
<c:set var="direction" value="${(fn:startsWith(language, 'iw') or fn:startsWith(language, 'ar') or fn:startsWith(language, 'fa') or fn:startsWith(language, 'ur')) ? 'rtl' : 'ltr'}" />
<fmt:setBundle basename="it.tredi.dw4.i18n.language" />

<html xmlns="http://www.w3.org/1999/xhtml" lang="${language}"><!--manifest="cache.manifest">-->
    <head>
        <title><fmt:message key="dw4.richiesta_non_valida" /></title>

		<meta name="viewport" content="width=device-width, initial-scale=1.0" />

        <!-- javascript -->
		<script src="<%= request.getContextPath() %>/common/js/jquery.js"></script>
		<script src="<%= request.getContextPath() %>/bootstrap/js/bootstrap.js"></script>
		
		<link href="<%= request.getContextPath() %>/favicon.ico" type="image/png" rel="icon"/>

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
    		<form id="loginForm" action="" method="post" class="form-signin">
    			<div class="row" style="text-align:center;"><img src='<%= request.getContextPath() %>/common/css/<%= (customDir.length() > 0) ? customDir+"/" : "" %>images/logo/docway-big.png' alt="DocWay" /></div>

    			<% if (request.getParameter("loginError") != null && request.getParameter("loginError").equals("true")) { %>
    				<div class="alert alert-danger"><fmt:message key="dw4.username_o_password_non_corretti" /></div>
    			<% } %>

    			<div class="alert alert-warning">
    				<div><fmt:message key="dw4.e_stata_rilevata_una_richiesta_non_valida_da_parte_del_client" /></div>
    				<h3><fmt:message key="dw4.si_prega_di_rieffetturare_il_login" />: <a class="loginLinkBlack" href="<%= request.getContextPath() %>/docway/home.jsf">DocWay Login</a></h3>
    			</div>

    			<div class="well">
		  			<strong><fmt:message key="dw4.dettagli" />:</strong><br/>
		  			remote_addr: <%= request.getRemoteAddr() %><br/>
		  			request_uri: <%= request.getAttribute("javax.servlet.error.request_uri") %><br/>
					status_code: <%= request.getAttribute("javax.servlet.error.status_code") %><br/>
					<!-- servlet_name: <%= request.getAttribute("javax.servlet.error.servlet_name") %> <br/>-->
					exception: <%= request.getAttribute("javax.servlet.error.exception") %><br/>
					<!-- Error info:
		  			<% exception.printStackTrace(); %>
					-->
				</div>
    		</form>
    	</div>

	</body>
</html>
