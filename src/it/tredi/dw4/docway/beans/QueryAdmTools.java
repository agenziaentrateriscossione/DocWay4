package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.UploadFileUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.io.File;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class QueryAdmTools extends DocWayQuery {
	private String xml;
	
	private boolean administrationMode = false;
	private String tipodoc = "";
	private String print = "";
	private String tipoarc = "";
	private String tipoarc_custom = "";
	private String cod_sede_import = "";
	private boolean chkImpAttach = false;
	
	private String fileNameSegnatura = "";
	private String fileNameRepertorio = "";
	private String fileNameRegistroEmergenza = "";
	private String fileNameImportDocumenti = "";
	
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	public QueryAdmTools() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
		this.xml = dom.asXML();
		
		if (XMLUtil.parseStrictAttribute(dom, "/response/@administrationMode").equals("true"))
			this.administrationMode = true;
		else
			this.administrationMode = false;
		
		this.print = "txt";
		this.tipoarc = "centrale";
		
		this.cod_sede_import = XMLUtil.parseStrictAttribute(dom, "/response/@cod_sede");
    }	
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public boolean isAdministrationMode() {
		return administrationMode;
	}

	public void setAdministrationMode(boolean administrationMode) {
		this.administrationMode = administrationMode;
	}
	
	public String getTipodoc() {
		return tipodoc;
	}

	public void setTipodoc(String tipologiaDocumento) {
		this.tipodoc = tipologiaDocumento;
	}
	
	public String getPrint() {
		return print;
	}

	public void setPrint(String tipoStampa) {
		this.print = tipoStampa;
	}
	
	public String getTipoarc() {
		return tipoarc;
	}

	public void setTipoarc(String tipoarc) {
		this.tipoarc = tipoarc;
	}

	public String getTipoarc_custom() {
		return tipoarc_custom;
	}

	public void setTipoarc_custom(String tipoarc_custom) {
		this.tipoarc_custom = tipoarc_custom;
	}
	
	public String getCod_sede_import() {
		return cod_sede_import;
	}

	public void setCod_sede_import(String cod_sede_import) {
		this.cod_sede_import = cod_sede_import;
	}
	
	public boolean isChkImpAttach() {
		return chkImpAttach;
	}

	public void setChkImpAttach(boolean chkImpAttach) {
		this.chkImpAttach = chkImpAttach;
	}
	
	public String getFileNameSegnatura() {
		return fileNameSegnatura;
	}

	public void setFileNameSegnatura(String fileNameSegnatura) {
		this.fileNameSegnatura = fileNameSegnatura;
	}

	public String getFileNameRepertorio() {
		return fileNameRepertorio;
	}

	public void setFileNameRepertorio(String fileNameRepertorio) {
		this.fileNameRepertorio = fileNameRepertorio;
	}

	public String getFileNameRegistroEmergenza() {
		return fileNameRegistroEmergenza;
	}

	public void setFileNameRegistroEmergenza(String fileNameRegistroEmergenza) {
		this.fileNameRegistroEmergenza = fileNameRegistroEmergenza;
	}

	public String getFileNameImportDocumenti() {
		return fileNameImportDocumenti;
	}

	public void setFileNameImportDocumenti(String fileNameImportDocumenti) {
		this.fileNameImportDocumenti = fileNameImportDocumenti;
	}
	
	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	/**
	 * Cambio della modalita' di accesso agli strumenti di amministrazione (solo
	 * amministratori, tutti gli utenti)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String changeAdmMode() throws Exception {
		try {
			//setGlobalFormRestore('xverb');
			String oldXverb = formsAdapter.getDefaultForm().getParam("xverb");
			formsAdapter.changeAdmMode(); 
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			formsAdapter.getDefaultForm().addParam("xverb", oldXverb);
			this.init(response.getDocument());
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
 	}
	
	/**
	 * Attivazione/Disattivazione dell'invio delle mail di notifica
	 * 
	 * @return
	 * @throws Exception
	 */
	public String changeSendMailStatus() throws Exception {
		try {
			//setGlobalFormRestore('verbo', 'xverb', 'dbTable');
			String oldVerbo = formsAdapter.getDefaultForm().getParam("verbo");
			String oldXverb = formsAdapter.getDefaultForm().getParam("xverb");
			String oldDbTable = formsAdapter.getDefaultForm().getParam("dbTable");
			
			// Federico 20/10/07: fix: per abilitare/disabilitare le email occorre invocare 'changePropertyValue' [RW 0047701]
			String propName = "invioEmailNotifica";
			String propVal = "Si"; // valori possibili: Si/No
			if (formsAdapter.checkBooleanFunzionalitaDisponibile("invioEmailNotifica", false))
				propVal = "No";
			formsAdapter.changePropertyValue(propName, propVal);
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			formsAdapter.getDefaultForm().addParam("verbo", oldVerbo);
			formsAdapter.getDefaultForm().addParam("xverb", oldXverb);
			formsAdapter.getDefaultForm().addParam("dbTable", oldDbTable);
			this.init(response.getDocument());
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Apertura in editing del titolario di classificazione
	 * @return
	 * @throws Exception
	 */
	public String openTitolario() throws Exception {
		try {
			formsAdapter.getIndexForm().addParam("verbo", "thEdit");
			
			XMLDocumento response = this.formsAdapter.getIndexForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			DocWayThEdit docwayThEdit = new DocWayThEdit();
			docwayThEdit.getFormsAdapter().fillFormsFromResponse(response);
			docwayThEdit.init(response.getDocument());
			//docwayThEdit.setPopupPage(true);
			docwayThEdit.setActive(true);
			setSessionAttribute("docwayThEdit", docwayThEdit);
			
			//return "thEdit";
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Trasferimento (fascicoli, documenti)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQTrasferimento() throws Exception {
		try {
			formsAdapter.gotoTableQ("trasferimento", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			QueryTrasferimento queryTrasferimento = new QueryTrasferimento();
			queryTrasferimento.getFormsAdapter().fillFormsFromResponse(response);
			queryTrasferimento.init(response.getDocument());
			setSessionAttribute("queryTrasferimento", queryTrasferimento);
			
			return "query@trasferimento";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Caricamento della pagina di strumenti di amministrazione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQAdmTools() throws Exception {
		try {
			formsAdapter.gotoTableQ("adm_tools", false);
			XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		
			QueryAdmTools queryAdmTools = new QueryAdmTools();
			queryAdmTools.getFormsAdapter().fillFormsFromResponse(responseDoc);
			queryAdmTools.init(responseDoc.getDocument());
			setSessionAttribute("queryAdmTools", queryAdmTools);
			
			return "query@adm_tools";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Rigenera relazioni tra i fascicoli
	 * 
	 * @return
	 * @throws Exception
	 */
	public String generaRelazioniFascicoli() throws Exception {
		try {
			formsAdapter.generaRelazioniFascicoli();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) { // caricamento della loadingbar
				
				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Controllo sul tipo di archivio selezionato per l'upload/download 
	 * file per la segnatura
	 * @param vce
	 */
	public void tipoarcValueChange(ValueChangeEvent vce) throws Exception {  
        this.tipoarc = (String) vce.getNewValue();
    }
	
	/**
	 * Download del file di segnatura
	 * 
	 * @return
	 * @throws Exception
	 */
	public String downloadFileSegnatura() throws Exception {
		try {
			if (tipodoc == null || tipodoc.equals("")) {
				this.setErroreResponse(I18N.mrs("dw4.occorre_selezionare_una_tipologia_di_documento"), Const.MSG_LEVEL_WARNING);
				return null;
			}
			
			formsAdapter.downloadFileSegnatura(tipodoc, print, tipoarc, tipoarc_custom);
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			return getResponseAttach(attachFile);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Upload del file di segnatura
	 * 
	 * @return
	 * @throws Exception
	 */
	public String uploadFileSegnatura() throws Exception {
		try {
			if (tipodoc == null || tipodoc.equals("")) {
				this.setErroreResponse(I18N.mrs("dw4.occorre_selezionare_una_tipologia_di_documento"), Const.MSG_LEVEL_WARNING);
				return null;
			}
			if (fileNameSegnatura == null || fileNameSegnatura.equals("")) {
				this.setErroreResponse(I18N.mrs("dw4.occorre_selezionare_il_file_da_importare"), Const.MSG_LEVEL_WARNING);
				return null;
			}
			
			UserBean userBean = getUserBean();
			try {
				File fileSegnatura = UploadFileUtil.getUserTempFile(fileNameSegnatura, userBean.getLogin(), userBean.getMatricola());
							
				formsAdapter.uploadFileSegnatura(tipodoc, print, tipoarc, tipoarc_custom);
				
				XMLDocumento response = getFormsAdapter().getDefaultForm().executeUploadRequestToXMLDocumento(userBean, "uploadFile", fileSegnatura);
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
			}
			finally {
				UploadFileUtil.deleteTempUserFolder(userBean.getLogin(), userBean.getMatricola());
			}
			
			this.fileNameSegnatura = "";
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Download del file di descrizione dei repertori
	 * 
	 * @return
	 * @throws Exception
	 */
	public String downloadFileDescrizioneRepertori() throws Exception {
		try {
			formsAdapter.downloadSpecificFile("repLocation");
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			return getResponseAttach(attachFile);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Upload del file di descrizione dei repertori
	 * 
	 * @return
	 * @throws Exception
	 */
	public String uploadFileDescrizioneRepertori() throws Exception {
		try {
			if (fileNameRepertorio == null || fileNameRepertorio.equals("")) {
				this.setErroreResponse(I18N.mrs("dw4.occorre_selezionare_il_file_da_importare"), Const.MSG_LEVEL_WARNING);
				return null;
			}
			
			UserBean userBean = getUserBean();
			try {
				File fileRepertorio = UploadFileUtil.getUserTempFile(fileNameRepertorio, userBean.getLogin(), userBean.getMatricola());
			
				formsAdapter.uploadSpecificFile("repLocation");
				
				XMLDocumento response = getFormsAdapter().getDefaultForm().executeUploadRequestToXMLDocumento(userBean, "uploadFile", fileRepertorio);
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
			}
			finally {
				UploadFileUtil.deleteTempUserFolder(userBean.getLogin(), userBean.getMatricola());
			}
			
			this.fileNameRepertorio = "";
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Importazione del registro di emergenza
	 * 
	 * @return
	 * @throws Exception
	 */
	public String importRegistroEmergenza() throws Exception {
		try {
			if (fileNameRegistroEmergenza == null || fileNameRegistroEmergenza.equals("")) {
				this.setErroreResponse(I18N.mrs("dw4.occorre_selezionare_il_file_da_importare"), Const.MSG_LEVEL_WARNING);
				return null;
			}
			if (cod_sede_import == null || cod_sede_import.equals("")) {
				this.setErroreResponse(I18N.mrs("dw4.occorre_valorizzare_il_codice_amministrazione_e_codice_area_organizzativa_omogenea"), Const.MSG_LEVEL_WARNING);
				return null;
			}
			else if (cod_sede_import.length() != 7) {
				this.setErroreResponse(I18N.mrs("dw4.il_codice_amministrazione_e_codice_area_organizzativa_omogenea_deve_essere_di_almeno_7_caratteri"), Const.MSG_LEVEL_WARNING);
				return null;
			}
			
			UserBean userBean = getUserBean();
			try {
				File fileRegistroEmergenza = UploadFileUtil.getUserTempFile(fileNameRegistroEmergenza, userBean.getLogin(), userBean.getMatricola());
			
				formsAdapter.importRegistroEmergenza(cod_sede_import);
				
				XMLDocumento response = getFormsAdapter().getDefaultForm().executeUploadRequestToXMLDocumento(userBean, "importRE", fileRegistroEmergenza);
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				this.fileNameRegistroEmergenza = "";
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				
				String verbo = response.getAttributeValue("/response/@verbo");
				if (verbo.equals("loadingbar")) { // caricamento della loadingbar
					
					DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
					docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
					docWayLoadingbar.init(response);
					setLoadingbar(docWayLoadingbar);
					docWayLoadingbar.setActive(true);
				}
				
			}
			finally {
				UploadFileUtil.deleteTempUserFolder(userBean.getLogin(), userBean.getMatricola());
			}

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Caricamento dei documenti da smistare (dopo upload da registro
	 * di emergenza)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String regDocsDaSmistare() throws Exception {
		try {
			formsAdapter.regDocsDaSmistare();
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return navigateResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Importazione di documenti da strumenti di amministrazione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String importDocs() throws Exception {
		try {
			if (fileNameImportDocumenti == null || fileNameImportDocumenti.equals("")) {
				this.setErroreResponse(I18N.mrs("dw4.occorre_selezionare_il_file_da_importare"), Const.MSG_LEVEL_WARNING);
				return null;
			}
			
			UserBean userBean = getUserBean();
			try {
				File fileImportDocumenti = UploadFileUtil.getUserTempFile(fileNameImportDocumenti, userBean.getLogin(), userBean.getMatricola()); 
			
				formsAdapter.importDocs(chkImpAttach);
				
				XMLDocumento response = getFormsAdapter().getDefaultForm().executeUploadRequestToXMLDocumento(userBean, "importFile", fileImportDocumenti);
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				this.fileNameImportDocumenti = "";
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				
				String verbo = response.getAttributeValue("/response/@verbo");
				if (verbo.equals("loadingbar")) { // caricamento della loadingbar
					
					DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
					docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
					docWayLoadingbar.init(response);
					setLoadingbar(docWayLoadingbar);
					docWayLoadingbar.setActive(true);
				}
				
			}
			finally {
				UploadFileUtil.deleteTempUserFolder(userBean.getLogin(), userBean.getMatricola());
			}

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
