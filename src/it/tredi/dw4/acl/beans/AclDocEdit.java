package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.addons.BaseAddOn;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;

import java.util.List;

import org.dom4j.Document;

public abstract class AclDocEdit extends AclPage {
	
	public abstract void init(Document dom);
	
	public abstract DocEditFormsAdapter getFormsAdapter();
	
	public abstract String saveDocument() throws Exception;
	
	public abstract String clearDocument() throws Exception;
	
	protected XMLDocumento _saveDocument(String pne, String pnce) throws Exception {
		List<BaseAddOn> addons = getAspects();
		if (addons != null && addons.size() > 0) {
			for (int i=0; i<addons.size(); i++) {
				BaseAddOn addon = (BaseAddOn) addons.get(i);
				if (addon != null)
					getFormsAdapter().getDefaultForm().addParams(addon.asFormAdapterParams());
			}
		}
		
		getFormsAdapter().saveDocument(pne, pnce);
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	protected XMLDocumento _clearDocument() throws Exception {
		List<BaseAddOn> addons = getAspects();
		if (addons != null && addons.size() > 0) {
			for (int i=0; i<addons.size(); i++) {
				BaseAddOn addon = (BaseAddOn) addons.get(i);
				if (addon != null)
					addon.clear();
			}
		}
		
		getFormsAdapter().clearDocument();
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	protected XMLDocumento _lookup(String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) throws Exception {
		getLookup().cleanFields(campi);
		getFormsAdapter().lookup(aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		
		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
		return response;
	}
	
	protected XMLDocumento _thVincolato(String numTitles4Page, String fieldName, String radice, String relazione, String chiave, String insRight, String delRight, String windowTitle, boolean iscascade, String value) throws Exception {
		getThvincolato().cleanFields(fieldName);
		getFormsAdapter().thVincolato(numTitles4Page, fieldName, radice, relazione, chiave, insRight, delRight, windowTitle, iscascade, value);
		
		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
		return response;		
	}
	
	public void callLookup(XmlEntity entity, String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) throws Exception {
		try {
			AclLookup aclLookup = new AclLookup();
			setLookup(aclLookup);
			aclLookup.setModel(entity);
	
			XMLDocumento response = this._lookup(aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
			if (handleErrorResponse(response, Const.MSG_LEVEL_ERROR)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return;
			}
			aclLookup.getFormsAdapter().fillFormsFromResponse(response);
			aclLookup.init(response.getDocument());
			
			if(aclLookup.getTitoli().size() == 1 && value != null && value.length() > 0){
				aclLookup.confirm(aclLookup.getTitoli().get(0));
			}
			else {
				aclLookup.setActive(true);
				
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;			
		}
	}
	
	public void callThesaurus(XmlEntity entity, String numTitles4Page, String fieldName, String radice, String relazione, String chiave, String insRight, String delRight,String windowTitle, String value) throws Exception {
		try {
			AclThvincolato aclThvincolato = new AclThvincolato();
			setThvincolato(aclThvincolato);
			aclThvincolato.setModel(entity);
		
			XMLDocumento response = this._thVincolato(numTitles4Page, fieldName, radice, relazione, chiave, insRight, delRight, windowTitle, false, value);
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return;
			}
			aclThvincolato.getFormsAdapter().fillFormsFromResponse(response);		
			aclThvincolato.init(response.getDocument());
			if ( aclThvincolato.getTitoli().size() == 1 && value.length() > 0)
				aclThvincolato.confirm(aclThvincolato.getTitoli().get(0));
			else
				aclThvincolato.setActive(true);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;			
		}
	}

	@Override
	public AclThvincolato getThvincolato() {
		return (AclThvincolato)super.getThvincolato();
	}	
	
	@Override
	public AclLookup getLookup() {
		return (AclLookup)super.getLookup();
	}
	
	public String clearField(String campi, XmlEntity entity) throws Exception{
		AclLookup aclLookup = new AclLookup();
		aclLookup.setModel(entity);
		aclLookup.cleanFields(campi);
		return null;
	}
	
	public void callLookupComune(XmlEntity entity, String campi, String value) throws Exception{
		String aliasName 	= "comuni_nome";
		String aliasName1 	= "";
		String titolo 		= "xml,/comune/@nome &quot;^ (&quot; xml,/comune/@prov &quot;^)&quot; &quot;^ ~&quot; XML,/comune/@cap";
		String ord 			= "xml(xpart:/comune/@nome),xml(xpart:/comune/@cap)";
		String xq			= "";
		String db 			= "";
		String newRecord	= ";dbTable=comune;fillField=comune.@nome;rightCode=ACL-4";
		
		callLookup(entity, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
	}
	
	public void callLookupCap(XmlEntity entity, String campi, String value) throws Exception{
		String aliasName 	= "comuni_cap";
		String aliasName1 	= "";
		String titolo 		= "xml,/comune/@nome &quot;^ (&quot; xml,/comune/@prov &quot;^)&quot; &quot;^ ~&quot; XML,/comune/@cap";
		String ord 			= "xml(xpart:/comune/@nome),xml(xpart:/comune/@cap)";
		String xq			= "";
		String db 			= "";
		String newRecord	= ";dbTable=comune;fillField=comune.@cap;rightCode=ACL-4";
		
		callLookup(entity, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
	}
	
	public void callThVincolato(XmlEntity entity, String fieldName, String chiave, String value) throws Exception {
		String numTitles4Page = "DEF";
		String radice = "Radice";
		String relazione = "7";
		String insRight = "ACL-14";
		String delRight = "ACL-15";
		String windowTitle = "";
		
		callThesaurus(entity, numTitles4Page, fieldName, radice, relazione, chiave, insRight, delRight, windowTitle, value);
	}


}
