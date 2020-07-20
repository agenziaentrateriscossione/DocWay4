package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.Ruolo;
import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class DocEditRuolo extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private Ruolo ruolo;
	private List<Societa> societa;

	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	
	public DocEditRuolo() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.ruolo = new Ruolo();
    	this.ruolo.init(domDocumento);    
    	this.societa = XMLUtil.parseSetOfElement(domDocumento, "//societa_select/societa", new Societa());
    	Societa test = new Societa();
    	test.setText("");
    	test.setCod("");
    	societa.add(0, test);
    	for (Iterator<Societa> iterator = societa.iterator(); iterator.hasNext();) {
			Societa society = (Societa) iterator.next();
			if ("selected".equals(society.getSelected())){
				ruolo.setSocieta(society);
			}
		}
    	if (null == ruolo.getSocieta()) ruolo.setSocieta(new Societa());
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if(checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.ruolo.asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("ruolo", "list_of_struttur");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("ruolo", response);		
			return "showdoc@ruolo@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setRuolo(Ruolo ruolo) {
		this.ruolo = ruolo;
	}

	public Ruolo getRuolo() {
		return this.ruolo;
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


	public void setSocieta(List<Societa> societa) {
		this.societa = societa;
	}

	public List<Societa> getSocieta() {
		return societa;
	}
	
	public void societaValueChange(ValueChangeEvent vce) {  
        String societa_name = (String)vce.getNewValue();
        for (Iterator<Societa> iterator = societa.iterator(); iterator.hasNext();) {
			Societa society = (Societa) iterator.next();
			if (society.getText().equals(societa_name) || (societa_name == null && society.getText().length() == 0)) {
				ruolo.setSocieta(society);
			}
		}
        
    }
	
	public boolean checkRequiredField(){
		boolean result = false;
		if (ruolo.getNome() == null || "".equals(ruolo.getNome().trim())){
			this.setErrorMessage("templateForm:ruolo_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.name") + "'");
			result = true;
		}
		if (ruolo.getCod_amm() == null || "".equals(ruolo.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:ruolo_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (ruolo.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:ruolo_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (ruolo.getCod_aoo() == null || "".equals(ruolo.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:ruolo_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (ruolo.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:ruolo_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}
		return result;
	}
}
