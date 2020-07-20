package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.Rif;
import it.tredi.dw4.utils.DateUtil;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

import it.tredi.dw4.i18n.I18N;

public class QueryRep_fasc extends DocWayQuery {
	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private String range_fasc_anno_from = "";
	private String range_fasc_anno_to = "";
	private String custom_classiffasccod = "";
	private String custom_classiffasc = "";
	
	private String fasc_stato = "I";
	private String fasc_personale = "also";
	
	private String currentConsistenzaCheckId = "";
	private boolean consistenza_fasc = false;
	private boolean consistenza_sottofasc = false;
	
	private Rif rif = new Rif();
		
	public QueryRep_fasc() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
		xml = dom.asXML();
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
	
	public String getRange_fasc_anno_from() {
		return range_fasc_anno_from;
	}

	public void setRange_fasc_anno_from(String range_fasc_anno_from) {
		this.range_fasc_anno_from = range_fasc_anno_from;
	}

	public String getRange_fasc_anno_to() {
		return range_fasc_anno_to;
	}

	public void setRange_fasc_anno_to(String range_fasc_anno_to) {
		this.range_fasc_anno_to = range_fasc_anno_to;
	}

	public String getCustom_classiffasccod() {
		return custom_classiffasccod;
	}

	public void setCustom_classiffasccod(String custom_classiffasccod) {
		this.custom_classiffasccod = custom_classiffasccod;
	}

	public String getCustom_classiffasc() {
		return custom_classiffasc;
	}

	public void setCustom_classiffasc(String custom_classiffasc) {
		this.custom_classiffasc = custom_classiffasc;
	}
	
	public String getFasc_stato() {
		return fasc_stato;
	}

	public void setFasc_stato(String fasc_stato) {
		this.fasc_stato = fasc_stato;
	}

	public String getFasc_personale() {
		return fasc_personale;
	}

	public void setFasc_personale(String fasc_personale) {
		this.fasc_personale = fasc_personale;
	}
	
	public boolean isConsistenza_fasc() {
		return consistenza_fasc;
	}

	public void setConsistenza_fasc(boolean consistenza_fasc) {
		this.consistenza_fasc = consistenza_fasc;
	}

	public boolean isConsistenza_sottofasc() {
		return consistenza_sottofasc;
	}

	public void setConsistenza_sottofasc(boolean consistenza_sottofasc) {
		this.consistenza_sottofasc = consistenza_sottofasc;
	}

	public Rif getRif() {
		return rif;
	}

