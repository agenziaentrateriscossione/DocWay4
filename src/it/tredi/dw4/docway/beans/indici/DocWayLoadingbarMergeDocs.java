package it.tredi.dw4.docway.beans.indici;

import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.docway.beans.DocWayLoadingbar;
import it.tredi.dw4.docway.beans.ShowdocFascicolo;
import it.tredi.dw4.utils.Logger;
import it.tredi.dw4.utils.XMLDocumento;

public class DocWayLoadingbarMergeDocs extends DocWayLoadingbar {

	public DocWayLoadingbarMergeDocs() throws Exception {
		super();
	}
	
	@Override
	public String refresh() throws Exception {
		try {
			XMLDocumento response = super._refresh();
			setDocument(response);
			getFormsAdapter().fillFormsFromResponse(response);
			
			if (handleErrorResponse(response)) {
				getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
				return null;
			}
			
			String verbo = response.getAttributeValue("/response/@verbo");
			if (verbo.equals("showdoc")) {
				
				// TODO reload del raccoglitore di tipo indice
				
				setActive(false);
				if (response.isXPathFound("/response/fascicolo")) {
					ShowdocFascicolo showdocFascicolo = new ShowdocFascicolo();
					showdocFascicolo.getFormsAdapter().fillFormsFromResponse(response);
					showdocFascicolo.init(response.getDocument());
					setSessionAttribute("showdocFascicolo", showdocFascicolo);		
					return "showdoc@fascicolo@reload";
				}
				else {
					// TODO gestire altri casi
					Logger.info(response.asXML());
				}
			}
			
			init(response);
			return null;
		}
		catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form
			return null;
		}
	}
		
	/**
	 * Ritorna l'eventuale nrecord del file doc di output generato
	 * @return
	 */
	public String getOutputDocLink() {
		if (getDocument() == null)
			return "";
		else
			return getDocument().getAttributeValue("//raccindice/docOutput/@link", "");
	}
	
	/**
	 * Ritorna il nome del file di aggregato prodotto (file temporaneo su Tomcat)
	 * @return
	 */
	public String getFileNameAggregato() {
		if (getDocument() == null)
			return "";
		else
			return getDocument().getAttributeValue("//raccindice/aggregato/@fileName", "");
	}
	
	/**
	 * Ritorna il file title dell'aggregato (se non trovato viene restituito il nome del file temporaneo di output)
	 * @return
	 */
	public String getFileTitleAggregato() {
		if (getDocument() == null)
			return "";
		else {
			String fileTitle = getDocument().getAttributeValue("//raccindice/aggregato/@fileTitle", "");
			if (fileTitle.isEmpty())
				fileTitle = getDocument().getAttributeValue("//raccindice/aggregato/@fileName", "");
			return fileTitle;
		}
	}
	
	/**
	 * Ritorna l'eventuale messaggio di errore riscontrato sulla procedura di merge dei documenti
	 * @return
	 */
	public String getErrorMessage() {
		if (getDocument() == null)
			return "";
		else
			return getDocument().getElementText("//raccindice/error", "");
	}
	
	
}
