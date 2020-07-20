package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.beans.Page;

public abstract class AclPage extends Page {
	
	public String buildTitlePageAndReturnNavigationRule(XMLDocumento response) throws Exception{
		AclTitles titles = new AclTitles();
		titles.getFormsAdapter().fillFormsFromResponse(response);
		titles.init(response.getDocument());
		
		titles.loadAspects(response.getAttributeValue("/response/@dbTable"), response, "showtitles");
		titles.loadButtons(response.getAttributeValue("/response/@dbTable"), response, "showtitles");
		
		setSessionAttribute("titles", titles);
		
		return "acl_showtitles";
	}
	
	/**
	 * Recupera il codice della sede (codammaoo) dalla sessione. Il codice rappresenta la sede attualmente selezionata (in caso di utente senza restrizione)
	 */
	public String getCurrentCodSedeRestriction() {
		return (String) getSessionAttribute("codSedeRestriction");
	}
	
	/**
	 * Registra in sessione il codice della sede (codammaoo) selezionata (in caso di operatore privo di restrizione)
	 * @param codSede
	 */
	public void setCurrentCodSedeRestriction(String codSede) {
		if (codSede != null && codSede.length() > 0)
			setSessionAttribute("codSedeRestriction", codSede);
	}
}
