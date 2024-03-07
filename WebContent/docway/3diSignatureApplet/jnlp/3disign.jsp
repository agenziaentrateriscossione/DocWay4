<%@page contentType="application/x-java-jnlp-file" %>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page pageEncoding="UTF-8"%>

<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setHeader("Content-Disposition", "attachment; filename=\"3disign.jnlp\"");
response.setDateHeader("Expires", -1);
response.setContentType("application/x-java-jnlp-file");

String jnlp_href 					= (String) request.getParameter("jnlp_href");
String DownloadFilesServiceURL 		= (String) request.getParameter("DownloadFilesServiceURL");
String InputFile					= URLEncoder.encode((String) request.getParameter("InputFile"));
//String FileNameURL					= URLEncoder.encode((String) request.getParameter("FileNameURL"));
String FileNameURL					= (String) request.getParameter("FileNameURL");

String OutputFileType 				= (String) request.getParameter("OutputFileType");
String SendToServerURL				= (String) request.getParameter("SendToServerURL");
//l'applet vuole url decode nei param, ma qui mi serve encoded per ripassarlo al link href 
//SendToServerURL = URLEncoder.encode(SendToServerURL);
String cookies						= (String) request.getParameter("cookies");
String successJsCode				= "";//(String) request.getParameter("successJsCode");
String errorJsCode					= "";//(String) request.getParameter("errorJsCode");
String InputOutputFilesSeparator 	= (String) request.getParameter("InputOutputFilesSeparator");

String codebase						= request.getRequestURL().toString();
codebase = codebase.substring(0,codebase.indexOf("jnlp"));
String href_prefix="jnlp/3disign.jsp?";

String href_suffix=
"jnlp_href=" + jnlp_href + "&" +
"DownloadFilesServiceURL=" + DownloadFilesServiceURL + "&" +
"InputFile=" + InputFile + "&" +
"FileNameURL=" + FileNameURL + "&" +
"OutputFileType=" + OutputFileType + "&" +
"SendToServerURL=" + SendToServerURL + "&" +
"cookies=" + cookies + "&" +
"successJsCode=" + successJsCode + "&" +
"errorJsCode=" + errorJsCode + "&" +
"InputOutputFilesSeparator=" + InputOutputFilesSeparator;

href_suffix = href_suffix.replace("&","&amp;");
String href = href_prefix + href_suffix;

%>
<?xml version="1.0" encoding="utf-8"?>

<jnlp spec="1.0+" codebase="<%= codebase %>" version="1.0">
	<information>
		<title>3diSignApplet</title>
		<vendor>3D Informatica</vendor>
		<offline-allowed />
	</information>
	<security>
		<all-permissions />
	</security>
  
	<!--<update check="always" policy="always"/>-->
	<update check="background" />
	
	<resources>
		<java href="http://java.sun.com/products/autodl/j2se" version="1.5+" />	
		<jar href="jars/3disignapplet.jar" main="true" />
		<jar href="jars/bctsp-jdk15-146.jar" main="false" />
		<jar href="jars/bcprov-jdk15-146.jar" main="false" />
		<jar href="jars/bcmail-jdk15-146.jar" main="false" />
		<jar href="jars/commons-io-2.4.jar" main="false" />
		<jar href="jars/commons-logging-1.1.1.jar" main="false" />
		<jar href="jars/httpclient-4.3.2.jar" main="false" />
		<jar href="jars/httpcore-4.3.1.jar" main="false" />
		<jar href="jars/httpmime-4.3.2.jar" main="false" />
        <jar href="jars/iaikPkcs11Wrapper.jar" main="false" />
		<jar href="jars/icepdf-core-4.3.3.jar" main="false" />
		<jar href="jars/icepdf-viewer-4.3.3.jar" main="false" />
		<jar href="jars/itextpdf-5.1.3.jar" main="false" />
		<jar href="jars/jaws.jar" main="false" />
		<jar href="jars/commons-codec-1.6.jar" main="false" />
	</resources>

	<!-- width , height are overridden by applet tag -->
	<applet-desc name="3diSignApplet" main-class="it.tredi.applet.signature.SignatureApplet.class" width="400" height="100">
		
			<param name="code" value="it.tredi.applet.signature.SignatureApplet.class" />
			<param name="archive" value="3disignapplet.jar" />
		    <param name="jnlp_href" value="<%= jnlp_href %>" />
		    <param name="DownloadFilesServiceURL" value="<%= DownloadFilesServiceURL %>" />
		    <param name="InputFile" value="<%= InputFile %>" />
		    
		    <param name="FileNameURL" value="<%= FileNameURL %>" />
			
			<param name="OutputFileType" value="<%= OutputFileType %>" /> <!-- possibili valori: p7m/pdf -->
			
			<param name="SendToServerURL" value="<%= SendToServerURL %>" />
			
			<param id="3diSignatureAppletParamCookies" name="cookies" value="<%= cookies %>" />
			<param name="successJsCode" value="<%= successJsCode %>" />
			<param name="errorJsCode" value="<%= errorJsCode %>" />
		    
		    <param name="InputOutputFilesSeparator" value="<%= InputOutputFilesSeparator %>" />			
	</applet-desc> 
</jnlp>