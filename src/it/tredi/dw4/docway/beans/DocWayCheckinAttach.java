package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.beans.DocEdit;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.docway.adapters.DocWayCheckinFormsAdapter;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;

/**
 * Bean di checkin di un allegato di un documento
 * 
 * @author mbernardini
 */
public class DocWayCheckinAttach  extends DocEdit {
	private DocWayCheckinFormsAdapter formsAdapter;
	private boolean visible = false; // TODO da eliminare se gestito con popup tradizione
	private Showdoc showdoc;
	private String dbTable = "";
	
	private String attach = "";
	private XwFile xwFile = new XwFile();
	
	// utilizzati per il checkin di file tramite iwx o upload classico
	protected String xwFileNamesAttached = "";
	protected String xwFileTitlesAttached = "";
	protected String xwFileIdsAttached = "";
	
	private boolean firmaDigitale = false;
	private boolean agent_pdf = false;
	private boolean agent_xml = false;
	
	private boolean signingEnabled = false; // TODO firma digitale
	private boolean enableIW = false;
	
	private String allowedChkinExtensions = ""; // eventuale elenco di estensioni separati da virgola (es. pdf,rtf)
	
	private String xml;
	
	public DocWayCheckinAttach() throws Exception {
		this.formsAdapter = new DocWayCheckinFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
    	xml 		= dom.asXML();
    	visible 	= true;
    	
    	dbTable 	= XMLUtil.parseStrictAttribute(dom, "/response/@dbTable", "");
    	if (dbTable.equals("@reload"))
    		dbTable 	= XMLUtil.parseStrictAttribute(dom, "/response/@oldTable", "");
    	
    	attach		= XMLUtil.parseStrictAttribute(dom, "/response/@attach", "");
    	
    	xwFile.setName(attach);
    	
    	//xwFile.setTitle(XMLUtil.parseStrictAttribute(dom, "/response/@name", ""));
    	xwFile.setTitle(""); // TODO il titolo viene forzato a vuoto in modo da lasciare la gestione all'utente. se lasciato vuoto assumera' il nome del nuovo file caricato
    	
    	enableIW 	= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@enableIW"));
    	
    	if (!dbTable.equals("") && dbTable.startsWith("@"))
    		dbTable = dbTable.substring(1);
    	
    	allowedChkinExtensions	= XMLUtil.parseStrictAttribute(dom, "/response/@allowedChkinExtensions", ""); // TODO da completare
    	
    	// fcappelli 20140729 - aggiunta gestione in iwx e sqf uploader dei tipi di file permessi
		String iwxFileFilter = XMLUtil.parseStrictAttribute(dom, "/response/funzionalita_disponibili/@iwxFileFilter");
		fileFilters = parseUploaderFileFilter(iwxFileFilter);
		String iwxImageFilter = XMLUtil.parseStrictAttribute(dom, "/response/funzionalita_disponibili/@iwxImageFilter");
		imageFilters = parseUploaderFileFilter(iwxImageFilter);
		
		String codiceRepertorio = XMLUtil.parseStrictAttribute(dom, "/response/@codice_rep");
		String dbTable = XMLUtil.parseStrictAttribute(dom, "/response/@dbTable");
		
		if (codiceRepertorio != null && codiceRepertorio.length() > 0)
			setUploadFilters(codiceRepertorio);
		else if (dbTable != null && dbTable.length() > 0) 
			setUploadFilters(dbTable);
		else {
			docInformaticiFileDescription = Page.docInformaticiFileDescriptionDefault;
			docInformaticiFileTypes = Page.docInformaticiFileTypesDefault;      
			iwxFileTypes = Page.iwxFileTypesDefault;
			docInformaticiImageDescription = Page.docInformaticiImageTypesDescriptionDefault;
			docInformaticiImageTypes = Page.docInformaticiImageTypesDefault;           
			iwxImageTypes = Page.iwxImageTypesDefault;
		}
    	
    	// TODO gestione da workflow - coming_from
    	// TODO gestione firma digitale
    }	
	
