package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.DocEditFormsAdapter;

import org.dom4j.Document;

public abstract class DocEdit extends Page {
	
	public abstract void init(Document dom);
	
	public abstract DocEditFormsAdapter getFormsAdapter();
	
	public abstract String saveDocument() throws Exception;
	
	public abstract String clearDocument() throws Exception;
	
	protected XMLDocumento _saveDocument(String pne, String pnce) throws Exception {
		getFormsAdapter().saveDocument(pne, pnce);
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	protected XMLDocumento _clearDocument() throws Exception {
		getFormsAdapter().clearDocument();
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	protected XMLDocumento _lookup(String aliasName, String aliasName1, String titolo, String ord, String campi, String xq, String db, String newRecord, String value) throws Exception {
		getLookup().cleanFields(campi);
		getFormsAdapter().lookup(aliasName, aliasName1, titolo, ord, campi, xq, db, newRecord, value);
		
		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
		return response;
	}
	
	protected XMLDocumento _thVincolato(String numTitles4Page, String fieldName, String radice, String relazione, String chiave, String insRight, String delRight, String windowTitle, boolean iscascade, String value) throws Exception {
		getThvincolato().cleanFields(fieldName);
		getFormsAdapter().thVincolato(numTitles4Page, fieldName, radice, relazione, chiave, insRight, delRight, windowTitle, iscascade, value);
		
		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di docedit
		return response;		
	}
}
