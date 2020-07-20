package it.tredi.dw4.acl.model;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.Map;

import javax.faces.event.ValueChangeEvent;

import org.dom4j.Document;

public class Vocabolario extends XmlEntity {
	private String chiave;
	private String frequenza;
	
	private boolean selezionato;

	public Vocabolario() {}
    
	public Vocabolario(String xmlVocabolario) throws Exception {
        this.init(XMLUtil.getDOM(xmlVocabolario));
    }
    
	public Vocabolario init(Document domVocabolario) {
    	this.chiave 	= XMLUtil.parseAttribute(domVocabolario, "vocabolario/@chiave");
    	if ( chiave != null && chiave.contains("|^|") ) chiave = chiave.substring(chiave.indexOf("|^|")+3);
    	this.frequenza	= XMLUtil.parseAttribute(domVocabolario, "vocabolario/@frequenza");
        return this;
    }
    
    public Map<String, String> asFormAdapterParams(String prefix){
    	return null;
    }
    
    public void setChiave(String text) {
		this.chiave = text;
	}

	public String getChiave() {
		return chiave;
	}

	public void setFrequenza(String frequenza) {
		this.frequenza = frequenza;
	}

	public String getFrequenza() {
		return frequenza;
	}
	
    public void setSelezionato(boolean selected) {
		this.selezionato = selected;
	}

	public boolean isSelezionato() {
		return this.selezionato;
	}
	
	/**
	 * Check/Uncheck di titoli del vocabolario
	 * @param vce
	 */
	public void checkSelezione(ValueChangeEvent vce) throws Exception {  
		this.selezionato = ((Boolean) vce.getNewValue()).booleanValue();
	}

}