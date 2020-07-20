package it.tredi.dw4.servlet;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.UploadFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet di appoggio per l'upload di file tramite IWX o swfUpload. In questo modo non viene esposto 
 * direttamente il service
 *  
 * @author mbernardini
 */
@SuppressWarnings("serial")
public class FileUploadServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			boolean docwayServiceUpload = true; // indica se si tratta di un upload su service o meno
			
			HashMap<String, String> params = new HashMap<String, String>();
			FileItem fileItem = null;
			
			FileItemFactory fileItemFactory = new DiskFileItemFactory();
			ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
			
			List items = uploadHandler.parseRequest(request);
			Iterator itr = items.iterator();
			while (itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				/*
				 * Handle Form Fields.
				 */
				if (item.isFormField()) {
					params.put(item.getFieldName(), item.getString()); // altri parametri della request (non file)
					Logger.info("FileUploadServlet - Param: " + item.getFieldName() + " = " + item.getString());
				} else {
					// upload di file
					fileItem = item;
					Logger.info("FileUploadServlet - Param File: Name = " + item.getFieldName() + ", File Name = " + item.getName() + ", Content type = " + item.getContentType() + ", File Size = " + item.getSize());
				}
			}
			
			// verifico se si tratta di un upload di file temporaneo tramite
			// i parametri inviati nella request
			if (params.containsKey("tempUpload") && params.get("tempUpload").equals("true"))
				docwayServiceUpload = false;
			
			String uploadResponse = "";
			
			if (docwayServiceUpload) {
				// upload su docway-service
				Logger.info("FileUploadServlet - docway4-service upload");
				
				// altri parametri necessari all'upload del file (o altre richieste)
				params.put("db", params.get("db"));
				String verbo = params.get("verbo");
				if (verbo == null || verbo.equals("")) {
					
					// richiesta di upload files tramite iwx
					params.put("verbo", "Iw_response");
					params.put("xverb", "upload");
					
				}
				else {
					
					// richiesta di una specifica azione derivante da caricamento di file (es. acquisizione massiva di documenti)
					params.put("verbo", verbo);
					String xverb = params.get("xverb");
					if (xverb == null)
						xverb = "";
					params.put("xverb", xverb);
					
				}
				
				uploadResponse = redirectRequestToService(params.get("login"), params.get("matricola"), params, fileItem); // es. ritorno: <--remotefile:11.tiff;-->
				Logger.info("FileUploadServlet - Response: " + uploadResponse);
			}
			else {
				// upload di file temporaneo
				Logger.info("FileUploadServlet - temp file upload");
				
				// salvataggio del nuovo file uploadato in dir temporanea
				boolean deleteTempDir = true;
				if (params.get("keepUserDir") != null && params.get("keepUserDir").equals("true"))
					deleteTempDir = false;
				File fileTemp = UploadFileUtil.createUserTempFile(fileItem, params.get("login"), params.get("matricola"), deleteTempDir);
				uploadResponse = "<--tempfile:" + fileTemp.getName() + ";-->";
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
		catch (Throwable t) {
			Logger.error("Errore in upload del file", t);
			
			response.getOutputStream().write(new String("<--ERROR-->").getBytes());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}

	/**
	 * Redirect della richiesta di upload a DocWay4-Service
	 * 
	 * @param userLogin login dell'utente corrente
	 * @param userMatricola matricola dell'utente corrente
	 * @param params parametri di tipo stringa da includere nella request
	 * @param fileItem file da caricare
	 * 
	 * @return contenuto della response restituito dal service 
	 * @throws Exception
	 */
	private String redirectRequestToService(String userLogin, String userMatricola, Map<String, String> params, FileItem fileItem) throws Exception {
		File file = null;
		try {
			AdapterConfig config = AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService");
			FormAdapter formAdapter = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
			
			file = UploadFileUtil.createUserTempFile(fileItem, userLogin, userMatricola, true);
			
			return formAdapter.executeUploadRequestToString(userLogin, "filename", file, params);
		}
		finally {
			if (file != null && file.exists()) {
				file.delete();
				
				UploadFileUtil.deleteTempUserFolder(userLogin, userMatricola); // cancellazione della directory dell'utente corrente
			}
		}
	}
	
}
