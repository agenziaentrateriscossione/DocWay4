package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Doc;
import it.tredi.dw4.docway.model.Email;
import it.tredi.dw4.docway.model.EmailRecipient;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class ResocontoDocAssegnati extends DocWayDocedit {
	private DocDocWayDocEditFormsAdapter formsAdapter;
	private String xml = "";
	
	private Doc doc = new Doc();
	private Email email = new Email();
	
	public ResocontoDocAssegnati() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		this.xml = dom.asXML();
		
		this.doc.init(dom);
		
		this.email.setMittente(XMLUtil.parseStrictElement(dom, "/response/mittente"));
		this.email.setCorpo(XMLUtil.parseStrictElement(dom, "/response/body"));
		this.email.setOggetto(this.doc.getOggetto());
		
		if (this.email.getDestinatario_int().size() == 0)
			this.email.getDestinatario_int().add(new EmailRecipient());
		if (this.email.getDestinatario_est().size() == 0)
			this.email.getDestinatario_est().add(new EmailRecipient());
	}
	
	@Override
	public DocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public Doc getDoc() {
		return doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}
	
	@Override
	public String saveDocument() throws Exception {
		return null;
	}
	
	@Override
	public String clearDocument() throws Exception {
		return null;
	}
	
	/**
	 * Invio della mail ai destinatari selezionati
	 * 
	 * @return
	 * @throws Exception
	 */
	public String inviaEmail() throws Exception {
		try {
			if (checkRequiredField()) return null;
			
			// TODO attualmente su docway4 i tiff vengono gia' gestiti come multipagina (eventualmente andrebbe spostato lato client il codice sotto)
			/*
			if (typeof(document.IW) != 'undefined') {
		        // c'è IW --> verifico se occorre generare un tif multipagina delle immagini allegate
		        var chkboxMultiPagTif = getElementByName('genera_tiff_multipagina');
		
		        if (chkboxMultiPagTif != null && chkboxMultiPagTif.checked) {
		            // occorre generare un tif multipagina delle immagini allegate
		            multiPagTif = generateMultiPageTif();
		
		            if (multiPagTif.length > 0) {
		                // upload sul server del tif generato
		                var bits = document.IW.uploadDelete;   // cancella i file locali dopo il trasferimento
		
		                var url = buildUrlForGet("", true, false, true) + "&verbo=Iw_response&xverb=tmpUpload&fileExt=.tif";
		                //alert(url);
		
		                var resp = document.IW.UploadParam(getHost(),
		                                                   url,
		                                                   window.location.port,
		                                                   'filename',
		                                                   multiPagTif,
		                                                   bits,
		                                                   document.IW.zCompressionNone,
		                                                   "uploadMultiPagTif");
		
		                if (resp == '1') {
		                    multiPagTif = document.IW.buffer;
		                    multiPagTif = replaceAll(multiPagTif, "<--remotefile:", "");
		                    multiPagTif = replaceAll(multiPagTif, ";-->", "");
		                    //alert(multiPagTif);
		                }
		                else {
		                    // errore nell'upload --> esco
		                    alert(document.IW.buffer);
		                    CloseWaitMsg();
		                    return false;
		                }
		            }
		        }
			}
			*/
			
			/*
			// indico l'eventuale tif multipagina generato e caricato sul server in una cartella temporanea
    		var hxpS = new _hxpSession(getForm('hxpForm'));
    		hxpS.setParameter("multiPagTif", multiPagTif);
			 */
			
			formsAdapter.getDefaultForm().addParams(this.email.asFormAdapterParams(""));
			formsAdapter.inviaEmailResocontoDocAssegnati();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			ShowdocNotifDiff showdocNotifDiff = new ShowdocNotifDiff();
			showdocNotifDiff.getFormsAdapter().fillFormsFromResponse(response);
			showdocNotifDiff.init(response.getDocument());
			showdocNotifDiff.setShowSxCol(false);
			setSessionAttribute("showdocNotifDiff", showdocNotifDiff);
			
			return "showdoc@notif_diff";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Controllo dei campi obbligatori del form di invio della mail
	 * @return
	 */
	private boolean checkRequiredField() {
		boolean result = false;
		
		// controllo sul valore del campo oggetto
		if (email.getOggetto() == null || email.getOggetto().equals("")) {
			this.setErrorMessage("templateForm:mailSubject", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.object") + "'");
			result = true;
		}
		
		// almeno un destinatario valorizzato e tutti gli indirizzi email 
		// dei destinatari validi
		boolean destinatarioExists = false;
		if (email.getDestinatario_int() != null && email.getDestinatario_int().size() > 0) {
			for (int i=0; i<email.getDestinatario_int().size(); i++) {
				if (email.getDestinatario_int().get(i) != null && email.getDestinatario_int().get(i).getName() != null 
						&& !email.getDestinatario_int().get(i).getName().equals("")) {
					destinatarioExists = true;
					
					if (!validateEmailAddress(email.getDestinatario_int().get(i))) {
						this.setErrorMessage("templateForm:destInt:" + i + ":destinatarioInt_input", I18N.mrs("dw4.occorre_indicare_un_indirizzo_email_valido"));
						result = true;
					}
				}
			}
		}
		if (email.getDestinatario_est() != null && email.getDestinatario_est().size() > 0) {
			for (int i=0; i<email.getDestinatario_est().size(); i++) {
				if (email.getDestinatario_est().get(i) != null && email.getDestinatario_est().get(i).getName() != null 
						&& !email.getDestinatario_est().get(i).getName().equals("")) {
					destinatarioExists = true;
					
					if (!validateEmailAddress(email.getDestinatario_est().get(i))) {
						this.setErrorMessage("templateForm:destEst:" + i + ":destinatarioEst_input", I18N.mrs("dw4.occorre_indicare_un_indirizzo_email_valido"));
						result = true;
					}
				}
			}
		}
		
		if (!destinatarioExists) {
			this.setErrorMessage("", I18N.mrs("dw4.deve_essere_indicato_almeno_un_destinatario"));
			result = true;
		}
		
		return result;
	}
	
	/**
	 * Verifica se il destinatario passato fa riferimento ad un indirizzo
	 * email valido
	 */
	private boolean validateEmailAddress(EmailRecipient rec) {
		boolean validAddress = false;
		
		if (rec != null && rec.getFull_email() != null && !rec.getFull_email().equals("")) {
			int start = rec.getFull_email().lastIndexOf("<");
			int end = rec.getFull_email().lastIndexOf(">");
			if (start != -1 && end != -1) {
				String[] addresses = rec.getFull_email().substring(start+1, end).split(";");
				if (addresses != null && addresses.length > 0) {
					for (int i=0; i<addresses.length; i++)
						if (StringUtil.isValidEmailAddress(addresses[i]))
							validAddress = true;
				}
			}
		}
		
		return validAddress;
	}
	
	/**
	 * Aggiunta di un destinatario interno
	 * @return
	 * @throws Exception
	 */
	public String addDestinatarioInt() throws Exception {
		EmailRecipient destinatario = (EmailRecipient) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("destInt");
		
		int index = 0;
		if (destinatario != null)
			index = email.getDestinatario_int().indexOf(destinatario);
		
		if (email.getDestinatario_int() != null) {
			if (email.getDestinatario_int().size() > index)
				email.getDestinatario_int().add(index+1, new EmailRecipient());
			else
				email.getDestinatario_int().add(new EmailRecipient());
		}
		
		return null;
	}
	
	/**
	 * Eliminazione di un destinatario interno
	 * @return
	 * @throws Exception
	 */
	public String deleteDestinatarioInt() throws Exception {
		EmailRecipient destinatario = (EmailRecipient) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("destInt");
		if (destinatario != null) {
			email.getDestinatario_int().remove(destinatario);
			if (email.getDestinatario_int().isEmpty()) 
				email.getDestinatario_int().add(new EmailRecipient());
		}
		return null;
	}

	/**
	 * Aggiunta di un destinatario esterno
	 * @return
	 * @throws Exception
	 */
	public String addDestinatarioEst() throws Exception {
		EmailRecipient destinatario = (EmailRecipient) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("destEst");
		
		int index = 0;
		if (destinatario != null)
			index = email.getDestinatario_est().indexOf(destinatario);
		
		if (email.getDestinatario_est() != null) {
			if (email.getDestinatario_est().size() > index)
				email.getDestinatario_est().add(index+1, new EmailRecipient());
			else
				email.getDestinatario_est().add(new EmailRecipient());
		}
		
		return null;
	}
	
	/**
	 * Eliminazione di un destinatario esterno
	 * @return
	 * @throws Exception
	 */
	public String deleteDestinatarioEst() throws Exception {
		EmailRecipient destinatario = (EmailRecipient) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("destEst");
		if (destinatario != null) {
			email.getDestinatario_est().remove(destinatario);
			if (email.getDestinatario_est().isEmpty()) 
				email.getDestinatario_est().add(new EmailRecipient());
		}
		return null;
	}
	
	/**
	 * Lookup su destinatario interno
	 * @return
	 * @throws Exception
	 */
	public String lookupDestinatarioInt() throws Exception {
		EmailRecipient destinatario = (EmailRecipient) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("destInt");
		int num = (email.getDestinatario_int().contains(destinatario)) ? email.getDestinatario_int().indexOf(destinatario) : 0;
		
		String value = email.getDestinatario_int().get(num).getFull_email();

		String aliasName 	= "persint_nomcogn";
		String aliasName1 	= "";
		String titolo 		= "xml,/persona_interna/@cognome xml,/persona_interna/@nome \"^ <~\" xml,/persona_interna/recapito/email/@addr \"~^>\""; //titolo 
		String ord 			= "xml(xpart:/persona_interna/@cognome)"; //ord
		String campiLookup	= ".destinatario_int[" + num + "].name=xml,/persona_interna/@cognome xml,/persona_interna/@nome" +
								" ; .destinatario_int[" + num + "].email=xml,/persona_interna/recapito/email/@addr" +
								" ; .destinatario_int[" + num + "].full_email=xml,/persona_interna/@cognome xml,/persona_interna/@nome \"^ <~\" xml,/persona_interna/recapito/email/@addr \"~^>\"";
		String db 			= getFormsAdapter().getDefaultForm().getParam("aclDb"); //db 
		String newRecord 	= ""; //newRecord 
		String xq			= ""; //extraQuery
		
		callLookup(email, aliasName, aliasName1, titolo, ord, campiLookup, xq, db, newRecord, value);
		
		return null;
	}
	
	/** 
	 * Clear di lookup su destinatario interno
	 * @return
	 * @throws Exception
	 */
	public String clearLookupDestinatarioInt() throws Exception {
		EmailRecipient destinatario = (EmailRecipient) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("destInt");
		int num = (email.getDestinatario_int().contains(destinatario)) ? email.getDestinatario_int().indexOf(destinatario) : 0;
		
		String campi = ".destinatario_int[" + num + "].name=xml,/persona_interna/@cognome xml,/persona_interna/@nome" +
						" ; .destinatario_int[" + num + "].email=xml,/persona_interna/recapito/email/@addr" +
						" ; .destinatario_int[" + num + "].full_email=xml,/persona_interna/@cognome xml,/persona_interna/@nome \"^ <~\" xml,/persona_interna/recapito/email/@addr \"~^>\"";
		return clearField(campi, email);
	}
	
	/**
	 * Lookup su destinatario esterno
	 * @return
	 * @throws Exception
	 */
	public String lookupDestinatarioEst() throws Exception {
		EmailRecipient destinatario = (EmailRecipient) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("destEst");
		int num = (email.getDestinatario_est().contains(destinatario)) ? email.getDestinatario_est().indexOf(destinatario) : 0;
		
		String value = email.getDestinatario_est().get(num).getFull_email();

		String aliasName 	= "persest_nomcogn";
		String aliasName1 	= "persest_cognome";
		String titolo 		= "xml,/persona_esterna/@cognome xml,/persona_esterna/@nome \"^ <~\" xml,/persona_esterna/recapito/email/@addr \"~^>\""; //titolo 
		String ord 			= "xml(xpart:/persona_esterna/@cognome),xml(xpart:/persona_esterna/@nome)"; //ord
		String campiLookup	= ".destinatario_est[" + num + "].name=xml,/persona_esterna/@cognome xml,/persona_esterna/@nome" +
								" ; .destinatario_est[" + num + "].email=xml,/persona_esterna/recapito/email/@addr" +
								" ; .destinatario_est[" + num + "].full_email=xml,/persona_esterna/@cognome xml,/persona_esterna/@nome \"^ <~\" xml,/persona_esterna/recapito/email/@addr \"~^>\"";
		String db 			= getFormsAdapter().getDefaultForm().getParam("aclDb"); //db 
		String newRecord 	= ""; //newRecord 
		String xq			= "";  //extraQuery
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("acl_ext_aoo_restriction", false))
			xq				= "[struest_codammaoo]=" + getDoc().getCod_amm_aoo() + " OR [persest_codammaoo]=" + getDoc().getCod_amm_aoo();
		
		callLookup(email, aliasName, aliasName1, titolo, ord, campiLookup, xq, db, newRecord, value);
		
		return null;
	}
	
	/** 
	 * Clear di lookup su destinatario esterno
	 * @return
	 * @throws Exception
	 */
	public String clearLookupDestinatarioEst() throws Exception {
		EmailRecipient destinatario = (EmailRecipient) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("destEst");
		int num = (email.getDestinatario_est().contains(destinatario)) ? email.getDestinatario_est().indexOf(destinatario) : 0;
		
		String campi = ".destinatario_est[" + num + "].name=xml,/persona_esterna/@cognome xml,/persona_esterna/@nome" +
						" ; .destinatario_est[" + num + "].email=xml,/persona_esterna/recapito/email/@addr" +
						" ; .destinatario_est[" + num + "].full_email=xml,/persona_esterna/@cognome xml,/persona_esterna/@nome xml,/persona_esterna/recapito/email/@addr";
		return clearField(campi, email);
	}
	
}
