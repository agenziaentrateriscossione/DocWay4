package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.docway.adapters.DocWayWebmailFormsAdapter;
import it.tredi.dw4.docway.model.webmail.Filter;
import it.tredi.dw4.docway.model.webmail.Mailbox;
import it.tredi.dw4.docway.model.webmail.Message;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.dom4j.Document;

public class DocWayWebmail extends Page {

	private DocWayWebmailFormsAdapter formsAdapter;
	private String xml = "";
	
	private List<Mailbox> mailboxes = new ArrayList<Mailbox>();
	private Filter filter = new Filter();
	private List<Message> messages = new ArrayList<Message>();
	private int tot_messages = 0;
	private String current_folder = "";
	private String current_mailbox_mail = "";
	private String current_protocol = "";
	
	private String colSort = "date";
	private boolean ascSort = false;
	
	private boolean connected = false; // indica se si e' connessi ad una casella di posta o meno
	
	public DocWayWebmail() throws Exception {
		this.formsAdapter = new DocWayWebmailFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public DocWayWebmailFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public List<Mailbox> getMailboxes() {
		return mailboxes;
	}

	public void setMailboxes(List<Mailbox> mailboxes) {
		this.mailboxes = mailboxes;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public int getTot_messages() {
		return tot_messages;
	}

	public void setTot_messages(int tot_messages) {
		this.tot_messages = tot_messages;
	}

	public String getCurrent_folder() {
		return current_folder;
	}

	public void setCurrent_folder(String current_folder) {
		this.current_folder = current_folder;
	}
	
	public String getCurrent_mailbox_mail() {
		return current_mailbox_mail;
	}

	public void setCurrent_mailbox_mail(String current_mailbox_mail) {
		this.current_mailbox_mail = current_mailbox_mail;
	}
	
	public String getCurrent_protocol() {
		return current_protocol;
	}

	public void setCurrent_protocol(String current_protocol) {
		this.current_protocol = current_protocol;
	}
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public String getColSort() {
		return colSort;
	}

	public void setColSort(String colSort) {
		this.colSort = colSort;
	}

	public boolean isAscSort() {
		return ascSort;
	}

	public void setAscSort(boolean ascSort) {
		this.ascSort = ascSort;
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
    	xml 					= dom.asXML();
    	
    	mailboxes 				= XMLUtil.parseSetOfElement(dom, "/response/mailboxes/mailbox", new Mailbox());
    	String count 			= XMLUtil.parseStrictAttribute(dom, "/response/messages/@count");
    	if (StringUtil.isNumber(count))
    		tot_messages 		= new Integer(count).intValue();
    	messages 				= XMLUtil.parseSetOfElement(dom, "/response/messages/message", new Message());
    	
    	String sorttype 		= XMLUtil.parseStrictAttribute(dom, "/response/messages/@sorttype", "");
    	if (sorttype.equals("asc"))
    		ascSort = true;
    	else
    		ascSort	= false;
    	
    	filter.init(XMLUtil.createDocument(dom, "/response/messages/filter"));
    	if (filter.getType() == null || filter.getType().equals(""))
    		filter.setType("sentdate");
    	
    	current_mailbox_mail 	= formsAdapter.getDefaultForm().getParam("currentmailbox");
    	current_folder 			= formsAdapter.getDefaultForm().getParam("currentfolder");
    	current_protocol 		= formsAdapter.getDefaultForm().getParam("currentprotocol");
    	
    	connected 				= StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/mailboxes/@connected"));
    }
	
	/**
	 * lettura dell'attributo colSort passato come attributo attraverso
	 * un commandLink
	 * @param event
	 */
	public void attrListenerSortHeader(ActionEvent event){
		String colSelected = (String) event.getComponent().getAttributes().get("colSort");
		if (colSelected == null)
			colSelected = "";
		
		if (colSelected.equals(colSort)) {
			ascSort = !ascSort;
		}
		else {
			colSort = colSelected;
			if (colSort.equals("date"))
				ascSort = false;
			else
				ascSort = true;
		}
	}
	
	/**
	 * Filtro email su casella di posta
	 * 
	 * @return
	 * @throws Exception
	 */
	public String search() throws Exception {
		try {
			if (checkSearchFilter()) return null;
			
			formsAdapter.search(colSort, ascSort);
			formsAdapter.getDefaultForm().addParams(filter.asFormAdapterParams(""));
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			this.init(response.getDocument());
			this.formsAdapter.fillFormsFromResponse(response);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Caricamento di una nuova mailbox
	 * @return
	 * @throws Exception
	 */
	public String loadMailbox() throws Exception {
		try {
			formsAdapter.loadMailbox(current_mailbox_mail);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				// in caso di errore nel cambio di mailbox (probabile errore di connessione sulla nuova) vengono azzerati i messaggi
				// e il filtro perche' relativi alla vecchia mailbox (quella dalla quale si sta uscendo) 
				messages = new ArrayList<Message>();
				filter = new Filter();
				connected = false;
				
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			this.formsAdapter.fillFormsFromResponse(response);
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
	 * Caricamento dell'albero delle directory della casella di posta
	 * @return
	 * @throws Exception
	 */
	public String loadFoldersTree() throws Exception {
		try {
			formsAdapter.loadFoldersTree();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			DocWayWebmailFolders docwayWebmailFolders = new DocWayWebmailFolders();
			docwayWebmailFolders.getFormsAdapter().fillFormsFromResponse(response);
			docwayWebmailFolders.init(response.getDocument());
			docwayWebmailFolders.setShowfolders(true);
			setSessionAttribute("docwayWebmailFolders", docwayWebmailFolders);
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Logout dalla casella di posta
	 * @return
	 * @throws Exception
	 */
	public String logout() throws Exception {
		try {
			formsAdapter.logout();
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			this.init(response.getDocument());
			this.formsAdapter.fillFormsFromResponse(response);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Caricamento di un messaggio email
	 * @return
	 * @throws Exception
	 */
	public String loadMessage() throws Exception {
		Message message = (Message) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("message");
		return loadMessage(message);
	}
	
	/**
	 * Caricamento di un messaggio email
	 * @return
	 * @throws Exception
	 */
	public String loadMessage(Message message) throws Exception {
		try {
			formsAdapter.loadMessage(message.getMessageNo());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}

			DocWayWebmailPreview docwayWebmailPreview = new DocWayWebmailPreview();
			docwayWebmailPreview.getFormsAdapter().fillFormsFromResponse(response);
			docwayWebmailPreview.init(response.getDocument());
			docwayWebmailPreview.setShowemail(true);
			setSessionAttribute("docwayWebmailPreview", docwayWebmailPreview);
						
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Controllo dei campo obbligatori
	 * 
	 * @return false se tutti i campo obbligatori sono stati compilati, true se anche un solo campo obbligatorio non e' compilato
	 */
	private boolean checkSearchFilter() {
		boolean result = false;
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Dovrebbe essere caricato dal file di properties dell'applicazione
		
		if (getFilter().getType().equals("content")) {
			
			// controllo sul campo contenuto
			if (getFilter().getContains() == null || getFilter().getContains().equals("")) {
				this.setErrorMessage("templateForm:contains", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.contiene") + "'");
				result = true;
			}
			
		}
		else { // filtro su data di spedizione
			
			// controllo sul formato della data di spedizione da
			if (getFilter().getFrom() == null || getFilter().getFrom().equals("")) {
				this.setErrorMessage("templateForm:sentDateFrom", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_spedizione") + " " + I18N.mrs("dw4.dal") + "'");
				result = true;
			}
			else if (!DateUtil.isValidDate(getFilter().getFrom(), formatoData)) {
				this.setErrorMessage("templateForm:sentDateFrom", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_spedizione") + " " + I18N.mrs("dw4.dal") + "': " + formatoData.toLowerCase());
				result = true;
			}
			
			// TODO attualmente il "data a" non viene utilizzato (problemi di ricerca in and con la libreria javamail)
			/*
			// controllo sul formato della data di spedizione a
			if (getFilter().getTo() == null || getFilter().getTo().equals("")) {
				this.setErrorMessage("templateForm:sentDateTo", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data_spedizione") + " " + I18N.mrs("dw4.al") + "'");
				result = true;
			}
			else if (!DateUtil.isValidDate(getFilter().getTo(), formatoData)) {
				this.setErrorMessage("templateForm:sentDateTo", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data_spedizione") + " " + I18N.mrs("dw4.al") + "': " + formatoData.toLowerCase());
				result = true;
			}
			
			// controllo se from e' antecedente a to
			if (!result) {
				try {
					int diff = DateUtil.getDateDifference(getFilter().getFrom(), getFilter().getTo(), formatoData);
					if (diff < 0) {
						this.setErrorMessage("", I18N.mrs("dw4.data_spedizione_da_e_antecedente_a_data_spedizione_da"));
						result = true;
					}
				}
				catch (Exception e) {
					Logger.debug(e.getMessage());
					this.setErrorMessage("", e.getMessage());
					result = true;
				}
			}
			*/
			
		}
		
		return result;
	}
	
	/**
	 * Aggiornamento del filtro su webmail in base al tipo selezionato
	 * dall'operatore
	 */
	public String changeFilterType() throws Exception {  
        if (filter.getType().equals("sentdate")) {
        	filter.setContains("");
        	if (filter.getFrom() == null || filter.getFrom().equals("")) // in caso di data nulla la inizializzo al giorno corrente
        		filter.setFrom(DateUtil.getCurrentDateNorm());
        }
        else if (filter.getType().equals("content")) {
        	filter.setFrom("");
        }
        
        filter.setTo(""); // TODO attualmente il "data a" non viene utilizzato (problemi di ricerca in and con la libreria javamail)
        
        return null;
    }
	
}
