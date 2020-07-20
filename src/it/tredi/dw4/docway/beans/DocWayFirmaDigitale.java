package it.tredi.dw4.docway.beans;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocumentFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.model.ConfigFirmaDigitale;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;

public class DocWayFirmaDigitale extends Showdoc {
	
	private DocumentFormsAdapter formsAdapter;
	private String xml = "";
	
	private ConfigFirmaDigitale configFirmaDigitale = new ConfigFirmaDigitale();
	private boolean visible = false; // da utlizzare in caso di popup dialog (ATTUALMENTE NON UTILIZZATO PER PROBLEMI CON CARICAMENTO APPLET)
	
	private String nrecord_doc = "";
	private XwFile fileDaFirmare = null;
	private List<XwFile> files = new ArrayList<XwFile>(); // contiene il file selezionato per la firma ed eventuali conversioni del file (es. der_to pdf)
	private boolean forceAppend = false;
	private String downlaodFileUrl = "";
	private String fileTitleWithExtension = "";
	
	private String outputFileType = "";
	
    public DocWayFirmaDigitale(List<XwFile> files, boolean forceAppend) throws Exception {
    	this.formsAdapter 	= new DocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
    	
    	this.files 			= files;
    	this.forceAppend 	= forceAppend;
    	
    	this.setOutputFileType("p7m"); //valore di default
	}
    
	public void init(Document dom) {
		xml 				= dom.asXML();
		nrecord_doc 		= XMLUtil.parseStrictAttribute(dom, "/response/doc/@nrecord");
		
		configFirmaDigitale.init(XMLUtil.createDocument(dom, "/response/configFirmaDigitale"));
		
		if (!configFirmaDigitale.isAbilitaFirmaSuDerTo()) // se la firma su der to non e' abilitata si avvia il processo di firma direttamente sul file selezionato dall'utente
			setFileDaFirmare(files.get(0));
		else if (files.size() == 1) { // se non ci sono file der to si forza la firma sul file originale
			setFileDaFirmare(files.get(0));
		}
		
		// dpranteda - 25/09/2015 : possibilita' di firmare in pdf
		if(this.fileDaFirmare == null) { // se ci sono piu' file controllo che la firma su pdf venga effettivamente lanciata su file con estensione pdf
			List<Integer> index = new ArrayList<Integer>();
			if(this.outputFileType.equalsIgnoreCase("pdf")){
				for(int i=0; i<files.size();i++) {
					if(files.get(i).getExtension().equalsIgnoreCase("pdf"))
						index.add(new Integer(i));
				}
				
				if(index.size() == 1) { // se c'e' un solo pdf ok, altrimenti mostrero' la lista
					setFileDaFirmare(files.get(index.get(0)));
				}
			}
		}
	}
	
	public DocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ConfigFirmaDigitale getConfigFirmaDigitale() {
		return configFirmaDigitale;
	}

