package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.beans.Errore;
import it.tredi.dw4.beans.Errormsg;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.FascicoloSpecialeInfo;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.OrdinamentoFascicoli_select;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.RomanConversion;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

public class QueryFascicolo extends DocWayQuery {
	
	//campi di ricerca
	private String fasc_numero;
	private String fasc_oggetto;
	private String fasc_soggetto;
	private String fasc_voceindice;
	private String fasc_scarto;
	private String fasc_note;
	private String fasc_anno;
	private String classif_infasc;
	private String fasc_classif;
	private String custom_classiffasccod;
	private String custom_da_numfasc;
	private String custom_a_numfasc;
	private boolean attivi = false;
	private boolean rpa = false;
	private boolean sottofascicoli = false;
	private boolean itf = false;
	private boolean cc = false;
	private String xml;
	private String rpanomeuff;
	private String rpanomepersona;
	private String itfnomeuff;
	private String itfnomepersona;
	private String ccnomeuff;
	private String ccnomepersona;
	private String range_fascstoriacreazionedata_from;
	private String range_fascstoriacreazionedata_to;
	private OrdinamentoFascicoli_select ordinamentoFascicoli_select = new OrdinamentoFascicoli_select();
	private String ordinamento;
	
	protected String codiceFascicoloCustom = "";
	protected String descrizioneFascicoloCustom = "";
	
	private boolean numUnicaFasc = false; // numerazioneUnicaFascicoli
	
	private String empty_classif = ""; // identifica se la classificazione deve essere fissa o meno nel form di ricerca
	private boolean nonClassificatoValid = false;
	private String nonClassificatoValidRPA = ""; // TODO ancora non gestito, aggiunto perche' caricato insieme a nonClassificatoValid
	
	private FascicoloSpecialeInfo fascicoloSpecialeInfo = new FascicoloSpecialeInfo(); // eventuale query su fascicolo speciale
	
	private String personalDirCliente = ""; // aggiunta di campi personalizzati specifici per il cliente
	
	private boolean showIfInsInFasc = false;
	
	protected final String DEFAULT_FASCICOLO_TITLE = "dw4.folders_search";
	protected String searchFascicoloTitle = DEFAULT_FASCICOLO_TITLE; // Titolo della pagina di inserimento del fascicolo (fascicolo, sottofascicolo, inserto, annesso)
	
	public String getFasc_oggetto() {
		return fasc_oggetto;
	}
	public void setFasc_oggetto(String fasc_oggetto) {
		this.fasc_oggetto = fasc_oggetto;
	}

	private DocDocWayQueryFormsAdapter formsAdapter;
	private String focusElement;
	
	public QueryFascicolo() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
		this.xml = dom.asXML();
		custom_classiffasccod = XMLUtil.parseStrictAttribute(dom, "/response/@classif_cod_infasc");
		classif_infasc = XMLUtil.parseStrictAttribute(dom, "/response/@classif_infasc");
		fasc_classif = classif_infasc;
		ordinamentoFascicoli_select.init(XMLUtil.createDocument(dom, "/response/ordinamentoFascicoli_select"));
		for (Iterator<Option> iterator = ordinamentoFascicoli_select.getOptions().iterator(); iterator.hasNext();) {
			Option option = (Option) iterator.next();
			if (option.getSelected().length()>0)
				ordinamento = option.getValue();
		}
		
		empty_classif = XMLUtil.parseStrictAttribute(dom, "/response/@empty_classif");
		if (empty_classif != null && empty_classif.toLowerCase().equals("si")) {
			custom_classiffasccod = "";
			fasc_classif = "";
		}
		
		sottofascicoli = formsAdapter.checkBooleanFunzionalitaDisponibile("mostraArticolazioneSottoFasc", false);
		
		fascicoloSpecialeInfo.init(XMLUtil.createDocument(dom, "/response/fascicolo_speciale_info"));
		
		// porzione di template personalizzato per il cliente
		personalDirCliente = XMLUtil.parseStrictAttribute(dom, "/response/@personalDirCliente");
		
		setNumUnicaFasc(StringUtil.booleanValue(FormsAdapter.getParameterFromCustomTupleValue("numerazioneUnicaFascicoli", formsAdapter.getDefaultForm().getParam("_cd"))));
		
