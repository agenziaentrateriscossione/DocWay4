package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.addons.BaseAddOn;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.DateUtil;
import it.tredi.dw4.utils.DocWayProperties;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;

import java.util.List;

import org.dom4j.Document;

public abstract class AclQuery extends AclPage {
	
	public final String CODSEDE_KEY = "%CODSEDE%";
	
	public abstract void init(Document dom);

	public abstract QueryFormsAdapter getFormsAdapter();

	public abstract String queryPlain() throws Exception;

	/**
	 * clear del form di ricerca. Azzeramento di tutti gli addons
	 * caricati sulla pagina di ricerca.
	 */
	public void resetAddonsQuery() {
		List<BaseAddOn> addons = getAspects();
		if (addons != null && addons.size() > 0) {
			for (int i=0; i<addons.size(); i++) {
				BaseAddOn addon = (BaseAddOn) addons.get(i);
				if (addon != null)
					addon.clear();
			}
		}
	}
	
	public XMLDocumento _queryPlain(String query, String verbo, String xverb)
			throws Exception {
		
		// caricamento di tutte le porzioni di query relative ad addons caricati
		// nella pagina di ricerca
		if (query != null && query.length() > 0)
			query = "(" + query + ")";
		
		List<BaseAddOn> addons = getAspects();
		if (addons != null && addons.size() > 0) {
			for (int i=0; i<addons.size(); i++) {
				BaseAddOn addon = (BaseAddOn) addons.get(i);
				if (addon != null) {
					String addonquery = addon.asQuery();
					if (addonquery != null && addonquery.trim().length() > 0) {
						if (query == null || query.length() == 0)
							query = addonquery;
						else
							query = query + " AND " + addonquery;
					}
				}
			}
		}
		
		getFormsAdapter().queryPlain(query, verbo, xverb);

		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}

	protected XMLDocumento _openIdex(String keyPath, String startKey,
			String shwMode, String common, String threl, String inputName,
			String windowTitle, String value) throws Exception {
		getFormsAdapter().openIndex(keyPath, startKey, shwMode, common, threl, inputName, windowTitle, value, false);

		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di Query
		return response;
	}

	public void openIndex(String keyPath, String value, String startKey, String common) throws Exception {
		openIndex("", keyPath, value, startKey, common);
	}

	public void openIndex(String inputName, String keyPath, String value, String startKey, String common) throws Exception {
		openIndex(this, inputName, keyPath, value, startKey, common);
	}
	
	public void openIndex(Object model, String inputName, String keyPath, String value, String startKey, String common) throws Exception {
		AclShowindex aclShowindex = new AclShowindex();
		setShowindex(aclShowindex);
		aclShowindex.setModel(model);

		XMLDocumento response = this._openIdex(keyPath, // keyPath
				startKey, // startKey
				null, // shwMode
				common, // common
				null, // threl
				inputName, // inputName
				null, // windowTitle
				value // value
				);

		aclShowindex.getFormsAdapter().fillFormsFromResponse(response);
		aclShowindex.init(response.getDocument());

		/*
		 * if ( aclShowindex().size() ==1 )
		 * aclShowindex(aclThvincolato.getTitoli().get(0)); else
		 */
		aclShowindex.setActive(true);
	}

	public String addQueryField(String query, String value) {
		return addQueryField(query, value, "AND");
	}

	public String addQueryField(String query, String value, String operator) {
		if (null == value || "".equals(value.trim()))
			return "";
		else {
			// Gestione delle Stoplist
			boolean removeStopListWord = true; // TODO Andrebbe letto da un file di properties dell'applicazione
			if (removeStopListWord && query.indexOf("SrcStp:") == -1)
				query = query + "|SrcStp:null"; // In questo modo viene imposto di non utilizzare le stoplist
			
			return "([" + query + "]=" + value + ") " + operator + " "; // fcappelli 20120906 - rimossi i doppi apici dalla query, vanno aggiunti dall'utente eventualmente
		}
	}

	/**
	 * Appende alla query un filtro su range di date in base ai parametri
	 * specificati
	 * 
	 * @param searchName chiave di ricerca (es. creaz, mod)
	 * @param dataFrom data di inizio dell'intervallo di ricerca
	 * @param dataTo data di fine dell'intervallo di ricerca
	 */
	public String addDateRangeQuery(String searchName, String dataFrom,
			String dataTo) {
		return addDateRangeQuery(searchName, dataFrom, dataTo, "");
	}

	/**
	 * Appende alla query un filtro su range di date in base ai parametri
	 * specificati
	 * 
	 * @param searchName chiave di ricerca (es. creaz, mod)
	 * @param dataFrom data di inizio dell'intervallo di ricerca
	 * @param dataTo data di fine dell'intervallo di ricerca
	 * @param operator operatore da appendere dopo la query
	 */
	public String addDateRangeQuery(String searchName, String dataFrom,
			String dataTo, String operator) {
		String formatoData = Const.DEFAULT_DATE_FORMAT; // TODO Caricare il formato della data dal file di properties

		String xwDataFrom = "";
		if (dataFrom != null && dataFrom.length() > 0)
			xwDataFrom = DateUtil.formatDate2XW(dataFrom, formatoData);

		String xwDataTo = "";
		if (dataTo != null && dataTo.length() > 0)
			xwDataTo = DateUtil.formatDate2XW(dataTo, formatoData);

		String rangeQuery = "";
		if (xwDataFrom.length() > 0 && xwDataTo.length() > 0) {
			// Ricerca su range di date
			rangeQuery = "[" + searchName + "]={" + xwDataFrom + "|" + xwDataTo
					+ "}";
		} else if (xwDataFrom.length() > 0) {
			// Ricerca esatta su dataFrom
			rangeQuery = "[" + searchName + "]=" + xwDataFrom;
		} else if (xwDataTo.length() > 0) {
			// Ricerca esatta su dataTo
			rangeQuery = "[" + searchName + "]=" + xwDataTo;
		}

		if (rangeQuery.length() > 0) {
			if (operator != null && operator.length() > 0)
				rangeQuery = "(" + rangeQuery + ") " + operator + " ";
			else
				rangeQuery = "(" + rangeQuery + ")";
		}

		return rangeQuery;
	}

	public String queryPlain(String query) throws Exception {
		try {
			XMLDocumento response = this._queryPlain(query, "", "");
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
				return null;
			}
			if (response.getAttributeValue("//response/@verbo", "").equals("showdoc")) {
				return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
			} 
			else {
				return buildTitlePageAndReturnNavigationRule(response);
			}
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
			return null;
		}
	}
	
	/**
	 * Ritorna true in caso di warning delle restrizioni parziali abilitato, false altrimenti.
	 * Per restrizioni parziali si intende il fatto che un operatore sia privo di restrizioni su anagrafica interna ma non sull'esterna o viceversa.
	 * Il messaggio e' utile in tutti i casi in cui piu' archivi ACL vengono accorpati su uno unico generando cosi' diversi doppioni
	 * che possono generare confusione sugli operatori.
	 */
	public boolean isWarningSuRestrizioniPartizialiEnabled() {
		try {
			return StringUtil.booleanValue(DocWayProperties.readProperty("abilitaWarningSuRestrizioniParziali", "no"));
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);			
			return false;
		}
	}

}
