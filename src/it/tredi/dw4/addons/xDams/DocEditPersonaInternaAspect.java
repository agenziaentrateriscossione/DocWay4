package it.tredi.dw4.addons.xDams;

import it.tredi.dw4.acl.beans.DocEditPersonaInterna;
import it.tredi.dw4.acl.model.custom.Archivio;
import it.tredi.dw4.acl.model.custom.ArchiveAuthorization;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.addons.BaseAddOn;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class DocEditPersonaInternaAspect extends BaseAddOn {

	private ArchiveAuthorization archiveAuthorization = new ArchiveAuthorization();
	
	public DocEditPersonaInternaAspect(String template, Page host) {
		super(template, host);
	}
	
	@Override
	public DocEditPersonaInterna getHost() {
		return (DocEditPersonaInterna) super.getHost();
	}

	@Override
	public void init(Document dom) {
		this.archiveAuthorization.init(XMLUtil.createDocument(dom, "//persona_interna/extra"));
		
		// visto che si tratta di un inserimento si azzerano i dati relativi a
		// userid e password (caso di ripeti nuovo)
		this.archiveAuthorization.setUserid("");
		this.archiveAuthorization.setPassword("");
	}
	
	@Override
	public Map<String, String> asFormAdapterParams() {
		String prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
		params.putAll(archiveAuthorization.asFormAdapterParams(prefix + ".extra"));
		return params;
	}
	
	@Override
	public void clear() {
		archiveAuthorization.setUserid("");
		archiveAuthorization.setPassword("");
		archiveAuthorization.setDominio("");
		List<Archivio> archivi = new ArrayList<Archivio>();
		archivi.add(new Archivio());
		archiveAuthorization.setArchivi(archivi);
	}
	
	public ArchiveAuthorization getArchiveAuthorization() {
		return archiveAuthorization;
	}

	public void setArchiveAuthorization(ArchiveAuthorization archiveAuthorization) {
		this.archiveAuthorization = archiveAuthorization;
	}
	
	/**
	 * Lookup su archivi, addon xDams (andrebbe spostato nella classe relativa all'aspect)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String lookupArchivio() throws Exception {
		try {
			Archivio archivio = (Archivio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archivio");
			int num = (archiveAuthorization.getArchivi().contains(archivio)) ? archiveAuthorization.getArchivi().indexOf(archivio): 0;
			
			String value 		= (archiveAuthorization.getArchivi()!= null && !"".equals(archiveAuthorization.getArchivi().get(num))) ? archiveAuthorization.getArchivi().get(num).getDescr() : "";
			String aliasName 	= "archivi_descr"; //aliasName 
			String aliasName1 	= ""; //aliasName1 
			String titolo 		= "xml,/archBridge/descr"; //titolo 
			String ord 			= "xml(xpart:/archBridge/descr)"; //ord 
			String campi 		= ".archivi["+num+"].id=xml,/archBridge/@alias " 
										+ "; .archivi["+num+"].descr=xml,/archBridge/descr"; //campi
			String xq 			= ""; // xq 
			String db 			= ""; //db 
			String newRecord 	= ""; //newRecord
			
			getHost().callLookup(archiveAuthorization, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		}
		catch (Throwable t) {
			getHost().handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getHost().getFormsAdapter().fillFormsFromResponse(getHost().getFormsAdapter().getLastResponse()); //restore delle form
		}
		
		return null;
	}
	
	/**
	 * Clear lookup su archivi, addon xDams (andrebbe spostato nella classe relativa all'aspect)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearLookupArchivio() throws Exception {
		try {
			Archivio archivio = (Archivio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("archivio");
			int num = (archiveAuthorization.getArchivi().contains(archivio)) ? archiveAuthorization.getArchivi().indexOf(archivio): 0;
			
			String campi = ".archivi["+num+"].id=xml,/archBridge/@alias ; .archivi["+num+"].descr=xml,/archBridge/descr"; //campi
			return getHost().clearField(campi, archiveAuthorization);
		}
		catch (Throwable t) {
			getHost().handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getHost().getFormsAdapter().fillFormsFromResponse(getHost().getFormsAdapter().getLastResponse()); //restore delle form
		}
		
		return null;
	}
	
	@Override
	public String asQuery() {
		return null;
	}

}
