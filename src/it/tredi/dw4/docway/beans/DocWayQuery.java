package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.beans.AttachFile;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.model.customfields.CustomQueryFields;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.QueryUtil;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;

public abstract class DocWayQuery extends Page {
	public abstract void init(Document dom);

	public abstract QueryFormsAdapter getFormsAdapter();

	public abstract String queryPlain() throws Exception;
	
	// gestione dei campi custom definiti dall'utente tramite modello XML in ricerca
	private CustomQueryFields customQueryFields = new CustomQueryFields();
	// definisce l'eventuale sezione contenente campi custom da mostrare.
	private String currentCustomFieldSection = "";

	public XMLDocumento _queryPlain(String query, String verbo, String xverb)
			throws Exception {
		getFormsAdapter().queryPlain(query, verbo, xverb);

		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}

	protected XMLDocumento _openIdex(String keyPath, String startKey,
			String shwMode, String common, String threl, String inputName,
			String windowTitle, String value, boolean acceptEmptySelection) throws Exception {
		getFormsAdapter().openIndex(keyPath, startKey, shwMode, common, threl, inputName, windowTitle, value, acceptEmptySelection);

		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di Query
		return response;
	}

	public void openIndex(String keyPath, String value, String startKey, String common, boolean acceptEmptySelection) throws Exception {
		openIndex("", keyPath, value, startKey, common, acceptEmptySelection);
	}

	public void openIndex(String inputName, String keyPath, String value, String startKey, String common, boolean acceptEmptySelection) throws Exception {
		DocWayShowindex showindex = new DocWayShowindex();
		setShowindex(showindex);
		showindex.setModel(this);

		XMLDocumento response = this._openIdex(keyPath, // keyPath
				startKey, // startKey
				null, // shwMode
				common, // common
				null, // threl
				inputName, // inputName
				null, // windowTitle
				value, // value
				acceptEmptySelection
				);

		showindex.getFormsAdapter().fillFormsFromResponse(response);
		showindex.init(response.getDocument());

		/*
		 * if ( aclShowindex().size() ==1 )
		 * aclShowindex(aclThvincolato.getTitoli().get(0)); else
		 */
		showindex.setActive(true);
	}

	protected String addQueryField(String query, String value) {
		return addQueryField(query, value, "AND");
	}

	protected String addQueryField(String query, String value, String operator) {
		if (null == value || "".equals(value.trim()))
			return "";
		else
			return "([" + query + "]=" + value + ") " + operator + " "; // fcappelli 20120906 - rimossi i doppi apici dalla query, vanno aggiunti dall'utente eventualmente
	}
	
	/**
	 * Esegue l'escaping dei valori riservati (operatori logici) nelle 
	 * query su eXtraWay
	 * 
	 * @param value
	 * @return
	 */
	protected String escapeQueryValue(String value) {
		return QueryUtil.escapeQueryValue(value);
	}

	/**
	 * Appende alla query un filtro su range di date in base ai parametri
	 * specificati
	 * 
	 * @param searchName chiave di ricerca (es. creaz, mod)
	 * @param dataFrom data di inizio dell'intervallo di ricerca
	 * @param dataTo data di fine dell'intervallo di ricerca
	 */
	protected String addDateRangeQuery(String searchName, String dataFrom,
			String dataTo) {
		return addDateRangeQuery(searchName, dataFrom, dataTo, "");
	}

	/**
	 * Appende alla query un filtro su range di date in base ai parametri
	 * specificati
	 * 
	 * @param searchName chiave di ricerca (es. creaz, mod)
	 * @param dataFrom data di inizio dell'intervallo di ricerca
	 * @param dataTo data di fine dell'intervallo di ricerca
	 * @param operator operatore da appendere dopo la query
	 */
	protected String addDateRangeQuery(String searchName, String dataFrom,
			String dataTo, String operator) {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Caricare il formato della data dal file di properties

		String xwDataFrom = "";
		if (dataFrom != null && dataFrom.length() > 0)
			xwDataFrom = DateUtil.formatDate2XW(dataFrom, formatoData);

		String xwDataTo = "";
		if (dataTo != null && dataTo.length() > 0)
			xwDataTo = DateUtil.formatDate2XW(dataTo, formatoData);

		String rangeQuery = "";
		if (xwDataFrom.length() > 0 && xwDataTo.length() > 0) {
			// Ricerca su range di date
			rangeQuery = "[" + searchName + "]={" + xwDataFrom + "|" + xwDataTo
					+ "}";
		} else if (xwDataFrom.length() > 0) {
			// Ricerca esatta su dataFrom
			rangeQuery = "[" + searchName + "]=" + xwDataFrom;
		} else if (xwDataTo.length() > 0) {
			// Ricerca esatta su dataTo
			rangeQuery = "[" + searchName + "]=" + xwDataTo;
		}

		if (rangeQuery.length() > 0) {
			if (operator != null && operator.length() > 0)
				rangeQuery = "(" + rangeQuery + ") " + operator + " ";
			else
				rangeQuery = "(" + rangeQuery + ")";
		}

		return rangeQuery;
	}
	
