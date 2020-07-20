package it.tredi.dw4.docway.beans;

import java.util.ArrayList;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.LookupFormsAdapter;
import it.tredi.dw4.beans.Lookup;
import it.tredi.dw4.docway.adapters.DocWayDelibereLookupFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.Campo;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

public class LookupSeduta extends Lookup {
	private DocWayDelibereLookupFormsAdapter formsAdapter;
	private ArrayList<Titolo> titoli;

	public LookupSeduta() throws Exception {
		this.formsAdapter = new DocWayDelibereLookupFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
    	this.setTitoli((ArrayList<Titolo>) XMLUtil.parseSetOfElement(dom, "//titolo", new Titolo()));
    	
    	//@ TODO la formattazione andrebbe spostata sul .xhtml 
    	for (Titolo titolo : titoli)
    	{
    		String tmp0 = titolo.getTesto().replace("Seduta del 12/12/9999", I18N.mrs("dw4.sospendi_proposta"));
    		String tmp1 = tmp0.replace("|si", " ("+I18N.mrs("dw4.straordinaria")+")");
    		String tmp2 = tmp1.replace("|no", "");
    		titolo.setTesto(tmp2);
    	}
    	
		this.setActive(true);
	}
	
	public String assegna() throws Exception{
		Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
		try{
			formsAdapter.confermaRinviaSeduta(setFields(titolo), titolo.getIndice());
			
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificShowdocPageAndReturnNavigationRule("seduta", response);
			setActive(false);
			return "showdoc@seduta@reload";
			
		}catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	private String setFields(Titolo titolo){
		String fieldsArray = "";
		for(Campo campo : titolo.getCampi()){
			fieldsArray += "'"+ StringUtil.quoteString(campo.getNome()) + "=" + campo.getText() + "',";
		}
		
		fieldsArray = fieldsArray.substring(0,fieldsArray.length()-1);
		return fieldsArray;
	}
	
	@Override
	public LookupFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	public void setFormsAdapter(DocWayDelibereLookupFormsAdapter formsAdapter) {
		this.formsAdapter = formsAdapter;
	}

	public ArrayList<Titolo> getTitoli() {
		return titoli;
	}

	public void setTitoli(ArrayList<Titolo> titoli) {
		this.titoli = titoli;
	}

}
