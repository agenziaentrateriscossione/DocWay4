package it.tredi.dw4.docway.beans.sogin;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.model.Classif;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class DocEditFascicolo extends it.tredi.dw4.docway.beans.DocEditFascicolo {

	private String data_apertura = "";
	private boolean show_numero_gara_oda_sap = false;
	private boolean show_fornitore = false;
	
	private String cod_sede = ""; // utilizzato per restrizione anagrafiche in lookup fornitore (ACL multiazienda)
	
	public DocEditFascicolo() throws Exception {
		super();
	}

	public void init(Document dom) {
		super.init(dom);
		
		cod_sede 		= XMLUtil.parseStrictAttribute(dom, "/response/@cod_sede");
		data_apertura 	= XMLUtil.parseStrictAttribute(dom, "/response/@currDate");
	}
	
	public String getData_apertura() {
		return data_apertura;
	}

	public void setData_apertura(String data_apertura) {
		this.data_apertura = data_apertura;
	}
	
	public boolean isShow_numero_gara_oda_sap() {
		return show_numero_gara_oda_sap;
	}

	public void setShow_numero_gara_oda_sap(boolean show_numero_gara_oda_sap) {
		this.show_numero_gara_oda_sap = show_numero_gara_oda_sap;
	}
	
	public boolean isShow_fornitore() {
		return show_fornitore;
	}

	public void setShow_fornitore(boolean show_fornitore) {
		this.show_fornitore = show_fornitore;
	}
	
	public String getCod_sede() {
		return cod_sede;
	}

	public void setCod_sede(String cod_sede) {
		this.cod_sede = cod_sede;
	}
	
	/**
	 * salvataggio del fascicolo
	 */
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredFieldSogin()) return null;
			
			// Inserimento manuale: 
			// 1) svuoto eventuali dati di classificazione da titolario residui 
			// 2) formatto i dati inseriti dall'operatore tramite inserimento manuale
			if (!isClassificazioneDaTitolario() && getFascicolo().getClassifNV() != null 
						&& getFascicolo().getClassifNV().getCod() != null && !getFascicolo().getClassifNV().getCod().equals("")
						&& getFascicolo().getClassifNV().getText() != null && !getFascicolo().getClassifNV().getText().equals("")) {
				getFascicolo().setClassif(new Classif());
				getFascicolo().getClassif().setCod(getFascicolo().getClassifNV().getCod());
				getFascicolo().getClassif().setText(getFascicolo().getClassifNV().getCod() + " - " + getFascicolo().getClassifNV().getText());
				getFascicolo().getClassifNV().setCod("");
				getFascicolo().getClassifNV().setText("");
			}
			
			getFormsAdapter().getDefaultForm().addParams(getFascicolo().asFormAdapterParams(""));
			getFormsAdapter().getDefaultForm().addParam(".storia.creazione.@data", data_apertura);
			XMLDocumento response = super._saveDocument("fascicolo", "list_of_doc");
		
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			String assignAndClose = XMLUtil.parseAttribute(response.getDocument(), "response/@assignAndClose", "false");
			if (isPopupPage() && assignAndClose.equals("true")) {
				// inserimento da popup con assegnazione di un documento al fascicolo
				getFormsAdapter().fillFormsFromResponse(response);
				this.init(response.getDocument());
				setSessionAttribute("docEditFascicolo", this);
				
				this.setPopupPage(true);
				return null;
			}
			else {
				// inserimento classico del fascicolo
				buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", "", "sogin", "", response, isPopupPage());		
				return "showdoc@fascicolo@reload";
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * controllo dei campo obbligatori specifici del template di Sogin
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	private boolean checkRequiredFieldSogin(){
		boolean result = super.checkRequiredField();
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		
		// controllo sul formato della data di apertura
		if (data_apertura != null && data_apertura.length() > 0) {
			if (!DateUtil.isValidDate(data_apertura, formatoData)) {
				this.setErrorMessage("templateForm:dataAperturaFascicolo", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_apertura") + "': " + formatoData.toLowerCase());
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * In caso di fascicolo di gara occorre visualizzare i campi Num. Gara e Num. Oda SAP
	 * @param vce
	 */
	public void tipologiaFascicoloValueChange(ValueChangeEvent vce) throws Exception {  
        String tipologia = (String) vce.getNewValue();
        
        if (tipologia != null) {
	        if (tipologia.equals("Fascicolo di Gara"))
	        	show_numero_gara_oda_sap = true;
	        else
	        	show_numero_gara_oda_sap = false;
	        
	        if (tipologia.equals("Fascicolo di Gara") || tipologia.equals("Fascicolo di Qualifica")) 
	        	show_fornitore = true;
	        else
	        	show_fornitore = false;
        }
        else {
        	show_numero_gara_oda_sap = false;
        	show_fornitore = false;
        }
    }
	
	/**
	 * lookup su fornitore
	 * @return
	 * @throws Exception
	 */
	public String lookupFornitore() throws Exception {
		try {
			String value = (getFascicolo().getExtra() != null && getFascicolo().getExtra().getFornitore() != null) ? getFascicolo().getExtra().getFornitore() : "";
			String aclDb = getFormsAdapter().getDefaultForm().getParam("aclDb");
			
			String aliasName 	= "struest_nome";
			String aliasName1 	= "struest_nome";
			String titolo 		= "xml,/struttura_esterna/nome"; //titolo 
			String ord 			= "xml(xpart:/struttura_esterna/nome)"; //ord 
			String campi 		= ".extra.fornitore=xml,/struttura_esterna/nome ; .extra.fornitore_codice=xml,/struttura_esterna/@cod_SAP"; //campi
			String db 			= aclDb; //db 
			String newRecord 	= "/base/acl/engine/acl.jsp?db=" + aclDb + ";dbTable=struttura_esterna;fillField=struttura_esterna.nome;rightCode=ACL-6"; //newRecord 
			String xq			= ""; //extraQuery
			if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("acl_ext_aoo_restriction", false) && cod_sede != null && !cod_sede.equals(""))
				xq			= "[struest_codammaoo]=" + cod_sede; //extraQuery
			
			callLookup(getFascicolo(), aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
			
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
			return clearField(campi, getFascicolo());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
}
