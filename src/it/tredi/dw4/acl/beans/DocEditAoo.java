package it.tredi.dw4.acl.beans;

import org.dom4j.Document;

import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.Aoo;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;

public class DocEditAoo extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private Aoo aoo;
	
	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	public DocEditAoo() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.aoo = new Aoo();
    	this.aoo.init(domDocumento);
    	// Se non venirre riassegnato al punto '.' l'nrecord in questo punto verrebbe restituito errore in caso di 'ripeti nuovo'
    	this.aoo.setNrecord(".");
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.aoo.asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("aoo", "list_of_struttur");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificShowdocPageAndReturnNavigationRule("aoo", response);		
			return "showdoc@aoo@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public Aoo getAoo() {
		return this.aoo;
	}
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(response);
			this.init(response.getDocument());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
		return null;
	}
	
	/**
	 * Lookup su email principale
	 * @return
	 * @throws Exception
	 */
	public String lookupEmailPrincipale() throws Exception {
		String value = (aoo.getEmailPrincipale() != null) ? aoo.getEmailPrincipale() : "";
		String aliasName = "casellaPostaElettronica_email"; //aliasName 
		String aliasName1 = ""; //aliasName1 
		String titolo = "xml,/casellaPostaElettronica/mailbox_in/@email"; //titolo 
		String ord = "xml(xpart:/casellaPostaElettronica/mailbox_in/@email)"; //ord 
		String campi = "emailPrincipale=xml,/casellaPostaElettronica/mailbox_in/@email"; //campi
		String xq = "[casellaPostaElettronica_codammaoo]=\"" + aoo.getCod_amm() + aoo.getCod_aoo() + "\" AND [casellaPostaElettronica_interop]=\"si\""; // xq
		String db = ""; //db 
		String newRecord = ""; //newRecord 
		
		callLookup(aoo, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	/**
	 * Clear lookup su email principale
	 * @return
	 * @throws Exception
	 */
	public String clearLookupEmailPrincipale() throws Exception {
		String campi = "emailPrincipale=xml,/casellaPostaElettronica/mailbox_in/@email"; //campi
		return clearField(campi, aoo);
	}
	
	public boolean checkRequiredField(){
		boolean result = false;
		if (aoo.getNome() == null || "".equals(aoo.getNome().trim())) {
			this.setErrorMessage("templateForm:aoo_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.name") + "'");
			result = true;
		}
		if (aoo.getCod_amm() == null || "".equals(aoo.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:aoo_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (aoo.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:aoo_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (aoo.getCod_aoo() == null || "".equals(aoo.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:aoo_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (aoo.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:aoo_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}
		
		return result;
	}	

}
