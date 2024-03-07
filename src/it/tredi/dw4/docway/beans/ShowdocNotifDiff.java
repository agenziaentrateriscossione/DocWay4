package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;

public class ShowdocNotifDiff extends DocWayShowdoc {
	private String xml;
	private DocDocWayDocumentFormsAdapter formsAdapter;
	
	private List<Titolo> titoliNotifica;
	
	private List<Titolo> titoliArrivo;
	private List<Titolo> titoliPartenza;
	private List<Titolo> titoliDifferito;
	private List<Titolo> titoliTraUffici;
	private List<Titolo> titoliVarie;
	
	private Doc doc = new Doc();
	private String errore = "";
	private boolean docWithFiles = false;
	
	private boolean enableIW = false;
	
	public ShowdocNotifDiff() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.xml 				= dom.asXML();
		this.titoliNotifica		= XMLUtil.parseSetOfElement(dom, "//titoli/titolo", new Titolo());
		this.titoliArrivo 		= XMLUtil.parseSetOfElement(dom, "//titoli/titolo[@dbTable = 'arrivo']", new Titolo());
		this.titoliPartenza 	= XMLUtil.parseSetOfElement(dom, "//titoli/titolo[@dbTable = 'partenza']", new Titolo());
		this.titoliDifferito 	= XMLUtil.parseSetOfElement(dom, "//titoli/titolo[@dbTable = 'differito']", new Titolo());
		this.titoliTraUffici 	= XMLUtil.parseSetOfElement(dom, "//titoli/titolo[@dbTable = 'interno']", new Titolo());
		this.titoliVarie 		= XMLUtil.parseSetOfElement(dom, "//titoli/titolo[@dbTable = 'varie']", new Titolo());
		this.errore				= XMLUtil.parseStrictElement(dom, "/response/errore");
		
		this.enableIW			= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@enableIW"));
		
		this.doc.init(dom);
		
		if ((doc.getFiles() != null && doc.getFiles().size() > 0 && doc.getFiles().get(0) != null && doc.getFiles().get(0).getName() != null && !doc.getFiles().get(0).getName().equals(""))
				|| (doc.getImmagini() != null && doc.getImmagini().size() > 0 && doc.getImmagini().get(0) != null && doc.getImmagini().get(0).getName() != null && !doc.getImmagini().get(0).getName().equals("")))
			this.docWithFiles 	= true;
		else
			this.docWithFiles	= false;
		
