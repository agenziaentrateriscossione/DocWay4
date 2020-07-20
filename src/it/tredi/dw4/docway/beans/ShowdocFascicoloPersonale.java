package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

public class ShowdocFascicoloPersonale extends ShowdocFascicolo {

	public ShowdocFascicoloPersonale() throws Exception {
		super();
	}

	@Override
	public void reload() throws Exception {
		try{
			super._reload("showdoc@fascicolo@personale");
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
	}
	
	/**
	 * inserimento di un nuovo fascicolo del personale
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insTableDocFascicolo() throws Exception {
		try {
			getFormsAdapter().insTableDoc("fascicolo@personale");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule("fascicolo", "", "", "@personale", response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * pulsante ripeti nuovo del fascicolo del personale
	 * 
	 * @return
	 * @throws Exception
	 */
	public String ripetiNuovo() throws Exception{
		try {
			getFormsAdapter().ripetiNuovoSottoFasc("fascicolo@personale", getFascicolo().getNumero(), -1, "", "", "", "", getFascicolo().getClassif().getText(), getFascicolo().getClassif().getCod(), "");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule("fascicolo", "", "", "@personale", response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * inserimento di un nuovo sottofascicolo
	 * @return
	 * @throws Exception
	 */
	public String insSottofascicolo() throws Exception{
		try {
			getFormsAdapter().insertSottoFasc(getFascicolo().getNumero(), this.getFormsAdapter().getDefaultForm().getParam("physDoc"), "", "", "", "", getFascicolo().getClassif().getText(), getFascicolo().getClassif().getCod(), "", getFascicolo().getCodiceFascicoloCustom(), getFascicolo().getDescrizioneFascicoloCustom());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule("fascicolo", "", "", "@personale", response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
}
