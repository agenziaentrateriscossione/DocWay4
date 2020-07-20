package it.tredi.dw4.servlet;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.UploadFileUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Gestione della firma digitale tramite applet:
 * - lista di file necessari all'utilizzo dell'applet di firma (dipendenze)
 * - chiamata a service per salvataggio del file firmato
 * 
 * @author mbernardini
 */
public class TreDiSignatureToolsServlet extends HttpServlet {

	private static final long serialVersionUID = -7611208551000071422L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HashMap<String, String> params = new HashMap<String, String>();
		FileItem fileItem = null;
		
		FileItemFactory fileItemFactory = new DiskFileItemFactory();
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		
		try {
			List<?> items = uploadHandler.parseRequest(request);
			Iterator<?> itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				/*
				 * Handle Form Fields.
				 */
				if (item.isFormField()) {
					params.put(item.getFieldName(), item.getString()); // altri parametri della request (non file)
					Logger.info("TreDiSignatureDependenciesServlet - Param: " + item.getFieldName() + " = " + item.getString());
				} else {
					// upload di file
					fileItem = item;
					Logger.info("TreDiSignatureDependenciesServlet - Param File: Name = " + item.getFieldName() + ", File Name = " + item.getName() + ", Content type = " + item.getContentType() + ", File Size = " + item.getSize());
				}
			}
		}
		catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}
		
		String action = (String) params.get("action");
		Logger.info("TreDiSignatureDependenciesServlet - action : " + action);
		
		if (action == null)
			action = "";
		
		String realPath = request.getSession().getServletContext().getRealPath("/");
		
		File depFolder = new File(realPath + "docway" + File.separator + "3diSignatureApplet" + File.separator + "dependencies");
		if (!depFolder.exists()) {
			Logger.error("TreDiSignatureDependenciesServlet - dependecies folder not found!");
			
			return;
		}
		
		if (action.equals("filelist")) {
			// recupero l'elenco delle dependencies
			
			File[] files = depFolder.listFiles();
			if (files != null && files.length > 0) {
				for (int i=0; i<files.length; i++) {
					if (files[i].isFile()) {
						String encodedFile = econdeFileName(files[i]);
						if (encodedFile != null) 
							response.getOutputStream().println(files[i].getName() + ";" + encodedFile);
					}
				}
			}
			
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
		else if (action.equals("getfile")) {
			// download di un file di dependencies specifico
			
			ServletOutputStream outStream = null;
			try {
				String fileName = params.get("filename");
				if (fileName != null && fileName.length() > 0) {
					File requestedFile = new File(depFolder, fileName);
					if (requestedFile.exists()) {
						outStream = response.getOutputStream();
						ServletContext context = getServletConfig().getServletContext();
						String mimetype = context.getMimeType(fileName);
			
						// imposta il content type della response
						if (mimetype == null) {
							mimetype = "application/octet-stream";
						}
						response.setContentType(mimetype);
						response.setContentLength(new Integer(requestedFile.length()+"").intValue());
						
						// imposta l'header HTTP
						response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
						
						outStream.write(getBytes(requestedFile));
					}
				}
			}
			catch (Throwable t) {
				Logger.error("TreDiSignatureDependenciesServlet - Errore in download del file", t);
			}
			finally {
				if (outStream != null)
					outStream.close();
			}
		}
		else if (action.equals("addsignedfile")) {
			String uploadResponse = "";
			
			// upload del file firmato
			Logger.info("TreDiSignatureDependenciesServlet - signed file upload");
			
			try {
				// upload su docway-service
				Logger.info("TreDiSignatureDependenciesServlet - docway4-service upload");
				
				Logger.info("TreDiSignatureDependenciesServlet - param login : " + params.get("login"));
				Logger.info("TreDiSignatureDependenciesServlet - param matricola : " + params.get("matricola"));
				Logger.info("TreDiSignatureDependenciesServlet - param db : " + params.get("db"));
				Logger.info("TreDiSignatureDependenciesServlet - param nrecord_doc : " + params.get("nrecord_doc"));
				Logger.info("TreDiSignatureDependenciesServlet - param fileid_originale : " + params.get("fileid_originale"));
				Logger.info("TreDiSignatureDependenciesServlet - param tipo_riconsegna : " + params.get("tipo_riconsegna"));
				Logger.info("TreDiSignatureDependenciesServlet - param _cd : " + params.get("_cd"));
				
				// altri parametri necessari all'upload del file
				params.put("verbo", "librofirma");
				params.put("xverb", "addsignedfile");
				
				XMLDocumento res = new XMLDocumento(redirectRequestToService(params.get("login"), params, fileItem));
				Logger.info("TreDiSignatureDependenciesServlet - Response: " + res.asXML());
				
				String status = res.getDocument().selectSingleNode("/response/librofirma/@status").getText();
				if (status.equals("error")) {
					String msg = res.getDocument().selectSingleNode("/response/librofirma/msg").getText();
					Logger.error("TreDiSignatureDependenciesServlet - Ritorno docway4-service: " + msg);
					
					uploadResponse = "<!--ERROR: " + msg + "-->";
				}
				else {
					uploadResponse = "<!--SUCCESS-->";
				}
			}
			catch (Throwable t) {
				Logger.error("TreDiSignatureDependenciesServlet - Errore in upload del file", t);
				
				uploadResponse = "<!--ERROR-->";
			}
			
			try {
				fileItem.delete(); // cancellazione del fileitem
			}
			catch (Exception e) {
				Logger.error("Errore in cancellazione del file temporaneo utilizzato in upload", e);
			}
			
			response.getOutputStream().write(uploadResponse.getBytes());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}
	
	/**
	 * Redirect della richiesta di upload a DocWay4-Service
	 * 
	 * @param userLogin login dell'utente corrente
	 * @param params parametri di tipo stringa da includere nella request
	 * @param fileItem file da caricare
	 * 
	 * @return contenuto della response restituito dal service 
	 * @throws Exception
	 */
	private String redirectRequestToService(String userLogin, Map<String, String> params, FileItem fileItem) throws Exception {
		File file = null;
		
		String matricola = "";
		if (params.get("matricola") != null)
			matricola = params.get("matricola");
		
		try {
			AdapterConfig config = AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService");
			FormAdapter formAdapter = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
			
			//dpranteda - 25/09/2015 :  possibilità di forzare il nome del file, utilizzato per firma pdf in pdf
			String forcedFileName = params.get("forcedFileName");
			if(forcedFileName == null || forcedFileName.length() == 0)
				file = UploadFileUtil.createUserTempFile(fileItem, userLogin, matricola, true);
			else
				file = UploadFileUtil.createUserTempFile(fileItem, forcedFileName, userLogin, matricola, true);
			
			return formAdapter.executeUploadRequestToString(userLogin, "filename", file, params);
		}
		finally {
			if (file != null && file.exists()) {
				file.delete();
				
				UploadFileUtil.deleteTempUserFolder(userLogin, matricola); // cancellazione della directory dell'utente corrente
			}
		}
	}
	
	/**
	 * codifica del file per invio ad applet di firma (utilizzato nella verifica di eventuali nuovi file da scaricare
	 * da parte dell'applet)
	 * 
	 * @param file
	 * @return
	 */
	private String econdeFileName(File file) {
		byte[] hash = null;
		
		DataInputStream datais = null;
		try {
			datais = new DataInputStream(new FileInputStream(file));
			byte[] content = new byte[(int) file.length()];
			datais.readFully(content);
			hash = MessageDigest.getInstance("SHA").digest(content);
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}
		finally {
			try {
				if (datais != null)
					datais.close();
			}
			catch (Exception ex) {
				Logger.error(ex.getMessage(), ex);
			}
		}
		
		if (hash != null)
			return new String(Base64.encodeBase64(hash));
		else 
			return null;
	}
	
	private byte[] getBytes(File file) {
		byte[] bytes = null;
		
		if (file != null && file.exists()) {
			FileInputStream fis = null;
			ByteArrayOutputStream bos = null;
			try {
				fis = new FileInputStream(file);
				bos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				
				for (int readNum; (readNum = fis.read(buf)) != -1;) {
					bos.write(buf, 0, readNum); //no doubt here is 0
	            }
				
				bytes = bos.toByteArray();
	        } 
			catch (IOException ex) {
				Logger.error(ex.getMessage(), ex);
	        }
			finally {
				try {
					if (fis != null)
						fis.close();
					if (bos != null)
						bos.close();
				}
				catch (Exception ex) { }
			}
		}
        
		return bytes;
	}
	
}
