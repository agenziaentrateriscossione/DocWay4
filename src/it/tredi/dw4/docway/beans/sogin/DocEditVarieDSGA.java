package it.tredi.dw4.docway.beans.sogin;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditVarie;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditVarieDSGA extends DocEditVarie {

	private List<Option> select_customSelectSedeArchivio = new ArrayList<Option>();
	
	public DocEditVarieDSGA() throws Exception {
		super();
	}
	
	public boolean isDocEditModify() {
		return false;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(Document domDocumento) {
		super.init(domDocumento);
		
		// per questo repertorio viene forzata la classificazione
		getDoc().getClassif().setCod("00/00");
		getDoc().getClassif().setText("00/00 - non classificato");
		
		select_customSelectSedeArchivio = XMLUtil.parseSetOfElement(domDocumento, "/response/select_customSelectSedeArchivio/option", new Option());
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			// nel caso in cui gli allegati siano disattivati in inserimenti di doc non protocollati
			// si procede con l'assegnazione di ''
			if (formsAdapter.checkBooleanFunzionalitaDisponibile("disAllegato", false)) {
				if (getDoc().getAllegati() != null && getDoc().getAllegati().size() == 1 && getDoc().getAllegati().get(0) != null) {
					getDoc().getAllegati().get(0).setText("0 - nessun allegato");
				}
			}
						
			if (checkRequiredField()) return null;
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", false, true));
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocVarieDSGA showdocVarieDSGA = new ShowdocVarieDSGA();
			showdocVarieDSGA.getFormsAdapter().fillFormsFromResponse(response);
			showdocVarieDSGA.init(response.getDocument());
			showdocVarieDSGA.loadAspects("@varie", response, "showdoc");
			setSessionAttribute("showdocVarieDSGA", showdocVarieDSGA);
			
			return "sogin_showdoc@varie@DSGA@reload";
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
