package it.tredi.dw4.soginSAP.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayQuery;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.Option;
import it.tredi.dw4.docway.model.Ordinamento_select;
import it.tredi.dw4.docway.model.Tabella;
import it.tredi.dw4.docway.model.TitoloRepertorio;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.soginSAP.model.SAPDoc;
import it.tredi.dw4.soginSAP.model.SAPStoria;
import it.tredi.dw4.soginSAP.ws.stubs.ServiceLocator;
import it.tredi.dw4.soginSAP.ws.stubs.ZSOGIN_DOC;
import it.tredi.dw4.soginSAP.ws.stubs.ZWS_SOGIN_UPDATE_TAB_PortType;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.RomanConversion;
import it.tredi.dw4.utils.XMLUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.client.Call;
import org.apache.axis.client.Stub;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

public class SoginSAPQuery extends DocWayQuery {
	private String xml;
	//campi di ricerca
	private boolean annullati;
	private boolean nonannullati;
	private boolean bozze;
	private boolean custom_non_repertoriati;
	private boolean differito;
	private String custom_globale;
	private String custom_da_num_prot;
	private String custom_a_num_prot;
	private String custom_numeri_prot;
	private String custom_repertorio;
	private String custom_rep_da;
	private String custom_rep_a;
	private String range_docdataprot_from="";
	private String range_docdataprot_to="";
	private String doc_anno;
	private String repertorio_anno;
	private String tipologia_repertorio;
	private String doc_voceindice;
	private String doc_oggetto;
	private String doc_keywords;
	private String doc_allegato;
	private String custom_doc_classifcod;
	private String custom_doc_classif;
	private String doc_note;
	private String doc_postit;
	private String rpanomeuff;
	private String rpanomepersona;
	private String rpamnomeuff;
	private String rpamnomepersona;
	private String opnomeuff;
	private String opnomepersona;
	private String ccnomeuff;
	private String ccnomepersona;
	private String umnomepersona;
	private String umnomeuff;
	private String doc_rifesternirifnome;
	private String codice_fiscale;
	private String partita_iva;
	private String range_docstoriacreazionedata_from="";
	private String range_docstoriacreazionedata_to="";
	private String doc_storiacreazioneoper;
	private String doc_storiacreazioneuffoper;
	private String rangenum_docnrecord_from;
	private String rangenum_docnrecord_to;
	private String range_docpubblicazioneal_from="";
	private String range_docpubblicazioneal_to="";
	private String richiedenteEsterno;
	private String richiedenteInterno;
	private String custom_numeri_rep;
	private Ordinamento_select ordinamento_select = new Ordinamento_select();
	private List<Option> arrivo_mezzo_trasmissione_select;
	private List<Option> arrivo_tipologia_select;
	private List<TitoloRepertorio> listof_rep;
	private List<TitoloRepertorio> listof_all_visible_rep;
	private String ordinamento;
	private String mezzo_trasmissione;
	private String tipologia;
	private boolean nonbozze;
	private boolean non_repertoriati;
	private boolean arrivo;
	private boolean partenza;
	private boolean interno;
	private boolean varie;
	private String radio ="1";
	private String doc_rifesternirifreferentenominativo; 
	private String doc_rifesternirifreferenteruolo;
	private String doc_rifestrenirifnprot;
	private String range_docrifesternirifdataprot_from="";
	private String range_docrifesternirifdataprot_to="";
	private boolean custom_cc;
	private boolean interoperabilita;
	private boolean escludi_our;
	private boolean escludi_rpa;
	
	//campi per SAP
	private String docSap;
	private String utente;
	private String causale;
	private String dataRegistrazione_from;
	private String dataRegistrazione_to;
	private String stato;
	private List<Option> stato_select;
	
	private boolean estremi_protocollo 		= true;
	private boolean dati_documento 			= true;
	private boolean responsabilita			= false;
	private boolean informazioni_servizio 	= false;
	
	
	private DocDocWayQueryFormsAdapter formsAdapter;
	private String focusElement;
	private boolean repertori_multipli;
	
	public SoginSAPQuery() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.xml = dom.asXML();
		this.ordinamento_select.init(XMLUtil.createDocument(dom, "/response/ordinamento_select"));
		this.arrivo_mezzo_trasmissione_select = XMLUtil.parseSetOfElement(dom, "response/arrivo_mezzo_trasmissione_select/option", new Option());
		this.arrivo_tipologia_select = XMLUtil.parseSetOfElement(dom, "response/arrivo_tipologia_select/option", new Option());
		
		this.listof_rep = XMLUtil.parseSetOfElement(dom, "response/listof_rep/repertorio", new TitoloRepertorio());
		this.listof_all_visible_rep = new ArrayList<TitoloRepertorio>(this.listof_rep);
		
		try {
			applyRepertoriRestrictions();
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}
		
		for (Iterator<Option> iterator = ordinamento_select.getOptions().iterator(); iterator.hasNext();) {
			Option option = (Option) iterator.next();
			if (option.getSelected().length()>0)
				ordinamento = option.getValue();
		}
		for (Iterator<Option> iterator = arrivo_mezzo_trasmissione_select.iterator(); iterator.hasNext();) {
			Option option = (Option) iterator.next();
			if (option.getSelected().length()>0)
				mezzo_trasmissione = option.getValue();
		}
		for (Iterator<Option> iterator = arrivo_tipologia_select.iterator(); iterator.hasNext();) {
			Option option = (Option) iterator.next();
			if (option.getSelected().length()>0)
				tipologia = option.getValue();
		}
		
		//prepara la lista di opzioni per select stato SAP
		this.stato_select = new ArrayList<Option>();
		Option statoOption = new Option(); //vuota
		statoOption.setLabel("");
		statoOption.setValue("");
		this.stato_select.add(statoOption);
		
		statoOption = new Option(); //associato
		statoOption.setLabel("Associato");
		statoOption.setValue("associato");
		this.stato_select.add(statoOption);
		
		statoOption = new Option(); //stornato
		statoOption.setLabel("Stornato");
		statoOption.setValue("stornato");
		this.stato_select.add(statoOption);
		
		statoOption = new Option(); //stornato
		statoOption.setLabel("Associato o stornato");
		statoOption.setValue("associato or stornato");
		this.stato_select.add(statoOption);
		
