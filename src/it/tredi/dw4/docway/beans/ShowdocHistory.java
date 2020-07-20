package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.XwFile;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class ShowdocHistory extends DocWayShowdoc {
	private DocDocWayDocumentFormsAdapter formsAdapter;
	private String xml;
	
	private String tipoDoc = ""; // tipologia del doc per il quale e' stata caricata l'history
	private List<XwFile> xwfiles = new ArrayList<XwFile>(); // history su un file (versioni)
	
	public ShowdocHistory() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		
		tipoDoc = XMLUtil.parseStrictAttribute(dom, "/response/doc/@tipo");
		xwfiles = XMLUtil.parseSetOfElement(dom, "/response/doc/files/node()[name()='xw:file']", new XwFile());
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
	
	@Override
	public DocDocWayDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public List<XwFile> getXwfiles() {
		return xwfiles;
	}

	public void setXwfiles(List<XwFile> xwfiles) {
		this.xwfiles = xwfiles;
	}
	
	/**
	 * Scaricamento di una conversione da una versione del file
	 * @return
	 * @throws Exception
	 */
	public String downloadFileVersionConvertito() throws Exception {
		XwFile fileConvertito = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("derFromXwfile");
		return downloadFile(fileConvertito);
	}
	
	/**
	 * Scaricamento di una versione del file di un doc
	 * @return
	 * @throws Exception
	 */
	public String downloadFileVersion() throws Exception {
		XwFile file = (XwFile) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("xwfile");
		return downloadFile(file);
	}
	
	/**
	 * Scaricamento da versioning del file
	 * @return
	 * @throws Exception
	 */
	public String downloadFile(XwFile file) throws Exception {
		try {
			String id = file.getName();
			String name = file.getTitle();
			
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
			
			getFormsAdapter().downloadFile(file.getName(), file.getTitle());
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
			
			return null;
		}
		catch (Throwable t) {
			// Errore nello scaricamento del file
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return null;
		}
	}
	
}
