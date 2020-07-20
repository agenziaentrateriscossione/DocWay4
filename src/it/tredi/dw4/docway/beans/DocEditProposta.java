package it.tredi.dw4.docway.beans;

import java.util.List;

import javax.faces.context.FacesContext;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.delibere.PropostaDocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.delibere.Proposta;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditProposta extends DocWayDocedit {
	
	private PropostaDocDocWayDocEditFormsAdapter formsAdapter;
	private List<Proposta> listof_proposte;
	private String xml = "";

	public DocEditProposta() throws Exception {
		this.formsAdapter = new PropostaDocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public String inserisciProposta() throws Exception{
		Proposta proposta = (Proposta) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titoloProposta");
		try{
			this.formsAdapter.inserisciProposta(proposta.getCod(), proposta.getCod_organo(), proposta.getTipo(), "");
			
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			
			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.xml 					=	dom.asXML();
		this.setListof_proposte(XMLUtil.parseSetOfElement(dom, "response/listof_proposte/proposta", new Proposta()));
	}

	@Override
	public DocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		return null;
	}

	@Override
	public String clearDocument() throws Exception {
		return null;
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

	public List<Proposta> getListof_proposte() {
		return listof_proposte;
	}

	public void setListof_proposte(List<Proposta> listof_proposte) {
		this.listof_proposte = listof_proposte;
	}
}
