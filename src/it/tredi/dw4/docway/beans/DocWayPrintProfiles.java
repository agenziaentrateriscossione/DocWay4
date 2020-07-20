package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.PrintProfilesFormsAdapter;
import it.tredi.dw4.beans.PrintProfiles;
import it.tredi.dw4.docway.adapters.DocWayPrintProfilesFormsAdapter;
import it.tredi.dw4.docway.model.Error_msg;
import it.tredi.dw4.docway.model.Print_Profile;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

public class DocWayPrintProfiles extends PrintProfiles {

	private DocWayPrintProfilesFormsAdapter formsAdapter;
	private String xml = "";
	
	private List<Error_msg> error_msg = new ArrayList<Error_msg>();
	private String typeSelection = ""; // assume i valori 'print_sel' e 'print_doc'
	private int profileSelectionIndex = 0;
	private List<Print_Profile> print_profiles = new ArrayList<Print_Profile>();
	
	public DocWayPrintProfiles() throws Exception {
		this.formsAdapter = new DocWayPrintProfilesFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		this.xml 			= dom.asXML();
	
		String selid = getSelid();
		if (selid != null && !selid.equals("")) {
			this.typeSelection = "print_sel";
		} else {
			String physDoc = getPhysDoc();
			if (physDoc != null && !physDoc.equals("")) {
				this.typeSelection = "print_doc";
			}
		}
		setVerbo(); // inizializzazione del verbo di stampa del profilo
		
		this.setPrint_profiles(XMLUtil.parseSetOfElement(dom, "/response/print_profile", new Print_Profile()));
		this.setError_msg(XMLUtil.parseSetOfElement(dom, "/response/error_msg", new Error_msg()));
		
		if (this.print_profiles != null && this.print_profiles.size() > 0) {
			for (int i=0; i<this.print_profiles.size(); i++) {
				Print_Profile cur = (Print_Profile) this.print_profiles.get(i);
				if (cur != null && cur.isDefaultProfile())
					this.profileSelectionIndex = i;
			}
		}
	}

	@Override
	public PrintProfilesFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public List<Print_Profile> getPrint_profiles() {
		return print_profiles;
	}

	public void setPrint_profiles(List<Print_Profile> print_profiles) {
		this.print_profiles = print_profiles;
	}

	public List<Error_msg> getError_msg() {
		return error_msg;
	}

	public void setError_msg(List<Error_msg> error_msg) {
		this.error_msg = error_msg;
	}
	
	public String getSelid() {
		return formsAdapter.getDefaultForm().getParam("selid");
	}
	
	public String getPhysDoc() {
		return formsAdapter.getDefaultForm().getParam("physDoc");
	}
	
	public String getTypeSelection() {
		return typeSelection;
	}

	public void setTypeSelection(String typeSelection) {
		this.typeSelection = typeSelection;
	}
	
	public int getProfileSelectionIndex() {
		return profileSelectionIndex;
	}

	public void setProfileSelectionIndex(int profileSelection) {
		this.profileSelectionIndex = profileSelection;
	}
	
	/**
	 * Ritorno alla pagina dei titoli
	 * 
	 * @throws Exception
	 */
	public String paginaTitoli() throws Exception {
		try {
			formsAdapter.paginaTitoli();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			DocWayTitles titles = new DocWayTitles();		
			titles.getFormsAdapter().fillFormsFromResponse(response);
			titles.init(response.getDocument());
			titles.setPopupPage(isPopupPage());
			setSessionAttribute("docwayTitles", titles);
			
			return "showtitles@docway";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Ritorna il template di profilo in base all'indice nella lista
	 * @return
	 */
	private String getProfileSelectionValue() {
		String value = "";
		if (profileSelectionIndex > -1 && profileSelectionIndex < print_profiles.size())
			value = print_profiles.get(profileSelectionIndex).getTemplate();
		
		return value;
	}
	
	/**
	 * Avvio della stampa
	 * 
	 * fgr 28/10/2004
	 * Funzione invocata per l'avviamento della stampa. In base al verbo impostato, prepara l'url
	 * per la stampa del singolo documento o della selezione corrente, aprendo una nuova finestra.
	 * Oltre ai valori di default 'print_selection','showdoc' e 'genericPrintHandler', vengono gestiti
	 * valori di verbo che contengono almeno un '.' (che indica la presenza di un package), cioe' valori
	 * che consentono di invocare una generica classe appartenente alla gerarchia di Response.
	 *	In questo caso la sintassi da utilizzare per il verbo è la seguente:
	 *	'package.nome_classe[|eventuale_xverb]'.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String startPrint() throws Exception {
		try {
			String printTemplate = getProfileSelectionValue() + "|"; // devo aggiungere un pipe altrimenti non viene considerato l'ultimo elemento (se vuoto)
			// controllo sul profilo di stampa
			if (printTemplate == null || printTemplate.equals("")) { // printTemplate??
				// Data di scadenza non valorizzata
				this.setErrorMessage("", I18N.mrs("dw4.profilo_di_stampa_vuoto_Impossibile_proseguire"));
				return null;
			}
			
			// controllo sul formato della tupla 'printTemplate'
			if (printTemplate.indexOf("|") != -1 && StringUtil.split(printTemplate, "|").length != 9) {
				// errore di sintassi nella tupla 'printTemplate'
				this.setErrorMessage("", I18N.mrs("dw4.errore_di_sintassi_nel_profilo_di_stampa_selezionato_Impossibile_proseguire"));
				return null;
			}
			
			// controllo sul verbo passato
			String verbo = formsAdapter.getDefaultForm().getParam("verbo");
			if (!verbo.equals("showdoc") && !verbo.equals("print_selection") && verbo.indexOf("genericPrintHandler") == -1 && verbo.indexOf(".") == -1) {
				this.setErrorMessage("", I18N.mrs("dw4.verbo") + " " + verbo + " " + I18N.mrs("dw4.non_gestito"));
				return null;
			}
			
			formsAdapter.startPrint(printTemplate);
			
			// TODO nella pagina di stampa profili non ci sono login e matricalo. Su Docway3 viene generato un URL in GET, su Docway4?
			UserBean userBean = getUserBean();
			formsAdapter.getDefaultForm().addParam("login", userBean.getLogin());
			formsAdapter.getDefaultForm().addParam("matricola", userBean.getMatricola());
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(userBean);
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());
			this.init(getFormsAdapter().getLastResponse().getDocument());
			
			verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) { // caricamento della loadingbar
				
				DocWayLoadingbar docWayLoadingbar = new DocWayLoadingbar();
				docWayLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				docWayLoadingbar.init(response);
				setLoadingbar(docWayLoadingbar);
				docWayLoadingbar.setActive(true);
			}
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Impostazione del verbo per la stampa in base al tipo di selezione richiesto
	 * 
	 * @return
	 * @throws Exception
	 */
	public String setVerbo() {
		if (typeSelection.equals("print_sel"))
			formsAdapter.getDefaultForm().addParam("verbo", "genericPrintHandler|@print_sel");
		else
			formsAdapter.getDefaultForm().addParam("verbo", "genericPrintHandler|@print_doc");
		return null;
	}
	
}
