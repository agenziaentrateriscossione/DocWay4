package it.tredi.dw4.beans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.acl.beans.DelegheTitles;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayDocedit;
import it.tredi.dw4.docway.model.Delega;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

/**
 *
 * Classe per la gestione delle deleghe
 * @author tiommi
 *
 */

public class DelegheOptions extends DocWayDocedit {

	private List<Delega> delegheDisponibili = new ArrayList<Delega>(); // deleghe che l'utente corrente può utilizzare
	private List<Delega> delegheAffidate = new ArrayList<Delega>(); // lista delle deleghe create dall'utente corrente
	private Delega delegaForm = new Delega();
	private boolean popupNewDelegaActive = false;
	private boolean popupEditDelegaActive = false;
	private DocEditFormsAdapter formsAdapter;
	private String xml;
	private String matricolaUtenteLoggato; // per controllo su auto-delega
	private int delegheResidue; // per controllo su numero deleghe
	private String matricolaRiferimento; // per sapere di chi si stanno controllando le deleghe, se vuota si tratta dell'utente corrente
	
	private boolean fromAcl = false;

	public Delega getDelegaForm() {
		return delegaForm;
	}

	public void setDelegaForm(Delega delegaForm) {
		this.delegaForm = delegaForm;
	}

	public List<Delega> getDelegheDisponibili() {
		return delegheDisponibili;
	}

	public void setDelegheDisponibili(List<Delega> delegheDisponibili) {
		this.delegheDisponibili = delegheDisponibili;
	}

	public List<Delega> getDelegheAffidate() {
		return delegheAffidate;
	}

	public void setDelegheAffidate(List<Delega> delegheAffidate) {
		this.delegheAffidate = delegheAffidate;
	}

	public boolean isPopupNewDelegaActive() {
		return popupNewDelegaActive;
	}

	public void setPopupNewDelegaActive(boolean popupNewDelegaActive) {
		this.popupNewDelegaActive = popupNewDelegaActive;
	}

	public boolean isPopupEditDelegaActive() {
		return popupEditDelegaActive;
	}

	public void setPopupEditDelegaActive(boolean popupEditDelegaActive) {
		this.popupEditDelegaActive = popupEditDelegaActive;
	}

	public String getMatricolaUtenteLoggato() {
		return matricolaUtenteLoggato;
	}

	public void setMatricolaUtenteLoggato(String matricolaUtenteLoggato) {
		this.matricolaUtenteLoggato = matricolaUtenteLoggato;
	}

	public int getDelegheResidue() {
		return delegheResidue;
	}

	public void setDelegheResidue(int delegheResidue) {
		this.delegheResidue = delegheResidue;
	}
	
	public String getMatricolaRiferimento() {
		return matricolaRiferimento;
	}

	public void setMatricolaRiferimento(String matricolaRiferimento) {
		this.matricolaRiferimento = matricolaRiferimento;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public DelegheOptions(String service) throws Exception {
		this.formsAdapter = new DocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration(service));
		// imposta controlli adhoc per il validatore (no controllo su autodelega)
		if (service.equalsIgnoreCase("aclService")) 
			fromAcl = true;
	}

	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.setXml(dom.asXML());

		String xpath = "/response/deleghe/";

		this.delegheDisponibili = XMLUtil.parseSetOfElement(dom, xpath+"from/delega", new Delega());
		this.delegheAffidate = XMLUtil.parseSetOfElement(dom, xpath+"to/delega", new Delega());
		this.matricolaUtenteLoggato = XMLUtil.parseStrictAttribute(dom, "/response/@matricola", "");
		this.popupNewDelegaActive = false;
		this.popupEditDelegaActive = false;
		this.delegheResidue = Integer.parseInt(XMLUtil.parseStrictAttribute(dom, "/response/@maxDeleghe", "5")) - delegheAffidate.size();

		this.delegaForm.getRifDelegati().clear();
		this.delegaForm.getRifDelegati().add(new Rif());
		
