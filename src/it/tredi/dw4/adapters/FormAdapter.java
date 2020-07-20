package it.tredi.dw4.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.UploadFileUtil;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class FormAdapter {
	private String host;
	private int port;
	private String protocol;
	private String resource;
	private String userAgent;
	
	private HttpClient client;
	private HttpHost httpHost;
	private HttpClientContext localcontext;
	private CookieStore cookieStore;
	
	private Map<String, String> params;
	
	public static String ENCODING_ISO_8859_1 = "ISO-8859-1";
	public static String ENCODING_UFT_8 = "UTF-8";
	
	public FormAdapter(String host, int port, String protocol, String resource, String userAgent) {
		this.host = host;
		this.port = port;
		this.protocol = protocol;
		this.resource = resource;
		this.userAgent = userAgent;
		
		this.client = HttpClients.createDefault();
		this.httpHost = new HttpHost(this.host, this.port, this.protocol);
		this.cookieStore = new BasicCookieStore();
		this.localcontext = HttpClientContext.create();
		this.localcontext.setCookieStore(this.cookieStore);
		this.params = new HashMap<String, String>();
	}

	//FIXME una volta impostata per adesso non è più possibile disabilitarla
	public void setCredentials(String userName, String password) {
	    CredentialsProvider credsProvider = new BasicCredentialsProvider();
	    credsProvider.setCredentials(
	            new AuthScope(this.httpHost.getHostName(), this.httpHost.getPort()),
	            new UsernamePasswordCredentials(userName, password));
	    client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		
	    // Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(this.httpHost, basicAuth);

		// Add AuthCache to the execution context
		localcontext.setAuthCache(authCache);
	}
	
	public void resetParams() {
		this.params.clear();
	}
	
	public Map<String, String> getParamsMap() {
		return this.params;
	}
	
	public String getParam(String name) {
		String value = this.params.get(name);
		return (value == null)? "" : value;
	}

	public int getParamAsInt(String name) {
		String value = getParam(name);
		return (value == null || value.equals("")) ? 0 : Integer.parseInt(value); // in caso di valore vuoto la conversione in int provocherebbe un errore
	}	
	
	public void addParam(String name, String value) {
		this.params.put(name, value);
	}
	
	public void addParams(Map<String, String> paramMap) {
		this.params.putAll(paramMap);
	}
	
	public void addParam(String name, int value) {
		this.params.put(name, Integer.toString(value));
	}
	
	public void addParam(String name, boolean value) {
		this.params.put(name, Boolean.toString(value));
	}
	
	public String removeParam(String name) {
		return this.params.remove(name);
	}
	
	private HttpResponse doPostRequest(String userLogin, Map<String, String> sessionParams, Map<String, String> forcedSessionParams) throws Exception {
		HttpPost post = new HttpPost(this.resource);
		
		//non essendo protetto il servizio passo lo username nell'header http Hw-Username
		//FIXME - bisogna implementare un mecanismo di crittazione simmetrica tra i due servizi
		//per offuscare il nome dell'utente.
		post.setHeader("Hw-Username", userLogin);
		
		if (this.userAgent != null) {
			post.setHeader("User-Agent", this.userAgent);
		}		
		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> param : this.params.entrySet()) {
			if (forcedSessionParams.get(param.getKey()) != null && forcedSessionParams.get(param.getKey()).length() > 0) {
				// se il parametro viene trovato fra i forcedSessionParams utilizzo il valore specificato
				// a livello di sessione "forzata"
				formParams.add(new BasicNameValuePair(param.getKey(), forcedSessionParams.get(param.getKey())));
			}
			else {
				// in caso contrario utilizzo il parametro inviato dal bean (se valorizzato) o quello definito in sessione NON "forzata"
				if(sessionParams.get(param.getKey()) == null || (sessionParams.get(param.getKey()) != null && param.getValue().length() > 0)){
					formParams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
				}
				else if(sessionParams.get(param.getKey()) != null) {
					formParams.add(new BasicNameValuePair(param.getKey(), sessionParams.get(param.getKey())));
				}
			}
		}
		
		// aggiunta dei parametri di sessione non "forzata" (solamente se non sono gia' gestiti dal form adapter)
		for (Map.Entry<String, String> param : sessionParams.entrySet()) {
			if (this.params.get(param.getKey()) == null)
				formParams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
		}
		
		// aggiunta dei parametri di sessione "forzata" (solamente se non sono gia' gestiti dal form adapter)
		for (Map.Entry<String, String> param : forcedSessionParams.entrySet()) {
			if (this.params.get(param.getKey()) == null)
				formParams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
		}
		
		// in caso sia presente il parametro jsessionid (riferimento a sessione sul Service), viene aggiunto
		// il valore all'header della richiesta
		if (this.params.containsKey("jsessionid") 
				&& this.params.get("jsessionid") != null && !this.params.get("jsessionid").equals("")) {
			post.setHeader("Cookie", "JSESSIONID="+ this.params.get("jsessionid"));
		}
		
		// mbernardini 18/02/2015 : encoding a UTF-8 per problema con carattere euro
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, ENCODING_UFT_8); //FIXME per adesso l'encoding è hardcodato
		post.setEntity(entity);
		
		//ora fai effettivamante la richiesta
		return this.client.execute(this.httpHost, post, this.localcontext);
	}
	
	public XMLDocumento executePOST(UserBean userBean) throws Exception {
		long startTime = System.currentTimeMillis();
		
		XMLDocumento resp = handleHttpResponse(doPostRequest(userBean.getLogin(), userBean.getServiceFormParams(), userBean.getForcedServiceFormParams()));
		
		long endTime = System.currentTimeMillis();
		Logger.info("FormAdapter.executePOST(), time elapsed: " + (endTime - startTime) + " ms"); 
		
		return resp;
	}
	
	public XMLDocumento executeGET() throws Exception {
		URIBuilder builder = new URIBuilder(this.httpHost.toURI());
		builder.setPath(this.resource);
		
		for (Map.Entry<String, String> param : this.params.entrySet()) {
			builder.setParameter(param.getKey(), param.getValue());
		}
		
		HttpGet get = new HttpGet(builder.build());
		
		if (this.userAgent != null) {
			get.setHeader("User-Agent", this.userAgent);
		}
		
		//ora fai effettivamante la richiesta
		HttpResponse response = this.client.execute(this.httpHost, get, this.localcontext);
		return handleHttpResponse(response);
	}
	
	/**
	 * Ritorna il file di versione dell'applicazione tramite il recupero dal service
	 * @param appName
	 * @return
	 * @throws Exception
	 */
	public XMLDocumento getVersioneApp(String appName) throws Exception {
		if (appName == null)
			appName = "xdocway";
		
		String pathVersioniXml = "";
		if (appName.equals("xdocway"))
			pathVersioniXml = "/application/xdocway/formatter/html/templates/xdocwaydoc/versioni.xml";
		else if(appName.equals("delibere"))
			pathVersioniXml = "/application/xdocway/formatter/html/templates/xdocwaydoc/versioni@to.xml";
		else
			// TODO gestire gli altri casi
			return null;
		
		// TODO gestire $versionsSuffix (@versioni@to)
		URIBuilder builder = new URIBuilder(this.httpHost.toURI() + this.resource.substring(0, this.resource.indexOf("/", 1)) + pathVersioniXml);
		HttpGet get = new HttpGet(builder.build());

		if (this.userAgent != null) {
			get.setHeader("User-Agent", this.userAgent);
		}
		
		//ora fai effettivamante la richiesta
		HttpResponse response = this.client.execute(this.httpHost, get, this.localcontext);
		return handleHttpResponse(response);
	}
	
	public AttachFile executeDownloadFile(UserBean userBean) throws Exception {
		return responseToAttachFile(doPostRequest(userBean.getLogin(), userBean.getServiceFormParams(), userBean.getForcedServiceFormParams()));
	}
	
	/**
	 * Richiesta di upload di un file
	 * @param userBean bean dell'utente
	 * @param fileParam nome del parametro con il quale uploadare il file
	 * @param file file da uploadare
	 * @return
	 * @throws Exception
	 */
	public XMLDocumento executeUploadRequestToXMLDocumento(UserBean userBean, String fileParam, File file) throws Exception {
		if (fileParam == null || fileParam.equals(""))
			fileParam = "filename";

		HashMap<String, File> files = new HashMap<String, File>();
		files.put(fileParam, file);
		
		return handleHttpResponse(_executeUploadRequest(userBean.getLogin(), files, userBean.getServiceFormParams()));
	}
	
	/**
	 * Richiesta di upload di piu' files
	 * @param userBean bean dell'utente
	 * @param files HashMap contenente i files da caricare (come nome del parametro viene utilizzata la chiave della hashmap)
	 * @return
	 * @throws Exception
	 */
	public XMLDocumento executeUploadRequestToXMLDocumento(UserBean userBean, HashMap<String, File> files) throws Exception {
		return handleHttpResponse(_executeUploadRequest(userBean.getLogin(), files, userBean.getServiceFormParams()));
	}
	
	/**
	 * Richiesta di upload di un file (upload da servlet)
	 * @param userLogin login dell'utente
	 * @param fileParam nome del parametro con il quale uploadare il file
	 * @param file file da uploadare
	 * @param otherParams altri parametri della request (es. login, matricola, verbo, xverb, ecc.)
	 * @return
	 * @throws Exception
	 */
	public String executeUploadRequestToString(String userLogin, String fileParam, File file, Map<String, String> otherParams) throws Exception {
		if (fileParam == null || fileParam.equals(""))
			fileParam = "filename";

		HashMap<String, File> files = new HashMap<String, File>();
		files.put(fileParam, file);
		
		return this.responseToString(_executeUploadRequest(userLogin, files, otherParams));
	}
	
	/**
	 * Richiesta di upload di un file
	 */
	@SuppressWarnings("rawtypes")
	private HttpResponse _executeUploadRequest(String userLogin, HashMap<String, File> files, Map<String, String> sessionParams) throws Exception {
		HttpPost post = new HttpPost(this.resource);
		
		//non essendo protetto il servizio passo lo username nell'header http Hw-Username
		//FIXME - bisogna implementare un mecanismo di crittazione simmetrica tra i due servizi
		//per offuscare il nome dell'utente.
		post.setHeader("Hw-Username", userLogin);
		
		if (this.userAgent != null) {
			post.setHeader("User-Agent", this.userAgent);
		}		
		
		MultipartEntityBuilder entity = MultipartEntityBuilder.create();
		entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.setCharset(Charset.forName(ENCODING_UFT_8/*ENCODING_ISO_8859_1*/)); //FIXME per adesso l'encoding è hardcodato
		
		for (Map.Entry<String, String> param : this.params.entrySet()) {
			if(sessionParams.get(param.getKey()) == null || (sessionParams.get(param.getKey()) != null && param.getValue().length() > 0)){
				entity.addTextBody(param.getKey(), param.getValue());
			}
			else if(sessionParams.get(param.getKey()) != null) {
				entity.addTextBody(param.getKey(), sessionParams.get(param.getKey()));
			}
		}
		
		//aggiunta dei parametri di sessione (solamente se non sono già gestiti dal form adapter
		for (Map.Entry<String, String> param : sessionParams.entrySet()) {
			if (this.params.get(param.getKey()) == null)
				entity.addTextBody(param.getKey(), param.getValue());
		}
		
		// aggiunta dei file
		Iterator it = files.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			if (entry != null) {
				String param = (String) entry.getKey();
				File file = (File) entry.getValue();
				entity.addPart(param, new FileBody(file));
			}
		}
		
		post.setEntity(entity.build());
		
		//ora fai effettivamante la richiesta
		return this.client.execute(this.httpHost, post, this.localcontext);
	}
	
	/**
	 * conversione della response del service in stringa (XML di ritorno)
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private String responseToString(HttpResponse response) throws Exception {
		return responseToString(response, false); // chiamata senza detect dell'encoding (default ISO-8859-1)
	}
	
	/**
	 * conversione della response del service in stringa con possibile riconoscimento dell'encoding. In caso di trasformazione
	 * senza detect dell'encoding verra' utilizzato come default ISO-8859-1
	 * 
	 * @param response
	 * @param detectEncoding
	 * @return
	 * @throws Exception
	 */
	private String responseToString(HttpResponse response, boolean detectEncoding) throws Exception {
		String responseString = "";
		
		long begin = System.currentTimeMillis();
		
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream instream = entity.getContent();
			try {
				String encoding = ENCODING_UFT_8/*ENCODING_ISO_8859_1*/; // ritorno classico di DocWay-service
				
				if (detectEncoding) { 
					// scaricamento di allegati HTML (o eventuali pagine di errore del service)
					
					byte[] bytes = IOUtils.toByteArray(instream);
				
					if (detectEncoding) {
			        	try {
			        		encoding = UploadFileUtil.detectCharset(bytes); // scaricamento di allegati HTML (o eventuali pagine di errore del service)
			        	}
			        	catch (Exception e) {
			        		Logger.error(e.getMessage(), e);
			        	}
			        }
					
					responseString = new String(bytes, encoding);
				}
				else {
					// caricamento di pagine classiche di DocWay (ritorno XML da DocWay-service con encoding ISO-8859-1)
					
					StringWriter writer = null;
			        try {
			        	writer = new StringWriter();
						char[] buffer = new char[1024];
				        
				        Reader reader = new BufferedReader(new InputStreamReader(instream, encoding));
		                int n;
		                while ((n = reader.read(buffer)) != -1) {
				        	writer.write(buffer, 0, n);
		                }
		                
						responseString = writer.toString();
			        }
			        finally {
			        	try {
				        	if (writer != null)
				        		writer.close();
			        	}
			        	catch (Exception e) { }
			        }
				}
			} 
			finally {
				if (instream != null)
					instream.close();
				
				// Exception: Invalid use of BasicClientConnManager: connection still allocated. Make sure to release the connection before allocating another one.
				EntityUtils.consume(entity); // chiusura della connessione // TODO come verificarlo?
			}
		}
		
		long end = System.currentTimeMillis();
		Logger.info("FormAdapter.responseToString(), time elapsed: " + (end - begin) + " ms");
		
		return responseString;
	}
	
	private AttachFile responseToAttachFile(HttpResponse response) throws Exception {
		AttachFile attachFile = null;
		
		Header contentType = response.getFirstHeader("Content-Type");
		if (contentType == null || contentType.getValue().equals("text/html") || contentType.getValue().startsWith("text/html;charset=") || contentType.getValue().equals("text/xml")) {
			// Ritorno XML (probabile errore riscontrato lato service). es. File non trovato o un file html
			String fileName = getFileNameFromResponse(response);
			
			String responseString = responseToString(response, true);
			if (responseString.startsWith("<html>")
					|| responseString.startsWith("<html ")
					|| responseString.startsWith("<!DOCTYPE html")
					|| responseString.startsWith("<!DOCTYPE HTML")) {
				//responseString = responseString.replace("<html>", "<html>\n<head><script type=\"text/javascript\" src=\"js/myjs.js\"></script></head>\n");
				
				if (fileName == null || fileName.equals(""))
					fileName = "info.html";
				
				attachFile = new AttachFile(fileName, responseString.getBytes());
			}
			else {
				if (fileName == null || fileName.equals("")) {
					int httpStatusCode = response.getStatusLine().getStatusCode();
					String statusLineReason = response.getStatusLine().getReasonPhrase();
					
					attachFile = new AttachFile(handleHttpResponse(responseString, httpStatusCode, statusLineReason));
				}
				else
					attachFile = new AttachFile(fileName, responseString.getBytes());
			}
		}
		else {
			// Download del file
			
			Header contentDisposition = response.getFirstHeader("Content-Disposition");
			if (contentDisposition != null) {
				String cdValue = contentDisposition.getValue();
				if (cdValue != null && cdValue.length() > 0 && cdValue.indexOf("filename") != -1) {
					// Caso di download del file eseguito con successo
					byte[] buf = null;
					
					// Recupero dall'header Content-Disposition il nome del file restituito dal service
					String filename = StringUtil.substringAfter(cdValue, "filename=");
					if (filename.startsWith("\"") && filename.endsWith("\""))
						filename = filename.substring(1, filename.length()-1);
					
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						InputStream instream = entity.getContent();
						try {
							int len;
							int size = 1024;
							
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							buf = new byte[size];
							while ((len = instream.read(buf, 0, size)) != -1)
								bos.write(buf, 0, len);
							buf = bos.toByteArray();
						} finally {
							instream.close();
							
							// Exception: Invalid use of BasicClientConnManager: connection still allocated. Make sure to release the connection before allocating another one.
							EntityUtils.consume(entity); // chiusura della connessione
						}
					}
					
					attachFile = new AttachFile(filename, buf);
				}
			}
		}
		
		return attachFile;
	}
	
	/**
	 * Data la response restituisce il nome del file da scaricare (se presente)
	 * 
	 * @param response
	 * @return
	 */
	private String getFileNameFromResponse(HttpResponse response) {
		String filename = "";
		Header contentDisposition = response.getFirstHeader("Content-Disposition");
		if (contentDisposition != null) {
			String cdValue = contentDisposition.getValue();
			if (cdValue != null && cdValue.length() > 0 && cdValue.indexOf("filename") != -1) {
				// Recupero dall'header Content-Disposition il nome del file restituito dal service
				filename = StringUtil.substringAfter(cdValue, "filename=");
				if (filename.startsWith("\"") && filename.endsWith("\""))
					filename = filename.substring(1, filename.length()-1);
			}
		}
		return filename;
	}
	
	/**
	 * Conversione della response HTTP in documento XML
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public XMLDocumento handleHttpResponse(HttpResponse response) throws Exception {
		String responseString = responseToString(response);
		int httpStatusCode = response.getStatusLine().getStatusCode();
		String statusLineReason = response.getStatusLine().getReasonPhrase();
		
		return handleHttpResponse(responseString, httpStatusCode, statusLineReason);
	}
	
	/**
	 * Conversione della response HTTP in documento XML
	 * 
	 * @param responseString
	 * @param httpStatusCode
	 * @param statusLineReason
	 * @return
	 * @throws Exception
	 */
	private XMLDocumento handleHttpResponse(String responseString, int httpStatusCode, String statusLineReason) throws Exception {
		XMLDocumento responseDocumento = null;

		//TEST - stato http		
		if (httpStatusCode == HttpStatus.SC_OK && responseString.length() >0) { //invocazione effettuata con successo
			try { //parsing xml
				
				// mbernardini 18/02/2015 : utilizzato un diverso costruttore in modo da evitare che "&#" vernga convertito in "&amp;#" 
				// compromettendo cosi' la visualizzazione di caratteri speciali come l'euro (&#x80;)
				responseDocumento = new XMLDocumento(responseString);
				//responseDocumento = new XMLDocumento(responseString, 2, true);
			} 
			catch (Exception e) {
				responseDocumento = ErrormsgFormsAdapter.buildErrorResponse(I18N.mrs("dw4.parsing_service_response_error") + ": " + resource, responseString, ErrormsgFormsAdapter.ERROR);
			}
		}
		else { //errore http
			// verifico se e' stata restituita la pagina di errore di DocWay3 (in questo caso cerco di estrapolare
			// i dettagli dell'errore in modo da mostrarli all'utente su DocWay4)
			if (isPaginaErrore(responseString)) {
				String errorCode = getErrorCode(responseString);
				errorCode = errorCode.replaceAll("\n", "");
				String errorDetails = getErrorDetails(responseString);
				String errorLevel = getErrorLevel(errorCode);
				
				responseDocumento = ErrormsgFormsAdapter.buildErrorResponse(errorCode, errorDetails, errorLevel, httpStatusCode+"");
			}
			else {
				responseDocumento = ErrormsgFormsAdapter.buildErrorResponse(I18N.mrs("dw4.invoking_service_error") + ": \nHTTP Status " + httpStatusCode + " - " + statusLineReason, responseString, ErrormsgFormsAdapter.FATAL, httpStatusCode+"");
			}
		}
		
		return responseDocumento; 
	}
	
	/**
	 * Verifica se il contenuto della response e' relativo ad una pagina di errore 
	 * di DocWay3 (xdocwayerror.jsp)
	 * 
	 * @param responseString reponse del service
	 * @return
	 */
	private boolean isPaginaErrore(String responseString) {
		if (responseString != null && responseString.contains("<!-- Inzio Pagina Errore -->"))
			return true;
		else
			return false;
	}
	
	/**
	 * Restituisce il codice di errore restituito dal service (letto dal contenuto della
	 * pagina di errore)
	 * 
	 * @param responseString reponse del service
	 * @return
	 */
	private String getErrorCode(String responseString) {
		String errorCode = "";
		if (responseString != null) {
			int index = responseString.indexOf("<code>");
			int index2 = responseString.indexOf("</code>");
			if (index != -1 && index2 != -1)
				errorCode = responseString.substring(index+6, index2);
		}
		return errorCode;
	}
	
	/**
	 * Restituisce il dettaglio dell'errore restituito dal service (letto dal contenuto della
	 * pagina di errore)
	 * 
	 * @param responseString reponse del service
	 * @return
	 */
	private String getErrorDetails(String responseString) {
		String errorDetails = "";
		if (responseString != null) {
			if (StringUtil.count(responseString, "<code>") > 1) {
				int index = responseString.lastIndexOf("<code>");
				int index2 = responseString.lastIndexOf("</code>");
				if (index != -1 && index2 != -1)
					errorDetails = responseString.substring(index+6, index2);
			}
		}
		return errorDetails;
	}
	
	/**
	 * definizione del livello di errore in base alla tipologia di errore restituito dal service
	 * @param errorCode
	 * @return
	 */
	private String getErrorLevel(String errorCode) {
		String errorLevel = ErrormsgFormsAdapter.FATAL;
		
		if (errorCode != null && errorCode.length() > 0) {
			if (errorCode.indexOf(Const.RITORNO_ESITO_RICERCA_NULLO) != -1)
				errorLevel = ErrormsgFormsAdapter.INFO;
			else if (errorCode.indexOf(Const.RITORNO_AMBITO_DI_RICERCA_TROPPO_AMPIO) != -1)
				errorLevel = ErrormsgFormsAdapter.WARNING;
			else if (errorCode.indexOf(Const.RITORNO_IMPOSSIBILE_RINVIARE_LE_PROPOSTE_NON_DISCUSSE) != -1)
				errorLevel = ErrormsgFormsAdapter.WARNING;
		}
		
		return errorLevel;
	}
	
}
