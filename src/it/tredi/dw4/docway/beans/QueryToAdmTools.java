package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.delibere.Organo;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class QueryToAdmTools extends DocWayQuery {
	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	private List<Organo> organi = new ArrayList<Organo>();
	private String customTupleName = "";
	
	public QueryToAdmTools() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		this.xml 		= dom.asXML();
		this.organi 	= XMLUtil.parseSetOfElement(dom, "/response/organo", new Organo());
		this.customTupleName	=	formsAdapter.getCustomTupleName();
	}

	@Override
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public String queryPlain() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String mostraDocumentoQ(String posizione, String sel, String db) throws Exception{
		try{
			formsAdapter.mostraDocumentoQ(posizione, sel, db);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());	
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public String inserisciDocInACL(String table) throws Exception{
		try{
			formsAdapter.inserisciDocInACL(table);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());	
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}

	/*
	 * getter / setter
	 * */
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public void setFormsAdapter(DocDocWayQueryFormsAdapter formsAdapter) {
		this.formsAdapter = formsAdapter;
	}

	public List<Organo> getOrgani() {
		return organi;
	}

	public void setOrgani(List<Organo> organi) {
		this.organi = organi;
	}

	public String getCustomTupleName() {
		return customTupleName;
	}

	public void setCustomTupleName(String customTupleName) {
		this.customTupleName = customTupleName;
	}

}
