package it.tredi.dw4.docway.beans.fatturepa;

import java.util.ArrayList;
import java.util.List;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayLoadingbar;
import it.tredi.dw4.docway.beans.DocWayQuery;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

/**
 * stampa del registro delle fatture attive o passive
 * 
 * @author mbernardini
 */
public class QueryRegistroFatture extends DocWayQuery {
	
	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private String selid = ""; // eventuale selezione di documenti
	private String count = ""; // numero di documenti inclusi nella selezione
	
	private String currDate = "";
	private String currYear = "";
	
	private String cicloFatture = ""; // ciclo passivo o attivo?
	private String printType = "annuale"; // tipologia di stampa (giornaliero, annuale, ecc.)
	private String printFormat = "pdf"; // formato di stampa (tipo file)
	
	// parametri relativi a PrintType
	private String doc_anno = "";
	private String range_docdatafattura_from = "";
	private String range_docdatafattura_to = "";
	private String custom_da_num_prot = "";
	private String custom_a_num_prot = "";
	private String custom_globale = "";
	private String rangenum_docnrecord_from = "";
	private String rangenum_docnrecord_to = "";
	
	// gestione multisocieta'
	private List<Societa> societaSelect = new ArrayList<Societa>();
	private String codSocieta = "";
	
	public QueryRegistroFatture() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		
		currDate = XMLUtil.parseStrictAttribute(dom, "/response/@currDate");
		currYear = XMLUtil.parseStrictAttribute(dom, "/response/@currYear");
		
		selid = XMLUtil.parseStrictAttribute(dom, "/response/@selid");
		count= XMLUtil.parseStrictAttribute(dom, "/response/@count");
		
		if (selid != null && !selid.equals(""))
			printType = "selezione";
		
		if (printType.equals("annuale"))
			doc_anno = currYear;
		
