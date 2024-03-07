package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;

import javax.faces.context.FacesContext;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

/**
 * Pagina di visualizzazione di fascicoli degli studenti
 */
public class ShowdocFascicoloStudente extends ShowdocFascicolo {

	public ShowdocFascicoloStudente() throws Exception {
		super();
	}

	@Override
	public void reload() throws Exception {
		try{
			super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/showdoc@fascicolo@studente");
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
	}
	
	/**
	 * inserimento di un nuovo fascicolo degli studenti
	 * @return
	 * @throws Exception
	 */
	@Override
	public String insTableDocFascicolo() throws Exception {
		try {
			getFormsAdapter().insTableDoc("fascicolo@studente");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule("fascicolo", "", "", "@studente", response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * pulsante ripeti nuovo del fascicolo degli studenti
	 * @return
	 * @throws Exception
	 */
	@Override
	public String ripetiNuovo() throws Exception{
		try {
			getFormsAdapter().ripetiNuovoSottoFasc("fascicolo@studente", getFascicolo().getNumero(), -1, "", "", "", "", getFascicolo().getClassif().getText(), getFascicolo().getClassif().getCod(), "");
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule("fascicolo", "", "", "@studente", response, isPopupPage());
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
	@Override
	public String insSottofascicolo() throws Exception{
		try {
			getFormsAdapter().insertSottoFasc(getFascicolo().getNumero(), this.getFormsAdapter().getDefaultForm().getParam("physDoc"), "", "", "", "", getFascicolo().getClassif().getText(), getFascicolo().getClassif().getCod(), "", getFascicolo().getCodiceFascicoloCustom(), getFascicolo().getDescrizioneFascicoloCustom());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			return buildSpecificDocEditPageAndReturnNavigationRule("fascicolo", "", "", "@studente", response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
}
