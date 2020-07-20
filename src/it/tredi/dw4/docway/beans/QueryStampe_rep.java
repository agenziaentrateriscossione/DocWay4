package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.Ordinamento_select;
import it.tredi.dw4.docway.model.TitoloRepertorio;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class QueryStampe_rep extends DocWayQuery {
	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private String selid = ""; // eventuale selezione di documenti
	private String count = ""; // numero di documenti inclusi nella selezione
	
	private String currDate = "";
	private String currYear = "";
	
	private String printType = "giornaliero"; // tipologia di stampa (giornaliero, annuale, ecc.)
	private String printFormat = "html"; // formato di stampa (tipo file)
	
	// parametri relativi a PrintType
	private String dataProt = "";
	private String doc_anno = "";
	private String range_docdataprot_from = "";
	private String range_docdataprot_to = "";
	private String custom_globale = "";
	private String custom_rep_da = "";
	private String custom_rep_a = "";
	
	private String custom_repertorio = "";
	private List<TitoloRepertorio> listof_rep;
	private Ordinamento_select ordinamento_select = new Ordinamento_select();
	private String ordinamento = "";
	
	private String currentCheckClientId = "";
	private boolean chkbox_printPostit = false;
	private boolean chkbox_customDocAnnullato = false;
	private boolean chkbox_customDocNonAnnullato = false;
	
	// gestione multisocieta'
	private List<Societa> societaSelect = new ArrayList<Societa>();
	private String codSocieta = "";
		
	public QueryStampe_rep() throws Exception {
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
		else if (printType.equals("giornaliero"))
			dataProt 		= currDate;
		
		listof_rep		 	= XMLUtil.parseSetOfElement(dom, "response/listof_rep/repertorio", new TitoloRepertorio());
		ordinamento_select.init(XMLUtil.createDocument(dom, "/response/ordinamentoStampe_select"));
		
		for (Iterator<Option> iterator = ordinamento_select.getOptions().iterator(); iterator.hasNext();) {
			Option option = (Option) iterator.next();
			if (option.getSelected().length()>0)
				ordinamento = option.getValue();
		}
		
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
	
	public void setCount(String count) {
		this.count = count;
	}
	
	public String getCount() {
		return count;
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

	public String getCustom_globale() {
		return custom_globale;
	}

	public void setCustom_globale(String custom_globale) {
		this.custom_globale = custom_globale;
	}

	public String getCustom_rep_da() {
		return custom_rep_da;
	}

	public void setCustom_rep_da(String custom_rep_da) {
		this.custom_rep_da = custom_rep_da;
	}

	public String getCustom_rep_a() {
		return custom_rep_a;
	}

	public void setCustom_rep_a(String custom_rep_a) {
		this.custom_rep_a = custom_rep_a;
	}
	
	public String getCustom_repertorio() {
		return custom_repertorio;
	}

	public void setCustom_repertorio(String repertorio) {
		this.custom_repertorio = repertorio;
	}
	
	public List<TitoloRepertorio> getListof_rep() {
		return listof_rep;
	}

	public void setListof_rep(List<TitoloRepertorio> listof_rep) {
		this.listof_rep = listof_rep;
	}
	
	public void setOrdinamento_select(Ordinamento_select ordinamento_select) {
		this.ordinamento_select = ordinamento_select;
	}

	public Ordinamento_select getOrdinamento_select() {
		return ordinamento_select;
	}
	
	public String getOrdinamento() {
		return ordinamento;
	}

	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}
	
	public boolean isChkbox_printPostit() {
		return chkbox_printPostit;
	}

	public void setChkbox_printPostit(boolean chkbox_printPostit) {
		this.chkbox_printPostit = chkbox_printPostit;
	}

	public boolean isChkbox_customDocAnnullato() {
		return chkbox_customDocAnnullato;
	}

	public void setChkbox_customDocAnnullato(boolean chkbox_customDocAnnullato) {
		this.chkbox_customDocAnnullato = chkbox_customDocAnnullato;
	}

	public boolean isChkbox_customDocNonAnnullato() {
		return chkbox_customDocNonAnnullato;
	}

	public void setChkbox_customDocNonAnnullato(boolean chkbox_customDocNonAnnullato) {
		this.chkbox_customDocNonAnnullato = chkbox_customDocNonAnnullato;
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
	 * Generazione del report di stampa repertori (pulsante
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
			
			boolean repertorioSelected = (custom_repertorio == null || custom_repertorio.equals("")) ? false : true;
			String qext = "NOT ([/doc/repertorio/@numero]=&null;) AND";
			
			// impostazione dei parametri di stampa in base alla selezione
			// dell'operatore
			customPreFindAndPrint(codSocieta, selid, query, qext, ordinamento, repertorioSelected, false, chkbox_printPostit, false, false, false);
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
	 * stampa repertori
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
			String queryRep = "";
			if (custom_repertorio != null && !custom_repertorio.equals("")) {
				int separator;
				String repTable = "";
				String repertorio = (separator = custom_repertorio.indexOf("-")) != -1 ? custom_repertorio.substring(0,separator) : "" ;
                if ( separator != -1 ){
                	repTable = custom_repertorio.substring(separator+1);
                	if ( "A".equals(repTable) ) repTable = "arrivo" ;
                	else if ( "I".equals(repTable) ) repTable = "interno" ;
                	else if ( "P".equals(repTable) ) repTable = "partenza" ;
                    else if ( "V".equals(repTable) ) repTable = "varie" ;
                }
                
                queryRep = "([doc_tipo]=" + repTable + " AND [doc_repertoriocod]=\"" + repertorio + "\")";
			}
			
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
						this.setErrorMessage("templateForm:dataProt_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.dates") + "': " + formatoData.toLowerCase());
						return null;
					}
				}
				
				if (range_docdataprot_to != null && !range_docdataprot_to.equals(""))
					query = "([docdataprot]={"+DateUtil.formatDate2XW(range_docdataprot_from, "")+"|"+DateUtil.formatDate2XW(range_docdataprot_to, "")+"})";
				else
					query = "([docdataprot]=" + DateUtil.formatDate2XW(range_docdataprot_from, "") + ")";
				
			}
			else if (printType.equals("fulltext")) {
				
				if (custom_globale == null || custom_globale.equals("")) {
					this.setErrorMessage("templateForm:fullTextField", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.ricerca_full_text") + "'");
					return null;
				}
				
				String escaptedValue = this.escapeQueryValue(custom_globale);
				query = "([@]=" + escaptedValue + " OR [doc_filesfiletesto]=" + escaptedValue + ")";
				
			}
			else if (printType.equals("numreprange") && custom_repertorio != null && !custom_repertorio.equals("")) {
				
				if (custom_rep_da == null || custom_rep_da.equals("")) {
					this.setErrorMessage("templateForm:numRep_from", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.numeri_di_repertorio") + "'");
					return null;
				}
				
				queryRep = "";
				int separator;
				String repertorio = (separator = custom_repertorio.indexOf("-")) != -1 ? custom_repertorio.substring(0,separator) : "" ;
	            String repTable = custom_repertorio.substring(separator+1, custom_repertorio.length()-1);
	            if ( "A".equals(repTable) ) repTable = "arrivo" ;
            	else if ( "I".equals(repTable) ) repTable = "interno" ;
            	else if ( "P".equals(repTable) ) repTable = "partenza" ;
                else if ( "V".equals(repTable) ) repTable = "varie" ;
	            
				if (custom_rep_a != null && !custom_rep_a.equals(""))
					query = "([doc_repertorionumero]={" + repertorio + "^-_CODSEDE_-_ANNOCORRENTE_" + StringUtil.fillString(custom_rep_da, "0", 7) + "|" + repertorio + "^-_CODSEDE_-_ANNOCORRENTE_" + StringUtil.fillString(custom_rep_a, "0", 7) + "})";
				else
					query = "([doc_repertorionumero]=" + repertorio + "^-_CODSEDE_-_ANNOCORRENTE_" + StringUtil.fillString(custom_rep_da, "0", 7) + ")";
			
				if (repTable.length() > 0)
					query = "([doc_tipo]=" + repTable + " AND " + query + ")";
				
			}
			
			if (!queryRep.equals(""))  {
				if (query.length() == 0)
					query = queryRep;
				else
					query = queryRep + " AND " + query;
			}
			
			// aggiunta del filtro sui doc annullati
			if (!query.equals("")) {
				if (chkbox_customDocAnnullato)
					query += " AND ([doc_annullato]=\"si\")";
				else if (chkbox_customDocNonAnnullato)
					query += " AND ([doc_annullato]=\"no\")";
			}
		}
		
		return query;
	}
	
	/**
	 * check dei parametri di printType specificati sulla maschera di stampa protocolli
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkItem() throws Exception {
		if (printType.equals("giornaliero")) {
			dataProt = currDate;
			doc_anno = "";
			range_docdataprot_from = "";
			range_docdataprot_to = "";
			custom_globale = "";
			custom_rep_da = "";
			custom_rep_a = "";
		}
		else if (printType.equals("annuale")) {
			dataProt = "";
			doc_anno = currYear;
			range_docdataprot_from = "";
			range_docdataprot_to = "";
			custom_globale = "";
			custom_rep_da = "";
			custom_rep_a = "";
		}
		else {
			dataProt = "";
			doc_anno = "";
			range_docdataprot_from = "";
			range_docdataprot_to = "";
			custom_globale = "";
			custom_rep_da = "";
			custom_rep_a = "";
		}
		
		return null;
	}
	
	/**
	 * Selezione di un check di opzioni di stampa
	 * 
	 * @param vce
	 * @throws Exception
	 */
	public void printOptionValueChange(ValueChangeEvent vce) throws Exception {
		currentCheckClientId = vce.getComponent().getClientId();
	}
	
	/**
	 * check dei parametri di opzioni di stampa specificati sulla maschera 
	 * di stampa protocolli
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkOpzioniStampa() throws Exception {
		if (currentCheckClientId.equals("templateForm:opt_chkbox_customDocNonAnnullato") && chkbox_customDocNonAnnullato) {
			chkbox_customDocAnnullato = false;
		}
		else if (currentCheckClientId.equals("templateForm:opt_chkbox_customDocAnnullato") && chkbox_customDocAnnullato) {
			chkbox_customDocNonAnnullato = false;
		}
		
		return null;
	}
		
}
