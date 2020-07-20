package it.tredi.dw4.docway.beans.sogin;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyVarie;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

public class DocEditModifyVarieET extends DocEditModifyVarie {

	private List<Option> select_customSelectSedeArchivio = new ArrayList<Option>();
	
	public DocEditModifyVarieET() throws Exception {
		super();
	}
	
	public boolean isDocEditModify() {
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(Document domDocumento) {
		super.init(domDocumento);
		
		select_customSelectSedeArchivio = XMLUtil.parseSetOfElement(domDocumento, "/response/select_customSelectSedeArchivio/option", new Option());
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", true, true));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocVarieET showdocVarieET = new ShowdocVarieET();
			showdocVarieET.getFormsAdapter().fillFormsFromResponse(response);
			showdocVarieET.init(response.getDocument());
			showdocVarieET.loadAspects("@varie", response, "showdoc");
			setSessionAttribute("showdocVarieET", showdocVarieET);
			
			return "sogin_showdoc@varie@ET@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			if (!isDocEditModify()) { 
				return clearDocumentoIfIsNotDocEditModify(response);
			}
			else {
				// caso di modifica di un doc
				ShowdocVarieET showdocVarieET = new ShowdocVarieET();
				showdocVarieET.getFormsAdapter().fillFormsFromResponse(response);
				showdocVarieET.init(response.getDocument());
				showdocVarieET.loadAspects("@varie", response, "showdoc");
				setSessionAttribute("showdocVarieET", showdocVarieET);
				
				return "sogin_showdoc@varie@ET@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public List<Option> getSelect_customSelectSedeArchivio() {
		return select_customSelectSedeArchivio;
	}

	public void setSelect_customSelectSedeArchivio(
			List<Option> select_customSelectSedeArchivio) {
		this.select_customSelectSedeArchivio = select_customSelectSedeArchivio;
	}
	
}
