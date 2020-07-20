package it.tredi.dw4.docway.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.adapters.QueryFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayQueryFormsAdapter;
import it.tredi.dw4.docway.model.TipologiaFascicolo;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class QueryFascicoloCustom extends DocWayQuery {

	private String xml;
	private DocDocWayQueryFormsAdapter formsAdapter;
	
	private List<TipologiaFascicolo> listof_tipologie = new ArrayList<TipologiaFascicolo>(); // personalView di ricerca (template personalizzati)
	
	public QueryFascicoloCustom() throws Exception {
		this.formsAdapter = new DocDocWayQueryFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(Document dom) {
		this.xml = dom.asXML();
		
		listof_tipologie = XMLUtil.parseSetOfElement(dom, "/response/listof_tipologie/tipologia", new TipologiaFascicolo());
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
	
	@Override
	public QueryFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String queryPlain() throws Exception {
		return null;
	}
	
	/**
	 * caricamento di una pagina di ricerca su specifico repertorio con campi custom
	 * 
	 * @return
	 * @throws Exception
	 */
	public String gotoTableQFascicoloCustom() throws Exception {
		try {
			TipologiaFascicolo titoloFascicolocustom = (TipologiaFascicolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("tipologia");
			formsAdapter.gotoTableQCustomFieldsFasc(Const.DOCWAY_TIPOLOGIA_FASCICOLO, false, Const.DOCWAY_TIPOLOGIA_FASCICOLO + "_" + titoloFascicolocustom.getCodice(), titoloFascicolocustom.getCodice(), titoloFascicolocustom.getDescrizione());
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			// caricamento della pagina di ricerca specifica per il repertorio con campi custom
			QueryFascicolo queryFascicolo = new QueryFascicolo();
			queryFascicolo.getFormsAdapter().fillFormsFromResponse(response);
			queryFascicolo.init(response.getDocument());
			setSessionAttribute("queryFascicolo", queryFascicolo);
				
			return "query@fascicolo";
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
		}
		
		return null;
	}

	public List<TipologiaFascicolo> getListof_tipologie() {
		return listof_tipologie;
	}

	public void setListof_tipologie(List<TipologiaFascicolo> listof_tipologie) {
		this.listof_tipologie = listof_tipologie;
	}
	
}