		/*
		try {
			prepareIndicesFilter();
		} catch (Exception e) {
			e.printStackTrace();
			//TODO
		}
		*/
    }	
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	@Override
	public String queryPlain() throws Exception {
		try {
			String query = createQuery();
			
			if("error".equals(query)) 
				return null;
			
			formsAdapter.findplain();
			return queryPlain(query);
			
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	
	public String createQuery() throws Exception {
		String query =  "";
		//ricerca globale
	    if(custom_globale.length()>0) {
	    	if ("1".equals(radio)){
		    	String query1 = custom_globale + " OR [doc_filesfiletesto]=" + custom_globale;
		    	query +=  addQueryField("@", query1);
	    	}
	    	else if ("2".equals(radio)){
	    		query +=  addQueryField("@", this.custom_globale);
	    	}
	    	else if ("3".equals(radio)){
	    	    query +=  addQueryField("doc_filesfiletesto", this.custom_globale);
	    	}
	    }
	    query +=  addQueryField("doc_voceindice", this.doc_voceindice);
	    query +=  addQueryField("doc_oggetto", this.doc_oggetto);
	    query +=  addQueryField("doc_keywords", this.doc_keywords);
	    query +=  addQueryField("xml,/doc/allegato", this.doc_allegato);
	    query +=  addQueryField("doc_note", this.doc_note);
	    query +=  addQueryField("xml,/doc/postit", this.doc_postit);
	    query +=  addQueryField("doc_mezzotrasmissionecod", this.mezzo_trasmissione);
	    query +=  addQueryField("doc_tipologiacod", this.tipologia);
	    query +=  addQueryField("doc_rifesternirifnome", this.doc_rifesternirifnome);
	    query +=  addQueryField("xml,/doc/rif_esterni/rif/@codice_fiscale", this.codice_fiscale);
	    query +=  addQueryField("xml,/doc/rif_esterni/rif/@partita_iva", this.partita_iva);
	    query +=  addQueryField("doc_storiacreazioneoper", this.doc_storiacreazioneoper);
	    query +=  addQueryField("doc_storiacreazioneuffoper", this.doc_storiacreazioneuffoper);
	    query +=  addQueryField("xml,/doc/rif_esterni/rif/nome", this.richiedenteEsterno);
	    query +=  addQueryField("albo_ric_interno", this.richiedenteInterno);

	    if (custom_cc && this.doc_rifesternirifreferentenominativo.trim().length() > 0) {
    		String query1 = "(([doc_rifesternirifreferentenominativo]=" + this.doc_rifesternirifreferentenominativo+") adj ([doc_rifesternirifcopiaconoscenza]=si)) AND ";
	    	query +=  query1;
	    }
	    else query +=  addQueryField("doc_rifesternirifreferentenominativo", this.doc_rifesternirifreferentenominativo);
	    query +=  addQueryField("doc_rifesternirifreferenteruolo", this.doc_rifesternirifreferenteruolo);
	    query +=  addQueryField("doc_rifestrenirifnprot", this.doc_rifestrenirifnprot);
	    
		if (null != rpanomeuff && rpanomeuff.trim().length()>0) query += "(([doc_rifinternirifnomeuff]="+rpanomeuff+") adj ([doc_rifinternirifdiritto]=RPA)) AND ";
		if (null != rpanomepersona && rpanomepersona.trim().length()>0) query += "(([doc_rifinternirifnomepersona]="+rpanomepersona+") adj ([doc_rifinternirifdiritto]=RPA)) AND ";
		if (interno && null != rpamnomeuff && rpamnomeuff.trim().length()>0) query += "(([doc_rifinternirifnomeuff]="+rpamnomeuff+") adj ([doc_rifinternirifdiritto]=RPAM)) AND ";
		if (interno && null != rpamnomepersona && rpamnomepersona.trim().length()>0) query += "(([doc_rifinternirifnomepersona]="+rpamnomepersona+") adj ([doc_rifinternirifdiritto]=RPAM)) AND ";
		if (null != opnomeuff && opnomeuff.trim().length()>0) query += "(([doc_rifinternirifnomeuff]="+opnomeuff+") adj ([doc_rifinternirifdiritto]=ITF)) AND ";
		if (null != opnomepersona && opnomepersona.trim().length()>0) query += "(([doc_rifinternirifnomepersona]="+opnomepersona+") adj ([doc_rifinternirifdiritto]=ITF)) AND ";
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
			if (null != range_docstoriacreazionedata_to && range_docstoriacreazionedata_to.length() > 0){
				if (!DateUtil.isValidDate(range_docstoriacreazionedata_to, formatoData)) {
					this.setErrorMessage("templateForm:range_docstoriacreazionedata_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("dw4.datacreazione") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
				query1 = "{"+DateUtil.formatDate2XW(range_docstoriacreazionedata_from, null)+"|"+DateUtil.formatDate2XW(range_docstoriacreazionedata_to, null)+"}";
			}
			else
				query1 = DateUtil.formatDate2XW(range_docstoriacreazionedata_from, null);
			query +=  addQueryField("docstoriacreazionedata", query1);
		}
		if (annullati) 		query +=  addQueryField("doc_annullato", "si");
		if (nonannullati) 	query +=  addQueryField("doc_annullato", "no");
		if (interoperabilita) 	query +=  addQueryField("/doc/rif_esterni/rif/interoperabilita/@title", "\"Segnatura.xml\"");
		if (differito) 		query +=  addQueryField("doc_protdifferito", "\"¦\"");
		if (bozze || nonbozze){
			if (bozze && nonbozze)	query +=  "("+addQueryField("doc_bozza", "si", "OR") +"(not([doc_bozza]=\"si\")))";
			else if (bozze) 		query +=  addQueryField("doc_bozza", "si");
			else if (nonbozze) 		query +=  "(not([doc_bozza]=\"si\"))";
		}
		if (non_repertoriati)  query += addQueryField("doc_repertorionumero", "\"&null;\"");
		String anno_prot = doc_anno;
        String da_num_prot = ( null != custom_da_num_prot ) ? custom_da_num_prot : "";
        if ( da_num_prot.length() > 0 ) da_num_prot = fillString(da_num_prot, "0", 7);
	    String a_num_prot = ( custom_a_num_prot != null) ? custom_a_num_prot : "";
        if ( a_num_prot.length() > 0 ) a_num_prot = fillString(a_num_prot, "0", 7);
        
        if ( anno_prot.length() > 0 || da_num_prot.length() > 0 ) { //almeno 'anno' o 'da' pieno
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
        
        if(range_docdataprot_from.trim().length()>0 || range_docdataprot_to.trim().length() > 0) {
        	String query1= addDateRangeQuery("docdataprot", range_docdataprot_from, range_docdataprot_to, "AND");
    		query +=  query1;
        }
        
        if( (rangenum_docnrecord_from!=  null && rangenum_docnrecord_from.trim().length()>0) || (rangenum_docnrecord_to != null && rangenum_docnrecord_to.trim().length() > 0)) {
        	String query1= addRangeQuery("docnrecord", fillString(rangenum_docnrecord_from, "0", 7), fillString(rangenum_docnrecord_to, "0", 7), "AND");
    		query +=  query1;
        }
        
        if( (null!=range_docpubblicazioneal_from && range_docpubblicazioneal_from.trim().length()>0) || (range_docpubblicazioneal_to != null && range_docpubblicazioneal_to.trim().length() > 0)) {
        	String query1= addRangeQuery("docpubblicazioneal", range_docpubblicazioneal_from, range_docpubblicazioneal_to, "AND");
    		query +=  query1;
        }
        
        if( (arrivo || partenza) && (range_docrifesternirifdataprot_from.trim().length() > 0 || range_docrifesternirifdataprot_to.trim().length() > 0)) {
        	String query1= addDateRangeQuery("docrifesternirifdataprot", range_docrifesternirifdataprot_from, range_docrifesternirifdataprot_to, "AND");
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
                query1 = "[docnumprot]=" + anno + "-_CODSEDE_-" + fillString(numeri_vect[0], "0", 7);
                for ( int i = 1; i < numeri_vect.length; i++ )
                    query1 += " OR " + anno + "-_CODSEDE_-" + fillString(numeri_vect[i], "0", 7);

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
        
        if ( custom_doc_classifcod.length()>0 ) {
            String classifcod = convertClassifCod(custom_doc_classifcod);
            query +=  addQueryField("doc_classifcod", classifcod);
        }
        if ( custom_doc_classif.trim().length()>0 ) {
            String classif = custom_doc_classif;
            classif = classif.replace("non", "\"non\"");
            if ( classif.length() > 0 ) {
                query +=  addQueryField("doc_classif", classif);
            }
        }
        
        //campi per SAP
  		query += addQueryField("/doc/extra/soginSAP/docSAP", this.docSap);
  		query += addQueryField("/doc/extra/soginSAP/causale", this.causale);
  		query += addQueryField("/doc/extra/soginSAP/utente", this.utente);
  		query += addQueryField("/doc/extra/soginSAP/@stato", this.stato);
  		
//        if (repertori_multipli){
//        	query +=  addQueryField("xml,/doc/extra/tipologie_repertorio/tipologia_repertorio", this.tipologia_repertorio);
//	        custom_repertorio = "";
//	        for (int i=1; i < listof_rep.size(); i++){
//	        	TitoloRepertorio rep = listof_rep.get(i);
//	        	if (rep.getList_tabelle() != null && rep.getList_tabelle().size() > 0) {
//	        		for (int j=0; j < rep.getList_tabelle().size(); j++) {
//	        			Tabella repTable = rep.getList_tabelle().get(j);
//	        			if (repTable.isSelected()) custom_repertorio+=rep.getCodice()+"-"+repTable.getTipo()+"%";
//	        		}
//	        	}
//	        }
//	        
//	        //repertorio        
//	        if ( custom_repertorio != null && custom_repertorio.length()>0 && custom_repertorio.split("%").length == 1) {
//	            String anno = "_ANNOCORRENTE_";
//	            String query1 = "";
//	
//	            //alert(repertorioId);
//	            int separator;
//	            String repertorio = (separator = custom_repertorio.indexOf("-")) != -1 ? custom_repertorio.substring(0,separator) : "" ;
//	            String repTable = custom_repertorio.substring(separator+1, custom_repertorio.length()-1);
//	            if ( "A".equals(repTable) ) repTable = "arrivo" ;
//	            else if ( "I".equals(repTable) ) repTable = "interno" ;
//	            else if ( "P".equals(repTable) ) repTable = "partenza" ;
//	            else if ( "V".equals(repTable) ) repTable = "varie" ;
//	            String rep_da = custom_rep_da;
//	            String rep_a = custom_rep_a;
//	            String numeri_sparsi_rep = custom_numeri_rep!= null ? custom_numeri_rep : "" ;
//	
//	            if ( repertorio.length() > 0 ) { //'repertorio' pieno
//	
//	                String query2 = "";
//	
//	                String queryTable = "";
//	
//	                if ( 0 != repTable.length() ){
//	                    queryTable = "[doc_tipo]=" + repTable;
//	                }
//	
//	                String operatore_sui_numeri = "OR";
//	                if ( rep_da.length() == 0 && numeri_sparsi_rep.length() == 0 ){
//	                    query1 = "[doc_repertoriocod]=\"" + repertorio + "\"";
//	                    operatore_sui_numeri = "AND";
//	                }
//	
//	                if ( rep_da.length() > 0 || numeri_sparsi_rep.length() > 0 ) { //'da' pieno o numeri di repertorio separati da virgole
//	                    if ( repertorio_anno.length() > 0 )	//'anno' pieno
//	                        anno = repertorio_anno;
//	                    if ( rep_da.length() > 0){
//	                        if ( rep_a.length() > 0 ) { //'a' pieno
//	                            query1 = "[doc_repertorionumero]={\"" +
//	                            repertorio + "^" + "_CODSEDE_-" + anno + fillString(rep_da, "0", 7) + "\"|\""  +
//	                            repertorio + "^" + "_CODSEDE_-" + anno + fillString(rep_a, "0", 7) + "\"}";
//	                        }
//	                        else { // 'a' vuoto
//	                            query1 = "[doc_repertorionumero]=" + repertorio + "^" + "_CODSEDE_-" + anno + fillString(rep_da, "0", 7);
//	                        }
//	                    }
//	                    if ( numeri_sparsi_rep.length() > 0 ) {
//	                        String[] numeri_rep_vect = numeri_sparsi_rep.split(",");
//	                        for (int i=0; i < numeri_rep_vect.length; i++) {
//	                        	query2 = query2.length()==0 ? query2 : query2+" OR ";
//	                        	String[] numeri_rep_range_vect = numeri_rep_vect[i].split("-");
//	                            if ( numeri_rep_range_vect.length == 1 ) {
//	
//	                            	query2 += "[doc_repertorionumero]=" + repertorio + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[0], "0", 7) + "";
//	                            }
//	                            else {
//	                            	query2 += "[doc_repertorionumero]={\"" + repertorio + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[0], "0", 7) + "\"|\""+
//	                            	repertorio + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[1], "0", 7)+"\"}";
//	                            }
//	                        }
//	                        if ( query1.length() > 0 )
//	                            query1 = "(" + query1 + " " + operatore_sui_numeri + " (" + query2 + "))";
//	                        else
//	                            query1 = query2;
//	                        query2 = "";
//	                    }
//	                }
//	
//	                if (queryTable != "") {
//	                    query1 = "((" + query1 + ") AND (" + queryTable + ")) AND ";
//	                }
//	            }
//	            if ( query.length() == 0 )
//	                query = query1;
//	            else if ( query1.length() > 0 )
//	                query = query + query1;
//	        }
//	        /* caso 2: più repertori selezionati */
//	        else if ( custom_repertorio != "" && custom_repertorio.length()>0 && custom_repertorio.split("%").length > 1 ){
//	            custom_rep_a = custom_rep_da = custom_numeri_rep = "";
//	            String repertorioId[] = custom_repertorio.split("%");
//	            String repertorio = "";
//	            String repTable = "";
//	            int separator;
//	            String query1 = "";
//	            for ( int is = 0; is < repertorioId.length; is++ ){
//	                repertorio = (separator = repertorioId[is].indexOf("-")) != -1 ? repertorioId[is].substring(0,separator) : "" ;
//	                if ( separator != -1 ){
//	                    repTable = repertorioId[is].substring(separator+1);
//	                    if ( "A".equals(repTable) ) repTable = "arrivo" ;
//	                    else if ( "I".equals(repTable) ) repTable = "interno" ;
//	                    else if ( "P".equals(repTable) ) repTable = "partenza" ;
//	                    else if ( "V".equals(repTable) ) repTable = "varie" ;
//	                }
//	                if ( query1 == "" )
//	                    query1 = "([doc_tipo]=" + repTable + " AND [doc_repertoriocod]=\"" + repertorio + "\")";
//	                else
//	                    query1 += " OR ([doc_tipo]=" + repTable + " AND [doc_repertoriocod]=\"" + repertorio + "\")";
//	            }
//	            
//	        	if (repertorio_anno.length() > 0 && custom_numeri_rep.length() == 0 && custom_rep_da.length() == 0 && custom_rep_a.length() == 0) {
//	    			repertorioId = custom_repertorio.split("%"); //un solo repertorio selezionato -> split non effettuato -> lo forzo in maniera da avere repertorioId come array
//	                for ( int is = 0; is < repertorioId.length; is++ ){
//	                    repertorio = (separator = repertorioId[is].indexOf("-")) != -1 ? repertorioId[is].substring(0,separator) : "" ;
//							query += " OR [doc_repertorionumero]=\"" + repertorio + "^" + "_CODSEDE_-" + repertorio_anno + "???????\"";
//							if (query.indexOf(" OR ") == 0) query = query.substring(4);
//	                }
//	        	}
//	        	if (query1.length()>0) query+="("+query1+") AND ";
//	        }
//	        else {
//            	if (repertorio_anno.length()>0 || custom_rep_da.length()>0 || custom_rep_a.length() > 0 || custom_numeri_rep.length()>0){
//            		this.setErrorMessage("templateForm:custom_repertorio", I18N.mrs("dw4.repertorio_select"));
//            		return "error";
//            	}
//            }
//        }
        
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
        else if ( filtro.length() > 0 ) query = "("+filtro.trim()+") AND " + query;
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		
		if (null != dataRegistrazione_from && dataRegistrazione_from.length()> 0){
			String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
			if (!DateUtil.isValidDate(dataRegistrazione_from, formatoData)) {
				this.setErrorMessage("templateForm:range_dataregistrazioneSAP_from", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("soginSAP.dataRegistrazione") +" "+I18N.mrs("dw4.da") + "': " + formatoData.toLowerCase());
				return null;
			}
			String query1="";
			if (null != dataRegistrazione_to && dataRegistrazione_to.length()> 0){
				if (!DateUtil.isValidDate(dataRegistrazione_to, formatoData)) {
					this.setErrorMessage("templateForm:range_dataregistrazioneSAP_to", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("soginSAP.dataRegistrazione") +" "+I18N.mrs("dw4.a") + "': " + formatoData.toLowerCase());
					return null;
				}
				query1 = "{"+DateUtil.formatDate2XW(dataRegistrazione_from, null)+"|"+DateUtil.formatDate2XW(dataRegistrazione_to, null)+"}";
			}
			else
				query1 = DateUtil.formatDate2XW(dataRegistrazione_from, null);
			query +=  addQueryField("/doc/extra/soginSAP/dataRegistrazione", query1);
		}
		
		//aggiungi il filtro sui repertori
    	String repFilter = "";
    	for (TitoloRepertorio repertorio : this.listof_rep) {
    		if (repertorio.getList_tabelle() != null && repertorio.getList_tabelle().size() > 0) {
        		for (int j=0; j < repertorio.getList_tabelle().size(); j++) {
        			Tabella repTable = repertorio.getList_tabelle().get(j);
        			if (repTable.isSelected()) {
			    		String repTableType = repTable.getTipo();
			    		
			    		if ( "A".equals(repTableType) ) repTableType = "arrivo" ;
			            else if ( "I".equals(repTableType) ) repTableType = "interno" ;
			            else if ( "P".equals(repTableType) ) repTableType = "partenza" ;
			            else if ( "V".equals(repTableType) ) repTableType = "varie" ;
			    		
			    		if ( repFilter.equals("") )
			    			repFilter = " AND (([doc_tipo]=" + repTableType + " AND [doc_repertoriocod]=\"" + repertorio.getCodice() + "\")";
			            else
			            	repFilter += " OR ([doc_tipo]=" + repTableType + " AND [doc_repertoriocod]=\"" + repertorio.getCodice() + "\")";
			    		
			    		String rep_da = custom_rep_da;
			            String rep_a = custom_rep_a;
			            String numeri_sparsi_rep = custom_numeri_rep!= null ? custom_numeri_rep : "" ;
			            
			            List<String> anni = Arrays.asList(repertorio_anno.split("OR|or"));
			            if (anni.get(0).trim().equals("")) {
			            	anni = new ArrayList<String>();
			            	anni.add("_ANNOCORRENTE_");
			            }
			            
			            String queryFinal = "";
			            String query1 = "";
			            String query2 = "";
			            
			            String operatore_sui_numeri = "OR";
			            if ( rep_da.length() == 0 && numeri_sparsi_rep.length() == 0 ){
			                operatore_sui_numeri = "AND";
			            }
			        	
			            for (String anno : anni) {
			            	anno = anno.trim();
			            	
				      		if ( rep_da.length() > 0 || numeri_sparsi_rep.length() > 0 ) { //'da' pieno o numeri di repertorio separati da virgole
				                if ( rep_da.length() > 0){
				                    if ( rep_a.length() > 0 ) { //'a' pieno
				                        query1 = "[doc_repertorionumero]={\"" +
				                        repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(rep_da, "0", 7) + "\"|\""  +
				                        repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(rep_a, "0", 7) + "\"}";
				                    }
				                    else { // 'a' vuoto
				                        query1 = "[doc_repertorionumero]=" + repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(rep_da, "0", 7);
				                    }
				                }
				                if ( numeri_sparsi_rep.length() > 0 ) {
				                    String[] numeri_rep_vect = numeri_sparsi_rep.split(",");
				                    for (int i=0; i < numeri_rep_vect.length; i++) {
				                    	query2 = query2.length()==0 ? query2 : query2+" OR ";
				                    	String[] numeri_rep_range_vect = numeri_rep_vect[i].split("-");
				                        if ( numeri_rep_range_vect.length == 1 ) {
	
				                        	query2 += "[doc_repertorionumero]=" + repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[0], "0", 7) + "";
				                        }
				                        else {
				                        	query2 += "[doc_repertorionumero]={\"" + repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[0], "0", 7) + "\"|\""+
				                        	repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[1], "0", 7)+"\"}";
				                        }
				                    }
				                    if ( query1.length() > 0 )
				                        query1 = "(" + query1 + " " + operatore_sui_numeri + " (" + query2 + "))";
				                    else
				                        query1 = query2;
				                    
				                    query2 = "";
				                }
				            }
				    	    
				      		if ( queryFinal.length() == 0 )
				      			queryFinal = query1;
				    	    else if ( query1.length() > 0 )
				    	    	queryFinal = queryFinal + " OR " + query1;
			            }
			            
			            if ( repFilter.length() == 0 )
			            	repFilter = queryFinal;
			    	    else if ( queryFinal.length() > 0 )
			    	    	repFilter = repFilter + " AND (" + queryFinal + ")";
        			}
        		}
    		}
    	}
    	
    	if ( !repFilter.equals("") )
    		repFilter += ")";
    	else {
    		String queryFinal = "";
    		
    		for (TitoloRepertorio repertorio : this.listof_all_visible_rep) {
//    			String query1 = "";
//    			String query2 = "";
//	    		String rep_da = custom_rep_da;
//	            String rep_a = custom_rep_a;
//	            String numeri_sparsi_rep = custom_numeri_rep!= null ? custom_numeri_rep : "" ;
//	            
//	            String anno = "_ANNOCORRENTE_";
//	            
//	            String operatore_sui_numeri = "OR";
//	            if ( rep_da.length() == 0 && numeri_sparsi_rep.length() == 0 ){
//	                operatore_sui_numeri = "AND";
//	            }
//	        	
//	      		if ( rep_da.length() > 0 || numeri_sparsi_rep.length() > 0 ) { //'da' pieno o numeri di repertorio separati da virgole
//	                if ( repertorio_anno.length() > 0 )	//'anno' pieno
//	                    anno = repertorio_anno;
//	                if ( rep_da.length() > 0){
//	                    if ( rep_a.length() > 0 ) { //'a' pieno
//	                        query1 = "[doc_repertorionumero]={\"" +
//	                        repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(rep_da, "0", 7) + "\"|\""  +
//			                repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(rep_a, "0", 7) + "\"}";
//	                    }
//	                    else { // 'a' vuoto
//	                        query1 = "[doc_repertorionumero]=" + repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(rep_da, "0", 7);
//	                    }
//	                }
//	                if ( numeri_sparsi_rep.length() > 0 ) {
//	                    String[] numeri_rep_vect = numeri_sparsi_rep.split(",");
//	                    for (int i=0; i < numeri_rep_vect.length; i++) {
//	                    	query2 = query2.length()==0 ? query2 : query2+" OR ";
//	                    	String[] numeri_rep_range_vect = numeri_rep_vect[i].split("-");
//	                        if ( numeri_rep_range_vect.length == 1 ) {
//	
//	                        	query2 += "[doc_repertorionumero]=" + repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[0], "0", 7) + "";
//	                        }
//	                        else {
//	                        	query2 += "[doc_repertorionumero]={\"" + repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[0], "0", 7) + "\"|\""+
//	                        	repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[1], "0", 7)+"\"}";
//	                        }
//	                    }
//	                    if ( query1.length() > 0 )
//	                        query1 = "(" + query1 + " " + operatore_sui_numeri + " (" + query2 + "))";
//	                    else
//	                        query1 = query2;
//	                    
//	                    query2 = "";
//	                }
//	            }
//	      		
//	      		if (query1.length() > 0) {
//	      			if (queryFinal.length() > 0)
//	      				queryFinal += " OR (" + query1 + ")";
//	      			else
//	      				queryFinal = "(" + query1 + ")";
//	      		}
    			
    			String rep_da = custom_rep_da;
	            String rep_a = custom_rep_a;
	            String numeri_sparsi_rep = custom_numeri_rep!= null ? custom_numeri_rep : "" ;
	            
	            List<String> anni = Arrays.asList(repertorio_anno.split("OR|or"));
	            if (anni.get(0).trim().equals("")) {
	            	anni = new ArrayList<String>();
	            	anni.add("_ANNOCORRENTE_");
	            }
	            
	            String query1 = "";
	            String query2 = "";
	            
	            String operatore_sui_numeri = "OR";
	            if ( rep_da.length() == 0 && numeri_sparsi_rep.length() == 0 ){
	                operatore_sui_numeri = "AND";
	            }
	        	
	            for (String anno : anni) {
	            	anno = anno.trim();
	            	
		      		if ( rep_da.length() > 0 || numeri_sparsi_rep.length() > 0 ) { //'da' pieno o numeri di repertorio separati da virgole
		                if ( rep_da.length() > 0){
		                    if ( rep_a.length() > 0 ) { //'a' pieno
		                        query1 = "[doc_repertorionumero]={\"" +
		                        repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(rep_da, "0", 7) + "\"|\""  +
		                        repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(rep_a, "0", 7) + "\"}";
		                    }
		                    else { // 'a' vuoto
		                        query1 = "[doc_repertorionumero]=" + repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(rep_da, "0", 7);
		                    }
		                }
		                if ( numeri_sparsi_rep.length() > 0 ) {
		                    String[] numeri_rep_vect = numeri_sparsi_rep.split(",");
		                    for (int i=0; i < numeri_rep_vect.length; i++) {
		                    	query2 = query2.length()==0 ? query2 : query2+" OR ";
		                    	String[] numeri_rep_range_vect = numeri_rep_vect[i].split("-");
		                        if ( numeri_rep_range_vect.length == 1 ) {

		                        	query2 += "[doc_repertorionumero]=" + repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[0], "0", 7) + "";
		                        }
		                        else {
		                        	query2 += "[doc_repertorionumero]={\"" + repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[0], "0", 7) + "\"|\""+
		                        	repertorio.getCodice() + "^" + "_CODSEDE_-" + anno + fillString(numeri_rep_range_vect[1], "0", 7)+"\"}";
		                        }
		                    }
		                    if ( query1.length() > 0 )
		                        query1 = "(" + query1 + " " + operatore_sui_numeri + " (" + query2 + "))";
		                    else
		                        query1 = query2;
		                    
		                    query2 = "";
		                }
		            }
		    	    
		      		if ( queryFinal.length() == 0 )
		      			queryFinal = query1;
		    	    else if ( query1.length() > 0 )
		    	    	queryFinal = queryFinal + " OR " + query1;
	            }
	      			
    		}

			if (queryFinal.length() > 0)
				repFilter = repFilter + " AND (" + queryFinal + ")";
		}
    	
    	if (repertorio_anno.length() > 0) {
			if (repFilter.length() == 0)
				repFilter = "[doc_anno]=" + repertorio_anno;
			else
				repFilter += " AND ([doc_anno]=" + repertorio_anno + ")";
		}
		
		if (tipologia_repertorio.length() > 0) {
			if (repFilter.length() == 0)
				repFilter = "[xml,/doc/extra/tipologie_repertorio/tipologia_repertorio]=" + this.tipologia_repertorio;
			else
				repFilter += " AND ([xml,/doc/extra/tipologie_repertorio/tipologia_repertorio]=" + this.tipologia_repertorio + ")";
		}
    	
		if ( query.length() > 0 && repFilter.length() > 0) {
			if (repFilter.trim().startsWith("AND") || repFilter.trim().startsWith("OR"))
				query += repFilter;
			else
				query += " AND " + repFilter;
		}
		else
			query += repFilter;
    	
		
		this.formsAdapter.getDefaultForm().addParam("qord", ordinamento);
		
		return query;
	}
	
	public String queryPlain(String query) throws Exception {
		try {
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
	
	public void setFocusElement(String focusElement) {
		this.focusElement = focusElement;
	}
	public String getFocusElement() {
		return focusElement;
	}
	
	public String openIndexAnnoDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_anno";
		this.openIndex("doc_anno", this.doc_anno, "0", "", needToFilterWithSelection);
		return null;
	}
	
	public String openIndexAnnoRep() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "repertorio_anno";
		this.openIndex("repertorio_anno", "doc_anno", this.repertorio_anno, "0", "", needToFilterWithSelection);
		return null;
	}
	
	public String openIndexDocSap() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "docSap";
		this.openIndex("docSap", "xml,/doc/extra/soginSAP/docSAP", this.docSap, "0", "", needToFilterWithSelection);
		return null;
	}
	
	public String openIndexCausaleSap() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "causale";
		this.openIndex("causale", "xml,/doc/extra/soginSAP/causale", this.causale, "0", " ", needToFilterWithSelection);
		return null;
	}
	
	public String openIndexUtenteSap() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "utente";
		this.openIndex("utente", "xml,/doc/extra/soginSAP/utente", this.utente, "0", "", needToFilterWithSelection);
		return null;
	}
	
	public String openIndexTipologiaDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "tipologia_repertorio";
		this.openIndex("tipologia_repertorio", "xml,/doc/extra/tipologie_repertorio/tipologia_repertorio", this.tipologia_repertorio, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexVoceIndice() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_voceindice";
		this.openIndex("doc_voceindice", "doc_voceindice", this.doc_voceindice, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexOggettoDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_oggetto";
		this.openIndex("doc_oggetto", "doc_oggetto", this.doc_oggetto, "0", "", needToFilterWithSelection);
		return null;
	}
	public String openIndexKeywordsDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_keywords";
		this.openIndex("doc_keywords", "doc_keywords", this.doc_keywords, "0", "", needToFilterWithSelection);
		return null;
	}
	public String openIndexAllegatoDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_allegato";
		this.openIndex("doc_allegato", "xml,/doc/allegato", this.doc_allegato, "0", "", needToFilterWithSelection);
		return null;
	}
	public String openIndexUORDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "rpanomeuff";
		this.openIndex("rpanomeuff", "doc_rifinternirifdirittonomeuff", this.rpanomeuff, "0", "RPA|^| ", needToFilterWithSelection);
		return null;
	}
	public String openIndexRPADoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "rpanomepersona";
		this.openIndex("rpanomepersona", "doc_rifinternirifdirittonomepersona", this.rpanomepersona, "0", "RPA|^| ", needToFilterWithSelection);
		return null;
	}
	public String openIndexUORMDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "rpamnomeuff";
		this.openIndex("rpamnomeuff", "doc_rifinternirifdirittonomeuff", this.rpamnomeuff, "0", "RPAM|^| ", needToFilterWithSelection);
		return null;
	}
	public String openIndexRPAMDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "rpamnomepersona";
		this.openIndex("rpamnomepersona", "doc_rifinternirifdirittonomepersona", this.rpamnomepersona, "0", "RPAM|^| ", needToFilterWithSelection);
		return null;
	}
	public String openIndexCCUffDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "ccnomeuff";
		this.openIndex("ccnomeuff", "doc_rifinternirifdirittonomeuff", this.ccnomeuff, "0", "CC|^| ", needToFilterWithSelection);
		return null;
	}
	public String openIndexCCPersonaDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "ccnomepersona";
		this.openIndex("ccnomepersona", "doc_rifinternirifdirittonomepersona", this.ccnomepersona, "0", "CC|^| ", needToFilterWithSelection);
		return null;
	}
	public String openIndexOPUffDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "opnomeuff";
		this.openIndex("opnomeuff", "doc_rifinternirifdirittonomeuff", this.opnomeuff, "0", "OP|^| ", needToFilterWithSelection);
		return null;
	}
	public String openIndexOPPersonaDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "opnomepersona";
		this.openIndex("opnomepersona", "doc_rifinternirifdirittonomepersona", this.opnomepersona, "0", "OP|^| ", needToFilterWithSelection);
		return null;
	}
	public String openIndexUMUffDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "umnomeuff";
		this.openIndex("umnomeuff", "doc_storiaultimamodificauffoper", this.umnomeuff, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexUMPersonaDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "umnomepersona";
		this.openIndex("umnomepersona", "doc_storiaultimamodificaoper", this.umnomepersona, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexClassifDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "custom_doc_classif";
		this.openIndex("custom_doc_classif", "doc_classif", this.custom_a_num_prot, "0", " ", needToFilterWithSelection);
		return null;
	}

	public String openIndexNoteDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_note";
		this.openIndex("doc_note", "doc_note", this.doc_note, "0", "", needToFilterWithSelection);
		return null;
	}
	public String openIndexPostitDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_postit";
		this.openIndex("doc_postit", "xml,/doc/postit", this.doc_postit, "0", "", needToFilterWithSelection);
		return null;
	}
	public String openIndexRifEsterniDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_rifesternirifnome";
		this.openIndex("doc_rifesternirifnome", "doc_rifesternirifnome", this.doc_rifesternirifnome, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexCodFiscaleDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "codice_fiscale";
		this.openIndex("codice_fiscale", "xml,/doc/rif_esterni/rif/@codice_fiscale", this.codice_fiscale, "0", "", needToFilterWithSelection);
		return null;
	}
	public String openIndexPIvaDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "partita_iva";
		this.openIndex("partita_iva", "xml,/doc/rif_esterni/rif/@partita_iva", this.partita_iva, "0", "", needToFilterWithSelection);
		return null;
	}
	public String openIndexCreazionePersonaDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_storiacreazioneoper";
		this.openIndex("doc_storiacreazioneoper", "doc_storiacreazioneoper", doc_storiacreazioneoper, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexCreazioneUffDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_storiacreazioneuffoper";
		this.openIndex("doc_storiacreazioneuffoper", "doc_storiacreazioneuffoper", doc_storiacreazioneuffoper, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexRichiedenteEsternoDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "richiedenteEsterno";
		this.openIndex("richiedenteEsterno", "xml,/doc/rif_esterni/rif/nome", this.richiedenteEsterno, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexRichiedenteInternoDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "richiedenteInterno";
		this.openIndex("richiedenteinterno", "albo_ric_interno", this.richiedenteInterno, "0", " ", needToFilterWithSelection);
		return null;
	}
	
	public String openIndexFirmatarioDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_rifesternirifreferentenominativo";
		this.openIndex("doc_rifesternirifreferentenominativo", "doc_rifesternirifreferentenominativo", this.doc_rifesternirifreferentenominativo, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexRuoloDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_rifesternirifreferenteruolo";
		this.openIndex("doc_rifesternirifreferenteruolo", "doc_rifesternirifreferenteruolo", this.doc_rifesternirifreferenteruolo, "0", " ", needToFilterWithSelection);
		return null;
	}
	public String openIndexNumeroDoc() throws Exception {
		boolean needToFilterWithSelection = prepareIndicesFilter();
		
		this.focusElement = "doc_rifestrenirifnprot";
		this.openIndex("doc_rifestrenirifnprot", "doc_rifestrenirifnprot", this.doc_rifestrenirifnprot, "0", " ", needToFilterWithSelection);
		return null;
	}
	
	public void setAnnullati(boolean attivi) {
		this.annullati = attivi;
	}
	public boolean isAnnullati() {
		return annullati;
	}
	public void setNonannullati(boolean rpa) {
		this.nonannullati = rpa;
	}
	public boolean isNonannullati() {
		return nonannullati;
	}
	public void setBozze(boolean sottofascicoli) {
		this.bozze = sottofascicoli;
	}
	public boolean isBozze() {
		return bozze;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getXml() {
		return xml;
	}
	
	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public List<Option> getStato_select() {
		return stato_select;
	}

	public void setStato_select(List<Option> stato_select) {
		this.stato_select = stato_select;
	}

	public String navigateResponse(XMLDocumento response) throws Exception{
		if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")){
			return buildSpecificShowdocPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response);
		}
		else{
			SoginSAPTitles titles = new SoginSAPTitles();
			titles.getFormsAdapter().fillFormsFromResponse(response);
			
			titles.init(response.getDocument());
			titles.setPopupPage(isPopupPage());
			setSessionAttribute("soginSAPTitles", titles);
			return "soginSAP@showtitles";
		}
	}
	
	@Override
	protected String buildSpecificPageAndReturnNavigationRule(String dbTable, String dirTemplate, String personalPackage, String suffix, XMLDocumento response, String pageType, boolean popup) throws Exception {
		return buildSpecificPageAndReturnNavigationRule(dbTable, dirTemplate, "it.tredi.dw4.docway.beans", personalPackage, suffix, response, pageType, popup);
	}
	
	public void setDoc_anno(String fasc_anno) {
		this.doc_anno = fasc_anno;
	}
	public String getDoc_anno() {
		return doc_anno;
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
	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}
	public String getOrdinamento() {
		return ordinamento;
	}
	
	public String resetQuery(){
		custom_globale="";
		annullati=false;
		nonannullati=false;
		bozze=false;
		differito=false;
		custom_da_num_prot="";
		custom_a_num_prot="";
		custom_numeri_prot="";
		custom_repertorio="";
		custom_rep_da="";
		custom_rep_a="";
		range_docdataprot_from="";
		range_docdataprot_to="";
		doc_anno="";
		tipologia_repertorio="";
		doc_voceindice="";
		doc_oggetto="";
		doc_keywords="";
		doc_allegato="";
		custom_doc_classifcod="";
		custom_doc_classif="";
		doc_note="";
		doc_postit="";
		rpanomeuff="";
		rpanomepersona="";
		opnomeuff="";
		opnomepersona="";
		ccnomeuff="";
		ccnomepersona="";
		umnomepersona="";
		umnomeuff="";
		doc_rifesternirifnome="";
		codice_fiscale="";
		partita_iva="";
		range_docstoriacreazionedata_from="";
		range_docstoriacreazionedata_to="";
		doc_storiacreazioneoper="";
		doc_storiacreazioneuffoper="";
		rangenum_docnrecord_from="";
		rangenum_docnrecord_to="";
		range_docpubblicazioneal_from="";
		range_docpubblicazioneal_to="";
		richiedenteEsterno="";
		richiedenteInterno="";
		custom_numeri_rep="";
		mezzo_trasmissione="";
		tipologia="";
		nonbozze=false;
		non_repertoriati=false;
		repertorio_anno = "";
		causale = "";
		utente = "";
		docSap = "";
		dataRegistrazione_from = "";
		dataRegistrazione_to = "";
		stato = "";
		
		clearRepertori();
		
		return null;
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

	/*
	public String getTipoRepertorio(){
		if (custom_repertorio.length()<1) return "";
		for (Iterator<TitoloRepertorio> iterator = listof_rep.iterator(); iterator.hasNext();) {
			TitoloRepertorio option = (TitoloRepertorio) iterator.next();
			if (custom_repertorio.equals(option.getCodice())){
				return option.getTabella().getTipo();
			}
		}
		return "";
	}
	*/
	
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

	public void setDifferito(boolean differito) {
		this.differito = differito;
	}

	public boolean isDifferito() {
		return differito;
	}

	public void setRpamnomeuff(String rpamnomeuff) {
		this.rpamnomeuff = rpamnomeuff;
	}

	public String getRpamnomeuff() {
		return rpamnomeuff;
	}

	public void setRpamnomepersona(String rpamnomepersona) {
		this.rpamnomepersona = rpamnomepersona;
	}

	public String getRpamnomepersona() {
		return rpamnomepersona;
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

	public void setRepertori_multipli(boolean repertori_multipli) {
		this.repertori_multipli = repertori_multipli;
	}

	public boolean isRepertori_multipli() {
		return repertori_multipli;
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
	
	public String gotoTableQGlobale() throws Exception {
		this.resetQuery();
		
		formsAdapter.gotoTableQ("globale", false);
		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
			
		//riempi il formsAdapter della pagina di destinazione
		getFormsAdapter().fillFormsFromResponse(responseDoc);
		init(responseDoc.getDocument());
		
		return "soginSAP@home";
	}
	
	public String ricercaDocumentoFromSAP() throws Exception {
		SoginSAPConnector soginSAPConnector = (SoginSAPConnector) getSessionAttribute("soginSAPConnector");
		if (soginSAPConnector != null) {
			
			ServiceLocator loc = new ServiceLocator();
			loc.setZWS_SOGIN_UPDATE_TABEndpointAddress(DocWayProperties.readProperty(DocWayProperties.SOGINSAP_NAMESPACE, "SAPEndpoint", ""));
			ZWS_SOGIN_UPDATE_TAB_PortType zws = loc.getZWS_SOGIN_UPDATE_TAB();
			
			// to use Basic HTTP Authentication:
			((Stub) zws)._setProperty(Call.USERNAME_PROPERTY, DocWayProperties.readProperty(DocWayProperties.SOGINSAP_NAMESPACE, "SAPUser", ""));
			((Stub) zws)._setProperty(Call.PASSWORD_PROPERTY, DocWayProperties.readProperty(DocWayProperties.SOGINSAP_NAMESPACE, "SAPPassword", ""));
						
			ZSOGIN_DOC zsoginDoc = new ZSOGIN_DOC();
			
			//controllo parametri
			String keySap = soginSAPConnector.getKeySap();
			String idDoc = soginSAPConnector.getIdDoc();
			
			try {
				soginSAPConnector.setIdDoc(null);
				soginSAPConnector.setKeySap(null);
				
				if ( keySap != null && idDoc != null) {
					soginSAPConnector.setIdDoc(null);
					
					//in questo caso cerchiamo su numero di protocollo
					XMLDocumento response = super._queryPlain("[/doc/@num_prot]=\"" + idDoc + "\"", "", "");
					if (handleErrorResponse(response)) {
						zsoginDoc.setKEYSAP(keySap);
						zsoginDoc.setDOCSAP("");
						zsoginDoc.setIDDOC("");
						zsoginDoc.setMANDT("010");
						zsoginDoc.setBUKRS("SOGI");
						zsoginDoc.setBELNR("0000000000");
						zsoginDoc.setGJAHR("2000");
						zsoginDoc.setAWTYP("01");
						zsoginDoc.setZZST_IDSOG("id doc");
						zsoginDoc.setTCODE("0000");
						zsoginDoc.setUNAME("000000000000");
						zsoginDoc.setSTBLG("0000000000");
						zsoginDoc.setSTJAH("0000");
						zsoginDoc.setZLOCK("X");
						String result = zws.zwsSogUpdateTabDoc(zsoginDoc);
						if (!result.trim().startsWith("01"))
							throw new Exception(result);
						
						return null;
					}
					
					zsoginDoc.setKEYSAP(keySap);
					zsoginDoc.setIDDOC(idDoc);
					zsoginDoc.setDOCSAP("");
					zsoginDoc.setMANDT("010");
					zsoginDoc.setBUKRS("SOGI");
					zsoginDoc.setBELNR("0000000000");
					zsoginDoc.setGJAHR("2000");
					zsoginDoc.setAWTYP("01");
					zsoginDoc.setZZST_IDSOG("id doc");
					zsoginDoc.setTCODE("0000");
					zsoginDoc.setUNAME("000000000000");
					zsoginDoc.setSTBLG("0000000000");
					zsoginDoc.setSTJAH("0000");
					zsoginDoc.setZLOCK("X");
					String result = zws.zwsSogUpdateTabDoc(zsoginDoc);
					if (!result.trim().startsWith("01"))
						throw new Exception(result);
					
					return navigateResponse(response);
				}
			}
			catch (Exception e) {
				zsoginDoc.setKEYSAP(keySap);
				zsoginDoc.setIDDOC("");
				zsoginDoc.setDOCSAP("");
				zsoginDoc.setMANDT("010");
				zsoginDoc.setBUKRS("SOGI");
				zsoginDoc.setBELNR("0000000000");
				zsoginDoc.setGJAHR("2000");
				zsoginDoc.setAWTYP("01");
				zsoginDoc.setZZST_IDSOG("id doc");
				zsoginDoc.setTCODE("0000");
				zsoginDoc.setUNAME("000000000000");
				zsoginDoc.setSTBLG("0000000000");
				zsoginDoc.setSTJAH("0000");
				zsoginDoc.setZLOCK("X");
				String result = zws.zwsSogUpdateTabDoc(zsoginDoc);
				if (!result.trim().startsWith("01"))
					throw new Exception(result);
				
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
		}
		
		try {
			generateError("Connettore SAP mancante: sessione scaduta");
			return null;
		}
		catch (Exception e) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String mostraDocumentoFromSAP() throws Exception {
		SoginSAPConnector soginSAPConnector = (SoginSAPConnector) getSessionAttribute("soginSAPConnector");
		if (soginSAPConnector != null) {
			ServiceLocator loc = new ServiceLocator();
			loc.setZWS_SOGIN_UPDATE_TABEndpointAddress(DocWayProperties.readProperty(DocWayProperties.SOGINSAP_NAMESPACE, "SAPEndpoint", ""));
			ZWS_SOGIN_UPDATE_TAB_PortType zws = loc.getZWS_SOGIN_UPDATE_TAB();
			
			// to use Basic HTTP Authentication:
			((Stub) zws)._setProperty(Call.USERNAME_PROPERTY, DocWayProperties.readProperty(DocWayProperties.SOGINSAP_NAMESPACE, "SAPUser", ""));
			((Stub) zws)._setProperty(Call.PASSWORD_PROPERTY, DocWayProperties.readProperty(DocWayProperties.SOGINSAP_NAMESPACE, "SAPPassword", ""));
			
			ZSOGIN_DOC zsoginDoc = new ZSOGIN_DOC();
			
			//controllo parametri
			String docSap = soginSAPConnector.getDocSap();
			String idDoc = soginSAPConnector.getIdDoc();
			
			try {
				soginSAPConnector.setIdDoc(null);
				soginSAPConnector.setDocSap(null);
				if ( docSap != null && idDoc != null) {
					
					XMLDocumento response = super._queryPlain("[/doc/@nrecord]=\"" + soginSAPConnector.getIdDoc() + "\"", "", "");
					if (handleErrorResponse(response)) {
						zsoginDoc.setDOCSAP(docSap);
						zsoginDoc.setIDDOC("");
						zsoginDoc.setMANDT("010");
						zsoginDoc.setBUKRS("SOGI");
						zsoginDoc.setBELNR("0000000000");
						zsoginDoc.setGJAHR("2000");
						zsoginDoc.setAWTYP("01");
						zsoginDoc.setKEYSAP("");
						zsoginDoc.setZZST_IDSOG("id doc");
						zsoginDoc.setTCODE("0000");
						zsoginDoc.setUNAME("000000000000");
						zsoginDoc.setSTBLG("0000000000");
						zsoginDoc.setSTJAH("0000");
						zsoginDoc.setZLOCK("X");
						String result = zws.zwsSogUpdateTabDoc(zsoginDoc);
						if (!result.trim().startsWith("01"))
							throw new Exception(result);
						
						return null;
					}
					
					//controlla che il doc trovato sia associato alla fattura SAP richiesta
					if (!response.isXPathFound("//doc/extra/soginSAP/docSAP[text() = '" + docSap + "']")) {
						
						zsoginDoc.setDOCSAP("");
						zsoginDoc.setIDDOC(idDoc);
						zsoginDoc.setMANDT("010");
						zsoginDoc.setBUKRS("SOGI");
						zsoginDoc.setBELNR("0000000000");
						zsoginDoc.setGJAHR("2000");
						zsoginDoc.setAWTYP("01");
						zsoginDoc.setKEYSAP("");
						zsoginDoc.setZZST_IDSOG("id doc");
						zsoginDoc.setTCODE("0000");
						zsoginDoc.setUNAME("000000000000");
						zsoginDoc.setSTBLG("0000000000");
						zsoginDoc.setSTJAH("0000");
						zsoginDoc.setZLOCK("X");
						String result = zws.zwsSogUpdateTabDoc(zsoginDoc);
						if (!result.trim().startsWith("01"))
							throw new Exception(result);
						
						generateError("Documento numero " + idDoc + " non associato con fattura " + docSap);
						return null;
					}
					else {
						zsoginDoc.setDOCSAP(docSap);
						zsoginDoc.setIDDOC(idDoc);
						zsoginDoc.setMANDT("010");
						zsoginDoc.setBUKRS("SOGI");
						zsoginDoc.setBELNR("0000000000");
						zsoginDoc.setGJAHR("2000");
						zsoginDoc.setAWTYP("01");
						zsoginDoc.setKEYSAP("");
						zsoginDoc.setZZST_IDSOG("id doc");
						zsoginDoc.setTCODE("0000");
						zsoginDoc.setUNAME("000000000000");
						zsoginDoc.setSTBLG("0000000000");
						zsoginDoc.setSTJAH("0000");
						zsoginDoc.setZLOCK("X");
						String result = zws.zwsSogUpdateTabDoc(zsoginDoc);
						if (!result.trim().startsWith("01"))
							throw new Exception(result);
						return navigateResponse(response);
					}
				}
				else {
					zsoginDoc.setDOCSAP("");
					zsoginDoc.setIDDOC("");
					zsoginDoc.setMANDT("010");
					zsoginDoc.setBUKRS("SOGI");
					zsoginDoc.setBELNR("0000000000");
					zsoginDoc.setGJAHR("2000");
					zsoginDoc.setAWTYP("01");
					zsoginDoc.setKEYSAP("");
					zsoginDoc.setZZST_IDSOG("id doc");
					zsoginDoc.setTCODE("0000");
					zsoginDoc.setUNAME("000000000000");
					zsoginDoc.setSTBLG("0000000000");
					zsoginDoc.setSTJAH("0000");
					zsoginDoc.setZLOCK("X");
					String result = zws.zwsSogUpdateTabDoc(zsoginDoc);
					if (!result.trim().startsWith("01"))
						throw new Exception(result);
					
					soginSAPConnector.setIdDoc(null);
					soginSAPConnector.setDocSap(null);
					generateError("Dati necessari per la visualizzazione SAP errati o mancanti");
					return null;
				}
			}
			catch (Exception e) {
				zsoginDoc.setDOCSAP("");
				zsoginDoc.setIDDOC("");
				zsoginDoc.setMANDT("010");
				zsoginDoc.setBUKRS("SOGI");
				zsoginDoc.setBELNR("0000000000");
				zsoginDoc.setGJAHR("2000");
				zsoginDoc.setAWTYP("01");
				zsoginDoc.setKEYSAP("");
				zsoginDoc.setZZST_IDSOG("id doc");
				zsoginDoc.setTCODE("0000");
				zsoginDoc.setUNAME("000000000000");
				zsoginDoc.setSTBLG("0000000000");
				zsoginDoc.setSTJAH("0000");
				zsoginDoc.setZLOCK("X");
				
				String result = zws.zwsSogUpdateTabDoc(zsoginDoc);
				if (!result.trim().startsWith("01"))
					throw new Exception(result);
				
				soginSAPConnector.setIdDoc(null);
				soginSAPConnector.setDocSap(null);
				
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
		}
		
		try {
			generateError("Connettore SAP mancante: sessione scaduta");
			return null;
		}
		catch (Exception e) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String getDocSap() {
		return docSap;
	}

	public void setDocSap(String docSap) {
		this.docSap = docSap;
	}

	public String getUtente() {
		return utente;
	}

	public void setUtente(String utente) {
		this.utente = utente;
	}

	public String getCausale() {
		return causale;
	}

	public void setCausale(String causale) {
		this.causale = causale;
	}
	
	public String getDataRegistrazione_from() {
		return dataRegistrazione_from;
	}

	public void setDataRegistrazione_from(String dataRegistrazione_from) {
		this.dataRegistrazione_from = dataRegistrazione_from;
	}

	public String getDataRegistrazione_to() {
		return dataRegistrazione_to;
	}

	public void setDataRegistrazione_to(String dataRegistrazione_to) {
		this.dataRegistrazione_to = dataRegistrazione_to;
	}

	public void setFormsAdapter(DocDocWayQueryFormsAdapter formsAdapter) {
		this.formsAdapter = formsAdapter;
	}
	
	public String terminaAssociazioneFattura() throws Exception {
		SoginSAPConnector soginSAPConnector = (SoginSAPConnector) getSessionAttribute("soginSAPConnector");
		if (soginSAPConnector != null) {
			try {
				//controllo parametri
				String docSap = soginSAPConnector.getDocSap();
				String idDoc = soginSAPConnector.getIdDoc();
				String dataRegistrazione = soginSAPConnector.getDataRegistrazione();
				String utente = soginSAPConnector.getUtente();
				String causale = soginSAPConnector.getCausale();
				if ( causale != null && docSap != null && dataRegistrazione != null && idDoc != null && utente != null) {
					//carica documento
					XMLDocumento response = super._queryPlain("[/doc/@nrecord]=\"" + idDoc + "\"", "", "");
					if (handleErrorResponse(response)) return null;
					
					SoginSAPAssociateDoc soginSAPAssociateDoc = new SoginSAPAssociateDoc();
					soginSAPAssociateDoc.getFormsAdapter().fillFormsFromResponse(response);
					soginSAPAssociateDoc.init(response.getDocument());
					
					SAPDoc doc = (SAPDoc) soginSAPAssociateDoc.getDoc();
					
					//associazione fattura
					if (causale.equals("FB60") 
						|| causale.equals("F-43") 
						|| causale.equals("FB65")
						|| causale.equals("F-41")
						|| causale.equals("MIRO")) {
						
						//controllo se documento già associato
						Attribute statoSAP = (Attribute) response.selectSingleNode("//doc/extra/soginSAP/@stato");
						
						if (statoSAP == null || !statoSAP.getValue().equals("associato")) {
							
							Map<String, String> params = new HashMap<String, String>();
							
							if (statoSAP != null) {
								//gestisci lo storico delle associazioni SAP
								Element docSapEl = (Element) response.selectSingleNode("//doc/extra/soginSAP/docSAP");
								Element dataRegistrazioneEl = (Element) response.selectSingleNode("//doc/extra/soginSAP/dataRegistrazione");
								Element utenteEl = (Element) response.selectSingleNode("//doc/extra/soginSAP/utente");
								Element causaleEl = (Element) response.selectSingleNode("//doc/extra/soginSAP/causale");
								Attribute causaleCod = (Attribute) response.selectSingleNode("//doc/extra/soginSAP/causale/@cod");
								
								SAPStoria sapStoria = new SAPStoria();
								sapStoria.setCausale(causaleEl.getText());
								sapStoria.setCausaleCod(causaleCod.getValue());
								sapStoria.setDocSap(docSapEl.getText());
								sapStoria.setDataRegistrazione(dataRegistrazioneEl.getText());
								sapStoria.setUtente(utenteEl.getText());
								sapStoria.setStato(statoSAP.getValue());
								params.putAll(sapStoria.asFormAdapterParams(".extra.storiaSAP.modifica[" + doc.getStoriaSAP().size() + "]"));
							}
						
							params.put(".extra.soginSAP.@stato", "associato");
							params.put(".extra.soginSAP.docSAP", docSap);
							params.put(".extra.soginSAP.dataRegistrazione", dataRegistrazione);
							params.put(".extra.soginSAP.utente", utente);
							params.put(".extra.soginSAP.causale", getCausaleMsgFromCod(causale));
							params.put(".extra.soginSAP.causale.@cod", causale);
							
							//parametri per gestione della storia
							params.put("storia_azione", "associazione fattura SAP");
							params.put("storia_intervento", "Documento associato alla fattura SAP num. " + idDoc);
							
							String saveResponseString = soginSAPAssociateDoc.saveDocument(params);
							if (saveResponseString == null)
								return null;
							
							response = new XMLDocumento(saveResponseString);
							if (handleErrorResponse(response)) return null;
							
							this.getFormsAdapter().fillFormsFromResponse(response);
							this.init(response.getDocument());
							
							response = super._queryPlain("[/doc/@nrecord]=\"" + idDoc + "\"", "", "");
							if (handleErrorResponse(response)) return null;
							
							soginSAPConnector.setDocSap(null);
							soginSAPConnector.setIdDoc(null);
							soginSAPConnector.setDataRegistrazione(null);
							soginSAPConnector.setUtente(null);
							soginSAPConnector.setCausale(null);
							
							return navigateResponse(response);
						}
						else {
							soginSAPConnector.setDocSap(null);
							soginSAPConnector.setIdDoc(null);
							soginSAPConnector.setDataRegistrazione(null);
							soginSAPConnector.setUtente(null);
							soginSAPConnector.setCausale(null);
							
							generateError("Documento già associato a SAP");
							return null;
						}
					}
					//storno
					else if (causale.equals("FB08") || causale.equals("MR8M")) {
						//controllo se documento è associato
						Attribute statoSAP = (Attribute) response.selectSingleNode("//doc/extra/soginSAP/@stato");
						
						if (statoSAP != null && statoSAP.getValue().equals("associato")) {
							//controllo che il DOCSAP passato corrisponda a quello salvato sul documento
							Element docSapEl = (Element) response.selectSingleNode("//doc/extra/soginSAP/docSAP");
							if (docSapEl == null || !docSapEl.getTextTrim().equals(docSap)) {
								generateError("Stornatura: Numero documento SAP non corrispondente a quello precedentemente associato");
								return null;
							}
							
							Element dataRegistrazioneEl = (Element) response.selectSingleNode("//doc/extra/soginSAP/dataRegistrazione");
							Element utenteEl = (Element) response.selectSingleNode("//doc/extra/soginSAP/utente");
							Element causaleEl = (Element) response.selectSingleNode("//doc/extra/soginSAP/causale");
							Attribute causaleCod = (Attribute) response.selectSingleNode("//doc/extra/soginSAP/causale/@cod");
							
							Map<String, String> params = new HashMap<String, String>();
							
							//gestisci lo storico delle associazioni SAP
							SAPStoria sapStoria = new SAPStoria();
							sapStoria.setCausale(causaleEl.getText());
							sapStoria.setCausaleCod(causaleCod.getValue());
							sapStoria.setDocSap(docSapEl.getText());
							sapStoria.setDataRegistrazione(dataRegistrazioneEl.getText());
							sapStoria.setUtente(utenteEl.getText());
							sapStoria.setStato(statoSAP.getValue());
							params.putAll(sapStoria.asFormAdapterParams(".extra.storiaSAP.modifica[" + doc.getStoriaSAP().size() + "]"));
							
							params.put(".extra.soginSAP.@stato", "stornato");
							params.put(".extra.soginSAP.dataRegistrazione", dataRegistrazione);
							params.put(".extra.soginSAP.utente", utente);
							params.put(".extra.soginSAP.causale", getCausaleMsgFromCod(causale));
							params.put(".extra.soginSAP.causale.@cod", causale);
							
							//parametri per gestione della storia
							params.put("storia_azione", "storno fattura SAP");
							params.put("storia_intervento", "Documento stornato dalla fattura SAP num. " + idDoc);
							
							String saveResponseString = soginSAPAssociateDoc.saveDocument(params);
							if (saveResponseString == null)
								return null;
							
							response = new XMLDocumento(saveResponseString);
							if (handleErrorResponse(response)) return null;
							
							this.getFormsAdapter().fillFormsFromResponse(response);
							this.init(response.getDocument());
							
							response = super._queryPlain("[/doc/@nrecord]=\"" + idDoc + "\"", "", "");
							if (handleErrorResponse(response)) return null;
							
							soginSAPConnector.setDocSap(null);
							soginSAPConnector.setIdDoc(null);
							soginSAPConnector.setDataRegistrazione(null);
							soginSAPConnector.setUtente(null);
							soginSAPConnector.setCausale(null);
							
							return navigateResponse(response);
						}
						else {
							soginSAPConnector.setDocSap(null);
							soginSAPConnector.setIdDoc(null);
							soginSAPConnector.setDataRegistrazione(null);
							soginSAPConnector.setUtente(null);
							soginSAPConnector.setCausale(null);
							
							generateError("Documento non associato a SAP");
							return null;
						}
						
					}
					else {
						generateError("Causale errata: " + causale);
						return null;
					}
				}
				else {
					generateError("Dati necessari per l'associazione SAP errati o mancanti");
					return null;
				}
				
			} catch (Exception e) {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
		}
		
		try {
			generateError("Connettore SAP mancante: sessione scaduta");
			return null;
		}
		catch (Exception e) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(e));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String clearRepertori() {
		if (this.listof_rep != null) {
			for (TitoloRepertorio repertorio : this.listof_rep) {
	    		if (repertorio.getList_tabelle() != null && repertorio.getList_tabelle().size() > 0) {
	        		for (int j=0; j < repertorio.getList_tabelle().size(); j++) {
	        			Tabella repTable = repertorio.getList_tabelle().get(j);
	        			repTable.setSelected(false);
	        		}
	    		}
			}
		}
		
		return null;
	}
	
	public String flagRepertori() {
		for (TitoloRepertorio repertorio : this.listof_rep) {
    		if (repertorio.getList_tabelle() != null && repertorio.getList_tabelle().size() > 0) {
        		for (int j=0; j < repertorio.getList_tabelle().size(); j++) {
        			Tabella repTable = repertorio.getList_tabelle().get(j);
        			repTable.setSelected(true);
        		}
    		}
		}
		
		return null;
	}
	
	private void generateError(String message) throws Exception {
		handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(new Exception(message)));
		formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
	}
	
	private String getCausaleMsgFromCod(String causale) {
		if (causale.equals("FB60")) {
			return "Fattura";
		}
		else if (causale.equals("F-43")) {
			return "Fattura generale";
		}
		else if (causale.equals("FB65")) {
			return "Accredito";
		}
		else if (causale.equals("F-41")) {
			return "Accredito generale";
		}
		else if (causale.equals("FB08")) {
			return "Storno singolo";
		}
		else if (causale.equals("MIRO")) {
			return "Inserire fattura in entrata";
		}
		else if (causale.equals("MR8M")) {
			return "Stornare documento fattura";
		}
		
		return "";
	}
	
	private void applyRepertoriRestrictions() throws IOException {
		List<String> restrictions = Arrays.asList(DocWayProperties.readProperty(DocWayProperties.SOGINSAP_NAMESPACE, "repertori", "").split("\\|"));
		
		Iterator<TitoloRepertorio> repertoriIt = this.listof_rep.iterator();
		while (repertoriIt.hasNext()) {
			TitoloRepertorio repertorio = repertoriIt.next();
			if (repertorio.getList_tabelle() != null && repertorio.getList_tabelle().size() > 0) {
        		for (int j=0; j < repertorio.getList_tabelle().size(); j++) {
        			Tabella repTable = repertorio.getList_tabelle().get(j);
        			repTable.setSelected(false);
        		}
			}
			
			if (!restrictions.contains(repertorio.getCodice()))
				repertoriIt.remove();
		}
		
	}
	
	private boolean prepareIndicesFilter() throws Exception {
		boolean needToFilter = true;
		
		//aggiungi il filtro sui repertori
    	String repFilter = "";
    	for (TitoloRepertorio repertorio : this.listof_rep) {
    		if (repertorio.getList_tabelle() != null && repertorio.getList_tabelle().size() > 0) {
        		for (int j=0; j < repertorio.getList_tabelle().size(); j++) {
        			Tabella repTable = repertorio.getList_tabelle().get(j);
        			if (repTable.isSelected()) {
    			
		    		String repTableType = repTable.getTipo();
		    		
		    		if ( "A".equals(repTableType) ) repTableType = "arrivo" ;
		            else if ( "I".equals(repTableType) ) repTableType = "interno" ;
		            else if ( "P".equals(repTableType) ) repTableType = "partenza" ;
		            else if ( "V".equals(repTableType) ) repTableType = "varie" ;
		    		
		    		if ( repFilter.length() == 0 )
		    			repFilter = "(([doc_tipo]=" + repTableType + " AND [doc_repertoriocod]=\"" + repertorio.getCodice() + "\")";
		            else
		            	repFilter += " OR ([doc_tipo]=" + repTableType + " AND [doc_repertoriocod]=\"" + repertorio.getCodice() + "\")";
        			}
        		}
    		}
    	}
//    	if (!repFilter.equals(""))
//    		repFilter += ")";
//    	else
//    		throw new Exception("Nessun repertorio selezionato");
//    	
//    	XMLDocumento response = super._queryPlain(repFilter, "", "");
//    	this.getFormsAdapter().fillFormsFromResponse(response);
    	
    	if (!repFilter.equals("")) {
    		repFilter += ")";
    		
    		XMLDocumento response = super._queryPlain(repFilter, "", "");
        	this.getFormsAdapter().fillFormsFromResponse(response);
        	
        	needToFilter = true;
    	}
    	else { //resetta la selezione corrente
    		this.getFormsAdapter().cancellaSelezioniCorrenti();
    		
    		needToFilter = false;
    	}
    	
    	return needToFilter;
	}
}
