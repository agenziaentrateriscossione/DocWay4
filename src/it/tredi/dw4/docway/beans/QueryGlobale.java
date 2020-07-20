package it.tredi.dw4.docway.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

import it.tredi.dw4.acl.model.Societa;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.LivelloVisibilita;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.Ordinamento_select;
import it.tredi.dw4.docway.model.Tabella;
import it.tredi.dw4.docway.model.TitoloRepertorio;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.RomanConversion;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class QueryGlobale extends DocWayQuery {
	
	private String xml;
	
	//campi di ricerca
	private boolean annullati;
	private boolean nonannullati;
	private boolean conservati;
	private boolean nonconservati;
	private boolean bozze;
	private boolean custom_non_repertoriati;
	private boolean differito;
	private boolean da_firmare;
	private boolean workflow_stato_attivo;
	private boolean workflow_stato_concluso;
	private boolean workflow_stato_cancellato;
	private String custom_globale = "";
	private String custom_da_num_prot = "";
	private String custom_a_num_prot = "";
	private String custom_numeri_prot = "";
	private String custom_repertorio = "";
	private String custom_rep_da = "";
	private String custom_rep_a = "";
	private String range_docdataprot_from = "";
	private String range_docdataprot_to = "";
	private String doc_anno = "";
	private String repertorio_anno = "";
	private String tipologia_repertorio = "";
	private String doc_voceindice = "";
	private String doc_oggetto = "";
	private String doc_autore = "";
	private String doc_keywords = "";
	private String doc_allegato = "";
	private String custom_doc_classifcod = "";
	private String custom_doc_classif = "";
	private String doc_note = "";
	private String doc_postit = "";
	private String rpanomeuff = "";
	private String rpanomepersona = "";
	private String rpamnomeuff = "";
	private String rpamnomepersona = "";
	private String opnomeuff = "";
	private String opnomepersona = "";
	private String cdsnomeuff = "";
	private String cdsnomepersona = "";
	private String ccnomeuff = "";
	private String ccnomepersona = "";
	private String umnomepersona = "";
	private String umnomeuff = "";
	private String doc_rifesternirifnome = "";
	private String codice_fiscale = "";
	private String partita_iva = "";
	private String range_docstoriacreazionedata_from = "";
	private String range_docstoriacreazionedata_to = "";
	private String doc_storiacreazioneoper = "";
	private String doc_storiacreazioneuffoper = "";
	private String doc_storiaprotocollazioneoper = "";
	private String doc_storiaprotocollazioneuffoper = "";
	private String rangenum_docnrecord_from = "";
	private String rangenum_docnrecord_to = "";
	private String range_docpubblicazioneal_from = "";
	private String range_docpubblicazioneal_to = "";
	private String richiedenteEsterno = "";
	private String richiedenteInterno = "";
	private String custom_numeri_rep = "";
	private String workflow_label = "";
	private Ordinamento_select ordinamento_select = new Ordinamento_select();
	private List<Option> arrivo_mezzo_trasmissione_select;
	private List<Option> arrivo_tipologia_select;
	private List<TitoloRepertorio> listof_rep;
	private String ordinamento = "";
	private String mezzo_trasmissione = "";
	private String tipologia = "";
	private boolean nonbozze;
	private boolean non_repertoriati;
	private boolean arrivo;
	private boolean partenza;
	private boolean interno;
	private boolean varie;
	private String radio ="1";
	private String doc_rifesternirifreferentenominativo = ""; 
	private String doc_rifesternirifreferenteruolo = "";
	private String doc_rifestrenirifnprot = "";
	private String range_docrifesternirifdataprot_from = "";
	private String range_docrifesternirifdataprot_to = "";
	private boolean custom_cc;
	private boolean interoperabilita;
	private boolean pec;
	private boolean repertori_multipli;
	private boolean escludi_our;
	private boolean escludi_rpa;
	
	private boolean estremi_protocollo 		= true;
	private boolean dati_documento 			= true;
	private boolean responsabilita			= false;
	private boolean informazioni_servizio 	= false;
	private boolean workflows_info 			= false;
	
	// gestione multisocieta'
	private List<Societa> societaSelect = new ArrayList<Societa>();
	private String codSocieta = "";
	
	// gestione campi custom
	private String customSelect1 = "";
	private String customSelect2 = "";
	private List<Option> customSelect1_select = new ArrayList<Option>();
	private List<Option> customSelect2_select = new ArrayList<Option>();
	
	// ricerca su visibilita
	private List<LivelloVisibilita> livelliVisibilita = new ArrayList<LivelloVisibilita>();
	
	private DocDocWayQueryFormsAdapter formsAdapter;
	private String focusElement;
	
	public QueryGlobale() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.xml = dom.asXML();
		this.ordinamento_select.init(XMLUtil.createDocument(dom, "/response/ordinamento_select"));
		this.arrivo_mezzo_trasmissione_select = XMLUtil.parseSetOfElement(dom, "response/arrivo_mezzo_trasmissione_select/option", new Option());
		this.arrivo_tipologia_select = XMLUtil.parseSetOfElement(dom, "response/arrivo_tipologia_select/option", new Option());
		this.listof_rep = XMLUtil.parseSetOfElement(dom, "response/listof_rep/repertorio", new TitoloRepertorio());
		this.customSelect1_select = XMLUtil.parseSetOfElement(dom, "/response/select_customSelect1/option", new Option()); // viene valorizzato solo il parametro 'value'
		this.customSelect2_select = XMLUtil.parseSetOfElement(dom, "/response/select_customSelect2/option", new Option()); // viene valorizzato solo il parametro 'value'
		
		this.custom_repertorio = "";
		TitoloRepertorio vuoto = new TitoloRepertorio();
		vuoto.setCodice("");
		vuoto.setDescrizione("");
		this.listof_rep.add(0, vuoto);
		
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
		
		// caricamento dati per ricerca su doc riservati
		this.livelliVisibilita = XMLUtil.parseSetOfElement(dom, "/response/livelliVisibilita/visibilita", new LivelloVisibilita());
		
		// commentato perche' non dovrebbe essere necessario impostare un valore di 
		// default in ricerca... a meno che non ci sia un reffinamento che deve ricaricare
		// il form
		/*for (Iterator<Option> iterator = arrivo_mezzo_trasmissione_select.iterator(); iterator.hasNext();) {
			Option option = (Option) iterator.next();
			if (option.getSelected().length()>0)
				mezzo_trasmissione = option.getValue();
		}
		for (Iterator<Option> iterator = arrivo_tipologia_select.iterator(); iterator.hasNext();) {
			Option option = (Option) iterator.next();
			if (option.getSelected().length()>0)
				tipologia = option.getValue();
		}*/
		
		// inizializzazione dei campi custom di ricerca
		getCustomQueryFields().init(dom);
    }	
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	@Override
	public String queryPlain() throws Exception {
		try {
			String query = createQuery();
			if("error".equals(query)) return null;
			
			formsAdapter.findplain(codSocieta);
			
			return queryPlain(query);
			
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Creazione della query globale su eXtraWay in base ai parametri specificati dall'operatore
	 * @return
	 * @throws Exception
	 */
	public String createQuery() throws Exception{
		String query =  "";
		//ricerca globale
	    if(custom_globale.length()>0) {
	    	if ("1".equals(radio)){
		    	String query1 = this.escapeQueryValue(custom_globale) + " OR [doc_filesfiletesto]=" + this.escapeQueryValue(custom_globale);
		    	query +=  addQueryField("@", query1);
	    	}
	    	else if ("2".equals(radio)){
	    		query +=  addQueryField("@", this.escapeQueryValue(this.custom_globale));
	    	}
	    	else if ("3".equals(radio)){
	    	    query +=  addQueryField("doc_filesfiletesto", this.escapeQueryValue(this.custom_globale));
	    	}
	    }
	    query +=  addQueryField("doc_voceindice", this.escapeQueryValue(this.doc_voceindice));
	    query +=  addQueryField("doc_oggetto", this.escapeQueryValue(this.doc_oggetto));
	    query +=  addQueryField("doc_autore", this.escapeQueryValue(this.doc_autore));
	    query +=  addQueryField("doc_keywords", this.escapeQueryValue(this.doc_keywords));
	    query +=  addQueryField("xml,/doc/allegato", this.escapeQueryValue(this.doc_allegato));
	    query +=  addQueryField("doc_note", this.escapeQueryValue(this.doc_note));
	    query +=  addQueryField("xml,/doc/postit", this.escapeQueryValue(this.doc_postit));
	    
	    if (this.mezzo_trasmissione != null && !this.mezzo_trasmissione.equals(""))
	    	query += "([doc_mezzotrasmissionecod]=\"" + this.mezzo_trasmissione + "\") AND ";
	    //query +=  addQueryField("doc_mezzotrasmissionecod", this.escapeQueryValue(this.mezzo_trasmissione));
	    
	    if (this.tipologia != null && !this.tipologia.equals(""))
	    	query += "([doc_tipologiacod]=\"" + this.tipologia + "\") AND ";
	    //query +=  addQueryField("doc_tipologiacod", this.escapeQueryValue(this.tipologia));
	    
	    // query su campi custom (customSelect1, customSelect2)
	    if (this.customSelect1 != null && !this.customSelect1.equals(""))
	    	query += "([xml,/doc/extra/customSelect1]=\"" + this.customSelect1 + "\") AND ";
	    if (this.customSelect2 != null && !this.customSelect2.equals(""))
	    	query += "([xml,/doc/extra/customSelect2]=\"" + this.customSelect2 + "\") AND ";
	    
	    if (arrivo || partenza){
		    query +=  addQueryField("doc_rifesternirifnome", this.escapeQueryValue(this.doc_rifesternirifnome));
		    query +=  addQueryField("xml,/doc/rif_esterni/rif/@codice_fiscale", this.escapeQueryValue(this.codice_fiscale));
		    query +=  addQueryField("xml,/doc/rif_esterni/rif/@partita_iva", this.escapeQueryValue(this.partita_iva));
	    }
	    query +=  addQueryField("doc_storiacreazioneoper", this.escapeQueryValue(this.doc_storiacreazioneoper));
	    query +=  addQueryField("doc_storiacreazioneuffoper", this.escapeQueryValue(this.doc_storiacreazioneuffoper));
	    query +=  addQueryField("xml,/doc/storia/protocollazione/@oper/", this.escapeQueryValue(this.doc_storiaprotocollazioneoper));
	    query +=  addQueryField("xml,/doc/storia/protocollazione/@uff_oper/", this.escapeQueryValue(this.doc_storiaprotocollazioneuffoper));
	    query +=  addQueryField("xml,/doc/rif_esterni/rif/nome", this.escapeQueryValue(this.richiedenteEsterno));
	    query +=  addQueryField("albo_ric_interno", this.escapeQueryValue(this.richiedenteInterno));
	    query +=  addQueryField("xml,/doc/workflows/Instance/@label", this.escapeQueryValue(this.workflow_label));

	    if (arrivo || partenza){
		    if (custom_cc && this.doc_rifesternirifreferentenominativo.trim().length()>0){
	    		String query1 = "(([doc_rifesternirifreferentenominativo]=" + this.doc_rifesternirifreferentenominativo+") adj ([doc_rifesternirifcopiaconoscenza]=si)) AND ";
		    	query +=  query1;
		    }
		    else query +=  addQueryField("doc_rifesternirifreferentenominativo", this.escapeQueryValue(this.doc_rifesternirifreferentenominativo));
		    query +=  addQueryField("doc_rifesternirifreferenteruolo", this.escapeQueryValue(this.doc_rifesternirifreferenteruolo));
		    query +=  addQueryField("doc_rifestrenirifnprot", this.escapeQueryValue(this.doc_rifestrenirifnprot));
	    }
	    
		if (null != rpanomeuff && rpanomeuff.trim().length()>0) query += "(([doc_rifinternirifnomeuff]="+rpanomeuff+") adj ([doc_rifinternirifdiritto]=RPA)) AND ";
		if (null != rpanomepersona && rpanomepersona.trim().length()>0) query += "(([doc_rifinternirifnomepersona]="+rpanomepersona+") adj ([doc_rifinternirifdiritto]=RPA)) AND ";
		if (interno && null != rpamnomeuff && rpamnomeuff.trim().length()>0) query += "(([doc_rifinternirifnomeuff]="+rpamnomeuff+") adj ([doc_rifinternirifdiritto]=RPAM)) AND ";
		if (interno && null != rpamnomepersona && rpamnomepersona.trim().length()>0) query += "(([doc_rifinternirifnomepersona]="+rpamnomepersona+") adj ([doc_rifinternirifdiritto]=RPAM)) AND ";
		if (null != cdsnomeuff && cdsnomeuff.trim().length()>0) query += "(([doc_rifinternirifnomeuff]="+cdsnomeuff+") adj ([doc_rifinternirifdiritto]=CDS)) AND ";
		if (null != cdsnomepersona && cdsnomepersona.trim().length()>0) query += "(([doc_rifinternirifnomepersona]="+cdsnomepersona+") adj ([doc_rifinternirifdiritto]=CDS)) AND ";
		if (null != opnomeuff && opnomeuff.trim().length()>0) query += "(([doc_rifinternirifnomeuff]="+opnomeuff+") adj ([doc_rifinternirifdiritto]=OP)) AND ";
		if (null != opnomepersona && opnomepersona.trim().length()>0) query += "(([doc_rifinternirifnomepersona]="+opnomepersona+") adj ([doc_rifinternirifdiritto]=OP)) AND ";
		if (null != ccnomeuff && ccnomeuff.trim().length()>0) query += "(([doc_rifinternirifnomeuff]="+ccnomeuff+") adj ([doc_rifinternirifdiritto]=CC)) AND ";
		if (null != ccnomepersona && ccnomepersona.trim().length()>0) query += "(([doc_rifinternirifnomepersona]="+ccnomepersona+") adj ([doc_rifinternirifdiritto]=CC)) AND ";
		if (null != umnomeuff && umnomeuff.trim().length()>0) query += "([doc_storiaultimamodificauffoper]="+umnomeuff+") AND ";
		if (null != umnomepersona && umnomepersona.trim().length()>0) query += "([doc_storiaultimamodificaoper]="+umnomepersona+") AND ";

		if (null != range_docstoriacreazionedata_from && range_docstoriacreazionedata_from.length()> 0){
			String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
			if (!DateUtil.isValidDate(range_docstoriacreazionedata_from, formatoData)) {
				this.setErrorMessage("templateForm:range_docstoriacreazionedata_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.datacreazione") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
				return null;
			}
			String query1="";
			if (null != range_docstoriacreazionedata_to && range_docstoriacreazionedata_to.length()> 0){
				if (!DateUtil.isValidDate(range_docstoriacreazionedata_to, formatoData)) {
					this.setErrorMessage("templateForm:range_docstoriacreazionedata_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.datacreazione") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
				query1 = "{"+DateUtil.formatDate2XW(range_docstoriacreazionedata_from, null)+"|"+DateUtil.formatDate2XW(range_docstoriacreazionedata_to, null)+"}";
			}
			else
				query1 = DateUtil.formatDate2XW(range_docstoriacreazionedata_from, null);
			
			query += addQueryField("docstoriacreazionedata", query1);
		}
		
		if (annullati && !nonannullati) 	query += addQueryField("doc_annullato", "si");
		if (nonannullati && !annullati)		query += addQueryField("doc_annullato", "no");
		
		if (conservati && !nonconservati)	query += "([doc_conservazione] <> \"&null;\") AND ";
		if (nonconservati && !conservati)	query += "([doc_conservazione] = \"&null;\") AND ";
		
		if (bozze && !nonbozze) 			query += addQueryField("doc_bozza", "si");
		if (nonbozze && !bozze)		 		query += "(NOT([doc_bozza]=\"si\")) AND ";
		
		if (interoperabilita) 				query += addQueryField("/doc/rif_esterni/rif/interoperabilita/@title", "\"Segnatura.xml\"");
		if (pec)							query += addQueryField("/doc//xw:file/@title", "\"daticert.xml\"");
		if (differito)	 					query += addQueryField("doc_protdifferito", "\"¦\"");
		if (da_firmare)	 					query += addQueryField("doc_statofirma", "\"attesa firma\"");
		if (non_repertoriati)  				query += addQueryField("doc_repertorionumero", "\"&null;\"");
		
		String statusQuery = "";
		
		if (this.workflow_stato_attivo) 	
			statusQuery += "([/doc/workflows/Instance/@status] = started)";
		
		if (this.workflow_stato_concluso) {
			if (statusQuery.length() > 0)
				statusQuery += " OR ";
			
			statusQuery += "([/doc/workflows/Instance/@status] = finished)";
		}
		
		if (this.workflow_stato_cancellato) {
			if (statusQuery.length() > 0)
				statusQuery += " OR ";
			
			statusQuery += "([/doc/workflows/Instance/@status] = cancelled)";
		}
		
		if (statusQuery.length() > 0)
			query += "(" + statusQuery + ") AND ";
		
		// aggiunta del filtro sulla visibilita'
		String queryVisibilita = addFiltroVisibilita();
		if (queryVisibilita.length() > 0)
			query += "(" + queryVisibilita + ") AND ";
		
		String anno_prot = doc_anno;
        String da_num_prot = ( null != custom_da_num_prot ) ? custom_da_num_prot : "";
        if ( da_num_prot.length() > 0 ) da_num_prot = StringUtil.fillString(da_num_prot, "0", 7);
	    String a_num_prot = ( custom_a_num_prot != null) ? custom_a_num_prot : "";
        if ( a_num_prot.length() > 0 ) a_num_prot = StringUtil.fillString(a_num_prot, "0", 7);
        
        if ( anno_prot != null && anno_prot.length() > 0 || da_num_prot.length() > 0 ) { //almeno 'anno' o 'da' pieno
            if ( anno_prot.length() > 0) { //'anno' pieno
                if ( da_num_prot.length() == 0 && a_num_prot.length() == 0 ) //'da' e 'a' vuoti
                    query += "([doc_anno]=" + anno_prot+") AND ";
                else if ( da_num_prot.length() > 0 && a_num_prot.length() == 0 ) // 'da' pieno e 'a' vuoto
                    query += "([docnumprot]=" + anno_prot + "-" + "_CODSEDE_-" + da_num_prot+") AND ";
	                else if( da_num_prot.length() > 0 && a_num_prot.length() > 0 ) //'da' e 'a' pieni
	                    query += "([docnumprot]={" + anno_prot + "-" + "_CODSEDE_-" + da_num_prot + "|" + anno_prot + "-" + "_CODSEDE_-" + a_num_prot + "}) AND ";
	            }
            else if ( da_num_prot.length() > 0 && a_num_prot.length() > 0 ) { //'anno' vuoto, 'da' e 'a' pieni
                query += "([docnumprot]={_ANNOCORRENTE_-_CODSEDE_-" + da_num_prot + "|" +
                "_ANNOCORRENTE_-_CODSEDE_-" + a_num_prot + "})" + " AND ";
            }
            else { // 'anno' vuoto e 'da' pieno
                query += "([docnumprot]=" + "_ANNOCORRENTE_-" + "_CODSEDE_-" + da_num_prot + ") AND ";
            }
        }
        if(range_docdataprot_from.trim().length()>0 || range_docdataprot_to.trim().length()>0){
        	String query1= addDateRangeQuery("docdataprot", range_docdataprot_from, range_docdataprot_to, "AND");
    		query +=  query1;
        }
        if( (rangenum_docnrecord_from!=  null && rangenum_docnrecord_from.trim().length()>0) || (rangenum_docnrecord_to != null && rangenum_docnrecord_to.trim().length()>0)){
        	String complete_docnrecord_from = "";
        	if (rangenum_docnrecord_from != null && rangenum_docnrecord_from.length() > 0)
        		complete_docnrecord_from = StringUtil.fillString(rangenum_docnrecord_from, "0", 7);
        	String complete_docnrecord_to = "";
        	if (rangenum_docnrecord_to != null && rangenum_docnrecord_to.length() > 0)
        		complete_docnrecord_to = StringUtil.fillString(rangenum_docnrecord_to, "0", 7);
        	String query1= addRangeQuery("docnrecord", complete_docnrecord_from, complete_docnrecord_to, "AND");
    		query +=  query1;
        }
        if( (null!=range_docpubblicazioneal_from && range_docpubblicazioneal_from.trim().length()>0) || (range_docpubblicazioneal_to != null && range_docpubblicazioneal_to.trim().length()>0)){
        	String query1= addRangeQuery("docpubblicazioneal", range_docpubblicazioneal_from, range_docpubblicazioneal_to, "AND");
    		query +=  query1;
        }
        if( (arrivo || partenza) && (range_docrifesternirifdataprot_from.trim().length()>0 || range_docrifesternirifdataprot_to.trim().length()>0)){
        	String docrifesternirifdataprot_searchname = "docrifesternirifdataprot";
        	if (partenza)
        		docrifesternirifdataprot_searchname = "docdatareale";
        	String query1= addDateRangeQuery(docrifesternirifdataprot_searchname, range_docrifesternirifdataprot_from, range_docrifesternirifdataprot_to, "AND");
    		query +=  query1;
        }
        //Numeri di protocollo separati da virgole
        if ( custom_numeri_prot != null ) {
            String numeri_prot = custom_numeri_prot;
            String anno = "_ANNOCORRENTE_";
            String query1;
            if ( numeri_prot.length() > 0 ) {
                if ( anno_prot.length() > 0 )	//'anno' pieno
                    anno = anno_prot;
                numeri_prot = numeri_prot.replaceAll(" ","");
                String[] numeri_vect = numeri_prot.split(",");
                query1 = "[docnumprot]=" + anno + "-_CODSEDE_-" + StringUtil.fillString(numeri_vect[0], "0", 7);
                for ( int i = 1; i < numeri_vect.length; i++ )
                    query1 += " OR " + anno + "-_CODSEDE_-" + StringUtil.fillString(numeri_vect[i], "0", 7);

                if ( query.length() == 0 )
                    query = query1;
                else {
                    int posNumProt;

                    if ((posNumProt = query.indexOf("[docnumprot]=")) == -1) {
                        query = query +"("+query1 + ") AND ";
                    }
                    else {
                        query = query.substring(0, posNumProt + 13) + query1.substring(13) + " OR " + query.substring(posNumProt + 13);
                    }
                }
            }
        }
        
        if ( custom_doc_classifcod != null && custom_doc_classifcod.length() > 0 ) {
            String classifcod = convertClassifCod(custom_doc_classifcod);
            query +=  addQueryField("doc_classifcod", classifcod);
        }
        if ( custom_doc_classif != null && custom_doc_classif.trim().length() > 0 ) {
            String classif = custom_doc_classif;
            classif = classif.replace("non", "\"non\"");
            if ( classif.length() > 0 ) {
                query +=  addQueryField("doc_classif", classif);
            }
        }
        if (repertori_multipli){
        	query +=  addQueryField("xml,/doc/extra/tipologie_repertorio/tipologia_repertorio", this.tipologia_repertorio);
	        custom_repertorio = "";
	        for (int i=1; i < listof_rep.size(); i++){
	        	TitoloRepertorio rep = listof_rep.get(i);
	        	if (rep.getList_tabelle() != null && rep.getList_tabelle().size() > 0) {
	        		for (int j=0; j < rep.getList_tabelle().size(); j++) {
	        			Tabella repTable = rep.getList_tabelle().get(j);
	        			if (repTable.isSelected()) custom_repertorio+=rep.getCodice()+"-"+repTable.getTipo()+"%";
	        		}
	        	}
	        }
	        
	        //repertorio        
	        if ( custom_repertorio != null && custom_repertorio.length()>0 && custom_repertorio.split("%").length == 1) {
	            String anno = "_ANNOCORRENTE_";
	            String query1 = "";
	
	            //alert(repertorioId);
	            int separator;
	            String repertorio = (separator = custom_repertorio.indexOf("-")) != -1 ? custom_repertorio.substring(0,separator) : "" ;
	            String repTable = custom_repertorio.substring(separator+1, custom_repertorio.length()-1);
	            if ( "A".equals(repTable) ) repTable = "arrivo" ;
	            else if ( "I".equals(repTable) ) repTable = "interno" ;
	            else if ( "P".equals(repTable) ) repTable = "partenza" ;
	            else if ( "V".equals(repTable) ) repTable = "varie" ;
	            String rep_da = custom_rep_da;
	            String rep_a = custom_rep_a;
	            String numeri_sparsi_rep = custom_numeri_rep!= null ? custom_numeri_rep : "" ;
	
	            if ( repertorio.length() > 0 ) { //'repertorio' pieno
	
	                String query2 = "";
	
	                String queryTable = "";
	
	                if ( 0 != repTable.length() ){
	                    queryTable = "[doc_tipo]=" + repTable;
	                }
	
	                String operatore_sui_numeri = "OR";
	                if ( rep_da.length() == 0 && numeri_sparsi_rep.length() == 0 ){
	                    if (repertorio_anno != null && repertorio_anno.length() > 0) { 
	                    	// se non e' richiesto il filtro su num repertorio, ma l'anno di repertorio e' specificato ... 
	                    	// aggiuta del filtro per anno di repertorio
		                	query1 = "[doc_repertorionumero]=" + repertorio + "^" + "_CODSEDE_-" + repertorio_anno + "???????";
		                }
	                    else {
	                    	query1 = "[doc_repertoriocod]=\"" + repertorio + "\"";
	                    }
	                    operatore_sui_numeri = "AND";
	                }
	
	                if ( rep_da.length() > 0 || numeri_sparsi_rep.length() > 0 ) { //'da' pieno o numeri di repertorio separati da virgole
	                    if ( repertorio_anno.length() > 0 )	//'anno' pieno
	                        anno = repertorio_anno;
	                    if ( rep_da.length() > 0){
	                        if ( rep_a.length() > 0 ) { //'a' pieno
	                            query1 = "[doc_repertorionumero]={\"" +
	                            repertorio + "^" + "_CODSEDE_-" + anno + StringUtil.fillString(rep_da, "0", 7) + "\"|\""  +
	                            repertorio + "^" + "_CODSEDE_-" + anno + StringUtil.fillString(rep_a, "0", 7) + "\"}";
	                        }
	                        else { // 'a' vuoto
	                            query1 = "[doc_repertorionumero]=" + repertorio + "^" + "_CODSEDE_-" + anno + StringUtil.fillString(rep_da, "0", 7);
	                        }
	                    }
	                    if ( numeri_sparsi_rep.length() > 0 ) {
	                        String[] numeri_rep_vect = numeri_sparsi_rep.split(",");
	                        for (int i=0; i < numeri_rep_vect.length; i++) {
	                        	query2 = query2.length()==0 ? query2 : query2+" OR ";
	                        	String[] numeri_rep_range_vect = numeri_rep_vect[i].split("-");
	                            if ( numeri_rep_range_vect.length == 1 ) {
	
	                            	query2 += "[doc_repertorionumero]=" + repertorio + "^" + "_CODSEDE_-" + anno + StringUtil.fillString(numeri_rep_range_vect[0], "0", 7) + "";
	                            }
	                            else {
	                            	query2 += "[doc_repertorionumero]={\"" + repertorio + "^" + "_CODSEDE_-" + anno + StringUtil.fillString(numeri_rep_range_vect[0], "0", 7) + "\"|\""+
	                            	repertorio + "^" + "_CODSEDE_-" + anno + StringUtil.fillString(numeri_rep_range_vect[1], "0", 7)+"\"}";
	                            }
	                        }
	                        if ( query1.length() > 0 )
	                            query1 = "(" + query1 + " " + operatore_sui_numeri + " (" + query2 + "))";
	                        else
	                            query1 = query2;
	                        query2 = "";
	                    }
	                }
	                
	                if (queryTable.length() > 0) {
	                    query1 = "((" + query1 + ") AND (" + queryTable + ")) AND ";
	                }
	            }
	            if ( query.length() == 0 )
	                query = query1;
	            else if ( query1.length() > 0 )
	                query = query + query1;
	        }
	        /* caso 2: più repertori selezionati */
	        else if ( custom_repertorio != null && custom_repertorio.length() > 0 && custom_repertorio.split("%").length > 1 ){
	            custom_rep_a = custom_rep_da = custom_numeri_rep = "";
	            String repertorioId[] = custom_repertorio.split("%");
	            String repertorio = "";
	            String repTable = "";
	            int separator;
	            String query1 = "";
	            for ( int is = 0; is < repertorioId.length; is++ ){
	                repertorio = (separator = repertorioId[is].indexOf("-")) != -1 ? repertorioId[is].substring(0,separator) : "" ;
	                if ( separator != -1 ){
	                    repTable = repertorioId[is].substring(separator+1);
	                    if ( "A".equals(repTable) ) repTable = "arrivo" ;
	                    else if ( "I".equals(repTable) ) repTable = "interno" ;
	                    else if ( "P".equals(repTable) ) repTable = "partenza" ;
	                    else if ( "V".equals(repTable) ) repTable = "varie" ;
	                }
	                if (query1.equals(""))
	                    query1 = "([doc_tipo]=" + repTable + " AND [doc_repertoriocod]=\"" + repertorio + "\")";
	                else
	                    query1 += " OR ([doc_tipo]=" + repTable + " AND [doc_repertoriocod]=\"" + repertorio + "\")";
	            }
	            
	        	if (repertorio_anno.length() > 0 && custom_numeri_rep.length() == 0 && custom_rep_da.length() == 0 && custom_rep_a.length() == 0) {
	    			repertorioId = custom_repertorio.split("%"); //un solo repertorio selezionato -> split non effettuato -> lo forzo in maniera da avere repertorioId come array
	                String queryRepAnno = "";
	    			for ( int is = 0; is < repertorioId.length; is++ ){
	                    repertorio = (separator = repertorioId[is].indexOf("-")) != -1 ? repertorioId[is].substring(0,separator) : "" ;
	                    queryRepAnno += " OR [doc_repertorionumero]=\"" + repertorio + "^" + "_CODSEDE_-" + repertorio_anno + "???????\"";
						if (queryRepAnno.indexOf(" OR ") == 0) 
							queryRepAnno = queryRepAnno.substring(4);
	                }
	    			if (queryRepAnno.length() > 0) // corretto bug di errore sintattico in caso di questi con anno rep valorizzato e piu' repertori selezionati
	    				query += "(" + queryRepAnno + ") AND ";
	        	}
	        	if (query1.length()>0) query += "("+query1+") AND ";
	        }
	        else {
            	if (repertorio_anno.length()>0 || custom_rep_da.length()>0 || custom_rep_a.length() > 0 || custom_numeri_rep.length()>0){
            		this.setErrorMessage("templateForm:custom_repertorio", I18N.mrs("dw4.repertorio_select"));
            		return "error";
            	}
            }
        }
        if (interno && escludi_our) formsAdapter.escludiUOR();
        if (interno && escludi_rpa) formsAdapter.escludiRPA();
        
        String filtro = "[doc_tipo]=";
        if (!query.contains("[doc_tipo]") && !arrivo && !partenza && !interno && !varie) filtro = "([doc_tipo]=arrivo OR partenza OR interno OR varie)";
        else if (!query.contains("[doc_tipo]")){
        	if (arrivo) filtro += "arrivo OR ";
        	if (partenza) filtro += "partenza OR ";
        	if (interno) filtro += "interno OR ";
        	if (varie) filtro += "varie";
        	if (filtro.endsWith(" OR "))
        		filtro = filtro.substring(0, filtro.length()-3);
        }
        else filtro ="";
    	if ( query.length() == 0 ) query = filtro;
        else if ( filtro.length() > 0 ) query = query + "("+filtro.trim()+")";
		
    	if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
    	
    	// costruzione del filtro di ricerca per i campi custom
    	String filtroCustomFields = getCustomQueryFields().createQuery(getCurrentCustomFieldSection());
    	if (filtroCustomFields != null && filtroCustomFields.length() > 0)
    		query = query + " AND " + filtroCustomFields;
    	
		this.formsAdapter.getDefaultForm().addParam("qord", ordinamento);
		
		return query;
	}
	
	/**
	 * Esecuzione della query di ricerca costruita
	 */
	public String queryPlain(String query) throws Exception {
		try {
			XMLDocumento response = super._queryPlain(query, "", "");
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
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
	
	public String openIndexAnnoDoc() throws Exception {
		this.focusElement = "doc_anno";
		this.openIndex("doc_anno", this.doc_anno, "0", "", false);
		return null;
	}
	
	public String openIndexAnnoRep() throws Exception {
		this.focusElement = "repertorio_anno";
		this.openIndex("repertorio_anno", "doc_anno", this.repertorio_anno, "0", "", false);
		return null;
	}
	
	public String openIndexTipologiaDoc() throws Exception {
		this.focusElement = "tipologia_repertorio";
		this.openIndex("tipologia_repertorio", "xml,/doc/extra/tipologie_repertorio/tipologia_repertorio", this.tipologia_repertorio, "0", " ", false);
		return null;
	}
	
	public String openIndexVoceIndice() throws Exception {
		this.focusElement = "doc_voceindice";
		this.openIndex("doc_voceindice", "doc_voceindice", this.doc_voceindice, "0", " ", false);
		return null;
	}
	
	public String openIndexOggettoDoc() throws Exception {
		this.focusElement = "doc_oggetto";
		this.openIndex("doc_oggetto", "doc_oggetto", this.doc_oggetto, "0", "", false);
		return null;
	}
	
	public String openIndexAutoreDoc() throws Exception {
		this.focusElement = "doc_autore";
		this.openIndex("doc_autore", "doc_autore", this.doc_autore, "0", "", false);
		return null;
	}
	
	public String openIndexKeywordsDoc() throws Exception {
		this.focusElement = "doc_keywords";
		this.openIndex("doc_keywords", "doc_keywords", this.doc_keywords, "0", "", false);
		return null;
	}
	
	public String openIndexAllegatoDoc() throws Exception {
		this.focusElement = "doc_allegato";
		this.openIndex("doc_allegato", "xml,/doc/allegato", this.doc_allegato, "0", "", false);
		return null;
	}
	
	public String openIndexUORDoc() throws Exception {
		this.focusElement = "rpanomeuff";
		this.openIndex("rpanomeuff", "doc_rifinternirifdirittonomeuff", this.rpanomeuff, "0", "RPA|^| ", false);
		return null;
	}
	
	public String openIndexRPADoc() throws Exception {
		this.focusElement = "rpanomepersona";
		this.openIndex("rpanomepersona", "doc_rifinternirifdirittonomepersona", this.rpanomepersona, "0", "RPA|^| ", false);
		return null;
	}
	
	public String openIndexUORMDoc() throws Exception {
		this.focusElement = "rpamnomeuff";
		this.openIndex("rpamnomeuff", "doc_rifinternirifdirittonomeuff", this.rpamnomeuff, "0", "RPAM|^| ", false);
		return null;
	}
	
	public String openIndexRPAMDoc() throws Exception {
		this.focusElement = "rpamnomepersona";
		this.openIndex("rpamnomepersona", "doc_rifinternirifdirittonomepersona", this.rpamnomepersona, "0", "RPAM|^| ", false);
		return null;
	}
	
	public String openIndexCCUffDoc() throws Exception {
		this.focusElement = "ccnomeuff";
		this.openIndex("ccnomeuff", "doc_rifinternirifdirittonomeuff", this.ccnomeuff, "0", "CC|^| ", false);
		return null;
	}
	
	public String openIndexCCPersonaDoc() throws Exception {
		this.focusElement = "ccnomepersona";
		this.openIndex("ccnomepersona", "doc_rifinternirifdirittonomepersona", this.ccnomepersona, "0", "CC|^| ", false);
		return null;
	}
	
	public String openIndexCDSUffDoc() throws Exception {
		this.focusElement = "cdsnomeuff";
		this.openIndex("cdsnomeuff", "doc_rifinternirifdirittonomeuff", this.cdsnomeuff, "0", "CDS|^| ", false);
		return null;
	}
	
	public String openIndexCDSPersonaDoc() throws Exception {
		this.focusElement = "cdsnomepersona";
		this.openIndex("cdsnomepersona", "doc_rifinternirifdirittonomepersona", this.opnomepersona, "0", "CDS|^| ", false);
		return null;
	}
	
	public String openIndexOPUffDoc() throws Exception {
		this.focusElement = "opnomeuff";
		this.openIndex("opnomeuff", "doc_rifinternirifdirittonomeuff", this.opnomeuff, "0", "OP|^| ", false);
		return null;
	}
	
	public String openIndexOPPersonaDoc() throws Exception {
		this.focusElement = "opnomepersona";
		this.openIndex("opnomepersona", "doc_rifinternirifdirittonomepersona", this.opnomepersona, "0", "OP|^| ", false);
		return null;
	}
	
	public String openIndexUMUffDoc() throws Exception {
		this.focusElement = "umnomeuff";
		this.openIndex("umnomeuff", "doc_storiaultimamodificauffoper", this.umnomeuff, "0", " ", false);
		return null;
	}
	
	public String openIndexUMPersonaDoc() throws Exception {
		this.focusElement = "umnomepersona";
		this.openIndex("umnomepersona", "doc_storiaultimamodificaoper", this.umnomepersona, "0", " ", false);
		return null;
	}
	
	public String openIndexClassifDoc() throws Exception {
		this.focusElement = "custom_doc_classif";
		this.openIndex("custom_doc_classif", "doc_classif", this.custom_a_num_prot, "0", " ", false);
		return null;
	}

	public String openIndexNoteDoc() throws Exception {
		this.focusElement = "doc_note";
		this.openIndex("doc_note", "doc_note", this.doc_note, "0", "", false);
		return null;
	}
	
	public String openIndexPostitDoc() throws Exception {
		this.focusElement = "doc_postit";
		this.openIndex("doc_postit", "xml,/doc/postit", this.doc_postit, "0", "", false);
		return null;
	}
	
	public String openIndexRifEsterniDoc() throws Exception {
		this.focusElement = "doc_rifesternirifnome";
		this.openIndex("doc_rifesternirifnome", "doc_rifesternirifnome", this.doc_rifesternirifnome, "0", " ", false);
		return null;
	}
	
	public String openIndexCodFiscaleDoc() throws Exception {
		this.focusElement = "codice_fiscale";
		this.openIndex("codice_fiscale", "xml,/doc/rif_esterni/rif/@codice_fiscale", this.codice_fiscale, "0", "", false);
		return null;
	}
	
	public String openIndexPIvaDoc() throws Exception {
		this.focusElement = "partita_iva";
		this.openIndex("partita_iva", "xml,/doc/rif_esterni/rif/@partita_iva", this.partita_iva, "0", "", false);
		return null;
	}
	
	public String openIndexCreazionePersonaDoc() throws Exception {
		this.focusElement = "doc_storiacreazioneoper";
		this.openIndex("doc_storiacreazioneoper", "doc_storiacreazioneoper", doc_storiacreazioneoper, "0", " ", false);
		return null;
	}
	
	public String openIndexCreazioneUffDoc() throws Exception {
		this.focusElement = "doc_storiacreazioneuffoper";
		this.openIndex("doc_storiacreazioneuffoper", "doc_storiacreazioneuffoper", doc_storiacreazioneuffoper, "0", " ", false);
		return null;
	}
	
	public String openIndexProtocollazionePersonaDoc() throws Exception {
		this.focusElement = "doc_storiaprotocollazioneoper";
		this.openIndex("doc_storiaprotocollazioneoper", "xml,/doc/storia/protocollazione/@oper/", doc_storiaprotocollazioneoper, "0", "", false);
		return null;
	}
	
	public String openIndexProtocollazioneUffDoc() throws Exception {
		this.focusElement = "doc_storiaprotocollazioneuffoper";
		this.openIndex("doc_storiaprotocollazioneuffoper", "xml,/doc/storia/protocollazione/@uff_oper/", doc_storiaprotocollazioneuffoper, "0", "", false);
		return null;
	}
	
	public String openIndexRichiedenteEsternoDoc() throws Exception {
		this.focusElement = "richiedenteEsterno";
		this.openIndex("richiedenteEsterno", "xml,/doc/rif_esterni/rif/nome", this.richiedenteEsterno, "0", " ", false);
		return null;
	}
	
	public String openIndexRichiedenteInternoDoc() throws Exception {
		this.focusElement = "richiedenteInterno";
		this.openIndex("richiedenteinterno", "albo_ric_interno", this.richiedenteInterno, "0", " ", false);
		return null;
	}
	
	public String openIndexFirmatarioDoc() throws Exception {
		this.focusElement = "doc_rifesternirifreferentenominativo";
		this.openIndex("doc_rifesternirifreferentenominativo", "doc_rifesternirifreferentenominativo", this.doc_rifesternirifreferentenominativo, "0", " ", false);
		return null;
	}
	
	public String openIndexRuoloDoc() throws Exception {
		this.focusElement = "doc_rifesternirifreferenteruolo";
		this.openIndex("doc_rifesternirifreferenteruolo", "doc_rifesternirifreferenteruolo", this.doc_rifesternirifreferenteruolo, "0", " ", false);
		return null;
	}
	
	public String openIndexNumeroDoc() throws Exception {
		this.focusElement = "doc_rifestrenirifnprot";
		this.openIndex("doc_rifestrenirifnprot", "doc_rifestrenirifnprot", this.doc_rifestrenirifnprot, "0", " ", false);
		return null;
	}
	
	public String openIndexWorkflowLabel() throws Exception {
		this.focusElement = "workflow_label";
		this.openIndex("workflow_label", "xml,/doc/workflows/Instance/@label", this.workflow_label, "0", "", false);
		return null;
	}

	public void setAnnullati(boolean attivi) {
		this.annullati = attivi;
	}
	
	public boolean isAnnullati() {
		return annullati;
	}
	
	public void setNonannullati(boolean nonannullati) {
		this.nonannullati = nonannullati;
	}
	
	public boolean isNonannullati() {
		return nonannullati;
	}
	
	public void setConservati(boolean conservati) {
		this.conservati = conservati;
	}
	
	public boolean isConservati() {
		return conservati;
	}
	
	public void setNonconservati(boolean nonconservati) {
		this.nonconservati = nonconservati;
	}
	
	public boolean isNonconservati() {
		return nonconservati;
	}
	
	public void setBozze(boolean bozze) {
		this.bozze = bozze;
	}
	
	public boolean isBozze() {
		return bozze;
	}
	
	public boolean isWorkflow_stato_attivo() {
		return workflow_stato_attivo;
	}

	public void setWorkflow_stato_attivo(boolean workflow_stato_attivo) {
		this.workflow_stato_attivo = workflow_stato_attivo;
	}

	public boolean isWorkflow_stato_concluso() {
		return workflow_stato_concluso;
	}

	public void setWorkflow_stato_concluso(boolean workflow_stato_concluso) {
		this.workflow_stato_concluso = workflow_stato_concluso;
	}

	public boolean isWorkflow_stato_cancellato() {
		return workflow_stato_cancellato;
	}

	public void setWorkflow_stato_cancellato(boolean workflow_stato_cancellato) {
		this.workflow_stato_cancellato = workflow_stato_cancellato;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getXml() {
		return xml;
	}
	
	public void setDoc_anno(String doc_anno) {
		this.doc_anno = doc_anno;
	}
	
	public String getDoc_anno() {
		return doc_anno;
	}
	
	public String fillMultiString(String stringa, String carattere, int lunghezza, String separator)
	{
	    String ret = "";
	    String[] vect = stringa.split(separator);
	    for ( int i = 0; i < vect.length; i++ )
	        ret += separator + StringUtil.fillString(vect[i], carattere, lunghezza);
	    return ret;
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
	            part = StringUtil.fillString(part, "0", 2);
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
	public void setCcnomeuff(String ccnomeuff) {
		this.ccnomeuff = ccnomeuff;
	}
	public String getCcnomeuff() {
		return ccnomeuff;
	}
	public void setCcnomepersona(String ccnomepersona) {
		this.ccnomepersona = ccnomepersona;
	}
	public String getCcnomepersona() {
		return ccnomepersona;
	}
	public void setRange_docstoriacreazionedata_from(
			String range_fascstoriacreazionedata_from) {
		this.range_docstoriacreazionedata_from = range_fascstoriacreazionedata_from;
	}
	public String getRange_docstoriacreazionedata_from() {
		return range_docstoriacreazionedata_from;
	}
	public void setRange_docstoriacreazionedata_to(
			String range_fascstoriacreazionedata_to) {
		this.range_docstoriacreazionedata_to = range_fascstoriacreazionedata_to;
	}
	public String getRange_docstoriacreazionedata_to() {
		return range_docstoriacreazionedata_to;
	}
	public String getWorkflow_label() {
		return workflow_label;
	}

	public void setWorkflow_label(String workflow_label) {
		this.workflow_label = workflow_label;
	}

	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}
	public String getOrdinamento() {
		return ordinamento;
	}
	
	public String resetQuery(){
		annullati = false;
		nonannullati = false;
		conservati = false;
		nonconservati = false;
		bozze = false;
		custom_non_repertoriati = false;
		differito = false;
		da_firmare = false;
		workflow_stato_attivo = false;
		workflow_stato_concluso = false;
		workflow_stato_cancellato = false;
		custom_globale = "";
		custom_da_num_prot = "";
		custom_a_num_prot = "";
		custom_numeri_prot = "";
		custom_repertorio = "";
		custom_rep_da = "";
		custom_rep_a = "";
		range_docdataprot_from="";
		range_docdataprot_to="";
		doc_anno = "";
		repertorio_anno = "";
		tipologia_repertorio = "";
		doc_voceindice = "";
		doc_oggetto = "";
		doc_autore = "";
		doc_keywords = "";
		doc_allegato = "";
		custom_doc_classifcod = "";
		custom_doc_classif = "";
		doc_note = "";
		doc_postit = "";
		rpanomeuff = "";
		rpanomepersona = "";
		rpamnomeuff = "";
		rpamnomepersona = "";
		opnomeuff = "";
		opnomepersona = "";
		cdsnomeuff = "";
		cdsnomepersona = "";
		ccnomeuff = "";
		ccnomepersona = "";
		umnomepersona = "";
		umnomeuff = "";
		doc_rifesternirifnome = "";
		codice_fiscale = "";
		partita_iva = "";
		range_docstoriacreazionedata_from = "";
		range_docstoriacreazionedata_to = "";
		doc_storiacreazioneoper = "";
		doc_storiacreazioneuffoper = "";
		doc_storiaprotocollazioneoper = "";
		doc_storiaprotocollazioneuffoper = "";
		rangenum_docnrecord_from = "";
		rangenum_docnrecord_to = "";
		range_docpubblicazioneal_from = "";
		range_docpubblicazioneal_to = "";
		richiedenteEsterno = "";
		richiedenteInterno = "";
		custom_numeri_rep = "";
		workflow_label = "";
		mezzo_trasmissione = "";
		tipologia = "";
		nonbozze = false;
		non_repertoriati = false;
		arrivo = false;
		partenza = false;
		interno = false;
		varie = false;
		radio ="1";
		doc_rifesternirifreferentenominativo = ""; 
		doc_rifesternirifreferenteruolo = "";
		doc_rifestrenirifnprot = "";
		range_docrifesternirifdataprot_from = "";
		range_docrifesternirifdataprot_to = "";
		custom_cc = false;
		interoperabilita = false;
		pec = false;
		repertori_multipli = false;
		escludi_our = false;
		escludi_rpa = false;
		codSocieta = "";
		customSelect1 = "";
		customSelect2 = "";
		
		// pulizia di una eventuale selezione su livelli di visibilita'
		cleanVisibilitaSelection();
		
		// pulizia di evenutali campi custom presenti nel form
		setCurrentCustomFieldSection("");
		getCustomQueryFields().cleanCustomFields();
		
		return null;
	}
	
	/**
	 * Pulizia di eventuali livelli di visibilita' selezionati come filtri di ricerca
	 */
	private void cleanVisibilitaSelection() {
		if (livelliVisibilita != null && livelliVisibilita.size() > 0) {
			for (int i=0; i<livelliVisibilita.size(); i++) {
				LivelloVisibilita visibilita = livelliVisibilita.get(i);
				if (visibilita != null)
					visibilita.setSelected(false);
			}
		}
	}
	
	public String refine() throws Exception {
		try {
			String query = createQuery();
			formsAdapter.refineQuery(formsAdapter.getLastResponse().getAttributeValue("/response/@selid"));
			XMLDocumento response = super._queryPlain(query, "", "");
			
			if (handleErrorResponse(response)) return null;
			
			return navigateResponse(response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	public void setOrdinamento_select(Ordinamento_select ordinamento_select) {
		this.ordinamento_select = ordinamento_select;
	}

	public Ordinamento_select getOrdinamento_select() {
		return ordinamento_select;
	}

	public void setCustom_da_num_prot(String custom_da_num_prot) {
		this.custom_da_num_prot = custom_da_num_prot;
	}

	public String getCustom_da_num_prot() {
		return custom_da_num_prot;
	}

	public void setCustom_a_num_prot(String custom_a_num_prot) {
		this.custom_a_num_prot = custom_a_num_prot;
	}

	public String getCustom_a_num_prot() {
		return custom_a_num_prot;
	}

	public void setCustom_numeri_prot(String custom_numeri_prot) {
		this.custom_numeri_prot = custom_numeri_prot;
	}

	public String getCustom_numeri_prot() {
		return custom_numeri_prot;
	}

	public void setCustom_repertorio(String custom_repertorio) {
		if (custom_repertorio.startsWith("it.tredi.")) 
			this.custom_repertorio = "";
		else
			this.custom_repertorio = custom_repertorio;
	}

	public String getCustom_repertorio() {
		return custom_repertorio;
	}

	public void setRange_docdataprot_from(String range_docdataprot_from) {
		this.range_docdataprot_from = range_docdataprot_from;
	}

	public String getRange_docdataprot_from() {
		return range_docdataprot_from;
	}

	public void setRange_docdataprot_to(String range_docdataprot_to) {
		this.range_docdataprot_to = range_docdataprot_to;
	}

	public String getRange_docdataprot_to() {
		return range_docdataprot_to;
	}

	public void setCustom_globale(String custom_globale) {
		this.custom_globale = custom_globale;
	}

	public String getCustom_globale() {
		return custom_globale;
	}

	public void setCustom_non_repertoriati(boolean custom_non_repertoriati) {
		this.custom_non_repertoriati = custom_non_repertoriati;
	}

	public boolean isCustom_non_repertoriati() {
		return custom_non_repertoriati;
	}

	public void setCustom_rep_da(String custom_rep_da) {
		this.custom_rep_da = custom_rep_da;
	}

	public String getCustom_rep_da() {
		return custom_rep_da;
	}

	public void setCustom_rep_a(String custom_rep_a) {
		this.custom_rep_a = custom_rep_a;
	}

	public String getCustom_rep_a() {
		return custom_rep_a;
	}

	public void setTipologia_repertorio(String tipologia_repertorio) {
		this.tipologia_repertorio = tipologia_repertorio;
	}

	public String getTipologia_repertorio() {
		return tipologia_repertorio;
	}

	public void setDoc_voceindice(String doc_voceindice) {
		this.doc_voceindice = doc_voceindice;
	}

	public String getDoc_voceindice() {
		return doc_voceindice;
	}

	public void setDoc_oggetto(String doc_oggetto) {
		this.doc_oggetto = doc_oggetto;
	}

	public String getDoc_oggetto() {
		return doc_oggetto;
	}
	
	public void setDoc_autore(String doc_autore) {
		this.doc_autore = doc_autore;
	}

	public String getDoc_autore() {
		return doc_autore;
	}

	public void setDoc_keywords(String doc_keywords) {
		this.doc_keywords = doc_keywords;
	}

	public String getDoc_keywords() {
		return doc_keywords;
	}

	public void setDoc_allegato(String doc_allegato) {
		this.doc_allegato = doc_allegato;
	}

	public String getDoc_allegato() {
		return doc_allegato;
	}

	public void setCustom_doc_classifcod(String custom_classifdoccod) {
		this.custom_doc_classifcod = custom_classifdoccod;
	}

	public String getCustom_doc_classifcod() {
		return custom_doc_classifcod;
	}

	public void setCustom_doc_classif(String custom_doc_classif) {
		this.custom_doc_classif = custom_doc_classif;
	}

	public String getCustom_doc_classif() {
		return custom_doc_classif;
	}

	public void setDoc_note(String doc_note) {
		this.doc_note = doc_note;
	}

	public String getDoc_note() {
		return doc_note;
	}

	public void setDoc_postit(String doc_postit) {
		this.doc_postit = doc_postit;
	}

	public String getDoc_postit() {
		return doc_postit;
	}

	public void setArrivo_mezzo_trasmissione_select(
			List<Option> arrivo_mezzo_trasmissione_select) {
		this.arrivo_mezzo_trasmissione_select = arrivo_mezzo_trasmissione_select;
	}

	public List<Option> getArrivo_mezzo_trasmissione_select() {
		return arrivo_mezzo_trasmissione_select;
	}

	public void setMezzo_trasmissione(String mezzo_trasmissione) {
		this.mezzo_trasmissione = mezzo_trasmissione;
	}

	public String getMezzo_trasmissione() {
		return mezzo_trasmissione;
	}

	public void setArrivo_tipologia_select(List<Option> arrivo_tipologia_select) {
		this.arrivo_tipologia_select = arrivo_tipologia_select;
	}

	public List<Option> getArrivo_tipologia_select() {
		return arrivo_tipologia_select;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}

	public String getTipologia() {
		return tipologia;
	}
	
	public void setCdsnomeuff(String nomeuff) {
		this.cdsnomeuff = nomeuff;
	}

	public String getCdsnomeuff() {
		return cdsnomeuff;
	}
	
	public void setCdsnomepersona(String nomepersona) {
		this.cdsnomepersona = nomepersona;
	}

	public String getCdsnomepersona() {
		return cdsnomepersona;
	}

	public void setOpnomeuff(String opnomeuff) {
		this.opnomeuff = opnomeuff;
	}

	public String getOpnomeuff() {
		return opnomeuff;
	}

	public void setOpnomepersona(String opnomepersona) {
		this.opnomepersona = opnomepersona;
	}

	public String getOpnomepersona() {
		return opnomepersona;
	}

	public void setUmnomepersona(String umnomepersona) {
		this.umnomepersona = umnomepersona;
	}

	public String getUmnomepersona() {
		return umnomepersona;
	}

	public void setUmnomeuff(String umnomeuff) {
		this.umnomeuff = umnomeuff;
	}

	public String getUmnomeuff() {
		return umnomeuff;
	}

	public void setDoc_rifesternirifnome(String doc_rifesternirifnome) {
		this.doc_rifesternirifnome = doc_rifesternirifnome;
	}

	public String getDoc_rifesternirifnome() {
		return doc_rifesternirifnome;
	}

	public void setCodice_fiscale(String codice_fiscale) {
		this.codice_fiscale = codice_fiscale;
	}

	public String getCodice_fiscale() {
		return codice_fiscale;
	}

	public void setPartita_iva(String partita_iva) {
		this.partita_iva = partita_iva;
	}

	public String getPartita_iva() {
		return partita_iva;
	}

	public void setRangenum_docnrecord_from(String rangenum_docnrecord_from) {
		this.rangenum_docnrecord_from = rangenum_docnrecord_from;
	}

	public String getRangenum_docnrecord_from() {
		return rangenum_docnrecord_from;
	}

	public void setRangenum_docnrecord_to(String rangenum_docnrecord_to) {
		this.rangenum_docnrecord_to = rangenum_docnrecord_to;
	}

	public String getRangenum_docnrecord_to() {
		return rangenum_docnrecord_to;
	}

	public void setDoc_storiacreazioneoper(String doc_storiacreazioneoper) {
		this.doc_storiacreazioneoper = doc_storiacreazioneoper;
	}

	public String getDoc_storiacreazioneoper() {
		return doc_storiacreazioneoper;
	}

	public void setDoc_storiacreazioneuffoper(String doc_storiacreazioneuffoper) {
		this.doc_storiacreazioneuffoper = doc_storiacreazioneuffoper;
	}

	public String getDoc_storiacreazioneuffoper() {
		return doc_storiacreazioneuffoper;
	}
	
	public void setDoc_storiaprotocollazioneoper(String oper) {
		this.doc_storiaprotocollazioneoper = oper;
	}

	public String getDoc_storiaprotocollazioneoper() {
		return doc_storiaprotocollazioneoper;
	}

	public void setDoc_storiaprotocollazioneuffoper(String uff) {
		this.doc_storiaprotocollazioneuffoper = uff;
	}

	public String getDoc_storiaprotocollazioneuffoper() {
		return doc_storiaprotocollazioneuffoper;
	}

	public void setNonbozze(boolean nonbozze) {
		this.nonbozze = nonbozze;
	}

	public boolean isNonbozze() {
		return nonbozze;
	}

	public void setNon_repertoriati(boolean non_repertoriati) {
		this.non_repertoriati = non_repertoriati;
	}

	public boolean isNon_repertoriati() {
		return non_repertoriati;
	}

	public void setListof_rep(List<TitoloRepertorio> listof_rep) {
		this.listof_rep = listof_rep;
	}

	public List<TitoloRepertorio> getListof_rep() {
		return listof_rep;
	}

	public void setCustom_numeri_rep(String custom_numeri_rep) {
		this.custom_numeri_rep = custom_numeri_rep;
	}

	public String getCustom_numeri_rep() {
		return custom_numeri_rep;
	}

	// TODO da eliminare
	/*public String getTipoRepertorio(){
		if (custom_repertorio.length()<1) return "";
		for (Iterator<TitoloRepertorio> iterator = listof_rep.iterator(); iterator.hasNext();) {
			TitoloRepertorio option = (TitoloRepertorio) iterator.next();
			if (custom_repertorio.equals(option.getCodice())){
				return option.getTabella().getTipo();
			}
		}
		return "";
	}*/

	public void setRange_docpubblicazioneal_from(
			String range_docpubblicazioneal_from) {
		this.range_docpubblicazioneal_from = range_docpubblicazioneal_from;
	}

	public String getRange_docpubblicazioneal_from() {
		return range_docpubblicazioneal_from;
	}

	public void setRange_docpubblicazioneal_to(
			String range_docpubblicazioneal_to) {
		this.range_docpubblicazioneal_to = range_docpubblicazioneal_to;
	}

	public String getRange_docpubblicazioneal_to() {
		return range_docpubblicazioneal_to;
	}

	public void setRichiedenteEsterno(String richiedente) {
		this.richiedenteEsterno = richiedente;
	}

	public String getRichiedenteEsterno() {
		return richiedenteEsterno;
	}

	public void setRichiedenteInterno(String richiedenteInterno) {
		this.richiedenteInterno = richiedenteInterno;
	}

	public String getRichiedenteInterno() {
		return richiedenteInterno;
	}

	public void setArrivo(boolean arrivo) {
		this.arrivo = arrivo;
	}

	public boolean isArrivo() {
		return arrivo;
	}

	public void setPartenza(boolean partenza) {
		this.partenza = partenza;
	}

	public boolean isPartenza() {
		return partenza;
	}

	public void setInterno(boolean interno) {
		this.interno = interno;
	}

	public boolean isInterno() {
		return interno;
	}

	public void setVarie(boolean varie) {
		this.varie = varie;
	}

	public boolean isVarie() {
		return varie;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}

	public String getRadio() {
		return radio;
	}

	public void setRepertorio_anno(String repertorio_anno) {
		this.repertorio_anno = repertorio_anno;
	}

	public String getRepertorio_anno() {
		return repertorio_anno;
	}

	public void setDoc_rifesternirifreferentenominativo(
			String doc_rifesternirifreferentenominativo) {
		this.doc_rifesternirifreferentenominativo = doc_rifesternirifreferentenominativo;
	}

	public String getDoc_rifesternirifreferentenominativo() {
		return doc_rifesternirifreferentenominativo;
	}

	public void setDoc_rifesternirifreferenteruolo(
			String doc_rifesternirifreferenteruolo) {
		this.doc_rifesternirifreferenteruolo = doc_rifesternirifreferenteruolo;
	}

	public String getDoc_rifesternirifreferenteruolo() {
		return doc_rifesternirifreferenteruolo;
	}

	public void setDoc_rifestrenirifnprot(String doc_rifestrenirifnprot) {
		this.doc_rifestrenirifnprot = doc_rifestrenirifnprot;
	}

	public String getDoc_rifestrenirifnprot() {
		return doc_rifestrenirifnprot;
	}

	public void setRange_docrifesternirifdataprot_from(
			String range_docrifesternirifdataprot_from) {
		this.range_docrifesternirifdataprot_from = range_docrifesternirifdataprot_from;
	}

	public String getRange_docrifesternirifdataprot_from() {
		return range_docrifesternirifdataprot_from;
	}

	public void setRange_docrifesternirifdataprot_to(
			String range_docrifesternirifdataprot_to) {
		this.range_docrifesternirifdataprot_to = range_docrifesternirifdataprot_to;
	}

	public String getRange_docrifesternirifdataprot_to() {
		return range_docrifesternirifdataprot_to;
	}

	public void setCustom_cc(boolean custom_cc) {
		this.custom_cc = custom_cc;
	}

	public boolean isCustom_cc() {
		return custom_cc;
	}

	public void setInteroperabilita(boolean interoperabilita) {
		this.interoperabilita = interoperabilita;
	}

	public boolean isInteroperabilita() {
		return interoperabilita;
	}
	
	public boolean isPec() {
		return pec;
	}

	public void setPec(boolean pec) {
		this.pec = pec;
	}

	public void setDifferito(boolean differito) {
		this.differito = differito;
	}

	public boolean isDifferito() {
		return differito;
	}
	
	public void setDa_firmare(boolean da_firmare) {
		this.da_firmare = da_firmare;
	}

	public boolean isDa_firmare() {
		return da_firmare;
	}

	public void setRpamnomeuff(String rpamnomeuff) {
		this.rpamnomeuff = rpamnomeuff;
	}

	public String getRpamnomeuff() {
		return rpamnomeuff;
	}

	public boolean isWorkflows_info() {
		return workflows_info;
	}

	public void setWorkflows_info(boolean workflows_info) {
		this.workflows_info = workflows_info;
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

	public void setRpamnomepersona(String rpamnomepersona) {
		this.rpamnomepersona = rpamnomepersona;
	}

	public String getRpamnomepersona() {
		return rpamnomepersona;
	}

	public void setRepertori_multipli(boolean repertori_multipli) {
		this.repertori_multipli = repertori_multipli;
		
		if (repertori_multipli)
			this.non_repertoriati = false;
	}

	public boolean isRepertori_multipli() {
		return repertori_multipli;
	}

	public boolean isAllSelect(){
		if (!arrivo && !partenza && !interno && !varie) return true;
		return false;
	}

	public void setEscludi_our(boolean escludi_our) {
		this.escludi_our = escludi_our;
	}

	public boolean isEscludi_our() {
		return escludi_our;
	}

	public void setEscludi_rpa(boolean escludi_rpa) {
		this.escludi_rpa = escludi_rpa;
	}

	public boolean isEscludi_rpa() {
		return escludi_rpa;
	}

	public void setEstremi_protocollo(boolean estremi_protocollo) {
		this.estremi_protocollo = estremi_protocollo;
	}

	public boolean isEstremi_protocollo() {
		return estremi_protocollo;
	}

	public void setDati_documento(boolean dati_documento) {
		this.dati_documento = dati_documento;
	}

	public boolean isDati_documento() {
		return dati_documento;
	}

	public void setResponsabilita(boolean responsabilita) {
		this.responsabilita = responsabilita;
	}

	public boolean isResponsabilita() {
		return responsabilita;
	}

	public void setInformazioni_servizio(boolean informazioni_servizio) {
		this.informazioni_servizio = informazioni_servizio;
	}

	public boolean isInformazioni_servizio() {
		return informazioni_servizio;
	}

	public String openCloseWidgetEstremi(){
		estremi_protocollo = !estremi_protocollo;
		return null;
	}

	public String openCloseWidgetDati(){
		dati_documento = !dati_documento;
		return null;
	}
	
	public String openCloseWidgetResponsabilita(){
		responsabilita = !responsabilita;
		return null;
	}
	
	public String openCloseWidgetInformazioni(){
		informazioni_servizio = !informazioni_servizio;
		return null;
	}
	
	public String openCloseWidgetWorklofw(){
		workflows_info = !workflows_info;
		return null;
	}
	
	public String getCustomSelect1() {
		return customSelect1;
	}

	public void setCustomSelect1(String customSelect1) {
		this.customSelect1 = customSelect1;
	}

	public String getCustomSelect2() {
		return customSelect2;
	}

	public void setCustomSelect2(String customSelect2) {
		this.customSelect2 = customSelect2;
	}

	public List<Option> getCustomSelect1_select() {
		return customSelect1_select;
	}

	public void setCustomSelect1_select(List<Option> customSelect1_select) {
		this.customSelect1_select = customSelect1_select;
	}

	public List<Option> getCustomSelect2_select() {
		return customSelect2_select;
	}

	public void setCustomSelect2_select(List<Option> customSelect2_select) {
		this.customSelect2_select = customSelect2_select;
	}
	
	public List<LivelloVisibilita> getLivelliVisibilita() {
		return livelliVisibilita;
	}

	public void setLivelliVisibilita(List<LivelloVisibilita> livelliVisibilita) {
		this.livelliVisibilita = livelliVisibilita;
	}
	
	public boolean isSelectRepertorioCustom(){
		boolean nsize = false;
		for (int i=1; i < listof_rep.size(); i++){
        	TitoloRepertorio rep = listof_rep.get(i);
        	if (rep.getList_tabelle() != null && rep.getList_tabelle().size() > 0) {
	        	for (int j=0; j < rep.getList_tabelle().size(); j++) {
	        		Tabella repTable = rep.getList_tabelle().get(j);
		        	if (repTable.isSelected()) {
		        		nsize = true;
		        		if (!(( rep.getCodice().equals("ALBO") && (repTable.getTipo().equals("A") || repTable.getTipo().equals("P"))) || 
		        				(rep.getCodice().equals("RAOL") && repTable.getTipo().equals("V") ) || 
		        				(rep.getCodice().equals("AOL") && repTable.getTipo().equals("V"))))
		        			return false;
		        	}
	        	}
        	}
        }
		return nsize;
	}
	
	/**
	 * Forza un repertorio come selezionato (utilizzato nelle ricerche specifiche su repertori personalizzati)
	 * @param cod_rep
	 * @param tipo
	 */
	public void forceRepertorioSelected(String cod_rep, String tipo) {
		for (int i=1; i < listof_rep.size(); i++){
        	TitoloRepertorio rep = listof_rep.get(i);
        	if (rep.getList_tabelle() != null 
        			&& rep.getCodice() != null 
        			&& rep.getCodice().toLowerCase().equals(cod_rep.toLowerCase()) 
        			&& rep.getList_tabelle().size() > 0) {
        		for (int j=0; j < rep.getList_tabelle().size(); j++) {
        			Tabella repTable = rep.getList_tabelle().get(j);
        			if (repTable.getTipo().toLowerCase().equals(tipo.toLowerCase())) 
        				repTable.setSelected(true);
        		}
        	}
        }
	}
	
	
	
	/**
	 * identifica l'eventuale sezione relativa ai campi custom definiti dall'utente da mostrare
	 * all'interno del form di ricerca dei documenti
	 */
	public void existsCustomFields(ValueChangeEvent e) {
		String customFieldSection = "";

		// la visualizzazione dei campi custom avviene se e solo se 
		// e' selezionata una sola tipologia documentale
		
		boolean arrivoChecked =  identifyValueChecked(e, "templateForm:arrivocheck", isArrivo());
		boolean partenzaChecked =  identifyValueChecked(e, "templateForm:partenzacheck", isPartenza());
		boolean internoChecked =  identifyValueChecked(e, "templateForm:internocheck", isInterno());
		boolean varieChecked =  identifyValueChecked(e, "templateForm:variecheck", isVarie());
		boolean repertoriChecked =  identifyValueChecked(e, "templateForm:repertoricheck", isRepertori_multipli());
		
		if (arrivoChecked && !partenzaChecked && !internoChecked && !varieChecked && !repertoriChecked)
			customFieldSection = "arrivo";
		else if (!arrivoChecked && partenzaChecked && !internoChecked && !varieChecked && !repertoriChecked)
			customFieldSection = "partenza";
		else if (!arrivoChecked && !partenzaChecked && internoChecked && !varieChecked && !repertoriChecked)
			customFieldSection = "interno";
		else if (!arrivoChecked && !partenzaChecked && !internoChecked && varieChecked && !repertoriChecked)
			customFieldSection = "varie";
				
		setCurrentCustomFieldSection(customFieldSection);
	}
	
	private boolean identifyValueChecked(ValueChangeEvent e, String clientId, boolean defaultvalue) {
		boolean checked = defaultvalue;
		if (e != null && e.getComponent() != null && e.getComponent().getClientId() != null) {
			if (e.getComponent().getClientId().equals(clientId)) {
				if (((Boolean) e.getNewValue()).booleanValue())
					checked = true;
				else
					checked = false;
			}
		}
		return checked;
	}
	
	/**
	 * caricamento di campi custom specifici di un repertorio
	 * @return
	 * @throws Exception
	 */
	public String existsCustomFieldsRep() {
		if (!arrivo && !partenza && !interno && !varie && repertori_multipli) {
			String customFieldSection = "";
			
			// gestione dei campi custom su repertori. la visualizzazione dei campi custom avviene
			// se e solo se e' selezionato un solo repertorio
			int countRepSelezionati = 0;
			for (int i=1; i < listof_rep.size(); i++) { // parto da 1 perche' il primo repertorio e' vuoto
	        	TitoloRepertorio rep = listof_rep.get(i);
	        	if (rep.getList_tabelle() != null && rep.getList_tabelle().size() > 0) {
	        		for (int j=0; j < rep.getList_tabelle().size(); j++) {
	        			Tabella repTable = rep.getList_tabelle().get(j);
	        			if (repTable.isSelected()) { 
	        				String tableName = repTable.getTipo();
	        				if (tableName.equals("A"))
	        					tableName = Const.DOCWAY_TIPOLOGIA_ARRIVO;
	        				else if (tableName.equals("P"))
	        					tableName = Const.DOCWAY_TIPOLOGIA_PARTENZA;
	        				else if (tableName.equals("I")) 
	        					tableName = Const.DOCWAY_TIPOLOGIA_INTERNO;
	        				else if (tableName.equals("V"))
	        					tableName = Const.DOCWAY_TIPOLOGIA_VARIE;
	        				
	        				customFieldSection = tableName + "_" + rep.getCodice();
	        				countRepSelezionati++;
	        			}
	        		}
	        	}
	        }
			if (countRepSelezionati > 1) // se sono stati selezionati piu' repertori i campi custom non devono essere caricati
				customFieldSection = "";
			
			setCurrentCustomFieldSection(customFieldSection);
		}
		return null;
	}
	
	/**
	 * aggiunta del filtro di ricerca sulla visibilita' dei documenti
	 * @return
	 */
	private String addFiltroVisibilita() {
		String query = "";
		
		if (livelliVisibilita != null && livelliVisibilita.size() > 0) {
			for (int i=0; i<livelliVisibilita.size(); i++) {
				LivelloVisibilita visibilita = livelliVisibilita.get(i);
				if (visibilita != null && visibilita.isSelected() && visibilita.getText() != null && !visibilita.getText().isEmpty()) {
					query += "[/doc/visibilita/@tipo/]=\"" + visibilita.getText() + "\" OR ";
				}
			}
		}
		if (query.endsWith(" OR "))
			query = query.substring(0, query.length()-3);
		
		return query;
	}
	
}
