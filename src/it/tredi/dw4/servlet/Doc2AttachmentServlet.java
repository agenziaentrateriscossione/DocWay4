package it.tredi.dw4.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.utils.Logger;

/**
 * Servlet di visualizzazione in anteprima della copertina di un raccoglitore di tipo Indice.
 * Necessaria per problemi di caricamento inline dei file con chiamata JSF!!!
 * 
 * @author mbernardini
 */
public class Doc2AttachmentServlet extends HttpServlet {

	private static final long serialVersionUID = 2326742976605318512L;

	// parametri necessari allo scaricamento di un allegato derivante da template
	private String physDoc;
	private String selid;
	private String pos;
	private String template;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream outStream = null;
		try {
			template = (String) request.getParameter("template");
			physDoc = (String) request.getParameter("physDoc");
			selid = (String) request.getParameter("selid");
			pos = (String) request.getParameter("pos");
			
			Logger.info("Doc2AttachmentServlet - template = " + template);
			Logger.info("Doc2AttachmentServlet - physDoc = " + physDoc);
			Logger.info("Doc2AttachmentServlet - selid = " + selid);
			Logger.info("Doc2AttachmentServlet - pos = " + pos);
			
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
			
			Logger.info("Doc2AttachmentServlet - login = " + login);
			Logger.info("Doc2AttachmentServlet - matricola = " + matricola);
			Logger.info("Doc2AttachmentServlet - db = " + db);
			Logger.info("Doc2AttachmentServlet - _cd = " + customTupleName);
			
			// recupero del file tramite chiamata al service
			AttachFile attachFile = this.redirectRequestToService(login, matricola, customTupleName, db); // TODO gestire il caso di ritono con errore?
			
			outStream = response.getOutputStream();
			ServletContext context = getServletConfig().getServletContext();
			String mimetype = context.getMimeType(attachFile.getFilename());

			// imposta il content type della response
			if (mimetype == null) {
				mimetype = "application/octet-stream";
			}
			response.setContentType(mimetype);
			response.setContentLength(attachFile.getContent().length);
			
			// imposta l'header HTTP
			response.setHeader("Content-Disposition", "inline; filename=\"" + attachFile.getFilename() + "\"");

			outStream.write(attachFile.getContent());
		} catch (Throwable t) {
			Logger.error("Errore in download del file da DocWay-service", t);
		}
		finally {
			if (outStream != null)
				outStream.close();
		}
	}
	
	/**
	 * Redirect della richiesta di download del file a DocWay4-Service
	 * @param login
	 * @param matricola
	 * @param customTupleName
	 * @param db
	 * @param doc2attachment
	 * 
	 * @return contenuto della response restituito dal service (file o messaggio di errore) 
	 * @throws Exception
	 */
	private AttachFile redirectRequestToService(String login, String matricola, String customTupleName, String db) throws Exception {
		UserBean userBean = new UserBean(login);
		userBean.setMatricola(matricola);
		
		userBean.setServiceFormParam("db", db);
		userBean.setServiceFormParam("verbo", "doc2attachment");
		userBean.setServiceFormParam("xverb", "@download");
		userBean.setServiceFormParam("_cd", customTupleName); // parametro necessario in caso di multisocieta'
		userBean.setServiceFormParam("template", template);
		userBean.setServiceFormParam("physDoc", physDoc);
		userBean.setServiceFormParam("selid", selid);
		userBean.setServiceFormParam("pos", pos);
					
		AdapterConfig config = AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService");
		FormAdapter formAdapter = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
		
		return formAdapter.executeDownloadFile(userBean);
	}
	
}
