package it.tredi.dw4.docway.beans.sogin;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyVarie;

public class DocEditModifyVarieDOCQLF extends DocEditModifyVarie {

	public DocEditModifyVarieDOCQLF() throws Exception {
		super();
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
			
			ShowdocVarieDOCQLF showdocVarieDOCQLF = new ShowdocVarieDOCQLF();
			showdocVarieDOCQLF.getFormsAdapter().fillFormsFromResponse(response);
			showdocVarieDOCQLF.init(response.getDocument());
			showdocVarieDOCQLF.loadAspects("@varie", response, "showdoc");
			setSessionAttribute("showdocVarieDOCQLF", showdocVarieDOCQLF);
			
			return "sogin_showdoc@varie@DOCQLF@reload";
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
				ShowdocVarieDOCQLF showdocVarieDOCQLF = new ShowdocVarieDOCQLF();
				showdocVarieDOCQLF.getFormsAdapter().fillFormsFromResponse(response);
				showdocVarieDOCQLF.init(response.getDocument());
				showdocVarieDOCQLF.loadAspects("@varie", response, "showdoc");
				setSessionAttribute("showdocVarieDOCQLF", showdocVarieDOCQLF);
				
				return "sogin_showdoc@varie@DOCQLF@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	/**
	 * lookup su fornitore
	 * @return
	 * @throws Exception
	 */
	public String lookupFornitore() throws Exception {
		try {
			String value = (getDoc().getExtra() != null && getDoc().getExtra().getFornitore() != null) ? getDoc().getExtra().getFornitore() : "";
			String aclDb = getFormsAdapter().getDefaultForm().getParam("aclDb");
			
			String aliasName 	= "struest_nome";
			String aliasName1 	= "struest_nome";
			String titolo 		= "xml,/struttura_esterna/nome"; //titolo 
			String ord 			= "xml(xpart:/struttura_esterna/nome)"; //ord 
			String campi 		= ".extra.fornitore=xml,/struttura_esterna/nome ; .extra.fornitore_codice=xml,/struttura_esterna/@cod_SAP"; //campi
			String db 			= aclDb; //db 
			String newRecord 	= "/base/acl/engine/acl.jsp?db=" + aclDb + ";dbTable=struttura_esterna;fillField=struttura_esterna.nome;rightCode=ACL-6"; //newRecord 
			String xq			= ""; //extraQuery
			
			callLookup(getDoc(), aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * clear su lookup su fornitore
	 * @return
	 * @throws Exception
	 */
	public String clearLookupFornitore() throws Exception {
		try {
			String campi = ".extra.fornitore=xml,/struttura_esterna/nome ; .extra.fornitore_codice=xml,/struttura_esterna/@cod_SAP"; 
			return clearField(campi, getDoc());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
