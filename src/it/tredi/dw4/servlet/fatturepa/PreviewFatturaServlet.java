package it.tredi.dw4.servlet.fatturepa;

import it.tredi.utils.bom.UTFBOMStringCleaner;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.ProcessingInstruction;
import org.dom4j.io.SAXReader;

/**
 * Servlet di visualizzazione in anteprima di una fatturaPA. Applicazione di un foglio di stile XSLT
 * all'XML della fattura in modo da produrre una visualizzazione piu' comprensibile per l'operatore.
 * 
 * Necessaria per problemi di caricamento inline dei file con chiamata JSF!!!
 * 
 * @author mbernardini
 */
public class PreviewFatturaServlet extends HttpServlet {

	private static final long serialVersionUID = -4464710984659819807L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream outStream = null;
		try {
			String fileName = (String) request.getParameter("name");
			Logger.info("PreviewFatturaServlet - Param name = " + fileName);
			
			String fileTitle = (String) request.getParameter("title");
			Logger.info("PreviewFatturaServlet - Param title = " + fileTitle);
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
			
			String xsltFileName = (String) request.getParameter("xsltFileName");
			
			Logger.info("PreviewFatturaServlet - Param login = " + login);
			Logger.info("PreviewFatturaServlet - Param matricola = " + matricola);
			Logger.info("PreviewFatturaServlet - Param db = " + db);
			Logger.info("PreviewFatturaServlet - Param _cd = " + customTupleName);
			
			// recupero del file tramite chiamata al service
			AttachFile attachFile = this.redirectRequestToService(login, matricola, customTupleName, db, fileName); // TODO gestire il caso di ritono con errore?
			
			// applicazione del foglio di stile XSLT all'XML della fattura
			byte[] fattura = transformFattura(request, attachFile.getContent(), xsltFileName);
			
			// modifica dell'estensione del nome/titolo del file per corretta gestione del mimetype
			// l'estensione dei file p7m viene convertita in xml perche' in realta' viene scaricato dal 
			// service il contenuto dei file (fattura xml)
			if (fileName.toLowerCase().endsWith(".p7m"))
				fileName = fileName.replace(".p7m", ".xml");
			if (fileTitle.toLowerCase().endsWith(".p7m"))
				fileTitle = fileTitle.replace(".p7m", ".xml");
			
			outStream = response.getOutputStream();
			ServletContext context = getServletConfig().getServletContext();
			String mimetype = context.getMimeType(fileName);

			// imposta il content type della response
			if (mimetype == null) {
				mimetype = "application/octet-stream";
			}
			response.setContentType(mimetype);
			response.setContentLength(fattura.length);
			
			// imposta l'header HTTP
			response.setHeader("Content-Disposition", "inline; filename=\"" + fileTitle + "\"");

			outStream.write(fattura);
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
	 * @param login
	 * @param matricola
	 * @param db
	 * @param fileName
	 * 
	 * @return contenuto della response restituito dal service (file o messaggio di errore) 
	 * @throws Exception
	 */
	private AttachFile redirectRequestToService(String login, String matricola, String customTupleName, String db, String fileName) throws Exception {
		UserBean userBean = new UserBean(login);
		userBean.setMatricola(matricola);
		
		userBean.setServiceFormParam("db", db);
		userBean.setServiceFormParam("verbo", "attach");
		userBean.setServiceFormParam("xverb", "");
		userBean.setServiceFormParam("enableIW", "true"); // impostato a true perche' questa chiamata viene fatta sempre e solo da IWX
		userBean.setServiceFormParam("_cd", customTupleName); // parametro necessario in caso di multisocieta'
		userBean.setServiceFormParam("name", fileName);
		userBean.setServiceFormParam("id", fileName);
		userBean.setServiceFormParam("extractIfP7M", "true"); // in caso di file firmato P7M forza l'estrazione del contenuto
					
		AdapterConfig config = AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService");
		FormAdapter formAdapter = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
		
		return formAdapter.executeDownloadFile(userBean);
	}
	
	/**
	 * generazione dell'anteprima della fattura (si da per scontato che il file passato faccia
	 * riferimento all'XML della fattura)
	 * 
	 * @param bytesfattura
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private byte[] transformFattura(HttpServletRequest req, byte[] bytesfattura, String xsltFileName) throws Exception {
		
		// in caso di mancata indicazione del file xslt da applicare viene utilizzato l'ultimo
		if (xsltFileName == null || xsltFileName.length() == 0) {
			List<String> xslts = DocWayProperties.getPropertyList("fatturepa.xslt");
			if (xslts != null && xslts.size() > 0)
				xsltFileName = xslts.get(xslts.size()-1).substring(xslts.get(xslts.size()-1).indexOf(",")+1);
		}
		
		String xslt = req.getRequestURL().toString().replace("previewfatturapa", "docway/fatturepa/xslt/" + xsltFileName);
		Logger.info("PreviewFatturaServlet - XSLT = " + xslt);
		
		String xmlFattura = "";
		
		// mbernardini 14/04/2015 : eventuale eliminazione di riferimenti a XSL gia' presenti all'interno della fattura
		try {
			SAXReader reader = new SAXReader();
			String strxml = new String(bytesfattura, "UTF-8");
			Document docFattura = reader.read(new StringReader(cleanXMLString(strxml)));
			
			List<?> pis = docFattura.processingInstructions();
			if (pis.size() > 0) {
				for (int i=0; i<pis.size(); i++) {
					ProcessingInstruction pi = (ProcessingInstruction) pis.get(i);
					if (pi != null && pi.getName().equals("xml-stylesheet"))
						docFattura.remove(pi);
				}
			}
			
			DocumentFactory factory = new DocumentFactory();
			Map<String, String> piArgs = new HashMap<String, String>();
			piArgs.put("type", "text/xsl");
			piArgs.put("href", xslt);
			ProcessingInstruction pi = factory.createProcessingInstruction("xml-stylesheet", piArgs);
			List content = docFattura.content();
			content.add(0, pi);
			docFattura.setContent(content);
			
			xmlFattura = docFattura.asXML();
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
			
			xmlFattura = new String(bytesfattura, "UTF-8");
			xmlFattura = cleanXMLString(xmlFattura);
			
			String xmlDeclaration = "";
			if (xmlFattura.contains("<?xml")) {
				xmlDeclaration = xmlFattura.substring(0, xmlFattura.indexOf("?>")+2);
				xmlFattura = xmlFattura.substring(xmlFattura.indexOf("?>")+2);
			}
			
			if (xmlFattura.contains("<?xml-stylesheet"))
				xmlFattura = xmlFattura.substring(xmlFattura.indexOf("?>")+2);
			
			xmlFattura = xmlDeclaration + "<?xml-stylesheet type=\"text/xsl\" href=\"" + xslt + "\"?>" + xmlFattura;
		}
		
		return xmlFattura.getBytes("UTF-8");
	}
	
	
	/**
	 * Ripulisce la stringa contenente XML in input dagli spazi all'inizio e
	 * alla fine e rimuove eventuali BOM ad inizio stringa.
	 * 
	 * @param inputXML
	 *            La stringa contenente il XML da pulire
	 * @return La stringa senza spazi e BOM all'inizio/fine o la stringa
	 *         iniziale se vuota o null.
	 */
	private String cleanXMLString(String inputXML) {
		String result = inputXML;
		if (inputXML != null && !inputXML.isEmpty()) {
			result = result.trim();
			result = UTFBOMStringCleaner.removeBOM(result);
		}
		return result;
	}
	
}
