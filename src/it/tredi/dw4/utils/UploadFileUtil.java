package it.tredi.dw4.utils;

import it.tredi.dw4.adapters.FormAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import org.apache.commons.fileupload.FileItem;
import org.mozilla.universalchardet.UniversalDetector;

public class UploadFileUtil {
	
	private static String tempDir = System.getProperty("java.io.tmpdir"); // directory temp di Tomcat

	/**
	 * Dato il file uploadato crea un file "temporaneo" per l'utente nella directory
	 * temp di tomcat
	 * 
	 * @param fileItem file da caricare
	 * @return
	 */
	public static File createTempFile(FileItem fileItem) {
		return createUserTempFile(fileItem , "", "", true);
	}
	
	/**
	 * Dato il file uploadato crea un file "temporaneo" per l'utente nella directory
	 * temp di tomcat
	 * 
	 * @param fileItem file da caricare
	 * @param login login dell'utente corrente
	 * @param matricola matricola dell'utente corrente
	 * @param deleteTempDir cancellazione della directory dell'utente?
	 * @return
	 */
	public static File createUserTempFile(FileItem fileItem, String login, String matricola, boolean deleteTempDir) {
		return createUserTempFile(fileItem, fileItem.getName(), login, matricola, deleteTempDir);
	}
	
	/**
	 * Dato il file uploadato crea un file "temporaneo" per l'utente nella directory
	 * temp di tomcat
	 * 
	 * @param fileItem file da caricare
	 * @param fileName nome da assegnare al file da caricare
	 * @param login login dell'utente corrente
	 * @param matricola matricola dell'utente corrente
	 * @param deleteTempDir cancellazione della directory dell'utente?
	 * @return
	 */
	public static File createUserTempFile(FileItem fileItem, String fileName, String login, String matricola, boolean deleteTempDir) {
		File file = null;
		try {
			// recupero del file da caricare
			FileOutputStream fos = null;
			try {
				String nomeFile = fileItem.getName();
				
				// mbernardini 07/05/2018 : modifica a procedura di upload per correzione valori ricevuti da test di carico di equitalia
				//int indexName = nomeFile.lastIndexOf("\\");
				//if (indexName != -1)
				//	nomeFile = nomeFile.substring(indexName+1);
				
				// commentato perche' venivano gestiti in maniera incorretta i file con doppia estensione (es. .pdf.p7m, .tar.gz, ecc.)
				/*String extFile = "";
				int indexName = nomeFile.lastIndexOf("/");
				if (indexName != -1)
					nomeFile = nomeFile.substring(indexName+1);
				int indexExt = nomeFile.lastIndexOf(".");
				if (indexExt != -1) {
					extFile = "." + nomeFile.substring(indexExt+1);
					nomeFile = nomeFile.substring(0, indexExt);
				}*/
				
				if (login != null && !login.equals("")) {
					// file caricato all'interno della directory specifica dell'utente
					if (matricola == null)
						matricola = "";
					
					File folder = new File(tempDir, login + "_" + matricola);
					if (deleteTempDir)
						UploadFileUtil.deleteFolder(folder); // cancellazione di eventuali residui precedenti (es. file di precedenti uplaod)
					
					if (!folder.exists())
						folder.mkdir();
					
					file = new File(folder, nomeFile);
					
					Logger.info("UploadFileUtil - tempDir: " + folder.getAbsolutePath());
					Logger.info("UploadFileUtil - tempFile: " + file.getName());
				}
				else {
					/*
					if (nomeFile.length() < 3)
						nomeFile = nomeFile+"000"; // se il nome file e' lungo 2 caratteri createTempFile() restituisce: java.lang.IllegalArgumentException: Prefix string too short
					*/
					
					if (nomeFile.length() > 200)
						nomeFile = nomeFile.substring(50); // se il nome file e' piu' lungo di 200 caratteri taglio l'inizio in modo da poter appendere l'UUID senza superare il limite massimo di caratteri
					
					// file temporaneo direttamente all'interno della tempDir
					file = new File(new File(tempDir), UUID.randomUUID().toString() + "_" + nomeFile);
					//file = File.createTempFile(nomeFile, extFile, new File(tempDir));
					
					Logger.info("UploadFileUtil - tempDir: " + tempDir);
					Logger.info("UploadFileUtil - tempFile: " + file.getName());
				}
				
				fos = new FileOutputStream(file);
				fos.write(fileItem.get());
			}
			finally {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
			}
		}
		catch (Throwable t) {
			Logger.error(t.getMessage(), t);
		}
		
		return file;
	}
	
	/**
	 * Ritorna un file "temporaneo" caricato nella directory
	 * temp di tomcat
	 * 
	 * @param fileName nome del file da caricare
	 * @return
	 */
	public static File getTempFile(String fileName) {
		return getUserTempFile(fileName, "", "");
	}
	
	/**
	 * Ritorna un file "temporaneo" caricato nella directory specifica
	 * dell'utente all'interno della directory temp di tomcat
	 * 
	 * @param fileName nome del file da caricare
	 * @param login login dell'utente corrente
	 * @param matricola matricola dell'utente corrente
	 * @return
	 */
	public static File getUserTempFile(String fileName, String login, String matricola) {
		if (login != null && !login.equals("")) {
			if (matricola == null)
				matricola = "";
			
			File folder = new File(tempDir, login + "_" + matricola);
			return new File(folder, fileName);
		}
		else {
			return new File(tempDir, fileName);
		}
	}
	
	/**
	 * Cancellazione della directory di upload tempoanea dell'utente
	 * 
	 * @param login login dell'utente corrente
	 * @param matricola matricola dell'utente corrente
	 */
	public static void deleteTempUserFolder(String login, String matricola) {
		if (login != null && !login.equals("")) {
			if (matricola == null)
				matricola = "";
			
			File folder = new File(tempDir, login + "_" + matricola);
			
			Logger.info("UploadFileUtil - deleteTempUserFolder: " + folder.getAbsolutePath());
			deleteFolder(folder);
		}
	}
	
	/**
	 * Cancellazione di una directory temporanea (e del suo contenuto)
	 * 
	 * @param dir directory da cancellare
	 * @param
	 * @return
	 */
	private static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if(files!=null) {
			for(File f: files) {
				if(f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}
	
	/**
	 * riconoscimento dell'encoding dato un array di bytes
	 * @param bytes
	 * @return
	 */
	public static String detectCharset(byte[] bytes) {
		String defaultencoding = FormAdapter.ENCODING_UFT_8;
		try {
			UniversalDetector detector = new UniversalDetector(null);
			
			detector.handleData(bytes, 0, bytes.length);
			
			detector.dataEnd();
			
			String encoding = detector.getDetectedCharset();
			Logger.info("UploadFileUtil.detectCharset(): detected charset : " + encoding);
			
			detector.reset();
			
			if (encoding == null) {
				encoding = defaultencoding;
			}
			return encoding;
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return defaultencoding;
		}
	}
	
}
