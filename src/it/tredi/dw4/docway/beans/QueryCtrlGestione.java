package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class QueryCtrlGestione extends DocWayQuery {
	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private String selid = ""; // eventuale selezione di documenti
	private String count = ""; // numero di documenti inclusi nella selezione
	
	private String currDate = "";
	private String currYear = "";
	
	private String printType = "annuale"; // tipologia di stampa (giornaliero, annuale, ecc.)
	private String printFormat = "html"; // formato di stampa (tipo file)
	
	// parametri relativi a PrintType
	private String dataProt = "";
	private String doc_anno = "";
	private String range_docdataprot_from = "";
	private String range_docdataprot_to = "";
	private String custom_da_num_prot = "";
	private String custom_a_num_prot = "";
	private Rif uor = new Rif();
	private String custom_globale = "";
	private String rangenum_docnrecord_from = "";
	private String rangenum_docnrecord_to = "";
	
	// parametri relativi a opzioni di stampa
	private boolean chkbox_dettaglio_uor = false;
	private boolean chkbox_dettaglio_protocollista = false;
	private boolean chkbox_dettaglio_allegati = false;
	private boolean chkbox_protocolli_noallegati = false;
	private boolean tmp_chkbox_protocolli_noallegati = false;
	private boolean chkbox_mittenti_uor = false;
	private boolean chkbox_mittenti_protocollista = false;
	private boolean chkbox_mittenti_globali = false;
	
	// gestione multisocieta'
	private List<Societa> societaSelect = new ArrayList<Societa>();
	private String codSocieta = "";
	
	public QueryCtrlGestione() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		xml 				= dom.asXML();
		
		currDate 			= XMLUtil.parseStrictAttribute(dom, "/response/@currDate");
		currYear 			= XMLUtil.parseStrictAttribute(dom, "/response/@currYear");
		
		selid 				= XMLUtil.parseStrictAttribute(dom, "/response/@selid");
		count				= XMLUtil.parseStrictAttribute(dom, "/response/@count");
		
		if (selid != null && !selid.equals(""))
			printType 		= "selezione";
		
		if (printType.equals("annuale"))
			doc_anno 		= currYear;
		
		// settaggio delle opzioni di stampa
		chkbox_dettaglio_uor 			= formsAdapter.checkBooleanFunzionalitaDisponibile("checkboxDettaglioUOR", false);
		chkbox_dettaglio_protocollista 	= formsAdapter.checkBooleanFunzionalitaDisponibile("checkboxDettaglioProtocollista", false);
		chkbox_dettaglio_allegati 		= formsAdapter.checkBooleanFunzionalitaDisponibile("checkboxDettaglioAllegati", false);
		chkbox_protocolli_noallegati 	= formsAdapter.checkBooleanFunzionalitaDisponibile("checkboxNumeriProtocolloNoAllegati", false);
		chkbox_mittenti_uor 			= formsAdapter.checkBooleanFunzionalitaDisponibile("checkboxMittentiUOR", false);
		chkbox_mittenti_protocollista 	= formsAdapter.checkBooleanFunzionalitaDisponibile("checkboxMittentiProtocollista", false);
		chkbox_mittenti_globali 		= formsAdapter.checkBooleanFunzionalitaDisponibile("checkboxMittentiGlobali", false);
		
		// recupero delle societa' da gestire
		societaSelect = XMLUtil.parseSetOfElement(dom, "/response/societa_select/societa", new Societa());
		codSocieta = "";
		if (societaSelect != null && societaSelect.size() > 0) {
			for (int i=0; i<societaSelect.size(); i++) {
				Societa societa = (Societa) societaSelect.get(i);
				if (societa != null && societa.getSelected().equals("selected"))
					codSocieta = societa.getCod();
			}
		}
    }
	
	/**
	 * ritorno ad una eventuale lista documenti da pagina corrente (caricamento in 
	 * base al selId passato)
	 * @return
	 * @throws Exception
	 */
	public String backToList() throws Exception {
		return gotoTitlesDocs(selid);
	}
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getSelid() {
		return selid;
	}

	public void setSelid(String selid) {
		this.selid = selid;
	}
	
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
	public String getPrintType() {
		return printType;
	}

	public void setPrintType(String printType) {
		this.printType = printType;
	}
	
	public String getPrintFormat() {
		return printFormat;
	}

	public void setPrintFormat(String printFormat) {
		this.printFormat = printFormat;
	}
	
	public String getDataProt() {
		return dataProt;
	}

	public void setDataProt(String dataProt) {
		this.dataProt = dataProt;
	}

	public String getDoc_anno() {
		return doc_anno;
	}

	public void setDoc_anno(String doc_anno) {
		this.doc_anno = doc_anno;
	}

	public String getRange_docdataprot_from() {
		return range_docdataprot_from;
	}

	public void setRange_docdataprot_from(String range_docdataprot_from) {
		this.range_docdataprot_from = range_docdataprot_from;
	}

	public String getRange_docdataprot_to() {
		return range_docdataprot_to;
	}

	public void setRange_docdataprot_to(String range_docdataprot_to) {
		this.range_docdataprot_to = range_docdataprot_to;
	}

	public String getCustom_da_num_prot() {
		return custom_da_num_prot;
	}

	public void setCustom_da_num_prot(String custom_da_num_prot) {
		this.custom_da_num_prot = custom_da_num_prot;
	}

	public String getCustom_a_num_prot() {
		return custom_a_num_prot;
	}

	public void setCustom_a_num_prot(String custom_a_num_prot) {
		this.custom_a_num_prot = custom_a_num_prot;
	}

	public Rif getUor() {
		return uor;
	}

	public void setUor(Rif uor) {
		this.uor = uor;
	}

	public String getCustom_globale() {
		return custom_globale;
	}

	public void setCustom_globale(String custom_globale) {
		this.custom_globale = custom_globale;
	}

	public String getRangenum_docnrecord_from() {
		return rangenum_docnrecord_from;
	}

	public void setRangenum_docnrecord_from(String rangenum_docnrecord_from) {
		this.rangenum_docnrecord_from = rangenum_docnrecord_from;
	}

	public String getRangenum_docnrecord_to() {
		return rangenum_docnrecord_to;
	}

	public void setRangenum_docnrecord_to(String rangenum_docnrecord_to) {
		this.rangenum_docnrecord_to = rangenum_docnrecord_to;
	}
	
	public boolean isChkbox_dettaglio_uor() {
		return chkbox_dettaglio_uor;
	}

	public void setChkbox_dettaglio_uor(boolean chkbox_dettaglio_uor) {
		this.chkbox_dettaglio_uor = chkbox_dettaglio_uor;
	}

	public boolean isChkbox_dettaglio_protocollista() {
		return chkbox_dettaglio_protocollista;
	}

	public void setChkbox_dettaglio_protocollista(
			boolean chkbox_dettaglio_protocollista) {
		this.chkbox_dettaglio_protocollista = chkbox_dettaglio_protocollista;
	}

	public boolean isChkbox_dettaglio_allegati() {
		return chkbox_dettaglio_allegati;
	}

	public void setChkbox_dettaglio_allegati(boolean chkbox_dettaglio_allegati) {
		this.chkbox_dettaglio_allegati = chkbox_dettaglio_allegati;
	}

	public boolean isChkbox_protocolli_noallegati() {
		return chkbox_protocolli_noallegati;
	}

	public void setChkbox_protocolli_noallegati(boolean chkbox_protocolli_noallegati) {
		this.chkbox_protocolli_noallegati = chkbox_protocolli_noallegati;
	}
	
	public boolean isTmp_chkbox_protocolli_noallegati() {
		return tmp_chkbox_protocolli_noallegati;
	}

	public void setTmp_chkbox_protocolli_noallegati(
			boolean tmp_chkbox_protocolli_noallegati) {
		this.tmp_chkbox_protocolli_noallegati = tmp_chkbox_protocolli_noallegati;
	}

	public boolean isChkbox_mittenti_uor() {
		return chkbox_mittenti_uor;
	}

	public void setChkbox_mittenti_uor(boolean chkbox_mittenti_uor) {
		this.chkbox_mittenti_uor = chkbox_mittenti_uor;
	}

	public boolean isChkbox_mittenti_protocollista() {
		return chkbox_mittenti_protocollista;
	}

	public void setChkbox_mittenti_protocollista(
			boolean chkbox_mittenti_protocollista) {
		this.chkbox_mittenti_protocollista = chkbox_mittenti_protocollista;
	}

	public boolean isChkbox_mittenti_globali() {
		return chkbox_mittenti_globali;
	}

	public void setChkbox_mittenti_globali(boolean chkbox_mittenti_globali) {
		this.chkbox_mittenti_globali = chkbox_mittenti_globali;
	}
	
	public String getCurrDate() {
		return currDate;
	}

	public void setCurrDate(String currDate) {
		this.currDate = currDate;
	}

	public String getCurrYear() {
		return currYear;
	}

	public void setCurrYear(String currYear) {
		this.currYear = currYear;
	}
	
	public List<Societa> getSocietaSelect() {
		return societaSelect;
	}

	public void setSocietaSelect(List<Societa> societaSelect) {
		this.societaSelect = societaSelect;
	}

	public String getCodSocieta() {
		return codSocieta;
	}

	public void setCodSocieta(String codSocieta) {
		this.codSocieta = codSocieta;
	}

	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	/**
	 * Generazione del report di controllo di gestione (pulsante
	 * 'stampa')
	 * 
	 * @return
	 * @throws Exception
	 */
	public String findAndPrint() throws Exception {
		try {
			String query = createQuery();
			
			// mancano dei parametri necessari alla produzione
			// del report
			if ((selid == null || selid.equals("")) && (query == null || query.equals("")))
				return null;
			
			String qext = (doc_anno != null && !doc_anno.equals("")) ? "([/fascicolo/@anno]=" + doc_anno + ") OR " : "";
			
			String jReportParams = (printFormat.equals("html") ? "boolean#IS_IGNORE_PAGINATION=true&" : "") +
	    			"boolean#dettaglioUOR=" + chkbox_dettaglio_uor +
	    	    	"&boolean#dettaglioProtocollista=" + chkbox_dettaglio_protocollista +
	    	    	"&boolean#dettaglioAllegati=" +  chkbox_dettaglio_allegati +
	    	    	"&boolean#protocolliNoAllegati=" + chkbox_protocolli_noallegati +
	    	    	//M0000059 - Mittenti ripetuti per UOR/Protocollista
	    	    	"&boolean#mittentiUOR=" + chkbox_mittenti_uor +
	    	    	"&boolean#mittentiProtocollista=" + chkbox_mittenti_protocollista +
	    	        // Federico 22/04/09: introdotto checkbox "Stampa mittenti nelle statistiche globali" [M 0000324]
	    	        "&boolean#mittentiGlobali=" + chkbox_mittenti_globali;
	    	
	    	formsAdapter.findAndPrintCtrlGestione(codSocieta, selid, query, qext, printFormat, jReportParams);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			String verbo = response.getAttributeValue("/response/@verbo");
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
			return null;
		}
	}
	
	/**
	 * Impostazione della query di generazione del report di
	 * controllo di gestione
	 * 
	 * @return
	 * @throws Exception
	 */
	private String createQuery() throws Exception {
		String query = "";
		
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
		
		if (!printType.equals("selezione") && selid != null && !selid.equals(""))
			selid = "";
		
		if (selid.equals("")) {
			if (printType.equals("giornaliero")) {
				
				if (dataProt == null || dataProt.equals("")) {
					this.setErrorMessage("templateForm:dataProt", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.data") + "'");
					return null;
				}
				else if (!DateUtil.isValidDate(dataProt, formatoData)) {
					this.setErrorMessage("templateForm:dataProt", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.data") + "': " + formatoData.toLowerCase());
					return null;
				}
				
				query = "([docdataprot]=" + DateUtil.formatDate2XW(dataProt, "") + ")";
				
			}
			else if (printType.equals("annuale")) {
				
				if (doc_anno == null || doc_anno.equals("")) {
					this.setErrorMessage("templateForm:docAnno", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.anno") + "'");
					return null;
				}
				else if (!StringUtil.isNumber(doc_anno)) {
					this.setErrorMessage("templateForm:docAnno", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " '" + I18N.mrs("dw4.anno") + "'");
					return null;
				}

				query = "([doc_anno]=" + doc_anno + ")";
				
			}
			else if (printType.equals("daterange")) {
				
				if (range_docdataprot_from == null || range_docdataprot_from.equals("")) {
					this.setErrorMessage("templateForm:dataProt_from", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.dates") + "'");
					return null;
				}
				else if (!DateUtil.isValidDate(range_docdataprot_from, formatoData)) {
					this.setErrorMessage("templateForm:dataProt_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.dates") + "': " + formatoData.toLowerCase());
					return null;
				}
				else if (range_docdataprot_to != null && !range_docdataprot_to.equals("")) {
					if (!DateUtil.isValidDate(range_docdataprot_to, formatoData)) {
						this.setErrorMessage("templateForm:dataProt_To", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.dates") + "': " + formatoData.toLowerCase());
						return null;
					}
				}
				
				if (range_docdataprot_to != null && !range_docdataprot_to.equals(""))
					query = "([docdataprot]={"+DateUtil.formatDate2XW(range_docdataprot_from, "")+"|"+DateUtil.formatDate2XW(range_docdataprot_to, "")+"})";
				else
					query = "([docdataprot]=" + DateUtil.formatDate2XW(range_docdataprot_from, "") + ")";
				
			}
			else if (printType.equals("numrange")) {
				
				if (custom_da_num_prot == null || custom_da_num_prot.equals("")) {
					this.setErrorMessage("templateForm:numProt_from", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.numeri_di_protocollo") + "'");
					return null;
				}
				
				if (custom_a_num_prot != null && !custom_a_num_prot.equals(""))
					query = "([docnumprot]={_ANNOCORRENTE_-_CODSEDE_-" + StringUtil.fillString(custom_da_num_prot, "0", 7) + "|_ANNOCORRENTE_-_CODSEDE_-" + StringUtil.fillString(custom_a_num_prot, "0", 7) + "})";
				else
					query = "([docnumprot]=_ANNOCORRENTE_-_CODSEDE_-" + StringUtil.fillString(custom_da_num_prot, "0", 7) + ")";
				
			}
			else if (printType.equals("uor")) {
				
				if (uor == null || uor.getCod_uff() == null || uor.getCod_uff().equals("")) {
					this.setErrorMessage("templateForm:nome_uff_input", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.proprietario") + "'");
					return null;
				}
				
				query = "([doc_rifinternirifcoduff]=" + uor.getCod_uff() + ") ADJ ([doc_rifinternirifdiritto]=RPA)";
				
			}
			else if (printType.equals("fulltext")) {
				
				if (custom_globale == null || custom_globale.equals("")) {
					this.setErrorMessage("templateForm:fullTextField", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.ricerca_full_text") + "'");
					return null;
				}
				
				String escaptedValue = this.escapeQueryValue(custom_globale);
				query = "[@]=" + escaptedValue + " OR [doc_filesfiletesto]=" + escaptedValue;
				
			}
			else if (printType.equals("idrange")) {
				
				if (rangenum_docnrecord_from == null || rangenum_docnrecord_from.equals("")) {
					this.setErrorMessage("templateForm:nrecord_from", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.intervallo_di_id") + "'");
					return null;
				}
				if (rangenum_docnrecord_to != null && !rangenum_docnrecord_to.equals(""))
					query = "([docnrecord]={" + StringUtil.fillString(rangenum_docnrecord_from, "0", 7) + "|" + StringUtil.fillString(rangenum_docnrecord_to, "0", 7) + "})";
				else
					query = "([docnrecord]=" + StringUtil.fillString(rangenum_docnrecord_from, "0", 7) + ")";
				
			}
		}
		
		return query;
	}
	
	/**
	 * check dei parametri di printType specificati sulla maschera di controllo
	 * di gestione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkItem() throws Exception {
		try {
			
			if (printType.equals("giornaliero")) {
				dataProt = currDate;
				doc_anno = "";
				range_docdataprot_from = "";
				range_docdataprot_to = "";
				custom_da_num_prot = "";
				custom_a_num_prot = "";
				uor = new Rif();
				custom_globale = "";
				rangenum_docnrecord_from = "";
				rangenum_docnrecord_to = "";
			}
			else if (printType.equals("annuale")) {
				dataProt = "";
				doc_anno = currYear;
				range_docdataprot_from = "";
				range_docdataprot_to = "";
				custom_da_num_prot = "";
				custom_a_num_prot = "";
				uor = new Rif();
				custom_globale = "";
				rangenum_docnrecord_from = "";
				rangenum_docnrecord_to = "";
			}
			else {
				dataProt = "";
				doc_anno = "";
				range_docdataprot_from = "";
				range_docdataprot_to = "";
				custom_da_num_prot = "";
				custom_a_num_prot = "";
				uor = new Rif();
				custom_globale = "";
				rangenum_docnrecord_from = "";
				rangenum_docnrecord_to = "";
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			return null;
		}
	}
	
	/**
	 * check del formato di stampa della maschera di controllo
	 * di gestione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkFormat() throws Exception {
		try {
			//RW0055025 - fcossu - Problemi controllo gestione
			if (printFormat.equals("csv") || printFormat.equals("xls") || printFormat.equals("odt") || printFormat.equals("rtf")) {
				tmp_chkbox_protocolli_noallegati = chkbox_protocolli_noallegati;
				chkbox_protocolli_noallegati = true;
			}
			 // Federico 20/11/08: l'else va riferito solo ai formati 'html' e 'pdf' [RW 0055025]
			else if (printFormat.equals("html") || printFormat.equals("pdf")) {
				chkbox_protocolli_noallegati = tmp_chkbox_protocolli_noallegati;
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			return null;
		}
	}
	
	/**
	 * Click su check di 'protocolli_noallegati'
	 * @param vce
	 * @throws Exception
	 */
	public void checkProtocolliNoAllegatiValueChange(ValueChangeEvent vce) throws Exception {
		tmp_chkbox_protocolli_noallegati = StringUtil.booleanValue(vce.getNewValue().toString());
	}
	
	/**
	 * Lookup su UOR (parametri printType)
	 * @return
	 * @throws Exception
	 */
	public String lookupUor() throws Exception {
		try {
			// return customPrintLookup(this,'struint_nome,gruppi_nome','nomeUfficio','xml(xpart:/struttura_interna/nome),xml(xpart:/gruppo/nome)');
			
			String value = uor.getNome_uff();
	
			String aliasName 	= "struint_nome,gruppi_nome";
			String aliasName1 	= "";
			String titolo 		= "xml,/*/nome"; //titolo 
			String ord 			= "xml(xpart:/struttura_interna/nome),xml(xpart:/gruppo/nome)"; //ord
			
			String campi 		= ".@nome_uff=xml,/*/nome ; .@cod_uff=xml,/struttura_interna/@cod_uff xml,/gruppo/@id";
			if (formsAdapter.checkBooleanFunzionalitaDisponibile("rolesManagement", false))
				campi 			= ".@nome_uff=xml,/*/nome ; .@cod_uff=xml,/struttura_interna/@cod_uff xml,/gruppo/@id xml,/ruolo/@id";
			
			String db 			= formsAdapter.getDefaultForm().getParam("aclDb"); //db 
			String newRecord 	= ""; //newRecord
			String xq			= ""; //extraQuery
			
			return customPrintLookup(uor, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Clear del lookup su UOR (parametri printType)
	 * @return
	 * @throws Exception
	 */
	public String clearUor() throws Exception {
		String campi 		= ".@nome_uff=xml,/*/nome ; .@cod_uff=xml,/struttura_interna/@cod_uff xml,/gruppo/@id";
		if (formsAdapter.checkBooleanFunzionalitaDisponibile("rolesManagement", false))
			campi 			= ".@nome_uff=xml,/*/nome ; .@cod_uff=xml,/struttura_interna/@cod_uff xml,/gruppo/@id xml,/ruolo/@id";
		
		return customClearPrintLookup(uor, campi);
	}
	
}
