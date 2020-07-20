package it.tredi.dw4.docway.beans;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.docway.model.Tabella;
import it.tredi.dw4.docway.model.TitoloRepertorio;
import it.tredi.dw4.docway.model.delibere.Custom_comp;
import it.tredi.dw4.docway.model.delibere.Organo;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;

public class QueryGlobale_to extends QueryGlobale {
	
	private List<Organo> organi;
	private String selectedOrgano = "";
	private String queryType = "query4standard";
	
	/*
	 * query4Standard
	 * */
	private boolean custom_verbale = false;
	private boolean custom_delibera = false; //anche per query4comp
	private boolean custom_proposta = false;
	private boolean custom_comunicazione = false;
	private boolean custom_seduta = false;
	
	//Anno seduta 
	private String custom_sedannoconvocazione = ""; //onblur="IsNumeric(this, false, true);"
	
	//Data seduta (gg/mm/aaaa)
	private String range_docodgsedutadataconvocazione_from = ""; //hxp-range-rangedatacheck
	private String range_docodgsedutadataconvocazione_to = ""; //hxp-range-rangedatacheck
	
	//Anno delibera/verbale
	private String custom_annodelverb = ""; //onblur="IsNumeric(this, false, true);"
	
	//N° delibera/verbale
	private String custom_numdelibverb_da = "";
	private String custom_numdelibverb_a = "";
	
	//UOR
	private String uor = "";
	
	//Oggetto
	private String doc_oggetto = "";
		
	//Full text
	private String custom_fulltext = "";
	
	/*
	 * query4Comp
	 * */
	private String relComponenti = "OR";
	private List<Custom_comp> componenti = new ArrayList<Custom_comp>();
	
	private String comunicazioniLabel = "";
	
	public QueryGlobale_to() throws Exception {
		super();
	}
	
	public QueryGlobale_to(String queryType) throws Exception {
		this();
		this.setQueryType(queryType);
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom){
		super.init(dom);
		
		this.organi = 			XMLUtil.parseSetOfElement(dom, "/response/organo", new Organo());
		
		String dicitComunicazione	= 	FormsAdapter.getParameterFromCustomTupleValue("dicitComunicazione", getFormsAdapter().getCustomTupleName());
		this.setComunicazioniLabel(dicitComunicazione.substring(dicitComunicazione.indexOf("|")+1));
		
		initComponenti();
	}
	
	private void initComponenti(){
		int numberOfComponenti = 3; 
		for (int i = 0; i< numberOfComponenti; i++)
			this.componenti.add(new Custom_comp());
	}
	
	private void resetComponenti(){
		this.componenti.clear();
		initComponenti();
	}
	
