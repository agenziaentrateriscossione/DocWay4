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

public class DocEditAoo extends AclDocEdit {
	private AclDocEditFormsAdapter formsAdapter;
	private Aoo aoo;
	
	// mbernardini 20/11/2014 : definizione della protocollazione automatica per PEC contenenti fatturePA
	private boolean abilitaFatturePA = false;
	// mbernardini 19/01/2015 : determina se e' attiva o meno la gestione dello split degli allegati per le mail di archiviazione/interoperabilita'
	private boolean abilitaSplitAttachmentsSuMailStorage = false;

	private String xml;
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml){
		this.xml = xml;
	}
	public DocEditAoo() throws Exception {
		this.formsAdapter = new AclDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	public void init(Document domDocumento) {
    	xml = domDocumento.asXML();
    	this.aoo = new Aoo();
    	this.aoo.init(domDocumento);
    	// Se non venirre riassegnato al punto '.' l'nrecord in questo punto verrebbe restituito errore in caso di 'ripeti nuovo'
    	this.aoo.setNrecord(".");
    	
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

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public Aoo getAoo() {
		return this.aoo;
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
			getFormsAdapter().fillFormsFromResponse(response);
			this.init(response.getDocument());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
		return null;
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

}
