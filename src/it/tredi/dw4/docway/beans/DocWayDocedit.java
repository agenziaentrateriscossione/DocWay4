package it.tredi.dw4.docway.beans;

import java.util.Arrays;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

import it.tredi.dw4.acl.beans.UserBean;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.CustomFieldsLookup;
import it.tredi.dw4.beans.Page;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.model.customfields.Field;
import it.tredi.dw4.model.customfields.FieldInstance;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;

public abstract class DocWayDocedit extends Page {
	
	// Utilizzato per caricare nel template di inserimento la corretta
	// modalita' di compilazione della classificazione
	private boolean classificazioneDaTitolario = true;
	
	// Visualizza o meno la sezione relativa al numero di raccomandata nella
	// scheda di DocEdit
	private boolean showRaccomandataFields = false;
	
	private boolean alertForTutti = true; // Alert in caso di selezione del check 'tutti' in responsabili CC
	
	private boolean resetJobsIWX = true; // indica se eseguire il reset dei jobs di IWX al caricamento della pagina
	
	private String appStringPreferences = ""; // Alcune preferenze dell'applicazione codificate in formato stringa
	
	public abstract void init(Document dom);
	
	public abstract DocEditFormsAdapter getFormsAdapter();
	
	public abstract String saveDocument() throws Exception;
	
	public abstract String clearDocument() throws Exception;
	
	public abstract XmlEntity getModel();
	
