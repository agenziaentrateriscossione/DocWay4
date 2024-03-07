package it.tredi.dw4.docway.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.adapters.DocWayArchiveUploadFormsAdapter;
import it.tredi.dw4.docway.model.CompressedArchive;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.UploadFileUtil;
import it.tredi.dw4.utils.XMLDocumento;

/**
 * @author tiommi 
 */
public class DocWayUploadArchive extends Page {
	
	private boolean visible = false;
	private CompressedArchive archive = new CompressedArchive();
	private DocWayArchiveUploadFormsAdapter formsAdapter;
	private DocEditDoc docEditDoc;
	
	// report fields
	private boolean reportVisible = false;
	private int totFiles;
	private int uplFiles;
	private int errFiles;
	private List<FileErrorLog> fileErrorList = new ArrayList<FileErrorLog>();
	
	// classe di support per leggere errori 
	// (niente hashmap perchè potrebbero esistere 2 errori su file con lo stesso name)
	public class FileErrorLog {
		private String fileName;
		private String errorDetail;
		
		public FileErrorLog(String fileName, String errorDetail) {
			this.fileName = fileName;
			this.errorDetail = errorDetail;
		}
		
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getErrorDetail() {
			return errorDetail;
		}
		public void setErrorDetail(String errorDetail) {
			this.errorDetail = errorDetail;
		}
	}
	
	@Override
	public FormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}
	
	public DocWayUploadArchive() throws Exception {
		this.formsAdapter = new DocWayArchiveUploadFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public boolean isVisible() {
		return this.visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void close() {
		this.visible = false;
		this.archive = new CompressedArchive();
	}
	
	public CompressedArchive getArchive() {
		return this.archive;
	}
	
	public void setDocEditDoc(DocEditDoc docEditDoc) {
		this.docEditDoc = docEditDoc;
	}
	
	public boolean isReportVisible() {
		return reportVisible;
	}

	public void setReportVisible(boolean reportVisible) {
		this.reportVisible = reportVisible;
	}

	public int getTotFiles() {
		return totFiles;
	}

	public void setTotFiles(int totFiles) {
		this.totFiles = totFiles;
	}
	
	public int getUplFiles() {
		return uplFiles;
	}

	public void setUplFiles(int uplFiles) {
		this.uplFiles = uplFiles;
	}

	public int getErrFiles() {
		return errFiles;
	}

	public void setErrFiles(int errFiles) {
		this.errFiles = errFiles;
	}
	
	public List<FileErrorLog> getFileErrorList() {
		return this.fileErrorList;
	}

	public void setFileErrorMap(ArrayList<FileErrorLog> fileErrorList) {
		this.fileErrorList = fileErrorList;
	}

	/**
	 * Avvio della procedura di unzip e di upload dei singoli file dell'archivio salvato nei temporanei
	 *
	 * @throws Exception
	 */
	public String confirmUpload() throws Exception {
		try {
			if (archive.getTitle() == null || archive.getTitle().isEmpty()) {
				this.setErroreResponse(I18N.mrs("dw4.occorre_selezionare_il_file_da_importare"), Const.MSG_LEVEL_WARNING);
				return null;
			}

			UserBean userBean = getUserBean();
			try {
				File fileArchive = UploadFileUtil.getUserTempFile(archive.getTitle(), userBean.getLogin(), userBean.getMatricola());
				
				formsAdapter.unZipAndUploadArchive(archive);

				XMLDocumento response = this.formsAdapter.getDefaultForm().executeUploadRequestToXMLDocumento(userBean, "uploadFile", fileArchive);
				if (handleErrorResponse(response)) {
					close();
					return null;
				}
				handleReportAndAttachXwFiles(response);
				openReport();
			}
			finally {
				UploadFileUtil.deleteTempUserFolder(userBean.getLogin(), userBean.getMatricola());
			}
			close();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			close();
			return null;
		}
	}

	/**
	 * Gestione della response con il report della decompressione dell'archivio
	 * @param response
	 */
	private void handleReportAndAttachXwFiles(XMLDocumento response) {
		Element unzipReport = response.getRootElement().element("unzip_report");
		if (unzipReport != null) {
			List<?> fileNodes = unzipReport.selectNodes("unzipped_file");
			List<?> errorNodes = unzipReport.selectNodes("error_log");
			this.uplFiles = fileNodes.size();
			this.errFiles = errorNodes.size();
			this.totFiles = uplFiles + errFiles;
			
			// handle dei file uploadati
			for (Object fileNode : fileNodes) {
				Element fileEl = (Element) fileNode;
				String id = fileEl.attributeValue("id");
				String name = fileEl.attributeValue("name");
				String title = fileEl.attributeValue("title");
				String da_firmare = fileEl.attributeValue("da_firmare");
				XwFile xwfile = new XwFile(id, name, title);
				xwfile.setDa_firmare(Boolean.parseBoolean(da_firmare));
				// aggiungo in testa per evitare l'xwFile vuoto di inserimento
				this.docEditDoc.getDoc().getFiles().add(0, xwfile);
			}
			
			// handle degli errori
			for (Object errorNode : errorNodes) {
				Element errorEl = (Element) errorNode;
				String name = errorEl.attributeValue("name");
				String log = errorEl.attributeValue("log");
				this.fileErrorList.add(new FileErrorLog(name, log));
			}
		}
	}
	
	private void openReport() {
		this.reportVisible = true;
	}
	
	public void closeReport() {
		this.reportVisible = false;
		this.totFiles = 0;
		this.uplFiles = 0;
		this.errFiles = 0;
		this.fileErrorList = new ArrayList<FileErrorLog>();
	}
}
