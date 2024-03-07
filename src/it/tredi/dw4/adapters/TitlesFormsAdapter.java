package it.tredi.dw4.adapters;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator.AdapterConfig;
import it.tredi.dw4.model.azionimassive.Azione;
import it.tredi.dw4.utils.StringUtil;
import it.tredi.dw4.utils.XMLDocumento;

public class TitlesFormsAdapter extends FormsAdapter {
	protected FormAdapter defaultForm;
	protected FormAdapter indexForm;

	public TitlesFormsAdapter(AdapterConfig config) {
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

	protected void fillDefaultFormFromResponse(XMLDocumento response) throws DocumentException {
		addSessionData(defaultForm, response);
		Element root = response.getRootElement();

		defaultForm.addParam("verbo", "showtitles");
		defaultForm.addParam("xverb", root.attributeValue("xverb", ""));
		defaultForm.addParam("sele", root.attributeValue("sele", ""));
		defaultForm.addParam("ripropz", root.attributeValue("ripropz", ""));
		defaultForm.addParam("xDoc", "");
		defaultForm.addParam("qord", root.attributeValue("qord", ""));
		defaultForm.addParam("destPage", root.attributeValue("destPage", ""));
		defaultForm.addParam("titColOrd", root.attributeValue("titColOrd", ""));
		defaultForm.addParam("rifInt", root.attributeValue("rifInt", ""));
		defaultForm.addParam("db", root.attributeValue("db", ""));

		/* tupla necessaria per la visualizzazione (apertura del thesauro dalle molliche relative alla
		 *  classificazione del documento).
		 */
		defaultForm.addParam("cPath", root.attributeValue("cPath", ""));

		/* introdotta gestione della 'personalView' per tornare alla pagina di ricerca da
		 * cui si e' partiti [RW 0032015].
		 */
		defaultForm.addParam("personalView", root.attributeValue("personalViewToUse", ""));
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
		indexForm.addParam("destPage", root.attributeValue("destPage", ""));
	}

	protected void resetForms() {
		this.defaultForm.resetParams();
		this.indexForm.resetParams();
	}

	/* ACTIONS - DEFAULTFORM */

	public void mostraDocumento(int posizione, String db, int asText, String xdoc, String dbTable, String view) {
		String xverb = defaultForm.getParam("xverb");

		//FIXME if (typeof (tipo) == 'undefined' || typeof (customShowDocTitles) == 'undefined'	|| !customShowDocTitles(tipo, 'showdoc', defaultForm.addParam("xverb.value))
		defaultForm.addParam("verbo", "showdoc");

		defaultForm.addParam("pos", posizione);
		defaultForm.addParam("db", db);
		defaultForm.addParam("view", view);
		defaultForm.addParam("auditVisualizzazione", "true");

		if (dbTable != null)
			defaultForm.addParam("dbTable", dbTable);

		String asTextCmd = "@asText";
		if (asText == 1) {
			if (xverb.indexOf(asTextCmd) == -1)
				defaultForm.addParam("xverb", xverb + asTextCmd);
		}
		else {
			defaultForm.addParam("xverb", xverb.replace(asTextCmd, ""));
		}

		if (xdoc != null)
			defaultForm.addParam("xDoc", xdoc);

		//FIXME
		/*
		 * 	var oldTarget_gerarchiaFasc = null;
			var newWindow = null;
		if (typeof (defaultForm.addParam("fascicoli_MostraGerarchia) != 'undefined'
				&& defaultForm.addParam("fascicoli_MostraGerarchia.value == 'true'
				&& originalXverb != '@fascicolo') {
			oldTarget_gerarchiaFasc = defaultForm.addParam("target;
			defaultForm.addParam("target = "win_document";
			newWindow = window.open(staticEmptyWindow, defaultForm.addParam("target,
					"");
			newWindow.focus();
			if (typeof (newWindow.OpenWaitMsg) == 'function')
				newWindow.OpenWaitMsg();
		}

		//DD 18/04/2006 Ripristino il vecchio target e le altre informazioni(RW: 0006138)
		if (oldTarget_gerarchiaFasc != null) {
			defaultForm.addParam("target = oldTarget_gerarchiaFasc;
			restoreTuple();
		}
		 */
	}

	public boolean primaPagina() {
		if (isPrimaPaginaEnabled()) {
			defaultForm.addParam("verbo", "showtitles");
			defaultForm.addParam("pos", 0);
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isPrimaPaginaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0)
			return true;
		else
			return false;
	}

	public boolean paginaPrecedente() {
		if (isPaginaPrecedenteEnabled()) {
			int pos = defaultForm.getParamAsInt("pos");
			defaultForm.addParam("verbo", "showtitles");
			int pageCount = defaultForm.getParamAsInt("pageCount");
			defaultForm.addParam("pos", pos - pageCount < 0? 0 : pos - pageCount);
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isPaginaPrecedenteEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		if (pos > 0)
			return true;
		else
			return false;
	}

	public boolean isIndietroRapidoEnabled() {
		return isPaginaPrecedenteEnabled();
	}
	
	public boolean paginaSuccessiva() {
		if (isPaginaSuccessivaEnabled()) {
			int pos = defaultForm.getParamAsInt("pos");
			int pageCount = defaultForm.getParamAsInt("pageCount");
			defaultForm.addParam("verbo", "showtitles");
			defaultForm.addParam("pos", pos + pageCount);
			return true;
		}
		else {
			return false;
		}
	}
	public boolean paginaSuccessiva(boolean bypassTitleClean) {
		if (isPaginaSuccessivaEnabled()) {
			int pos = defaultForm.getParamAsInt("pos");
			int pageCount = defaultForm.getParamAsInt("pageCount");
			defaultForm.addParam("verbo", "showtitles");
			defaultForm.addParam("pos", pos + pageCount);
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isPaginaSuccessivaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int count = defaultForm.getParamAsInt("count");
		if (count > pos + pageCount)
			return true;
		else
			return false;
	}

	public boolean isAvantiRapidoEnabled() {
		return isPaginaSuccessivaEnabled();
	}
	
	/**
	 * Caricamento di una pagina specifica della lista titoli
	 * @param currentPage
	 * @return
	 */
	public void paginaSpecifica(int currentPage) {
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int pos = (currentPage-1) * pageCount;

		defaultForm.addParam("verbo", "showtitles");
		defaultForm.addParam("pos", pos);
	}

	public boolean ultimaPagina() {
		if (isUltimaPaginaEnabled()) {
			int pageCount = defaultForm.getParamAsInt("pageCount");
			int count = defaultForm.getParamAsInt("count");
			defaultForm.addParam("verbo", "showtitles");
			defaultForm.addParam("pos", (int)Math.floor((count - 1) / pageCount) * pageCount);
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isUltimaPaginaEnabled() {
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int count = defaultForm.getParamAsInt("count");
		if (count > pos + pageCount)
			return true;
		else
			return false;
	}

	public int getFirstPosition(){
		int pos = defaultForm.getParamAsInt("pos");
		return pos +1;
	}
	public int getLastPosition(){
		int pos = defaultForm.getParamAsInt("pos");
		int pageCount = defaultForm.getParamAsInt("pageCount");
		int count = defaultForm.getParamAsInt("count");
		if (count < pos + pageCount)
			return count;
		else return pos + pageCount;
	}

	public int getCount(){
		return defaultForm.getParamAsInt("count");
	}

	public String getSelid(){
		return defaultForm.getParam("selid");
	}

	public int getCurrent() {
		int pos = getFirstPosition();
		int pageCount = defaultForm.getParamAsInt("pageCount");

		if (pos % pageCount == 0)
			return pos/pageCount;
		else
			return (pos/pageCount) + 1;
	}

	public int getTotal() {
		int count = getCount();
		int pageCount = defaultForm.getParamAsInt("pageCount");

		if (count % pageCount == 0)
			return count/pageCount;
		else
			return (count/pageCount) + 1;
	}

	/**
	 * Ritorna true se gli indici sono gestiti tramite elasticsearch, false in caso di gestione tramite extraway
	 * @return
	 */
	public boolean isElasticsearchEnabled() {
		String elasticsearch = defaultForm.getParam("elasticsearch");
		if (elasticsearch != null && elasticsearch.toLowerCase().equals("true"))
			return true;
		else
			return false;
	}
	
	/* ACTIONS - INDEXFORM */

	/**
	 * Imposta i parametri di formsAdapter necessari all'operazione
	 * di esportazione dei risultati di una ricerca
	 */
	public void esportaCSV() {
		defaultForm.addParam("verbo", "exportdocs_response");
		defaultForm.addParam("xverb", "@exportCSV");
	}

	/**
	 * Imposta i parametri di formsAdapter necessari all'operazione
	 * di esportazione dei risultati di una ricerca
	 */
	public void esportaCSVAdvanced() {
		defaultForm.addParam("verbo", "exportdocs_response");
		defaultForm.addParam("xverb", "@exportCSVAdvanced");
	}

	/**
	 * Imposta i parametri di formsAdapter necessari all'operazione
	 * di selezione dei campi per esportazione avanzata csv
	 */
	public void exportCSVAdvancedQueryColumns() {
		defaultForm.addParam("verbo", "exportdocs_response");
		defaultForm.addParam("xverb", "@exportCSVAdvancedQueryColumns");
	}

	/**
	 * Imposta i parametri di formsAdapter necessari all'operazione
	 * di esportazione dei diritti delle persone esterne tramite JasperReport
	 *
	 * @param type
	 * @param printTemplate
	 * @param dataSource
	 */
	public void printPIRights(String type, String printTemplate, String dataSource) {
		defaultForm.addParam("verbo", "Genericprinthandler");
		defaultForm.addParam("xverb", "@print_sel");
		defaultForm.addParam("printTemplate", printTemplate);
		defaultForm.addParam("profileType", "jasper");
		defaultForm.addParam("jReportParams", "");
		defaultForm.addParam("outType", type);
		defaultForm.addParam("dataSource", dataSource);
		defaultForm.addParam("dbTable", "@stampe");
	}

	public void nuovaRicerca() {
		defaultForm.addParam("verbo", "query");
		defaultForm.addParam("dbTable", "");
		defaultForm.addParam("personalView", "");
		defaultForm.addParam("query", "");
		defaultForm.addParam("xverb", "");
		defaultForm.addParam("selid", "");
	}

	private String customFieldIdxName(String fieldIdxName, String dbTable) {
		if (dbTable.equals("@fascicolo"))
			return "qordIdx1";
		else if (dbTable.equals("@stampe"))
			return "";

		return fieldIdxName;
	}

	/**
	 * Ordinamento di una selezione
	 *
	 * @param colSort ordinamento tramite selezione di una colonna
	 * @param xwOrd ordinamento tramite criterio predefinito (selezionato da tendina)
	 * @param xwOrdIndex indice (nella tendina) del criterio di ordinamento selezionato
	 */
	public void sort(String colSort, String xwOrd, int xwOrdIndex) {
		if (colSort != null && colSort.length() > 0) {
			// ordinamento in base a colonna di titolo

			String lastSort = defaultForm.getParam("titColOrd");
			if (lastSort != null && lastSort.length() > 0 && lastSort.toLowerCase().equals(colSort.toLowerCase())) {
				// invert order
				if (lastSort.charAt(0) == 'x')
					colSort = StringUtil.replace(colSort, "xml", "XML");
				else
					colSort = StringUtil.replace(colSort, "XML", "xml");
			}

			defaultForm.addParam("qord", colSort);
			defaultForm.addParam("titColOrd", colSort);
		}
		else if (xwOrd != null && xwOrd.length() > 0) {
			// ordinamento e probabilistica in base alla tendina

			defaultForm.addParam("titColOrd", "");
			String qopt = "";
			if (xwOrd.equals("*xwProb*"))
				defaultForm.addParam("qopt", (new Integer(defaultForm.getParam(qopt)).intValue() + 1)+"");
			else {
				defaultForm.addParam("qord", xwOrd);

				String fieldIdxName = this.customFieldIdxName("qordIdx", defaultForm.getParam("dbTable"));
				if (fieldIdxName.length() > 0)
					defaultForm.addParam(fieldIdxName, xwOrdIndex);
			}

			// 31/03/2005 GMG, VM
			// Modificato valore tupla pos per effettuare il posizionamento sul primo documento della nuova selezione ordinata
			defaultForm.addParam("pos", "0");
			defaultForm.addParam("xverb", "@sort");
		}
	}

	/**
	 * Inversione dell'ordinamento di una selezione
	 */
	public void invertOrder() {
		if (defaultForm.getParam("qord").equals("*"))
			defaultForm.addParam("qord", "");
		else if (defaultForm.getParam("qord").equals(""))
			defaultForm.addParam("qord", "*");
		else {
			String s = defaultForm.getParam("qord");
			int index = -1;
			int TOOBIG = 99999;
			String chr;
			boolean cs;

			while (true) {
				index = Math.min(s.indexOf("xml", index + 1) == -1 ? TOOBIG : s.indexOf("xml", index + 1),
						s.indexOf("XML", index + 1) == -1 ? TOOBIG : s.indexOf("XML", index + 1));
				if (index == TOOBIG)
					break;
				cs = (s.charAt(index) == 'x') ? true : false;
				for ( int j = index; j < index + 3; j++) {
					chr = cs ? (String.valueOf(s.charAt(j))).toUpperCase() : (String.valueOf(s.charAt(j))).toLowerCase();
					s = s.substring(0, j) + chr + s.substring(j + 1);
				}
			}
			defaultForm.addParam("qord", s);
		}
		defaultForm.addParam("xverb", "@sort");
	}

	public void esportaCSVAdvanced(String selRac, String klRac, String exportsXML, boolean exportCVSAdvancedSave, String nomeExportCSVAdvanced) {
		defaultForm.addParam("selRac", selRac);
		defaultForm.addParam("klRac", klRac);
		defaultForm.addParam("exportsXML", exportsXML);
		defaultForm.addParam("exportCVSAdvancedSave", exportCVSAdvancedSave);
		defaultForm.addParam("nomeExportCSVAdvanced", nomeExportCSVAdvanced);
		esportaCSVAdvanced();
	}
	
	/**
	 * Avvio di una specifica azione massiva da lista titoli tramite stored procedure LUA
	 * @param klRac Eventuale lista di record selezionati fra i titoli restituiti dalla ricerca (posizione dei doc checkati all'interno della selezione)
	 * @param azione
	 * @param dateFieldFormat
	 */
	public void startAzioneMassiva(String klRac, Azione azione, String dateFieldFormat) throws Exception {
		defaultForm.addParam("verbo", "azionimassive_response");
		defaultForm.addParam("xverb", "@startAzione");
		if (klRac != null)
			defaultForm.addParam("klRac", klRac);
		defaultForm.addParam("stored_azione", azione.getStored());
		defaultForm.addParam("params_azione", azione.getXmlParametriInputForStoredProcedureInvocation(dateFieldFormat));
	}
	
}
