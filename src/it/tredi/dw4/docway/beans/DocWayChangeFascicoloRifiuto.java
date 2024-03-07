package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.DocEditFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.Fascicolo;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLDocumento;

/**
 * Selezione del fascicolo all'interno del quale inserire le bozze rifiutate (se abilitata la funzione di rifiuto bozze in arrivo)
 */
public class DocWayChangeFascicoloRifiuto extends DocWayDocedit { 
	// FIXME la classe estende DocEdit anziche' Query per poter utilizzare i metodi di lookup (sarebbe piu' corretto generalizzare i lookup)

	private String xml;
	private DocDocWayDocEditFormsAdapter formsAdapter;
	
	private boolean visible = false;
	private Fascicolo fascicolo = new Fascicolo();
	
	public DocWayChangeFascicoloRifiuto() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@Override
	public void init(Document dom) {
		this.xml = dom.asXML();
		
		this.fascicolo = new Fascicolo();
		this.fascicolo.init(dom);
	}

	@Override
	public DocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	@Override
	public String saveDocument() throws Exception {
		try {
			if (fascicolo.getNrecord() == null || fascicolo.getNrecord().isEmpty()) { // nessun nrecord selezionato
				this.setErrorMessage("", I18N.mrs("dw4.errore_nella_selezione_del_fascicolo"));
				return null;
			}
			// aggiornamento della property relativa al fascicolo di rifiuto
			formsAdapter.changePropertyValue("rifiutoBozzeArrivo.fascicolo.nrecord", fascicolo.getNrecord());
			
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			// aggiornamento eseguito correttamente, chiudo il popup
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			close();
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
	}

	@Override
	public String clearDocument() throws Exception {
		return close();
	}
	
	/**
	 * Lookup su fascicoli
	 * @return
	 * @throws Exception
	 */
	public String lookupFascicolo() throws Exception {
		String value = (fascicolo != null && fascicolo.getOggetto() != null && !fascicolo.getOggetto().isEmpty()) ? fascicolo.getOggetto() : "";
		if (!value.isEmpty()) {
			String aliasName = "fasc_oggetto";
			String aliasName1 = "fasc_oggetto";
			String titolo = "xml,/fascicolo/oggetto"; //titolo 
			String ord = "xml(xpart:/fascicolo/oggetto)"; //ord
			String campi = ".oggetto=xml,/fascicolo/oggetto ; .@nrecord=xml,/fascicolo/@nrecord";
			String db = getFormsAdapter().getDefaultForm().getParam("db"); //db 
			String newRecord = ""; //newRecord
			String xq = ""; //extraQuery
			if (getFormsAdapter().checkBooleanFunzionalitaDisponibile("acl_ext_aoo_restriction", false))
				xq += " AND ([fasc_codammaoo]=" + fascicolo.getCod_amm_aoo() + ")";
			
			callLookup(fascicolo, aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		}
		return null;
	}
	
	/**
	 * Clear lookup su fascicoli
	 * @return
	 * @throws Exception
	 */
	public String clearLookupFascicolo() throws Exception {
		String campi = ".oggetto=xml,/fascicolo/oggetto ; .@nrecord=xml,/fascicolo/@nrecord";
		return clearField(campi, fascicolo);
	}
	
	/**
	 * Chiusura del popup (modale)
	 * @return
	 * @throws Exception
	 */
	public String close() throws Exception {
		this.visible = false;
		return null;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Fascicolo getFascicolo() {
		return fascicolo;
	}

	public void setFascicolo(Fascicolo fascicolo) {
		this.fascicolo = fascicolo;
	}
	
	@Override
	public XmlEntity getModel() {
		return null;
	}
	
}
