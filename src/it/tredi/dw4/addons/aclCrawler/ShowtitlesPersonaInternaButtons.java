package it.tredi.dw4.addons.aclCrawler;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.acl.beans.AclLoadingbar;
import it.tredi.dw4.aclCrawler.adapters.AclCrawlerFormsAdapter;
import it.tredi.dw4.adapters.AdaptersConfigurationLocator;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.addons.BaseAddOn;
import it.tredi.dw4.beans.Errormsg;
import it.tredi.dw4.beans.Page;

public class ShowtitlesPersonaInternaButtons extends BaseAddOn {
	private AclCrawlerFormsAdapter formsAdapter;
	
	public ShowtitlesPersonaInternaButtons(String template, Page host) throws Exception {
		super(template, host);
		
		this.formsAdapter = new AclCrawlerFormsAdapter(AdaptersConfigurationLocator.getInstance().getAdapterConfiguration("aclService"));
	}

	@Override
	public void init(Document dom) {
		try {
			this.formsAdapter.fillFormsFromResponse(new XMLDocumento(dom));
		} catch (Throwable t) {
			try {
				handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
				getFormsAdapter().fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
	}

	public AclCrawlerFormsAdapter getFormsAdapter() {
		return this.formsAdapter;
	}

	@Override
	public Map<String, String> asFormAdapterParams() {
		return null;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public String asQuery() {
		return null;
	}

	public void exportCsv() throws Exception {
		String printableQuery = (String) getHost().getSessionAttribute("AclCrawlerPrintableQuery");
		
		if (printableQuery == null)
			printableQuery = "";
		
		this.formsAdapter.export(printableQuery, "csv");
		try {
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getHost().getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				AclLoadingbar aclLoadingbar = new AclLoadingbar();
				aclLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				aclLoadingbar.init(response);
				getHost().setLoadingbar(aclLoadingbar);
				aclLoadingbar.setActive(true);
			}
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(formsAdapter.getLastResponse()); // restore delle form
		}
	}

	public void exportPdf() throws Exception {
		String printableQuery = (String) getHost().getSessionAttribute("AclCrawlerPrintableQuery");
		
		if (printableQuery == null)
			printableQuery = "";
		
		this.formsAdapter.export(printableQuery, "pdf");
		try {
			XMLDocumento response = this.formsAdapter.getDefaultForm().executePOST(getHost().getUserBean());
			getFormsAdapter().fillFormsFromResponse(response);
			
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("loadingbar")) {
				AclLoadingbar aclLoadingbar = new AclLoadingbar();
				aclLoadingbar.getFormsAdapter().fillFormsFromResponse(response);
				aclLoadingbar.init(response);
				getHost().setLoadingbar(aclLoadingbar);
				aclLoadingbar.setActive(true);
			}
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(formsAdapter.getLastResponse()); // restore delle form
		}
	}
	
	/**
	 * Analizza la response XML e, in caso di errore, imposta il
	 * popup di visualizzazione dell'errore
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public boolean handleErrorResponse(XMLDocumento response) throws Exception {
		if (ErrormsgFormsAdapter.isResponseErrorMessage(response)) {
			Errormsg errormsg = new Errormsg();
			errormsg.setActive(true);
			errormsg.init(response.getDocument());
			
			HttpSession session = null;
			session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			session.setAttribute("errormsg", errormsg);
			
			return true;
		}
		
		return false;
	}
}
