package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclDocEditFormsAdapter;
import it.tredi.dw4.acl.model.Aoo;
import it.tredi.dw4.acl.model.GestoreMailbox;
import it.tredi.dw4.acl.model.Interoperabilita;
import it.tredi.dw4.acl.model.Mailbox_archiviazione;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class DocEditModifyAoo extends AclDocEdit {
	
	private String xml = "";
	
	private AclDocEditFormsAdapter formsAdapter;
	private Aoo aoo;
	
	private boolean adminAcl = false;
	
	// mbernardini 20/11/2014 : definizione della protocollazione automatica per PEC contenenti fatturePA
	private boolean abilitaFatturePA = false;
	// mbernardini 19/01/2015 : determina se e' attiva o meno la gestione dello split degli allegati per le mail di archiviazione/interoperabilita'
	private boolean abilitaSplitAttachmentsSuMailStorage = false;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	
	public DocEditModifyAoo() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.aoo = new Aoo();
    	this.aoo.init(domDocumento);    			
    	
    	UserBean user = getUserBean();
    	String matricola = user.getMatricola();
    	String emailUff = XMLUtil.parseStrictAttribute(domDocumento, "/response/@actionOnPecAddress");
    	String codUff = XMLUtil.parseStrictAttribute(domDocumento, "/response/@actionOnPecCodUff");
    	this.adminAcl = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(domDocumento, "/response/@adminAcl"));
    	
    	// mbernardini 15/10/2015 : gestori della mailbox
    	if (this.aoo.getMailbox_archiviazione() != null) {
	    	for (int i=0; i<this.aoo.getMailbox_archiviazione().size(); i++) {
	    		this.checkActionOnArchiviazione(this.aoo.getMailbox_archiviazione().get(i), this.adminAcl, matricola, codUff, emailUff);
	    	}
    	}
    	if (this.aoo.getInteroperabilita() != null) {
	    	for (int i=0; i<this.aoo.getInteroperabilita().size(); i++) {
	    		this.checkActionOnInteroperabilita(this.aoo.getInteroperabilita().get(i), this.adminAcl, matricola, codUff, emailUff);
	    	}
    	}
    	
    	this.abilitaFatturePA = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(domDocumento, "/response/@abilitaFatturePA"));
    	this.abilitaSplitAttachmentsSuMailStorage = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(domDocumento, "/response/@abilitaSplitAttachmentsSuMailStorage"));
    }	
	
	public AclDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String saveDocument() throws Exception {
		try {
			if (checkRequiredField()) return null;
			formsAdapter.getDefaultForm().addParams(this.aoo.asFormAdapterParams(""));
			
			XMLDocumento response = super._saveDocument("aoo", "list_of_struttur");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			buildSpecificShowdocPageAndReturnNavigationRule("aoo", response);		
			return "showdoc@aoo@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}

	public void setAoo(Aoo profilo) {
		this.aoo = profilo;
	}

	public Aoo getAoo() {
		return this.aoo;
	}
	
	public boolean isAdminAcl() {
		return adminAcl;
	}

	public void setAdminAcl(boolean adminAcl) {
		this.adminAcl = adminAcl;
	}
	
	public boolean isAbilitaFatturePA() {
		return abilitaFatturePA;
	}

	public void setAbilitaFatturePA(boolean abilitaFatturePA) {
		this.abilitaFatturePA = abilitaFatturePA;
	}
	
	public boolean isAbilitaSplitAttachmentsSuMailStorage() {
		return abilitaSplitAttachmentsSuMailStorage;
	}

	public void setAbilitaSplitAttachmentsSuMailStorage(
			boolean abilitaSplitAttachmentsSuMailStorage) {
		this.abilitaSplitAttachmentsSuMailStorage = abilitaSplitAttachmentsSuMailStorage;
	}
	
	@Override
	public String clearDocument() throws Exception {
		try {
			XMLDocumento response = super._clearDocument();
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			buildSpecificShowdocPageAndReturnNavigationRule("aoo", response);
			return "showdoc@aoo@reload";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
			
	}
	public String lookupInteroperabilitaDocumentModel() throws Exception {
		Interoperabilita interop = (Interoperabilita) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("interop");
		int num = (aoo.getInteroperabilita().contains(interop)) ? aoo.getInteroperabilita().indexOf(interop): 0;
		String value = (aoo.getInteroperabilita()!= null && !"".equals(aoo.getInteroperabilita().get(num))) ? aoo.getInteroperabilita().get(num).getDocumentModel() : "";

		String aliasName = "docmodelname";
		String aliasName1 = "";
		String titolo = "xml,/documentModel/@name"; //titolo 
		String ord = "xml(xpart:/documentModel/@name)"; //ord 
		String campi = ".interoperabilita["+num+"].@documentModel=xml,/documentModel/@name"; //campi
		String xq = "NOT([/documentModel/@type/]=\"webmail\")"; //xq 
		String db = ""; //db 
		String newRecord = ""; //newRecord 
		callLookup(aoo, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearInteroperabilitaDocumentModel() throws Exception{
		Interoperabilita interop = (Interoperabilita) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("interop");
		int num = (aoo.getInteroperabilita().contains(interop)) ? aoo.getInteroperabilita().indexOf(interop): 0;
		String campi = ".interoperabilita["+num+"].@documentModel=xml,/documentModel/@name"; //campi
		return clearField(campi, aoo);
	}
	
	public String lookupMailboxDocumentModel() throws Exception {
		Mailbox_archiviazione mailbox = (Mailbox_archiviazione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int num = (aoo.getMailbox_archiviazione().contains(mailbox)) ? aoo.getMailbox_archiviazione().indexOf(mailbox): 0;
		String value = (aoo.getMailbox_archiviazione()!= null && !"".equals(aoo.getMailbox_archiviazione().get(num))) ? aoo.getMailbox_archiviazione().get(num).getDocumentModel() : "";

		String aliasName = "docmodelname";
		String aliasName1 = "";
		String titolo = "xml,/documentModel/@name"; //titolo 
		String ord = "xml(xpart:/documentModel/@name)"; //ord 
		String campi = ".mailbox_archiviazione["+num+"].@documentModel=xml,/documentModel/@name"; //campi
		String xq = "NOT([/documentModel/@type/]=\"webmail\")"; //xq 
		String db = ""; //db 
		String newRecord = ""; //newRecord 
		callLookup(aoo, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	public String clearMailboxDocumentModel() throws Exception{
		Mailbox_archiviazione mailbox = (Mailbox_archiviazione) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("mailbox");
		int num = (aoo.getMailbox_archiviazione().contains(mailbox)) ? aoo.getMailbox_archiviazione().indexOf(mailbox): 0;
		String campi = ".mailbox_archiviazione["+num+"].@documentModel=xml,/documentModel/@name"; //campi
		return clearField(campi, aoo);
	}
	
	/**
	 * Lookup su persone interne per selezione dei gestori delle mailbox di archiviazione
	 */
	public String lookupGestoreArchiviazione() throws Exception {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		int[] posizioneGestore = getIndexGestoreOnArchiviazione(gestore);
		String value = aoo.getMailbox_archiviazione().get(posizioneGestore[0]).getGestoriMailbox().get(posizioneGestore[1]).getNome_pers();
		
		if (checkRequiredField()) { // verifico i campi obbligatori in modo da essere sicuro che il cod amm e il cod aoo siano compilati 
			aoo.getMailbox_archiviazione().get(posizioneGestore[0]).getGestoriMailbox().get(posizioneGestore[1]).setNome_pers("");
			return null; 
		}
		
		String xq = "[persint_codammaoo]=" + aoo.getCod_amm() + aoo.getCod_aoo();
		xq = "(" + xq + ") AND [/persona_interna/@tipo]=\"&null;\"";
		
		String aliasName 	= "persint_nomcogn";
		String aliasName1 	= "persint_cognome";
		String titolo 		= "xml,/persona_interna/@cognome xml,/persona_interna/@nome &quot;- ~&quot; XML,/persona_interna/recapito/email/@addr &quot;- ~&quot; XML,/persona_interna/@soprannome";
		String ord 			= "xml(xpart:/persona_interna/@cognome)";
		String campi 		= "mailbox_archiviazione["+posizioneGestore[0]+"].gestoriMailbox["+posizioneGestore[1]+"].nome_pers=xml,/persona_interna/@cognome xml,/persona_interna/@nome"
								+ " ; mailbox_archiviazione["+posizioneGestore[0]+"].gestoriMailbox["+posizioneGestore[1]+"].@matricola=xml,/persona_interna/@matricola";
		String db 			= "";
		String newRecord	= "";
		
		callLookup(aoo, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	/**
	 * Clear lookup su persone interne per selezione dei gestori delle mailbox di archiviazione
	 */
	public String clearLookupGestoreArchiviazione() throws Exception {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		int[] posizioneGestore = getIndexGestoreOnArchiviazione(gestore);
		String campi = "mailbox_archiviazione["+posizioneGestore[0]+"].gestoriMailbox["+posizioneGestore[1]+"].nome_pers=xml,/persona_interna/@cognome xml,/persona_interna/@nome"
						+ " ; mailbox_archiviazione["+posizioneGestore[0]+"].gestoriMailbox["+posizioneGestore[1]+"].@matricola=xml,/persona_interna/@matricola";
		return clearField(campi, aoo); 
	}
	
	/**
	 * Ritorna la posizione del gestore all'interno dell'elenco delle caselle di archiviazione
	 */
	private int[] getIndexGestoreOnArchiviazione(GestoreMailbox gestore) {
		int[] posizioneGestore = new int[2];
		for (int i=0; i<aoo.getMailbox_archiviazione().size(); i++) {
			Mailbox_archiviazione archiviazione = aoo.getMailbox_archiviazione().get(i);
			if (archiviazione != null && archiviazione.getGestoriMailbox().contains(gestore)) {
				posizioneGestore[0] = i;
				posizioneGestore[1] = archiviazione.getGestoriMailbox().indexOf(gestore);
			}
		}
		return posizioneGestore;
	}
	
	/**
	 * Lookup su persone interne per selezione dei gestori delle mailbox di interoperabilita'
	 */
	public String lookupGestoreInteroperabilita() throws Exception {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		int[] posizioneGestore = getIndexGestoreOnInteroperabilita(gestore);
		String value = aoo.getInteroperabilita().get(posizioneGestore[0]).getGestoriMailbox().get(posizioneGestore[1]).getNome_pers();
		
		if (checkRequiredField()) {
			// verifico i campi obbligatori in modo da essere sicuro che il cod amm e il cod aoo siano compilati
			aoo.getInteroperabilita().get(posizioneGestore[0]).getGestoriMailbox().get(posizioneGestore[1]).setNome_pers("");
			return null; 
		}
		
		String xq = "[persint_codammaoo]=" + aoo.getCod_amm() + aoo.getCod_aoo();
		xq = "(" + xq + ") AND [/persona_interna/@tipo]=\"&null;\"";
		
		String aliasName 	= "persint_nomcogn";
		String aliasName1 	= "persint_cognome";
		String titolo 		= "xml,/persona_interna/@cognome xml,/persona_interna/@nome &quot;- ~&quot; XML,/persona_interna/recapito/email/@addr &quot;- ~&quot; XML,/persona_interna/@soprannome";
		String ord 			= "xml(xpart:/persona_interna/@cognome)";
		String campi 		= "interoperabilita["+posizioneGestore[0]+"].gestoriMailbox["+posizioneGestore[1]+"].nome_pers=xml,/persona_interna/@cognome xml,/persona_interna/@nome"
								+ " ; interoperabilita["+posizioneGestore[0]+"].gestoriMailbox["+posizioneGestore[1]+"].@matricola=xml,/persona_interna/@matricola";
		String db 			= "";
		String newRecord	= "";
		
		callLookup(aoo, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		return null;
	}
	
	/**
	 * Clear lookup su persone interne per selezione dei gestori delle mailbox di interoperabilita'
	 */
	public String clearLookupGestoreInteroperabilita() throws Exception {
		GestoreMailbox gestore = (GestoreMailbox) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("gestore");
		int[] posizioneGestore = getIndexGestoreOnInteroperabilita(gestore);
		String campi = "interoperabilita["+posizioneGestore[0]+"].gestoriMailbox["+posizioneGestore[1]+"].nome_pers=xml,/persona_interna/@cognome xml,/persona_interna/@nome"
						+ " ; interoperabilita["+posizioneGestore[0]+"].gestoriMailbox["+posizioneGestore[1]+"].@matricola=xml,/persona_interna/@matricola";
		return clearField(campi, aoo); 
	}
	
	/**
	 * Ritorna la posizione del gestore all'interno dell'elenco delle caselle di interoperabilita'
	 */
	private int[] getIndexGestoreOnInteroperabilita(GestoreMailbox gestore) {
		int[] posizioneGestore = new int[2];
		for (int i=0; i<aoo.getInteroperabilita().size(); i++) {
			Interoperabilita interoperabilita = aoo.getInteroperabilita().get(i);
			if (interoperabilita != null && interoperabilita.getGestoriMailbox().contains(gestore)) {
				posizioneGestore[0] = i;
				posizioneGestore[1] = interoperabilita.getGestoriMailbox().indexOf(gestore);
			}
		}
		return posizioneGestore;
	}
	
	public boolean checkRequiredField(){
		boolean result = false;
		if (aoo.getNome() == null || "".equals(aoo.getNome().trim())) {
			this.setErrorMessage("templateForm:aoo_nome", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.name") + "'");
			result = true;
		}
		if (aoo.getCod_amm() == null || "".equals(aoo.getCod_amm().trim())) {
			this.setErrorMessage("templateForm:aoo_codamm", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_amm") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAmm
			if (aoo.getCod_amm().length() != Const.COD_AMM_LENGTH) {
				this.setErrorMessage("templateForm:aoo_codamm", I18N.mrs("acl.si_prega_di_inserire_4_caratteri_nel_campo_cod_amm"));
				result = true;
			}
		}
		if (aoo.getCod_aoo() == null || "".equals(aoo.getCod_aoo().trim())) {
			this.setErrorMessage("templateForm:aoo_codaoo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("acl.cod_aoo") + "'");
			result = true;
		}
		else {
			// Controllo la lunghezza del campo codAoo
			if (aoo.getCod_aoo().length() != Const.COD_AOO_LENGTH) {
				this.setErrorMessage("templateForm:aoo_codaoo", I18N.mrs("acl.si_prega_di_inserire_3_caratteri_nel_campo_cod_aoo"));
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Verifica se l'utente corrente possiede i diritti di intervento sulla mailbox di archiviazione (aggiornamento dati, cambio password, 
	 * ecc.). La verifica avviene nel modo seguente:
	 * 1) l'utente corrente e' amministratore di ACL
	 * 2) la matricola dell'utente corrisponde a quella indicata per un responsabile di una PEC; 
	 * 3) il coduff dell'ufficio di appartenenza dell'utente corrisponde a quello indicato per un responsabile di una PEC;
	 * 4) l'indirizzo pec dell'ufficio di appartenenza dell'utente corrisponde a quello indicato su una PEC;
	 * 5) l'utente corrente e' presente nella lista di gestori della mailbox
	 */
	private void checkActionOnArchiviazione(Mailbox_archiviazione archiviazione, boolean adminAcl, String matricola, String codUff, String emailUff) {
		if (archiviazione != null) {
			boolean update = false;
			boolean changePwd = false;
			
			if (adminAcl
					|| (matricola != null && matricola.length() > 0 && matricola.equals(archiviazione.getResponsabile().getMatricola()))
					|| (codUff != null && codUff.length() > 0 && codUff.equals(archiviazione.getResponsabile().getCod_uff()))
					|| (emailUff != null && emailUff.length() > 0 && emailUff.equals(archiviazione.getMailbox().getEmail()))) {
				update = true;
				changePwd = true;
			}
			else {
				if (archiviazione.getGestoriMailbox() != null && archiviazione.getGestoriMailbox().size() > 0) {
					int index = 0;
					while (index < archiviazione.getGestoriMailbox().size() && !(update || changePwd)) {
						GestoreMailbox gestore = archiviazione.getGestoriMailbox().get(index);
						if (gestore != null && gestore.getMatricola() != null && gestore.getMatricola().equals(matricola)) {
							changePwd = true;
							if (gestore.getLivello() != null && gestore.getLivello().equals(GestoreMailbox.LIVELLO_TITOLARE))
								update = true;
						}
						index++;
					}
				}
			}
			
			archiviazione.setUpdateMailbox(update);
			archiviazione.setChangePassword(changePwd);
		}
	}
	
	/**
	 * Verifica se l'utente corrente possiede i diritti di intervento sulla mailbox di interoperabilita' (aggiornamento dati, cambio password, 
	 * ecc.). La verifica avviene nel modo seguente:
	 * 1) l'utente corrente e' amministratore di ACL
	 * 2) la matricola dell'utente corrisponde a quella indicata per un responsabile di una PEC; 
	 * 3) il coduff dell'ufficio di appartenenza dell'utente corrisponde a quello indicato per un responsabile di una PEC;
	 * 4) l'indirizzo pec dell'ufficio di appartenenza dell'utente corrisponde a quello indicato su una PEC (mail in scaricamento);
	 * 5) l'utente corrente e' presente nella lista di gestori della mailbox
	 */
	private void checkActionOnInteroperabilita(Interoperabilita interoperabilita, boolean adminAcl, String matricola, String codUff, String emailUff) {
		if (interoperabilita != null) {
			boolean update = false;
			boolean changePwd = false;
			
			if (adminAcl
					|| (matricola != null && matricola.length() > 0 && matricola.equals(interoperabilita.getResponsabile().getMatricola()))
					|| (codUff != null && codUff.length() > 0 && codUff.equals(interoperabilita.getResponsabile().getCod_uff()))
					|| (emailUff != null && emailUff.length() > 0 && emailUff.equals(interoperabilita.getMailbox_in().getEmail()))) {
				update = true;
				changePwd = true;
			}
			else {
				if (interoperabilita.getGestoriMailbox() != null && interoperabilita.getGestoriMailbox().size() > 0) {
					int index = 0;
					while (index < interoperabilita.getGestoriMailbox().size() && !(update || changePwd)) {
						GestoreMailbox gestore = interoperabilita.getGestoriMailbox().get(index);
						if (gestore != null && gestore.getMatricola() != null && gestore.getMatricola().equals(matricola)) {
							changePwd = true;
							if (gestore.getLivello() != null && gestore.getLivello().equals(GestoreMailbox.LIVELLO_TITOLARE))
								update = true;
						}
						index++;
					}
				}
			}
			
			interoperabilita.setUpdateMailbox(update);
			interoperabilita.setChangePassword(changePwd);
		}
	}

}
