package it.tredi.dw4.docway.beans.sogin;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class DocEditModifyFascicolo extends it.tredi.dw4.docway.beans.DocEditModifyFascicolo {

	private String data_apertura = "";
	private String data_chiusura = "";
	private boolean show_numero_gara_oda_sap = false;
	private boolean show_fornitore = false;
	
	public DocEditModifyFascicolo() throws Exception {
		super();
	}
	
	/**
	 * init del bean di modifica del fascicolo
	 */
	public void init(Document dom) {
		super.init(dom);
		
		if (getFascicolo().getExtra() != null 
				&& getFascicolo().getExtra().getTipologia_fascicolo() != null) { 
			if (getFascicolo().getExtra().getTipologia_fascicolo().equals("Fascicolo di Gara"))
				show_numero_gara_oda_sap = true;
			
			if (getFascicolo().getExtra().getTipologia_fascicolo().equals("Fascicolo di Gara") || getFascicolo().getExtra().getTipologia_fascicolo().equals("Fascicolo di Qualifica"))
				show_fornitore = true;
		}
		
		if (getFascicolo().getCreazione() != null 
				&& getFascicolo().getCreazione().getData() != null)
			data_apertura = DateUtil.getDateNorm(getFascicolo().getCreazione().getData());
		if (getFascicolo().getChiusura() != null 
				&& getFascicolo().getChiusura().getData() != null)
			data_chiusura = DateUtil.getDateNorm(getFascicolo().getChiusura().getData());
	}
	
	/**
	 * salvataggio del fascicolo
	 */
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredFieldSogin()) return null;
			
			getFormsAdapter().getDefaultForm().addParam(".storia.creazione.@data", data_apertura);
			getFormsAdapter().getDefaultForm().addParam(".storia.chiusura.@data", data_chiusura);
			getFormsAdapter().getDefaultForm().addParams(getFascicolo().asFormAdapterParams(""));
			XMLDocumento response = super._saveDocument("fascicolo", "list_of_doc");
			
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", "", "sogin", "", response, isPopupPage());	
			return "showdoc@fascicolo@reload";
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
		
		// controllo sul formato della data di chiusura
		if (data_chiusura != null && data_chiusura.length() > 0) {
			if (!DateUtil.isValidDate(data_chiusura, formatoData)) {
				this.setErrorMessage("templateForm:dataChiusuraFascicolo", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_chiusura") + "': " + formatoData.toLowerCase());
				result = true;
			}
		}
		
		return result;
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
	
	public String getData_apertura() {
		return data_apertura;
	}

	public void setData_apertura(String data_apertura) {
		this.data_apertura = data_apertura;
	}

	public String getData_chiusura() {
		return data_chiusura;
	}

	public void setData_chiusura(String data_chiusura) {
		this.data_chiusura = data_chiusura;
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
			if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("acl_ext_aoo_restriction", false))
				xq				= "[struest_codammaoo]=" + getFascicolo().getCod_amm_aoo();
			
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
