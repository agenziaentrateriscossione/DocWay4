package it.tredi.dw4.adapters;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.utils.string.Text;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.StringUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;

public class FormsAdapter {
	protected XMLDocumento lastResponse;
	protected Map<String, Boolean> funzionalitaDisponibili = new HashMap<String, Boolean>();
    public static final String customTupleName = "_cd";
    private String customTemplate = ""; // dpranteda 10/12/2014 - eventuale template personalizzato (es. Docway Delibere)

    public String getCustomTemplate() {
		return customTemplate;
	}
    
    public XMLDocumento getLastResponse() {
		return lastResponse;
	}
	
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		this.lastResponse = response;
		parseFunzionalitaDisponibili(response);
		this.customTemplate = response.getRootElement().attributeValue("dw4customTemplate", "");
	}

	public void addSessionData(FormAdapter form, XMLDocumento response) throws DocumentException {
		Element root = response.getRootElement();

		//input hidden presi da hxp.xsl, progetto genericPresentation
		form.addParam("globalFormRestore", "");
		form.addParam("host", root.attributeValue("host", ""));
		form.addParam("_hcf", root.attributeValue("_hcf", ""));
		form.addParam("_hca", root.attributeValue("_hca", ""));
		form.addParam("uri", root.attributeValue("uri", ""));
		form.addParam("port", root.attributeValue("port", ""));
		form.addParam("db", root.attributeValue("db", ""));
		form.addParam("dbTable", root.attributeValue("dbTable", ""));
		form.addParam("user", root.attributeValue("user", ""));
		form.addParam("pwd", root.attributeValue("pwd", ""));
		form.addParam("query", root.attributeValue("query", ""));
		form.addParam("pos", root.attributeValue("pos", ""));
		form.addParam("count", root.attributeValue("count", ""));
		form.addParam("keyCount", root.attributeValue("keyCount", ""));
		form.addParam("pageCount", root.attributeValue("pageCount", ""));
		form.addParam("selid", root.attributeValue("selid", ""));
		form.addParam("selTable", root.attributeValue("selTable", ""));
		form.addParam("dt", root.attributeValue("dt", ""));
		form.addParam("noRef", root.attributeValue("noref", ""));
		form.addParam("opt", root.attributeValue("opt", ""));
		form.addParam("qopt", root.attributeValue("qopt", ""));
		form.addParam("qadj", root.attributeValue("qadj", ""));
		form.addParam("qext", root.attributeValue("qext", ""));
		form.addParam("keylist", root.attributeValue("keylist", ""));
		form.addParam("klAll", root.attributeValue("klAll", ""));
		form.addParam("klRac", root.attributeValue("klRac", ""));
		form.addParam("selRac", root.attributeValue("selRac", ""));
		form.addParam("revSel", root.attributeValue("revSel", ""));
		form.addParam("flap", root.attributeValue("flap", ""));
		form.addParam("aclDb", root.attributeValue("aclDb", ""));
		form.addParam("login", root.attributeValue("login", ""));
		form.addParam("matricola", root.attributeValue("matricola", ""));
		
		form.addParam("opCodPersona", root.attributeValue("opCodPersona", ""));
		form.addParam("opNomePersona", root.attributeValue("opNomePersona", ""));
		
		/* browser gerarchico */
		form.addParam("hierCount", root.attributeValue("hierCount", ""));
		form.addParam("hierStatus", root.attributeValue("hierStatus", ""));
		form.addParam("docStart", root.attributeValue("docStart", ""));
		
		/* raccolte */
		form.addParam("cName", root.attributeValue("cName", ""));
		form.addParam("cId", root.attributeValue("cId", ""));
		form.addParam("cCount", root.attributeValue("cCount", ""));
		/* aggiunto per poter utilizzare il codice per la gestione delle raccolte impiegato su nif */
		form.addParam("cIsPublic", root.attributeValue("cIsPublic", ""));
		
		/* informazioni su utente */
		form.addParam("userRights", root.attributeValue("userRights", ""));
		form.addParam("acl", root.attributeValue("acl", ""));
		form.addParam("showdocUOR", root.attributeValue("showdocUOR", ""));
		form.addParam("doceditUOR", root.attributeValue("doceditUOR", ""));
		form.addParam("docInGestioneUOR", root.attributeValue("docInGestioneUOR", ""));
		form.addParam("uorInGestioneSelected", root.attributeValue("uorInGestioneSelected", ""));
		form.addParam("browser", root.attributeValue("browser", ""));
		String enableIW = root.attributeValue("enableIW", "");
		if (enableIW.equals(""))
			enableIW = isEnabledIWX(); // occorre forzare un valore per iw altrimenti non funzionerebbero le ricerche
		form.addParam("enableIW", enableIW);
		form.addParam("transformJava", root.attributeValue("transformJava", ""));
		form.addParam("view", root.attributeValue("view", ""));
		form.addParam("toDo", root.attributeValue("toDo", ""));
		form.addParam("specificAppCode", root.attributeValue("specificAppCode", ""));
		form.addParam("specificDbCode", root.attributeValue("specificDbCode", ""));
		form.addParam("warnings", root.attributeValue("warnings", ""));
		form.addParam("appPreferences", root.attributeValue("appPreferences", ""));
		form.addParam("appStringPreferences", root.attributeValue("appStringPreferences", ""));
		form.addParam("mansioni", root.attributeValue("mansioni", ""));
		form.addParam("respUOR", root.attributeValue("respUOR", ""));
		form.addParam("groups", root.attributeValue("groups", ""));
		form.addParam("language", root.attributeValue("language", ""));
		/* profilo */
		form.addParam("_r", root.attributeValue("_r", ""));
		
		/* workflow */
		form.addParam("wfActive", root.attributeValue("wfActive", ""));
		form.addParam("wfId", root.attributeValue("wfId", ""));
		form.addParam("wfAction", root.attributeValue("wfAction", ""));
		form.addParam("wfDoc", root.attributeValue("wfDoc", ""));
		form.addParam("wfText", root.attributeValue("wfText", ""));
		form.addParam("wfLock", root.attributeValue("wfLock", ""));
		form.addParam("wfSelId", root.attributeValue("wfSelId", ""));
		form.addParam("wfPos", root.attributeValue("wfPos", ""));
		form.addParam("wfFCount", root.attributeValue("wfFCount", ""));
		form.addParam("wfMode", root.attributeValue("wfMode", ""));
		
		/* gestione embedded app */
		form.addParam(Const.DOCWAY_EMBEDDED_APP_NAME, root.attributeValue(Const.DOCWAY_EMBEDDED_APP_NAME, ""));
		
		/* documenti selezionati per l'esecuzione delle action disponibili di un flusso [RW 0037104] */
		form.addParam("selectedWfDocs", root.attributeValue("selectedWfDocs", ""));
		
		/* nome della classe che gestisce le interazioni utente richieste dalle function [RW 0037104] */
		form.addParam("WfFunctionInteractionManagerClass", root.attributeValue("WfFunctionInteractionManagerClass", ""));
		
		/* gis */
		form.addParam("allotri", root.attributeValue("allotri", ""));
		
		/* indice della tendina dell'ordinamento selezionato */
		form.addParam("qordIdx", root.attributeValue("qordIdx", ""));
		form.addParam("qordIdx1", root.attributeValue("qordIdx1", ""));
		
		/* per larghezza colonna titoli */
		form.addParam("colsSize", root.attributeValue("colsSize", ""));
		
		/* dati custom */
		form.addParam(customTupleName, root.attributeValue(customTupleName, "").replaceAll("&#38;", "&"));
		
		/* showtitles page title */
		form.addParam("stpTitle", root.attributeValue("stpTitle", ""));
		
		/*
		 * al posto di dbTable ora viene utilizzata la tupla 'thVOptions' per usare i checkbox al posto dei
		 * radio button nel thesauro vincolato (si veda anomalia 0022367).
		 * Il nome di tale tupla è volutamente generico (cioè non riferito all'uso dei checkbox) in modo
		 * che possa essere utilizzata per trasmettere anche altre opzioni relative ai th vincolati
		 * (possibili usi futuri).
		 */
		form.addParam("thVOptions", root.attributeValue("thVOptions", ""));
		
		/* tupla che indica il marcatore xml delle sezioni in lingua dei documenti. */
		form.addParam("languageMarker", root.attributeValue("languageMarker", ""));
		
		/* Pattern della classificazione custom */
		form.addParam("classifFormat", root.attributeValue("classifFormat", ""));
		
		/*
		 * handType per la creazione delle risposte di errore (consente di chiudere o meno le finestre
		 * aperte dal js premendo 'indietro' nelle pagine di errore) [RW 0033834]
		 */
		form.addParam("errorMsgHandType", root.attributeValue("", ""));  //FIXME probabilmente si può levare di qui
		
		/* introdotta possibilità di avere dei file di risorse personalizzati per cliente [EB 111] */
		form.addParam("resourceFileCustomDir", root.attributeValue("resourceFileCustomDir", ""));
		
		// mbernardini 11/02/2014 : gestione comune della sessione fra DocWay4 e DocWay4-service
		form.addParam("jsessionid", root.attributeValue("jsessionid", ""));
		
		//dpranteda 10/12/2014 : parametro per settare template personalizzati per applicazioni incluse (es. Docway Delibere)
		form.addParam("dw4customTemplate", this.customTemplate);
	}
	
	public void addBeforeEndEmbedded(FormAdapter form, XMLDocumento response) throws DocumentException {
		Element root = response.getRootElement();
		
		form.addParam("fileList", root.attributeValue("fileList", ""));
		form.addParam("extraView", "");
		form.addParam("selectedIndex", root.attributeValue("selectedIndex", ""));
	}
	
	public Map<String, Boolean> getFunzionalitaDisponibili() {
		return funzionalitaDisponibili;
	}
	
	@SuppressWarnings("unchecked")
	protected void parseFunzionalitaDisponibili(XMLDocumento response) {
		funzionalitaDisponibili.clear();
		List<Element> funzDisponibili = (List<Element>) response.selectNodes("//funzionalita_disponibili");
		for (Iterator<Element> iter = funzDisponibili.iterator(); iter.hasNext();) {
			Element funzDisponibiliEl = (Element) iter.next();
			if (null != funzDisponibiliEl){
				Iterator<Attribute> attrsIt = funzDisponibiliEl.attributeIterator();
				while (attrsIt.hasNext()) {
					Attribute attr = attrsIt.next();
					this.funzionalitaDisponibili.put(attr.getName(), StringUtil.booleanValue(attr.getValue()));
				}
			}
		}
	}
	
	public boolean checkBooleanFunzionalitaDisponibile(String key, boolean defaultValue) {
		if (funzionalitaDisponibili.containsKey(key))
			return funzionalitaDisponibili.get(key).booleanValue();
		else
			return defaultValue;
	}
	
	 /**
     * Restituisce il valore di un parametro contenuto nella tupla _cd
     * @param paramName nome del parametro da cercare
     * @param customTupleData dove cercare
     * @return valore del parametro, null se assente
     */
    @SuppressWarnings("rawtypes")
	public static String getParameterFromCustomTupleValue(String paramName, String customTupleData) {
        Vector v = Text.split(customTupleData, "&");
        String val;
        for (int i = 0; i < v.size(); i++) {
            if ((val = (String)v.get(i)).indexOf(paramName + "=") == 0)
                return Text.unescape(val.substring(val.indexOf("=") + 1)).toString();
        }
        return null;
    }

    /**
     * Imposta il valore di un parametro contenuto nella tupla _cd
     * @param paramName nome del parametro da settare
     * @param paramValue valore da impostare. Se null allora il parametro deve essere rimosso
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static String setParameterFromCustomTupleValue(String paramName, String paramValue, String paramString) {
		Vector v = Text.split(paramString, "&");
        String val;
        boolean setted = false;
        for (int i = 0; i < v.size() && !setted; i++) {
            if ((val = (String)v.get(i)).indexOf(paramName + "=") == 0) {
                /* Federico-Nicola 01/02/06: spostato 'setted = true;' fuori dall'if, se no l'
                 * aggiornamento di una tupla già esistente non funziona [RW 0032659]
                 */
                setted = true;

                if (paramValue == null) {
                    v.set(i, null);
                }
                else {
                    //setted = true;
                    v.set(i, paramName + "=" + Text.escape(paramValue));
                }
            }
        }

        // Federico 26/10/05: se 'paramValue' è null non deve essere aggiunto al Vector
        if (!setted && paramValue != null) {
            v.add(paramName + "=" + Text.escape(paramValue));
        }

        paramString = "";

        /* Federico-Nicola 01/02/06: eliminato test '!setted' in quanto il vettore 'v' è
         * stato aggiornato e quindi va ricopiato nella tupla 'customTupleValue' dato che
         * è stata svuotata [RW 0032659]
         */
        for (int i = 0; i < v.size()/* && !setted*/; i++) {
            if ((val = (String)v.get(i)) == null || val.length() == 0) continue;
            paramString += "&" + val;
        }

        if (paramString.length() > 0) paramString = paramString.substring(1);
        return paramString;
    }
    
    /**
	 * tentativo di recupero di iwx dalla sessione o dalla request per assegnazione del valore di default
	 * 
	 * @return true se IWX deve essere attivato, false altrimenti
	 */
	public String isEnabledIWX() {
		String enableIWX = "";
		
		try {
			HttpSession session = null;
			if (FacesContext.getCurrentInstance() != null)
				session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			
			if (session != null && session.getAttribute("enableIWX") != null) { // recupero abilitazione iwx dalla sessione
				enableIWX = (String) session.getAttribute("enableIWX");
			}
			else {  // identificazione del client e controllo compatibilita' iwx
				Map<String, String> headerMap = FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap();
				if (headerMap.containsKey("user-agent")) {
					if (headerMap.get("user-agent").toLowerCase().indexOf("windows") != -1)
						enableIWX = "true";
					else
						enableIWX = "false";
				}
			}
		}
		catch (Exception e) {
			Logger.warn("FormsAdapter.isEnabledIWX() : impossibile identificare abilitazione IWX sul client tramite FacesContext. ERR: " + e.getMessage());
		}
		
		if (enableIWX == null || enableIWX.equals(""))
			enableIWX = "false"; // occorre impostare un valore di default in caso di dato vuoto altrimenti non funzionerebbero le ricerche
		
		return enableIWX;
	}

}
