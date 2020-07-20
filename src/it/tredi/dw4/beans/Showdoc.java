package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.DocumentFormsAdapter;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.utils.StringUtil;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public abstract class Showdoc extends Page {
	
	private String currentPage = ""; // utilizzata per la navigazione dei risultati di una ricerca tramite campo testo
	
	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	
	public abstract void init(Document dom);
	
	public abstract DocumentFormsAdapter getFormsAdapter();
	
	public abstract String paginaTitoli() throws Exception;
	
	public abstract void reload() throws Exception;
	
	protected XMLDocumento _paginaTitoli() throws Exception {
		getFormsAdapter().paginaTitoli();
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());	
	}
	
	public abstract String modifyTableDoc() throws Exception;
	
	protected XMLDocumento _modifyTableDoc() throws Exception {
		getFormsAdapter().modifyTableDoc();
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	public void _reload(String navigationRule) throws Exception {
		
		// TODO senza questa porzione di codice non funziona il reload subito dopo il salvataggio
		// TODO e' corretto in questo modo (controllare docway3!!!)
		if (getFormsAdapter().getDefaultForm().getParam("selid").equals("")) {
			String physDoc = getFormsAdapter().getDefaultForm().getParam("physDoc");
			if (physDoc != null && !physDoc.equals(""))
				getFormsAdapter().getDefaultForm().addParam("pos", new Integer(physDoc).intValue() - 1);
		}
		
		XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(response);
		init(response.getDocument());
		FacesContext.getCurrentInstance().getExternalContext().redirect(navigationRule + ".jsf");
	}
	
	/**
	 * Reload della pagina senza redirect a navigation rule (chiamate AJAX)
	 * 
	 * @throws Exception
	 */
	public void _reloadWithoutNavigationRule() throws Exception {
		
		// TODO senza questa porzione di codice non funziona il reload subito dopo il salvataggio
		// TODO e' corretto in questo modo (controllare docway3!!!)
		if (getFormsAdapter().getDefaultForm().getParam("selid").equals("")) {
			String physDoc = getFormsAdapter().getDefaultForm().getParam("physDoc");
			if (physDoc != null && !physDoc.equals(""))
				getFormsAdapter().getDefaultForm().addParam("pos", new Integer(physDoc).intValue() - 1);
		}
		
		XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(response);
		init(response.getDocument());
	}
	
	public void _reload() throws Exception {
		XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
		
		String dbTable = response.getAttributeValue("/response/@dbTable");
		String navigationRule = buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
		redirectToJsf(navigationRule, response);
	}

	public String primaPagina() throws Exception {
		if (getFormsAdapter().primaPagina()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			String dbTable = response.getAttributeValue("/response/@dbTable");
			return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
		}
		return null;
	}	
	
	public String paginaPrecedente() throws Exception {
		if (getFormsAdapter().paginaPrecedente()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			String dbTable = response.getAttributeValue("/response/@dbTable");
			return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);		
		}
		return null;
	}	
		
	/**
	 * Caricamento di una specifica pagina della lista titoli
	 * @return
	 * @throws Exception
	 */
	public String paginaSpecifica() throws Exception {
		try {
			if (StringUtil.isNumber(currentPage)) {
				int curr = new Integer(currentPage).intValue();
				if (curr > 0 && curr <= getFormsAdapter().getDefaultForm().getParamAsInt("count")) {
					getFormsAdapter().paginaSpecifica(curr);
					XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
					if (handleErrorResponse(response)) {
						getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
						return null;
					}
					
					String dbTable = response.getAttributeValue("/response/@dbTable");
					return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
				}
				else
					currentPage = getFormsAdapter().getCurrent() + "";
			}
			else
				currentPage = getFormsAdapter().getCurrent() + "";
			
			return null;
		} 
		catch (Throwable t){
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
	
	public String paginaSuccessiva() throws Exception {
		if (getFormsAdapter().paginaSuccessiva()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			String dbTable = response.getAttributeValue("/response/@dbTable");
			return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
		}
		return null;
	}
	
	public String ultimaPagina() throws Exception {
		if (getFormsAdapter().ultimaPagina()) {
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			String dbTable = response.getAttributeValue("/response/@dbTable");
			return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);			
		}
		return null;
	}

	//dpranteda - mancava la gestione degli errori nella risposta
	public String remove() throws Exception{
		try{
			getFormsAdapter().remove();
		
			XMLDocumento response = getFormsAdapter().getDefaultForm().executePOST(getUserBean());
			if(handleErrorResponse(response)){
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			String verbo = response.getAttributeValue("/response/@verbo");
			String dbTable = response.getAttributeValue("/response/@dbTable");
		
			if ("@reload".equals(dbTable)) {
				getFormsAdapter().fillFormsFromResponse(response);
				reload();
				return null;
			}
			else if ("query".equals(verbo))
				return "show@acl_home";
			else
				return buildSpecificShowdocPageAndReturnNavigationRule(dbTable, response);
		}catch (Throwable t){
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
			}
	}
	
}
