package it.tredi.dw4.beans;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.QueryFormsAdapter;

import org.dom4j.Document;

public abstract class Query extends Page {
	
	public abstract void init(Document dom);
	
	public abstract QueryFormsAdapter getFormsAdapter();
	
	public abstract String queryPlain() throws Exception;	
	
	public XMLDocumento _queryPlain(String query, String verbo, String xverb) throws Exception {
		getFormsAdapter().queryPlain(query, verbo, xverb);
		
		return getFormsAdapter().getDefaultForm().executePOST(getUserBean());
	}
	
	protected XMLDocumento _openIdex(String keyPath, String startKey, String shwMode, String common, String threl, String inputName, String windowTitle, String value, boolean acceptEmptySelection) throws Exception {
		getFormsAdapter().openIndex(keyPath, startKey, shwMode, common, threl, inputName, windowTitle, value, acceptEmptySelection);
		
		XMLDocumento response = getFormsAdapter().getIndexForm().executePOST(getUserBean());
		getFormsAdapter().fillFormsFromResponse(getFormsAdapter().getLastResponse()); //restore delle form partendo dall'xml dell'ultima richiesta effettuata sulla pagina di Query
		return response;		
	}
	
	protected String addQueryField(String query, String value){
		return addQueryField(query, value, "AND");
	}
	
	protected String addQueryField(String query, String value, String operator){
		if (null == value || "".equals(value.trim())) return "";
		else return "(["+query+"]=" + value + ") " + operator + " "; //fcappelli 20120906 - rimossi i doppi apici dalla query, vanno aggiunti dall'utente eventualmente
	}
}
