package it.tredi.dw4.acl.beans;

import java.util.Map;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclTitlesFormsAdapter;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.Titles;
import it.tredi.dw4.i18n.I18N;
import it.tredi.dw4.utils.XMLUtil;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class AclTitles extends Titles {
	
	private AclTitlesFormsAdapter formsAdapter;
	private String xml;
	
	public AclTitles() throws Exception {
		this.formsAdapter = new AclTitlesFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
    
    @SuppressWarnings("unchecked")
	public void init(Document domTitoli) {
    	super.titoli = XMLUtil.parseSetOfElement(domTitoli, "//titolo", new Titolo());
    	this.xml = domTitoli.asXML();
    }
	
	public AclTitlesFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	//TODO - questo metodo dovrebbe testare il tipo di UD e comportarsi di conseguenza, da completare
	public String mostraDocumento() throws Exception {
		Titolo titolo = (Titolo)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
		
		String index = titolo.getIndice();
		String dbTable = titolo.getDbTable();
		String tipo = titolo.getDb();
		
		XMLDocumento response = super._mostraDocumento(Integer.valueOf(index)-1, tipo, dbTable);
		return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}

	/**
	 * Esportazione CSV dei titoli risultanti da una ricerca
	 * 
	 * @return
	 * @throws Exception
	 */
	public String esportaCSV() throws Exception {
		try {
			String dbTable = getFormsAdapter().getDefaultForm().getParam("dbTable");
			if (dbTable == null || dbTable.equals("")) {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(I18N.mrs("dw4.impossibile_effettuare_l_operazione_da_ricerca_globale_Utilizzare_maschere_ricerca_avanzata"), "", ErrormsgFormsAdapter.INFO));
				return null;
			}
			 
			return super._esportaCSV();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;			
		}
	}
	
	/**
	 * Esportazione dei diritti degli utenti risultanti da una ricerca avanzata
	 * su persone interne
	 * 
	 * @return
	 * @throws Exception
	 */
	public String printPIRights() throws Exception {
		try {
			Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			String type = params.get("type");
			
			getFormsAdapter().printPIRights(type, "Stampe/acl/JasperReport/Diritti Utenti/diritti_utenti.jasper", "JRXW%23");
			
			return super._printPIRights();
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}

	
}