	public void setConfigFirmaDigitale(ConfigFirmaDigitale configFirmaDigitale) {
		this.configFirmaDigitale = configFirmaDigitale;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public String getNrecord_doc() {
		return nrecord_doc;
	}

	public void setNrecord_doc(String nrecord_doc) {
		this.nrecord_doc = nrecord_doc;
	}
	
	public List<XwFile> getFiles() {
		return files;
	}

	public void setFiles(List<XwFile> files) {
		this.files = files;
	}
	
	public XwFile getFileDaFirmare() {
		return fileDaFirmare;
	}

	private void setFileDaFirmare(XwFile file) {
		this.fileDaFirmare = file;
		
		if (this.fileDaFirmare != null) {
			// viene verificato che il titolo effettivamente contenga
			// l'estensione del file
			fileTitleWithExtension = file.getTitle();
			int index = file.getTitle().lastIndexOf(".");
			if (index == -1 || !file.getTitle().substring(index+1).equals(file.getExtension()))
				fileTitleWithExtension = file.getTitle().substring(0, index) + "." + file.getExtension();
			
			buildDownloadFileUrl();
		}
	}
	
	public String getFileTitleWithExtension() {
		return fileTitleWithExtension;
	}
	
	public String getDownlaodFileUrl() {
		return downlaodFileUrl;
	}
	
	public String getEncodedDownloadFileUrl() {
		try {
			return URLEncoder.encode(downlaodFileUrl, "UTF-8");
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
			
			return downlaodFileUrl;
		}
	}

	public void setDownlaodFileUrl(String downlaodFileUrl) {
		this.downlaodFileUrl = downlaodFileUrl;
	}

	/**
	 * ritorna i parametri da passare al postURL dell'applet per il salvataggio.
	 * formato: name1=value1[&name2=value2[...]]
	 * 
	 * @return
	 */
	public String getPostParams() {
		StringBuffer sbParams = new StringBuffer();
		
		try {
			UserBean userBean = getUserBean();
			
			sbParams.append("nrecord_doc=");
			sbParams.append(nrecord_doc);
			sbParams.append("&file_name=");
			sbParams.append(URLEncoder.encode(fileTitleWithExtension, "UTF-8"));
			
			// dpranteda - 25/09/2015 :  se firmo in pdf il file originale (pdf), forzo la rinomina del file firmato
			if (this.outputFileType.equalsIgnoreCase("pdf") && files.get(0).getExtension().equalsIgnoreCase("pdf")){
				sbParams.append("&forcedFileName=");
				sbParams.append(URLEncoder.encode(fileTitleWithExtension.substring(0,fileTitleWithExtension.lastIndexOf(".")) + "_" + I18N.mrs("dw4.firmato") + "." + fileDaFirmare.getExtension(), "UTF-8"));
			}
			
			sbParams.append("&fileid_originale=");
			sbParams.append(URLEncoder.encode(configFirmaDigitale.getFileId(), "UTF-8"));
			sbParams.append("&login=");
			sbParams.append(URLEncoder.encode(userBean.getLogin(), "UTF-8"));
			sbParams.append("&matricola=");
			sbParams.append(URLEncoder.encode(userBean.getMatricola(), "UTF-8"));
			if (configFirmaDigitale.getParams().get("sign_type") != null) {
				sbParams.append("&signatureType=");
				sbParams.append(configFirmaDigitale.getParams().get("sign_type"));
			}
			sbParams.append("&tipo_riconsegna=");
			if (!forceAppend) {
				if(outputFileType.equalsIgnoreCase("pdf")){ //dpranteda - 28/09/2015 : possibilita' di gestire il salvataggio dei pdf firmati
					sbParams.append(configFirmaDigitale.getTipoRiconsegnaPDF());
				}else{
					sbParams.append(configFirmaDigitale.getTipoRiconsegna());
				}
			}
			else
				sbParams.append("APPEND"); // in caso di firma su immagine viene forzato l'append
			
			// TODO: gestire JSESSIONID!
			// parametro securityKey aggiunto per dare una parvenza di sicurezza, altrimenti verrebbero 
			// passati all'applet tutti i parametri in chiaro e farremmo una pessima figura. 
			// Questa gestione deve comunque essere migliorata, almeno per quanto riguarda il passaggio dei dati 
			// dell'utente (login e matricola)
			sbParams.append("&securityKey=");
			String key = nrecord_doc + "_" + configFirmaDigitale.getFileId() + "_" + userBean.getLogin() + "_" + userBean.getMatricola() + "_3DI";
			key = DigestUtils.md5Hex(key);
			sbParams.append(URLEncoder.encode(key, "UTF-8"));
			
			// aalberghini 20150624: aggiunto db ai parametri passati
			sbParams.append("&db=");
			sbParams.append(formsAdapter.getDefaultForm().getParam("db"));
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		
		return sbParams.toString();
	}
	
	/**
	 * ritorna l'url di post per il salvataggio del file firmato, comprensivo
	 * di tutti i parametri necessari al salvataggio
	 * @return
	 */
	public String getPostUrlWithParams() {
		StringBuffer sbUrl = new StringBuffer();
		
		if (configFirmaDigitale.getPostUrl() != null && configFirmaDigitale.getPostUrl().length() > 0) {
			try {
				sbUrl.append(configFirmaDigitale.getPostUrl());
				if (!configFirmaDigitale.getPostUrl().contains("?"))
					sbUrl.append("?");
				else
					sbUrl.append("&");
				
				sbUrl.append(getPostParams());
				
				sbUrl.append("&_cd=");
				sbUrl.append(URLEncoder.encode(formsAdapter.getDefaultForm().getParam("_cd"), "UTF-8")); // parametro necessario alla gestione del multisocieta'
			}
			catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}
		
		return sbUrl.toString();
	}
	
	/**
	 * costruzione dell'url di download del file (chiamata da applet di firma digitale)
	 * @return
	 */
	private void buildDownloadFileUrl() {
		try {
			UserBean userBean = getUserBean();
			
			downlaodFileUrl = configFirmaDigitale.getBaseUrl()
								+ "filedownload?name="
								+ URLEncoder.encode(fileDaFirmare.getName(), "UTF-8")
								+ "&title="
								+ URLEncoder.encode(fileTitleWithExtension, "UTF-8")
								+ "&login="
								+ userBean.getLogin()
								+ "&matricola="
								+ userBean.getMatricola()
								+ "&_cd="
								+ URLEncoder.encode(formsAdapter.getDefaultForm().getParam("_cd"), "UTF-8") // parametro necessario alla gestione del multisocieta'
								+ "&db="
								+ formsAdapter.getDefaultForm().getParam("db")
								;
		}
		catch (Throwable t) {
			Logger.error(t.getMessage(), t);
		}
	}
	
	/**
	 * Chiusura del popup dialog
	 * @return
	 * @throws Exception
	 */
	public String close() throws Exception {
		visible = false;
		
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute("showdocFirmaDigitale", null);
		return null;
	}

	@Override
	public String paginaTitoli() throws Exception {
		return null;
	}

	@Override
	public void reload() throws Exception {
	}

	@Override
	public String modifyTableDoc() throws Exception {
		return null;
	}
	
	/**
	 * selezione del file da firmare (in caso di attivazione della firma sui der to di un file
	 * @return
	 * @throws Exception
	 */
	public String selezionaFileDaFirmare() throws Exception {
		try {
			XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("file");
			if (file != null) {
				int num = (getFiles().contains(file)) ? getFiles().indexOf(file): 0;
				if (num > 0) // e' stato selezionato un der to per la firma digitale, quindi si forza l'append del file
					forceAppend = true;
				
				setFileDaFirmare(file);
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
		}
		
		return null;
	}
	
	public String getOutputFileType() {
		return outputFileType;
	}

	public void setOutputFileType(String outputFileType) {
		this.outputFileType = outputFileType;
	}
	
}
