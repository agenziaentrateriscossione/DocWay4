package it.tredi.dw4.docway.beans.sogin;

import it.tredi.dw4.utils.XMLDocumento;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditVarie;

public class DocEditVarieDOCQLF extends DocEditVarie {

	public DocEditVarieDOCQLF() throws Exception {
		super();
	}

	@Override
	public void init(Document domDocumento) {
		super.init(domDocumento);
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
