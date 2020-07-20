package it.tredi.dw4.acl.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.adapters.AclHierBrowserFormsAdapter;
import it.tredi.dw4.acl.model.Hier;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.beans.HierBrowser;
import it.tredi.dw4.utils.XMLUtil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class AclHierBrowser extends HierBrowser {
	
	private AclHierBrowserFormsAdapter formsAdapter;
	
	private List<Hier> hiers = new ArrayList<Hier>();
	
	private Hier selected;
	private Hier lastClicked = new Hier();
	
	private String xml; 
	
	public String getXml() {
		return xml;
	}
	
	public void setXml(String xml) {
		this.xml = xml;
	}

	public AclHierBrowser() throws Exception {
		this.formsAdapter = new AclHierBrowserFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
		hiers = new ArrayList<Hier>();
		selected = new Hier();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(Document dom) {
		this.xml = dom.asXML(); //REMOVE 
		this.hiers = XMLUtil.parseSetOfElement(dom, "//hier", new Hier());
	}
	
	@Override
	public AclHierBrowserFormsAdapter getFormsAdapter() {
		return formsAdapter;
	}

	@Override
	public String mostraDocumento() throws Exception {
		Hier hier = (Hier) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("hier");		
		selected = hier;
		lastClicked = hier;
		
		XMLDocumento response = super._mostraDocumento(hier.getNdoc());
		return buildSpecificShowdocPageAndReturnNavigationRule(response.getRootElement().attributeValue("dbTable"), response);
		
	}

	/**
	 * apertura/chiusura di un nodo dell'albero della gerarchia
	 * @param hier
	 * @param forceOpen
	 * @return
	 * @throws Exception
	 */
	@Override
	public String docToggle() throws Exception {
		Hier hier = (Hier) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("hier");
		lastClicked = hier;
		
		XMLDocumento response = null;
		if (hier.isOpened())
			response = super._docToggle(hier.getNdoc(), hier.getNdoc());
		else
			response = super._docToggle(hier.getNdoc());
		
		if (handleErrorResponse(response)) {
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		
		formsAdapter.fillFormsFromResponse(response);
		this.xml = response.asXML();
		this.init(response.getDocument());
		return null;
	}
	
	public void setHiers(List<Hier> nodi) {
		this.hiers = nodi;
	}

	public List<Hier> getHiers() {
		return hiers;
	}

	public void setSelected(Hier selected) {
		this.selected = selected;
	}

	public Hier getSelected() {
		return selected;
	}
	
	public Hier getLastClicked() {
		return lastClicked;
	}

	public void setLastClicked(Hier lastClicked) {
		this.lastClicked = lastClicked;
	}
	
	/**
	 * refresh della gerarchia (viene aperto l'ultimo nodo cliccato: espanso, chiuso o visualizzato)
	 * @return
	 * @throws Exception
	 */
	public String refreshGerarchia() throws Exception {
		if (lastClicked != null) { 
			getFormsAdapter().docCurrent(lastClicked.getNdoc());
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			formsAdapter.fillFormsFromResponse(response);
			this.xml = response.asXML();
			this.init(response.getDocument());
		}
		return null;
	}
	
	/**
	 * rigenerazione della gerarchia
	 * 
	 * @return
	 * @throws Exception
	 */
	public String generaRelazioniStrutture() throws Exception {
		try {
			formsAdapter.generaRelazioniStrutture(); 
			XMLDocumento response = formsAdapter.getDefaultForm().executePOST(getUserBean());
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			
			if (handleErrorResponse(response)) {
				formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
				return null;
			}
			
			AclLoadingbar aclLoadingbar = new AclLoadingbar();
			aclLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
			aclLoadingbar.init(response);
			setLoadingbar(aclLoadingbar);
			aclLoadingbar.setActive(true);
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;
		}
		return null;
	}	
	
}
