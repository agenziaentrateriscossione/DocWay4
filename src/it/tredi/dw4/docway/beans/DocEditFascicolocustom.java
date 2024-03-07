package it.tredi.dw4.docway.beans;

import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.doc.adapters.DocDocWayDocEditFormsAdapter;
import it.tredi.dw4.docway.model.TipologiaFascicolo;
import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.Const;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DocEditFascicolocustom extends DocWayDocedit {

	private DocDocWayDocEditFormsAdapter formsAdapter;
	
	private String xml = "";

	private List<TipologiaFascicolo> listof_tipologie;
	
	public DocEditFascicolocustom() throws Exception {
		this.formsAdapter = new DocDocWayDocEditFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("docwayService"));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		xml = dom.asXML();
		
		listof_tipologie = XMLUtil.parseSetOfElement(dom, "/response/listof_tipologie/tipologia", new TipologiaFascicolo());
		
	}

	@Override
	public DocDocWayDocEditFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	@Override
	public String saveDocument() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clearDocument() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<TipologiaFascicolo> getListof_tipologie() {
		return listof_tipologie;
	}

	public void setListof_tipologie(List<TipologiaFascicolo> listof_tipologie) {
		this.listof_tipologie = listof_tipologie;
	}
	
	/**
	 * Selezione di un fascicolo dall'elenco (redirect a docEdit)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insTableDocFascicolocustom() throws Exception{
		TipologiaFascicolo titoloFascicolocustom = (TipologiaFascicolo) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("titoloFascicolocustom");
		return insTableDocFascicolocustom(titoloFascicolocustom);
	}
	
	/**
	 * Selezione di un fascicolo dall'elenco (redirect a docEdit)
	 * 
	 * @return
	 * @throws Exception
	 */
	public String insTableDocFascicolocustom(TipologiaFascicolo titoloFascicolocustom) throws Exception{
		try {
			XMLDocumento response = super._insTableFascicoloCustom(Const.DOCWAY_TIPOLOGIA_FASCICOLO, titoloFascicolocustom.getCodice(), titoloFascicolocustom.getDescrizione());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			return buildSpecificDocEditPageAndReturnNavigationRule(Const.DOCWAY_TIPOLOGIA_FASCICOLO, response, false);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	@Override
	public XmlEntity getModel() {
		return null;
	}
	
}
