package it.tredi.dw4.aclCrawler.adapters;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.FormAdapter;
import it.tredi.dw4.adapters.FormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;

public class AclCrawlerFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	protected FormAdapter indexForm;
	
	public AclCrawlerFormsAdapter(AdapterConfig config) {
		this.defaultForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
		this.indexForm = new FormAdapter(config.getHost(), config.getPort(), config.getProtocol(), config.getResource(), config.getUserAgent());
	}
	
	public FormAdapter getDefaultForm() {
		return defaultForm;
	}
	
	public FormAdapter getIndexForm() {
		return indexForm;
	}
	
	@Override
	public void fillFormsFromResponse(XMLDocumento response) throws DocumentException {
		super.fillFormsFromResponse(response);
		resetForms();
		
		fillDefaultFormFromResponse(response);
		fillIndexFormFromResponse(response);
	}
	
	public void init() {
		defaultForm.addParam("xverb", "init");
	}
	
	public void queryPlain(String query) {
		defaultForm.addParam("verbo", "apps.aclCrawler.Acl_crawler");
		defaultForm.addParam("xverb", "query");
		defaultForm.addParam("query", query);
		defaultForm.addParam("dbTable", "@persona_interna");
	}
	
	public void export(String printableQuery, String type) {
		defaultForm.addParam("verbo", "apps.aclCrawler.Acl_crawler");
		defaultForm.addParam("xverb", "export");
		defaultForm.addParam("exporttype", type);
		defaultForm.addParam("dbTable", "@persona_interna");
	}
	
	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		Element root = response.getRootElement();
		
		defaultForm.addParam("verbo", "apps.aclCrawler.Acl_crawler");
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		defaultForm.addParam("sele", root.attributeValue("sele", ""));
		defaultForm.addParam("db", root.attributeValue("db", ""));
		
		/* memorizza lo stato di esplosione dell'albero gerarchico */
		defaultForm.addParam("hierSearchList", "");
		defaultForm.addParam("hierSearchShow", root.attributeValue("hierSearchShow", ""));
		defaultForm.addParam("cuttingBranch", "");
		
		/* per i repertori su cui ho visione completa */
		defaultForm.addParam("repVisComp", "");
		
		/* per navigazione su thesauro */ 
		defaultForm.addParam("keypath", "''");
		defaultForm.addParam("startkey", "''");
		defaultForm.addParam("relation", "''");
		defaultForm.addParam("xMode", "");
		
		/* per navigazione su thesauro vincolato */ 
		defaultForm.addParam("lookup_new", "");
		defaultForm.addParam("lookup_campi", "");
		defaultForm.addParam("lookup_fieldVal", "");
		
		/* management export documents */
		defaultForm.addParam("exclusionFields", "");
		defaultForm.addParam("inclusionFields", "");
		defaultForm.addParam("rangeDocs", "");
		defaultForm.addParam("pathFiles", "");
		
		/* uso speciale per upload di file da strumenti di amministrazione */
		defaultForm.addParam("propertyEntry", "");
		
		/* per ultimo accesso e cambio password */
		defaultForm.addParam("userInfo", root.attributeValue("userInfo", ""));
		defaultForm.addParam("pwdLock", root.attributeValue("pwdLock", ""));
		defaultForm.addParam("pwdPhysdoc", root.attributeValue("pwdPhysdoc", ""));
		defaultForm.addParam("physDoc", root.attributeValue("physDoc", ""));
		defaultForm.addParam("pwdChangeInfo", "");
		
		/* per inserimento/modifica di repertori */ 
		defaultForm.addParam("codice_rep", "");
		defaultForm.addParam("descrizione_rep", "");
		
		/* tupla per il pulsante 'back' del thesauro gerarchico */
		defaultForm.addParam("thBackPage", root.attributeValue("", "thBackPage"));
		
		defaultForm.addParam("personalView", "");
		
		/* introdotta tupla per informazioni di avviamento delle stampe Jasper [RW 0039816] */
		defaultForm.addParam("jReportInfo", "");
		defaultForm.addParam("jReportParams", "");
		
		/*  introdotta possibilita' di indicare un file xsl da usare per filtrare i documenti esportati [RW 0047738] */
		defaultForm.addParam("xslExportFilterName", "");
		
		/* per ultimo accesso e cambio password */
		String pwdInfo = "";
		if (!root.attributeValue("lastLoginDate", "").equals("") || !root.attributeValue("pwdLeftTime", "").equals(""))
			pwdInfo = root.attributeValue("lastLoginDate", "") + "|" + root.attributeValue("lastLoginTime", "") + "|" + root.attributeValue("pwdLeftTime", "");
		defaultForm.addParam("pwdInfo", pwdInfo);
		
		//inserimento in un fascicolo
		defaultForm.addParam("physDoc_infasc", root.attributeValue("physDoc_infasc", ""));
		defaultForm.addParam("diritto_infasc", root.attributeValue("diritto_infasc", ""));
		defaultForm.addParam("nome_persona_infasc", root.attributeValue("nome_persona_infasc", ""));
		defaultForm.addParam("nome_uff_infasc", root.attributeValue("nome_uff_infasc", ""));
		defaultForm.addParam("cod_persona_infasc", root.attributeValue("cod_persona_infasc", ""));
		defaultForm.addParam("cod_uff_infasc", root.attributeValue("cod_uff_infasc", ""));
	}
	
	protected void fillIndexFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(indexForm, response);
		Element root = response.getRootElement();
		
		indexForm.addParam("verbo", "showindex");		
		indexForm.addParam("xverb", "");
		indexForm.addParam("db", root.attributeValue("db", ""));
		indexForm.addParam("keypath", "");
		indexForm.addParam("fillField", "");
		indexForm.addParam("startkey", "");
		indexForm.addParam("keyCount", root.attributeValue("keyCount", ""));
		indexForm.addParam("threl", "");
		indexForm.addParam("doubleKey", "");
		indexForm.addParam("shwMode", "");
		indexForm.addParam("xMode", "");
		
		indexForm.addParam("minf", root.attributeValue("minf", ""));
		indexForm.addParam("maxf", root.attributeValue("maxf", ""));
		indexForm.addParam("xRels", "");
		indexForm.addParam("vRels", "");
		indexForm.addParam("cPath", "");
		indexForm.addParam("lRel", "");
		indexForm.addParam("sele", "");
		indexForm.addParam("destPage", "");
		indexForm.addParam("winTarget", "");
	}
	
	protected void resetForms() {
		this.defaultForm.resetParams();
		this.indexForm.resetParams();
	}
	
	public void openIndex(
			String keyPath, // identifica la chiave negli indici
			String startKey,// posizionamento
			String shwMode, // imposta la modalità di formattazione ('dt' per data)
			String common,  // parte comune (se contiene '|^|' allora la parte della chiave che precede '|^|' viene scartata)
			String threl,   // opzionalmente l'indice della relazione con cui attingere la descrizione associata alla chiave (classificazioni)
			String inputName,//l'input dove depositare l'esito della selezione
			String windowTitle, // window title
			String value, 
			boolean acceptEmptySelection) { //se la selezione è vuota forza comunque il vocabolario su quella selezione 
		
		String vocTitle = "Vocabolario";
		if (windowTitle != null && windowTitle.length() > 0)
			vocTitle = windowTitle;
		this.indexForm.addParam("verbo", "showindex");
		this.indexForm.addParam("view", vocTitle);

/* FIXME
	  	if("undefined"!=typeof(customGetKeyName))
	   		keyPath=customGetKeyName(keyPath);   		

	  if(("undefined"!=typeof(theInput))&&(""!=theInput))
	  {//element=theInput;
	   qKey=theInput;
	   element=qForm[qKey];
	  }
	  else
	  {if ("undefined"!=typeof(customGetIndexedElement))
	   {qKey=customGetIndexedElement(keyPath);
	    element=qForm[qKey];
	   }
	   else
	   {proArch="";
	    if("undefined"!=typeof(getNowArch))
	     proArch="#"+getNowArch(getForm('hxpForm').db.value);
	    qKey="["+keyPath+"]"+proArch;
	    element=qForm[qKey];
	    if(!element){
	     qKey="["+keyPath+"]";
	     element=qForm[qKey];
	    }
	   }
	  }
	  */

		if (value != null && value.length() > 0)
			startKey = value;
		if (startKey.startsWith("\""))
			startKey = startKey.substring(1);
	  
		if (common == null) common = "";
		if (startKey == null) startKey = "";
		this.indexForm.addParam("threl", threl == null? "": threl);
		this.indexForm.addParam("cPath", common == null? "": common);
		this.indexForm.addParam("doubleKey", common.equals(" ")? "1": "0");
		this.indexForm.addParam("shwMode", shwMode == null? "": shwMode);
		this.indexForm.addParam("sele", "");
		this.indexForm.addParam("startkey", common + startKey);
		this.indexForm.addParam("selid", this.defaultForm.getParam("selid")); //FIXME
		this.indexForm.addParam("acceptEmptySelection", acceptEmptySelection);

		if (inputName != null && inputName.length() > 0)
			this.indexForm.addParam("fillField", inputName);
		else
			this.indexForm.addParam("fillField", keyPath);
		
		this.indexForm.addParam("keypath", keyPath);

	  //FIXME if (!raffinamento) svuotaSelezione();
	}	
}
