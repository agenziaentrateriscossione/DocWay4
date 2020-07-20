package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.PersonalView;
import it.tredi.dw4.model.customfields.QueryPage;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.dom4j.Document;

public class QueryPersonalView extends DocWayQuery {

	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private List<PersonalView> listof_personalView = new ArrayList<PersonalView>(); // personalView di ricerca (template personalizzati)
	
	private String tipoTabella = "";
	private String textTabella = "";
	
	private List<QueryPage> listof_customFields = new ArrayList<QueryPage>(); // pagine di ricerca specifiche su repertori con campi custom
	
	public QueryPersonalView() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.xml = dom.asXML();
		
		listof_personalView = XMLUtil.parseSetOfElement(dom, "/response/listof_personalView/personalView", new PersonalView());
		listof_customFields = XMLUtil.parseSetOfElement(dom, "/response/listof_customFields/queryPage", new QueryPage());
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public List<PersonalView> getListof_personalView() {
		return listof_personalView;
	}

	public void setListof_personalView(List<PersonalView> listof_personalView) {
		this.listof_personalView = listof_personalView;
	}
	
	public String getTipoTabella() {
		return tipoTabella;
	}

	public void setTipoTabella(String tipoTabella) {
		this.tipoTabella = tipoTabella;
	}

	public String getTextTabella() {
		return textTabella;
	}

	public void setTextTabella(String textTabella) {
		this.textTabella = textTabella;
	}
	
	public List<QueryPage> getListof_customFields() {
		return listof_customFields;
	}

	public void setListof_queryPage(List<QueryPage> listof_customFields) {
		this.listof_customFields = listof_customFields;
	}

	@Override
	public QueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	/**
	 * Lettura degli attributi tipoTabella e textTabella passati come attributi attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerSelectPersonalView(ActionEvent event){
		this.tipoTabella = (String) event.getComponent().getAttributes().get("tipoTabella");
		this.textTabella = (String) event.getComponent().getAttributes().get("textTabella");
	}
	
	/**
	 * Selezione di un repertorio dall'elenco (redirect a docEdit)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQ() throws Exception{
		PersonalView personalView = (PersonalView) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("personalView");
		return gotoTableQ(personalView);
	}
	
	/**
	 * Caricamento pagina di ricerca di una specifica personalView
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQ(PersonalView personalView) throws Exception {
		try {
			String tableName = "";
			if (tipoTabella.equals("A"))
				tableName = Const.DOCWAY_TIPOLOGIA_ARRIVO;
			else if (tipoTabella.equals("P"))
				tableName = Const.DOCWAY_TIPOLOGIA_PARTENZA;
			else if (tipoTabella.equals("I")) 
				tableName = Const.DOCWAY_TIPOLOGIA_INTERNO;
			else if (tipoTabella.equals("V"))
				tableName = Const.DOCWAY_TIPOLOGIA_VARIE;
			formsAdapter.gotoTableQ(tableName + "#personalView=" + personalView.getTemplate(), false);
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificQueryPageAndReturnNavigationRule(tableName, response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * caricamento di una pagina di ricerca su specifico repertorio con campi custom
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQCustomFields() throws Exception {
		try {
			QueryPage customFieldsQueryPage = (QueryPage) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("customFields");
			if (customFieldsQueryPage != null) {
				
				formsAdapter.gotoTableQCustomFieldsRep("globale", false, customFieldsQueryPage.getTabella() + "_" + customFieldsQueryPage.getCodice());
				XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				
				// caricamento della pagina di ricerca specifica per il repertorio con campi custom
				QueryCustomFieldsRep queryCustomFieldsRep = new QueryCustomFieldsRep();
				queryCustomFieldsRep.getFormsAdapter().fillFormsFromResponse(response);
				queryCustomFieldsRep.init(response.getDocument());
				setSessionAttribute("queryCustomFieldsRep", queryCustomFieldsRep);
				
				return "query@repCustomFields";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		
		return null;
	}
	
}
