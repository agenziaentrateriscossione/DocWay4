package it.tredi.dw4.acl.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import it.tredi.dw4.acl.adapters.AclQueryFormsAdapter;
import it.tredi.dw4.acl.model.Sede;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.QueryToAdmTools;
import it.tredi.dw4.docway.model.ExportPersonalizzato;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class AclHome extends AclQuery {
	
	private String xml;
	
	private String searchTerms = "";
	private String query = "";
	private boolean struttura_interna = false;
	private boolean struttura_esterna = false;
	private boolean persona_interna = false;
	private boolean persona_esterna = false;
	private boolean profilo = false;
	private boolean gruppo = false;
	private boolean comune = false;
	//private boolean pec = false;
	private boolean ruolo = false;
	
	private String createDateFrom = "";
	private String createDateTo = "";
	private String updateDateFrom = "";
	private String updateDateTo = "";
	
	// caricamento di rif interni/esterni da DocWay
	private String searchKeyRif = "";
	
	private String valueRif = ""; 
	private String codAmmAooRestriction = "";
	
	//Valore di inizializzazione del campo del model
	private String lookupFieldVal = ""; 
	private String xverbInsPersona = "";
	
	// caricamento di risorse di ACL da URL esterno (prettyFaces)
	private String aclAlias = ""; // alias o chiave di ricerca
	private String aclValue = ""; // nrecord o valore da ricercare
	
	// nome dell'archivio di ACL
	private String aclDb = "";
	
	// indica se l'utente corrente e' amministratore (superUser o amministratore di ACL)
	private boolean adminAcl = false;
	
	//caricamento showdocOrgano
	private String organoPosizione = "";
	private String organoSel = "";
	private String organoDb = "";
	
	//inserimento organo doceditOrgano
	private String table = "";
	
	private List<ExportPersonalizzato> esportazioni;
	private boolean esportazioniPersonalizzate = false;
	
	// mbernardini 20/10/2015 : selezione di aoo in caso di utenti privi di restrizioni
	private String selectedCodSede = null;
	private List<Sede> sedi = new ArrayList<Sede>();
	private boolean aoo_init_popup_active = false;
	
	public String getlookupFieldVal() {
		return lookupFieldVal;
	}

	public void setlookupFieldVal(String fieldValue) {
		this.lookupFieldVal = fieldValue;
	}
	
	public String getXverbInsPersona() {
		return xverbInsPersona;
	}

	public void setXverbInsPersona(String xverbInsPersona) {
		this.xverbInsPersona = xverbInsPersona;
	}

	private AclQueryFormsAdapter formsAdapter;
	
	public AclQueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	public void setFormsAdapter(AclQueryFormsAdapter formsAdapter) {
		this.formsAdapter = formsAdapter;
	}

	public AclHome() throws Exception {
		this.formsAdapter = new AclQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
		init();
	}
	
	public String init() throws Exception{
		clearForm();
		
/*		
		formsAdapter.getDefaultForm().addParam("verbo", "");
		formsAdapter.getDefaultForm().addParam("query", "");
		
		XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean().getLogin());
		
		if (handleErrorResponse(response)) return null;
		
		formsAdapter.fillFormsFromResponse(response);
		this.xml = response.asXML();
		
	*/	
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		if (dom != null) {
			this.xml = dom.asXML();
			this.adminAcl = StringUtil.booleanValue(XMLUtil.parseStrictAttribute(dom, "/response/@adminAcl"));
		
			// mbernardini 20/10/2015 : selezione delle aoo sulle quali operare in caso di utente senza restrizioni alla propria aoo
			this.sedi = XMLUtil.parseSetOfElement(dom, "/response/sedi/sede", new Sede());
			if (this.sedi != null && this.sedi.size() > 0) {
				boolean intrestr = formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_INT_AOO_RESTRICTION, false);
				boolean extrestr = formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_EXT_AOO_RESTRICTION, false);
				String codSede = null;
				try {
					codSede = getCurrentCodSedeRestriction();
				}
				catch (Exception e) {
					Logger.warn("AclHome.init(): unable to load codsede from session... " + e.getMessage());
				}
				if ((!intrestr || !extrestr) && (codSede == null || codSede.length() == 0)) {
					// l'utente corrente non ha restrizioni alla propria AOO e non risulta selezionata una AOO da utilizzare... occorre quindi
					// mostrare il popup di delezione delle AOO
					Node codSedeNode = dom.selectSingleNode("/response/@cod_sede");
					if (codSedeNode != null)
						this.selectedCodSede = codSedeNode.getText();
					this.aoo_init_popup_active = true;
				}
			}
		}
    }
	
	public String queryPlain() throws Exception {
		//DO NOTHING
		return null;
	}
	
	public String search() throws Exception{
		try {
			this.query = ""; // Azzero il valore di query in caso di search (caso di precedente query che aveva restituito 'nessun risultato')
			
			if (!checkFields()) return null;
			
			String codSede = this.formsAdapter.getLastResponse().getAttributeValue("/response/@cod_sede");
			
			String extra = "";
			if (struttura_interna) 	extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"struttura_interna\"", DIRITTO_INT_AOO_RESTRICTION, "[struint_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
			if (struttura_esterna) 	extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"struttura_esterna\"", DIRITTO_EXT_AOO_RESTRICTION, "[struest_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
			if (persona_interna) 	extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"persona_interna\" AND [/persona_interna/@tipo]=\"&null;\"", DIRITTO_INT_AOO_RESTRICTION, "[persint_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
			if (persona_esterna) 	extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"persona_esterna\"", DIRITTO_EXT_AOO_RESTRICTION, "[persest_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
			if (profilo) 			extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"persona_interna\" AND [persint_tipo]=\"profilo\"", DIRITTO_INT_AOO_RESTRICTION, "[persint_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
			if (ruolo) 				extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"ruolo\"", DIRITTO_INT_AOO_RESTRICTION, "[ruoli_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
			if (comune) 			extra = addQueryFieldOR(extra, "[UD,/xw/@UdType]=\"comune\"");
			if (gruppo) 			extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"gruppo\"", DIRITTO_INT_AOO_RESTRICTION, "[gruppi_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
			//if (pec) 				extra = addQueryFieldOR(extra, "[UD,/xw/@UdType]=\"aoo\"");
			
			if (searchTerms.length() > 0) {
				this.query = "([@]="+searchTerms+")";
				
				// Gestione delle Stoplist
				boolean removeStopListWord = true; // TODO Andrebbe letto da un file di properties dell'applicazione
				if (removeStopListWord && (this.query.indexOf("]=") > 0))
					this.query = StringUtil.replace(this.query, "]=", "|SrcStp:null]="); // In questo modo viene imposto di non utilizzare le stoplist
			}
			
			// Filtro su date di creazione e modifica
			String creazRangeQuery = addDateRangeQuery("creaz", createDateFrom, createDateTo);
			if (creazRangeQuery != null && creazRangeQuery.length() > 0)
				this.query = addQueryFieldAND(this.query, creazRangeQuery);
			String modRangeQuery = addDateRangeQuery("mod", updateDateFrom, updateDateTo);
			if (modRangeQuery != null && modRangeQuery.length() > 0)
				this.query = addQueryFieldAND(this.query, modRangeQuery);
			
			// In caso non ci siano i filtri extra occorre forzare il filtro per AOO
			if (extra.length() == 0) {
				// Nel caso di ricerca senza filtri aggiungo tutti i filtri alla query di ricerca (nel caso in cui
				// l'utente abbia indicato almeno un termine di ricerca - ricerca non vuota)
				
				if (this.query.length() == 0) {
					// Ricerca troppo ampia... restituisco un messaggio di avviso
					handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(I18N.mrs("dw4.ambito_di_ricerca_troppo_ampio"), this.query, ErrormsgFormsAdapter.INFO));
					return null;
				}
				 
				// Aggiungo i filtri su tutte le tipologie
				extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"struttura_interna\"", DIRITTO_INT_AOO_RESTRICTION, "[struint_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
				extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"struttura_esterna\"", DIRITTO_EXT_AOO_RESTRICTION, "[struest_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
				extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"persona_interna\" AND [/persona_interna/@tipo]=\"&null;\"", DIRITTO_INT_AOO_RESTRICTION, "[persint_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
				extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"persona_esterna\"", DIRITTO_EXT_AOO_RESTRICTION, "[persest_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
				extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"persona_interna\" AND [persint_tipo]=\"profilo\"", DIRITTO_INT_AOO_RESTRICTION, "[persint_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
				extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"ruolo\"", DIRITTO_INT_AOO_RESTRICTION, "[ruoli_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
				extra = addQueryFieldOR(extra, "[UD,/xw/@UdType]=\"comune\"");
				extra = addQueryFieldOR(extra, addQueryRestriction("[UD,/xw/@UdType]=\"gruppo\"", DIRITTO_INT_AOO_RESTRICTION, "[gruppi_codammaoo]=\"" + CODSEDE_KEY + "\"", codSede));
			}

			// mbernardini 02/05/2017 : ottimizzazione da eseguire solo in caso di integrazione con elasticsearch disattivata (<?THEN?> non implementato su Elasticsearch)
			if (!this.formsAdapter.isElasticsearchEnabled()) {
				// mbernardini 24/03/2017 : ottimizzata la query su ricerca home acl in base alle indicazioni di rtirabassi
				// teoricamente in questo punto sia query che extra dovrebbero risultare valorizzati
				if (!this.query.isEmpty())
					this.query = "(" + this.query + ") <?THEN?>  [?SEL]=<?SEL0?>";
			}
			this.query = addQueryFieldAND(this.query, encapsulateQuery(extra));
			
			// Imposto l'ordinamento dei risultati
			String ord = "xml(xpart:/persona_interna/@cognome)," +
							"xml(xpart:/persona_interna/@nome)," +
							"xml(xpart:/persona_interna/@nome_profilo)," +
							"xml(xpart:/persona_esterna/@cognome)," +
							"xml(xpart:/persona_esterna/@nome)," +
							"xml(xpart:/struttura_esterna/nome)," +
							"xml(xpart:/struttura_interna/nome)," +
							"xml(xpart:/gruppo/nome)," +
							"xml(xpart:/ruolo/nome)," +
							"xml(xpart:/comune/@nome)," +
							"xml(xpart:/comune/@cap)";
			this.formsAdapter.getDefaultForm().addParam("qord", ord); // TODO Andrebbe specificato all'interno di un file di properties
			
			XMLDocumento response = this._queryPlain(this.query, "", "");
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				//clearForm(); // In questo modo il form viene svuotato e non ti porti i dati di ricerca in tutte le pagine di ACL
				return null;
			}	
	
			this.xml = response.asXML();
			clearForm();
	
			Element root = response.getRootElement();
			String verbo = root.attributeValue("verbo", "");
			
			if (verbo.equals("showtitles")){
				AclTitles titles = new AclTitles();
				titles.getFormsAdapter().fillFormsFromResponse(response);
				
				titles.init(response.getDocument());
				
				setSessionAttribute("titles", titles);
				return "acl_showtitles";
			}
			else if(verbo.equals("showdoc")){
				return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
			}
			
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}			
	}
	
	private String addQueryRestriction(String query, String diritto, String queryRestriction, String userCodSede) {
		if (this.formsAdapter.checkBooleanFunzionalitaDisponibile(diritto, false)) {
			query = query.trim() + " AND " + queryRestriction.trim().replaceAll(CODSEDE_KEY, userCodSede);
		}
		else {
			String codSedeRestriction = getCurrentCodSedeRestriction();
			if (codSedeRestriction != null && codSedeRestriction.length() > 0)
				query = query.trim() + " AND " + queryRestriction.trim().replaceAll(CODSEDE_KEY, codSedeRestriction);
		}
		return query;
	}
	
	private String addQueryFieldOR(String query, String field){
		if ("".equals(query)) return field;
		else return query+" OR "+field;
	}
	
	private String addQueryFieldAND(String query, String field){
		if ("".equals(query)) return field;
		else return query+" AND "+field;
	}
	
	private String encapsulateQuery(String query){
		if ("".equals(query)) return query;
		else return "("+query+")";
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String fuffa) {
		this.xml = fuffa;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}
	
	public void setSearchTerms(String terms) {
		this.searchTerms = terms;
	}

	public String getSearchTerms() {
		return searchTerms;
	}

	public void setStruttura_interna(boolean struttura_interna) {
		this.struttura_interna = struttura_interna;
	}

	public boolean isStruttura_interna() {
		return struttura_interna;
	}

	public void setStruttura_esterna(boolean struttura_esterna) {
		this.struttura_esterna = struttura_esterna;
	}

	public boolean isStruttura_esterna() {
		return struttura_esterna;
	}

	public void setPersona_esterna(boolean persona_esterna) {
		this.persona_esterna = persona_esterna;
	}

	public boolean isPersona_esterna() {
		return persona_esterna;
	}

	public void setPersona_interna(boolean persona_interna) {
		this.persona_interna = persona_interna;
	}

	public boolean isPersona_interna() {
		return persona_interna;
	}

	public void setProfilo(boolean profilo) {
		this.profilo = profilo;
	}

	public boolean isProfilo() {
		return profilo;
	}

	public void setGruppo(boolean gruppo) {
		this.gruppo = gruppo;
	}

	public boolean isGruppo() {
		return gruppo;
	}

	public void setComune(boolean comune) {
		this.comune = comune;
	}

	public boolean isComune() {
		return comune;
	}