		// inizializzazione di componenti common
		initCommons(dom);
	}	
	
	public DocDocWayDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getXml() {
		return xml;
	}
	
	public List<Titolo> getTitoliArrivo() {
		return titoliArrivo;
	}

	public void setTitoliArrivo(List<Titolo> titoliArrivo) {
		this.titoliArrivo = titoliArrivo;
	}

	public List<Titolo> getTitoliPartenza() {
		return titoliPartenza;
	}

	public void setTitoliPartenza(List<Titolo> titoliPartenza) {
		this.titoliPartenza = titoliPartenza;
	}

	public List<Titolo> getTitoliDifferito() {
		return titoliDifferito;
	}

	public void setTitoliDifferito(List<Titolo> titoliDifferito) {
		this.titoliDifferito = titoliDifferito;
	}

	public List<Titolo> getTitoliTraUffici() {
		return titoliTraUffici;
	}

	public void setTitoliTraUffici(List<Titolo> titoliTraUffici) {
		this.titoliTraUffici = titoliTraUffici;
	}

	public List<Titolo> getTitoliVarie() {
		return titoliVarie;
	}

	public void setTitoliVarie(List<Titolo> titoliVarie) {
		this.titoliVarie = titoliVarie;
	}
	
	public List<Titolo> getTitoliNotifica() {
		return titoliNotifica;
	}

	public void setTitoliNotifica(List<Titolo> titoliNotifica) {
		this.titoliNotifica = titoliNotifica;
	}
	
	public Doc getDoc() {
		return doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}
	
	public String getErrore() {
		return errore;
	}

	public void setErrore(String errore) {
		this.errore = errore;
	}
	
	public boolean isDocWithFiles() {
		return docWithFiles;
	}

	public void setDocWithFiles(boolean docWithFiles) {
		this.docWithFiles = docWithFiles;
	}
	
	public boolean isEnableIW() {
		return enableIW;
	}

	public void setEnableIW(boolean enableIW) {
		this.enableIW = enableIW;
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
	 * Download del file
	 */
	public String downloadFile(String fileId, String fileName) throws Exception {
		try {
			String id = fileId;
			String name = fileName;
			
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
			
			getFormsAdapter().downloadFile(fileId, fileName);
			AttachFile attachFile = getFormsAdapter().getDefaultForm().executeDownloadFile(getUserBean());
			
			if (attachFile.getContent() != null) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				formsAdapter.getDefaultForm().addParam("toDo", ""); // azzeramento del parametro 'toDo'
				
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
	
	/**
	 * Download di doc informatico di tipo file
	 * @throws Exception
	 */
	public String downloadFile() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		downloadFile(file.getName(), file.getTitle());
		
		return null;
	}
	
	/**
	 * Download di doc informatico di tipo immagine
	 * @throws Exception
	 */
	public String downloadImage() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("image");
		downloadFile(file.getName(), file.getTitle());
		
		return null;
	}
	
	/**
	 * Mostra l'anteprima files di un documento
	 * inviato in notifica differita
	 * 
	 * @return
	 * @throws Exception
	 */
	public String showDocNotifDiff() throws Exception {
		try {
			Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
			
			if (titolo != null) {
				formsAdapter.showDocNotifDiff(titolo.getIndice());
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
				this.init(response.getDocument());
			}
			
			return null;
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Caricamento in showdoc di un documento da pagina di 
	 * notifiche differite
	 * @return
	 * @throws Exception
	 */
	public String goToShowDocFromNotifDiff() throws Exception {
		try {
			Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
			
			if (titolo != null) {
				formsAdapter.goToShowDocFromNotifDiff(titolo.getIndice());
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				String dbTable = response.getAttributeValue("/response/@dbTable", "");
				if (dbTable.startsWith("@")) dbTable = dbTable.substring(1);
				
				if (dbTable.equals("differito"))
					dbTable = "arrivo";
				return this.buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
			}
			
			return null;
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Rimanda alla pagina per l'invio via email degli allegati di un doc. notificato 
	 * in modo differito da lista titoli notifica
	 * 
	 * @return
	 * @throws Exception
	 */
	public String showEMailForm() throws Exception {
		try {
			return showEMailForm((Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title"));
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Rimanda alla pagina per l'invio via email degli allegati di un doc. notificato 
	 * in modo differito da anteprima del documento
	 * 
	 * @return
	 * @throws Exception
	 */
	public String showEMailFormDoc() throws Exception {
		try {
			Titolo titolo = null;
			if (titoliNotifica != null && titoliNotifica.size() > 0) {
				int i = 0;
				while (i < titoliNotifica.size() || titolo == null) {
					if (titoliNotifica.get(i) != null && titoliNotifica.get(i).isSelected())
						titolo = titoliNotifica.get(i);
					i++;
				}
			}
			return showEMailForm(titolo);
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Rimanda alla pagina per l'invio via email degli allegati di un doc. notificato 
	 * in modo differito [RW 0032629] (Federico Grillini, 12/12/2005)
	 * 
	 * @param titolo
	 * @return
	 * @throws Exception
	 */
	public String showEMailForm(Titolo titolo) throws Exception {
		if (titolo != null) {
			formsAdapter.showEMailForm(titolo.getIndice());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ResocontoDocAssegnati resocontoDocAssegnati = new ResocontoDocAssegnati();
			resocontoDocAssegnati.getFormsAdapter().fillFormsFromResponse(response);
			resocontoDocAssegnati.init(response.getDocument());
			resocontoDocAssegnati.setShowSxCol(false);
			setSessionAttribute("resocontoDocAssegnati", resocontoDocAssegnati);
			
			return "resoconto_doc_assegnati@sendmail_form";
		}
		return null;
	}
	
}
