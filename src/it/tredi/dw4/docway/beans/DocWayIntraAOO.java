package it.tredi.dw4.docway.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.doc.adapters.IntraAooDocumentFormsAdapter;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.docway.model.intraaoo.IntraAooOption;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;

/**
 * Pannello (modale) di comunicazione intra-aoo (protocollazione di documenti su aoo esterna)
 */
public class DocWayIntraAOO extends Page {

	private IntraAooDocumentFormsAdapter formsAdapter;
	private Map<String, IntraAooOption> aoos;
	
	private boolean active = false;
	
	// cod_amm_aoo selezionata dall'operatore ed eventuale RPA da settare sul nuovo protocollo
	private String selectedAoo;
	private Rif assegnazioneRPA = new Rif();
	
	public DocWayIntraAOO(XMLDocumento containerResponse, List<IntraAooOption> options) throws Exception {
		this.formsAdapter = new IntraAooDocumentFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
		this.formsAdapter.fillFormsFromResponse(containerResponse);
		
		aoos = new HashMap<String, IntraAooOption>();
		for (int i=0; i<options.size(); i++) {
			IntraAooOption option = options.get(i);
			if (option != null) {
				aoos.put(option.getCodAmmAoo(), option);
				
				// assegno il primo AOO presente nell'elenco
				if (selectedAoo == null || selectedAoo.isEmpty())
					selectedAoo = option.getCodAmmAoo();
			}
		}
		
		this.active = true;
	}
	
	@Override
	public IntraAooDocumentFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public Map<String, IntraAooOption> getAoos() {
		return aoos;
	}

