<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page isErrorPage="true" %>
<%@ page import="java.util.Date" %>
<%@ page import="it.tredi.dw4.utils.DocWayProperties" %>
<%@ page contentType="text/html; charset=UTF-8"%>

<%
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
        
        <style type="text/css">
        .form-signin {
        	max-width:80%;
        }
        </style>
    </head>
    <body class="${direction}">

    	<div class="container">
    		<form id="loginForm" action="" method="post" class="form-signin">
    			<div class="row" style="text-align:center;"><img src='<%= request.getContextPath() %>/common/css/<%= (customDir.length() > 0) ? customDir+"/" : "" %>images/logo/docway-big.png' alt="DocWay" /></div>

    			<div class="alert alert-danger">
    				<h3 style="margin-bottom:30px;"><fmt:message key="dw4.scheda_invalidata_Utilizzo_dell_applicazione_in_multischeda" /></h3>
    				<p><fmt:message key="dw4.non_e_possibile_portare_a_termine_l_attivita_sulla_scheda_corrente_Utilizzare_l_ultima_aperta" /></p>
    				<p><fmt:message key="dw4.si_prega_di_chiudere_la_finestra_corrente" />!</p>
    			</div>
    		</form>
    	</div>

	</body>
</html>
