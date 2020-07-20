package it.tredi.dw4.servlet;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.UploadFileUtil;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

/**
 * Servlet di appoggio per il download di file tramite IWX. In questo modo non
 * viene esposto direttamente il service
 * 
 * @author mbernardini
 */
@SuppressWarnings("serial")
public class FileDownloadServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream outStream = null;
		try {
			String fileName = (String) request.getParameter("name");
			Logger.info("FileDownloadServlet - Param name = " + fileName);
			
			String fileTitle = (String) request.getParameter("title");
			Logger.info("FileDownloadServlet - Param title = " + fileTitle);
			if (fileTitle == null || fileTitle.length() == 0)
				fileTitle = fileName;
			
			String login = "";
			String matricola = "";
			HttpSession session = request.getSession();
			if (session != null && session.getAttribute("userBean") != null) {
				// recupero dei dati dell'utente dalla sessione
				UserBean user = (UserBean) session.getAttribute("userBean");
				login = user.getLogin();
				matricola = user.getMatricola();
			}
			else {
				// tentativo di recupeor dell'utente tramite parametri dalla request
				login = (String) request.getParameter("login");
				matricola = (String) request.getParameter("matricola");
			}
			
			String db = (String) request.getParameter("db");
			String customTupleName = (String) request.getParameter("_cd"); // parametro necessario in caso di multisocieta'
			
			// parametri necessarii in caso di applicazione della segnatura
			String physDoc = (String) request.getParameter("physDoc");
			String selid = (String) request.getParameter("selid");
			String pos = (String) request.getParameter("pos");
			
			// eventuale estrazione di file P7M
			boolean extractIfP7M = StringUtil.booleanValue((String) request.getParameter("extractIfP7M"));
			
			// modalita' di download del file
			String mode = "";
			if (request.getParameter("mode") != null)
				mode = (String) request.getParameter("mode");
			if (!mode.equals("attachment") && !mode.equals("inline"))
				mode = "attachment";
			
			Logger.info("FileDownloadServlet - Param login = " + login);
			Logger.info("FileDownloadServlet - Param matricola = " + matricola);
			Logger.info("FileDownloadServlet - Param db = " + db);
			Logger.info("FileDownloadServlet - Param _cd = " + customTupleName);
			Logger.info("FileDownloadServlet - Param physDoc = " + physDoc);
			Logger.info("FileDownloadServlet - Param selid = " + selid);
			Logger.info("FileDownloadServlet - Param pos = " + pos);
			Logger.info("FileDownloadServlet - Param extractIfP7M = " + extractIfP7M);
			
			// recupero del file tramite chiamata al service
			AttachFile attachFile = this.redirectRequestToService(login, matricola, customTupleName, physDoc, selid, pos, db, fileName, extractIfP7M); // TODO gestire il caso di ritono con errore?
			
			if (extractIfP7M) {
				// in caso di estrazione di p7m occorre eliminare l'estensione del file P7M
				fileName = fileName.toLowerCase().replace(".p7m", "");
				fileTitle = fileTitle.toLowerCase().replace(".p7m", "");
			}
			
			outStream = response.getOutputStream();
			ServletContext context = getServletConfig().getServletContext();
			String mimetype = context.getMimeType(fileName);

			// imposta il content type della response
			if (mimetype == null) {
				mimetype = "application/octet-stream";
			}
			
			// mbernardini 17/03/2015 : in caso di allegati eml viene forzato il mode attachment visto che IE cerca di interpretare il file e non
			// utilizza il client di posta per l'apertura del file
			if (fileName.toLowerCase().endsWith(".eml"))
				mode = "attachment";
			
			String encoding = UploadFileUtil.detectCharset(attachFile.getContent());
			
			response.setCharacterEncoding(encoding); // problema caratteri accentati
			
			response.setContentType(mimetype);
			response.setContentLength(attachFile.getContent().length);
			
			// imposta l'header HTTP
			if (mode.equals("inline")) {
				mode += " ; charset=" + encoding;
				
				//fcappelli 20150123 - test per definire se il corretto mimtype se aperto all'interno del browser,
				//nello specifico per discriminare pdf firmati da p7m
				TikaConfig tika = new TikaConfig();
				Metadata metadata = new Metadata();
				metadata.set(Metadata.RESOURCE_NAME_KEY, attachFile.getFilename());
				MediaType mediaType = tika.getDetector().detect(TikaInputStream.get(attachFile.getContent()), metadata);
				mimetype = mediaType.toString();
			}
			
			response.setHeader("Content-Disposition", mode + "; filename=\"" + fileTitle + "\"");

			outStream.write(attachFile.getContent());
		} catch (Throwable t) {
			Logger.error("Errore in download del file da DocWay-service", t);
			// TODO gestire il caso di eccezione con il ritorno di errore a IWX
		}
		finally {
			if (outStream != null)
				outStream.close();
		}
	}
	
	/**
	 * Redirect della richiesta di download del file a DocWay4-Service
	 * 
	 * @return contenuto della response restituito dal service (file o messaggio di errore) 
	 * @throws Exception
	 */
	private AttachFile redirectRequestToService(String login, String matricola, String customTupleName, String physDoc, String selid, String pos, String db, String fileName, boolean extractIfP7M) throws Exception {
		UserBean userBean = new UserBean(login);
		userBean.setMatricola(matricola);
		
		userBean.setServiceFormParam("db", db);
		userBean.setServiceFormParam("verbo", "attach");
		userBean.setServiceFormParam("xverb", "");
		userBean.setServiceFormParam("enableIW", "true"); // impostato a true perche' questa chiamata viene fatta sempre e solo da IWX
		userBean.setServiceFormParam("_cd", customTupleName); // parametro necessario in caso di multisocieta'
		userBean.setServiceFormParam("physDoc", physDoc);
		userBean.setServiceFormParam("selid", selid);
		userBean.setServiceFormParam("pos", pos);
		userBean.setServiceFormParam("name", fileName);
		userBean.setServiceFormParam("id", fileName);
		userBean.setServiceFormParam("extractIfP7M", extractIfP7M+"");
							
		AdapterConfig config = AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService");
		FormAdapter formAdapter = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
		
		return formAdapter.executeDownloadFile(userBean);
	}

}