		// attributi della response necessari alla generazione
		// della maschera di ricerca di un fascicolo
		String physDoc_infasc = XMLUtil.parseStrictAttribute(dom, "response/@physDoc_infasc");
		String classif_cod_infasc = XMLUtil.parseStrictAttribute(dom, "response/@classif_cod_infasc");
		String bAssegnaLinkFasc = XMLUtil.parseStrictAttribute(dom, "response/@bAssegnaLinkFasc");
		
		if ((physDoc_infasc.length() > 0 || bAssegnaLinkFasc.equals("true")) && !classif_cod_infasc.equals(""))
			showIfInsInFasc = true;
		
		// inizializzazione dei campi custom di ricerca
		getCustomQueryFields().init(dom);
		setCurrentCustomFieldSection("fascicolo");
		
		codiceFascicoloCustom = XMLUtil.parseStrictAttribute(dom, "/response/@codice_fasc");
		descrizioneFascicoloCustom = XMLUtil.parseStrictAttribute(dom, "/response/@descrizione_fasc");
		
		if (!StringUtil.booleanValue(empty_classif) && showIfInsInFasc) {
			nonClassificatoValid = true;
			nonClassificatoValidRPA = XMLUtil.parseStrictAttribute(dom, "/response/@cod_persona_infasc");
		}
    }	
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	@Override
	public String queryPlain() throws Exception {
		try {
			String query = createQuery();
			if (query != null) {
				formsAdapter.findplain();
				return queryPlain(query);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Creazione della query di ricerca fascicoli su eXtraWay in base ai 
	 * parametri specificati dall'operatore
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String createQuery() throws Exception {
		boolean ignoreClassifDescription = false;
		if (isPopupPage() && fasc_classif.length() > 0 && !StringUtil.booleanValue(empty_classif))
			ignoreClassifDescription = true; // in popup si sta eseguendo una fascicolazione.. la descrizione della classificazione puo' essere ignorata se specificato il codice
			
		return createQuery(ignoreClassifDescription);
	}
	
	/**
	 * Creazione della query di ricerca fascicoli su eXtraWay in base ai 
	 * parametri specificati dall'operatore
	 * 
	 * @param ignoreClassifDescription true se occorre non impostare il filtro su descrizione di classificazione, false altrimenti (default) 
	 * @return
	 * @throws Exception
	 */
	protected String createQuery(boolean ignoreClassifDescription) throws Exception {
		String query =  "";
		
		query +=  addQueryField("fasc_numero", this.escapeQueryValue(fasc_numero));
		query +=  addQueryField("fasc_oggetto", this.escapeQueryValue(fasc_oggetto));
		query +=  addQueryField("fasc_soggetto", this.escapeQueryValue(fasc_soggetto));
		query +=  addQueryField("fasc_voceindice", this.escapeQueryValue(fasc_voceindice));
		query +=  addQueryField("fasc_scarto", this.escapeQueryValue(fasc_scarto));
		query +=  addQueryField("fasc_note", this.escapeQueryValue(fasc_note));
		
		if (null != range_fascstoriacreazionedata_from && range_fascstoriacreazionedata_from.length()> 0){
			String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
			if (!DateUtil.isValidDate(range_fascstoriacreazionedata_from, formatoData)) {
				this.setErrorMessage("templateForm:range_fascstoriacreazionedata_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.datacreazione") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
				return null;
			}
			String query1="";
			if (null != range_fascstoriacreazionedata_to && range_fascstoriacreazionedata_to.length()> 0){
				if (!DateUtil.isValidDate(range_fascstoriacreazionedata_to, formatoData)) {
					this.setErrorMessage("templateForm:range_fascstoriacreazionedata_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.datacreazione") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
				query1 = "{"+range_fascstoriacreazionedata_from+"|"+range_fascstoriacreazionedata_to+"}";
			}
			else
				query1 = range_fascstoriacreazionedata_from;
			query +=  addQueryField("fascstoriacreazionedata", query1);
		}
		
//		query +=  addQueryField("custom_da_numfasc", custom_da_numfasc);
//		query +=  addQueryField("custom_a_numfasc", custom_a_numfasc);
//		query +=  addQueryField("fasc_classif", fasc_classif);
		
		if (null != rpanomeuff && rpanomeuff.trim().length()>0) query += "(([fasc_rifinternirifnomeuff]=" + this.escapeQueryValue(rpanomeuff) + ") adj ([fasc_rifinternirifdiritto]=RPA)) AND ";
		if (null != rpanomepersona && rpanomepersona.trim().length()>0) query += "(([fasc_rifinternirifnomepersona]=" + this.escapeQueryValue(rpanomepersona) + ") adj ([fasc_rifinternirifdiritto]=RPA)) AND ";
		if (null != itfnomeuff && itfnomeuff.trim().length()>0) query += "(([fasc_rifinternirifnomeuff]=" + this.escapeQueryValue(itfnomeuff) + ") adj ([fasc_rifinternirifdiritto]=ITF)) AND ";
		if (null != itfnomepersona && itfnomepersona.trim().length()>0) query += "(([fasc_rifinternirifnomepersona]=" + this.escapeQueryValue(itfnomepersona) + ") adj ([fasc_rifinternirifdiritto]=ITF)) AND ";
		if (null != ccnomeuff && ccnomeuff.trim().length()>0) query += "(([fasc_rifinternirifnomeuff]=" + this.escapeQueryValue(ccnomeuff) + ") adj ([fasc_rifinternirifdiritto]=CC)) AND ";
		if (null != ccnomepersona && ccnomepersona.trim().length()>0) query += "(([fasc_rifinternirifnomepersona]=" + this.escapeQueryValue(ccnomepersona) + ") adj ([fasc_rifinternirifdiritto]=CC)) AND ";
		
		if ( custom_classiffasccod.length() > 0 )
			custom_classiffasccod = convertClassifCod(custom_classiffasccod);
		
		if ( custom_da_numfasc.length() > 0) { //da_numero pieno (ricerca per numero)
            
			if (custom_classiffasccod.length() > 0 || numUnicaFasc) { //classif pieno o numerazione unica fascicoli
                String anno = "_ANNOCORRENTE_";
                if ( fasc_anno.length() > 0 )	//anno_fasc pieno
                    anno = fasc_anno;

                String da_numfasc = fillString(custom_da_numfasc, "0", 5);
                String a_numfasc = "";
                if (custom_a_numfasc.length() > 0)
                	a_numfasc = fillString(custom_a_numfasc, "0", 5);

                String classifPart = (numUnicaFasc) ? "" : custom_classiffasccod + ".";
                String query1 = null;
                if ( a_numfasc.length() == 0 ) // 'da' pieno e 'a' vuoto
                    query1 = anno + "-" + "_CODSEDE_-" + classifPart + da_numfasc;
                else //'da' e 'a'
                    query1 = "{" + anno + '-' + "_CODSEDE_-" + classifPart + da_numfasc + "|" +
                    anno + "-" + "_CODSEDE_-" + classifPart + a_numfasc + "}";
                query +=  addQueryField("fasc_numero", query1);
	        }
            else {
            	Errormsg errormsg = new Errormsg();
    			errormsg.setActive(true);
    			Errore error = new Errore();
    			error.setLevel("warning");
    			error.setErrtype(I18N.mrs("dw4.ricerca_per_numero_di_fascicolo_ignorata_Occorre_valorizzare_il_codice_di_classificazione"));
    			errormsg.setErrore(error);
    			
    			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    			session.setAttribute("errormsg", errormsg);
    			return null;
            }
        }
		 else {
			 String query1 = "";
            if ( fasc_anno.length() > 0 )	//ricerca per anno
                query1 = "[fasc_anno]=" + fasc_anno;
            if ( custom_classiffasccod.length() > 0 )	//ricerca per classificazione
                if ( query1.length() == 0 ) query1 = "[fasc_classifcod]=" + custom_classiffasccod;
                else query1 = "(" + query1 + ") AND ([fasc_classifcod]=" + custom_classiffasccod+ ")";
            if ( fasc_classif.length() > 0 && !ignoreClassifDescription)	//ricerca per classificazione
                if ( query1.length() == 0 ) query1 = "[fasc_classif]=" + this.escapeQueryValue(fasc_classif);
                else query1 = "(" + query1 + ") AND ([fasc_classif]=" + this.escapeQueryValue(fasc_classif) + ")";
            
            if ( query1.length() > 0 ) {
            	//se si proviene da inserimento di documento in un fascicolo occorre estendere la ricerca anche
                //ai fascioli non classificati (compresa ricerca per anno e numero)
                if (nonClassificatoValid)
                	query1 = addPhraseForNotClassif(query1);
                
                if ( query.length() == 0 ) query = query1+" AND ";
                else query = query + "(" + query1 + ") AND ";
            }
        }
		
		if (rpa) {
			boolean copiaInFasc = this.formsAdapter.getDefaultForm().getParam("bAssegnaLinkFasc").equals("true");
			String nonClassificatoValidRPA = formsAdapter.getDefaultForm().getParam("nonClassificatoValidRPA");
			String query1 = "";
			if ( !copiaInFasc && nonClassificatoValidRPA!= null && nonClassificatoValidRPA.length() > 0) //si proviene da inserimento di doc in fascicolo
                query1 = "([fasc_rifinternirifcodpersona]=\"" + nonClassificatoValidRPA + "\") adj ([fasc_rifinternirifdiritto]=RPA)";
            else
                query1 = "([fasc_rifinternirifcodpersona]=\"_CODPERSONA_\") adj ([fasc_rifinternirifdiritto]=RPA)";

            if ( query.length() == 0 ) 	query = query1 + " AND ";
            else 						query = query +"(" + query1 + ") AND ";
//			query += "(([fasc_rifinternirifcodpersona]=\"_CODPERSONA_\") adj ([fasc_rifinternirifdiritto]=RPA)) AND ";
		}
		if (cc)  {
			/* TODO
			 if ( typeof(getForm('theForm')['nonClassificatoValidRPA']) != 'undefined' ) //si proviene da inserimento di doc in fascicolo
            	query1 = "([fasc_rifinternirifcodpersona]=\"" + getForm('theForm')['nonClassificatoValidRPA'].value + "\") adj ([fasc_rifinternirifdiritto]=CC)";
            else
                query1 = "([fasc_rifinternirifcodpersona]=\"_CODPERSONA_\") adj ([fasc_rifinternirifdiritto]=CC)";

            if ( query.length() == 0 ) 	query = query1;
            else 						query = query +"(" + query1 + ") AND ";
			 */
			
			query += "(([fasc_rifinternirifcodpersona]=\"_CODPERSONA_\") adj ([fasc_rifinternirifdiritto]=CC)) AND ";
		}
		if (itf)  {
			/* TODO:
			String query1 = ""; 
			if ( typeof(getForm('theForm')['nonClassificatoValidRPA']) != 'undefined' ) //si proviene da inserimento di doc in fascicolo
                query1 = "([fasc_rifinternirifcodpersona]=\"" + getForm('theForm')['nonClassificatoValidRPA'].value + "\") adj ([fasc_rifinternirifdiritto]=ITF)";
            else
                query1 = "([fasc_rifinternirifcodpersona]=\"_CODPERSONA_\") adj ([fasc_rifinternirifdiritto]=ITF)";

            if ( query.length() == 0 ) 	query = query1;
            else 						query = query +"(" + query1 + ") AND ";
			 */
			query += "(([fasc_rifinternirifcodpersona]=\"_CODPERSONA_\") adj ([fasc_rifinternirifdiritto]=ITF)) AND ";
		}
		
		if (codiceFascicoloCustom != null && codiceFascicoloCustom.length() > 0) {
			query += "[xml,/fascicolo/tipologia/@cod]=\"" + codiceFascicoloCustom + "\" AND ";
		}
		
		if (attivi)  query += "([fasc_stato]=aperto) AND ";
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "[UD,/xw/@UdType]=fascicolo";
		
		// costruzione del filtro di ricerca per i campi custom
    	String filtroCustomFields = getCustomQueryFields().createQuery(getCurrentCustomFieldSection());
    	if (filtroCustomFields != null && filtroCustomFields.length() > 0)
    		query = query + " AND " + filtroCustomFields;
		
		this.formsAdapter.getDefaultForm().addParam("qord", ordinamento);
		if (sottofascicoli) this.formsAdapter.getDefaultForm().addParam("fascicoli_MostraGerarchia", "true");
		
		return query;
	}
	
	/**
	 * aggiunta di filtri su non classificato (se abilitato) in caso di ricerche su classificazione. codice recuperato
	 * da javascript di docway3
	 * 
	 * @param query
	 * @return
	 */
	private String addPhraseForNotClassif(String query) {
		String query1 = "";
		
		String anno_fasc = this.fasc_anno;
		String classifcod = "00/00";
		
		String da_numfasc = custom_da_numfasc;
		if (da_numfasc.length() > 0) {
			String a_numfasc = custom_a_numfasc;
			String anno = "";
			if (anno_fasc.length() > 0)
				anno = anno_fasc;
			else if (a_numfasc.length() > 0) {
				a_numfasc = fillMultiString(custom_a_numfasc, "0", 4, ".");
				anno = "_ANNOCORRENTE_";
			}
			else
				anno = "_ANNOCORRENTENORANGE_";
			
			da_numfasc = fillMultiString(custom_a_numfasc, "0", 4, ".");
			
			// Federico 21/11/07: in caso di numerazione unica dei fascicoli, si deve poter cercare per numero senza indicare
	        // la classificazione [RW 0048503]
			String classifPart  = (numUnicaFasc) ? "" : classifcod + ".";
			
			if (a_numfasc.length() == 0) // 'da' pieno e 'a' vuoto
	            query1 = "[fasc_numero]=" + anno + "-" + "_CODSEDE_-" + classifPart + da_numfasc;
	        else //'da' e 'a'
	            query1 = "[fasc_numero]={" + anno + "-" + "_CODSEDE_-" + classifPart + da_numfasc + "|" + anno + "-" + "_CODSEDE_-" + classifPart + a_numfasc + "}";
	        query = query.replace("[fasc_numero]", query1 + " OR [fasc_numero]");
		}
		else {
			if (classifcod.length() > 0)	//ricerca per classificazione
				query = query.replace("[fasc_classifcod]", "[fasc_classifcod]=" + classifcod + " OR [fasc_classifcod]");
		}
		
		return query;
	}
	
	/**
	 * Esecuzione della query di ricerca costruita
	 */
	public String queryPlain(String query) throws Exception {
		try {
			XMLDocumento response = super._queryPlain(query, "", "@fascicolo");
			
			if (handleErrorResponse(response)) 
				return null;
			
			return navigateResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}
	
	public String getFocusElement() {
		return focusElement;
	}
	
	public String openIndexOggettoFascicolo() throws Exception {
		this.focusElement = "fasc_oggetto";
		this.openIndex("fasc_oggetto", this.fasc_oggetto, "0", " ", false);
		return null;
	}
	
	public String openIndexSoggettoFascicolo() throws Exception {
		this.focusElement = "fasc_soggetto";
		this.openIndex("fasc_soggetto", this.fasc_soggetto, "0", " ", false);
		return null;
	}
	
	public String openIndexVoceIndiceFascicolo() throws Exception {
		this.focusElement = "fasc_voceindice";
		this.openIndex("fasc_voceindice", this.fasc_voceindice, "0", " ", false);
		return null;
	}
	
	public String openIndexNoteFascicolo() throws Exception {
		this.focusElement = "fasc_note";
		this.openIndex("fasc_note", this.fasc_note, "0", "", false);
		return null;
	}
	
	public String openIndexAnnoFascicolo() throws Exception {
		this.focusElement = "fasc_anno";
		this.openIndex("fasc_anno", this.fasc_anno, "0", "", false);
		return null;
	}
	
	public String openIndexClassifFascicolo() throws Exception {
		this.focusElement = "fasc_classif";
		this.openIndex("fasc_classif", this.fasc_classif, "0", " ", false);
		return null;
	}
	
	public String openIndexUORFascicolo() throws Exception {
		this.focusElement = "rpanomeuff";
		this.openIndex("rpanomeuff", "fasc_rifinternirifdirittonomeuff", this.rpanomeuff, "0", "RPA|^| ", false);
		return null;
	}
	
	public String openIndexRPAFascicolo() throws Exception {
		this.focusElement = "rpanomepersona";
		this.openIndex("rpanomepersona", "fasc_rifinternirifdirittonomepersona", this.rpanomepersona, "0", "RPA|^| ", false);
		return null;
	}
	
	public String openIndexITFUffFascicolo() throws Exception {
		this.focusElement = "itfnomeuff";
		this.openIndex("itfnomeuff", "fasc_rifinternirifdirittonomeuff", this.itfnomeuff, "0", "ITF|^| ", false);
		return null;
	}
	
	public String openIndexITFPersonaFascicolo() throws Exception {
		this.focusElement = "itfnomepersona";
		this.openIndex("itfnomepersona","fasc_rifinternirifdirittonomepersona", this.itfnomepersona, "0", "ITF|^| ", false);
		return null;
	}
	
	public String openIndexCCUffFascicolo() throws Exception {
		this.focusElement = "ccnomeuff";
		this.openIndex("ccnomeuff", "fasc_rifinternirifdirittonomeuff", this.ccnomeuff, "0", "CC|^| ", false);
		return null;
	}
	
	public String openIndexCCPersonaFascicolo() throws Exception {
		this.focusElement = "ccnomepersona";
		this.openIndex("ccnomepersona", "fasc_rifinternirifdirittonomepersona", this.ccnomepersona, "0", "CC|^| ", false);
		return null;
	}
	
	public void setFasc_soggetto(String fasc_soggetto) {
		this.fasc_soggetto = fasc_soggetto;
	}
	
	public String getFasc_soggetto() {
		return fasc_soggetto;
	}
	
	public void setFasc_voceindice(String fasc_voceindice) {
		this.fasc_voceindice = fasc_voceindice;
	}
	
	public String getFasc_voceindice() {
		return fasc_voceindice;
	}
	
	public void setFasc_note(String fasc_note) {
		this.fasc_note = fasc_note;
	}
	
	public String getFasc_note() {
		return fasc_note;
	}
	
	public void setFasc_scarto(String fasc_scarto) {
		this.fasc_scarto = fasc_scarto;
	}
	
	public String getFasc_scarto() {
		return fasc_scarto;
	}
	
	public void setAttivi(boolean attivi) {
		this.attivi = attivi;
	}
	
	public boolean isAttivi() {
		return attivi;
	}
	
	public void setRpa(boolean rpa) {
		this.rpa = rpa;
	}
	
	public boolean isRpa() {
		return rpa;
	}
	
	public void setSottofascicoli(boolean sottofascicoli) {
		this.sottofascicoli = sottofascicoli;
	}
	
	public boolean isSottofascicoli() {
		return sottofascicoli;
	}
	
	public void setCc(boolean cc) {
		this.cc = cc;
	}
	
	public boolean isCc() {
		return cc;
	}
	
	public void setItf(boolean incaricato) {
		this.itf = incaricato;
	}
	
	public boolean isItf() {
		return itf;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getXml() {
		return xml;
	}
	
	public void setFasc_numero(String fasc_numero) {
		this.fasc_numero = fasc_numero;
	}
	
	public String getFasc_numero() {
		return fasc_numero;
	}
	
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
			return "showtitles@fascicolo";
		}
	}
	
	public void setFasc_anno(String fasc_anno) {
		this.fasc_anno = fasc_anno;
	}
	
	public String getFasc_anno() {
		return fasc_anno;
	}
	
	public void setFasc_classif(String fasc_classif) {
		this.fasc_classif = fasc_classif;
	}
	
	public String getFasc_classif() {
		return fasc_classif;
	}
	
	public String getClassif_infasc() {
		return classif_infasc;
	}
	
	public void setClassif_infasc(String classif_infasc) {
		this.classif_infasc = classif_infasc;
	}
	
	public void setCustom_da_numfasc(String custom_da_numfasc) {
		this.custom_da_numfasc = custom_da_numfasc;
	}
	
	public String getCustom_da_numfasc() {
		return custom_da_numfasc;
	}
	
	public void setCustom_a_numfasc(String custom_a_numfasc) {
		this.custom_a_numfasc = custom_a_numfasc;
	}
	
	public String getCustom_a_numfasc() {
		return custom_a_numfasc;
	}
	
	public String fillMultiString(String stringa, String carattere, int lunghezza, String separator)
	{
	    String ret = "";
	    String[] vect = stringa.split(separator);
	    for ( int i = 0; i < vect.length; i++ )
	        ret += separator + fillString(vect[i], carattere, lunghezza);
	    return ret;
	}
	
	public String fillString(String stringa, String carattere, int lunghezza)
	{
	    int start = stringa.length();
	    for ( int i = start; i < lunghezza; i++ )
	        stringa = carattere + stringa;
	    return stringa;
	}
	
	public void setCustom_classiffasccod(String fasc_codclassif) {
		this.custom_classiffasccod = fasc_codclassif;
	}
	
	public String getCustom_classiffasccod() {
		return custom_classiffasccod;
	}
	
	public String convertClassifCod(String stringa)
	{
	    //TODO:
//		if(getClassifFormat() != null){
//			return unformatClassif(stringa);
//		 }

	    String ret = "";
	    String[] vect = stringa.split("/");
	    for ( int i = 0; i < vect.length; i++ ) {
	    	String part = vect[i];
	        try{
	        	part = String.valueOf(RomanConversion.valueOf(part));
	        } catch(IllegalArgumentException ex){}
	    	
	        if ( part.indexOf("*") == -1 && part.indexOf("?") == -1 )
	            part = fillString(part, "0", 2);
	        ret += ((ret.length()>0) ? "/" : "") + part;
	    }
	    return ret;
	}
	
	public void setRpanomeuff(
			String fasc_rifinternirifdirittonomeuff) {
		this.rpanomeuff = fasc_rifinternirifdirittonomeuff;
	}
	
	public String getRpanomeuff() {
		return rpanomeuff;
	}
	
	public void setRpanomepersona(String rpanomepersona) {
		this.rpanomepersona = rpanomepersona;
	}
	
	public String getRpanomepersona() {
		return rpanomepersona;
	}
	
	public void setItfnomeuff(String itfnomeuff) {
		this.itfnomeuff = itfnomeuff;
	}
	
	public String getItfnomeuff() {
		return itfnomeuff;
	}
	
	public void setCcnomeuff(String ccnomeuff) {
		this.ccnomeuff = ccnomeuff;
	}
	
	public String getCcnomeuff() {
		return ccnomeuff;
	}
	
	public void setItfnomepersona(String itfnomepersona) {
		this.itfnomepersona = itfnomepersona;
	}
	
	public String getItfnomepersona() {
		return itfnomepersona;
	}
	
	public void setCcnomepersona(String ccnomepersona) {
		this.ccnomepersona = ccnomepersona;
	}
	
	public String getCcnomepersona() {
		return ccnomepersona;
	}
	
	public void setRange_fascstoriacreazionedata_from(
			String range_fascstoriacreazionedata_from) {
		this.range_fascstoriacreazionedata_from = range_fascstoriacreazionedata_from;
	}
	
	public String getRange_fascstoriacreazionedata_from() {
		return range_fascstoriacreazionedata_from;
	}
	
	public void setRange_fascstoriacreazionedata_to(
			String range_fascstoriacreazionedata_to) {
		this.range_fascstoriacreazionedata_to = range_fascstoriacreazionedata_to;
	}
	
	public String getRange_fascstoriacreazionedata_to() {
		return range_fascstoriacreazionedata_to;
	}
	
	public void setOrdinamentoFascicoli_select(
			OrdinamentoFascicoli_select ordinamentoFascicoli_select) {
		this.ordinamentoFascicoli_select = ordinamentoFascicoli_select;
	}
	
	public OrdinamentoFascicoli_select getOrdinamentoFascicoli_select() {
		return ordinamentoFascicoli_select;
	}
	
	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}
	
	public String getOrdinamento() {
		return ordinamento;
	}
	
	public String getEmpty_classif() {
		return empty_classif;
	}
	
	public void setEmpty_classif(String empty_classif) {
		this.empty_classif = empty_classif;
	}
	
	public FascicoloSpecialeInfo getFascicoloSpecialeInfo() {
		return fascicoloSpecialeInfo;
	}
	
	public void setFascicoloSpecialeInfo(FascicoloSpecialeInfo fascicoloSpecialeInfo) {
		this.fascicoloSpecialeInfo = fascicoloSpecialeInfo;
	}
	
	public String getPersonalDirCliente() {
		return personalDirCliente;
	}
	
	public void setPersonalDirCliente(String personalDirCliente) {
		this.personalDirCliente = personalDirCliente;
	}
	
	public boolean isShowIfInsInFasc() {
		return showIfInsInFasc;
	}
	
	public void setShowIfInsInFasc(boolean showIfInsInFasc) {
		this.showIfInsInFasc = showIfInsInFasc;
	}
	
	public boolean isNumUnicaFasc() {
		return numUnicaFasc;
	}
	
	public void setNumUnicaFasc(boolean numUnicaFasc) {
		this.numUnicaFasc = numUnicaFasc;
	}
	
	public boolean isNonClassificatoValid() {
		return nonClassificatoValid;
	}
	
	public void setNonClassificatoValid(boolean nonClassificatoValid) {
		this.nonClassificatoValid = nonClassificatoValid;
	}
	
	public String getNonClassificatoValidRPA() {
		return nonClassificatoValidRPA;
	}
	
	public void setNonClassificatoValidRPA(String nonClassificatoValidRPA) {
		this.nonClassificatoValidRPA = nonClassificatoValidRPA;
	}
	
	/**
	 * Inserimento di un nuovo fascicolo
	 * @return
	 * @throws Exception
	 */
	public String insTableDocFascicolo() throws Exception {
		formsAdapter.insTableDoc(Const.DOCWAY_TIPOLOGIA_FASCICOLO); 

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_FASCICOLO, responseDoc, this.isPopupPage());
	}
	
	/**
	 * reset del form di ricerca (pulsante 'pulisci')
	 */
	public String resetQuery(){
		fasc_numero="";
		fasc_oggetto="";
		fasc_soggetto="";
		fasc_voceindice="";
		fasc_scarto="";
		fasc_note="";
		fasc_anno="";
		fasc_classif="";
		custom_classiffasccod="";
		custom_da_numfasc="";
		custom_a_numfasc="";
		rpanomeuff="";
		rpanomepersona="";
		itfnomeuff="";
		itfnomepersona="";
		ccnomeuff="";
		ccnomepersona="";
		range_fascstoriacreazionedata_from="";
		range_fascstoriacreazionedata_to="";
		
		// pulizia di evenutali campi custom presenti nel form
		getCustomQueryFields().cleanCustomFields();
		
		return null;
	}
	
	public String refine() throws Exception {
		try {
			String query = createQuery();
			if (query != null) {
				formsAdapter.refineQuery(formsAdapter.getLastResponse().getAttributeValue("/response/@selid"));
				XMLDocumento response = super._queryPlain(query, "", "");
				if (handleErrorResponse(response)) {
					formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
					return null;
				}
				
				return navigateResponse(response);
			}
			
			return "";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Switch del form di ricerca da generico a specifico del fascicolo speciale
	 * @return
	 * @throws Exception
	 */
	public String switchToTableFS() throws Exception {
		try {
			formsAdapter.switchToTableFS(fascicoloSpecialeInfo.getId());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			return buildSpecificQueryPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Switch del form di ricerca da specifico del fascicolo speciale a generico
	 * @return
	 * @throws Exception
	 */
	public String goToTableQ() throws Exception {
		try {
			formsAdapter.gotoTableQ("fascicolo", false);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificQueryPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String getSearchFascicoloTitle() {
		if (searchFascicoloTitle == null || searchFascicoloTitle.length() == 0)
			searchFascicoloTitle = DEFAULT_FASCICOLO_TITLE;
		
		if (descrizioneFascicoloCustom == null || descrizioneFascicoloCustom.equals(""))
			return I18N.mrs(searchFascicoloTitle);
		else
			return descrizioneFascicoloCustom + " - " + I18N.mrs("acl.search");
	}
}