		cicloFatture = "attivo";
	}

	@Override
	public QueryFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public String queryPlain() throws Exception {
		return null;
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

	public String getDoc_anno() {
		return doc_anno;
	}

	public void setDoc_anno(String doc_anno) {
		this.doc_anno = doc_anno;
	}

	public String getRange_docdatafattura_from() {
		return range_docdatafattura_from;
	}

	public void setRange_docdatafattura_from(String range_docdata_from) {
		this.range_docdatafattura_from = range_docdata_from;
	}

	public String getRange_docdatafattura_to() {
		return range_docdatafattura_to;
	}

	public void setRange_docdatafattura_to(String range_docdata_to) {
		this.range_docdatafattura_to = range_docdata_to;
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
	
	public String getCicloFatture() {
		return cicloFatture;
	}

	public void setCicloFatture(String cicloFatture) {
		this.cicloFatture = cicloFatture;
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
	
	/**
	 * check dei parametri di printType specificati sulla maschera di controllo
	 * di gestione
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkItem() throws Exception {
		try {
			
			if (printType.equals("annuale")) {
				doc_anno = currYear;
				range_docdatafattura_from = "";
				range_docdatafattura_to = "";
				custom_da_num_prot = "";
				custom_a_num_prot = "";
				custom_globale = "";
				rangenum_docnrecord_from = "";
				rangenum_docnrecord_to = "";
			}
			else {
				doc_anno = "";
				range_docdatafattura_from = "";
				range_docdatafattura_to = "";
				custom_da_num_prot = "";
				custom_a_num_prot = "";
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
	 * generazione del report di registro delle fatture (pulsante 'stampa')
	 * 
	 * @return
	 * @throws Exception
	 */
	public String findAndPrint() throws Exception {
		try {
			String query = createQuery();
			
			// mancano dei parametri necessari alla produzione del report
			if ((selid == null || selid.equals("")) && (query == null || query.equals("")))
				return null;
			
			String qord = "xml(xpart:/doc/repertorio/@numero)"; // ordinamento per numero di repertorio
			String view = getFormsAdapter().getDefaultForm().getParam("view") + "DATA_INT|"; // TODO e' corretto?
			
			formsAdapter.findAndPrintRegistroFatture(cicloFatture, codSocieta, view, selid, query, qord, printFormat);
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
	 * impostazione della query di generazione del report di registro delle fatture
	 * 
	 * @return
	 * @throws Exception
	 */
	private String createQuery() throws Exception {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
		
		String query = "([doc_repertoriocod]=" + ((cicloFatture != null && cicloFatture.equals("passivo")) ? "FTRPAP" : "FTRPAA") + ") AND ";
		
		if (!printType.equals("selezione") && selid != null && !selid.equals(""))
			selid = "";
		
		if (selid.equals("")) {
			if (printType.equals("annuale")) {
				
				if (doc_anno == null || doc_anno.equals("")) {
					this.setErrorMessage("templateForm:docAnno", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.anno") + "'");
					return null;
				}
				else if (!StringUtil.isNumber(doc_anno)) {
					this.setErrorMessage("templateForm:docAnno", I18N.mrs("dw4.inserire_un_valore_numerico_nel_campo") + " '" + I18N.mrs("dw4.anno") + "'");
					return null;
				}

				query += "([doc_anno]=" + doc_anno + ")";
				
			}
			else if (printType.equals("daterange")) {
				
				if (range_docdatafattura_from == null || range_docdatafattura_from.equals("")) {
					this.setErrorMessage("templateForm:dataFattura_from", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.dates") + "'");
					return null;
				}
				else if (!DateUtil.isValidDate(range_docdatafattura_from, formatoData)) {
					this.setErrorMessage("templateForm:dataFattura_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.dates") + "': " + formatoData.toLowerCase());
					return null;
				}
				else if (range_docdatafattura_to != null && !range_docdatafattura_to.equals("")) {
					if (!DateUtil.isValidDate(range_docdatafattura_to, formatoData)) {
						this.setErrorMessage("templateForm:dataFattura_To", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.dates") + "': " + formatoData.toLowerCase());
						return null;
					}
				}
				
				if (range_docdatafattura_to != null && !range_docdatafattura_to.equals(""))
					query += "([doc_dataemissionefattura]={"+DateUtil.formatDate2XW(range_docdatafattura_from, "")+"|"+DateUtil.formatDate2XW(range_docdatafattura_to, "")+"})";
				else
					query += "([doc_dataemissionefattura]=" + DateUtil.formatDate2XW(range_docdatafattura_from, "") + ")";
				
			}
			else if (printType.equals("numrange")) {
				
				if (custom_da_num_prot == null || custom_da_num_prot.equals("")) {
					this.setErrorMessage("templateForm:numProt_from", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.numeri_di_protocollo") + "'");
					return null;
				}
				
				if (custom_a_num_prot != null && !custom_a_num_prot.equals(""))
					query += "([docnumprot]={_ANNOCORRENTE_-_CODSEDE_-" + StringUtil.fillString(custom_da_num_prot, "0", 7) + "|_ANNOCORRENTE_-_CODSEDE_-" + StringUtil.fillString(custom_a_num_prot, "0", 7) + "})";
				else
					query += "([docnumprot]=_ANNOCORRENTE_-_CODSEDE_-" + StringUtil.fillString(custom_da_num_prot, "0", 7) + ")";
				
			}
			else if (printType.equals("fulltext")) {
				
				if (custom_globale == null || custom_globale.equals("")) {
					this.setErrorMessage("templateForm:fullTextField", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.ricerca_full_text") + "'");
					return null;
				}
				
				String escaptedValue = this.escapeQueryValue(custom_globale);
				query += "[@]=" + escaptedValue + " OR [doc_filesfiletesto]=" + escaptedValue;
				
			}
			else if (printType.equals("idrange")) {
				
				if (rangenum_docnrecord_from == null || rangenum_docnrecord_from.equals("")) {
					this.setErrorMessage("templateForm:nrecord_from", I18N.mrs("acl.requiredfield") + " '" + I18N.mrs("dw4.intervallo_di_id") + "'");
					return null;
				}
				if (rangenum_docnrecord_to != null && !rangenum_docnrecord_to.equals(""))
					query += "([docnrecord]={" + StringUtil.fillString(rangenum_docnrecord_from, "0", 7) + "|" + StringUtil.fillString(rangenum_docnrecord_to, "0", 7) + "})";
				else
					query += "([docnrecord]=" + StringUtil.fillString(rangenum_docnrecord_from, "0", 7) + ")";
				
			}
		}
		
		return query;
	}

}
