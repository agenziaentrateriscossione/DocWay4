package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.TitoloRepertorio;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditRepertorio extends DocWayDocedit {
	
	private DocDocWayDocEditFormsAdapter formsAdapter;
	
	private String xml = "";
	private List<TitoloRepertorio> listof_rep = new ArrayList<TitoloRepertorio>();
	
	private String tipoRepertorio = ""; // tipo di repertorio selezionato dall'utente (A, P, I, V), in base al quale creare il documento
	private String textTabella = "";
	
	private String excludeInsertRepertori = "";
	private HashMap<String, String> customInsertRepertorio = new HashMap<String, String>();
	
	public DocEditRepertorio() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		xml = dom.asXML();
		
		listof_rep = XMLUtil.parseSetOfElement(dom, "/response/listof_rep/repertorio", new TitoloRepertorio());
		
		// caricamento dei repertori da escludere dalla pagina di inserimento (lettura
		// da properties di DocWay4)
		excludeInsertRepertori = DocWayProperties.readProperty("exclude.insert.codici_repertori", "");
		if (excludeInsertRepertori.length() > 0 && !excludeInsertRepertori.endsWith(","))
			excludeInsertRepertori = excludeInsertRepertori + ",";
		
		// caricamento di repertori con formsAdapter personalizzato (personalizzazioni a livello di logica
		// di business)
		List<String> customInsertList = DocWayProperties.getPropertyList("custom.insert.repertorio");
		if (customInsertList != null && customInsertList.size() > 0) {
			for (int i=0; i<customInsertList.size(); i++) {
				String rep = customInsertList.get(i);
				if (rep != null && rep.length() > 0) {
					int index = rep.indexOf(",");
					if (index != -1) 
						customInsertRepertorio.put(rep.substring(0, index).trim(), rep.substring(index+1).trim());
				}
			}
		}
	}
	
	public DocDocWayDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public List<TitoloRepertorio> getListof_rep() {
		return listof_rep;
	}

	public void setListof_rep(List<TitoloRepertorio> listof_rep) {
		this.listof_rep = listof_rep;
	}
	
	public String getTipoRepertorio() {
		return tipoRepertorio;
	}

	public void setTipoRepertorio(String tipo) {
		this.tipoRepertorio = tipo;
	}
	
	public String getTextTabella() {
		return textTabella;
	}

	public void setTextTabella(String textTabella) {
		this.textTabella = textTabella;
	}
	
	public String getExcludeInsertRepertori() {
		return excludeInsertRepertori;
	}

	public void setExcludeInsertRepertori(String excludeInsertRepertori) {
		this.excludeInsertRepertori = excludeInsertRepertori;
	}
	
	public HashMap<String, String> getCustomInsertRepertorio() {
		return customInsertRepertorio;
	}

	public void setCustomInsertRepertorio(HashMap<String, String> customInsertRepertorio) {
		this.customInsertRepertorio = customInsertRepertorio;
	}
	
	@Override
	public String saveDocument() throws Exception {
		return null;
	}

	@Override
	public String clearDocument() throws Exception {
		return null;
	}
	
	/**
	 * Lettura degli attributi tabellaRepertorio e textTabella passati come attributi attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerSelectRepertorio(ActionEvent event){
		this.tipoRepertorio = (String) event.getComponent().getAttributes().get("tipoRepertorio");
		this.textTabella = (String) event.getComponent().getAttributes().get("textTabella");
	}
	
	/**
	 * Selezione di un repertorio dall'elenco (redirect a docEdit)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insTableDocRep() throws Exception{
		TitoloRepertorio titoloRepertorio = (TitoloRepertorio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titoloRepertorio");
		return insTableDocRep(titoloRepertorio);
	}
	
	/**
	 * Selezione di un repertorio dall'elenco (redirect a docEdit)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insTableDocRep(TitoloRepertorio titoloRepertorio) throws Exception{
		try {
			String tableName = getTableName(tipoRepertorio);
			XMLDocumento response = super._insTableDocRep(tableName, titoloRepertorio.getCodice(), titoloRepertorio.getDescrizione() + " " + textTabella);
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(tableName, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * chiamata di inserimento di un repertorio FPN o FPNALGERIA di Condotte
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insTableDocRepFPN() throws Exception {
		try {
			TitoloRepertorio titoloRepertorio = (TitoloRepertorio) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titoloRepertorio");
			
			String tableName = getTableName(tipoRepertorio);
			
			getFormsAdapter().insTableDocRepFPN(tableName, titoloRepertorio.getCodice(), titoloRepertorio.getDescrizione() + " " + textTabella);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(tableName, response, false);
			
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	private String getTableName(String letterTipoRepertorio) {
		String tableName = "";
		if (letterTipoRepertorio.equals("A"))
			tableName = Const.DOCWAY_TIPOLOGIA_ARRIVO;
		else if (letterTipoRepertorio.equals("P"))
			tableName = Const.DOCWAY_TIPOLOGIA_PARTENZA;
		else if (letterTipoRepertorio.equals("I")) 
			tableName = Const.DOCWAY_TIPOLOGIA_INTERNO;
		else if (letterTipoRepertorio.equals("V"))
			tableName = Const.DOCWAY_TIPOLOGIA_VARIE;
		return tableName;
	}
	
	@Override
	public XmlEntity getModel() {
		return null;
	}
	
}