		this.matricolaRiferimento = XMLUtil.parseStrictAttribute(dom, xpath+"@cod_riferimento", "");
	}

	public DocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	/**
	 * metodo di reload
	 * @throws Exception
	 */
	public String reload() throws Exception {
		try {
			formsAdapter.reloadDelegheOptions(matricolaRiferimento);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			this.cleanBean();
			this.getFormsAdapter().fillFormsFromResponse(response);
			this.init(response.getDocument());

			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	/**
	 * pulizia del bean per refresh della pagina
	 */
	private void cleanBean() {
		this.delegheAffidate.clear();
		this.delegheDisponibili.clear();
	}

	/**
	 * metodo che apre la modale per la selezione di una nuova delega
	 */
	public void goToNewDelega() {
		this.popupNewDelegaActive = true;
	}

	/**
	 * 
	 * @param delegaType - tipo: 'to' = delegato, 'from' = delegante
	 * @return
	 * @throws Exception
	 */
	public String confirmDelega() throws Exception {

		// validazione
		if (checkRequiredField()) return null;

		if (delegaForm.getType().equalsIgnoreCase("to"))
			this.formsAdapter.confirmDelega(delegaForm, matricolaRiferimento);
		else if (delegaForm.getType().equalsIgnoreCase("from")) 
			this.formsAdapter.editDelegante(delegaForm, matricolaRiferimento);
		this.formsAdapter.getDefaultForm().executePOST(getUserBean());

		this.popupNewDelegaActive = false;
		this.popupEditDelegaActive = false;

		// resetta la XmlEntity pulendo il form di inserimento
		delegaForm.clean();

		// refresh
		reload();

		return null;
	}

	/**
	 * rimozione della delega selezionata
	 * @throws Exception
	 */
	public String removeDelega() throws Exception {
		return removeDelega("to");
	}
	public String removeDelega(String type) throws Exception {

		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
		if (type.equalsIgnoreCase("to"))
			this.formsAdapter.removeDelega(delega, matricolaRiferimento);
		else if (type.equalsIgnoreCase("from")) 
			this.formsAdapter.removeDelegante(delega, matricolaRiferimento);
		
		this.formsAdapter.getDefaultForm().executePOST(getUserBean());

		//refresh
		reload();

		return null;
	}

	/**
	 * modifica della delega selezionata
	 * @return
	 */
	public String editDelega(String delegaType) {

		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");

		//popola il rif per sistemare tutti gli input del template dei rifinterni per rendere possibile la modifica
		Rif rif = new Rif();
		rif.setNome_persona(delega.getNomePersona());
		rif.setNome_uff(delega.getNomeUfficio());
		rif.setCod_persona(delega.getCodPersona());
		rif.setCod_uff(delega.getCodUfficio());
		this.delegaForm.getRifDelegati().clear();
		this.delegaForm.getRifDelegati().add(0, rif);

		//reformatta de date
		delegaForm.setInizio(delega.dataInizioToString());
		delegaForm.setFine(delega.dataFineToString());

		//flags
		delegaForm.setPermanente(delega.isPermanente());
		delegaForm.setUneditable(delega.isUneditable());
		delegaForm.setAttiva(delega.isAttiva());
		delegaForm.setSostituto(delega.isSostituto());
		
		//tipo di delega
		delegaForm.setType(delegaType);
		
		this.popupEditDelegaActive = true;
		return null;
	}

	/**
	 * toggle del flag sostituto per la delega selezionata
	 * @throws Exception
	 */
	public String toggleSostituto() throws Exception {
		Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
		this.formsAdapter.toggleSostituto(delega, this.matricolaRiferimento);

		this.formsAdapter.getDefaultForm().executePOST(getUserBean());

		//refresh
		reload();

		return null;

	}

	private boolean checkRequiredField() {
		boolean result = false;
		boolean maxAlreadyNotified = false;
		boolean duplicatoAlreadyNotified = false;

		// Controllo almeno un delegato
		List<Rif> delegati = delegaForm.getRifDelegati();
		// (null || vuota || solo elementi empty)
		if(delegati == null || delegati.isEmpty() || countNotEmpty(delegati) == 0) {
			String[] fieldIds = { "templateForm:rifint_delega_list:0:delegato_nome_uff_input", "templateForm:rifint_delega_list:0:delegato_nome_persona_input"};
			this.setErrorMessage(fieldIds, I18N.mrs("dw4.occorre_inserire_almeno_un_delegato"));
			result = true;
		}

		// Controllo che non ci sia il rif relativo all'utente correntemente loggato o che non sia già stato inserito e che il numero delle nuove deleghe non superi il limite
		if(!popupEditDelegaActive) {
			int countNewDeleghe = 0;
			for(int i = 0; i < delegati.size(); i++) {
				// rif correntemente loggato
				Rif delegato = delegati.get(i);
				if (getMatricolaUtenteLoggato().equals(delegato.getCod_persona()) && !fromAcl) {
					String[] fieldIds = { "templateForm:rifint_delega_list:"+i+":delegato_nome_uff_input", "templateForm:rifint_delega_list:"+i+":delegato_nome_persona_input"};
					this.setErrorMessage(fieldIds, I18N.mrs("dw4.impossibile_autodelega"));
					result = true;
				}
				if (fromAcl && matricolaRiferimento.equals(delegato.getCod_persona())) {
					String[] fieldIds = { "templateForm:rifint_delega_list:"+i+":delegato_nome_uff_input", "templateForm:rifint_delega_list:"+i+":delegato_nome_persona_input"};
					this.setErrorMessage(fieldIds, I18N.mrs("dw4.impossibile_autodelega"));
					result = true;
				}
				else {
					if(!duplicatoAlreadyNotified) {
						boolean alreadyDelegato = isAlreadyDelegato(delegato.getCod_persona());
						if(alreadyDelegato) {
							String[] fieldIds = { "templateForm:rifint_delega_list:"+i+":delegato_nome_uff_input", "templateForm:rifint_delega_list:"+i+":delegato_nome_persona_input"};
							this.setErrorMessage(fieldIds, I18N.mrs("dw4.delega_gia_presente"));
							result = true;
							duplicatoAlreadyNotified = true;
						}
					}
					if(delegato.getCod_persona() != null && !delegato.getCod_persona().isEmpty())
						countNewDeleghe++;
					if(countNewDeleghe > delegheResidue && !maxAlreadyNotified) {
						this.setErrorMessage("", I18N.mrs("dw4.superato_numero_massimo_deleghe") + " " + delegheResidue);
						result = true;
						maxAlreadyNotified = true;
					}
				}
			}
		}
		// Controllo su date
		boolean permanente = delegaForm.isPermanente();
		String startDate = delegaForm.getInizio();
		String endDate = delegaForm.getFine();
		LocalDate startLocalDate = delegaForm.inizioToLocalDate();
		LocalDate endLocalDate = delegaForm.fineToLocalDate();
		if(!permanente) {
			if(startDate == null || startDate.isEmpty()) {
				this.setErrorMessage("templateForm:durataDelega_from", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.durata_inizio") + "'");
				result = true;
			}
			if(endDate == null || endDate.isEmpty()) {
				this.setErrorMessage("templateForm:durataDelega_to", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.durata_fine") + "'");
				result = true;
			}
			if(startLocalDate != null && endLocalDate != null && startLocalDate.isAfter(endLocalDate)) {
				String[] fieldIds = { "templateForm:durataDelega_from", "templateForm:durataDelega_to"};
				this.setErrorMessage(fieldIds, I18N.mrs("dw4.la_data_di_inzio_e_di_fine_risultano_invertite"));
				result = true;
			}

		}

		return result;
	}

	/**
	 * Conta il numero dei Rif non vuoti
	 * @param delegati - lista dei rif inseriti dall'utente
	 * @return
	 */
	private int countNotEmpty(List<Rif> delegati) {
		int result = 0;
		for(Rif rif : delegati) {
			if(rif.getCod_persona() != null && !rif.getCod_persona().isEmpty())
				result++;
		}
		return result;
	}

	/**
	 * Verifica che la matricola non sia già stata inserita tra le deleghe affidate
	 * @param matricola - matricola del nuovo delegato
	 * @return
	 */
	private boolean isAlreadyDelegato(String matricola) {
		for(Delega delegaAffidata : delegheAffidate) {
			if(delegaAffidata.getCodPersona().equals(matricola))
				return true;
		}
		return false;
	}

	/**
	 * Chiusura modale per la selezione o modifica della delega e clean dei relativi campi
	 */
	public void closeDelega() {
		this.popupNewDelegaActive = false;
		this.popupEditDelegaActive = false;

		// resetta la XmlEntity pulendo il form di inserimento
		delegaForm.clean();
	}

	/**
	 * Metodo per l'aggiornamento delle date a seconda del flag permanente
	 */
	public void updateDataInput() {
		if(this.delegaForm.isPermanente()) {
			this.delegaForm.setInizio(null);
			this.delegaForm.setFine(null);
		}
	}

	/**
	 * Cancellazione di un rif int dalla lista dei nuovi delegati
	 */
	public void deleteRifInt_delegato(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		this.delegaForm.getRifDelegati().remove(rif);
		if(this.delegaForm.getRifDelegati().isEmpty())
			this.delegaForm.getRifDelegati().add(new Rif());
	}

	/**
	 * Aggiunta di un rif int nella lista dei nuovi delegati
	 */
	public void addRifInt_delegato(){
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int index = 0;
		if (rif != null)
			index =this.delegaForm.getRifDelegati().indexOf(rif);

		Rif rifToAdd = new Rif();

		if (this.delegaForm.getRifDelegati().size() > index)
			this.delegaForm.getRifDelegati().add(index+1, rifToAdd);
		else
			this.delegaForm.getRifDelegati().add(rifToAdd);
	}

	/**
	 * RifintLookup sul delegato
	 *
	 * @throws Exception
	 */
	public void lookupPersona_delegato() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (this.delegaForm.getRifDelegati().contains(rif)) ? this.delegaForm.getRifDelegati().indexOf(rif) : 0;
		Rif delegato = this.delegaForm.getRifDelegati().get(num);

		String value = (delegato != null) ? delegato.getNome_uff() : "";
		String value2 = (delegato != null) ? delegato.getNome_persona() : "";
		String campi = 	".rifDelegati["+num+"].@nome_uff" +
						"|.rifDelegati["+num+"].@nome_persona" +
						"|.rifDelegati["+num+"].@cod_uff" +
						"|.rifDelegati["+num+"].@cod_persona";

		rifintLookupPersona(delegaForm, value, value2, campi);
		rif.setUfficio_completo(false);
	}

	/**
	 * RifintLookup su ufficio delegato
	 *
	 * @throws Exception
	 */
	public void lookupUfficio_delegato() throws Exception {
		Rif rif = (Rif) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("rif");
		int num = (this.delegaForm.getRifDelegati().contains(rif)) ? this.delegaForm.getRifDelegati().indexOf(rif) : 0;
		Rif delegato = this.delegaForm.getRifDelegati().get(num);

		String value = (delegato != null) ? delegato.getNome_uff() : "";
		String value2 = (delegato != null) ? delegato.getNome_persona() : "";
		String campi = 	".rifDelegati["+num+"].@nome_uff" +
						"|.rifDelegati["+num+"].@nome_persona" +
						"|.rifDelegati["+num+"].@cod_uff" +
						"|.rifDelegati["+num+"].@cod_persona";

		rifintLookupUfficio(delegaForm, value, value2, campi);
	}
	
	/**
	 * Ritorno alla pagina dei titles deleghe
	 * 
	 * @throws Exception
	 */
	public String paginaTitoli() throws Exception {
		try {
			formsAdapter.backToPaginaTitoli();
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean()); 	
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			DelegheTitles delegheTitles = (DelegheTitles) getSessionAttribute("delegheTitles");
			delegheTitles.getFormsAdapter().fillFormsFromResponse(response);
			delegheTitles.init(response.getDocument());
			return "acl_showdeleghetitles";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	// N.B. la classe docEdit è necessaria per il lookup e altre funzioni (non abbiamo un documento da salvare o pulire)
	@Override
	public String saveDocument() throws Exception {
		return null;
	}
	@Override
	public String clearDocument() throws Exception {
		return null;
	}

	@Override
	public XmlEntity getModel() {
		// TODO probabilmente non necessario per questo bean (utilizzato dai lookup su campi custom)
		return null;
	}
	
}
