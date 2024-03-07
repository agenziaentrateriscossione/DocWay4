package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.StringUtil;

import org.dom4j.Document;

public class QueryRaccoglitore extends DocWayQuery {
	//campi di ricerca
	private String rac_oggetto;
	private String rac_note;
	private String rac_anno;
	private boolean pubblici = true;
	private boolean privati = true;
	private boolean raccoglitoriRPA;
	private boolean aperti;
	private boolean chiusi;
	private String xml;
	private boolean rpatiporuolo = false;
	private String rpanomeuff;
	private String rpanomepersona;
	private boolean cctiporuolo = false;
	private String ccnomeuff;
	private String ccnomepersona;
	private String ordinamento = "xml(xpart:/raccoglitore/oggetto)";
	
	private String rangenum_nrecord_from = "";
	private String rangenum_nrecord_to = "";
	
	private DocDocWayQueryFormsAdapter formsAdapter;
	private String focusElement;
	
	public QueryRaccoglitore() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	public void init(Document dom) {
		this.xml = dom.asXML();
		
		// inizializzazione dei campi custom di ricerca
		getCustomQueryFields().init(dom);
		setCurrentCustomFieldSection("raccoglitore");
    }	
	
	public DocDocWayQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	@Override
	public String queryPlain() throws Exception {
		try {
			String query = createQuery();
			formsAdapter.findplain();
			return queryPlain(query);
			
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Creazione della query di ricerca raccoglitori su eXtraWay in base ai 
	 * parametri specificati dall'operatore
	 * 
	 * @return
	 * @throws Exception
	 */
	public String createQuery() throws Exception{
		String query =  "";
		query +=  addQueryField("rac_oggetto", this.escapeQueryValue(rac_oggetto));
		query +=  addQueryField("rac_note", this.escapeQueryValue(rac_note));
		query +=  addQueryField("rac_anno", this.escapeQueryValue(rac_anno));
		
		if( (rangenum_nrecord_from!=  null && rangenum_nrecord_from.trim().length()>0) || (rangenum_nrecord_to != null && rangenum_nrecord_to.trim().length()>0)){
        	String complete_nrecord_from = "";
        	if (rangenum_nrecord_from != null && rangenum_nrecord_from.length() > 0)
        		complete_nrecord_from = StringUtil.fillString(rangenum_nrecord_from, "0", 8);
        	String complete_nrecord_to = "";
        	if (rangenum_nrecord_to != null && rangenum_nrecord_to.length() > 0)
        		complete_nrecord_to = StringUtil.fillString(rangenum_nrecord_to, "0", 8);
        	String query1= addRangeQuery("rac_nrecord", complete_nrecord_from, complete_nrecord_to, "AND");
    		query +=  query1;
        }
		
		if (null != rpanomeuff && rpanomeuff.trim().length()>0) query += "(([rac_rifinternirifnomeuff]=" + this.escapeQueryValue(rpanomeuff) + ") adj ([rac_rifinternirifdiritto]=RPA)) AND ";
		if (null != rpanomepersona && rpanomepersona.trim().length()>0) query += "(([rac_rifinternirifnomepersona]=" + this.escapeQueryValue(rpanomepersona) + ") adj ([rac_rifinternirifdiritto]=RPA)) AND ";
		if (null != ccnomeuff && ccnomeuff.trim().length()>0) query += "(([rac_rifinternirifnomeuff]=" + this.escapeQueryValue(ccnomeuff) + ") adj ([rac_rifinternirifdiritto]=CC)) AND ";
		if (null != ccnomepersona && ccnomepersona.trim().length()>0) query += "(([rac_rifinternirifnomepersona]=" + this.escapeQueryValue(ccnomepersona) + ") adj ([rac_rifinternirifdiritto]=CC)) AND ";
		
		if (raccoglitoriRPA) {
			String query1 = "([rac_rifinternirifcodpersona]=\"_CODPERSONA_\") adj ([rac_rifinternirifdiritto]=RPA)";
            if ( query.length() == 0 ) 	query = query1 + " AND ";
            else 						query = query +"(" + query1 + ") AND ";
		}
		
		if ( aperti && !chiusi )	query += "([rac_stato]=\"aperto\") AND ";
		if ( chiusi && !aperti )  	query += "([rac_stato]=\"chiuso\") AND ";
		if (pubblici && ! privati)  query += "([rac_pubblico]=si) AND ";
		if (!pubblici && privati)  	query += "([rac_pubblico]=no) AND ";
		if (query.endsWith(" AND "))
			query = query.substring(0, query.length()-4);
		else if("".equals(query.trim())) query = "[UD,/xw/@UdType]=raccoglitore";
		
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
			XMLDocumento response = super._queryPlain(query, "", "@raccoglitore");
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
	
	public String openIndexOggettoRaccoglitore() throws Exception {
		this.focusElement = "rac_oggetto";
		this.openIndex("rac_oggetto", this.rac_oggetto, "0", " ", false);
		return null;
	}
	
	public String openIndexNoteRaccoglitore() throws Exception {
		this.focusElement = "rac_note";
		this.openIndex("rac_note", this.rac_note, "0", "", false);
		return null;
	}
	
	public String openIndexAnnoRaccoglitore() throws Exception {
		this.focusElement = "rac_anno";
		this.openIndex("rac_anno", this.rac_anno, "0", "", false);
		return null;
	}
	
	public String openIndexUORRaccoglitore() throws Exception {
		this.focusElement = "rpanomeuff";
		this.openIndex("rpanomeuff", "rac_rifinternirifdirittonomeuff", this.rpanomeuff, "0", "RPA|^| ", false);
		return null;
	}
	
	public String openIndexRPARaccoglitore() throws Exception {
		this.focusElement = "rpanomepersona";
		this.openIndex("rpanomepersona", "rac_rifinternirifdirittonomepersona", this.rpanomepersona, "0", "RPA|^| ", false);
		return null;
	}
	
	public String switchRpaRuoloUfficio() throws Exception {
		this.rpatiporuolo = !this.rpatiporuolo;
		this.rpanomeuff = "";
		this.rpanomepersona = "";
		return null;
	}
	
	public String openIndexCCUffRaccoglitore() throws Exception {
		this.focusElement = "ccnomeuff";
		this.openIndex("ccnomeuff", "rac_rifinternirifdirittonomeuff", this.ccnomeuff, "0", "CC|^| ", false);
		return null;
	}
	
	public String openIndexCCPersonaRaccoglitore() throws Exception {
		this.focusElement = "ccnomepersona";
		this.openIndex("ccnomepersona", "rac_rifinternirifdirittonomepersona", this.ccnomepersona, "0", "CC|^| ", false);
		return null;
	}
	
	public String switchCcRuoloUfficio() throws Exception {
		this.cctiporuolo = !this.cctiporuolo;
		this.ccnomeuff = "";
		this.ccnomepersona = "";
		return null;
	}
	
	public void setRac_note(String fasc_note) {
		this.rac_note = fasc_note;
	}
	
	public String getRac_note() {
		return rac_note;
	}
	
	public void setPubblici(boolean attivi) {
		this.pubblici = attivi;
	}
	
	public boolean isPubblici() {
		return pubblici;
	}
	
	public void setRaccoglitoriRPA(boolean rpa) {
		this.raccoglitoriRPA = rpa;
	}
	
	public boolean isRaccoglitoriRPA() {
		return raccoglitoriRPA;
	}
	
	public void setAperti(boolean sottofascicoli) {
		this.aperti = sottofascicoli;
	}
	
	public boolean isAperti() {
		return aperti;
	}
	
	public void setPrivati(boolean cc) {
		this.privati = cc;
	}
	
	public boolean isPrivati() {
		return privati;
	}
	
	public void setChiusi(boolean incaricato) {
		this.chiusi = incaricato;
	}
	
	public boolean isChiusi() {
		return chiusi;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}
	
	public String getXml() {
		return xml;
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
			return "showtitles@raccoglitore";
		}
	}
	public void setRac_anno(String fasc_anno) {
		this.rac_anno = fasc_anno;
	}
	public String getRac_anno() {
		return rac_anno;
	}

	public String fillMultiString(String stringa, String carattere, int lunghezza, String separator) {
	    String ret = "";
	    String[] vect = stringa.split(separator);
	    for ( int i = 0; i < vect.length; i++ )
	        ret += separator + fillString(vect[i], carattere, lunghezza);
	    return ret;
	}
	
	public String fillString(String stringa, String carattere, int lunghezza) {
	    int start = stringa.length();
	    for ( int i = start; i < lunghezza; i++ )
	        stringa = carattere + stringa;
	    return stringa;
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
	
	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}
	
	public String getOrdinamento() {
		return ordinamento;
	}
	
	public boolean isRpatiporuolo() {
		return rpatiporuolo;
	}
	
	public void setRpatiporuolo(boolean rpatiporuolo) {
		this.rpatiporuolo = rpatiporuolo;
	}
	
	public boolean isCctiporuolo() {
		return cctiporuolo;
	}
	
	public void setCctiporuolo(boolean cctiporuolo) {
		this.cctiporuolo = cctiporuolo;
	}
	
	public String getCcnomeuff() {
		return ccnomeuff;
	}
	
	public void setCcnomeuff(String ccnomeuff) {
		this.ccnomeuff = ccnomeuff;
	}
	
	public String getCcnomepersona() {
		return ccnomepersona;
	}
	
	public void setCcnomepersona(String ccnomepersona) {
		this.ccnomepersona = ccnomepersona;
	}
	
	public String getRangenum_nrecord_from() {
		return rangenum_nrecord_from;
	}
	
	public void setRangenum_nrecord_from(String rangenum_nrecord_from) {
		this.rangenum_nrecord_from = rangenum_nrecord_from;
	}
	
	public String getRangenum_nrecord_to() {
		return rangenum_nrecord_to;
	}
	
	public void setRangenum_nrecord_to(String rangenum_nrecord_to) {
		this.rangenum_nrecord_to = rangenum_nrecord_to;
	}
	
	public String getRac_oggetto() {
		return rac_oggetto;
	}
	
	public void setRac_oggetto(String fasc_oggetto) {
		this.rac_oggetto = fasc_oggetto;
	}
	
	public String resetQuery(){
		rac_oggetto="";
		rac_note="";
		rac_anno="";
		rpatiporuolo = false;
		rpanomeuff="";
		rpanomepersona="";
		cctiporuolo = false;
		ccnomeuff="";
		ccnomepersona="";
		
		rangenum_nrecord_from = "";
		rangenum_nrecord_to = "";
		
		// pulizia di evenutali campi custom presenti nel form
		getCustomQueryFields().cleanCustomFields();
		
		return null;
	}
	
	/**
	 * Inserimento di un nuovo raccoglitore
	 * @return
	 * @throws Exception
	 */
	public String insTableDocRaccoglitore() throws Exception {
		formsAdapter.insTableDoc(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE); 

		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_RACCOGLITORE, responseDoc, this.isPopupPage());
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

}
