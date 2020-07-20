package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.docway.beans.ShowdocDoc;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocumentFormsAdapter;
import it.tredi.dw4.docway.model.delibere.InternoProposta;

public class ShowdocInternoProposta extends ShowdocDoc {
	private String categoriaLabel = "";
	
	public ShowdocInternoProposta() throws Exception {
		this.formsAdapter = new DocDocWayDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		doc = new InternoProposta();
		doc.init(dom);
		
		initCommon(dom);
		
		String dicitCategoria 		=	FormsAdapter.getParameterFromCustomTupleValue("dicitCategoria", formsAdapter.getCustomTupleName()); 
		if(!dicitCategoria.equals(""))
			this.setCategoriaLabel(dicitCategoria.substring(0, dicitCategoria.indexOf("|")));
	}

	@Override
	public void reload() throws Exception {
		super._reload("showdoc@interno@proposta");
	}
	
	@Override
	public String modifyTableDoc() throws Exception {
		if (getDoc().getRepertorio() != null && getDoc().getRepertorio().getCod() != null && getDoc().getRepertorio().getCod().length() > 0)
			this.formsAdapter.modifyTableDoc(getDoc().getRepertorio().getCod(), getDoc().getRepertorio().getText());
		else
			this.formsAdapter.modifyTableDoc();
		
		XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		return buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());

	}

	public String getCategoriaLabel() {
		return categoriaLabel;
	}

	public void setCategoriaLabel(String categoriaLabel) {
		this.categoriaLabel = categoriaLabel;
	}
}
