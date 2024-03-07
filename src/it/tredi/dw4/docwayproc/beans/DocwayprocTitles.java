package it.tredi.dw4.docwayproc.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.TitlesFormsAdapter;
import it.tredi.dw4.beans.Titles;
import it.tredi.dw4.docway.doc.adapters.DocDocWayTitlesFormsAdapter;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.context.FacesContext;

import org.dom4j.Document;
import org.dom4j.Element;

public class DocwayprocTitles extends Titles {
	protected DocDocWayTitlesFormsAdapter formsAdapter;
	private String xml = "";
	
	private String dbTable = "";
	private String action = "";
	
	public DocwayprocTitles() throws Exception {
		this.formsAdapter = new DocDocWayTitlesFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		titoli 			= XMLUtil.parseSetOfElement(dom, "//titolo", new Titolo());
		
		Element root 	= dom.getRootElement();
		dbTable		 	= root.attributeValue("dbTable", "");
		xml 			= dom.asXML();
    	
    	setCurrentPage(this.formsAdapter.getCurrent()+"");
    	
    	// inizializzazione delle azioni massive su lista titoli
    	initAzioniMassive(dom);
	}

	@Override
	public TitlesFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String mostraDocumento() throws Exception {
		try {	
			Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
			String index = titolo.getIndice();
			String dbTable = titolo.getDbTable();
			String tipo = titolo.getDb();
			
			XMLDocumento response = super._mostraDocumento(Integer.valueOf(index)-1, tipo, dbTable);
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
			
			if (!dbTable.startsWith("@"))
				dbTable = "@" + dbTable;
			return "docwayproc@showdoc" + dbTable;
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getDbTable() {
		return dbTable;
	}

	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * esportazione in formato CSV delle voci di indice
	 * @return
	 * @throws Exception
	 */
	public String exportCSV() throws Exception {
		try {
			this.formsAdapter.esportaCSV(getSelid(), "");
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				DocwayprocLoadingbar docwayprocLoadingbar = new DocwayprocLoadingbar();
				docwayprocLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docwayprocLoadingbar.init(response);
				setLoadingbar(docwayprocLoadingbar);
				docwayprocLoadingbar.setActive(true);
				action = "exportCSV";
			}
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

}
