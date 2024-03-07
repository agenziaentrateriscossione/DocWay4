package it.tredi.dw4.acl.beans;

import it.tredi.dw4.beans.Showdoc;
import it.tredi.dw4.utils.XMLDocumento;

public abstract class AclShowdoc extends Showdoc {
	
	public String paginaTitoli() throws Exception {
		XMLDocumento response = this._paginaTitoli();	
		
		AclTitles titles = (AclTitles)getSessionAttribute("titles");		
		
		titles.getFormsAdapter().fillFormsFromResponse(response);
		titles.init(response.getDocument());
		
		return "acl_showtitles";		
	}
	
	public String modifyTableDoc() throws Exception {
		XMLDocumento response = super._modifyTableDoc();
		
		if (handleErrorResponse(response)) {
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
		buildSpecificDocEditModifyPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response, false);
		return "docEdit@modify"+response.getRootElement().attributeValue("dbTable");
	}
}
