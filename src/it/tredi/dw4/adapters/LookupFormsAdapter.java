package it.tredi.dw4.adapters;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

public class LookupFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	
	public LookupFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}
	
	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		resetForms();
		
		fillDefaultFormFromResponse(response);
	}
	
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		Element root = response.getRootElement();
		
		defaultForm.addParam("verbo", "lookup");
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		defaultForm.addParam("sele", root.attributeValue("sele", ""));
		defaultForm.addParam("ripropz", root.attributeValue("ripropz", ""));
		defaultForm.addParam("xDoc", "");
		defaultForm.addParam("qord", root.attributeValue("qord", ""));
		defaultForm.addParam("destPage", root.attributeValue("destPage", ""));
		defaultForm.addParam("lookup_titolo", root.attributeValue("lookup_titolo", "").replaceAll("&#34;", "\""));
		defaultForm.addParam("lookup_campi", root.attributeValue("lookup_campi", "").replaceAll("&#34;", "\""));
		defaultForm.addParam("lookup_alias", root.attributeValue("lookup_alias", ""));
		defaultForm.addParam("lookup_db", root.attributeValue("lookup_db", ""));
		defaultForm.addParam("lookup_new", root.attributeValue("lookup_new", ""));
		defaultForm.addParam("lookup_xq", root.attributeValue("lookup_xq", ""));
		defaultForm.addParam("lookup_fieldVal", root.attributeValue("lookup_fieldVal", ""));
		defaultForm.addParam("selected_campi", root.attributeValue("selected_campi", ""));
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
	}
	
	/* ACTIONS - DEFAULTFORM */
	
	public boolean primaPagina() {
		if (isPrimaPaginaEnabled()) {
			defaultForm.addParam("verbo", "lookup_response");
			defaultForm.addParam("pos", 0);
			
			//FIXME
			//sendRequest();
			
			return true;
		}
		else {
			return false;	
		}
	}
	
	public boolean isPrimaPaginaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0)
			return true;
		else
			return false;
	}
	
	public boolean paginaPrecedente() {
		if (isPaginaPrecedenteEnabled()) {
			int pos = defaultForm.getParamAsInt("pos");
			defaultForm.addParam("verbo", "lookup_response");
			int pageCount = defaultForm.getParamAsInt("pageCount");
			defaultForm.addParam("pos", pos - pageCount < 0? 0 : pos - pageCount);
			
			//FIXME
			//sendRequest();
						
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isPaginaPrecedenteEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0)
			return true;
		else
			return false;
	}	
	
	public boolean paginaSuccessiva() {
		if (isPaginaSuccessivaEnabled()) {
			int pos = defaultForm.getParamAsInt("pos");
			int pageCount = defaultForm.getParamAsInt("pageCount");
			defaultForm.addParam("verbo", "lookup_response");
			defaultForm.addParam("pos", pos + pageCount);
			
			//FIXME
			//sendRequest();
						
			return true;
		}
		else {
			return false;
		}		
	}
	
	public boolean isPaginaSuccessivaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int count = defaultForm.getParamAsInt("count");		
		if (count > pos + pageCount)
			return true;
		else
			return false;
	}	
	
	public boolean ultimaPagina() {
		if (isUltimaPaginaEnabled()) {
			int pageCount = defaultForm.getParamAsInt("pageCount");
			int count = defaultForm.getParamAsInt("count");			
			defaultForm.addParam("verbo", "lookup_response");
			defaultForm.addParam("pos", (int)Math.floor((count - 1) / pageCount) * pageCount);
			
			//FIXME
			//sendRequest();
						
			return true;
		}
		else {
			return false;
		}	
	}	
	
	public boolean isUltimaPaginaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int count = defaultForm.getParamAsInt("count");		
		if (count > pos + pageCount)
			return true;
		else
			return false;
	}
	
	public int getFirstPosition(){
		int pos = defaultForm.getParamAsInt("pos");
		return pos +1;
	}
	
	public int getLastPosition(){
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int count = defaultForm.getParamAsInt("count");
		if (count < pos + pageCount)
			return count;
		else return pos + pageCount;
	}
	
	public int getCurrent() {
		int pos = defaultForm.getParamAsInt("pos") + 1;
		int pageCount = defaultForm.getParamAsInt("pageCount");
		
		if (pos % pageCount == 0)
			return pos/pageCount;
		else
			return (pos/pageCount) + 1;
	}
	
	public int getTotal() {
		int count = defaultForm.getParamAsInt("count");	
		int pageCount = defaultForm.getParamAsInt("pageCount");
		
		if (count % pageCount == 0)
			return count/pageCount;
		else
			return (count/pageCount) + 1;
	}
	
	/**
	 * Caricamento di una pagina specifica della lista titoli
	 * @param currentPage
	 * @return
	 */
	public void paginaSpecifica(int currentPage) {
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int pos = (currentPage-1) * pageCount;
		
		defaultForm.addParam("verbo", "lookup_response");
		defaultForm.addParam("pos", pos);		
	}
	
	public boolean isIndietroRapidoEnabled() {
		return isPaginaPrecedenteEnabled();
	}
	
	public boolean isAvantiRapidoEnabled() {
		return isPaginaSuccessivaEnabled();
	}
	
	/**
	 * Ritorna true se gli indici sono gestiti tramite elasticsearch, false in caso di gestione tramite extraway
	 * @return
	 */
	public boolean isElasticsearchEnabled() {
		String elasticsearch = defaultForm.getParam("elasticsearch");
		if (elasticsearch != null && elasticsearch.toLowerCase().equals("true"))
			return true;
		else
			return false;
	}
	
	/**
	 * Ritorna la stringa di attivazione del pulsante 'nuovo' su lookup.
	 * Viene utilizzato nel controllo di attivazione del pulsante. Se non vengono
	 * correttamente impostati i parametri presenti in 'lookup_new', anche
	 * se si dispone dei permessi di inserimento il pulsante non verra' mostrato
	 * in modo da non provocare poi un'errore successivo
	 * 
	 * @return Valore impostato sul parametro lookup_new
	 */
	public String getLookupNew() {
		return defaultForm.getParam("lookup_new");
	}
	
	/**
	 * Imposta il form per la richiesta di nuovo record da lookup
	 */
	public void newRecord() {
		String lookupNew = defaultForm.getParam("lookup_new");
		String[] lookupNewParams = lookupNew.split(";");
		
		String dbTable = lookupNewParams[1].substring(lookupNewParams[1].indexOf("=") + 1);
		
		// Era presente ma non utilizzato neppure sul javascript di DocWay3
		//String fillField = lookupNewParams[2].substring(lookupNewParams[2].indexOf("=") + 1);
		
		defaultForm.addParam("verbo", "docEdit");
		defaultForm.addParam("dbTable", "@"+dbTable);
		defaultForm.addParam("selid", "");
		defaultForm.addParam("pos", 0); // Sul javascript era impostato a vuoto (ma poi darebbe una 'java.lang.NumberFormatException')
	}
	
	/**
	 * Imposta il valore di lookup dopo un'attivita' di inserimento di un 
	 * nuovo record
	 * 
	 * @param physDoc physDoc del nuovo documento creato che deve essere agganciato al lookup
	 */
	public void redoLookupAfterInsert(String physDoc) {
		defaultForm.addParam("verbo", "lookup_response");
		defaultForm.addParam("selid", "");
		defaultForm.addParam("lookup_fieldVal", "_PHYSDOC=" + physDoc);
	}
	
}
