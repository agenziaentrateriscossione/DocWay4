package it.tredi.dw4.acl.beans;

import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;
import org.dom4j.Element;

import it.tredi.dw4.acl.adapters.AclTitlesFormsAdapter;
import it.tredi.dw4.acl.model.TitoloDelega;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.DelegheOptions;
import it.tredi.dw4.beans.Titles;
import it.tredi.dw4.docway.model.Delega;
import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.utils.XMLUtil;

public class DelegheTitles extends Titles {

	
	private AclTitlesFormsAdapter formsAdapter;
	private String xml;
	private String dbTable;
	private List<TitoloDelega> titoliDelega;
	private String titleFormat;
	
	public void setXml(String xml) {
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}
	
	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}

	public String getDbTable() {
		return dbTable;
	}
	
	public List<TitoloDelega> getTitoliDelega() {
		return titoliDelega;
	}
	
	public void setTitoliDelega(List<TitoloDelega> titoliDelega) {
		this.titoliDelega = titoliDelega;
	}
	
	public String getTitleFormat() {
		return titleFormat;
	}

	public void setTitleFormat(String titleFormat) {
		this.titleFormat = titleFormat;
	}
	
	public DelegheTitles() throws Exception {
		this.formsAdapter = new AclTitlesFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void init(Document domTitoli) {
		this.titoliDelega = XMLUtil.parseSetOfElement(domTitoli, "//titolo", new TitoloDelega());
    	this.xml = domTitoli.asXML();
    	Element root = domTitoli.getRootElement();
		this.dbTable = root.attributeValue("dbTable", "");
		this.titleFormat = root.attributeValue("ripropz", "");
	}
	
	public AclTitlesFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}
	
	@Override
	public String mostraDocumento() throws Exception {
		try {
			TitoloDelega title = (TitoloDelega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
			String index = title.getIndice();
			formsAdapter.gotoDeleghePersonaInterna(title.getCod_delegante(), Integer.valueOf(index)-1, titleFormat);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			DelegheOptions optionsDeleghe = new DelegheOptions("aclService");
			optionsDeleghe.getFormsAdapter().fillFormsFromResponse(response);
			optionsDeleghe.init(response.getDocument());
			setSessionAttribute("optionsDeleghe", optionsDeleghe);

			redirectToJsf("options@deleghe", response);
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
			return null;
		}
	}
	
	public String mostraDelegato() throws Exception {
		try {
			TitoloDelega title = (TitoloDelega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("title");
			String index = title.getIndice();
			Delega delega = (Delega) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("delega");
			formsAdapter.gotoDeleghePersonaInterna(delega.getCodPersona(), Integer.valueOf(index)-1, titleFormat);
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			DelegheOptions optionsDeleghe = new DelegheOptions("aclService");
			optionsDeleghe.getFormsAdapter().fillFormsFromResponse(response);
			optionsDeleghe.init(response.getDocument());
			setSessionAttribute("optionsDeleghe", optionsDeleghe);

			redirectToJsf("options@deleghe", response);
			return null;
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); // restore delle form
			return null;
		}
	}
	
	/**
	 * Apertura del menù relativo alla ricerca delle deleghe
	 * @return
	 * @throws Exception
	 */
	public String gotoQueryDeleghe() throws Exception {
		formsAdapter.gotoTableQDeleghe();
		XMLDocumento responseDoc = formsAdapter.getDefaultForm().executePOST(getUserBean());
		return buildSpecificQueryPageAndReturnNavigationRule("deleghe", responseDoc);
	}
}
