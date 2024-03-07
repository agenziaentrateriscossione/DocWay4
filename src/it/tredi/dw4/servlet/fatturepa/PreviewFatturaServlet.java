package it.tredi.dw4.servlet.fatturepa;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
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
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.ProcessingInstruction;
import org.dom4j.io.SAXReader;

import it.tredi.configurator.reader.FileReader;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;
import it.tredi.utils.bom.UTFBOMStringCleaner;
import it.tredi.utils.bom.UnicodeBOMInputStream;

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
	
	private static final String XSLT_FOP_PATH = "/it/tredi/dw4/servlet/fatturepa/xslfo/";

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
			String xslFopFileName = (String) request.getParameter("xslFopFileName");
			
			Logger.info("PreviewFatturaServlet - Param login = " + login);
			Logger.info("PreviewFatturaServlet - Param matricola = " + matricola);
			Logger.info("PreviewFatturaServlet - Param db = " + db);
			Logger.info("PreviewFatturaServlet - Param _cd = " + customTupleName);
			
			// recupero del file tramite chiamata al service
			AttachFile attachFile = this.redirectRequestToService(login, matricola, customTupleName, db, fileName); // TODO gestire il caso di ritono con errore?
			
			boolean fop = Boolean.parseBoolean(request.getParameter("xslFOP"));
			// genera il PDf con FOP
			byte[] fattura;
			if (fop) {
				
				// nuovi parametri per visualizzazione XSL FOP
				Map <String, String> signatureParams = new HashMap<String, String>();
				if (fop) {
					signatureParams.put("NumeroProtocollo", (String) request.getParameter("numProt"));
					signatureParams.put("DataProtocollo", (String) request.getParameter("dataProt"));
					signatureParams.put("TipoDocumento", (String) request.getParameter("tipoDoc"));
					signatureParams.put("ClassificazioneDocumento", (String) request.getParameter("cassif"));
				}
			
				// estra il file di stile xsl da conf 
				File xsltFile;
				String pathTemplateFattura = "/fatturePA/xslFOP/" + xslFopFileName;
				FileReader fileReader = new FileReader(Const.RESOURCE_APP_CONTEXT);
				try {
					xsltFile = fileReader.getCustomResource(pathTemplateFattura);
					// se non presente in conf lo estrae come resource dal progetto
					if (xsltFile == null) {
						ClassLoader classLoader = getClass().getClassLoader();
						xsltFile = new File(URLDecoder.decode(classLoader.getResource(XSLT_FOP_PATH + xslFopFileName).getPath(), "utf-8"));
					}
				}
				catch (Exception ex) {
					Logger.error("Impossibile caricare il foglio di stile XSLT necessario per la genarazione del PDF con FOP", ex);
					throw ex;
				}
				
				StreamSource xmlSource;
				
				// modifica dell'estensione del nome/titolo del file per corretta gestione del mimetype
				int index = fileTitle.indexOf(".");
				if (index != -1)
					fileTitle = fileTitle.substring(0, index);
				fileTitle += ".pdf";
				
				// ottiene il byte array del file xml da mostrare
				fattura = transformFatturaFOP(attachFile.getContent(), signatureParams);
				xmlSource = new StreamSource(new ByteArrayInputStream(fattura));
				
				// crea un instanza del FOP factory
				FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
				// user agent necessario per la trasformazione
				FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
				// setpup output
				outStream = response.getOutputStream();
				
				response.setContentType(MimeConstants.MIME_PDF);
				response.setHeader("Content-Disposition", "inline; filename=\"" + fileTitle + "\"");
				
				try {
					// Construct fop with desired output format
					Fop fopFile = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outStream);

					// Setup XSLT
					TransformerFactory factory = TransformerFactory.newInstance();
					Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

					// Resulting SAX events (the generated FO) must be piped through to
					// FOP
					Result res = new SAXResult(fopFile.getDefaultHandler());

					// Start XSLT transformation and FOP processing
					// That's where the XML is first transformed to XSL-FO and then
					// PDF is created
					transformer.transform(xmlSource, res);
				} 
				catch (Exception ex) {
					Logger.error("Errore nella creazione del PDF con FOP", ex);
					throw ex;
				}
			}
			// applicazione del foglio di stile XSLT all'XML della fattura
			else {

				fattura = transformFatturaXSLT(request, attachFile.getContent(), xsltFileName);
				
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
			}
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
	private byte[] transformFatturaXSLT(HttpServletRequest req, byte[] bytesfattura, String xsltFileName) throws Exception {
		
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
			// mbernardini 10/04/2019 : problema di enconding su contenuto del file XML
			//String strxml = new String(bytesfattura, "UTF-8");
			//Document docFattura = reader.read(new StringReader(cleanXMLString(strxml)));
			UnicodeBOMInputStream is = new UnicodeBOMInputStream(new ByteArrayInputStream(bytesfattura));
			is.skipBOM();
			Document docFattura = reader.read(is);
			
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
		
		return xmlFattura.getBytes(/*"UTF-8"*/); // mbernardini 10/04/2019 : corretto problema di enconding su contenuto del file XML
	}
	
	/**
	 * generazione dell'byte array con i dati di segnatura necessario per la creazione del PDF con FOP
	 * 
	 * @param bytesfattura
	 * @return
	 * @throws Exception
	 */
	private byte[] transformFatturaFOP(byte[] bytesfattura, Map<String, String> signatureParams) throws Exception {
		
		String xmlFattura = "";
		
		// mbernardini 14/04/2015 : eventuale eliminazione di riferimenti a XSL gia' presenti all'interno della fattura
		try {
			SAXReader reader = new SAXReader();
			// mbernardini 10/04/2019 : problema di enconding su contenuto del file XML
			//String strxml = new String(bytesfattura, "UTF-8");
			//Document docFattura = reader.read(new StringReader(cleanXMLString(strxml)));
			UnicodeBOMInputStream is = new UnicodeBOMInputStream(new ByteArrayInputStream(bytesfattura));
			is.skipBOM();
			Document docFattura = reader.read(is);
			
			// Aggiunta dei dati di segnatura
			if (signatureParams != null) {
				Element datiSegnatura = DocumentHelper.createElement("DatiSegnatura");
				for (Map.Entry<String, String> entry : signatureParams.entrySet()) {
					Element keyElement = DocumentHelper.createElement(entry.getKey());
					keyElement.addText((entry.getValue() != null) ? entry.getValue() : "");
					datiSegnatura.add(keyElement);
				}
				docFattura.getRootElement().add(datiSegnatura);
			}
			
			xmlFattura = docFattura.asXML();
		}
		catch (Exception e) {
			Logger.error("PreviewFattura.transformFatturaFOP(): got exception... " + e.getMessage(), e);
		}
		
		return xmlFattura.getBytes(/*"UTF-8"*/); // mbernardini 10/04/2019 : corretto problema di enconding su contenuto del file XML
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