	@Override
	public String queryPlain() throws Exception {
		try {
			
			String listaRepVisCompleta = "";
			
			for(TitoloRepertorio repertorio : getListof_rep()){
				listaRepVisCompleta += makeListaRepVisCompleta(repertorio);
			}
			
			if(!listaRepVisCompleta.isEmpty())
				getFormsAdapter().queryplain(listaRepVisCompleta);
			
			String query = createQuery();
			if("error".equals(query)) return null;

			return queryPlain(query);
			
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	@Override
	public String queryPlain(String query) throws Exception {
		return super.queryPlain(query);
	}
	
	@Override
	public String createQuery() throws Exception {
		String query = "";
		
		query = getPhraseForTo();
		
		if(!query.equalsIgnoreCase("__STOP_QUERY__"))
		{	
			if(!uor.equals(""))
				query += " AND ([doc_rifinternirifnomeuff]="+ uor + ") adj ([doc_rifinternirifdiritto]=RPA) ";
			
			if(!range_docodgsedutadataconvocazione_from.isEmpty() || !range_docodgsedutadataconvocazione_to.isEmpty())
				query += " AND " + addDateRangeQuery("docodgsedutadataconvocazione", range_docodgsedutadataconvocazione_from, range_docodgsedutadataconvocazione_to);
			
			if(!doc_oggetto.isEmpty())
				query = addToQuery(query,"[doc_oggetto]=" + escapeQueryValue(doc_oggetto) , "AND");
			
			return query;
		}else
		{
			return "error";
		}
	}
	
	@Override
	public String resetQuery() {
		super.resetQuery();
		
		this.selectedOrgano = "";
		this.custom_verbale = false;
		this.custom_delibera = false;
		this.custom_proposta = false;
		this.custom_comunicazione = false;
		this.custom_seduta = false;
		this.custom_sedannoconvocazione = ""; 
		this.range_docodgsedutadataconvocazione_from = ""; 
		this.range_docodgsedutadataconvocazione_to = "";
		this.custom_annodelverb = ""; 
		this.custom_numdelibverb_da = "";
		this.custom_numdelibverb_a = "";
		this.uor = "";
		this.doc_oggetto = "";
		this.custom_fulltext = "";
		this.relComponenti = "OR";
		resetComponenti();
		
		return null;
	}
	
	private String makeListaRepVisCompleta(TitoloRepertorio repertorio){
		String codice = repertorio.getCodice();
		String listaRepVisCompleta = "";
		
		for(Tabella tabella : repertorio.getList_tabelle()){
			if (tabella.getVis().equals("completa"))
				listaRepVisCompleta += codice + "-" + tabella.getTipo() + "|";
		}
		
		return listaRepVisCompleta;
	}
	
	private String getPhraseForTo(){
		if(queryType.equals("query4comp"))
		{
			//ricerca per componenti
			return getQueryForComponenti();
		}else
		{
			//ricerca standard
			return getQueryStandard();
		}
	}
	
	private String getQueryForComponenti(){
		String query = "";
		query = "[ALIAS_C]=\"" + selectedOrgano.replaceAll(";", "\" OR \"") + "\"";
		
		String compList = "";
		boolean primoPieno = false;
		String tmp_s = "";
		
		for(Custom_comp componente : componenti){
			tmp_s = componente.getValue().trim();
			
			if(!tmp_s.isEmpty()){
				if(!primoPieno)
					primoPieno = true;
				else
					compList += " " + relComponenti + " ";
			compList += "([ALIAS_A]=\"" + tmp_s + "\"" + componente.getPresenzaForQuery() + ")";
			}
		}
		
		if(primoPieno)
			query = "(" + query + ") AND (" + compList + ")";
		
		if(custom_seduta && !custom_delibera)//seduta
		{
			query = query.replaceAll("ALIAS_A", "sed_nominativo");
			query = query.replaceAll("ALIAS_B", "sed_presenza");
			query = query.replaceAll("ALIAS_C", "sed_organocod");
		}else if(!custom_seduta && custom_delibera)//delibera
		{
			query = query.replaceAll("ALIAS_A", "doc_deliberanominativo");
			query = query.replaceAll("ALIAS_B", "doc_deliberaparere");
			query = query.replaceAll("ALIAS_C", "doc_propostacodorgano");
		}else if( (custom_seduta && custom_delibera) || (!custom_seduta && !custom_delibera))//entrambi selezinati o nulla selezionato -> seduta OR delibera
		{
			String query1 = query.replaceAll("ALIAS_A", "ALIAS1_A");
			query1 = query1.replaceAll("ALIAS_B", "ALIAS1_B");
			query1 = query1.replaceAll("ALIAS_C", "ALIAS1_C");
			
			query = new String("(" + query + ") OR (" + query1 + ")").replaceAll("ALIAS_A", "sed_nominativo");
			query = query.replaceAll("ALIAS_B", "sed_presenza");
			query = query.replaceAll("ALIAS_C", "sed_organocod");
			query = query.replaceAll("ALIAS1_A", "doc_deliberanominativo");
			query = query.replaceAll("ALIAS1_B", "doc_deliberaparere");
			query = query.replaceAll("ALIAS1_C", "doc_propostacodorgano");
		}
		
		return query;
	}
	
	private String getQueryStandard(){
		String query = "";
		query = "[doc_propostacodorgano]=\"" + selectedOrgano.replaceAll(";", "\" OR \"") + "\"";
		
		if(!custom_sedannoconvocazione.isEmpty())
			query = addToQuery(query,"[doc_odgsedutaannoconvocazione]=\"" + custom_sedannoconvocazione + "\"", "AND");
		
		if(!custom_fulltext.isEmpty())
			query = addToQuery(query, "[@]=" + this.escapeQueryValue(custom_fulltext) + " OR [doc_filesfiletesto]=" + this.escapeQueryValue(custom_fulltext), "AND");
		
		String anno_del_verb = !custom_annodelverb.isEmpty() ? custom_annodelverb : "_ANNOCORRENTE_";
		
		String aliasRep = "doc_repertoriocod";
		String tipo = "";
		String apici = "\"";
		
		if(!custom_numdelibverb_da.isEmpty() || !custom_numdelibverb_a.isEmpty()){
			//forzare la scelta di almeno un tipo di documento
			if(custom_verbale || custom_delibera || custom_proposta || custom_comunicazione){
				aliasRep = "doc_repertorionumero";
				
				String rep_da = custom_numdelibverb_da;
				String rep_a = custom_numdelibverb_a;
				
				if(!rep_a.isEmpty() && !rep_a.equalsIgnoreCase(rep_da)){// 'a' pieno e differente da 'da'
					tipo = "{\"REPERTORIO^" + "_CODSEDE_-" + anno_del_verb + StringUtil.fillString(rep_da, "0", 7) + "\"|\"" + 
							"REPERTORIO^" + "_CODSEDE_-" + anno_del_verb + StringUtil.fillString(rep_a, "0", 7) + "\"}";
				}else{// 'a' vuoto o uguale a 'da'
					tipo = "\"REPERTORIO^" + "_CODSEDE_-" + anno_del_verb + StringUtil.fillString(rep_da, "0", 7) + "\"";
				}
				apici = "";
			}else{
				//send alert(Se si indica un numero di delibera/verbale è necessario selezionare almeno un tipo di documento!)
				setErrorMessageNoInputField("templateForm:tipoDocumento", "Se si indica un numero di delibera/verbale è necessario selezionare almeno un tipo di documento!");
				return "__STOP_QUERY__";
			}
		}//ricerca per anno di del./verb. senza indicazione del numero di del./verb. [M 0000015]
		else if(!custom_annodelverb.isEmpty()){
			if(custom_verbale || custom_delibera || custom_proposta || custom_comunicazione){
				aliasRep = "doc_repertorionumero";
				tipo = "\"REPERTORIO^" + "_CODSEDE_-" + anno_del_verb + "*\"";
				apici = "";
			}else{
				//send alert(Se si indica un numero di delibera/verbale è necessario selezionare almeno un tipo di documento!)
				setErrorMessageNoInputField("templateForm:tipoDocumento", "Se si indica un numero di delibera/verbale è necessario selezionare almeno un tipo di documento!");
				return "__STOP_QUERY__";
			}
		}
		
		String queryForTipo = "";
		if(custom_verbale){
			queryForTipo = addToQuery(queryForTipo, "([" + aliasRep + "]=" + apici + organiSceltiToRep(selectedOrgano, "VxD", tipo) + apici + ") AND [doc_tipo]=partenza", "OR");
		}
		if(custom_delibera){
			queryForTipo = addToQuery(queryForTipo, "([" + aliasRep + "]=" + apici + organiSceltiToRep(selectedOrgano, "D", tipo) + apici + ") AND [doc_tipo]=interno", "OR");
		}
		if(custom_proposta){
			queryForTipo = addToQuery(queryForTipo, "[doc_personalviewcod]=\"PRP\" AND [doc_repertorionumero]=\"&null;\"", "OR");
		}
		if(custom_comunicazione){
			queryForTipo = addToQuery(queryForTipo, "[doc_personalviewcod]=\"CMZ\"", "OR");
		}
		
		query = addToQuery(query, queryForTipo, "AND");
		return query;
	}
	
	private String organiSceltiToRep(String organiScelti, String tipo, String tipo1){
		String[] v = organiScelti.split(";");
		String ret = "";
		String s;
		
		for(int i=0; i<v.length;i++){
			s = v[i];
			if(tipo1.indexOf("REPERTORIO") != -1){
				ret += ";" + tipo1.replaceAll("REPERTORIO", tipo + "x" + s.substring(s.indexOf("-")+1));
			}else{
				ret += ";" + tipo + "x" + s.substring(s.indexOf("-")+1);
			}
		}
		
		if(tipo1.indexOf("REPERTORIO") != -1){
			return ret.substring(1).replaceAll(";", " OR ");
		}else
		{
			return ret.substring(1).replaceAll(";", "\" OR \"");
		}
	}
	
	private String addToQuery(String frase, String frammento, String tipo){
		if(frammento.isEmpty())
			return frase;
		
		if(frase.isEmpty())
			return frammento;
		else
			return "(" + frase + ") " + tipo + " (" + frammento + ")";
	}
	
	/*
	 * getter / setter
	 * */
	public List<Organo> getOrgani() {
		return organi;
	}

	public void setOrgani(List<Organo> organi) {
		this.organi = organi;
	}

	public String getSelectedOrgano() {
		return selectedOrgano;
	}

	public void setSelectedOrgano(String selectedOrgano) {
		this.selectedOrgano = selectedOrgano;
	}

	public boolean isCustom_verbale() {
		return custom_verbale;
	}

	public void setCustom_verbale(boolean custom_verbale) {
		this.custom_verbale = custom_verbale;
	}

	public boolean isCustom_delibera() {
		return custom_delibera;
	}

	public void setCustom_delibera(boolean custom_delibera) {
		this.custom_delibera = custom_delibera;
	}

	public boolean isCustom_proposta() {
		return custom_proposta;
	}

	public void setCustom_proposta(boolean custom_proposta) {
		this.custom_proposta = custom_proposta;
	}

	public boolean isCustom_comunicazione() {
		return custom_comunicazione;
	}

	public void setCustom_comunicazione(boolean custom_comunicazione) {
		this.custom_comunicazione = custom_comunicazione;
	}

	public String getCustom_fulltext() {
		return custom_fulltext;
	}

	public void setCustom_fulltext(String custom_fulltext) {
		this.custom_fulltext = custom_fulltext;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getUor() {
		return uor;
	}

	public void setUor(String uor) {
		this.uor = uor;
	}

	public String getDoc_oggetto() {
		return doc_oggetto;
	}

	public void setDoc_oggetto(String doc_oggetto) {
		this.doc_oggetto = doc_oggetto;
	}

	public String getCustom_sedannoconvocazione() {
		return custom_sedannoconvocazione;
	}

	public void setCustom_sedannoconvocazione(String custom_sedannoconvocazione) {
		this.custom_sedannoconvocazione = custom_sedannoconvocazione;
	}

	public String getRange_docodgsedutadataconvocazione_from() {
		return range_docodgsedutadataconvocazione_from;
	}

	public void setRange_docodgsedutadataconvocazione_from(
			String range_docodgsedutadataconvocazione_from) {
		this.range_docodgsedutadataconvocazione_from = range_docodgsedutadataconvocazione_from;
	}

	public String getRange_docodgsedutadataconvocazione_to() {
		return range_docodgsedutadataconvocazione_to;
	}

	public void setRange_docodgsedutadataconvocazione_to(
			String range_docodgsedutadataconvocazione_to) {
		this.range_docodgsedutadataconvocazione_to = range_docodgsedutadataconvocazione_to;
	}

	public String getCustom_annodelverb() {
		return custom_annodelverb;
	}

	public void setCustom_annodelverb(String custom_annodelverb) {
		this.custom_annodelverb = custom_annodelverb;
	}

	public String getCustom_numdelibverb_da() {
		return custom_numdelibverb_da;
	}

	public void setCustom_numdelibverb_da(String custom_numdelibverb_da) {
		this.custom_numdelibverb_da = custom_numdelibverb_da;
	}

	public String getCustom_numdelibverb_a() {
		return custom_numdelibverb_a;
	}

	public void setCustom_numdelibverb_a(String custom_numdelibverb_a) {
		this.custom_numdelibverb_a = custom_numdelibverb_a;
	}

	public String getRelComponenti() {
		return relComponenti;
	}

	public void setRelComponenti(String relComponenti) {
		this.relComponenti = relComponenti;
	}

	public List<Custom_comp> getComponenti() {
		return componenti;
	}

	public void setComponenti(List<Custom_comp> componenti) {
		this.componenti = componenti;
	}

	public boolean isCustom_seduta() {
		return custom_seduta;
	}

	public void setCustom_seduta(boolean custom_seduta) {
		this.custom_seduta = custom_seduta;
	}

	public String getComunicazioniLabel() {
		return comunicazioniLabel;
	}

	public void setComunicazioniLabel(String comunicazioniLabel) {
		this.comunicazioniLabel = comunicazioniLabel;
	}
}