	public void setAoos(Map<String, IntraAooOption> aoos) {
		this.aoos = aoos;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String getSelectedAoo() {
		return selectedAoo;
	}

	public void setSelectedAoo(String selectedAoo) {
		this.selectedAoo = selectedAoo;
	}

	public Rif getAssegnazioneRPA() {
		return assegnazioneRPA;
	}

	public void setAssegnazioneRPA(Rif assegnazioneRpa) {
		this.assegnazioneRPA = assegnazioneRpa;
	}
	
	/**
	 * Salvataggio del documento su nuovo protocollo
	 * @return
	 * @throws Exception
	 */
	public String confirm() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			getFormsAdapter().saveIntraAoo(selectedAoo, assegnazioneRPA);
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response, Const.MSG_LEVEL_ERROR)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			// reload del documento in showdoc
			close();
			String page = buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("oldTable"), response);
			return page + "@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Chiusura del pannello (operazione annullata)
	 * @return
	 * @throws Exception
	 */
	public String close() throws Exception {
		this.active = false;
		setSessionAttribute("docWayIntraAOO", null);
		return null;
	}
	
	/**
	 * Lookup su ufficio RPA in comunicazione intra-aoo
	 * @return
	 * @throws Exception
	 */
	public String lookupUfficio_rpaIntraAoo() throws Exception {
		try {
			String value = (assegnazioneRPA != null && assegnazioneRPA.getNome_uff() != null) ? assegnazioneRPA.getNome_uff() : "";
			
			clearUfficio_rpaIntraAoo();
			getFormsAdapter().lookupUfficio(selectedAoo, value);
			XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
			if (handleErrorResponse(response, Const.MSG_LEVEL_ERROR)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			return parseLookupResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Elaborazione di una risposta derivante da un lookup su intra-aoo
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private String parseLookupResponse(XMLDocumento response) throws Exception {
		DocWayRifintIntraAooLookup rifintIntraAooLookup = new DocWayRifintIntraAooLookup(assegnazioneRPA);
		rifintIntraAooLookup.getFormsAdapter().fillFormsFromResponse(response);
		rifintIntraAooLookup.init(response.getDocument());
		
		if(rifintIntraAooLookup.getPageCount() == 1 && rifintIntraAooLookup.getTitoli().size() == 1)
			rifintIntraAooLookup.confirm(rifintIntraAooLookup.getTitoli().get(0));
		else
			rifintIntraAooLookup.setActive(true);
		
		setSessionAttribute("rifintIntraAooLookup", rifintIntraAooLookup);
		
		return null;
	}
	
	/**
	 * Pulizia lookup su ufficio RPA in comunicazione intra-aoo
	 * @return
	 * @throws Exception
	 */
	public String clearUfficio_rpaIntraAoo() throws Exception {
		clearAssegnatarioRPA();
		return null;
	}
	
	/**
	 * Lookup su persona RPA in comunicazione intra-aoo
	 * @return
	 * @throws Exception
	 */
	public String lookupPersona_rpaIntraAoo() throws Exception {
		try {
			String value = (assegnazioneRPA != null && assegnazioneRPA.getNome_persona() != null) ? assegnazioneRPA.getNome_persona() : "";
			
			clearPersona_rpaIntraAoo();
			getFormsAdapter().lookupPersona(selectedAoo, value, assegnazioneRPA.getCod_uff());
			XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
			if (handleErrorResponse(response, Const.MSG_LEVEL_ERROR)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			return parseLookupResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Pulizia lookup su persona RPA in comunicazione intra-aoo
	 * @return
	 * @throws Exception
	 */
	public String clearPersona_rpaIntraAoo() throws Exception {
		assegnazioneRPA.setNome_persona("");
		assegnazioneRPA.setCod_persona("");
		return null;
	}
	
	/**
	 * Lookup su ruolo RPA in comunicazione intra-aoo
	 * @return
	 * @throws Exception
	 */
	public String lookupRuolo_rpaIntraAoo() throws Exception {
		try {
			String value = (assegnazioneRPA != null && assegnazioneRPA.getNome_uff() != null) ? assegnazioneRPA.getNome_uff() : "";
			
			clearRuolo_rpaIntraAoo();
			getFormsAdapter().lookupRuolo(selectedAoo, value);
			XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
			if (handleErrorResponse(response, Const.MSG_LEVEL_ERROR)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			return parseLookupResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Pulizia lookup su ruolo RPA in comunicazione intra-aoo
	 * @return
	 * @throws Exception
	 */
	public String clearRuolo_rpaIntraAoo() throws Exception {
		clearAssegnatarioRPA();
		return null;
	}
	
	/**
	 * Azzeramento dell'assegnatario settato causato dalla selezione di una nuova AOO
	 */
	public String clearAssegnatarioAfterAooChange() {
		clearAssegnatarioRPA();
		return null;
	}
	
	/**
	 * Azzeramento dell'assegnatario settato
	 */
	private void clearAssegnatarioRPA() {
		assegnazioneRPA.setCod_uff("");
		assegnazioneRPA.setNome_uff("");
		assegnazioneRPA.setNome_persona("");
		assegnazioneRPA.setCod_persona("");
	}
	
	/**
	 * Ritorna la persona di default da settare come RPA per l'AOO selezionata
	 * @return
	 */
	public String getDefaultNomePersonaRPA() {
		String value = "";
		if (selectedAoo != null && !selectedAoo.isEmpty() && aoos != null) {
			IntraAooOption aoo = aoos.get(selectedAoo);
			if (aoo != null)
				value = aoo.getNomePersonaResponsabile();
		}
		return value;
	}
	
	/**
	 * Ritorna l'ufficio di default da settare come RPA per l'AOO selezionata
	 * @return
	 */
	public String getDefaultNomeUfficioRPA() {
		String value = "";
		if (selectedAoo != null && !selectedAoo.isEmpty() && aoos != null) {
			IntraAooOption aoo = aoos.get(selectedAoo);
			if (aoo != null)
				value = aoo.getNomeUfficioResponsabile();
		}
		return value;
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	public boolean checkRequiredField() {
		boolean result = false;
		
		boolean defaultRPA = false;
		if ((getDefaultNomeUfficioRPA() != null && !getDefaultNomeUfficioRPA().isEmpty()) || (getDefaultNomePersonaRPA() != null && !getDefaultNomePersonaRPA().isEmpty()))
			defaultRPA = true;
		
		// Controllo su proprietario specificato
		if ((assegnazioneRPA.getCod_uff() == null || assegnazioneRPA.getCod_uff().isEmpty()) 
				&& (assegnazioneRPA.getCod_persona() == null || assegnazioneRPA.getCod_persona().isEmpty()) 
				&& !defaultRPA) {
			String[] fieldIds = { "templateForm:rpaIntraAoo_nome_uff_input", "templateForm:rpaIntraAoo_nome_persona_input", "templateForm:rpaIntraAoo_nome_ruolo_input" };
			this.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_valorizzare_il_campo_proprietario"));
			result = true;
		}
		
		// Controllo se il proprietario e' stato compilato correttamente
		if (assegnazioneRPA.getCod_uff() != null && !assegnazioneRPA.getCod_uff().isEmpty() 
				&& !assegnazioneRPA.getTipo_uff().equals("ruolo") 
				&& (assegnazioneRPA.getCod_persona() == null || assegnazioneRPA.getCod_persona().isEmpty())) {
			this.setErrorMessage("templateForm:rpaIntraAoo_nome_persona_input",  I18N.mrs("dw4.occorre_valorizzare_il_campo_proprietario"));
			result = true;
		}
		
		return result;
	}
	
}
