package it.tredi.dw4.docway.model.condotte;

import it.tredi.dw4.model.XmlEntity;
import it.tredi.dw4.utils.XMLUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.dom4j.Document;

public class Dati_contabili extends XmlEntity {

	private String protocollo_iva = "";
	private Fornitore fornitore = new Fornitore();
	private List<Scadenza> scadenze;
	private String note = "";
	
	@SuppressWarnings("unchecked")
	@Override
	public XmlEntity init(Document dom) {
		this.protocollo_iva = XMLUtil.parseStrictElement(dom, "dati_contabili/protocollo_iva");
		this.fornitore.init(XMLUtil.createDocument(dom, "dati_contabili/fornitore"));
		this.scadenze = XMLUtil.parseSetOfElement(dom, "dati_contabili/scadenze/scadenza", new Scadenza());
		this.note = XMLUtil.parseStrictElement(dom, "dati_contabili/note");
		
		if (this.scadenze.size() == 0) this.scadenze.add(new Scadenza()); 
		
		return this;
	}

	@Override
	public Map<String, String> asFormAdapterParams(String prefix) {
		if (null == prefix) prefix = "";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put(prefix + ".protocollo_iva", this.protocollo_iva);
    	params.putAll(fornitore.asFormAdapterParams(prefix + ".fornitore"));
    	for (int i = 0; i < scadenze.size(); i++) {
    		Scadenza scadenza = (Scadenza) scadenze.get(i);
    		if (scadenza != null && scadenza.getText() != null && scadenza.getText().length() > 0)
    		params.putAll(scadenza.asFormAdapterParams(".scadenze.scadenza["+String.valueOf(i)+"]"));
		}
    	params.put(prefix + ".note", this.note);
    	
    	return params;
	}
	
	/**
	 * aggiunta di una scadenza
	 */
	public String addScadenza() {
		Scadenza scadenza = (Scadenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("scadenza");
		int index = 0;
		if (scadenza != null)
			index = scadenze.indexOf(scadenza);
		
		if (scadenze.size() > index)
			scadenze.add(index+1,  new Scadenza());
		else
			scadenze.add(new Scadenza());
		
		return null;
	}
	
	/**
	 * eliminazione di un scadenze
	 */
	public String deleteScadenza() {
		Scadenza scadenza = (Scadenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("scadenza");
		if (scadenza != null) {
			scadenze.remove(scadenza);
			if (scadenze.isEmpty()) 
				scadenze.add(new Scadenza());
		}
		return null;
	}
	
	/**
	 * spostamento in alto di un scadenza
	 */
	public String moveUpScadenza() {
		Scadenza scadenza = (Scadenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("scadenza");
		if (scadenza != null && scadenze != null) {
			int index = scadenze.indexOf(scadenza);
			if (index > 0 ) {
				scadenze.remove(index);
				scadenze.add(index-1, scadenza);
			}
		}
		return null;
	}

	/**
	 * spostamento in basso di un scadenza
	 */
	public String moveDownScadenza() {
		Scadenza scadenza = (Scadenza) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("scadenza");
		if (scadenza != null && scadenze != null) {
			int index = scadenze.indexOf(scadenza);
			if (index < scadenze.size()-1 ) {
				scadenze.remove(index);
				scadenze.add(index+1, scadenza);
			}
		}
		return null;
	}
	
	public String getProtocollo_iva() {
		return protocollo_iva;
	}

	public void setProtocollo_iva(String protocollo_iva) {
		this.protocollo_iva = protocollo_iva;
	}
	
	public Fornitore getFornitore() {
		return fornitore;
	}

	public void setFornitore(Fornitore fornitore) {
		this.fornitore = fornitore;
	}
	
	public List<Scadenza> getScadenze() {
		return scadenze;
	}

	public void setScadenze(List<Scadenza> scadenze) {
		this.scadenze = scadenze;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
