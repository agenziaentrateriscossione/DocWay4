package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.Ordinamento_select;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.docway.model.Tabella;
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

public class QueryStampe extends DocWayQuery {
	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private boolean arrivo = true;
	private boolean partenza = true;
	private boolean interno = true;
	private boolean varie = false;
	
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
	private String custom_da_num_prot = "";
	private String custom_a_num_prot = "";
	private Rif uor = new Rif();
	private String custom_globale = "";
	private String custom_rep_da = "";
	private String custom_rep_a = "";
	
	private int count_selected_repertori = 0;
	private String custom_repertorio = "";
	
	private List<TitoloRepertorio> listof_rep;
	private Ordinamento_select ordinamento_select = new Ordinamento_select();
	private String ordinamento = "";
	
	private String currentCheckClientId = "";
	private boolean chkbox_printId = false;
	private boolean chkbox_classif = false;
	private boolean chkbox_printUor = false;
	private boolean chkbox_printPostit = false;
	private boolean chkbox_customDocAnnullato = false;
	private boolean chkbox_customDocNonAnnullato = false;
	private boolean chkbox_distCons = false;
	
	// gestione multisocieta'
	private List<Societa> societaSelect = new ArrayList<Societa>();
	private String codSocieta = "";
		
	public QueryStampe() throws Exception {
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
	
	public boolean isArrivo() {
		return arrivo;
	}

	public void setArrivo(boolean arrivo) {
		this.arrivo = arrivo;
	}

	public boolean isPartenza() {
		return partenza;
	}

	public void setPartenza(boolean partenza) {
		this.partenza = partenza;
	}

	public boolean isInterno() {
		return interno;
	}

	public void setInterno(boolean interno) {
		this.interno = interno;
	}

	public boolean isVarie() {
		return varie;
	}

	public void setVarie(boolean varie) {
		this.varie = varie;
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
	
	public boolean isChkbox_printId() {
		return chkbox_printId;
	}

	public void setChkbox_printId(boolean chkbox_printId) {
		this.chkbox_printId = chkbox_printId;
	}

	public boolean isChkbox_classif() {
		return chkbox_classif;
	}

	public void setChkbox_classif(boolean chkbox_classif) {
		this.chkbox_classif = chkbox_classif;
	}

	public boolean isChkbox_printUor() {
		return chkbox_printUor;
	}

	public void setChkbox_printUor(boolean chkbox_printUor) {
		this.chkbox_printUor = chkbox_printUor;
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

	public boolean isChkbox_distCons() {
		return chkbox_distCons;
	}

	public void setChkbox_distCons(boolean chkbox_distCons) {
		this.chkbox_distCons = chkbox_distCons;
	}
	
	public int getCount_selected_repertori() {
		return count_selected_repertori;
	}

	public void setCount_selected_repertori(int count_selected_repertori) {
		this.count_selected_repertori = count_selected_repertori;
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
	 * Generazione del report di stampa protocolli (pulsante
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
			
			boolean repertorioSelected = (count_selected_repertori == 0) ? false : true;
			
			// impostazione dei parametri di stampa in base alla selezione
			// dell'operatore
			customPreFindAndPrint(codSocieta, selid, query, "", ordinamento, repertorioSelected, chkbox_printUor, chkbox_printPostit, chkbox_classif, chkbox_printId, chkbox_distCons);
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
	 * stampa protocolli
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
			if (count_selected_repertori > 0) {
				String repertorioId[] = custom_repertorio.split("%");
				int separator;
				for ( int is = 0; is < repertorioId.length; is++ ){
					String repTable = "";
					String repertorio = (separator = repertorioId[is].indexOf("-")) != -1 ? repertorioId[is].substring(0,separator) : "" ;
	                if ( separator != -1 ){
	                	repTable = repertorioId[is].substring(separator+1);
	                	if ( "A".equals(repTable) ) repTable = "arrivo" ;
	                	else if ( "I".equals(repTable) ) repTable = "interno" ;
	                	else if ( "P".equals(repTable) ) repTable = "partenza" ;
	                    else if ( "V".equals(repTable) ) repTable = "varie" ;
	                }
	                
	                if (queryRep.equals(""))
	                    queryRep = "([doc_tipo]=" + repTable + " AND [doc_repertoriocod]=\"" + repertorio + "\")";
	                else
	                    queryRep += " OR ([doc_tipo]=" + repTable + " AND [doc_repertoriocod]=\"" + repertorio + "\")";
	            }
				
				if (!queryRep.equals(""))
					queryRep = "(" + queryRep + ")";
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
				query = "([@]=" + escaptedValue + " OR [doc_filesfiletesto]=" + escaptedValue + ")";
				
			}
			else if (printType.equals("numreprange") && count_selected_repertori == 1) {
				
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
			
			// aggiunta del filtro sui flussi di documenti da ricercare
			if (!query.equals("")) {
				if (arrivo || partenza || interno || varie) {
					String filtro = "[doc_tipo]=";
			        if (arrivo) filtro += "arrivo OR ";
			        if (partenza) filtro += "partenza OR ";
			        if (interno) filtro += "interno OR ";
			        if (varie) filtro += "varie";
			        
			        if (filtro.endsWith(" OR "))
			        	filtro = filtro.substring(0, filtro.length()-3);
			        
			        query += " AND (" + filtro + ")";
				}
			}
			
			// mbernardini 27/04/2015 : aggiunta del filtro su doc annullati o non annullati
			if (!query.equals("")) {
				if (chkbox_customDocAnnullato)
					query += " AND ([doc_annullato]=\"si\")";
				else if (chkbox_customDocNonAnnullato)
					query += " AND ([doc_annullato]=\"no\")";
			}
			
			// TODO visto che si tratta di una stampa di protocolli si puo' mettere il filtro su bozza=no
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
			custom_da_num_prot = "";
			custom_a_num_prot = "";
			uor = new Rif();
			custom_globale = "";
			custom_rep_da = "";
			custom_rep_a = "";
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
			custom_rep_da = "";
			custom_rep_a = "";
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
			custom_rep_da = "";
			custom_rep_a = "";
		}
		
		return null;
	}
	
	/**
	 * check dei parametri di tipo doc specificati sulla maschera di stampa protocolli
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkTipoDoc() throws Exception {
		if (partenza || interno || varie)
			chkbox_distCons = false;
		
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
		if (currentCheckClientId.equals("templateForm:opt_chkbox_printUor") && chkbox_printUor) {
			chkbox_distCons = false;
		}
		else if (currentCheckClientId.equals("templateForm:opt_chkbox_distCons") && chkbox_distCons) { 
			chkbox_printUor = false;
			arrivo = true;
			partenza = false;
			interno = false;
			varie = false;
		}
		else if (currentCheckClientId.equals("templateForm:opt_chkbox_customDocNonAnnullato") && chkbox_customDocNonAnnullato) {
			chkbox_customDocAnnullato = false;
		}
		else if (currentCheckClientId.equals("templateForm:opt_chkbox_customDocAnnullato") && chkbox_customDocAnnullato) {
			chkbox_customDocNonAnnullato = false;
		}
		
		return null;
	}
	
	/**
	 * check dei parametri di opzioni relativi ai repertori
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkOpzioniRepertori() throws Exception {
		custom_repertorio = "";
		count_selected_repertori = 0;
		
		// se si selezionano piu' di un repertorio vengono azzerati (e disabilitati) i valori
		// 'custom_rep_da' e 'custom_rep_a'
		for (int i=0; i < listof_rep.size(); i++){
        	TitoloRepertorio rep = listof_rep.get(i);
        	if (rep.getList_tabelle() != null && rep.getList_tabelle().size() > 0) {
        		for (int j=0; j < rep.getList_tabelle().size(); j++) {
        			Tabella repTable = rep.getList_tabelle().get(j);
        			if (repTable.isSelected()) {
        				custom_repertorio+=rep.getCodice()+"-"+repTable.getTipo()+"%";
        				count_selected_repertori++;
        			}
        		}
        	}
        }
		if (count_selected_repertori >= 2) {
			custom_rep_da = "";
			custom_rep_a = "";
		}
		
		return null;
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
