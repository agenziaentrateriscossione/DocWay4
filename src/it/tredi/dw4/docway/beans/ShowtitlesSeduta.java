package it.tredi.dw4.docway.beans;

import org.dom4j.Document;

import it.tredi.dw4.utils.XMLDocumento;
import it.tredi.dw4.adapters.ErrormsgFormsAdapter;
import it.tredi.dw4.model.Titolo;
import it.tredi.dw4.utils.XMLUtil;

public class ShowtitlesSeduta extends DocWayTitles {
	
	private String stpTitle = "";

	public ShowtitlesSeduta() throws Exception {
		super();
	}
	
	@Override
	public void init(Document domTitoli) throws Exception {
		super.init(domTitoli);
		this.stpTitle = XMLUtil.parseStrictAttribute(domTitoli,"/response/@stpTitle","");
		if(stpTitle != null && !stpTitle.equals(""))
			this.stpTitle = stpTitle.substring(stpTitle.lastIndexOf("|")+1);
	}

	public String getStpTitle(){
		return this.stpTitle;
	}
	
	@Override
	public String mostraDocumento(Titolo titolo) throws Exception {
		try{	
			String index = titolo.getIndice();
			String dbTable = titolo.getDbTable();
			String tipo = titolo.getDb();
			
			XMLDocumento response = super._mostraDocumento(Integer.valueOf(index)-1, tipo, dbTable);
			dbTable = response.getAttributeValue("/response/@dbTable", "");
			if (dbTable.startsWith("@")) dbTable = dbTable.substring(1);
			
			//return this.buildSpecificShowdocPageAndReturnNavigationRule(dbTable,"delibere",response);
			return this.buildSpecificShowdocPageAndReturnNavigationRule(dbTable,response);
			
		} catch (Throwable t) {
			handleErrorResponse(ErrormsgFormsAdapter.buildErrorResponse(t));
			formsAdapter.fillFormsFromResponse(formsAdapter.getLastResponse()); //restore delle form
			return null;			
		}
	}
	
}