	/**
	 * Appende alla query un filtro su range di numeri in base ai parametri
	 * specificati
	 * 
	 * @param searchName chiave di ricerca (es. creaz, mod)
	 * @param from data di inizio dell'intervallo di ricerca
	 * @param to data di fine dell'intervallo di ricerca
	 * @param operator operatore da appendere dopo la query
	 */
	protected String addRangeQuery(String searchName, String from,
			String to, String operator) {
		String rangeQuery = "";
		if (from!= null && from.length() > 0 && to!= null && to.length() > 0) {
			// Ricerca su range di numeri
			rangeQuery = "[" + searchName + "]={" + from + "|" + to
					+ "}";
		} else if (from != null && from.length() > 0) {
			// Ricerca esatta su from
			rangeQuery = "[" + searchName + "]=" + from;
		} else if (to != null && to.length() > 0) {
			// Ricerca esatta su to
			rangeQuery = "[" + searchName + "]=" + to;
		}

		if (rangeQuery.length() > 0) {
			if (operator != null && operator.length() > 0)
				rangeQuery = "(" + rangeQuery + ") " + operator + " ";
			else
				rangeQuery = "(" + rangeQuery + ")";
		}
		return rangeQuery;
	}
	
	/**
	 * Appende alla query un filtro su range di numeri in base ai parametri
	 * specificati
	 * 
	 * @param searchName chiave di ricerca (es. creaz, mod)
	 * @param values elenco di valori da impostare
	 * @param operator operatore da appendere dopo la query
	 */
	protected String addRangeQuery(String searchName, String[] values, String operator) {
		String rangeQuery = "";
		if (values != null && values.length > 0) {
			rangeQuery = "[" + searchName + "]={";
			for (int i=0; i<values.length; i++) {
				if (values[i] != null && !values[i].equals("")) {
					rangeQuery += values[i];
					if (i != values.length-1)
						rangeQuery += ",";
				}
			}
			rangeQuery += "}";
		}

		if (rangeQuery.length() > 0) {
			if (operator != null && operator.length() > 0)
				rangeQuery = "(" + rangeQuery + ") " + operator + " ";
			else
				rangeQuery = "(" + rangeQuery + ")";
		}
		return rangeQuery;
	}