/*
	public void setPec(boolean pec) {
		this.pec = pec;
	}

	public boolean isPec() {
		return pec;
	}
*/	
	public String clearForm() {
		this.searchTerms = "";
		this.query = "";
		this.struttura_interna = false;
		this.struttura_esterna = false;
		this.persona_interna = false;
		this.persona_esterna = false;
		this.profilo = false;
		this.ruolo = false;
		this.comune = false;
		this.gruppo = false;
		//this.pec = false;
		this.createDateFrom = "";
		this.createDateTo = "";
		this.updateDateFrom = "";
		this.updateDateTo = "";
				
		return null;
	}

	public void setRuolo(boolean ruolo) {
		this.ruolo = ruolo;
	}

	public boolean isRuolo() {
		return ruolo;
	}
	
	
	public String getCreateDateFrom() {
		return createDateFrom;
	}

	public void setCreateDateFrom(String createDateFrom) {
		this.createDateFrom = createDateFrom;
	}

	public String getCreateDateTo() {
		return createDateTo;
	}

	public void setCreateDateTo(String createDateTo) {
		this.createDateTo = createDateTo;
	}

	public String getUpdateDateFrom() {
		return updateDateFrom;
	}

	public void setUpdateDateFrom(String updateDateFrom) {
		this.updateDateFrom = updateDateFrom;
	}

	public String getUpdateDateTo() {
		return updateDateTo;
	}

	public void setUpdateDateTo(String updateDateTo) {
		this.updateDateTo = updateDateTo;
	}
	
	public String getValueRif() {
		return valueRif;
	}

	public void setValueRif(String rif) {
		this.valueRif = rif;
	}
	
	public String getSearchKeyRif() {
		return searchKeyRif;
	}

	public void setSearchKeyRif(String searchKey) {
		this.searchKeyRif = searchKey;
	}
	
	public String getCodAmmAooRestriction() {
		return codAmmAooRestriction;
	}

	public void setCodAmmAooRestriction(String codAmmAooRestriction) {
		this.codAmmAooRestriction = codAmmAooRestriction;
	}
	
	public String getAclDb() {
		return aclDb;
	}

	public void setAclDb(String aclDb) {
		this.aclDb = aclDb;
	}
	
	public boolean isAdminAcl() {
		return adminAcl;
	}

	public void setAdminAcl(boolean adminAcl) {
		this.adminAcl = adminAcl;
	}
	
	public String getAclAlias() {
		return aclAlias;
	}

	public void setAclAlias(String aclAlias) {
		this.aclAlias = aclAlias;
	}

	public String getAclValue() {
		return aclValue;
	}

	public void setAclValue(String aclValue) {
		this.aclValue = aclValue;
	}
	
	/**
	 * Verifico se all'interno dei campi di tipo data sono stati impostati dei valori corretti
	 * @return true se anche un solo campo non è compilato correttamente, false altrimenti
	 */
	public boolean checkFields(){
		boolean result = true;
		
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Andrebbe caricato da file di properties dell'applicazione
		
		if (createDateFrom != null && createDateFrom.length() > 0) {
			if (!DateUtil.isValidDate(createDateFrom, formatoData)) {
				this.setErrorMessage("templateForm:dataCreazFrom", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.create_date_from") + "': " + formatoData.toLowerCase());
				createDateFrom = ""; // Necessario per bug in ricerca libera (alto dx) da qualsiasi altra pagina diversa da home dopo un errore formato su home
				result = false;
			}
		}
		if (createDateTo != null && createDateTo.length() > 0) {
			if (!DateUtil.isValidDate(createDateTo, formatoData)) {
				this.setErrorMessage("templateForm:dataCreazTo", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.create_date_to") + "': " + formatoData.toLowerCase());
				createDateTo = ""; // Necessario per bug in ricerca libera (alto dx) da qualsiasi altra pagina diversa da home dopo un errore formato su home
				result = false;
			}
		}
		if (updateDateFrom != null && updateDateFrom.length() > 0) {
			if (!DateUtil.isValidDate(updateDateFrom, formatoData)) {
				this.setErrorMessage("templateForm:dataModFrom", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.update_date_from") + "': " + formatoData.toLowerCase());
				updateDateFrom = ""; // Necessario per bug in ricerca libera (alto dx) da qualsiasi altra pagina diversa da home dopo un errore formato su home
				result = false;
			}
		}
		if (updateDateTo != null && updateDateTo.length() > 0) {
			if (!DateUtil.isValidDate(updateDateTo, formatoData)) {
				this.setErrorMessage("templateForm:dataModTo", I18N.mrs("acl.inserire_una_data_valida_nel_campo") + " '" + I18N.mrs("acl.update_date_to") + "': " + formatoData.toLowerCase());
				updateDateTo = ""; // Necessario per bug in ricerca libera (alto dx) da qualsiasi altra pagina diversa da home dopo un errore formato su home
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * Caricamento di un rif esterno (chiamata pretty faces)
	 * @return
	 * @throws Exception
	 */
	public String loadRifEst() throws Exception {
		if (valueRif != null && !valueRif.equals("")) {
			if (valueRif.startsWith("PE"))
				searchKeyRif = "PERSEST_MATRICOLA";
			else // if (valueRif.startsWith("SE"))
				searchKeyRif = "STRUEST_CODUFF";
		}
		
		// mbernardini 27/10/2015 : in caso di utente con restrizioni alla propria AOO
		// occorre filtrare i soli risultati associati alla propria AOO
		String xq = null;
		if (codAmmAooRestriction != null && !codAmmAooRestriction.isEmpty()
				// mbernardini 05/11/2015 : in caso di caricamento rif da docway la restrizione va sempre applicata visto che l'utente ha selezionato una specifica struttura/persona
				/*&& formsAdapter.checkBooleanFunzionalitaDisponibile(DIRITTO_EXT_AOO_RESTRICTION, false)*/) {
			String aooalias = getCodAmmAooSearchkey(searchKeyRif);
			if (aooalias != null && !aooalias.isEmpty())
				xq = "[" + aooalias + "]=\"" + codAmmAooRestriction + "\"";
		}
		
		return loadRif(xq);
	}
	
	/**
	 * Caricamento di un rif interno/esterno (chiamata pretty faces)
	 * @return
	 * @throws Exception
	 */
	public String loadRif() throws Exception {
		// mbernardini 05/11/2015 : in caso di caricamento rif da docway la restrizione va sempre applicata visto che l'utente ha selezionato una specifica struttura/persona
		String xq = null;
		if (codAmmAooRestriction != null && !codAmmAooRestriction.isEmpty()) {
			String aooalias = getCodAmmAooSearchkey(searchKeyRif);
			if (aooalias != null && !aooalias.isEmpty())
				xq = "[" + aooalias + "]=\"" + codAmmAooRestriction + "\"";
		}
		return loadRif(xq);
	}
	
	/**
	 * Caricamento di un rif interno/esterno (chiamata pretty faces)
	 * @param extraQuery eventuale extra query da applicare come filtro
	 * @return
	 * @throws Exception
	 */
	public String loadRif(String extraQuery) throws Exception {
		if (searchKeyRif != null && !searchKeyRif.equals("") 
							&& valueRif != null && !valueRif.equals("")) {
			String q = "";
			
			// verifica della presenza di alias multipli separati da virgola (,)
			String[] searchKeys = searchKeyRif.split(",");
			if (searchKeys != null && searchKeys.length > 0) {
				for (int i=0; i<searchKeys.length; i++) {
					if (searchKeys[i] != null && !searchKeys[i].equals("")) {
						if (!q.equals("")) // la ricerca su alias multipli viene fatta in OR
							q = q + " OR ";
						q = q + "([" + searchKeys[i] + "]=\"" + valueRif + "\")";
					}
				}
			}
			
			if (aclDb != null && aclDb.trim().length() > 0) {
				this.formsAdapter.getDefaultForm().addParam("db", aclDb);
				this.formsAdapter.getIndexForm().addParam("db", aclDb);
			}
			
			if (!q.isEmpty() && extraQuery != null && !extraQuery.isEmpty())
				q += " AND " + extraQuery;
			Logger.info("AclHome.loadRif(): query = " + q);
			
			return this.queryPlain(q);
		}
		else {
			// TODO gestione del messaggio di errore
			return null;
		}
	}
	
	
	/**
	 * Caricamento di un record di ACL da URL (prettyFaces)
	 * @return
	 * @throws Exception
	 */
	public String load() throws Exception {
		try {
			if (aclAlias != null && !aclAlias.equals("")
					&& aclValue != null && !aclValue.equals("")) {
				String q = "";

				// verifica della presenza di alias multipli separati da virgola (,)
				String[] aliases = aclAlias.split(",");
				if (aliases != null && aliases.length > 0) {
					for (int i=0; i<aliases.length; i++) {
						if (aliases[i] != null && !aliases[i].equals("")) {
							if (!q.equals("")) // la ricerca su alias multipli viene fatta in OR
								q = q + " OR ";
							q = q + "([" + aliases[i] + "]=\"" + aclValue + "\")";
						}
					}
				}

				// nel caso in cui sia stato specificato un database tramite
				// prettyfaces si aggiorna il formsAdapter
				if (aclDb != null && !aclDb.equals(""))
					formsAdapter.getDefaultForm().addParam("db", aclDb);
				
				formsAdapter.getDefaultForm().addParam("auditVisualizzazione", "true");

				return this.queryPlain(q);
			}
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	/**
	 * Caricamento della maschera di inserimento di una persona esterna (chiamata pretty faces)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insPersonaEsternaPopup() throws Exception {
		String lookupNew = "/base/acl/engine/acl.jsp?db=acl;dbTable=persona_esterna;fillField=persona_esterna.@cognome;rightCode=ACL-8";
		if (aclDb != null && aclDb.trim().length() > 0) {
			this.formsAdapter.getDefaultForm().addParam("db", aclDb);
			this.formsAdapter.getIndexForm().addParam("db", aclDb);
			lookupNew = "/base/acl/engine/acl.jsp?db=" + aclDb + ";dbTable=persona_esterna;fillField=persona_esterna.@cognome;rightCode=ACL-8";
		}
		formsAdapter.insTableDocLookup(lookupNew, lookupFieldVal);
		if (xverbInsPersona != null && xverbInsPersona.trim().length() > 0) // possibile lookup in inserimento firmatario (contiene la struttura di appartenenza)
			formsAdapter.getDefaultForm().addParam("xverb", xverbInsPersona);
		
		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("persona_esterna", responseDoc, true);
	}
	
	/**
	 * Caricamento della maschera di inserimento di una struttura esterna (chiamata pretty faces)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insStrutturaEsternaPopup() throws Exception {
		String lookupNew = "/base/acl/engine/acl.jsp?db=acl;dbTable=struttura_esterna;fillField=struttura_esterna.nome;rightCode=ACL-6";
		if (aclDb != null && aclDb.trim().length() > 0) {
			this.formsAdapter.getDefaultForm().addParam("db", aclDb);
			this.formsAdapter.getIndexForm().addParam("db", aclDb);
			lookupNew = "/base/acl/engine/acl.jsp?db=" + aclDb + ";dbTable=struttura_esterna;fillField=struttura_esterna.nome;rightCode=ACL-6";
		}
		formsAdapter.insTableDocLookup(lookupNew, lookupFieldVal);
		
		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("struttura_esterna", responseDoc, true);
	}
	
	/**
	 * Caricamento della maschera di inserimento di un comune (chiamata pretty faces)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insComunePopup() throws Exception {
		String lookupNew = "/base/acl/engine/acl.jsp?db=acl;dbTable=comune;fillField=comune.@nome;rightCode=ACL-4";
		if (aclDb != null && aclDb.trim().length() > 0) {
			this.formsAdapter.getDefaultForm().addParam("db", aclDb);
			this.formsAdapter.getIndexForm().addParam("db", aclDb);
			lookupNew = "/base/acl/engine/acl.jsp?db=" + aclDb + ";dbTable=comune;fillField=comune.@nome;rightCode=ACL-4";
		}
		formsAdapter.insTableDocLookup(lookupNew, lookupFieldVal);
		
		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificDocEditPageAndReturnNavigationRule("comune", responseDoc, true);
	}

	/**
	 * Caricamento del form di cambio password per un utente di ACL.
	 * N.B.: cambio password di TOMCAT (solo se MD5)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String changeUserPwd() throws Exception {
		try {
			if (aclDb != null && aclDb.trim().length() > 0) {
				this.formsAdapter.getDefaultForm().addParam("db", aclDb);
				this.formsAdapter.getIndexForm().addParam("db", aclDb);
			}
			formsAdapter.goToChangePwd(getUserBean().getLogin());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			//formsAdapter.fillFormsFromResponse(response);
			
			ChangePassword changePassword = new ChangePassword();
			changePassword.getFormsAdapter().fillFormsFromResponse(response);
			changePassword.init(response.getDocument());
			changePassword.setPopupPage(true); // caricamento in template POPUP
			setSessionAttribute("changePassword", changePassword);
			
			return "changePassword";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	//caricamento showdocOrgano
	public String mostraDocumentoQ() throws Exception{
		try{
			QueryToAdmTools queryToAdmTools = (QueryToAdmTools) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("queryToAdmTools");
			
			//sessione scaduta...evito il messaggio d'errore e soprattutto non faccio redirect sulla home di ACL
			if(queryToAdmTools == null)
				FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() +  "/docway/show-queryToAdmTools.pf");
			
			String customTuple = queryToAdmTools.getFormsAdapter().getCustomTupleName();
			String hxpFormDbValue = queryToAdmTools.getFormsAdapter().getDb();
			formsAdapter.mostraDocumentoQ(organoPosizione, organoSel, organoDb, customTuple, hxpFormDbValue);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());	
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			
			return buildSpecificShowdocPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	} 
	
	//inserimento nuovo organo
	public String inserisciDocInACL() throws Exception{
		try{
			QueryToAdmTools queryToAdmTools = (QueryToAdmTools) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("queryToAdmTools");
			String customTuple = queryToAdmTools.getFormsAdapter().getCustomTupleName();
			String hxpFormDbValue = queryToAdmTools.getFormsAdapter().getDefaultForm().getParam("db");
			String hxpFormAclDbValue = queryToAdmTools.getFormsAdapter().getDefaultForm().getParam("aclDb");
			formsAdapter.inserisciDocInACL(getTable(), customTuple, hxpFormDbValue, hxpFormAclDbValue);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());	
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse());
			return buildSpecificDocEditPageAndReturnNavigationRule(response.getAttributeValue("/response/@dbTable"), response, isPopupPage());
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	public List<ExportPersonalizzato> getEsportazioni() {
		return esportazioni;
	}

	public void setEsportazioni(List<ExportPersonalizzato> esportazioni) {
		this.esportazioni = esportazioni;
	}

	public boolean isEsportazioniPersonalizzate() {
		return esportazioniPersonalizzate;
	}

	public void setEsportazioniPersonalizzate(boolean esportazioniPersonalizzate) {
		this.esportazioniPersonalizzate = esportazioniPersonalizzate;
	}
	
	public List<Sede> getSedi() {
		return sedi;
	}

	public void setSedi(List<Sede> sedi) {
		this.sedi = sedi;
	}
	
	public String getSelectedCodSede() {
		return selectedCodSede;
	}

	public void setSelectedCodSede(String codSede) {
		this.selectedCodSede = codSede;
	}
	
	public boolean isAoo_init_popup_active() {
		return aoo_init_popup_active;
	}

	public void setAoo_init_popup_active(boolean aoo_init_popup_active) {
		this.aoo_init_popup_active = aoo_init_popup_active;
	}
	
	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getOrganoPosizione() {
		return organoPosizione;
	}

	public void setOrganoPosizione(String organoPosizione) {
		this.organoPosizione = organoPosizione;
	}

	public String getOrganoSel() {
		return organoSel;
	}

	public void setOrganoSel(String organoSel) {
		this.organoSel = organoSel;
	}

	public String getOrganoDb() {
		return organoDb;
	}

	public void setOrganoDb(String organoDb) {
		this.organoDb = organoDb;
	}
	
	/**
	 * Selezione di un code sede in caso di utente privo di restrizioni
	 */
	public void codSedeChangeListener(ValueChangeEvent e) {
		if (e.getNewValue() != null)
			setCurrentCodSedeRestriction((String) e.getNewValue()); // salvataggio in sessione della sede selezionata
	}
	
	/**
	 * chiusura del popup di selezione AOO in caso di login ad ACL
	 * @return
	 * @throws Exception
	 */
	public String closeAooInitSelection() throws Exception {
		setAoo_init_popup_active(false);
		return null;
	}
	
	/**
	 * Selezione di un code sede dal popup caricato post login in ACL
	 * @return
	 * @throws Exception
	 */
	public String selectInitAoo() throws Exception {
		if (this.selectedCodSede != null)
			setCurrentCodSedeRestriction(this.selectedCodSede); // salvataggio in sessione della sede selezionata dal popup post login ACL
		return closeAooInitSelection();
	}
	
	/**
     * In base al keyName passato cerca di "dedurre" la chiave di ricerca per il filtro su cod_amm_aoo
     */
    private String getCodAmmAooSearchkey(String keyName) {
    	String searchKey = null;
    	
    	if (keyName != null) {
    		keyName = keyName.toLowerCase();
    		if ((keyName.startsWith("struint") && !keyName.contains(",")) || keyName.contains("/struttura_interna"))
    			searchKey = "/struttura_interna/#cod_ammaoo/";
    		else if ((keyName.startsWith("persint") && !keyName.contains(",")) || (keyName.startsWith("profilo")  && !keyName.contains(",")) || keyName.contains("/persona_interna"))
    			searchKey = "/persona_interna/#cod_ammaoo/";
    		else if ((keyName.startsWith("gruppi") && !keyName.contains(",")) || keyName.contains("/gruppo"))
    			searchKey = "/gruppo/#cod_ammaoo/";
    		else if ((keyName.startsWith("ruoli") && !keyName.contains(",")) || keyName.contains("/ruolo"))
    			searchKey = "/ruolo/#cod_ammaoo/";
    		else if ((keyName.startsWith("struest") && !keyName.contains(",")) || keyName.contains("/struttura_esterna"))
    			searchKey = "/struttura_esterna/#cod_ammaoo/";
    		else if ((keyName.startsWith("persest") && !keyName.contains(",")) || keyName.contains("/persona_esterna"))
    			searchKey = "/persona_esterna/#cod_ammaoo/";
    		else
    			Logger.warn("AclHome.getCodAmmAooSearchKey(): impossible to obtain cod_amm_aoo search key, keyName = " + keyName);
    	}
    	
    	return searchKey;
    }
	
}