	protected XMLDocumento _saveDocument(String pne, String pnce) throws Exception {
		getFormsAdapter().getDefaultForm().addParams(getCustomfields().asFormAdapterParams("")); // salvataggio dei campi aggiuntivi (aggiunta a formAdapter)
		getFormsAdapter().saveDocument(pne, pnce);
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	protected XMLDocumento _clearDocument() throws Exception {
		classificazioneDaTitolario = true;
		showRaccomandataFields = false;
		
		getFormsAdapter().clearDocument();
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	/**
	 * Chiamata a Lookup classico. I campi che vengono riempiti in caso di selezione di un risultato di
	 * lookup sono gli stessi che vengono azzerati in caso di chiusura senza selezione
	 * 
	 * @return
	 * @throws Exception
	 */
	protected XMLDocumento _lookup(String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) throws Exception {
		return _lookup(aliasName, aliasName1, titolo, ord, campi, campi, xq, db, newRecord, value);
	}
	
	/**
	 * Chiamata a Lookup con distinzione fra i campi da riempire (in caso di selezione
	 * di un risultato di lookup) e i campi da ripulire (in caso di chiusura senza selezione)
	 * 
	 * @return
	 * @throws Exception
	 */
	protected XMLDocumento _lookup(String aliasName, String aliasName1, String titolo, String ord, String campiLookup, String campiClear, String xq, String db, String newRecord, String value) throws Exception {
		getLookup().cleanFields(campiClear);
		getFormsAdapter().lookup(aliasName, aliasName1, titolo, ord, campiLookup, xq, db, newRecord, value);
		
		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
		return response;
	}
	
	/**
	 * Chiamata a lookup su rif interni
	 * 
	 * @return
	 * @throws Exception
	 */
	protected XMLDocumento _rifintLookup(String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) throws Exception {
		getRifintLookup().cleanFields(campi);
		getFormsAdapter().rifintLookup(aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		
		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
		return response;
	}
	
	/**
	 * RifintLookup su ufficio
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String rifintLookupUfficio(XmlEntity entity, String valueUfficio, String valuePersona, String campi, Object todoObject, String todoMethod) throws Exception {
		// in caso di lookup su rif interno (ufficio) occorre impostare a false il reset dei jobs di iwx
		// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
		// caricate si perderebbero le anteprime
		setResetJobsIWX(false);
				
		// TODO Gestione dei parametri skipAOORestriction e lookupRifInt_getUORData4Titles
		
		String aliasName 	= "struint_nome,gruppi_nome";
		String aliasName1 	= "";
		String titolo 		= "xml,/struttura_interna/nome \"^\" xml,/gruppo/nome"; //titolo
		String ord 			= "xml(xpart:/struttura_interna/nome),xml(xpart:/gruppo/nome)(join)"; //ord 
		String db 			= ""; //db 
		String newRecord 	= ""; //newRecord 
		String xq			= ""; //extraQuery
		
		callRifintLookup(entity, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, valueUfficio + "|" + valuePersona, todoObject, todoMethod);
				
		return null;
	}
	
	protected String rifintLookupUfficio(XmlEntity entity, String valueUfficio, String valuePersona, String campi) throws Exception {
		return rifintLookupUfficio(entity, valueUfficio, valuePersona, campi, null, null);
	}
	
	/**
	 * RifintLookup su persona
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String rifintLookupPersona(XmlEntity entity, String valueUfficio, String valuePersona, String campi) throws Exception {
		return rifintLookupPersona(entity, valueUfficio, valuePersona, campi, "");
	}
	
	/**
	 * RifintLookup su persona
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String rifintLookupPersona(XmlEntity entity, String valueUfficio, String valuePersona, String campi, String xq, Object todoObject, String todoMethod) throws Exception {
		// in caso di lookup su rif interno (persona) occorre impostare a false il reset dei jobs di iwx
		// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
		// caricate si perderebbero le anteprime
		setResetJobsIWX(false);
		
		String aliasName 	= "persint_nomcogn";
		String aliasName1 	= "";
		String titolo 		= "xml,/persona_interna/@cognome xml,/persona_interna/@nome"; //titolo
		String ord 			= "xml(xpart:/persona_interna/@cognome)"; //ord 
		String db 			= ""; //db 
		String newRecord 	= ""; //newRecord 
		
		if (xq == null)
			xq = "";
		
		callRifintLookup(entity, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, valueUfficio + "|" + valuePersona, todoObject, todoMethod);
				
		return null;
	}
	
	protected String rifintLookupPersona(XmlEntity entity, String valueUfficio, String valuePersona, String campi, String xq) throws Exception {
		return rifintLookupPersona(entity, valueUfficio, valuePersona, campi, xq, null, "");
	}
	
	/**
	 * Chiamata a Lookup su Rif Interni su DocWayDoc

	 * @throws Exception
	 */
	protected void callRifintLookup(XmlEntity entity, String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value, Object todoObject, String todoMethod) throws Exception {
		try {
			DocWayRifintLookup docwayRifintLookup = new DocWayRifintLookup();
			setRifintLookup(docwayRifintLookup);
			docwayRifintLookup.setModel(entity);
			docwayRifintLookup.setLookupSuRepertorioByExtraQuery(xq);
			
			// tiommi 17/01/2018 aggiunta logica per trigger sulla modifica del campo cod_uff dell'RPA 
			// per logica di override della visibilità
			if (todoObject != null && todoMethod != null) {
				docwayRifintLookup.setTodoOnCompleteLookupObject(todoObject);
				docwayRifintLookup.setTodoOnCompleteLookupMethod(todoMethod);
			}
	
			XMLDocumento response = this._rifintLookup(aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
			if (handleErrorResponse(response, Const.MSG_LEVEL_ERROR)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return;
			}
			docwayRifintLookup.getFormsAdapter().fillFormsFromResponse(response);
			docwayRifintLookup.init(response.getDocument());
			
			if(docwayRifintLookup.getTitoli().size() == 1 && value != null && value.length() > 0){
				docwayRifintLookup.confirm(docwayRifintLookup.getTitoli().get(0));
			}
			else {
				docwayRifintLookup.setActive(true);
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;			
		}
	}
	
	protected void callRifintLookup(XmlEntity entity, String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) throws Exception {
		callRifintLookup(entity, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value, null, null);
	}
	
	/**
	 * Pulizia di campi di RifintLookup
	 * 
	 * @param campi stringa contenente i campi da ripulire
	 * @param entity entita' sulla quale si sta operando
	 * @return
	 * @throws Exception
	 */
	public String clearFieldRifint(String campi, XmlEntity entity) throws Exception{
		DocWayRifintLookup docwayRifintLookup = new DocWayRifintLookup();
		docwayRifintLookup.setModel(entity);
		docwayRifintLookup.cleanFields(campi);
		return null;
	}
	
	/**
	 * Lookup su ruolo
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String lookupRuolo(XmlEntity entity, String value, String campi, String codammaoo) throws Exception {
		// in caso di lookup su rif interno (ruolo) occorre impostare a false il reset dei jobs di iwx
		// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
		// caricate si perderebbero le anteprime
		setResetJobsIWX(false);
		
		String aliasName 	= "ruoli_nome";
		String aliasName1 	= "";
		String titolo 		= "xml,/ruolo/nome \"^ (~\" xml,/ruolo/societa \"~^)\""; //titolo 
		String ord 			= "xml(xpart:/ruolo/nome)"; //ord 
		String db 			= getFormsAdapter().getDefaultForm().getParam("aclDb"); //db
		String newRecord 	= ""; //newRecord 
		String xq			= ""; //extraQuery
		if (codammaoo != null && !codammaoo.isEmpty())
			xq				= "[ruoli_codammaoo]=" + codammaoo;
		
		callLookup(entity, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		
		return null;
	}

	/**
	 * Chiamata a Lookup su DocWayDoc con separazione fra i campi da riempire dopo la selezione
	 * di un risultato di lookup e i campi da azzerare in caso di mancata selezione

	 * @throws Exception
	 */
	protected void callLookupWithClearFields(XmlEntity entity, String aliasName, String aliasName1, String titolo, String ord, String campiLookup, String campiClear, String xq, String db, String newRecord, String value) throws Exception {
		callLookup(entity, aliasName, aliasName1, titolo, ord, campiLookup, campiClear, xq, db, newRecord, value, "", "");
	}
	
	/**
	 * Chiamata a Lookup su DocWayDoc

	 * @throws Exception
	 */
	protected void callLookup(XmlEntity entity, String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) throws Exception {
		callLookup(entity, aliasName, aliasName1, titolo, ord, campi, campi, xq, db, newRecord, value, "", "");
	}
	
	/**
	 * Chiamata a Lookup su DocWayDoc
	 * @throws Exception
	 */
	protected void callLookup(XmlEntity entity, String aliasName, String aliasName1, String titolo, String ord, String campiLookup, String campiClear, String xq, String db, String newRecord, String value, String tipoDoc, String xverbDoc) throws Exception {
		try {
			// in caso di lookup occorre impostare a false il reset dei jobs di iwx
			// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
			// caricate si perderebbero le anteprime
			setResetJobsIWX(false);
			
			DocWayLookup docwayLookup = new DocWayLookup();
			setLookup(docwayLookup);
			docwayLookup.setModel(entity);
			
			// Tipologia del documento corrente e operazione corrente (inserimento, 
			// modifica) per la gestione di lookup particolari (es. voci d'indice)
			if (tipoDoc != null && tipoDoc.length() > 0)
				docwayLookup.setTipoDoc(tipoDoc);
			if (xverbDoc != null && xverbDoc.length() > 0)
				docwayLookup.setXverbDoc(xverbDoc);
	
			XMLDocumento response = this._lookup(aliasName, aliasName1, titolo, ord, campiLookup, campiClear, xq, db, newRecord, value);
			if (handleErrorResponse(response, Const.MSG_LEVEL_ERROR)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return;
			}
			docwayLookup.getFormsAdapter().fillFormsFromResponse(response);
			docwayLookup.init(response.getDocument());
			docwayLookup.setLookupFieldVal(value);
			
			// mbernardini 24/03/2015 : in caso di lookup su voce di indice occorre in ogni caso aprire il popup di 
			// selezione (come su docway3) in modo da consentire l'associazione di eventuali assegnatari (RPA, CDS, CC)
			if (!docwayLookup.getLookupType().equals("voceIndice") && docwayLookup.getTitoli().size() == 1 && value != null && value.length() > 0) {
				docwayLookup.confirm(docwayLookup.getTitoli().get(0));
			}
			else {
				docwayLookup.setActive(true);
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;			
		}
	}
	
	/**
	 * Pulizia di campi di Lookup
	 * 
	 * @param campi stringa contenente i campi da ripulire
	 * @param entity entita' sulla quale si sta operando
	 * @return
	 * @throws Exception
	 */
	public String clearField(String campi, XmlEntity entity) throws Exception{
		// in caso di clear lookup occorre impostare a false il reset dei jobs di iwx
		// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
		// caricate si perderebbero le anteprime
		setResetJobsIWX(false);
		
		DocWayLookup docwayLookup = new DocWayLookup();
		docwayLookup.setModel(entity);
		docwayLookup.cleanFields(campi);
		return null;
	}
	
	/**
	 * Chiamata a Reset di Lookup su campi custom di DocWay
	 * @param instancesId identificativi delle istanze alle quali fa riferimento il campo di lookup
	 * 
	 * @throws Exception
	 */
	public String clearLookupCustomField(String instancesId) throws Exception {
		try {
			// in caso di clear lookup occorre impostare a false il reset dei jobs di iwx
			// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
			// caricate si perderebbero le anteprime
			setResetJobsIWX(false);
			
			Field field = (Field) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("field");
			if (field != null) {
				
				String campi = "";
				if (field.getLookupParams().containsKey("campi"))
					campi = getLookupFieldsWithInstances(field.getLookupParams().get("campi"), instancesId);
				
				CustomFieldsLookup customFieldsLookup = new CustomFieldsLookup();
				setCustomfieldsLookup(customFieldsLookup);
				customFieldsLookup.setField(field);
				customFieldsLookup.setModel(this.getModel());
				customFieldsLookup.setCustomFieldsModel(getCustomfields());
				
				customFieldsLookup.cleanFields(campi);
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form			
		}
		
		return null;
	}
	
	/**
	 * Chiamata a Lookup su campi custom di DocWay
	 * @param instancesId identificativi delle istanze alle quali fa riferimento il campo di lookup
	 * 
	 * @throws Exception
	 */
	public String lookupCustomField(String instancesId) throws Exception {
		try {
			// in caso di lookup occorre impostare a false il reset dei jobs di iwx
			// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
			// caricate si perderebbero le anteprime
			setResetJobsIWX(false);
			
			Field field = (Field) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("field");
			FieldInstance instance = (FieldInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("fieldInstance");
			if (instancesId.contains(":")) { // si tratta di un campo contenuto in un gruppo
				field = (Field) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("subfield");
				instance = (FieldInstance) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("subfieldInstance");
			}
			
			if (field != null && instance != null) {
				
				String aliasName 	= "";
				if (field.getLookupParams().containsKey("aliasName"))
					aliasName		= field.getLookupParams().get("aliasName");
				String aliasName1 	= "";
				if (field.getLookupParams().containsKey("aliasName1"))
					aliasName1		= field.getLookupParams().get("aliasName1");
				String titolo 		= "";
				if (field.getLookupParams().containsKey("titolo"))
					titolo			= field.getLookupParams().get("titolo");
				String ord 			= "";
				if (field.getLookupParams().containsKey("ord"))
					ord				= field.getLookupParams().get("ord");
				String campi 		= "";
				if (field.getLookupParams().containsKey("campi"))
					campi			= getLookupFieldsWithInstances(field.getLookupParams().get("campi"), instancesId);
				String db 			= "";
				if (field.getLookupParams().containsKey("db"))
					db				= field.getLookupParams().get("db");
				String xq			= ""; //extraQuery
				if (field.getLookupParams().containsKey("xq"))
					xq				= field.getLookupParams().get("xq");
				String value 		= instance.getValue();
				if (value.equals(""))
					value = "*";
				
				// mbernardini 13/12/2016 : gestione delle restrizioni su aoo tramite extraquery su lookup
				// sostituzione di [CODAMMAOO] con il codice della sede dell'operatore corrente
				if (xq != null && !xq.isEmpty()) {
					if (getUserBean() != null && getUserBean().getCodSede() != null)
						xq = xq.replaceAll("_CODAMMAOO_", getUserBean().getCodSede());
				}
				
				CustomFieldsLookup customFieldsLookup = new CustomFieldsLookup();
				setCustomfieldsLookup(customFieldsLookup);
				customFieldsLookup.setField(field);
				customFieldsLookup.setInstance(instance);
				customFieldsLookup.setModel(this.getModel());
				customFieldsLookup.setCustomFieldsModel(getCustomfields());
				
				
				customFieldsLookup.cleanFields(campi);
				getFormsAdapter().lookup(aliasName, aliasName1, titolo, ord, campi, xq, db, "", value);
				
				XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
				if (handleErrorResponse(response)) {
					getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
					return null;
				}
				customFieldsLookup.getFormsAdapter().fillFormsFromResponse(response);
				customFieldsLookup.init(response.getDocument());
				customFieldsLookup.setLookupFieldVal(value);
				
				if(customFieldsLookup.getTitoli().size() == 1 && value != null && value.length() > 0){
					customFieldsLookup.confirm(customFieldsLookup.getTitoli().get(0));
				}
				else {
					customFieldsLookup.setActive(true);
				}
				
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse());
			}
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form			
		}
		
		return null;
	}
	
	/**
	 * aggiunta degli indici delle istanze (lookup in istanze multiple) all'interno dei campi di definizione 
	 * del lookup (per il riempimento dei valori di ritorno) in base all'identificativo dell'elemento selezionato
	 * nella pagina (campo di lookup)
	 * 
	 * @param campi definizione dell'elenco di campi da riempire dopo la selezione da lookup
	 * @param instancesId identificativi delle istanze alle quali fa riferimento il campo di lookup
	 * @return
	 */
	private String getLookupFieldsWithInstances(String campi, String instancesId) {
		if (instancesId != null && instancesId.length() > 0) {
			String[] instancesIdSplit = instancesId.split("\\:");
			
			// inserimento degli indici di istanza all'interno della definizione dei campi
			String[] campiSplit = campi.split(" ; ");
			for (int i=0; i<campiSplit.length; i++) {
				if (campiSplit[i].trim().length() > 0) {
					String[] partiCampo = campiSplit[i].trim().split("\\.");
					if (partiCampo.length-1 == instancesIdSplit.length) {
						String campo = partiCampo[0];
						for (int j=1; j<partiCampo.length; j++) {
							int index = partiCampo[j].indexOf("=");
							if (index != -1) { // campo finale da valorizzare
								String key = partiCampo[j].substring(0, index);
								String path = partiCampo[j].substring(index+1);
								campo += "." + key + "[" + instancesIdSplit[j-1] + "]=" + path;
							}
							else { // campo intermedio (possibile riferimento ad un gruppo di campi)
								campo += "." + partiCampo[j] + "[" + instancesIdSplit[j-1] + "]";
							}
						}
						
						campiSplit[i] = campo;
					}
				}
			}
			
			campi = Arrays.asList(campiSplit).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ; ");
		}
		return campi;
	}
	
	/**
	 * Chiamata a Pulizia di campi custom di DocWay
	 * 
	 * @param campi stringa contenente i campi da ripulire
	 * @return
	 * @throws Exception
	 */
	public String clearCustomField(String campi) throws Exception{
		// in caso di clear lookup occorre impostare a false il reset dei jobs di iwx
		// perche' viene poi ricaricata la pagina di inserimento/modifica e in caso di immagini precedentemente
		// caricate si perderebbero le anteprime
		setResetJobsIWX(false);
		
		CustomFieldsLookup customFieldsLookup = new CustomFieldsLookup();
		customFieldsLookup.cleanFields(campi);
		return null;
	}
	
	/**
	 * Chiamata a titolario di classificazione
	 * 
	 * @param entity
	 * @param windowTitle
	 * @throws Exception
	 */
	protected void callShowThesRel(XmlEntity entity, String windowTitle, String relation, String keypath, String startkey, String db, String toDo, String value) throws Exception {
		callShowThesRel(entity, windowTitle, relation, keypath, startkey, db, toDo, value, "");
	}
	
	/**
	 * Chiamata a titolario di classificazione
	 * 
	 * @param entity
	 * @param windowTitle
	 * @throws Exception
	 */
	protected void callShowThesRel(XmlEntity entity, String windowTitle, String relation, String keypath, String startkey, String db, String toDo, String value, String classifPrefix) throws Exception {
		try {
			DocWayShowthes docwayShowthes = new DocWayShowthes(); 
			setShowthes(docwayShowthes);
			docwayShowthes.setModel(entity);
			docwayShowthes.setWindowTitle(windowTitle);
			if (classifPrefix != null)
				docwayShowthes.setClassifPrefix(classifPrefix);
			
			UserBean userBean = getUserBean();
			getFormsAdapter().showthesRel(userBean.getLogin(), userBean.getMatricola(), relation, keypath, startkey, db, false, toDo, value); // TODO modificato a causa del problema cache in login (verificare meglio, si tratta solo di un workaround)
		
			XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(userBean);
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return;
			}
			
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
			
			docwayShowthes.getFormsAdapter().fillFormsFromResponse(response);		
			docwayShowthes.init(response.getDocument());
			
			docwayShowthes.setActive(true);
			docwayShowthes.selectThes();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;			
		}
	}
	
	public boolean isClassificazioneDaTitolario() {
		return classificazioneDaTitolario;
	}

	public void setClassificazioneDaTitolario(boolean classificazioneDaTitolario) {
		this.classificazioneDaTitolario = classificazioneDaTitolario;
	}
	
	public boolean isShowRaccomandataFields() {
		return showRaccomandataFields;
	}

	public void setShowRaccomandataFields(boolean showRaccomandataFields) {
		this.showRaccomandataFields = showRaccomandataFields;
	}
	
	public boolean isAlertForTutti() {
		// Tento il recupero del valore dal bean dell'utente
		UserBean userbean = getUserBean();
		if (userbean != null)
			return userbean.isAlertForTuttiConfirm();
		else
			return alertForTutti;
	}
	
	public void setAlertForTutti(boolean alertForTutti) {
		// Imposto il valore anche sul bean dell'utente
		UserBean userbean = getUserBean();
		if (userbean != null) {
			userbean.setAlertForTuttiConfirm(alertForTutti);
			setSessionAttribute("userBean", userbean);
		}
		this.alertForTutti = alertForTutti;
	}
	
	public boolean isResetJobsIWX() {
		return resetJobsIWX;
	}

	public void setResetJobsIWX(boolean resetJobsIWX) {
		this.resetJobsIWX = resetJobsIWX;
	}
	
	public String getAppStringPreferences() {
		return appStringPreferences;
	}

	public void setAppStringPreferences(String appStringPreferences) {
		this.appStringPreferences = appStringPreferences;
	}
	
	/**
	 * Imposta per la pagina l'inserimento manuale della classificazione
	 * @return
	 */
	public String passaInserimentoManualeClassif() {
		this.setClassificazioneDaTitolario(false);
		return null;
	}
	
	/**
	 * Imposta per la pagina l'inserimento da titolario della classificazione
	 * @return
	 */
	public String passaInserimentoTitolarioClassif() {
		this.setClassificazioneDaTitolario(true);
		return null;
	}
	
	/**
	 * Attiva o meno la sezione relativa al numero di raccomandata in base
	 * alla selezione del mezzo di trasmissione
	 * @param e nuovo valore associato alla proprieta' mezzo di trasmissione
	 */
	public void checkMezzo(ValueChangeEvent e) {
		String mezzoTrasmissione = "";
		if (e.getNewValue() != null)
			mezzoTrasmissione = e.getNewValue().toString();
		if (mezzoTrasmissione.startsWith("Raccomandata"))
			this.showRaccomandataFields = true;
		else
			this.showRaccomandataFields = false;
	}
	
	/**
	 * Inserimento di un nuovo repertorio
	 * @return
	 * @throws Exception
	 */
	protected XMLDocumento _insTableDocRep(String tableName, String codRep, String descrRep) throws Exception {
		getFormsAdapter().insTableDocRep(tableName, codRep, descrRep);
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	/**
	 * Inserimento di un nuovo fascicolo custom
	 * @return
	 * @throws Exception
	 */
	protected XMLDocumento _insTableFascicoloCustom(String tableName, String codFasc, String descrFasc) throws Exception {
		getFormsAdapter().insTableFascicoloCustom(tableName, codFasc, descrFasc);
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}

	/**
	 * Chiamata a thesauro vincolato da DocWay
	 * @param entity
	 * @param fieldName
	 * @param chiave
	 * @param value
	 * @throws Exception
	 */
	public void callThVincolato(XmlEntity entity, String fieldName, String chiave, String value) throws Exception {
		String numTitles4Page = "DEF";
		String radice = "Radice";
		String relazione = "7";
		String insRight = "ACL-14";
		String delRight = "ACL-15";
		String windowTitle = "";
		
		callThesaurus(entity, numTitles4Page, fieldName, radice, relazione, chiave, insRight, delRight, windowTitle, value);
	}
	
	protected void callThesaurus(XmlEntity entity, String numTitles4Page, String fieldName, String radice, String relazione, String chiave, String insRight, String delRight,String windowTitle, String value) throws Exception {
		try {
			DocWayThvincolato docwayThvincolato = new DocWayThvincolato();
			setThvincolato(docwayThvincolato);
			docwayThvincolato.setModel(entity);
		
			XMLDocumento response = this._thVincolato(numTitles4Page, fieldName, radice, relazione, chiave, insRight, delRight, windowTitle, false, value);
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return;
			}
			docwayThvincolato.getFormsAdapter().fillFormsFromResponse(response);		
			docwayThvincolato.init(response.getDocument());
			if ( docwayThvincolato.getTitoli().size() == 1 && value.length() > 0)
				docwayThvincolato.confirm(docwayThvincolato.getTitoli().get(0));
			else
				docwayThvincolato.setActive(true);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return;			
		}
	}
	
	protected XMLDocumento _thVincolato(String numTitles4Page, String fieldName, String radice, String relazione, String chiave, String insRight, String delRight, String windowTitle, boolean iscascade, String value) throws Exception {
		getThvincolato().cleanFields(fieldName);
		getFormsAdapter().thVincolato(numTitles4Page, fieldName, radice, relazione, chiave, insRight, delRight, windowTitle, iscascade, value);
		
		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
		return response;		
	}
	
}