	/**
	 * Esecuzione della query specificata con identificazione dell'errore/warning/info e redirect a corretta 
	 * pagina di destinazione
	 * 
	 * @param query Query da eseguire
	 * @return
	 * @throws Exception
	 */
	public String queryPlain(String query) throws Exception {
		try {
			XMLDocumento response = this._queryPlain(query, "", "");
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
				return null;
			}
			if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")) {
				// restituito un solo risultato, redirect a showdoc
				
				return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
			} 
			else {
				// restituiti piu' risultati redirect a showtitles
				
				return buildTitlePageAndReturnNavigationRule(response);
			}
		} 
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
			return null;
		}
	}
	
	public String buildTitlePageAndReturnNavigationRule(XMLDocumento response) throws Exception{
		DocWayTitles titles = new DocWayTitles();
		titles.getFormsAdapter().fillFormsFromResponse(response);
		titles.init(response.getDocument());
		setSessionAttribute("titles", titles);
		return "showtitles@docway";
	}
	
	/**
	 * Lettura della response contenente un file (es. operazione di download) o il messaggio
	 * di errore restituito dall'operazione
	 * 
	 * @param attachFile oggetto contenente il file da scaricare o il messaggio di errore da restituire
	 * @return
	 * @throws Exception
	 */
	public String getResponseAttach(AttachFile attachFile) throws Exception {
		if (attachFile.getContent() != null) {
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());
			
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			response.setContentType(new MimetypesFileTypeMap().getContentType(attachFile.getFilename()));
			response.setContentLength(attachFile.getContent().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + attachFile.getFilename() + "\"");
			ServletOutputStream out;
			out = response.getOutputStream();
			out.write(attachFile.getContent());
			faces.responseComplete();
		}
		else {
			// Gestione del messaggio di ritorno! (si dovrebbe trattare solo di messaggi di errore)
			handleErrorResponse(attachFile.getXmlDocumento());
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());
		}
		
		return null;
	}
	
	/**
	 * Data la response derivante da una ricerca carica la pagina di destinazione 
	 * corretta: pagina dei titoli in caso di piu' risultati, showdoc in caso di un 
	 * solo documento restituito
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String navigateResponse(XMLDocumento response) throws Exception{
		if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")){
			return buildSpecificShowdocPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response);
		}
		else{
			DocWayTitles titles = new DocWayTitles();
			titles.getFormsAdapter().fillFormsFromResponse(response);
			
			titles.init(response.getDocument());
			titles.setPopupPage(isPopupPage());
			setSessionAttribute("docwayTitles", titles);
			return "showtitles@docway";
		}
	}
		
	/**
	 * Impostazione di alcuni parametri di stampa (gestione dei report di
	 * Docway)
	 */
	protected void customPreFindAndPrint(String codSocieta, String selid, String query, String qext, String qord, boolean repertorioSelected, boolean printUor, boolean printPostit, boolean classif, boolean printId, boolean distCons) throws Exception {
		String view = "";
		String jReportInfo = "";
		String jReportParams = "";

		String print_id = "false";
		String print_uor = "false";
		String print_classif = "false";
		String print_postit = "false";
		String dist_cons = "false";

		if (repertorioSelected)
			view = getFormsAdapter().getDefaultForm().getParam("view") + "REP|";

		// RW0047906 - Stampa repertori
		if (printUor)
			print_uor = "true";
		if (printPostit)
			print_postit = "true";
		if (classif)
			print_classif = "true";
		if (printId)
			print_id = "true";
		
		jReportInfo = "Stampe/xdocway/JasperReport/Stampe_Docway/registro_protocollo_dataprot.jasper%jasper%pdf%JRXW#";
		jReportParams = "boolean#REQ_ID=" + print_id + "&boolean#REQ_UOR=" + print_uor + "&boolean#REQ_POSTIT=" + print_postit + "&boolean#REQ_CLASSIF=" + print_classif;

		// Federica e Federico 28/12/07: introdotta gestione dei gruppi/ruoli
		// nelle stampe [RW 0048491]
		String rolesQueryExtension = "";
		String rolesOrdExtension = "";
		if (getFormsAdapter().checkBooleanFunzionalitaDisponibile(
				"rolesManagement", false)) {
			rolesQueryExtension = " OR [UD,/xw/@UdType/]=\"ruolo\"";
			rolesOrdExtension = ",xml(xpart:/ruolo/nome)";
		}

		if (distCons) {
			// Federico 07/12/06: cambiati i percorsi dei file delle stampe
			// affinche' vengano trovati correttamente dal classloader [RW
			// 0041111]
			// Federica e Federico 28/12/07: introdotta gestione dei
			// gruppi/ruoli nelle stampe [RW 0048491]
			jReportInfo = "Stampe/xdocway/JasperReport/Stampe_Docway/distinta_consegna.jasper%jasper%pdf%JRXW#%acl%[UD,/xw/@UdType/]=\"struttura_interna\" OR [UD,/xw/@UdType/]=\"gruppo\""
					+ rolesQueryExtension
					+ "%xml(xpart:/struttura_interna/nome),xml(xpart:/gruppo/nome)"
					+ rolesOrdExtension;

			// Federico 10/01/08: la distinta di consegna richiede una gestione
			// dei diritti utente (per testare la visualizzazione
			// dei doc.) particolare [RW 0048970]
			jReportParams = "boolean#REQ_ID="
					+ print_id
					+ "&boolean#REQ_UOR="
					+ print_uor
					+ "&boolean#REQ_POSTIT="
					+ print_postit
					+ "&boolean#REQ_CLASSIF="
					+ print_classif
					+ "&string#JRXWRightsCustomClass=it.highwaytech.xway4jasper.xdocway.JRXWRights4DistintaConsegna";
			dist_cons = "true";
		}

		String ord = getFormsAdapter().getDefaultForm().getParam("xwOrd");
		if (ord != null && !ord.equals("")) {
			if (ord.indexOf("doc/rif_interni/rif") != -1
					&& dist_cons.equals("false")) {
				// Federico 07/12/06: cambiati i percorsi dei file delle stampe
				// affinche' vengano trovati correttamente dal classloader [RW
				// 0041111]
				// Federica e Federico 28/12/07: introdotta gestione dei
				// gruppi/ruoli nelle stampe [RW 0048491]
				jReportInfo = "Stampe/xdocway/JasperReport/Stampe_Docway/registro_protocollo_uor.jasper%jasper%pdf%JRXW#%acl%[UD,/xw/@UdType/]=\"struttura_interna\" OR [UD,/xw/@UdType/]=\"gruppo\""
						+ rolesQueryExtension
						+ "%xml(xpart:/struttura_interna/nome),xml(xpart:/gruppo/nome)"
						+ rolesOrdExtension;
				jReportParams = "boolean#REQ_ID=" + print_id
						+ "&boolean#REQ_UOR=" + print_uor
						+ "&boolean#REQ_POSTIT=" + print_postit
						+ "&boolean#REQ_CLASSIF=" + print_classif;
			} else if (dist_cons.equals("false")) {
				// Federico 07/12/06: cambiati i percorsi dei file delle stampe
				// affinche' vengano trovati correttamente dal classloader [RW
				// 0041111]
				jReportInfo = "Stampe/xdocway/JasperReport/Stampe_Docway/registro_protocollo_dataprot.jasper%jasper%pdf%JRXW#";
				jReportParams = "boolean#REQ_ID=" + print_id
						+ "&boolean#REQ_UOR=" + print_uor
						+ "&boolean#REQ_POSTIT=" + print_postit
						+ "&boolean#REQ_CLASSIF=" + print_classif;
			}
		}

		// RW0047906 - Stampa repertori
		String dbTable = getFormsAdapter().getDefaultForm().getParam("dbTable");
		if (dbTable.equals("@stampe_rep")) {
			jReportInfo = "Stampe/xdocway/JasperReport/Stampe_Docway/registro_repertori.jasper%jasper%pdf%JRXW#";
			jReportParams = "boolean#REQ_ID=true&boolean#REQ_UOR=true&boolean#REQ_POSTIT="
					+ print_postit + "&boolean#REQ_CLASSIF=true";
		}

		getFormsAdapter().findAndPrint(codSocieta, selid, query, qext, qord, view, jReportInfo, jReportParams);
	}
	
	/**
	 * Lookup su maschera di stampa
	 * @return
	 */
	public String customPrintLookup(XmlEntity model, String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) throws Exception {
		getFormsAdapter().customPrintLookup(aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		if (handleErrorResponse(response)) {
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
		
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); 
		
		DocWayLookup docwayLookup = new DocWayLookup();
		setLookup(docwayLookup);
		docwayLookup.setModel(model);
		
		docwayLookup.getFormsAdapter().fillFormsFromResponse(response);
		docwayLookup.init(response.getDocument());
		
		if(docwayLookup.getTitoli().size() == 1 && value != null && value.length() > 0){
			docwayLookup.confirm(docwayLookup.getTitoli().get(0));
		}
		else {
			docwayLookup.setActive(true);
		}
		
		return null;
	}
	
	/**
	 * Clear su lookup della maschera di stampa
	 * @return
	 */
	public String customClearPrintLookup(XmlEntity model, String campi) throws Exception {
		DocWayLookup docwayLookup = new DocWayLookup();
		docwayLookup.setModel(model);
		docwayLookup.cleanFields(campi);
		
		return null;
	}
	
	public CustomQueryFields getCustomQueryFields() {
		return customQueryFields;
	}

	public void setCustomQueryFields(CustomQueryFields customQueryFields) {
		this.customQueryFields = customQueryFields;
	}
	
	public String getCurrentCustomFieldSection() {
		return currentCustomFieldSection;
	}

	public void setCurrentCustomFieldSection(String customFieldSection) {
		// verifico se effettivamente esistono campi custom definiti per il tipo 
		// selezionato
		if (customFieldSection.length() > 0 && getCustomQueryFields().getQuerysections() != null) {
			if (!getCustomQueryFields().getQuerysections().containsKey(customFieldSection))
				customFieldSection = "";
		}
		
		this.currentCustomFieldSection = customFieldSection;
	}
	
	/**
	 * ritorno ad una eventuale lista documenti da pagina corrente (caricamento in 
	 * base al selId passato)
	 * @param selid selid della selezione da caricare
	 * @return
	 * @throws Exception
	 */
	protected String gotoTitlesDocs(String selid) throws Exception {
		try {
			if (selid != null && selid.length() > 0) {
				getFormsAdapter().gotoTitles(selid);
				getFormsAdapter().getDefaultForm().addParam("dbTable", "");
				
				XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				
				DocWayTitles titles = new DocWayTitles();
				titles.getFormsAdapter().fillFormsFromResponse(response);
				titles.init(response.getDocument());
				setSessionAttribute("docwayTitles", titles);
				
				return "showtitles@docway";
			}
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

}
