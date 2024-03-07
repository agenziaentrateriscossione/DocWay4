package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;

public class ShowdocFascicoloPersonale extends ShowdocFascicolo {
	
	private boolean active; 

	public ShowdocFascicoloPersonale() throws Exception {
		super();
	}
	
	@Override
	public void init(Document dom) {
		this.active = XMLUtil.parseStrictAttribute(dom, "/response/funzionalita_disponibili/@alberoFascicoliAttivo").equals("true");
		super.init(dom);
	}

	@Override
	public void reload() throws Exception {
		try{
			super._reload(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/docway/showdoc@fascicolo@personale");
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
		}
	}
	
	/**
	 * inserimento di un nuovo fascicolo del personale
	 * @return
	 * @throws Exception
	 */
	@Override
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
	 * @return
	 * @throws Exception
	 */
	@Override
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
	@Override
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
	
	// ERM015806
	// carica folder tree completo
	public String showFolderTree(String fid) throws Exception {
		try {
			
			// prendi tutto fino al secondo '.' per avere radice
			String[] tmp = getFascicolo().getNumero().split("\\.");
			if(tmp.length < 2) {
				throw new Exception("Codice fascicolo errarto: " + fid);
			}
			getFormsAdapter().loadFolderTree(tmp[0] + "." + tmp[1]);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return buildSpecificShowdocPageAndReturnNavigationRule("fascicolo", "", "", "@albero", response, false); // no popup
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}

	public boolean isActive() {
		return active;
	}	
}