	public DocWayCheckinFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		return commitChkin();			
	}

	@Override
	public String clearDocument() throws Exception {
		visible = false;
		setSessionAttribute("docwayCheckinAttach", null);
		
		if (showdoc != null) 
			showdoc.reload(); // si evita in caso di F5 il reload del popup // TODO necessario solo se non popup classico
		
		return null;
	}
	
	/**
	 * Commit effettivo del checkin del file
	 */
	public String commitChkin() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// aggiunta di mbussetti del 21/03/2005: possibilita' di richiedere conversioni su una nuova versione
			String convReq = "";
			if (agent_pdf)
				convReq += "pdf;";
			if (agent_xml)
				convReq += "xml;";
			if (convReq.length() > 0)
				convReq = convReq.substring(0, convReq.length()-1);
			
			formsAdapter.commitChkin(attach, xwFile.getXwayId(), xwFile.getTitle(), convReq);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			visible = false;
			
			if (showdoc != null) 
				showdoc.reload();
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = false;
		
		if (xwFile.getXwayId() == null || xwFile.getXwayId().equals("")) {
			this.setErrorMessage("", I18N.mrs("dw4.occorre_selezionare_un_file_prima_di_procedere_con_l_operazione"));
			result = true;
		}
		
		if (xwFile.getTitle() == null || xwFile.getTitle().equals("")) {
			this.setErrorMessage("templateForm:chkinFileTitle", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.nuova_descrizione") + "'");
			result = true;
		}
				
		return result;
	}
	
	/**
	 * Aggiornamento del file (dopo upload e caricamento
	 * su xway)
	 * 
	 * @return
	 */
	public String addFiles() {
		if (xwFileIdsAttached != null && !xwFileIdsAttached.equals("") 
				&& xwFileNamesAttached != null && !xwFileNamesAttached.equals("")
						&& xwFileTitlesAttached != null && !xwFileTitlesAttached.equals("")) {
			String fileId = xwFileIdsAttached.substring(1); // elimino il pipe iniziale (|)
			String fileName = xwFileNamesAttached.substring(1); // elimino il pipe iniziale (|)
			String fileTitle = xwFileTitlesAttached.substring(1); // elimino il pipe iniziale (|)
			
			if (!fileId.equals("") && !fileName.equals("") && !fileTitle.equals("")) {
				xwFile.setXwayId(fileId);
				xwFile.setName(fileName); // TODO necessarie le associazioni di name e title?
				if (xwFile.getTitle() == null || xwFile.getTitle().equals(""))
					xwFile.setTitle(fileTitle);
			}
		}
		
		// azzeramento dei valori relativi a files caricati
		setXwFileIdsAttached("");
		setXwFileNamesAttached("");
		setXwFileTitlesAttached("");
		
		return null;
	}
	
	/**
	 * Download del file (dopo preleva)
	 */
	public String downloadFile() throws Exception {
		try {
			String id = xwFile.getName();
			String name = xwFile.getTitle();
			
			name = name.replaceAll("'", "_");
			
			String ext_id = "";
			if (id.lastIndexOf(".") != -1)
				ext_id = id.substring(id.lastIndexOf(".")+1);
			String ext_name = "";
			if (name.lastIndexOf(".") != -1)
				ext_name = name.substring(name.lastIndexOf(".")+1);
			
			if (ext_name.equals("")) // se il titolo (name) non ha estensione gli viene assegnato quello dell'id
				name += "." + ext_id;
			
			if (!ext_id.equals("") && !ext_name.equals("") && !ext_id.endsWith(ext_name))
				name += "." + ext_id;
			
			getFormsAdapter().downloadFile(xwFile.getName(), xwFile.getTitle());
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				FacesContext faces = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
				response.setContentType(new MimetypesFileTypeMap().getContentType(id));
				response.setContentLength(attachFile.getContent().length);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
				ServletOutputStream out;
				out = response.getOutputStream();
				out.write(attachFile.getContent());
				faces.responseComplete();
			}
			else {
				// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
				handleErrorResponse(attachFile.getXmlDocumento());
			}
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		
		return null;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setShowdoc(Showdoc showdoc) {
		this.showdoc = showdoc;
	}

	public Showdoc getShowdoc() {
		return showdoc;
	}
	
	public String getDbTable() {
		return dbTable;
	}

	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}
	
	public XwFile getXwFile() {
		return xwFile;
	}

	public void setXwFile(XwFile xwFile) {
		this.xwFile = xwFile;
	}
	
	public boolean isFirmaDigitale() {
		return firmaDigitale;
	}

	public void setFirmaDigitale(boolean firmaDigitale) {
		this.firmaDigitale = firmaDigitale;
	}

	public boolean isSigningEnabled() {
		return signingEnabled;
	}

	public void setSigningEnabled(boolean firmaDigitale) {
		this.signingEnabled = firmaDigitale;
	}
	
	public boolean isEnableIW() {
		return enableIW;
	}

	public void setEnableIW(boolean enableIW) {
		this.enableIW = enableIW;
	}
	
	public boolean isAgent_pdf() {
		return agent_pdf;
	}

	public void setAgent_pdf(boolean agentPdf) {
		this.agent_pdf = agentPdf;
	}

	public boolean isAgent_xml() {
		return agent_xml;
	}

	public void setAgent_xml(boolean agentXml) {
		this.agent_xml = agentXml;
	}
	
	public String getXwFileIdsAttached() {
		return xwFileIdsAttached;
	}

	public void setXwFileIdsAttached(String xwFileIdsAttached) {
		this.xwFileIdsAttached = xwFileIdsAttached;
	}

	public String getXwFileNamesAttached() {
		return xwFileNamesAttached;
	}

	public void setXwFileNamesAttached(String xwFileNamesAttached) {
		this.xwFileNamesAttached = xwFileNamesAttached;
	}
	
	public String getXwFileTitlesAttached() {
		return xwFileTitlesAttached;
	}

	public void setXwFileTitlesAttached(String xwFileTitlesAttached) {
		this.xwFileTitlesAttached = xwFileTitlesAttached;
	}
	
	public String getAllowedChkinExtensions() {
		return allowedChkinExtensions;
	}

	public void setAllowedChkinExtensions(String allowedChkinExtensions) {
		this.allowedChkinExtensions = allowedChkinExtensions;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}
	
}
