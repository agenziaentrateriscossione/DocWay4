package it.tredi.dw4.servlet;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.UploadFileUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Gestione della firma digitale tramite applet:
 * - lista di file necessari all'utilizzo dell'applet di firma (dipendenze)
 * - chiamata a service per salvataggio del file firmato
 * 
 * @author mbernardini
 */
public class TreDiSignatureJnlpProducer extends HttpServlet {

	private static final long serialVersionUID = -7611208551000071422L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String jnlpFileName = request.getParameter("jnlpFileName");
		
		if(jnlpFileName != null && !jnlpFileName.isEmpty()) {
			// download file jnlp
			
			response.setContentType("application/x-java-jnlp-file;charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Content-Disposition", "attachment; filename=\"3disign.jnlp\"");
			response.setDateHeader("Expires", -1);
			response.setContentType("application/x-java-jnlp-file");
			
			File jnlpFile = new File(jnlpFileName);
			if(!jnlpFile.exists())
				throw new ServletException("Missing JNLP File!");
			
			FileUtils.copyFile(jnlpFile, response.getOutputStream());
			jnlpFile.delete();
		}else {
			//generazione del jnlp file e redirect con parametro jnlpFileName
			
			// versione modificata con Bean di Sessione
//			TreDiSignatureJnlpParams treDiSignatureJnlpParams =	(TreDiSignatureJnlpParams) request.getSession().getAttribute("treDiSignatureJnlpParams");
//			String jnlp_href 					= treDiSignatureJnlpParams.getJnlp_href();
//			String DownloadFilesServiceURL 		= treDiSignatureJnlpParams.getDownloadFilesServiceURL();
//			String InputFile					= treDiSignatureJnlpParams.getInputFile(); //URLEncoder.encode((String) request.getParameter("InputFile"));
//			//String FileNameURL					= URLEncoder.encode((String) request.getParameter("FileNameURL"));
//			String FileNameURL					= treDiSignatureJnlpParams.getFileNameURL();
//
//			String OutputFileType 				= treDiSignatureJnlpParams.getOutputFileType();
//			String SendToServerURL				= treDiSignatureJnlpParams.getSendToServerURL();
//			//l'applet vuole url decode nei param, ma qui mi serve encoded per ripassarlo al link href 
//			//SendToServerURL = URLEncoder.encode(SendToServerURL);
//			String cookies						= (String) request.getParameter("cookies");
//			String successJsCode				= "";//(String) request.getParameter("successJsCode");
//			String errorJsCode					= "";//(String) request.getParameter("errorJsCode");
//			String InputOutputFilesSeparator 	= treDiSignatureJnlpParams.getInputOutputFilesSeparator();
//
//			String codebase						= request.getRequestURL().toString();
//			codebase = codebase.substring(0,codebase.indexOf("jnlp"));
				
			// versione copiata da JSP
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

			boolean debug 						= Boolean.valueOf((String) request.getParameter("debug"));
			
			String codebase						= request.getRequestURL().toString();
			codebase = codebase.substring(0,codebase.indexOf("jnlp"));
			
			if(debug) {
				Logger.info("jnlp_href: " + jnlp_href);
				Logger.info("DownloadFilesServiceURL: " + DownloadFilesServiceURL);
				Logger.info("InputFile: " + InputFile);
				Logger.info("FileNameURL: " + FileNameURL);
				Logger.info("OutputFileType: " + OutputFileType);
				Logger.info("SendToServerURL: " + SendToServerURL);
				Logger.info("cookies: " + cookies);
				Logger.info("successJsCode: " + successJsCode);
				Logger.info("errorJsCode: " + errorJsCode);
				Logger.info("InputOutputFilesSeparator: " + InputOutputFilesSeparator);
				Logger.info("codebase: " + codebase);
			}
			
			
			UserBean user = (UserBean) request.getSession().getAttribute("userBean");
			String filePrefix = "user";
			if(user != null) {
				filePrefix = user.getLogin();
			}else {
				Logger.info("userBean: " + null);
			}
			
			File jnlpFile = File.createTempFile(filePrefix, ".jnlp");
			if(jnlpFile == null)
				throw new ServletException("Impossibile salvare JNLP File!");
			
			FileWriter writer = new FileWriter(jnlpFile);

			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			writer.write("\n");
			writer.write("<jnlp spec=\"1.0+\" codebase=\"");
			writer.write( codebase );
			writer.write("\" version=\"1.0\">\n");
			writer.write("\t<information>\n");
			writer.write("\t\t<title>3diSignApplet</title>\n");
			writer.write("\t\t<vendor>3D Informatica</vendor>\n");
			writer.write("\t\t<offline-allowed />\n");
			writer.write("\t</information>\n");
			writer.write("\t<security>\n");
			writer.write("\t\t<all-permissions />\n");
			writer.write("\t</security>\n");
			writer.write("  \n");
			writer.write("\t<!--<update check=\"always\" policy=\"always\"/>-->\n");
			writer.write("\t<update check=\"background\" />\n");
			writer.write("\t\n");
			writer.write("\t<resources>\n");
			writer.write("\t\t<java href=\"http://java.sun.com/products/autodl/j2se\" version=\"1.5+\" />\t\n");
			writer.write("\t\t<jar href=\"jars/3disignapplet.jar\" main=\"true\" />\n");
			writer.write("\t\t<jar href=\"jars/bctsp-jdk15-146.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/bcprov-jdk15-146.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/bcmail-jdk15-146.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/commons-io-2.4.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/commons-logging-1.1.1.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/httpclient-4.3.2.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/httpcore-4.3.1.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/httpmime-4.3.2.jar\" main=\"false\" />\n");
			writer.write("        <jar href=\"jars/iaikPkcs11Wrapper.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/icepdf-core-4.3.3.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/icepdf-viewer-4.3.3.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/itextpdf-5.1.3.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/jaws.jar\" main=\"false\" />\n");
			writer.write("\t\t<jar href=\"jars/commons-codec-1.6.jar\" main=\"false\" />\n");
			writer.write("\t</resources>\n");
			writer.write("\n");
			writer.write("\t<!-- width , height are overridden by applet tag -->\n");
			writer.write("\t<applet-desc name=\"3diSignApplet\" main-class=\"it.tredi.applet.signature.SignatureApplet.class\" width=\"400\" height=\"100\">\n");
			writer.write("\t\t\n");
			writer.write("\t\t\t<param name=\"code\" value=\"it.tredi.applet.signature.SignatureApplet.class\" />\n");
			writer.write("\t\t\t<param name=\"archive\" value=\"3disignapplet.jar\" />\n");
			writer.write("\t\t    <param name=\"jnlp_href\" value=\"");
			writer.write( jnlp_href );
			writer.write("\" />\n");
			writer.write("\t\t    <param name=\"DownloadFilesServiceURL\" value=\"");
			writer.write( DownloadFilesServiceURL );
			writer.write("\" />\n");
			writer.write("\t\t    <param name=\"InputFile\" value=\"");
			writer.write( InputFile );
			writer.write("\" />\n");
			writer.write("\t\t    \n");
			writer.write("\t\t    <param name=\"FileNameURL\" value=\"");
			writer.write( FileNameURL );
			writer.write("\" />\n");
			writer.write("\t\t\t\n");
			writer.write("\t\t\t<param name=\"OutputFileType\" value=\"");
			writer.write( OutputFileType );
			writer.write("\" /> <!-- possibili valori: p7m/pdf -->\n");
			writer.write("\t\t\t\n");
			writer.write("\t\t\t<param name=\"SendToServerURL\" value=\"");
			writer.write( SendToServerURL );
			writer.write("\" />\n");
			writer.write("\t\t\t\n");
			writer.write("\t\t\t<param id=\"3diSignatureAppletParamCookies\" name=\"cookies\" value=\"");
			writer.write( cookies );
			writer.write("\" />\n");
			writer.write("\t\t\t<param name=\"successJsCode\" value=\"");
			writer.write( successJsCode );
			writer.write("\" />\n");
			writer.write("\t\t\t<param name=\"errorJsCode\" value=\"");
			writer.write( errorJsCode );
			writer.write("\" />\n");
			writer.write("\t\t    \n");
			writer.write("\t\t    <param name=\"InputOutputFilesSeparator\" value=\"");
			writer.write( InputOutputFilesSeparator );
			writer.write("\" />\t\t\t\n");
			writer.write("\t</applet-desc> \n");
			writer.write("</jnlp>");
			
			writer.close();
			
			if(debug) {
				Logger.info(new String(Files.readAllBytes(Paths.get(jnlpFile.getAbsolutePath()))));
			}
			
			response.sendRedirect(jnlp_href + "?jnlpFileName=" + jnlpFile.getAbsolutePath());
		}
	}

}
