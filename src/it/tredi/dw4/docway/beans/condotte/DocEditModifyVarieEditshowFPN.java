package it.tredi.dw4.docway.beans.condotte;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocEditModifyVarie;
import it.tredi.dw4.docway.model.condotte.Dati_approvazione;
import it.tredi.dw4.docway.model.condotte.Dati_contabili;
import it.tredi.dw4.docway.model.condotte.Dati_fattura;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

public class DocEditModifyVarieEditshowFPN extends DocEditModifyVarie {

	// campi specifici del repertorio fatture passive di condotte
	private Dati_fattura dati_fattura = new Dati_fattura();
	private Dati_contabili dati_contabili = new Dati_contabili();
	private Dati_approvazione dati_approvazione = new Dati_approvazione();
	
	private String hideDivs = ""; // elenco di div da nascondere in fase di modifica
	private String showDivs = ""; // elenco di div da visualizzare in fase di modifica
	
	public DocEditModifyVarieEditshowFPN() throws Exception {
		super();
	}
	
	public boolean isDocEditModify() {
		return true;
	}
	
	public Dati_fattura getDati_fattura() {
		return dati_fattura;
	}

	public void setDati_fattura(Dati_fattura dati_fattura) {
		this.dati_fattura = dati_fattura;
	}
	
	public Dati_contabili getDati_contabili() {
		return dati_contabili;
	}

	public void setDati_contabili(Dati_contabili dati_contabili) {
		this.dati_contabili = dati_contabili;
	}

	public Dati_approvazione getDati_approvazione() {
		return dati_approvazione;
	}

	public void setDati_approvazione(Dati_approvazione dati_approvazione) {
		this.dati_approvazione = dati_approvazione;
	}
	
	public String getHideDivs() {
		return hideDivs;
	}

	public void setHideDivs(String hideDivs) {
		this.hideDivs = hideDivs;
	}
	
	public String getShowDivs() {
		return showDivs;
	}

	public void setShowDivs(String showDivs) {
		this.showDivs = showDivs;
	}
	
	@Override
	public void init(Document domDocumento) {
		super.init(domDocumento);
		
		// init dei dati di fatture di condotte
		dati_fattura.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/dati_fattura"));
		dati_contabili.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/dati_contabili"));
		dati_approvazione.init(XMLUtil.createDocument(domDocumento, "/response/doc/extra/dati_approvazione"));
		
		// caricamento di eventuali sezioni da nascondere in fase di inserimento di una fattura
		String toDo = XMLUtil.parseStrictAttribute(domDocumento, "/response/@toDo");
		if (toDo.startsWith("hideDivs")) {
			hideDivs = toDo.substring(9, toDo.length()-1);
		}
		else if (toDo.startsWith("hideShowDivs")) {
			String tmp = toDo.substring(13, toDo.length()-1);
			int index = tmp.indexOf("|");
			if (index != -1) {
				hideDivs = tmp.substring(0, index);
				showDivs = tmp.substring(index+1);
			}
		}
		
		if (hideDivs.length() > 0 && !hideDivs.endsWith(","))
			hideDivs += ",";
		if (showDivs.length() > 0 && !showDivs.endsWith(","))
			showDivs += ",";
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// TODO e' corretto spedire tutti i dati anche se sono solo pochi quelli modificabili?
			
			formsAdapter.getDefaultForm().addParams(getDoc().asFormAdapterParams("", true, true));
			formsAdapter.getDefaultForm().addParams(dati_fattura.asFormAdapterParams(".extra.dati_fattura"));
			formsAdapter.getDefaultForm().addParams(dati_contabili.asFormAdapterParams(".extra.dati_contabili"));
			formsAdapter.getDefaultForm().addParams(dati_approvazione.asFormAdapterParams(".extra.dati_approvazione"));
			
			XMLDocumento response = super._saveDocument("doc", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocVarieFPN showdocVarieFPN = new ShowdocVarieFPN();
			showdocVarieFPN.getFormsAdapter().fillFormsFromResponse(response);
			showdocVarieFPN.init(response.getDocument());
			showdocVarieFPN.loadAspects("@varie", response, "showdoc");
			setSessionAttribute("showdocVarieFPN", showdocVarieFPN);
			
			return "condotte_showdoc@varie@FPN@reload";
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
			
			ShowdocVarieFPN showdocVarieFPN = new ShowdocVarieFPN();
			showdocVarieFPN.getFormsAdapter().fillFormsFromResponse(response);
			showdocVarieFPN.init(response.getDocument());
			showdocVarieFPN.loadAspects("@varie", response, "showdoc");
			setSessionAttribute("showdocVarieFPN", showdocVarieFPN);
			
			return "condotte_showdoc@varie@FPN@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * lookup su centro di costo
	 * @return
	 * @throws Exception
	 */
	public String lookupCentroCosto() throws Exception {
		String value = (getDati_fattura().getCentro_costo().getName() != null) ? getDati_fattura().getCentro_costo().getName() : "";

		String aliasName 	= "struint_nome,ruoli_nome";
		String aliasName1 	= "struint_nome,ruoli_nome";
		String titolo 		= "xml,/*/nome \"^ (~\" xml,/ruolo/societa \"~^)\""; //titolo 
		String ord 			= "xml(xpart:/struttura_interna/nome),xml(xpart:/ruolo/nome)"; //ord
		String campi 		= ".centro_costo.name=xml,/*/nome" +
								" ; .centro_costo.@cod=xml,/struttura_interna/@cod_uff xml,/ruolo/@id";
		String db 			= getFormsAdapter().getDefaultForm().getParam("aclDb"); //db 
		String newRecord 	= ""; //newRecord
		String xq			= ""; //extraQuery
		
		callLookup(getDati_fattura(), aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		
		return null;
	}
	
	/**
	 * clear lookup su centro di costo
	 * @return
	 * @throws Exception
	 */
	public String clearLookupCentroCosto() throws Exception {
		String campi = ".centro_costo.name=xml,/struttura_interna/nome" +
								" ; .centro_costo.@cod=xml,/struttura_interna/@cod_uff";
		return clearField(campi, getDati_fattura());
	}
	
	/**
	 * controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = super.checkRequiredField();
		
		if (hideDivs != null && hideDivs.length() > 0) {
			if (!hideDivs.contains("dati_uff_contabile,")) {
				// controllo sul campo centro di costo
				if (getDati_fattura().getCentro_costo().getCod() == null || getDati_fattura().getCentro_costo().getCod().length() == 0) {
					this.setErrorMessage("templateForm:centrocosto_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.centro_di_costo") + "'");
					result = true;
				}
			}
			
			if (!hideDivs.contains("dati_approvazione,")) {
				// controllo sul campo note di approvazione
				if (getDati_approvazione().getNote() == null || getDati_approvazione().getNote().length() == 0) {
					this.setErrorMessage("templateForm:da_note", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.notes") + "'");
					result = true;
				}
			}
		}
				
		return result;
	}

}