	public void setRif(Rif rif) {
		this.rif = rif;
	}

	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	/**
	 * Generazione del report di repertorio fascicoli (pulsante
	 * 'stampa')
	 * 
	 * @return
	 * @throws Exception
	 */
	public String stampaRepertorioFascicoli() throws Exception {
		try {
			String anno 			= "";
			String cons_fasc     	= "false";
			String cons_sottofasc 	= "false";
			
			String query = "";
			if (range_fasc_anno_from != null && !range_fasc_anno_from.equals("") && range_fasc_anno_to != null && !range_fasc_anno_to.equals("")) {
				if (range_fasc_anno_from.equals(range_fasc_anno_to)) {
					query = "[fasc_anno]=\"" + range_fasc_anno_from +"\"";
					anno = range_fasc_anno_from;
				}
				else {
					query = "[fasc_anno]={" + range_fasc_anno_from + "|" + range_fasc_anno_to + "}";
					anno = I18N.mrs("dw4.dal") + " " + range_fasc_anno_from + " " + I18N.mrs("dw4.al") + " " + range_fasc_anno_to;
				}
			}
			else if (range_fasc_anno_from != null && !range_fasc_anno_from.equals("") && (range_fasc_anno_to == null || range_fasc_anno_to.equals(""))) {
				query = "[fasc_anno]={" + range_fasc_anno_from + "|}";
				anno = I18N.mrs("dw4.dal") + " " + range_fasc_anno_from;
			}
			else if ((range_fasc_anno_from == null || range_fasc_anno_from.equals("") && range_fasc_anno_to != null && !range_fasc_anno_to.equals(""))) {
				query = "[fasc_anno]={|" + range_fasc_anno_to + "}";
				anno = I18N.mrs("dw4.fino_al") + " " + range_fasc_anno_to;
			}
			else { // Federico 23/02/09: l'anno non deve essere valorizzato obbligatoriamente [M 0000124]
				anno = DateUtil.getCurrentYear() + "";
				query = "[fasc_anno]=\"" + anno + "\"";
			}
			
			if (custom_classiffasccod != null && !custom_classiffasccod.equals("") && custom_classiffasc != null && !custom_classiffasc.equals(""))
				query += (query.length() > 0 ? " AND " : "") + "[fasc_classif]=\" " + custom_classiffasccod + " - " + custom_classiffasc + "\"";
			else if (custom_classiffasccod != null && !custom_classiffasccod.equals("") && (custom_classiffasc == null || custom_classiffasc.equals("")))
				query += (query.length() > 0 ? " AND " : "") + "[fasc_classifcod]=\"" + custom_classiffasccod + "\"";
			else if ((custom_classiffasccod == null || custom_classiffasccod.equals("")) && custom_classiffasc != null && !custom_classiffasc.equals(""))
				query += (query.length() > 0 ? " AND " : "") + "[fasc_classifcod]=\"" + custom_classiffasc + "\"";
			
			if (fasc_stato != null) {
				if (fasc_stato.equals("A"))
					query += (query.length() > 0 ? " AND " : "") + "[fasc_stato]=\"aperto\"";
				else if (fasc_stato.equals("C"))
					query += (query.length() > 0 ? " AND " : "") + "[fasc_stato]=\"chiuso\"";
			}
			
			if (fasc_personale.equals("only"))
				query += (query.length() > 0 ? " AND " : "") + "[fasc_classifcod]=\"_FASCPERSONALEONLY_\"";
			else if (fasc_personale.equals("no"))
				query += (query.length() > 0 ? " AND " : "") + "NOT([fasc_classifcod]=\"_FASCPERSONALENO_\")";
			
			if (consistenza_fasc)
				cons_fasc = "true";
			if (consistenza_sottofasc)
				cons_sottofasc = "true";
						
			if (rif != null && rif.getNome_uff() != null && !rif.getNome_uff().equals(""))
				query += (query.length() > 0 ? " AND " : "") + "([fasc_rifinternirifnomeuff]=\" " +  rif.getNome_uff() + "\" ADJ [fasc_rifinternirifdiritto]=\"RPA\")";

			if (rif != null && rif.getNome_persona() != null && !rif.getNome_persona().equals(""))
				query += (query.length() > 0 ? " AND " : "") + "([fasc_rifinternirifnomepersona]=\" " +  rif.getNome_persona() + "\" ADJ [fasc_rifinternirifdiritto]=\"RPA\")";
			
			// Federico 07/12/06: cambiati i percorsi dei file delle stampe affinche' vengano trovati correttamente dal classloader [RW 0041111]
			String jReportInfo = "Stampe/xdocway/JasperReport/Repertorio_Fascicoli/repertorio_fascicoli.jasper%jasper%pdf%JRXW#";
			String jReportParams = "string#ANNO=" + anno + "&boolean#CONSISTENZA_FASC=" +  cons_fasc + "&boolean#CONSISTENZA_SOTTOFASC=" + cons_sottofasc;

			formsAdapter.stampaRepertorioFascicoli(jReportInfo, jReportParams, query);
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
	 * Lookup su UOR
	 * @return
	 * @throws Exception
	 */
	public String lookupUor() throws Exception {
		try {
			String value 		= rif.getNome_uff();
	
			String aliasName 	= "struint_nome";
			String aliasName1 	= "";
			String titolo 		= "xml,/struttura_interna/nome"; //titolo 
			String ord 			= "xml(xpart:/struttura_interna/nome)"; //ord
			String campi 		= ".@nome_uff=xml,/struttura_interna/nome ; .@cod_uff=xml,/struttura_interna/@cod_uff";
			String db 			= formsAdapter.getDefaultForm().getParam("aclDb"); //db 
			String newRecord 	= ""; //newRecord
			String xq			= ""; //extraQuery
			
			return customPrintLookup(rif, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Clear del lookup su UOR
	 * @return
	 * @throws Exception
	 */
	public String clearUor() throws Exception {
		String campi 		= ".@nome_uff=xml,/struttura_interna/nome ; .@cod_uff=xml,/struttura_interna/@cod_uff";
		
		return customClearPrintLookup(rif, campi);
	}
	
	/**
	 * Lookup su RPA
	 * @return
	 * @throws Exception
	 */
	public String lookupRpa() throws Exception {
		try {
			String value 		= rif.getNome_persona();
	
			String aliasName 	= "persint_nome";
			String aliasName1 	= "";
			String titolo 		= "xml,/persona_interna/@cognome xml,/persona_interna/@nome"; //titolo 
			String ord 			= "xml(xpart:/persona_interna/@cognome),xml(xpart:/persona_interna/@nome)"; //ord
			String campi 		= ".@nome_persona=xml,/persona_interna/@cognome xml,/persona_interna/@nome ; .@cod_persona=xml,/persona_interna/@cod_persona";
			String db 			= formsAdapter.getDefaultForm().getParam("aclDb"); //db 
			String newRecord 	= ""; //newRecord
			String xq			= ""; //extraQuery
			
			return customPrintLookup(rif, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Clear del lookup su RPA
	 * @return
	 * @throws Exception
	 */
	public String clearRpa() throws Exception {
		String campi 		= ".@nome_persona=xml,/persona_interna/@cognome xml,/persona_interna/@nome ; .@cod_persona=xml,/persona_interna/@cod_persona";
		
		return customClearPrintLookup(rif, campi);
	}
	
	/**
	 * Selezione di un check di opzioni di consistenza
	 * 
	 * @param vce
	 * @throws Exception
	 */
	public void consistenzaOptionValueChange(ValueChangeEvent vce) throws Exception {
		currentConsistenzaCheckId = vce.getComponent().getClientId();
	}
	
	/**
	 * Controllo delle opzioni di consistenza dei fascicoli selezionate
	 * dall'operatore
	 * 
	 * @return
	 * @throws Exception
	 */
	public String checkOpzioniConsistenza() throws Exception {
		if (currentConsistenzaCheckId.equals("templateForm:chkbox_consistenza_fasc") && !consistenza_fasc)
			consistenza_sottofasc = false;
		else if (currentConsistenzaCheckId.equals("templateForm:chkbox_consistenza_sottofasc") && consistenza_sottofasc) 
			consistenza_fasc = true;
			
		return null;
	}
	
}
