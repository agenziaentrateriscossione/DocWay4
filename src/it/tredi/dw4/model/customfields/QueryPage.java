package it.tredi.dw4.model.customfields;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Map;

import org.dom4j.Document;

public class QueryPage extends XmlEntity {

	private String codice = "";
	private String tabella = "";
	private String descrizione = "";
	private String icona = "";
	
	@Override
	public XmlEntity init(Document dom) {
		this.codice 		= XMLUtil.parseStrictAttribute(dom, "queryPage/@codice");
		this.tabella 		= XMLUtil.parseStrictAttribute(dom, "queryPage/@tabella");
		this.descrizione 	= (XMLUtil.parseStrictElement(dom, "queryPage/descrizione")).trim();
    	this.icona			= XMLUtil.parseStrictElement(dom, "queryPage/icona");
    	
    	return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		return null;
	}
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public String getTabella() {
		return tabella;
	}

	public void setTabella(String tabella) {
		this.tabella = tabella;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getIcona() {
		return icona;
	}

	public void setIcona(String icona) {
		this.icona = icona;
	}
	
}
