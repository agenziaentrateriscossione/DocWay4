package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

import org.dom4j.Document;

/**
 * Bean di ricerca per uno specifico repertorio con campi custom definiti dall'utente (estensione di queryGlobale).
 * 
 * Il caricamento dei campi custom e la costruzione della query su campi custom sono gia' gestite in queryGlobale visto che i campi custom sono gia' inclusi
 * in quel template. Il bean corrente serve nel caso in cui sia necessario esporre delle maschere di ricerca specifiche per i singoli repertori 
 * con campi custom definiti. 
 * 
 * @author mbernardini
 */
public class QueryCustomFieldsRep extends QueryGlobale {
	
	private String descrizioneRepertorio = "";
	private String customfields_dbTable = "";
	private String customfields_codRep = "";
	private int customfields_resources = 0;
	
	private boolean dati_repertorio = true; // apertura/chiusura della sezione dati repertorio
	
	public QueryCustomFieldsRep() throws Exception {
		super();
	}

	@Override
	public void init(Document dom) {
		super.init(dom);
		
		descrizioneRepertorio 	= XMLUtil.parseStrictElement(dom, "/response/customqueryfields/descrizione", true);
		customfields_dbTable 	= XMLUtil.parseStrictAttribute(dom, "/response/customqueryfields/page/@dbTable");
		customfields_codRep 	= XMLUtil.parseStrictAttribute(dom, "/response/customqueryfields/page/@codRep");
		
		if (getCustomQueryFields() != null && getCustomQueryFields().getQuerysections() != null)
			customfields_resources = getCustomQueryFields().getQuerysections().size();
		
		forzaRepertorio();
	}
	
	/**
	 * forzatura della ricerca su tipo corrente (porzione query generata da QueryGlobale)
	 */
	private void forzaRepertorio() {
		setRepertori_multipli(true);
		
		if (customfields_resources == 1) { // e' stata richiesta uno ed un solo repertorio sul quale ricercare
			
			forceRepertorioSelected(customfields_codRep, customfields_dbTable.substring(0, 1).toUpperCase());
			
			if (customfields_dbTable.equals(Const.DOCWAY_TIPOLOGIA_ARRIVO))
				setArrivo(true);
			else if (customfields_dbTable.equals(Const.DOCWAY_TIPOLOGIA_PARTENZA))
				setPartenza(true);
			else if (customfields_dbTable.equals(Const.DOCWAY_TIPOLOGIA_INTERNO))
				setInterno(true);
			else if (customfields_dbTable.equals(Const.DOCWAY_TIPOLOGIA_VARIE))
				setVarie(true);
			
			setCurrentCustomFieldSection(customfields_dbTable + "_" + customfields_codRep);
			
		}
		else {
			// non esiste una ed una sola risorsa da ricercare
			// TODO generazione del messaggio di errore... situazione possibile?
		}
	}
	
	/**
	 * reset del form di ricerca
	 */
	@Override
	public String resetQuery() {
		super.resetQuery();
		
		forzaRepertorio();
		
		return null;
	}
	
	public String getDescrizioneRepertorio() {
		return descrizioneRepertorio;
	}

	public void setDescrizioneRepertorio(String descrizioneRepertorio) {
		this.descrizioneRepertorio = descrizioneRepertorio;
	}

	public String getCustomfields_dbTable() {
		return customfields_dbTable;
	}

	public void setCustomfields_dbTable(String customfields_dbTable) {
		this.customfields_dbTable = customfields_dbTable;
	}

	public String getCustomfields_codRep() {
		return customfields_codRep;
	}

	public void setCustomfields_codRep(String customfields_codRep) {
		this.customfields_codRep = customfields_codRep;
	}

	public int getCustomfields_resources() {
		return customfields_resources;
	}

	public void setCustomfields_resources(int customfields_resources) {
		this.customfields_resources = customfields_resources;
	}
	
	/**
	 * apertura/chiusura della sezione dei dati di protocollo
	 */
	public String openCloseWidgetDatiRepertorio() {
		dati_repertorio = !dati_repertorio;
		return null;
	}
	
	public boolean isDati_repertorio() {
		return dati_repertorio;
	}

	public void setDati_repertorio(boolean dati_repertorio) {
		this.dati_repertorio = dati_repertorio;
	}
	
}
